package org.swistowski.dvh;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.dvh.activities.ItemDetailActivity;
import org.swistowski.dvh.activities.LoginActivity;
import org.swistowski.dvh.activities.WebViewActivity;
import org.swistowski.dvh.atapters.ItemsFragmentPagerAdapter;
import org.swistowski.dvh.fragments.ItemListFragment;
import org.swistowski.dvh.fragments.SettingsFragment;
import org.swistowski.dvh.models.Character;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.models.ItemMover;
import org.swistowski.dvh.util.Database;
import org.swistowski.dvh.util.DatabaseLoader;
import org.swistowski.dvh.util.ImageStorage;
import org.swistowski.dvh.views.ClientWebView;
import org.swistowski.dvh.views.DisableableViewPager;


public class MainActivity extends ActionBarActivity implements ItemListFragment.OnItemIterationListener, SettingsFragment.OnSettingsIterationListener, ViewPager.OnPageChangeListener {
    private static final String LOG_TAG = "MainActivity";

    private FragmentStatePagerAdapter mPagerAdapter;
    private DisableableViewPager mViewPager;
    private PagerTabStrip mPageTabs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tracker t = getTracker();
        t.setScreenName("MainScreen");

        t.send(new HitBuilders.ScreenViewBuilder().build());

        if (!getWebView().isPrepared()) {
            Database.getInstance().setIsLoading(true);
            getWebView().prepare(new Runnable() {
                @Override
                public void run() {
                    Log.v(LOG_TAG, "web view initialized");
                    reloadDatabase();
                }
            });
        }
        setContentView(R.layout.items_tabs);
        initUI();
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
        if (!Database.getInstance().getIsLoading()) {

            mViewPager = (DisableableViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ItemsFragmentPagerAdapter(getSupportFragmentManager());
            mPageTabs = (PagerTabStrip) findViewById(R.id.pager_title_strip);
            mViewPager.setOnPageChangeListener(this);

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
            onPageSelected(0);
        } else {
            //setContentView(R.layout.layout_waiting);
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
                actionRefresh();
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
                Database.getInstance().setIsLoading(false);
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
                        if (Database.getInstance().getIsLoading()) {
                            Log.v(LOG_TAG, message);
                          //  ((TextView) findViewById(R.id.progress_text)).setText(message);
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
        if (!Database.getInstance().getIsLoading()) {
            mViewPager.setDisabled(true);
            Database.getInstance().cleanCharacters();
            Database.getInstance().setIsLoading(true);
            (new DatabaseLoader(this, getWebView(), Database.getInstance())).process(new Runnable() {
                @Override
                public void run() {
                    Database.getInstance().setIsLoading(false);
                    mViewPager.setDisabled(false);
                    initUI();
                    finished.run();
                }
            });
        }
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
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Database.getInstance().setFilterText(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            /*case R.id.action_filter:
                new FilterByBucketDialogFragment().show(getSupportFragmentManager(), "FilterByBucketDialogFragment");
                return true;
                */
            case R.id.toggle_filters:
                item.setChecked(!item.isChecked());
                if(item.isChecked()){
                    findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.fragment_container).setVisibility(View.GONE);
                }
            case R.id.action_refresh:
                actionRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void actionRefresh() {
        Database.getInstance().cleanCharacters();
        Database.getInstance().setIsLoading(true);
        initUI();
        reloadDatabase();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(Database.getInstance().getCharacters()!=null && position<Database.getInstance().getCharacters().size()){
            Character c = Database.getInstance().getCharacters().get(position);
            if (ImageStorage.getInstance().getImage(c.getId())!=null) {
                Log.v(LOG_TAG, "Set backround from cache");

                mPageTabs.setBackgroundDrawable(new BitmapDrawable(getResources(), ImageStorage.getInstance().getImage(c.getId())));
            } else {
                Log.v(LOG_TAG, "Set backround from url");
                ImageStorage.getInstance().fetchImage(c.getId(), c.getBackgroundPath(), new ImageStorage.UrlFetchWaiter() {
                    @Override
                    public void onImageFetched(Bitmap bitmap) {
                        mPageTabs.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                        //invalidate();
                    }
                });
            }
        } else {
            mPageTabs.setBackgroundDrawable(new BitmapDrawable(Bitmap.createBitmap(1,1, Bitmap.Config.ALPHA_8)));
            mPageTabs.setBackgroundColor(getResources().getColor(R.color.background_material_dark));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
