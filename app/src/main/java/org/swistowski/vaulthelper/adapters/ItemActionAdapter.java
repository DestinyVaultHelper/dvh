package org.swistowski.vaulthelper.adapters;

import android.app.Activity;
import android.database.DataSetObserver;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

import org.swistowski.vaulthelper.models.Item;

import java.util.List;

/**
 * Created by damian on 18.05.15.
 */
public class ItemActionAdapter implements ListAdapter {
    private final Activity mActivity;
    private final Item mItem;
    private List<Item.Action> mActions;

    public ItemActionAdapter(Activity activity, Item item) {
        mItem = item;
        mActivity = activity;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    private List<Item.Action> getActions() {
        if (mActions == null) {
            mActions = mItem.posibleActions();
        }
        return mActions;
    }

    @Override
    public int getCount() {
        return getActions().size();
    }

    @Override
    public Object getItem(int i) {
        return getActions().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Item.Action action = (Item.Action) getItem(i);
        if (view == null) {
            view = new Button(mActivity);

        } else {
            view = (Button) view;
        }
        ((Button) view).setText(Html.fromHtml(String.format(view.getResources().getString(action.getLabel()), action.getArgs())));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.doAction(mActivity);
            }
        });
        return view;
        // return null;
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
        return getCount() == 0;
    }
}
