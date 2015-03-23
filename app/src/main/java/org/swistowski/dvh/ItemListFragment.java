package org.swistowski.dvh;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.swistowski.dvh.models.Item;

public class ItemListFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    public static final int DIRECTION_TO = 1;
    public static final int DIRECTION_FROM = 2;
    private static final String ARG_DIRECTION = "direction";
    private static final String ARG_SUBJECT = "subject";
    private ItemAdapter mAdapter;
    private OnItemIterationListener mListener;
    private int mDirection;

    private String mSubject;


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
            mDirection = getArguments().getInt(ARG_DIRECTION);
            mSubject = getArguments().getString(ARG_SUBJECT);
        }

        mAdapter = new ItemAdapter(getActivity(), mDirection, mSubject);
        Database.getInstance().registerItemAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        Database.getInstance().unregisterItemAdapter(mAdapter);
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
        if (null != mListener) {
            return mListener.onItemLongClicked(this, mAdapter.getItem(position), mSubject, mDirection);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onItemClicked(this, mAdapter.getItem(position), mSubject, mDirection);
        }
    }

    public interface OnItemIterationListener {
        public void onItemClicked(ItemListFragment fragment, Item item, String subject, int direction);

        public boolean onItemLongClicked(ItemListFragment fragment, Item item, String subject, int direction);
    }
}
