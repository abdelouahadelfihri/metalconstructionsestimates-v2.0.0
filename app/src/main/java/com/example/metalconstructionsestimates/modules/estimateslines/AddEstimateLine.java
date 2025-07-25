package com.example.metalconstructionsestimates.modules.estimateslines;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.modules.estimates.EstimateDetails;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.modules.steels.Steels;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;


public class AddEstimateLine extends AppCompatActivity {
    Intent intent;
    DBAdapter dbAdapter;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    Integer estimateId;
    Integer steelId = null;
    String geometricShape = "";
    TextInputEditText steelTypeEditText;
    TextInputEditText weightEditText;
    TextInputEditText lengthEditText;
    TextInputEditText widthEditText;
    TextInputEditText heightEditText;
    TextInputEditText quantityEditText;
    TextInputEditText totalEditText;
    TextInputEditText marginEditText;
    TextInputEditText netQuantityPlusMarginEditText;
    TextInputEditText unitPriceEditText;
    TextInputEditText totalPriceEditText;

    String formattedTotal;
    String formattedNetQuantityPlusMargin;
    String formattedTotalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estimate_line);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        estimateId = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("estimateIdExtra")));

        steelTypeEditText = findViewById(R.id.steelTypeEditText_add_estimate_line);
        weightEditText = findViewById(R.id.weightEditText_add_estimate_line);
        lengthEditText = findViewById(R.id.lengthEditText_add_estimate_line);
        widthEditText = findViewById(R.id.widthEditText_add_estimate_line);
        heightEditText = findViewById(R.id.heightEditText_add_estimate_line);
        quantityEditText = findViewById(R.id.quantityEditText_add_estimate_line);
        totalEditText = findViewById(R.id.totalEditText_add_estimate_line);
        marginEditText = findViewById(R.id.marginEditText_add_estimate_line);
        netQuantityPlusMarginEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
        unitPriceEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
        totalPriceEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            assert data != null;
                            // get String data from Intent
                            Float total, netQuantityPlusMargin, totalPrice;
                            String steelIdExtraResult = Objects.requireNonNull(data.getExtras()).getString("steelIdExtraResult");
                            assert steelIdExtraResult != null;
                            steelId = Integer.parseInt(steelIdExtraResult);

                            Steel steel;
                            dbAdapter = new DBAdapter(getApplicationContext());
                            steel = dbAdapter.getSteelById(steelId);

                            geometricShape = steel.getGeometricShape();

                            steelTypeEditText.setText(steel.getType());


                            if (steel.getWeight() == null) {
                                weightEditText.setText("");
                            } else {
                                weightEditText.setText(String.format(Locale.getDefault(), "%.2f", steel.getWeight()));
                            }

                            switch (geometricShape) {
                                case "Profile":

                                    lengthEditText.setEnabled(true);
                                    widthEditText.setEnabled(false);
                                    heightEditText.setEnabled(false);

                                    widthEditText.setText("");
                                    heightEditText.setText("");

                                    if ((!lengthEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
                                        formattedTotal = new BigDecimal(total).toPlainString();
                                        totalEditText.setText(formattedTotal);

                                        if (!marginEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) / 100;
                                            String formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);

                                        } else {
                                            formattedNetQuantityPlusMargin = new BigDecimal(total).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);
                                        }

                                    } else {
                                        totalEditText.setText("");
                                        netQuantityPlusMarginEditText.setText("");
                                        totalPriceEditText.setText("");
                                    }

                                    if (!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginEditText.getText().toString()) * Float.parseFloat(unitPriceEditText.getText().toString());
                                        formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                        totalPriceEditText.setText(formattedTotalPrice);
                                    } else {
                                        totalPriceEditText.setText("");
                                    }
                                    break;
                                case "Surface":
                                    lengthEditText.setEnabled(true);
                                    widthEditText.setEnabled(true);
                                    heightEditText.setEnabled(false);

                                    heightEditText.setText("");

                                    if ((!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
                                        formattedTotal = new BigDecimal(total).toPlainString();
                                        totalEditText.setText(formattedTotal);

                                        if (!marginEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) / 100;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);
                                        } else {
                                            netQuantityPlusMargin = total;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);
                                        }
                                    } else {
                                        totalEditText.setText("");
                                        netQuantityPlusMarginEditText.setText("");
                                        totalPriceEditText.setText("");
                                    }
                                    if (!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginEditText.getText().toString()) * Float.parseFloat(unitPriceEditText.getText().toString());
                                        formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                        totalPriceEditText.setText(formattedTotalPrice);
                                    } else {
                                        totalPriceEditText.setText("");
                                    }
                                    break;
                                case "Volume":

                                    lengthEditText.setEnabled(true);
                                    widthEditText.setEnabled(true);
                                    heightEditText.setEnabled(true);

                                    if ((!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!heightEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(heightEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
                                        formattedTotal = new BigDecimal(total).toPlainString();
                                        totalEditText.setText(formattedTotal);

                                        if (!marginEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) / 100;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);
                                        } else {
                                            netQuantityPlusMargin = total;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);
                                        }

                                    } else {
                                        totalEditText.setText("");
                                        netQuantityPlusMarginEditText.setText("");
                                        totalPriceEditText.setText("");
                                    }
                                    if (!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginEditText.getText().toString()) * Float.parseFloat(unitPriceEditText.getText().toString());
                                        formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                        totalPriceEditText.setText(formattedTotalPrice);
                                    } else {
                                        totalPriceEditText.setText("");
                                    }
                                    break;
                            }
                        }
                    }
                }
        );

        Button addEstimateLine = findViewById(R.id.addButton_add_estimate_line);
        Button clearEstimateLineForm = findViewById(R.id.clearButton_add_estimate_line);

        clearEstimateLineForm.setOnClickListener(view -> {
            TextInputEditText steelTypeTextInputEditText = findViewById(R.id.steelTypeEditText_add_estimate_line);
            TextInputEditText weightTextInputEditText = findViewById(R.id.weightEditText_add_estimate_line);
            TextInputEditText lengthTextInputEditText = findViewById(R.id.lengthEditText_add_estimate_line);
            TextInputEditText widthTextInputEditText = findViewById(R.id.widthEditText_add_estimate_line);
            TextInputEditText heightTextInputEditText = findViewById(R.id.heightEditText_add_estimate_line);
            TextInputEditText quantityTextInputEditText = findViewById(R.id.quantityEditText_add_estimate_line);
            TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
            TextInputEditText marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
            TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
            TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
            TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

            Objects.requireNonNull(steelTypeEditText.getText()).clear();
            Objects.requireNonNull(weightEditText.getText()).clear();
            Objects.requireNonNull(lengthEditText.getText()).clear();
            Objects.requireNonNull(widthEditText.getText()).clear();
            Objects.requireNonNull(heightEditText.getText()).clear();
            Objects.requireNonNull(quantityEditText.getText()).clear();
            Objects.requireNonNull(totalEditText.getText()).clear();
            Objects.requireNonNull(marginEditText.getText()).clear();
            Objects.requireNonNull(netQuantityPlusMarginEditText.getText()).clear();
            Objects.requireNonNull(unitPriceEditText.getText()).clear();
            Objects.requireNonNull(totalPriceEditText.getText()).clear();
        });

        Button selectSteel = findViewById(R.id.selectSteelButton);

        selectSteel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult();
            }
        });

        addEstimateLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText steelTypeTextInputEditText = findViewById(R.id.steelTypeEditText_add_estimate_line);
                TextInputEditText weightTextInputEditText = findViewById(R.id.weightEditText_add_estimate_line);
                TextInputEditText lengthTextInputEditText = findViewById(R.id.lengthEditText_add_estimate_line);
                TextInputEditText widthTextInputEditText = findViewById(R.id.widthEditText_add_estimate_line);
                TextInputEditText heightTextInputEditText = findViewById(R.id.heightEditText_add_estimate_line);
                TextInputEditText quantityTextInputEditText = findViewById(R.id.quantityEditText_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                EstimateLine estimateLine = new EstimateLine();

                if (Objects.requireNonNull(steelTypeEditText.getText()).toString().isEmpty()) {
                    Toast steelEmptyToast = Toast.makeText(getApplication(), "Steel type empty", Toast.LENGTH_LONG);
                    steelEmptyToast.show();
                } else {
                    if(dbAdapter.getSteelByType(steelTypeEditText.getText().toString()) == null){
                        Toast steelEmptyToast = Toast.makeText(getApplication(), "Steel does not exist in database", Toast.LENGTH_LONG);
                        steelEmptyToast.show();
                    }
                    else{
                        estimateLine.setSteel(steelId);
                        estimateLine.setEstimate(estimateId);
                        if (!Objects.requireNonNull(weightEditText.getText()).toString().isEmpty()) {
                            estimateLine.setWeight(Float.parseFloat(weightEditText.getText().toString()));
                        } else {
                            estimateLine.setWeight(null);
                        }

                        if (!Objects.requireNonNull(lengthEditText.getText()).toString().isEmpty()) {
                            estimateLine.setLength(Float.parseFloat(lengthEditText.getText().toString()));
                        } else {
                            estimateLine.setLength(null);
                        }

                        if (!Objects.requireNonNull(widthEditText.getText()).toString().isEmpty()) {
                            estimateLine.setWidth(Float.parseFloat(widthEditText.getText().toString()));
                        } else {
                            estimateLine.setWidth(null);
                        }

                        if (!Objects.requireNonNull(heightEditText.getText()).toString().isEmpty()) {
                            estimateLine.setHeight(Float.parseFloat(heightEditText.getText().toString()));
                        } else {
                            estimateLine.setHeight(null);
                        }

                        if (!Objects.requireNonNull(quantityEditText.getText()).toString().isEmpty()) {
                            estimateLine.setQuantity(Long.parseLong(quantityEditText.getText().toString()));
                        } else {
                            estimateLine.setQuantity(null);
                        }

                        if (!Objects.requireNonNull(totalEditText.getText()).toString().isEmpty()) {
                            estimateLine.setTotal(Float.parseFloat(totalEditText.getText().toString()));
                        } else {
                            estimateLine.setTotal(null);
                        }

                        if (!Objects.requireNonNull(marginEditText.getText()).toString().isEmpty()) {
                            estimateLine.setMargin(Integer.parseInt(marginEditText.getText().toString()));
                        } else {
                            estimateLine.setMargin(null);
                        }

                        if (!Objects.requireNonNull(netQuantityPlusMarginEditText.getText()).toString().isEmpty()) {
                            estimateLine.setNetQuantityPlusMargin(Float.parseFloat(netQuantityPlusMarginEditText.getText().toString()));
                        } else {
                            estimateLine.setNetQuantityPlusMargin(null);
                        }

                        if (!Objects.requireNonNull(unitPriceEditText.getText()).toString().isEmpty()) {
                            estimateLine.setUnitPrice(Float.parseFloat(unitPriceEditText.getText().toString()));
                        } else {
                            estimateLine.setUnitPrice(null);
                        }

                        if (!Objects.requireNonNull(totalPriceEditText.getText()).toString().isEmpty()) {
                            estimateLine.setTotalPrice(Float.parseFloat(totalPriceEditText.getText().toString()));
                        } else {
                            estimateLine.setTotalPrice(null);
                        }

                        dbAdapter.saveEstimateLine(estimateLine);
                        Toast saveResultToast;
                        saveResultToast = Toast.makeText(getApplicationContext(),
                                "Estimate line has been successfully added", Toast.LENGTH_LONG);
                        saveResultToast.show();
                        Float estimateExcludingTaxTotal = dbAdapter.getEstimateExcludingTaxTotal(estimateId);
                        Float excludingTaxTotalAfterDiscount;
                        Float allTaxIncludedTotal;
                        Estimate estimate;
                        estimate = dbAdapter.getEstimateById(estimateId);
                        estimate.setExcludingTaxTotal(estimateExcludingTaxTotal);
                        if (estimate.getDiscount() != null) {
                            excludingTaxTotalAfterDiscount = estimateExcludingTaxTotal - estimateExcludingTaxTotal * (estimate.getDiscount() / 100);
                            estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                            if (estimate.getVat() != null) {
                                allTaxIncludedTotal = excludingTaxTotalAfterDiscount + excludingTaxTotalAfterDiscount * estimate.getVat()/100;
                            } else {
                                allTaxIncludedTotal = excludingTaxTotalAfterDiscount;
                            }
                            estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        } else {
                            estimate.setExcludingTaxTotalAfterDiscount(estimateExcludingTaxTotal);
                            if (estimate.getVat() != null) {
                                allTaxIncludedTotal = estimateExcludingTaxTotal + estimateExcludingTaxTotal * estimate.getVat()/100;
                            } else {
                                allTaxIncludedTotal = estimateExcludingTaxTotal;
                            }
                            estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        }

                        dbAdapter.updateEstimate(estimate);
                        intent = new Intent(AddEstimateLine.this, EstimateDetails.class);
                        intent.putExtra("estimateIdExtra", estimateId.toString());
                        startActivity(intent);
                    }
                }
            }
        });

        lengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String lengthStr = s.toString().trim();

                if (lengthStr.isEmpty()) {
                    // Clear results and stay in the activity
                    totalEditText.setText("");
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                BigDecimal length;
                try {
                    length = new BigDecimal(lengthStr);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalEditText.setText("");
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                if (!geometricShape.isEmpty()) {
                    try {
                        BigDecimal total = BigDecimal.ZERO;
                        BigDecimal netQuantityPlusMargin = BigDecimal.ZERO;
                        BigDecimal totalPrice = BigDecimal.ZERO;

                        BigDecimal width = !widthEditText.getText().toString().isEmpty() ?
                                new BigDecimal(widthEditText.getText().toString()) : BigDecimal.ZERO;
                        BigDecimal height = !heightEditText.getText().toString().isEmpty() ?
                                new BigDecimal(heightEditText.getText().toString()) : BigDecimal.ZERO;
                        BigDecimal weight = !weightEditText.getText().toString().isEmpty() ?
                                new BigDecimal(weightEditText.getText().toString()) : BigDecimal.ZERO;
                        BigDecimal quantity = !quantityEditText.getText().toString().isEmpty() ?
                                new BigDecimal(quantityEditText.getText().toString()) : BigDecimal.ZERO;
                        BigDecimal margin = !marginEditText.getText().toString().isEmpty() ?
                                new BigDecimal(marginEditText.getText().toString()) : BigDecimal.ZERO;
                        BigDecimal unitPrice = !unitPriceEditText.getText().toString().isEmpty() ?
                                new BigDecimal(unitPriceEditText.getText().toString()) : null;


                        switch (geometricShape) {
                            case "Profile":
                                if (weight.signum() != 0 && quantity.signum() != 0) {
                                    total = length.multiply(weight).multiply(quantity);
                                }
                                break;

                            case "Surface":
                                if (width.signum() != 0 && weight.signum() != 0 && quantity.signum() != 0) {
                                    total = length.multiply(width).multiply(weight).multiply(quantity);
                                }
                                break;

                            case "Volume":
                                if (width.signum() != 0 && height.signum() != 0 && weight.signum() != 0 && quantity.signum() != 0) {
                                    total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                                }
                                break;
                        }

                        if (total.signum() != 0) {
                            totalEditText.setText(total.stripTrailingZeros().toPlainString());

                            // Add margin
                            if (margin.signum() != 0) {
                                netQuantityPlusMargin = total.add(total.multiply(margin).divide(new BigDecimal("100")));
                            } else {
                                netQuantityPlusMargin = total;
                            }

                            netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.stripTrailingZeros().toPlainString());

                            if (unitPrice != null && netQuantityPlusMargin.signum() != 0) {
                                totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                totalPriceEditText.setText(totalPrice.stripTrailingZeros().toPlainString());
                            } else {
                                totalPriceEditText.setText("");
                            }
                        } else {
                            totalEditText.setText("");
                            netQuantityPlusMarginEditText.setText("");
                            totalPriceEditText.setText("");
                        }

                    } catch (NumberFormatException e) {
                        totalEditText.setText("");
                        netQuantityPlusMarginEditText.setText("");
                        totalPriceEditText.setText("");
                    }
                }
            }
        });

        widthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String widthStr = s.toString().trim();

                if (widthStr.isEmpty()) {
                    // Clear results and stay in the activity
                    totalEditText.setText("");
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                BigDecimal width;
                try {
                    width = new BigDecimal(widthStr);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalEditText.setText("");
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                if (geometricShape.isEmpty()) return;

                BigDecimal length = parse(lengthEditText.getText().toString());
                BigDecimal height = parse(heightEditText.getText().toString());
                BigDecimal weight = parse(weightEditText.getText().toString());
                BigDecimal quantity = parse(quantityEditText.getText().toString());
                BigDecimal margin = parse(marginEditText.getText().toString());
                BigDecimal unitPrice = parse(unitPriceEditText.getText().toString());

                BigDecimal total = BigDecimal.ZERO;
                BigDecimal netQuantityPlusMargin = BigDecimal.ZERO;
                BigDecimal totalPrice = BigDecimal.ZERO;

                switch (geometricShape) {
                    case "Surface":
                        if (isNonZero(length, width, weight, quantity)) {
                            total = length.multiply(width).multiply(weight).multiply(quantity);
                            totalEditText.setText(total.toPlainString());

                            netQuantityPlusMargin = applyMargin(total, margin);
                            netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toPlainString());
                        } else {
                            totalEditText.setText("");
                            netQuantityPlusMarginEditText.setText("");
                            totalPriceEditText.setText("");
                            return;
                        }
                        break;

                    case "Volume":
                        if (isNonZero(length, width, height, weight, quantity)) {
                            total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                            totalEditText.setText(total.toPlainString());

                            netQuantityPlusMargin = applyMargin(total, margin);
                            netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toPlainString());
                        } else {
                            totalEditText.setText("");
                            netQuantityPlusMarginEditText.setText("");
                            totalPriceEditText.setText("");
                            return;
                        }
                        break;
                }

                if (unitPrice.compareTo(BigDecimal.ZERO) > 0 && netQuantityPlusMargin.compareTo(BigDecimal.ZERO) > 0) {
                    totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                    totalPriceEditText.setText(totalPrice.toPlainString());
                } else {
                    totalPriceEditText.setText("");
                }
            }
        });

        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText weightTextInputEditText = findViewById(R.id.weightEditText_add_estimate_line);
                TextInputEditText lengthTextInputEditText = findViewById(R.id.lengthEditText_add_estimate_line);
                TextInputEditText widthTextInputEditText = findViewById(R.id.widthEditText_add_estimate_line);
                TextInputEditText quantityTextInputEditText = findViewById(R.id.quantityEditText_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String heightStr = s.toString().trim();

                if (heightStr.isEmpty()) {
                    // Clear results and stay in the activity
                    totalEditText.setText("");
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                BigDecimal height;
                try {
                    height = new BigDecimal(heightStr);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalEditText.setText("");
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                if (!geometricShape.isEmpty() && geometricShape.equals("Volume")) {
                    String lengthStr = lengthEditText.getText().toString();
                    String widthStr = widthEditText.getText().toString();
                    String weightStr = weightEditText.getText().toString();
                    String quantityStr = quantityEditText.getText().toString();
                    String marginStr = marginEditText.getText().toString();
                    String unitPriceStr = unitPriceEditText.getText().toString();

                    boolean allFilled = !lengthStr.isEmpty() && !widthStr.isEmpty() && !weightStr.isEmpty()
                            && !quantityStr.isEmpty();

                    if (allFilled) {
                        BigDecimal length = new BigDecimal(lengthStr);
                        BigDecimal width = new BigDecimal(widthStr);
                        BigDecimal weight = new BigDecimal(weightStr);
                        BigDecimal quantity = new BigDecimal(quantityStr);

                        BigDecimal total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                        totalEditText.setText(total.toPlainString());

                        BigDecimal netQuantity = total;
                        if (!marginStr.isEmpty()) {
                            BigDecimal margin = new BigDecimal(marginStr);
                            BigDecimal marginValue = total.multiply(margin).divide(BigDecimal.valueOf(100));
                            netQuantity = total.add(marginValue);
                        }

                        netQuantityPlusMarginEditText.setText(netQuantity.toPlainString());
                    } else {
                        totalEditText.setText("");
                        netQuantityPlusMarginEditText.setText("");
                        totalPriceEditText.setText("");
                        return;
                    }

                    if (!unitPriceStr.isEmpty() && !netQuantityPlusMarginEditText.getText().toString().isEmpty()) {
                        BigDecimal unitPrice = new BigDecimal(unitPriceStr);
                        BigDecimal netQuantity = new BigDecimal(netQuantityPlusMarginEditText.getText().toString());
                        BigDecimal totalPrice = unitPrice.multiply(netQuantity);
                        totalPriceEditText.setText(totalPrice.toPlainString());
                    } else {
                        totalPriceEditText.setText("");
                    }
                }
            }
        });

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText lengthInput = findViewById(R.id.lengthEditText_add_estimate_line);
                TextInputEditText widthInput = findViewById(R.id.widthEditText_add_estimate_line);
                TextInputEditText heightInput = findViewById(R.id.heightEditText_add_estimate_line);
                TextInputEditText quantityInput = findViewById(R.id.quantityEditText_add_estimate_line);
                TextInputEditText totalInput = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginInput = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityInput = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceInput = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceInput = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String weightStr = s.toString().trim();

                if (weightStr.isEmpty()) {
                    // Clear results and stay in the activity
                    totalInput.setText("");
                    netQuantityInput.setText("");
                    totalPriceInput.setText("");
                    return;
                }

                BigDecimal weight;
                try {
                    weight = new BigDecimal(weightStr);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalInput.setText("");
                    netQuantityInput.setText("");
                    totalPriceInput.setText("");
                    return;
                }

                if (!geometricShape.isEmpty()) {
                    try {
                        BigDecimal length = tryParse(lengthInput);
                        BigDecimal width = tryParse(widthInput);
                        BigDecimal height = tryParse(heightInput);
                        BigDecimal quantity = tryParse(quantityInput);
                        BigDecimal margin = tryParse(marginInput);
                        BigDecimal unitPrice = tryParse(unitPriceInput);

                        BigDecimal total = BigDecimal.ZERO;

                        switch (geometricShape) {
                            case "Profile":
                                if (length != null && quantity != null) {
                                    total = length.multiply(weight).multiply(quantity);
                                }
                                break;
                            case "Surface":
                                if (length != null && width != null && quantity != null) {
                                    total = length.multiply(width).multiply(weight).multiply(quantity);
                                }
                                break;
                            case "Volume":
                                if (length != null && width != null && height != null && quantity != null) {
                                    total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                                }
                                break;
                        }

                        if (total.compareTo(BigDecimal.ZERO) > 0) {
                            totalInput.setText(total.stripTrailingZeros().toPlainString());

                            BigDecimal netQty = (margin != null)
                                    ? total.add(total.multiply(margin).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP))
                                    : total;
                            netQuantityInput.setText(netQty.stripTrailingZeros().toPlainString());

                            if (unitPrice != null) {
                                BigDecimal totalPrice = unitPrice.multiply(netQty);
                                totalPriceInput.setText(totalPrice.stripTrailingZeros().toPlainString());
                            } else {
                                totalPriceInput.setText("");
                            }
                        } else {
                            totalInput.setText("");
                            netQuantityInput.setText("");
                            totalPriceInput.setText("");
                        }
                    } catch (NumberFormatException e) {
                        // Invalid input, clear results
                        totalInput.setText("");
                        netQuantityInput.setText("");
                        totalPriceInput.setText("");
                    }
                }
            }

            private BigDecimal tryParse(TextInputEditText editText) {
                String text = editText.getText().toString().trim();
                if (!text.isEmpty()) {
                    try {
                        return new BigDecimal(text);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            }
        });

        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText weightTextInputEditText = findViewById(R.id.weightEditText_add_estimate_line);
                TextInputEditText lengthTextInputEditText = findViewById(R.id.lengthEditText_add_estimate_line);
                TextInputEditText widthTextInputEditText = findViewById(R.id.widthEditText_add_estimate_line);
                TextInputEditText heightTextInputEditText = findViewById(R.id.heightEditText_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String quantity = s.toString().trim();

                if (quantity.isEmpty()) {
                    // Clear results and stay in the activity
                    totalEditText.setText("");
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                BigDecimal quantityBD;
                try {
                    quantityBD = new BigDecimal(quantity);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalEditText.setText("");
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                BigDecimal length = !lengthEditText.getText().toString().isEmpty()
                        ? new BigDecimal(lengthEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal weight = !weightEditText.getText().toString().isEmpty()
                        ? new BigDecimal(weightEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal width = !widthEditText.getText().toString().isEmpty()
                        ? new BigDecimal(widthEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal height = !heightEditText.getText().toString().isEmpty()
                        ? new BigDecimal(heightEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal margin = !marginEditText.getText().toString().isEmpty()
                        ? new BigDecimal(marginEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal unitPrice = !unitPriceEditText.getText().toString().isEmpty()
                        ? new BigDecimal(unitPriceEditText.getText().toString()) : BigDecimal.ZERO;

                BigDecimal total = BigDecimal.ZERO;

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!lengthEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty())){
                                total = length.multiply(weight).multiply(quantityBD);
                                totalEditText.setText(total.stripTrailingZeros().toPlainString());

                                BigDecimal marginFactor = BigDecimal.ONE.add(margin.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                                BigDecimal netQuantityPlusMargin = total.multiply(marginFactor);
                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.stripTrailingZeros().toPlainString());

                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                totalPriceEditText.setText(totalPrice.stripTrailingZeros().toPlainString());
                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }
                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                BigDecimal netQuantityPlusMargin = new BigDecimal(netQuantityPlusMarginEditText.getText().toString());
                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();
                                totalPriceEditText.setText(formattedTotalPrice);
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                        case "Surface":
                            if((!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = length.multiply(width).multiply(weight).multiply(quantityBD);
                                totalEditText.setText(total.stripTrailingZeros().toPlainString());

                                BigDecimal marginFactor = BigDecimal.ONE.add(margin.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                                BigDecimal netQuantityPlusMargin = total.multiply(marginFactor);
                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.stripTrailingZeros().toPlainString());

                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                totalPriceEditText.setText(totalPrice.stripTrailingZeros().toPlainString());
                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }
                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                BigDecimal netQuantityPlusMargin = new BigDecimal(netQuantityPlusMarginEditText.getText().toString());
                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();
                                totalPriceEditText.setText(formattedTotalPrice);
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                        case "Volume":
                            if((!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!heightEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = length.multiply(width).multiply(height).multiply(weight).multiply(quantityBD);
                                totalEditText.setText(total.stripTrailingZeros().toPlainString());

                                BigDecimal marginFactor = BigDecimal.ONE.add(margin.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                                BigDecimal netQuantityPlusMargin = total.multiply(marginFactor);
                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.stripTrailingZeros().toPlainString());

                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                totalPriceEditText.setText(totalPrice.stripTrailingZeros().toPlainString());
                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }
                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                BigDecimal netQuantityPlusMargin = new BigDecimal(netQuantityPlusMarginEditText.getText().toString());
                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();
                                totalPriceEditText.setText(formattedTotalPrice);
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                    }
                }
            }
        });

        marginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String marginStr = s.toString().trim();

                if (marginStr.isEmpty()) {
                    // Clear results and stay in the activity
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }

                BigDecimal margin;
                try {
                    margin = new BigDecimal(marginStr);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    netQuantityPlusMarginEditText.setText("");
                    totalPriceEditText.setText("");
                    return;
                }


                String totalStr = totalEditText.getText().toString();
                String unitPriceStr = unitPriceEditText.getText().toString();

                BigDecimal netQuantityPlusMargin = BigDecimal.ZERO;

                // Calculate net quantity + margin

                if (!totalStr.isEmpty()) {
                    BigDecimal total = new BigDecimal(totalStr);

                    if (margin.compareTo(BigDecimal.ZERO) > 0) {
                        // marginRate = margin / 100
                        BigDecimal marginRate = margin.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
                        netQuantityPlusMargin = total.add(total.multiply(marginRate));
                    } else {
                        // No margin or zero margin
                        netQuantityPlusMargin = total;
                    }

                    String formattedNetQuantityPlusMargin = netQuantityPlusMargin.stripTrailingZeros().toPlainString();
                    netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);
                } else {
                    netQuantityPlusMarginEditText.setText("");
                }

                // Calculate total price
                String netQuantityPlusMarginStr = netQuantityPlusMarginEditText.getText().toString();
                if (!unitPriceStr.isEmpty() && !netQuantityPlusMarginStr.isEmpty()) {
                    BigDecimal unitPrice = new BigDecimal(unitPriceStr);
                    BigDecimal totalPrice = netQuantityPlusMargin.multiply(unitPrice);

                    String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();
                    totalPriceEditText.setText(formattedTotalPrice);
                } else {
                    totalPriceEditText.setText("");
                }
            }
        });

        unitPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String unitPriceStr = s.toString();

                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String netQuantityPlusMarginStr = netQuantityPlusMarginEditText.getText().toString();

                if (!unitPriceStr.isEmpty() && !netQuantityPlusMarginStr.isEmpty()) {
                    BigDecimal unitPrice = new BigDecimal(unitPriceStr);
                    BigDecimal netQuantityPlusMargin = new BigDecimal(netQuantityPlusMarginStr);

                    BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                    String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();

                    totalPriceEditText.setText(formattedTotalPrice);
                } else {
                    totalPriceEditText.setText("");
                }
            }
        });
    }

    private BigDecimal parse(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    BigDecimal getBD(TextInputEditText et) {
        String val = et.getText().toString();
        if (val.isEmpty()) return null;
        return new BigDecimal(val);
    }

    private boolean isNonZero(BigDecimal... values) {
        for (BigDecimal val : values) {
            if (val.compareTo(BigDecimal.ZERO) <= 0) return false;
        }
        return true;
    }

    private BigDecimal applyMargin(BigDecimal total, BigDecimal margin) {
        return total.add(total.multiply(margin).divide(new BigDecimal("100")));
    }

    public void startActivityForResult() {
        // Create an Intent to start the target activity
        Intent intent = new Intent(AddEstimateLine.this, Steels.class);

        // Start the target activity for result using the ActivityResultLauncher
        activityResultLauncher.launch(intent);
    }
}