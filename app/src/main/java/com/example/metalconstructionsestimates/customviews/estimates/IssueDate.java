package com.example.metalconstructionsestimates.customviews.estimates;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class IssueDate extends LinearLayout {

    private TextView estimateIssueDateLabel;
    private TextView estimateIssueDate;

    public IssueDate(Context context) {
        super(context);
        init();
    }
    public IssueDate(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_issue_date, this, true);
        estimateIssueDate = findViewById(R.id.textView_select_estimate_creation_date);
        estimateExpirationDate = findViewById(R.id.textView_select_estimate_expiration_date);
    }

    public TextView getTextViewEstimateIssueDate(){

        return estimateIssueDate;
    }

    public TextView getTextViewEstimateExpirationDate(){
        return estimateExpirationDate;
    }

}