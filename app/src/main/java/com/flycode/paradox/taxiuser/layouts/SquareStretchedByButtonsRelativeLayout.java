package com.flycode.paradox.taxiuser.layouts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by anhaytananun on 13.01.16.
 */
public class SquareStretchedByButtonsRelativeLayout extends RelativeLayout {
    public SquareStretchedByButtonsRelativeLayout(Context context) {
        super(context);
    }

    public SquareStretchedByButtonsRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareStretchedByButtonsRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = 0;

//        for (int index = 0 ; index < getChildCount() ; index++) {
//            View child = getChildAt(index);
//
//            if (child instanceof Button) {
//                child.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                if (child.getMeasuredWidth() > width) {
//                    width = child.getMeasuredWidth();
//                }
//            }
//        }

        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        int width = getMeasuredWidth();

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint pincel1 = new Paint();
        pincel1.setColor(Color.rgb(151, 217, 69));
        pincel1.setShadowLayer(10.0f, 0.0f, 2.0f, 0xFF000000);
        RectF rectangle = new RectF(30, 20,200,100);
        canvas.drawRoundRect(rectangle, 6, 6, pincel1);
    }
}