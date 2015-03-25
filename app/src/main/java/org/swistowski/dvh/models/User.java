package org.swistowski.dvh.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private static final String LOG_TAG = "User";
    private final String mDisplayName;
    private final String mPsnId;
    private final boolean mIsPsn;

    private User(String displayName, String psnId) {
        Log.v(LOG_TAG, "new user: " + displayName + " " + psnId);
        mDisplayName = displayName;
        mPsnId = psnId;
        mIsPsn = !psnId.equals("");
    }

    static public User fromJson(JSONObject data) throws JSONException {
        return new User(
                data.getJSONObject("user").getString("displayName"),
                data.optString("psnId", "")
        );
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public int getAccountType() {
        Log.v(LOG_TAG, "mIsPsn user: " + (mIsPsn ? 2 : 1));
        return mIsPsn ? 2 : 1;
    }
}
