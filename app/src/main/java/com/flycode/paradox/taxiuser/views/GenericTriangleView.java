package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

import java.util.Locale;

/**
 * Created by anhaytananun on 22.12.15.
 */
public class GenericTriangleView extends View {
    private String text;
    private String colorString;
    private String hoverColorString;
    private String textColorString;

    private boolean isTouched = false;
    private boolean isUp = false;

    private Paint textPaint;
    private Path trianglePath;

    public GenericTriangleView(Context context) {
        super(context);
    }

    public GenericTriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        extractAttributes(context, attrs);
    }

    public GenericTriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        extractAttributes(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (colorString == null
                || text == null) {
            return;
        }

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        trianglePath.reset();

        if (isUp) {
            trianglePath.moveTo(0, height);
            trianglePath.lineTo(width / 2, 0);
            trianglePath.lineTo(width, height);
            trianglePath.lineTo(0, height);
        } else {
            trianglePath.moveTo(0, 0);
            trianglePath.lineTo(width / 2, height);
            trianglePath.lineTo(width, 0);
            trianglePath.lineTo(0, 0);
        }

        Paint paint = new Paint();
        paint.setStrokeWidth(0);

        if (isTouched) {
            paint.setColor(Color.parseColor(hoverColorString));
        } else {
            paint.setColor(Color.parseColor(colorString));
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        canvas.drawPath(trianglePath, paint);

        Rect textBounds = new Rect();
        textPaint.setTextSize(height / 4);
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        if (isUp) {
            canvas.drawText(
                    text,
                    (width - textBounds.width()) / 2,
                    height / 2 + 3 * textBounds.height() / 2,
                    textPaint);
        } else {
            canvas.drawText(
                    text,
                    (width - textBounds.width()) / 2,
                    height / 2 - textBounds.height() / 2,
                    textPaint);
        }

        canvas.scale(1, 1);
    }

    private void extractAttributes(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.genericTriangle);
        text = arr.getText(R.styleable.genericTriangle_text).toString().toUpperCase(Locale.ENGLISH);
        colorString = arr.getText(R.styleable.genericTriangle_colorString).toString();
        hoverColorString = arr.getText(R.styleable.genericTriangle_hoverColorString).toString();
        textColorString = arr.getText(R.styleable.genericTriangle_textColorString).toString();

        isUp = arr.getBoolean(R.styleable.genericTriangle_isUp, false);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor(textColorString));
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setLinearText(true);
        textPaint.setTypeface(TypefaceUtils.getTypeface(getContext(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR));

        trianglePath = new Path();

        arr.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isTouched = true;
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isTouched = false;
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            isTouched = false;
            invalidate();
        }

        return super.onTouchEvent(event);
    }
}
