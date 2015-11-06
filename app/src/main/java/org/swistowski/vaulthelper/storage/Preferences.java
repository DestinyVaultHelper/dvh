package org.swistowski.vaulthelper.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by damian on 05.11.15.
 */
public class Preferences implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static Preferences instance;
    private Context context;

    private Preferences(Context context) {
        this.context = context;
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
    }

    public static Preferences getInstance() {
        return instance;
    }

    public static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        } else {
            instance.context = context;
        }
        return instance;
    }

    public int tabStyle() {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("tab_style", "0"));
    }

    public boolean showAll(){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_all", true);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("tab_style")){
            ItemMonitor.getInstance().notifyChanged();
        } else if(key.equals("show_all")){
            ItemMonitor.getInstance().notifyChanged();
        }
        Log.v("Preferences", "key changed: " + key + " " + sharedPreferences.toString());
    }
}
