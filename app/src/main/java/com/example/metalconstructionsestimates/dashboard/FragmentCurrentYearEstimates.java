package com.example.metalconstructionsestimates.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.arraysadapters.EstimatesListAdapter;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.databinding.FragmentCurrentYearEstimatesBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentCurrentYearEstimates extends Fragment {

    FragmentCurrentYearEstimatesBinding fragmentCurrentYearEstimatesBinding;
    Locale moroccoLocale = new Locale("ar", "MA");
    String currentYearEstimatesTotal;
    String currentYearEstimatesCount;
    public FragmentCurrentYearEstimates() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCurrentYearEstimatesBinding = FragmentCurrentYearEstimatesBinding.inflate(inflater, container, false);

        DBAdapter dbAdapter = new DBAdapter(getContext());

        if(dbAdapter.getCurrentYearEstimatesCount() == 0)
            fragmentCurrentYearEstimatesBinding.tvEstimateCountValue.setText(R.string.noYearlyEstimates);
        else{
            if(dbAdapter.getCurrentWeekEstimatesCount() == 1){
                currentYearEstimatesCount = "Yearly Number of Estimates : 1";
            }
            else{
                currentYearEstimatesCount = "Yearly Number of Estimates : " + dbAdapter.getCurrentYearEstimatesCount();
            }

            fragmentCurrentYearEstimatesBinding.tvEstimateCountValue.setText(currentYearEstimatesCount);
        }

        if(dbAdapter.getCurrentYearEstimatesTotal() == 0.0f){
            fragmentCurrentYearEstimatesBinding.tvEstimateTotalValue.setText("Yearly Total of Estimates : 0 DH");
        }
        else{
            currentYearEstimatesTotal = "Yearly Total of Estimates :";
            currentYearEstimatesTotal += String.valueOf(dbAdapter.getCurrentYearEstimatesTotal());
            currentYearEstimatesTotal += " DH";
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