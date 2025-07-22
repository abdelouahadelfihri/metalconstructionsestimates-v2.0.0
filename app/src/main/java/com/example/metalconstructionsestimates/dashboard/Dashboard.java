package com.example.metalconstructionsestimates.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Objects;


public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        View statusBarSpacer = findViewById(R.id.statusBarSpacer);
        ViewCompat.setOnApplyWindowInsetsListener(statusBarSpacer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = systemBars.top;
            v.setLayoutParams(params);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#4F5EB1"));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new DashboardPagerAdapter(this));
        TextView allEstimatesTotalTextView = findViewById(R.id.value_total);
        TextView customersCountTextView = findViewById(R.id.value_customers);
        TextView steelsCountTextView = findViewById(R.id.value_steels);
        TextView estimatesCountTextView = findViewById(R.id.value_estimates);

        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        if(dbAdapter.getEstimatesTotal() == 0f){
            allEstimatesTotalTextView.setText(R.string.zeroDH);
        }
        else{
            String allEstimatesTotal = dbAdapter.getEstimatesTotal() + " DH";
            allEstimatesTotalTextView.setText(allEstimatesTotal);
        }

        // Set counts for each category
        setCounts(dbAdapter, customersCountTextView, estimatesCountTextView, steelsCountTextView);

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(getTabTitle(position))).attach();
        viewPager.post(() -> viewPager.setCurrentItem(0, false));
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