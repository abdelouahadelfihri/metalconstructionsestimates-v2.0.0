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
import android.widget.DatePicker;
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
import java.util.Locale;
import java.util.Objects;

public class AddEstimate extends AppCompatActivity {
    Integer customerId;
    DBAdapter dbAdapter;
    TextView expirationDateTextView, issueDateTextView, dueDateTextView;

    String statusValue = "Pending";  // Default to Pending, not "Select status"
    String dueTermsValue = "Select due terms";

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
        // Set initial selection to "Pending" if it's in the array (assume position 1; adjust if needed)
        for (int i = 0; i < estimateStatusSpinnerAdapter.getCount(); i++) {
            if (estimateStatusSpinnerAdapter.getItem(i).toString().equals("Pending")) {
                estimateStatusSpinner.setSelection(i);
                statusValue = "Pending";
                break;
            }
        }

        Spinner dueTermsSpinner = (Spinner) findViewById(R.id.dueTermsSpinner);
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

        estimateStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusValue = parent.getItemAtPosition(position).toString();
                Log.d("AddEstimate", "Status selected: " + statusValue);  // Debug log
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                statusValue = "Pending";
                Log.d("AddEstimate", "Status nothing selected, default: " + statusValue);
            }
        });

        dueTermsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dueTermsValue = parent.getItemAtPosition(position).toString();
                // Reset dueDateValue each time
                dueDateValue = "";

                // Get issue date from your TextView
                String issueDateStr = issueDateTextView.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                Date issueDate = null;

                try {
                    if (!issueDateStr.equals("--/--/----") && !issueDateStr.isEmpty()) {
                        issueDate = sdf.parse(issueDateStr);
                    }
                } catch (ParseException e) {
                    Log.e("AddEstimateActivity", "Error parsing issue date", e);
                }

                Calendar cal = Calendar.getInstance();
                if (issueDate == null) {
                    Log.d("AddEstimate", "No issue date set, skipping due date calc");
                    return;
                }
                cal.setTime(issueDate);

                if (dueTermsValue.equals("Due on receipt")) {
                    // Same as issue date
                    dueDateValue = issueDateStr;
                } else if (dueTermsValue.equals("Next day")) {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    dueDateValue = sdf.format(cal.getTime());
                } else if (dueTermsValue.equals("Custom")) {
                    // Open DatePicker for custom selection
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            AddEstimate.this,
                            (view1, year, month, dayOfMonth) -> {
                                Calendar customCal = Calendar.getInstance();
                                customCal.set(year, month, dayOfMonth);
                                dueDateValue = sdf.format(customCal.getTime());
                                dueDateTextView.setText(dueDateValue);
                                Log.d("AddEstimate", "Custom due date: " + dueDateValue);
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();
                    return;  // Don't update UI yet, as dialog is async
                } else if (!dueTermsValue.equals("Select due terms")) {
                    // For "2 days", "3 days", ..., "180 days"
                    String daysStr = dueTermsValue.replace(" days", "").trim();
                    int days = Integer.parseInt(daysStr);
                    cal.add(Calendar.DAY_OF_MONTH, days);
                    dueDateValue = sdf.format(cal.getTime());
                    Log.d("AddEstimate", "Due date set to " + days + " days: " + dueDateValue);
                }

                // Update your UI if due date was set
                if (!dueDateValue.isEmpty()) {
                    dueDateTextView.setText(dueDateValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dueTermsValue = "Select due terms";
                Log.d("AddEstimate", "Due terms nothing selected: " + dueTermsValue);
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            String customerIdExtraResult = data.getExtras().getString("customerIdExtraResult");
                            if (customerIdExtraResult != null) {
                                customerId = Integer.parseInt(customerIdExtraResult);
                                TextInputEditText customerIdTextInputEditText = findViewById(R.id.customerEditText_add_estimate);
                                String customerName = dbAdapter.getCustomerById(customerId).getName();
                                customerIdTextInputEditText.setText(customerName);
                            }
                        }
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

                String location = Objects.requireNonNull(estimateLocationTextInputEditText.getText()).toString().trim();
                String customerText = Objects.requireNonNull(customerIdTextInputEditText.getText()).toString().trim();
                String discount = Objects.requireNonNull(estimateDiscountTextInputEditText.getText()).toString().trim();
                String vat = Objects.requireNonNull(vatTextInputEditText.getText()).toString().trim();

                // Only show toast if truly all fields empty (relaxed from original)
                if (location.isEmpty() && issueDateValue.isEmpty() && expirationDateValue.isEmpty() && customerText.isEmpty()
                        && discount.isEmpty() && vat.isEmpty() && statusValue.equals("Select status") && dueTermsValue.equals("Select due terms")) {
                    Toast emptyFields = Toast.makeText(getApplicationContext(), "All fields are empty.", Toast.LENGTH_LONG);
                    emptyFields.show();
                    return;
                }

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

                        // Customer handling (preserve selected customerId if set)
                        if (customerId != null) {
                            estimate.setCustomer(customerId);
                            customerExists = true;
                        } else if (!Objects.requireNonNull(customerIdTextInputEditText.getText()).toString().trim().isEmpty()) {
                            if (allDigitString(customerIdTextInputEditText.getText().toString().trim())) {
                                Customer customer = dbAdapter.getCustomerById(Integer.parseInt(customerIdTextInputEditText.getText().toString().trim()));
                                if (customer != null) {
                                    estimate.setCustomer(customer.getId());
                                    customerExists = true;
                                } else {
                                    customerExists = false;
                                }
                            } else {
                                Integer custId = dbAdapter.getCustomerIdByName(customerIdTextInputEditText.getText().toString().trim());
                                if (custId != null) {
                                    estimate.setCustomer(custId);
                                    customerExists = true;
                                } else {
                                    customerExists = false;
                                }
                            }
                            if (!customerExists) {
                                estimate.setCustomer(null);
                            }
                        } else {
                            estimate.setCustomer(null);
                            customerExists = true;  // Allow null customer
                        }

                        if (!customerExists) {
                            Toast customerNotExistingToast = Toast.makeText(getApplicationContext(), "Le client saisi ne corresponds à aucun client dans la base de données", Toast.LENGTH_LONG);
                            customerNotExistingToast.show();
                            customerExists = true;
                            return;
                        }

                        // Dates
                        estimate.setIssueDate(issueDateValue.isEmpty() ? "" : issueDateValue);
                        estimate.setExpirationDate(expirationDateValue.isEmpty() ? "" : expirationDateValue);
                        estimate.setDueDate(dueDateValue.isEmpty() ? "" : dueDateValue);

                        // Status (log for debug)
                        String finalStatus = (statusValue == null || statusValue.equals("Select status")) ? "Pending" : statusValue;
                        estimate.setStatus(finalStatus);
                        Log.d("AddEstimate", "Saving status: " + finalStatus);

                        // Due terms (CRITICAL FIX: Now set it!)
                        String finalDueTerms = (dueTermsValue == null || dueTermsValue.equals("Select due terms")) ? "" : dueTermsValue;
                        estimate.setDueTerms(finalDueTerms);  // Assuming your Estimate model has this setter
                        Log.d("AddEstimate", "Saving due terms: " + finalDueTerms);
                        Log.d("AddEstimate", "Saving due date: " + estimate.getDueDate());  // Assuming getter for log

                        // Other fields
                        estimate.setDoneIn(Objects.requireNonNull(estimateLocationTextInputEditText.getText()).toString().trim().isEmpty() ? "" : estimateLocationTextInputEditText.getText().toString().trim());

                        if (Objects.requireNonNull(estimateDiscountTextInputEditText.getText()).toString().trim().isEmpty()) {
                            estimate.setDiscount(null);
                        } else {
                            estimate.setDiscount(Float.parseFloat(estimateDiscountTextInputEditText.getText().toString().trim()));
                        }

                        if (Objects.requireNonNull(vatTextInputEditText.getText()).toString().trim().isEmpty()) {
                            estimate.setVat(null);
                        } else {
                            estimate.setVat(Float.parseFloat(vatTextInputEditText.getText().toString().trim()));
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
        });

        clearAddEstimateForm.setOnClickListener(view -> {
            TextInputEditText estimateLocationTextInputEditText = findViewById(R.id.locationEditText_add_estimate);
            TextInputEditText customerIdTextInputEditText = findViewById(R.id.customerEditText_add_estimate);
            TextInputEditText estimateDiscountTextInputEditText = findViewById(R.id.discountEditText_add_estimate);
            TextInputEditText vatTextInputEditText = findViewById(R.id.vatEditText_add_estimate);

            Objects.requireNonNull(estimateLocationTextInputEditText.getText()).clear();
            Objects.requireNonNull(customerIdTextInputEditText.getText()).clear();
            Objects.requireNonNull(estimateDiscountTextInputEditText.getText()).clear();
            Objects.requireNonNull(vatTextInputEditText.getText()).clear();

            issueDateTextView.setText(R.string.issueDate);
            expirationDateTextView.setText(R.string.expirationDate);
            dueDateTextView.setText(R.string.dueDate);

            // Reset spinners to defaults
            customerId = null;
            statusValue = "Pending";
            dueTermsValue = "Select due terms";
            issueDateValue = "";
            expirationDateValue = "";
            dueDateValue = "";
        });

        issueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            issueDateValue = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);  // Ensure 0-padded
            issueDateTextView.setText(issueDateValue);
            Log.d("AddEstimate", "Issue date set: " + issueDateValue);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date issueDate;
            try {
                issueDate = sdf.parse(issueDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // Validate against expiration if set
            if (!expirationDateValue.isEmpty()) {
                try {
                    Date expirationDate = sdf.parse(expirationDateValue);
                    long diffInMillis = expirationDate.getTime() - issueDate.getTime();
                    long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);
                    if (daysBetween <= 0) {
                        Toast.makeText(getApplicationContext(), "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                        issueDateTextView.setText(R.string.issueDate);
                        issueDateValue = "";
                        return;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            // Re-trigger due date calc if due terms already selected
            if (!dueTermsValue.equals("Select due terms")) {
                dueTermsSpinner.getOnItemSelectedListener().onItemSelected(null, null, 0, 0);  // Simulate re-select to recalc
            }
        };

        expirationDateSetListner = (picker, year, month, day) -> {
            month = month + 1;
            expirationDateValue = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);  // Ensure 0-padded
            expirationDateTextView.setText(expirationDateValue);
            Log.d("AddEstimate", "Expiration date set: " + expirationDateValue);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            Date expirationDate;
            try {
                expirationDate = sdf.parse(expirationDateValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // Validate against issue if set
            if (!issueDateValue.isEmpty()) {
                try {
                    Date issueDate = sdf.parse(issueDateValue);
                    long diffInMillis = expirationDate.getTime() - issueDate.getTime();
                    long daysBetween = diffInMillis / (1000 * 60 * 60 * 24);
                    if (daysBetween <= 0) {
                        Toast.makeText(getApplicationContext(), "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                        expirationDateTextView.setText(R.string.expirationDate);
                        expirationDateValue = "";
                        return;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        dueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            dueDateValue = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);  // Ensure 0-padded
            dueDateTextView.setText(dueDateValue);
            Log.d("AddEstimate", "Due date set (manual): " + dueDateValue);
        };

        // Set up click listeners for date pickers (moved after defining listeners)
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
    }

    public void startActivityForResult() {
        Intent intent = new Intent(AddEstimate.this, Customers.class);
        activityResultLauncher.launch(intent);
    }

    public boolean allDigitString(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}