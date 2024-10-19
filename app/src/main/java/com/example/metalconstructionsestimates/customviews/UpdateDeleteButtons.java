package com.example.metalconstructionsestimates.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.metalconstructionsestimates.R;

public class UpdateDeleteButtons extends LinearLayout {
    private Button update;
    private Button delete;
    public UpdateDeleteButtons(Context context) {
        super(context);
        init();
    }

    public Button getUpdateButton(){
        return update;
    }

    public Button getDeleteButton(){
        return delete;
    }

    public UpdateDeleteButtons(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.details_screen_update_delete_buttons, this, true);
        delete = findViewById(R.id.button_delete);
        update = findViewById(R.id.button_update);
    }
}