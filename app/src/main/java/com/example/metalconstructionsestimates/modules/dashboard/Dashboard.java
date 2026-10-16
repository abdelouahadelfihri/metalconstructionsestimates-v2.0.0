package com.example.metalconstructionsestimates.modules.dashboard;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.modules.dashboard.charts.BarChartView;
import com.example.metalconstructionsestimates.modules.dashboard.charts.DonutChartView;
import com.example.metalconstructionsestimates.util.CurrencyManager;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class Dashboard extends AppCompatActivity {

    private static final String STATE_TAB = "dashboard_tab";
    private static final int MONTHS_IN_CHART = 12;
    private static final int TOP_CUSTOMERS = 5;

    public static final int TAB_DAY = 0;
    public static final int TAB_WEEK = 1;
    public static final int TAB_MONTH = 2;
    public static final int TAB_YEAR = 3;

    private TabLayout tabLayout;
    private CurrencyManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        cm = new CurrencyManager(this);
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());

        TextView allEstimatesTotalTextView = findViewById(R.id.value_total);
        TextView customersCountTextView = findViewById(R.id.value_customers);
        TextView steelsCountTextView = findViewById(R.id.value_steels);
        TextView estimatesCountTextView = findViewById(R.id.value_estimates);

        allEstimatesTotalTextView.setText(cm.formatAmount(dbAdapter.getEstimatesTotal()));
        customersCountTextView.setText(String.valueOf(dbAdapter.getCustomersCount()));
        steelsCountTextView.setText(String.valueOf(dbAdapter.getSteelsCount()));
        estimatesCountTextView.setText(String.valueOf(dbAdapter.getEstimatesCount()));

        ArrayList<Estimate> estimates = dbAdapter.retrieveEstimates();
        ArrayList<Customer> customers = dbAdapter.retrieveCustomers();
        dbAdapter.close();

        bindHeroStats(estimates);
        bindMonthlyChart(estimates);
        bindStatusChart(estimates);
        bindTopCustomers(estimates, customers);
        setupPeriodTabs(savedInstanceState);
    }

    // ── Hero: average value + approval rate ─────────────────────────────────

    private void bindHeroStats(List<Estimate> estimates) {
        float total = 0f;
        int approved = 0;
        int decided = 0;
        for (Estimate e : estimates) {
            total += amountOf(e);
            if ("Approved".equals(e.getStatus())) approved++;
            if ("Approved".equals(e.getStatus()) || "Cancelled".equals(e.getStatus())) decided++;
        }
        float average = estimates.isEmpty() ? 0f : total / estimates.size();
        ((TextView) findViewById(R.id.value_average)).setText(cm.formatAmount(average));

        // Approval rate over estimates that reached a decision (approved or cancelled)
        TextView rate = findViewById(R.id.value_approval_rate);
        rate.setText(decided == 0 ? "—" : Math.round(100f * approved / decided) + "%");
    }

    // ── Monthly value bar chart ─────────────────────────────────────────────

    private void bindMonthlyChart(List<Estimate> estimates) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        start.add(Calendar.MONTH, -(MONTHS_IN_CHART - 1));
        int startKey = monthKey(start);

        float[] values = new float[MONTHS_IN_CHART];
        String[] labels = new String[MONTHS_IN_CHART];
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        Calendar cursor = (Calendar) start.clone();
        for (int i = 0; i < MONTHS_IN_CHART; i++) {
            labels[i] = monthFormat.format(cursor.getTime());
            cursor.add(Calendar.MONTH, 1);
        }

        Calendar issue = Calendar.getInstance();
        for (Estimate e : estimates) {
            if (e.getIssueDate() == null) continue;
            issue.setTimeInMillis(e.getIssueDate());
            int index = monthKey(issue) - startKey;
            if (index >= 0 && index < MONTHS_IN_CHART) values[index] += amountOf(e);
        }

        BarChartView chart = findViewById(R.id.chart_monthly);
        chart.setValueFormatter(cm::formatAmount);
        chart.setData(values, labels);

        // Month-over-month change
        TextView delta = findViewById(R.id.value_month_delta);
        float current = values[MONTHS_IN_CHART - 1];
        float previous = values[MONTHS_IN_CHART - 2];
        if (previous > 0) {
            float change = (current - previous) / previous * 100f;
            boolean up = change >= 0;
            String text = (up ? "▲ " : "▼ ") + Math.round(Math.abs(change)) + "%";
            delta.setText(getString(R.string.dashboardVsLastMonth, text));
            delta.setTextColor(ContextCompat.getColor(this,
                    up ? R.color.dashboard_positive : R.color.dashboard_negative));
        } else if (current > 0) {
            delta.setText(R.string.dashboardNewThisMonth);
            delta.setTextColor(ContextCompat.getColor(this, R.color.dashboard_positive));
        } else {
            delta.setVisibility(View.GONE);
        }
    }

    private static int monthKey(Calendar c) {
        return c.get(Calendar.YEAR) * 12 + c.get(Calendar.MONTH);
    }

    // ── Status donut ────────────────────────────────────────────────────────

    private void bindStatusChart(List<Estimate> estimates) {
        long startOfToday = startOfToday();
        // Order: approved, pending, overdue, cancelled
        int[] counts = new int[4];
        float[] amounts = new float[4];
        for (Estimate e : estimates) {
            int bucket;
            String status = e.getStatus();
            if ("Approved".equals(status)) {
                bucket = 0;
            } else if ("Pending".equals(status)) {
                // Same rule as DBAdapter.retrieveOverdueEstimates()
                boolean overdue = e.getDueDate() != null && e.getDueDate() < startOfToday;
                bucket = overdue ? 2 : 1;
            } else if ("Cancelled".equals(status)) {
                bucket = 3;
            } else {
                continue;
            }
            counts[bucket]++;
            amounts[bucket] += amountOf(e);
        }

        int[] colors = {
                ContextCompat.getColor(this, R.color.status_approved),
                ContextCompat.getColor(this, R.color.status_pending),
                ContextCompat.getColor(this, R.color.status_overdue),
                ContextCompat.getColor(this, R.color.status_cancelled)
        };
        int[] labels = {R.string.statusApproved, R.string.statusPending,
                R.string.statusOverdue, R.string.statusCancelled};

        float[] values = new float[4];
        for (int i = 0; i < 4; i++) values[i] = counts[i];

        DonutChartView donut = findViewById(R.id.chart_status);
        donut.setData(values, colors, String.valueOf(estimates.size()),
                getString(R.string.dashboardEstimatesShort));

        LinearLayout legend = findViewById(R.id.legend_status);
        legend.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < 4; i++) {
            View row = inflater.inflate(R.layout.item_dashboard_legend, legend, false);
            row.findViewById(R.id.legend_dot).setBackgroundTintList(ColorStateList.valueOf(colors[i]));
            ((TextView) row.findViewById(R.id.legend_label)).setText(labels[i]);
            ((TextView) row.findViewById(R.id.legend_amount)).setText(cm.formatAmount(amounts[i]));
            ((TextView) row.findViewById(R.id.legend_count)).setText(String.valueOf(counts[i]));
            legend.addView(row);
        }
    }

    private static long startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // ── Top customers ───────────────────────────────────────────────────────

    private void bindTopCustomers(List<Estimate> estimates, List<Customer> customers) {
        Map<Integer, String> names = new HashMap<>();
        for (Customer c : customers) names.put(c.getId(), c.getName());

        Map<Integer, float[]> totals = new HashMap<>(); // id -> {amount, count}
        for (Estimate e : estimates) {
            float[] t = totals.get(e.getCustomer());
            if (t == null) {
                t = new float[2];
                totals.put(e.getCustomer(), t);
            }
            t[0] += amountOf(e);
            t[1]++;
        }

        List<Map.Entry<Integer, float[]>> ranked = new ArrayList<>(totals.entrySet());
        Collections.sort(ranked, (a, b) -> Float.compare(b.getValue()[0], a.getValue()[0]));

        LinearLayout list = findViewById(R.id.list_top_customers);
        list.removeAllViews();
        float max = ranked.isEmpty() ? 0f : ranked.get(0).getValue()[0];
        if (max <= 0f) {
            findViewById(R.id.empty_top_customers).setVisibility(View.VISIBLE);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < Math.min(TOP_CUSTOMERS, ranked.size()); i++) {
            Map.Entry<Integer, float[]> entry = ranked.get(i);
            String name = names.get(entry.getKey());
            int count = (int) entry.getValue()[1];

            View row = inflater.inflate(R.layout.item_dashboard_top_customer, list, false);
            ((TextView) row.findViewById(R.id.customer_name)).setText(
                    name != null ? name : getString(R.string.dashboardUnknownCustomer));
            ((TextView) row.findViewById(R.id.customer_amount)).setText(cm.formatAmount(entry.getValue()[0]));
            ((TextView) row.findViewById(R.id.customer_count)).setText(
                    count + " " + getString(R.string.dashboardEstimatesShort));
            LinearProgressIndicator bar = row.findViewById(R.id.customer_bar);
            bar.setProgress(Math.round(1000f * entry.getValue()[0] / max));
            list.addView(row);
        }
    }

    // ── Period tabs (today / week / month / year) ───────────────────────────

    private void setupPeriodTabs(Bundle savedInstanceState) {
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabDay));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabWeek));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabMonth));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabYear));

        int selected = savedInstanceState != null ? savedInstanceState.getInt(STATE_TAB, TAB_DAY) : TAB_DAY;
        TabLayout.Tab tab = tabLayout.getTabAt(selected);
        if (tab != null) tab.select();

        // On rotation the FragmentManager restores the shown fragment itself
        if (savedInstanceState == null) showPeriod(selected);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showPeriod(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showPeriod(int position) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.periodContainer, createPeriodFragment(position))
                .commit();
    }

    private static Fragment createPeriodFragment(int position) {
        switch (position) {
            case TAB_WEEK:
                return new FragmentCurrentWeekEstimates();
            case TAB_MONTH:
                return new FragmentCurrentMonthEstimates();
            case TAB_YEAR:
                return new FragmentCurrentYearEstimates();
            case TAB_DAY:
            default:
                return new FragmentCurrentDayEstimates();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (tabLayout != null) outState.putInt(STATE_TAB, tabLayout.getSelectedTabPosition());
    }

    private static float amountOf(Estimate e) {
        return e.getAllTaxIncludedTotal() != null ? e.getAllTaxIncludedTotal() : 0f;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
