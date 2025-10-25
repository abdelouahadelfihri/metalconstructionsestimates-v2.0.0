package com.example.metalconstructionsestimates;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.metalconstructionsestimates.modules.dashboard.Dashboard;
import com.example.metalconstructionsestimates.dbbackuprestore.BackUpRestore;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.modules.estimates.Estimates;
import com.example.metalconstructionsestimates.modules.steels.Steels;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewCustomers, imageViewEstimates, imageViewSteels,
              imageViewDashboard, imageViewBackups;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        imageViewCustomers = findViewById(R.id.imageViewCustomers);
        imageViewEstimates = findViewById(R.id.imageViewEstimates);
        imageViewSteels = findViewById(R.id.imageViewSteels);
        imageViewDashboard = findViewById(R.id.imageViewDashboard);
        imageViewBackups = findViewById(R.id.imageViewBackups);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Create the drawer toggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle menu item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_customers) {
                Intent intent = new Intent(MainActivity.this, Customers.class);
                startActivity(intent);
            } else if (id == R.id.nav_estimates) {
                Intent intent = new Intent(MainActivity.this, Estimates.class);
                startActivity(intent);
            } else if (id == R.id.nav_steels) {
                Intent intent = new Intent(MainActivity.this, Steels.class);
                startActivity(intent);
            } else if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
            } else if (id == R.id.nav_backup) {
                Intent intent = new Intent(MainActivity.this, BackUpRestore.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

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