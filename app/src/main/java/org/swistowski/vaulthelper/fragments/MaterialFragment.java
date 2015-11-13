package org.swistowski.vaulthelper.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.adapters.MaterialsAdapter;
import org.swistowski.vaulthelper.storage.FilterMonitor;
import org.swistowski.vaulthelper.storage.ItemMonitor;


public class MaterialFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private MaterialsAdapter mAdapter;

    public static MaterialFragment newInstance() {
        MaterialFragment fragment = new MaterialFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MaterialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_materials, container, false);


        AdapterView listView = (AdapterView) view.findViewById(R.id.materialListView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new MaterialsAdapter(getActivity());
        ItemMonitor.getInstance().registerAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        ItemMonitor.getInstance().unregisterAdapter(mAdapter);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MaterialsAdapter.Material material = (MaterialsAdapter.Material) mAdapter.getItem(position);
        FilterMonitor.getInstance().doUpdateSearch(material.getName());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
