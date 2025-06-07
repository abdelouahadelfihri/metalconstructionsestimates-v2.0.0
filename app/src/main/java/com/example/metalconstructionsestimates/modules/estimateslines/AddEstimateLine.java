package com.example.metalconstructionsestimates.modules.estimateslines;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.modules.estimates.EstimateDetails;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.modules.steels.Steels;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


public class AddEstimateLine extends AppCompatActivity {
    Intent intent;
    DBAdapter dbAdapter;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    Integer estimateId;
    Integer steelId = null;
    String geometricShape = "";
    TextInputEditText steelTypeTextInputEditText;
    TextInputEditText weightTextInputEditText;
    TextInputEditText lengthTextInputEditText;
    TextInputEditText widthTextInputEditText;
    TextInputEditText heightTextInputEditText;
    TextInputEditText quantityTextInputEditText;
    TextInputEditText totalTextInputEditText;
    TextInputEditText marginTextInputEditText;
    TextInputEditText netQuantityPlusMarginTextInputEditText;
    TextInputEditText unitPriceTextInputEditText;
    TextInputEditText totalPriceTextInputEditText;

    String formattedTotal;
    String formattedNetQuantityPlusMargin;
    String formattedTotalPrice;
    Float total,netQuantityPlusMargin,totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estimate_line);
        Toolbar toolbar = findViewById(R.id.toolbar_add_estimate_line);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        estimateId = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("estimateIdExtra")));

        steelTypeTextInputEditText = findViewById(R.id.steelTypeEditText_add_estimate_line);
        weightTextInputEditText = findViewById(R.id.weightEditText_add_estimate_line);
        lengthTextInputEditText = findViewById(R.id.lengthEditText_add_estimate_line);
        widthTextInputEditText = findViewById(R.id.widthEditText_add_estimate_line);
        heightTextInputEditText = findViewById(R.id.heightEditText_add_estimate_line);
        quantityTextInputEditText = findViewById(R.id.quantityEditText_add_estimate_line);
        totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
        marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
        netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
        unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
        totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

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

                            steelTypeTextInputEditText.setText(steel.getType());


                            if (steel.getWeight() == null) {
                                weightTextInputEditText.setText("");
                            } else {
                                weightTextInputEditText.setText(steel.getWeight().toString());
                            }

                            switch (geometricShape) {
                                case "Profile":

                                    lengthTextInputEditText.setEnabled(true);
                                    widthTextInputEditText.setEnabled(false);
                                    heightTextInputEditText.setEnabled(false);

                                    widthTextInputEditText.setText("");
                                    heightTextInputEditText.setText("");

                                    if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                        formattedTotal = new BigDecimal(total).toPlainString();
                                        totalTextInputEditText.setText(formattedTotal);

                                        if (!marginTextInputEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;
                                            String formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginTextInputEditText.setText(formattedNetQuantityPlusMargin);

                                        } else {
                                            formattedNetQuantityPlusMargin = new BigDecimal(total).toPlainString();
                                            netQuantityPlusMarginTextInputEditText.setText(formattedNetQuantityPlusMargin);
                                        }

                                    } else {
                                        totalTextInputEditText.setText("");
                                        netQuantityPlusMarginTextInputEditText.setText("");
                                        totalPriceTextInputEditText.setText("");
                                    }

                                    if (!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()) * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                        formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                        totalPriceTextInputEditText.setText(formattedTotalPrice);
                                    } else {
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    break;
                                case "Surface":
                                    lengthTextInputEditText.setEnabled(true);
                                    widthTextInputEditText.setEnabled(true);
                                    heightTextInputEditText.setEnabled(false);

                                    heightTextInputEditText.setText("");

                                    if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                        formattedTotal = new BigDecimal(total).toPlainString();
                                        totalTextInputEditText.setText(formattedTotal);

                                        if (!marginTextInputEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginTextInputEditText.setText(formattedNetQuantityPlusMargin);
                                        } else {
                                            netQuantityPlusMargin = total;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginTextInputEditText.setText(formattedNetQuantityPlusMargin);
                                        }
                                    } else {
                                        totalTextInputEditText.setText("");
                                        netQuantityPlusMarginTextInputEditText.setText("");
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    if (!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()) * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                        formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                        totalPriceTextInputEditText.setText(formattedTotalPrice);
                                    } else {
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    break;
                                case "Volume":

                                    lengthTextInputEditText.setEnabled(true);
                                    widthTextInputEditText.setEnabled(true);
                                    heightTextInputEditText.setEnabled(true);

                                    if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!heightTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(heightTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                        formattedTotal = new BigDecimal(total).toPlainString();
                                        totalTextInputEditText.setText(formattedTotal);

                                        if (!marginTextInputEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginTextInputEditText.setText(formattedNetQuantityPlusMargin);
                                        } else {
                                            netQuantityPlusMargin = total;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginTextInputEditText.setText(formattedNetQuantityPlusMargin);
                                        }

                                    } else {
                                        totalTextInputEditText.setText("");
                                        netQuantityPlusMarginTextInputEditText.setText("");
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    if (!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()) * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                        formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                        totalPriceTextInputEditText.setText(formattedTotalPrice);
                                    } else {
                                        totalPriceTextInputEditText.setText("");
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

            Objects.requireNonNull(steelTypeTextInputEditText.getText()).clear();
            Objects.requireNonNull(weightTextInputEditText.getText()).clear();
            Objects.requireNonNull(lengthTextInputEditText.getText()).clear();
            Objects.requireNonNull(widthTextInputEditText.getText()).clear();
            Objects.requireNonNull(heightTextInputEditText.getText()).clear();
            Objects.requireNonNull(quantityTextInputEditText.getText()).clear();
            Objects.requireNonNull(totalTextInputEditText.getText()).clear();
            Objects.requireNonNull(marginTextInputEditText.getText()).clear();
            Objects.requireNonNull(netQuantityPlusMarginTextInputEditText.getText()).clear();
            Objects.requireNonNull(unitPriceTextInputEditText.getText()).clear();
            Objects.requireNonNull(totalPriceTextInputEditText.getText()).clear();
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

                if (Objects.requireNonNull(steelTypeTextInputEditText.getText()).toString().isEmpty()) {
                    Toast steelEmptyToast = Toast.makeText(getApplication(), "Steel type empty", Toast.LENGTH_LONG);
                    steelEmptyToast.show();
                } else {
                    if(dbAdapter.getSteelByType(steelTypeTextInputEditText.getText().toString()) == null){
                        Toast steelEmptyToast = Toast.makeText(getApplication(), "Steel does not exist in database", Toast.LENGTH_LONG);
                        steelEmptyToast.show();
                    }
                    else{
                        estimateLine.setSteel(steelId);
                        estimateLine.setEstimate(estimateId);
                        if (!Objects.requireNonNull(weightTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setWeight(Float.parseFloat(weightTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setWeight(null);
                        }

                        if (!Objects.requireNonNull(lengthTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setLength(Float.parseFloat(lengthTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setLength(null);
                        }

                        if (!Objects.requireNonNull(widthTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setWidth(Float.parseFloat(widthTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setWidth(null);
                        }

                        if (!Objects.requireNonNull(heightTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setHeight(Float.parseFloat(heightTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setHeight(null);
                        }

                        if (!Objects.requireNonNull(quantityTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setQuantity(Integer.parseInt(quantityTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setQuantity(null);
                        }

                        if (!Objects.requireNonNull(totalTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setTotal(Float.parseFloat(totalTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setTotal(null);
                        }

                        if (!Objects.requireNonNull(marginTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setMargin(Integer.parseInt(marginTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setMargin(null);
                        }

                        if (!Objects.requireNonNull(netQuantityPlusMarginTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setNetQuantityPlusMargin(Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setNetQuantityPlusMargin(null);
                        }

                        if (!Objects.requireNonNull(unitPriceTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setUnitPrice(Float.parseFloat(unitPriceTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setUnitPrice(null);
                        }

                        if (!Objects.requireNonNull(totalPriceTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setTotalPrice(Float.parseFloat(totalPriceTextInputEditText.getText().toString()));
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

        lengthTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText weightTextInputEditText = findViewById(R.id.weightEditText_add_estimate_line);
                TextInputEditText widthTextInputEditText = findViewById(R.id.widthEditText_add_estimate_line);
                TextInputEditText heightTextInputEditText = findViewById(R.id.heightEditText_add_estimate_line);
                TextInputEditText quantityTextInputEditText = findViewById(R.id.quantityEditText_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String length = s.toString();
                if (!geometricShape.isEmpty()) {
                    try {
                        BigDecimal bdLength = new BigDecimal(length.isEmpty() ? "0" : length);
                        BigDecimal bdWeight = new BigDecimal(weightTextInputEditText.getText().toString().isEmpty() ? "0" : weightTextInputEditText.getText().toString());
                        BigDecimal bdQuantity = new BigDecimal(quantityTextInputEditText.getText().toString().isEmpty() ? "0" : quantityTextInputEditText.getText().toString());
                        BigDecimal total = BigDecimal.ZERO;
                        BigDecimal netQuantityPlusMargin = BigDecimal.ZERO;

                        switch (geometricShape) {
                            case "Profile":
                                if (bdLength.signum() > 0 && bdWeight.signum() > 0 && bdQuantity.signum() > 0) {
                                    total = bdLength.multiply(bdWeight).multiply(bdQuantity);
                                    totalTextInputEditText.setText(total.toPlainString());

                                    BigDecimal bdMargin = new BigDecimal(marginTextInputEditText.getText().toString().isEmpty() ? "0" : marginTextInputEditText.getText().toString());
                                    netQuantityPlusMargin = total.add(total.multiply(bdMargin).divide(new BigDecimal("100")));
                                    netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toPlainString());
                                } else {
                                    totalTextInputEditText.setText("");
                                    netQuantityPlusMarginTextInputEditText.setText("");
                                    totalPriceTextInputEditText.setText("");
                                }
                                break;

                            case "Surface":
                                BigDecimal bdWidth = new BigDecimal(widthTextInputEditText.getText().toString().isEmpty() ? "0" : widthTextInputEditText.getText().toString());
                                if (bdLength.signum() > 0 && bdWidth.signum() > 0 && bdWeight.signum() > 0 && bdQuantity.signum() > 0) {
                                    total = bdLength.multiply(bdWidth).multiply(bdWeight).multiply(bdQuantity);
                                    totalTextInputEditText.setText(total.toPlainString());

                                    BigDecimal bdMargin = new BigDecimal(marginTextInputEditText.getText().toString().isEmpty() ? "0" : marginTextInputEditText.getText().toString());
                                    netQuantityPlusMargin = total.add(total.multiply(bdMargin).divide(new BigDecimal("100")));
                                    netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toPlainString());
                                } else {
                                    totalTextInputEditText.setText("");
                                    netQuantityPlusMarginTextInputEditText.setText("");
                                    totalPriceTextInputEditText.setText("");
                                }
                                break;

                            case "Volume":
                                bdWidth = new BigDecimal(widthTextInputEditText.getText().toString().isEmpty() ? "0" : widthTextInputEditText.getText().toString());
                                BigDecimal bdHeight = new BigDecimal(heightTextInputEditText.getText().toString().isEmpty() ? "0" : heightTextInputEditText.getText().toString());

                                if (bdLength.signum() > 0 && bdWidth.signum() > 0 && bdHeight.signum() > 0 && bdWeight.signum() > 0 && bdQuantity.signum() > 0) {
                                    total = bdLength.multiply(bdWidth).multiply(bdHeight).multiply(bdWeight).multiply(bdQuantity);
                                    totalTextInputEditText.setText(total.toPlainString());

                                    BigDecimal bdMargin = new BigDecimal(marginTextInputEditText.getText().toString().isEmpty() ? "0" : marginTextInputEditText.getText().toString());
                                    netQuantityPlusMargin = total.add(total.multiply(bdMargin).divide(new BigDecimal("100")));
                                    netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toPlainString());
                                } else {
                                    totalTextInputEditText.setText("");
                                    netQuantityPlusMarginTextInputEditText.setText("");
                                    totalPriceTextInputEditText.setText("");
                                }
                                break;
                        }

                        // Calculate total price if possible
                        String unitPriceStr = unitPriceTextInputEditText.getText().toString();
                        if (!unitPriceStr.isEmpty() && netQuantityPlusMargin.signum() > 0) {
                            BigDecimal bdUnitPrice = new BigDecimal(unitPriceStr);
                            BigDecimal totalPrice = bdUnitPrice.multiply(netQuantityPlusMargin);
                            totalPriceTextInputEditText.setText(totalPrice.toPlainString());
                        } else {
                            totalPriceTextInputEditText.setText("");
                        }
                    } catch (NumberFormatException e) {
                        // In case any conversion fails, clear the calculated fields
                        totalTextInputEditText.setText("");
                        netQuantityPlusMarginTextInputEditText.setText("");
                        totalPriceTextInputEditText.setText("");
                    }
                }
            }
        });


        widthTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText weightEditText = findViewById(R.id.weightEditText_add_estimate_line);
                TextInputEditText lengthEditText = findViewById(R.id.lengthEditText_add_estimate_line);
                TextInputEditText heightEditText = findViewById(R.id.heightEditText_add_estimate_line);
                TextInputEditText quantityEditText = findViewById(R.id.quantityEditText_add_estimate_line);
                TextInputEditText totalEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginEditText = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String widthStr = s.toString();

                if (!geometricShape.isEmpty()) {
                    try {
                        BigDecimal length = new BigDecimal(lengthEditText.getText().toString());
                        BigDecimal width = new BigDecimal(widthStr);
                        BigDecimal weight = new BigDecimal(weightEditText.getText().toString());
                        BigDecimal quantity = new BigDecimal(quantityEditText.getText().toString());
                        BigDecimal total = BigDecimal.ZERO;
                        BigDecimal netQuantityPlusMargin;
                        BigDecimal totalPrice;

                        switch (geometricShape) {
                            case "Surface":
                                total = length.multiply(width).multiply(weight).multiply(quantity);
                                totalEditText.setText(total.toPlainString());

                                if (marginEditText.getText().toString().isEmpty()) {
                                    netQuantityPlusMargin = total;
                                } else {
                                    BigDecimal margin = new BigDecimal(marginEditText.getText().toString());
                                    BigDecimal marginAmount = total.multiply(margin).divide(BigDecimal.valueOf(100));
                                    netQuantityPlusMargin = total.add(marginAmount);
                                }
                                netQuantityEditText.setText(netQuantityPlusMargin.toPlainString());

                                if (!unitPriceEditText.getText().toString().isEmpty()) {
                                    BigDecimal unitPrice = new BigDecimal(unitPriceEditText.getText().toString());
                                    totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                    totalPriceEditText.setText(totalPrice.toPlainString());
                                } else {
                                    totalPriceEditText.setText("");
                                }
                                break;

                            case "Volume":
                                BigDecimal height = new BigDecimal(heightEditText.getText().toString());
                                total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                                totalEditText.setText(total.toPlainString());

                                if (marginEditText.getText().toString().isEmpty()) {
                                    netQuantityPlusMargin = total;
                                } else {
                                    BigDecimal margin = new BigDecimal(marginEditText.getText().toString());
                                    BigDecimal marginAmount = total.multiply(margin).divide(BigDecimal.valueOf(100));
                                    netQuantityPlusMargin = total.add(marginAmount);
                                }
                                netQuantityEditText.setText(netQuantityPlusMargin.toPlainString());

                                if (!unitPriceEditText.getText().toString().isEmpty()) {
                                    BigDecimal unitPrice = new BigDecimal(unitPriceEditText.getText().toString());
                                    totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                    totalPriceEditText.setText(totalPrice.toPlainString());
                                } else {
                                    totalPriceEditText.setText("");
                                }
                                break;
                        }
                    } catch (NumberFormatException e) {
                        // Reset fields if any input is invalid
                        totalEditText.setText("");
                        netQuantityEditText.setText("");
                        totalPriceEditText.setText("");
                    }
                }
            }
        });

        heightTextInputEditText.addTextChangedListener(new TextWatcher() {
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

                String heightStr = s.toString();

                if (!geometricShape.isEmpty() && geometricShape.equals("Volume")) {
                    String lengthStr = lengthTextInputEditText.getText().toString();
                    String widthStr = widthTextInputEditText.getText().toString();
                    String weightStr = weightTextInputEditText.getText().toString();
                    String quantityStr = quantityTextInputEditText.getText().toString();
                    String marginStr = marginTextInputEditText.getText().toString();
                    String unitPriceStr = unitPriceTextInputEditText.getText().toString();

                    boolean allFilled = !lengthStr.isEmpty() && !widthStr.isEmpty() && !heightStr.isEmpty()
                            && !weightStr.isEmpty() && !quantityStr.isEmpty();

                    if (allFilled) {
                        BigDecimal length = new BigDecimal(lengthStr);
                        BigDecimal width = new BigDecimal(widthStr);
                        BigDecimal height = new BigDecimal(heightStr);
                        BigDecimal weight = new BigDecimal(weightStr);
                        BigDecimal quantity = new BigDecimal(quantityStr);

                        BigDecimal total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                        totalTextInputEditText.setText(total.toPlainString());

                        BigDecimal netQuantity = total;
                        if (!marginStr.isEmpty()) {
                            BigDecimal margin = new BigDecimal(marginStr);
                            BigDecimal marginValue = total.multiply(margin).divide(BigDecimal.valueOf(100));
                            netQuantity = total.add(marginValue);
                        }

                        netQuantityPlusMarginTextInputEditText.setText(netQuantity.toPlainString());
                    } else {
                        totalTextInputEditText.setText("");
                        netQuantityPlusMarginTextInputEditText.setText("");
                        totalPriceTextInputEditText.setText("");
                        return;
                    }

                    if (!unitPriceStr.isEmpty() && !netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty()) {
                        BigDecimal unitPrice = new BigDecimal(unitPriceStr);
                        BigDecimal netQuantity = new BigDecimal(netQuantityPlusMarginTextInputEditText.getText().toString());
                        BigDecimal totalPrice = unitPrice.multiply(netQuantity);
                        totalPriceTextInputEditText.setText(totalPrice.toPlainString());
                    } else {
                        totalPriceTextInputEditText.setText("");
                    }
                }
            }
        });

        weightTextInputEditText.addTextChangedListener(new TextWatcher() {
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

                String weightStr = s.toString();

                if (!geometricShape.isEmpty()) {
                    try {
                        BigDecimal weight = new BigDecimal(weightStr);
                        BigDecimal length = tryParse(lengthInput);
                        BigDecimal width = tryParse(widthInput);
                        BigDecimal height = tryParse(heightInput);
                        BigDecimal quantity = tryParse(quantityInput);
                        BigDecimal margin = tryParse(marginInput);
                        BigDecimal unitPrice = tryParse(unitPriceInput);

                        BigDecimal total = BigDecimal.ZERO;

                        switch (geometricShape) {
                            case "Profile":
                                if (length != null && quantity != null && weight != null) {
                                    total = length.multiply(weight).multiply(quantity);
                                }
                                break;
                            case "Surface":
                                if (length != null && width != null && quantity != null && weight != null) {
                                    total = length.multiply(width).multiply(weight).multiply(quantity);
                                }
                                break;
                            case "Volume":
                                if (length != null && width != null && height != null && quantity != null && weight != null) {
                                    total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                                }
                                break;
                        }

                        if (total.compareTo(BigDecimal.ZERO) > 0) {
                            totalInput.setText(total.stripTrailingZeros().toPlainString());

                            BigDecimal netQty = (margin != null)
                                    ? total.add(total.multiply(margin).divide(BigDecimal.valueOf(100)))
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

        quantityTextInputEditText.addTextChangedListener(new TextWatcher() {
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


                String quantity = s.toString();

                BigDecimal quantityBD = new BigDecimal(quantity);
                BigDecimal length = !lengthTextInputEditText.getText().toString().isEmpty()
                        ? new BigDecimal(lengthTextInputEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal weight = !weightTextInputEditText.getText().toString().isEmpty()
                        ? new BigDecimal(weightTextInputEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal width = !widthTextInputEditText.getText().toString().isEmpty()
                        ? new BigDecimal(widthTextInputEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal height = !heightTextInputEditText.getText().toString().isEmpty()
                        ? new BigDecimal(heightTextInputEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal margin = !marginTextInputEditText.getText().toString().isEmpty()
                        ? new BigDecimal(marginTextInputEditText.getText().toString()) : BigDecimal.ZERO;
                BigDecimal unitPrice = !unitPriceTextInputEditText.getText().toString().isEmpty()
                        ? new BigDecimal(unitPriceTextInputEditText.getText().toString()) : BigDecimal.ZERO;

                BigDecimal total = BigDecimal.ZERO;

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = length.multiply(weight).multiply(quantityBD);
                                totalTextInputEditText.setText(total.stripTrailingZeros().toPlainString());

                                BigDecimal marginFactor = BigDecimal.ONE.add(margin.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                                BigDecimal netQuantityPlusMargin = total.multiply(marginFactor);
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.stripTrailingZeros().toPlainString());

                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                totalPriceTextInputEditText.setText(totalPrice.stripTrailingZeros().toPlainString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                BigDecimal netQuantityPlusMargin = new BigDecimal(netQuantityPlusMarginTextInputEditText.getText().toString());
                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();
                                totalPriceTextInputEditText.setText(formattedTotalPrice);
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                        case "Surface":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = length.multiply(weight).multiply(quantityBD);
                                totalTextInputEditText.setText(total.stripTrailingZeros().toPlainString());

                                BigDecimal marginFactor = BigDecimal.ONE.add(margin.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                                BigDecimal netQuantityPlusMargin = total.multiply(marginFactor);
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.stripTrailingZeros().toPlainString());

                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                totalPriceTextInputEditText.setText(totalPrice.stripTrailingZeros().toPlainString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                BigDecimal netQuantityPlusMargin = new BigDecimal(netQuantityPlusMarginTextInputEditText.getText().toString());
                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();
                                totalPriceTextInputEditText.setText(formattedTotalPrice);
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                        case "Volume":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = length.multiply(weight).multiply(quantityBD);
                                totalTextInputEditText.setText(total.stripTrailingZeros().toPlainString());

                                BigDecimal marginFactor = BigDecimal.ONE.add(margin.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                                BigDecimal netQuantityPlusMargin = total.multiply(marginFactor);
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.stripTrailingZeros().toPlainString());

                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                totalPriceTextInputEditText.setText(totalPrice.stripTrailingZeros().toPlainString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                BigDecimal netQuantityPlusMargin = new BigDecimal(netQuantityPlusMarginTextInputEditText.getText().toString());
                                BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                                String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();
                                totalPriceTextInputEditText.setText(formattedTotalPrice);
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                    }
                }
            }
        });

        marginTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String marginStr = s.toString();
                String totalStr = totalTextInputEditText.getText().toString();
                String unitPriceStr = unitPriceTextInputEditText.getText().toString();

                BigDecimal netQuantityPlusMargin = BigDecimal.ZERO;

                // Calculate net quantity + margin
                if (!marginStr.isEmpty()) {
                    if (!totalStr.isEmpty()) {
                        BigDecimal total = new BigDecimal(totalStr);
                        BigDecimal margin = new BigDecimal(marginStr);

                        // netQuantityPlusMargin = total + total * (margin / 100)
                        BigDecimal marginRate = margin.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
                        netQuantityPlusMargin = total.add(total.multiply(marginRate));

                        String formattedNetQuantityPlusMargin = netQuantityPlusMargin.stripTrailingZeros().toPlainString();
                        netQuantityPlusMarginTextInputEditText.setText(formattedNetQuantityPlusMargin);
                    } else {
                        netQuantityPlusMarginTextInputEditText.setText("");
                    }
                } else {
                    if (!totalStr.isEmpty()) {
                        netQuantityPlusMargin = new BigDecimal(totalStr);
                        String formattedNetQuantityPlusMargin = netQuantityPlusMargin.stripTrailingZeros().toPlainString();
                        netQuantityPlusMarginTextInputEditText.setText(formattedNetQuantityPlusMargin);
                    } else {
                        netQuantityPlusMarginTextInputEditText.setText("");
                    }
                }

                // Calculate total price
                String netQuantityPlusMarginStr = netQuantityPlusMarginTextInputEditText.getText().toString();
                if (!unitPriceStr.isEmpty() && !netQuantityPlusMarginStr.isEmpty()) {
                    BigDecimal unitPrice = new BigDecimal(unitPriceStr);
                    BigDecimal totalPrice = netQuantityPlusMargin.multiply(unitPrice);

                    String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();
                    totalPriceTextInputEditText.setText(formattedTotalPrice);
                } else {
                    totalPriceTextInputEditText.setText("");
                }
            }
        });


        unitPriceTextInputEditText.addTextChangedListener(new TextWatcher() {
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

                String netQuantityPlusMarginStr = netQuantityPlusMarginTextInputEditText.getText().toString();

                if (!unitPriceStr.isEmpty() && !netQuantityPlusMarginStr.isEmpty()) {
                    BigDecimal unitPrice = new BigDecimal(unitPriceStr);
                    BigDecimal netQuantityPlusMargin = new BigDecimal(netQuantityPlusMarginStr);

                    BigDecimal totalPrice = unitPrice.multiply(netQuantityPlusMargin);
                    String formattedTotalPrice = totalPrice.stripTrailingZeros().toPlainString();

                    totalPriceTextInputEditText.setText(formattedTotalPrice);
                } else {
                    totalPriceTextInputEditText.setText("");
                }
            }
        });

    }
    public void startActivityForResult() {
        // Create an Intent to start the target activity
        Intent intent = new Intent(AddEstimateLine.this, Steels.class);

        // Start the target activity for result using the ActivityResultLauncher
        activityResultLauncher.launch(intent);
    }
}