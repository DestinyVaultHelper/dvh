package org.swistowski.destinyshelve;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class LoginActivity extends ActionBarActivity {
    public final static String URL = "org.swistowski.destinyshelve.URL";
    public final static String CSRF = "org.swistowski.destinyshelve.URL";
    public final static int GET_CSRF_REQUEST = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
    protected void goToUrl(String url){
        Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
        i.putExtra(URL, url);
        startActivityForResult(i, GET_CSRF_REQUEST);
    }

    public void onPSNButtonClick(View view){
        goToUrl("https://www.bungie.net/en/User/SignIn/Psnid");
    }

    public void onOneButtonClick(View view){
        goToUrl("https://www.bungie.net/en/User/SignIn/Wlid");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_CSRF_REQUEST) {
            setResult(resultCode, data);
            finish();
        }
    }
}
