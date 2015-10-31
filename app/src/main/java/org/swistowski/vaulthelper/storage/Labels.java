package org.swistowski.vaulthelper.storage;

import android.content.Context;

import org.swistowski.vaulthelper.db.DB;
import org.swistowski.vaulthelper.models.Label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by damian on 29.10.15.
 */
public class Labels {

    private DB mDb;
    private static Labels mInstance = new Labels();
    private Context context;
    private HashMap<Long, Set<Long>> items;
    private HashMap<Long, Label> labels;
    private List<Label> labelsList;

    private long current = -1;


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

    private HashMap<Long, Set<Long>> getItems() {
        if (items == null) {
            items = getDb().getAllItems();
        }
        return items;
    }

    public List<Label> getLabelList() {
        if (labelsList == null) {
            Collection<Label> labelsFromDb = getDb().getLabels();
            labelsList = new ArrayList<>(labelsFromDb);
        }
        return labelsList;
    }

    public Map<Long, Label> getLabels() {
        if (labels == null) {
            labels = new HashMap<>();
            for (Label label : getLabelList()) {
                labels.put(label.getId(), label);
            }
        }
        return labels;
    }

    private Set<Long> getLabelItems(Long labelId) {
        if(!getItems().containsKey(labelId)){
            getItems().put(labelId, new HashSet<Long>());
        }
        return getItems().get(labelId);
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
        return getItems().size();
    }

    public long getCurrent() {
        if(current==-1){
            current = getLabelList().get(0).getId();
        }
        return current;
    }

    public void setCurrent(long current){
        this.current = current;
        ItemMonitor.getInstance().notifyChanged();
        //LabelMonitor.getInstance().notifyChanged();
    }


    public Label add(Label label) {
        label.setId(getDb().addLabel(label.getName(), label.getColor()));
        cleanLocalCache();
        LabelMonitor.getInstance().notifyChanged();
        return label;
    }

    private void cleanLocalCache() {
        labelsList = null;
        labels = null;
        items = null;
    }

    public void update(Label label) {
        getDb().updateLabel(label.getId(), label.getName(), label.getColor());
        LabelMonitor.getInstance().notifyChanged();
    }

    public void delete(Label label) {
        getDb().deleteLabel(label.getId());
        cleanLocalCache();
        if(current==label.getId()){
            current = -1;
        }
        LabelMonitor.getInstance().notifyChanged();
    }
}
