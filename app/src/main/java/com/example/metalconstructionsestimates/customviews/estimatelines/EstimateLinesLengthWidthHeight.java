package com.example.metalconstructionsestimates.customviews.estimatelines;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;

public class EstimateLinesLengthWidthHeight extends LinearLayout {
    private TextInputEditText textInputEditTextLength;

    private TextInputEditText textInputEditTextWidth;

    private TextInputEditText textInputEditTextHeight;

    public TextInputEditText getTextInputEditTextLength(){
        return textInputEditTextLength;
    }

    public TextInputEditText getTextInputEditTextWidth(){
        return textInputEditTextWidth;
    }

    public TextInputEditText getTextInputEditTextHeight(){
        return textInputEditTextHeight;
    }

    public EstimateLinesLengthWidthHeight(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.estimate_lines_length_width_height, this, true);
        textInputEditTextLength = findViewById(R.id.textInputEditText_estimate_line_length);
        textInputEditTextWidth = findViewById(R.id.textInputEditText_estimate_line_width);
        textInputEditTextHeight = findViewById(R.id.textInputEditText_estimate_line_height);
    }
}