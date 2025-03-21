package com.example.metalconstructionsestimates.customviews.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class DashboardDatabaseEntitiesTotals extends LinearLayout {
    private TextView textViewSteelsCount;
    private TextView textViewCustomersCount;

    private TextView textViewEstimatesCount;

    public TextView getTextViewSteelsCount(){
        return textViewSteelsCount;
    }

    public TextView getTextViewCustomersCount(){
        return textViewCustomersCount;
    }

    public TextView getTextViewEstimatesCount(){
        return textViewEstimatesCount;
    }



    public DashboardDatabaseEntitiesTotals(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.dashboard_entities_counts, this, true);
        textViewEstimatesCount = findViewById(R.id.textView_estimates_count);
        textViewSteelsCount = findViewById(R.id.textView_steels_count);
        textViewCustomersCount = findViewById(R.id.textView_customers_count);
    }
}