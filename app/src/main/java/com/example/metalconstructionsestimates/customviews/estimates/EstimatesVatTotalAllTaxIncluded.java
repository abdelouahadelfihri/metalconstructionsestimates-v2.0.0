package com.example.metalconstructionsestimates.customviews.estimates;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.android.material.textfield.TextInputEditText;

import com.example.metalconstructionsestimates.R;

public class EstimatesVatTotalAllTaxIncluded extends LinearLayout {

    private TextInputEditText textInputEditTextVat;
    private TextInputEditText textInputEditTotalAllTaxIncluded;

    public EstimatesVatTotalAllTaxIncluded(Context context) {
        super(context);
        init();
    }
    public EstimatesVatTotalAllTaxIncluded(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimates_vat_total_all_tax_included, this, true);
        textInputEditTextVat = findViewById(R.id.editTextVat);
        textInputEditTotalAllTaxIncluded = findViewById(R.id.editTextTotalAllTaxIncluded);
    }

    public TextInputEditText getTextInputEditTextVat(){
        return textInputEditTextVat;
    }

    public TextInputEditText getTextInputEditTextTotalAllTaxIncluded(){
        return textInputEditTotalAllTaxIncluded;
    }

}