package org.swistowski.vaulthelper.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.atapters.ItemAdapter;
import org.swistowski.vaulthelper.models.Item;
import org.swistowski.vaulthelper.storage.ItemMonitor;
import org.swistowski.vaulthelper.views.ItemView;

public class ItemListFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int DIRECTION_TO = 1;
    private static final String ARG_DIRECTION = "direction";
    private static final String ARG_SUBJECT = "subject";
    private ItemAdapter mAdapter;
    private OnItemIterationListener mListener;

    private String mSubject;
    private boolean mEnabled = true;


    public static Fragment newInstance(int direction, String subject) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DIRECTION, direction);
        args.putString(ARG_SUBJECT, subject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSubject = getArguments().getString(ARG_SUBJECT);
        }

        mAdapter = new ItemAdapter(getActivity(), mSubject);
        ItemMonitor.getInstance().registerItemAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        ItemMonitor.getInstance().unregisterItemAdapter(mAdapter);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_collection_object, container, false);

        AdapterView listView = (AdapterView) rootView.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnItemIterationListener) activity;
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mEnabled) {
            return false;
        }
        if (null != mListener) {
            return mListener.onItemLongClicked(this, mAdapter.getItem(position), mSubject);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mEnabled) {
            return;
        }
        if (null != mListener) {
            Item item = mAdapter.getItem(position);
            if (!((ItemView) view).getIsGrayed() && item.isMoveable()) {
                mListener.onItemClicked(this, item, mSubject);
            } else {
                mListener.onItemLongClicked(this, item, mSubject);
            }
        }
    }

    @Override
    public void onRefresh() {
        mEnabled = false;
        mListener.refreshRequest(new Runnable() {
            @Override
            public void run() {
                mEnabled = true;
            }
        });
    }

    public interface OnItemIterationListener {
        void onItemClicked(ItemListFragment fragment, Item item, String subject);

        boolean onItemLongClicked(ItemListFragment fragment, Item item, String subject);

        void refreshRequest(Runnable finished);
    }
}
