package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class CurrentDayEstimatesCount extends LinearLayout {
    private TextView textViewCurrentDayEstimatesCountLabel;
    private TextView textViewCurrentDayEstimatesCount;

    public CurrentDayEstimatesCount(Context context) {
        super(context);
        init();
    }

    public TextView getTextViewCurrentDayEstimatesCount() {
        return textViewCurrentDayEstimatesCount;
    }

    public CurrentDayEstimatesCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_current_day_estimates_count, this, true);
        textViewCurrentDayEstimatesCount = findViewById(R.id.textView_current_day_estimates_count);
    }
}