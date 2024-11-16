package com.example.metalconstructionsestimates.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.customviews.dashboard.DashboardDatabaseEntitiesTotals;
import com.example.metalconstructionsestimates.db.DBAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.widget.TextView;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    DashboardDatabaseEntitiesTotals dashboardDatabaseEntitiesTotals;
    String customersCount, steelsCount, estimatesCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar_dashboard);
        toolbar.setBackgroundColor(Color.parseColor("#4F5EB1"));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager2) findViewById(R.id.viewPager);
        viewPager.setAdapter(new DashboardPagerAdapter(this));
        dashboardDatabaseEntitiesTotals = findViewById(R.id.dashboard_database_entities_totals);
        TextView customersCountTextView = dashboardDatabaseEntitiesTotals.getTextViewCustomersCount();
        TextView estimatesCountTextView = dashboardDatabaseEntitiesTotals.getTextViewEstimatesCount();
        TextView steelsCountTextView = dashboardDatabaseEntitiesTotals.getTextViewSteelsCount();
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

        if(dbAdapter.getCustomersCount() == 0){
            String zeroCustomers = "0 Customers";
            customersCountTextView.setText(zeroCustomers);
        }
        else{
            customersCount = String.valueOf(dbAdapter.getCustomersCount());
            if(dbAdapter.getCustomersCount() == 1){
                customersCount += " Customer";
            }
            else{
                customersCount += " Customers";
            }

            customersCountTextView.setText(customersCount);

        }

        if(dbAdapter.getEstimatesCount() == 0){
            String zeroEstimates = "0 Estimates";
            estimatesCountTextView.setText(zeroEstimates);
        }
        else{
            estimatesCount = String.valueOf(dbAdapter.getEstimatesCount());
            if(dbAdapter.getEstimatesCount() == 1){
                estimatesCount += " Estimate";
            }
            else{
                estimatesCount += " Estimates";
            }

            estimatesCountTextView.setText(estimatesCount);

        }

        if(dbAdapter.getSteelsCount() == 0){
            String zeroSteels = "0 Steels";
            steelsCountTextView.setText(zeroSteels);
        }
        else{
            steelsCount = String.valueOf(dbAdapter.getSteelsCount());
            if(dbAdapter.getSteelsCount() == 1){
                steelsCount += " Steel";
            }
            else{
                steelsCount += " Steels";
            }

            steelsCountTextView.setText(steelsCount);

        }

        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getTabTitle(position))
        ).attach();
    }

    private String getTabTitle(int position) {
        // You can return tab titles based on position if needed
        switch (position) {
            case 0:
                return "Daily Estimates";
            case 1:
                return "Weekly Estimates";
            case 2:
                return "Monthly Estimates";
            case 3:
                return "Yearly Estimates";
            default:
                return null;
        }
    }
}