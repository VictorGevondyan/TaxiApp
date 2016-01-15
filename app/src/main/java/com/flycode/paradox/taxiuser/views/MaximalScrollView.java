package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.flycode.paradox.taxiuser.R;

/**
 * Created by anhaytananun on 23.12.15.
 */
public class MaximalScrollView extends ScrollView {
    private int maxWidth = -1;
    private int maxHeight = -1;

    public MaximalScrollView(Context context) {
        super(context);
    }

    public MaximalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        extractAttributes(context, attrs);
    }

    public MaximalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        extractAttributes(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (maxWidth != -1 && width > maxWidth) {
            width = maxWidth;
        }
        if (maxHeight != -1 && height > maxHeight) {
            height = maxHeight;
        }

        setMeasuredDimension(width, height);
    }

    private void extractAttributes(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.maximalDimension);
        maxWidth = arr.getDimensionPixelSize(R.styleable.maximalDimension_maxWidth, -1);
        arr.recycle();
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}