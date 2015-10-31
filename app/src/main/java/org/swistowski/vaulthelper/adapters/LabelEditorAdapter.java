package org.swistowski.vaulthelper.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.swistowski.vaulthelper.models.Label;
import org.swistowski.vaulthelper.storage.Labels;
import org.swistowski.vaulthelper.views.LabelView;

/**
 * Created by damian on 30.10.15.
 */
public class LabelEditorAdapter extends BaseAdapter {
    private final Context context;

    public LabelEditorAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Labels.getInstance().getLabelList().size();
    }

    @Override
    public Object getItem(int position) {
        return Labels.getInstance().getLabelList().get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position >= 0)
            return ((Label) getItem(position)).getId();
        return -1;
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
}
