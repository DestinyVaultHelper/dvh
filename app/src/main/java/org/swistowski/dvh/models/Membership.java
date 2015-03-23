package org.swistowski.dvh.models;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class Membership  implements Serializable {
    private final String mId;
    private final int mType;

    private Membership(String id, int type){
        Log.v("memebership id", ""+id);
        mId = id;
        mType = type;
    }

    public String getId() {
        return mId;
    }

    public int getType(){
        return mType;
    }

    final public String getTigerType() {
        if(mType==1){
            return "TigerXbox";
        } else {
            return "TigerPSN";
        }
    }

    public static Membership fromJson(JSONObject jsonObject) {
        Log.v("membership", jsonObject.toString());
        return new Membership(jsonObject.optString("membershipId"), jsonObject.optInt("membershipType"));
    }
}
