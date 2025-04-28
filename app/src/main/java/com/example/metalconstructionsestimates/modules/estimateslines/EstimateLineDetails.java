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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;
import com.example.metalconstructionsestimates.modules.steels.Steels;

import com.example.metalconstructionsestimates.customviews.estimatelines.EstimateLinesSteelTypeSelectSteel;
import com.example.metalconstructionsestimates.customviews.UpdateDeleteButtons;

import java.util.concurrent.atomic.AtomicReference;

public class EstimateLineDetails extends AppCompatActivity {

    EstimateLine estimateLine;
    DBAdapter dbAdapter;
    Integer estimateLineId;
    String geometricShape = "";
    Integer steelId;
    Float total,netQuantityPlusMargin,totalPrice;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    EstimateLinesSteelTypeSelectSteel estimateLinesSteelTypeSelectSteel;
    EstimateLinesLengthWidthHeight estimateLinesLengthWidthHeight;
    UpdateDeleteButtons updateDeleteButtons;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_line_details);
        Toolbar toolbar = findViewById(R.id.toolbar_estimate_line_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        estimateLinesLengthWidthHeight = findViewById(R.id.estimate_lines_details_length_width_height);
        estimateLinesSteelTypeSelectSteel = findViewById(R.id.estimate_lines_details_steel_id_select_steel);
        TextInputEditText estimateLineIdTextInputEditText = findViewById(R.id.editText_estimate_line_id_estimate_line_details);
        TextInputEditText estimateIdTextInputEditText = findViewById(R.id.editText_estimate_id_estimate_line_details);
        AtomicReference<TextInputEditText> steelTypeTextInputEditText = new AtomicReference<>(estimateLinesSteelTypeSelectSteel.getTextInputEditTextSteelType());
        AtomicReference<TextInputEditText> weightTextInputEditText = new AtomicReference<>(findViewById(R.id.editText_steel_weight_estimate_line_details));
        AtomicReference<TextInputEditText> lengthTextInputEditText = new AtomicReference<>(estimateLinesLengthWidthHeight.getTextInputEditTextLength());
        AtomicReference<TextInputEditText> widthTextInputEditText = new AtomicReference<>(estimateLinesLengthWidthHeight.getTextInputEditTextWidth());
        AtomicReference<TextInputEditText> heightTextInputEditText = new AtomicReference<>(estimateLinesLengthWidthHeight.getTextInputEditTextHeight());
        AtomicReference<TextInputEditText> quantityTextInputEditText = new AtomicReference<>(findViewById(R.id.editText_quantity_estimate_line_details));
        AtomicReference<TextInputEditText> totalTextInputEditText = new AtomicReference<>(findViewById(R.id.editText_total_estimate_line_details));
        AtomicReference<TextInputEditText> marginTextInputEditText = new AtomicReference<>(findViewById(R.id.editText_margin_estimate_line_details));
        AtomicReference<TextInputEditText> netQuantityPlusMarginTextInputEditText = new AtomicReference<>(findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details));
        AtomicReference<TextInputEditText> unitPriceTextInputEditText = new AtomicReference<>(findViewById(R.id.editText_unit_price_estimate_line_details));
        AtomicReference<TextInputEditText> totalPriceTextInputEditText = new AtomicReference<>(findViewById(R.id.editText_unit_price_estimate_line_details));

        dbAdapter = new DBAdapter(getApplicationContext());
        estimateLineId = Integer.parseInt(getIntent().getStringExtra("estimateLineIdExtra"));
        estimateLine = dbAdapter.getEstimateLineById(estimateLineId);

        estimateLineIdTextInputEditText.setText(estimateLine.getId().toString());
        estimateIdTextInputEditText.setText(estimateLine.getEstimate().toString());
        String steelType = dbAdapter.getSteelById(estimateLine.getSteel()).getType();
        steelTypeTextInputEditText.get().setText(steelType);

        geometricShape = dbAdapter.getSteelById(estimateLine.getSteel()).getGeometricShape();
        switch (geometricShape) {
            case "Profile":

                estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(false);
                estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(false);

                estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setText("");
                estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setText("");

                break;
            case "Surface":

                estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(true);
                estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(false);

                estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setText("");

                break;
            case "Volume":

                estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(true);
                estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(true);

                break;
        }
        if(estimateLine.getWeight() == null){
            weightTextInputEditText.get().setText("");
        }
        else{
            weightTextInputEditText.get().setText(estimateLine.getWeight().toString());
        }

        if(estimateLine.getLength() == null){
            lengthTextInputEditText.get().setText("");
        }
        else{
            lengthTextInputEditText.get().setText(estimateLine.getLength().toString());
        }

        if(estimateLine.getWidth() == null){
            widthTextInputEditText.get().setText("");
        }
        else{
            widthTextInputEditText.get().setText(estimateLine.getWidth().toString());
        }

        if(estimateLine.getHeight() == null){
            heightTextInputEditText.get().setText("");
        }
        else{
            heightTextInputEditText.get().setText(estimateLine.getHeight().toString());
        }

        if(estimateLine.getQuantity() == null){
            quantityTextInputEditText.get().setText("");
        }
        else{
            quantityTextInputEditText.get().setText(estimateLine.getQuantity().toString());
        }

        if(estimateLine.getMargin() == null){
            marginTextInputEditText.get().setText("");
        }
        else{
            marginTextInputEditText.get().setText(estimateLine.getMargin().toString());
        }

        if(estimateLine.getNetQuantityPlusMargin() == null){
            netQuantityPlusMarginTextInputEditText.get().setText("");
        }
        else{
            netQuantityPlusMarginTextInputEditText.get().setText(estimateLine.getNetQuantityPlusMargin().toString());
        }

        if(estimateLine.getUnitPrice() == null){
            unitPriceTextInputEditText.get().setText("");
        }
        else{
            unitPriceTextInputEditText.get().setText(estimateLine.getUnitPrice().toString());
        }

        if(estimateLine.getTotalPrice() == null){
            totalPriceTextInputEditText.get().setText("");
        }
        else{
            totalPriceTextInputEditText.get().setText(estimateLine.getTotalPrice().toString());
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        // get String data from Intent
                        Float total, netQuantityPlusMargin, totalPrice;
                        quantityTextInputEditText.set(findViewById(R.id.editText_quantity_estimate_line_details));
                        totalTextInputEditText.set(findViewById(R.id.editText_total_estimate_line_details));
                        marginTextInputEditText.set(findViewById(R.id.editText_margin_estimate_line_details));
                        netQuantityPlusMarginTextInputEditText.set(findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details));
                        String steelIdExtraResult = data.getExtras().getString("steelIdExtraResult");
                        steelId = Integer.parseInt(steelIdExtraResult);
                        steelTypeTextInputEditText.set(estimateLinesSteelTypeSelectSteel.getTextInputEditTextSteelType());
                        weightTextInputEditText.set(findViewById(R.id.editText_steel_weight_estimate_line_details));

                        Steel steel;

                        steel = dbAdapter.getSteelById(steelId);
                        geometricShape = steel.getGeometricShape();
                        steelTypeTextInputEditText.get().setText(steel.getType());

                        if(steel.getWeight() == null){
                            weightTextInputEditText.get().setText("");
                        }
                        else{
                            weightTextInputEditText.get().setText(steel.getWeight().toString());
                        }

                        unitPriceTextInputEditText.set(findViewById(R.id.editText_unit_price_estimate_line_details));
                        totalPriceTextInputEditText.set(findViewById(R.id.editText_total_price_estimate_line_details));
                        weightTextInputEditText.get().setText(steel.getWeight().toString());
                            switch (geometricShape) {
                                case "Profile":

                                    estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(false);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(false);

                                    estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setText("");
                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setText("");

                                    if ((!weightTextInputEditText.get().getText().toString().isEmpty()) && (!lengthTextInputEditText.get().getText().toString().isEmpty()) && (!quantityTextInputEditText.get().getText().toString().isEmpty())) {

                                        total = Float.parseFloat(weightTextInputEditText.get().getText().toString()) * Float.parseFloat(lengthTextInputEditText.get().getText().toString()) * Float.parseFloat(quantityTextInputEditText.get().getText().toString());
                                        totalTextInputEditText.get().setText(total.toString());

                                        if (!marginTextInputEditText.get().getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.get().getText().toString()) / 100;
                                            netQuantityPlusMarginTextInputEditText.get().setText(netQuantityPlusMargin.toString());

                                        } else {
                                            netQuantityPlusMarginTextInputEditText.get().setText(total.toString());
                                        }

                                        if (!unitPriceTextInputEditText.get().getText().toString().isEmpty() && (!netQuantityPlusMarginTextInputEditText.get().getText().toString().isEmpty())) {
                                            totalPrice = total * Float.parseFloat(unitPriceTextInputEditText.get().getText().toString());
                                            totalPriceTextInputEditText.get().setText(totalPrice.toString());
                                        } else {
                                            totalPriceTextInputEditText.get().setText("");
                                        }

                                    }
                                    else{
                                        totalTextInputEditText.get().setText("");
                                        netQuantityPlusMarginTextInputEditText.get().setText("");
                                        totalPriceTextInputEditText.get().setText("");
                                    }
                                    break;
                                case "Surface":
                                    estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(false);

                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setText("");

                                    if ((!weightTextInputEditText.get().getText().toString().isEmpty()) &&(!lengthTextInputEditText.get().getText().toString().isEmpty()) && (!widthTextInputEditText.get().getText().toString().isEmpty()) && (!quantityTextInputEditText.get().getText().toString().isEmpty())) {

                                        total = Float.parseFloat(weightTextInputEditText.get().getText().toString()) * Float.parseFloat(lengthTextInputEditText.get().getText().toString()) * Float.parseFloat(widthTextInputEditText.get().getText().toString()) * Float.parseFloat(quantityTextInputEditText.get().getText().toString());
                                        totalTextInputEditText.get().setText(total.toString());

                                        if (!marginTextInputEditText.get().getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.get().getText().toString()) / 100;
                                            netQuantityPlusMarginTextInputEditText.get().setText(netQuantityPlusMargin.toString());
                                        } else {
                                            netQuantityPlusMargin = total;
                                            netQuantityPlusMarginTextInputEditText.get().setText(total.toString());
                                        }
                                        if ((!unitPriceTextInputEditText.get().getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                            totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceTextInputEditText.get().getText().toString());
                                            totalPriceTextInputEditText.get().setText(totalPrice.toString());
                                        } else {
                                            totalPriceTextInputEditText.get().setText("");
                                        }
                                    }
                                    else{
                                        totalTextInputEditText.get().setText("");
                                        netQuantityPlusMarginTextInputEditText.get().setText("");
                                        totalPriceTextInputEditText.get().setText("");
                                    }
                                    break;
                                case "Volume":

                                    estimateLinesLengthWidthHeight.getTextInputEditTextLength().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextWidth().setEnabled(true);
                                    estimateLinesLengthWidthHeight.getTextInputEditTextHeight().setEnabled(true);

                                    if ((!weightTextInputEditText.get().getText().toString().isEmpty()) &&(!lengthTextInputEditText.get().getText().toString().isEmpty()) && (!widthTextInputEditText.get().getText().toString().isEmpty()) && (!heightTextInputEditText.get().getText().toString().isEmpty()) && (!quantityTextInputEditText.get().getText().toString().isEmpty())) {

                                        total = Float.parseFloat(weightTextInputEditText.get().getText().toString()) * Float.parseFloat(widthTextInputEditText.get().getText().toString()) * Float.parseFloat(lengthTextInputEditText.get().getText().toString())  * Float.parseFloat(heightTextInputEditText.get().getText().toString()) * Float.parseFloat(quantityTextInputEditText.get().getText().toString());
                                        totalTextInputEditText.get().setText(total.toString());

                                        if (!marginTextInputEditText.get().getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.get().getText().toString()) / 100;
                                            netQuantityPlusMarginTextInputEditText.get().setText(netQuantityPlusMargin.toString());
                                        } else {
                                            netQuantityPlusMargin = total;
                                            netQuantityPlusMarginTextInputEditText.get().setText(total.toString());
                                        }

                                        if ((!unitPriceTextInputEditText.get().getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                            totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceTextInputEditText.get().getText().toString());
                                            totalPriceTextInputEditText.get().setText(totalPrice.toString());
                                        } else {
                                            totalPriceTextInputEditText.get().setText("");
                                        }
                                    }
                                    else{
                                        totalTextInputEditText.get().setText("");
                                        netQuantityPlusMarginTextInputEditText.get().setText("");
                                        totalPriceTextInputEditText.get().setText("");
                                    }
                                    break;
                            }
                        }
                    }
        );

        updateDeleteButtons = (UpdateDeleteButtons) findViewById(R.id.estimate_line_details_update_delete_buttons);

        Button updateEstimateLine = updateDeleteButtons.getUpdateButton();
        Button deleteEstimateLine = updateDeleteButtons.getDeleteButton();
        estimateLinesSteelTypeSelectSteel = (EstimateLinesSteelTypeSelectSteel) findViewById(R.id.estimate_lines_details_steel_id_select_steel);
        Button selectSteel = estimateLinesSteelTypeSelectSteel.getSelectSteelButton();

        selectSteel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult();
            }
        });

        updateEstimateLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText estimateIdTextInputEditText = findViewById(R.id.editText_estimate_id_estimate_line_details);
                TextInputEditText steelTypeTextInputEditText = estimateLinesSteelTypeSelectSteel.getTextInputEditTextSteelType();
                TextInputEditText weightTextInputEditText = findViewById(R.id.editText_steel_weight_estimate_line_details);
                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText quantityTextInputEditText = findViewById(R.id.editText_quantity_estimate_line_details);
                TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);
                EstimateLine estimateLine = new EstimateLine();

                estimateLine.setEstimate(Integer.parseInt(estimateIdTextInputEditText.getText().toString()));
                estimateLine.setSteel(steelId);

                if(weightTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setWeight(null);
                }
                else{
                    estimateLine.setWeight(Float.parseFloat(weightTextInputEditText.getText().toString()));
                }

                if(lengthTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setLength(null);
                }
                else{
                    estimateLine.setLength(Float.parseFloat(lengthTextInputEditText.getText().toString()));
                }

                if(widthTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setWidth(null);
                }
                else{
                    estimateLine.setWidth(Float.parseFloat(widthTextInputEditText.getText().toString()));
                }

                if(heightTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setHeight(null);
                }
                else{
                    estimateLine.setHeight(Float.parseFloat(heightTextInputEditText.getText().toString()));
                }

                if(quantityTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setQuantity(null);
                }
                else{
                    estimateLine.setQuantity(Integer.parseInt(quantityTextInputEditText.getText().toString()));
                }

                if(totalTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setTotal(null);
                }
                else{
                    estimateLine.setTotal(Float.parseFloat(totalTextInputEditText.getText().toString()));
                }

                if(marginTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setMargin(null);
                }
                else{
                    estimateLine.setMargin(Integer.parseInt(marginTextInputEditText.getText().toString()));
                }

                if(netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setNetQuantityPlusMargin(null);
                }
                else{
                    estimateLine.setNetQuantityPlusMargin(Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()));
                }

                if(unitPriceTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setUnitPrice(null);
                }
                else{
                    estimateLine.setUnitPrice(Float.parseFloat(unitPriceTextInputEditText.getText().toString()));
                }


                if(totalPriceTextInputEditText.getText().toString().isEmpty()){
                    estimateLine.setTotalPrice(null);
                }
                else{
                    estimateLine.setTotalPrice(Float.parseFloat(totalPriceTextInputEditText.getText().toString()));
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
                TextInputEditText estimateLineId = findViewById(R.id.editText_estimate_line_id_estimate_line_details);
                DBAdapter adapter = new DBAdapter(getApplicationContext());
                adapter.deleteEstimateLine(Integer.parseInt(estimateLineId.getText().toString()));
                Toast deleteResult = Toast.makeText(getApplicationContext(), "The estimate line has been successfully deleted.", Toast.LENGTH_LONG);
                deleteResult.show();
            }
        });

        lengthTextInputEditText.get().addTextChangedListener(new TextWatcher() {
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
                TextInputEditText weightTextInputEditText = findViewById(R.id.editText_steel_weight_estimate_line_details);

                TextInputEditText quantityTextInputEditText = findViewById(R.id.editText_quantity_estimate_line_details);
                TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);
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

        widthTextInputEditText.get().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText weightTextInputEditText = findViewById(R.id.editText_steel_weight_estimate_line_details);
                TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText quantityTextInputEditText = findViewById(R.id.editText_quantity_estimate_line_details);
                TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);

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

        heightTextInputEditText.get().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText weightTextInputEditText = findViewById(R.id.editText_steel_weight_estimate_line_details);
                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText quantityTextInputEditText = findViewById(R.id.editText_quantity_estimate_line_details);
                TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);

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

        weightTextInputEditText.get().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText quantityTextInputEditText = findViewById(R.id.editText_quantity_estimate_line_details);
                TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);

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

        quantityTextInputEditText.get().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText weightTextInputEditText = findViewById(R.id.editText_steel_weight_estimate_line_details);
                TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);

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

        marginTextInputEditText.get().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);
                TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);

                Float margin;

                if(s.toString().isEmpty()){
                    margin = 0f;
                }
                else{
                    margin = Float.parseFloat(s.toString());
                }

                if(!s.toString().isEmpty()){
                    if(!totalTextInputEditText.getText().toString().isEmpty()){
                        Float total = Float.parseFloat(totalTextInputEditText.getText().toString());
                        netQuantityPlusMargin = total + total * (margin/100);
                        netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                    }
                    else{
                        netQuantityPlusMarginTextInputEditText.setText("");
                    }
                }
                else{
                    if(!totalTextInputEditText.getText().toString().isEmpty()){
                        Float total = Float.parseFloat(totalTextInputEditText.getText().toString());
                        netQuantityPlusMargin = total;
                        netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());
                    }
                    else{
                        netQuantityPlusMarginTextInputEditText.setText("");
                    }
                }

                if((!unitPriceTextInputEditText.getText().toString().isEmpty()) && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                    totalPrice = Float.parseFloat(netQuantityPlusMarginTextInputEditText.getText().toString()) * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                    totalPriceTextInputEditText.setText(totalPrice.toString());
                }
                else{
                    totalPriceTextInputEditText.setText("");
                }
            }
        });


        unitPriceTextInputEditText.get().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String unitPrice = s.toString();
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);
                if((!unitPrice.isEmpty()) && (!netQuantityPlusMarginTextInputEditText.getText().toString().isEmpty())){
                    Float totalPrice = Float.parseFloat(unitPrice) * Float.parseFloat(netQuantityPlusMarginTextInputEditText.toString());

                    totalPriceTextInputEditText.setText(totalPrice.toString());
                }
                else{
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
        switch (requestCode) {

            case 1:
                if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                    // get String data from Intent
                    TextInputEditText quantityTextInputEditText = findViewById(R.id.editText_quantity_estimate_line_details);
                    TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                    TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                    TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                    String steelIdExtraResult = data.getExtras().getString("steelIdExtraResult");
                    Integer steelId = Integer.parseInt(steelIdExtraResult);
                    TextInputEditText steelTypeTextInputEditText = estimateLinesSteelTypeSelectSteel.getTextInputEditTextSteelType();
                    TextInputEditText weightTextInputEditText = findViewById(R.id.editText_steel_weight_estimate_line_details);

                    Steel steel;

                    steel = dbAdapter.getSteelById(steelId);

                    geometricShape = steel.getGeometricShape();

                    steelTypeTextInputEditText.setText(steel.getType());

                    TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                    TextInputEditText widthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                    TextInputEditText heightTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                    TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                    TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);
                    weightTextInputEditText.setText(steel.getWeight().toString());

                    switch (geometricShape) {
                        case "Profile":
                            if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                totalTextInputEditText.setText(total.toString());
                                if (!marginTextInputEditText.getText().toString().isEmpty()) {

                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;

                                } else {
                                    netQuantityPlusMargin = total;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());

                                if ((!unitPriceTextInputEditText.getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                    totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                    totalPriceTextInputEditText.setText(totalPrice.toString());
                                } else {
                                    totalPriceTextInputEditText.setText("");
                                }
                            }
                            break;
                        case "Surface":
                            if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());
                                totalTextInputEditText.setText(total.toString());
                                if (!marginTextInputEditText.getText().toString().isEmpty()) {

                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;

                                } else {
                                    netQuantityPlusMargin = total;
                                }
                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());

                                if ((!unitPriceTextInputEditText.getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                    totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                    totalPriceTextInputEditText.setText(totalPrice.toString());
                                } else {
                                    totalPriceTextInputEditText.setText("");
                                }
                            }
                            break;
                        case "Volume":
                            if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthTextInputEditText.getText().toString().isEmpty()) && (!heightTextInputEditText.getText().toString().isEmpty()) && (!weightTextInputEditText.getText().toString().isEmpty()) && (!quantityTextInputEditText.getText().toString().isEmpty())) {

                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthTextInputEditText.getText().toString()) * Float.parseFloat(heightTextInputEditText.getText().toString()) * Float.parseFloat(weightTextInputEditText.getText().toString()) * Float.parseFloat(quantityTextInputEditText.getText().toString());

                                totalTextInputEditText.setText(total.toString());

                                if (!marginTextInputEditText.getText().toString().isEmpty()) {

                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginTextInputEditText.getText().toString()) / 100;

                                } else {
                                    netQuantityPlusMargin = total;
                                }

                                netQuantityPlusMarginTextInputEditText.setText(netQuantityPlusMargin.toString());

                                if ((!unitPriceTextInputEditText.getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                    totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceTextInputEditText.getText().toString());
                                    totalPriceTextInputEditText.setText(totalPrice.toString());
                                } else {
                                    totalPriceTextInputEditText.setText("");
                                }
                            }
                            break;
                    }
                }

                break;
        }
    }
}