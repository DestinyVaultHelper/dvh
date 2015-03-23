package org.swistowski.dvh.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Character implements Serializable {
    private final int mClassType;
    private final int mLevel;
    private final String mId;

    private Character(final String characterId, final int classType, final int level) {
        mId = characterId;
        mLevel = level;
        mClassType = classType;
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
        return new Character(
                data.getJSONObject("characterBase").getString("characterId"),
                data.getJSONObject("characterBase").getInt("classType"),
                data.getInt("characterLevel")

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
}
