package com.example.metalconstructionsestimates.customviews.estimates;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;


public class EstimateCustomerIdSelectCustomer extends LinearLayout {
    private TextInputEditText textInputEditTextCustomerId;
    private Button buttonSelectCustomer;

    public TextInputEditText getTextInputEditTextCustomerId(){
        return textInputEditTextCustomerId;
    }

    public Button getButtonSelectCustomer(){
        return buttonSelectCustomer;
    }

    public EstimateCustomerIdSelectCustomer(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_customer_id_select_customer, this, true);
        textInputEditTextCustomerId = findViewById(R.id.editText_customer_id);
        buttonSelectCustomer = findViewById(R.id.button_select_customer);
    }
}