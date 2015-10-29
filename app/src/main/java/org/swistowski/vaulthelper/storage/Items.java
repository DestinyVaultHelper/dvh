package org.swistowski.vaulthelper.storage;

/**
 * Created by damian on 29.10.15.
 */
public class Items {
    static public Items mInstance = new Items();

    private Items() {

    }

    static public Items getInstance() {
        return mInstance;
    }
}
