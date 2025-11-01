package com.example.metalconstructionsestimates.modules.estimates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.metalconstructionsestimates.models.Customer;
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
import java.text.ParseException;
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

    private DatePickerDialog.OnDateSetListener expirationDateSetListner, issueDateSetListener, dueDateSetListener;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    ActivityEstimateDetailsBinding activityEstimateDetailsBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEstimateDetailsBinding = ActivityEstimateDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityEstimateDetailsBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String estimateIdExtra = getIntent().getStringExtra("estimateIdExtra");
        assert estimateIdExtra != null;
        estimateId = Integer.parseInt(estimateIdExtra);
        dbAdapter = new DBAdapter(getApplicationContext());
        estimate = dbAdapter.getEstimateById(estimateId);

        Spinner estimateStatusSpinner = findViewById(R.id.estimateStatusSpinner);
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

        issueDateTextView = findViewById(R.id.issueDateValue);
        expirationDateTextView = findViewById(R.id.expirationDateValue);
        dueDateTextView = findViewById(R.id.dueDateValue);
        TextInputEditText totalExclTaxEditText = findViewById(R.id.totalExclTaxEditText);
        TextInputEditText discountEditText = findViewById(R.id.discountEditText);
        TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
        TextInputEditText locationEditText = findViewById(R.id.locationEditText_estimate_details);
        AtomicReference<TextInputEditText> customerIdEditText = new AtomicReference<>(findViewById(R.id.customerIdEditText));
        Button selectCustomerButton = findViewById(R.id.selectCustomerButton);
        TextInputEditText vatEditText = findViewById(R.id.vatEditText);
        TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);
        TextInputEditText estimateIdEditText = findViewById(R.id.estimateIdEditText_estimate_details);

        Button newEstimateLineButton = findViewById(R.id.newEstimateLineButton);
        Button updateEstimateButton = findViewById(R.id.updateButton_estimate_details);
        Button refreshEstimateLinesListButton = findViewById(R.id.refreshEstimateLinesListButton);
        Button deleteEstimateButton = findViewById(R.id.deleteEstimateButton);

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
            // Show RecyclerView, hide "no lines" TextView
            noEstimateLinesTextView.setVisibility(View.GONE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.VISIBLE);

            activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            EstimateLinesListAdapter adapter = new EstimateLinesListAdapter(this, estimateLinesList);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setAdapter(adapter);

            // recyclerView constraints: Top to vatLayout
            recyclerParams.topToBottom = R.id.vatLayout;
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutParams(recyclerParams);

            // button constraints: Top to recyclerView
            buttonParams.topToBottom = R.id.estimateLinesRecyclerView;
            activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);

        } else {
            // Show "no lines" TextView, hide RecyclerView
            noEstimateLinesTextView.setVisibility(View.VISIBLE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.GONE);

            // noLinesTextView constraints: Top to vatLayout
            noLinesParams.topToBottom = R.id.vatLayout;
            noEstimateLinesTextView.setLayoutParams(noLinesParams);

            // button constraints: Top to noEstimateLinesTextView
            buttonParams.topToBottom = R.id.noEstimateLinesTextView;
            activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);
        }

        if(estimate.getIssueDate().isEmpty()){
            issueDateTextView.setText(R.string.issueDate);
            issueDateValue = "";
        }
        else{
            issueDateValue = estimate.getIssueDate();
            issueDateTextView.setText(estimate.getIssueDate());
        }

        if (estimate.getDueDate().isEmpty()) {
            dueDateTextView.setText(R.string.dueDate);
            dueDateValue = "";
        }
        else{
            dueDateValue = estimate.getDueDate();
            dueDateTextView.setText(estimate.getDueDate());
        }

        if (estimate.getDueTerms().isEmpty()) {
            dueTermsSpinner.setSelection(0);
        }
        else{
            int position = dueTermsSpinnerAdapter.getPosition(estimate.getDueTerms());

            if (position >= 0) {
                // ✅ Value exists in the spinner list
                dueTermsSpinner.setSelection(position);
            } else {
                // ❌ Value not found → add it dynamically
                dueTermsSpinnerAdapter.add(estimate.getDueTerms());
                dueTermsSpinnerAdapter.notifyDataSetChanged();

                // Select the newly added value
                int newPosition = dueTermsSpinnerAdapter.getPosition(estimate.getDueTerms());
                dueTermsSpinner.setSelection(newPosition);
            }
        }

        if(estimate.getStatus().isEmpty()){
            estimateStatusSpinner.setSelection(0);
        }
        else{
            switch(estimate.getStatus()){
                case "Pending":
                    estimateStatusSpinner.setSelection(1);
                    break;
                case "Approved":
                    estimateStatusSpinner.setSelection(2);
                    break;
                case "Cancelled":
                    estimateStatusSpinner.setSelection(3);
                    break;
            }
        }

        if(!estimate.getExpirationDate().isEmpty()){
            expirationDateTextView.setText(estimate.getExpirationDate());
        }
        else{
            expirationDateTextView.setText(R.string.expirationDate);
        }

        estimateIdEditText.setText(String.format(estimate.getId().toString()));
        locationEditText.setText(estimate.getDoneIn());
        customerId = estimate.getCustomer();

        if(estimate.getCustomer() == null){
            customerIdEditText.get().setText("");
        }
        else{
            String customerName = dbAdapter.getCustomerById(estimate.getCustomer()).getName();
            customerIdEditText.get().setText(String.format(customerName));
        }

        if(estimate.getExcludingTaxTotal() == null){
            totalExclTaxEditText.setText("");
        }
        else{
            formattedTotalExcludingTax = BigDecimal.valueOf(estimate.getExcludingTaxTotal()).toPlainString();
            totalExclTaxEditText.setText(formattedTotalExcludingTax);
        }

        totalExclTaxEditText.setEnabled(false);

        if(estimate.getDiscount() == null){
            discountEditText.setText("");
        }
        else{
            discountEditText.setText(String.format(estimate.getDiscount().toString()));
        }

        if(estimate.getExcludingTaxTotalAfterDiscount() == null){
            totalAfterDiscountEditText.setText("");
        }
        else{
            BigDecimal totalAfterDiscount = BigDecimal.valueOf(estimate.getExcludingTaxTotalAfterDiscount()).setScale(0, RoundingMode.DOWN);
            formattedTotalAfterDiscount = totalAfterDiscount.toPlainString();
            totalAfterDiscountEditText.setText(formattedTotalAfterDiscount);
        }

        totalAfterDiscountEditText.setEnabled(false);

        if(estimate.getVat() == null){
            vatEditText.setText("");
        }
        else{
            vatEditText.setText(String.format(estimate.getVat().toString()));
        }

        if(estimate.getAllTaxIncludedTotal() == null){
            totalAllTaxIncludedEditText.setText("");
        }
        else{
            BigDecimal totalAllTaxIncluded = BigDecimal.valueOf(estimate.getAllTaxIncludedTotal()).setScale(0, RoundingMode.DOWN);
            formattedTotalAllTaxIncluded = totalAllTaxIncluded.toPlainString();
            totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);
        }

        totalAllTaxIncludedEditText.setEnabled(false);

        selectCustomerButton.setOnClickListener(v -> startActivityForResult());

        AtomicInteger selectedCustomerId = new AtomicInteger(-1);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                String customerIdExtraResult = extras.getString("customerIdExtraResult");
                                assert customerIdExtraResult != null;
                                selectedCustomerId.set(Integer.parseInt(customerIdExtraResult));
                                customerIdEditText.set(findViewById(R.id.customerIdEditText));
                                customerId = selectedCustomerId.get();
                                String customerName = dbAdapter.getCustomerById(selectedCustomerId.get()).getName();
                                customerIdEditText.get().setText(customerName);
                            }
                        }
                    }
                }
        );

        dueTermsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String dueTerms = parent.getItemAtPosition(position).toString();
                if(dueTerms.equals("Select due terms")){
                    dueTermsSpinner.setSelection(0);
                    return;
                }
                if(dueTerms.isEmpty()){
                    return;
                }
                if (dueTerms.equals("Custom")) {
                    final Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            EstimateDetails.this, // use activity context
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                    // Format date as yyyy-MM-dd (or as you prefer)
                                    String selectedDate = String.format("%04d-%02d-%02d",
                                            selectedYear,
                                            selectedMonth + 1, // month is 0-based
                                            selectedDay);
                                    dueDateTextView.setText(selectedDate);
                                }
                            },
                            year, month, day);

                    datePickerDialog.show();
                }
                else if(dueTerms.equals("Due on receipt")){
                    dueDateTextView.setText(issueDateTextView.getText());
                }
                else if(dueTerms.equals("Next day")){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = null;
                    // Parse the input date
                    try {
                        date = sdf.parse(issueDateTextView.getText().toString());
                        // Now you can work with 'date'
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Handle the error gracefully, maybe show a Toast
                    }

                    if (date != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        dueDateTextView.setText(sdf.format(calendar.getTime()));
                    }
                }
                else{
                    dueTerms = dueTerms.replace(" days", "");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    Date date = null;
                    // Parse the input date
                    try {
                        date = sdf.parse(issueDateTextView.getText().toString());
                        // Now you can work with 'date'
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Handle the error gracefully, maybe show a Toast
                    }

                    // Use Calendar to add days
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(dueTerms));
                    dueDateTextView.setText(sdf.format(calendar.getTime()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newEstimateLineButton.setOnClickListener(view -> {
            Intent intent = new Intent(EstimateDetails.this, AddEstimateLine.class);
            intent.putExtra("estimateIdExtra", estimateId.toString());
            startActivity(intent);
        });

        deleteEstimateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(EstimateDetails.this);
                alertDelete.setTitle("Confirmation de suppression");
                alertDelete.setMessage("Voulez-vous vraiment supprimer le devis?");
                alertDelete.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        TextInputEditText estimateIdTextInputEditText = findViewById(R.id.estimateIdEditText_estimate_details);
                        dbAdapter.deleteEstimate(Integer.parseInt(estimateIdTextInputEditText.getText().toString()));
                        Toast deleteSuccessToast = Toast.makeText(getApplicationContext(), "Suppression du devis a été effectuée avec succés", Toast.LENGTH_LONG);
                        deleteSuccessToast.show();

                        if(dbAdapter.retrieveEstimates().isEmpty()){
                            dbAdapter.setSeqEstimates();
                        }

                        Intent intent = new Intent(EstimateDetails.this, Estimates.class);
                        startActivity(intent);
                    }
                });
                alertDelete.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alertDelete.show();
            }
        });

        updateEstimateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertUpdate = new AlertDialog.Builder(EstimateDetails.this);
                alertUpdate.setTitle("Confirm Update");
                alertUpdate.setMessage("Do you really want to update the estimate ?");
                alertUpdate.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        estimate = new Estimate();
                        TextView issueDate = findViewById(R.id.issueDateValue);
                        TextView expirationDate = findViewById(R.id.expirationDateValue);
                        TextView dueDate = findViewById(R.id.dueDateValue);
                        Spinner estimateStatusSpinner = (Spinner) findViewById(R.id.estimateStatusSpinner);
                        Spinner dueTermsSpinner = (Spinner) findViewById(R.id.dueTermsSpinner);
                        TextInputEditText discountEditText = findViewById(R.id.discountEditText);
                        TextInputEditText estimateIdEditText = findViewById(R.id.estimateIdEditText_estimate_details);
                        TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                        TextInputEditText locationEditText = findViewById(R.id.locationEditText_estimate_details);
                        TextInputEditText customerIdEditText = findViewById(R.id.customerIdEditText);
                        TextInputEditText vatEditText = findViewById(R.id.vatEditText);
                        TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);

                        if (customerId != null) {
                            estimate.setCustomer(customerId);
                            customerExists = true;
                        } else {
                            if (Objects.requireNonNull(customerIdEditText.getText()).toString().isEmpty()) {
                                estimate.setCustomer(null);
                            } else {
                                if (allDigitString(customerIdEditText.getText().toString())) {
                                    Customer customer = dbAdapter.getCustomerById(Integer.parseInt(customerIdEditText.getText().toString()));
                                    if (customer != null) {
                                        estimate.setCustomer(customer.getId());
                                        customerExists = true;
                                    } else {
                                        customerExists = false;
                                    }
                                } else {
                                    Integer customer = dbAdapter.getCustomerIdByName(customerIdEditText.getText().toString());
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

                        estimate.setId(Integer.parseInt(estimateIdEditText.getText().toString()));

                        if (!locationEditText.getText().toString().isEmpty()) {
                            estimate.setDoneIn(locationEditText.getText().toString());
                        } else {
                            estimate.setDoneIn("");
                        }

                        if (!issueDate.getText().toString().isEmpty()) {
                            estimate.setIssueDate(issueDate.getText().toString());
                        } else {
                            estimate.setIssueDate("");
                        }

                        if (!expirationDate.getText().toString().isEmpty()) {
                            estimate.setExpirationDate(expirationDate.getText().toString());
                        } else {
                            estimate.setExpirationDate("");
                        }

                        if(dueDate.getText().toString().isEmpty()){
                            estimate.setDueDate("");
                        }
                        else{
                            estimate.setDueDate(dueDate.getText().toString());
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

                        Float totalExcludingTax = 0.0f;
                        totalExcludingTax = dbAdapter.getEstimateExcludingTaxTotal(estimateId);
                        estimate.setExcludingTaxTotal(totalExcludingTax);

                        if (!Objects.requireNonNull(discountEditText.getText()).toString().isEmpty()) {
                            estimate.setDiscount(Float.parseFloat(discountEditText.getText().toString()));
                        } else {
                            estimate.setDiscount(null);
                        }

                        if (!Objects.requireNonNull(totalAfterDiscountEditText.getText()).toString().isEmpty()) {
                            estimate.setExcludingTaxTotalAfterDiscount(Float.parseFloat(totalAfterDiscountEditText.getText().toString()));
                        } else {
                            estimate.setExcludingTaxTotalAfterDiscount(null);
                        }

                        if (!Objects.requireNonNull(vatEditText.getText()).toString().isEmpty()) {
                            estimate.setVat(Float.parseFloat(vatEditText.getText().toString()));
                        } else {
                            estimate.setVat(null);
                        }

                        if (!Objects.requireNonNull(totalAllTaxIncludedEditText.getText()).toString().isEmpty()) {
                            estimate.setAllTaxIncludedTotal(Float.parseFloat(totalAllTaxIncludedEditText.getText().toString()));
                        } else {
                            estimate.setAllTaxIncludedTotal(null);
                        }

                        dbAdapter.updateEstimate(estimate);
                        Toast updateSuccessToast = Toast.makeText(getApplicationContext(), "Estimate has been successfully updated", Toast.LENGTH_LONG);
                        updateSuccessToast.show();
                        Intent intent = new Intent(EstimateDetails.this, Estimates.class);
                        startActivity(intent);
                    }
                });

                alertUpdate.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alertUpdate.show();
            }
        });

        refreshEstimateLinesListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView issueDateTextView = findViewById(R.id.issueDateValue);
                TextView expirationDateTextView = findViewById(R.id.expirationDateValue);
                TextView dueDateTextView = findViewById(R.id.dueDateValue);
                TextInputEditText totalExclTaxEditText = findViewById(R.id.totalExclTaxEditText);
                TextInputEditText discountEditText = findViewById(R.id.discountEditText);
                TextInputEditText estimateIdEditText = findViewById(R.id.estimateIdEditText_estimate_details);
                TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText locationEditText = findViewById(R.id.locationEditText_estimate_details);
                TextInputEditText vatEditText = findViewById(R.id.vatEditText);
                TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);
                estimate = dbAdapter.getEstimateById(Integer.parseInt(estimateIdEditText.getText().toString()));
                locationEditText.setText(estimate.getDoneIn());

                if(estimate.getIssueDate().isEmpty()){
                    issueDateTextView.setText(R.string.issueDate);
                }
                else{
                    issueDateTextView.setText(estimate.getIssueDate());
                }

                if (estimate.getDueDate().isEmpty()) {
                    dueDateTextView.setText(R.string.dueDate);
                }
                else{
                    dueDateTextView.setText(estimate.getDueDate());
                }

                if (estimate.getDueTerms().isEmpty()) {
                    dueTermsSpinner.setSelection(0);
                }
                else{
                    int position = dueTermsSpinnerAdapter.getPosition(estimate.getDueTerms());

                    if (position >= 0) {
                        // ✅ Value exists in the spinner list
                        dueTermsSpinner.setSelection(position);
                    } else {
                        // ❌ Value not found → add it dynamically
                        dueTermsSpinnerAdapter.add(estimate.getDueTerms());
                        dueTermsSpinnerAdapter.notifyDataSetChanged();

                        // Select the newly added value
                        int newPosition = dueTermsSpinnerAdapter.getPosition(estimate.getDueTerms());
                        dueTermsSpinner.setSelection(newPosition);
                    }
                }

                if(estimate.getStatus().isEmpty()){
                    estimateStatusSpinner.setSelection(0);
                }
                else{
                    switch(estimate.getStatus()){
                        case "Pending":
                            estimateStatusSpinner.setSelection(1);
                            break;
                        case "Approved":
                            estimateStatusSpinner.setSelection(2);
                            break;
                        case "Cancelled":
                            estimateStatusSpinner.setSelection(3);
                            break;
                    }
                }

                if(!estimate.getExpirationDate().isEmpty()){
                    expirationDateTextView.setText(estimate.getExpirationDate());
                }
                else{
                    expirationDateTextView.setText(R.string.expirationDate);
                }

                formattedTotalExcludingTax = BigDecimal.valueOf(estimate.getExcludingTaxTotal()).toPlainString();
                totalExclTaxEditText.setText(formattedTotalExcludingTax);
                discountEditText.setText(String.format(estimate.getDiscount().toString()));
                formattedTotalAfterDiscount = BigDecimal.valueOf(estimate.getExcludingTaxTotalAfterDiscount()).toPlainString();
                totalAfterDiscountEditText.setText(formattedTotalAfterDiscount);
                vatEditText.setText(String.format(estimate.getVat().toString()));
                formattedTotalAllTaxIncluded = BigDecimal.valueOf(estimate.getAllTaxIncludedTotal()).toPlainString();
                totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);
                ArrayList<EstimateLine> estimateLinesList = db.searchEstimateLines(Integer.parseInt(estimateIdEditText.getText().toString()));

                ConstraintLayout.LayoutParams recyclerParams = (ConstraintLayout.LayoutParams)
                        activityEstimateDetailsBinding.estimateLinesRecyclerView.getLayoutParams();

                ConstraintLayout.LayoutParams noLinesParams = (ConstraintLayout.LayoutParams)
                        noEstimateLinesTextView.getLayoutParams();

                ConstraintLayout.LayoutParams buttonParams = (ConstraintLayout.LayoutParams)
                        activityEstimateDetailsBinding.newEstimateLineButton.getLayoutParams();

                if (!estimateLinesList.isEmpty()) {
                    // Show RecyclerView, hide "no lines" TextView
                    noEstimateLinesTextView.setVisibility(View.GONE);
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.VISIBLE);

                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    EstimateLinesListAdapter adapter = new EstimateLinesListAdapter(EstimateDetails.this, estimateLinesList);
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setAdapter(adapter);

                    // recyclerView constraints: Top to vatLayout
                    recyclerParams.topToBottom = R.id.vatLayout;
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutParams(recyclerParams);

                    // button constraints: Top to recyclerView
                    buttonParams.topToBottom = R.id.estimateLinesRecyclerView;
                    activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);

                } else {
                    // Show "no lines" TextView, hide RecyclerView
                    noEstimateLinesTextView.setVisibility(View.VISIBLE);
                    activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.GONE);

                    // noLinesTextView constraints: Top to vatLayout
                    noLinesParams.topToBottom = R.id.vatLayout;
                    noEstimateLinesTextView.setLayoutParams(noLinesParams);

                    // button constraints: Top to noEstimateLinesTextView
                    buttonParams.topToBottom = R.id.noEstimateLinesTextView;
                    activityEstimateDetailsBinding.newEstimateLineButton.setLayoutParams(buttonParams);
                }

            }
        });

        discountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalExclTaxEditText = findViewById(R.id.totalExclTaxEditText);
                TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText vatEditText = findViewById(R.id.vatEditText);
                TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);

                String discount = s.toString();
                Float totalExcludingTax, totalExcludingTaxAfterDiscount, vat, totalAllTaxIncluded;
                totalExcludingTax = Float.parseFloat(Objects.requireNonNull(totalExclTaxEditText.getText()).toString());
                vat = Float.parseFloat(Objects.requireNonNull(vatEditText.getText()).toString());
                if (!discount.isEmpty()) {
                    totalExcludingTaxAfterDiscount = totalExcludingTax - totalExcludingTax * Float.parseFloat(discount) / 100;
                    formattedTotalAfterDiscount = new BigDecimal(totalExcludingTaxAfterDiscount).toPlainString();
                    totalAfterDiscountEditText.setText(formattedTotalAfterDiscount);
                    if (!vat.toString().isEmpty()) {
                        totalAllTaxIncluded = totalExcludingTaxAfterDiscount + totalExcludingTaxAfterDiscount * vat / 100;
                    } else {
                        totalAllTaxIncluded = totalExcludingTaxAfterDiscount;
                    }

                    formattedTotalAllTaxIncluded = new BigDecimal(totalAllTaxIncluded).toPlainString();
                    totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);


                } else {
                    totalAfterDiscountEditText.setText(String.format(Locale.getDefault(), "%s", totalExcludingTax));
                    if (!vat.toString().isEmpty()) {
                        totalAllTaxIncluded = totalExcludingTax + totalExcludingTax * vat / 100;
                    } else {
                        totalAllTaxIncluded = totalExcludingTax;
                    }
                    formattedTotalAllTaxIncluded = new BigDecimal(totalAllTaxIncluded).toPlainString();
                    totalAllTaxIncludedEditText.setText(formattedTotalAllTaxIncluded);
                }
            }
        });

        vatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalExcludingTaxAfterDiscountTextInputEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText totalAllTaxIncludedTextInputEditText = findViewById(R.id.totalInclTaxEditText);
                String vat = s.toString();
                Float totalExcludingTaxAfterDiscount, totalAllTaxIncluded;
                totalExcludingTaxAfterDiscount = Float.parseFloat(totalExcludingTaxAfterDiscountTextInputEditText.getText().toString());
                if (!vat.isEmpty()) {
                    totalAllTaxIncluded = totalExcludingTaxAfterDiscount + totalExcludingTaxAfterDiscount * Float.parseFloat(vat) / 100;
                } else {
                    totalAllTaxIncluded = totalExcludingTaxAfterDiscount;
                }

                formattedTotalAllTaxIncluded = new BigDecimal(totalAllTaxIncluded).toPlainString();
                totalAllTaxIncludedTextInputEditText.setText(formattedTotalAllTaxIncluded);
            }
        });

        issueDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    EstimateDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    issueDateSetListener,
                    year, month, day
            );
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    EstimateDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    expirationDateSetListner,
                    year, month, day
            );
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dueDateTextView.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    EstimateDetails.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dueDateSetListener,
                    year, month, day
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateSetListner = (picker, year, month, day) -> {
            month = month + 1;
            expirationDateValue = year + "-" + month + "-" + day;
            expirationDateTextView.setText(expirationDateValue);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());

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
                    TextView expirationDateTextView = (TextView) findViewById(R.id.expirationDateValue);
                    expirationDateTextView.setText(R.string.expirationDate);
                    expirationDateValue = "";
                }
            }
        };

        issueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            issueDateValue = year + "-" + month + "-" + day;
            issueDateTextView.setText(issueDateValue);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());

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
                    TextView issueDateTextView = (TextView) findViewById(R.id.issueDateValue);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.estimate_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preview_estimate) {
            // TODO: launch your preview action here
            Toast.makeText(this, "Preview clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void startActivityForResult() {
        Intent intent = new Intent(EstimateDetails.this, Customers.class);
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