package org.swistowski.vaulthelper.util;

import android.app.Activity;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.storage.Characters;
import org.swistowski.vaulthelper.views.ClientWebView;
import org.swistowski.vaulthelper.models.Character;
import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.models.Membership;

import java.util.List;

public class DataLoader {
    private final String LOG_TAG = "DatabaseLoader";
    private final Activity act;
    private final ClientWebView webView;
    private final Data data;
    private Runnable mOnFinish;
    private Callback mOnError;
    private Callback mOnMessage;


    public interface Callback{
        public void onMessage(String message);
    }

    public DataLoader(Activity act, ClientWebView webView, Data data) {
        this.act = act;
        this.webView = webView;
        this.data = data;
    }

    public void process(Runnable onFinish, Callback onError, Callback onMessage){
        mOnFinish = onFinish;
        mOnError = onError;
        mOnMessage = onMessage;
        doLoadAllData();
    }

    public void process(final Runnable runnable) {
        process(runnable, new Callback(){
            @Override
            public void onMessage(String message) {
                runnable.run();
            }
        }, new Callback() {
            @Override
            public void onMessage(String message) {

            }
        });
    }

    private void doLoadAllData() {
        Data.getInstance().setIsLoading(true);
        if (data.getUser() == null) {
            doGetCurrentUser();
            return;
        }
        if (data.getMembership() == null) {
            doSearchDestinyPlayer(""+ data.getUser().getAccountType(), data.getUser().getAccountName());
            return;
        }
        if (Characters.getInstance().all() == null) {
            doGetAccount(data.getMembership());
            return;
        }
        if (data.getItems().size() == 0) {
            doLoadItems(data.getMembership(), Characters.getInstance().all());
            return;
        }

        if(mOnFinish!=null){
            Data.getInstance().setIsLoading(false);
            mOnFinish.run();
        }
    }

    void doGetCurrentUser() {
        mOnMessage.onMessage(act.getString(R.string.loading_user_message));
        // getting current user from bungie
        webView.call("userService.GetCurrentUser").then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                try {
                    data.loadUserFromJson(new JSONObject(result));
                    doLoadAllData();
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "exception", e);
                    onError("Cannot get user data");
                }
            }

            @Override
            public void onError(String result) {
                mOnError.onMessage(result);
            }
        });
    }
    void doSearchDestinyPlayer(String type, String displayName) {
        mOnMessage.onMessage(act.getString(R.string.searching_your_account_message));
        // know that i'm logged in, search me as the player
        webView.call("destinyService.SearchDestinyPlayer", type, displayName).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                //JSONObject json = null;
                try {
                    Data.getInstance().loadMembershipFromJson(new JSONArray(result).getJSONObject(0));
                    doLoadAllData();
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "exception", e);
                    onError("Cannot get membership data");
                    //Sentry.captureException(e);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Log.e(LOG_TAG, "doSearchDestinyPlayer unsucessfull");
                mOnError.onMessage(result);
            }
        });
    }

    void doGetAccount(final Membership membership) {
        mOnMessage.onMessage(act.getString(R.string.downloading_message));
        // i know my membershipId, so i'm destiny player, get my characters
        webView.call("destinyService.GetAccount", membership.getTigerType(), "" + membership.getId(), "true").then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                try {
                    Characters.getInstance().loadFromJson(new JSONObject(result).getJSONObject("data").getJSONArray("characters"));
                    doLoadAllData();
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "exception", e);
                    onError("Cannot get account data");
                }
            }

            @Override
            public void onError(String result) {
                Log.e(LOG_TAG, "doGetAccount unsucessfull " + result);
                mOnError.onMessage(result);
            }
        });
    }

    private void doLoadItems(Membership membership, List<Character> characters) {
        mOnMessage.onMessage(act.getString(R.string.loading_your_items_message));
        WaitForAll waiter = new WaitForAll() {
            @Override
            public void finished() {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doLoadAllData();
                    }
                });

            }
        };
        for (Character c : characters) {
            waiter.increase();
            doGetCharacterInventory(membership, c, waiter);
        }
        waiter.increase();
        doGetVaultInventory(membership, waiter);

    }

    void doGetCharacterInventory(final Membership membership, final Character character, final WaitForAll waiter) {
        mOnMessage.onMessage(String.format(act.getString(R.string.loading_inventory_message), character));
        webView.call("destinyService.GetCharacterInventory", "" + membership.getType(), "" + membership.getId(), character.getId(), "true").then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                try {
                    Data.getInstance().putItems(character.getId(), Item.fromJson(result));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "exception", e);
                    onError("Cannot get character inventory data");
                }
                waiter.decrease();
            }

            @Override
            public void onError(String result) {
                Log.e("doGetCharacterInventory", "unsucessfull " + result);
                mOnError.onMessage(result);
            }
        });
    }


    private void doGetVaultInventory(final Membership membership, final WaitForAll waiter) {
        mOnMessage.onMessage(act.getString(R.string.loading_vault_inventory_message));
        webView.call("destinyService.GetVault", "" + membership.getType(), "true", membership.getId()).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                try {
                    Data.getInstance().putItems(Data.VAULT_ID, Item.fromJson(result, true));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "exception", e);
                }
                waiter.decrease();
            }

            @Override
            public void onError(String result) {
                mOnError.onMessage(result);
            }
        });
    }
}
