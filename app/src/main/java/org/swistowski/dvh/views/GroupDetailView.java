package org.swistowski.dvh.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import org.swistowski.dvh.R;
import org.swistowski.dvh.util.ImageStorage;


public class GroupDetailView extends FrameLayout {
    private ImageStorage.DownloadImageTask mDownloadImageTask;

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
    }

    public void setText(String text) {
        CheckBox tv = (CheckBox) findViewById(R.id.detail_checkbox);
        tv.setText(text);
    }
    public void setChecked(boolean value){
        CheckBox tv = (CheckBox) findViewById(R.id.detail_checkbox);
        tv.setChecked(value);
    }

}