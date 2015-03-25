package org.swistowski.dvh;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.swistowski.dvh.util.ImageStorage;

public class Application extends android.app.Application {
    private Tracker mTracker;
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


    synchronized Tracker getTracker() {
        if(mTracker==null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
