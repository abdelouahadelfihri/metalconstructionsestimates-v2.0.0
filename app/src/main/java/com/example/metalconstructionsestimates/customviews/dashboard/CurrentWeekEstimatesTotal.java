package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class CurrentWeekEstimatesTotal extends LinearLayout {
    private TextView textViewCurrentWeekEstimatesTotalLabel;
    private TextView textViewCurrentWeekEstimatesTotal;

    public CurrentWeekEstimatesTotal(Context context) {
        super(context);
        init();
    }

    public TextView getTextViewCurrentWeekEstimatesTotal() {
        return textViewCurrentWeekEstimatesTotal;
    }

    public CurrentWeekEstimatesTotal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_current_week_estimates_total, this, true);
        textViewCurrentWeekEstimatesTotalLabel = findViewById(R.id.textView_current_week_estimates_total_label);
        textViewCurrentWeekEstimatesTotal = findViewById(R.id.textView_current_week_estimates_total);
    }
}