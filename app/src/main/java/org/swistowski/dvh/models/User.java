package org.swistowski.dvh.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private final String mDisplayName;

    private User(String displayName) {
        mDisplayName = displayName;
    }

    static public User fromJson(JSONObject data) throws JSONException {
        return new User(
                data.getString("displayName")
        );
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
