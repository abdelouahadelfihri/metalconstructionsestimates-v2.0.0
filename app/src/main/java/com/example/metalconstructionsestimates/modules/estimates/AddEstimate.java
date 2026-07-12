package com.example.metalconstructionsestimates.modules.estimates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.example.metalconstructionsestimates.SettingsActivity;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddEstimate extends AppCompatActivity {

    Integer customerId;
    DBAdapter dbAdapter;
    TextView expirationDateTextView, issueDateTextView, dueDateTextView;

    private String statusValue   = "Select status";
    private String dueTermsValue = "Select due terms";

    long issueDateTimestamp       = 0;
    long dueDateTimestamp         = 0;
    long expirationDateTimestamp  = 0;
    long previousIssueDateTimestamp      = 0;
    long previousDueDateTimestamp        = 0;
    long previousExpirationDateTimestamp = 0;

    String expirationDateValue = "", issueDateValue = "", dueDateValue = "";

    private ActivityResultLauncher<Intent> activityResultLauncher;
    Button addEstimate, clearAddEstimateForm;
    private DatePickerDialog.OnDateSetListener expirationDateSetListener, issueDateSetListener, dueDateSetListener;
    Intent intent;
    boolean customerExists = true;

    // ── Settings values ────────────────────────────────────────────────────
    private String dateFormat;          // from settings, e.g. "dd/MM/yyyy"
    private int    defaultExpirationDays; // from settings, e.g. 30
    private float  defaultVat;          // from settings, e.g. 20.0
    private String defaultDueTerms;     // from settings, e.g. "30 days"

    // Spinner + adapter kept as fields so listeners can reference them
    private Spinner           dueTermsSpinner;
    private ArrayAdapter<String> dueTermsSpinnerAdapter;
    private List<String>      termsList;

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

        // ── Load settings ──────────────────────────────────────────────────
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_SETTINGS, MODE_PRIVATE);
        dateFormat            = prefs.getString(SettingsActivity.KEY_DATE_FORMAT, "dd/MM/yyyy");
        defaultExpirationDays = prefs.getInt(SettingsActivity.KEY_EXPIRATION_DAYS, 30);
        defaultVat            = prefs.getFloat(SettingsActivity.KEY_DEFAULT_VAT, 20.0f);
        defaultDueTerms       = prefs.getString(SettingsActivity.KEY_DUE_TERMS, "Due on receipt");

        Estimate estimate = new Estimate();

        // ── Status spinner ─────────────────────────────────────────────────
        Spinner estimateStatusSpinner = findViewById(R.id.estimateStatusSpinner);
        ArrayAdapter<CharSequence> estimateStatusSpinnerAdapter = ArrayAdapter.createFromResource(
                this, R.array.estimate_status, android.R.layout.simple_spinner_item);
        estimateStatusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estimateStatusSpinner.setAdapter(estimateStatusSpinnerAdapter);

        // ── Due terms spinner ──────────────────────────────────────────────
        dueTermsSpinner = findViewById(R.id.dueTermsSpinner);
        String[] termsArray = getResources().getStringArray(R.array.due_terms);
        termsList = new ArrayList<>(Arrays.asList(termsArray));
        dueTermsSpinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, termsList);
        dueTermsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dueTermsSpinner.setAdapter(dueTermsSpinnerAdapter);

        // ── Pre-select default due terms from settings ─────────────────────
        int defaultTermsPos = dueTermsSpinnerAdapter.getPosition(defaultDueTerms);
        if (defaultTermsPos >= 0) {
            dueTermsSpinner.setSelection(defaultTermsPos);
            dueTermsValue = defaultDueTerms;
            estimate.setDueTerms(defaultDueTerms);
        }

        // ── Views ──────────────────────────────────────────────────────────
        Button selectCustomer  = findViewById(R.id.selectCustomerButton_add_estimate);
        addEstimate            = findViewById(R.id.addButton_add_estimate);
        clearAddEstimateForm   = findViewById(R.id.clearButton_add_estimate);
        issueDateTextView      = findViewById(R.id.issueDateValue);
        dueDateTextView        = findViewById(R.id.dueDateValue);
        expirationDateTextView = findViewById(R.id.expirationDateValue);

        // ── Pre-fill VAT from settings ─────────────────────────────────────
        TextInputEditText vatTextInputEditText = findViewById(R.id.vatEditText_add_estimate);
        vatTextInputEditText.setText(String.valueOf(
                defaultVat % 1 == 0 ? (int) defaultVat : defaultVat));

        // ── Listeners ──────────────────────────────────────────────────────
        selectCustomer.setOnClickListener(view -> startActivityForResult());

        estimateStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusValue = parent.getItemAtPosition(position).toString();
                estimate.setStatus(statusValue);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                statusValue = "Pending";
                estimate.setStatus(statusValue);
            }
        });

        dueTermsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dueTermsValue = parent.getItemAtPosition(position).toString();
                estimate.setDueTerms(dueTermsValue);

                if (issueDateTimestamp == 0) return;

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(issueDateTimestamp);
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
                long dueTimestamp = 0;

                if (dueTermsValue.equals("Due on receipt")) {
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
                                dueDateTimestamp = customTimestamp;
                                estimate.setDueDate(customTimestamp);
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
                    String daysStr = dueTermsValue.replace(" days", "").trim();
                    try {
                        int days = Integer.parseInt(daysStr);
                        cal.add(Calendar.DAY_OF_MONTH, days);
                        dueTimestamp = cal.getTimeInMillis();
                    } catch (NumberFormatException ignored) {}
                }

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
                        String customerIdExtraResult = Objects.requireNonNull(
                                Objects.requireNonNull(data).getExtras()).getString("customerIdExtraResult");
                        assert customerIdExtraResult != null;
                        customerId = Integer.parseInt(customerIdExtraResult);
                        TextInputEditText customerIdTextInputEditText = findViewById(R.id.customerEditText_add_estimate);
                        String customerName = dbAdapter.getCustomerById(customerId).getName();
                        customerIdTextInputEditText.setText(customerName);
                    }
                }
        );

        addEstimate.setOnClickListener(view -> {
            TextInputEditText estimateLocationTextInputEditText = findViewById(R.id.locationEditText_add_estimate);
            TextInputEditText customerIdTextInputEditText      = findViewById(R.id.customerEditText_add_estimate);
            TextInputEditText estimateDiscountTextInputEditText = findViewById(R.id.discountEditText_add_estimate);
            TextInputEditText vatField                         = findViewById(R.id.vatEditText_add_estimate);

            if (Objects.requireNonNull(estimateLocationTextInputEditText.getText()).toString().isEmpty()
                    && issueDateValue.isEmpty()
                    && expirationDateValue.isEmpty()
                    && customerIdTextInputEditText.getText().toString().isEmpty()
                    && Objects.requireNonNull(estimateDiscountTextInputEditText.getText()).toString().isEmpty()
                    && vatField.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Empty Fields.", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder alertAdd = new AlertDialog.Builder(AddEstimate.this);
                alertAdd.setTitle("Confirm Add");
                alertAdd.setMessage("Do you really want to add the estimate ?");
                alertAdd.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dbAdapter = new DBAdapter(getApplicationContext());
                    TextInputEditText locField      = findViewById(R.id.locationEditText_add_estimate);
                    TextInputEditText custField     = findViewById(R.id.customerEditText_add_estimate);
                    TextInputEditText discField     = findViewById(R.id.discountEditText_add_estimate);
                    TextInputEditText vatFieldInner = findViewById(R.id.vatEditText_add_estimate);

                    if (customerId != null) {
                        estimate.setCustomer(customerId);
                        customerExists = true;
                    } else {
                        if (Objects.requireNonNull(custField.getText()).toString().isEmpty()) {
                            estimate.setCustomer(null);
                        } else {
                            if (allDigitString(custField.getText().toString())) {
                                Customer customer = dbAdapter.getCustomerById(
                                        Integer.parseInt(custField.getText().toString()));
                                if (customer != null) {
                                    estimate.setCustomer(customer.getId());
                                    customerExists = true;
                                } else {
                                    customerExists = false;
                                }
                            } else {
                                Integer customer = dbAdapter.getCustomerIdByName(custField.getText().toString());
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
                        Toast.makeText(getApplicationContext(),
                                "Le client saisi ne corresponds à aucun client dans la base de données",
                                Toast.LENGTH_LONG).show();
                        customerExists = true;
                    }

                    estimate.setIssueDate(issueDateTimestamp != 0 ? issueDateTimestamp : 0L);
                    estimate.setExpirationDate(expirationDateTimestamp != 0 ? expirationDateTimestamp : 0L);
                    estimate.setDueDate(dueDateTimestamp != 0 ? dueDateTimestamp : 0L);
                    estimate.setDueTerms(!dueTermsValue.isEmpty() ? dueTermsValue : "");
                    estimate.setDoneIn(!Objects.requireNonNull(locField.getText()).toString().isEmpty()
                            ? locField.getText().toString() : "");

                    estimate.setDiscount(!Objects.requireNonNull(discField.getText()).toString().isEmpty()
                            ? Float.parseFloat(discField.getText().toString()) : null);

                    estimate.setVat(!Objects.requireNonNull(vatFieldInner.getText()).toString().isEmpty()
                            ? Float.parseFloat(vatFieldInner.getText().toString()) : null);

                    dbAdapter.saveEstimate(estimate);
                    Toast.makeText(getApplicationContext(),
                            "Estimate has been successfully added", Toast.LENGTH_LONG).show();
                    intent = new Intent(AddEstimate.this, Estimates.class);
                    startActivity(intent);
                });
                alertAdd.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
                alertAdd.show();
            }
        });

        clearAddEstimateForm.setOnClickListener(view -> {
            TextInputEditText locField  = findViewById(R.id.locationEditText_add_estimate);
            TextInputEditText custField = findViewById(R.id.customerEditText_add_estimate);
            TextInputEditText discField = findViewById(R.id.discountEditText_add_estimate);
            TextInputEditText vatField2 = findViewById(R.id.vatEditText_add_estimate);

            Objects.requireNonNull(locField.getText()).clear();
            issueDateTextView.setText(R.string.issueDate);
            expirationDateTextView.setText(R.string.expirationDate);
            dueDateTextView.setText(R.string.dueDate);
            Objects.requireNonNull(custField.getText()).clear();
            Objects.requireNonNull(discField.getText()).clear();

            // Reset VAT back to default from settings
            vatField2.setText(String.valueOf(defaultVat % 1 == 0 ? (int) defaultVat : defaultVat));

            // Reset timestamps
            issueDateTimestamp = 0;
            expirationDateTimestamp = 0;
            dueDateTimestamp = 0;
            issueDateValue = "";
            expirationDateValue = "";
            dueDateValue = "";

            // Reset due terms to default from settings
            int defaultTermsPos2 = dueTermsSpinnerAdapter.getPosition(defaultDueTerms);
            if (defaultTermsPos2 >= 0) dueTermsSpinner.setSelection(defaultTermsPos2);
        });

        // ── Issue date picker ──────────────────────────────────────────────
        issueDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    AddEstimate.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    issueDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dueDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    AddEstimate.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dueDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    AddEstimate.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    expirationDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        // ── Date set listeners ─────────────────────────────────────────────
        expirationDateSetListener = (picker, year, month, day) -> {
            String currentExpirationDateValue = expirationDateTextView.getText().toString();
            previousExpirationDateTimestamp = expirationDateTimestamp;

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long expirationTimestamp = cal.getTimeInMillis();

            // Use date format from settings
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            expirationDateValue = sdf.format(cal.getTime());
            expirationDateTextView.setText(expirationDateValue);

            if (issueDateTimestamp > 0) {
                long daysBetween = (expirationTimestamp - issueDateTimestamp) / (1000 * 60 * 60 * 24);
                if (daysBetween <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    if (currentExpirationDateValue.equals(getString(R.string.expirationDate))) {
                        expirationDateTextView.setText(R.string.expirationDate);
                        expirationDateValue = "";
                        expirationDateTimestamp = 0;
                    } else {
                        expirationDateTextView.setText(currentExpirationDateValue);
                        expirationDateValue = currentExpirationDateValue;
                        expirationDateTimestamp = previousExpirationDateTimestamp;
                    }
                    return;
                }
            }
            expirationDateTimestamp = expirationTimestamp;
        };

        issueDateSetListener = (picker, year, month, day) -> {
            String currentIssueDateValue = issueDateTextView.getText().toString();
            previousIssueDateTimestamp = issueDateTimestamp;

            Calendar issueCal = Calendar.getInstance();
            issueCal.set(year, month, day, 0, 0, 0);
            issueCal.set(Calendar.MILLISECOND, 0);
            long issueTimestamp = issueCal.getTimeInMillis();

            // Use date format from settings
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            issueDateValue = sdf.format(issueCal.getTime());
            issueDateTextView.setText(issueDateValue);

            // Validate against expiration
            if (expirationDateTimestamp > 0) {
                long daysBetween = (expirationDateTimestamp - issueTimestamp) / (1000 * 60 * 60 * 24);
                if (daysBetween <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    if (currentIssueDateValue.equals(getString(R.string.issueDate))) {
                        issueDateTextView.setText(R.string.issueDate);
                        issueDateValue = "";
                        issueDateTimestamp = 0;
                    } else {
                        issueDateTextView.setText(currentIssueDateValue);
                        issueDateValue = currentIssueDateValue;
                        issueDateTimestamp = previousIssueDateTimestamp;
                    }
                    return;
                }
            }

            // Validate against due date
            if (dueDateTimestamp > 0) {
                long daysBetween = (dueDateTimestamp - issueTimestamp) / (1000 * 60 * 60 * 24);
                if (daysBetween < 0) {
                    Toast.makeText(getApplicationContext(),
                            "Due date should be after the issue date", Toast.LENGTH_SHORT).show();
                    if (currentIssueDateValue.isEmpty()) {
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
                        if (termsList.size() > 21) termsList.remove(21);
                        termsList.add(dueTerm);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, termsList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dueTermsSpinner.setAdapter(adapter);
                        dueTermsSpinner.setSelection(termsList.size() - 1);
                    }
                }
            }

            // ── Auto-calculate expiration date from settings ───────────────
            if (expirationDateTimestamp == 0 && defaultExpirationDays > 0) {
                Calendar expCal = Calendar.getInstance();
                expCal.setTimeInMillis(issueTimestamp);
                expCal.add(Calendar.DAY_OF_MONTH, defaultExpirationDays);
                expirationDateTimestamp = expCal.getTimeInMillis();
                expirationDateValue = sdf.format(expCal.getTime());
                expirationDateTextView.setText(expirationDateValue);
                estimate.setExpirationDate(expirationDateTimestamp);
            }

            // ── ADD THIS: Auto-calculate due date from default due terms ───
            if (dueDateTimestamp == 0 && !defaultDueTerms.isEmpty()) {
                Calendar dueCal = Calendar.getInstance();
                dueCal.setTimeInMillis(issueTimestamp);
                long autoDueTimestamp = 0;

                if (defaultDueTerms.equals("Due on receipt")) {
                    autoDueTimestamp = issueTimestamp;
                } else if (defaultDueTerms.equals("Next day")) {
                    dueCal.add(Calendar.DAY_OF_MONTH, 1);
                    autoDueTimestamp = dueCal.getTimeInMillis();
                } else if (!defaultDueTerms.equals("Custom")
                        && !defaultDueTerms.equals("Select due terms")) {
                    try {
                        int days = Integer.parseInt(defaultDueTerms.replace(" days", "").trim());
                        dueCal.add(Calendar.DAY_OF_MONTH, days);
                        autoDueTimestamp = dueCal.getTimeInMillis();
                    } catch (NumberFormatException ignored) {}
                }

                if (autoDueTimestamp != 0) {
                    dueDateTimestamp = autoDueTimestamp;
                    estimate.setDueDate(dueDateTimestamp);
                    dueDateValue = sdf.format(new Date(dueDateTimestamp));
                    dueDateTextView.setText(dueDateValue);
                    // Reflect in spinner
                    int pos = dueTermsSpinnerAdapter.getPosition(defaultDueTerms);
                    if (pos >= 0) dueTermsSpinner.setSelection(pos);
                }
            }

            issueDateTimestamp = issueTimestamp;
        };

        dueDateSetListener = (picker, year, month, day) -> {
            String currentDueDateValue = dueDateTextView.getText().toString();
            previousDueDateTimestamp = dueDateTimestamp;

            Calendar dueCal = Calendar.getInstance();
            dueCal.set(year, month, day, 0, 0, 0);
            dueCal.set(Calendar.MILLISECOND, 0);
            long dueTimestamp = dueCal.getTimeInMillis();

            // Use date format from settings
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            dueDateValue = sdf.format(dueCal.getTime());
            dueDateTextView.setText(dueDateValue);

            if (issueDateTimestamp > 0) {
                long daysBetween = (dueTimestamp - issueDateTimestamp) / (1000 * 60 * 60 * 24);
                if (daysBetween < 0) {
                    Toast.makeText(getApplicationContext(),
                            "Due date should be after the issue date", Toast.LENGTH_SHORT).show();
                    if (currentDueDateValue.equals(getString(R.string.dueDate))) {
                        dueDateTextView.setText(R.string.dueDate);
                        dueDateValue = "";
                        dueDateTimestamp = 0;
                    } else {
                        dueDateTextView.setText(currentDueDateValue);
                        dueDateValue = currentDueDateValue;
                        dueDateTimestamp = previousDueDateTimestamp;
                    }
                    return;
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
                        if (termsList.size() > 21) termsList.remove(21);
                        termsList.add(dueTerm);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, termsList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dueTermsSpinner.setAdapter(adapter);
                        dueTermsSpinner.setSelection(termsList.size() - 1);
                    }
                }
            }

            dueDateTimestamp = dueTimestamp;
            estimate.setDueDate(dueDateTimestamp);
        };
    }

    public void startActivityForResult() {
        Intent intent = new Intent(AddEstimate.this, Customers.class);
        activityResultLauncher.launch(intent);
    }

    public boolean allDigitString(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbAdapter != null) dbAdapter.close();
    }
}