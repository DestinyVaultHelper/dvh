package org.swistowski.vaulthelper.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.storage.Items;
import org.swistowski.vaulthelper.views.ItemView;
import org.swistowski.vaulthelper.views.MaterialView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by damian on 12.11.15.
 */
public class MaterialsAdapter extends BaseAdapter {

    private final Context context;

    public class Material {
        private final String name;
        private final String url;
        private final long hash;
        private int count = 0;

        protected Material(String name, String url, int count, long hash) {
            this.name = name;
            this.url = url;
            this.count = count;
            this.hash = hash;
        }

        public void addCount(int count) {
            this.count += count;
        }

        public long getHash() {
            return hash;
        }

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }
    }

    ;
    static String[] NAMES = {
            "Helium Filaments",
            "Relic Iron",
            "Spinmetal",
            "Spirit Bloom",
            "Wormspore",
            "Mote of Light",
            //"Etheric Light",
            "Exotic Shard",
            "Armor Materials",
            "Weapon Parts",
            "Strange Coin",
            "Passage Coin",
            "Three of Coins",
            "Hadium Flake",

    };
    private List<Material> materials;
    private Map<String, Material> counter;

    public List<Material> getMaterials() {
        if (materials == null) {
            materials = new ArrayList<>();
            for (String name : NAMES) {
                Material material = null;
                for(Item item: Items.getInstance().allWithoutFiltering()){
                    if(item.getName().equals(name)){
                        if(material==null){
                            material= new Material(item.getName(), item.getIcon(), item.getStackSize(), item.getItemHash());
                            materials.add(material);
                        } else {
                            material.addCount(item.getStackSize());
                        }
                    }
                }
            }
            /*
            counter = new HashMap<>();
            Set<String> helper = new HashSet<String>(Arrays.asList(NAMES));

            for (Item item : Items.getInstance().all()) {
                if (helper.contains(item.getName())) {
                    if (counter.containsKey(item.getName())) {
                        counter.get(item.getName()).addCount(item.getStackSize());
                    } else {
                        Material material = new Material(item.getName(), item.getIcon(), item.getStackSize(), item.getItemHash());
                        materials.add(material);
                        counter.put(item.getName(), material);
                    }
                }
            }
            */
        }
        return materials;
    }


    public MaterialsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return getMaterials().size();
    }

    @Override
    public Object getItem(int position) {
        return getMaterials().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Material material = (Material) this.getItem(position);
        final MaterialView materialView;
        if (convertView == null) {
            materialView = new MaterialView(context);
        } else {
            materialView = (MaterialView) convertView;
        }
        materialView.setMaterial(material);

        return materialView;
    }

}
