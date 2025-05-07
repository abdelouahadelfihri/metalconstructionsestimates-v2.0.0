package com.example.metalconstructionsestimates.modules.steels;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.customviews.UpdateDeleteButtons;

public class SteelDetails extends AppCompatActivity {
    Steel steel;
    DBAdapter dbAdapter;
    Intent intent;

    UpdateDeleteButtons updateDeleteButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steel_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String steelIdExtra = getIntent().getStringExtra("steelIdExtra");
        Integer steelId = Integer.parseInt(steelIdExtra);
        dbAdapter = new DBAdapter(getApplicationContext());
        steel = dbAdapter.getSteelById(steelId);
        TextInputEditText steelIdTextInputEditText = findViewById(R.id.steelIdEditText);
        TextInputEditText steelTypeTextInputEditText = findViewById(R.id.steelTypeEditText);
        Spinner steelGeometricShapeSpinner = findViewById(R.id.shapeSpinner);
        ArrayAdapter<CharSequence> steelGeometricShapeSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.geometric_shapes,android.R.layout.simple_spinner_item);
        steelGeometricShapeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        steelGeometricShapeSpinner.setAdapter(steelGeometricShapeSpinnerAdapter);
        TextInputEditText steelWeightTextInputEditText = findViewById(R.id.weightEditText);
        Spinner steelUnitSpinner = findViewById(R.id.unitSpinner);
        ArrayAdapter<CharSequence> steelUnitSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.geometric_shapes,android.R.layout.simple_spinner_item);
        steelUnitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        steelUnitSpinner.setAdapter(steelUnitSpinnerAdapter);
        steelIdTextInputEditText.setText(steelId.toString());
        steelTypeTextInputEditText.setText(steel.getType());

        switch(steel.getGeometricShape()){
            case "Profile":
                steelGeometricShapeSpinner.setSelection(1);
                break;
            case "Surface":
                steelGeometricShapeSpinner.setSelection(2);
                break;
            case "Volume":
                steelGeometricShapeSpinner.setSelection(3);
                break;
        }

        Float weight = steel.getWeight();

        if(steel.getWeight() == null){
            steelWeightTextInputEditText.setText("");
        }
        else{
            steelWeightTextInputEditText.setText(weight.toString());
        }

        if(steel.getUnit().isEmpty()){
            steelUnitSpinner.setSelection(0);
        }
        else{
            switch(steel.getUnit()){
                case "Meter":
                    steelUnitSpinner.setSelection(1);
                case "Square Meter":
                    steelUnitSpinner.setSelection(2);
                case "Cubic Meter":
                    steelUnitSpinner.setSelection(3);

            }
        }

        Button updateButton = findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertUpdate = new AlertDialog.Builder(SteelDetails.this);
                alertUpdate.setTitle("Confirmation de modification");
                alertUpdate.setMessage("Voulez-vous vraiment modifier l\'acier?");
                alertUpdate.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        steel = new Steel();
                        TextInputEditText steelIdTextInputEditText = (TextInputEditText) findViewById(R.id.steelIdEditText);
                        TextInputEditText steelTypeTextInputEditText = (TextInputEditText) findViewById(R.id.steelTypeEditText);
                        Spinner steelGeometricShapeSpinner = findViewById(R.id.shapeSpinner);
                        TextInputEditText weightTextInputEditText = findViewById(R.id.weightEditText);
                        Spinner unitSpinner = findViewById(R.id.unitSpinner);
                        steel.setId(Integer.parseInt(steelIdTextInputEditText.getText().toString()));

                        if (!steelTypeTextInputEditText.getText().toString().isEmpty()) {
                            steel.setType(steelTypeTextInputEditText.getText().toString());
                        } else {
                            steel.setType("");
                        }

                        if (!steelGeometricShapeSpinner.getSelectedItem().toString().isEmpty()) {
                            steel.setGeometricShape(steelGeometricShapeSpinner.getSelectedItem().toString());
                        } else {
                            steel.setGeometricShape("");
                        }

                        if (!weightTextInputEditText.getText().toString().isEmpty()) {
                            steel.setWeight(Float.parseFloat(weightTextInputEditText.getText().toString()));
                        } else {
                            steel.setWeight(null);
                        }

                        if (!steelUnitSpinner.getSelectedItem().toString().isEmpty()) {
                            steel.setUnit(steelUnitSpinner.getSelectedItem().toString());
                        } else {
                            steel.setUnit("");
                        }

                        dbAdapter.updateSteel(steel);
                        Toast updateSuccessToast = Toast.makeText(getApplicationContext(), "La modification de l\'acier a été effectué avec succés", Toast.LENGTH_LONG);
                        updateSuccessToast.show();
                        intent = new Intent(SteelDetails.this, Steels.class);
                        startActivity(intent);
                    }
                });
                alertUpdate.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alertUpdate.show();
            }
        });

        Button deleteSteel = updateDeleteButtons.getDeleteButton();
        deleteSteel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(SteelDetails.this);
                alertDelete.setTitle("Confirmation de suppression");
                alertDelete.setMessage("Voulez-vous vraiment supprimer la pièce métallique?");
                alertDelete.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        steel = new Steel();
                        TextInputEditText steelIdTextInputEditText = (TextInputEditText) findViewById(R.id.steelIdEditText);
                        dbAdapter.deleteSteel(Integer.parseInt(steelIdTextInputEditText.getText().toString()));
                        Toast deleteSuccessToast = Toast.makeText(getApplicationContext(), "La suppression de l\'acier a été effectuée avec succés", Toast.LENGTH_LONG);
                        deleteSuccessToast.show();

                        if(dbAdapter.retrieveSteels().isEmpty()){
                            dbAdapter.setSeqSteels();
                        }

                        intent = new Intent(SteelDetails.this, Steels.class);
                        startActivity(intent);
                    }
                });
                alertDelete.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alertDelete.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();  // or use NavUtils.navigateUpFromSameTask(this);
        return true;
    }
}