package org.swistowski.vaulthelper.filters;

import android.util.Log;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Item;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by damian on 22.10.15.
 */
public class BucketFilter extends LabelFilter {


    @Override
    protected Map<String, Integer> generateLabelMap() {
        return new LinkedHashMap<String, Integer>() {{
            put("Primary Weapons", R.string.primary_weapons_bucket);
            put("Special Weapons", R.string.special_weapons_bucket);
            put("Heavy Weapons", R.string.heavy_weapons_bucket);
            put("Helmet", R.string.helmets_bucket);
            put("Chest Armor", R.string.chest_armors_bucket);
            put("Gauntlets", R.string.gauntlets_bucket);
            put("Leg Armor", R.string.leg_armor_bucket);
            put("Class Armor", R.string.class_armor_bucket);
            put("Materials", R.string.materials_bucket);
            put("Consumables", R.string.consumables_bucket);
            put("Emblems", R.string.emblems_bucket);
            put("Ghost", R.string.ghosts_bucket);
            put("Shaders", R.string.shaders_bucket);
            put("Ships", R.string.ships_bucket);
            put("Vehicle", R.string.vehicle_bucket);
            put("Artifacts", R.string.artifacts_bucket);
            put("Emotes", R.string.emotes_bucket);
        }};
    }

    @Override
    protected String getLabelKey(Item item) {
        return item.getBucketName();
    }

    @Override
    public int getMenuLabel() {
        return R.string.bucket_filter_label;
    }
}
