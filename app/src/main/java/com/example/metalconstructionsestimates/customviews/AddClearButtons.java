package com.example.metalconstructionsestimates.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;

public class AddClearButtons extends LinearLayout {
    private Button add;
    private Button clear;
    public AddClearButtons(Context context) {
        super(context);
        init();
    }

    public Button getAddButton(){
        return add;
    }

    public Button getClearButton(){
        return clear;
    }

    public AddClearButtons(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.add_screen_add_clear_buttons, this, true);
        add = findViewById(R.id.button_add);
        clear = findViewById(R.id.button_clear);
    }
}