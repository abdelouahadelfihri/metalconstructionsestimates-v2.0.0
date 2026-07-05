package com.example.metalconstructionsestimates.modules.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.recyclerviewadapters.EstimatesListAdapter;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.databinding.FragmentCurrentMonthEstimatesBinding;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.util.CurrencyManager;

import java.util.ArrayList;

public class FragmentCurrentMonthEstimates extends Fragment {

    private FragmentCurrentMonthEstimatesBinding binding;

    public FragmentCurrentMonthEstimates() {}

    DBAdapter dbAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCurrentMonthEstimatesBinding.inflate(inflater, container, false);

        if (getContext() == null) return binding.getRoot();

        dbAdapter = new DBAdapter(getContext());

        int count = dbAdapter.getCurrentMonthEstimatesCount();
        binding.tvEstimateCountValue.setText(String.valueOf(count));

        float total = dbAdapter.getCurrentMonthEstimatesTotal();
        CurrencyManager cm = new CurrencyManager(getContext());
        binding.tvEstimateTotalValue.setText(cm.formatAmount(total));

        ArrayList<Estimate> list = dbAdapter.getCurrentMonthEstimates();

        if (list.isEmpty()) {
            binding.rvEstimates.setVisibility(View.GONE);
            binding.tvNoEstimates.setVisibility(View.VISIBLE);
        } else {
            binding.rvEstimates.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvEstimates.setAdapter(new EstimatesListAdapter(getContext(), list));
            binding.rvEstimates.setVisibility(View.VISIBLE);
            binding.tvNoEstimates.setVisibility(View.GONE);
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