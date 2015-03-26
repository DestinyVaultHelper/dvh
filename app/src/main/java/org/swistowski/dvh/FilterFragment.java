package org.swistowski.dvh;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.swistowski.dvh.models.Item;

import java.util.List;


public class FilterFragment extends Fragment {
    private static final String LOG_TAG = "FilterFragment";
    private OnFragmentInteractionListener mListener;
    private BaseAdapter mAdapter;
    public FilterFragment() {
        // Required empty public constructor
    }

    private class BucketNamesListAdapter extends BaseAdapter {

        private List<String> mItems;

        public BucketNamesListAdapter(){
            super();
            reloadItems();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView == null) {
                view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, null);
            } else {
                view = (TextView) convertView;
            }
            view.setTextColor(getResources().getColor(R.color.wallet_highlighted_text_holo_light));
            view.setText((String)getItem(position));
            return view;
        }

        @Override
        public void notifyDataSetChanged() {
            Log.v(LOG_TAG, "filter changed");
            reloadItems();
            Log.v(LOG_TAG, Database.getInstance().getBucketNames().toString());
            super.notifyDataSetChanged();
        }

        private void reloadItems() {
            mItems = Database.getInstance().getBucketNames();
            Log.v(LOG_TAG, "items loaded: " + mItems);
        }

        @Override
        public String toString(){
            return "BucketNamesAdapter: "+mItems.toString();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new BucketNamesListAdapter();
        Database.getInstance().registerItemAdapter(mAdapter);
    }

    @Override
    public void onDestroy(){
        Database.getInstance().unregisterItemAdapter(mAdapter);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String[] items = new String[]{};
        View root_view = inflater.inflate(R.layout.fragment_filter, container, false);
        return root_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFilterChanged(Filter newFilter);
    }

    public interface Filter {
        boolean applyFilter(Item item);
    }

}
