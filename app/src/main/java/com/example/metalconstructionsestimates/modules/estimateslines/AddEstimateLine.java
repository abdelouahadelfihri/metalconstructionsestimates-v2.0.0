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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
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
                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!length.isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(length) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);

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
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);

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
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);

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
                TextInputEditText weightTextInputEditText = findViewById(R.id.weightEditText_add_estimate_line);
                TextInputEditText lengthTextInputEditText = findViewById(R.id.lengthEditText_add_estimate_line);
                TextInputEditText heightTextInputEditText = findViewById(R.id.heightEditText_add_estimate_line);
                TextInputEditText quantityTextInputEditText = findViewById(R.id.quantityEditText_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String width = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Surface":
                            if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!width.isEmpty())  && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(width) * Float.parseFloat(weightTextInputEditText.getText().toString()) *  Float.parseFloat(quantityTextInputEditText.getText().toString());
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);

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
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);
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
                TextInputEditText weightTextInputEditText = findViewById(R.id.weightEditText_add_estimate_line);
                TextInputEditText lengthTextInputEditText = findViewById(R.id.lengthEditText_add_estimate_line);
                TextInputEditText widthTextInputEditText = findViewById(R.id.widthEditText_add_estimate_line);
                TextInputEditText quantityTextInputEditText = findViewById(R.id.quantityEditText_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String height = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Volume":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!height.isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty())  && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(height) * Float.parseFloat(weightTextInputEditText.getText().toString())  * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);
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
                TextInputEditText lengthTextInputEditText = findViewById(R.id.lengthEditText_add_estimate_line);
                TextInputEditText widthTextInputEditText = findViewById(R.id.widthEditText_add_estimate_line);
                TextInputEditText heightTextInputEditText = findViewById(R.id.heightEditText_add_estimate_line);
                TextInputEditText quantityTextInputEditText = findViewById(R.id.quantityEditText_add_estimate_line);
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText marginTextInputEditText = findViewById(R.id.marginEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

                String weight = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weight.isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weight) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);
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
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);
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
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);
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

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantity);
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);

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
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);
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
                                formattedTotal = new BigDecimal(total).toPlainString();
                                totalTextInputEditText.setText(formattedTotal);
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
                TextInputEditText totalTextInputEditText = findViewById(R.id.totalEditText_add_estimate_line);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.unitPriceEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

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
                        netQuantityPlusMargin = Float.parseFloat(totalTextInputEditText.getText().toString());
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

                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.netQuantityEditText_add_estimate_line);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.totalPriceEditText_add_estimate_line);

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