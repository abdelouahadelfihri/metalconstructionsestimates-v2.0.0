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
    NumberFormat numberFormat = NumberFormat.getNumberInstance(moroccoLocale);
    String currentYearEstimatesTotal;
    String currentYearEstimatesCount;
    public FragmentCurrentYearEstimates() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCurrentYearEstimatesBinding = FragmentCurrentYearEstimatesBinding.inflate(inflater, container, false);

        DBAdapter dbAdapter = new DBAdapter(getContext());

        if(dbAdapter.getCurrentYearEstimatesCount() == 0)
            fragmentCurrentYearEstimatesBinding.currentYearEstimatesCount.getTextViewCurrentYearEstimatesCount().setText(R.string.noYearlyEstimates);
        else{
            if(dbAdapter.getCurrentWeekEstimatesCount() == 1){
                currentYearEstimatesCount = "Yearly Number of Estimates : 1 estimate recorded";
            }
            else{
                currentYearEstimatesCount = "Yearly Number of Estimates : " + dbAdapter.getCurrentYearEstimatesCount() + " estimates recorded";
            }

            fragmentCurrentYearEstimatesBinding.currentYearEstimatesCount.getTextViewCurrentYearEstimatesCount().setText(currentYearEstimatesCount);
        }

        if(dbAdapter.getCurrentYearEstimatesTotal() == 0.0f){
            fragmentCurrentYearEstimatesBinding.currentYearEstimatesTotal.getTextViewCurrentYearEstimatesTotal().setText(R.string.zeroDH);
        }
        else{
            currentYearEstimatesTotal = "Yearly Total of Estimates :";
            currentYearEstimatesTotal += String.valueOf(dbAdapter.getCurrentWeekEstimatesTotal());
            currentYearEstimatesTotal += " DH";
            fragmentCurrentYearEstimatesBinding.currentYearEstimatesTotal.getTextViewCurrentYearEstimatesTotal().setText(currentYearEstimatesTotal);
        }

        ArrayList<Estimate> currentYearEstimatesList = dbAdapter.getCurrentYearEstimates();



        if (currentYearEstimatesList.isEmpty()){
            fragmentCurrentYearEstimatesBinding.recyclerViewCurrentYearEstimates.setVisibility(View.GONE);
            fragmentCurrentYearEstimatesBinding.noYearlyEstimatesTextView.setVisibility(View.VISIBLE);
        } else {
            fragmentCurrentYearEstimatesBinding.recyclerViewCurrentYearEstimates.setVisibility(View.VISIBLE);
            fragmentCurrentYearEstimatesBinding.noYearlyEstimatesTextView.setVisibility(View.GONE);
            EstimatesListAdapter estimateAdapter = new EstimatesListAdapter(getContext(), currentYearEstimatesList);
            fragmentCurrentYearEstimatesBinding.recyclerViewCurrentYearEstimates.setAdapter(estimateAdapter);
            fragmentCurrentYearEstimatesBinding.recyclerViewCurrentYearEstimates.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return fragmentCurrentYearEstimatesBinding.getRoot();
    }
}