package com.example.metalconstructionsestimates.customviews.estimatelines;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.LinearLayout;


import com.example.metalconstructionsestimates.R;

public class EstimateLinesSteelIdSelectSteel extends LinearLayout {
    private TextInputEditText editTextSteelId;
    private Button selectSteelButton;

    public TextInputEditText getTextInputEditTextSteelId(){
        return editTextSteelId;
    }

    public Button getSelectSteelButton(){
        return selectSteelButton;
    }

    public EstimateLinesSteelIdSelectSteel(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_lines_steel_id_select_steel, this, true);
        editTextSteelId = findViewById(R.id.editText_steel_id_textInputEditText);
        selectSteelButton = findViewById(R.id.button_select_steel);
    }
}