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

public class AddEstimate extends AppCompatActivity {
    Integer customerId;
    DBAdapter dbAdapter;
    TextView expirationDateTextView, issueDateTextView, dueDateTextView;

    private String statusValue = "Select status";
    private String dueTermsValue = "Select due terms";

    long issueDateTimestamp = 0;
    long dueDateTimestamp = 0;
    long expirationDateTimestamp = 0;

    String expirationDateValue = "", issueDateValue = "", dueDateValue = "";
    private ActivityResultLauncher<Intent> activityResultLauncher;
    Button addEstimate;
    Button clearAddEstimateForm;
    private DatePickerDialog.OnDateSetListener expirationDateSetListener, issueDateSetListener, dueDateSetListener;
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
                estimate.setDueTerms(dueTermsValue);

                // IMPORTANT: use timestamp, not TextView
                if (issueDateTimestamp == 0) {
                    return;
                }

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(issueDateTimestamp);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                long dueTimestamp = 0;

                if (dueTermsValue.equals("Due on receipt")) {

                    // Same as issue date
                    dueTimestamp = issueDateTimestamp;

                } else if (dueTermsValue.equals("Next day")) {

                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    dueTimestamp = cal.getTimeInMillis();

                } else if (dueTermsValue.equals("Custom")) {

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            AddEstimate.this,
                            (view1, year, month, dayOfMonth) -> {

                                Calendar customCal = Calendar.getInstance();
                                customCal.set(year, month, dayOfMonth, 0, 0, 0);
                                customCal.set(Calendar.MILLISECOND, 0);

                                long customTimestamp = customCal.getTimeInMillis();

                                // save
                                dueDateTimestamp = customTimestamp;
                                estimate.setDueDate(customTimestamp);

                                // display
                                String formatted = sdf.format(customCal.getTime());
                                dueDateValue = formatted;
                                dueDateTextView.setText(formatted);
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();
                    return;

                } else if (!dueTermsValue.equals("Select due terms")) {

                    // "2 days", "3 days", etc.
                    String daysStr = dueTermsValue.replace(" days", "").trim();
                    int days = Integer.parseInt(daysStr);

                    cal.add(Calendar.DAY_OF_MONTH, days);
                    dueTimestamp = cal.getTimeInMillis();
                }

                // ===== SAVE + DISPLAY =====
                if (dueTimestamp != 0) {

                    dueDateTimestamp = dueTimestamp;
                    estimate.setDueDate(dueTimestamp);

                    String formatted = sdf.format(new Date(dueTimestamp));
                    dueDateValue = formatted;
                    dueDateTextView.setText(formatted);
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

                            // ISSUE DATE
                            if (issueDateTimestamp != 0) {
                                estimate.setIssueDate(issueDateTimestamp);
                            } else {
                                estimate.setIssueDate(0);
                            }

                            // EXPIRATION DATE
                            if (expirationDateTimestamp != 0) {
                                estimate.setExpirationDate(expirationDateTimestamp);
                            } else {
                                estimate.setExpirationDate(0);
                            }
                            // DUE DATE
                            if (dueDateTimestamp != 0) {
                                estimate.setDueDate(dueDateTimestamp);
                            } else {
                                estimate.setDueDate(0);
                            }

                            if(!dueTermsValue.isEmpty()){
                                estimate.setDueTerms(dueTermsValue);
                            } else {
                                estimate.setDueTerms("");
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
                    expirationDateSetListener,
                    year, month, day
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateSetListener = (picker, year, month, day) -> {

            // Create calendar (month is already 0-based from DatePicker)
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // Timestamp (for DB)
            long expirationTimestamp = cal.getTimeInMillis();

            // Formatted string (for UI)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            expirationDateValue = sdf.format(cal.getTime());
            expirationDateTextView.setText(expirationDateValue);

            // ===== VALIDATION =====
            if (issueDateTimestamp > 0) {

                long diffInMillis = expirationTimestamp - issueDateTimestamp;
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);

                if (daysBetween <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "Expiration date should be after the issue date",
                            Toast.LENGTH_SHORT).show();

                    expirationDateTextView.setText(R.string.expirationDate);
                    expirationDateValue = "";
                    return;
                }
            }

            // Save timestamp (important)
            expirationDateTimestamp = expirationTimestamp;
        };

        issueDateSetListener = (picker, year, month, day) -> {

            // Build issue date as timestamp
            String currentIssueDateValue = issueDateTextView.getText().toString();
            Calendar issueCal = Calendar.getInstance();
            issueCal.set(year, month, day, 0, 0, 0);
            issueCal.set(Calendar.MILLISECOND, 0);

            long issueTimestamp = issueCal.getTimeInMillis();

            // UI format (only for display)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            issueDateValue = sdf.format(issueCal.getTime());
            issueDateTextView.setText(issueDateValue);

            // =========================
            // VALIDATE against expiration
            // =========================
            if (expirationDateTimestamp > 0) {

                long diffInMillis = expirationDateTimestamp - issueTimestamp;
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);

                if (daysBetween <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "Expiration date should be after the issue date",
                            Toast.LENGTH_SHORT).show();
                    if(currentIssueDateValue.isEmpty()){
                        issueDateTextView.setText(R.string.issueDate);
                        issueDateValue = "";
                        issueDateTimestamp = 0;
                        return;
                    }
                }
            }

            // =========================
            // VALIDATE against due date
            // =========================
            if (dueDateTimestamp > 0) {

                long diffInMillis = dueDateTimestamp - issueTimestamp;
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);

                if (daysBetween < 0) {

                    Toast.makeText(getApplicationContext(),
                            "Due date should be after the issue date",
                            Toast.LENGTH_SHORT).show();
                    if(currentIssueDateValue.isEmpty()){
                        issueDateTextView.setText(R.string.issueDate);
                        issueDateValue = "";
                        issueDateTimestamp = 0;
                        return;
                    }

                } else if (daysBetween == 0) {
                    dueTermsSpinner.setSelection(1);

                } else if (daysBetween == 1) {
                    dueTermsSpinner.setSelection(2);

                } else {

                    String dueTerm = daysBetween + " days";

                    int position = dueTermsSpinnerAdapter.getPosition(dueTerm);

                    if (position >= 0) {
                        dueTermsSpinner.setSelection(position);
                    } else {

                        if (termsList.size() > 21) {
                            termsList.remove(21);
                        }

                        termsList.add(dueTerm);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                termsList
                        );

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dueTermsSpinner.setAdapter(adapter);

                        dueTermsSpinner.setSelection(termsList.size() - 1);
                    }
                }
            }

            // store timestamp for DB
            issueDateTimestamp = issueTimestamp;
        };

        dueDateSetListener = (picker, year, month, day) -> {

            // Build due date as timestamp
            String previousDueDateValue = dueDateTextView.getText().toString();
            Calendar dueCal = Calendar.getInstance();
            dueCal.set(year, month, day, 0, 0, 0);
            dueCal.set(Calendar.MILLISECOND, 0);

            long dueTimestamp = dueCal.getTimeInMillis();

            // UI display format only
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dueDateValue = sdf.format(dueCal.getTime());
            dueDateTextView.setText(dueDateValue);

            // =========================
            // VALIDATION against issue date
            // =========================
            if (issueDateTimestamp > 0) {

                long diffInMillis = dueTimestamp - issueDateTimestamp;
                long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);

                if (daysBetween < 0) {

                    Toast.makeText(getApplicationContext(),
                            "Due date should be after the issue date",
                            Toast.LENGTH_SHORT).show();

                    if(previousDueDateValue.isEmpty()){
                        dueDateTextView.setText(R.string.dueDate);
                        dueDateValue = "";
                        dueDateTimestamp = 0;
                        return;
                    }

                } else if (daysBetween == 0) {
                    dueTermsSpinner.setSelection(1);

                } else if (daysBetween == 1) {
                    dueTermsSpinner.setSelection(2);

                } else {

                    String dueTerm = daysBetween + " days";

                    int position = dueTermsSpinnerAdapter.getPosition(dueTerm);

                    if (position >= 0) {
                        dueTermsSpinner.setSelection(position);
                    } else {

                        if (termsList.size() > 21) {
                            termsList.remove(21);
                        }

                        termsList.add(dueTerm);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                termsList
                        );

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dueTermsSpinner.setAdapter(adapter);

                        dueTermsSpinner.setSelection(termsList.size() - 1);
                    }
                }
            }

            // store timestamp for DB
            dueDateTimestamp = dueTimestamp;

            // also update your model if needed
            estimate.setDueDate(dueDateTimestamp);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close DBAdapter to release database resources
        if (dbAdapter != null) {
            dbAdapter.close();
        }
    }
}