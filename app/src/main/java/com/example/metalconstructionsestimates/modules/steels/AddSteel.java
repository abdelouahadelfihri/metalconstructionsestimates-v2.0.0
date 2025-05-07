package com.example.metalconstructionsestimates.modules.steels;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.metalconstructionsestimates.customviews.AddClearButtons;

public class AddSteel extends AppCompatActivity {
    Steel steel;
    DBAdapter adapter;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_steel);
        adapter = new DBAdapter(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Spinner steelGeometricShapeSpinner = (Spinner) findViewById(R.id.spinner_geometric_shape);
        ArrayAdapter<CharSequence> steelGeometricShapeAdapter = ArrayAdapter.createFromResource(this, R.array.geometric_shapes, android.R.layout.simple_spinner_item);
        steelGeometricShapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        steelGeometricShapeSpinner.setAdapter(steelGeometricShapeAdapter);
        steelGeometricShapeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                Spinner steelUnitSpinner = (Spinner) findViewById(R.id.spinner_steel_unit_add_steel);
                switch(item.toString()){
                    case "Profile":
                        steelUnitSpinner.setSelection(1);
                        break;
                    case "Surface":
                        steelUnitSpinner.setSelection(2);
                        break;
                    case "Volume":
                        steelUnitSpinner.setSelection(3);
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        AddClearButtons addClearButtons = (AddClearButtons) findViewById(R.id.add_clear_buttons_add_steel);
        Button addSteel = addClearButtons.getAddButton();
        Button clearAddSteelForm = addClearButtons.getClearButton();

        addSteel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText steelTypeTextInputEditText = (TextInputEditText) findViewById(R.id.textInputEditText_steel_type_add_steel);
                Spinner steelGeometricShapeSpinner = (Spinner) findViewById(R.id.spinner_steel_geometric_shape_add_steel);
                TextInputEditText weightTextInputEditText = (TextInputEditText) findViewById(R.id.textInputEditText_steel_weight_add_steel);
                Spinner unitSpinner = (Spinner) findViewById(R.id.spinner_steel_unit_add_steel);
                if (steelGeometricShapeSpinner.getSelectedItem().toString().isEmpty() && weightTextInputEditText.getText().toString().isEmpty() && steelTypeTextInputEditText.getText().toString().isEmpty() && unitSpinner.getSelectedItem().toString().isEmpty()) {
                    Toast emptyFields = Toast.makeText(getApplicationContext(), "Champs vides.", Toast.LENGTH_LONG);
                    emptyFields.show();
                } else {
                    AlertDialog.Builder alertAdd = new AlertDialog.Builder(AddSteel.this);
                    alertAdd.setTitle("Confirmation d'ajout");
                    alertAdd.setMessage("Voulez-vous vraiment ajouter l\'acier?");
                    alertAdd.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            TextInputEditText steelTypeTextInputEditText = (TextInputEditText) findViewById(R.id.textInputEditText_steel_type_add_steel);
                            Spinner steelGeometricShapeSpinner = (Spinner) findViewById(R.id.spinner_steel_geometric_shape_add_steel);
                            TextInputEditText weightTextInputEditText = (TextInputEditText) findViewById(R.id.textInputEditText_steel_weight_add_steel);
                            Spinner unitSpinner = (Spinner) findViewById(R.id.spinner_steel_unit_add_steel);
                            steel = new Steel();

                            if (steelTypeTextInputEditText.getText().toString().isEmpty()) {
                                steel.setType("");
                            } else {
                                steel.setType(steelTypeTextInputEditText.getText().toString());
                            }

                            if (steelGeometricShapeSpinner.getSelectedItem().toString().isEmpty()) {
                                steel.setGeometricShape("");
                            } else {
                                steel.setGeometricShape(steelGeometricShapeSpinner.getSelectedItem().toString());
                            }

                            if (weightTextInputEditText.getText().toString().isEmpty()) {
                                steel.setWeight(null);
                            } else {
                                steel.setWeight(Float.parseFloat(weightTextInputEditText.getText().toString()));
                            }

                            if (unitSpinner.getSelectedItem().toString().isEmpty()) {
                                steel.setUnit("");
                            } else {
                                steel.setUnit(unitSpinner.getSelectedItem().toString());
                            }

                            adapter.saveSteel(steel);
                            Toast addSuccessToast = Toast.makeText(getApplicationContext(), "L\'ajout de l\'acier a été effectué avec succés", Toast.LENGTH_LONG);
                            addSuccessToast.show();
                            intent = new Intent(AddSteel.this, Steels.class);
                            startActivity(intent);
                        }
                    });
                    alertAdd.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // close dialog
                            dialog.cancel();
                        }
                    });
                    alertAdd.show();
                }
            }
        });

        clearAddSteelForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner unitSpinner;
                TextInputEditText weightTextInputEditText;
                Spinner steelGeometricShapeSpinner;
                steelGeometricShapeSpinner = (Spinner) findViewById(R.id.spinner_steel_geometric_shape_add_steel);
                weightTextInputEditText = (TextInputEditText) findViewById(R.id.textInputEditText_steel_weight_add_steel);
                unitSpinner = (Spinner) findViewById(R.id.spinner_steel_unit_add_steel);
                steelGeometricShapeSpinner.setSelection(0);
                weightTextInputEditText.getText().clear();
                unitSpinner.setSelection(0);
            }
        });

        Spinner steelUnitSpinner = findViewById(R.id.spinner_steel_unit_add_steel);
        ArrayAdapter<CharSequence> steelUnitAdapter = ArrayAdapter.createFromResource(this, R.array.units, android.R.layout.simple_spinner_item);
        steelUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        steelUnitSpinner.setAdapter(steelUnitAdapter);

        steelUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                Spinner steelGeometricShapeSpinner = (Spinner) findViewById(R.id.spinner_steel_geometric_shape_add_steel);
                switch(item.toString()){
                    case "Meter":
                        steelGeometricShapeSpinner.setSelection(1);
                        break;
                    case "Square Meter":
                        steelGeometricShapeSpinner.setSelection(2);
                        break;
                    case "Cubic Meter":
                        steelGeometricShapeSpinner.setSelection(3);
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}