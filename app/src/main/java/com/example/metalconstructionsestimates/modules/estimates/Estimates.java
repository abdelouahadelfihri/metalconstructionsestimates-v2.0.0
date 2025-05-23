package com.example.metalconstructionsestimates.modules.estimates;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import com.example.metalconstructionsestimates.arraysadapters.EstimatesListAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Objects;

import com.example.metalconstructionsestimates.databinding.ActivityEstimatesBinding;

public class Estimates extends AppCompatActivity {

    public ActivityResultLauncher<Intent> activityResultLauncher;
    Spinner paymentStatusSpinner;
    TextInputEditText estimatesSearchEditText;
    private ActivityEstimatesBinding activityEstimatesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activityEstimatesBinding = ActivityEstimatesBinding.inflate(getLayoutInflater());

        setContentView(activityEstimatesBinding.getRoot());

        Toolbar toolBar = findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        paymentStatusSpinner = findViewById(R.id.spinner_payment_status);

        ArrayAdapter<CharSequence> paymentStatusAdapter = ArrayAdapter.createFromResource(this, R.array.payment_status, android.R.layout.simple_spinner_item);

        paymentStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        paymentStatusSpinner.setAdapter(paymentStatusAdapter);

        DBAdapter db = new DBAdapter(getApplicationContext());
        ArrayList<Estimate> estimatesList = db.retrieveEstimates();

        final EstimatesListAdapter estimateListAdapter = new EstimatesListAdapter(this, estimatesList);

        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimateListAdapter);

        activityEstimatesBinding.estimatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(estimatesList.isEmpty()){
            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
            activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
            activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
        }
        else{
            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
            activityEstimatesBinding.emptyView.setVisibility(View.GONE);
            estimateListAdapter.updateEstimates(estimatesList);
        }

        paymentStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                DBAdapter db = new DBAdapter(getApplicationContext());
                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();
                if(!searchText.isEmpty()){
                    switch(item.toString()){
                        case "All":
                            ArrayList<Estimate> allEstimatesList = db.searchEstimates(searchText);
                            if (allEstimatesList.isEmpty()) {
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                                estimateListAdapter.updateEstimates(allEstimatesList);
                            }
                            break;
                        case "Paid":
                            db = new DBAdapter(getApplicationContext());
                            ArrayList<Estimate> paidEstimatesList = db.searchPaidEstimates(searchText);
                            if (paidEstimatesList.isEmpty()) {
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                                estimateListAdapter.updateEstimates(paidEstimatesList);
                            }
                            break;
                        case "Partially Paid":
                            db = new DBAdapter(getApplicationContext());
                            ArrayList<Estimate> partiallyPaidEstimatesList = db.searchPartiallyPaidEstimates(searchText);
                            if (partiallyPaidEstimatesList.isEmpty()) {
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                                estimateListAdapter.updateEstimates(partiallyPaidEstimatesList);
                            }
                            break;
                        case "Unpaid":
                            db = new DBAdapter(getApplicationContext());
                            ArrayList<Estimate> unPaidEstimatesList = db.searchUnPaidEstimates(searchText);
                            if (unPaidEstimatesList.isEmpty()) {
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                                estimateListAdapter.updateEstimates(unPaidEstimatesList);
                            }
                            break;
                    }
                }
                else{
                    switch(item.toString()){
                        case "All":
                            ArrayList<Estimate> allEstimatesList = db.retrieveEstimates();
                            if (allEstimatesList.isEmpty()) {
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                                estimateListAdapter.updateEstimates(allEstimatesList);
                            }
                            break;
                        case "Paid":
                            db = new DBAdapter(getApplicationContext());
                            ArrayList<Estimate> paidEstimatesList = db.retrievePaidEstimates();
                            if (paidEstimatesList.isEmpty()) {
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                                estimateListAdapter.updateEstimates(paidEstimatesList);
                            }
                            break;
                        case "Partially Paid":
                            db = new DBAdapter(getApplicationContext());
                            ArrayList<Estimate> partiallyPaidEstimatesList = db.retrievePartiallyPaidEstimates();
                            if (partiallyPaidEstimatesList.isEmpty()) {
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                                estimateListAdapter.updateEstimates(partiallyPaidEstimatesList);
                            }
                            break;
                        case "Unpaid":
                            db = new DBAdapter(getApplicationContext());
                            ArrayList<Estimate> unPaidEstimatesList = db.retrieveUnpaidEstimates();
                            if (unPaidEstimatesList.isEmpty()) {
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                                estimateListAdapter.updateEstimates(unPaidEstimatesList);
                            }
                            break;
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        FloatingActionButton clearEstimateForm = findViewById(R.id.fab_clear);
        FloatingActionButton addEstimate = findViewById(R.id.fab_add);
        FloatingActionButton reloadEstimatesList = findViewById(R.id.fab_refresh);

        estimatesSearchEditText = findViewById(R.id.searchEditText);

        estimatesSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString();
                activityEstimatesBinding.estimatesRecyclerView.setLayoutManager(new LinearLayoutManager(Estimates.this.getApplicationContext()));
                DBAdapter db = new DBAdapter(getApplicationContext());
                String selectedPaymentStatus = paymentStatusSpinner.getSelectedItem().toString();
                switch(selectedPaymentStatus){
                    case "All":
                        ArrayList<Estimate> estimatesSearchList = db.searchEstimates(searchText);
                        if (!estimatesSearchList.isEmpty()) {
                            EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                            findViewById(R.id.emptyView).setVisibility(View.GONE);
                            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                            activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        }
                        else{
                            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                            Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                            searchResultToast.show();
                        }
                        break;
                    case "Paid":
                        ArrayList<Estimate> paidEstimatesSearchList = db.searchPaidEstimates(searchText);
                        if (!paidEstimatesSearchList.isEmpty()) {
                            EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, paidEstimatesSearchList);
                            findViewById(R.id.emptyView).setVisibility(View.GONE);

                            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                            activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        }
                        else{
                            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                            Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                            searchResultToast.show();
                        }
                        break;
                    case "Partially Paid":
                        ArrayList<Estimate> partiallyPaidEstimatesSearchList = db.searchPartiallyPaidEstimates(searchText);
                        if (!partiallyPaidEstimatesSearchList.isEmpty()) {
                            EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, partiallyPaidEstimatesSearchList);
                            findViewById(R.id.emptyView).setVisibility(View.GONE);

                            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                            activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        }
                        else{
                            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                            Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                            searchResultToast.show();
                        }
                        break;
                    case "Unpaid":
                        ArrayList<Estimate> unPaidEstimatesSearchList = db.searchUnPaidEstimates(searchText);
                        if (!unPaidEstimatesSearchList.isEmpty()) {
                            EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, unPaidEstimatesSearchList);
                            findViewById(R.id.emptyView).setVisibility(View.GONE);
                            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                            activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        }
                        else{
                            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                            Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                            searchResultToast.show();
                        }
                        break;

                }
            }
        });

        reloadEstimatesList.setOnClickListener(view -> {

            DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

            ArrayList<Estimate> estimatesSearchList = dbAdapter.retrieveEstimates();

            if(estimatesSearchList.isEmpty()){
                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                Toast reloadResultToast = Toast.makeText(getApplicationContext(), "Estimates list is empty", Toast.LENGTH_LONG);
                reloadResultToast.show();
            }
            else{
                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                activityEstimatesBinding.emptyView.setVisibility(View.GONE);
                estimateListAdapter.updateEstimates(estimatesSearchList);
            }

        });

        addEstimate.setOnClickListener(view -> {
            Intent intent = new Intent(Estimates.this, AddEstimate.class);
            startActivity(intent);
        });

        clearEstimateForm.setOnClickListener(view -> {
            estimatesSearchEditText.getText().clear();
        });


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