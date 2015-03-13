package org.swistowski.destinyshelve.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by damian on 13.03.15.
 */
public class Character {
    private String className;
    private int classType;
    private int level;
    private String characterId;


    public Character(JSONObject data) throws JSONException {
        classType = data.getJSONObject("characterBase").getInt("classType");
        characterId = data.getJSONObject("characterBase").getString("characterId");

        if(classType==0){
            className = "titan";
        } else if(classType==1){
            className = "hunter";
        } else {
            className = "warlock";
        }
        level = data.getInt("characterLevel");
    }
    public String getCharacterId(){
        return characterId;
    };

    public String getClassName(){
        return className;
    };
    public String toString(){
        return className + " " + level;
    };
}
