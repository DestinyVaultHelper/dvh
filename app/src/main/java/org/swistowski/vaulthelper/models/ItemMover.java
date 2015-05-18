package org.swistowski.vaulthelper.models;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.vaulthelper.Application;
import org.swistowski.vaulthelper.util.Data;
import org.swistowski.vaulthelper.views.ClientWebView;
import org.swistowski.vaulthelper.fragments.ItemListFragment;

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
        Log.v(LOG_TAG, "equip "+obj.toString());

        webView.call("destinyService.EquipItem", obj).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                for (Item tested_item : Data.getInstance().getAllItems()) {
                    if (tested_item.getBucketTypeHash() == item.getBucketTypeHash() && item.getItemHash() != tested_item.getItemHash() && Data.getInstance().getItemOwner(tested_item).equals(owner) && tested_item.getCanEquip()) {
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

    private static Promise moveToVault(final ClientWebView webView, final String owner, final Item item, final Promise p) {
        JSONObject obj = generateMoveJson(owner, item, true);
        final Promise p_inner = new Promise();
        if (item.isEquipped()) {
            /* find items from the same hash */
            List<Item> proposed = new ArrayList<>();
            String item_owner = Data.getInstance().getItemOwner(item);
            for (Item tested_item : Data.getInstance().getAllItems()) {
                if (tested_item.getBucketTypeHash() == item.getBucketTypeHash() && item.getItemHash() != tested_item.getItemHash() && Data.getInstance().getItemOwner(tested_item).equals(item_owner) && tested_item.getCanEquip()) {
                    Log.v(LOG_TAG, "other: " + tested_item + Data.getInstance().getItemOwner(tested_item));
                    proposed.add(tested_item);
                }
            }
            Collections.sort(proposed);


            if(proposed.size()>0){
                final Item to_equip = proposed.get(proposed.size()-1);
                equip(webView, owner, to_equip, p).then(new Result() {
                    @Override
                    public void onSuccess() {
                        item.setIsEquipped(false);
                        to_equip.setIsEquipped(true);
                        moveToVault(webView, owner, item, p).then(new Result() {
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
                        p.onError("Cannot move equipped item, Do not know how to unequip " + item);
                    }
                }, 1);
            }
            return p_inner;
        }
        Log.v(LOG_TAG, "item: " + item.isEquipped());
        webView.call("destinyService.TransferItem", obj).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                webView.queueRunnable(new Runnable() {
                    @Override
                    public void run() {
                        item.moveTo(Data.VAULT_ID);
                        if(p!=null)
                            p.onSuccess();
                        p_inner.onSuccess();
                    }
                });
            }

            @Override
            public void onError(String result) {
                try {
                    if(new JSONObject(result).optInt("errorCode")==1656){
                        // do reload database
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(p!=null)
                    p.onError(result);
                p_inner.onError(result);
            }
        });
        return p_inner;
    }

    private static Promise moveFromVault(final ClientWebView webView, final String subject, final Item item, final Promise p) {
        JSONObject obj = generateMoveJson(subject, item, false);
        final Promise p_inner = new Promise();
        webView.call("destinyService.TransferItem", obj).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                webView.queueRunnable(new Runnable() {
                    @Override
                    public void run() {
                        item.moveTo(subject);
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


    public static Promise move(final ClientWebView webView, final Item item, final String subject) {
        final Promise p = new Promise();

        final String owner = Data.getInstance().getItemOwner(item);
        if ((owner.equals(Data.VAULT_ID) || subject.equals(Data.VAULT_ID))) {
            if (subject.equals(Data.VAULT_ID)) {
                moveToVault(webView, owner, item, p);
            } else {
                moveFromVault(webView, subject, item, p);
            }
        } else  {
            moveToVault(webView, owner, item, null).then(new Result() {
                @Override
                public void onSuccess() {
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveFromVault(webView, subject, item, p);
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

    private static JSONObject generateMoveJson(String characterId, Item item, boolean transferToVault) {
        JSONObject json = new JSONObject();
        try {
            json.put("characterId", characterId);
            json.put("itemId", item.getItemId());
            json.put("itemReferenceHash", item.getItemHash());
            json.put("membershipType", Data.getInstance().getMembership().getType());
            json.put("stackSize", item.getStackSize());
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
