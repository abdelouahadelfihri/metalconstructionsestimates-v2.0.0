package com.example.metalconstructionsestimates.modules.estimatepreview;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Business;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.printings.PdfPrintAdapter;

import java.io.IOException;
import java.util.List;

public class EstimatePreviewActivity extends AppCompatActivity {

    private LinearLayout linesContainer;
    private TextView tvTotalBeforeVat, tvAllTotal, tvVat, tvDiscount;

    // New business & customer info TextViews
    private TextView tvBusinessName, tvBusinessAddress, tvBusinessPhone;
    private TextView tvCustomerName, tvCustomerAddress, tvCustomerPhone;
    private TextView tvEstimateLineProduct, tvEstimateLineQty, tvEstimateLineUnitPrice,
            tvEstimateLineTotal;
    private File cachedPdf;
    String productType;
    private List<EstimateLine> estimateLines;

    float productX, qtyX, unitPriceX, totalX;

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

        ImageView btnDownloadPdf = findViewById(R.id.btnDownloadPdf);
        ImageView btnPrint = findViewById(R.id.btnPrint);
        ImageView btnSendMail = findViewById(R.id.btnSendMail);

        // Example: Fill estimateLines dynamically from database or intent
        estimateLines = dbAdapter.searchEstimateLines(Integer.parseInt(estimateId)); // Replace with your data source
        fillEstimateLines();

        btnDownloadPdf.setOnClickListener(v -> downloadPdf());
        btnPrint.setOnClickListener(v -> {
            File pdf = getOrCreatePdf();

            if (pdf != null && pdf.exists()) {
                printPdf(pdf);
            } else {
                Toast.makeText(this, "PDF not available", Toast.LENGTH_SHORT).show();
            }
        });



        btnSendMail.setOnClickListener(v -> {

            assert customer != null;
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                Toast.makeText(this, "Customer email missing", Toast.LENGTH_SHORT).show();
                return;
            }

            File pdf = getOrCreatePdf();

            if (pdf != null && pdf.exists()) {
                sendPdfByEmail(customer.getEmail(), pdf);
            } else {
                Toast.makeText(this, "PDF not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getOrCreatePdf() {
        if (cachedPdf == null || !cachedPdf.exists()) {
            cachedPdf = createPdfFile();
        }
        return cachedPdf;
    }

    private void downloadPdf() {

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        int y = 50;

        // ===== SAME DRAWING CODE =====
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText("ESTIMATE", 40, y, paint);
        y += 40;

        paint.setTextSize(14);
        paint.setFakeBoldText(true);

        canvas.drawText("Business Information:", 40, y, paint);
        paint.setFakeBoldText(false);

        canvas.drawText(tvBusinessName.getText().toString(), 40, y + 20, paint);
        canvas.drawText(tvBusinessAddress.getText().toString(), 40, y + 40, paint);
        canvas.drawText(tvBusinessPhone.getText().toString(), 40, y + 60, paint);

        canvas.drawText("Customer Information:", 300, y, paint);
        canvas.drawText(tvCustomerName.getText().toString(), 300, y + 20, paint);
        canvas.drawText(tvCustomerAddress.getText().toString(), 300, y + 40, paint);
        canvas.drawText(tvCustomerPhone.getText().toString(), 300, y + 60, paint);

        y += 90;

        paint.setFakeBoldText(true);
        canvas.drawText("Product", 40, y, paint);
        canvas.drawText("Qty", 100, y, paint);
        canvas.drawText("Unit Price", 260, y, paint);
        canvas.drawText("Total", 380, y, paint);

        y += 20;
        paint.setFakeBoldText(false);

        for (EstimateLine line : estimateLines) {

            String productType = "";
            if (line.getSteel() != 0) {
                productType = dbAdapter.getSteelById(line.getSteel()).getType();
            }

            tvEstimateLineProduct = findViewById(R.id.estimate_line_product);
            tvEstimateLineQty = findViewById(R.id.estimate_line_qty);
            tvEstimateLineUnitPrice = findViewById(R.id.estimate_line_unit_price);
            tvEstimateLineTotal = findViewById(R.id.estimate_line_total);

            productX = 40;
            qtyX = 100;
            unitPriceX = 260;
            totalX = 380;

            canvas.drawText(productType,  productX, y, paint);

            canvas.drawText(String.valueOf(line.getQuantity()), qtyX, y, paint);

            canvas.drawText(String.format(java.util.Locale.getDefault(),"%.2f", line.getUnitPrice()), unitPriceX, y, paint);

            canvas.drawText(String.format(java.util.Locale.getDefault(),"%.2f", line.getTotalPrice()), totalX , y, paint);

            y += 20;
        }

        y += 20;
        canvas.drawText(tvTotalBeforeVat.getText().toString(), 300, y, paint);
        y += 20;
        canvas.drawText(tvDiscount.getText().toString(), 300, y, paint);
        y += 20;
        canvas.drawText(tvVat.getText().toString(), 300, y, paint);
        y += 20;
        canvas.drawText(tvAllTotal.getText().toString(), 300, y, paint);

        pdfDocument.finishPage(page);

        // ===== SAVE TO REAL DOWNLOADS =====
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME,
                    "Estimate_" + System.currentTimeMillis() + ".pdf");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(
                    MediaStore.Files.getContentUri("external"),
                    values
            );

            assert uri != null;
            OutputStream out = getContentResolver().openOutputStream(uri);
            pdfDocument.writeTo(out);

            if (out != null) out.close();

            Toast.makeText(this, "Saved to Downloads", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

    private void fillEstimateLines() {
        linesContainer.removeAllViews();

        for (EstimateLine line : estimateLines) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            productType = dbAdapter.getSteelById(line.getSteel()).getType();

            TextView productTextView = createCell(productType, 2);
            TextView qtyTextView = createCell(String.valueOf(line.getNetQuantityPlusMargin()), 1);
            TextView unitPriceTextView = createCell(
                    String.format(java.util.Locale.getDefault(),"%.2f", line.getUnitPrice()), 1);
            TextView totalTextView = createCell(
                    String.format(java.util.Locale.getDefault(),"%.2f", line.getTotalPrice()), 1);

            // Product first
            row.addView(productTextView);
            row.addView(qtyTextView);
            row.addView(unitPriceTextView);
            row.addView(totalTextView);

            linesContainer.addView(row);
        }

        tvTotalBeforeVat.setText(
                String.format(java.util.Locale.getDefault(),
                        "Total Before VAT: %.2f",
                        estimate.getExcludingTaxTotalAfterDiscount()));

        double discountRate = estimate.getDiscount();
        double discount = estimate.getExcludingTaxTotal() * discountRate / 100f;
        double vat = estimate.getExcludingTaxTotalAfterDiscount() * estimate.getVat() / 100f;

        tvDiscount.setText(
                String.format(java.util.Locale.getDefault(),
                        "Discount: %.2f = %.2f",
                        estimate.getDiscount(),
                        discount));

        tvVat.setText(
                String.format(java.util.Locale.getDefault(),
                        "VAT: %.2f = %.2f",
                        estimate.getVat(),
                        vat));

        tvAllTotal.setText(
                String.format(java.util.Locale.getDefault(),
                        "Total After VAT: %.2f",
                        estimate.getAllTaxIncludedTotal()));
    }

    private TextView createCell(String text, int weight) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    private File createPdfFile() {

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        int y = 50;

        // ================= TITLE =================
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText("ESTIMATE", 40, y, paint);

        y += 40;

        // ================= BUSINESS =================
        paint.setTextSize(14);
        paint.setFakeBoldText(true);

        canvas.drawText("Business Information:", 40, y, paint);
        paint.setFakeBoldText(false);

        canvas.drawText(tvBusinessName.getText().toString(), 40, y + 20, paint);
        canvas.drawText(tvBusinessAddress.getText().toString(), 40, y + 40, paint);
        canvas.drawText(tvBusinessPhone.getText().toString(), 40, y + 60, paint);

        // ================= CUSTOMER =================
        canvas.drawText("Customer Information:", 300, y, paint);
        canvas.drawText(tvCustomerName.getText().toString(), 300, y + 20, paint);
        canvas.drawText(tvCustomerAddress.getText().toString(), 300, y + 40, paint);
        canvas.drawText(tvCustomerPhone.getText().toString(), 300, y + 60, paint);

        y += 90;

        // ================= TABLE HEADER =================
        paint.setFakeBoldText(true);

        canvas.drawText("Product", 40, y, paint);
        canvas.drawText("Qty", 100, y, paint);
        canvas.drawText("Unit Price", 260, y, paint);
        canvas.drawText("Total", 380, y, paint);

        y += 20;
        paint.setFakeBoldText(false);

        // ================= TABLE LINES =================
        for (EstimateLine line : estimateLines) {

            String productType = "";
            if (line.getSteel() != 0) {
                productType = dbAdapter.getSteelById(line.getSteel()).getType();
            }

            tvEstimateLineProduct = findViewById(R.id.estimate_line_product);
            tvEstimateLineQty = findViewById(R.id.estimate_line_qty);
            tvEstimateLineUnitPrice = findViewById(R.id.estimate_line_unit_price);
            tvEstimateLineTotal = findViewById(R.id.estimate_line_total);

            productX = 40;
            qtyX = 100;
            unitPriceX = 260;
            totalX = 380;

            canvas.drawText(productType,  productX, y, paint);

            canvas.drawText(String.valueOf(line.getQuantity()), qtyX, y, paint);

            canvas.drawText(String.format(java.util.Locale.getDefault(),"%.2f", line.getUnitPrice()), unitPriceX, y, paint);

            canvas.drawText(String.format(java.util.Locale.getDefault(),"%.2f", line.getTotalPrice()), totalX , y, paint);

            y += 20;
        }

        // ================= TOTALS =================
        y += 20;

        canvas.drawText(tvTotalBeforeVat.getText().toString(), 300, y, paint);
        y += 20;

        canvas.drawText(tvDiscount.getText().toString(), 300, y, paint);
        y += 20;

        canvas.drawText(tvVat.getText().toString(), 300, y, paint);
        y += 20;

        canvas.drawText(tvAllTotal.getText().toString(), 300, y, paint);

        pdfDocument.finishPage(page);

        // ================= SAVE FILE =================
        File downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        assert downloadsDir != null;
        if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
            Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show();
            return null;
        }

        File file = new File(downloadsDir, "Estimate.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF created successfully", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();

        return file;
    }

    private void printPdf(File pdfFile) {
        PrintManager printManager =
                (PrintManager) getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter adapter =
                new PdfPrintAdapter(this, pdfFile.getAbsolutePath());

        printManager.print(
                "Estimate Print",
                adapter,
                new PrintAttributes.Builder().build()
        );
    }

    private void sendPdfByEmail(String email, File pdfFile) {
        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Estimate");
        intent.putExtra(Intent.EXTRA_TEXT, "Please find the estimate attached.");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Send estimate"));
    }
}