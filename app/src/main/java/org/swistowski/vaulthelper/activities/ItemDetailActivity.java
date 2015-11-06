package org.swistowski.vaulthelper.activities;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.adapters.ItemActionAdapter;
import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.models.Label;
import org.swistowski.vaulthelper.storage.Items;
import org.swistowski.vaulthelper.util.ImageStorage;
import org.swistowski.vaulthelper.views.LabelView;


public class ItemDetailActivity extends ActionBarActivity {
    public static final String ITEM = "item";
    public static final String STACK_SIZE = "stack-size";
    private static final String LOG_TAG = "ItemDetailActivity";

    public static void showItemItent(Context parent, Item item) {
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

        for (Item tmp_item : Items.getInstance().all()) {
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
        setListViewHeightBasedOnChildren(lv);

        lv = (ListView) findViewById(R.id.item_labels_list);
        lv.setAdapter(new ItemLabelsAdapter(this, item));
        Log.v(LOG_TAG, item.getLabels().toString());
        setListViewHeightBasedOnChildren(lv);
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

    private class ItemLabelsAdapter implements ListAdapter {
        private final Item item;
        private final Context context;

        public ItemLabelsAdapter(Context context, Item item) {
            this.context = context;
            this.item = item;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return item.getLabels().size();
        }

        @Override
        public Object getItem(int position) {
            return item.getLabels().get(position);
        }

        @Override
        public long getItemId(int position) {
            return ((Label) getItem(position)).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LabelView itemView;
            if (convertView == null) {
                itemView = new LabelView(context);
            } else {
                itemView = (LabelView) convertView;
            }
            itemView.setLabel((Label) getItem(position));
            return itemView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
