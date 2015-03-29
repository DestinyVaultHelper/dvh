package org.swistowski.dvh.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.identityHashCode;

public class ClientWebView extends WebView {
    private final String LOG_TAG = "ClientWebView";
    private boolean mPrepared = false;
    private boolean mInitialized = false;
    private final Map<String, Promise> mPromises = new HashMap<String, Promise>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public ClientWebView(Context context) {
        super(context);
        init(context);
    }

    public ClientWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClientWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    @SuppressLint("AddJavascriptInterface")
    public void prepare(final Runnable onInit) {
        Log.v("ClientWebView", "ready = true");
        if (!mPrepared) {
            mPrepared = true;
            setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (!mInitialized) {
                        mInitialized = true;
                        onInit.run();
                    }
                    super.onPageFinished(view, url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.v(LOG_TAG, "onReceivedError");
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }
            });
            getSettings().setJavaScriptEnabled(true);
            addJavascriptInterface(new JsObject(mPromises), "clientHandler");
            loadUrl("https://www.bungie.net/");
        } else
            onInit.run();
    }

    public boolean isPrepared() {
        return mPrepared;
    }

    public Promise call(String methodName, JSONObject parameters) {
        final Promise p = new Promise();
        final int promiseId = identityHashCode(p);
        Log.v(LOG_TAG, "promise created JSON: "+promiseId);
        mPromises.put("" + promiseId, p);
        queueJavascript("bungieNetPlatform." + methodName + "(" + parameters.toString() + " , function(data){window.clientHandler.accept(\"" + promiseId + "\", JSON.stringify(data))}, function(data){window.clientHandler.error(\"" + promiseId + "\", JSON.stringify(data))})");
        return p;
    }

    public Promise call(String methodName, String... arguments) {
        final Promise p = new Promise();
        final int promiseId = identityHashCode(p);

        String tmp = "";
        for (String argument : arguments) {
            tmp += argument + " ";
        }
        String processedArguments = "";
        for (String arg : arguments) {
            if (!processedArguments.equals("")) {
                processedArguments += ",";
            }
            processedArguments += '"' + arg + '"';
        }
        mPromises.put("" + promiseId, p);
        Log.v(LOG_TAG, "promise created STRING: "+promiseId + " " + processedArguments);
        queueJavascript("bungieNetPlatform." + methodName + "(" + processedArguments + (!processedArguments.equals("") ? ", " : "") + "function(data){window.clientHandler.accept(" +'"' + promiseId + '"' +", JSON.stringify(data))}, function(data){window.clientHandler.error(\"" + promiseId + "\", JSON.stringify(data))})");
        return p;
    }

    private void queueJavascript(final String javascript) {
        Log.v(LOG_TAG, "queue javascript: " + javascript);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT < 19) {
                    loadUrl("javascript:" + javascript);
                } else {
                    evaluateJavascript("javascript:" + javascript, null);
                }
            }
        });
    }

    public void queueRunnable(Runnable r) {
        mHandler.post(r);
    }

    public interface Callback {
        public void onAccept(String result);

        public void onError(String result);
    }

    private class JsObject {
        private final Map<String, Promise> mPromises;

        public JsObject(Map<String, Promise> promises) {
            mPromises = promises;
        }

        @JavascriptInterface
        public void accept(String promiseId, String result) {
            Log.v(LOG_TAG, "interface accept " + promiseId + " " + result);
            mPromises.get(promiseId).accept(result);
            mPromises.remove(promiseId);
        }

        @JavascriptInterface
        public void error(String promiseId, String result) {
            Log.v(LOG_TAG, "interface error " + promiseId);
            Log.v(LOG_TAG, "error value"+ result);
            mPromises.get(promiseId).error(result);
            mPromises.remove(promiseId);
        }
    }

    public class Promise {
        private Callback mCallback;

        public void then(Callback callback) {
            mCallback = callback;
        }

        public void accept(String result) {
            mCallback.onAccept(result);
        }

        public void error(String result) {
            mCallback.onError(result);
        }
    }
}
