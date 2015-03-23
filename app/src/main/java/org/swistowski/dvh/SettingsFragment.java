package org.swistowski.dvh;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsFragment extends Fragment {

    private OnSettingsIterationListener mListener;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(v.getId());
            }
        };
        view.findViewById(R.id.button_login).setOnClickListener(ocl);
        view.findViewById(R.id.button_reload).setOnClickListener(ocl);
        view.findViewById(R.id.button_logout).setOnClickListener(ocl);
        return view;
    }

    void onButtonPressed(int buttonId) {
        if (mListener != null) {
            mListener.onButtonPressed(buttonId);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSettingsIterationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSettingsIterationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public interface OnSettingsIterationListener {
        public void onButtonPressed(int buttonId);
    }

}
