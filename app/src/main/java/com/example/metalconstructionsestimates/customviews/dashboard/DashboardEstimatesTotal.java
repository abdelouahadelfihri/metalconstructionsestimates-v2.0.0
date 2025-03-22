package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class DashboardEstimatesTotal extends LinearLayout {
    private TextView textView_all_estimates_total_label;
    private TextView textView_all_estimates_total;

    public TextView getTextViewAllEstimatesTotalLabel(){
        return textView_all_estimates_total_label;
    }

    public TextView getTextViewAllEstimatesTotal(){
        return textView_all_estimates_total;
    }


    public DashboardEstimatesTotal(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_total_all_estimates, this, true);
        textView_all_estimates_total = findViewById(R.id.textView_all_estimates_total);
    }
}