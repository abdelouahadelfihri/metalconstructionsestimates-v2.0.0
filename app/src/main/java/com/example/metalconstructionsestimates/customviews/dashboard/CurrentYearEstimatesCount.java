package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class CurrentYearEstimatesCount extends LinearLayout {
    private TextView textViewCurrentYearEstimatesCountLabel;
    private TextView textViewCurrentYearEstimatesCount;

    public CurrentYearEstimatesCount(Context context) {
        super(context);
        init();
    }

    public TextView getTextViewCurrentYearEstimatesCount() {
        return textViewCurrentYearEstimatesCount;
    }

    public CurrentYearEstimatesCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_current_year_estimates_count, this, true);
        textViewCurrentYearEstimatesCountLabel = findViewById(R.id.textView_current_year_estimates_count_label);
        textViewCurrentYearEstimatesCount = findViewById(R.id.textView_current_year_estimates_count);
    }
}