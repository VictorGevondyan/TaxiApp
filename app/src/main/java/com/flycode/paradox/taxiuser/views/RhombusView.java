package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by anhaytananun on 18.01.16.
 */
public class RhombusView extends View {
    private boolean isFilled;
    private int color;

    public RhombusView(Context context) {
        super(context);
    }

    public RhombusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RhombusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float density = getContext().getResources().getDisplayMetrics().density;
        int sideStroke = 1;

        Point a = new Point(sideStroke, height / 2);
        Point b = new Point(width / 2, sideStroke);
        Point c = new Point(width - sideStroke, height / 2);
        Point d = new Point(width / 2, height - sideStroke);

        Path path = new Path();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(d.x, d.y);
        path.lineTo(a.x, a.y);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(isFilled ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
        paint.setStrokeWidth(density);
        paint.setAntiAlias(true);

        canvas.drawPath(path, paint);
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
        invalidate();
    }
}
