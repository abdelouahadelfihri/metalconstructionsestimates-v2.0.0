package com.example.metalconstructionsestimates.customviews.estimates;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.metalconstructionsestimates.R;

public class EstimatesAllPaidPartiallyPaidUnpaid extends LinearLayout {
    private TextView textViewPaymentStatus;
    private TextView spinnerPaymentStatus;

    public EstimatesAllPaidPartiallyPaidUnpaid(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimates_all_paid_partiallypaid_unpaid, this, true);
        textViewPaymentStatus = findViewById(R.id.textView_payment_status);
        spinnerPaymentStatus = findViewById(R.id.spinner_payment_status);
    }

    public TextView getTextViewPaymentStatus(){
        return textViewPaymentStatus;
    }

    public TextView getSpinnerPaymentStatus(){
        return spinnerPaymentStatus;
    }

}