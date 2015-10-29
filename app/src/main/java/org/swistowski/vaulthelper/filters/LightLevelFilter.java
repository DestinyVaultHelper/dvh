package org.swistowski.vaulthelper.filters;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Item;

/**
 * Created by damian on 22.10.15.
 */
public class LightLevelFilter extends BaseFilter {
    @Override
    protected int[] getLabels() {
        return new int[]{R.string.ligth_level_0, R.string.ligth_level_1, R.string.ligth_level_2, R.string.ligth_level_3, R.string.ligth_level_4, R.string.ligth_level_5};
    }

    @Override
    public boolean filter(Item item) {
        if (getFilters().containsValue(Boolean.TRUE)) {
            if (item.getPrimaryStatValue() == 0) {
                return false;
            }
            if (getFilters().get(R.string.ligth_level_0) && item.getPrimaryStatValue() < 200) {
                return true;
            }
            if (getFilters().get(R.string.ligth_level_1) && item.getPrimaryStatValue() >= 200 && item.getPrimaryStatValue() < 250) {
                return true;
            }
            if (getFilters().get(R.string.ligth_level_2) && item.getPrimaryStatValue() >= 250 && item.getPrimaryStatValue() < 280) {
                return true;
            }
            if (getFilters().get(R.string.ligth_level_3) && item.getPrimaryStatValue() >= 280 && item.getPrimaryStatValue() < 300) {
                return true;
            }
            if (getFilters().get(R.string.ligth_level_4) && item.getPrimaryStatValue() >= 300 && item.getPrimaryStatValue() < 310) {
                return true;
            }
            if(getFilters().get(R.string.ligth_level_5) && item.getPrimaryStatValue() >= 310){
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int getMenuLabel() {
        return R.string.light_level_filter_label;
    }
}
