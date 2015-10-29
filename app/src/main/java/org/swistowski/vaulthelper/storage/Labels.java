package org.swistowski.vaulthelper.storage;

import android.content.Context;

import org.swistowski.vaulthelper.db.DB;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by damian on 29.10.15.
 */
public class Labels {

    private DB mDb;
    private static Labels mInstance = new Labels();
    private Context context;
    private HashMap<String, Set<Long>> mLabels = new HashMap<String, Set<Long>>();

    private Labels() {
    }

    public static Labels getInstance() {
        return mInstance;
    }

    public DB getDb() {
        if (mDb == null) {
            mDb = new DB(getContext());
        }
        return mDb;
    }


    private Set<Long> getLabelItems(String label) {
        Set<Long> labels = mLabels.get(label);
        if (labels == null) {
            labels = getDb().labelItems(label);
            mLabels.put(label, labels);
        }
        return labels;
    }

    public void addLabel(long item_id, String label) {
        getDb().addLabel(item_id, label);
        getLabelItems(label).add(item_id);
    }

    public void deleteLabel(long item_id, String label) {
        getDb().deleteLabel(item_id, label);
        getLabelItems(label).remove(item_id);
    }

    public boolean hasLabel(long item_id, String label) {
        return getLabelItems(label).contains(item_id);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
