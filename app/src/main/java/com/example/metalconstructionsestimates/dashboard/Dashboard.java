package com.example.metalconstructionsestimates.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.widget.TextView;
import java.util.Objects;


public class Dashboard extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#4F5EB1"));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new DashboardPagerAdapter(this));
        TextView allEstimatesTotalTextView = findViewById(R.id.value_total);
        TextView customersCountTextView = findViewById(R.id.value_customers);
        TextView steelsCountTextView = findViewById(R.id.value_steels);
        TextView estimatesCountTextView = findViewById(R.id.value_estimates);

        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        String allEstimatesTotal = "All Estimates Total:";
        allEstimatesTotal += String.valueOf(dbAdapter.getEstimatesTotal());
        allEstimatesTotalTextView.setText(allEstimatesTotal);

        // Set counts for each category
        setCounts(dbAdapter, customersCountTextView, estimatesCountTextView, steelsCountTextView);

        tabLayout = findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(getTabTitle(position))).attach();
    }

    private void setCounts(DBAdapter dbAdapter, TextView customersCountTextView, TextView estimatesCountTextView, TextView steelsCountTextView) {
        if (dbAdapter.getCustomersCount() == 0) {
            customersCountTextView.setText("0");
        } else {
            customersCountTextView.setText(String.valueOf(dbAdapter.getCustomersCount()));
        }

        if (dbAdapter.getEstimatesCount() == 0) {
            estimatesCountTextView.setText("0");
        } else {
            estimatesCountTextView.setText(String.valueOf(dbAdapter.getEstimatesCount()));
        }

        if (dbAdapter.getSteelsCount() == 0) {
            steelsCountTextView.setText("0");
        } else {
            steelsCountTextView.setText(String.valueOf(dbAdapter.getSteelsCount()));
        }
    }

    private String getTabTitle(int position) {
        // You can return tab titles based on position if needed
        switch (position) {
            case 0:
                return "Daily Est";
            case 1:
                return "Weekly Est";
            case 2:
                return "Monthly Est";
            case 3:
                return "Yearly Est";
            default:
                return null;
        }
    }
}