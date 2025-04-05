package com.example.metalconstructionsestimates;

import android.os.Bundle;
import android.widget.GridView;
import com.example.metalconstructionsestimates.db.DBAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    String[] values = {
            "Dashboard", "Estimates", "Customers", "Steels","Backups"
    } ;

    int[] images = {
            R.drawable.dashboard, R.drawable.estimates, R.drawable.customers, R.drawable.steels,R.drawable.backups
    };

    public DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        gridView = findViewById(R.id.griview);
        GridAdapter gridAdapter = new GridAdapter(this, values, images);
        gridView.setAdapter(gridAdapter);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

// Handle insets manually if needed
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (view, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            view.setPadding(0, statusBarInsets.top, 0, 0); // Adjust for the status bar height
            return insets;
        });

    }

}