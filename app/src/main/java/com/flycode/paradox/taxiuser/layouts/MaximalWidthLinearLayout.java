package com.flycode.paradox.taxiuser.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.flycode.paradox.taxiuser.R;

/**
 * Created by anhaytananun on 23.12.15.
 */
public class MaximalWidthLinearLayout extends LinearLayout {
    private int maxWidth;

    public MaximalWidthLinearLayout(Context context) {
        super(context);
    }

    public MaximalWidthLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        extractAttributes(context, attrs);
    }

    public MaximalWidthLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        extractAttributes(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = getMeasuredHeight();

        if (width > maxWidth) {
            width = maxWidth;
        }

        setMeasuredDimension(width, height);
    }

    private void extractAttributes(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.maximalDimension);
        maxWidth = arr.getDimensionPixelSize(R.styleable.maximalDimension_maxWidth, 0);
        arr.recycle();
    }
}
