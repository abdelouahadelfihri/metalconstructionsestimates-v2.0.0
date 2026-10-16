package com.example.metalconstructionsestimates.modules.dashboard.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.metalconstructionsestimates.R;

/**
 * Donut chart with a headline and caption drawn in the centre.
 */
public class DonutChartView extends View {

    private float[] values = new float[0];
    private int[] colors = new int[0];
    private String centerTitle = "";
    private String centerCaption = "";

    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint captionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();
    private final int trackColor;
    private final float density;

    public DonutChartView(Context context) {
        this(context, null);
    }

    public DonutChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        float scaled = getResources().getDisplayMetrics().scaledDensity;
        trackColor = ContextCompat.getColor(context, R.color.dashboard_track);

        ringPaint.setStyle(Paint.Style.STROKE);

        titlePaint.setColor(ContextCompat.getColor(context, R.color.dashboard_text_primary));
        titlePaint.setTextSize(24 * scaled);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        captionPaint.setColor(ContextCompat.getColor(context, R.color.dashboard_text_secondary));
        captionPaint.setTextSize(12 * scaled);
        captionPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(float[] values, int[] colors, String centerTitle, String centerCaption) {
        this.values = values;
        this.colors = colors;
        this.centerTitle = centerTitle;
        this.centerCaption = centerCaption;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Square: as tall as it is wide, bounded by the requested height
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int size = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED ? w : Math.min(w, h);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = Math.min(getWidth(), getHeight());
        float stroke = size * 0.13f;
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float r = size / 2f - stroke / 2f - getPaddingTop();
        oval.set(cx - r, cy - r, cx + r, cy + r);
        ringPaint.setStrokeWidth(stroke);

        ringPaint.setColor(trackColor);
        canvas.drawArc(oval, 0, 360, false, ringPaint);

        float total = 0f;
        int nonZero = 0;
        for (float v : values) {
            total += v;
            if (v > 0) nonZero++;
        }

        if (total > 0) {
            float gap = nonZero > 1 ? 2.5f : 0f;
            float start = -90f;
            for (int i = 0; i < values.length; i++) {
                if (values[i] <= 0) continue;
                float sweep = 360f * values[i] / total;
                ringPaint.setColor(colors[i]);
                canvas.drawArc(oval, start + gap / 2, Math.max(sweep - gap, 0.5f), false, ringPaint);
                start += sweep;
            }
        }

        float titleY = cy + titlePaint.getTextSize() / 3f - 4 * density;
        canvas.drawText(centerTitle, cx, titleY, titlePaint);
        canvas.drawText(centerCaption, cx, titleY + captionPaint.getTextSize() + 6 * density, captionPaint);
    }
}
