package org.swistowski.vaulthelper.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.atapters.ItemActionAdapter;
import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.util.Data;
import org.swistowski.vaulthelper.util.ImageStorage;


public class ItemDetailActivity extends ActionBarActivity {
    public static final String ITEM = "item";
    public static final String STACK_SIZE = "stack-size";
    private static final String LOG_TAG = "ItemDetailActivity";

    public static void showItemItent(Context parent, Item item) {
        Log.v(LOG_TAG, "owner: " + Data.getInstance().getItemOwner(item));
        Intent intent = new Intent(parent, ItemDetailActivity.class);
        Bundle b = new Bundle();
        b.putLong(ItemDetailActivity.ITEM, item.getItemHash());
        b.putLong(ItemDetailActivity.STACK_SIZE, item.getStackSize());
        intent.putExtras(b);
        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Bundle b = getIntent().getExtras();
        Item item = null;
        long itemHash = b.getLong(ITEM);
        long itemStackSize = b.getLong(STACK_SIZE);

        for (Item tmp_item : Data.getInstance().getAllItems()) {
            if (tmp_item.getItemHash() == itemHash && tmp_item.getStackSize() == itemStackSize) {
                item = tmp_item;
                break;
            }
        }
        ((ImageView) findViewById(R.id.detail_icon)).setImageBitmap(ImageStorage.getInstance().getImage(item.getItemHash()));
        setTitle(item.getName());
        ((TextView) findViewById(R.id.detail_name)).setText(item.getDetails());
        ListView lv = (ListView) findViewById(R.id.item_actions_list);
        lv.setAdapter(new ItemActionAdapter(this, item));

        /*
        for (String debugAttr : item.debugAttrs()) {
            Log.v(LOG_TAG, debugAttr);
        }
        */
        item.debugAttrs();
        //ListView debug = (ListView) findViewById(R.id.detail_list_view);
        //debug.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item, item.debugAttrs()));
        //Log.v(LOG_TAG, item.debugAttrs().toString());

        setListViewHeightBasedOnChildren((ListView) findViewById(R.id.item_actions_list));
        //setListViewHeightBasedOnChildren((ListView) findViewById(R.id.detail_list_view));
        /*
        setListViewHeightBasedOnChildren((ListView) findViewById(R.id.layouts_list));

        ((ImageButton) findViewById(R.id.new_layout_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "Add layout clicked");
                handleCreateNewLayout();
            }
        });
        */


    }

    private void handleCreateNewLayout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setHint(R.string.add_new_layout_hint);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v(LOG_TAG, "Create new layout clicked, text typed: " + input.getText().toString());
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setTitle(R.string.add_new_layout_label);

        builder.setView(input);
        builder.show();
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, GridLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
