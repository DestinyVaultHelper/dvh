package org.swistowski.vaulthelper.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.filters.BaseFilter;
import org.swistowski.vaulthelper.storage.Filters;
import org.swistowski.vaulthelper.storage.ItemMonitor;
import org.swistowski.vaulthelper.views.GroupDetailView;
import org.swistowski.vaulthelper.views.GroupTitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFilteringFragment extends Fragment {
    private static final String LOG_TAG = "ListFilteringFragment";

    private class FilterGroup {
        final private String mTitle;
        final private BaseFilter mFilter;
        private final ArrayList<Map.Entry<Integer, Boolean>> mEntries;


        public FilterGroup(String title, BaseFilter filter) {
            mTitle = title;
            mFilter = filter;
            mEntries = new ArrayList<>();
            reloadEntries();
        }

        private void reloadEntries() {
            mEntries.clear();
            mEntries.addAll(mFilter.getFilters().entrySet());
        }

        public String getTitle() {
            return mTitle;
        }


        public Map.Entry<Integer, Boolean> getChild(int childPosition) {
            return mEntries.get(childPosition);
        }
    }

    private class FilterAdapter extends BaseExpandableListAdapter {
        List<FilterGroup> mGroups;
        private static final String LOG_TAG = "FilterAdapter";
        private Context mContext;

        public FilterAdapter(Context context) {
            super();
            mContext = context;
            mGroups = new ArrayList<>();
            for (BaseFilter filter : Filters.getInstance().getFilters()) {
                mGroups.add(new FilterGroup(getResources().getString(filter.getMenuLabel()), filter));
            }
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
            return "" + groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return "" + childPosition;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * 100 + childPosition;
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
            view.setOnCheckedChangeListener(null);

            boolean need_check = false;

            for (Map.Entry<Integer, Boolean> entry : mGroups.get(groupPosition).mEntries) {
                if (entry.getValue()) {
                    need_check = true;
                    break;
                }
            }
            view.setCheck(need_check);

            view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        for (Map.Entry<Integer, Boolean> entry : mGroups.get(groupPosition).mEntries) {
                            entry.setValue(false);
                        }
                        ItemMonitor.getInstance().notifyChanged();
                        mFilterAdapter.notifyDataSetChanged();
                    }
                }
            });

            view.setText(mGroups.get(groupPosition).getTitle());
            return view;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final GroupDetailView view;
            final Map.Entry<Integer, Boolean> item = mGroups.get(groupPosition).getChild(childPosition);

            if (convertView == null) {
                view = new GroupDetailView(getContext());
            } else {
                view = (GroupDetailView) convertView;
            }
            view.setText(getContext().getString(item.getKey()));
            view.onCheckedChanged(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Map.Entry<Integer, Boolean> entry = mGroups.get(groupPosition).mEntries.get(childPosition);
                    if (entry.getValue() != isChecked) {
                        entry.setValue(isChecked);
                        /*
                        if (isChecked) {
                            //parent.
                            ((GroupTitleView) parent).doCheck();
                        }
                        */
                        ItemMonitor.getInstance().notifyChanged();
                        mFilterAdapter.notifyDataSetChanged();
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
        // Inflate the layout for this fragment
        View root_view = inflater.inflate(R.layout.fragment_list_filtering, container, false);

        ((ExpandableListView) root_view.findViewById(R.id.expandable_filters)).setAdapter(mFilterAdapter);
        return root_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mFilterAdapter = new FilterAdapter(getActivity());
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onAttach(Activity act) {
        super.onAttach(act);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
