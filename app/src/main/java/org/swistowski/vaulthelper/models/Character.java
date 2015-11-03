package org.swistowski.vaulthelper.models;

import android.text.Html;
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
        Log.v("Character", data.toString());
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

    String getGenderName(){
        if(mGenderHash==2204441813L){
            return "Female";
        } else if(mGenderHash==3111576190L) {
            return "Male";
        }
        return "Unknown";
    }

    String getRaceName(){
        Log.v("Character", ""+ mRaceHash);
        if(mRaceHash==898834093L){
            return "Exo";
        } else if(mRaceHash==2803282938L){
            return "Awoken";
        } else if(mRaceHash ==3887404748L)  {
            return "Human";
        }
        return "Unknown";
    }

    public String toString() {
        return "<b>"+ mLevel + " " + getClassName() + "</b> " + getRaceName() + " "+getGenderName();
    }

    public String getBackgroundPath() {
        return mBackgroundPath;
    }

    public String getEmblemPath() {
        return mEmblemPath;
    }

}
