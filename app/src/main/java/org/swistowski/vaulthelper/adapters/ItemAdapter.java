package org.swistowski.vaulthelper.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.storage.Items;
import org.swistowski.vaulthelper.views.ItemView;

import java.util.List;

public class ItemAdapter extends BaseAdapter {
    private static final String LOG_TAG = "ItemArrayAdapter";
    private final String mSubject;
    private final Context mContext;
    private List<Item> mItems;

    public ItemAdapter(Context context, String subject) {
        mContext = context;
        mSubject = subject;
        reloadItems();
    }

    @Override
    public String toString() {
        return "item adapter: " + mSubject;
    }

    private void reloadItems() {
        if(mSubject!=null) {
            mItems = Items.getInstance().allNotFor(mSubject);
        } else {
            mItems = Items.getInstance().all();
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
        itemView.setRequireReloadDataListener(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        if(Items.getInstance().getItemOwner(item)!=null) {
            itemView.setIsGrayed(Items.getInstance().getItemOwner(item).equals(mSubject));
        }

        return itemView;
    }

    @Override
    public void notifyDataSetChanged() {
        reloadItems();
        super.notifyDataSetChanged();
    }
}