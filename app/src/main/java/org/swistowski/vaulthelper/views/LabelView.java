package org.swistowski.vaulthelper.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Label;

/**
 * Created by damian on 30.10.15.
 */
public class LabelView extends FrameLayout {

    private static final String LOG_TAG = "LabelView";

    public LabelView(Context context) {
        super(context);
        init();
    }


    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LabelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.label_view, this, true);
    }

    public void setLabel(Label label) {
        TextView text = (TextView) findViewById(R.id.label_label);
        text.setText(label.getName());
        setBackgroundColor((int) label.getColor());
    }

}
