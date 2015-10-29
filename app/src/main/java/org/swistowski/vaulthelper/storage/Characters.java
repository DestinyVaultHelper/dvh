package org.swistowski.vaulthelper.storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.swistowski.vaulthelper.models.*;
import org.swistowski.vaulthelper.models.Character;

import java.util.List;

/**
 * Store information about characters
 */
public class Characters {
    public static Characters mInstance = new Characters();
    private Characters(){}

    public static Characters getInstance(){
        return mInstance;
    }

    private List<Character> mCharacters;

    public void loadFromJson(JSONArray jsonArray) throws JSONException {
        mCharacters = Character.collectionFromJson(jsonArray);
    }

    public List<Character> all() {
        return mCharacters;
    }

    public void clean() {
        mCharacters=null;
    }

    public Character get(String id) {
        for (Character character : mCharacters) {
            if (character.getId().equals(id)) {
                return character;
            }
        }
        return null;
    }
}
