package org.swistowski.vaulthelper.storage;

import org.swistowski.vaulthelper.filters.BaseFilter;
import org.swistowski.vaulthelper.filters.BucketFilter;
import org.swistowski.vaulthelper.filters.CompletedFilter;
import org.swistowski.vaulthelper.filters.DamageFilter;
import org.swistowski.vaulthelper.filters.LightLevelFilter;
import org.swistowski.vaulthelper.filters.TierNameFilter;
import org.swistowski.vaulthelper.models.Item;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Information about active filters
 */
public class Filters implements Serializable {
    private final static Collection<BaseFilter> FILTERS;
    private static final Filters mInstance = new Filters();
    private String mFilterText = "";

    static {
        FILTERS = new LinkedList<>();
        FILTERS.add(new BucketFilter());
        FILTERS.add(new DamageFilter());
        FILTERS.add(new CompletedFilter());
        FILTERS.add(new TierNameFilter());
        FILTERS.add(new LightLevelFilter());
    }

    private Filters() {
    }

    static public Filters getInstance() {
        return mInstance;
    }


    public boolean isVisible(Item item) {
        for (BaseFilter filter : getFilters()) {
            if (!filter.filter(item)) {
                return false;
            }
        }
        return filterByText(item);
    }

    private boolean filterByText(Item item) {
        if (!mFilterText.equals("")) {
            return item.getName().toLowerCase().contains(mFilterText.toLowerCase());
        }
        return true;
    }

    public void setFilterText(String filterText) {
        this.mFilterText = filterText;
        ItemMonitor.getInstance().notifyChanged();
    }

    public Collection<BaseFilter> getFilters() {
        return FILTERS;
    }

}
