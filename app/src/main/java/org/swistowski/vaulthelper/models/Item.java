package org.swistowski.vaulthelper.models;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.vaulthelper.Application;
import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.util.Data;
import org.swistowski.vaulthelper.views.ClientWebView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Item implements Serializable, Comparable<Item> {
    private static final String LOG_TAG = "Item Class";
    private final long mItemHash;
    private final String mItemInstanceId;
    private final int mBindStatus;
    private final int mDamageType;
    private final int mPrimaryStatValue;
    private boolean mIsEquipped;
    private final int mItemLevel;
    private int mStackSize;
    private final int mQualityLevel;
    private boolean mCanEquip;
    private boolean mIsEquipment;
    private final boolean mIsGridComplete;

    private final String mName;
    private final String mItemDescription;
    private String mIcon;
    private String mSecondaryIcon;
    private final String mTierTypeName;
    private final String mItemTypeName;
    private final long mBucketTypeHash;
    private final int mItemType;
    private final int mItemSubType;
    private final int mClassType;
    private final String mBucketName;
    private Runnable requireReloadDataListener;


    //private final String mJson;
    //private final String mDefinition;
    //private final String mBucketDescription;

    private Item(long itemHash, int bindStatus, boolean isEquipped, int itemLevel, int stackSize, int qualityLevel, boolean canEquip, boolean isEquipment, boolean isGridComplete, String itemInstanceId,

                 String itemName, String itemDescription, String icon, String secondaryIcon, String tierTypeName, String itemTypeName, long bucketTypeHash, int itemType, int itemSubType, int classType,
                 int primaryStatValue,
                 int damageType,
                 String bucketName,
                 String bucketDescription,
                 String json, String definition

    ) {
        mItemHash = itemHash;
        mBindStatus = bindStatus;
        mIsEquipped = isEquipped;
        mItemLevel = itemLevel;
        mStackSize = stackSize;
        mQualityLevel = qualityLevel;
        mCanEquip = canEquip;
        mIsEquipment = isEquipment;
        mIsGridComplete = isGridComplete;
        mIsEquipment = isEquipment;

        mName = (itemName == null) ? "No name" : itemName;
        mIcon = icon;
        mItemDescription = itemDescription;
        mTierTypeName = tierTypeName;
        mItemTypeName = itemTypeName;
        mBucketTypeHash = bucketTypeHash;
        mItemType = itemType;
        mItemSubType = itemSubType;
        mClassType = classType;
        mItemInstanceId = itemInstanceId;

        mPrimaryStatValue = primaryStatValue;
        mDamageType = damageType;
        mBucketName = bucketName;
        //mBucketDescription = bucketDescription;

        //mJson = json;
        //mDefinition = definition;

    }

    private static List<Item> fillFromBucket(JSONArray bucket_content, JSONObject items_definitions, JSONObject bucket_definitions, String key, List<Item> items) {
        for (int j = 0; j < bucket_content.length(); j++) {
            JSONArray bucket_items = bucket_content.optJSONObject(j).optJSONArray("items");
            for (int k = 0; k < bucket_items.length(); k++) {
                JSONObject item = bucket_items.optJSONObject(k);
                JSONObject definition = items_definitions.optJSONObject(item.optString("itemHash"));
                JSONObject bucket;
                if (definition != null) {
                    if (bucket_definitions != null)
                        bucket = bucket_definitions.optJSONObject(definition.optString("bucketTypeHash"));
                    else
                        bucket = null;
                    // i'm interested only in transferrable items
                    if (!definition.optBoolean("nonTransferrable")) {
                        items.add(createItem(key, item, definition, bucket));
                    }
                }
            }

        }
        return items;
    }

    private static List<Item> fromJson(JSONObject data, boolean isVault) throws JSONException {
        List<Item> items = new ArrayList<Item>();
        JSONObject items_definitions = data.getJSONObject("definitions").getJSONObject("items");
        JSONObject bucket_definitions = data.getJSONObject("definitions").getJSONObject("buckets");
        if (!isVault) {
            JSONObject buckets = data.getJSONObject("data").getJSONObject("buckets");
            Iterator<String> i = buckets.keys();
            while (i.hasNext()) {
                String key = i.next();
                JSONArray bucket_content = buckets.getJSONArray(key);
                items = fillFromBucket(bucket_content, items_definitions, bucket_definitions, key, items);
            }
        } else {
            JSONArray bucket_content = data.optJSONObject("data").optJSONArray("buckets");
            items = fillFromBucket(bucket_content, items_definitions, bucket_definitions, "VAULT", items);
        }

        return items;
    }

    private static Item createItem(String key, JSONObject item, JSONObject definition, JSONObject bucket) {
        JSONObject primaryStat = item.optJSONObject("primaryStat");
        int primaryStatValue = 0;
        if (primaryStat != null) {
            primaryStatValue = primaryStat.optInt("value", 0);
        }
        return new Item(
                //key,
                item.optLong("itemHash"),
                item.optInt("bindStatus"),
                item.optBoolean("isEquipped"),
                item.optInt("itemLevel"),
                item.optInt("stackSize"),
                item.optInt("qualityLevel"),
                item.optBoolean("canEquip"),
                item.optBoolean("isEquipment"),
                item.optBoolean("isGridComplete"),
                item.optString("itemInstanceId"),

                definition.optString("itemName"),
                definition.optString("itemDescription"),
                definition.optString("icon"),
                definition.optString("secondaryIcon"),
                definition.optString("tierTypeName"),
                definition.optString("itemTypeName"),
                definition.optLong("bucketTypeHash"),
                definition.optInt("itemType"),
                definition.optInt("itemSubType"),
                definition.optInt("classType"),

                primaryStatValue,
                item.optInt("damageType", 0),
                bucket != null ? bucket.optString("bucketName") : "",
                bucket != null ? bucket.optString("bucketDescription") : "",
                item.toString(),
                definition.toString()
        );
    }

    public static List<Item> fromJson(String result, boolean isVault) throws JSONException {
        return fromJson(new JSONObject(result), isVault);
    }

    public static List<Item> fromJson(String result) throws JSONException {
        return fromJson(result, false);
    }

    private static final int typeToImportance(int type) {
        switch (type) {
            case 3: // weapon
                return 100;
            case 2: // armor
                return 90;
            case 8: //
                return 80;
            case 9:
                return 70;

        }
        return 3;
    }

    @Override
    public int compareTo(Item another) {
        Data data = Data.getInstance();
        boolean is_fav = data.hasLabel(getInstanceId(), "Favorites");
        boolean other_is_fav = data.hasLabel(another.getInstanceId(), "Favorites");
        if (is_fav && !other_is_fav) {
            return -1;
        } else if (other_is_fav && !is_fav) {
            return 1;
        }
        ;
        int t = typeToImportance(another.mItemType) - typeToImportance(mItemType);
        if (t != 0) {
            return t;
        }
        int ql = another.mQualityLevel - mQualityLevel;
        if (ql != 0) {
            return ql;
        }
        int compare = mBucketName.compareTo(another.mBucketName);
        if (compare != 0) {
            return compare;
        }
        ;
        return mName.compareTo(another.mName);
    }

    public String[] debugAttrs() {
        String[] ret = new String[]{
                "Bucket name " + mBucketName,
                "Item Hash " + mItemHash,
                "Bind Status" + mBindStatus,
                "is equipped " + mIsEquipped,
                "level " + mItemLevel,
                "stack size " + mStackSize,
                "quality level " + mQualityLevel,
                "can equip " + mCanEquip,
                "is equipment " + mIsEquipment,
                "is grid complete " + mIsGridComplete,
                "instance id " + mItemInstanceId,

                "name " + mName,
                "description " + mItemDescription,
                "secondary icon " + mSecondaryIcon,
                "tier type name " + mTierTypeName,
                "item type name " + mItemTypeName,
                "bucket type hash " + mBucketTypeHash,
                "type " + mItemType,
                "sub type " + mItemSubType,
                "class type " + mClassType,
                //"item json " +mJson,
                //"item definition " + mDefinition,
                //"bucket name " + mBucketName,
                //"bucket description " + mBucketDescription

        };
        return ret;
    }

    public String toString() {
        return mName != null ? mName : "Item without name";
    }

    public long getItemHash() {
        return mItemHash;
    }

    public String getName() {
        return mName;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public boolean isVisible() {
        return true;
    }

    public String getItemId() {
        return mItemInstanceId;
    }

    public String getDetails() {
        return mItemDescription;
    }

    public void moveTo(String target) {
        Data.getInstance().changeOwner(this, target);
    }

    public boolean isEquipped() {
        return mIsEquipped;
    }

    public long getBucketTypeHash() {
        return mBucketTypeHash;
    }

    public void setIsEquipped(boolean isEquipped) {
        mIsEquipped = isEquipped;
    }


    public int getPrimaryStatValue() {
        return mPrimaryStatValue;
    }

    public int getDamageType() {
        return mDamageType;
    }

    public String getDamageTypeName() {
        if (mDamageType == 3) {
            return "Solar";
        } else if (mDamageType == 2) {
            return "Arc";
        } else if (mDamageType == 4) {
            return "Void";
        }
        return "";
    }

    public int getType() {
        return mItemType;
    }

    public int getStackSize() {
        return mStackSize;
    }

    public String getBucketName() {
        return mBucketName;
    }

    public boolean getIsCompleted() {
        return mIsGridComplete;
    }

    public long getInstanceId() {
        return Long.valueOf(mItemInstanceId);
    }

    public boolean getCanEquip() {
        return mCanEquip;
    }

    public void setCanEquip(boolean canEquip) {
        mCanEquip = canEquip;
    }

    public List<Action> posibleActions() {

        List<Action> actions = new LinkedList<Action>();
        if (mCanEquip) {
            actions.add(new Action(R.string.equip, new Action.ActionRunnable() {
                @Override
                public void run(Activity activity) {
                    doEquip(activity);
                }
            }));
        }
        Data data = Data.getInstance();
        for (final String owner : data.getItems().keySet()) {
            if (!owner.equals(data.getItemOwner(this))) {
                String ownerLabel = owner;
                if (data.getCharacter(owner) != null) {
                    ownerLabel = data.getCharacter(owner).toString();
                }
                Item item = null;
                for (Item tmp_item : data.getAllItems()) {
                    if (Item.this.getItemHash() == tmp_item.getItemHash()) {
                        item = tmp_item;
                        break;
                    }
                }
                final Item finalItem = item;
                actions.add(new Action(R.string.move_to, new Action.ActionRunnable() {
                    @Override
                    public void run(final Activity activity) {
                        ItemMover.move(((Application) activity.getApplication()).getWebView(), finalItem, owner, finalItem.getStackSize()).then(
                                new ItemMover.Result() {
                                    @Override
                                    public void onSuccess() {
                                        Log.v(LOG_TAG, "Move success " + Item.this);
                                        activity.finish();
                                    }

                                    @Override
                                    public void onError(String e) {
                                        // onMoveError(e);
                                    }
                                }
                        );
                    }
                }, ownerLabel));
            }
        }

        return actions;
    }

    private void doEquip(final Activity activity) {
        Toast.makeText(activity, activity.getString(R.string.do_equip_in_progress), Toast.LENGTH_SHORT).show();
        // activity.finish();

        final ClientWebView webView = ((Application) activity.getApplication()).getWebView();
        final String owner = Data.getInstance().getItemOwner(this);
        Log.v(LOG_TAG, "owner: " + owner);
        ItemMover.equip(webView, owner, this, null).then(
                new ItemMover.Result() {
                    @Override
                    public void onSuccess() {
                        Log.v(LOG_TAG, "doGetCharacterInventory");
                        Membership membership = Data.getInstance().getMembership();
                        Log.v(LOG_TAG, "owner: " + owner);
                        org.swistowski.vaulthelper.models.Character character = Data.getInstance().getCharacter(owner);
                        webView.call("destinyService.GetCharacterInventory", "" + membership.getType(), "" + membership.getId(), character.getId(), "true").then(new ClientWebView.Callback() {
                            @Override
                            public void onAccept(String result) {
                                Log.v("Got character info", result);
                                List<Item> items = Data.getInstance().getItems().get(owner);
                                Log.v(LOG_TAG, Data.getInstance().getItems().keySet().toString());
                                HashMap<Long, Item> hash2item = new HashMap<Long, Item>();
                                for (Item ii : items) {
                                    hash2item.put(ii.getInstanceId(), ii);
                                }

                                try {
                                    JSONObject loadedItems = new JSONObject(result);
                                    //Log.v(LOG_TAG, loadedItems.optJSONObject("data").optJSONObject("buckets").toString());
                                    for (Iterator<String> iter = loadedItems.optJSONObject("data").optJSONObject("buckets").keys(); iter.hasNext(); ) {
                                        JSONArray bucketData = loadedItems.optJSONObject("data").optJSONObject("buckets").optJSONArray(iter.next());
                                        for (int i = 0; i < bucketData.length(); i++) {
                                            JSONArray bucketItems = bucketData.optJSONObject(i).optJSONArray("items");
                                            for (int j = 0; j < bucketItems.length(); j++) {
                                                JSONObject bi = bucketData.optJSONObject(j);
                                                if (bi != null) {
                                                    Log.v(LOG_TAG, "bi" + bi.toString());
                                                    Item currentItem = hash2item.get(bi.optLong("itemHash"));
                                                    if (currentItem != null) {
                                                        if (currentItem.getCanEquip() != bi.optBoolean("canEquip")) {
                                                            Log.v(LOG_TAG, "ci" + currentItem.toString());
                                                        }
                                                        if (currentItem.getIsEquipped() != bi.opt("isEquipped")) {
                                                            Log.v(LOG_TAG, "is equipped update" + currentItem.toString());
                                                        }
                                                        currentItem.setIsEquipped(bi.optBoolean("isEquipped"));
                                                        currentItem.setCanEquip(bi.optBoolean("canEquip"));
                                                    }
                                                }

                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Data.getInstance().notifyItemsChanged();
                                        Toast.makeText(activity, String.format(activity.getString(R.string.do_equip_finished), Item.this.toString()), Toast.LENGTH_SHORT).show();
                                        activity.finish();
                                    }
                                });

                            }

                            @Override
                            public void onError(String result) {
                                Log.e("doGetCharacterInventory", "unsucessfull " + result);
                                //onMoveError(result);
                            }
                        });


                    }

                    @Override
                    public void onError(String e) {
                        //onMoveError(e);
                    }
                }

        );

    }

    public Object getIsEquipped() {
        return mIsEquipped;
    }

    public static class Action {
        private final int mLabel;
        private final ActionRunnable mAction;
        private String[] mArgs;

        public Action(int label, ActionRunnable action) {
            mLabel = label;
            mAction = action;
        }

        public Action(int label, ActionRunnable action, String... args) {
            this(label, action);
            mArgs = args;
        }

        public void doAction(Activity activity) {
            mAction.run(activity);
        }

        public int getLabel() {
            return mLabel;
        }

        public String[] getArgs() {
            return mArgs;
        }

        public interface ActionRunnable {
            void run(Activity activity);
        }
    }
}
