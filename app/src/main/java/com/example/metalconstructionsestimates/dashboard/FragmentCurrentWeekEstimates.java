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
import com.example.metalconstructionsestimates.databinding.FragmentCurrentWeekEstimatesBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentCurrentWeekEstimates extends Fragment {

    FragmentCurrentWeekEstimatesBinding fragmentCurrentWeekEstimatesBinding;
    Locale moroccoLocale = new Locale("ar", "MA");
    NumberFormat numberFormat = NumberFormat.getNumberInstance(moroccoLocale);
    String currentWeekEstimatesCount;
    String currentWeekEstimatesTotal;
    public FragmentCurrentWeekEstimates() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCurrentWeekEstimatesBinding = FragmentCurrentWeekEstimatesBinding.inflate(inflater, container, false);

        DBAdapter dbAdapter = new DBAdapter(getContext());

        if(dbAdapter.getCurrentWeekEstimatesCount() == 0)
            fragmentCurrentWeekEstimatesBinding.tvEstimateCountValue.setText(R.string.noWeeklyEstimates);
        else{
            if(dbAdapter.getCurrentWeekEstimatesCount() == 1){
                currentWeekEstimatesCount = "Weekly Number of Estimates : 1";
            }
            else{
                currentWeekEstimatesCount = "Weekly Number of Estimates : " + dbAdapter.getCurrentWeekEstimatesCount();
            }

            fragmentCurrentWeekEstimatesBinding.tvEstimateCountValue.setText(currentWeekEstimatesCount);
        }

        if(dbAdapter.getCurrentWeekEstimatesTotal() == 0.0f){
            fragmentCurrentWeekEstimatesBinding.tvEstimateTotalValue.setText("Weekly Total of Estimates : 0 DH");
        }
        else{
            currentWeekEstimatesTotal = "Weekly Total of Estimates :";
            currentWeekEstimatesTotal += String.valueOf(dbAdapter.getCurrentWeekEstimatesTotal());
            currentWeekEstimatesTotal += " DH";
            fragmentCurrentWeekEstimatesBinding.tvEstimateTotalValue.setText(currentWeekEstimatesTotal);
        }

        ArrayList<Estimate> currentWeekEstimatesList = dbAdapter.getCurrentWeekEstimates();



        if (currentWeekEstimatesList.isEmpty()){
            fragmentCurrentWeekEstimatesBinding.rvEstimates.setVisibility(View.GONE);
            fragmentCurrentWeekEstimatesBinding.tvNoEstimates.setVisibility(View.VISIBLE);
        } else {
            fragmentCurrentWeekEstimatesBinding.rvEstimates.setVisibility(View.VISIBLE);
            fragmentCurrentWeekEstimatesBinding.tvNoEstimates.setVisibility(View.GONE);
            EstimatesListAdapter estimateAdapter = new EstimatesListAdapter(getContext(), currentWeekEstimatesList);
            fragmentCurrentWeekEstimatesBinding.rvEstimates.setAdapter(estimateAdapter);
            fragmentCurrentWeekEstimatesBinding.rvEstimates.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return fragmentCurrentWeekEstimatesBinding.getRoot();

    }
}