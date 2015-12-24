package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by anhaytananun on 22.12.15.
 */
public class ReceivePasswordView extends View {
    private boolean isTouched = false;

    public ReceivePasswordView(Context context) {
        super(context);
    }

    public ReceivePasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReceivePasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Point a = new Point(0, 0);
        Point b = new Point(width / 2, height);
        Point c = new Point(width, 0);

        Path trianglePath = new Path();
        trianglePath.moveTo(a.x, a.y);
        trianglePath.lineTo(b.x, b.y);
        trianglePath.lineTo(c.x, c.y);
        trianglePath.lineTo(a.x, a.y);

        Paint paint = new Paint();
        paint.setStrokeWidth(2);

        if (isTouched) {
            paint.setColor(Color.parseColor("#117F20"));
        } else {
            paint.setColor(Color.parseColor("#7A7A7A"));
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        canvas.drawPath(trianglePath, paint);

        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(height / 6);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setLinearText(true);
        textPaint.setTypeface(TypefaceUtils.getTypeface(getContext(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        String receive = getContext().getString(R.string.receive_password).split("\n")[0];
        String password = getContext().getString(R.string.receive_password).split("\n")[1];
        Rect receiveBounds = new Rect();
        Rect passwordBounds = new Rect();
        textPaint.getTextBounds(receive, 0, receive.length(), receiveBounds);
        textPaint.getTextBounds(password, 0, password.length(), passwordBounds);

        canvas.drawText(
                receive,
                (width - receiveBounds.width()) / 2,
                height / 3 - receiveBounds.height() + receiveBounds.height() / 5,
                textPaint);
        canvas.drawText(
                password,
                (width - passwordBounds.width()) / 2,
                height / 3 + receiveBounds.height(),
                textPaint);

        canvas.scale(1, 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isTouched = true;
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isTouched = false;
            invalidate();
        }

        return super.onTouchEvent(event);
    }
}