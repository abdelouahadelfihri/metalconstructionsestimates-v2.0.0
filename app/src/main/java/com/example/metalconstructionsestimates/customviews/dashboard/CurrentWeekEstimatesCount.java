package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class CurrentWeekEstimatesCount extends LinearLayout {
    private TextView textViewCurrentWeekEstimatesCount;

    public CurrentWeekEstimatesCount(Context context) {
        super(context);
        init();
    }

    public TextView getTextViewCurrentWeekEstimatesCount() {
        return textViewCurrentWeekEstimatesCount;
    }

    public CurrentWeekEstimatesCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_current_week_estimates_count, this, true);
        textViewCurrentWeekEstimatesCount = findViewById(R.id.textView_current_week_estimates_count);
    }
}