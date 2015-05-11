package org.swistowski.vaulthelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.swistowski.vaulthelper.Application;
import org.swistowski.vaulthelper.R;


public class LoginActivity extends ActionBarActivity {
    public final static String URL = "org.swistowski.destinyshelve.URL";
    public final static int LOGIN_REQUEST = 60;
    private static final String PSN_URL_DEFAULT = "https://www.bungie.net/en/User/SignIn/Psnid";
    private String psn_url = PSN_URL_DEFAULT;
    private String xone_url = XONE_URL_DEFAULT;
    private static final String XONE_URL_DEFAULT = "https://www.bungie.net/en/User/SignIn/Xuid";
    private static final String LOG_TAG = "LoginActivity";
    private static final String XONE_URL_ID = "Xone";
    private static final String PSN_URL_ID = "psn";

    public static void goLogin(Activity c, String xBoxUrl, String psnUrl){
        Intent intent = new Intent(c, LoginActivity.class);
        intent.putExtra(XONE_URL_ID, xBoxUrl);
        intent.putExtra(PSN_URL_ID, psnUrl);
        c.startActivityForResult(intent, LoginActivity.LOGIN_REQUEST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = intent.getStringExtra(XONE_URL_ID);
        if(url!=null){
            xone_url = url;
        }
        url = intent.getStringExtra(PSN_URL_ID);
        if(url!=null){
            psn_url = url;
        }

        setContentView(R.layout.activity_login);
        TextView loginInformation = (TextView)findViewById(R.id.loginDetailInformation);
        loginInformation.setText(Html.fromHtml(getString(R.string.login_information)));
    }

    void goToUrl(String url){
        Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
        i.putExtra(URL, url);
        startActivityForResult(i, LOGIN_REQUEST);
    }

    private Tracker getTracker(){
        return  ((Application) getApplication()).getTracker();
    }

    public void onPSNButtonClick(View view){
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.tracker_category_user_action))
                .setAction(getString(R.string.tracker_action_click))
                .setLabel("Psn button")
                .build());
        goToUrl(psn_url);
    }

    public void onOneButtonClick(View view) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.tracker_category_user_action))
                .setAction(getString(R.string.tracker_action_click))
                .setLabel("One button")
                .build());
        Log.v(LOG_TAG, "go to: "+xone_url);
        goToUrl(xone_url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult requestCode: " + requestCode + " resultCode: " + resultCode);
        if(requestCode == LOGIN_REQUEST) {
            Intent resultIntent = new Intent();
            setResult(resultCode, resultIntent);
            finish();
        }
    }
}
