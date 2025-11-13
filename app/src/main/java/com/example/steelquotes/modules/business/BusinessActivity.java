
package com.example.steelquotes.modules.business;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.steelquotes.MainActivity;
import com.example.steelquotes.R;
import com.example.steelquotes.database.DBAdapter;
import com.example.steelquotes.models.Business;
import com.google.android.material.textfield.TextInputEditText;

public class BusinessActivity extends AppCompatActivity {
    Business business;
    DBAdapter dbAdapter;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);
        dbAdapter = new DBAdapter(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button add_business_button = (Button) findViewById(R.id.btnAdd_add_business);
        Button clear_button = (Button) findViewById(R.id.btnClear_add_business);

        clear_button.setOnClickListener(view -> {
            TextInputEditText businessFaxTextInputEditText;
            TextInputEditText businessEmailTextInputEditText;
            TextInputEditText businessMobileTextInputEditText;
            TextInputEditText businessAddressTextInputEditText;
            TextInputEditText businessPhoneTextInputEditText;
            TextInputEditText businessNameTextInputEditText;
            businessNameTextInputEditText = findViewById(R.id.etName_add_business);
            businessEmailTextInputEditText = findViewById(R.id.etEmail_add_business);
            businessPhoneTextInputEditText = findViewById(R.id.etPhone_add_business);
            businessMobileTextInputEditText = findViewById(R.id.etMobile_add_business);
            businessFaxTextInputEditText = findViewById(R.id.etFax_add_business);
            businessAddressTextInputEditText = findViewById(R.id.etAddress_add_business);
            businessNameTextInputEditText.getText().clear();
            businessEmailTextInputEditText.getText().clear();
            businessPhoneTextInputEditText.getText().clear();
            businessMobileTextInputEditText.getText().clear();
            businessFaxTextInputEditText.getText().clear();
            businessAddressTextInputEditText.getText().clear();
        });

        add_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputEditText businessNameTextInputEditText = findViewById(R.id.etName_add_business);
                TextInputEditText businessEmailTextInputEditText = findViewById(R.id.etEmail_add_business);
                TextInputEditText businessPhoneTextInputEditText = findViewById(R.id.etPhone_add_business);
                TextInputEditText businessMobileTextInputEditText = findViewById(R.id.etMobile_add_business);
                TextInputEditText businessFaxTextInputEditText = findViewById(R.id.etFax_add_business);
                TextInputEditText businessAddressTextInputEditText = findViewById(R.id.etAddress_add_business);

                if (businessNameTextInputEditText.getText().toString().isEmpty() && businessEmailTextInputEditText.getText().toString().isEmpty() &&
                        businessPhoneTextInputEditText.getText().toString().isEmpty() && businessMobileTextInputEditText.getText().toString().isEmpty() &&
                        businessFaxTextInputEditText.getText().toString().isEmpty() && businessAddressTextInputEditText.getText().toString().isEmpty()) {
                    Toast emptyFields = Toast.makeText(getApplicationContext(), "Empty Fields !", Toast.LENGTH_LONG);
                    emptyFields.show();
                } else {
                    AlertDialog.Builder alertAdd = new AlertDialog.Builder(BusinessActivity.this);
                    alertAdd.setTitle("Confirm Add");
                    alertAdd.setMessage("Do you really want to add the new business ?");
                    alertAdd.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            TextInputEditText businessNameTextInputEditText = findViewById(R.id.etName_add_business);
                            TextInputEditText businessEmailTextInputEditText = findViewById(R.id.etEmail_add_business);
                            TextInputEditText businessPhoneTextInputEditText = findViewById(R.id.etPhone_add_business);
                            TextInputEditText businessMobileTextInputEditText = findViewById(R.id.etMobile_add_business);
                            TextInputEditText businessFaxTextInputEditText = findViewById(R.id.etFax_add_business);
                            TextInputEditText businessAddressTextInputEditText = findViewById(R.id.etAddress_add_business);
                            business = new Business();

                            if (businessNameTextInputEditText.getText().toString().isEmpty()) {
                                business.setName("");
                            } else {
                                business.setName(businessNameTextInputEditText.getText().toString());
                            }

                            if (businessEmailTextInputEditText.getText().toString().isEmpty()) {
                                business.setEmail("");
                            } else {
                                business.setEmail(businessEmailTextInputEditText.getText().toString());
                            }

                            if (businessPhoneTextInputEditText.getText().toString().isEmpty()) {
                                business.setPhone("");
                            } else {
                                business.setPhone(businessPhoneTextInputEditText.getText().toString());
                            }

                            if (businessMobileTextInputEditText.getText().toString().isEmpty()) {
                                business.setMobile("");
                            } else {
                                business.setMobile(businessMobileTextInputEditText.getText().toString());
                            }

                            if (businessFaxTextInputEditText.getText().toString().isEmpty()) {
                                business.setFax("");
                            } else {
                                business.setFax(businessFaxTextInputEditText.getText().toString());
                            }

                            if (businessAddressTextInputEditText.getText().toString().isEmpty()) {
                                business.setAddress("");
                            } else {
                                business.setAddress(businessAddressTextInputEditText.getText().toString());
                            }

                            dbAdapter.saveBusiness(business);
                            Toast addSuccessToast = Toast.makeText(getApplicationContext(), "Business has been successfully added", Toast.LENGTH_LONG);
                            addSuccessToast.show();
                            intent = new Intent(BusinessActivity.this, MainActivity.class);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close DBAdapter to release database resources
        if (dbAdapter != null) {
            dbAdapter.close();
        }
    }
}