package org.swistowski.destinyshelve;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static org.swistowski.destinyshelve.LoginActivity.CSRF;


public class WebViewActivity extends ActionBarActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        String url = intent.getStringExtra(LoginActivity.URL);
        getWebView().loadUrl(url);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
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

    protected WebView getWebView() {

        if (webView == null) {

            webView = (WebView) findViewById(R.id.webView);


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || true) {
                /* fallback, i can get headers in normal way only in lollipop */

                class JsObject {
                    @JavascriptInterface
                    public void processCSRF(String csrf) {
                        gotCSRF(csrf);
                    }
                }
                webView.addJavascriptInterface(new JsObject(), "INTERFACE");
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // call the gotCSRF method with javascript
                        view.loadUrl("javascript:window.INTERFACE.processCSRF(bungieNetPlatform.getCSRFHeader()['x-csrf'])");
                        super.onPageFinished(view, url);
                    }
                });
            } else {
                /* it's faster wait to for request to api, and get csrf directly from headers */
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        WebResourceResponse ret = super.shouldInterceptRequest(view, request);
                        if (request.getUrl().toString().startsWith("https://www.bungie.net/Platform/User/GetBungieNetUser/")) {
                            //Log.v("cookies get", request.getRequestHeaders().toString());
                            String cookie =  CookieManager.getInstance().getCookie("http://www.bungie.net");
                            if(cookie!=null) {
                                Log.v("cookies", cookie);
                            } else {
                                Log.v("cookies", "no cookies");
                            }
                            //gotCSRF(request.getRequestHeaders().get("x-csrf"));
                            if(ret!=null) {
                                Log.v("response headers", ret.toString());
                            } else {
                                Log.v("response headers", "ret is null");
                            }
                        }


                        //Log.v("response headers", ret.toString());
                        return ret;
                    }
                });
            }


            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }
        return webView;
    }

    private void gotCSRF(String csrf) {
        Log.v("foo bar", "got csrf "+csrf);
        Intent resultIntent = new Intent();
        resultIntent.putExtra(CSRF, csrf);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
