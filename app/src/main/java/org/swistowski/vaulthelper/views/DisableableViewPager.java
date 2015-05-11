package org.swistowski.vaulthelper.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DisableableViewPager extends ViewPager {
    private boolean mDissabled = false;

    public DisableableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDisabled(boolean disabled){
        mDissabled=disabled;
        setEnabled(!disabled);
        setClickable(!disabled);
        recurseDisable(this, !disabled);
    }

    private void recurseDisable(ViewGroup v, boolean state) {
        View a;
        for(int i = 0; i < v.getChildCount(); i++) {
            a = v.getChildAt(i);
            if(a instanceof ViewGroup) recurseDisable((ViewGroup) a, state);
            else if(a != null) {
                a.setEnabled(state);
                a.setClickable(state);
            }
        }
        return;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mDissabled) {
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDissabled) {
            return true;
        }
        return super.onTouchEvent(event);
    }
}
