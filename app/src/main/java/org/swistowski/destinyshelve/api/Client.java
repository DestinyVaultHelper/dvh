package org.swistowski.destinyshelve.api;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

/**
 * Created by damian on 11.03.15.
 */

public class Client {
    final String CLIENT_TAG = "Client log";
    public interface Callback{
        public void onAccept(JSONObject result);
        public void onError(JSONObject result);
    }
    private class Promise{
        public void then(Callback callback){

        }
    }

    private WebView mWebView;
    public Client(WebView webView, final Runnable onInit){
        mWebView = webView;

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v("Page finished", url);
                onInit.run();
                super.onPageFinished(view, url);
            }
        });
    }
    public Promise call(String methodName, String...arguments){
        Log.v(CLIENT_TAG, "calling client: "+methodName+" "+arguments.toString());
        Promise p = new Promise();
        return p;
    }
}
