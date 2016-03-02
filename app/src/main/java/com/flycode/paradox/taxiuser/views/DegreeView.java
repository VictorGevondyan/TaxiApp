package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by anhaytananun on 17.02.16.
 */
public class DegreeView extends View {

    public DegreeView(Context context) {
        super(context);
    }

    public DegreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DegreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();

        setPivotY(getY() + 6 * height);
    }
}
