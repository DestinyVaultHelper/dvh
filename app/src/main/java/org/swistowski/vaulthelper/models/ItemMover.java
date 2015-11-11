package org.swistowski.vaulthelper.models;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.storage.Items;
import org.swistowski.vaulthelper.storage.Data;
import org.swistowski.vaulthelper.views.ClientWebView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemMover {
    private static final String LOG_TAG = "ItemMover";

    public static Promise equip(final ClientWebView webView, final String owner, final Item item, final Promise p) {
        final Promise p_inner = new Promise();

        JSONObject obj = new JSONObject();
        try {
            obj.put("characterId", owner);
            obj.put("itemId", item.getItemId());
            obj.put("membershipType", Data.getInstance().getMembership().getType());
        } catch (JSONException e) {
            Log.e(LOG_TAG, "exception", e);
            e.printStackTrace();
        }

        webView.call("destinyService.EquipItem", obj).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                for (Item tested_item : Items.getInstance().all()) {
                    if (tested_item.getBucketTypeHash() == item.getBucketTypeHash() && item.getItemHash() != tested_item.getItemHash() && Items.getInstance().getItemOwner(tested_item).equals(owner) && tested_item.getCanEquip()) {
                        tested_item.setIsEquipped(false);
                        item.setIsEquipped(true);
                        break;
                    }
                }
                p_inner.onSuccess();
            }

            @Override
            public void onError(String result) {
                p_inner.onError(result);
            }
        });
        return p_inner;
    }



    private static Promise moveToVault(final ClientWebView webView, final String owner, final Item item, final int stackSize, final Promise p) {
        JSONObject obj = generateMoveJson(owner, item, true, stackSize);
        final Promise p_inner = new Promise();
        if (item.isEquipped()) {
            /* find items from the same hash */
            List<Item> proposed = new ArrayList<>();
            String item_owner = Items.getInstance().getItemOwner(item);
            for (Item tested_item : Items.getInstance().all()) {
                if (tested_item.getBucketTypeHash() == item.getBucketTypeHash() && item.getItemHash() != tested_item.getItemHash() && Items.getInstance().getItemOwner(tested_item).equals(item_owner) && tested_item.getCanEquip()) {
                    proposed.add(tested_item);
                }
            }
            Collections.sort(proposed);


            if (proposed.size() > 0) {
                final Item to_equip = proposed.get(proposed.size() - 1);
                equip(webView, owner, to_equip, p).then(new Result() {
                    @Override
                    public void onSuccess() {
                        item.setIsEquipped(false);
                        to_equip.setIsEquipped(true);
                        moveToVault(webView, owner, item, stackSize, p).then(new Result() {
                            @Override
                            public void onSuccess() {
                                p_inner.onSuccess();

                            }

                            @Override
                            public void onError(String e) {
                                p_inner.onError(e);
                            }
                        });

                    }

                    @Override
                    public void onError(String e) {
                        p.onError(e);
                        p_inner.onError(e);
                    }
                });
            } else {
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String message = webView.getContext().getString(R.string.cannot_move_error, item.toString());
                        p_inner.onError(message);
                        if (p != null) {
                            p.onError(message);
                        } else {
                            Log.e(LOG_TAG, message);
                        }
                    }
                }, 1);
            }
            return p_inner;
        }

        webView.call("destinyService.TransferItem", obj).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                webView.queueRunnable(new Runnable() {
                    @Override
                    public void run() {
                        item.moveTo(Items.VAULT_ID, stackSize);
                        if (p != null)
                            p.onSuccess();
                        p_inner.onSuccess();
                    }
                });
            }

            @Override
            public void onError(String result) {

                try {
                    if (new JSONObject(result).optInt("errorCode") == 1656) {
                        // fix equipped status
                        String item_owner = Items.getInstance().getItemOwner(item);
                        for (Item tested_item : Items.getInstance().all()) {
                            if (tested_item.getBucketTypeHash() == item.getBucketTypeHash() && item.getItemHash() != tested_item.getItemHash() && Items.getInstance().getItemOwner(tested_item).equals(item_owner) && tested_item.isEquipped()) {
                                tested_item.setIsEquipped(false);
                            }
                        }
                        item.setIsEquipped(true);
                        moveToVault(webView, owner, item, stackSize, p_inner);
                    } else {
                        if (p != null)
                            p.onError(result);
                        p_inner.onError(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return p_inner;
    }

    private static Promise moveFromVault(final ClientWebView webView, final String subject, final Item item, final int stackSize, final Promise p) {
        JSONObject obj = generateMoveJson(subject, item, false, stackSize);
        final Promise p_inner = new Promise();
        webView.call("destinyService.TransferItem", obj).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                webView.queueRunnable(new Runnable() {
                    @Override
                    public void run() {
                        item.moveTo(subject, stackSize);
                        p.onSuccess();
                        p_inner.onSuccess();
                    }
                });
            }

            @Override
            public void onError(String result) {
                p.onError(result);
                p_inner.onError(result);
            }
        });
        return p_inner;
    }


    public static Promise move(final ClientWebView webView, final Item item, final String subject, final int stackSize) {
        final Promise p = new Promise();

        final String owner = Items.getInstance().getItemOwner(item);
        if ((owner.equals(Items.VAULT_ID) || subject.equals(Items.VAULT_ID))) {
            if (subject.equals(Items.VAULT_ID)) {
                moveToVault(webView, owner, item, stackSize, p);
            } else {
                moveFromVault(webView, subject, item, stackSize, p);
            }
        } else {
            moveToVault(webView, owner, item, stackSize, null).then(new Result() {
                @Override
                public void onSuccess() {
                    Item vault_item = null;
                    for (Item tmp_item : Items.getInstance().allAsMap().get(Items.VAULT_ID)) {
                        if (tmp_item.getItemHash() == item.getItemHash() && tmp_item.getItemId() == item.getItemId()) {
                            vault_item = tmp_item;
                            break;
                        }
                    }
                    final Item tmp_item = vault_item;
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveFromVault(webView, subject, tmp_item, stackSize, p);
                        }
                    }, 1000);
                }

                @Override
                public void onError(String e) {

                    p.onError(e);
                }
            });

        }


        return p;
    }

    private static JSONObject generateMoveJson(String characterId, Item item, boolean transferToVault, int stackSize) {
        JSONObject json = new JSONObject();
        try {
            json.put("characterId", characterId);
            json.put("itemId", item.getItemId());
            json.put("itemReferenceHash", item.getItemHash());
            json.put("membershipType", Data.getInstance().getMembership().getType());
            json.put("stackSize", stackSize);
            json.put("transferToVault", transferToVault);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "exception", e);
            e.printStackTrace();
        }
        return json;
    }

    private static JSONObject generateMoveJson(String characterId, String itemId, long itemReferenceHash, int membershipType, boolean transferToVault, int stackSize) {
        JSONObject json = new JSONObject();
        try {
            json.put("characterId", characterId);
            json.put("itemId", itemId);
            json.put("itemReferenceHash", itemReferenceHash);
            json.put("membershipType", membershipType);
            json.put("stackSize", stackSize);
            json.put("transferToVault", transferToVault);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "exception", e);
            e.printStackTrace();
        }
        return json;
    }

    public interface Result {
        public void onSuccess();

        public void onError(String e);
    }

    public static class Promise {
        private Result callback;

        public void then(Result callback) {
            this.callback = callback;
        }

        public void onSuccess() {
            if (callback != null)
                callback.onSuccess();
        }

        public void onError(String e) {
            if (callback != null) {
                callback.onError(e);
            }
        }
    }
}
