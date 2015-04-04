package org.swistowski.dvh.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import org.swistowski.dvh.R;


public class GroupDetailView extends FrameLayout implements CompoundButton.OnCheckedChangeListener {
    private static final String LOG_TAG = "GroupDetailView";
    private CompoundButton.OnCheckedChangeListener mOnCheckedChanged;

    public GroupDetailView(Context context) {
        super(context);
        init();
    }

    public GroupDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View root_view = LayoutInflater.from(getContext()).inflate(R.layout.group_filter_detail_view, this, true);
        CheckBox tv = (CheckBox) findViewById(R.id.detail_checkbox);
        tv.setOnCheckedChangeListener(this);
    }

    public void setText(String text) {
        CheckBox tv = (CheckBox) findViewById(R.id.detail_checkbox);
        tv.setText(text);
    }
    public void setChecked(boolean value){
        CheckBox tv = (CheckBox) findViewById(R.id.detail_checkbox);
        tv.setChecked(value);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(mOnCheckedChanged!=null){
            mOnCheckedChanged.onCheckedChanged(buttonView, isChecked);
        }
    }

    public void onCheckedChanged(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChanged = onCheckedChangeListener;
    }
}