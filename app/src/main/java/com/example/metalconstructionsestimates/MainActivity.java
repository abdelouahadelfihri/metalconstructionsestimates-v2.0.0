package com.example.metalconstructionsestimates;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.metalconstructionsestimates.dashboard.Dashboard;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.modules.customers.AddCustomer;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.modules.estimates.Estimates;
import com.example.metalconstructionsestimates.modules.steels.Steels;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewCustomers, imageViewEstimates, imageViewSteels,
              imageViewDashboard, imageViewBackups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imageViewCustomers = findViewById(R.id.imageViewCustomers);
        imageViewEstimates = findViewById(R.id.imageViewEstimates);
        imageViewSteels = findViewById(R.id.imageViewSteels);
        imageViewDashboard = findViewById(R.id.imageViewDashboard);
        imageViewBackups = findViewById(R.id.imageViewBackups);

        imageViewCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Customers.class);
                startActivity(intent);
            }
        });

        imageViewEstimates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Estimates.class);
                startActivity(intent);
            }
        });

        imageViewSteels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Steels.class);
                startActivity(intent);
            }
        });

        imageViewDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
            }
        });

        imageViewBackups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BackUpRestore.class);
                startActivity(intent);
            }
        });

    }

}