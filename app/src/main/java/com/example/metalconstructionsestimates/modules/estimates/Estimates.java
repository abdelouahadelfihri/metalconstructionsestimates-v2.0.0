package com.example.metalconstructionsestimates.modules.estimates;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import com.example.metalconstructionsestimates.arraysadapters.EstimatesListAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Objects;

import com.example.metalconstructionsestimates.databinding.ActivityEstimatesBinding;

public class Estimates extends AppCompatActivity {

    public ActivityResultLauncher<Intent> activityResultLauncher;
    Spinner paymentStatusSpinner;
    TextInputEditText estimatesSearchEditText;
    Button allEstimatesButton, pendingEstimatesButton, approvedEstimatesButton,
            overdueEstimatesButton, cancelledEstimatesButton;
    String selectedEstimateStatus;

    private ActivityEstimatesBinding activityEstimatesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEstimatesBinding = ActivityEstimatesBinding.inflate(getLayoutInflater());
        setContentView(activityEstimatesBinding.getRoot());

        Toolbar toolBar = findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        DBAdapter db = new DBAdapter(getApplicationContext());
        ArrayList<Estimate> estimatesList = db.retrieveEstimates();
        allEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        selectedEstimateStatus = "All";

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

        allEstimatesButton = findViewById(R.id.buttonAll);
        pendingEstimatesButton = findViewById(R.id.buttonPending);
        approvedEstimatesButton = findViewById(R.id.buttonApproved);
        overdueEstimatesButton = findViewById(R.id.buttonOverdue);
        cancelledEstimatesButton = findViewById(R.id.buttonCancel);

        allEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                selectedEstimateStatus = "All";
                pendingEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                approvedEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                overdueEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                cancelledEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();
                ArrayList<Estimate> estimatesSearchList = db.searchEstimates(searchText);
                if(!searchText.isEmpty()){
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
                else{
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
            }
        });

        pendingEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pendingEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                selectedEstimateStatus = "Pending";
                allEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                approvedEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                overdueEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                cancelledEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();
                ArrayList<Estimate> estimatesSearchList = db.searchEstimates(searchText);
                if(!searchText.isEmpty()){
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
                else{
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
            }
        });

        approvedEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approvedEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                selectedEstimateStatus = "Approved";
                allEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                pendingEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                overdueEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                cancelledEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();
                ArrayList<Estimate> estimatesSearchList = db.searchEstimates(searchText);
                if(!searchText.isEmpty()){
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
                else{
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
            }
        });

        overdueEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overdueEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                selectedEstimateStatus = "Overdue";
                allEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                pendingEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                approvedEstimatesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();
                if(!searchText.isEmpty()){
                    ArrayList<Estimate> estimatesSearchList = db.searchEstimates(searchText);
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
                else{
                    ArrayList<Estimate> estimatesSearchList = db.searchEstimates(searchText);
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
            }
        });
        cancelledEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();
                if(!searchText.isEmpty()){
                    ArrayList<Estimate> estimatesSearchList = db.searchEstimates(searchText);
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
                else{
                    ArrayList<Estimate> estimatesSearchList = db.searchEstimates(searchText);
                    if (!estimatesSearchList.isEmpty()) {
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                    }
                    else{
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
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
                if(!searchText.isEmpty()){
                    activityEstimatesBinding.estimatesRecyclerView.setLayoutManager(new LinearLayoutManager(Estimates.this.getApplicationContext()));
                    DBAdapter db = new DBAdapter(getApplicationContext());
                    switch(selectedEstimateStatus){
                        case "All":
                            ArrayList<Estimate> allEstimateList = db.searchEstimates(searchText);
                            if (!allEstimateList.isEmpty()) {
                                EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                activityEstimatesBinding.emptyView.setText(R.string.noResult);
                                findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                                Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                                searchResultToast.show();
                            }
                            break;
                        case "Pending":
                            ArrayList<Estimate> pendingEstimatesList = db.searchPaidEstimates(searchText);
                            if (!pendingEstimatesList.isEmpty()) {
                                EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, paidEstimatesSearchList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);

                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noResult);
                                Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                                searchResultToast.show();
                            }
                            break;
                        case "Cancelled":
                            ArrayList<Estimate> cancelledEstimatesList = db.searchUnPaidEstimates(searchText);
                            if (!cancelledEstimatesList.isEmpty()) {
                                EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, unPaidEstimatesSearchList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noResult);
                                Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                                searchResultToast.show();
                            }
                            break;
                        case "Overdue":
                            ArrayList<Estimate> overdueEstimatesList = db.searchUnPaidEstimates(searchText);
                            if (!overdueEstimatesList.isEmpty()) {
                                EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, unPaidEstimatesSearchList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noResult);
                                Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                                searchResultToast.show();
                            }
                            break;

                    }
                }
            }
        });

        reloadEstimatesList.setOnClickListener(view -> {

            DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

            ArrayList<Estimate> estimatesSearchList = dbAdapter.retrieveEstimates();

            if(estimatesSearchList.isEmpty()){
                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                activityEstimatesBinding.emptyView.setVisibility(View.VISIBLE);
                activityEstimatesBinding.emptyView.setText(R.string.noEstimates);
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

}