package org.swistowski.destinyshelve;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.destinyshelve.api.Client;
import org.swistowski.destinyshelve.models.Character;


public class MainActivity extends ActionBarActivity {
    private WebView webView;
    private boolean is_finished = false;
    private String csrf;
    private JSONObject user_data;

    private ProgressDialog pd;
    private String membershiId;
    private int membershipType;
    private Client client;

    private class Item {
        public Item(JSONObject data) throws JSONException {

        }
    }

    private ArrayList<Character> characters = new ArrayList<Character>();
    private HashMap<String, ArrayList<Item>> items = new HashMap<String, ArrayList<Item>>();

    protected WebView getWebView() {

        return webView;
    }

    protected void onBungieInitialized(){
        updateUserData();
    }

    protected void progressMessage(String message){
        if(pd==null) {
            pd = new ProgressDialog(this);
        }
        pd.setMessage(message);
        pd.show();
    }

    protected void loadUrl(String url){
        getWebView().loadUrl(url);
        progressMessage("Connecting with bungie");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (webView == null) {

            //webView = new WebView(this);

            //findViewById(R.layout.activity_main).addView(webView);

            webView = (WebView) findViewById(R.id.hiddenWebView);
            client = new Client(webView, new Runnable() {
                @Override
                public void run() {
                    onBungieInitialized();
                }
            });


            class JsObject {
                @JavascriptInterface
                public void userGet(String user){
                    try {
                        user_data = new JSONObject(user);
                        Log.v("user data", user_data.toString());

                        final String displayName = user_data.getJSONObject("user").getString("displayName");

                        getWebView().post(new Runnable() {
                            @Override
                            public void run() {
                                progressMessage("Fetching membership id");
                                getWebView().loadUrl("javascript:bungieNetPlatform.destinyService.SearchDestinyPlayer(\"all\", \""+displayName+"\", function(e){window.INTERFACE.setMembership(e[0].membershipId, e[0].membershipType)}, function(e){window.INTERFACE.error()})");
                            }
                        });
                    } catch(JSONException e){
                        e.printStackTrace();
                    }
                }

                @JavascriptInterface
                public void setMembership(String _membershiId, int _membershipType){
                    membershiId = _membershiId;
                    membershipType = _membershipType;
                    Log.v("Membership id", membershiId + " " + membershipType);

                    final String javascript = "bungieNetPlatform.destinyService.GetAccount(\""+(membershipType==2?"TigerPSN":"TigerXbox")+"\", \""+membershiId+"\", true, function(e){window.INTERFACE.setAccounts(JSON.stringify(e.data.characters))}, function(e){error(e)})";

                    getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            progressMessage("Getting information about your characters");
                            getWebView().loadUrl("javascript:"+javascript);
                        }
                    });
                }
                @JavascriptInterface
                public void setAccounts(String accounts){
                    Log.v("acconts", accounts);
                    try {
                        JSONArray array = new JSONArray(accounts);
                        for(int i=0;i<array.length();i++){
                            final Character c = new Character(array.getJSONObject(i));
                            Log.v("adding", c.toString());
                            characters.add(c);
                            final String javascript = "bungieNetPlatform.destinyService.GetCharacterInventory(\""+membershipType+"\", \""+membershiId+"\", \""+c.getCharacterId()+"\", true, function(e){window.INTERFACE.characterData(JSON.stringify(e), \""+c.getCharacterId()+"\")})";
                            Log.v("javascript: ", javascript);
                            getWebView().post(new Runnable() {
                                @Override
                                public void run() {
                                    progressMessage("Getting information about "+c.toString()+" items");
                                    getWebView().loadUrl("javascript:"+javascript);
                                }
                            });
                            // grap information about every character
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @JavascriptInterface
                public void characterData(final String data, final String currentCharactedId){
                    ArrayList<Item> list = new ArrayList<Item>();

                    JSONObject json = null;
                    try {
                        json = new JSONObject(data);

                        JSONObject buckets = json.getJSONObject("data").getJSONObject("buckets");
                        Iterator<String> i = buckets.keys();
                        while(i.hasNext()){
                            String key = i.next();
                            JSONArray bucket_content = buckets.getJSONArray(key);
                            for(int j=0;j<bucket_content.length();j++){
                                JSONArray bucket_items = bucket_content.getJSONObject(j).getJSONArray("items");
                                for(int k=0;k<bucket_items.length();k++){
                                    Log.v("key: ", key+ bucket_items.getJSONObject(k).toString());
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*
                    getWebView().post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v("got data: ", currentCharactedId + " " + data);
                        }
                    });
                    */
                };

                @JavascriptInterface
                public void error(){
                    goLogin();
                }
            }
            webView.addJavascriptInterface(new JsObject(), "INTERFACE");

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }

        loadUrl("http://www.bungie.net/");
    }

    private void updateUserData(){
        progressMessage("Loading player data");
        //getWebView().loadUrl("javascript:request = new XMLHttpRequest(); request.open('GET', 'https://www.bungie.net/Platform/User/GetBungieNetUser/');request.setRequestHeader('x-csrf', bungieNetPlatform.getCSRFHeader()['x-csrf']); request.onload = function(e){window.INTERFACE.userData(this.response)}; request.send()");
        getWebView().loadUrl("javascript:bungieNetPlatform.userService.GetCurrentUser(function(e){window.INTERFACE.userGet(JSON.stringify(e))}, function(e){ window.INTERFACE.error()})");
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(user_data==null){
            updateUserData();
        }
        /*
        if(csrf!=null){
            /*
            client = new Client(csrf);
            client.getUser(new Client.JSONResonseCallback() {

                @Override
                public void onAccept(JSONObject jsonObject) {
                    try{
                        if(jsonObject.getInt("ErrorCode")==99){
                            goLogin();
                        }
                    } catch (JSONException e){
                        Log.v("MainActivity", "no error code");
                    }
                }
            });

            getWebView().loadUrl("javascript:request = new XMLHttpRequest(); request.open('GET', 'https://www.bungie.net/Platform/User/GetBungieNetUser/');request.setRequestHeader('x-csrf', bungieNetPlatform.getCSRFHeader()['x-csrf']); request.onload = function(e){window.INTERFACE.getResponse(this.response)}; request.send()");

           goLogin();
            */

    }

    private void goLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LoginActivity.GET_CSRF_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==LoginActivity.GET_CSRF_REQUEST && resultCode== Activity.RESULT_OK){
            csrf = data.getStringExtra(LoginActivity.CSRF);
            Log.v("Activity result", "request csrf: "+ csrf);
        }

    }
}
