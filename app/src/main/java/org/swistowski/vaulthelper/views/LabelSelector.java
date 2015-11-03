package org.swistowski.vaulthelper.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.adapters.LabelSelectorAdapter;
import org.swistowski.vaulthelper.models.Label;
import org.swistowski.vaulthelper.storage.Labels;

/**
 * Created by damian on 29.10.15.
 */
public class LabelSelector extends FrameLayout implements AdapterView.OnItemSelectedListener {
    private HandleEditListener listener;
    private BaseAdapter adapter;

    public LabelSelector(Context context) {
        super(context);
        init();
    }

    public LabelSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LabelSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.label_selector, this, true);
        Spinner label_selector = (Spinner) findViewById(R.id.label_selector_spinner);
        adapter = new LabelSelectorAdapter(getContext());
        label_selector.setAdapter(adapter);

        ImageButton button = (ImageButton) findViewById((R.id.label_selector_button));

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.doRequestEditLabels();
                }
            }
        });
        label_selector.setOnItemSelectedListener(this);

    }

    public void setHandleEditListener(HandleEditListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Label label = (Label) adapter.getItem(position);
        Labels.getInstance().setCurrent(label.getId());
        findViewById(R.id.label_selector_root).setBackgroundColor((int)label.getColor());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface HandleEditListener {
        void doRequestEditLabels();
    }
}
