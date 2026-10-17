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
import java.util.Collections;
import java.util.Map;

public class EstimateLinesListAdapter extends RecyclerView.Adapter<EstimateLinesListAdapter.ViewHolder> {

    private ArrayList<EstimateLine> estimateLinesList;
    private Activity                activity;

    // ── Settings ───────────────────────────────────────────────────────────
    private CurrencyManager currencyManager;

    // Steel id -> steel type, so each line shows what it is made of
    private Map<Integer, String> steelNames = Collections.emptyMap();

    public EstimateLinesListAdapter(Activity activity, ArrayList<EstimateLine> estimateLinesList) {
        this.estimateLinesList = estimateLinesList;
        this.activity          = activity;
        this.currencyManager   = new CurrencyManager(activity);
    }

    public void updateEstimateLines(ArrayList<EstimateLine> estimateLines, Map<Integer, String> steelNames) {
        this.estimateLinesList = estimateLines;
        this.steelNames        = steelNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEstimateLineId;
        public TextView textViewSteel;
        public TextView textViewQuantity;
        public TextView textViewDimensions;
        public TextView textViewUnitPrice;
        public TextView textViewTotalPrice;

        public ViewHolder(View view) {
            super(view);
            textViewEstimateLineId = view.findViewById(R.id.recycler_view_estimate_line_id_estimate_line);
            textViewSteel          = view.findViewById(R.id.recycler_view_steel_id_estimate_line);
            textViewQuantity       = view.findViewById(R.id.recycler_view_quantity_estimate_line);
            textViewDimensions     = view.findViewById(R.id.recycler_view_dimensions_estimate_line);
            textViewUnitPrice      = view.findViewById(R.id.recycler_view_unit_price_estimate_line);
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
        String currencyCode = currencyManager.getActiveCurrencyCode();

        holder.textViewEstimateLineId.setText("#" + estimateLine.getId());

        // ── Steel: type name, falling back to its id ──────────────────────
        String steelName = estimateLine.getSteel() == null ? null : steelNames.get(estimateLine.getSteel());
        if (steelName != null && !steelName.isEmpty()) {
            holder.textViewSteel.setText(steelName);
        } else {
            holder.textViewSteel.setText("Steel #" + estimateLine.getSteel());
        }

        // ── Quantity and margin ────────────────────────────────────────────
        StringBuilder quantity = new StringBuilder("Qty : ")
                .append(estimateLine.getQuantity() == null ? "-" : estimateLine.getQuantity());
        if (estimateLine.getMargin() != null && estimateLine.getMargin() != 0) {
            quantity.append("  ·  Margin : ").append(estimateLine.getMargin()).append('%');
        }
        if (estimateLine.getWeight() != null && estimateLine.getWeight() != 0) {
            quantity.append("  ·  Weight : ").append(plain(estimateLine.getWeight()));
        }
        holder.textViewQuantity.setText(quantity);

        // ── Dimensions: only the ones that are set ─────────────────────────
        StringBuilder dimensions = new StringBuilder();
        appendDimension(dimensions, "L", estimateLine.getLength());
        appendDimension(dimensions, "W", estimateLine.getWidth());
        appendDimension(dimensions, "H", estimateLine.getHeight());
        holder.textViewDimensions.setVisibility(dimensions.length() == 0 ? View.GONE : View.VISIBLE);
        holder.textViewDimensions.setText(dimensions);

        // ── Unit price and total with currency ────────────────────────────
        holder.textViewUnitPrice.setText(estimateLine.getUnitPrice() == null
                ? ""
                : "Unit price : " + EstimatesListAdapter.formatLargeNumber(estimateLine.getUnitPrice()) + " " + currencyCode);
        holder.textViewTotalPrice.setText(
                EstimatesListAdapter.formatLargeNumber(estimateLine.getTotalPrice()) + " " + currencyCode);

        holder.itemView.setOnClickListener(v -> {
            EstimateLine clicked = estimateLinesList.get(holder.getLayoutPosition());
            Intent intent = new Intent(v.getContext(), EstimateLineDetails.class);
            intent.putExtra("estimateLineIdExtra", clicked.getId().toString());
            v.getContext().startActivity(intent);
        });
    }

    private static void appendDimension(StringBuilder text, String label, Float value) {
        if (value == null || value == 0) return;
        if (text.length() > 0) text.append("  ×  ");
        text.append(label).append(' ').append(plain(value));
    }

    // 6.0 -> "6", 0.25 -> "0.25"
    private static String plain(Float value) {
        return new BigDecimal(value.toString()).setScale(3, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Override
    public int getItemCount() {
        return estimateLinesList.size();
    }
}
