package com.example.metalconstructionsestimates.customviews.estimatesdetails;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;

public class RefreshDeleteEstimateButtons extends LinearLayout {
    private Button refreshEstimate;
    private Button deleteEstimate;
    public RefreshDeleteEstimateButtons(Context context) {
        super(context);
        init();
    }

    public Button getButtonRefreshEstimate(){
        return refreshEstimate;
    }

    public Button getButtonDeleteEstimate(){
        return deleteEstimate;
    }

    public RefreshDeleteEstimateButtons(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_details_refresh_delete_buttons, this, true);
        deleteEstimate = findViewById(R.id.button_delete_estimate);
        refreshEstimate = findViewById(R.id.button_refresh_estimate);
    }
}