package org.swistowski.dvh;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.dvh.activities.ItemDetailActivity;
import org.swistowski.dvh.activities.LoginActivity;
import org.swistowski.dvh.activities.WebViewActivity;
import org.swistowski.dvh.atapters.ItemsFragmentPagerAdapter;
import org.swistowski.dvh.fragments.AdFragment;
import org.swistowski.dvh.fragments.ItemListFragment;
import org.swistowski.dvh.fragments.SettingsFragment;
import org.swistowski.dvh.models.Character;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.models.ItemMover;
import org.swistowski.dvh.purchase.IabHelper;
import org.swistowski.dvh.purchase.IabResult;
import org.swistowski.dvh.purchase.Purchase;
import org.swistowski.dvh.util.Data;
import org.swistowski.dvh.util.DataLoader;
import org.swistowski.dvh.util.ImageStorage;
import org.swistowski.dvh.views.ClientWebView;
import org.swistowski.dvh.views.DisableableViewPager;


public class MainActivity extends ActionBarActivity implements ItemListFragment.OnItemIterationListener, SettingsFragment.OnSettingsIterationListener, ViewPager.OnPageChangeListener, AdFragment.OnAdIterationListener {
    private static final String LOG_TAG = "MainActivity";
    private static final String SKU_PREMIUM = "dvh_1";

    private FragmentStatePagerAdapter mPagerAdapter;
    private DisableableViewPager mViewPager;
    private PagerTabStrip mPageTabs;
    private MenuItem mFilterMenuItem;
    private boolean filtersVisible = false;

    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tracker t = getTracker();
        t.setScreenName("MainScreen");

        t.send(new HitBuilders.ScreenViewBuilder().build());

        if (!getWebView().isPrepared()) {
            Data.getInstance().setIsLoading(true);


            getWebView().prepare(new Runnable() {
                @Override
                public void run() {
                    Log.v(LOG_TAG, "web view initialized");
                    reloadDatabase();
                }
            });

        }

        if (savedInstanceState != null) {
            filtersVisible = savedInstanceState.getBoolean("filtersVisible");
        }

        setContentView(R.layout.items_tabs);
        initUI();

        // TODO: hide it
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAie9nvNN+AqBa7aheimLk+9mx588PCR4R0BQXmGeKYeMf9kVwJmfh1Y7TqIOyk8ujNgiNDJDcD8l+3L3LuJZgGGDCvGG0BK9GJvAsctJ077HitX31AEmkD8e+xgWzjtJsNw4xosdetLaJ0cQgMHWrj5z+Ox5UN3jPuIDLu5dnOd8cMqotWkkLoh9ETm3QX8l/oag9gWDt3vWhZ5+apco8VI72kXHq4VL+WuKUpBuJfJu3lwkKyL/0Cz1FJQZ36Dl2OMx59UVRbc8aHX+Cp+i+IKjmKDwtguP9CpGjpXVvcfeg2e+G5WUdrppib5MS/0Q+Z47NMcpvjV1jk2qSnQhDlQIDAQAB";
        //
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        try {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        Log.d(LOG_TAG, "Problem setting up In-app Billing: " + result);
                    }
                    // Hooray, IAB is fully set up!
                }
            });
        } catch (NullPointerException e) {

        }
        getWebView().setCurrentActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
        getWebView().setCurrentActivity(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle stateToSave) {
        super.onSaveInstanceState(stateToSave);
        stateToSave.putBoolean("filtersVisible", filtersVisible);
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
        try {
            initUIInner();
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "init fallback ", e);
        }
    }

    void initUIInner() {
        Log.v(LOG_TAG, "got view: " + mFilterMenuItem);

        setFiltersVisible(filtersVisible);
        if (!Data.getInstance().getIsLoading()) {
            findViewById(R.id.waiting_screen).setVisibility(View.GONE);
            mViewPager = (DisableableViewPager) findViewById(R.id.pager);
            /*
            mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
                private static final float MIN_SCALE = 0.75f;

                public void transformPage(View view, float position) {
                    int pageWidth = view.getWidth();

                    if (position < -1) { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        view.setAlpha(0);

                    } else if (position <= 0) { // [-1,0]
                        // Use the default slide transition when moving to the left page
                        view.setAlpha(1);
                        view.setTranslationX(0);
                        view.setScaleX(1);
                        view.setScaleY(1);

                    } else if (position <= 1) { // (0,1]
                        // Fade the page out.
                        view.setAlpha(1 - position);

                        // Counteract the default slide transition
                        view.setTranslationX(pageWidth * -position);

                        // Scale the page down (between MIN_SCALE and 1)
                        float scaleFactor = MIN_SCALE
                                + (1 - MIN_SCALE) * (1 - Math.abs(position));
                        view.setScaleX(scaleFactor);
                        view.setScaleY(scaleFactor);

                    } else { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        view.setAlpha(0);
                    }
                }
            });
            */
            mPagerAdapter = new ItemsFragmentPagerAdapter(getSupportFragmentManager(), this);
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
            findViewById(R.id.waiting_screen).setVisibility(View.VISIBLE);
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
                Data.getInstance().clean();
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(LoginActivity.URL, "https://www.bungie.net/en/User/SignOut");
                startActivityForResult(intent, LoginActivity.LOGIN_REQUEST);
                return;
        }
    }

    private void goLogin(){
        Log.v(LOG_TAG, "go login");
        getWebView().callAny("document.getElementsByClassName(\"btn_login psn\")[0].href+\"#\"+document.getElementsByClassName(\"btn_login live\")[0].href").then(new ClientWebView.Callback() {
            @Override
            public void onAccept(String result) {
                String[] urls = result.split("#");
                Log.v(LOG_TAG, "Xbox url:" + urls[1]);
                LoginActivity.goLogin(MainActivity.this, urls[1], urls[0]);
            }

            @Override
            public void onError(String result) {
                Log.v(LOG_TAG, "error url:" + result);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LoginActivity.LOGIN_REQUEST);
            }
        });

    }

    private void reloadDatabase() {
        (new DataLoader(this, getWebView(), Data.getInstance())).process(new Runnable() {
            @Override
            public void run() {
                getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.tracker_category_database))
                        .setAction(getString(R.string.tracker_action_loaded))
                        .setLabel("Success")
                        .build());
                Log.v(LOG_TAG, "web view data loaded");
                Data.getInstance().setIsLoading(false);
                initUI();
            }
        }, new DataLoader.Callback() {
            @Override
            public void onMessage(final String message) {
                getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.tracker_category_database))
                        .setAction(getString(R.string.tracker_action_loaded))
                        .setLabel("Failure")
                        .build());
                String errorMesssage = null;
                String errorStatus = null;
                try {
                    JSONObject errorObj = new JSONObject(message);
                    errorMesssage = errorObj.getString("errorMessage");
                    errorStatus = errorObj.optString("errorStatus");

                } catch (JSONException e) {
                    errorMesssage = message;
                }
                Log.v(LOG_TAG, "Error status "+errorStatus);
                if (errorStatus.equals("WebAuthRequired")) {
                    goLogin();
                } else {
                    final String messageToShow = errorMesssage;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(messageToShow)
                                    .setTitle("Bungie Api error");
                            Log.v(LOG_TAG, "error " + message);
                            Log.v(LOG_TAG, "i'm not logged in");
                            builder.show();
                        }
                    });
                }

            }
        }, new DataLoader.Callback() {
            @Override
            public void onMessage(final String message) {
                Log.v("ON message", message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Data.getInstance().getIsLoading()) {
                            Log.v(LOG_TAG, message);
                            try {
                                ((TextView) findViewById(R.id.progress_text)).setText(message);
                            } catch (NullPointerException e) {
                                Log.e(LOG_TAG, "null point exception on text show, skipping");
                            }
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
        if (!Data.getInstance().getIsLoading()) {
            mViewPager.setDisabled(true);
            Data.getInstance().cleanCharacters();
            Data.getInstance().setIsLoading(true);
            (new DataLoader(this, getWebView(), Data.getInstance())).process(new Runnable() {
                @Override
                public void run() {
                    Data.getInstance().setIsLoading(false);
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
        Log.v(LOG_TAG, "foo " + Build.VERSION.SDK_INT);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        SupportMenuItem searchItem = (SupportMenuItem) menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        /*
        searchItem.setSupportOnActionExpandListener(new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // do work
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // do work
                return true;
            }
        });
        */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Data.getInstance().setFilterText(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mFilterMenuItem = menu.findItem(R.id.toggle_filters);
        if (mFilterMenuItem != null) {
            //mFilterMenuItem.setChecked(false);
            mFilterMenuItem.setChecked(filtersVisible);
        }
        return true;
    }

    private void setFiltersVisible(boolean visibility) {
        filtersVisible = visibility;
        View container = findViewById(R.id.fragment_container);
        Log.v(LOG_TAG, "filters show: " + container + " " + visibility);
        if (container != null) {
            if (visibility) {
                container.setVisibility(View.VISIBLE);
            } else {
                container.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.v(LOG_TAG, "Back button!");
            if (filtersVisible) {
                setFiltersVisible(false);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.toggle_filters:
                Log.v(LOG_TAG, "item checked: " + item.isChecked());
                item.setChecked(!item.isChecked());
                setFiltersVisible(item.isChecked());
                return true;
            case R.id.action_refresh:
                actionRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void actionRefresh() {
        Data.getInstance().cleanCharacters();
        Data.getInstance().setIsLoading(true);
        initUI();
        reloadDatabase();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (Data.getInstance().getCharacters() != null && position < Data.getInstance().getCharacters().size()) {
            Character c = Data.getInstance().getCharacters().get(position);
            String itemHash = c.getBackgroundPath().replace('/', '-');
            if (ImageStorage.getInstance().getImage(itemHash) != null) {
                Log.v(LOG_TAG, "Set backround from cache");

                mPageTabs.setBackgroundDrawable(new BitmapDrawable(getResources(), ImageStorage.getInstance().getImage(itemHash)));
            } else {
                Log.v(LOG_TAG, "Set backround from url");
                ImageStorage.getInstance().fetchImage(itemHash, c.getBackgroundPath(), new ImageStorage.UrlFetchWaiter() {
                    @Override
                    public void onImageFetched(Bitmap bitmap) {
                        mPageTabs.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                        //invalidate();
                    }
                });
            }
        } else {
            mPageTabs.setBackgroundDrawable(new BitmapDrawable(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)));
            mPageTabs.setBackgroundColor(getResources().getColor(R.color.background_material_dark));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRequestSupportDev() {
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isFailure()) {
                    Log.d(LOG_TAG, "Error purchasing: " + result);
                    return;
                } else if (info.getSku().equals(SKU_PREMIUM)) {
                    new AlertDialog.Builder(getApplicationContext()).setTitle("Thank you").setMessage("I will have cold beer tonight").create().show();
                }
            }
        };
        mHelper.launchPurchaseFlow(this, SKU_PREMIUM, 10001, mPurchaseFinishedListener);
    }
}
