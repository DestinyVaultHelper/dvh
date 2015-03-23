package org.swistowski.dvh;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.swistowski.dvh.models.Item;
import org.swistowski.dvh.util.ImageStorage;


public class ItemDetailActivity extends ActionBarActivity {
    public static final String ITEM = "item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Bundle b = getIntent().getExtras();
        Item item = (Item) b.getSerializable(ITEM);
        ((ImageView) findViewById(R.id.detail_icon)).setImageBitmap(ImageStorage.getInstance().getImage(item.getItemHash()));
        setTitle(item.getName());
        ((TextView) findViewById(R.id.detail_name)).setText(item.getDetails());
        ListView lv = (ListView) findViewById(R.id.detail_list_view);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item,  item.debugAttrs()));
    }
}
