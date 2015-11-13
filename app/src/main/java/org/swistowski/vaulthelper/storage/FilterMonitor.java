package org.swistowski.vaulthelper.storage;

import android.util.Log;
import android.widget.BaseAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by damian on 29.10.15.
 */
public class FilterMonitor {
    public void registerSearchWatcher(SearchWatcher searchWatcher) {
        registeredWatchers.add(searchWatcher);
    }

    public void unregisterSearchWatcher(SearchWatcher searchWatcher){
        registeredWatchers.remove(searchWatcher);
    }

    public void doUpdateSearch(String name) {
        for (SearchWatcher registeredWatcher : registeredWatchers) {
            registeredWatcher.onUpdateSearch(name);
        }
    }

    public interface SearchWatcher {
        void onUpdateSearch(String newValue);
    }
    private final Set<SearchWatcher> registeredWatchers = new HashSet<>();
    private static final FilterMonitor mInstance = new FilterMonitor();

    private FilterMonitor() {

    }

    static public FilterMonitor getInstance() {
        return mInstance;
    }
    /*
    public void registerMonitor(Runnable monitor) {
        registeredAdapters.add(monitor);
    }

    public void unregisterMonitor(Runnable monitor) {
        registeredAdapters.remove(monitor);
    }


    public void notifyChanged() {
        for (Runnable monitor : registeredAdapters) {
            Log.v("FinterMonitor", "notifing: "+adapter);
            adapter.notifyDataSetChanged();
        }
    }
    */
}
