package com.flycode.paradox.taxiuser.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by anhaytananun on 13.01.16.
 */
public class RhombusLayout extends RelativeLayout {
    public RhombusLayout(Context context) {
        super(context);
    }

    public RhombusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RhombusLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        int width = getMeasuredWidth();

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }

        float density = getContext().getResources().getDisplayMetrics().density;
        int padding = (int)(density * 10);

        View topChild = getChildAt(0);
        View leftChild = getChildAt(1);
        View rightChild = getChildAt(2);
        View bottomChild = getChildAt(3);

        topChild.layout(
                getMeasuredWidth() / 4 + padding,
                padding,
                3 * getMeasuredWidth() / 4 - padding,
                getMeasuredHeight() / 2 - padding);
        leftChild.layout(
                padding,
                getMeasuredWidth() / 4 + padding,
                getMeasuredWidth() / 2 - padding,
                3 * getMeasuredHeight() / 4 - padding);
        rightChild.layout(
                getMeasuredWidth() / 2 + padding,
                getMeasuredWidth() / 4 + padding,
                getMeasuredWidth() - padding,
                3 * getMeasuredWidth() / 4 - padding);
        bottomChild.layout(
                getMeasuredWidth() / 4 + padding,
                getMeasuredWidth() / 2 + padding,
                3 * getMeasuredWidth() / 4 - padding,
                getMeasuredHeight() - padding);
    }
}
