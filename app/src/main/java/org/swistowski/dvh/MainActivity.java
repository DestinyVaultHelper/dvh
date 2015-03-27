package org.swistowski.dvh;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.models.ItemMover;
import org.swistowski.dvh.util.DatabaseLoader;


public class MainActivity extends ActionBarActivity implements ItemListFragment.OnItemIterationListener, SettingsFragment.OnSettingsIterationListener{
    private static final String LOG_TAG = "MainActivity";

    private FragmentStatePagerAdapter mPagerAdapter;
    private DisableableViewPager mViewPager;
    private boolean mIsLoading = false;

    void setIsLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
        Log.v(LOG_TAG, "is loading: " + isLoading);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tracker t = getTracker();
        t.setScreenName("MainScreen");

        t.send(new HitBuilders.ScreenViewBuilder().build());

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
        initUI();
        if(getIntent()!=null){
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        //handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v(LOG_TAG, "Query was: " + query);
        }
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        return !ranBefore;
    }

    private void commitFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("RanBefore", true);
        editor.apply();
    }

    void initUI() {
        Log.v(LOG_TAG, "initUI");
        if (!mIsLoading) {
            setContentView(R.layout.items_tabs);
            mViewPager = (DisableableViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ItemsFragmentPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mPagerAdapter);
            if (isFirstTime()) {
                final View overflow = findViewById(R.id.tutorial_overflow);
                overflow.setVisibility(View.VISIBLE);
                overflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overflow.setVisibility(View.GONE);
                        commitFirstTime();
                    }
                });
                Log.v(LOG_TAG, "First time run");
            }
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
                getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.tracker_category_database))
                        .setAction(getString(R.string.tracker_action_loaded))
                        .setLabel("Success")
                        .build());
                Log.v(LOG_TAG, "web view data loaded");
                setIsLoading(false);
                initUI();
            }
        }, new DatabaseLoader.Callback() {
            @Override
            public void onMessage(final String message) {
                getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.tracker_category_database))
                        .setAction(getString(R.string.tracker_action_loaded))
                        .setLabel("Failure")
                        .build());
                String errorMesssage = "";
                try {
                    errorMesssage = new JSONObject(message).getString("errorMessage");
                } catch (JSONException e) {
                    errorMesssage = message;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage(errorMesssage)
                        .setTitle("Bungie Api error");
                Log.v(LOG_TAG, "error " + message);
                Log.v(LOG_TAG, "i'm not logged in");
                goLogin();
                builder.show();
            }
        }, new DatabaseLoader.Callback() {
            @Override
            public void onMessage(final String message) {
                Log.v("ON message", message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsLoading) {
                            ((TextView) findViewById(R.id.progress_text)).setText(message);
                        }
                    }
                });
            }
        });
    }

    private Tracker getTracker() {
        return ((Application) getApplication()).getTracker();
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

    @Override
    public void refreshRequest(final Runnable finished) {
        if (!getIsLoading()) {
            mViewPager.setDisabled(true);
            Database.getInstance().cleanCharacters();
            setIsLoading(true);
            (new DatabaseLoader(this, getWebView(), Database.getInstance())).process(new Runnable() {
                @Override
                public void run() {
                    setIsLoading(false);
                    mViewPager.setDisabled(false);
                    initUI();
                    finished.run();
                }
            });
        }
    }

    private boolean getIsLoading() {
        return mIsLoading;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult requestCOde: " + requestCode + " resultCode" + resultCode);
        if (requestCode == LoginActivity.LOGIN_REQUEST) {
            if (resultCode == WebViewActivity.RESULT_RELOAD) {
                reloadDatabase();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }


}
