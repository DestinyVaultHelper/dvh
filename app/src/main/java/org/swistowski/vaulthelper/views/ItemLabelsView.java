package org.swistowski.vaulthelper.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.swistowski.vaulthelper.models.Label;

import java.util.List;

/**
 * Created by damian on 31.10.15.
 */
public class ItemLabelsView extends View {
    Paint paint = new Paint();
    private List<Label> labels;

    public ItemLabelsView(Context context){
        super(context);
    }

    public ItemLabelsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemLabelsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onDraw(Canvas canvas) {
        if(labels.size()>0){
            //setAlpha(1);
            int stripWidth = canvas.getWidth()/labels.size();
            int pos = 0;
            for(Label label: labels){
                paint.setColor((int)label.getColor());
                canvas.drawRect(pos, 0, pos + stripWidth, canvas.getHeight(), paint);
                pos+=stripWidth;
            }
        }
        else {
            //setAlpha(0);
        }
    }

    public void setLabels(List<Label> labels){
        this.labels = labels;
        invalidate();
    }
}
