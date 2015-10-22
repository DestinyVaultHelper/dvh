package org.swistowski.vaulthelper.filters;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Item;

/**
 * Created by damian on 22.10.15.
 */
public class CompletedFilter extends BaseFilter {
    @Override
    protected int[] getLabels() {
        return new int[]{R.string.is_completed, R.string.is_not_completed};
    }

    @Override
    public boolean filter(Item item) {
        if (getFilters().containsValue(Boolean.TRUE)) {
            return getFilters().get(R.string.is_completed).booleanValue() && item.getIsCompleted() ||
                    (getFilters().get(R.string.is_not_completed).booleanValue() && !item.getIsCompleted());
        }
        return true;
    }

    @Override
    public int getMenuLabel() {
        return R.string.completed_filter_label;
    }
}
