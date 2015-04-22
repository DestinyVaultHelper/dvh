package org.swistowski.dvh.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.swistowski.dvh.Application;
import org.swistowski.dvh.R;


public class LoginActivity extends ActionBarActivity {
    public final static String URL = "org.swistowski.destinyshelve.URL";
    public final static int LOGIN_REQUEST = 60;
    private static final String PSN_URL = "https://www.bungie.net/en/User/SignIn/Psnid";
    private static final String XONE_URL = "https://www.bungie.net/en/User/SignIn/Xuid";
    private static final String LOG_TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        goToUrl(PSN_URL);
    }

    public void onOneButtonClick(View view){
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.tracker_category_user_action))
                .setAction(getString(R.string.tracker_action_click))
                .setLabel("One button")
                .build());
        goToUrl(XONE_URL);
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
