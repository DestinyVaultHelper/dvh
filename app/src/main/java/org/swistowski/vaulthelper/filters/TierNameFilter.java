package org.swistowski.vaulthelper.filters;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Item;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by damian on 22.10.15.
 */
public class TierNameFilter extends LabelFilter {
    @Override
    protected Map<String, Integer> generateLabelMap() {
        return new LinkedHashMap<String, Integer>() {
            {
                put("Common", R.string.tier_name_common);
                put("Rare", R.string.tier_name_rare);
                put("Legendary", R.string.tier_name_legendary);
                put("Exotic", R.string.tier_name_exotic);
            }
        };
    }

    @Override
    protected String getLabelKey(Item item) {
        return item.getTierTypeName();
    }

    @Override
    public int getMenuLabel() {
        return R.string.tier_filter_label;
    }
}
