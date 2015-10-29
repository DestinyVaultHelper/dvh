package org.swistowski.vaulthelper.storage;

import android.widget.BaseAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by damian on 29.10.15.
 */
public class ItemMonitor {
    private final Set<BaseAdapter> registeredAdapters = new HashSet<BaseAdapter>();
    private static final ItemMonitor mInstance = new ItemMonitor();

    private ItemMonitor() {

    }

    static public ItemMonitor getInstance() {
        return mInstance;
    }

    public void registerItemAdapter(BaseAdapter adapter) {
        registeredAdapters.add(adapter);
    }

    public void unregisterItemAdapter(BaseAdapter adapter) {
        registeredAdapters.remove(adapter);
    }


    public void notifyItemsChanged() {
        for (BaseAdapter adapter : registeredAdapters) {
            adapter.notifyDataSetChanged();
        }
    }
}
