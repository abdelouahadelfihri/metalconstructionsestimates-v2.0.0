package com.example.metalconstructionsestimates.modules.estimates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Calendar;
import java.util.Objects;

public class AddEstimate extends AppCompatActivity {
    Integer customerId;
    DBAdapter dbAdapter;
    TextView expirationDateTextView, issueDateTextView, dueDateTextView;

    String expirationDateValue = "", issueDateValue = "", dueDateValue = "";
    private ActivityResultLauncher<Intent> activityResultLauncher;
    Button addEstimate;
    Button clearAddEstimateForm;
    private DatePickerDialog.OnDateSetListener expirationDateSetListner, issueDateSetListener, dueDateSetListener;
    Intent intent;
    boolean customerExists = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estimate);
        dbAdapter = new DBAdapter(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        Spinner estimateStatusSpinner = (Spinner) findViewById(R.id.estimateStatusSpinner);
        ArrayAdapter<CharSequence> estimateStatusSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.estimate_status, android.R.layout.simple_spinner_item);
        estimateStatusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estimateStatusSpinner.setAdapter(estimateStatusSpinnerAdapter);

        Spinner dueTermsSpinner = (Spinner) findViewById(R.id.estimateStatusSpinner);
        ArrayAdapter<CharSequence> dueTermsSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.due_terms, android.R.layout.simple_spinner_item);
        dueTermsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dueTermsSpinner.setAdapter(dueTermsSpinnerAdapter);

        Button selectCustomer = findViewById(R.id.selectCustomerButton_add_estimate);
        addEstimate = findViewById(R.id.addButton_add_estimate);
        clearAddEstimateForm = findViewById(R.id.clearButton_add_estimate);

        issueDateTextView = findViewById(R.id.issueDateValue);
        dueDateTextView = findViewById(R.id.dueDateValue);
        expirationDateTextView = findViewById(R.id.expirationDateValue);

        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult();
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String customerIdExtraResult;
                        customerIdExtraResult = Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).getString("customerIdExtraResult");
                        customerId = Integer.parseInt(customerIdExtraResult);
                        TextInputEditText customerIdTextInputEditText = findViewById(R.id.customerEditText_add_estimate);
                        String customerName = dbAdapter.getCustomerById(customerId).getName();
                        customerIdTextInputEditText.setText(customerName);
                    }
                }
        );

        addEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText estimateLocationTextInputEditText = findViewById(R.id.locationEditText_add_estimate);
                TextInputEditText customerIdTextInputEditText = findViewById(R.id.customerEditText_add_estimate);
                TextInputEditText estimateDiscountTextInputEditText = findViewById(R.id.discountEditText_add_estimate);
                TextInputEditText vatTextInputEditText = findViewById(R.id.vatEditText_add_estimate);

                if (estimateLocationTextInputEditText.getText().toString().isEmpty() && issueDateValue.isEmpty() && expirationDateValue.isEmpty() && customerIdTextInputEditText.getText().toString().isEmpty() && estimateDiscountTextInputEditText.getText().toString().isEmpty() && vatTextInputEditText.getText().toString().isEmpty()) {
                    Toast emptyFields = Toast.makeText(getApplicationContext(), "Empty Fields.", Toast.LENGTH_LONG);
                    emptyFields.show();
                } else {
                    AlertDialog.Builder alertAdd = new AlertDialog.Builder(AddEstimate.this);
                    alertAdd.setTitle("Add Confirmation");
                    alertAdd.setMessage("Do you really want to add the estimate ?");
                    alertAdd.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbAdapter = new DBAdapter(getApplicationContext());
                            TextInputEditText estimateLocationTextInputEditText = findViewById(R.id.locationEditText_add_estimate);
                            TextInputEditText customerIdTextInputEditText = findViewById(R.id.customerEditText_add_estimate);
                            TextInputEditText estimateDiscountTextInputEditText = findViewById(R.id.discountEditText_add_estimate);
                            TextInputEditText vatTextInputEditText = findViewById(R.id.vatEditText_add_estimate);

                            Estimate estimate = new Estimate();

                            if (customerId != null) {
                                estimate.setCustomer(customerId);
                                customerExists = true;
                            } else {
                                if (customerIdTextInputEditText.getText().toString().isEmpty()) {
                                    estimate.setCustomer(null);
                                } else {
                                    if (allDigitString(customerIdTextInputEditText.getText().toString())) {
                                        Customer customer = dbAdapter.getCustomerById(Integer.parseInt(customerIdTextInputEditText.getText().toString()));
                                        if (customer != null) {
                                            estimate.setCustomer(customer.getId());
                                            customerExists = true;
                                        } else {
                                            customerExists = false;
                                        }
                                    } else {
                                        Integer customer = dbAdapter.getCustomerIdByName(customerIdTextInputEditText.getText().toString());
                                        if (customer != null) {
                                            estimate.setCustomer(customer);
                                            customerExists = true;
                                        } else {
                                            customerExists = false;
                                        }
                                    }
                                }
                            }

                            if (!customerExists) {
                                Toast customerNotExistingToast = Toast.makeText(getApplicationContext(), "Le client saisi ne corresponds à aucun client dans la base de données", Toast.LENGTH_LONG);
                                customerNotExistingToast.show();
                                customerExists = true;
                                return;
                            }

                            if (!issueDateValue.isEmpty()) {
                                estimate.setIssueDate(issueDateValue);
                            } else {
                                estimate.setIssueDate("");
                            }

                            if (!expirationDateValue.isEmpty()){
                                estimate.setExpirationDate(expirationDateValue);
                            } else {
                                estimate.setExpirationDate("");
                            }

                            if (!dueDateValue.isEmpty()){
                                estimate.setDueDate(dueDateValue);
                            } else {
                                estimate.setDueDate("");
                            }

                            if(estimateStatusSpinner.getSelectedItem().toString().isEmpty()){
                                estimate.setStatus("");
                            }
                            else{
                                estimate.setStatus(estimateStatusSpinner.getSelectedItem().toString());
                            }

                            if(dueTermsSpinner.getSelectedItem().toString().isEmpty()){
                                estimate.setDueTerms("");
                            }
                            else{
                                estimate.setDueTerms(dueTermsSpinner.getSelectedItem().toString());
                            }

                            if (estimateLocationTextInputEditText.getText().toString().isEmpty()) {
                                estimate.setDoneIn("");
                            } else {
                                estimate.setDoneIn(estimateLocationTextInputEditText.getText().toString());
                            }

                            if (estimateDiscountTextInputEditText.getText().toString().isEmpty()) {
                                estimate.setDiscount(null);
                            } else {
                                estimate.setDiscount(Float.parseFloat(estimateDiscountTextInputEditText.getText().toString()));
                            }

                            if (vatTextInputEditText.getText().toString().isEmpty()) {
                                estimate.setVat(null);
                            } else {
                                estimate.setVat(Float.parseFloat(vatTextInputEditText.getText().toString()));
                            }

                            dbAdapter.saveEstimate(estimate);
                            Toast saveSuccessToast = Toast.makeText(getApplicationContext(), "Estimate has been successfully added", Toast.LENGTH_LONG);
                            saveSuccessToast.show();
                            intent = new Intent(AddEstimate.this, Estimates.class);
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

        clearAddEstimateForm.setOnClickListener(view -> {
            TextInputEditText estimateLocationTextInputEditText;
            TextInputEditText customerIdTextInputEditText;
            TextInputEditText estimateDiscountTextInputEditText;
            TextInputEditText vatTextInputEditText;

            estimateLocationTextInputEditText = findViewById(R.id.locationEditText_add_estimate);
            customerIdTextInputEditText = findViewById(R.id.customerEditText_add_estimate);
            estimateDiscountTextInputEditText = findViewById(R.id.discountEditText_add_estimate);
            vatTextInputEditText = findViewById(R.id.vatEditText_add_estimate);

            Objects.requireNonNull(estimateLocationTextInputEditText.getText()).clear();
            issueDateTextView.setText(R.string.issueDate);
            expirationDateTextView.setText(R.string.expirationDate);
            dueDateTextView.setText(R.string.dueDate);
            Objects.requireNonNull(customerIdTextInputEditText.getText()).clear();
            Objects.requireNonNull(estimateDiscountTextInputEditText.getText()).clear();
            Objects.requireNonNull(vatTextInputEditText.getText()).clear();
        });

        issueDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    AddEstimate.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    issueDateSetListener,
                    year, month, day
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dueDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    AddEstimate.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dueDateSetListener,
                    year, month, day
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    AddEstimate.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    expirationDateSetListner,
                    year, month, day
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateSetListner = (picker, year, month, day) -> {
            month = month + 1;
            expirationDateValue = year + "-" + month + "-" + day;
            expirationDateTextView.setText(expirationDateValue);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

            Date expirationDate = null;
            try {
                expirationDate = sdf.parse(expirationDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Date issueDate = null;

            if(!issueDateValue.isEmpty()){
                try {
                    issueDate = sdf.parse(issueDateValue);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                long diffInMillis = expirationDate.getTime() - issueDate.getTime();
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);
                if(daysBetween <= 0){
                    Toast.makeText(getApplicationContext(), "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    expirationDateTextView.setText(R.string.expirationDate);
                    expirationDateValue = "";
                }
            }
        };

        issueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            issueDateValue = year + "-" + month + "-" + day;
            issueDateTextView.setText(issueDateValue);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

            Date issueDate = null;

            try {
                issueDate = sdf.parse(issueDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Date expirationDate = null;

            if(!expirationDateValue.isEmpty()){
                try {
                    expirationDate = sdf.parse(expirationDateValue);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                long diffInMillis = expirationDate.getTime() - issueDate.getTime();
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);
                if(daysBetween <= 0){
                    Toast.makeText(getApplicationContext(), "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    issueDateTextView.setText(R.string.issueDate);
                    issueDateValue = "";
                }
            }

        };

        dueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            dueDateValue = year + "-" + month + "-" + day;
            dueDateTextView.setText(dueDateValue);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

            Date dueDate = null;

            try {
                dueDate = sdf.parse(dueDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Date issueDate = null;

            if(!issueDateValue.isEmpty()){
                try {
                    issueDate = sdf.parse(issueDateValue);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                long diffInMillis = Objects.requireNonNull(dueDate).getTime() - Objects.requireNonNull(issueDate).getTime();
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);
                if(daysBetween <= 0){
                    Toast.makeText(getApplicationContext(), "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    issueDateTextView.setText(R.string.issueDate);
                    issueDateValue = "";
                }
            }

        };
    }
    public void startActivityForResult() {
        Intent intent = new Intent(AddEstimate.this, Customers.class);
        activityResultLauncher.launch(intent);

    }
    public boolean allDigitString(String s) {
        boolean result = true;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                result = false;
                break;
            }
        }
        return result;
    }
}