package org.swistowski.vaulthelper.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.atapters.LabelSelectorAdapter;

/**
 * Created by damian on 29.10.15.
 */
public class LabelSelector extends FrameLayout {
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
        //label_selector.setAdapter(new LabelSelectorAdapter());
    }
}
