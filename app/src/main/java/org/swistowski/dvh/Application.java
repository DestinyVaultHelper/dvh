package org.swistowski.dvh;

import org.swistowski.dvh.util.ImageStorage;

public class Application extends android.app.Application {
    private ClientWebView mWebView;

    @Override
    public void onCreate()
    {
        mWebView = new ClientWebView(getApplicationContext());
        ImageStorage.getInstance().setContext(getApplicationContext());
        super.onCreate();
    }

    public ClientWebView getWebView() {
        return mWebView;
    }
}
