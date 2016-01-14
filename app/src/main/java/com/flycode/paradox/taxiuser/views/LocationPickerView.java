package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by anhaytananun on 13.01.16.
 */
public class LocationPickerView extends View {
    public LocationPickerView(Context context) {
        super(context);
    }

    public LocationPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocationPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight() / 2;

        Point a = new Point(0, 0);
        Point b = new Point(width / 2, height / 3);
        Point c = new Point(width, 0);
        Point d = new Point(width / 2, height);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#673AB7"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Path pickerPath = new Path();
        pickerPath.moveTo(a.x, a.y);
        pickerPath.lineTo(b.x, b.y);
        pickerPath.lineTo(c.x, c.y);
        pickerPath.lineTo(d.x, d.y);
        pickerPath.lineTo(a.x, a.y);

        canvas.drawPath(pickerPath, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        setMeasuredDimension(width, height * 2);
    }
}
