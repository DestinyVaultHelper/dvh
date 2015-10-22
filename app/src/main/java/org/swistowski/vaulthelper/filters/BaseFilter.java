package org.swistowski.vaulthelper.filters;

import org.swistowski.vaulthelper.models.Item;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by damian on 22.10.15.
 */
public abstract class BaseFilter {
    abstract protected  int[] getLabels();
    abstract public boolean filter(Item item);
    abstract public int getMenuLabel();

    protected HashMap<Integer, Boolean> mFilters;

    public BaseFilter(){
    }

    public HashMap<Integer, Boolean> getFilters(){
        if(mFilters==null){
            mFilters=new LinkedHashMap<>();
            for(int label: getLabels()){
                mFilters.put(label, Boolean.FALSE);
            }
        }
        return mFilters;
    }
}
