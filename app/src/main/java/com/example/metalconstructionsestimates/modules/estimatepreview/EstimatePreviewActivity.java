package com.example.metalconstructionsestimates.modules.estimatepreview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Business;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class EstimatePreviewActivity extends AppCompatActivity {

    private LinearLayout linesContainer;
    private TextView tvTotalBeforeVat, tvAllTotal, tvVat, tvDiscount;

    // New business & customer info TextViews
    private TextView tvBusinessName, tvBusinessAddress, tvBusinessPhone;
    private TextView tvCustomerName, tvCustomerAddress, tvCustomerPhone;
    private ImageView btnDownloadPdf, btnPrint, btnSendMail;

    private List<EstimateLine> estimateLines;
    private double discountRate = 0.1; // 10% Discount

    private File generatedPdf;
    DBAdapter dbAdapter;
    Estimate estimate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_preview);

        // Business Info
        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvBusinessAddress = findViewById(R.id.tvBusinessAddress);
        tvBusinessPhone = findViewById(R.id.tvBusinessPhone);
        dbAdapter = new DBAdapter(getApplicationContext());
        Business business = dbAdapter.getBusiness();
        if (business != null) {
            tvBusinessName.setText(business.getName());
            tvBusinessAddress.setText(business.getAddress());
            tvBusinessPhone.setText(business.getPhone());
        } else {
            Toast.makeText(this, "No business record found. Please add your business info first.", Toast.LENGTH_LONG).show();
        }

        String estimateId = getIntent().getStringExtra("estimateId");
        assert estimateId != null;

        // Customer Info
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);

        estimate = dbAdapter.getEstimateById(Integer.parseInt(estimateId));
        Customer customer = dbAdapter.getCustomerById(estimate.getCustomer());
        if (customer != null) {
            tvCustomerName.setText(customer.getName());
            tvCustomerAddress.setText(customer.getAddress());
            tvCustomerPhone.setText(customer.getTelephone());
        } else {
            Toast.makeText(this, "No customer record found. Please add your business info first.", Toast.LENGTH_LONG).show();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linesContainer = findViewById(R.id.linesContainer);
        tvTotalBeforeVat = findViewById(R.id.tvTotalBeforeVat);
        tvAllTotal = findViewById(R.id.tvAllTotal);
        tvVat = findViewById(R.id.tvVat);
        tvDiscount = findViewById(R.id.tvDiscount);

        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);
        btnPrint = findViewById(R.id.btnPrint);
        btnSendMail = findViewById(R.id.btnSendMail);

        // Example: Fill estimateLines dynamically from database or intent
        estimateLines = dbAdapter.searchEstimateLines(Integer.parseInt(estimateId)); // Replace with your data source
        fillEstimateLines();

        btnDownloadPdf.setOnClickListener(v -> createPdf());
        btnPrint.setOnClickListener(v -> printPdf());
        btnSendMail.setOnClickListener(v -> sendEmail(getApplicationContext()));
    }

    private void fillEstimateLines() {
        linesContainer.removeAllViews();

        for (EstimateLine line : estimateLines) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView qtyTextView = createCell(String.valueOf(line.getNetQuantityPlusMargin()), 1);
            String productType = dbAdapter.getSteelById(line.getSteel()).getType();
            TextView productTextView = createCell(productType + "", 2); // Replace with product name if available
            TextView unitPriceTextView = createCell(String.format("%.2f", line.getUnitPrice()), 1);
            TextView totalTextView = createCell(String.format("%.2f", line.getTotalPrice()), 1);

            row.addView(qtyTextView);
            row.addView(productTextView);
            row.addView(unitPriceTextView);
            row.addView(totalTextView);

            linesContainer.addView(row);

        }


        tvTotalBeforeVat.setText(String.format("Total Before VAT: %.2f", estimate.getExcludingTaxTotal()));
        discountRate = estimate.getDiscount();
        double discount = estimate.getExcludingTaxTotal() * discountRate /100f;
        double vat = estimate.getExcludingTaxTotalAfterDiscount() * estimate.getVat() /100f;

        tvDiscount.setText(String.format("Discount: %.2f = %.2f", estimate.getDiscount(), discount));
        tvVat.setText(String.format("VAT: %.2f = %.2f", estimate.getVat(), vat));

        tvAllTotal.setText(String.format("Total After VAT: %.2f", estimate.getAllTaxIncludedTotal()));
    }

    private TextView createCell(String text, int weight) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    private void createPdf() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        int y = 50;

        // Title
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText("ESTIMATE", 40, y, paint);
        y += 40;

        // Business & Customer info
        paint.setTextSize(14);
        paint.setFakeBoldText(true);
        // Business Info
        canvas.drawText("Business Information:", 40, y, paint);
        paint.setFakeBoldText(false);
        canvas.drawText(tvBusinessName.getText().toString(), 40, y + 20, paint);
        canvas.drawText(tvBusinessAddress.getText().toString(), 40, y + 40, paint);
        canvas.drawText(tvBusinessPhone.getText().toString(), 40, y + 60, paint);

        // Customer Info
        canvas.drawText("Customer Information:", 300, y, paint);
        canvas.drawText(tvCustomerName.getText().toString(), 300, y + 20, paint);
        canvas.drawText(tvCustomerAddress.getText().toString(), 300, y + 40, paint);
        canvas.drawText(tvCustomerPhone.getText().toString(), 300, y + 60, paint);

        y += 80;

        // Table header
        paint.setFakeBoldText(true);
        canvas.drawText("Qty", 40, y, paint);
        canvas.drawText("Product", 100, y, paint);
        canvas.drawText("Unit Price", 300, y, paint);
        canvas.drawText("Total", 400, y, paint);
        y += 20;
        paint.setFakeBoldText(false);

        // Table rows
        for (EstimateLine line : estimateLines) {
            canvas.drawText(String.valueOf(line.getQuantity()), 40, y, paint);
            canvas.drawText(line.getSteel() + "", 100, y, paint); // Replace with product name
            canvas.drawText(String.format("%.2f", line.getUnitPrice()), 300, y, paint);
            canvas.drawText(String.format("%.2f", line.getTotalPrice()), 400, y, paint);
            y += 20;
        }

        // Totals
        y += 20;
        canvas.drawText(tvTotalBeforeVat.getText().toString(), 300, y, paint);
        y += 20;
        canvas.drawText(tvDiscount.getText().toString(), 300, y, paint);
        y += 20;
        canvas.drawText(tvVat.getText().toString(), 300, y, paint);
        y += 20;
        canvas.drawText(tvAllTotal.getText().toString(), 300, y, paint);

        pdfDocument.finishPage(page);

        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Estimates");
        if (!pdfDir.exists()) pdfDir.mkdirs();

        generatedPdf = new File(pdfDir, "Estimate.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(generatedPdf));
            Toast.makeText(this, "PDF saved: " + generatedPdf.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

    private void printPdf() {

        if (generatedPdf == null || !generatedPdf.exists()) {
            createPdf(); // generate automatically if not done
        }

        if (generatedPdf != null && generatedPdf.exists()) {
            Intent printIntent = new Intent(Intent.ACTION_VIEW);
            printIntent.setDataAndType(Uri.fromFile(generatedPdf), "application/pdf");
            printIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(printIntent);
        } else {
            Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendEmail(Context context) {

        if (generatedPdf == null || !generatedPdf.exists()) {
            createPdf(); // generate automatically if not done
        }

        if (generatedPdf != null && generatedPdf.exists()) {
            Uri pdfUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    generatedPdf
            );
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("application/pdf");
            Estimate estimate = dbAdapter.getEstimateById(Integer.parseInt(getIntent().getStringExtra("estimateId")));
            Customer customer = dbAdapter.getCustomerById(estimate.getCustomer());
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{customer.getEmail()});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your PDF File");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the attached PDF file.");
            emailIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(emailIntent, "Send email using:"));
        } else {
            Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
        }

    }

}