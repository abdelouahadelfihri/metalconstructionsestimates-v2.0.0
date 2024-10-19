package com.example.metalconstructionsestimates.customviews.estimates;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;

public class EstimateDoneInIsPaid extends LinearLayout {

    private TextInputEditText editTextDoneIn;
    private CheckBox checkBoxIsEstimatePaid;

    public EstimateDoneInIsPaid(Context context) {
        super(context);
        init();
    }
    public EstimateDoneInIsPaid(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_done_in_is_paid, this, true);
        editTextDoneIn = findViewById(R.id.editText_done_in);
        checkBoxIsEstimatePaid = findViewById(R.id.checkBox_is_estimate_paid);
    }
    public TextInputEditText getTextInputEditTextDoneIn(){
        return editTextDoneIn;
    }
    public CheckBox getCheckBoxIsEstimatePaid(){
        return checkBoxIsEstimatePaid;
    }

}