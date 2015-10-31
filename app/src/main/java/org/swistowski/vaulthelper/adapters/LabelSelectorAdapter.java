package org.swistowski.vaulthelper.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.swistowski.vaulthelper.models.Label;
import org.swistowski.vaulthelper.storage.Labels;
import org.swistowski.vaulthelper.views.LabelView;

/**
 * Created by damian on 29.10.15.
 */
public class LabelSelectorAdapter extends BaseAdapter {
    private final Context context;

    public LabelSelectorAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getDropDownView(int i, View convertView, ViewGroup viewGroup) {
        return getView(i, convertView, viewGroup);

    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return Labels.getInstance().getLabelList().size();
    }

    @Override
    public Object getItem(int i) {
        return Labels.getInstance().getLabelList().get(i);
    }

    @Override
    public long getItemId(int i) {
        if (i >= 0)
            return ((Label) getItem(i)).getId();
        return -1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final LabelView itemView;
        if (convertView == null) {
            itemView = new LabelView(context);
            //itemView.setPadding(48, 48, 48, 48);
            //itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 128));
        } else {
            itemView = (LabelView) convertView;
        }
        itemView.setLabel((Label) getItem(position));
        return itemView;
    }

    @Override
    public int getItemViewType(int i) {
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
