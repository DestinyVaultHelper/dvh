package org.swistowski.vaulthelper.filters;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Item;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by damian on 22.10.15.
 */
public class DamageFilter extends LabelFilter {

    @Override
    protected Map<String, Integer> generateLabelMap() {
        return new LinkedHashMap<String, Integer>(){{
            put("Arc", R.string.arc_damage);
            put("Solar", R.string.solar_damage);
            put("Void", R.string.void_damage);
        }};
    }

    @Override
    protected String getLabelKey(Item item){
        return item.getDamageTypeName();
    }

    @Override
    public int getMenuLabel() {
        return R.string.damage_type_filter_label;
    }
}
