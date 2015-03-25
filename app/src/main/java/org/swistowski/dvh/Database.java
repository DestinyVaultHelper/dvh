package org.swistowski.dvh;

import android.util.Log;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.dvh.models.Character;
import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.models.Membership;
import org.swistowski.dvh.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Database implements Serializable {
    public static final String VAULT_ID = "VAULT";
    private static final String LOG_TAG = "Database";
    private static final Database ourInstance = new Database();
    private User mUser;
    private Membership mMembership;
    private List<Character> mCharacters;
    private Map<String, List<Item>> items = new HashMap<>();
    private Map<Item, String> itemsOwners = new HashMap<>();

    private final Set<BaseAdapter> registeredAdapters = new HashSet<>();

    private Database() {
    }

    public static Database getInstance() {
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

    public void loadCharactersFromJson(JSONArray jsonArray) throws JSONException {
        mCharacters = Character.collectionFromJson(jsonArray);
    }

    public Membership getMembership() {
        return mMembership;
    }

    public List<Character> getCharacters() {
        return mCharacters;
    }

    public Map<String, List<Item>> getItems() {
        return items;
    }

    public void putItems(String id, List<Item> items) {
        this.items.put(id, items);
        for (Item item : items) {
            this.itemsOwners.put(item, id);
        }
    }

    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<Item>();

        for (Map.Entry<String, List<Item>> entry : items.entrySet()) {
            for (Item i : entry.getValue()) {
                if (i.isVisible())
                    allItems.add(i);
            }
        }
        Collections.sort(allItems);
        return allItems;
    }

    public void clean() {
        mUser = null;
        mMembership = null;
        mCharacters = null;
        cleanItems();
    }

    void cleanItems() {
        items = new HashMap<>();
        itemsOwners = new HashMap<>();
    }

    public List<Item> notForItems(String key) {
        List<Item> allItems = new ArrayList<Item>();
        for (Map.Entry<String, List<Item>> entry : items.entrySet()) {
            if (!entry.getKey().equals(key)) {
                for (Item i : entry.getValue()) {
                    if (i.isVisible())
                        allItems.add(i);
                }
            }
        }
        Collections.sort(allItems);
        return allItems;
    }


    public ArrayList<Item> getItemsFiltered(String id) {
        ArrayList<Item> allItems = new ArrayList<Item>();
        for (Item item : items.get(id)) {
            if (item.isVisible())
                allItems.add(item);
        }
        Collections.sort(allItems);
        return allItems;
    }

    public void cleanCharacters() {
        mCharacters = null;
        cleanItems();
    }

    public String getItemOwner(Item item) {
        return itemsOwners.get(item);
    }

    public String getItemOwnerName(Item item) {
        String owner = getItemOwner(item);
        if(owner==null){
            return "None";
        }
        if (owner.equals(VAULT_ID)) {
            return "Vault";
        }
        for (Character character : mCharacters) {
            if (character.getId().equals(owner)) {
                return character.toString();
            }
        }
        return "None";
    }

    public void changeOwner(Item item, String target) {
        String owner = getItemOwner(item);
        items.get(owner).remove(item);
        items.get(target).add(item);
        itemsOwners.put(item, target);

        notifyItemsChanged();
    }

    private void notifyItemsChanged() {
        for (BaseAdapter adapter : registeredAdapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public void registerItemAdapter(BaseAdapter adapter) {
        Log.v(LOG_TAG, "Adding adapter: " + adapter.toString());
        registeredAdapters.add(adapter);
    }

    public void unregisterItemAdapter(BaseAdapter adapter) {
        Log.v(LOG_TAG, "Removing adapter: " + adapter.toString());
        registeredAdapters.remove(adapter);
    }

        /*
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void load(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            FileInputStream fis = null;
            try {
                fis = context.openFileInput("database");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            try {
                try (ObjectInputStream is = new ObjectInputStream(fis)) {
                    try {
                        ourInstance = (Database) is.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

        @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void save(Context context) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try (FileOutputStream fos = context.openFileOutput("database", Context.MODE_PRIVATE)) {
                try (ObjectOutputStream os = new ObjectOutputStream(fos)) {
                    os.writeObject(ourInstance);
                    os.close();
                }
                fos.close();
            }
        }
    }
    */

}
