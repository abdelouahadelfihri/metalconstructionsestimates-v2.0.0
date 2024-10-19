package com.example.metalconstructionsestimates.customviews.estimatesdetails;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;

public class NewEstimateLineUpdateButtons extends LinearLayout {
    private Button newEstimateLine;
    private Button updateEstimate;
    public NewEstimateLineUpdateButtons(Context context) {
        super(context);
        init();
    }

    public Button getButtonNewEstimateLine(){
        return newEstimateLine;
    }

    public Button getButtonUpdateEstimate(){
        return updateEstimate;
    }

    public NewEstimateLineUpdateButtons(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_details_update_new_estimate_line_buttons, this, true);
        newEstimateLine = findViewById(R.id.button_new_estimate_line);
        updateEstimate = findViewById(R.id.button_update_estimate);
    }
}