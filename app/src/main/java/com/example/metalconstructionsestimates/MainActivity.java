package com.example.metalconstructionsestimates;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Business;
import com.example.metalconstructionsestimates.modules.business.AddBusiness;
import com.example.metalconstructionsestimates.modules.business.BusinessDetails;
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
              imageViewDashboard, imageViewBusiness, imageViewBackups,
              imageViewSettings, imageViewExit;

    private static final long EXIT_TIME_INTERVAL = 2000; // 2 seconds
    private long lastExitPressTime = 0;

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
        imageViewBusiness = findViewById(R.id.imageViewBusiness);
        imageViewBackups = findViewById(R.id.imageViewBackups);
        imageViewSettings = findViewById(R.id.imageViewSettings);
        imageViewExit = findViewById(R.id.imageViewExit);

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
            } else if (id == R.id.nav_business) {

                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

                Business business = dbAdapter.getBusiness();

                dbAdapter.close();

                Intent intent;

                if(business == null){
                    intent = new Intent(MainActivity.this, AddBusiness.class);
                }
                else{
                    intent = new Intent(MainActivity.this, BusinessDetails.class);
                }

                startActivity(intent);

            } else if (id == R.id.nav_backup) {
                Intent intent = new Intent(MainActivity.this, BackUpRestore.class);
                startActivity(intent);
            }
            else if (id == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
            else if (id == R.id.nav_exit) {
                handleExitClick();
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

        imageViewBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                Business business = dbAdapter.getBusiness();
                dbAdapter.close();
                Intent intent;
                if(business == null){
                    intent = new Intent(MainActivity.this, AddBusiness.class);
                }
                else{
                    intent = new Intent(MainActivity.this, BusinessDetails.class);
                }
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
        imageViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleExitClick();
            }
        });
    }

    private void handleExitClick() {
        if (System.currentTimeMillis() - lastExitPressTime < EXIT_TIME_INTERVAL) {
            finish();
        } else {
            lastExitPressTime = System.currentTimeMillis();
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}