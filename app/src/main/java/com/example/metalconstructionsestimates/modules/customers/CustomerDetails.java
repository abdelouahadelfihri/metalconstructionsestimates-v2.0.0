package com.example.metalconstructionsestimates.modules.customers;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.metalconstructionsestimates.models.Estimate;
import com.google.android.material.textfield.TextInputEditText;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CustomerDetails extends AppCompatActivity {
    Customer customer;
    DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String customerIdExtra = getIntent().getStringExtra("customerIdExtra");
        assert customerIdExtra != null;
        Integer customerId = Integer.parseInt(customerIdExtra);
        dbAdapter = new DBAdapter(getApplicationContext());
        customer = dbAdapter.getCustomerById(customerId);

        AtomicReference<TextInputEditText> customerIdTextInputEditText = new AtomicReference<>(findViewById(R.id.customerIdEditText_customer_details));
        TextInputEditText customerNameTextInputEditText = findViewById(R.id.nameEditText_customer_details);
        TextInputEditText customerEmailTextInputEditText = findViewById(R.id.emailEditText_customer_details);
        TextInputEditText customerPhoneTextInputEditText = findViewById(R.id.telephoneEditText_customer_details);
        TextInputEditText customerMobileTextInputEditText = findViewById(R.id.mobileEditText_customer_details);
        TextInputEditText customerFaxTextInputEditText = findViewById(R.id.faxEditText_customer_details);
        TextInputEditText customerAddressTextInputEditText = findViewById(R.id.addressEditText_customer_details);

        customerIdTextInputEditText.get().setText(customerId.toString());
        customerNameTextInputEditText.setText(customer.getName());
        customerEmailTextInputEditText.setText(customer.getEmail());
        customerPhoneTextInputEditText.setText(customer.getTelephone());
        customerMobileTextInputEditText.setText(customer.getMobile());
        customerFaxTextInputEditText.setText(customer.getFax());
        customerAddressTextInputEditText.setText(customer.getAddress());

        Button deleteCustomer = findViewById(R.id.buttonDelete_customer_details);
        Button updateCustomer = findViewById(R.id.buttonEdit_customer_details);


        deleteCustomer.setOnClickListener(view -> {
            AlertDialog.Builder alertDelete = new AlertDialog.Builder(CustomerDetails.this);
            alertDelete.setTitle("Delete Confirmation");
            alertDelete.setMessage("Do you really want to delete the customer ?");
            alertDelete.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                // continue with delete
                customer = new Customer();
                customerIdTextInputEditText.set(findViewById(R.id.customerIdEditText_customer_details));
                dbAdapter.deleteCustomer(Integer.parseInt(customerIdTextInputEditText.get().getText().toString()));
                Toast deletesuccess = Toast.makeText(getApplicationContext(), "Customer has been successfully deleted", Toast.LENGTH_LONG);
                deletesuccess.show();
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

        updateCustomer.setOnClickListener(view -> {
            AlertDialog.Builder alertUpdate = new AlertDialog.Builder(CustomerDetails.this);
            alertUpdate.setTitle("Confirm Update");
            alertUpdate.setMessage("Do you really want to update customer ?");
            alertUpdate.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                // continue with delete
                customer = new Customer();

                if (!Objects.requireNonNull(customerIdTextInputEditText.get().getText()).toString().isEmpty()) {
                    customer.setId(Integer.parseInt(customerIdTextInputEditText.get().getText().toString()));
                } else {
                    customer.setId(Integer.valueOf(""));
                }

                if (!Objects.requireNonNull(customerNameTextInputEditText.getText()).toString().isEmpty()) {
                    customer.setName(customerNameTextInputEditText.getText().toString());
                } else {
                    customer.setName("");
                }

                if (!Objects.requireNonNull(customerEmailTextInputEditText.getText()).toString().isEmpty()) {
                    customer.setEmail(customerEmailTextInputEditText.getText().toString());
                } else {
                    customer.setEmail("");
                }

                if (!Objects.requireNonNull(customerPhoneTextInputEditText.getText()).toString().isEmpty()) {
                    customer.setTelephone(customerPhoneTextInputEditText.getText().toString());
                } else {
                    customer.setTelephone("");
                }

                if (!Objects.requireNonNull(customerMobileTextInputEditText.getText()).toString().isEmpty()) {
                    customer.setMobile(customerMobileTextInputEditText.getText().toString());
                } else {
                    customer.setMobile("");
                }

                if (!Objects.requireNonNull(customerFaxTextInputEditText.getText()).toString().isEmpty()) {
                    customer.setFax(customerFaxTextInputEditText.getText().toString());
                } else {
                    customer.setFax("");
                }

                if (!Objects.requireNonNull(customerAddressTextInputEditText.getText()).toString().isEmpty()) {
                    customer.setAddress(customerAddressTextInputEditText.getText().toString());
                } else {
                    customer.setAddress("");
                }

                dbAdapter.updateCustomer(customer);

                Toast updateSuccessToast = Toast.makeText(getApplicationContext(), "Customer has been successfully updated", Toast.LENGTH_LONG);
                updateSuccessToast.show();
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