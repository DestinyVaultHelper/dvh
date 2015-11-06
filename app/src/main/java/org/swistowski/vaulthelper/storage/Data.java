package org.swistowski.vaulthelper.storage;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.swistowski.vaulthelper.models.Membership;
import org.swistowski.vaulthelper.models.User;

import java.io.Serializable;

public class Data implements Serializable {
    private static final String LOG_TAG = "Database";
    private static final Data ourInstance = new Data();

    private User mUser;
    private Membership mMembership;
    private boolean mShowAll = true;

    private Context context;


    private boolean mIsLoading = false;

    private Data() {
    }

    public static Data getInstance() {
        return ourInstance;
    }


    public User loadUserFromJson(JSONObject json) throws JSONException {
        mUser = User.fromJson(json);
        return mUser;
    }

    public User getUser() {
        return mUser;
    }

    public Membership loadMembershipFromJson(JSONObject json) {
        this.mMembership = Membership.fromJson(json);
        return mMembership;
    }


    public Membership getMembership() {
        return mMembership;
    }


    public void clean() {
        mUser = null;
        mMembership = null;
        Characters.getInstance().clean();
        Items.getInstance().clean();
    }

    public void cleanCharacters() {
        Characters.getInstance().clean();
        Items.getInstance().clean();
    }


    public synchronized void setIsLoading(boolean isLoading) {
        mIsLoading = isLoading;
    }

    public boolean getIsLoading() {
        return mIsLoading;
    }



    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        Labels.getInstance().setContext(context);
    }
}
