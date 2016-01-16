package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by anhaytananun on 16.01.16.
 */
public class CarBackView extends View {
    public CarBackView(Context context) {
        super(context);
    }

    public CarBackView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CarBackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int radius = width / 8;

        Point la = new Point(0, 0);
        Point lb = new Point(width / 2, height);
        Point lc = new Point(0, height);
        Point ra = new Point(width, 0);
        Point rb = new Point(width / 2, height);
        Point rc = new Point(width, height);
        Point sa = new Point(width / 4, height / 2);
        Point sb = new Point(width / 2, height);
        Point sc = new Point(3 * width / 4, height / 2);

        RectF oval = new RectF();
        oval.set(
                width / 2 - radius,
                height - radius,
                width / 2 + radius,
                height + radius
        );

        Path leftTrianglePath = new Path();
        leftTrianglePath.moveTo(la.x, la.y);
        leftTrianglePath.lineTo(lb.x, lb.y);
        leftTrianglePath.lineTo(lc.x, lc.y);
        leftTrianglePath.lineTo(la.x, la.y);

        Path rightTrianglePath = new Path();
        rightTrianglePath.moveTo(ra.x, ra.y);
        rightTrianglePath.lineTo(rb.x, rb.y);
        rightTrianglePath.lineTo(rc.x, rc.y);
        rightTrianglePath.lineTo(ra.x, ra.y);

        Path centerTrianglePath = new Path();
        centerTrianglePath.moveTo(sa.x, sa.y);
        centerTrianglePath.lineTo(sb.x, sb.y);
        centerTrianglePath.lineTo(sc.x, sc.y);
        centerTrianglePath.lineTo(sa.x, sa.y);

        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#00FFFF"));

        canvas.drawPath(centerTrianglePath, paint);

        paint.setColor(Color.parseColor("#E64D4D4D"));

        canvas.drawPath(leftTrianglePath, paint);
        canvas.drawPath(rightTrianglePath, paint);
    }
}
