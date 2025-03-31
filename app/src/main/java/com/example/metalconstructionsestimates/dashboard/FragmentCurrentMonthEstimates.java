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
import com.example.metalconstructionsestimates.databinding.FragmentCurrentMonthEstimatesBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentCurrentMonthEstimates extends Fragment {

    FragmentCurrentMonthEstimatesBinding fragmentCurrentMonthEstimatesBinding;
    Locale moroccoLocale = new Locale("ar", "MA");
    NumberFormat numberFormat = NumberFormat.getNumberInstance(moroccoLocale);
    String currentMonthEstimatesCount;
    String currentMonthEstimatesTotal;
    public FragmentCurrentMonthEstimates() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCurrentMonthEstimatesBinding = FragmentCurrentMonthEstimatesBinding.inflate(inflater, container, false);

        DBAdapter dbAdapter = new DBAdapter(getContext());
        if(dbAdapter.getCurrentMonthEstimatesCount() == 0)
            fragmentCurrentMonthEstimatesBinding.currentMonthEstimatesCount.getTextViewCurrentMonthEstimatesCount().setText(R.string.noMonthlyEstimates);
        else{
            if(dbAdapter.getCurrentMonthEstimatesCount() == 1){
                currentMonthEstimatesCount = "Monthly Number of Estimates : 1";
            }
            else{
                currentMonthEstimatesCount = "Monthly Number of Estimates : " + dbAdapter.getCurrentMonthEstimatesCount();
            }
            fragmentCurrentMonthEstimatesBinding.currentMonthEstimatesCount.getTextViewCurrentMonthEstimatesCount().setText(currentMonthEstimatesCount);
        }


        if(dbAdapter.getCurrentMonthEstimatesTotal() == 0.0f){
            fragmentCurrentMonthEstimatesBinding.currentMonthEstimatesTotal.getTextViewCurrentMonthEstimatesTotal().setText("Monthly Total of Estimates : 0 DH");
        }
        else{
            currentMonthEstimatesTotal = "Monthly Total of Estimates :";
            currentMonthEstimatesTotal += String.valueOf(dbAdapter.getCurrentWeekEstimatesTotal());
            currentMonthEstimatesTotal += " DH";
            fragmentCurrentMonthEstimatesBinding.currentMonthEstimatesTotal.getTextViewCurrentMonthEstimatesTotal().setText(currentMonthEstimatesTotal);
        }

        ArrayList<Estimate> currentMonthEstimatesList = dbAdapter.getCurrentMonthEstimates();



        if (currentMonthEstimatesList.isEmpty()){
            fragmentCurrentMonthEstimatesBinding.recyclerViewCurrentMonthEstimates.setVisibility(View.GONE);
            fragmentCurrentMonthEstimatesBinding.noMonthlyEstimatesTextView.setVisibility(View.VISIBLE);
        } else {
            fragmentCurrentMonthEstimatesBinding.recyclerViewCurrentMonthEstimates.setVisibility(View.VISIBLE);
            fragmentCurrentMonthEstimatesBinding.noMonthlyEstimatesTextView.setVisibility(View.GONE);
            EstimatesListAdapter estimateAdapter = new EstimatesListAdapter(getContext(), currentMonthEstimatesList);
            fragmentCurrentMonthEstimatesBinding.recyclerViewCurrentMonthEstimates.setAdapter(estimateAdapter);
            fragmentCurrentMonthEstimatesBinding.recyclerViewCurrentMonthEstimates.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return fragmentCurrentMonthEstimatesBinding.getRoot();
    }
}