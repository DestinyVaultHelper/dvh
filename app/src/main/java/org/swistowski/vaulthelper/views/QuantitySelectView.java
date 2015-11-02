package org.swistowski.vaulthelper.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.swistowski.vaulthelper.R;

/**
 * Created by damian on 21.10.15.
 */
public class QuantitySelectView extends FrameLayout {
    private static int MIN = 1;
    private TextView mLabel;
    private SeekBar mSeekbar;

    static public void getStackValue(Context context, int stackSize, final OnStackSelectInterface onStackSelectInterface) {
        final QuantitySelectView selectView = new QuantitySelectView(context);
        selectView.setMax(stackSize);

        new AlertDialog.Builder(context)
                .setTitle(R.string.please_select_a_quantity)
                .setView(selectView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onStackSelectInterface.onStackSizeSelect(selectView.getValue());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public QuantitySelectView(Context context) {
        super(context);
        init();
    }

    public QuantitySelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuantitySelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.quantyty_select, this, true);
        mLabel = (TextView) findViewById(R.id.quantytySelectLabel);
        mSeekbar = (SeekBar) findViewById(R.id.quantytySelectSeekBar);
        setMax(2);
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mLabel.setText(Integer.toString(i + MIN));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setMax(int max) {
        mSeekbar.setMax(max - MIN);
        mSeekbar.setProgress(max - MIN);
    }

    public int getValue() {
        return mSeekbar.getProgress() + MIN;
    }

    public interface OnStackSelectInterface {
        void onStackSizeSelect(int i);
    }
}
