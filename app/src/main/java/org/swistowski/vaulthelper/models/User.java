package org.swistowski.vaulthelper.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private static final String LOG_TAG = "models.User";
    private final String mDisplayName;
    private final String mPsnId;
    private final String mGameTag;
    private final boolean mIsPsn;

    private User(String displayName, String psnId, String gameTag) {
        Log.v(LOG_TAG, "new user: " + displayName + " " + psnId);
        mDisplayName = displayName;
        mPsnId = psnId;
        mIsPsn = !psnId.equals("");
        mGameTag = gameTag;
    }

    static public User fromJson(JSONObject data) throws JSONException {
        Log.v(LOG_TAG, "json: "+data);
        return new User(
                data.getJSONObject("user").getString("displayName"),
                data.optString("psnId", ""),
                data.optString("gamerTag", "")
        );
    }
    public String getAccountName(){
        return mIsPsn?mPsnId:mGameTag;
    }
    @Deprecated
    private String getDisplayName() {
        return mDisplayName;
    }

    public int getAccountType() {
        Log.v(LOG_TAG, "mIsPsn user: " + (mIsPsn ? 2 : 1));
        return mIsPsn ? 2 : 1;
    }


}
