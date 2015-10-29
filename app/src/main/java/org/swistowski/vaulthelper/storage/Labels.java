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
    private HashMap<Long, Set<Long>> mLabels;
    private long current = 1;


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
    private  HashMap<Long, Set<Long>> getLabels(){
        if(mLabels==null){
            mLabels = getDb().getAllItems();
        }
        return mLabels;
    }

    private Set<Long> getLabelItems(Long labelId) {
        return getLabels().get(labelId);
    }

    public void addLabelToItem(long item, long labelId) {
        getDb().addItem(item, labelId);
        getLabelItems(labelId).add(item);
    }

    public void deleteLabelFromItem(long item, long labelId) {
        getDb().deleteItem(item, labelId);
        getLabelItems(labelId).remove(item);
    }

    public boolean hasLabel(long item, long labelId) {
        return getLabelItems(labelId).contains(item);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public int count() {
        return mLabels.size();
    }

    public long getCurrent() {
        return current;
    }
}
