package com.example.metalconstructionsestimates.arraysadapters;

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

import java.util.ArrayList;


public class EstimateLinesListAdapter extends RecyclerView.Adapter<EstimateLinesListAdapter.ViewHolder> {

    private ArrayList<EstimateLine> estimateLinesList;
    private Activity activity;

    public EstimateLinesListAdapter(Activity activity, ArrayList<EstimateLine> estimateLinesList) {
        this.estimateLinesList = estimateLinesList;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewEstimateLineId;
        public TextView textViewEstimateId;
        public TextView textViewSteelId;
        public TextView textViewTotalPrice;

        public ViewHolder(View view) {
            super(view);
            textViewEstimateLineId = view.findViewById(R.id.recycler_view_estimate_line_id_estimate_line);
            textViewEstimateId = view.findViewById(R.id.recycler_view_estimate_id_estimate_line);
            textViewSteelId = view.findViewById(R.id.recycler_view_steel_id_estimate_line);
            textViewTotalPrice = view.findViewById(R.id.recycler_view_total_price_estimate_line);
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
        String estimateLineId = "Id Ligne De Devis : " + estimateLine.getId().toString();
        String estimateId = "Id Devis : " + estimateLine.getEstimate();
        String steelId = "Id Acier : " + estimateLine.getSteel();
        String totalPrice = "Prix Total : " + estimateLine.getTotalPrice();
        holder.textViewEstimateLineId.setText(estimateLineId);
        holder.textViewEstimateId.setText(estimateId);
        holder.textViewSteelId.setText(steelId);
        holder.textViewTotalPrice.setText(totalPrice);

        // Set an onClick listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int layoutPosition = holder.getLayoutPosition();
                EstimateLine clickedEstimateLine = estimateLinesList.get(layoutPosition);  // Get the Steel item at this position
                Intent intent;
                intent = new Intent(v.getContext(), EstimateLineDetails.class);
                intent.putExtra("estimateLineIdExtra", clickedEstimateLine.getId().toString());
                v.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return estimateLinesList.size();
    }
}