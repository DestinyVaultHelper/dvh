package org.swistowski.dvh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;


public class FilterFragment extends Fragment {
    private static final String LOG_TAG = "FilterFragment";
    private BaseAdapter mAdapter;
    public FilterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_filter, container, false);
        ((ImageButton) root_view.findViewById(R.id.search_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearchButtonClick();
            }


        });
        return root_view;
    }

    private void handleSearchButtonClick() {
        new FilterByBucketDialogFragment().show(getFragmentManager(), "FilterByBucketDialogFragment");
    }
}
