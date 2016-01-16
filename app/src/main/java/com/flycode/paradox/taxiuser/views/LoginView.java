package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by anhaytananun on 22.12.15.
 */
public class LoginView extends View {
    private boolean isTouched = false;

    public LoginView(Context context) {
        super(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int radius = width / 8;

        Point a = new Point(0, height);
        Point b = new Point(width / 2, 0);
        Point c = new Point(width, height);
        Point d = new Point(width / 2 + radius, height);
        Point e = new Point(width / 2 - radius, height);

        RectF oval = new RectF();
//        oval.set(
//                width / 2 - radius,
//                height / 2 - radius,
//                width / 2 + radius,
//                height / 2 + radius
//        );
        oval.set(
                width / 2 - radius,
                height - radius,
                width / 2 + radius,
                height + radius
        );

        Path trianglePath = new Path();
        trianglePath.moveTo(e.x, e.y);
        trianglePath.lineTo(a.x, a.y);
        trianglePath.lineTo(b.x, b.y);
        trianglePath.lineTo(c.x, c.y);
        trianglePath.lineTo(d.x, d.y);
        trianglePath.arcTo(oval, 0, -180);

        Paint paint = new Paint();
        paint.setStrokeWidth(2);

        if (isTouched) {
            paint.setColor(Color.parseColor("#66FFFFFF"));
        } else {
            paint.setColor(Color.parseColor("#66FFFFFF"));
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        canvas.drawPath(trianglePath, paint);

        Paint loginTextPaint = new Paint();
        loginTextPaint.setStyle(Paint.Style.FILL);
        loginTextPaint.setColor(Color.parseColor("#CCFFFFFF"));
        loginTextPaint.setTextSize(height / 6);
        loginTextPaint.setFakeBoldText(false);
        loginTextPaint.setAntiAlias(true);
        loginTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        loginTextPaint.setLinearText(true);
        loginTextPaint.setTypeface(TypefaceUtils.getTypeface(getContext(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR));

        Paint orTextPaint = new Paint();
        orTextPaint.setStyle(Paint.Style.FILL);
        orTextPaint.setColor(Color.parseColor("#CCFFFFFF"));
        orTextPaint.setTextSize(height / 6);
        orTextPaint.setFakeBoldText(false);
        orTextPaint.setAntiAlias(true);
        orTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        orTextPaint.setLinearText(true);
        orTextPaint.setTypeface(TypefaceUtils.getTypeface(getContext(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        String loginText = getContext().getString(R.string.login);
        String orText = getContext().getString(R.string.or);
        Rect loginBounds = new Rect();
        Rect orBounds = new Rect();
        loginTextPaint.getTextBounds(loginText, 0, loginText.length(), loginBounds);
        orTextPaint.getTextBounds(orText, 0, orText.length(), orBounds);

        canvas.drawText(
                loginText,
                (width - loginBounds.width()) / 2,
                (height - loginBounds.height()) / 2 + loginBounds.height(),
                loginTextPaint);
        canvas.drawText(orText,
                (width - orBounds.width()) / 2,
                height,
                orTextPaint);
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
