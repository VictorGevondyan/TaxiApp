package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.flycode.paradox.taxiuser.R;
import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by anhaytananun on 22.12.15.
 */
public class GenericTriangleView extends View {
    private String text;
    private String borderColor;

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
        if (borderColor == null
                || text == null) {
            return;
        }

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
        paint.setColor(Color.parseColor(borderColor));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        canvas.drawPath(trianglePath, paint);

        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(height / 4);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setLinearText(true);
        textPaint.setTypeface(TypefaceUtils.getTypeface(getContext(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_THIN));

        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        canvas.drawText(
                text,
                (width - textBounds.width()) / 2,
                height / 2 - textBounds.height() / 2,
                textPaint);

        canvas.scale(1, 1);
    }

    private void extractAttributes(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.genericTriangle);
        text = arr.getText(R.styleable.genericTriangle_text).toString();
        borderColor = arr.getText(R.styleable.genericTriangle_borderColor).toString();
        arr.recycle();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }
}