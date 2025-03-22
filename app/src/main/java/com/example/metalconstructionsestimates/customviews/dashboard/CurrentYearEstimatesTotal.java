package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class CurrentYearEstimatesTotal extends LinearLayout {

    private TextView textViewCurrentYearEstimatesTotal;

    public CurrentYearEstimatesTotal(Context context) {
        super(context);
        init();
    }

    public TextView getTextViewCurrentYearEstimatesTotal() {
        return textViewCurrentYearEstimatesTotal;
    }

    public CurrentYearEstimatesTotal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_current_year_estimates_total, this, true);
        textViewCurrentYearEstimatesTotal = findViewById(R.id.textView_current_year_estimates_total);
    }
}