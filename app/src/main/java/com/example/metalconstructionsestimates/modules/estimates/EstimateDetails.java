package com.example.metalconstructionsestimates.modules.estimates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.metalconstructionsestimates.SettingsActivity;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.modules.estimates.estimatepreview.EstimatePreviewActivity;
import com.example.metalconstructionsestimates.util.CurrencyManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.recyclerviewadapters.EstimateLinesListAdapter;
import com.example.metalconstructionsestimates.databinding.ActivityEstimateDetailsBinding;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.modules.estimateslines.AddEstimateLine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class EstimateDetails extends AppCompatActivity {

    Integer estimateId;
    Integer customerId = null;
    boolean customerExists = true;

    String formattedTotalExcludingTax;
    String formattedTotalAfterDiscount;
    String formattedTotalAllTaxIncluded;

    Estimate estimate;

    DBAdapter dbAdapter;
    TextView expirationDateTextView, issueDateTextView, dueDateTextView;
    String expirationDateValue = "", issueDateValue = "", dueDateValue = "";

    long issueDateTimestamp              = 0;
    long previousIssueDateTimestamp      = 0;
    long dueDateTimestamp                = 0;
    long previousDueDateTimestamp        = 0;
    long expirationDateTimestamp         = 0;
    long previousExpirationDateTimestamp = 0;

    private DatePickerDialog.OnDateSetListener expirationDateSetListener, issueDateSetListener, dueDateSetListener;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    MaterialButton previewButton;

    ActivityEstimateDetailsBinding activityEstimateDetailsBinding;

    // ── Settings values ────────────────────────────────────────────────────
    private String          dateFormat;
    private CurrencyManager currencyManager;

    // ── Spinners kept as fields so issueDateSetListener can access them ────
    private Spinner              dueTermsSpinner;
    private ArrayAdapter<String> dueTermsSpinnerAdapter;
    private List<String>         termsList;
    private Spinner              estimateStatusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEstimateDetailsBinding = ActivityEstimateDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityEstimateDetailsBinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // ── Load settings ──────────────────────────────────────────────────
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_SETTINGS, MODE_PRIVATE);
        dateFormat      = prefs.getString(SettingsActivity.KEY_DATE_FORMAT, "dd/MM/yyyy");
        currencyManager = new CurrencyManager(this);

        String estimateIdExtra = getIntent().getStringExtra("estimateIdExtra");
        assert estimateIdExtra != null;
        estimateId = Integer.parseInt(estimateIdExtra);
        dbAdapter  = new DBAdapter(getApplicationContext());
        estimate   = dbAdapter.getEstimateById(estimateId);

        // ── Status spinner ─────────────────────────────────────────────────
        estimateStatusSpinner = findViewById(R.id.estimateStatusSpinner);
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

        previewButton = findViewById(R.id.previewButton);
        previewButton.setOnClickListener(view -> {
            Intent intent = new Intent(EstimateDetails.this, EstimatePreviewActivity.class);
            intent.putExtra("estimateId", estimateIdExtra);
            startActivity(intent);
        });

        // ── Views ──────────────────────────────────────────────────────────
        issueDateTextView      = findViewById(R.id.issueDateValue);
        expirationDateTextView = findViewById(R.id.expirationDateValue);
        dueDateTextView        = findViewById(R.id.dueDateValue);

        TextInputEditText totalExclTaxEditText        = findViewById(R.id.totalExclTaxEditText);
        TextInputEditText discountEditText             = findViewById(R.id.discountEditText);
        TextInputEditText totalAfterDiscountEditText   = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
        TextInputEditText locationEditText             = findViewById(R.id.locationEditText_estimate_details);
        AtomicReference<TextInputEditText> customerIdEditText = new AtomicReference<>(findViewById(R.id.customerIdEditText));
        Button selectCustomerButton                   = findViewById(R.id.selectCustomerButton);
        TextInputEditText vatEditText                 = findViewById(R.id.vatEditText);
        TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);
        TextInputEditText estimateIdEditText          = findViewById(R.id.estimateIdEditText_estimate_details);

        Button newEstimateLineButton          = findViewById(R.id.newEstimateLineButton);
        Button updateEstimateButton           = findViewById(R.id.updateButton_estimate_details);
        Button refreshEstimateLinesListButton = findViewById(R.id.refreshEstimateLinesListButton);
        Button deleteEstimateButton           = findViewById(R.id.deleteEstimateButton);

        DBAdapter db = new DBAdapter(getApplicationContext());
        ArrayList<EstimateLine> estimateLinesList = db.searchEstimateLines(estimateId);
        TextView noEstimateLinesTextView = findViewById(R.id.noEstimateLinesTextView);

        ConstraintLayout.LayoutParams recyclerParams = (ConstraintLayout.LayoutParams)
                activityEstimateDetailsBinding.estimateLinesRecyclerView.getLayoutParams();
        ConstraintLayout.LayoutParams noLinesParams = (ConstraintLayout.LayoutParams)
                noEstimateLinesTextView.getLayoutParams();
        ConstraintLayout.LayoutParams buttonParams = (ConstraintLayout.LayoutParams)
                activityEstimateDetailsBinding.newEstimateLineButton.getLayoutParams();

        if (!estimateLinesList.isEmpty()) {
            noEstimateLinesTextView.setVisibility(View.GONE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.VISIBLE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setAdapter(
                    new EstimateLinesListAdapter(this, estimateLinesList));
            recyclerParams.topToBottom = R.id.vatLayout;
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutParams(recyclerParams);
            buttonParams.topToBottom = R.id.estimateLinesRecyclerView;
            activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);
        } else {
            noEstimateLinesTextView.setVisibility(View.VISIBLE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.GONE);
            noLinesParams.topToBottom = R.id.vatLayout;
            noEstimateLinesTextView.setLayoutParams(noLinesParams);
            buttonParams.topToBottom = R.id.noEstimateLinesTextView;
            activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);
        }

        // ── Populate fields from DB using settings date format ─────────────
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        if (estimate.getIssueDate() == 0) {
            issueDateTextView.setText(R.string.issueDate);
            issueDateValue = "";
            previousIssueDateTimestamp = 0;
        } else {
            issueDateTimestamp = estimate.getIssueDate();
            previousIssueDateTimestamp = estimate.getIssueDate();
            issueDateValue = sdf.format(new Date(issueDateTimestamp));
            issueDateTextView.setText(issueDateValue);
        }

        if (estimate.getDueDate() == 0) {
            dueDateTextView.setText(R.string.dueDate);
            dueDateValue = "";
            previousDueDateTimestamp = 0;
        } else {
            dueDateTimestamp = estimate.getDueDate();
            previousDueDateTimestamp = estimate.getDueDate();
            dueDateValue = sdf.format(new Date(dueDateTimestamp));
            dueDateTextView.setText(dueDateValue);
        }

        if (estimate.getExpirationDate() == 0) {
            expirationDateTextView.setText(R.string.expirationDate);
            previousExpirationDateTimestamp = 0;
            expirationDateValue = "";
        } else {
            expirationDateTimestamp = estimate.getExpirationDate();
            previousExpirationDateTimestamp = estimate.getExpirationDate();
            expirationDateValue = sdf.format(new Date(expirationDateTimestamp));
            expirationDateTextView.setText(expirationDateValue);
        }

        // ── Due terms ──────────────────────────────────────────────────────
        if (estimate.getDueTerms().isEmpty()) {
            dueTermsSpinner.setSelection(0);
        } else {
            int position = dueTermsSpinnerAdapter.getPosition(estimate.getDueTerms());
            if (position >= 0) {
                dueTermsSpinner.setSelection(position);
            } else {
                dueTermsSpinnerAdapter.add(estimate.getDueTerms());
                dueTermsSpinnerAdapter.notifyDataSetChanged();
                dueTermsSpinner.setSelection(dueTermsSpinnerAdapter.getPosition(estimate.getDueTerms()));
            }
        }

        // ── Status ─────────────────────────────────────────────────────────
        if (estimate.getStatus().isEmpty()) {
            estimateStatusSpinner.setSelection(0);
        } else {
            switch (estimate.getStatus()) {
                case "Pending":   estimateStatusSpinner.setSelection(1); break;
                case "Approved":  estimateStatusSpinner.setSelection(2); break;
                case "Cancelled": estimateStatusSpinner.setSelection(3); break;
            }
        }

        estimateIdEditText.setText(estimate.getId().toString());
        locationEditText.setText(estimate.getDoneIn());
        customerId = estimate.getCustomer();

        if (estimate.getCustomer() == null) {
            customerIdEditText.get().setText("");
        } else {
            customerIdEditText.get().setText(dbAdapter.getCustomerById(estimate.getCustomer()).getName());
        }

        if (estimate.getExcludingTaxTotal() == null) {
            totalExclTaxEditText.setText("");
        } else {
            formattedTotalExcludingTax = BigDecimal.valueOf(estimate.getExcludingTaxTotal()).toPlainString();
            totalExclTaxEditText.setText(formattedTotalExcludingTax);
        }
        totalExclTaxEditText.setEnabled(false);

        if (estimate.getDiscount() == null) {
            discountEditText.setText("");
        } else {
            discountEditText.setText(estimate.getDiscount().toString());
        }

        if (estimate.getExcludingTaxTotalAfterDiscount() == null) {
            totalAfterDiscountEditText.setText("");
        } else {
            formattedTotalAfterDiscount = BigDecimal.valueOf(
                    estimate.getExcludingTaxTotalAfterDiscount()).setScale(0, RoundingMode.DOWN).toPlainString();
            totalAfterDiscountEditText.setText(formattedTotalAfterDiscount);
        }
        totalAfterDiscountEditText.setEnabled(false);

        if (estimate.getVat() == null) {
            vatEditText.setText("");
        } else {
            vatEditText.setText(estimate.getVat().toString());
        }

        if (estimate.getAllTaxIncludedTotal() == null) {
            totalAllTaxIncludedEditText.setText("");
        } else {
            formattedTotalAllTaxIncluded = BigDecimal.valueOf(
                    estimate.getAllTaxIncludedTotal()).setScale(0, RoundingMode.DOWN).toPlainString();
            totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);
        }
        totalAllTaxIncludedEditText.setEnabled(false);

        // ── Customer picker ────────────────────────────────────────────────
        selectCustomerButton.setOnClickListener(v -> startActivityForResult());
        AtomicInteger selectedCustomerId = new AtomicInteger(-1);
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            String customerIdExtraResult = extras.getString("customerIdExtraResult");
                            assert customerIdExtraResult != null;
                            selectedCustomerId.set(Integer.parseInt(customerIdExtraResult));
                            customerIdEditText.set(findViewById(R.id.customerIdEditText));
                            customerId = selectedCustomerId.get();
                            customerIdEditText.get().setText(
                                    dbAdapter.getCustomerById(selectedCustomerId.get()).getName());
                        }
                    }
                }
        );

        // ── Due terms spinner listener ─────────────────────────────────────
        dueTermsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String dueTerms = parent.getItemAtPosition(position).toString();
                if (dueTerms.equals("Select due terms") || dueTerms.isEmpty()) return;

                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

                if (dueTerms.equals("Custom")) {
                    Calendar calendar = Calendar.getInstance();
                    new DatePickerDialog(EstimateDetails.this,
                            (v, selectedYear, selectedMonth, selectedDay) -> {
                                Calendar customCal = Calendar.getInstance();
                                customCal.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
                                customCal.set(Calendar.MILLISECOND, 0);
                                dueDateTimestamp = customCal.getTimeInMillis();
                                dueDateTextView.setText(sdf.format(customCal.getTime()));
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();

                } else if (dueTerms.equals("Due on receipt")) {
                    dueDateTextView.setText(issueDateTextView.getText());
                    dueDateTimestamp = issueDateTimestamp;

                } else if (dueTerms.equals("Next day")) {
                    if (issueDateTimestamp == 0) return;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(issueDateTimestamp);
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    dueDateTimestamp = cal.getTimeInMillis();
                    dueDateTextView.setText(sdf.format(cal.getTime()));

                } else {
                    if (issueDateTimestamp == 0) return;
                    try {
                        int days = Integer.parseInt(dueTerms.replace(" days", "").trim());
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(issueDateTimestamp);
                        cal.add(Calendar.DAY_OF_MONTH, days);
                        dueDateTimestamp = cal.getTimeInMillis();
                        dueDateTextView.setText(sdf.format(cal.getTime()));
                    } catch (NumberFormatException ignored) {}
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ── Action buttons ─────────────────────────────────────────────────
        newEstimateLineButton.setOnClickListener(view -> {
            Intent intent = new Intent(EstimateDetails.this, AddEstimateLine.class);
            intent.putExtra("estimateIdExtra", estimateId.toString());
            startActivity(intent);
        });

        deleteEstimateButton.setOnClickListener(view -> {
            new AlertDialog.Builder(EstimateDetails.this)
                    .setTitle("Delete Confirmation")
                    .setMessage("Do you really want to delete the estimate ?")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        TextInputEditText estimateIdTI = findViewById(R.id.estimateIdEditText_estimate_details);
                        dbAdapter.deleteEstimate(Integer.parseInt(
                                Objects.requireNonNull(estimateIdTI.getText()).toString()));
                        Toast.makeText(getApplicationContext(),
                                "Suppression du devis a été effectuée avec succés", Toast.LENGTH_LONG).show();
                        if (dbAdapter.retrieveEstimates().isEmpty()) dbAdapter.setSeqEstimates();
                        startActivity(new Intent(EstimateDetails.this, Estimates.class));
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                    .show();
        });

        updateEstimateButton.setOnClickListener(view -> {
            new AlertDialog.Builder(EstimateDetails.this)
                    .setTitle("Confirm Update")
                    .setMessage("Do you really want to update the estimate ?")
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        estimate = new Estimate();
                        Spinner statusSpinner    = findViewById(R.id.estimateStatusSpinner);
                        Spinner termsSpinner     = findViewById(R.id.dueTermsSpinner);
                        TextInputEditText discField     = findViewById(R.id.discountEditText);
                        TextInputEditText estIdField    = findViewById(R.id.estimateIdEditText_estimate_details);
                        TextInputEditText afterDiscField= findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                        TextInputEditText locField      = findViewById(R.id.locationEditText_estimate_details);
                        TextInputEditText custField     = findViewById(R.id.customerIdEditText);
                        TextInputEditText vatField      = findViewById(R.id.vatEditText);
                        TextInputEditText totalInclField= findViewById(R.id.totalInclTaxEditText);

                        if (customerId != null) {
                            estimate.setCustomer(customerId);
                            customerExists = true;
                        } else {
                            if (Objects.requireNonNull(custField.getText()).toString().isEmpty()) {
                                estimate.setCustomer(null);
                            } else if (allDigitString(custField.getText().toString())) {
                                Customer customer = dbAdapter.getCustomerById(
                                        Integer.parseInt(custField.getText().toString()));
                                if (customer != null) { estimate.setCustomer(customer.getId()); customerExists = true; }
                                else { customerExists = false; }
                            } else {
                                Integer customer = dbAdapter.getCustomerIdByName(custField.getText().toString());
                                if (customer != null) { estimate.setCustomer(customer); customerExists = true; }
                                else { customerExists = false; }
                            }
                        }

                        if (!customerExists) {
                            Toast.makeText(getApplicationContext(),
                                    "Le client saisi ne corresponds à aucun client dans la base de données",
                                    Toast.LENGTH_LONG).show();
                            customerExists = true;
                            return;
                        }

                        estimate.setId(Integer.parseInt(Objects.requireNonNull(estIdField.getText()).toString()));
                        estimate.setDoneIn(!Objects.requireNonNull(locField.getText()).toString().isEmpty()
                                ? locField.getText().toString() : "");
                        estimate.setIssueDate(issueDateTimestamp != 0 ? issueDateTimestamp : 0L);
                        estimate.setExpirationDate(expirationDateTimestamp != 0 ? expirationDateTimestamp : 0L);
                        estimate.setDueDate(dueDateTimestamp != 0 ? dueDateTimestamp : 0L);
                        estimate.setStatus(!statusSpinner.getSelectedItem().toString().isEmpty()
                                ? statusSpinner.getSelectedItem().toString() : "");
                        estimate.setDueTerms(!termsSpinner.getSelectedItem().toString().isEmpty()
                                ? termsSpinner.getSelectedItem().toString() : "");
                        estimate.setExcludingTaxTotal(dbAdapter.getEstimateExcludingTaxTotal(estimateId));
                        estimate.setDiscount(!Objects.requireNonNull(discField.getText()).toString().isEmpty()
                                ? Float.parseFloat(discField.getText().toString()) : null);
                        estimate.setExcludingTaxTotalAfterDiscount(
                                !Objects.requireNonNull(afterDiscField.getText()).toString().isEmpty()
                                        ? Float.parseFloat(afterDiscField.getText().toString()) : null);
                        estimate.setVat(!Objects.requireNonNull(vatField.getText()).toString().isEmpty()
                                ? Float.parseFloat(vatField.getText().toString()) : null);
                        estimate.setAllTaxIncludedTotal(
                                !Objects.requireNonNull(totalInclField.getText()).toString().isEmpty()
                                        ? Float.parseFloat(totalInclField.getText().toString()) : null);

                        dbAdapter.updateEstimate(estimate);
                        Toast.makeText(getApplicationContext(),
                                "Estimate has been successfully updated", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EstimateDetails.this, Estimates.class));
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                    .show();
        });

        refreshEstimateLinesListButton.setOnClickListener(view -> {
            TextInputEditText estIdField     = findViewById(R.id.estimateIdEditText_estimate_details);
            TextInputEditText totalExclField = findViewById(R.id.totalExclTaxEditText);
            TextInputEditText discField      = findViewById(R.id.discountEditText);
            TextInputEditText afterDiscField = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
            TextInputEditText locField       = findViewById(R.id.locationEditText_estimate_details);
            TextInputEditText vatField       = findViewById(R.id.vatEditText);
            TextInputEditText totalInclField = findViewById(R.id.totalInclTaxEditText);

            estimate = dbAdapter.getEstimateById(Integer.parseInt(
                    Objects.requireNonNull(estIdField.getText()).toString()));
            locField.setText(estimate.getDoneIn());

            SimpleDateFormat sdfRefresh = new SimpleDateFormat(dateFormat, Locale.getDefault());

            issueDateTextView.setText(estimate.getIssueDate() != 0
                    ? sdfRefresh.format(new Date(estimate.getIssueDate()))
                    : getString(R.string.issueDate));
            dueDateTextView.setText(estimate.getDueDate() != 0
                    ? sdfRefresh.format(new Date(estimate.getDueDate()))
                    : getString(R.string.dueDate));
            expirationDateTextView.setText(estimate.getExpirationDate() != 0
                    ? sdfRefresh.format(new Date(estimate.getExpirationDate()))
                    : getString(R.string.expirationDate));

            if (estimate.getDueTerms().isEmpty()) {
                dueTermsSpinner.setSelection(0);
            } else {
                int pos = dueTermsSpinnerAdapter.getPosition(estimate.getDueTerms());
                if (pos >= 0) {
                    dueTermsSpinner.setSelection(pos);
                } else {
                    dueTermsSpinnerAdapter.add(estimate.getDueTerms());
                    dueTermsSpinnerAdapter.notifyDataSetChanged();
                    dueTermsSpinner.setSelection(dueTermsSpinnerAdapter.getPosition(estimate.getDueTerms()));
                }
            }

            if (estimate.getStatus().isEmpty()) {
                estimateStatusSpinner.setSelection(0);
            } else {
                switch (estimate.getStatus()) {
                    case "Pending":   estimateStatusSpinner.setSelection(1); break;
                    case "Approved":  estimateStatusSpinner.setSelection(2); break;
                    case "Cancelled": estimateStatusSpinner.setSelection(3); break;
                }
            }

            formattedTotalExcludingTax = BigDecimal.valueOf(estimate.getExcludingTaxTotal()).toPlainString();
            totalExclField.setText(formattedTotalExcludingTax);
            discField.setText(estimate.getDiscount().toString());
            formattedTotalAfterDiscount = BigDecimal.valueOf(
                    estimate.getExcludingTaxTotalAfterDiscount()).toPlainString();
            afterDiscField.setText(formattedTotalAfterDiscount);
            vatField.setText(estimate.getVat().toString());
            formattedTotalAllTaxIncluded = BigDecimal.valueOf(
                    estimate.getAllTaxIncludedTotal()).toPlainString();
            totalInclField.setText(formattedTotalAllTaxIncluded);

            ArrayList<EstimateLine> refreshedLines = db.searchEstimateLines(
                    Integer.parseInt(estIdField.getText().toString()));

            ConstraintLayout.LayoutParams rp = (ConstraintLayout.LayoutParams)
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.getLayoutParams();
            ConstraintLayout.LayoutParams nlp = (ConstraintLayout.LayoutParams)
                    noEstimateLinesTextView.getLayoutParams();
            ConstraintLayout.LayoutParams bp = (ConstraintLayout.LayoutParams)
                    activityEstimateDetailsBinding.newEstimateLineButton.getLayoutParams();

            if (!refreshedLines.isEmpty()) {
                noEstimateLinesTextView.setVisibility(View.GONE);
                activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.VISIBLE);
                activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutManager(
                        new LinearLayoutManager(getApplicationContext()));
                activityEstimateDetailsBinding.estimateLinesRecyclerView.setAdapter(
                        new EstimateLinesListAdapter(EstimateDetails.this, refreshedLines));
                rp.topToBottom = R.id.vatLayout;
                activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutParams(rp);
                bp.topToBottom = R.id.estimateLinesRecyclerView;
                activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(bp);
            } else {
                noEstimateLinesTextView.setVisibility(View.VISIBLE);
                activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.GONE);
                nlp.topToBottom = R.id.vatLayout;
                noEstimateLinesTextView.setLayoutParams(nlp);
                bp.topToBottom = R.id.noEstimateLinesTextView;
                activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(bp);
            }
        });

        // ── Discount TextWatcher ───────────────────────────────────────────
        discountEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalExclField = findViewById(R.id.totalExclTaxEditText);
                TextInputEditText afterDiscField = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText vatField       = findViewById(R.id.vatEditText);
                TextInputEditText totalInclField = findViewById(R.id.totalInclTaxEditText);
                String discount = s.toString();
                try {
                    float totalExcl = Float.parseFloat(
                            Objects.requireNonNull(totalExclField.getText()).toString());
                    float vat = Float.parseFloat(
                            Objects.requireNonNull(vatField.getText()).toString());
                    float totalAfterDisc = !discount.isEmpty()
                            ? totalExcl - totalExcl * Float.parseFloat(discount) / 100
                            : totalExcl;
                    float totalIncl = totalAfterDisc + totalAfterDisc * vat / 100;
                    formattedTotalAfterDiscount  = new BigDecimal(totalAfterDisc).toPlainString();
                    formattedTotalAllTaxIncluded = new BigDecimal(totalIncl).toPlainString();
                    afterDiscField.setText(formattedTotalAfterDiscount);
                    totalInclField.setText(formattedTotalAllTaxIncluded);
                } catch (NumberFormatException ignored) {}
            }
        });

        // ── VAT TextWatcher ────────────────────────────────────────────────
        vatEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText afterDiscField = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText totalInclField = findViewById(R.id.totalInclTaxEditText);
                String vat = s.toString();
                try {
                    float totalAfterDisc = Float.parseFloat(
                            Objects.requireNonNull(afterDiscField.getText()).toString());
                    float totalIncl = vat.isEmpty()
                            ? totalAfterDisc
                            : totalAfterDisc + totalAfterDisc * Float.parseFloat(vat) / 100;
                    formattedTotalAllTaxIncluded = new BigDecimal(totalIncl).toPlainString();
                    totalInclField.setText(formattedTotalAllTaxIncluded);
                } catch (NumberFormatException ignored) {}
            }
        });

        // ── Date picker click listeners ────────────────────────────────────
        issueDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(EstimateDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, issueDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(EstimateDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, expirationDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dueDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(EstimateDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, dueDateSetListener,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        // ── Date set listeners ─────────────────────────────────────────────
        expirationDateSetListener = (picker, year, month, day) -> {
            String currentValue = expirationDateTextView.getText().toString();
            previousExpirationDateTimestamp = expirationDateTimestamp;

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long expirationTimestamp = cal.getTimeInMillis();

            SimpleDateFormat sdfPick = new SimpleDateFormat(dateFormat, Locale.getDefault());
            expirationDateValue = sdfPick.format(cal.getTime());
            expirationDateTextView.setText(expirationDateValue);

            if (issueDateTimestamp > 0) {
                long daysBetween = (expirationTimestamp - issueDateTimestamp) / (1000 * 60 * 60 * 24);
                if (daysBetween <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    if (currentValue.equals(getString(R.string.expirationDate))) {
                        expirationDateTextView.setText(R.string.expirationDate);
                        expirationDateValue = "";
                        expirationDateTimestamp = 0;
                    } else {
                        expirationDateTextView.setText(currentValue);
                        expirationDateValue = currentValue;
                        expirationDateTimestamp = previousExpirationDateTimestamp;
                    }
                    return;
                }
            }
            expirationDateTimestamp = expirationTimestamp;
            estimate.setExpirationDate(expirationDateTimestamp);
        };

        issueDateSetListener = (picker, year, month, day) -> {
            String currentValue = issueDateTextView.getText().toString();
            previousIssueDateTimestamp = issueDateTimestamp;

            Calendar issueCal = Calendar.getInstance();
            issueCal.set(year, month, day, 0, 0, 0);
            issueCal.set(Calendar.MILLISECOND, 0);
            long issueTimestamp = issueCal.getTimeInMillis();

            SimpleDateFormat sdfPick = new SimpleDateFormat(dateFormat, Locale.getDefault());
            issueDateValue = sdfPick.format(issueCal.getTime());
            issueDateTextView.setText(issueDateValue);

            // Validate against expiration
            if (expirationDateTimestamp > 0) {
                long daysBetween = (expirationDateTimestamp - issueTimestamp) / (1000 * 60 * 60 * 24);
                if (daysBetween <= 0) {
                    Toast.makeText(getApplicationContext(),
                            "Expiration date should be after the issue date", Toast.LENGTH_SHORT).show();
                    if (currentValue.equals(getString(R.string.issueDate))) {
                        issueDateTextView.setText(R.string.issueDate);
                        issueDateValue = ""; issueDateTimestamp = 0;
                    } else {
                        issueDateTextView.setText(currentValue);
                        issueDateValue = currentValue;
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
                    if (currentValue.equals(getString(R.string.issueDate))) {
                        issueDateTextView.setText(R.string.issueDate);
                        issueDateValue = ""; issueDateTimestamp = 0;
                    } else {
                        issueDateTextView.setText(currentValue);
                        issueDateValue = currentValue;
                        issueDateTimestamp = previousIssueDateTimestamp;
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

            // ── Auto-calculate due date from currently selected due terms ──
            // Only runs when there is no existing due date set
            if (dueDateTimestamp == 0) {
                String currentDueTerms = dueTermsSpinner.getSelectedItem() != null
                        ? dueTermsSpinner.getSelectedItem().toString() : "";

                if (!currentDueTerms.isEmpty()
                        && !currentDueTerms.equals("Select due terms")
                        && !currentDueTerms.equals("Custom")) {

                    Calendar dueCal = Calendar.getInstance();
                    dueCal.setTimeInMillis(issueTimestamp);
                    long autoDueTimestamp = 0;

                    if (currentDueTerms.equals("Due on receipt")) {
                        autoDueTimestamp = issueTimestamp;
                    } else if (currentDueTerms.equals("Next day")) {
                        dueCal.add(Calendar.DAY_OF_MONTH, 1);
                        autoDueTimestamp = dueCal.getTimeInMillis();
                    } else {
                        try {
                            int days = Integer.parseInt(
                                    currentDueTerms.replace(" days", "").trim());
                            dueCal.add(Calendar.DAY_OF_MONTH, days);
                            autoDueTimestamp = dueCal.getTimeInMillis();
                        } catch (NumberFormatException ignored) {}
                    }

                    if (autoDueTimestamp != 0) {
                        dueDateTimestamp = autoDueTimestamp;
                        estimate.setDueDate(dueDateTimestamp);
                        dueDateValue = sdfPick.format(new Date(dueDateTimestamp));
                        dueDateTextView.setText(dueDateValue);
                    }
                }
            }

            issueDateTimestamp = issueTimestamp;
            estimate.setIssueDate(issueDateTimestamp);
        };

        dueDateSetListener = (picker, year, month, day) -> {
            String currentValue = dueDateTextView.getText().toString();
            previousDueDateTimestamp = dueDateTimestamp;

            Calendar dueCal = Calendar.getInstance();
            dueCal.set(year, month, day, 0, 0, 0);
            dueCal.set(Calendar.MILLISECOND, 0);
            long dueTimestamp = dueCal.getTimeInMillis();

            SimpleDateFormat sdfPick = new SimpleDateFormat(dateFormat, Locale.getDefault());
            dueDateValue = sdfPick.format(dueCal.getTime());
            dueDateTextView.setText(dueDateValue);

            if (issueDateTimestamp > 0) {
                long daysBetween = (dueTimestamp - issueDateTimestamp) / (1000 * 60 * 60 * 24);
                if (daysBetween < 0) {
                    Toast.makeText(getApplicationContext(),
                            "Due date should be after the issue date", Toast.LENGTH_SHORT).show();
                    if (currentValue.equals(getString(R.string.dueDate))) {
                        dueDateTextView.setText(R.string.dueDate);
                        dueDateValue = ""; dueDateTimestamp = 0;
                    } else {
                        dueDateTextView.setText(currentValue);
                        dueDateValue = currentValue;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return false; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preview_estimate) {
            Toast.makeText(this, "Preview clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startActivityForResult() {
        activityResultLauncher.launch(new Intent(EstimateDetails.this, Customers.class));
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