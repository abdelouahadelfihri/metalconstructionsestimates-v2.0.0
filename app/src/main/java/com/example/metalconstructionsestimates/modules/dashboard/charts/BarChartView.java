package com.example.metalconstructionsestimates.modules.dashboard.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.metalconstructionsestimates.R;

import java.util.Locale;

/**
 * Lightweight vertical bar chart. Shows the last 6 bars on narrow screens and
 * all bars (up to 12) when there is enough room. Tapping a bar selects it and
 * shows its formatted value above it.
 */
public class BarChartView extends View {

    public interface ValueFormatter {
        String format(float value);
    }

    private static final int NARROW_BAR_COUNT = 6;
    private static final float WIDE_THRESHOLD_DP = 480f;

    private float[] values = new float[0];
    private String[] labels = new String[0];
    private ValueFormatter formatter = v -> String.valueOf(v);

    private int firstVisible;
    private int selected = -1;

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valueTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final float density;

    private final int brandColor;
    private final int trackColor;

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        float scaled = getResources().getDisplayMetrics().scaledDensity;

        brandColor = ContextCompat.getColor(context, R.color.dashboard_brand);
        trackColor = ContextCompat.getColor(context, R.color.dashboard_track);
        int secondaryText = ContextCompat.getColor(context, R.color.dashboard_text_secondary);
        int primaryText = ContextCompat.getColor(context, R.color.dashboard_text_primary);

        gridPaint.setColor(trackColor);
        gridPaint.setStrokeWidth(density);

        axisTextPaint.setColor(secondaryText);
        axisTextPaint.setTextSize(11 * scaled);

        valueTextPaint.setColor(primaryText);
        valueTextPaint.setTextSize(12 * scaled);
        valueTextPaint.setFakeBoldText(true);
        valueTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(float[] values, String[] labels) {
        this.values = values;
        this.labels = labels;
        selected = values.length - 1;
        requestLayout();
        invalidate();
    }

    public void setValueFormatter(ValueFormatter formatter) {
        this.formatter = formatter;
        invalidate();
    }

    private int visibleCount() {
        boolean wide = getWidth() / density >= WIDE_THRESHOLD_DP;
        return wide ? values.length : Math.min(NARROW_BAR_COUNT, values.length);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (values.length == 0) return;

        int count = visibleCount();
        firstVisible = values.length - count;
        if (selected < firstVisible) selected = values.length - 1;

        float max = 0f;
        for (int i = firstVisible; i < values.length; i++) max = Math.max(max, values[i]);
        float niceMax = niceCeiling(max);

        float axisWidth = axisTextPaint.measureText(compact(niceMax)) + 8 * density;
        float left = getPaddingLeft() + axisWidth;
        float right = getWidth() - getPaddingRight();
        float top = getPaddingTop() + valueTextPaint.getTextSize() + 10 * density;
        float labelHeight = axisTextPaint.getTextSize() + 8 * density;
        float bottom = getHeight() - getPaddingBottom() - labelHeight;
        float chartHeight = bottom - top;

        // Grid lines + axis labels
        int gridLines = 3;
        axisTextPaint.setTextAlign(Paint.Align.LEFT);
        for (int g = 0; g <= gridLines; g++) {
            float y = bottom - chartHeight * g / gridLines;
            canvas.drawLine(left, y, right, y, gridPaint);
            // With no data only the baseline gets a label
            if (g > 0 && niceMax == 0) continue;
            String text = compact(niceMax * g / gridLines);
            canvas.drawText(text, getPaddingLeft(), y + axisTextPaint.getTextSize() / 3, axisTextPaint);
        }

        float slot = (right - left) / count;
        float barWidth = Math.min(slot * 0.6f, 36 * density);
        float radius = Math.min(6 * density, barWidth / 2);

        axisTextPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < count; i++) {
            int index = firstVisible + i;
            float cx = left + slot * i + slot / 2;
            float h = niceMax == 0 ? 0 : chartHeight * values[index] / niceMax;
            boolean isSelected = index == selected;

            // Background track so empty months still read as a slot
            barPaint.setColor(trackColor);
            barPaint.setAlpha(110);
            rect.set(cx - barWidth / 2, top, cx + barWidth / 2, bottom);
            canvas.drawRoundRect(rect, radius, radius, barPaint);

            if (h > 0) {
                barPaint.setColor(brandColor);
                barPaint.setAlpha(isSelected ? 255 : 150);
                float barTop = bottom - Math.max(h, 2 * density);
                rect.set(cx - barWidth / 2, barTop, cx + barWidth / 2, bottom);
                canvas.drawRoundRect(rect, radius, radius, barPaint);
                // Square off the bottom corners
                rect.set(cx - barWidth / 2, Math.max(barTop, bottom - radius), cx + barWidth / 2, bottom);
                canvas.drawRect(rect, barPaint);
            }

            axisTextPaint.setFakeBoldText(isSelected);
            canvas.drawText(labels[index], cx, getHeight() - getPaddingBottom() - 2 * density, axisTextPaint);

            if (isSelected && values[index] > 0) {
                String text = formatter.format(values[index]);
                float textWidth = valueTextPaint.measureText(text);
                // Keep the value label inside the chart bounds
                float tx = Math.max(left + textWidth / 2, Math.min(right - textWidth / 2, cx));
                float ty = bottom - h - 6 * density;
                canvas.drawText(text, tx, ty, valueTextPaint);
            }
        }
        axisTextPaint.setFakeBoldText(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (values.length == 0) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) return true;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int count = visibleCount();
            float niceMax = niceCeiling(maxVisible(count));
            float axisWidth = axisTextPaint.measureText(compact(niceMax)) + 8 * density;
            float left = getPaddingLeft() + axisWidth;
            float right = getWidth() - getPaddingRight();
            float slot = (right - left) / count;
            int i = (int) ((event.getX() - left) / slot);
            if (i >= 0 && i < count) {
                selected = values.length - count + i;
                invalidate();
                performClick();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private float maxVisible(int count) {
        float max = 0f;
        for (int i = values.length - count; i < values.length; i++) max = Math.max(max, values[i]);
        return max;
    }

    /** Rounds up to a "nice" axis maximum (1, 2, 2.5, 5 x 10^n). */
    private static float niceCeiling(float value) {
        if (value <= 0) return 0;
        double exp = Math.pow(10, Math.floor(Math.log10(value)));
        double f = value / exp;
        double nice = f <= 1 ? 1 : f <= 2 ? 2 : f <= 2.5 ? 2.5 : f <= 5 ? 5 : 10;
        return (float) (nice * exp);
    }

    public static String compact(float v) {
        if (v >= 1_000_000f) return trim(v / 1_000_000f) + "M";
        if (v >= 1_000f) return trim(v / 1_000f) + "k";
        return String.valueOf(Math.round(v));
    }

    private static String trim(float v) {
        return v >= 10 || v == Math.floor(v)
                ? String.valueOf(Math.round(v))
                : String.format(Locale.US, "%.1f", v);
    }
}
