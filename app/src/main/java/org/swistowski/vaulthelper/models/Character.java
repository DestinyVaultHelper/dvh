package org.swistowski.vaulthelper.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Character implements Serializable {
    private static final String LOG_TAG = "ModelCharacter";
    private final int mClassType;
    private final int mLevel;
    private final String mEmblemPath;
    private final String mBackgroundPath;
    private final String mId;

    private Character(final String characterId, final int classType, final int level, final String emblemPath, final String backgroundPath) {
        mId = characterId;
        mLevel = level;
        mClassType = classType;
        mEmblemPath = emblemPath;
        mBackgroundPath = backgroundPath;
    }

    static public ArrayList<Character> collectionFromJson(JSONArray data) throws JSONException {
        ArrayList<Character> collection = new ArrayList<Character>();

        for (int i = 0; i < data.length(); i++) {
            final Character c = Character.fromJson(data.getJSONObject(i));
            collection.add(c);
        }
        return collection;
    }

    private static Character fromJson(JSONObject data) throws JSONException {
        Log.v(LOG_TAG, data.toString());
        return new Character(
                data.getJSONObject("characterBase").getString("characterId"),
                data.getJSONObject("characterBase").getInt("classType"),
                data.getInt("characterLevel"),
                data.optString("emblemPath", ""),
                data.optString("backgroundPath", "")
        );
    }

    public String getId() {
        return mId;
    }

    String getClassName() {
        switch (mClassType) {
            case 0:
                return "Titan";
            case 1:
                return "Hunter";
            default:
                return "Warlock";
        }
    }

    public String toString() {
        return getClassName() + " " + mLevel;
    }

    public String getBackgroundPath() {
        return mBackgroundPath;
    }
}
