package org.swistowski.vaulthelper.util;

abstract class WaitForAll {
    private int mItems = 0;
    abstract public void finished();

    public synchronized int decrease() {
        mItems--;
        if(mItems<=0){
            finished();
        }
        return mItems;
    }

    public synchronized int increase() {
        mItems++;
        return mItems;
    }
}
