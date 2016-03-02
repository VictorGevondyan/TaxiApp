package com.flycode.paradox.taxiuser.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by anhaytananun on 29.01.16.
 */
public class FullScreenRelativeLayout extends RelativeLayout {
    public FullScreenRelativeLayout(Context context) {
        super(context);
    }

    public FullScreenRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        setMeasuredDimension(metrics.widthPixels, metrics.heightPixels);
    }
}
