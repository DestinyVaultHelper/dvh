package org.swistowski.dvh.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import org.swistowski.dvh.R;
import org.swistowski.dvh.views.GroupDetailView;
import org.swistowski.dvh.views.GroupTitleView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFilteringFragment extends Fragment {
    private static final String LOG_TAG = "ListFilteringFragment";

    private class FilterAdapter extends BaseExpandableListAdapter {
        private static final String LOG_TAG = "FilterAdapter";
        private Context mContext;
        public FilterAdapter(Context context){
            super();
            mContext = context;
        }
        Context getContext() {
            return mContext;
        }

        @Override
        public int getGroupCount() {
            return 5;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 5+groupPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return ""+groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {

            return ""+childPosition;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition*100+childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final GroupTitleView view;
            if(convertView==null){
                view = new GroupTitleView(getContext());
            } else {
                view = (GroupTitleView) convertView;
            }
            view.setText("Position: "+groupPosition);
            Log.v(LOG_TAG, "GV: " + groupPosition);
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final GroupDetailView view;
            if(convertView==null){
                view = new GroupDetailView(getContext());
            } else {
                view = (GroupDetailView) convertView;
            }
            view.setText("Child: "+groupPosition+" "+childPosition);
            Log.v(LOG_TAG, "CV: " + groupPosition+ " " + childPosition);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
    private FilterAdapter mFilterAdapter;

    public ListFilteringFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        // Inflate the layout for this fragment
        View root_view = inflater.inflate(R.layout.fragment_list_filtering, container, false);

        mFilterAdapter = new FilterAdapter(root_view.getContext());
        ((ExpandableListView) root_view.findViewById(R.id.expandable_filters)).setAdapter(mFilterAdapter);
        return root_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onDestroyView(){
        Log.v(LOG_TAG, "onDestroyView");

        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        Log.v(LOG_TAG, "onDestroy");
        super.onDestroy();

    }
    @Override
    public void onAttach(Activity act){
        Log.v(LOG_TAG, "onAttach");
        super.onAttach(act);
    }

    @Override
    public void onDetach(){
        Log.v(LOG_TAG, "onDetach");
        super.onDetach();
    }
}
