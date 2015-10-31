package org.swistowski.vaulthelper.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import org.swistowski.vaulthelper.R;
import org.swistowski.vaulthelper.models.Label;

/**
 * Created by damian on 30.10.15.
 */
public class LabelEditView extends FrameLayout implements ColorPicker.OnColorChangedListener, TextWatcher {
    private Label label;
    private ColorPicker picker;
    private TextView textview;

    public LabelEditView(Context context) {
        super(context);
        init();
    }


    public LabelEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LabelEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.label_edit_view, this, true);
        textview = (TextView) findViewById(R.id.label_name_edit);
        textview.addTextChangedListener(this);
        picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        picker.setShowOldCenterColor(false);
        picker.setOnColorChangedListener(this);
    }

    public void setLabel(Label label) {
        this.label = label;
        textview.setText(label.getName());

        picker.setColor((int) label.getColor());
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public void onColorChanged(int color) {
        Log.v("COlor", "new color: " + color);
        label.setColor(color);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        label.setName(s.toString());
    }
}
