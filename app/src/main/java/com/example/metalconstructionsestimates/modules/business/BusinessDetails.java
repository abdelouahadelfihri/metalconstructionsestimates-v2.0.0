package com.example.metalconstructionsestimates.modules.business;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.MainActivity;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Business;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.modules.estimates.EstimateDetails;
import com.example.metalconstructionsestimates.modules.estimates.Estimates;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class BusinessDetails extends AppCompatActivity {
    Business business;
    DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DBAdapter(getApplicationContext());
        business = dbAdapter.getBusiness();

        TextInputEditText businessNameTextInputEditText = findViewById(R.id.nameEditText_business_details);
        TextInputEditText businessEmailTextInputEditText = findViewById(R.id.emailEditText_business_details);
        TextInputEditText businessPhoneTextInputEditText = findViewById(R.id.telephoneEditText_business_details);
        TextInputEditText businessMobileTextInputEditText = findViewById(R.id.mobileEditText_business_details);
        TextInputEditText businessFaxTextInputEditText = findViewById(R.id.faxEditText_business_details);
        TextInputEditText businessAddressTextInputEditText = findViewById(R.id.addressEditText_business_details);


        businessNameTextInputEditText.setText(business.getName());
        businessEmailTextInputEditText.setText(business.getEmail());
        businessPhoneTextInputEditText.setText(business.getPhone());
        businessMobileTextInputEditText.setText(business.getMobile());
        businessFaxTextInputEditText.setText(business.getFax());
        businessAddressTextInputEditText.setText(business.getAddress());

        Button deleteBusiness = findViewById(R.id.buttonDelete_business_details);
        Button updateBusiness = findViewById(R.id.buttonEdit_business_details);


        deleteBusiness.setOnClickListener(view -> {
            AlertDialog.Builder alertDelete = new AlertDialog.Builder(BusinessDetails.this);
            alertDelete.setTitle("Delete Confirmation");
            alertDelete.setMessage("Do you really want to delete the business ?");
            alertDelete.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                // continue with delete
                dbAdapter.deleteBusiness();

                Toast.makeText(getApplicationContext(),
                        "Company profile has been successfully deleted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(BusinessDetails.this, MainActivity.class));

                if(dbAdapter.retrieveCustomers().isEmpty()){
                    dbAdapter.setSeqCustomers();
                }

            });
            alertDelete.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // close dialog
                    dialog.cancel();
                }
            });
            alertDelete.show();
        });

        updateBusiness.setOnClickListener(view -> {
            AlertDialog.Builder alertUpdate = new AlertDialog.Builder(BusinessDetails.this);
            alertUpdate.setTitle("Confirm Update");
            alertUpdate.setMessage("Do you really want to update business ?");
            alertUpdate.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                // continue with delete
                business = new Business();

                if (!businessNameTextInputEditText.getText().toString().isEmpty()) {
                    business.setName(businessNameTextInputEditText.getText().toString());
                } else {
                    business.setName("");
                }

                if (!businessEmailTextInputEditText.getText().toString().isEmpty()) {
                    business.setEmail(businessEmailTextInputEditText.getText().toString());
                } else {
                    business.setEmail("");
                }

                if (!businessPhoneTextInputEditText.getText().toString().isEmpty()) {
                    business.setPhone(businessPhoneTextInputEditText.getText().toString());
                } else {
                    business.setPhone("");
                }

                if (!businessMobileTextInputEditText.getText().toString().isEmpty()) {
                    business.setMobile(businessMobileTextInputEditText.getText().toString());
                } else {
                    business.setMobile("");
                }

                if (!businessFaxTextInputEditText.getText().toString().isEmpty()) {
                    business.setFax(businessFaxTextInputEditText.getText().toString());
                } else {
                    business.setFax("");
                }

                if (!businessAddressTextInputEditText.getText().toString().isEmpty()) {
                    business.setAddress(businessAddressTextInputEditText.getText().toString());
                } else {
                    business.setAddress("");
                }

                dbAdapter.updateBusiness(business);
                Toast.makeText(getApplicationContext(),
                        "The company profile has been successfully updated", Toast.LENGTH_LONG).show();
                startActivity(new Intent(BusinessDetails.this, MainActivity.class));
            });
            alertUpdate.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // close dialog
                    dialog.cancel();
                }
            });
            alertUpdate.show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();  // or use NavUtils.navigateUpFromSameTask(this);
        return true;
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