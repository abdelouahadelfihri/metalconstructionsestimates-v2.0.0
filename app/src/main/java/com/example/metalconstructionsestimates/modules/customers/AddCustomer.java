
package com.example.metalconstructionsestimates.modules.customers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;

public class AddCustomer extends AppCompatActivity {
    Customer customer;
    DBAdapter adapter;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        adapter = new DBAdapter(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button add_customer_button = (Button) findViewById(R.id.btnAdd_add_customer);
        Button clear_button = (Button) findViewById(R.id.btnClear_add_customer);

        clear_button.setOnClickListener(view -> {
            TextInputEditText customerFaxTextInputEditText;
            TextInputEditText customerEmailTextInputEditText;
            TextInputEditText customerMobileTextInputEditText;
            TextInputEditText customerAddressTextInputEditText;
            TextInputEditText customerPhoneTextInputEditText;
            TextInputEditText customerNameTextInputEditText;
            customerNameTextInputEditText = findViewById(R.id.etName_add_customer);
            customerEmailTextInputEditText = findViewById(R.id.etEmail_add_customer);
            customerPhoneTextInputEditText = findViewById(R.id.etPhone_add_customer);
            customerMobileTextInputEditText = findViewById(R.id.etMobile_add_customer);
            customerFaxTextInputEditText = findViewById(R.id.etFax_add_customer);
            customerAddressTextInputEditText = findViewById(R.id.etAddress_add_customer);
            customerNameTextInputEditText.getText().clear();
            customerEmailTextInputEditText.getText().clear();
            customerPhoneTextInputEditText.getText().clear();
            customerMobileTextInputEditText.getText().clear();
            customerFaxTextInputEditText.getText().clear();
            customerAddressTextInputEditText.getText().clear();
        });

        add_customer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputEditText customerNameTextInputEditText = findViewById(R.id.etName_add_customer);
                TextInputEditText customerEmailTextInputEditText = findViewById(R.id.etEmail_add_customer);
                TextInputEditText customerPhoneTextInputEditText = findViewById(R.id.etPhone_add_customer);
                TextInputEditText customerMobileTextInputEditText = findViewById(R.id.etMobile_add_customer);
                TextInputEditText customerFaxTextInputEditText = findViewById(R.id.etFax_add_customer);
                TextInputEditText customerAddressTextInputEditText = findViewById(R.id.etAddress_add_customer);

                if (customerNameTextInputEditText.getText().toString().isEmpty() && customerEmailTextInputEditText.getText().toString().isEmpty() &&
                        customerPhoneTextInputEditText.getText().toString().isEmpty() && customerMobileTextInputEditText.getText().toString().isEmpty() &&
                        customerFaxTextInputEditText.getText().toString().isEmpty() && customerAddressTextInputEditText.getText().toString().isEmpty()) {
                    Toast emptyFields = Toast.makeText(getApplicationContext(), "Empty Fields !", Toast.LENGTH_LONG);
                    emptyFields.show();
                } else {
                    AlertDialog.Builder alertAdd = new AlertDialog.Builder(AddCustomer.this);
                    alertAdd.setTitle("Add Confirmation");
                    alertAdd.setMessage("Do you really want to add the new customer ?");
                    alertAdd.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            TextInputEditText customerNameTextInputEditText = findViewById(R.id.etName_add_customer);
                            TextInputEditText customerEmailTextInputEditText = findViewById(R.id.etEmail_add_customer);
                            TextInputEditText customerPhoneTextInputEditText = findViewById(R.id.etPhone_add_customer);
                            TextInputEditText customerMobileTextInputEditText = findViewById(R.id.etMobile_add_customer);
                            TextInputEditText customerFaxTextInputEditText = findViewById(R.id.etFax_add_customer);
                            TextInputEditText customerAddressTextInputEditText = findViewById(R.id.etAddress_add_customer);
                            customer = new Customer();

                            if (customerNameTextInputEditText.getText().toString().isEmpty()) {
                                customer.setName("");
                            } else {
                                customer.setName(customerNameTextInputEditText.getText().toString());
                            }

                            if (customerEmailTextInputEditText.getText().toString().isEmpty()) {
                                customer.setEmail("");
                            } else {
                                customer.setEmail(customerEmailTextInputEditText.getText().toString());
                            }

                            if (customerPhoneTextInputEditText.getText().toString().isEmpty()) {
                                customer.setTelephone("");
                            } else {
                                customer.setTelephone(customerPhoneTextInputEditText.getText().toString());
                            }

                            if (customerMobileTextInputEditText.getText().toString().isEmpty()) {
                                customer.setMobile("");
                            } else {
                                customer.setMobile(customerMobileTextInputEditText.getText().toString());
                            }

                            if (customerFaxTextInputEditText.getText().toString().isEmpty()) {
                                customer.setFax("");
                            } else {
                                customer.setFax(customerFaxTextInputEditText.getText().toString());
                            }

                            if (customerAddressTextInputEditText.getText().toString().isEmpty()) {
                                customer.setAddress("");
                            } else {
                                customer.setAddress(customerAddressTextInputEditText.getText().toString());
                            }

                            adapter.saveCustomer(customer);
                            Toast addsuccess = Toast.makeText(getApplicationContext(), "Customer has been successfully added", Toast.LENGTH_LONG);
                            addsuccess.show();
                            intent = new Intent(AddCustomer.this, Customers.class);
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
}