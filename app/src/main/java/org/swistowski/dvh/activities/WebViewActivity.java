package org.swistowski.dvh.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.swistowski.dvh.R;
import org.swistowski.dvh.activities.LoginActivity;

public class WebViewActivity extends Activity {
    public static final int RESULT_RELOAD = 123;
    private static final String LOG_TAG = "WebViewActivity";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        String url = intent.getStringExtra(LoginActivity.URL);
        getWebView().loadUrl(url);
    }


    WebView getWebView() {
        if (webView == null) {
            webView = (WebView) findViewById(R.id.webView);
           // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || true) {
                /* fallback, i can get headers in normal way only in lollipop */
                class JsObject {
                    @JavascriptInterface
                    public void pageFinished() {
                        pageLoaded();
                    }
                }
                webView.addJavascriptInterface(new JsObject(), "INTERFACE");
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // call the pageLoaded method with javascript
                        Log.v(LOG_TAG, "url loaded: " + url);
                        // view.loadUrl("javascript:window.INTERFACE.pageFinished()");
                        super.onPageFinished(view, url);
                        if(url.equals("https://www.bungie.net/")){
                            view.loadUrl("javascript:window.INTERFACE.pageFinished()");
                        }
                    }
                });
            /*
            } else {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        WebResourceResponse ret = super.shouldInterceptRequest(view, request);
                        if (request.getUrl().toString().startsWith("https://www.bungie.net/Platform/User/GetBungieNetUser/")) {
                            pageLoaded();
                        }
                        return ret;
                    }
                });
            }
            */

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }
        return webView;
    }

    private void pageLoaded() {
        Intent resultIntent = new Intent();
        setResult(RESULT_RELOAD, resultIntent);
        finish();
    }
}
