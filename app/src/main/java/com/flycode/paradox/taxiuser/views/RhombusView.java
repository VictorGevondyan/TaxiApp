package com.flycode.paradox.taxiuser.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Button;

import com.flycode.paradox.taxiuser.utils.TypefaceUtils;

/**
 * Created by anhaytananun on 18.01.16.
 */
public class RhombusView extends Button {
    private boolean isFilled;
    private int textColor;
    private int color;

    private Paint textPaint;

    public RhombusView(Context context) {
        super(context);
        init();
    }

    public RhombusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RhombusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMinHeight(0);
        setMinWidth(0);
        setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#00A99D"));
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setLinearText(true);
        textPaint.setTypeface(TypefaceUtils.getTypeface(getContext(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float density = getContext().getResources().getDisplayMetrics().density;
        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
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
        paint.setStyle(isFilled ? Paint.Style.FILL: Paint.Style.STROKE);
        paint.setStrokeWidth(isFilled ? 0 : density);
        paint.setAntiAlias(true);

        canvas.drawPath(path, paint);

        if (getText() != null && getText().length() != 0) {
            Rect bounds = new Rect();
            textPaint.setTextSize(15 * scaledDensity);
            textPaint.getTextBounds(getText().toString(), 0, getText().length(), bounds);
            textPaint.setColor(textColor);

            canvas.drawText(
                    getText().toString(),
                    (width - bounds.width()) / 2,
                    (height - textPaint.descent() - textPaint.ascent()) / 2,
                    textPaint);
        }
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
        invalidate();
    }
}
