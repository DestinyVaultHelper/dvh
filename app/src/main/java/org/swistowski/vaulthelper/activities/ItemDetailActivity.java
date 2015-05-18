package org.swistowski.vaulthelper.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.atapters.ItemActionAdapter;
import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.util.Data;
import org.swistowski.vaulthelper.util.ImageStorage;


public class ItemDetailActivity extends ActionBarActivity {
    public static final String ITEM = "item";
    public static final String OWNER = "owner";
    private static final String LOG_TAG = "ItemDetailActivity";

    public static void showItemItent(Context parent, Item item) {
        Log.v(LOG_TAG, "owner: " + Data.getInstance().getItemOwner(item));
        Intent intent = new Intent(parent, ItemDetailActivity.class);
        Bundle b = new Bundle();
        b.putLong(ItemDetailActivity.ITEM, item.getItemHash());
        b.putString(OWNER, Data.getInstance().getItemOwner(item));
        intent.putExtras(b);
        parent.startActivity(intent);
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Bundle b = getIntent().getExtras();
        Item item = null;
        long itemHash = b.getLong(ITEM);

        for (Item tmp_item : Data.getInstance().getAllItems()) {
            if (tmp_item.getItemHash() == itemHash) {
                item = tmp_item;
                break;
            }
        }
        final String owner = b.getString(OWNER);
        ((ImageView) findViewById(R.id.detail_icon)).setImageBitmap(ImageStorage.getInstance().getImage(item.getItemHash()));
        setTitle(item.getName());
        ((TextView) findViewById(R.id.detail_name)).setText(item.getDetails());
        ListView lv = (ListView) findViewById(R.id.item_actions_list);
        lv.setAdapter((new ItemActionAdapter(this, item)));
        //ListView lv = (ListView) findViewById(R.id.detail_list_view);
        //lv.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item, item.debugAttrs()));
    }
}
