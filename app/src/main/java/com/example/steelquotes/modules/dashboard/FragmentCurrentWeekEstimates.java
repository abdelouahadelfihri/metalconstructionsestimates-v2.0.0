package com.example.steelquotes.modules.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.steelquotes.R;
import com.example.steelquotes.recyclerviewadapters.EstimatesListAdapter;
import com.example.steelquotes.database.DBAdapter;
import com.example.steelquotes.databinding.FragmentCurrentWeekEstimatesBinding;
import com.example.steelquotes.models.Estimate;

import java.util.ArrayList;

public class FragmentCurrentWeekEstimates extends Fragment {

    private FragmentCurrentWeekEstimatesBinding binding;

    public FragmentCurrentWeekEstimates() {}

    DBAdapter dbAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCurrentWeekEstimatesBinding.inflate(inflater, container, false);

        if (getContext() == null) return binding.getRoot();

        dbAdapter = new DBAdapter(getContext());

        int count = dbAdapter.getCurrentWeekEstimatesCount();
        binding.tvEstimateCountValue.setText(String.valueOf(count));

        float total = dbAdapter.getCurrentWeekEstimatesTotal();
        binding.tvEstimateTotalValue.setText(
                total == 0.0f ? getString(R.string.zeroDH) : String.format("%.2f DH", total)
        );

        ArrayList<Estimate> list = dbAdapter.getCurrentWeekEstimates();

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