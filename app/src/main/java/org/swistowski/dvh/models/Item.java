package org.swistowski.dvh.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.dvh.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Item implements Serializable, Comparable<Item> {
    private static final String LOG_TAG = "Item Class";
    private final String mBucketName;
    private final long mItemHash;
    private final String mItemInstanceId;
    private final int mBindStatus;
    private final int mDamageType;
    private final int mPrimaryStatValue;
    private boolean mIsEquipped;
    private final int mItemLevel;
    private final int mStackSize;
    private final int mQualityLevel;
    private final boolean mCanEquip;
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
    // private final String mJson;
    // private final String mDefinition;

    private Item(String bucketName, long itemHash, int bindStatus, boolean isEquipped, int itemLevel, int stackSize, int qualityLevel, boolean canEquip, boolean isEquipment, boolean isGridComplete, String itemInstanceId,
                 String itemName, String itemDescription, String icon, String secondaryIcon, String tierTypeName, String itemTypeName, long bucketTypeHash, int itemType, int itemSubType, int classType,
                 int primaryStatValue,
                 int damageType,
                 String json, String definition

    ) {
        mBucketName = bucketName;
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
        // mJson = json;
        // mDefinition = definition;
    }

    private static List<Item> fillFromBucket(JSONArray bucket_content, JSONObject items_definitions, String key, List<Item> items) {
        for (int j = 0; j < bucket_content.length(); j++) {
            JSONArray bucket_items = bucket_content.optJSONObject(j).optJSONArray("items");
            for (int k = 0; k < bucket_items.length(); k++) {
                JSONObject item = bucket_items.optJSONObject(k);
                JSONObject definition = items_definitions.optJSONObject(item.optString("itemHash"));
                // i'm interested only in transferrable items
                if (!definition.optBoolean("nonTransferrable")) {
                    items.add(createItem(key, item, definition));
                }
            }

        }
        return items;
    }

    private static List<Item> fromJson(JSONObject data, boolean isVault) throws JSONException {
        List<Item> items = new ArrayList<Item>();
        JSONObject items_definitions = data.getJSONObject("definitions").getJSONObject("items");

        if (!isVault) {
            JSONObject buckets = data.getJSONObject("data").getJSONObject("buckets");
            Iterator<String> i = buckets.keys();
            while (i.hasNext()) {
                String key = i.next();
                JSONArray bucket_content = buckets.getJSONArray(key);
                items = fillFromBucket(bucket_content, items_definitions, key, items);
            }
        } else {
            JSONArray bucket_content = data.optJSONObject("data").optJSONArray("buckets");
            items = fillFromBucket(bucket_content, items_definitions, "VAULT", items);
        }

        return items;
    }

    private static Item createItem(String key, JSONObject item, JSONObject definition) {
        JSONObject primaryStat = item.optJSONObject("primaryStat");
        int primaryStatValue = 0;
        if(primaryStat!=null){
            primaryStatValue = primaryStat.optInt("value", 0);
        }
        return new Item(
                key,
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

    @Override
    public int compareTo(Item another) {
        int t = another.mItemType - mItemType;
        if (t != 0)
            return t;
        int ql = another.mQualityLevel - mQualityLevel;
        if (ql != 0) {
            return ql;
        }
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
        //        "item json " +mJson,
        //        "item definition " + mDefinition

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
        return mItemType==2 || mItemType==3;
    }

    public String getItemId() {
        return mItemInstanceId;
    }

    public String getDetails() {
        return mItemDescription;
    }

    public void moveTo(String target) {
        Database.getInstance().changeOwner(this, target);

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
        if(mDamageType==3){
            return "Solar";
        } else if(mDamageType==2){
            return "Arc";
        } else if(mDamageType==4){
            return "Void";
        }
        return "";
    }

}
