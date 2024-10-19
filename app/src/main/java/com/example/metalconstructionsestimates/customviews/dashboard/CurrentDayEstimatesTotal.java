package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class CurrentDayEstimatesTotal extends LinearLayout {
    private TextView textViewCurrentDayEstimatesTotalLabel;
    private TextView textViewCurrentDayEstimatesTotal;

    public CurrentDayEstimatesTotal(Context context) {
        super(context);
        init();
    }

    public TextView getTextViewCurrentDayEstimatesTotal() {
        return textViewCurrentDayEstimatesTotal;
    }

    public CurrentDayEstimatesTotal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_current_day_estimates_total, this, true);
        textViewCurrentDayEstimatesTotalLabel = findViewById(R.id.textView_current_day_estimates_total_label);
        textViewCurrentDayEstimatesTotal = findViewById(R.id.textView_current_day_estimates_total);
    }
}