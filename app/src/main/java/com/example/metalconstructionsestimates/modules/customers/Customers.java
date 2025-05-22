package com.example.metalconstructionsestimates.modules.customers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.CustomersListAdapter;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.metalconstructionsestimates.databinding.ActivityCustomersBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.Objects;

public class Customers extends AppCompatActivity {


    TextInputEditText customerSearchEditText;
    FloatingActionButton addCustomer,clearCustomerForm,reloadCustomersList;

    private ActivityCustomersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolBar = findViewById(R.id.toolbar_customers);
        setSupportActionBar(toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Context c = getApplicationContext();

        DBAdapter dbAdapter = new DBAdapter(c);
        ArrayList<Customer> listCustomers = dbAdapter.retrieveCustomers();
        TextView noCustomersTextView = findViewById(R.id.noCustomersTextView);
        CustomersListAdapter customersListAdapter = new CustomersListAdapter(this, listCustomers);

        if(listCustomers.isEmpty()){
            binding.customerRecyclerView.setVisibility(View.GONE);
            noCustomersTextView.setVisibility(View.VISIBLE);
        }
        else{
            noCustomersTextView.setVisibility(View.GONE);
            binding.customerRecyclerView.setVisibility(View.VISIBLE);
            binding.customerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            binding.customerRecyclerView.setAdapter(customersListAdapter);
        }

        addCustomer = findViewById(R.id.fab_add_customer);
        clearCustomerForm = findViewById(R.id.fab_clear_customers);
        reloadCustomersList = findViewById(R.id.fab_refresh_customers);

        addCustomer.setOnClickListener(view -> {
            Intent intent = new Intent(Customers.this, AddCustomer.class);
            startActivity(intent);
        });

        customerSearchEditText = findViewById(R.id.searchEditText_customers);

        customerSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString();
                if(searchText.isEmpty()){
                    Toast searchResultToast = Toast.makeText(getApplicationContext(), "Type for search,separate by comma", Toast.LENGTH_LONG);
                    searchResultToast.show();
                }
                else{
                    binding.customerRecyclerView.setLayoutManager(new LinearLayoutManager(Customers.this.getApplicationContext()));
                    DBAdapter db = new DBAdapter(getApplicationContext());
                    ArrayList<Customer> customersSearchList = db.searchCustomers(searchText);
                    if (!customersSearchList.isEmpty()) {
                        CustomersListAdapter customers_list_adapter = new CustomersListAdapter(Customers.this, customersSearchList);
                        findViewById(R.id.noCustomersTextView).setVisibility(View.GONE);
                        binding.customerRecyclerView.setVisibility(View.VISIBLE);
                        binding.customerRecyclerView.setAdapter(customers_list_adapter);
                    }
                    else{
                        binding.customerRecyclerView.setVisibility(View.GONE);
                        findViewById(R.id.noCustomersTextView).setVisibility(View.VISIBLE);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
            }
        });

        reloadCustomersList.setOnClickListener(view -> {

            binding.customerRecyclerView.setLayoutManager(new LinearLayoutManager(Customers.this.getApplicationContext()));
            DBAdapter db = new DBAdapter(getApplicationContext());
            ArrayList<Customer> customersList = db.retrieveCustomers();
            CustomersListAdapter customers_list_adapter = new CustomersListAdapter(Customers.this, customersList);
            if (customersList.isEmpty()) {
                binding.customerRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.noCustomersTextView).setVisibility(View.VISIBLE);
                Toast reloatResultToast = Toast.makeText(getApplicationContext(), "Customers List is empty", Toast.LENGTH_LONG);
                reloatResultToast.show();
            } else {
                findViewById(R.id.noCustomersTextView).setVisibility(View.GONE);
                binding.customerRecyclerView.setVisibility(View.VISIBLE);
                binding.customerRecyclerView.setAdapter(customers_list_adapter);
            }

        });

        clearCustomerForm.setOnClickListener(view -> {
            customerSearchEditText = findViewById(R.id.searchEditText_customers);
            customerSearchEditText.getText().clear();
        });
    }
}