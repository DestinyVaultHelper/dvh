package org.swistowski.vaulthelper.atapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.swistowski.vaulthelper.fragments.ItemListFragment;
import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.util.Data;
import org.swistowski.vaulthelper.views.ItemView;

import java.util.List;

public class ItemAdapter extends BaseAdapter {
    private static final String LOG_TAG = "ItemArrayAdapter";
    private final int mDirection;
    private final String mSubject;
    private final Context mContext;
    private List<Item> mItems;

    public ItemAdapter(Context context, int direction, String subject) {
        mContext = context;
        mDirection = direction;
        mSubject = subject;
        reloadItems();
    }

    @Override
    public String toString() {
        return "item adapter: " + mDirection + " " + mSubject;
    }

    private void reloadItems() {
        Log.v(LOG_TAG, "Loading items " + mDirection + " " + mSubject);
        if (mDirection == ItemListFragment.DIRECTION_FROM) {
            mItems = Data.getInstance().getItemsFiltered(mSubject);
        } else {
            mItems = Data.getInstance().notForItems(mSubject);
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getItemHash();
    }

    Context getContext() {
        return mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Item item = this.getItem(position);
        final ItemView itemView;
        if (convertView == null) {
            itemView = new ItemView(getContext());
            //itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 128));
        } else {
            itemView = (ItemView) convertView;
        }
        itemView.setItem(item);
        itemView.setRequireReloadDataListener(new Runnable(){
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        //itemView.setDetails(item.getDetails());

        return itemView;
    }

    @Override
    public void notifyDataSetChanged() {
        reloadItems();
        super.notifyDataSetChanged();
    }
}