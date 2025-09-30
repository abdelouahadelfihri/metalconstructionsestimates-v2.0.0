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

import com.example.metalconstructionsestimates.modules.estimates.EstimateDetails;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Toast;
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
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.modules.steels.Steels;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class EstimateLineDetails extends AppCompatActivity {

    EstimateLine estimateLine;
    DBAdapter dbAdapter;
    Integer estimateLineId;
    String geometricShape = "";
    Integer steelId;
    String formattedTotal;
    String formattedNetQuantityPlusMargin;
    String formattedTotalPrice;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_line_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextInputEditText estimateLineIdEditText = findViewById(R.id.et_estimate_line_id);
        TextInputEditText estimateIdEditText = findViewById(R.id.et_estimate_id);
        TextInputEditText steelTypeEditText = findViewById(R.id.et_steel_id);
        TextInputEditText weightEditText = findViewById(R.id.et_weight);
        TextInputEditText lengthEditText = findViewById(R.id.et_length);
        TextInputEditText widthEditText = findViewById(R.id.et_width);
        TextInputEditText heightEditText = findViewById(R.id.et_height);
        TextInputEditText quantityEditText = findViewById(R.id.et_quantity);
        TextInputEditText totalEditText = findViewById(R.id.et_total);
        TextInputEditText marginEditText = findViewById(R.id.et_margin);
        TextInputEditText netQuantityPlusMarginEditText = findViewById(R.id.et_net_quantity);
        TextInputEditText unitPriceEditText = findViewById(R.id.et_unit_price);
        TextInputEditText totalPriceEditText = findViewById(R.id.et_total_price);

        dbAdapter = new DBAdapter(getApplicationContext());
        estimateLineId = Integer.parseInt(getIntent().getStringExtra("estimateLineIdExtra"));
        estimateLine = dbAdapter.getEstimateLineById(estimateLineId);

        estimateLineIdEditText.setText(estimateLine.getId().toString());
        estimateIdEditText.setText(estimateLine.getEstimate().toString());
        String steelType = dbAdapter.getSteelById(estimateLine.getSteel()).getType();
        steelTypeEditText.setText(steelType);

        if(estimateLine.getWeight() == null){
            weightEditText.setText("");
        }
        else{
            weightEditText.setText(BigDecimal.valueOf(estimateLine.getWeight()).toPlainString());
        }

        geometricShape = dbAdapter.getSteelById(estimateLine.getSteel()).getGeometricShape();
        switch (geometricShape) {
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
        }

        if(estimateLine.getLength() == null){
            lengthEditText.setText("");
        }
        else{
            lengthEditText.setText(BigDecimal.valueOf(estimateLine.getLength()).toPlainString());
        }

        if(estimateLine.getWidth() == null){
            widthEditText.setText("");
        }
        else{
            widthEditText.setText(BigDecimal.valueOf(estimateLine.getWidth()).toPlainString());
        }

        if(estimateLine.getHeight() == null){
            heightEditText.setText("");
        }
        else{
            heightEditText.setText(BigDecimal.valueOf(estimateLine.getHeight()).toPlainString());
        }

        if(estimateLine.getQuantity() == null){
            quantityEditText.setText("");
        }
        else{
            quantityEditText.setText(estimateLine.getQuantity().toString());
        }

        if(estimateLine.getMargin() == null){
            marginEditText.setText("");
        }
        else{
            marginEditText.setText(estimateLine.getMargin().toString());
        }

        if(estimateLine.getTotal() == null){
            totalEditText.setText("");
        }
        else{
            totalEditText.setText(BigDecimal.valueOf(estimateLine.getTotal()).toPlainString());
        }

        if(estimateLine.getNetQuantityPlusMargin() == null){
            netQuantityPlusMarginEditText.setText("");
        }
        else{
            netQuantityPlusMarginEditText.setText(BigDecimal.valueOf(estimateLine.getNetQuantityPlusMargin()).toPlainString());
        }

        if(estimateLine.getUnitPrice() == null){
            unitPriceEditText.setText("");
        }
        else{
            unitPriceEditText.setText(BigDecimal.valueOf(estimateLine.getUnitPrice()).toPlainString());
        }

        if(estimateLine.getTotalPrice() == null){
            totalPriceEditText.setText("");
        }
        else{
            totalPriceEditText.setText(BigDecimal.valueOf(estimateLine.getTotalPrice()).toPlainString());
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        // get String data from Intent
                        Float total, netQuantityPlusMargin, totalPrice;

                        String steelIdExtraResult = data.getExtras().getString("steelIdExtraResult");
                        steelId = Integer.parseInt(steelIdExtraResult);
                        Steel steel;

                        steel = dbAdapter.getSteelById(steelId);
                        geometricShape = steel.getGeometricShape();
                        steelTypeEditText.setText(steel.getType());

                        if(steel.getWeight() == null){
                            weightEditText.setText("");
                        }
                        else{
                            weightEditText.setText(steel.getWeight().toString());
                        }


                        weightEditText.setText(steel.getWeight().toString());
                            switch (geometricShape) {
                                case "Profile":

                                    lengthEditText.setEnabled(true);
                                    widthEditText.setEnabled(false);
                                    heightEditText.setEnabled(false);

                                    widthEditText.setText("");
                                    heightEditText.setText("");

                                    if ((!weightEditText.getText().toString().isEmpty()) && (!lengthEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
                                        totalEditText.setText(total.toString());

                                        if (!marginEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) / 100;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);

                                        } else {
                                            formattedTotal = new BigDecimal(total).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedTotal);
                                        }

                                        if (!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())) {
                                            totalPrice = total * Float.parseFloat(unitPriceEditText.getText().toString());
                                            formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                            totalPriceEditText.setText(formattedTotalPrice);
                                        } else {
                                            totalPriceEditText.setText("");
                                        }

                                    }
                                    else{
                                        totalEditText.setText("");
                                        netQuantityPlusMarginEditText.setText("");
                                        totalPriceEditText.setText("");
                                    }
                                    break;
                                case "Surface":
                                    lengthEditText.setEnabled(true);
                                    widthEditText.setEnabled(true);
                                    heightEditText.setEnabled(false);

                                    heightEditText.setText("");

                                    if ((!weightEditText.getText().toString().isEmpty()) &&(!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
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
                                        if ((!unitPriceEditText.getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                            totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceEditText.getText().toString());
                                            formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                            totalPriceEditText.setText(formattedTotalPrice);
                                        } else {
                                            totalPriceEditText.setText("");
                                        }
                                    }
                                    else{
                                        totalEditText.setText("");
                                        netQuantityPlusMarginEditText.setText("");
                                        totalPriceEditText.setText("");
                                    }
                                    break;
                                case "Volume":

                                    lengthEditText.setEnabled(true);
                                    widthEditText.setEnabled(true);
                                    heightEditText.setEnabled(true);

                                    if ((!weightEditText.getText().toString().isEmpty()) &&(!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!heightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {

                                        total = Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(lengthEditText.getText().toString())  * Float.parseFloat(heightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
                                        totalEditText.setText(total.toString());

                                        if (!marginEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) / 100;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);
                                        } else {
                                            netQuantityPlusMargin = total;
                                            formattedNetQuantityPlusMargin = new BigDecimal(netQuantityPlusMargin).toPlainString();
                                            netQuantityPlusMarginEditText.setText(formattedNetQuantityPlusMargin);
                                        }

                                        if ((!unitPriceEditText.getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                            totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceEditText.getText().toString());
                                            formattedTotalPrice = new BigDecimal(totalPrice).toPlainString();
                                            totalPriceEditText.setText(formattedTotalPrice);
                                        } else {
                                            totalPriceEditText.setText("");
                                        }
                                    }
                                    else{
                                        totalEditText.setText("");
                                        netQuantityPlusMarginEditText.setText("");
                                        totalPriceEditText.setText("");
                                    }
                                    break;
                            }
                        }
                    }
        );

        Button updateEstimateLine = findViewById(R.id.btn_update);
        Button deleteEstimateLine = findViewById(R.id.btn_delete);
        Button selectSteel = findViewById(R.id.btn_select_steel);

        selectSteel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult();
            }
        });

        updateEstimateLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EstimateLine estimateLine = new EstimateLine();

                estimateLine.setEstimate(Integer.parseInt(estimateIdEditText.getText().toString()));
                estimateLine.setSteel(steelId);

                if(weightEditText.getText().toString().isEmpty()){
                    estimateLine.setWeight(null);
                }
                else{
                    estimateLine.setWeight(Float.parseFloat(weightEditText.getText().toString()));
                }

                if(lengthEditText.getText().toString().isEmpty()){
                    estimateLine.setLength(null);
                }
                else{
                    estimateLine.setLength(Float.parseFloat(lengthEditText.getText().toString()));
                }

                if(widthEditText.getText().toString().isEmpty()){
                    estimateLine.setWidth(null);
                }
                else{
                    estimateLine.setWidth(Float.parseFloat(widthEditText.getText().toString()));
                }

                if(heightEditText.getText().toString().isEmpty()){
                    estimateLine.setHeight(null);
                }
                else{
                    estimateLine.setHeight(Float.parseFloat(heightEditText.getText().toString()));
                }

                if(quantityEditText.getText().toString().isEmpty()){
                    estimateLine.setQuantity(null);
                }
                else{
                    estimateLine.setQuantity(Long.parseLong(quantityEditText.getText().toString()));
                }

                if(totalEditText.getText().toString().isEmpty()){
                    estimateLine.setTotal(null);
                }
                else{
                    estimateLine.setTotal(Float.parseFloat(totalEditText.getText().toString()));
                }

                if(marginEditText.getText().toString().isEmpty()){
                    estimateLine.setMargin(null);
                }
                else{
                    estimateLine.setMargin(Integer.parseInt(marginEditText.getText().toString()));
                }

                if(netQuantityPlusMarginEditText.getText().toString().isEmpty()){
                    estimateLine.setNetQuantityPlusMargin(null);
                }
                else{
                    estimateLine.setNetQuantityPlusMargin(Float.parseFloat(netQuantityPlusMarginEditText.getText().toString()));
                }

                if(unitPriceEditText.getText().toString().isEmpty()){
                    estimateLine.setUnitPrice(null);
                }
                else{
                    estimateLine.setUnitPrice(Float.parseFloat(unitPriceEditText.getText().toString()));
                }

                if(totalPriceEditText.getText().toString().isEmpty()){
                    estimateLine.setTotalPrice(null);
                }
                else{
                    estimateLine.setTotalPrice(Float.parseFloat(totalPriceEditText.getText().toString()));
                }

                dbAdapter.updateEstimateLine(estimateLine);
                Toast updateResult = Toast.makeText(getApplicationContext(), "Modification du ligne de devis a été éffectué avec succés ", Toast.LENGTH_LONG);
                updateResult.show();
                Float estimateExcludingTaxTotal = dbAdapter.getEstimateExcludingTaxTotal(estimateLine.getEstimate());
                Float excludingTaxTotalAfterDiscount;
                Float allTaxIncludedTotal;
                Estimate estimate;
                estimate = dbAdapter.getEstimateById(estimateLine.getEstimate());
                estimate.setExcludingTaxTotal(estimateExcludingTaxTotal);
                if (!estimate.getDiscount().toString().isEmpty() || estimate.getDiscount() != null) {
                    excludingTaxTotalAfterDiscount = estimateExcludingTaxTotal - estimateExcludingTaxTotal * estimate.getDiscount() / 100;
                    estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                    if (!estimate.getVat().toString().isEmpty() || estimate.getVat() != null) {
                        allTaxIncludedTotal = excludingTaxTotalAfterDiscount + excludingTaxTotalAfterDiscount * estimate.getVat() / 100;
                    } else {
                        allTaxIncludedTotal = excludingTaxTotalAfterDiscount;
                    }
                    estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                } else {
                    estimate.setExcludingTaxTotalAfterDiscount(estimateExcludingTaxTotal);
                    if (!estimate.getVat().toString().isEmpty() || estimate.getVat() != null) {
                        allTaxIncludedTotal = estimateExcludingTaxTotal + estimateExcludingTaxTotal * estimate.getVat() / 100;
                    } else {
                        allTaxIncludedTotal = estimateExcludingTaxTotal;
                    }
                    estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                }
                dbAdapter.updateEstimate(estimate);
            }
        });

        deleteEstimateLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText estimateLineIdEditText = findViewById(R.id.et_estimate_line_id);
                DBAdapter adapter = new DBAdapter(getApplicationContext());
                adapter.deleteEstimateLine(Integer.parseInt(estimateLineIdEditText.getText().toString()));
                Toast deleteResult = Toast.makeText(getApplicationContext(), "The estimate line has been successfully deleted.", Toast.LENGTH_LONG);
                deleteResult.show();
                if(adapter.retrieveEstimatesLinesCount() == 0){
                    adapter.setSeqEstimateLines();
                }
                String estimateId = estimateLine.getEstimate().toString();
                Intent intent = new Intent(getApplicationContext(), EstimateDetails.class);
                intent.putExtra("estimateIdExtra", estimateId);
                startActivity(intent);
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
                    totalTextInputEditText.setText("");
                    netQuantityPlusMarginTextInputEditText.setText("");
                    totalPriceTextInputEditText.setText("");
                    return;
                }

                BigDecimal length;
                try {
                    length = new BigDecimal(lengthStr);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalTextInputEditText.setText("");
                    netQuantityPlusMarginTextInputEditText.setText("");
                    totalPriceTextInputEditText.setText("");
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
                    totalTextInputEditText.setText("");
                    netQuantityPlusMarginTextInputEditText.setText("");
                    totalPriceTextInputEditText.setText("");
                    return;
                }

                BigDecimal width;
                try {
                    width = new BigDecimal(widthStr);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalTextInputEditText.setText("");
                    netQuantityPlusMarginTextInputEditText.setText("");
                    totalPriceTextInputEditText.setText("");
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
                    totalTextInputEditText.setText("");
                    netQuantityPlusMarginTextInputEditText.setText("");
                    totalPriceTextInputEditText.setText("");
                    return;
                }

                BigDecimal height;
                try {
                    height = new BigDecimal(heightStr);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalTextInputEditText.setText("");
                    netQuantityPlusMarginTextInputEditText.setText("");
                    totalPriceTextInputEditText.setText("");
                    return;
                }

                if (!geometricShape.isEmpty() && geometricShape.equals("Volume")) {
                    String lengthStr = lengthTextInputEditText.getText().toString();
                    String widthStr = widthTextInputEditText.getText().toString();
                    String weightStr = weightTextInputEditText.getText().toString();
                    String quantityStr = quantityTextInputEditText.getText().toString();
                    String marginStr = marginTextInputEditText.getText().toString();
                    String unitPriceStr = unitPriceTextInputEditText.getText().toString();

                    boolean allFilled = !lengthStr.isEmpty() && !widthStr.isEmpty() && !weightStr.isEmpty()
                            && !quantityStr.isEmpty();

                    if (allFilled) {
                        BigDecimal length = new BigDecimal(lengthStr);
                        BigDecimal width = new BigDecimal(widthStr);
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
                    totalTextInputEditText.setText("");
                    netQuantityPlusMarginTextInputEditText.setText("");
                    totalPriceTextInputEditText.setText("");
                    return;
                }

                BigDecimal quantityBD;
                try {
                    quantityBD = new BigDecimal(quantity);
                } catch (NumberFormatException e) {
                    // Handle invalid input gracefully
                    totalTextInputEditText.setText("");
                    netQuantityPlusMarginTextInputEditText.setText("");
                    totalPriceTextInputEditText.setText("");
                    return;
                }

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
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty())){
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
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = length.multiply(width).multiply(weight).multiply(quantityBD);
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
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!heightTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = length.multiply(width).multiply(height).multiply(weight).multiply(quantityBD);
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();  // or use NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    public void startActivityForResult() {
        // Create an Intent to start the target activity
        Intent intent = new Intent(EstimateLineDetails.this, Steels.class);
        // Start the target activity for result using the ActivityResultLauncher
        activityResultLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check that it is the SecondActivity with an OK result
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String steelIdExtraResult = data.getExtras().getString("steelIdExtraResult");
            Integer steelId = Integer.parseInt(steelIdExtraResult);

            Steel steel = dbAdapter.getSteelById(steelId);

            geometricShape = steel.getGeometricShape();

            TextInputEditText steelTypeEditText = findViewById(R.id.et_steel_id);
            steelTypeEditText.setText(steel.getType());

            TextInputEditText weightEditText = findViewById(R.id.et_weight);
            TextInputEditText lengthTextInputEditText = findViewById(R.id.et_length);
            TextInputEditText quantityEditText = findViewById(R.id.et_quantity);
            TextInputEditText totalTextInputEditText = findViewById(R.id.et_total);
            TextInputEditText marginTextInputEditText = findViewById(R.id.et_margin);
            TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.et_net_quantity);
            TextInputEditText unitPriceTextInputEditText = findViewById(R.id.et_unit_price);
            TextInputEditText totalPriceTextInputEditText = findViewById(R.id.et_total_price);
            TextInputEditText widthEditText = findViewById(R.id.et_width);
            TextInputEditText heightEditText = findViewById(R.id.et_height);

            BigDecimal length = getBD(lengthTextInputEditText);
            BigDecimal width = getBD(widthEditText);
            BigDecimal height = getBD(heightEditText);
            BigDecimal weight = getBD(weightEditText);
            BigDecimal quantity = getBD(quantityEditText);
            BigDecimal margin = getBD(marginTextInputEditText);
            BigDecimal unitPrice = getBD(unitPriceTextInputEditText);

            BigDecimal total = null;
            BigDecimal netQuantityPlusMargin;
            BigDecimal totalPrice;

            switch (geometricShape) {
                case "Profile":
                    if (length != null && weight != null && quantity != null) {
                        total = length.multiply(weight).multiply(quantity);
                        totalTextInputEditText.setText(total.toPlainString());

                        if (margin != null) {
                            BigDecimal marginFraction = margin.divide(BigDecimal.valueOf(100));
                            netQuantityPlusMargin = total.add(total.multiply(marginFraction));
                        } else {
                            netQuantityPlusMargin = total;
                        }

                        netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toPlainString());

                        if (unitPrice != null) {
                            totalPrice = netQuantityPlusMargin.multiply(unitPrice);
                            totalPriceTextInputEditText.setText(totalPrice.toPlainString());
                        } else {
                            totalPriceTextInputEditText.setText("");
                        }
                    }
                    break;

                case "Surface":
                    if (length != null && width != null && weight != null && quantity != null) {
                        total = length.multiply(width).multiply(weight).multiply(quantity);
                        totalTextInputEditText.setText(total.toPlainString());

                        if (margin != null) {
                            BigDecimal marginFraction = margin.divide(BigDecimal.valueOf(100));
                            netQuantityPlusMargin = total.add(total.multiply(marginFraction));
                        } else {
                            netQuantityPlusMargin = total;
                        }

                        netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toPlainString());

                        if (unitPrice != null) {
                            totalPrice = netQuantityPlusMargin.multiply(unitPrice);
                            totalPriceTextInputEditText.setText(totalPrice.toPlainString());
                        } else {
                            totalPriceTextInputEditText.setText("");
                        }
                    }
                    break;

                case "Volume":
                    if (length != null && width != null && height != null && weight != null && quantity != null) {
                        total = length.multiply(width).multiply(height).multiply(weight).multiply(quantity);
                        totalTextInputEditText.setText(total.toPlainString());

                        if (margin != null) {
                            BigDecimal marginFraction = margin.divide(BigDecimal.valueOf(100));
                            netQuantityPlusMargin = total.add(total.multiply(marginFraction));
                        } else {
                            netQuantityPlusMargin = total;
                        }

                        netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toPlainString());

                        if (unitPrice != null) {
                            totalPrice = netQuantityPlusMargin.multiply(unitPrice);
                            totalPriceTextInputEditText.setText(totalPrice.toPlainString());
                        } else {
                            totalPriceTextInputEditText.setText("");
                        }
                    }
                    break;
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close DBAdapter to release database resources
        if (dbAdapter != null) {
            dbAdapter.close();
        }
    }
}