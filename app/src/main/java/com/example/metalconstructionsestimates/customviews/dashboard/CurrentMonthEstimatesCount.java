package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class CurrentMonthEstimatesCount extends LinearLayout {
    private TextView textViewCurrentMonthEstimatesCountLabel;
    private TextView textViewCurrentMonthEstimatesCount;

    public CurrentMonthEstimatesCount(Context context) {
        super(context);
        init();
    }

    public TextView getTextViewCurrentMonthEstimatesCount() {
        return textViewCurrentMonthEstimatesCount;
    }

    public CurrentMonthEstimatesCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_current_month_estimates_count, this, true);
        textViewCurrentMonthEstimatesCountLabel = findViewById(R.id.textView_current_month_estimates_count_label);
        textViewCurrentMonthEstimatesCount = findViewById(R.id.textView_current_month_estimates_count);
    }
}