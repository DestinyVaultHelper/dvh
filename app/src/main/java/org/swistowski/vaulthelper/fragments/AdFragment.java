package org.swistowski.vaulthelper.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.swistowski.vaulthelper.R;


public class AdFragment extends Fragment {

    private OnAdIterationListener mListener;


    public static AdFragment newInstance() {
        AdFragment fragment = new AdFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ad, container, false);
        /*
        here i can connect listeners
         */
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("29C967B06B52D5394DC9B626917C8021").build();
        mAdView.loadAd(adRequest);
        view.findViewById(R.id.support_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRequestSupportDev();
            }
        });
        view.findViewById(R.id.rate_me_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRateMeClick(view);
            }
        });
        return view;
    }

    private boolean startActivityHelper(Intent aIntent) {
        try
        {
            startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }

    private void handleRateMeClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=org.swistowski.vaulthelper"));
            if (!startActivityHelper(intent)) {
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=org.swistowski.vaulthelper"));
                if (!startActivityHelper(intent)) {
                    Toast.makeText(getActivity(), "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                }
            }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAdIterationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAdIterationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAdIterationListener {
        public void onRequestSupportDev();
    }

}
