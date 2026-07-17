package com.example.metalconstructionsestimates.modules.estimatepreview;

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
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import com.example.metalconstructionsestimates.util.CurrencyManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EstimatePreviewActivity extends AppCompatActivity {

    private LinearLayout linesContainer;
    private TextView tvTotalBeforeVat, tvAllTotal, tvVat, tvDiscount;

    private TextView tvBusinessName, tvBusinessAddress, tvBusinessPhone;
    private TextView tvCustomerName, tvCustomerAddress, tvCustomerPhone;
    private ImageView btnDownloadPdf, btnPrint, btnSendMail;

    String productType;
    private List<EstimateLine> estimateLines;
    private double discountRate = 0.1;

    private File generatedPdf;
    DBAdapter dbAdapter;
    Estimate estimate;

    // ── Settings ───────────────────────────────────────────────────────────
    private CurrencyManager currencyManager;
    private String          currencyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_preview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // ── Load settings ──────────────────────────────────────────────────
        currencyManager = new CurrencyManager(this);
        currencyCode    = currencyManager.getActiveCurrencyCode();

        // ── Business info ──────────────────────────────────────────────────
        tvBusinessName    = findViewById(R.id.tvBusinessName);
        tvBusinessAddress = findViewById(R.id.tvBusinessAddress);
        tvBusinessPhone   = findViewById(R.id.tvBusinessPhone);

        dbAdapter = new DBAdapter(getApplicationContext());
        Business business = dbAdapter.getBusiness();
        if (business != null) {
            tvBusinessName.setText(business.getName());
            tvBusinessAddress.setText(business.getAddress());
            tvBusinessPhone.setText(business.getPhone());
        } else {
            Toast.makeText(this,
                    "No business record found. Please add your business info first.",
                    Toast.LENGTH_LONG).show();
        }

        String estimateId = getIntent().getStringExtra("estimateId");
        assert estimateId != null;

        // ── Customer info ──────────────────────────────────────────────────
        tvCustomerName    = findViewById(R.id.tvCustomerName);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);
        tvCustomerPhone   = findViewById(R.id.tvCustomerPhone);

        estimate = dbAdapter.getEstimateById(Integer.parseInt(estimateId));
        Customer customer = dbAdapter.getCustomerById(estimate.getCustomer());
        if (customer != null) {
            tvCustomerName.setText(customer.getName());
            tvCustomerAddress.setText(customer.getAddress());
            tvCustomerPhone.setText(customer.getTelephone());
        } else {
            Toast.makeText(this,
                    "No customer record found. Please add your business info first.",
                    Toast.LENGTH_LONG).show();
        }

        linesContainer   = findViewById(R.id.linesContainer);
        tvTotalBeforeVat = findViewById(R.id.tvTotalBeforeVat);
        tvAllTotal       = findViewById(R.id.tvAllTotal);
        tvVat            = findViewById(R.id.tvVat);
        tvDiscount       = findViewById(R.id.tvDiscount);

        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);
        btnPrint       = findViewById(R.id.btnPrint);
        btnSendMail    = findViewById(R.id.btnSendMail);

        estimateLines = dbAdapter.searchEstimateLines(Integer.parseInt(estimateId));
        fillEstimateLines();

        btnDownloadPdf.setOnClickListener(v -> {
            File f = createPdf();
            if (f != null) {
                Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_LONG).show();
            }
        });
        btnPrint.setOnClickListener(v -> printPdf(generatedPdf));
        btnSendMail.setOnClickListener(v -> {
            assert customer != null;
            sendPdfByEmail(customer.getEmail(), generatedPdf);
        });
    }

    private void fillEstimateLines() {
        linesContainer.removeAllViews();

        for (EstimateLine line : estimateLines) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            // Same order and weight as XML header:
            // Product(w1) → Qty(w1) → Unit Price(w1) → Total(w1)
            productType = dbAdapter.getSteelById(line.getSteel()).getType();
            TextView productTextView   = createCell(productType, 1);
            TextView qtyTextView       = createCell(String.valueOf(line.getNetQuantityPlusMargin()), 1);
            TextView unitPriceTextView = createCell(String.format(Locale.getDefault(), "%.2f", line.getUnitPrice()), 1);
            TextView totalTextView     = createCell(String.format(Locale.getDefault(), "%.2f", line.getTotalPrice()), 1);

            row.addView(productTextView);
            row.addView(qtyTextView);
            row.addView(unitPriceTextView);
            row.addView(totalTextView);

            linesContainer.addView(row);
        }

        // ── Totals with currency ───────────────────────────────────────────
        tvTotalBeforeVat.setText("Total Before VAT: "
                + currencyManager.formatAmount(estimate.getExcludingTaxTotal()));

        discountRate    = estimate.getDiscount();
        double discount = estimate.getExcludingTaxTotal() * discountRate / 100f;
        double vat      = estimate.getExcludingTaxTotalAfterDiscount() * estimate.getVat() / 100f;

        tvDiscount.setText(String.format(Locale.getDefault(),
                "Discount: %.2f%% = %s", estimate.getDiscount(),
                currencyManager.formatAmount((float) discount)));

        tvVat.setText(String.format(Locale.getDefault(),
                "VAT: %.2f%% = %s", estimate.getVat(),
                currencyManager.formatAmount((float) vat)));

        tvAllTotal.setText("Total After VAT: "
                + currencyManager.formatAmount(estimate.getAllTaxIncludedTotal()));
    }

    private TextView createCell(String text, int weight) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    private File createPdf() {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint      = new Paint();
        Paint labelPaint = new Paint();
        labelPaint.setTextSize(9);
        labelPaint.setColor(android.graphics.Color.GRAY);

        int y = 50;

        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText("ESTIMATE", 40, y, paint);
        y += 40;

        paint.setTextSize(14);
        canvas.drawText("Business Information:", 40, y, paint);
        canvas.drawText("Customer Information:", 300, y, paint);
        paint.setFakeBoldText(false);
        paint.setTextSize(12);
        y += 20;

        canvas.drawText("Company Name", 40, y, labelPaint);
        canvas.drawText(tvBusinessName.getText().toString(), 40, y + 14, paint);
        canvas.drawText("Customer Name", 300, y, labelPaint);
        canvas.drawText(tvCustomerName.getText().toString(), 300, y + 14, paint);
        y += 30;

        canvas.drawText("Address", 40, y, labelPaint);
        canvas.drawText(tvBusinessAddress.getText().toString(), 40, y + 14, paint);
        canvas.drawText("Address", 300, y, labelPaint);
        canvas.drawText(tvCustomerAddress.getText().toString(), 300, y + 14, paint);
        y += 30;

        canvas.drawText("Phone", 40, y, labelPaint);
        canvas.drawText(tvBusinessPhone.getText().toString(), 40, y + 14, paint);
        canvas.drawText("Phone", 300, y, labelPaint);
        canvas.drawText(tvCustomerPhone.getText().toString(), 300, y + 14, paint);
        y += 44;

        paint.setTextSize(10);
        paint.setColor(android.graphics.Color.GRAY);
        canvas.drawText("Currency: " + currencyCode, 40, y, paint);
        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(12);
        y += 20;

        paint.setFakeBoldText(true);
        canvas.drawText("Product",    40,  y, paint);
        canvas.drawText("Qty",        150, y, paint);
        canvas.drawText("Unit Price", 280, y, paint);
        canvas.drawText("Total",      420, y, paint);
        y += 20;
        paint.setFakeBoldText(false);

        for (EstimateLine line : estimateLines) {
            productType = dbAdapter.getSteelById(line.getSteel()).getType();
            canvas.drawText(productType,                                         40,  y, paint);
            canvas.drawText(String.valueOf(line.getQuantity()),                  150, y, paint);
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", line.getUnitPrice()),  280, y, paint);
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", line.getTotalPrice()), 420, y, paint);
            y += 20;
        }

        y += 20;
        canvas.drawText(tvTotalBeforeVat.getText().toString(), 300, y, paint); y += 20;
        canvas.drawText(tvDiscount.getText().toString(),       300, y, paint); y += 20;
        canvas.drawText(tvVat.getText().toString(),            300, y, paint); y += 20;
        canvas.drawText(tvAllTotal.getText().toString(),       300, y, paint);

        pdfDocument.finishPage(page);

        File downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir != null && !downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }
        File pdfFile = new File(downloadsDir, "Estimate.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
            pdfDocument.close();
            return null;
        }

        pdfDocument.close();
        generatedPdf = pdfFile;
        return pdfFile;
    }

    private void printPdf(File pdfFile) {
        pdfFile = ensurePdfExists();
        if (pdfFile == null) {
            Toast.makeText(this, "Could not generate the PDF file.", Toast.LENGTH_SHORT).show();
            return;
        }
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter adapter = new PdfPrintAdapter(this, pdfFile.getAbsolutePath());
        printManager.print("Estimate Print", adapter,
                new PrintAttributes.Builder().build());
    }

    private void sendPdfByEmail(String email, File pdfFile) {
        pdfFile = ensurePdfExists();
        if (pdfFile == null) {
            Toast.makeText(this, "Could not generate the PDF file.", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(
                this, getPackageName() + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_EMAIL,   new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Estimate");
        intent.putExtra(Intent.EXTRA_TEXT,    "Please find the estimate attached.");
        intent.putExtra(Intent.EXTRA_STREAM,  uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Send estimate"));
    }

    private File ensurePdfExists() {
        if (generatedPdf != null && generatedPdf.exists()) {
            return generatedPdf;
        }
        return createPdf();
    }

    private void copyToDownloads(File sourceFile) {
        File downloads = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File destFile = new File(downloads, "Estimate.pdf");
        try {
            InputStream  in  = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
            in.close();
            out.close();
            Toast.makeText(this, "PDF saved: " + destFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Copy failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}