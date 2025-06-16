package com.example.metalconstructionsestimates.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.EstimatesListAdapter;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.databinding.FragmentCurrentYearEstimatesBinding;
import java.util.ArrayList;

public class FragmentCurrentYearEstimates extends Fragment {

    FragmentCurrentYearEstimatesBinding fragmentCurrentYearEstimatesBinding;

    public FragmentCurrentYearEstimates() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCurrentYearEstimatesBinding = FragmentCurrentYearEstimatesBinding.inflate(inflater, container, false);

        DBAdapter dbAdapter = new DBAdapter(getContext());

        if(dbAdapter.getCurrentYearEstimatesCount() == 0)
            fragmentCurrentYearEstimatesBinding.tvEstimateCountValue.setText("0");
        else{
            fragmentCurrentYearEstimatesBinding.tvEstimateCountValue.setText(String.valueOf(dbAdapter.getCurrentYearEstimatesCount()));
        }

        if(dbAdapter.getCurrentYearEstimatesTotal() == 0.0f){
            fragmentCurrentYearEstimatesBinding.tvEstimateTotalValue.setText(R.string.zeroDH);
        }
        else{
            String currentYearEstimatesTotal = dbAdapter.getCurrentYearEstimatesTotal().toString() + " DH";
            fragmentCurrentYearEstimatesBinding.tvEstimateTotalValue.setText(currentYearEstimatesTotal);
        }

        ArrayList<Estimate> currentYearEstimatesList = dbAdapter.getCurrentYearEstimates();

        if (currentYearEstimatesList.isEmpty()){
            fragmentCurrentYearEstimatesBinding.rvEstimates.setVisibility(View.GONE);
            fragmentCurrentYearEstimatesBinding.tvNoEstimates.setVisibility(View.VISIBLE);
        } else {
            fragmentCurrentYearEstimatesBinding.rvEstimates.setVisibility(View.VISIBLE);
            fragmentCurrentYearEstimatesBinding.tvNoEstimates.setVisibility(View.GONE);
            EstimatesListAdapter estimateAdapter = new EstimatesListAdapter(getContext(), currentYearEstimatesList);
            fragmentCurrentYearEstimatesBinding.rvEstimates.setAdapter(estimateAdapter);
            fragmentCurrentYearEstimatesBinding.rvEstimates.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return fragmentCurrentYearEstimatesBinding.getRoot();
    }
}