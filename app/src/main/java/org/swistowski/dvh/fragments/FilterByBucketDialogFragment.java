package org.swistowski.dvh.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import org.swistowski.dvh.util.Data;
import org.swistowski.dvh.R;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FilterByBucketDialogFragment extends DialogFragment implements DialogInterface.OnMultiChoiceClickListener {
    private static final String LOG_TAG = "FilterByBucketDF";
    private boolean[] values;
    private String[] labels;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinkedHashMap<String, Boolean> items = Data.getInstance().getBucketFilters();
        labels = new String[items.size()];
        values = new boolean[items.size()];
        int i = 0;
        for (Map.Entry<String, Boolean> entry : items.entrySet()) {
            labels[i] = entry.getKey();
            values[i] = entry.getValue();
            Log.v(LOG_TAG, entry.getKey()+" "+entry.getValue());
            i++;
        }
        Log.v(LOG_TAG, items.toString());
        Log.v(LOG_TAG, "" + labels.length);
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.bucket_filter_title)
            .setMultiChoiceItems(labels, values, this)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handleOk();
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .create();
    }

    private void handleOk() {
        Log.v(LOG_TAG, "ok selected");
        Set<String> bucketsToShow = new HashSet <String>();
        for(int i=0;i<values.length;i++){
            if(values[i]){
                bucketsToShow.add(labels[i]);
            }
        }
        Data.getInstance().setBucketFilters(bucketsToShow);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        values[which] = isChecked;
    }

}
