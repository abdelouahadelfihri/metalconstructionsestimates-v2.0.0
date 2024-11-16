package com.example.metalconstructionsestimates.customviews.estimates;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;

public class EstimateLocationAmountPaid extends LinearLayout {

    private TextInputEditText editTextLocation;
    private TextInputEditText editTextAmountPaid;

    public EstimateLocationAmountPaid(Context context) {
        super(context);
        init();
    }
    public EstimateLocationAmountPaid(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_location_amount_paid, this, true);
        editTextLocation = findViewById(R.id.editText_location);
        editTextAmountPaid = findViewById(R.id.editText_amount_paid);
    }
    public TextInputEditText getTextInputEditTextLocation(){
        return editTextLocation;
    }
    public TextInputEditText getTextInputEditTextAmountPaid(){
        return editTextAmountPaid;
    }

}