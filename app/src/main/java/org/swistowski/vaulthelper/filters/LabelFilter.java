package org.swistowski.vaulthelper.filters;

import org.swistowski.vaulthelper.models.Item;

import java.util.Map;

/**
 * Created by damian on 22.10.15.
 */
public abstract class LabelFilter extends BaseFilter {
    private Map<String, Integer> mLabelMap;

    abstract protected Map<String, Integer> generateLabelMap();

    abstract protected String getLabelKey(Item item);

    public Map<String, Integer> getLabelMap() {
        if (mLabelMap == null) {
            mLabelMap = generateLabelMap();
        }
        return mLabelMap;
    }

    @Override
    protected int[] getLabels() {
        int size = getLabelMap().size();
        int[] labels = new int[size];
        Integer[] tmp = getLabelMap().values().toArray(new Integer[size]);
        for (int i = 0; i < size; i++) {
            labels[i] = tmp[i];
        }
        return labels;
    }

    @Override
    public boolean filter(Item item) {
        if (getFilters().containsValue(Boolean.TRUE)) {
            String labelKey = getLabelKey(item);
            if(getLabelMap().containsKey(labelKey)) {
                int selectedCode = getLabelMap().get(labelKey);
                return getFilters().containsKey(selectedCode) && getFilters().get(selectedCode);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
