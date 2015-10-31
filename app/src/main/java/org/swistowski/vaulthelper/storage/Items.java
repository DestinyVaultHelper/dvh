package org.swistowski.vaulthelper.storage;

import org.swistowski.vaulthelper.models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by damian on 29.10.15.
 */
public class Items {
    public static final String VAULT_ID = "VAULT";

    private Map<String, List<Item>> items = new HashMap<String, List<Item>>();
    private Map<Item, String> itemsOwners = new HashMap<Item, String>();


    static public Items mInstance = new Items();

    private Items() {

    }

    static public Items getInstance() {
        return mInstance;
    }

    public Map<String, List<Item>> allAsMap() {
        return items;
    }

    public void put(String id, List<Item> items) {
        this.items.put(id, items);
        for (Item item : items) {
            this.itemsOwners.put(item, id);
        }
    }

    public List<Item> allNotFor(String key) {
        List<Item> allItems = new ArrayList<Item>();
        for (Map.Entry<String, List<Item>> entry : items.entrySet()) {
            if (Data.getInstance().showAll() || !entry.getKey().equals(key)) {
                for (Item i : entry.getValue()) {
                    if (i.isVisible() && Filters.getInstance().isVisible(i))
                        allItems.add(i);
                }
            }
        }
        Collections.sort(allItems);
        return allItems;
    }

    public List<Item> all() {
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
        items = new HashMap<String, List<Item>>();
        itemsOwners = new HashMap<Item, String>();
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
        org.swistowski.vaulthelper.models.Character character = Characters.getInstance().get(owner);
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
        ItemMonitor.getInstance().notifyChanged();
    }

}
