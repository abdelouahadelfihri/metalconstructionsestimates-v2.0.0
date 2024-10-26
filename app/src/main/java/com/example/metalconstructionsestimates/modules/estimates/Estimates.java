package com.example.metalconstructionsestimates.modules.estimates;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.metalconstructionsestimates.arraysadapters.EstimatesListAdapter;
import com.example.metalconstructionsestimates.customviews.estimates.EstimateDoneInIsPaid;
import com.example.metalconstructionsestimates.customviews.estimates.EstimateCustomerIdSelectCustomer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.customviews.estimates.EstimatesAllPaidUnpaid;
import com.example.metalconstructionsestimates.customviews.estimates.EstimatesDiscountTotalAfterDiscount;
import com.example.metalconstructionsestimates.customviews.estimates.EstimatesVatTotalAllTaxIncluded;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.metalconstructionsestimates.customviews.estimates.IssueDateExpirationDate;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import com.example.metalconstructionsestimates.databinding.ActivityEstimatesBinding;

public class Estimates extends AppCompatActivity {

    public ActivityResultLauncher<Intent> activityResultLauncher;
    public EstimatesDiscountTotalAfterDiscount estimatesDiscountTotalAfterDiscount;
    public EstimateDoneInIsPaid estimateDoneInIsPaid;
    public EstimateCustomerIdSelectCustomer estimateCustomerIdSelectCustomer;
    public EstimatesVatTotalAllTaxIncluded estimatesVatTotalAllTaxIncluded;
    public EstimatesAllPaidUnpaid estimatesAllPaidUnpaid;
    public IssueDateExpirationDate issueDateExpirationDate;
    String expirationDateString = "",issueDateString = "";

    Integer customerId;
    TextInputEditText estimate_id,done_in,customer_id,total_excluding_tax,discount,total_excluding_tax_after_discount,vat,total_all_tax_included;
    private DatePickerDialog.OnDateSetListener issueDateSetListener,expirationDateSetListener;
    TextView issueDate,expirationDate,issue_date,expiration_date,allEstimates,paidEstimates,unpaidEstimates;
    CheckBox isPaid;
    private ActivityEstimatesBinding activityEstimatesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activityEstimatesBinding = ActivityEstimatesBinding.inflate(getLayoutInflater());

        setContentView(activityEstimatesBinding.getRoot());

        Toolbar toolBar = findViewById(R.id.toolbar_estimates);

        setSupportActionBar(toolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        estimateDoneInIsPaid = findViewById(R.id.done_in_estimate_date_estimates);

        issueDateExpirationDate = activityEstimatesBinding.issueDateExpirationDateEstimates;
        issueDate = issueDateExpirationDate.getTextViewEstimateIssueDate();
        expirationDate = issueDateExpirationDate.getTextViewEstimateExpirationDate();
        estimateCustomerIdSelectCustomer = activityEstimatesBinding.customerIdSelectCustomerEstimates;
        Button selectCustomer = estimateCustomerIdSelectCustomer.getButtonSelectCustomer();
        estimatesAllPaidUnpaid = activityEstimatesBinding.customEstimatesAllPaidUnpaid;

        allEstimates = estimatesAllPaidUnpaid.getTextViewAllEstimates();
        unpaidEstimates = estimatesAllPaidUnpaid.getTextViewUnpaidEstimates();
        paidEstimates = estimatesAllPaidUnpaid.getTextViewPaidEstimates();

        DBAdapter db = new DBAdapter(getApplicationContext());
        ArrayList<Estimate> estimatesList = db.retrieveEstimates();

        RecyclerView recyclerViewEstimates = findViewById(R.id.recycler_view_estimates);


        final EstimatesListAdapter estimateListAdapter = new EstimatesListAdapter(this, estimatesList);

        recyclerViewEstimates.setAdapter(estimateListAdapter);

        recyclerViewEstimates.setLayoutManager(new LinearLayoutManager(this));

        if(estimatesList.isEmpty()){
            activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.GONE);
            activityEstimatesBinding.noEstimatesTextView.setVisibility(View.VISIBLE);
            activityEstimatesBinding.noEstimatesTextView.setText(R.string.noEstimates);
        }
        else{
            activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.VISIBLE);
            activityEstimatesBinding.noEstimatesTextView.setVisibility(View.GONE);
            estimateListAdapter.updateEstimates(estimatesList);
        }

        allEstimates.setBackgroundColor(Color.parseColor("#4F5EB1"));
        paidEstimates.setBackgroundColor(Color.LTGRAY);
        unpaidEstimates.setBackgroundColor(Color.LTGRAY);
        allEstimates.setTextColor(Color.WHITE);
        paidEstimates.setTextColor(Color.BLACK);
        unpaidEstimates.setTextColor(Color.BLACK);

        allEstimates.setOnClickListener(view -> {
            estimatesAllPaidUnpaid = activityEstimatesBinding.customEstimatesAllPaidUnpaid;
            allEstimates = estimatesAllPaidUnpaid.getTextViewAllEstimates();
            unpaidEstimates = estimatesAllPaidUnpaid.getTextViewUnpaidEstimates();
            paidEstimates = estimatesAllPaidUnpaid.getTextViewPaidEstimates();
            allEstimates.setBackgroundColor(Color.parseColor("#4F5EB1"));
            allEstimates.setTextColor(Color.WHITE);
            paidEstimates.setBackgroundColor(Color.LTGRAY);
            paidEstimates.setTextColor(Color.BLACK);
            unpaidEstimates.setBackgroundColor(Color.LTGRAY);
            unpaidEstimates.setTextColor(Color.BLACK);

            ArrayList<Estimate> allEstimatesList = db.retrieveEstimates();

            if (allEstimatesList.isEmpty()) {
                activityEstimatesBinding.noEstimatesTextView.setVisibility(View.VISIBLE);
                activityEstimatesBinding.noEstimatesTextView.setText(R.string.noEstimates);
                activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.GONE);
            } else {
                activityEstimatesBinding.noEstimatesTextView.setVisibility(View.GONE);
                activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.VISIBLE);
                estimateListAdapter.updateEstimates(allEstimatesList);
            }
        });

        paidEstimates.setOnClickListener(view -> {
            estimatesAllPaidUnpaid = activityEstimatesBinding.customEstimatesAllPaidUnpaid;
            allEstimates = estimatesAllPaidUnpaid.getTextViewAllEstimates();
            unpaidEstimates = estimatesAllPaidUnpaid.getTextViewUnpaidEstimates();
            paidEstimates = estimatesAllPaidUnpaid.getTextViewPaidEstimates();
            allEstimates.setBackgroundColor(Color.LTGRAY);
            allEstimates.setTextColor(Color.BLACK);
            paidEstimates.setBackgroundColor(Color.parseColor("#4F5EB1"));
            paidEstimates.setTextColor(Color.WHITE);
            unpaidEstimates.setBackgroundColor(Color.LTGRAY);
            unpaidEstimates.setTextColor(Color.BLACK);

            ArrayList<Estimate> paidEstimatesList = db.retrievePaidEstimates();

            if (paidEstimatesList.isEmpty()) {
                activityEstimatesBinding.noEstimatesTextView.setVisibility(View.VISIBLE);
                activityEstimatesBinding.noEstimatesTextView.setText(R.string.noPaidEstimates);
                activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.GONE);
            } else {
                activityEstimatesBinding.noEstimatesTextView.setVisibility(View.GONE);
                activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.VISIBLE);
                estimateListAdapter.updateEstimates(paidEstimatesList);
            }
        });

        unpaidEstimates.setOnClickListener(view -> {
            estimatesAllPaidUnpaid = activityEstimatesBinding.customEstimatesAllPaidUnpaid;
            allEstimates = estimatesAllPaidUnpaid.getTextViewAllEstimates();
            unpaidEstimates = estimatesAllPaidUnpaid.getTextViewUnpaidEstimates();
            paidEstimates = estimatesAllPaidUnpaid.getTextViewPaidEstimates();
            allEstimates.setBackgroundColor(Color.LTGRAY);
            allEstimates.setTextColor(Color.BLACK);
            paidEstimates.setBackgroundColor(Color.LTGRAY);
            paidEstimates.setTextColor(Color.BLACK);
            unpaidEstimates.setBackgroundColor(Color.parseColor("#4F5EB1"));
            unpaidEstimates.setTextColor(Color.WHITE);

            ArrayList<Estimate> unPaidEstimatesList = db.retrieveUnpaidEstimates();

            if (unPaidEstimatesList.isEmpty()) {
                activityEstimatesBinding.noEstimatesTextView.setVisibility(View.VISIBLE);
                activityEstimatesBinding.noEstimatesTextView.setText(R.string.noUnpaidEstimates);
                activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.GONE);
            } else {
                activityEstimatesBinding.noEstimatesTextView.setVisibility(View.GONE);
                activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.VISIBLE);
                estimateListAdapter.updateEstimates(unPaidEstimatesList);
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        String customerIdExtraResult;
                        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                        customerIdExtraResult = Objects.requireNonNull(data).getExtras().getString("customerIdExtraResult");
                        customerId = Integer.parseInt(Objects.requireNonNull(customerIdExtraResult));
                        TextInputEditText customerIdTextInputEditText = estimateCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
                        String customerName = dbAdapter.getCustomerById(customerId).getName();
                        customerIdTextInputEditText.setText(customerName);
                    }
                }
        );

        selectCustomer.setOnClickListener(view -> startActivityForResult());

        FloatingActionButton clearEstimateForm = findViewById(R.id.fab_clear_estimate_form);
        FloatingActionButton addEstimate = findViewById(R.id.fab_add_estimate);
        FloatingActionButton searchEstimate = findViewById(R.id.fab_search_estimates);
        FloatingActionButton reloadEstimatesList = findViewById(R.id.fab_refresh_estimates_list);

        searchEstimate.setOnClickListener(view -> {
            estimate_id = findViewById(R.id.editText_estimate_id_estimates);
            done_in = estimateDoneInIsPaid.getTextInputEditTextDoneIn();
            customer_id = estimateCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
            total_excluding_tax = findViewById(R.id.editText_total_excluding_tax_estimates);
            discount = estimatesDiscountTotalAfterDiscount.getTextInputEditTextDiscount();
            total_excluding_tax_after_discount = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
            vat = estimatesVatTotalAllTaxIncluded.getTextInputEditTextVat();
            total_all_tax_included = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();
            isPaid = estimateDoneInIsPaid.getCheckBoxIsEstimatePaid();
            Estimate estimate = new Estimate();
            DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
            boolean isEstimateIdEmpty = estimate_id.getText().toString().isEmpty();
            boolean isDoneInEmpty = done_in.getText().toString().isEmpty();
            boolean isCustomerIdEmpty = customer_id.getText().toString().isEmpty();
            boolean isTotalExcludingTaxEmpty = total_excluding_tax.getText().toString().isEmpty();
            boolean isDiscountEmpty = discount.getText().toString().isEmpty();
            boolean isTotalExcludingTaxAfterDiscountEmpty = total_excluding_tax_after_discount.getText().toString().isEmpty();
            boolean isVatEmpty = vat.getText().toString().isEmpty();
            boolean isTotalAllTaxIncludedEmpty = total_all_tax_included.getText().toString().isEmpty();

            if (isEstimateIdEmpty && isDoneInEmpty && issueDateString.isEmpty() && expirationDateString.isEmpty() &&
                    isCustomerIdEmpty && isTotalExcludingTaxEmpty && isDiscountEmpty &&
                    isTotalExcludingTaxAfterDiscountEmpty && isVatEmpty && isTotalAllTaxIncludedEmpty) {

                Toast emptyFields = Toast.makeText(getApplicationContext(), "Champs vides.", Toast.LENGTH_LONG);
                emptyFields.show();
            }
            else{
                if (customer_id.getText().toString().isEmpty()) {
                    estimate.setCustomer(null);
                } else {
                    if (allDigitString(customer_id.getText().toString())) {
                        Customer customer = dbAdapter.getCustomerById(Integer.parseInt(customer_id.getText().toString()));
                        if (customer != null) {
                            estimate.setCustomer(customer.getId());
                        } else {
                            estimate.setCustomer(null);
                        }
                    } else {
                        Integer customer = dbAdapter.getCustomerIdByName(customer_id.getText().toString());
                        if (customer != null) {
                            estimate.setCustomer(customer);
                        } else {
                            estimate.setCustomer(null);
                        }
                    }
                }

                if (!issueDateString.isEmpty()) {
                    estimate.setIssueDate(issueDateString);
                } else {
                    estimate.setExpirationDate("");
                }

                if (!expirationDateString.isEmpty()) {
                    estimate.setExpirationDate(expirationDateString);
                } else {
                    estimate.setExpirationDate("");
                }

                if (done_in.getText().toString().isEmpty()) {
                    estimate.setDoneIn("");
                } else {
                    estimate.setDoneIn(done_in.getText().toString());
                }

                if (discount.getText().toString().isEmpty()) {
                    estimate.setDiscount(null);
                } else {
                    estimate.setDiscount(Float.parseFloat(discount.getText().toString()));
                }

                if (vat.getText().toString().isEmpty()) {
                    estimate.setVat(null);
                } else {
                    estimate.setVat(Float.parseFloat(vat.getText().toString()));
                }

                if (total_all_tax_included.getText().toString().isEmpty()) {
                    estimate.setAllTaxIncludedTotal(null);
                } else {
                    estimate.setAllTaxIncludedTotal(Float.parseFloat(total_all_tax_included.getText().toString()));
                }

                if (isPaid.isChecked()) {
                    estimate.setIsEstimatePaid("true");
                } else {
                    estimate.setIsEstimatePaid("false");
                }

                ArrayList<Estimate> estimatesSearchList = dbAdapter.searchEstimates(estimate);

                if(estimatesSearchList.isEmpty()){
                    activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.GONE);
                    activityEstimatesBinding.noEstimatesTextView.setVisibility(View.VISIBLE);
                    Toast searchResultToast = Toast.makeText(getApplicationContext(), "La recherche n'a retourné aucun résultat", Toast.LENGTH_LONG);
                    searchResultToast.show();
                }
                else{
                    activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.VISIBLE);
                    activityEstimatesBinding.noEstimatesTextView.setVisibility(View.GONE);
                    estimateListAdapter.updateEstimates(estimatesSearchList);
                }
            }
        });

        reloadEstimatesList.setOnClickListener(view -> {

            DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

            ArrayList<Estimate> estimatesSearchList = dbAdapter.retrieveEstimates();

            if(estimatesSearchList.isEmpty()){
                activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.GONE);
                activityEstimatesBinding.noEstimatesTextView.setVisibility(View.VISIBLE);
                Toast reloadResultToast = Toast.makeText(getApplicationContext(), "Estimates list is empty", Toast.LENGTH_LONG);
                reloadResultToast.show();
            }
            else{
                activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.VISIBLE);
                activityEstimatesBinding.noEstimatesTextView.setVisibility(View.GONE);
                estimateListAdapter.updateEstimates(estimatesSearchList);
            }

        });

        addEstimate.setOnClickListener(view -> {
            Intent intent = new Intent(Estimates.this, AddEstimate.class);
            startActivity(intent);
        });

        clearEstimateForm.setOnClickListener(view -> {

            issue_date = issueDateExpirationDate.getTextViewEstimateIssueDate();
            expiration_date = issueDateExpirationDate.getTextViewEstimateExpirationDate();
            total_excluding_tax = findViewById(R.id.editText_total_excluding_tax_estimates);
            discount = estimatesDiscountTotalAfterDiscount.getTextInputEditTextDiscount();
            total_excluding_tax_after_discount = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
            vat = estimatesVatTotalAllTaxIncluded.getTextInputEditTextVat();
            total_all_tax_included = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();
            isPaid = estimateDoneInIsPaid.getCheckBoxIsEstimatePaid();
            estimate_id.getText().clear();
            done_in.getText().clear();
            issueDateString = "";
            expirationDateString = "";
            issue_date.setText(R.string.issue_date);
            expiration_date.setText(R.string.expiration_date);
            customer_id.getText().clear();
            total_excluding_tax.getText().clear();
            discount.getText().clear();
            total_excluding_tax_after_discount.getText().clear();
            vat.getText().clear();
            total_all_tax_included.getText().clear();
            isPaid.setChecked(false);
        });

        estimatesDiscountTotalAfterDiscount = findViewById(R.id.customEstimatesDiscountTotalAfterDiscount);
        estimatesVatTotalAllTaxIncluded = findViewById(R.id.customEstimatesVatTotalAllTaxIncluded);

        allEstimates = estimatesAllPaidUnpaid.getTextViewAllEstimates();
        unpaidEstimates = estimatesAllPaidUnpaid.getTextViewUnpaidEstimates();
        paidEstimates = estimatesAllPaidUnpaid.getTextViewPaidEstimates();

        discount = estimatesDiscountTotalAfterDiscount.getTextInputEditTextDiscount();
        total_excluding_tax_after_discount = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();

        vat = estimatesVatTotalAllTaxIncluded.getTextInputEditTextVat();

        total_all_tax_included = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();

        TextInputEditText total_excluding_tax_edit_text = findViewById(R.id.editText_total_excluding_tax_estimates);
        TextInputEditText discount_edit_text = estimatesDiscountTotalAfterDiscount.getTextInputEditTextDiscount();
        TextInputEditText total_after_discount_edit_text = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
        TextInputEditText vat_edit_text = estimatesVatTotalAllTaxIncluded.getTextInputEditTextVat();
        TextInputEditText total_all_tax_included_edit_text = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();
        total_excluding_tax_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Locale moroccoLocale = new Locale("ar", "MA");
                NumberFormat numberFormat = NumberFormat.getNumberInstance(moroccoLocale);
                String totalAfterDiscountFormattedValue;
                String totalTaxExcluded = s.toString();
                Float totalAfterDiscount;
                Float totalAllTaxIncluded;
                if(!totalTaxExcluded.isEmpty()){
                    if(!discount_edit_text.getText().toString().isEmpty()){
                        totalAfterDiscount = Float.parseFloat(totalTaxExcluded) - Float.parseFloat(totalTaxExcluded) * Float.parseFloat(discount_edit_text.getText().toString())/100;
                    }
                    else{
                        totalAfterDiscount = Float.parseFloat(totalTaxExcluded);
                    }
                    totalAfterDiscountFormattedValue = numberFormat.format(totalAfterDiscount);
                    total_after_discount_edit_text.setText(totalAfterDiscountFormattedValue);
                    if(!vat_edit_text.getText().toString().isEmpty()){
                        totalAllTaxIncluded = totalAfterDiscount + totalAfterDiscount * Float.parseFloat(vat_edit_text.getText().toString())/100;
                    }
                    else{
                        totalAllTaxIncluded = totalAfterDiscount;
                    }
                    total_all_tax_included_edit_text.setText(numberFormat.format(totalAllTaxIncluded));
                }
                else{
                    total_after_discount_edit_text.setText("");
                    total_all_tax_included_edit_text.setText("");
                }
            }
        });

        discount_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String discount = s.toString();
                Float totalAfterDiscount = 0.0f;
                Float totalAllTaxIncluded = 0.0f;
                Locale moroccoLocale = new Locale("ar", "MA");
                NumberFormat numberFormat = NumberFormat.getNumberInstance(moroccoLocale);
                if(!discount.isEmpty()){
                    if(!total_excluding_tax_edit_text.getText().toString().isEmpty()){
                        totalAfterDiscount = Float.parseFloat(total_excluding_tax_edit_text.getText().toString()) - Float.parseFloat(total_excluding_tax_edit_text.getText().toString()) * Float.parseFloat(discount) /100;
                        total_after_discount_edit_text.setText(numberFormat.format(totalAfterDiscount));
                        if(!vat_edit_text.getText().toString().isEmpty()){
                            totalAllTaxIncluded = totalAfterDiscount + totalAfterDiscount * Float.parseFloat(vat_edit_text.getText().toString())/100;
                        }
                        else{
                            totalAllTaxIncluded = totalAfterDiscount;
                        }
                        if(totalAllTaxIncluded != 0.0f){
                            total_all_tax_included_edit_text.setText(numberFormat.format(totalAllTaxIncluded));
                        }
                    }
                    else{
                        total_after_discount_edit_text.setText("");
                        total_all_tax_included_edit_text.setText("");
                    }
                }
                else{
                    if(!total_excluding_tax_edit_text.getText().toString().isEmpty()){
                        totalAfterDiscount = Float.parseFloat(total_excluding_tax_edit_text.getText().toString());
                        total_after_discount_edit_text.setText(numberFormat.format(totalAfterDiscount));
                        if(!vat_edit_text.getText().toString().isEmpty()){
                            totalAllTaxIncluded = totalAfterDiscount + totalAfterDiscount * Float.parseFloat(vat_edit_text.getText().toString())/100;
                        }
                        else{
                            totalAllTaxIncluded = totalAfterDiscount;
                        }
                        total_all_tax_included_edit_text.setText(numberFormat.format(totalAllTaxIncluded));
                    }
                    else{
                        total_after_discount_edit_text.setText("");
                        total_all_tax_included_edit_text.setText("");
                    }
                }
            }
        });

        total_after_discount_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Locale morroccoLocale = new Locale("ar","MA");
                NumberFormat numberFormat = NumberFormat.getNumberInstance(morroccoLocale);
                String totalAfterDiscount = s.toString();
                Float totalAllTaxIncluded;
                if(!totalAfterDiscount.isEmpty()){
                    if(!vat_edit_text.getText().toString().isEmpty()){
                        totalAllTaxIncluded = Float.parseFloat(totalAfterDiscount) + Float.parseFloat(totalAfterDiscount) * Float.parseFloat(vat_edit_text.getText().toString())/100;
                        total_all_tax_included_edit_text.setText(numberFormat.format(totalAllTaxIncluded));
                    }
                    else{
                        total_all_tax_included_edit_text.setText(numberFormat.format(totalAfterDiscount));
                    }
                }
                else{
                    total_all_tax_included_edit_text.setText("");
                }
            }
        });

        vat_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String vat = s.toString();
                Float totalAllTaxIncluded;
                Locale moroccoLocale = new Locale("ar", "MA");
                NumberFormat numberFormat = NumberFormat.getNumberInstance(moroccoLocale);
                if(!vat.isEmpty()){
                    if(!total_after_discount_edit_text.getText().toString().isEmpty()){
                        totalAllTaxIncluded = Float.parseFloat(total_after_discount_edit_text.getText().toString()) + Float.parseFloat(total_after_discount_edit_text.getText().toString()) * Float.parseFloat(vat)/100;
                        total_all_tax_included_edit_text.setText(numberFormat.format(totalAllTaxIncluded));
                    }
                    else{
                        total_all_tax_included_edit_text.setText("");
                    }
                }
                else{
                    if(!total_after_discount_edit_text.getText().toString().isEmpty()){
                        totalAllTaxIncluded = Float.parseFloat(total_after_discount_edit_text.getText().toString());
                        total_all_tax_included_edit_text.setText(numberFormat.format(totalAllTaxIncluded));
                    }
                    else{
                        total_all_tax_included_edit_text.setText("");
                    }
                }
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
                    Estimates.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    issueDateSetListener,
                    year, month, day
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDate.setOnClickListener(view -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(
                    Estimates.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    expirationDateSetListener,
                    year, month, day
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        expirationDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            expirationDateString = year + "-" + month + "-" + day;
            expirationDate.setText("Date Expiration : " + expirationDateString);
        };

        issueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            issueDateString = year + "-" + month + "-" + day;
            issueDate.setText("Date Création : " + issueDateString);
        };
    }

    public void startActivityForResult() {
        Intent intent = new Intent(Estimates.this, Customers.class);
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