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
import com.example.metalconstructionsestimates.customviews.estimatelines.EstimateLinesLengthWidthHeight;
import com.example.metalconstructionsestimates.customviews.estimatelines.EstimateLinesSteelTypeSelectSteel;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.modules.estimates.EstimateDetails;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.modules.steels.Steels;
import com.example.metalconstructionsestimates.customviews.AddClearButtons;

import java.util.Objects;


public class AddEstimateLine extends AppCompatActivity {
    Intent intent;
    DBAdapter dbAdapter;
    EstimateLinesSteelTypeSelectSteel estimateLinesSteelTypeSelectSteel;
    EstimateLinesLengthWidthHeight estimateLinesLengthWidthHeight;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    AddClearButtons addClearButtons;
    Integer estimateId;
    Integer steelId = null;
    String geometricShape = "";

    Float total,netQuantityPlusMargin,totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estimate_line);
        Toolbar toolbar = findViewById(R.id.toolbar_add_estimate_line);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        estimateId = Integer.parseInt(getIntent().getStringExtra("estimateIdExtra"));
        estimateLinesSteelTypeSelectSteel = findViewById(R.id.estimate_lines_steel_type_select_steel_add_estimate_line);
        estimateLinesLengthWidthHeight = findViewById(R.id.length_width_height_add_estimate_line);

        TextInputEditText steelTypeTextInputEditText = estimateLinesSteelTypeSelectSteel.getTextInputEditTextSteelType();
        TextInputEditText weightTextInputEditText = findViewById(R.id.textInputEditText_steel_weight_add_estimate_line);
        TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
        TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
        TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
        TextInputEditText quantityTextInputEditText = findViewById(R.id.textInputEditText_quantity_add_estimate_line);
        TextInputEditText totalTextInputEditText = findViewById(R.id.textInputEditText_total_add_estimate_line);
        TextInputEditText marginTextInputEditText = findViewById(R.id.textInputEditText_margin_add_estimate_line);
        TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
        TextInputEditText unitPriceTextInputEditText = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
        TextInputEditText totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);

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
                            String steelIdExtraResult = data.getExtras().getString("steelIdExtraResult");
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

                                    estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(false);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(false);

                                    estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setText("");
                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setText("");

                                    if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                        totalTextInputEditText.setText(total.toString());

                                        if (!marginTextInputEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;
                                            netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());

                                        } else {
                                            netQuantityPlusMarginTextInputEditText.setText(total.toString());
                                        }

                                    } else {
                                        totalTextInputEditText.setText("");
                                        netQuantityPlusMarginTextInputEditText.setText("");
                                        totalPriceTextInputEditText.setText("");
                                    }

                                    if (!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()) * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                        totalPriceTextInputEditText.setText(totalPrice.toString());
                                    } else {
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    break;
                                case "Surface":
                                    estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(false);

                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setText("");

                                    if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                        totalTextInputEditText.setText(total.toString());

                                        if (!marginTextInputEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;
                                            netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                                        } else {
                                            netQuantityPlusMargin = total;
                                            netQuantityPlusMarginTextInputEditText.setText(total.toString());
                                        }
                                    } else {
                                        totalTextInputEditText.setText("");
                                        netQuantityPlusMarginTextInputEditText.setText("");
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    if (!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()) * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                        totalPriceTextInputEditText.setText(totalPrice.toString());
                                    } else {
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    break;
                                case "Volume":

                                    estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(true);

                                    if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!heightTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(heightTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                        totalTextInputEditText.setText(total.toString());

                                        if (!marginTextInputEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;
                                            netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                                        } else {
                                            netQuantityPlusMargin = total;
                                            netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                                        }

                                    } else {
                                        totalTextInputEditText.setText("");
                                        netQuantityPlusMarginTextInputEditText.setText("");
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    if (!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())) {
                                        totalPrice = Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()) * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                        totalPriceTextInputEditText.setText(totalPrice.toString());
                                    } else {
                                        totalPriceTextInputEditText.setText("");
                                    }
                                    break;
                            }
                        }
                    }
                }
        );

        addClearButtons = findViewById(R.id.add_clear_buttons_add_estimate_line);
        Button addEstimateLine = addClearButtons.getAddButton();
        Button clearEstimateLineForm = addClearButtons.getClearButton();

        clearEstimateLineForm.setOnClickListener(view -> {
            TextInputEditText steelTypeTextInputEditText1 = estimateLinesSteelTypeSelectSteel.getTextInputEditTextSteelType();
            TextInputEditText steelWeightTextInputEditText = findViewById(R.id.textInputEditText_steel_weight_add_estimate_line);
            estimateLinesLengthWidthHeight = findViewById(R.id.length_width_height_add_estimate_line);
            TextInputEditText steelLengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
            TextInputEditText steelWidthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
            TextInputEditText steelHeightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
            TextInputEditText steelQuantityTextInputEditText = findViewById(R.id.textInputEditText_quantity_add_estimate_line);
            TextInputEditText totalTextInputEditText1 = findViewById(R.id.textInputEditText_total_add_estimate_line);
            TextInputEditText marginTextInputEditText1 = findViewById(R.id.textInputEditText_margin_add_estimate_line);
            TextInputEditText netQuantityPlusMarginTextInputEditText1 = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
            TextInputEditText unitPriceTextInputEditText1 = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
            TextInputEditText totalPriceTextInputEditText1 = findViewById(R.id.textInputEditText_total_price_add_estimate_line);
            Objects.requireNonNull(steelTypeTextInputEditText1.getText()).clear();
            Objects.requireNonNull(steelWeightTextInputEditText.getText()).clear();
            Objects.requireNonNull(steelLengthTextInputEditText.getText()).clear();
            Objects.requireNonNull(steelWidthTextInputEditText.getText()).clear();
            Objects.requireNonNull(steelHeightTextInputEditText.getText()).clear();
            Objects.requireNonNull(steelQuantityTextInputEditText.getText()).clear();
            Objects.requireNonNull(totalTextInputEditText1.getText()).clear();
            Objects.requireNonNull(marginTextInputEditText1.getText()).clear();
            Objects.requireNonNull(netQuantityPlusMarginTextInputEditText1.getText()).clear();
            Objects.requireNonNull(unitPriceTextInputEditText1.getText()).clear();
            Objects.requireNonNull(totalPriceTextInputEditText1.getText()).clear();
        });

        Button selectSteel = estimateLinesSteelTypeSelectSteel.getSelectSteelButton();

        selectSteel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult();
            }
        });

        addEstimateLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText totalTextInputEditText;
                TextInputEditText netQuantityPlusMarginTextInputEditText;
                TextInputEditText totalPriceTextInputEditText;
                TextInputEditText marginTextInputEditText;
                TextInputEditText steelWeightTextInputEditText;
                TextInputEditText steelLengthTextInputEditText;
                TextInputEditText steelHeightTextInputEditText;
                TextInputEditText unitPriceTextInputEditText;
                TextInputEditText steelTypeTextInputEditText;
                TextInputEditText steelQuantityTextInputEditText;
                TextInputEditText steelWidthTextInputEditText;
                EstimateLine estimateLine = new EstimateLine();
                steelTypeTextInputEditText = estimateLinesSteelTypeSelectSteel.getTextInputEditTextSteelType();
                estimateLinesLengthWidthHeight = findViewById(R.id.length_width_height_add_estimate_line);
                steelLengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                steelWidthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                steelHeightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                steelWeightTextInputEditText = findViewById(R.id.textInputEditText_steel_weight_add_estimate_line);
                steelQuantityTextInputEditText = findViewById(R.id.textInputEditText_quantity_add_estimate_line);
                totalTextInputEditText = findViewById(R.id.textInputEditText_total_add_estimate_line);
                marginTextInputEditText = findViewById(R.id.textInputEditText_margin_add_estimate_line);
                netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
                unitPriceTextInputEditText = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
                totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);

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
                        if (!Objects.requireNonNull(steelWeightTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setWeight(Float.parseFloat(steelWeightTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setWeight(null);
                        }

                        if (!Objects.requireNonNull(steelLengthTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setLength(Float.parseFloat(steelLengthTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setLength(null);
                        }

                        if (!Objects.requireNonNull(steelWidthTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setWidth(Float.parseFloat(steelWidthTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setWidth(null);
                        }

                        if (!Objects.requireNonNull(steelHeightTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setHeight(Float.parseFloat(steelHeightTextInputEditText.getText().toString()));
                        } else {
                            estimateLine.setHeight(null);
                        }

                        if (!Objects.requireNonNull(steelQuantityTextInputEditText.getText()).toString().isEmpty()) {
                            estimateLine.setQuantity(Integer.parseInt(steelQuantityTextInputEditText.getText().toString()));
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
                                "Estimate line have been successfully added", Toast.LENGTH_LONG);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText weightTextInputEditText = findViewById(R.id.textInputEditText_steel_weight_add_estimate_line);

                TextInputEditText quantityTextInputEditText = findViewById(R.id.textInputEditText_quantity_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.textInputEditText_total_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.textInputEditText_margin_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);
                String length = s.toString();
                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!length.isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(length) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());

                                totalTextInputEditText.setText(total.toString());

                                if(!marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }
                                else{
                                    netQuantityPlusMargin = total;;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());

                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                        case "Surface":
                            if((!length.isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())){

                                total = Float.parseFloat(length) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());

                                totalTextInputEditText.setText(total.toString());

                                if(!marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }
                                else{
                                    netQuantityPlusMargin = total;;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());

                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                        case "Volume":

                            if((!length.isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!heightTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())){

                                total = Float.parseFloat(length) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(heightTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());

                                totalTextInputEditText.setText(total.toString());

                                if(!marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }
                                else{
                                    netQuantityPlusMargin = total;;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());

                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                    }
                }
            }
        });

        widthTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText weightTextInputEditText = findViewById(R.id.textInputEditText_steel_weight_add_estimate_line);
                TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText quantityTextInputEditText = findViewById(R.id.textInputEditText_quantity_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.textInputEditText_total_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.textInputEditText_margin_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);

                String width = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Surface":
                            if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!width.isEmpty())  && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(width) * Float.parseFloat(weightTextInputEditText.getText().toString()) *  Float.parseFloat(quantityTextInputEditText.getText().toString());
                                totalTextInputEditText.setText(total.toString());

                                if (marginTextInputEditText.getText().toString().isEmpty()) {
                                    netQuantityPlusMargin = total;
                                } else {
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }

                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }

                            break;
                        case "Volume":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!width.isEmpty()) && (!heightTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty())  && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(width) * Float.parseFloat(heightTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                totalTextInputEditText.setText(total.toString());
                                if(marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }

                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                    }
                }
            }
        });

        heightTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText weightTextInputEditText = findViewById(R.id.textInputEditText_steel_weight_add_estimate_line);
                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText quantityTextInputEditText = findViewById(R.id.textInputEditText_quantity_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.textInputEditText_total_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.textInputEditText_margin_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);

                String height = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Volume":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!height.isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty())  && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(height) * Float.parseFloat(weightTextInputEditText.getText().toString())  * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                totalTextInputEditText.setText(total.toString());
                                if(marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }

                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                    }
                }
            }
        });

        weightTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText quantityTextInputEditText = findViewById(R.id.textInputEditText_quantity_add_estimate_line);
                TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText totalTextInputEditText = findViewById(R.id.textInputEditText_total_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.textInputEditText_margin_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);

                String weight = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weight.isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weight) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                totalTextInputEditText.setText(total.toString());
                                if(marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }

                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                        case "Surface":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!weight.isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(weight) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                totalTextInputEditText.setText(total.toString());
                                if(marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                        case "Volume":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!heightTextInputEditText.getText().toString().isEmpty()) && (!weight.isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(heightTextInputEditText.getText().toString()) * Float.parseFloat(weight)  * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                totalTextInputEditText.setText(total.toString());
                                if(marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                    }
                }
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

                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText weightTextInputEditText = findViewById(R.id.textInputEditText_steel_weight_add_estimate_line);
                TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText totalTextInputEditText = findViewById(R.id.textInputEditText_total_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.textInputEditText_margin_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);

                String quantity = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantity);
                                totalTextInputEditText.setText(total.toString());

                                if(marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                        case "Surface":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantity);
                                totalTextInputEditText.setText(total.toString());
                                if(marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }
                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceTextInputEditText.setText("");
                            }
                            break;
                        case "Volume":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!heightTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(heightTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantity);
                                totalTextInputEditText.setText(total.toString());
                                if(marginTextInputEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) /100;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalTextInputEditText.setText("");
                                netQuantityPlusMarginTextInputEditText.setText("");
                                totalPriceTextInputEditText.setText("");
                            }

                            if(!unitPriceTextInputEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceTextInputEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());
                                totalPriceTextInputEditText.setText(totalPrice.toString());
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

                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.textInputEditText_unit_price_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.textInputEditText_total_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);

                Float netQuantityPlusMargin = 0f;

                String margin = s.toString();

                if(!margin.isEmpty()){
                    if(!totalTextInputEditText.getText().toString().isEmpty()){
                        float total = Float.parseFloat(totalTextInputEditText.getText().toString());
                        netQuantityPlusMargin = total + total * (Float.parseFloat(margin)/100);
                        netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                    }
                    else{
                        netQuantityPlusMarginTextInputEditText.setText("");
                    }
                }
                else{
                    if(!totalTextInputEditText.getText().toString().isEmpty()){
                        total = Float.parseFloat(totalTextInputEditText.getText().toString());
                        netQuantityPlusMargin = total;
                        netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                    }
                    else{
                        netQuantityPlusMarginTextInputEditText.setText("");
                    }
                }

                if((!unitPriceTextInputEditText.getText().toString().isEmpty()) && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                    totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                    totalPriceTextInputEditText.setText(totalPrice.toString());
                }
                else{
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
                String unitPrice = s.toString();
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.textInputEditText_net_quantity_plus_margin_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.textInputEditText_total_price_add_estimate_line);
                if((!unitPrice.isEmpty()) && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                    Float totalPrice = Float.parseFloat(unitPrice) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString());

                    totalPriceTextInputEditText.setText(totalPrice.toString());
                }
                else{
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