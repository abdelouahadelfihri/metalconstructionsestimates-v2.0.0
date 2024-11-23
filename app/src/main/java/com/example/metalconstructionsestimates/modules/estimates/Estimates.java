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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.metalconstructionsestimates.arraysadapters.EstimatesListAdapter;
import com.example.metalconstructionsestimates.customviews.estimates.EstimateLocationAmountPaid;
import com.example.metalconstructionsestimates.customviews.estimates.EstimateCustomerIdSelectCustomer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.customviews.estimates.EstimatesAllPaidPartiallyPaidUnpaid;
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
    public EstimateLocationAmountPaid estimateLocationAmountPaid;
    public EstimateCustomerIdSelectCustomer estimateCustomerIdSelectCustomer;
    public EstimatesVatTotalAllTaxIncluded estimatesVatTotalAllTaxIncluded;
    public EstimatesAllPaidPartiallyPaidUnpaid estimatesAllPaidPartiallyPaidUnpaid;
    public IssueDateExpirationDate issueDateExpirationDate;
    String expirationDateString = "",issueDateString = "";
    Spinner paymentStatusSpinner;
    Integer customerId;
    TextInputEditText estimate_id,location,amount_paid,customer_id,total_excluding_tax,discount,total_excluding_tax_after_discount,vat,total_all_tax_included;
    private DatePickerDialog.OnDateSetListener issueDateSetListener,expirationDateSetListener;
    TextView issueDate,expirationDate,issue_date,expiration_date,allEstimates,paidEstimates, partiallyPaidEstimates,unpaidEstimates;

    private ActivityEstimatesBinding activityEstimatesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activityEstimatesBinding = ActivityEstimatesBinding.inflate(getLayoutInflater());

        setContentView(activityEstimatesBinding.getRoot());

        Toolbar toolBar = findViewById(R.id.toolbar_estimates);

        setSupportActionBar(toolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        estimateLocationAmountPaid = findViewById(R.id.done_in_estimate_date_estimates);

        issueDateExpirationDate = activityEstimatesBinding.issueDateExpirationDateEstimates;
        issueDate = issueDateExpirationDate.getTextViewEstimateIssueDate();
        expirationDate = issueDateExpirationDate.getTextViewEstimateExpirationDate();
        estimateCustomerIdSelectCustomer = activityEstimatesBinding.customerIdSelectCustomerEstimates;
        Button selectCustomer = estimateCustomerIdSelectCustomer.getButtonSelectCustomer();
        estimatesAllPaidPartiallyPaidUnpaid = activityEstimatesBinding.customEstimatesAllPaidUnpaid;
        paymentStatusSpinner = estimatesAllPaidPartiallyPaidUnpaid.getSpinnerPaymentStatus();

        ArrayAdapter<CharSequence> paymentStatusAdapter = ArrayAdapter.createFromResource(this, R.array.payment_status, android.R.layout.simple_spinner_item);

        paymentStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        paymentStatusSpinner.setAdapter(paymentStatusAdapter);

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

        paymentStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                switch(item.toString()){
                    case "All":
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
                        break;
                    case "Paid":
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
                        break;
                    case "Partially Paid":
                        ArrayList<Estimate> partiallyPaidEstimatesList = db.retrievePartiallyPaidEstimates();

                        if (partiallyPaidEstimatesList.isEmpty()) {
                            activityEstimatesBinding.noEstimatesTextView.setVisibility(View.VISIBLE);
                            activityEstimatesBinding.noEstimatesTextView.setText(R.string.noPaidEstimates);
                            activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.GONE);
                        } else {
                            activityEstimatesBinding.noEstimatesTextView.setVisibility(View.GONE);
                            activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.VISIBLE);
                            estimateListAdapter.updateEstimates(partiallyPaidEstimatesList);
                        }
                        break;
                    case "Unpaid":
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
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
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
            location = estimateLocationAmountPaid.getTextInputEditTextLocation();
            customer_id = estimateCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
            total_excluding_tax = findViewById(R.id.editText_total_excluding_tax_estimates);
            discount = estimatesDiscountTotalAfterDiscount.getTextInputEditTextDiscount();
            total_excluding_tax_after_discount = estimatesDiscountTotalAfterDiscount.getTextInputEditTextTotalAfterDiscount();
            vat = estimatesVatTotalAllTaxIncluded.getTextInputEditTextVat();
            total_all_tax_included = estimatesVatTotalAllTaxIncluded.getTextInputEditTextTotalAllTaxIncluded();
            amount_paid = estimateLocationAmountPaid.getTextInputEditTextAmountPaid();
            Estimate estimate = new Estimate();
            DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
            boolean isEstimateIdEmpty = estimate_id.getText().toString().isEmpty();
            boolean isLocationEmpty = location.getText().toString().isEmpty();
            boolean isCustomerIdEmpty = customer_id.getText().toString().isEmpty();
            boolean isTotalExcludingTaxEmpty = total_excluding_tax.getText().toString().isEmpty();
            boolean isDiscountEmpty = discount.getText().toString().isEmpty();
            boolean isTotalExcludingTaxAfterDiscountEmpty = total_excluding_tax_after_discount.getText().toString().isEmpty();
            boolean isVatEmpty = vat.getText().toString().isEmpty();
            boolean isTotalAllTaxIncludedEmpty = total_all_tax_included.getText().toString().isEmpty();

            if (isEstimateIdEmpty && isLocationEmpty && issueDateString.isEmpty() && expirationDateString.isEmpty() &&
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

                if (location.getText().toString().isEmpty()) {
                    estimate.setDoneIn("");
                } else {
                    estimate.setDoneIn(location.getText().toString());
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

                if (amount_paid.getText().toString().isEmpty()) {
                    estimate.setAmountPaid(null);
                } else {
                    estimate.setAmountPaid(Float.parseFloat(amount_paid.getText().toString()));
                }

                ArrayList<Estimate> estimatesSearchList = dbAdapter.searchEstimates(estimate);

                if(estimatesSearchList.isEmpty()){
                    activityEstimatesBinding.recyclerViewEstimates.setVisibility(View.GONE);
                    activityEstimatesBinding.noEstimatesTextView.setVisibility(View.VISIBLE);
                    Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
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
            amount_paid = estimateLocationAmountPaid.getTextInputEditTextAmountPaid();
            estimate_id.getText().clear();
            location.getText().clear();
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
            amount_paid.getText().clear();
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