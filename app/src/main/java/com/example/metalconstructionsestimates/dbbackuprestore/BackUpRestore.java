package com.example.metalconstructionsestimates.dbbackuprestore;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.database.DBHelper;
import com.example.metalconstructionsestimates.database.IntermediateDBAdapter;
import com.example.metalconstructionsestimates.dbbackuprestore.google.GoogleDriveActivity;
import com.example.metalconstructionsestimates.dbbackuprestore.google.GoogleDriveApiDataRepository;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.google.android.gms.common.api.ApiException;
import com.google.api.services.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackUpRestore extends GoogleDriveActivity {

    private static final String LOG_TAG = "BackUpRestore";
    private static final String GOOGLE_DRIVE_DB_LOCATION = "db";
    private static final String INTERMEDIATE_DB_LOCATION = "/data/data/com.example.metalconstructionsestimates/databases/intermediateestimatesdb";
    private static final String DB_LOCATION = "/data/data/com.example.metalconstructionsestimates/databases/estimatesdb";
    private GoogleDriveApiDataRepository googleDriveRepository;
    private ExecutorService executorService;
    private Handler handler;
    private DBAdapter dbAdapter;
    private IntermediateDBAdapter intermediateDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up_restore);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI components
        Button googleDriveSignIn = findViewById(R.id.btnSignIn);
        Button localBackup = findViewById(R.id.btnLocalBackup);
        Button localRestore = findViewById(R.id.btnLocalRestore);
        Button googleDriveBackup = findViewById(R.id.btnDriveBackup);
        Button googleDriveRestore = findViewById(R.id.btnDriveRestore);

        // Initialize executor and handler
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Initialize database adapters
        dbAdapter = new DBAdapter(getApplicationContext());
        intermediateDBAdapter = new IntermediateDBAdapter(getApplicationContext());

        // Set up button listeners
        googleDriveSignIn.setOnClickListener(v -> startGoogleDriveSignIn());

        localBackup.setOnClickListener(v -> pickDirectory());

        localRestore.setOnClickListener(v -> pickFile());

        googleDriveBackup.setOnClickListener(v -> showProgressDialogAndExecuteTask(
                "Google Drive Backup...", this::performGoogleDriveBackup));

        googleDriveRestore.setOnClickListener(v -> showProgressDialogAndExecuteTask(
                "Google Drive Restore...", this::performGoogleDriveRestore));
    }

    private void showProgressDialogAndExecuteTask(String loadingMessage, Runnable task) {
        View progressView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);
        AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setView(progressView)
                .setCancelable(false)
                .create();

        TextView progressText = progressView.findViewById(R.id.progressText);
        progressText.setText(loadingMessage);
        progressDialog.show();

        executorService.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error in task: " + loadingMessage, e);
                handler.post(() -> showToastMessage("Error: " + e.getMessage()));
            } finally {
                handler.post(progressDialog::dismiss);
            }
        });
    }

    private void performGoogleDriveBackup() {
        if (googleDriveRepository == null) {
            handler.post(() -> showToastMessage("Google Drive sign-in failed"));
            return;
        }

        executorService.execute(() -> {
            try {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a temporary consistent backup using VACUUM INTO
                File tempDb = new File(getCacheDir(), "temp_db.db");
                if (tempDb.exists()) {
                    tempDb.delete();
                }
                db.execSQL("VACUUM INTO '" + tempDb.getAbsolutePath().replace("'", "''") + "'");

                // Upload the temp file
                googleDriveRepository.uploadFile(tempDb, GOOGLE_DRIVE_DB_LOCATION)
                        .addOnSuccessListener(r -> handler.post(() -> showToastMessage("Backup to Google Drive successful")))
                        .addOnFailureListener(e -> handler.post(() -> {
                            Log.e(LOG_TAG, "Error uploading file", e);
                            showToastMessage("Error during backup");
                        }));

                // Clean up temp file
                tempDb.delete();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error during Google Drive backup", e);
                handler.post(() -> showToastMessage("Error: " + e.getMessage()));
            }
        });
    }

    private void performGoogleDriveRestore() {
        if (googleDriveRepository == null) {
            handler.post(() -> showToastMessage("Google Drive sign-in failed"));
            return;
        }

        File db = new File(INTERMEDIATE_DB_LOCATION);
        db.getParentFile().mkdirs();
        if (db.exists()) {
            db.delete();
        }

        googleDriveRepository.downloadFile(db, GOOGLE_DRIVE_DB_LOCATION)
                .addOnSuccessListener(r -> {
                    updateActualDbFromIntermediateDb();
                    handler.post(() -> showToastMessage("Database restored from Google Drive"));
                })
                .addOnFailureListener(e -> handler.post(() -> {
                    Log.e(LOG_TAG, "Error downloading file", e);
                    showToastMessage("Error during restore");
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        // Close database adapters
        if (dbAdapter != null) {
            dbAdapter.close();
        }
        if (intermediateDBAdapter != null) {
            intermediateDBAdapter.close();
        }
    }

    @Override
    protected void onGoogleDriveSignedInSuccess(Drive driveApi) {
        handler.post(() -> showToastMessage("Google Drive Client is ready"));
        googleDriveRepository = new GoogleDriveApiDataRepository(driveApi);
    }

    @Override
    protected void onGoogleDriveSignedInFailed(ApiException exception) {
        handler.post(() -> showToastMessage("Google Drive sign-in failed"));
        Log.e(LOG_TAG, "Error during Google Drive sign-in", exception);
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activityResultPickFileLauncher.launch(intent);
    }

    private void restoreDatabase(Uri fileUri) {
        String destinationFilePath = getDatabasePath("intermediateestimatesdb").getAbsolutePath();
        DocumentFile sourceFile = DocumentFile.fromSingleUri(getApplicationContext(), fileUri);

        try {
            if (sourceFile == null) {
                throw new IOException("Source file is null");
            }

            // Read the database content into a byte array
            try (InputStream fis = getApplicationContext().getContentResolver().openInputStream(sourceFile.getUri())) {
                if (fis == null) {
                    throw new IOException("Failed to open input stream");
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                // Write the content to the new file
                try (FileOutputStream fos = new FileOutputStream(destinationFilePath)) {
                    fos.write(baos.toByteArray());
                }
            }

            updateActualDbFromIntermediateDb();
            handler.post(() -> showToastMessage("Database restored successfully!"));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error restoring database", e);
            handler.post(() -> showToastMessage("Error restoring database: " + e.getMessage()));
        }
    }

    private void pickDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        activityResultPickFolderLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> activityResultPickFolderLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri == null) {
                        handler.post(() -> showToastMessage("Failed to select directory"));
                        return;
                    }

                    DocumentFile documentFile = DocumentFile.fromTreeUri(getApplicationContext(), uri);
                    if (documentFile == null) {
                        handler.post(() -> showToastMessage("Invalid directory selected"));
                        return;
                    }

                    DocumentFile backupFile = documentFile.createFile("application/octet-stream", "estimatesdb_backup_" + System.currentTimeMillis());
                    if (backupFile == null) {
                        handler.post(() -> showToastMessage("Failed to create backup file"));
                        return;
                    }

                    executorService.execute(() -> {
                        try {
                            DBHelper dbHelper = new DBHelper(getApplicationContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

                            // Create a temporary consistent backup using VACUUM INTO
                            String tempBackupPath = getCacheDir() + "/temp_backup.db";
                            File tempFile = new File(tempBackupPath);
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                            db.execSQL("VACUUM INTO '" + tempBackupPath.replace("'", "''") + "'");  // Escape single quotes in path if needed

                            // Read from the temp file
                            try (FileInputStream fis = new FileInputStream(tempBackupPath)) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = fis.read(buffer)) != -1) {
                                    baos.write(buffer, 0, bytesRead);
                                }

                                try (OutputStream os = getApplicationContext().getContentResolver().openOutputStream(backupFile.getUri())) {
                                    if (os == null) {
                                        throw new IOException("Failed to open output stream");
                                    }
                                    os.write(baos.toByteArray());
                                    handler.post(() -> showToastMessage("Database backup completed successfully"));
                                }
                            }

                            // Clean up temp file
                            new File(tempBackupPath).delete();
                        } catch (Exception e) {
                            Log.e(LOG_TAG, "Error during backup", e);
                            handler.post(() -> showToastMessage("Error during backup: " + e.getMessage()));
                        }
                    });
                }
            });

    private final ActivityResultLauncher<Intent> activityResultPickFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        mergeFileContentWithDatabase(uri);
                    }
                }
            });

    private void mergeFileContentWithDatabase(Uri fileUri) {
        showProgressDialogAndExecuteTask("Merging data...", () -> restoreDatabase(fileUri));
    }

    private void updateActualDbFromIntermediateDb() {
        executorService.execute(() -> {
            try {

                dbAdapter.clearEstimatesLinesTable();
                dbAdapter.clearEstimatesTable();
                dbAdapter.clearCustomersTable();
                dbAdapter.clearSteelsTable();

                // Retrieve data from intermediate database
                ArrayList<Steel> steelsListFromIntermediateDB = intermediateDBAdapter.retrieveSteels();
                for (Steel steel : steelsListFromIntermediateDB) {
                    if (dbAdapter.getSteelById(steel.getId()) == null) {
                        dbAdapter.saveSteel(steel);
                    } else {
                        dbAdapter.updateSteel(steel);
                    }
                }

                ArrayList<Customer> customersListFromIntermediateDB = intermediateDBAdapter.retrieveCustomers();
                for (Customer customer : customersListFromIntermediateDB) {
                    if (dbAdapter.getCustomerById(customer.getId()) == null) {
                        dbAdapter.saveCustomer(customer);
                    } else {
                        dbAdapter.updateCustomer(customer);
                    }
                }

                ArrayList<Estimate> estimatesListFromIntermediateDB = intermediateDBAdapter.retrieveEstimates();
                for (Estimate estimate : estimatesListFromIntermediateDB) {
                    if (dbAdapter.getEstimateById(estimate.getId()) == null) {
                        dbAdapter.saveEstimate(estimate);
                    } else {
                        dbAdapter.updateEstimate(estimate);
                    }

                    ArrayList<EstimateLine> estimateLinesListFromIntermediateDB = intermediateDBAdapter.retrieveEstimatesLines();
                    for (EstimateLine estimateLine : estimateLinesListFromIntermediateDB) {
                        if (dbAdapter.getEstimateLineById(estimateLine.getId()) == null) {
                            dbAdapter.saveEstimateLine(estimateLine);
                        } else {
                            dbAdapter.updateEstimateLine(estimateLine);
                        }
                    }
                }
                handler.post(() -> Log.d(LOG_TAG, "Database update completed successfully"));
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error updating database from intermediate DB", e);
                handler.post(() -> showToastMessage("Error updating database: " + e.getMessage()));
            }
        });
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}