package com.example.metalconstructionsestimates.modules.estimates;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;
import android.provider.MediaStore;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import android.graphics.Color;
import android.graphics.Typeface;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EstimatePreviewActivity extends AppCompatActivity {

    private static final String TAG = "EstimatePreviewActivity";

    private LinearLayout linesContainer;
    private TextView tvTotalBeforeVat, tvAllTotal, tvVat, tvDiscount;

    private TextView tvBusinessName, tvBusinessAddress, tvBusinessPhone;
    private TextView tvCustomerName, tvCustomerAddress, tvCustomerPhone;
    private ImageView btnDownloadPdf, btnPrint, btnSendMail;

    private static final int PAGE_W = 595, PAGE_H = 842;
    private static final int MARGIN = 40;
    private static final int ROW_H = 24;
    private static final int BOTTOM_LIMIT = 790;
    // Column edges: Product | Qty | Unit Price | Total
    private static final int[] COL_X = {40, 250, 330, 435, 555};
    private static final int BRAND = Color.parseColor("#0066CC");

    private PdfDocument pdfDocument;
    private PdfDocument.Page currentPage;
    private Canvas canvas;
    private int pageNumber;
    private Paint textPaint, boldPaint, labelPaint, borderPaint, fillPaint, brandPaint, whitePaint;

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

        btnDownloadPdf.setOnClickListener(v -> createPdf());

        btnPrint.setOnClickListener(v -> printPdf(generatedPdf));
        btnSendMail.setOnClickListener(v -> {
            String email = (customer != null && customer.getEmail() != null)
                    ? customer.getEmail() : "";
            sendPdfByEmail(email, generatedPdf);
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
        pdfDocument = new PdfDocument();
        pageNumber = 0;
        initPaints();

        int y = startNewPage();

        // ── Title + number/date ───────────────────────────────────────────
        brandPaint.setTextSize(26);
        canvas.drawText("ESTIMATE", MARGIN, y + 10, brandPaint);

        String estimateId = getIntent().getStringExtra("estimateId");
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Estimate No: " + estimateId, PAGE_W - MARGIN, y - 4, textPaint);
        canvas.drawText("Date: " + date, PAGE_W - MARGIN, y + 12, textPaint);
        textPaint.setTextAlign(Paint.Align.LEFT);

        // accent line under the title
        borderPaint.setStrokeWidth(2f);
        borderPaint.setColor(BRAND);
        canvas.drawLine(MARGIN, y + 22, PAGE_W - MARGIN, y + 22, borderPaint);
        borderPaint.setStrokeWidth(0.8f);
        borderPaint.setColor(Color.DKGRAY);
        y += 45;

        // ── Business / Customer boxes ─────────────────────────────────────
        drawInfoBox(MARGIN, y, 250, "Business Information",
                new String[]{"Company Name", "Address", "Phone"},
                new String[]{tvBusinessName.getText().toString(),
                        tvBusinessAddress.getText().toString(),
                        tvBusinessPhone.getText().toString()});
        drawInfoBox(305, y, 250, "Customer Information",
                new String[]{"Customer Name", "Address", "Phone"},
                new String[]{tvCustomerName.getText().toString(),
                        tvCustomerAddress.getText().toString(),
                        tvCustomerPhone.getText().toString()});
        y += 110;

        // ── Table header ──────────────────────────────────────────────────
        drawTableHeader(y);
        y += ROW_H;

        // ── Table rows (each cell bordered) ───────────────────────────────
        for (EstimateLine line : estimateLines) {
            if (y + ROW_H > BOTTOM_LIMIT) {
                y = startNewPage();
                drawTableHeader(y);
                y += ROW_H;
            }
            String product = dbAdapter.getSteelById(line.getSteel()).getType();
            drawRow(y, new String[]{
                    product,
                    String.valueOf(line.getNetQuantityPlusMargin()),
                    String.format(Locale.getDefault(), "%.2f", line.getUnitPrice()),
                    String.format(Locale.getDefault(), "%.2f", line.getTotalPrice())
            }, false);
            y += ROW_H;
        }

        // ── Totals box ────────────────────────────────────────────────────
        int totalsHeight = ROW_H * 4;
        if (y + 20 + totalsHeight > BOTTOM_LIMIT) {
            y = startNewPage();
        } else {
            y += 20;
        }

        double discountValue = estimate.getExcludingTaxTotal() * estimate.getDiscount() / 100.0;
        double vatValue = estimate.getExcludingTaxTotalAfterDiscount() * estimate.getVat() / 100.0;

        String[] labels = {
                "Total Before VAT",
                String.format(Locale.getDefault(), "Discount (%.2f%%)", estimate.getDiscount()),
                String.format(Locale.getDefault(), "VAT (%.2f%%)", estimate.getVat()),
                "Total After VAT"
        };
        String[] values = {
                currencyManager.formatAmount(estimate.getExcludingTaxTotal()),
                currencyManager.formatAmount((float) discountValue),
                currencyManager.formatAmount((float) vatValue),
                currencyManager.formatAmount(estimate.getAllTaxIncludedTotal())
        };

        int left = COL_X[2] - 20;   // box starts a bit left of "Unit Price" column
        int right = COL_X[4];
        int mid = left + 105;
        for (int i = 0; i < 4; i++) {
            boolean last = (i == 3);
            float top = y + i * ROW_H;
            if (last) {
                canvas.drawRect(left, top, right, top + ROW_H, brandPaint2());
            }
            canvas.drawRect(left, top, right, top + ROW_H, borderPaint);
            canvas.drawLine(mid, top, mid, top + ROW_H, borderPaint);

            Paint p = last ? whitePaint : (i == 0 ? textPaint : textPaint);
            p.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(labels[i], left + 6, baseline(top, p), p);
            p.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(values[i], right - 6, baseline(top, p), p);
            p.setTextAlign(Paint.Align.LEFT);
        }

        finishCurrentPage();

        try {
            String fileDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            String fileName = "Estimate_" + fileDate + "_" + System.currentTimeMillis() + ".pdf";

            File pdfFile = new File(getCacheDir(), fileName);
            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                pdfDocument.writeTo(fos);
            }

            // Copy to Downloads
            ContentValues values2 = new ContentValues();
            values2.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values2.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values2.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values2);
            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri);
                     FileInputStream in = new FileInputStream(pdfFile)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            }

            pdfDocument.close();
            generatedPdf = pdfFile;
            Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_LONG).show();
            return pdfFile;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
            pdfDocument.close();
            return null;
        }
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

        // Only pre-fill the recipient when the customer has an email
        if (email != null && !email.trim().isEmpty()) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.trim()});
        }

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

    private void initPaints() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(11);
        textPaint.setColor(Color.BLACK);

        boldPaint = new Paint(textPaint);
        boldPaint.setTypeface(Typeface.DEFAULT_BOLD);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextSize(8);
        labelPaint.setColor(Color.GRAY);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(0.8f);
        borderPaint.setColor(Color.DKGRAY);

        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#E8F0FA"));

        brandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        brandPaint.setColor(BRAND);
        brandPaint.setTypeface(Typeface.DEFAULT_BOLD);

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(11);
        whitePaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    private Paint brandPaint2() {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(BRAND);
        return p;
    }

    /** Finishes the current page (if any), starts a new one, returns the starting y. */
    private int startNewPage() {
        if (currentPage != null) finishCurrentPage();
        pageNumber++;
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create();
        currentPage = pdfDocument.startPage(info);
        canvas = currentPage.getCanvas();
        return 60;
    }

    private void finishCurrentPage() {
        labelPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Page " + pageNumber, PAGE_W / 2f, PAGE_H - 25, labelPaint);
        labelPaint.setTextAlign(Paint.Align.LEFT);
        pdfDocument.finishPage(currentPage);
        currentPage = null;
    }

    private float baseline(float top, Paint p) {
        return top + ROW_H / 2f + p.getTextSize() / 3f;
    }

    /** Cuts the text so it fits inside maxWidth (adds "..." if shortened). */
    private String fit(String text, Paint p, float maxWidth) {
        if (text == null) return "";
        if (p.measureText(text) <= maxWidth) return text;
        int count = p.breakText(text, true, maxWidth - p.measureText("..."), null);
        return text.substring(0, Math.max(0, count)) + "...";
    }

    private void drawInfoBox(int x, int y, int width, String title, String[] labels, String[] values) {
        canvas.drawRect(x, y, x + width, y + 95, borderPaint);
        canvas.drawRect(x, y, x + width, y + 20, fillPaint);
        canvas.drawRect(x, y, x + width, y + 20, borderPaint);
        canvas.drawText(title, x + 8, y + 14, boldPaint);

        int cy = y + 32;
        for (int i = 0; i < labels.length; i++) {
            canvas.drawText(labels[i], x + 8, cy, labelPaint);
            canvas.drawText(fit(values[i], textPaint, width - 16), x + 8, cy + 11, textPaint);
            cy += 21;
        }
    }

    private void drawTableHeader(int y) {
        drawRow(y, new String[]{"Product", "Qty", "Unit Price (" + currencyCode + ")",
                "Total (" + currencyCode + ")"}, true);
    }

    private void drawRow(int y, String[] cells, boolean header) {
        for (int i = 0; i < 4; i++) {
            float l = COL_X[i], r = COL_X[i + 1];
            if (header) canvas.drawRect(l, y, r, y + ROW_H, fillPaint);
            canvas.drawRect(l, y, r, y + ROW_H, borderPaint);

            Paint p = header ? boldPaint : textPaint;
            String text = fit(cells[i], p, (r - l) - 12);
            if (i == 0) {                       // product: left aligned
                p.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(text, l + 6, baseline(y, p), p);
            } else {                            // numbers: right aligned
                p.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(text, r - 6, baseline(y, p), p);
                p.setTextAlign(Paint.Align.LEFT);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}