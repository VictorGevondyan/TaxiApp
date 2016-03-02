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
 * Created by anhaytananun on 28.01.16.
 */
public class WhiteRhombusView extends View {
    private Paint solidRhombusPaint;
    private Paint strokeRhombusPaint;
    private Paint transparentRhombusPaint;

    private int shadowRadius;

    public WhiteRhombusView(Context context) {
        super(context);
        init();
    }

    public WhiteRhombusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WhiteRhombusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        float density = getContext().getResources().getDisplayMetrics().density;

        shadowRadius = (int)(12 * density);

        solidRhombusPaint = new Paint();
        solidRhombusPaint.setColor(Color.parseColor("#FFFFFF"));
        solidRhombusPaint.setStyle(Paint.Style.FILL);
        solidRhombusPaint.setAntiAlias(true);

        transparentRhombusPaint = new Paint();
        transparentRhombusPaint.setColor(Color.parseColor("#000000"));
        transparentRhombusPaint.setStyle(Paint.Style.FILL);
        transparentRhombusPaint.setAntiAlias(true);

        strokeRhombusPaint = new Paint();
        strokeRhombusPaint.setColor(Color.parseColor("#FFFFFF"));
        strokeRhombusPaint.setStyle(Paint.Style.STROKE);
        strokeRhombusPaint.setAntiAlias(true);
        strokeRhombusPaint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Point a = new Point(0, height / 2);
        Point b = new Point(width / 2, 0);
        Point c = new Point(width, height / 2);
        Point d = new Point(width / 2, height);

        Path smallRhombus = new Path();
        smallRhombus.moveTo(a.x + shadowRadius, a.y);
        smallRhombus.lineTo(b.x, b.y + shadowRadius);
        smallRhombus.lineTo(c.x - shadowRadius, c.y);
        smallRhombus.lineTo(d.x, d.y - shadowRadius);
        smallRhombus.lineTo(a.x + shadowRadius, a.y);

        Path bigRhombus = new Path();
        bigRhombus.moveTo(a.x, a.y);
        bigRhombus.lineTo(b.x, b.y);
        bigRhombus.lineTo(c.x, c.y);
        bigRhombus.lineTo(d.x, d.y);
        bigRhombus.lineTo(a.x, a.y);

        setLayerType(LAYER_TYPE_SOFTWARE, solidRhombusPaint);

        canvas.drawPath(bigRhombus, transparentRhombusPaint);
        canvas.drawPath(bigRhombus, strokeRhombusPaint);
        canvas.drawPath(smallRhombus, solidRhombusPaint);
    }
}
