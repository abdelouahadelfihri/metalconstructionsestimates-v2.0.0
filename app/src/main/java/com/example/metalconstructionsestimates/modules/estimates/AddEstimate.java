package com.example.metalconstructionsestimates.modules.estimates;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.customviews.estimates.EstimateCustomerIdSelectCustomer;
import com.example.metalconstructionsestimates.customviews.estimates.EstimateLocationAmountPaid;
import com.example.metalconstructionsestimates.customviews.estimates.IssueDateExpirationDate;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.customviews.AddClearButtons;

import java.util.Calendar;
import java.util.Objects;

public class AddEstimate extends AppCompatActivity {
    Integer customerId;
    DBAdapter dbAdapter;
    TextView expirationDate,issueDate;
    String expirationDateValue = "", issueDateValue = "";
    EstimateCustomerIdSelectCustomer estimatesCustomerIdSelectCustomer;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    AddClearButtons addClearButtons;
    EstimateLocationAmountPaid estimateLocationAmountPaid;
    IssueDateExpirationDate issueDateExpirationDate;
    private DatePickerDialog.OnDateSetListener expirationDateSetListner,issueDateSetListener;
    Intent intent;
    boolean customerExists = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estimate);
        dbAdapter = new DBAdapter(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar_add_estimate);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        issueDateExpirationDate = findViewById(R.id.issue_date_expiration_date_add_estimate);
        estimateLocationAmountPaid = findViewById(R.id.location_amount_paid_add_estimate);
        estimatesCustomerIdSelectCustomer = findViewById(R.id.customer_id_select_customer_add_estimate);
        Button selectCustomer = estimatesCustomerIdSelectCustomer.getButtonSelectCustomer();
        addClearButtons = findViewById(R.id.add_clear_buttons_add_estimates);
        Button addEstimate = addClearButtons.getAddButton();
        Button clearAddEstimateForm = addClearButtons.getClearButton();

        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult();
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String customerIdExtraResult;
                        customerIdExtraResult = Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).getString("customerIdExtraResult");
                        customerId = Integer.parseInt(customerIdExtraResult);
                        TextInputEditText customerIdTextInputEditText = estimatesCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
                        String customerName = dbAdapter.getCustomerById(customerId).getName();
                        customerIdTextInputEditText.setText(customerName);
                    }
                }
        );

        addEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText estimateLocationTextInputEditText = estimateLocationAmountPaid.getTextInputEditTextLocation();
                TextInputEditText amountPaidTextInputEditText = estimateLocationAmountPaid.getTextInputEditTextAmountPaid();
                TextInputEditText customerIdTextInputEditText = estimatesCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
                TextInputEditText estimateDiscountTextInputEditText = findViewById(R.id.textInputEditText_estimate_discount_add);
                TextInputEditText vatTextInputEditText = findViewById(R.id.textInputEditText_estimate_vat_add);

                if (estimateLocationTextInputEditText.getText().toString().isEmpty() && issueDateValue.isEmpty() && expirationDateValue.isEmpty() && customerIdTextInputEditText.getText().toString().isEmpty() && estimateDiscountTextInputEditText.getText().toString().isEmpty() && vatTextInputEditText.getText().toString().isEmpty() && !amountPaidTextInputEditText.getText().toString().isEmpty()) {
                    Toast emptyFields = Toast.makeText(getApplicationContext(), "Empty Fields.", Toast.LENGTH_LONG);
                    emptyFields.show();
                } else {
                    AlertDialog.Builder alertAdd = new AlertDialog.Builder(AddEstimate.this);
                    alertAdd.setTitle("Add Confirmation");
                    alertAdd.setMessage("Do you really want to add the estimate ?");
                    alertAdd.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbAdapter = new DBAdapter(getApplicationContext());
                            TextInputEditText estimateLocationTextInputEditText = estimateLocationAmountPaid.getTextInputEditTextLocation();
                            TextInputEditText customerIdTextInputEditText = estimatesCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
                            TextInputEditText estimateDiscountTextInputEditText = findViewById(R.id.textInputEditText_estimate_discount_add);
                            TextInputEditText vatTextInputEditText = findViewById(R.id.textInputEditText_estimate_vat_add);
                            TextInputEditText amountPaidTextInputEditText = estimateLocationAmountPaid.getTextInputEditTextAmountPaid();

                            Estimate estimate = new Estimate();

                            if (customerId != null) {
                                estimate.setCustomer(customerId);
                                customerExists = true;
                            } else {
                                if (customerIdTextInputEditText.getText().toString().isEmpty()) {
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

                            if (!issueDateValue.isEmpty()) {
                                estimate.setIssueDate(issueDateValue);
                            } else {
                                estimate.setIssueDate("");
                            }

                            if (!expirationDateValue.isEmpty()){
                                estimate.setExpirationDate(expirationDateValue);
                            } else {
                                estimate.setExpirationDate("");
                            }

                            if (estimateLocationTextInputEditText.getText().toString().isEmpty()) {
                                estimate.setDoneIn("");
                            } else {
                                estimate.setDoneIn(estimateLocationTextInputEditText.getText().toString());
                            }

                            if (estimateDiscountTextInputEditText.getText().toString().isEmpty()) {
                                estimate.setDiscount(null);
                            } else {
                                estimate.setDiscount(Float.parseFloat(estimateDiscountTextInputEditText.getText().toString()));
                            }

                            if (vatTextInputEditText.getText().toString().isEmpty()) {
                                estimate.setVat(null);
                            } else {
                                estimate.setVat(Float.parseFloat(vatTextInputEditText.getText().toString()));
                            }

                            if (amountPaidTextInputEditText.getText().toString().isEmpty()) {
                                estimate.setAmountPaid(null);
                            } else {
                                estimate.setAmountPaid(Float.parseFloat(amountPaidTextInputEditText.getText().toString()));
                            }

                            dbAdapter.saveEstimate(estimate);
                            Toast saveSuccessToast = Toast.makeText(getApplicationContext(), "Estimate have been successfully added", Toast.LENGTH_LONG);
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
            TextInputEditText estimateDiscountTextInputEditText;
            TextInputEditText vatTextInputEditText;
            TextInputEditText estimateLocationTextInputEditText;
            TextInputEditText customerIdTextInputEditText;
            TextInputEditText amountPaidTextInputEditText;
            estimateLocationTextInputEditText = estimateLocationAmountPaid.getTextInputEditTextLocation();
            amountPaidTextInputEditText = estimateLocationAmountPaid.getTextInputEditTextAmountPaid();
            issueDate = issueDateExpirationDate.getTextViewEstimateIssueDate();
            expirationDate = issueDateExpirationDate.getTextViewEstimateExpirationDate();
            customerIdTextInputEditText = estimatesCustomerIdSelectCustomer.getTextInputEditTextCustomerId();
            estimateDiscountTextInputEditText = findViewById(R.id.textInputEditText_estimate_discount_add);
            vatTextInputEditText = findViewById(R.id.textInputEditText_estimate_vat_add);
            Objects.requireNonNull(estimateLocationTextInputEditText.getText()).clear();
            issueDate.setText(R.string.issue_date);
            expirationDate.setText(R.string.expiration_date);
            Objects.requireNonNull(customerIdTextInputEditText.getText()).clear();
            Objects.requireNonNull(estimateDiscountTextInputEditText.getText()).clear();
            Objects.requireNonNull(vatTextInputEditText.getText()).clear();
            Objects.requireNonNull(amountPaidTextInputEditText.getText()).clear();
        });

        issueDate = issueDateExpirationDate.getTextViewEstimateIssueDate();
        expirationDate = issueDateExpirationDate.getTextViewEstimateExpirationDate();

        issueDate.setOnClickListener(view -> {
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

        expirationDate.setOnClickListener(view -> {
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
            expirationDate.setText("Expiration Date : " + expirationDateValue);
        };

        issueDateSetListener = (picker, year, month, day) -> {
            month = month + 1;
            issueDateValue = year + "-" + month + "-" + day;
            issueDate.setText("Issue Date : " + issueDateValue);
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