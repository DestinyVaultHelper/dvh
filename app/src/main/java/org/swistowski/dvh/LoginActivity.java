package org.swistowski.dvh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;


public class LoginActivity extends ActionBarActivity {
    public final static String URL = "org.swistowski.destinyshelve.URL";
    public final static int LOGIN_REQUEST = 60;
    private static final String PSN_URL = "https://www.bungie.net/en/User/SignIn/Psnid";
    private static final String XONE_URL = "https://www.bungie.net/en/User/SignIn/Wlid";
    private static final String LOG_TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    void goToUrl(String url){
        Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
        i.putExtra(URL, url);
        startActivityForResult(i, LOGIN_REQUEST);
    }

    public void onPSNButtonClick(View view){
        goToUrl(PSN_URL);
    }

    public void onOneButtonClick(View view){
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
