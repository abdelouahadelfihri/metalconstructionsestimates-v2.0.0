package com.example.metalconstructionsestimates.modules.estimatepreview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.models.Business;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class EstimatePreviewActivity extends AppCompatActivity {

    private TextView tvBusinessInfo, tvCustomerInfo, tvEstimateInfo, tvTotals;
    private LinearLayout linesContainer;
    private Button btnDownloadPdf;

    private Estimate estimate;
    private Customer customer;
    private Business business;
    private List<EstimateLine> estimateLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_preview);

        tvBusinessInfo = findViewById(R.id.tvBusinessInfo);
        tvCustomerInfo = findViewById(R.id.tvCustomerInfo);
        tvEstimateInfo = findViewById(R.id.tvEstimateInfo);
        tvTotals = findViewById(R.id.tvTotals);
        linesContainer = findViewById(R.id.linesContainer);
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);

        // Receive data from previous activity
        estimate = (Estimate) getIntent().getSerializableExtra("estimate");
        customer = (Customer) getIntent().getSerializableExtra("customer");
        business = (Business) getIntent().getSerializableExtra("business");
        estimateLines = (List<EstimateLine>) getIntent().getSerializableExtra("lines");

        // Display the data
        displayPreview();

        btnDownloadPdf.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                generatePdf();
            }
        });
    }

    private void displayPreview() {
        if (business != null) {
            tvBusinessInfo.setText(
                    business.getName() + "\n" +
                            business.getAddress() + "\n" +
                            "Email: " + business.getEmail() + "\n" +
                            "Phone: " + business.getPhone());
        }

        if (customer != null) {
            tvCustomerInfo.setText(
                    customer.getName() + "\n" +
                            customer.getAddress() + "\n" +
                            "Tel: " + customer.getTelephone() + " / " + customer.getMobile());
        }

        if (estimate != null) {
            tvEstimateInfo.setText(
                    "Issued: " + estimate.getIssueDate() + "\n" +
                            "Expires: " + estimate.getExpirationDate() + "\n" +
                            "Status: " + estimate.getStatus());
        }

        if (estimateLines != null) {
            for (EstimateLine line : estimateLines) {
                TextView lineView = new TextView(this);
                lineView.setText(
                        "Steel ID: " + line.getSteel() +
                                " | Qty: " + line.getQuantity() +
                                " | Unit: " + line.getUnitPrice() +
                                " | Total: " + line.getTotalPrice());
                linesContainer.addView(lineView);
            }
        }

        if (estimate != null) {
            tvTotals.setText(
                    "Subtotal: " + estimate.getExcludingTaxTotal() + "\n" +
                            "Discount: " + estimate.getDiscount() + "\n" +
                            "VAT: " + estimate.getVat() + "\n" +
                            "Total: " + estimate.getAllTaxIncludedTotal());
        }
    }

    private void generatePdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        int x = 40, y = 50;

        paint.setTextSize(16);
        canvas.drawText("Estimate Preview", x, y, paint);
        y += 30;

        paint.setTextSize(12);
        canvas.drawText("Business: " + business.getName(), x, y, paint); y += 15;
        canvas.drawText("Customer: " + customer.getName(), x, y, paint); y += 15;
        canvas.drawText("Issue Date: " + estimate.getIssueDate(), x, y, paint); y += 25;

        for (EstimateLine line : estimateLines) {
            canvas.drawText("Steel " + line.getSteel() + " | Qty: " + line.getQuantity() +
                    " | Unit: " + line.getUnitPrice() + " | Total: " + line.getTotalPrice(), x, y, paint);
            y += 15;
        }

        y += 20;
        canvas.drawText("Total (TTC): " + estimate.getAllTaxIncludedTotal(), x, y, paint);

        document.finishPage(page);

        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloads, "Estimate_" + estimate.getId() + ".pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        document.close();
    }
}