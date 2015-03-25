package org.swistowski.dvh.util;

import android.app.Activity;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.dvh.ClientWebView;
import org.swistowski.dvh.Database;
import org.swistowski.dvh.models.Character;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.models.Membership;

import java.util.List;

public class DatabaseLoader {
    private final String LOG_TAG = "DatabaseLoader";
    private final Activity act;
    private final ClientWebView webView;
    private final Database database;
    private Runnable onFinish;
    private Runnable onError;
    private Callback onMessage;



    public interface Callback{
        public void onMessage(String message);
    }

    public DatabaseLoader(Activity act, ClientWebView webView, Database database) {
        this.act = act;
        this.webView = webView;
        this.database = database;
    }

    public void process(Runnable onFinish, Runnable onError, Callback onMessage){
        this.onFinish = onFinish;
        this.onError = onError;
        this.onMessage = onMessage;
        doLoadAllData();
    }

    public void process(Runnable runnable) {
        process(runnable, runnable, new Callback() {
            @Override
            public void onMessage(String message) {

            }
        });
    }

    private void doLoadAllData() {
        Log.v(LOG_TAG, "doLoadAllData");
        if (database.getUser() == null) {
            doGetCurrentUser();
            return;
        }
        if (database.getMembership() == null) {
            doSearchDestinyPlayer(""+database.getUser().getAccountType(), database.getUser().getDisplayName());
            return;
        }
        if (database.getCharacters() == null) {
            doGetAccount(database.getMembership());
            return;
        }
        if (database.getItems().size() == 0) {
            doLoadItems(database.getMembership(), database.getCharacters());
            return;
        }

        if(onFinish!=null){
            onFinish.run();
        }
    }

    void doGetCurrentUser() {
        onMessage.onMessage("Loading User");
        Log.v(LOG_TAG, "doGetCurrentUser");
        // getting current user from bungie
        webView.call("userService.GetCurrentUser").then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                Log.v(LOG_TAG, "doGetCurrentUser in service");
                try {
                    database.loadUserFromJson(new JSONObject(result));
                    Log.v(LOG_TAG, "doGetCurrentUser database loaded");
                    doLoadAllData();
                } catch (JSONException e) {
                    onError("Cannot get user data");
                }
                Log.v(LOG_TAG, "got displayname: " + Database.getInstance().getUser().getDisplayName());
            }

            @Override
            public void onError(String result) {
                Log.v(LOG_TAG, "doGetCurrentUser error");
                onError.run();
            }
        });
    }
    void doSearchDestinyPlayer(String type, String displayName) {
        onMessage.onMessage("Searching your account");
        Log.v(LOG_TAG, "doSearchDestinyPlayer "+type+" "+displayName);
        // know that i'm logged in, search me as the player
        webView.call("destinyService.SearchDestinyPlayer", type, displayName).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                Log.v(LOG_TAG, "doSearchDestinyPlayer onAccept");
                //JSONObject json = null;
                try {
                    Database.getInstance().loadMembershipFromJson(new JSONArray(result).getJSONObject(0));
                    Log.v(LOG_TAG, "doSearchDestinyPlayer loaded");
                    doLoadAllData();
                } catch (JSONException e) {
                    onError("Cannot get membership data");
                    //Sentry.captureException(e);
                    //e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Log.e(LOG_TAG, "doSearchDestinyPlayer unsucessfull");
                onError.run();
            }
        });
    }

    void doGetAccount(final Membership membership) {
        onMessage.onMessage("Downloading information about your account");
        Log.v("MainActivity", "doGetAccount");
        // i know my membershipId, so i'm destiny player, get my characters
        webView.call("destinyService.GetAccount", membership.getTigerType(), "" + membership.getId(), "true").then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                Log.v(LOG_TAG, "account fetched");
                try {
                    Database.getInstance().loadCharactersFromJson(new JSONObject(result).getJSONObject("data").getJSONArray("characters"));
                    doLoadAllData();
                } catch (JSONException e) {
                    onError("Cannot get account data");
                }
            }

            @Override
            public void onError(String result) {

                Log.e(LOG_TAG, "doGetAccount unsucessfull " + result);
                onError.run();
            }
        });
    }

    private void doLoadItems(Membership membership, List<Character> characters) {
        onMessage.onMessage("Loading your items");
        Log.v(LOG_TAG, "doLoadItems");
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
        Log.v(LOG_TAG, "doGetCharacterInventory");
        onMessage.onMessage("Loading inventory for "+character);
        webView.call("destinyService.GetCharacterInventory", "" + membership.getType(), "" + membership.getId(), character.getId(), "true").then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                Log.v("Got character info", result);
                try {
                    Database.getInstance().putItems(character.getId(), Item.fromJson(result));
                } catch (JSONException e) {
                    onError("Cannot get character inventory data");
                }
                waiter.decrease();
            }

            @Override
            public void onError(String result) {
                Log.e("doGetCharacterInventory", "unsucessfull " + result);
                onError.run();
            }
        });
    }


    private void doGetVaultInventory(final Membership membership, final WaitForAll waiter) {
        onMessage.onMessage("Loading vault inventory");
        Log.v(LOG_TAG, "doGetVaultInventory");
        webView.call("destinyService.GetVault", "" + membership.getType(), "true", membership.getId()).then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                try {
                    Database.getInstance().putItems(Database.VAULT_ID, Item.fromJson(result, true));
                } catch (JSONException e) {
                }
                waiter.decrease();
            }

            @Override
            public void onError(String result) {
                Log.v("MainActivity", "doGetVaultInventory fail");
                onError.run();
            }
        });
    }
}
