package com.example.metalconstructionsestimates.modules.estimateslines;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.modules.estimates.EstimateDetails;
import com.example.metalconstructionsestimates.modules.steels.Steels;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EstimateLineDetails extends AppCompatActivity {

    private EstimateLine estimateLine;
    private DBAdapter dbAdapter;
    private Integer estimateLineId;
    private String geometricShape = "";
    private Integer steelId;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    // Fields promoted to instance level so every TextWatcher / callback
    // shares the SAME views instead of re-running findViewById with wrong ids.
    private TextInputEditText estimateLineIdEditText;
    private TextInputEditText estimateIdEditText;
    private TextInputEditText steelTypeEditText;
    private TextInputEditText weightEditText;
    private TextInputEditText lengthEditText;
    private TextInputEditText widthEditText;
    private TextInputEditText heightEditText;
    private TextInputEditText quantityEditText;
    private TextInputEditText totalEditText;
    private TextInputEditText marginEditText;
    private TextInputEditText netQuantityPlusMarginEditText;
    private TextInputEditText unitPriceEditText;
    private TextInputEditText totalPriceEditText;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_line_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        estimateLineIdEditText = findViewById(R.id.et_estimate_line_id);
        estimateIdEditText = findViewById(R.id.et_estimate_id);
        steelTypeEditText = findViewById(R.id.et_steel_id);
        weightEditText = findViewById(R.id.et_weight);
        lengthEditText = findViewById(R.id.et_length);
        widthEditText = findViewById(R.id.et_width);
        heightEditText = findViewById(R.id.et_height);
        quantityEditText = findViewById(R.id.et_quantity);
        totalEditText = findViewById(R.id.et_total);
        marginEditText = findViewById(R.id.et_margin);
        netQuantityPlusMarginEditText = findViewById(R.id.et_net_quantity);
        unitPriceEditText = findViewById(R.id.et_unit_price);
        totalPriceEditText = findViewById(R.id.et_total_price);

        dbAdapter = new DBAdapter(getApplicationContext());
        estimateLineId = Integer.parseInt(getIntent().getStringExtra("estimateLineIdExtra"));
        estimateLine = dbAdapter.getEstimateLineById(estimateLineId);

        // FIX: keep steelId in sync with the line's current steel so that
        // clicking "Update" without reselecting a steel doesn't wipe it out.
        steelId = estimateLine.getSteel();

        estimateLineIdEditText.setText(estimateLine.getId().toString());
        estimateIdEditText.setText(estimateLine.getEstimate().toString());

        Steel currentSteel = dbAdapter.getSteelById(estimateLine.getSteel());
        steelTypeEditText.setText(currentSteel.getType());
        geometricShape = currentSteel.getGeometricShape();

        setFieldsEnabledForShape(geometricShape);

        weightEditText.setText(formatOrEmpty(estimateLine.getWeight()));
        lengthEditText.setText(formatOrEmpty(estimateLine.getLength()));
        widthEditText.setText(formatOrEmpty(estimateLine.getWidth()));
        heightEditText.setText(formatOrEmpty(estimateLine.getHeight()));
        quantityEditText.setText(estimateLine.getQuantity() == null ? "" : estimateLine.getQuantity().toString());
        marginEditText.setText(estimateLine.getMargin() == null ? "" : estimateLine.getMargin().toString());
        totalEditText.setText(formatOrEmpty(estimateLine.getTotal()));
        netQuantityPlusMarginEditText.setText(formatOrEmpty(estimateLine.getNetQuantityPlusMargin()));
        unitPriceEditText.setText(formatOrEmpty(estimateLine.getUnitPrice()));
        totalPriceEditText.setText(formatOrEmpty(estimateLine.getTotalPrice()));

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) {
                        return;
                    }
                    Intent data = result.getData();
                    String steelIdExtraResult = data.getStringExtra("steelIdExtraResult");
                    if (steelIdExtraResult == null) {
                        Toast.makeText(getApplicationContext(), "No steel returned", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    steelId = Integer.parseInt(steelIdExtraResult);
                    Steel steel = dbAdapter.getSteelById(steelId);
                    geometricShape = steel.getGeometricShape();
                    steelTypeEditText.setText(steel.getType());

                    // FIX: single, null-safe assignment (old code called
                    // steel.getWeight().toString() unconditionally afterwards,
                    // which crashed whenever a steel had no weight).
                    weightEditText.setText(formatOrEmpty(steel.getWeight()));

                    setFieldsEnabledForShape(geometricShape);
                    recalculate();
                }
        );

        Button updateEstimateLine = findViewById(R.id.btn_update);
        Button deleteEstimateLine = findViewById(R.id.btn_delete);
        Button selectSteel = findViewById(R.id.btn_select_steel);

        selectSteel.setOnClickListener(view -> startActivityForResult());

        updateEstimateLine.setOnClickListener(view -> {
            EstimateLine updated = new EstimateLine();

            // FIX: the id was never set, so updateEstimateLine() had nothing
            // to match against.
            updated.setId(estimateLineId);
            updated.setEstimate(Integer.parseInt(estimateIdEditText.getText().toString()));
            updated.setSteel(steelId);

            updated.setWeight(parseFloatOrNull(weightEditText));
            updated.setLength(parseFloatOrNull(lengthEditText));
            updated.setWidth(parseFloatOrNull(widthEditText));
            updated.setHeight(parseFloatOrNull(heightEditText));
            updated.setQuantity(quantityEditText.getText().toString().isEmpty()
                    ? null : Long.parseLong(quantityEditText.getText().toString()));
            updated.setTotal(parseFloatOrNull(totalEditText));
            updated.setMargin(marginEditText.getText().toString().isEmpty()
                    ? null : Integer.parseInt(marginEditText.getText().toString()));
            updated.setNetQuantityPlusMargin(parseFloatOrNull(netQuantityPlusMarginEditText));
            updated.setUnitPrice(parseFloatOrNull(unitPriceEditText));
            updated.setTotalPrice(parseFloatOrNull(totalPriceEditText));

            dbAdapter.updateEstimateLine(updated);
            Toast.makeText(getApplicationContext(), "Estimate line has been successfully updated", Toast.LENGTH_LONG).show();

            recalculateEstimateTotals(updated.getEstimate());

            Intent intent = new Intent(getApplicationContext(), EstimateDetails.class);
            intent.putExtra("estimateIdExtra", updated.getEstimate().toString());
            startActivity(intent);
            finish();
        });

        deleteEstimateLine.setOnClickListener(view -> {
            dbAdapter.deleteEstimateLine(Integer.parseInt(estimateLineIdEditText.getText().toString()));
            Toast.makeText(getApplicationContext(), "The estimate line has been successfully deleted.", Toast.LENGTH_LONG).show();
            if (dbAdapter.retrieveEstimatesLinesCount() == 0) {
                dbAdapter.setSeqEstimateLines();
            }
            String estimateId = estimateLine.getEstimate().toString();
            Intent intent = new Intent(getApplicationContext(), EstimateDetails.class);
            intent.putExtra("estimateIdExtra", estimateId);
            startActivity(intent);
        });

        TextWatcher recalcWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                recalculate();
            }
        };

        lengthEditText.addTextChangedListener(recalcWatcher);
        widthEditText.addTextChangedListener(recalcWatcher);
        heightEditText.addTextChangedListener(recalcWatcher);
        weightEditText.addTextChangedListener(recalcWatcher);
        quantityEditText.addTextChangedListener(recalcWatcher);
        marginEditText.addTextChangedListener(recalcWatcher);
        unitPriceEditText.addTextChangedListener(recalcWatcher);
    }

    /** Enables/disables length/width/height inputs based on the steel's geometric shape. */
    @SuppressLint("SetTextI18n")
    private void setFieldsEnabledForShape(String shape) {
        switch (shape) {
            case "Profile":
                lengthEditText.setEnabled(true);
                widthEditText.setEnabled(false);
                heightEditText.setEnabled(false);
                widthEditText.setText("");
                heightEditText.setText("");
                break;
            case "Surface":
                lengthEditText.setEnabled(true);
                widthEditText.setEnabled(true);
                heightEditText.setEnabled(false);
                heightEditText.setText("");
                break;
            case "Volume":
                lengthEditText.setEnabled(true);
                widthEditText.setEnabled(true);
                heightEditText.setEnabled(true);
                break;
            default:
                break;
        }
    }

    /**
     * Single source of truth for total / net quantity+margin / total price.
     * Called from every relevant TextWatcher and after a steel is picked,
     * so the five near-duplicate, inconsistent calculation blocks from the
     * original code are replaced by one.
     */
    private void recalculate() {
        if (geometricShape.isEmpty()) return;

        BigDecimal length = parseOrNull(lengthEditText);
        BigDecimal width = parseOrNull(widthEditText);
        BigDecimal height = parseOrNull(heightEditText);
        BigDecimal weight = parseOrNull(weightEditText);
        BigDecimal quantity = parseOrNull(quantityEditText);
        BigDecimal margin = parseOrNull(marginEditText);
        BigDecimal unitPrice = parseOrNull(unitPriceEditText);

        BigDecimal total = null;
        switch (geometricShape) {
            case "Profile":
                if (length != null && weight != null && quantity != null) {
                    total = length.multiply(weight).multiply(quantity);
                }
                break;
            case "Surface":
                if (length != null && width != null && weight != null && quantity != null) {
                    total = length.multiply(width).multiply(weight).multiply(quantity);
                }
                break;
            case "Volume":
                if (length != null && width != null && height != null && weight != null && quantity != null) {
                    total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                }
                break;
            default:
                break;
        }

        if (total == null) {
            totalEditText.setText("");
            netQuantityPlusMarginEditText.setText("");
            totalPriceEditText.setText("");
            return;
        }

        totalEditText.setText(total.stripTrailingZeros().toPlainString());

        BigDecimal netQuantityPlusMargin = (margin != null)
                ? total.add(total.multiply(margin).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP))
                : total;
        netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.stripTrailingZeros().toPlainString());

        if (unitPrice != null) {
            BigDecimal totalPrice = netQuantityPlusMargin.multiply(unitPrice);
            totalPriceEditText.setText(totalPrice.stripTrailingZeros().toPlainString());
        } else {
            totalPriceEditText.setText("");
        }
    }

    private void recalculateEstimateTotals(Integer estimateId) {
        Float estimateExcludingTaxTotal = dbAdapter.getEstimateExcludingTaxTotal(estimateId);
        Estimate estimate = dbAdapter.getEstimateById(estimateId);
        estimate.setExcludingTaxTotal(estimateExcludingTaxTotal);

        Float excludingTaxTotalAfterDiscount;
        // FIX: check for null BEFORE calling toString() on it (old code did
        // getDiscount().toString() first, which threw NPE when discount was null).
        if (estimate.getDiscount() != null) {
            excludingTaxTotalAfterDiscount = estimateExcludingTaxTotal
                    - estimateExcludingTaxTotal * estimate.getDiscount() / 100;
        } else {
            excludingTaxTotalAfterDiscount = estimateExcludingTaxTotal;
        }
        estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);

        Float allTaxIncludedTotal;
        if (estimate.getVat() != null) {
            allTaxIncludedTotal = excludingTaxTotalAfterDiscount
                    + excludingTaxTotalAfterDiscount * estimate.getVat() / 100;
        } else {
            allTaxIncludedTotal = excludingTaxTotalAfterDiscount;
        }
        estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);

        dbAdapter.updateEstimate(estimate);
    }

    private BigDecimal parseOrNull(TextInputEditText et) {
        String text = et.getText().toString().trim();
        if (text.isEmpty()) return null;
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Float parseFloatOrNull(TextInputEditText et) {
        String text = et.getText().toString().trim();
        if (text.isEmpty()) return null;
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatOrEmpty(Float value) {
        return value == null ? "" : new BigDecimal(value.toString()).toPlainString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void startActivityForResult() {
        Intent intent = new Intent(EstimateLineDetails.this, Steels.class);
        activityResultLauncher.launch(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbAdapter != null) {
            dbAdapter.close();
        }
    }
}