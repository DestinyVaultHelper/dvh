package org.swistowski.dvh;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.swistowski.dvh.util.Data;
import org.swistowski.dvh.util.ImageStorage;
import org.swistowski.dvh.views.ClientWebView;

public class Application extends android.app.Application {
    private Tracker mTracker;
    private ClientWebView mWebView;

    @Override
    public void onCreate()
    {
        mWebView = new ClientWebView(getApplicationContext());
        ImageStorage.getInstance().setContext(getApplicationContext());
        Data.getInstance().setContext(getApplicationContext());
        super.onCreate();
    }

    public ClientWebView getWebView() {
        return mWebView;
    }


    public synchronized Tracker getTracker() {
        if(mTracker==null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
