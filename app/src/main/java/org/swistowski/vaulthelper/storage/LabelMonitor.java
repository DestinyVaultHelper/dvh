package org.swistowski.vaulthelper.storage;

import android.widget.BaseAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by damian on 29.10.15.
 */
public class LabelMonitor {
    private final Set<BaseAdapter> registeredAdapters = new HashSet<BaseAdapter>();
    private static final LabelMonitor mInstance = new LabelMonitor();

    private LabelMonitor() {

    }

    static public LabelMonitor getInstance() {
        return mInstance;
    }

    public void registerAdapter(BaseAdapter adapter) {
        registeredAdapters.add(adapter);
    }

    public void unregisterAdapter(BaseAdapter adapter) {
        registeredAdapters.remove(adapter);
    }


    public void notifyChanged() {
        for (BaseAdapter adapter : registeredAdapters) {
            adapter.notifyDataSetChanged();
        }
    }
}
