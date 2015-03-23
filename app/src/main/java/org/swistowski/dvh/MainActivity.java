package org.swistowski.dvh;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.joshdholtz.sentry.Sentry;

import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.models.ItemMover;
import org.swistowski.dvh.util.DatabaseLoader;


public class MainActivity extends FragmentActivity implements ItemListFragment.OnItemIterationListener, SettingsFragment.OnSettingsIterationListener {
    private static final String LOG_TAG = "MainActivity";

    private FragmentStatePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private boolean mIsLoading = false;

    void setIsLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getWebView().isPrepared()) {
            setIsLoading(true);
            getWebView().prepare(new Runnable() {
                @Override
                public void run() {
                    Log.v(LOG_TAG, "web view initialized");
                    reloadDatabase();
                }
            });
        }
        Sentry.init(this.getApplicationContext(), "https://0bb761867bfb4aa69bbaa247a2fe4862:e9ef23acb2094494a8a452d526a46513@app.getsentry.com/40292");
        initUI();

    }

    void initUI() {
        Log.v(LOG_TAG, "initUI");
        if (!mIsLoading) {
            setContentView(R.layout.activity_items_preview);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ItemsFragmentPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);
        } else {
            setContentView(R.layout.layout_waiting);
        }
    }

    private ClientWebView getWebView() {
        return ((Application) getApplication()).getWebView();
    }


    @Override
    public void onButtonPressed(int ButtonId) {
        Intent intent;
        switch (ButtonId) {
            case R.id.button_login:
                goLogin();
                return;
            case R.id.button_reload:
                Database.getInstance().cleanCharacters();
                setIsLoading(true);
                initUI();
                reloadDatabase();
                return;
            case R.id.button_logout:
                Database.getInstance().clean();
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(LoginActivity.URL, "https://www.bungie.net/en/User/SignOut");
                startActivityForResult(intent, LoginActivity.LOGIN_REQUEST);
                return;
        }
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LoginActivity.LOGIN_REQUEST);
    }

    private void reloadDatabase() {
        (new DatabaseLoader(this, getWebView(), Database.getInstance())).process(new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "web view data loaded");
                setIsLoading(false);
                initUI();
            }
        }, new Runnable() {
            @Override
            public void run() {
                Log.v(LOG_TAG, "i'm not logged in");
                goLogin();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //webView.post()
        initUI();
    }

    @Override
    public void onItemClicked(ItemListFragment fragment, final Item item, String subject, int direction) {
        ItemMover.move(getWebView(), item, direction, subject).then(
                new ItemMover.Result() {
                    @Override
                    public void onSuccess() {
                        Log.v(LOG_TAG, "Move success " + item);

                    }

                    @Override
                    public void onError(String e) {
                        onMoveError(e);
                    }
                }
        );

        Log.v(LOG_TAG, item.toString() + " clicked");
    }

    private void onMoveError(String e) {
        String message;
        try {
            message = new JSONObject(e).optString("errorMessage");
        } catch (JSONException e1) {
            message = e;
        }
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onItemLongClicked(ItemListFragment fragment, Item item, String subject, int direction) {
        Intent intent = new Intent(this, ItemDetailActivity.class);
        Bundle b = new Bundle();
        b.putSerializable(ItemDetailActivity.ITEM, item);
        intent.putExtras(b);
        startActivity(intent);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult requestCOde: " + requestCode + " resultCode" + resultCode);
        if (requestCode == LoginActivity.LOGIN_REQUEST) {
            if (resultCode == WebViewActivity.RESULT_RELOAD) {
                reloadDatabase();
            }

        }
    }
}
