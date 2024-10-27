package com.example.metalconstructionsestimates;

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
import androidx.documentfile.provider.DocumentFile;

import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.db.DBHelper;
import com.example.metalconstructionsestimates.db.IntermediateDBAdapter;
import com.example.metalconstructionsestimates.dbbackuprestore.google.GoogleDriveActivity;
import com.example.metalconstructionsestimates.dbbackuprestore.google.GoogleDriveApiDataRepository;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.google.android.gms.common.api.ApiException;
import com.google.api.services.drive.Drive;

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
    IntermediateDBAdapter intermediateDBAdapter;
    DBAdapter dbAdapter;
    public static final String INTERMEDIATE_DB_LOCATION = "/data/data/com.example.metalconstructionsestimates/databases/intermediateestimatesdb";
    public static final String DB_LOCATION = "/data/data/com.example.metalconstructionsestimates/databases/estimatesdb" ;
    private GoogleDriveApiDataRepository googleDriveRepository;


    private ExecutorService executor1;
    private ExecutorService executor2;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up_restore);
        Button googleDriveSignIn = findViewById(R.id.googleSignIn);
        Button localBackup = findViewById(R.id.localBackup);
        Button localRestore = findViewById(R.id.localRestore);
        Button googleDriveBackup = findViewById(R.id.googleDriveBackup);
        Button googleDriveRestore = findViewById(R.id.googleDriveRestore);

        executor1 = Executors.newSingleThreadExecutor();
        executor2 = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        localRestore.setOnClickListener(v -> pickFile());

        googleDriveRestore.setOnClickListener(v -> showProgressDialogAndExecuteTask(
                executor2, "Google Drive Restore...", this::performGoogleDriveRestore));

        googleDriveSignIn.setOnClickListener(v -> {
            BackUpRestore.this.startGoogleDriveSignIn();
        });

        localBackup.setOnClickListener(v -> {
            pickDirectory();
        });

        googleDriveBackup.setOnClickListener(v -> {
            File db = new File(DB_LOCATION);

            if (googleDriveRepository == null) {
                BackUpRestore.this.showMessage("Google Drive sign in failed");
            }
            else {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                SQLiteDatabase estimatesDatabase = dbHelper.getWritableDatabase();
                googleDriveRepository.uploadFile(db, GOOGLE_DRIVE_DB_LOCATION)
                        .addOnSuccessListener(r -> BackUpRestore.this.showMessage("Upload success"))
                        .addOnFailureListener(e -> {
                            Log.e(LOG_TAG, "error upload file", e);
                            BackUpRestore.this.showMessage("Error upload");
                        });
            }
        });

    }

    private void showProgressDialogAndExecuteTask(ExecutorService executor, String loadingMessage, Runnable task) {
        View progressView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);
        AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setView(progressView)
                .setCancelable(false)
                .create();

        TextView progressText = progressView.findViewById(R.id.progressText);
        progressText.setText(loadingMessage);
        progressDialog.show();

        executor.execute(() -> {
            task.run();
            handler.post(progressDialog::dismiss);
        });
    }

    private void performGoogleDriveRestore() {
            // Simulate long-running task
            try {
                // Simulate long-running task
                if (googleDriveRepository == null) {
                    BackUpRestore.this.showMessage("Google Drive sign in failed");
                }
                else {
                    File db = new File(INTERMEDIATE_DB_LOCATION);
                    db.getParentFile().mkdirs();
                    db.delete();
                    googleDriveRepository.downloadFile(db, GOOGLE_DRIVE_DB_LOCATION)
                            .addOnSuccessListener(r -> {
                                updateActualDbFromIntermediateDb();
                                BackUpRestore.this.showMessage("Database restored from Google Drive");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(LOG_TAG, "error download file", e);
                                BackUpRestore.this.showMessage("Error download");
                            });

                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor1.shutdown();
        executor2.shutdown();
    }


    @Override
    protected void onGoogleDriveSignedInSuccess(Drive driveApi){
        showMessage("Google Drive Client is ready");
        googleDriveRepository = new GoogleDriveApiDataRepository(driveApi);
    }

    @Override
    protected void onGoogleDriveSignedInFailed(ApiException exception){
        showMessage("Google Drive sign in failed");
        Log.e(LOG_TAG,"error google drive sign in", exception);
    }


    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        activityResultPickFileLauncher.launch(intent);
    }

    public void restoreDatabase(Uri fileUri){

    String destinationFilePath = getDatabasePath("intermediateestimatesdb").getAbsolutePath();

    DocumentFile sourceFile = DocumentFile.fromSingleUri(getApplicationContext(), fileUri);

    try {
        // Read the database content into a byte array
        InputStream fis = getApplicationContext().getContentResolver().openInputStream(sourceFile.getUri());
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();

        // Write the content to the new file
        FileOutputStream fos = new FileOutputStream(destinationFilePath);
        fos.write(buffer);
        fos.close();
        updateActualDbFromIntermediateDb();
        runOnUiThread(() -> Toast.makeText(this, "Database restored successfully!", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
        e.printStackTrace();
        runOnUiThread(() -> Toast.makeText(this, "Error restoring database", Toast.LENGTH_SHORT).show());
        }
    }

    private void pickDirectory() {

    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    activityResultPickFolderLauncher.launch(intent);

    }

    public ActivityResultLauncher<Intent> activityResultPickFolderLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Uri uri = intent.getData();
                    DocumentFile documentFile = DocumentFile.fromTreeUri(getApplicationContext(), uri);
                    DocumentFile estimatesdb_backup = documentFile.createFile(null,"estimatesdb_backup" + System.currentTimeMillis());

                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();

                    try {
                        // Read the database content into a byte array
                        FileInputStream fis = new FileInputStream(db.getPath());
                        byte[] buffer = new byte[fis.available()];
                        fis.read(buffer);
                        fis.close();

                        // Write the content to the new file
                        OutputStream os = getApplicationContext().getContentResolver().openOutputStream(estimatesdb_backup.getUri());
                        os.write(buffer);
                        os.close();
                        Toast.makeText(this, "Database backup completed successfully", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }
                }
            }
    );

    public ActivityResultLauncher<Intent> activityResultPickFileLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Uri uri = intent.getData();
                    mergeFileContentWithDatabase(uri);
                }
            }
    );

    private void mergeFileContentWithDatabase(Uri fileUri) {
        View progressView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);
        AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setView(progressView)
                .setCancelable(false)
                .create();

        TextView progressText = progressView.findViewById(R.id.progressText);
        progressText.setText("Merging data...");

        progressDialog.show();

        executor1.execute(() -> {
            restoreDatabase(fileUri);
            handler.post(progressDialog::dismiss);
        });
    }

    public void updateActualDbFromIntermediateDb() {
        intermediateDBAdapter = new IntermediateDBAdapter(getApplicationContext());

        dbAdapter = new DBAdapter(getApplicationContext());

        ArrayList<Steel> steelsListFromIntermediateDB = intermediateDBAdapter.retrieveSteels();

        Steel steel = new Steel();

        for (int i = 0; i < steelsListFromIntermediateDB.size(); i++) {

            Integer steelId = steelsListFromIntermediateDB.get(i).getId();

            steel.setId(steelsListFromIntermediateDB.get(i).getId());
            steel.setType(steelsListFromIntermediateDB.get(i).getType());
            steel.setGeometricShape(steelsListFromIntermediateDB.get(i).getGeometricShape());
            steel.setUnit(steelsListFromIntermediateDB.get(i).getUnit());
            steel.setWeight(steelsListFromIntermediateDB.get(i).getWeight());

            if (dbAdapter.getSteelById(steelId) == null) {
                dbAdapter.saveSteel(steel);
            } else {
                dbAdapter.updateSteel(steel);
            }

        }

        ArrayList<Customer> customersListFromIntermediateDB = intermediateDBAdapter.retrieveCustomers();
        Customer customer;

        for (int i = 0; i < customersListFromIntermediateDB.size(); i++) {

            Integer customerId = customersListFromIntermediateDB.get(i).getId();

            customer = new Customer();

            customer.setId(customersListFromIntermediateDB.get(i).getId());
            customer.setName(customersListFromIntermediateDB.get(i).getName());
            customer.setEmail(customersListFromIntermediateDB.get(i).getEmail());
            customer.setTelephone(customersListFromIntermediateDB.get(i).getTelephone());
            customer.setMobile(customersListFromIntermediateDB.get(i).getMobile());
            customer.setFax(customersListFromIntermediateDB.get(i).getFax());
            customer.setAddress(customersListFromIntermediateDB.get(i).getAddress());

            if (dbAdapter.getCustomerById(customerId) == null) {
                dbAdapter.saveCustomer(customer);
            } else {
                dbAdapter.updateCustomer(customer);
            }
        }

        ArrayList<Estimate> estimatesListFromIntermediateDB = intermediateDBAdapter.retrieveEstimates();

        Estimate estimate;

        for (int i = 0; i < estimatesListFromIntermediateDB.size(); i++) {

            Integer estimateId = estimatesListFromIntermediateDB.get(i).getId();

            estimate = new Estimate();

            estimate.setId(estimatesListFromIntermediateDB.get(i).getId());
            estimate.setCustomer(estimatesListFromIntermediateDB.get(i).getCustomer());
            estimate.setDoneIn(estimatesListFromIntermediateDB.get(i).getDoneIn());
            estimate.setIssueDate(estimatesListFromIntermediateDB.get(i).getIssueDate());
            estimate.setExpirationDate(estimatesListFromIntermediateDB.get(i).getExpirationDate());
            estimate.setIsEstimatePaid(estimatesListFromIntermediateDB.get(i).getIsEstimatePaid());
            estimate.setExcludingTaxTotal(estimatesListFromIntermediateDB.get(i).getExcludingTaxTotal());
            estimate.setDiscount(estimatesListFromIntermediateDB.get(i).getDiscount());
            estimate.setExcludingTaxTotalAfterDiscount(estimatesListFromIntermediateDB.get(i).getExcludingTaxTotalAfterDiscount());
            estimate.setVat(estimatesListFromIntermediateDB.get(i).getVat());
            estimate.setAllTaxIncludedTotal(estimatesListFromIntermediateDB.get(i).getAllTaxIncludedTotal());

            if (dbAdapter.getEstimateById(estimateId) == null) {
                dbAdapter.saveEstimate(estimate);
            } else {
                dbAdapter.updateEstimate(estimate);
            }

            ArrayList<EstimateLine> estimateLinesListFromIntermediateDB = intermediateDBAdapter.retrieveEstimatesLines();
            EstimateLine estimateLine = new EstimateLine();


            for (i = 0; i < estimateLinesListFromIntermediateDB.size(); i++) {
                estimateLine.setId(estimateLinesListFromIntermediateDB.get(i).getId());
                estimateLine.setEstimate(estimateLinesListFromIntermediateDB.get(i).getEstimate());
                estimateLine.setSteel(estimateLinesListFromIntermediateDB.get(i).getSteel());
                estimateLine.setWeight(estimateLinesListFromIntermediateDB.get(i).getWeight());
                estimateLine.setLength(estimateLinesListFromIntermediateDB.get(i).getLength());
                estimateLine.setWidth(estimateLinesListFromIntermediateDB.get(i).getWidth());
                estimateLine.setHeight(estimateLinesListFromIntermediateDB.get(i).getHeight());
                estimateLine.setQuantity(estimateLinesListFromIntermediateDB.get(i).getQuantity());
                estimateLine.setTotal(estimateLinesListFromIntermediateDB.get(i).getTotal());
                estimateLine.setMargin(estimateLinesListFromIntermediateDB.get(i).getMargin());
                estimateLine.setNetQuantityPlusMargin(estimateLinesListFromIntermediateDB.get(i).getNetQuantityPlusMargin());
                estimateLine.setUnitPrice(estimateLinesListFromIntermediateDB.get(i).getUnitPrice());
                estimateLine.setTotalPrice(estimateLinesListFromIntermediateDB.get(i).getTotalPrice());

                Integer estimateLineId = estimateLinesListFromIntermediateDB.get(i).getId();

                if (dbAdapter.getEstimateLineById(estimateLineId) == null) {
                    dbAdapter.saveEstimateLine(estimateLine);
                } else {
                    dbAdapter.updateEstimateLine(estimateLine);
                }

            }
        }
    }
}