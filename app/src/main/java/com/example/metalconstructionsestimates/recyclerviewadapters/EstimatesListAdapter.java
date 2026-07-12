package com.example.metalconstructionsestimates.recyclerviewadapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.SettingsActivity;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.modules.estimates.EstimateDetails;
import com.example.metalconstructionsestimates.util.CurrencyManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EstimatesListAdapter extends RecyclerView.Adapter<EstimatesListAdapter.EstimateViewHolder> {

    private ArrayList<Estimate> estimates;
    private LayoutInflater      mInflater;
    private Context             context;

    // ── Settings values ────────────────────────────────────────────────────
    private String          dateFormat;
    private CurrencyManager currencyManager;

    public EstimatesListAdapter(Context context, ArrayList<Estimate> estimates) {
        this.context   = context;
        mInflater      = LayoutInflater.from(context);
        this.estimates = estimates;

        // Load settings once for the whole list
        SharedPreferences prefs = context.getSharedPreferences(
                SettingsActivity.PREFS_SETTINGS, Context.MODE_PRIVATE);
        dateFormat      = prefs.getString(SettingsActivity.KEY_DATE_FORMAT, "dd/MM/yyyy");
        currencyManager = new CurrencyManager(context);
    }

    public void updateEstimates(ArrayList<Estimate> estimates) {
        this.estimates = estimates;
        notifyDataSetChanged();
    }

    @Override
    public EstimateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.estimate_item, parent, false);
        return new EstimateViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(EstimateViewHolder holder, int position) {
        Estimate estimate = estimates.get(position);

        // ── Total with currency symbol ──────────────────────────────────────
        Float total = estimate.getAllTaxIncludedTotal();
        String formattedTotal = formatLargeNumber(total);
        String currencyCode = currencyManager.getActiveCurrencyCode();
        holder.estimateTotalAllTaxIncludedTextView.setText("Incl. VAT : " + formattedTotal + " " + currencyCode);

        // ── Estimate ID ─────────────────────────────────────────────────────
        holder.estimateIdTextView.setText("Estimate Id : " + estimate.getId().toString());

        // ── Issue date using settings date format ──────────────────────────
        Long issueDateMillis = estimate.getIssueDate();
        String formattedIssueDate = (issueDateMillis != null && issueDateMillis != 0)
                ? new SimpleDateFormat(dateFormat, Locale.getDefault()).format(new Date(issueDateMillis))
                : "";
        holder.estimateCreationDateTextView.setText("Issue Date : " + formattedIssueDate);

        // ── Location ────────────────────────────────────────────────────────
        holder.estimateDoneInTextView.setText("Location : " + estimate.getDoneIn());
    }

    class EstimateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView estimateIdTextView;
        public final TextView estimateCreationDateTextView;
        public final TextView estimateDoneInTextView;
        public final TextView estimateTotalAllTaxIncludedTextView;
        final EstimatesListAdapter estimateListAdapter;

        public EstimateViewHolder(View itemView, EstimatesListAdapter estimateListAdapter) {
            super(itemView);
            estimateIdTextView                  = itemView.findViewById(R.id.recycler_view_estimate_id);
            estimateCreationDateTextView        = itemView.findViewById(R.id.recycler_view_estimate_date);
            estimateDoneInTextView              = itemView.findViewById(R.id.recycler_view_done_in);
            estimateTotalAllTaxIncludedTextView = itemView.findViewById(R.id.recycler_view_total_all_tax_included);
            this.estimateListAdapter            = estimateListAdapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            Estimate estimate = estimates.get(mPosition);
            Intent intent = new Intent(view.getContext(), EstimateDetails.class);
            intent.putExtra("estimateIdExtra", estimate.getId().toString());
            view.getContext().startActivity(intent);
            estimateListAdapter.notifyDataSetChanged();
        }
    }

    // ── Formats large numbers as 1.5K / 2.3M with currency code appended ──
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
        return estimates.size();
    }
}