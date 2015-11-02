package org.swistowski.vaulthelper.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;
import org.swistowski.vaulthelper.R;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.identityHashCode;

public class ClientWebView extends WebView {
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public interface ErrorHandler {
        boolean processError(ConsoleMessage cm);
    }

    private ErrorHandler errorHandler;
    private final String LOG_TAG = "ClientWebView";
    private boolean mPrepared = false;
    private boolean mInitialized = false;
    private final Map<String, Promise> mPromises = new HashMap<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Activity currentActivity;

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

        if (!mPrepared) {

            mPrepared = true;
            setWebChromeClient(new WebChromeClient() {
                public boolean onConsoleMessage(ConsoleMessage cm) {
                    if (errorHandler != null) {
                        errorHandler.processError(cm);
                    }
                    if (errorHandler == null)
                        Log.d(LOG_TAG, "Does not have error handler");
                    else
                        Log.d(LOG_TAG, "Have error handler");
                    Log.d(LOG_TAG + " js", cm.message() + " -- From line "
                            + cm.lineNumber() + " of "
                            + cm.sourceId());
                    return true;
                }
            });

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
                    if (currentActivity != null) {
                        new AlertDialog.Builder(currentActivity).setTitle(getContext().getString(R.string.critical_error)).setMessage(
                                String.format(getContext().getString(R.string.no_url_error), failingUrl)
                        ).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.exit(0);
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        System.exit(0);
                    }

                    super.onReceivedError(view, errorCode, description, failingUrl);
                }
            });
            getSettings().setJavaScriptEnabled(true);
            addJavascriptInterface(new JsObject(mPromises), "clientHandler");
            loadUrl("http://www.bungie.net/");

        } else
            onInit.run();

    }


    public boolean isPrepared() {
        return mPrepared;
    }

    public Promise callAny(String javascript) {
        final Promise p = new Promise();
        final int promiseId = identityHashCode(p);
        mPromises.put("" + promiseId, p);
        queueJavascript("window.clientHandler.accept(\"" + promiseId + "\", " + javascript + ")");
        return p;
    }

    public Promise call(String methodName, JSONObject parameters) {
        final Promise p = new Promise();
        final int promiseId = identityHashCode(p);
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
        queueJavascript("bungieNetPlatform." + methodName + "(" + processedArguments + (!processedArguments.equals("") ? ", " : "") + "function(data){window.clientHandler.accept(" + '"' + promiseId + '"' + ", JSON.stringify(data))}, function(data){window.clientHandler.error(\"" + promiseId + "\", JSON.stringify(data))})");
        return p;
    }

    private void queueJavascript(final String javascript) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                loadUrl("javascript:" + "window.clientHandler.log(\"\"+ " + javascript + ")");
            }
        });
    }

    public void queueRunnable(Runnable r) {
        mHandler.post(r);
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
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
            mPromises.get(promiseId).accept(result);
            mPromises.remove(promiseId);
        }

        @JavascriptInterface
        public void error(String promiseId, String result) {
            mPromises.get(promiseId).error(result);
            mPromises.remove(promiseId);
        }

        @JavascriptInterface
        public boolean log(String data) {
            return true;
        }

        public String getString(String value) {
            return value;
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
