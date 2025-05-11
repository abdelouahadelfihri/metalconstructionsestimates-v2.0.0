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


public class EstimateLineDetails extends AppCompatActivity {

    EstimateLine estimateLine;
    DBAdapter dbAdapter;
    Integer estimateLineId;
    String geometricShape = "";
    Integer steelId;
    Float total,netQuantityPlusMargin,totalPrice;

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
        TextInputEditText weightEditText = findViewById(R.id.et_steel_id);
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
        if(estimateLine.getWeight() == null){
            weightEditText.setText("");
        }
        else{
            weightEditText.setText(estimateLine.getWeight().toString());
        }

        if(estimateLine.getLength() == null){
            lengthEditText.setText("");
        }
        else{
            lengthEditText.setText(estimateLine.getLength().toString());
        }

        if(estimateLine.getWidth() == null){
            widthEditText.setText("");
        }
        else{
            widthEditText.setText(estimateLine.getWidth().toString());
        }

        if(estimateLine.getHeight() == null){
            heightEditText.setText("");
        }
        else{
            heightEditText.setText(estimateLine.getHeight().toString());
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
            totalEditText.setText(estimateLine.getTotal().toString());
        }

        if(estimateLine.getNetQuantityPlusMargin() == null){
            netQuantityPlusMarginEditText.setText("");
        }
        else{
            netQuantityPlusMarginEditText.setText(estimateLine.getNetQuantityPlusMargin().toString());
        }

        if(estimateLine.getUnitPrice() == null){
            unitPriceEditText.setText("");
        }
        else{
            unitPriceEditText.setText(estimateLine.getUnitPrice().toString());
        }

        if(estimateLine.getTotalPrice() == null){
            totalPriceEditText.setText("");
        }
        else{
            totalPriceEditText.setText(estimateLine.getTotalPrice().toString());
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
                                            netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());

                                        } else {
                                            netQuantityPlusMarginEditText.setText(total.toString());
                                        }

                                        if (!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())) {
                                            totalPrice = total * Float.parseFloat(unitPriceEditText.getText().toString());
                                            totalPriceEditText.setText(totalPrice.toString());
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
                                        totalEditText.setText(total.toString());

                                        if (!marginEditText.getText().toString().isEmpty()) {
                                            netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) / 100;
                                            netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());
                                        } else {
                                            netQuantityPlusMargin = total;
                                            netQuantityPlusMarginEditText.setText(total.toString());
                                        }
                                        if ((!unitPriceEditText.getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                            totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceEditText.getText().toString());
                                            totalPriceEditText.setText(totalPrice.toString());
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
                                            netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());
                                        } else {
                                            netQuantityPlusMargin = total;
                                            netQuantityPlusMarginEditText.setText(total.toString());
                                        }

                                        if ((!unitPriceEditText.getText().toString().isEmpty()) && (!netQuantityPlusMargin.toString().isEmpty())) {
                                            totalPrice = netQuantityPlusMargin * Float.parseFloat(unitPriceEditText.getText().toString());
                                            totalPriceEditText.setText(totalPrice.toString());
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
                    estimateLine.setQuantity(Integer.parseInt(quantityEditText.getText().toString()));
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
            }
        });

        lengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                String length = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!length.isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())){

                                total = Float.parseFloat(length) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());

                                totalEditText.setText(total.toString());

                                if(!marginEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) /100;
                                }
                                else{
                                    netQuantityPlusMargin = total;;
                                }

                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());

                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }
                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginEditText.getText().toString());
                                totalPriceEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                        case "Surface":
                            if((!length.isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())){

                                total = Float.parseFloat(length) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());

                                totalEditText.setText(total.toString());

                                if(!marginEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) /100;
                                }
                                else{
                                    netQuantityPlusMargin = total;;
                                }

                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());

                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }
                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginEditText.getText().toString());
                                totalPriceEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                        case "Volume":

                            if((!length.isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!heightEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())){

                                total = Float.parseFloat(length) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(heightEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());

                                totalEditText.setText(total.toString());

                                if(!marginEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) /100;
                                }
                                else{
                                    netQuantityPlusMargin = total;;
                                }

                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());

                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }
                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginEditText.getText().toString());
                                totalPriceEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                    }
                }
            }
        });

        widthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                String width = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Surface":
                                if ((!lengthEditText.getText().toString().isEmpty()) && (!width.isEmpty())  && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {
                                    total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(width) * Float.parseFloat(weightEditText.getText().toString()) *  Float.parseFloat(quantityEditText.getText().toString());
                                    totalEditText.setText(total.toString());

                                    if (marginEditText.getText().toString().isEmpty()) {
                                        netQuantityPlusMargin = total;
                                    } else {
                                        netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) / 100;
                                    }

                                    netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());
                                }
                                else{
                                    totalEditText.setText("");
                                    netQuantityPlusMarginEditText.setText("");
                                    totalPriceEditText.setText("");
                                }

                                if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                    totalPrice = Float.parseFloat(unitPriceEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginEditText.getText().toString());
                                    totalPriceEditText.setText(totalPrice.toString());
                                }
                                else{
                                    totalPriceEditText.setText("");
                                }

                            break;
                        case "Volume":
                            if((!lengthEditText.getText().toString().isEmpty()) && (!width.isEmpty()) && (!heightEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty())  && (!quantityEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(width) * Float.parseFloat(heightEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
                                totalEditText.setText(total.toString());
                                if(marginEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }

                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginEditText.getText().toString());
                                totalPriceEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                    }
                }
            }
        });

        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                String height = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Volume":
                            if((!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!height.isEmpty()) && (!weightEditText.getText().toString().isEmpty())  && (!quantityEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(height) * Float.parseFloat(weightEditText.getText().toString())  * Float.parseFloat(quantityEditText.getText().toString());
                                totalEditText.setText(total.toString());
                                if(marginEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }

                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginEditText.getText().toString());
                                totalPriceEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                    }
                }
            }
        });

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                String weight = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!lengthEditText.getText().toString().isEmpty()) && (!weight.isEmpty()) && (!quantityEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(weight) * Float.parseFloat(quantityEditText.getText().toString());
                                totalEditText.setText(total.toString());
                                if(marginEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }

                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginEditText.getText().toString());
                                totalPriceEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                        case "Surface":
                            if((!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!weight.isEmpty()) && (!quantityEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(weight) * Float.parseFloat(quantityEditText.getText().toString());
                                totalEditText.setText(total.toString());
                                if(marginEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
                            }
                            if(!unitPriceEditText.getText().toString().isEmpty() && (!netQuantityPlusMarginEditText.getText().toString().isEmpty())){
                                totalPrice = Float.parseFloat(unitPriceEditText.getText().toString()) * Float.parseFloat(netQuantityPlusMarginEditText.getText().toString());
                                totalPriceEditText.setText(totalPrice.toString());
                            }
                            else{
                                totalPriceEditText.setText("");
                            }
                            break;
                        case "Volume":
                            if((!lengthEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!heightEditText.getText().toString().isEmpty()) && (!weight.isEmpty()) && (!quantityEditText.getText().toString().isEmpty())){
                                total = Float.parseFloat(lengthEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(heightEditText.getText().toString()) * Float.parseFloat(weight)  * Float.parseFloat(quantityEditText.getText().toString());
                                totalEditText.setText(total.toString());
                                if(marginEditText.getText().toString().isEmpty()){
                                    netQuantityPlusMargin = total;
                                }
                                else{
                                    netQuantityPlusMargin = total + total * Float.parseFloat(marginEditText.getText().toString()) /100;
                                }
                                netQuantityPlusMarginEditText.setText(netQuantityPlusMargin.toString());
                            }
                            else{
                                totalEditText.setText("");
                                netQuantityPlusMarginEditText.setText("");
                                totalPriceEditText.setText("");
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

        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {

                TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                TextInputEditText weightEditText = findViewById(R.id.editText_steel_weight_estimate_line_details);
                TextInputEditText heightEditText = estimateLinesLengthWidthHeight.getTextInputEditTextHeight();
                TextInputEditText widthEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);

                String quantity = s.toString();

                if(!geometricShape.isEmpty()){
                    switch(geometricShape){
                        case "Profile":
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantity);
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
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantity);
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
                            if((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!heightEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantity.isEmpty())){
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(heightEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantity);
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
                    TextInputEditText quantityEditText = findViewById(R.id.editText_quantity_estimate_line_details);
                    TextInputEditText totalTextInputEditText = findViewById(R.id.editText_total_estimate_line_details);
                    TextInputEditText marginTextInputEditText = findViewById(R.id.editText_margin_estimate_line_details);
                    TextInputEditText netQuantityPlusMarginTextInputEditText = findViewById(R.id.editText_net_quantity_plus_margin_estimate_line_details);
                    String steelIdExtraResult = data.getExtras().getString("steelIdExtraResult");
                    Integer steelId = Integer.parseInt(steelIdExtraResult);
                    TextInputEditText steelTypeTextInputEditText = estimateLinesSteelTypeSelectSteel.getTextInputEditTextSteelType();
                    TextInputEditText weightEditText = findViewById(R.id.editText_steel_weight_estimate_line_details);

                    Steel steel;

                    steel = dbAdapter.getSteelById(steelId);

                    geometricShape = steel.getGeometricShape();

                    steelTypeTextInputEditText.setText(steel.getType());

                    TextInputEditText lengthTextInputEditText = estimateLinesLengthWidthHeight.getTextInputEditTextLength();
                    TextInputEditText widthEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                    TextInputEditText heightEditText = estimateLinesLengthWidthHeight.getTextInputEditTextWidth();
                    TextInputEditText unitPriceTextInputEditText = findViewById(R.id.editText_unit_price_estimate_line_details);
                    TextInputEditText totalPriceTextInputEditText = findViewById(R.id.editText_total_price_estimate_line_details);
                    weightEditText.setText(steel.getWeight().toString());

                    switch (geometricShape) {
                        case "Profile":
                            if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
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
                            if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {
                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());
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
                            if ((!lengthTextInputEditText.getText().toString().isEmpty()) && (!widthEditText.getText().toString().isEmpty()) && (!heightEditText.getText().toString().isEmpty()) && (!weightEditText.getText().toString().isEmpty()) && (!quantityEditText.getText().toString().isEmpty())) {

                                total = Float.parseFloat(lengthTextInputEditText.getText().toString()) * Float.parseFloat(widthEditText.getText().toString()) * Float.parseFloat(heightEditText.getText().toString()) * Float.parseFloat(weightEditText.getText().toString()) * Float.parseFloat(quantityEditText.getText().toString());

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