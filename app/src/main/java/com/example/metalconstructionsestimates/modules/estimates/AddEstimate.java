package com.example.metalconstructionsestimates.modules.estimates;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AddEstimate extends AppCompatActivity {
    Integer customerId;
    DBAdapter dbAdapter;
    TextView expirationDateTextView, issueDateTextView, dueDateTextView;

    private String statusValue = "Select status";
    private String dueTermsValue = "Select due terms";

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

        Estimate estimate = new Estimate();

        Spinner estimateStatusSpinner = (Spinner) findViewById(R.id.estimateStatusSpinner);
        ArrayAdapter<CharSequence> estimateStatusSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.estimate_status, android.R.layout.simple_spinner_item);
        estimateStatusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estimateStatusSpinner.setAdapter(estimateStatusSpinnerAdapter);

        Spinner dueTermsSpinner = findViewById(R.id.dueTermsSpinner);

        String[] termsArray = getResources().getStringArray(R.array.due_terms);
        List<String> termsList = new ArrayList<>(Arrays.asList(termsArray));

        ArrayAdapter<String> dueTermsSpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, termsList
        );

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

        estimateStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusValue = parent.getItemAtPosition(position).toString();
                estimate.setStatus(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                statusValue = "Pending";
                estimate.setStatus(statusValue); // default if nothing selected
            }
        });

        dueTermsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dueTermsValue = parent.getItemAtPosition(position).toString();
                estimate.setDueTerms(parent.getItemAtPosition(position).toString());

                // Get issue date from your TextView
                String issueDateStr = issueDateTextView.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                Date issueDate = null;

                try {
                    if(!issueDateStr.equals("--/--/----")){
                        issueDate = sdf.parse(issueDateStr);
                    }
                } catch (ParseException e) {
                    Log.e("AddEstimateActivity", "Error parsing issue date", e);
                }

                Calendar cal = Calendar.getInstance();
                if (issueDate == null) {
                    return;
                }
                cal.setTime(issueDate);

                if (dueTermsValue.equals("Due on receipt")) {
                    // Same as issue date
                    if (!issueDateStr.isEmpty()) {
                        dueDateValue = issueDateStr;
                        estimate.setDueDate(issueDateStr);
                    }
                }
                else if (dueTermsValue.equals("Next day")) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    dueDateValue = sdf.format(cal.getTime());
                    estimate.setDueDate(dueDateValue);

                } else if (dueTermsValue.equals("Custom")) {
                    // Open DatePicker for custom selection
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            AddEstimate.this,
                            (view1, year, month, dayOfMonth) -> {
                                Calendar customCal = Calendar.getInstance();
                                customCal.set(year, month, dayOfMonth);
                                dueDateValue = sdf.format(customCal.getTime());
                                estimate.setDueDate(dueDateValue);
                                dueDateTextView.setText(dueDateValue);
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();

                } else if (!dueTermsValue.equals("Select due terms")) {
                    // For "2 days", "3 days", ..., "180 days"
                    String daysStr = dueTermsValue.replace(" days", "").trim();
                    int days = Integer.parseInt(daysStr);
                    cal.add(Calendar.DAY_OF_MONTH, days);
                    dueDateValue = sdf.format(cal.getTime());
                    estimate.setDueDate(dueDateValue);
                }

                // Update your UI if due date was set
                if (!dueDateValue.isEmpty()) {
                    dueDateTextView.setText(dueDateValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dueTermsValue = "Select due terms";
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String customerIdExtraResult;
                        customerIdExtraResult = Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).getString("customerIdExtraResult");
                        assert customerIdExtraResult != null;
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

                if (Objects.requireNonNull(estimateLocationTextInputEditText.getText()).toString().isEmpty() && issueDateValue.isEmpty() && expirationDateValue.isEmpty() && customerIdTextInputEditText.getText().toString().isEmpty() && Objects.requireNonNull(estimateDiscountTextInputEditText.getText()).toString().isEmpty() && vatTextInputEditText.getText().toString().isEmpty()) {
                    Toast emptyFields = Toast.makeText(getApplicationContext(), "Empty Fields.", Toast.LENGTH_LONG);
                    emptyFields.show();
                } else {
                    AlertDialog.Builder alertAdd = new AlertDialog.Builder(AddEstimate.this);
                    alertAdd.setTitle("Confirm Add");
                    alertAdd.setMessage("Do you really want to add the estimate ?");
                    alertAdd.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbAdapter = new DBAdapter(getApplicationContext());
                            TextInputEditText estimateLocationTextInputEditText = findViewById(R.id.locationEditText_add_estimate);
                            TextInputEditText customerIdTextInputEditText = findViewById(R.id.customerEditText_add_estimate);
                            TextInputEditText estimateDiscountTextInputEditText = findViewById(R.id.discountEditText_add_estimate);
                            TextInputEditText vatTextInputEditText = findViewById(R.id.vatEditText_add_estimate);

                            if (customerId != null) {
                                estimate.setCustomer(customerId);
                                customerExists = true;
                            } else {
                                if (Objects.requireNonNull(customerIdTextInputEditText.getText()).toString().isEmpty()) {
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
                                            estimate.setCustomer(null);
                                            customerExists = false;
                                        }
                                    }
                                }
                            }

                            if (!customerExists) {
                                Toast customerNotExistingToast = Toast.makeText(getApplicationContext(), "Le client saisi ne corresponds à aucun client dans la base de données", Toast.LENGTH_LONG);
                                customerNotExistingToast.show();
                                customerExists = true;
                            }

                            if (!issueDateValue.isEmpty()) {
                                estimate.setIssueDate(issueDateValue);
                            } else {
                                estimate.setIssueDate("");
                            }

                            if(!dueDateValue.isEmpty()){
                                estimate.setDueDate(dueDateValue);
                            } else {
                                estimate.setDueDate("");
                            }

                            if(!statusValue.isEmpty()){
                                estimate.setStatus(statusValue);
                            } else {
                                estimate.setStatus("");
                            }

                            if(!dueTermsValue.isEmpty()){
                                estimate.setDueTerms(dueTermsValue);
                            } else {
                                estimate.setDueTerms("");
                            }

                            if (!expirationDateValue.isEmpty()){
                                estimate.setExpirationDate(expirationDateValue);
                            } else {
                                estimate.setExpirationDate("");
                            }

                            if (Objects.requireNonNull(estimateLocationTextInputEditText.getText()).toString().isEmpty()) {
                                estimate.setDoneIn("");
                            } else {
                                estimate.setDoneIn(estimateLocationTextInputEditText.getText().toString());
                            }

                            if (Objects.requireNonNull(estimateDiscountTextInputEditText.getText()).toString().isEmpty()) {
                                estimate.setDiscount(null);
                            } else {
                                estimate.setDiscount(Float.parseFloat(estimateDiscountTextInputEditText.getText().toString()));
                            }

                            if (Objects.requireNonNull(vatTextInputEditText.getText()).toString().isEmpty()) {
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date expirationDate = null;
            try {
                expirationDate = sdf.parse(expirationDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Date issueDate = null;

            if(!issueDateValue.isEmpty()){
                try {
                    if(!issueDateValue.isEmpty()){
                        issueDate = sdf.parse(issueDateValue);
                    }
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date issueDate;

            try {
                issueDate = sdf.parse(issueDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Date expirationDate;

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

            Date dueDate = null;
            if(!dueDateValue.isEmpty()){
                try {
                    dueDate = sdf.parse(dueDateValue);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                long diffInMillis = dueDate.getTime() - issueDate.getTime();
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);

                if(daysBetween < 0){
                    Toast.makeText(getApplicationContext(), "Due date should be after the issue date", Toast.LENGTH_SHORT).show();
                    dueDateTextView.setText(R.string.dueDate);
                    dueDateValue = "";
                }
                else if(daysBetween == 0){
                    dueTermsSpinner.setSelection(1);
                }
                else if(daysBetween == 1){
                    dueTermsSpinner.setSelection(2);
                }
                else{
                    String dueTerm = daysBetween + " days";
                    dueDateTextView.setText(dueDateValue);
                    int position = dueTermsSpinnerAdapter.getPosition(dueTerm);

                    if (position >= 0) {
                        // ✅ Value exists in the spinner list
                        dueTermsSpinner.setSelection(position);
                    } else {
                        if(termsList.size() > 21){
                            termsList.remove(21);
                        }
                        termsList.add(dueTerm);
                        ArrayAdapter<String> due_terms_spinner_adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, termsList
                        );
                        dueTermsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dueTermsSpinner.setAdapter(due_terms_spinner_adapter);
                        dueTermsSpinner.setSelection(termsList.size() - 1);
                    }
                }
            }
        };

        dueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            dueDateValue = year + "-" + month + "-" + day;
            estimate.setDueDate(dueDateValue);
            dueDateTextView.setText(dueDateValue);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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
                assert dueDate != null;
                assert issueDate != null;
                long diffInMillis = dueDate.getTime() - issueDate.getTime();
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);

                if(daysBetween < 0){
                    Toast.makeText(getApplicationContext(), "Due date should be after the issue date", Toast.LENGTH_SHORT).show();
                    dueDateTextView.setText(R.string.dueDate);
                    dueDateValue = "";
                }
                else if(daysBetween == 0){
                    dueTermsSpinner.setSelection(1);
                }
                else if(daysBetween == 1){
                    dueTermsSpinner.setSelection(2);
                }
                else{
                    String dueTerm = daysBetween + " days";
                    dueDateTextView.setText(dueDateValue);
                    int position = dueTermsSpinnerAdapter.getPosition(dueTerm);

                    if (position >= 0) {
                        // ✅ Value exists in the spinner list
                        dueTermsSpinner.setSelection(position);
                    } else {
                        if(termsList.size() > 21){
                            termsList.remove(21);
                        }
                        termsList.add(dueTerm);
                        ArrayAdapter<String> due_terms_spinner_adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, termsList
                        );
                        dueTermsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dueTermsSpinner.setAdapter(due_terms_spinner_adapter);
                        dueTermsSpinner.setSelection(termsList.size() - 1);
                    }
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