package org.swistowski.vaulthelper.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.vaulthelper.db.DB;
import org.swistowski.vaulthelper.models.Character;
import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.models.Membership;
import org.swistowski.vaulthelper.models.User;
import org.swistowski.vaulthelper.storage.Characters;
import org.swistowski.vaulthelper.storage.Filters;
import org.swistowski.vaulthelper.storage.ItemMonitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data implements Serializable {
    public static final String VAULT_ID = "VAULT";
    private static final String LOG_TAG = "Database";
    private static final Data ourInstance = new Data();
    private User mUser;
    private Membership mMembership;

    private Map<String, List<Item>> items = new HashMap<String, List<Item>>();
    private Map<Item, String> itemsOwners = new HashMap<Item, String>();
    private Set<String> bucketNames = new HashSet<String>();


    private HashMap<String, Set<Long>> mLabels = new HashMap<String, Set<Long>>();
    private List<String> mAllLabels = null;


    private DB mDb;
    private Context context;
    private boolean mShowAll = true;

    private boolean mIsLoading = false;

    private Data() {
    }

    public static Data getInstance() {
        return ourInstance;
    }


    public User loadUserFromJson(JSONObject json) throws JSONException {
        mUser = User.fromJson(json);
        return mUser;
    }

    public User getUser() {
        return mUser;
    }

    public Membership loadMembershipFromJson(JSONObject json) {
        this.mMembership = Membership.fromJson(json);
        return mMembership;
    }


    public Membership getMembership() {
        return mMembership;
    }

    public Map<String, List<Item>> getItems() {
        return items;
    }

    public void putItems(String id, List<Item> items) {
        this.items.put(id, items);
        for (Item item : items) {
            this.itemsOwners.put(item, id);
            this.bucketNames.add(item.getBucketName());
        }
    }

    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<Item>();

        for (Map.Entry<String, List<Item>> entry : items.entrySet()) {
            for (Item i : entry.getValue()) {
                if (i.isVisible() && Filters.getInstance().isVisible(i))
                    allItems.add(i);
            }
        }
        Collections.sort(allItems);
        return allItems;
    }

    public void clean() {
        mUser = null;
        mMembership = null;
        Characters.getInstance().clean();
        cleanItems();
    }

    void cleanItems() {
        items = new HashMap<String, List<Item>>();
        itemsOwners = new HashMap<Item, String>();
    }


    public List<Item> notForItems(String key) {
        List<Item> allItems = new ArrayList<Item>();
        for (Map.Entry<String, List<Item>> entry : items.entrySet()) {
            if (showAll() || !entry.getKey().equals(key)) {
                for (Item i : entry.getValue()) {
                    if (i.isVisible() && Filters.getInstance().isVisible(i))
                        allItems.add(i);
                }
            }
        }
        Collections.sort(allItems);
        return allItems;
    }

    /**
     * @return true if show all filter is enabled
     */
    public boolean showAll() {
        return mShowAll;
    }

    public void setShowAll(boolean showall) {
        mShowAll = showall;
        ItemMonitor.getInstance().notifyItemsChanged();
    }

    public void cleanCharacters() {
        Characters.getInstance().clean();
        cleanItems();
    }

    public String getItemOwner(Item item) {
        String owner = itemsOwners.get(item);
        if (owner == null) {
            for (Map.Entry<Item, String> entry : itemsOwners.entrySet()) {
                if (entry.getKey().getItemHash() == item.getItemHash()) {
                    return entry.getValue();
                }
            }
        }
        return owner;
    }

    public String getItemOwnerName(Item item) {
        if (item.getLocation() == item.LOCATION_VENDOR) {
            return "Vendor";
        }
        if (item.getLocation() == item.LOCATION_POSTMASTER) {
            return "Postmaster";
        }
        String owner = getItemOwner(item);
        if (owner == null) {
            return "None";
        }
        if (owner.equals(VAULT_ID)) {
            return "Vault";
        }
        Character character = Characters.getInstance().get(owner);
        if (character != null) {
            return character.toString();
        }

        return "None";
    }

    public void changeOwner(Item item, String target, int stackSize) {
        if (item.getInstanceId() != 0) {
            String owner = getItemOwner(item);
            if (items.get(owner) != null) {
                items.get(owner).remove(item);
                items.get(target).add(item);
                itemsOwners.put(item, target);
            }

        } else {
            int leftOvers = item.getStackSize() - stackSize;
            boolean exists = false;
            /*
            Item new_item = item.make_clone();
            new_item.setStackSize(item.getStackSize()-stackSize);
            */
            for (Item tmp_item : items.get(target)) {
                if (tmp_item.getItemHash() == item.getItemHash()) {
                    // exists!
                    item.setStackSize(tmp_item.getStackSize() + stackSize);
                    if (items.get(target) != null) {
                        items.get(target).remove(tmp_item);
                        itemsOwners.remove(tmp_item);
                        exists = true;
                    }
                    break;

                }
            }
            // moved to fresh place
            if (!exists) {
                item.setStackSize(stackSize);
            }

            String owner = getItemOwner(item);
            if (items.get(owner) != null) {
                items.get(owner).remove(item);
                items.get(target).add(item);
                itemsOwners.put(item, target);
            }

            if (leftOvers > 0) {
                // recreate!
                Item new_item = item.make_clone();
                new_item.setStackSize(leftOvers);
                items.get(owner).add(new_item);
                itemsOwners.put(new_item, owner);
            }
        }
        ItemMonitor.getInstance().notifyItemsChanged();
    }


    public synchronized void setIsLoading(boolean isLoading) {
        mIsLoading = isLoading;
    }

    public boolean getIsLoading() {
        return mIsLoading;
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;

    }
}
