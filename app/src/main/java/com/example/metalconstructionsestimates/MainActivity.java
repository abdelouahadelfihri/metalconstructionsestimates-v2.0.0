package com.example.metalconstructionsestimates;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.metalconstructionsestimates.db.DBAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    ImageView imageViewCustomers, imageViewEstimates, imageViewSteels,
              imageViewDashboard, imageViewBackups;

    public DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imageViewCustomers = findViewById(R.id.imageViewCustomers);
        imageViewEstimates = findViewById(R.id.imageViewEstimates);
        imageViewSteels = findViewById(R.id.imageViewSteels);
        imageViewDashboard = findViewById(R.id.imageViewDashboard);
        imageViewBackups = findViewById(R.id.imageViewBackups);
    }

}