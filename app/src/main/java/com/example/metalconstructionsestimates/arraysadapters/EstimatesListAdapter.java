package com.example.metalconstructionsestimates.arraysadapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.modules.estimates.EstimateDetails;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.util.ArrayList;

public class EstimatesListAdapter extends RecyclerView.Adapter<EstimatesListAdapter.EstimateViewHolder>{

    private ArrayList<Estimate> estimates;

    private LayoutInflater mInflater;

    public EstimatesListAdapter(Context context, ArrayList<Estimate> estimates){

        mInflater = LayoutInflater.from(context);
        this.estimates = estimates;

    }

    public void updateEstimates(ArrayList<Estimate> estimates){

        this.estimates = estimates;
        notifyDataSetChanged();

    }

    @Override
    public EstimateViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View mItemView = mInflater.inflate(R.layout.estimate_item, parent, false);
        return new EstimateViewHolder(mItemView, this);

    }

    @Override
    public void onBindViewHolder(EstimateViewHolder holder, int position){

        Estimate estimate = estimates.get(position);


        Float total = estimate.getAllTaxIncludedTotal();
        String formattedTotal = formatLargeNumber(total);
        String estimateTotalAllTaxIncluded = "Incl. VAT : " + formattedTotal;
        String estimateId = "Estimate Id : " + estimate.getId().toString();
        String estimateCreationDate = "Issue Date : " + estimate.getIssueDate();
        String estimateDoneIn = "Location : " + estimate.getDoneIn();

        holder.estimateIdTextView.setText(estimateId);
        holder.estimateCreationDateTextView.setText(estimateCreationDate);
        holder.estimateDoneInTextView.setText(estimateDoneIn);
        holder.estimateTotalAllTaxIncludedTextView.setText(estimateTotalAllTaxIncluded);

    }

    class EstimateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView estimateIdTextView;
        public final TextView estimateCreationDateTextView;
        public final TextView estimateDoneInTextView;
        public final TextView estimateTotalAllTaxIncludedTextView;

        final EstimatesListAdapter estimateListAdapter;

        public EstimateViewHolder(View itemView, EstimatesListAdapter estimateListAdapter){
            super(itemView);
            estimateIdTextView = (TextView) itemView.findViewById(R.id.recycler_view_estimate_id);
            estimateCreationDateTextView = (TextView) itemView.findViewById(R.id.recycler_view_estimate_date);
            estimateDoneInTextView = (TextView) itemView.findViewById(R.id.recycler_view_done_in);
            estimateTotalAllTaxIncludedTextView = (TextView) itemView.findViewById(R.id.recycler_view_total_all_tax_included);
            this.estimateListAdapter = estimateListAdapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int mPosition = getLayoutPosition();

            Estimate estimate = estimates.get(mPosition);
            String estimateId = estimate.getId().toString();
            Intent intent = new Intent(view.getContext(), EstimateDetails.class);
            intent.putExtra("estimateIdExtra", estimateId);
            view.getContext().startActivity(intent);
            estimateListAdapter.notifyDataSetChanged();
        }
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
    public int getItemCount(){
        return estimates.size();
    }
}