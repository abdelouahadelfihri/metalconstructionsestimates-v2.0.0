package com.example.metalconstructionsestimates.modules.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.recyclerviewadapters.EstimatesListAdapter;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.databinding.FragmentCurrentDayEstimatesBinding;
import com.example.metalconstructionsestimates.util.CurrencyManager;

import java.util.ArrayList;
import java.util.Locale;

public class FragmentCurrentDayEstimates extends Fragment {
    private FragmentCurrentDayEstimatesBinding binding;

    public FragmentCurrentDayEstimates() {}

    DBAdapter dbAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCurrentDayEstimatesBinding.inflate(inflater, container, false);

        dbAdapter = new DBAdapter(requireContext());

        // Set Count
        int count = dbAdapter.getCurrentDayEstimatesCount();
        binding.tvEstimateCountValue.setText(String.valueOf(count));

        // Set Total
        float total = dbAdapter.getCurrentDayEstimatesTotal();
        CurrencyManager cm = new CurrencyManager(getContext());
        binding.tvEstimateTotalValue.setText(cm.formatAmount(total));

        // Set List
        ArrayList<Estimate> estimates = dbAdapter.getCurrentDayEstimates();
        if (estimates.isEmpty()) {
            binding.rvEstimates.setVisibility(View.GONE);
            binding.tvNoEstimates.setVisibility(View.VISIBLE);
        } else {
            binding.rvEstimates.setVisibility(View.VISIBLE);
            binding.tvNoEstimates.setVisibility(View.GONE);
            EstimatesListAdapter adapter = new EstimatesListAdapter(getContext(), estimates);
            binding.rvEstimates.setAdapter(adapter);
            binding.rvEstimates.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (dbAdapter != null) {
            dbAdapter.close();
        }
    }
}
