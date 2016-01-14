package com.flycode.paradox.taxiuser.views;

import android.content.Context;
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
 * Created by anhaytananun on 13.01.16.
 */
public class OrderView extends View {
    private int orderStage = 0;

    private Paint textPaint;
    private Paint solidRhombusPaint;
    private Paint transparentRhombusPaint;
    private Paint strokeRhombusPaint;

    private float density;
    private float scaledDensity;

    private int shadowRadius;

    public OrderView(Context context) {
        super(context);
        init();
    }

    public OrderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        density = getContext().getResources().getDisplayMetrics().density;

        shadowRadius = (int)(12 * density);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#4D4D4D"));
        textPaint.setTextSize(15 * scaledDensity);
        textPaint.setFakeBoldText(false);
        textPaint.setAntiAlias(true);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setLinearText(true);
        textPaint.setTypeface(TypefaceUtils.getTypeface(getContext(), TypefaceUtils.AVAILABLE_FONTS.ROBOTO_REGULAR));

        solidRhombusPaint = new Paint();
        solidRhombusPaint.setColor(Color.parseColor("#00FFFF"));
        solidRhombusPaint.setStyle(Paint.Style.FILL);
        solidRhombusPaint.setAntiAlias(true);

        transparentRhombusPaint = new Paint();
        transparentRhombusPaint.setColor(Color.parseColor("#00FFFF"));
        transparentRhombusPaint.setAlpha(51);
        transparentRhombusPaint.setStyle(Paint.Style.FILL);
        transparentRhombusPaint.setAntiAlias(true);

        strokeRhombusPaint = new Paint();
        strokeRhombusPaint.setColor(Color.parseColor("#00FFFF"));
        strokeRhombusPaint.setStrokeWidth(1);
        strokeRhombusPaint.setStyle(Paint.Style.STROKE);
        strokeRhombusPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
        float density = getContext().getResources().getDisplayMetrics().density;

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

        String order = getContext().getString(R.string.order);
        String confirm = getContext().getString(R.string.confirm);
        Rect orderBounds = new Rect();
        Rect confirmBounds = new Rect();
        textPaint.getTextBounds(order, 0, order.length(), orderBounds);
        textPaint.getTextBounds(confirm, 0, confirm.length(), confirmBounds);

        if (orderStage == 0) {
            canvas.drawText(
                    order,
                    (width - orderBounds.width()) / 2,
                    (height - textPaint.descent() - textPaint.ascent()) / 2,
                    textPaint);
        } else if (orderStage == 1) {
            canvas.drawText(
                    confirm,
                    (width - confirmBounds.width()) / 2,
                    (height - textPaint.descent() - textPaint.ascent()) / 2,
                    textPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        String order = getContext().getString(R.string.order);
        String confirm = getContext().getString(R.string.confirm);
        Rect orderBounds = new Rect();
        Rect confirmBounds = new Rect();
        textPaint.getTextBounds(order, 0, order.length(), orderBounds);
        textPaint.getTextBounds(confirm, 0, confirm.length(), confirmBounds);

        int width = orderBounds.width() > confirmBounds.width() ? orderBounds.width() : confirmBounds.width();
        width = width + (int) (2.5 * orderBounds.height()) + shadowRadius;

        setMeasuredDimension(width, width);
    }

    public void setOrderStage(int orderStage) {
        this.orderStage = orderStage;
        invalidate();
    }
}
