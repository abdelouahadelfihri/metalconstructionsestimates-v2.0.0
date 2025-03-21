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
    public FragmentCurrentWeekEstimates() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCurrentWeekEstimatesBinding = FragmentCurrentWeekEstimatesBinding.inflate(inflater, container, false);

        DBAdapter dbAdapter = new DBAdapter(getContext());

        if(dbAdapter.getCurrentWeekEstimatesCount() == 0)
            fragmentCurrentWeekEstimatesBinding.currentWeekEstimatesCount.getTextViewCurrentWeekEstimatesCount().setText(R.string.noWeeklyEstimates);
        else{
            if(dbAdapter.getCurrentWeekEstimatesCount() == 1){
                currentWeekEstimatesCount = "Weekly Number of Estimates : 1 estimate recorded";
            }
            else{
                currentWeekEstimatesCount = "Weekly Number of Estimates : " + dbAdapter.getCurrentWeekEstimatesCount() + " estimates recorded";
            }
            fragmentCurrentWeekEstimatesBinding.currentWeekEstimatesCount.getTextViewCurrentWeekEstimatesCount().setText(currentWeekEstimatesCount);
        }

        if(dbAdapter.getCurrentWeekEstimatesTotal() == 0.0f){
            fragmentCurrentWeekEstimatesBinding.currentWeekEstimatesTotal.getTextViewCurrentWeekEstimatesTotal().setText(R.string.zeroDH);
        }
        else{
            fragmentCurrentWeekEstimatesBinding.currentWeekEstimatesTotal.getTextViewCurrentWeekEstimatesTotal().setText(String.valueOf(numberFormat.format(dbAdapter.getCurrentWeekEstimatesTotal())));
        }

        ArrayList<Estimate> currentWeekEstimatesList = dbAdapter.getCurrentWeekEstimates();



        if (currentWeekEstimatesList.isEmpty()){
            fragmentCurrentWeekEstimatesBinding.recyclerViewCurrentWeekEstimates.setVisibility(View.GONE);
            fragmentCurrentWeekEstimatesBinding.noWeeklyEstimatesTextView.setVisibility(View.VISIBLE);
        } else {
            fragmentCurrentWeekEstimatesBinding.recyclerViewCurrentWeekEstimates.setVisibility(View.VISIBLE);
            fragmentCurrentWeekEstimatesBinding.noWeeklyEstimatesTextView.setVisibility(View.GONE);
            EstimatesListAdapter estimateAdapter = new EstimatesListAdapter(getContext(), currentWeekEstimatesList);
            fragmentCurrentWeekEstimatesBinding.recyclerViewCurrentWeekEstimates.setAdapter(estimateAdapter);
            fragmentCurrentWeekEstimatesBinding.recyclerViewCurrentWeekEstimates.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return fragmentCurrentWeekEstimatesBinding.getRoot();

    }
}