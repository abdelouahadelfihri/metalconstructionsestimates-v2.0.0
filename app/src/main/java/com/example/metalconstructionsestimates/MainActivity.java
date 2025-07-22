package com.example.metalconstructionsestimates;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.metalconstructionsestimates.dashboard.Dashboard;
import com.example.metalconstructionsestimates.dbbackuprestore.BackUpRestore;
import com.example.metalconstructionsestimates.modules.customers.Customers;
import com.example.metalconstructionsestimates.modules.estimates.Estimates;
import com.example.metalconstructionsestimates.modules.steels.Steels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewCustomers, imageViewEstimates, imageViewSteels,
              imageViewDashboard, imageViewBackups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View statusBarSpacer = findViewById(R.id.statusBarSpacer);
        ViewCompat.setOnApplyWindowInsetsListener(statusBarSpacer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = systemBars.top;
            v.setLayoutParams(params);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
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