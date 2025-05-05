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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.EstimateLinesListAdapter;
import com.example.metalconstructionsestimates.databinding.ActivityEstimateDetailsBinding;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.customviews.estimates.EstimateCustomerIdSelectCustomer;
import com.example.metalconstructionsestimates.customviews.estimates.EstimateLocationAmountPaid;
import com.example.metalconstructionsestimates.customviews.estimates.EstimatesDiscountTotalAfterDiscount;
import com.example.metalconstructionsestimates.customviews.estimates.EstimatesVatTotalAllTaxIncluded;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.customviews.estimatesdetails.RefreshDeleteEstimateButtons;
import com.example.metalconstructionsestimates.customviews.estimatesdetails.NewEstimateLineUpdateButtons;
import com.example.metalconstructionsestimates.modules.estimateslines.AddEstimateLine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class EstimateDetails extends AppCompatActivity {

    Integer customerId;
    Integer estimateId;

    Estimate estimate;

    DBAdapter dbAdapter;
    TextView expirationDate,issueDate;

    String expirationDateLabelAndValue = "",issueDateLabelAndValue = "";
    String expirationDateValue, issueDateValue;

    private DatePickerDialog.OnDateSetListener expirationDateSetListner,issueDateSetListener;
    EstimatesDiscountTotalAfterDiscount estimatesDiscountTotalAfterDiscount;
    RefreshDeleteEstimateButtons refreshDeleteEstimateButtons;
    NewEstimateLineUpdateButtons newEstimateLineUpdateButtons;
    EstimatesVatTotalAllTaxIncluded estimatesVatTotalAllTaxIncluded;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    EstimateCustomerIdSelectCustomer estimateDetailsCustomerIdSelectCustomer;

    EstimateLocationAmountPaid estimateDetailsLocationAmountPaid;

    ActivityEstimateDetailsBinding activityEstimateDetailsBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEstimateDetailsBinding = ActivityEstimateDetailsBinding.inflate(getLayoutInflater());
        setContentView(activityEstimateDetailsBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar_estimate_details);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String estimateIdExtra = getIntent().getStringExtra("estimateIdExtra");
        estimateId = Integer.parseInt(estimateIdExtra);
        dbAdapter = new DBAdapter(getApplicationContext());
        estimate = dbAdapter.getEstimateById(estimateId);

        issueDate = findViewById(R.id.issueDateValue_estimate_details);
        expirationDate = findViewById(R.id.expirationDateValue_estimate_details);
        TextInputEditText discount = findViewById(R.id.discountEditText_estimate_details);
        TextInputEditText totalAfterDiscount = findViewById(R.id.totalAfterDiscountEditText_estimate_details);
        TextInputEditText location = findViewById(R.id.locationEditText_estimate_details);
        CheckBox isPaid = findViewById(R.id.paidCheckbox_estimate_details);
        TextInputEditText customerId = findViewById(R.id.customerIdEditText_estimate_details);
        Button selectCustomer = findViewById(R.id.selectCustomerButton);
        TextInputEditText vat = findViewById(R.id.vatEditText_estimate_details);
        TextInputEditText totalAllTaxIncluded = findViewById(R.id.totalInclTaxEditText_estimate_details);
        TextInputEditText estimateIdTextInputEditText = findViewById(R.id.estimateIdEditText_estimate_details);

        Button newEstimateLineButton = findViewById(R.id.newEstimateLineButton);
        Button updateEstimateButton = findViewById(R.id.updateButton_estimate_details);
        Button refreshEstimateLinesListButton = findViewById(R.id.refreshEstimateLinesListButton);
        Button deleteEstimateButton = findViewById(R.id.deleteEstimateButton_estimate_details);

        Button newEstimateLine = newEstimateLineUpdateButtons.getButtonNewEstimateLine();
        Button updateEstimate = newEstimateLineUpdateButtons.getButtonUpdateEstimate();
        Button refreshEstimateLinesList = refreshDeleteEstimateButtons.getButtonRefreshEstimate();
        Button deleteEstimate = refreshDeleteEstimateButtons.getButtonDeleteEstimate();
        TextInputEditText locationTextInputEditText = estimateDetailsLocationAmountPaid.getTextInputEditTextLocation();
        TextInputEditText estimateCustomerTextInputEditText = estimateDetailsCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
        TextInputEditText totalExcludingTaxTextInputEditText = findViewById(R.id.totalExclTaxEditText_estimate_details);
        TextInputEditText discountTextInputEditText = estimatesDiscountTotalAfterDiscount.getTextInputEditTextDiscount();
        TextInputEditText totalExcludingTaxTotalAfterDiscountTextInputEditText = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
        TextInputEditText vatTextInputEditText = estimatesVatTotalAllTaxIncluded.getTextInputEditTextVat();
        TextInputEditText totalAllTaxIncludedTextInputEditText = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();
        TextInputEditText amountPaidTextInputEditText = estimateDetailsLocationAmountPaid.getTextInputEditTextAmountPaid();

        DBAdapter db = new DBAdapter(getApplicationContext());
        ArrayList<EstimateLine> estimateLinesList = db.searchEstimateLines(estimateId);
        TextView noEstimateLinesTextView = findViewById(R.id.noEstimateLinesTextView);

        if (!estimateLinesList.isEmpty()) {
            noEstimateLinesTextView.setVisibility(View.GONE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            EstimateLinesListAdapter estimatesLinesListAdapter = new EstimateLinesListAdapter(this, estimateLinesList);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setAdapter(estimatesLinesListAdapter);
        } else {
            noEstimateLinesTextView.setVisibility(View.VISIBLE);
            activityEstimateDetailsBinding.estimateLinesRecyclerView.setVisibility(View.GONE);
        }

        if(!estimate.getExpirationDate().isEmpty()){
            expirationDateValue = estimate.getExpirationDate();
            expirationDateLabelAndValue = "Expiration Date: " + estimate.getExpirationDate();
        }
        else{
            expirationDateValue = "";
            expirationDateLabelAndValue = "Expiration Date: --/--/--";
        }

        if(!estimate.getIssueDate().isEmpty()){
            issueDateValue = estimate.getIssueDate();
            issueDateLabelAndValue = "Issue Date : " + estimate.getIssueDate();
        }
        else{
            issueDateValue = "";
            issueDateLabelAndValue = "Issue Date : --/--/--";
        }

        estimateIdTextInputEditText.setText(String.format(estimate.getId().toString()));
        locationTextInputEditText.setText(estimate.getDoneIn());
        issueDate.setText(issueDateLabelAndValue);
        expirationDate.setText(expirationDateLabelAndValue);

        if(estimate.getCustomer() == null){
            estimateCustomerTextInputEditText.setText("");
        }
        else{
            estimateCustomerTextInputEditText.setText(String.format(estimate.getCustomer().toString()));
        }


        if(estimate.getExcludingTaxTotal() == null){
            totalExcludingTaxTextInputEditText.setText("");
        }
        else{
            totalExcludingTaxTextInputEditText.setText(String.format(estimate.getExcludingTaxTotal().toString()));
        }

        totalExcludingTaxTextInputEditText.setEnabled(false);

        if(estimate.getDiscount() == null){
            discountTextInputEditText.setText("");
        }
        else{
            discountTextInputEditText.setText(String.format(estimate.getDiscount().toString()));
        }

        if(estimate.getExcludingTaxTotalAfterDiscount() == null){
            totalExcludingTaxTotalAfterDiscountTextInputEditText.setText("");
        }
        else{
            totalExcludingTaxTotalAfterDiscountTextInputEditText.setText(String.format(estimate.getExcludingTaxTotalAfterDiscount().toString()));
        }

        totalExcludingTaxTotalAfterDiscountTextInputEditText.setEnabled(false);

        if(estimate.getVat() == null){
            vatTextInputEditText.setText("");
        }
        else{
            vatTextInputEditText.setText(String.format(estimate.getVat().toString()));
        }

        if(estimate.getAllTaxIncludedTotal() == null){
            totalAllTaxIncludedTextInputEditText.setText("");
        }
        else{
            totalAllTaxIncludedTextInputEditText.setText(String.format(estimate.getAllTaxIncludedTotal().toString()));
        }

        totalAllTaxIncludedTextInputEditText.setEnabled(false);

        if(estimate.getAmountPaid() == null){
            amountPaidTextInputEditText.setText("");
        }
        else{
            amountPaidTextInputEditText.setText(String.format(estimate.getAmountPaid().toString()));
        }

        newEstimateLineUpdateButtons = findViewById(R.id.new_estimate_line_update_buttons);

        refreshDeleteEstimateButtons = findViewById(R.id.delete_estimate_refresh_buttons);

        Button selectCustomer = estimateDetailsCustomerIdSelectCustomer.getButtonSelectCustomer();
        selectCustomer.setOnClickListener(v -> startActivityForResult());

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        String customerIdExtraResult;
                        customerIdExtraResult = Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).getString("customerIdExtraResult");
                        customerId = Integer.parseInt(Objects.requireNonNull(customerIdExtraResult));
                        TextInputEditText customerIdTextInputEditText = estimateDetailsCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
                        String customerName = dbAdapter.getCustomerById(customerId).getName();
                        customerIdTextInputEditText.setText(customerName);
                    }
                }
        );
        
        newEstimateLine.setOnClickListener(view -> {
            Intent intent = new Intent(EstimateDetails.this, AddEstimateLine.class);
            intent.putExtra("estimateIdExtra", estimateId.toString());
            startActivity(intent);
        });

        deleteEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(EstimateDetails.this);
                alertDelete.setTitle("Confirmation de suppression");
                alertDelete.setMessage("Voulez-vous vraiment supprimer le devis?");
                alertDelete.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        TextInputEditText estimateIdTextInputEditText = findViewById(R.id.editText_estimate_id_estimate_details);
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

        updateEstimate.setOnClickListener(new View.OnClickListener() {
            EstimateCustomerIdSelectCustomer estimateDetailsCustomerIdSelectCustomer = findViewById(R.id.estimate_details_customer_id_select_customer);
            EstimateLocationAmountPaid estimateDetailsLocationAmountPaid = findViewById(R.id.done_in_is_paid_estimate_details);
            EstimatesVatTotalAllTaxIncluded estimatesVatTotalAllTaxIncluded = findViewById(R.id.estimatesDetailsVatTotalAllTaxIncluded);
            EstimatesDiscountTotalAfterDiscount estimatesDiscountTotalAfterDiscount = findViewById(R.id.estimatesDetailsDiscountTotalAfterDiscount);

            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertUpdate = new AlertDialog.Builder(EstimateDetails.this);
                alertUpdate.setTitle("Update Confirmation");
                alertUpdate.setMessage("Do you really want to update the estimate ?");
                alertUpdate.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        estimate = new Estimate();
                        TextInputEditText estimateIdTextInputEditText = findViewById(R.id.editText_estimate_id_estimate_details);
                        TextInputEditText locationTextInputEditText = estimateDetailsLocationAmountPaid.getTextInputEditTextLocation();
                        TextInputEditText amountPaidTextInputEditText = estimateDetailsLocationAmountPaid.getTextInputEditTextAmountPaid();
                        TextInputEditText estimateCustomerTextInputEditText = estimateDetailsCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
                        TextInputEditText totalExcludingTaxAfterDiscountTextInputEditText = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
                        TextInputEditText vatTextInputEditText = estimatesVatTotalAllTaxIncluded.getTextInputEditTextVat();
                        TextInputEditText totalAllTaxIncludedTextInputEditText = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();

                        TextInputEditText discountTextInputEditText = estimatesDiscountTotalAfterDiscount.getTextInputEditTextDiscount();

                        estimate.setId(Integer.parseInt(estimateIdTextInputEditText.getText().toString()));

                        if (!locationTextInputEditText.getText().toString().isEmpty()) {
                            estimate.setDoneIn(locationTextInputEditText.getText().toString());
                        } else {
                            estimate.setDoneIn("");
                        }

                        if (!issueDateValue.isEmpty()) {
                            estimate.setIssueDate(issueDateValue);
                        } else {
                            estimate.setIssueDate("");
                        }

                        if (!expirationDateValue.isEmpty()) {
                            estimate.setExpirationDate(expirationDateValue);
                        } else {
                            estimate.setExpirationDate("");
                        }

                        if (!estimateCustomerTextInputEditText.getText().toString().isEmpty()) {
                            estimate.setCustomer(Integer.parseInt(estimateCustomerTextInputEditText.getText().toString()));
                        } else {
                            estimate.setCustomer(null);
                        }

                        Float totalExcludingTax = 0.0f;
                        totalExcludingTax = dbAdapter.getEstimateExcludingTaxTotal(estimateId);
                        estimate.setExcludingTaxTotal(totalExcludingTax);

                        if (!discountTextInputEditText.getText().toString().isEmpty()) {
                            estimate.setDiscount(Float.parseFloat(discountTextInputEditText.getText().toString()));
                        } else {
                            estimate.setDiscount(null);
                        }

                        if (!totalExcludingTaxAfterDiscountTextInputEditText.getText().toString().isEmpty()) {
                            estimate.setExcludingTaxTotalAfterDiscount(Float.parseFloat(totalExcludingTaxAfterDiscountTextInputEditText.getText().toString()));
                        } else {
                            estimate.setExcludingTaxTotalAfterDiscount(null);
                        }

                        if (!vatTextInputEditText.getText().toString().isEmpty()) {
                            estimate.setVat(Float.parseFloat(vatTextInputEditText.getText().toString()));
                        } else {
                            estimate.setVat(null);
                        }

                        if (!totalAllTaxIncludedTextInputEditText.getText().toString().isEmpty()) {
                            estimate.setAllTaxIncludedTotal(Float.parseFloat(totalAllTaxIncludedTextInputEditText.getText().toString()));
                        } else {
                            estimate.setAllTaxIncludedTotal(null);
                        }

                        if (!amountPaidTextInputEditText.getText().toString().isEmpty()) {
                            estimate.setAmountPaid(Float.parseFloat(amountPaidTextInputEditText.getText().toString()));
                        } else {
                            estimate.setAmountPaid(null);
                        }

                        dbAdapter.updateEstimate(estimate);
                        Toast updateSuccessToast = Toast.makeText(getApplicationContext(), "Estimate have been successfully updated", Toast.LENGTH_LONG);
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

        refreshEstimateLinesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EstimatesDiscountTotalAfterDiscount estimatesDiscountTotalAfterDiscount = EstimateDetails.this.findViewById(R.id.estimatesDetailsDiscountTotalAfterDiscount);
                estimate = dbAdapter.getEstimateById(Integer.parseInt(estimateIdTextInputEditText.getText().toString()));
                locationTextInputEditText.setText(estimate.getDoneIn());
                TextView issueDate = issueDateExpirationDate.getTextViewEstimateIssueDate();
                TextView expirationDate = issueDateExpirationDate.getTextViewEstimateExpirationDate();
                issueDate.setText(estimate.getIssueDate());
                expirationDate.setText(estimate.getExpirationDate());
                estimateCustomerTextInputEditText.setText(String.format(estimate.getCustomer().toString()));
                totalExcludingTaxTextInputEditText.setText(String.format(estimate.getExcludingTaxTotal().toString()));
                discountTextInputEditText.setText(String.format(estimate.getDiscount().toString()));
                TextInputEditText totalExcludingTaxAfterDiscountTextInputEditText = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
                totalExcludingTaxAfterDiscountTextInputEditText.setText(String.format(estimate.getExcludingTaxTotalAfterDiscount().toString()));
                vatTextInputEditText.setText(String.format(estimate.getVat().toString()));
                totalAllTaxIncludedTextInputEditText.setText(String.format(estimate.getAllTaxIncludedTotal().toString()));
                TextInputEditText amountPaidTextInputEditText = estimateDetailsLocationAmountPaid.getTextInputEditTextAmountPaid();
                amountPaidTextInputEditText.setText(String.format(estimate.getAmountPaid().toString()));
                totalAllTaxIncludedTextInputEditText.setText(String.format(estimate.getAllTaxIncludedTotal().toString()));
                ArrayList<EstimateLine> estimateLinesList = db.searchEstimateLines(Integer.parseInt(estimateIdTextInputEditText.getText().toString()));

                if (estimateLinesList.isEmpty()) {
                    activityEstimateDetailsBinding.recyclerViewEstimateLines.setVisibility(View.GONE);
                    activityEstimateDetailsBinding.noEstimateLinesTextView.setVisibility(View.VISIBLE);
                } else {
                    activityEstimateDetailsBinding.recyclerViewEstimateLines.setVisibility(View.VISIBLE);
                    activityEstimateDetailsBinding.noEstimateLinesTextView.setVisibility(View.GONE);
                    EstimateLinesListAdapter estimatesLinesListAdapter = new EstimateLinesListAdapter(EstimateDetails.this, estimateLinesList);
                    activityEstimateDetailsBinding.recyclerViewEstimateLines.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    activityEstimateDetailsBinding.recyclerViewEstimateLines.setAdapter(estimatesLinesListAdapter);
                }

            }
        });

        discountTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                EstimatesDiscountTotalAfterDiscount estimatesDiscountTotalAfterDiscount = findViewById(R.id.estimatesDetailsDiscountTotalAfterDiscount);
                EstimatesVatTotalAllTaxIncluded estimatesVatTotalAllTaxIncluded = findViewById(R.id.estimatesDetailsVatTotalAllTaxIncluded);
                TextInputEditText totalExcludingTaxAfterDiscountTextInputEditText = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
                TextInputEditText vatTextInputEditText = estimatesVatTotalAllTaxIncluded.getTextInputEditTextVat();
                TextInputEditText totalAllTaxIncludedTextInputEditText = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();
                TextInputEditText totalExcludingTaxTextInputEditText = findViewById(R.id.editText_total_excluding_tax_estimate_details);
                String discount = s.toString();
                Float totalExcludingTax, totalExcludingTaxAfterDiscount, vat, totalAllTaxIncluded;
                totalExcludingTax = Float.parseFloat(Objects.requireNonNull(totalExcludingTaxTextInputEditText.getText()).toString());
                vat = Float.parseFloat(Objects.requireNonNull(vatTextInputEditText.getText()).toString());
                if (!discount.isEmpty()) {
                    totalExcludingTaxAfterDiscount = totalExcludingTax - totalExcludingTax * Float.parseFloat(discount) / 100;
                    totalExcludingTaxAfterDiscountTextInputEditText.setText(String.format(Locale.getDefault(), "%s", totalExcludingTaxAfterDiscount));
                    if (!vat.toString().isEmpty()) {
                        totalAllTaxIncluded = totalExcludingTaxAfterDiscount + totalExcludingTaxAfterDiscount * vat / 100;
                    } else {
                        totalAllTaxIncluded = totalExcludingTaxAfterDiscount;
                    }

                    totalAllTaxIncludedTextInputEditText.setText(String.format(Locale.getDefault(), "%s", totalAllTaxIncluded));


                } else {
                    totalExcludingTaxAfterDiscountTextInputEditText.setText(String.format(Locale.getDefault(), "%s", totalExcludingTax));
                    if (!vat.toString().isEmpty()) {
                        totalAllTaxIncluded = totalExcludingTax + totalExcludingTax * vat / 100;
                    } else {
                        totalAllTaxIncluded = totalExcludingTax;
                    }

                    totalAllTaxIncludedTextInputEditText.setText(String.format(Locale.getDefault(), "%s", totalAllTaxIncluded));
                }
            }
        });

        vatTextInputEditText.addTextChangedListener(new TextWatcher() {
            EstimatesDiscountTotalAfterDiscount estimatesDiscountTotalAfterDiscount = findViewById(R.id.estimatesDetailsDiscountTotalAfterDiscount);
            EstimatesVatTotalAllTaxIncluded estimatesVatTotalAllTaxIncluded = findViewById(R.id.estimatesDetailsVatTotalAllTaxIncluded);
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalExcludingTaxAfterDiscountTextInputEditText = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
                TextInputEditText totalAllTaxIncludedTextInputEditText = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();
                String vat = s.toString();
                Float totalExcludingTaxAfterDiscount, totalAllTaxIncluded;
                totalExcludingTaxAfterDiscount = Float.parseFloat(totalExcludingTaxAfterDiscountTextInputEditText.getText().toString());
                if (!vat.isEmpty()) {
                    totalAllTaxIncluded = totalExcludingTaxAfterDiscount + totalExcludingTaxAfterDiscount * Float.parseFloat(vat) / 100;
                } else {
                    totalAllTaxIncluded = totalExcludingTaxAfterDiscount;
                }
                totalAllTaxIncludedTextInputEditText.setText(totalAllTaxIncluded.toString());
            }
        });

        issueDate = issueDateExpirationDate.getTextViewEstimateIssueDate();
        expirationDate = issueDateExpirationDate.getTextViewEstimateExpirationDate();

        issueDate.setOnClickListener(view -> {
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

        expirationDate.setOnClickListener(view -> {
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

        expirationDateSetListner = (picker, year, month, day) -> {
            month = month + 1;
            expirationDateValue = year + "-" + month + "-" + day;
            expirationDate.setText("Expiration Date : " + expirationDateValue);
        };

        issueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            issueDateValue = year + "-" + month + "-" + day;
            issueDate.setText("Issue Date : " + issueDateValue);
        };

    }
    public void startActivityForResult() {
        Intent intent = new Intent(EstimateDetails.this, Customers.class);
        activityResultLauncher.launch(intent);
    }
}