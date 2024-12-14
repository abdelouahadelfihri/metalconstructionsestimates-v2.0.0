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
import android.util.TypedValue;
import android.view.ViewGroup;
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

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new DashboardPagerAdapter(this));
        dashboardDatabaseEntitiesTotals = findViewById(R.id.dashboard_database_entities_totals);
        TextView customersCountTextView = dashboardDatabaseEntitiesTotals.getTextViewCustomersCount();
        TextView estimatesCountTextView = dashboardDatabaseEntitiesTotals.getTextViewEstimatesCount();
        TextView steelsCountTextView = dashboardDatabaseEntitiesTotals.getTextViewSteelsCount();
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

        // Set counts for each category
        setCounts(dbAdapter, customersCountTextView, estimatesCountTextView, steelsCountTextView);

        tabLayout = findViewById(R.id.tabLayout);

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(getTabTitle(position))).attach();

        // Update the tab text size after the tabs have been initialized
        tabLayout.post(() -> {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    // Access the tab's view and modify the text size
                    TextView textView = (TextView) ((ViewGroup) tab.view).getChildAt(1);  // Access the TextView
                    if (textView != null) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);  // Set text size to 10sp
                    }
                }
            }
        });
    }

    private void setCounts(DBAdapter dbAdapter, TextView customersCountTextView, TextView estimatesCountTextView, TextView steelsCountTextView) {
        if (dbAdapter.getCustomersCount() == 0) {
            customersCountTextView.setText("0 Customers");
        } else {
            customersCount = String.valueOf(dbAdapter.getCustomersCount());
            customersCount += dbAdapter.getCustomersCount() == 1 ? " Customer" : " Customers";
            customersCountTextView.setText(customersCount);
        }

        if (dbAdapter.getEstimatesCount() == 0) {
            estimatesCountTextView.setText("0 Estimates");
        } else {
            estimatesCount = String.valueOf(dbAdapter.getEstimatesCount());
            estimatesCount += dbAdapter.getEstimatesCount() == 1 ? " Estimate" : " Estimates";
            estimatesCountTextView.setText(estimatesCount);
        }

        if (dbAdapter.getSteelsCount() == 0) {
            steelsCountTextView.setText("0 Steels");
        } else {
            steelsCount = String.valueOf(dbAdapter.getSteelsCount());
            steelsCount += dbAdapter.getSteelsCount() == 1 ? " Steel" : " Steels";
            steelsCountTextView.setText(steelsCount);
        }
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