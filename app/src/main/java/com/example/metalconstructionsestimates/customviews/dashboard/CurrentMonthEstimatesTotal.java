package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class CurrentMonthEstimatesTotal extends LinearLayout {
    private TextView textViewCurrentMonthEstimatesTotalLabel;
    private TextView textViewCurrentMonthEstimatesTotal;

    public CurrentMonthEstimatesTotal(Context context) {
        super(context);
        init();
    }

    public TextView getTextViewCurrentMonthEstimatesTotal() {
        return textViewCurrentMonthEstimatesTotal;
    }

    public CurrentMonthEstimatesTotal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_current_month_estimates_total, this, true);
        textViewCurrentMonthEstimatesTotalLabel = findViewById(R.id.textView_current_month_estimates_total_label);
        textViewCurrentMonthEstimatesTotal = findViewById(R.id.textView_current_month_estimates_total);
    }
}