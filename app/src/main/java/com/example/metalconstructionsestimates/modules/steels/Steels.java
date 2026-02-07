package com.example.metalconstructionsestimates.modules.steels;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.recyclerviewadapters.SteelsListAdapter;
import com.example.metalconstructionsestimates.databinding.ActivitySteelsBinding;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Steel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class Steels extends AppCompatActivity {
    Intent intent;
    SteelsListAdapter steelsListAdapter;
    TextInputEditText steelsSearchEditText;
    FloatingActionButton addSteel, clearSearchSteelForm, reloadSteelsList;
    ActivitySteelsBinding activitySteelsBinding;
    DBAdapter dbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySteelsBinding = ActivitySteelsBinding.inflate(getLayoutInflater());
        dbAdapter = new DBAdapter(getApplicationContext());
        setContentView(activitySteelsBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ArrayList<Steel> steelsList = dbAdapter.retrieveSteels();
        AtomicReference<RecyclerView> recyclerViewSteels = new AtomicReference<>(findViewById(R.id.steelsRecyclerView));

        if (steelsList.isEmpty()) {
            activitySteelsBinding.steelsRecyclerView.setVisibility(View.GONE);
            activitySteelsBinding.noSteelsTextView.setVisibility(View.VISIBLE);
            activitySteelsBinding.noSteelsTextView.setText(R.string.noSteels);
        } else {
            activitySteelsBinding.steelsRecyclerView.setVisibility(View.VISIBLE);
            activitySteelsBinding.noSteelsTextView.setVisibility(View.GONE);
            steelsListAdapter = new SteelsListAdapter(this, steelsList);
            recyclerViewSteels.get().setHasFixedSize(true);
            recyclerViewSteels.get().setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerViewSteels.get().setAdapter(steelsListAdapter);
        }

        steelsSearchEditText = findViewById(R.id.searchEditText);

        steelsSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    String searchText = s.toString();
                    activitySteelsBinding.steelsRecyclerView.setLayoutManager(new LinearLayoutManager(Steels.this.getApplicationContext()));
                    DBAdapter db = new DBAdapter(getApplicationContext());
                    ArrayList<Steel> steelsSearchList = db.searchSteels(searchText);
                    if (!steelsSearchList.isEmpty()) {
                        SteelsListAdapter steels_list_adapter = new SteelsListAdapter(Steels.this, steelsSearchList);
                        findViewById(R.id.noSteelsTextView).setVisibility(View.GONE);
                        activitySteelsBinding.steelsRecyclerView.setVisibility(View.VISIBLE);
                        activitySteelsBinding.steelsRecyclerView.setAdapter(steels_list_adapter);
                    }
                    else{
                        activitySteelsBinding.steelsRecyclerView.setVisibility(View.GONE);
                        findViewById(R.id.noSteelsTextView).setVisibility(View.VISIBLE);
                        activitySteelsBinding.noSteelsTextView.setText(R.string.noResult);
                        Toast searchResultToast = Toast.makeText(getApplicationContext(), "No results found.", Toast.LENGTH_LONG);
                        searchResultToast.show();
                    }
                }
                else{
                    ArrayList<Steel> steelsList = dbAdapter.retrieveSteels();
                    AtomicReference<RecyclerView> recyclerViewSteels = new AtomicReference<>(findViewById(R.id.steelsRecyclerView));

                    if (steelsList.isEmpty()) {
                        activitySteelsBinding.steelsRecyclerView.setVisibility(View.GONE);
                        activitySteelsBinding.noSteelsTextView.setVisibility(View.VISIBLE);
                        activitySteelsBinding.noSteelsTextView.setText(R.string.noSteels);
                    } else {
                        activitySteelsBinding.steelsRecyclerView.setVisibility(View.VISIBLE);
                        activitySteelsBinding.noSteelsTextView.setVisibility(View.GONE);
                        steelsListAdapter = new SteelsListAdapter(Steels.this, steelsList);
                        recyclerViewSteels.get().setHasFixedSize(true);
                        recyclerViewSteels.get().setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerViewSteels.get().setAdapter(steelsListAdapter);
                    }
                }
            }
        });

        reloadSteelsList = findViewById(R.id.fab_refresh);
        reloadSteelsList.setOnClickListener(view -> {

            recyclerViewSteels.set(findViewById(R.id.steelsRecyclerView));
            ArrayList<Steel> steels_list = dbAdapter.retrieveSteels();
            steelsListAdapter = new SteelsListAdapter(Steels.this, steels_list);
            if (steels_list.isEmpty()) {
                recyclerViewSteels.get().setVisibility(View.GONE);
                findViewById(R.id.noSteelsTextView).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.noSteelsTextView).setVisibility(View.GONE);
                recyclerViewSteels.get().setVisibility(View.VISIBLE);
                recyclerViewSteels.get().setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerViewSteels.get().setAdapter(steelsListAdapter);
            }
        });

        addSteel = findViewById(R.id.fab_add);

        addSteel.setOnClickListener(view -> {
            intent = new Intent(Steels.this, AddSteel.class);
            startActivity(intent);
        });

        clearSearchSteelForm = findViewById(R.id.fab_clear);

        clearSearchSteelForm.setOnClickListener(view -> {

            steelsSearchEditText.getText().clear();

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