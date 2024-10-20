package com.example.metalconstructionsestimates.modules.customers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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


    TextInputEditText customer_id,customer_name,customer_email,customer_phone,customer_mobile,customer_fax,customer_address;
    FloatingActionButton addCustomer,searchCustomers,clearCustomerForm;

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
            binding.recyclerViewCustomers.setVisibility(View.GONE);
            noCustomersTextView.setVisibility(View.VISIBLE);
        }
        else{
            noCustomersTextView.setVisibility(View.GONE);
            binding.recyclerViewCustomers.setVisibility(View.VISIBLE);
            binding.recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            binding.recyclerViewCustomers.setAdapter(customersListAdapter);
        }

        addCustomer = findViewById(R.id.fab_add_customer);
        searchCustomers = findViewById(R.id.fab_search_customer);
        clearCustomerForm = findViewById(R.id.fab_clear_customer_form);

        addCustomer.setOnClickListener(view -> {
            Intent intent = new Intent(Customers.this, AddCustomer.class);
            startActivity(intent);
        });

        searchCustomers.setOnClickListener(view -> {

            customer_id = Customers.this.findViewById(R.id.editText_customer_id_customers);
            customer_name = Customers.this.findViewById(R.id.editText_customer_name_customers);
            customer_email = Customers.this.findViewById(R.id.editText_customer_email_customers);
            customer_phone = Customers.this.findViewById(R.id.editText_customer_phone_customers);
            customer_mobile = Customers.this.findViewById(R.id.editText_customer_mobile_customers);
            customer_fax = Customers.this.findViewById(R.id.editText_customer_fax_customers);
            customer_address = Customers.this.findViewById(R.id.editText_customer_address_customers);

            Customer customer = new Customer();

            if(customer_id.getText().toString().isEmpty() &&
            customer_name.getText().toString().isEmpty() &&
            customer_email.getText().toString().isEmpty() &&
            customer_phone.getText().toString().isEmpty() &&
            customer_mobile.getText().toString().isEmpty() &&
            customer_fax.getText().toString().isEmpty() &&
            customer_address.getText().toString().isEmpty()) {
                Toast emptyFieldsToast = Toast.makeText(getApplicationContext(), "Champs vides", Toast.LENGTH_LONG);
                emptyFieldsToast.show();
            }
            else {
                if (!customer_id.getText().toString().isEmpty()) {
                    customer.setId(Integer.parseInt(customer_id.getText().toString()));
                } else {
                    customer.setId(null);
                }
                if (!customer_name.getText().toString().isEmpty()) {
                    customer.setName(customer_name.getText().toString());
                } else {
                    customer.setName(null);
                }
                if (!customer_email.getText().toString().isEmpty()) {
                    customer.setEmail(customer_email.getText().toString());
                } else {
                    customer.setEmail(null);
                }
                if (!customer_phone.getText().toString().isEmpty()) {
                    customer.setTelephone(customer_phone.getText().toString());
                } else {
                    customer.setTelephone(null);
                }

                if (!customer_mobile.getText().toString().isEmpty()) {
                    customer.setMobile(customer_mobile.getText().toString());
                } else {
                    customer.setMobile(null);
                }

                if (!customer_fax.getText().toString().isEmpty()) {
                    customer.setFax(customer_fax.getText().toString());
                } else {
                    customer.setFax(null);
                }

                if (!customer_address.getText().toString().isEmpty()) {
                    customer.setAddress(customer_address.getText().toString());
                } else {
                    customer.setAddress(null);
                }

                binding.recyclerViewCustomers.setLayoutManager(new LinearLayoutManager(Customers.this.getApplicationContext()));
                DBAdapter db = new DBAdapter(getApplicationContext());
                ArrayList<Customer> customersList = db.searchCustomers(customer);
                CustomersListAdapter customers_list_adapter = new CustomersListAdapter(Customers.this, customersList);
                if (customersList.isEmpty()) {
                    binding.recyclerViewCustomers.setVisibility(View.GONE);
                    findViewById(R.id.noCustomersTextView).setVisibility(View.VISIBLE);
                    Toast searchResultToast = Toast.makeText(getApplicationContext(), "Empty Search Result", Toast.LENGTH_LONG);
                    searchResultToast.show();
                } else {
                    findViewById(R.id.noCustomersTextView).setVisibility(View.GONE);
                    binding.recyclerViewCustomers.setVisibility(View.VISIBLE);
                    binding.recyclerViewCustomers.setAdapter(customers_list_adapter);
                }
            }
        });

        clearCustomerForm.setOnClickListener(view -> {
            customer_id = findViewById(R.id.editText_customer_id_customers);
            customer_name = findViewById(R.id.editText_customer_name_customers);
            customer_email = findViewById(R.id.editText_customer_email_customers);
            customer_phone = findViewById(R.id.editText_customer_phone_customers);
            customer_mobile = findViewById(R.id.editText_customer_mobile_customers);
            customer_fax = findViewById(R.id.editText_customer_fax_customers);
            customer_address = findViewById(R.id.editText_customer_address_customers);
            customer_id.getText().clear();
            customer_name.getText().clear();
            customer_email.getText().clear();
            customer_phone.getText().clear();
            customer_mobile.getText().clear();
            customer_fax.getText().clear();
            customer_address.getText().clear();
        });
    }
}