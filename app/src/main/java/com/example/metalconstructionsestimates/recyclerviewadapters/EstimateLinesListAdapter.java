package com.example.metalconstructionsestimates.recyclerviewadapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.modules.estimateslines.EstimateLineDetails;
import com.example.metalconstructionsestimates.util.CurrencyManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class EstimateLinesListAdapter extends RecyclerView.Adapter<EstimateLinesListAdapter.ViewHolder> {

    private ArrayList<EstimateLine> estimateLinesList;
    private Activity                activity;

    // ── Settings ───────────────────────────────────────────────────────────
    private CurrencyManager currencyManager;

    public EstimateLinesListAdapter(Activity activity, ArrayList<EstimateLine> estimateLinesList) {
        this.estimateLinesList = estimateLinesList;
        this.activity          = activity;
        this.currencyManager   = new CurrencyManager(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEstimateLineId;
        public TextView textViewEstimateId;
        public TextView textViewSteelId;
        public TextView textViewTotalPrice;

        public ViewHolder(View view) {
            super(view);
            textViewEstimateLineId = view.findViewById(R.id.recycler_view_estimate_line_id_estimate_line);
            textViewEstimateId     = view.findViewById(R.id.recycler_view_estimate_id_estimate_line);
            textViewSteelId        = view.findViewById(R.id.recycler_view_steel_id_estimate_line);
            textViewTotalPrice     = view.findViewById(R.id.recycler_view_total_price_estimate_line);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.estimate_line_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EstimateLine estimateLine = estimateLinesList.get(position);

        holder.textViewEstimateLineId.setText("Estimate Line Id : " + estimateLine.getId().toString());
        holder.textViewEstimateId.setText("Estimate Id : " + estimateLine.getEstimate());
        holder.textViewSteelId.setText("Steel Id : " + estimateLine.getSteel());

        // ── Total with currency ────────────────────────────────────────────
        Float totalPrice = estimateLine.getTotalPrice();
        String formattedTotal = formatLargeNumber(totalPrice);
        String currencyCode = currencyManager.getActiveCurrencyCode();
        holder.textViewTotalPrice.setText("Estimate Line Total : " + formattedTotal + " " + currencyCode);

        holder.itemView.setOnClickListener(v -> {
            EstimateLine clicked = estimateLinesList.get(holder.getLayoutPosition());
            Intent intent = new Intent(v.getContext(), EstimateLineDetails.class);
            intent.putExtra("estimateLineIdExtra", clicked.getId().toString());
            v.getContext().startActivity(intent);
        });
    }

    private String formatLargeNumber(Float value) {
        if (value == null) return "";
        BigDecimal number = new BigDecimal(value.toString());
        if (number.compareTo(new BigDecimal("1000000")) >= 0) {
            return number.divide(new BigDecimal("1000000"))
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString() + "M";
        } else if (number.compareTo(new BigDecimal("1000")) >= 0) {
            return number.divide(new BigDecimal("1000"))
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString() + "K";
        } else {
            return number.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
    }

    @Override
    public int getItemCount() {
        return estimateLinesList.size();
    }
}