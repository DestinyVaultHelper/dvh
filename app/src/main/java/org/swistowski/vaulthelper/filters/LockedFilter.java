package org.swistowski.vaulthelper.filters;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Item;

/**
 * Created by damian on 11.11.15.
 */
public class LockedFilter extends BaseFilter {

    @Override
    protected int[] getLabels() {
        return new int[]{R.string.is_locked, R.string.is_not_locked};
    }

    @Override
    public boolean filter(Item item) {
        if (getFilters().containsValue(Boolean.TRUE)) {
            return getFilters().get(R.string.is_locked).booleanValue() && item.getIsLocked() ||
                    (getFilters().get(R.string.is_not_locked).booleanValue() && !item.getIsLocked());
        }
        return true;
    }

    @Override
    public int getMenuLabel() {
        return R.string.locked_filter_label;
    }
}
