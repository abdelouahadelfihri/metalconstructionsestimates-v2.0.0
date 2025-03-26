package com.example.metalconstructionsestimates;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.db.DBHelper;
import com.example.metalconstructionsestimates.dbbackuprestore.google.GoogleDriveActivity;
import com.example.metalconstructionsestimates.dbbackuprestore.google.GoogleDriveApiDataRepository;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.modules.steels.AddSteel;
import com.example.metalconstructionsestimates.modules.steels.Steels;
import com.google.android.gms.common.api.ApiException;
import com.google.api.services.drive.Drive;
import android.net.Uri;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;
import com.example.metalconstructionsestimates.db.IntermediateDBAdapter;


public class MainActivity extends AppCompatActivity {

    GridView gridView;
    String[] values = {
            "Dashboard", "Estimates", "Customers", "Steels","Backups Restore"
    } ;

    int[] images = {
            R.drawable.dashboard, R.drawable.estimates, R.drawable.customers, R.drawable.steels,R.drawable.backups
    };

    public DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolBar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolBar);
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