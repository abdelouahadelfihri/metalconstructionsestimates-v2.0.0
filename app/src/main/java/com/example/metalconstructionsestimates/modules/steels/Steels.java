package com.example.metalconstructionsestimates.modules.steels;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.metalconstructionsestimates.arraysadapters.CustomersListAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.SteelsListAdapter;
import com.example.metalconstructionsestimates.databinding.ActivitySteelsBinding;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Steel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class Steels extends AppCompatActivity {
    Intent intent;
    SteelsListAdapter steelsListAdapter;
    TextInputEditText customerSearchEditText;
    FloatingActionButton addSteel, clearSearchSteelForm, reloadSteelsList;
    ActivitySteelsBinding activitySteelsBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySteelsBinding = ActivitySteelsBinding.inflate(getLayoutInflater());
        setContentView(activitySteelsBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar_steels);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBAdapter db = new DBAdapter(getApplicationContext());
        ArrayList<Steel> steelsList = db.retrieveSteels();
        AtomicReference<RecyclerView> recyclerViewSteels = new AtomicReference<>(findViewById(R.id.recycler_view_steels));

        if (steelsList.isEmpty()) {
            activitySteelsBinding.recyclerViewSteels.setVisibility(View.GONE);
            activitySteelsBinding.emptyView.setVisibility(View.VISIBLE);
            activitySteelsBinding.emptyView.setText(R.string.noSteels);
        } else {
            activitySteelsBinding.recyclerViewSteels.setVisibility(View.VISIBLE);
            activitySteelsBinding.emptyView.setVisibility(View.GONE);
            steelsListAdapter = new SteelsListAdapter(this, steelsList);
            recyclerViewSteels.get().setHasFixedSize(true);
            recyclerViewSteels.get().setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerViewSteels.get().setAdapter(steelsListAdapter);
        }

        customerSearchEditText = findViewById(R.id.editText_search_customers);

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
                activitySteelsBinding.recyclerViewSteels.setLayoutManager(new LinearLayoutManager(Steels.this.getApplicationContext()));
                DBAdapter db = new DBAdapter(getApplicationContext());
                ArrayList<Steel> steelsSearchList = db.searchSteels(searchText);
                if (!steelsSearchList.isEmpty()) {
                    SteelsListAdapter steels_list_adapter = new SteelsListAdapter(Steels.this, customersSearchList);
                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                    activitySteelsBinding.recyclerViewSteels.setVisibility(View.VISIBLE);
                    activitySteelsBinding.recyclerViewSteels.setAdapter(customers_list_adapter);
                }
                else{
                    activitySteelsBinding.recyclerViewSteels.setVisibility(View.GONE);
                    findViewById(R.id.noCustomersTextView).setVisibility(View.VISIBLE);
                    Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                    searchResultToast.show();
                }
            }
        });

        reloadSteelsList = findViewById(R.id.fab_refresh_steels_list);
        reloadSteelsList.setOnClickListener(view -> {

            recyclerViewSteels.set(findViewById(R.id.recycler_view_steels));
            DBAdapter db1 = new DBAdapter(getApplicationContext());
            ArrayList<Steel> steels_list = db1.retrieveSteels();
            steelsListAdapter = new SteelsListAdapter(Steels.this, steels_list);
            if (steels_list.isEmpty()) {
                recyclerViewSteels.get().setVisibility(View.GONE);
                findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.empty_view).setVisibility(View.GONE);
                recyclerViewSteels.get().setVisibility(View.VISIBLE);
                recyclerViewSteels.get().setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerViewSteels.get().setAdapter(steelsListAdapter);
            }
        });

        addSteel = findViewById(R.id.fab_add_steel);

        addSteel.setOnClickListener(view -> {
            intent = new Intent(Steels.this, AddSteel.class);
            startActivity(intent);
        });

        clearSearchSteelForm = findViewById(R.id.fab_clear_steel_form);

        clearSearchSteelForm.setOnClickListener(view -> {

        });
    }
}