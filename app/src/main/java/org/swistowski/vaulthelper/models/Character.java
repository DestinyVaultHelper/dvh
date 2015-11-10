package org.swistowski.vaulthelper.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.vaulthelper.storage.Preferences;

import java.io.Serializable;
import java.util.ArrayList;

public class Character implements Serializable {
    private static final String LOG_TAG = "ModelCharacter";
    private static final long GENDER_FEMALE = 2204441813L;
    private static final long GENDER_MALE = 3111576190L;
    private static final long RACE_EXO = 898834093L;
    private static final long RACE_AWOKEN = 2803282938L;
    private static final long RACE_HUMAN = 3887404748L;
    private static final int APPEARANCE_SHORT = 0;
    private static final int APPEARANCE_LONG = 1;
    private static final int APPEARANCE_MEDIUM = 2;
    private final int mClassType;
    private final int mLevel;
    private final String mEmblemPath;
    private final String mBackgroundPath;
    private final String mId;
    private final long mGenderHash;
    private final long mRaceHash;

    private Character(final String characterId, final int classType, final int level, final String emblemPath, final String backgroundPath, final long raceHash, final long genderHash) {
        mId = characterId;
        mLevel = level;
        mClassType = classType;
        mEmblemPath = emblemPath;
        mBackgroundPath = backgroundPath;
        mRaceHash = raceHash;
        mGenderHash = genderHash;
    }

    static public ArrayList<Character> collectionFromJson(JSONArray data) throws JSONException {
        ArrayList<Character> collection = new ArrayList<>();

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
                data.getInt("characterLevel"),
                data.optString("emblemPath", ""),
                data.optString("backgroundPath", ""),
                data.getJSONObject("characterBase").optLong("raceHash"),
                data.getJSONObject("characterBase").optLong("genderHash")
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

    String getGenderName() {
        if (mGenderHash == GENDER_FEMALE) {
            return "Female";
        } else if (mGenderHash == GENDER_MALE) {
            return "Male";
        }
        return "Unknown";
    }

    String getGenderSymbol() {
        if (mGenderHash == GENDER_FEMALE) {
            return "♀";
        } else if (mGenderHash == GENDER_MALE) {
            return "♂";
        }
        return "";
    }

    String getRaceName() {
        if (mRaceHash == RACE_EXO) {
            return "Exo";
        } else if (mRaceHash == RACE_AWOKEN) {
            return "Awoken";
        } else if (mRaceHash == RACE_HUMAN) {
            return "Human";
        }
        return "Unknown";
    }

    String getRaceSymbol() {
        return getRaceName().substring(0, 1);
    }

    public String getBackgroundPath() {
        return mBackgroundPath;
    }

    public String getEmblemPath() {
        return mEmblemPath;
    }

    public String getLabel(int i) {
        switch (i) {
            case APPEARANCE_SHORT:
                return "<b>" + getClassName() + "</b> " + mLevel;
            case APPEARANCE_LONG:
                return "<b>" + mLevel + " " + getClassName() + "</b> " + getRaceName() + " " + getGenderName();
            case APPEARANCE_MEDIUM:
                return "<b>" + mLevel + " " + getClassName() + "</b> " + getRaceSymbol() + getGenderSymbol();
        }
        return "<b>" + getClassName() + "</b> " + mLevel;
    }

    public String getLabel() {
        return getLabel(Preferences.getInstance().tabStyle());
    }
}
