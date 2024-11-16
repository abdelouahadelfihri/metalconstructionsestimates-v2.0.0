package com.example.metalconstructionsestimates.customviews.estimates;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class EstimatesAllPaidUnpaid extends LinearLayout {
    private TextView textViewAllEstimates;
    private TextView textViewPaidEstimates;
    private TextView textViewPartiallyPaidEstimates
    private TextView textViewUnpaidEstimates;

    public EstimatesAllPaidUnpaid(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimates_all_paid_partiallypaid_unpaid, this, true);
        textViewAllEstimates = findViewById(R.id.textView_allEstimates);
        textViewPaidEstimates = findViewById(R.id.textView_paidEstimates);
        textViewPartiallyPaidEstimates = findViewById(R.id.textView_partiallyPaidEstimates)
        textViewUnpaidEstimates = findViewById(R.id.textView_unpaidEstimates);
        textViewAllEstimates.setBackgroundColor(Color.LTGRAY);
        textViewPartiallyPaidEstimates.setBackgroundColor(Color.LTGRAY);
        textViewPaidEstimates.setBackgroundColor(Color.LTGRAY);
        textViewUnpaidEstimates.setBackgroundColor(Color.LTGRAY);
        textViewAllEstimates.setTextColor(Color.BLACK);
        textViewPaidEstimates.setTextColor(Color.BLACK);
        textViewPartiallyPaidEstimates.setTextColor(Color.BLACK);
        textViewUnpaidEstimates.setTextColor(Color.BLACK);
    }

    public TextView getTextViewAllEstimates(){
        return textViewAllEstimates;
    }

    public TextView getTextViewPaidEstimates(){
        return textViewPaidEstimates;
    }

    public TextView getTextViewPartiallyUnpaidEstimates(){
        return textViewPartiallyPaidEstimates;
    }

    public TextView getTextViewUnpaidEstimates(){
        return textViewUnpaidEstimates;
    }
}