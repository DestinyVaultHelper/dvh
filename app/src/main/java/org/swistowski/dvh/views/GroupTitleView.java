package org.swistowski.dvh.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.swistowski.dvh.R;
import org.swistowski.dvh.util.ImageStorage;


public class GroupTitleView extends FrameLayout {
    private ImageStorage.DownloadImageTask mDownloadImageTask;

    public GroupTitleView(Context context) {
        super(context);
        init();
    }

    public GroupTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.group_filter_view, this, true);
    }

    public void setText(String text) {
        TextView tv = (TextView) findViewById(R.id.label_group_name);
        tv.setText(text);
    }
    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        ((CheckBox) findViewById(R.id.group_checkbox)).setOnCheckedChangeListener(listener);
    }
}