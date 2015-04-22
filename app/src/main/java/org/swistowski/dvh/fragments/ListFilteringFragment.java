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
import android.widget.CompoundButton;
import android.widget.ExpandableListView;

import org.swistowski.dvh.R;
import org.swistowski.dvh.util.Data;
import org.swistowski.dvh.views.GroupDetailView;
import org.swistowski.dvh.views.GroupTitleView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFilteringFragment extends Fragment {
    private static final String LOG_TAG = "ListFilteringFragment";

    public interface FilterGetter {
        LinkedHashMap<String, Boolean> getFilters();
    };

    static final FilterGetter bucketFilters = new FilterGetter(){
        @Override
        public LinkedHashMap<String, Boolean> getFilters() {
            return Data.getInstance().getBucketFilters();
        }
    };

    static final FilterGetter damageFilters = new FilterGetter() {
        @Override
        public LinkedHashMap<String, Boolean> getFilters() {
            return Data.getInstance().getDamageFilters();
        }
    };

    static final FilterGetter completedFilters = new FilterGetter() {
        @Override
        public LinkedHashMap<String, Boolean> getFilters() {
            return Data.getInstance().getCompletedFilters();
        }
    };

    private class FilterGroup {
        final private String mTitle;
        final private FilterGetter mFilterGetter;
        private final ArrayList<Map.Entry<String, Boolean>> mEntries;


        public FilterGroup(String title, FilterGetter filterGetter){
            mTitle = title;
            mFilterGetter = filterGetter;
            mEntries = new ArrayList<>();
            reloadEntries();
        }

        private void reloadEntries(){
            mEntries.clear();
            mEntries.addAll(mFilterGetter.getFilters().entrySet());
        }

        public String getTitle() {
            return mTitle;
        }


        public Map.Entry<String, Boolean> getChild(int childPosition) {
            return mEntries.get(childPosition);
        }
    }

    private class FilterAdapter extends BaseExpandableListAdapter {
        List<FilterGroup> mGroups;
        private static final String LOG_TAG = "FilterAdapter";
        private Context mContext;

        public FilterAdapter(Context context){
            super();
            mContext = context;
            mGroups = new ArrayList<>();
            mGroups.add(new FilterGroup(getResources().getString(R.string.bucket_filter_label), bucketFilters));
            mGroups.add(new FilterGroup(getResources().getString(R.string.damage_type_filter_label), damageFilters));
            mGroups.add(new FilterGroup("Completed", completedFilters));
        }
        Context getContext() {
            return mContext;
        }

        @Override
        public int getGroupCount() {
            return mGroups.size();
         }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroups.get(groupPosition).mEntries.size();
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
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final GroupTitleView view;
            if (convertView == null) {
                view = new GroupTitleView(getContext());
            } else {
                view = (GroupTitleView) convertView;
            }
            view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (Map.Entry<String, Boolean> entry : mGroups.get(groupPosition).mEntries) {
                        entry.setValue(isChecked);
                    };
                    Data.getInstance().notifyItemsChanged();
                    mFilterAdapter.notifyDataSetChanged();
                }
            });
            view.setText(mGroups.get(groupPosition).getTitle());
            return view;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final GroupDetailView view;
            final Map.Entry<String, Boolean> item = mGroups.get(groupPosition).getChild(childPosition);

            if(convertView==null){
                view = new GroupDetailView(getContext());
            } else {
                view = (GroupDetailView) convertView;
            }
            view.setText(item.getKey());
            view.onCheckedChanged(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.v(LOG_TAG, "Postion " + groupPosition + " child: " + childPosition + " " + isChecked);
                    Map.Entry<String, Boolean> entry = mGroups.get(groupPosition).mEntries.get(childPosition);
                    if (entry.getValue() != isChecked) {
                        entry.setValue(isChecked);
                        Data.getInstance().notifyItemsChanged();
                    }
                }
            });
            view.setChecked(item.getValue());
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

        ((ExpandableListView) root_view.findViewById(R.id.expandable_filters)).setAdapter(mFilterAdapter);
        return root_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        mFilterAdapter = new FilterAdapter(getActivity());
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
