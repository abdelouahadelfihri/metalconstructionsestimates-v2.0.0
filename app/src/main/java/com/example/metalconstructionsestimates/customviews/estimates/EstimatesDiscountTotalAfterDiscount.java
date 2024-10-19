package com.example.metalconstructionsestimates.customviews.estimates;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;

public class EstimatesDiscountTotalAfterDiscount extends LinearLayout {
    private TextInputEditText textInputEditTextDiscount;
    private TextInputEditText textInputEditTextTotalAfterDiscount;

    public EstimatesDiscountTotalAfterDiscount(Context context) {
        super(context);
        init();
    }
    public EstimatesDiscountTotalAfterDiscount(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimates_discount_total_after_discount, this, true);
        textInputEditTextDiscount = findViewById(R.id.editText_discount);
        textInputEditTextTotalAfterDiscount = findViewById(R.id.editText_total_after_discount);
    }

    public TextInputEditText getTextInputEditTextDiscount(){
        return textInputEditTextDiscount;
    }
    public TextInputEditText getTextInputEditTextTotalAfterDiscount(){
        return textInputEditTextTotalAfterDiscount;
    }
}