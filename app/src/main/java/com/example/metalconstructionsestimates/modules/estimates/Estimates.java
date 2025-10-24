package com.example.metalconstructionsestimates.modules.estimates;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import com.example.metalconstructionsestimates.recyclerviewadapters.EstimatesListAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
    TextInputEditText estimatesSearchEditText;
    Button allEstimatesButton, pendingEstimatesButton, approvedEstimatesButton,
            overdueEstimatesButton, cancelledEstimatesButton;
    String selectedEstimateStatus;
    DBAdapter dbAdapter;

    private ActivityEstimatesBinding activityEstimatesBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEstimatesBinding = ActivityEstimatesBinding.inflate(getLayoutInflater());
        setContentView(activityEstimatesBinding.getRoot());

        Toolbar toolBar = findViewById(R.id.toolbar);

        setSupportActionBar(toolBar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DBAdapter(getApplicationContext());
        ArrayList<Estimate> estimatesList = dbAdapter.retrieveEstimates();

        allEstimatesButton = findViewById(R.id.buttonAll);

        allEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_selected));
        allEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(),  R.color.white));
        selectedEstimateStatus = "All";

        pendingEstimatesButton = findViewById(R.id.buttonPending);
        pendingEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
        pendingEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

        overdueEstimatesButton = findViewById(R.id.buttonOverdue);
        overdueEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
        overdueEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

        cancelledEstimatesButton = findViewById(R.id.buttonCancel);
        cancelledEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
        cancelledEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

        approvedEstimatesButton = findViewById(R.id.buttonApproved);
        approvedEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
        approvedEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

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

                allEstimatesButton = findViewById(R.id.buttonAll);
                allEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_selected));
                allEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                selectedEstimateStatus = "All";

                pendingEstimatesButton = findViewById(R.id.buttonPending);
                pendingEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                pendingEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                overdueEstimatesButton = findViewById(R.id.buttonOverdue);
                overdueEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                overdueEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                cancelledEstimatesButton = findViewById(R.id.buttonCancel);
                cancelledEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                cancelledEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                approvedEstimatesButton = findViewById(R.id.buttonApproved);
                approvedEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                approvedEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                estimatesSearchEditText = findViewById(R.id.searchEditText);

                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();

                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

                if(!searchText.isEmpty()){
                    ArrayList<Estimate> estimatesSearchList = dbAdapter.searchEstimates(searchText);
                    if(estimatesSearchList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(estimatesSearchList);
                    }
                }
                else{
                    ArrayList<Estimate> estimatesList = dbAdapter.retrieveEstimates();
                    if(estimatesList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(estimatesList);
                    }
                }
            }
        });

        pendingEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pendingEstimatesButton = findViewById(R.id.buttonPending);
                pendingEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_selected));
                pendingEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                selectedEstimateStatus = "Pending";

                allEstimatesButton = findViewById(R.id.buttonAll);
                allEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                allEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                overdueEstimatesButton = findViewById(R.id.buttonOverdue);
                overdueEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                overdueEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                cancelledEstimatesButton = findViewById(R.id.buttonCancel);
                cancelledEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                cancelledEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                approvedEstimatesButton = findViewById(R.id.buttonApproved);
                approvedEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                approvedEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();

                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

                if(!searchText.isEmpty()){
                    ArrayList<Estimate> estimatesSearchList = dbAdapter.searchEstimates(searchText);
                    if(estimatesSearchList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(estimatesSearchList);
                    }
                }
                else{
                    ArrayList<Estimate> estimatesPendingList = dbAdapter.retrievePendingEstimates();
                    if(estimatesList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesPendingList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(estimatesPendingList);
                    }
                }
            }
        });

        approvedEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                approvedEstimatesButton = findViewById(R.id.buttonApproved);
                approvedEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_selected));
                approvedEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                selectedEstimateStatus = "Approved";

                allEstimatesButton = findViewById(R.id.buttonAll);
                allEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                allEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                overdueEstimatesButton = findViewById(R.id.buttonOverdue);
                overdueEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                overdueEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                cancelledEstimatesButton = findViewById(R.id.buttonCancel);
                cancelledEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                cancelledEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                pendingEstimatesButton = findViewById(R.id.buttonPending);
                pendingEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                pendingEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();

                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

                if(!searchText.isEmpty()){
                    ArrayList<Estimate> estimatesSearchList = dbAdapter.searchEstimates(searchText);
                    if(estimatesSearchList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(estimatesSearchList);
                    }
                }
                else{
                    ArrayList<Estimate> approvedEstimatesList = dbAdapter.retrieveApprovedEstimates();
                    if(estimatesList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, approvedEstimatesList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(approvedEstimatesList);
                    }
                }
            }
        });

        overdueEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                overdueEstimatesButton = findViewById(R.id.buttonOverdue);
                overdueEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_selected));
                overdueEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                selectedEstimateStatus = "Overdue";

                allEstimatesButton = findViewById(R.id.buttonAll);
                allEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                allEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                approvedEstimatesButton = findViewById(R.id.buttonApproved);
                approvedEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                approvedEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                cancelledEstimatesButton = findViewById(R.id.buttonCancel);
                cancelledEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                cancelledEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                pendingEstimatesButton = findViewById(R.id.buttonPending);
                pendingEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                pendingEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();

                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

                if(!searchText.isEmpty()){
                    ArrayList<Estimate> overdueEstimatesSearchList = dbAdapter.searchOverdueEstimates(searchText);
                    if(overdueEstimatesSearchList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, overdueEstimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(overdueEstimatesSearchList);
                    }
                }
                else{
                    ArrayList<Estimate> overdueEstimatesList = dbAdapter.retrieveOverdueEstimates();
                    if(estimatesList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, overdueEstimatesList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(overdueEstimatesList);
                    }
                }
            }
        });

        cancelledEstimatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelledEstimatesButton = findViewById(R.id.buttonCancel);
                cancelledEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_selected));
                cancelledEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                selectedEstimateStatus = "Cancelled";

                allEstimatesButton = findViewById(R.id.buttonAll);
                allEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                allEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                approvedEstimatesButton = findViewById(R.id.buttonApproved);
                approvedEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                approvedEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                overdueEstimatesButton = findViewById(R.id.buttonOverdue);
                overdueEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                overdueEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                pendingEstimatesButton = findViewById(R.id.buttonPending);
                pendingEstimatesButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.button_bg_default));
                pendingEstimatesButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.button_text_default));

                estimatesSearchEditText = findViewById(R.id.searchEditText);
                String searchText = Objects.requireNonNull(estimatesSearchEditText.getText()).toString();

                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

                if(!searchText.isEmpty()){
                    ArrayList<Estimate> estimatesSearchList = dbAdapter.searchEstimates(searchText);
                    if(estimatesSearchList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, estimatesSearchList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(estimatesSearchList);
                    }
                }
                else{
                    ArrayList<Estimate> cancelledEstimatesList = dbAdapter.retrieveCancelledEstimates();
                    if(estimatesList.isEmpty()){
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                        activityEstimatesBinding.emptyView.setText(R.string.noResult);
                        findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                    else{
                        EstimatesListAdapter estimates_list_adapter = new EstimatesListAdapter(Estimates.this, cancelledEstimatesList);
                        findViewById(R.id.emptyView).setVisibility(View.GONE);
                        activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimates_list_adapter);
                        estimateListAdapter.updateEstimates(cancelledEstimatesList);
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
                            ArrayList<Estimate> allEstimatesList = dbAdapter.searchEstimates(searchText);
                            if (!allEstimatesList.isEmpty()) {
                                EstimatesListAdapter all_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, allEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(all_estimates_list_adapter);
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
                            ArrayList<Estimate> pendingEstimatesList = dbAdapter.searchPendingEstimates(searchText);
                            if (!pendingEstimatesList.isEmpty()) {
                                EstimatesListAdapter pending_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, pendingEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);

                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(pending_estimates_list_adapter);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noResult);
                                Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                                searchResultToast.show();
                            }
                            break;
                        case "Approved":
                            ArrayList<Estimate> approvedEstimatesList = dbAdapter.searchApprovedEstimates(searchText);
                            if (!approvedEstimatesList.isEmpty()) {
                                EstimatesListAdapter approved_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, approvedEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(approved_estimates_list_adapter);
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
                            ArrayList<Estimate> cancelledEstimatesList = dbAdapter.searchCancelledEstimates(searchText);
                            if (!cancelledEstimatesList.isEmpty()) {
                                EstimatesListAdapter cancelled_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, cancelledEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(cancelled_estimates_list_adapter);
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
                            ArrayList<Estimate> overdueEstimatesList = dbAdapter.searchOverdueEstimates(searchText);
                            if (!overdueEstimatesList.isEmpty()) {
                                EstimatesListAdapter overdue_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, overdueEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(overdue_estimates_list_adapter);
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
                else{
                    switch(selectedEstimateStatus){
                        case "All":
                            ArrayList<Estimate> allEstimatesList = dbAdapter.retrieveEstimates();
                            if (!allEstimatesList.isEmpty()) {
                                EstimatesListAdapter all_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, allEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(all_estimates_list_adapter);
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
                            ArrayList<Estimate> pendingEstimatesList = dbAdapter.retrievePendingEstimates();
                            if (!pendingEstimatesList.isEmpty()) {
                                EstimatesListAdapter pending_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, pendingEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);

                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(pending_estimates_list_adapter);
                            }
                            else{
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
                                findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
                                activityEstimatesBinding.emptyView.setText(R.string.noResult);
                                Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                                searchResultToast.show();
                            }
                            break;
                        case "Approved":
                            ArrayList<Estimate> approvedEstimatesList = dbAdapter.retrieveApprovedEstimates();
                            if (!approvedEstimatesList.isEmpty()) {
                                EstimatesListAdapter approved_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, approvedEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(approved_estimates_list_adapter);
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
                            ArrayList<Estimate> cancelledEstimatesList = dbAdapter.retrieveCancelledEstimates();
                            if (!cancelledEstimatesList.isEmpty()) {
                                EstimatesListAdapter cancelled_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, cancelledEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(cancelled_estimates_list_adapter);
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
                            ArrayList<Estimate> overdueEstimatesList = dbAdapter.retrieveOverdueEstimates();
                            if (!overdueEstimatesList.isEmpty()) {
                                EstimatesListAdapter overdue_estimates_list_adapter = new EstimatesListAdapter(Estimates.this, overdueEstimatesList);
                                findViewById(R.id.emptyView).setVisibility(View.GONE);
                                activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
                                activityEstimatesBinding.estimatesRecyclerView.setAdapter(overdue_estimates_list_adapter);
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