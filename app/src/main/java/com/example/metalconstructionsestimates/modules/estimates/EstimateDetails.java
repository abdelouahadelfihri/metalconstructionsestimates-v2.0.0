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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.EstimateLinesListAdapter;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class EstimateDetails extends AppCompatActivity {

    Integer estimateId;
    Integer customerId;

    String formattedTotalExcludingTax;
    String formattedTotalAfterDiscount;
    String formattedTotalAllTaxIncluded;

    Estimate estimate;

    DBAdapter dbAdapter;
    TextView expirationDateTextView,issueDateTextView, dueDateTextView;
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
        ArrayAdapter<CharSequence> dueTermsSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.due_terms, android.R.layout.simple_spinner_item);
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


        if(!estimate.getExpirationDate().isEmpty()){
            expirationDateTextView.setText(estimate.getExpirationDate());
        }
        else{
            expirationDateTextView.setText("");
        }
        Log.i("dueDate", estimate.getDueDate());
        if (estimate.getDueDate() == null || estimate.getDueDate().isEmpty()) {
            dueDateTextView.setText(R.string.dueDate);
        }
        else{
            dueDateTextView.setText(estimate.getDueDate());
        }

        if (estimate.getDueTerms() == null || estimate.getDueTerms().isEmpty()) {
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

        if(estimate.getStatus() == null || estimate.getStatus().isEmpty()){
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

        if(!estimate.getIssueDate().isEmpty()){
            issueDateTextView.setText(estimate.getIssueDate());
        }
        else{
            issueDateTextView.setText("");
        }

        estimateIdEditText.setText(String.format(estimate.getId().toString()));
        locationEditText.setText(estimate.getDoneIn());
        customerId = estimate.getCustomer();

        if(estimate.getCustomer() == null){
            customerIdEditText.get().setText("");
        }
        else{
            customerIdEditText.get().setText(String.format(estimate.getCustomer().toString()));
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
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                        calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(dueTerms));
                        dueDateTextView.setText(sdf.format(calendar.getTime()));
                    }

                }
                else{
                    dueTerms = dueTerms.replace(" days", "");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
                alertUpdate.setTitle("Update Confirmation");
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
                        Log.i("status", estimateStatusSpinner.getSelectedItem().toString());

                        if(estimateStatusSpinner.getSelectedItem().toString().isEmpty()){
                            estimate.setStatus("");
                        }
                        else{
                            estimate.setStatus(estimateStatusSpinner.getSelectedItem().toString());
                        }
                        Log.i("dueTerms", dueTermsSpinner.getSelectedItem().toString());
                        if(dueTermsSpinner.getSelectedItem().toString().isEmpty()){
                            estimate.setDueTerms("");
                        }
                        else{
                            estimate.setDueTerms(dueTermsSpinner.getSelectedItem().toString());
                        }

                        Float totalExcludingTax = 0.0f;
                        totalExcludingTax = dbAdapter.getEstimateExcludingTaxTotal(estimateId);
                        estimate.setExcludingTaxTotal(totalExcludingTax);

                        if (!discountEditText.getText().toString().isEmpty()) {
                            estimate.setDiscount(Float.parseFloat(discountEditText.getText().toString()));
                        } else {
                            estimate.setDiscount(null);
                        }

                        if (!totalAfterDiscountEditText.getText().toString().isEmpty()) {
                            estimate.setExcludingTaxTotalAfterDiscount(Float.parseFloat(totalAfterDiscountEditText.getText().toString()));
                        } else {
                            estimate.setExcludingTaxTotalAfterDiscount(null);
                        }

                        if (!vatEditText.getText().toString().isEmpty()) {
                            estimate.setVat(Float.parseFloat(vatEditText.getText().toString()));
                        } else {
                            estimate.setVat(null);
                        }

                        if (!totalAllTaxIncludedEditText.getText().toString().isEmpty()) {
                            estimate.setAllTaxIncludedTotal(Float.parseFloat(totalAllTaxIncludedEditText.getText().toString()));
                        } else {
                            estimate.setAllTaxIncludedTotal(null);
                        }

                        if(dbAdapter.getCustomerById(Integer.parseInt(Objects.requireNonNull(customerIdEditText.getText()).toString())) == null){
                            Toast.makeText(getApplicationContext(), "Customer does not exist", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else{
                            if (!Objects.requireNonNull(customerIdEditText.getText()).toString().isEmpty()) {
                                estimate.setCustomer(Integer.parseInt(customerIdEditText.getText().toString()));
                            } else {
                                estimate.setCustomer(null);
                            }
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
                TextView issueDate = findViewById(R.id.issueDateValue);
                TextView expirationDate = findViewById(R.id.expirationDateValue);
                TextInputEditText totalExclTaxEditText = findViewById(R.id.totalExclTaxEditText);
                TextInputEditText discountEditText = findViewById(R.id.discountEditText);
                TextInputEditText estimateIdEditText = findViewById(R.id.estimateIdEditText_estimate_details);
                TextInputEditText totalAfterDiscountEditText = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
                TextInputEditText locationEditText = findViewById(R.id.locationEditText_estimate_details);
                TextInputEditText vatEditText = findViewById(R.id.vatEditText);
                TextInputEditText totalAllTaxIncludedEditText = findViewById(R.id.totalInclTaxEditText);
                estimate = dbAdapter.getEstimateById(Integer.parseInt(estimateIdEditText.getText().toString()));
                locationEditText.setText(estimate.getDoneIn());
                issueDate.setText(estimate.getIssueDate());
                expirationDate.setText(estimate.getExpirationDate());
                formattedTotalExcludingTax = new BigDecimal(estimate.getExcludingTaxTotal()).toPlainString();
                totalExclTaxEditText.setText(formattedTotalExcludingTax);
                discountEditText.setText(String.format(estimate.getDiscount().toString()));
                formattedTotalAfterDiscount = new BigDecimal(estimate.getExcludingTaxTotalAfterDiscount()).toPlainString();
                totalAfterDiscountEditText.setText(formattedTotalAfterDiscount);
                vatEditText.setText(String.format(estimate.getVat().toString()));
                formattedTotalAllTaxIncluded = new BigDecimal(estimate.getAllTaxIncludedTotal()).toPlainString();
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
                    TextView issueDateTextView = (TextView) findViewById(R.id.issueDateValue);
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
                if(daysBetween < 0){
                    Toast.makeText(getApplicationContext(), "Due date should be after the issue date", Toast.LENGTH_SHORT).show();
                    issueDateTextView.setText(R.string.issueDate);
                    issueDateValue = "";
                }
                else{
                    Boolean daysBetweenInDueTermsSpinner =  false;
                    String daysBetweenString = daysBetween + " days";
                    if(daysBetween == 0){
                        dueTermsSpinner.setSelection(0);
                    }
                    else if(daysBetween == 1){
                        dueTermsSpinner.setSelection(1);
                    }
                    else{
                        for (int i = 0; i < dueTermsSpinner.getAdapter().getCount(); i++) {
                            String item = (String) dueTermsSpinner.getAdapter().getItem(i);
                            if(Objects.equals(item, daysBetweenString)){
                                daysBetweenInDueTermsSpinner = true;
                                dueTermsSpinner.setSelection(i);
                            }
                        }
                        if(!daysBetweenInDueTermsSpinner){
                            if (dueTermsSpinner.getAdapter().getCount() != 20) {
                                String customValue = dueTermsSpinner.getAdapter().getItem(dueTermsSpinner.getAdapter().getCount() - 1).toString();
                                dueTermsSpinnerAdapter.remove(customValue);
                                dueTermsSpinnerAdapter.notifyDataSetChanged();
                            }
                            dueTermsSpinnerAdapter.add(daysBetweenString);
                            dueTermsSpinner.setSelection(dueTermsSpinnerAdapter.getCount());
                            dueTermsSpinnerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

        };

    }
    public void startActivityForResult() {
        Intent intent = new Intent(EstimateDetails.this, Customers.class);
        activityResultLauncher.launch(intent);
    }
}