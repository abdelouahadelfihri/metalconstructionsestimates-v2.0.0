package com.example.metalconstructionsestimates.modules.dashboard;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.modules.dashboard.charts.BarChartView;
import com.example.metalconstructionsestimates.modules.dashboard.charts.DonutChartView;
import com.example.metalconstructionsestimates.recyclerviewadapters.EstimatesListAdapter;
import com.example.metalconstructionsestimates.util.CurrencyManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;


public class Dashboard extends AppCompatActivity {

    private static final String STATE_TAB = "dashboard_tab";
    private static final String STATE_CUSTOM_START = "dashboard_custom_start";
    private static final String STATE_CUSTOM_END = "dashboard_custom_end";
    private static final String TAG_DAY_PICKER = "dashboard_day_picker";
    private static final String TAG_RANGE_PICKER = "dashboard_range_picker";

    private static final int TOP_CUSTOMERS = 5;
    private static final int ALL_TIME_YEARS = 6;
    private static final long DAY_MS = 24L * 60 * 60 * 1000;

    public static final int TAB_DAY = 0;
    public static final int TAB_WEEK = 1;
    public static final int TAB_MONTH = 2;
    public static final int TAB_YEAR = 3;
    public static final int TAB_ALL = 4;
    /** Selection made from the calendar instead of a tab. */
    private static final int CUSTOM = -1;

    private TabLayout tabLayout;
    private NestedScrollView scrollView;
    private CurrencyManager cm;
    private EstimatesListAdapter listAdapter;

    private ArrayList<Estimate> allEstimates = new ArrayList<>();
    private final Map<Integer, String> customerNames = new HashMap<>();

    // Current selection: a preset tab, or a custom [start, end) range of whole local days
    private int selectedTab = TAB_ALL;
    private long customStart;
    private long customEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        cm = new CurrencyManager(this);
        scrollView = findViewById(R.id.scrollContent);
        ((BarChartView) findViewById(R.id.chart_trend)).setValueFormatter(cm::formatAmount);

        RecyclerView list = findViewById(R.id.rv_estimates);
        list.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new EstimatesListAdapter(this, new ArrayList<>());
        list.setAdapter(listAdapter);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabDay), false);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabWeek), false);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabMonth), false);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabYear), false);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tabAll), false);

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(STATE_TAB, TAB_ALL);
            customStart = savedInstanceState.getLong(STATE_CUSTOM_START);
            customEnd = savedInstanceState.getLong(STATE_CUSTOM_END);
        }
        syncTabs();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (selectedTab == tab.getPosition()) return;
                selectedTab = tab.getPosition();
                renderFromTop();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        MaterialButton rangeButton = findViewById(R.id.value_period_range);
        rangeButton.setOnClickListener(this::showCalendarMenu);

        // Pickers survive rotation as fragments, but their listeners do not
        reattachPickerListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload so edits made from the estimates list show up when coming back
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        ((TextView) findViewById(R.id.value_customers)).setText(String.valueOf(dbAdapter.getCustomersCount()));
        ((TextView) findViewById(R.id.value_steels)).setText(String.valueOf(dbAdapter.getSteelsCount()));
        allEstimates = dbAdapter.retrieveEstimates();
        ArrayList<Customer> customers = dbAdapter.retrieveCustomers();
        dbAdapter.close();

        customerNames.clear();
        for (Customer c : customers) customerNames.put(c.getId(), c.getName());

        render();
    }

    // ── Calendar: pick a day or a custom period ─────────────────────────────

    private void showCalendarMenu(View anchor) {
        PopupMenu menu = new PopupMenu(this, anchor);
        menu.getMenu().add(0, 1, 0, R.string.dashboardSelectDay);
        menu.getMenu().add(0, 2, 1, R.string.dashboardSelectPeriod);
        menu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) showDayPicker();
            else showRangePicker();
            return true;
        });
        menu.show();
    }

    private CalendarConstraints pastOnly() {
        return new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build();
    }

    private void showDayPicker() {
        long initial = selectedTab == CUSTOM ? customStart : startOfToday();
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.dashboardSelectDay)
                .setTheme(R.style.DashboardDatePicker)
                .setCalendarConstraints(pastOnly())
                .setSelection(localDayToUtc(initial))
                .build();
        picker.addOnPositiveButtonClickListener(this::onDayPicked);
        picker.show(getSupportFragmentManager(), TAG_DAY_PICKER);
    }

    private void showRangePicker() {
        long initialStart = selectedTab == CUSTOM ? customStart : startOfToday() - 6 * DAY_MS;
        long initialEnd = selectedTab == CUSTOM ? customEnd - DAY_MS : startOfToday();
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.dashboardSelectPeriod)
                .setTheme(R.style.DashboardRangePicker)
                .setCalendarConstraints(pastOnly())
                .setSelection(new Pair<>(localDayToUtc(initialStart), localDayToUtc(initialEnd)))
                .build();
        picker.addOnPositiveButtonClickListener(this::onRangePicked);
        picker.show(getSupportFragmentManager(), TAG_RANGE_PICKER);
    }

    @SuppressWarnings("unchecked")
    private void reattachPickerListeners() {
        Object day = getSupportFragmentManager().findFragmentByTag(TAG_DAY_PICKER);
        if (day instanceof MaterialDatePicker) {
            ((MaterialDatePicker<Long>) day).addOnPositiveButtonClickListener(this::onDayPicked);
        }
        Object range = getSupportFragmentManager().findFragmentByTag(TAG_RANGE_PICKER);
        if (range instanceof MaterialDatePicker) {
            ((MaterialDatePicker<Pair<Long, Long>>) range).addOnPositiveButtonClickListener(this::onRangePicked);
        }
    }

    private void onDayPicked(Long utcDay) {
        if (utcDay == null) return;
        long day = utcToLocalDay(utcDay);
        applyCustom(day, addDays(day, 1));
    }

    private void onRangePicked(Pair<Long, Long> range) {
        if (range == null || range.first == null || range.second == null) return;
        long start = utcToLocalDay(range.first);
        long lastDay = utcToLocalDay(range.second);
        applyCustom(start, addDays(lastDay, 1));
    }

    private void applyCustom(long start, long endExclusive) {
        selectedTab = CUSTOM;
        customStart = start;
        customEnd = endExclusive;
        syncTabs();
        renderFromTop();
    }

    /** Highlights the selected tab, or no tab for a calendar selection. */
    private void syncTabs() {
        if (selectedTab == CUSTOM) {
            tabLayout.selectTab(null);
            tabLayout.setSelectedTabIndicatorColor(android.graphics.Color.TRANSPARENT);
            // selectTab(null) leaves the old tab's text highlighted; clear it ourselves
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) tab.view.setSelected(false);
            }
        } else {
            tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.dashboard_brand));
            TabLayout.Tab tab = tabLayout.getTabAt(selectedTab);
            if (tab != null && !tab.isSelected()) tab.select();
        }
    }

    /** The date picker works in UTC midnights; convert to/from local midnights. */
    private static long utcToLocalDay(long utcMillis) {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.setTimeInMillis(utcMillis);
        Calendar local = Calendar.getInstance();
        local.clear();
        local.set(utc.get(Calendar.YEAR), utc.get(Calendar.MONTH), utc.get(Calendar.DAY_OF_MONTH));
        return local.getTimeInMillis();
    }

    private static long localDayToUtc(long localMillis) {
        Calendar local = Calendar.getInstance();
        local.setTimeInMillis(localMillis);
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.clear();
        utc.set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH));
        return utc.getTimeInMillis();
    }

    private static long addDays(long localDay, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(localDay);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTimeInMillis();
    }

    // ── Rendering for the selected period ───────────────────────────────────

    private void renderFromTop() {
        render();
        // Show the updated totals and charts from the top
        scrollView.post(() -> scrollView.scrollTo(0, 0));
    }

    private void render() {
        Period period = selectedTab == CUSTOM
                ? Period.custom(this, customStart, customEnd)
                : Period.preset(this, selectedTab, allEstimates);

        ArrayList<Estimate> inPeriod = new ArrayList<>();
        float previousTotal = 0f;
        for (Estimate e : allEstimates) {
            long issue = e.getIssueDate() != null ? e.getIssueDate() : 0L;
            if (period.contains(issue)) inPeriod.add(e);
            else if (period.containsPrevious(issue)) previousTotal += amountOf(e);
        }

        ((MaterialButton) findViewById(R.id.value_period_range)).setText(period.rangeLabel);
        ((TextView) findViewById(R.id.value_period_caption)).setText(period.allTime
                ? getString(R.string.dashboardTotalCaption)
                : getString(R.string.dashboardPeriodCaption, period.rangeLabel));
        ((TextView) findViewById(R.id.value_estimates)).setText(String.valueOf(inPeriod.size()));

        float total = bindHeroStats(inPeriod);
        bindTrendChart(period, inPeriod, total, previousTotal);
        bindStatusChart(inPeriod);
        bindTopCustomers(inPeriod);
        bindList(inPeriod);
    }

    // ── Hero: total, average value, approval rate ───────────────────────────

    private float bindHeroStats(List<Estimate> estimates) {
        float total = 0f;
        int approved = 0;
        int decided = 0;
        for (Estimate e : estimates) {
            total += amountOf(e);
            if ("Approved".equals(e.getStatus())) approved++;
            if ("Approved".equals(e.getStatus()) || "Cancelled".equals(e.getStatus())) decided++;
        }
        ((TextView) findViewById(R.id.value_total)).setText(cm.formatAmount(total));

        float average = estimates.isEmpty() ? 0f : total / estimates.size();
        ((TextView) findViewById(R.id.value_average)).setText(cm.formatAmount(average));

        // Approval rate over estimates that reached a decision (approved or cancelled)
        TextView rate = findViewById(R.id.value_approval_rate);
        rate.setText(decided == 0 ? "—" : Math.round(100f * approved / decided) + "%");
        return total;
    }

    // ── Trend bar chart + change vs previous period ─────────────────────────

    private void bindTrendChart(Period period, List<Estimate> estimates, float total, float previousTotal) {
        float[] values = new float[period.labels.length];
        int noTimeCount = 0;
        float noTimeAmount = 0f;
        for (Estimate e : estimates) {
            int index;
            if (period.bucket == Period.Bucket.HOUR) {
                // The issue date only holds the day: place the estimate by its creation time,
                // but only when it was created on that same day (not back- or post-dated)
                Long created = e.getCreatedAt();
                index = created != null && created > 0 ? period.bucketOf(created) : -1;
                if (index < 0) {
                    noTimeCount++;
                    noTimeAmount += amountOf(e);
                    continue;
                }
            } else {
                index = period.bucketOf(e.getIssueDate() != null ? e.getIssueDate() : 0L);
            }
            if (index >= 0 && index < values.length) values[index] += amountOf(e);
        }

        TextView note = findViewById(R.id.note_trend);
        note.setVisibility(noTimeCount > 0 ? View.VISIBLE : View.GONE);
        if (noTimeCount > 0) {
            note.setText(getResources().getQuantityString(R.plurals.dashboardNoRecordedTime,
                    noTimeCount, noTimeCount, cm.formatAmount(noTimeAmount)));
        }

        ((TextView) findViewById(R.id.title_trend)).setText(period.trendTitleRes);
        BarChartView chart = findViewById(R.id.chart_trend);
        // Highlight "now" when it is in range, otherwise the last bar
        // Highlight "now" when it is in range, otherwise the biggest bar (none if all empty)
        int highlight = period.bucketOf(System.currentTimeMillis());
        if (highlight < 0) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] > 0 && (highlight < 0 || values[i] > values[highlight])) highlight = i;
            }
        }
        chart.setData(values, period.labels, highlight);

        TextView delta = findViewById(R.id.value_delta);
        delta.setVisibility(View.VISIBLE);
        if (period.deltaRes == 0) {
            delta.setVisibility(View.GONE);
        } else if (previousTotal > 0) {
            float change = (total - previousTotal) / previousTotal * 100f;
            boolean up = change >= 0;
            String text = (up ? "▲ " : "▼ ") + Math.round(Math.abs(change)) + "%";
            delta.setText(getString(period.deltaRes, text));
            delta.setTextColor(ContextCompat.getColor(this,
                    up ? R.color.dashboard_positive : R.color.dashboard_negative));
        } else if (total > 0) {
            delta.setText(R.string.dashboardNewThisPeriod);
            delta.setTextColor(ContextCompat.getColor(this, R.color.dashboard_positive));
        } else {
            delta.setVisibility(View.GONE);
        }
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
                getResources().getQuantityString(R.plurals.dashboardEstimatesShort, estimates.size()));

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
        truncateToDay(cal);
        return cal.getTimeInMillis();
    }

    // ── Top customers ───────────────────────────────────────────────────────

    private void bindTopCustomers(List<Estimate> estimates) {
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
        findViewById(R.id.empty_top_customers).setVisibility(max <= 0f ? View.VISIBLE : View.GONE);
        if (max <= 0f) return;

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < Math.min(TOP_CUSTOMERS, ranked.size()); i++) {
            Map.Entry<Integer, float[]> entry = ranked.get(i);
            String name = customerNames.get(entry.getKey());
            int count = (int) entry.getValue()[1];

            View row = inflater.inflate(R.layout.item_dashboard_top_customer, list, false);
            ((TextView) row.findViewById(R.id.customer_name)).setText(
                    name != null ? name : getString(R.string.dashboardUnknownCustomer));
            ((TextView) row.findViewById(R.id.customer_amount)).setText(cm.formatAmount(entry.getValue()[0]));
            ((TextView) row.findViewById(R.id.customer_count)).setText(
                    getResources().getQuantityString(R.plurals.dashboardEstimatesCount, count, count));
            LinearProgressIndicator bar = row.findViewById(R.id.customer_bar);
            bar.setProgress(Math.round(1000f * entry.getValue()[0] / max));
            list.addView(row);
        }
    }

    // ── Estimates list ──────────────────────────────────────────────────────

    private void bindList(ArrayList<Estimate> estimates) {
        // Newest first
        Collections.sort(estimates, (a, b) -> Long.compare(
                b.getIssueDate() != null ? b.getIssueDate() : 0L,
                a.getIssueDate() != null ? a.getIssueDate() : 0L));
        listAdapter.updateEstimates(estimates);
        findViewById(R.id.rv_estimates).setVisibility(estimates.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.tv_no_estimates).setVisibility(estimates.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_TAB, selectedTab);
        outState.putLong(STATE_CUSTOM_START, customStart);
        outState.putLong(STATE_CUSTOM_END, customEnd);
    }

    private static float amountOf(Estimate e) {
        return e.getAllTaxIncludedTotal() != null ? e.getAllTaxIncludedTotal() : 0f;
    }

    private static void truncateToDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ── Period: date range, previous range and chart buckets ────────────────

    private static class Period {
        /** How the trend chart splits the range into bars. */
        enum Bucket { HOUR, DAY, WEEK, MONTH, YEAR }

        Bucket bucket;
        boolean allTime;
        long start;
        long end;
        long previousStart;
        long previousEnd;
        int firstYear;
        String[] labels;
        String rangeLabel;
        int trendTitleRes;
        int deltaRes;

        private final Calendar scratch = Calendar.getInstance();

        static Period preset(AppCompatActivity context, int tab, List<Estimate> estimates) {
            Locale locale = Locale.getDefault();
            Calendar cal = Calendar.getInstance();
            truncateToDay(cal);
            Period p;

            switch (tab) {
                case TAB_DAY:
                    p = custom(context, cal.getTimeInMillis(), addDays(cal.getTimeInMillis(), 1));
                    p.deltaRes = R.string.dashboardVsYesterday;
                    return p;
                case TAB_WEEK: {
                    // Monday-based week, same as DBAdapter.getCurrentWeekEstimates()
                    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                    int daysSinceMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : (dayOfWeek - Calendar.MONDAY);
                    long start = addDays(cal.getTimeInMillis(), -daysSinceMonday);
                    p = custom(context, start, addDays(start, 7));
                    p.labels = p.dayLabels("EEE");
                    p.deltaRes = R.string.dashboardVsLastWeek;
                    return p;
                }
                case TAB_MONTH: {
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    long start = cal.getTimeInMillis();
                    cal.add(Calendar.MONTH, 1);
                    p = range(start, cal.getTimeInMillis(), Bucket.WEEK);
                    p.previousStart = monthsBefore(start, 1);
                    // Weeks of the month: 1–7, 8–14, ...
                    int days = (int) Math.round((p.end - p.start) / (double) DAY_MS);
                    p.labels = new String[(days + 6) / 7];
                    for (int i = 0; i < p.labels.length; i++) {
                        p.labels[i] = (i * 7 + 1) + "–" + Math.min(days, i * 7 + 7);
                    }
                    p.rangeLabel = new SimpleDateFormat("MMMM yyyy", locale).format(start);
                    p.trendTitleRes = R.string.dashboardTrendByWeek;
                    p.deltaRes = R.string.dashboardVsLastMonth;
                    return p;
                }
                case TAB_YEAR: {
                    cal.set(Calendar.DAY_OF_YEAR, 1);
                    long start = cal.getTimeInMillis();
                    cal.add(Calendar.YEAR, 1);
                    p = range(start, cal.getTimeInMillis(), Bucket.MONTH);
                    p.previousStart = monthsBefore(start, 12);
                    p.labels = p.monthLabels("MMM");
                    p.rangeLabel = new SimpleDateFormat("yyyy", locale).format(start);
                    p.trendTitleRes = R.string.dashboardTrendByMonth;
                    p.deltaRes = R.string.dashboardVsLastYear;
                    return p;
                }
                case TAB_ALL:
                default: {
                    // One bar per year, from the first estimate (at most the last few years)
                    int currentYear = cal.get(Calendar.YEAR);
                    int minYear = currentYear;
                    Calendar scratch = Calendar.getInstance();
                    for (Estimate e : estimates) {
                        if (e.getIssueDate() == null || e.getIssueDate() == 0L) continue;
                        scratch.setTimeInMillis(e.getIssueDate());
                        minYear = Math.min(minYear, scratch.get(Calendar.YEAR));
                    }
                    p = new Period();
                    p.allTime = true;
                    p.bucket = Bucket.YEAR;
                    p.start = Long.MIN_VALUE;
                    p.end = Long.MAX_VALUE;
                    p.firstYear = Math.max(minYear, currentYear - (ALL_TIME_YEARS - 1));
                    p.labels = new String[currentYear - p.firstYear + 1];
                    for (int i = 0; i < p.labels.length; i++) p.labels[i] = String.valueOf(p.firstYear + i);
                    p.rangeLabel = context.getString(R.string.dashboardAllTime);
                    p.trendTitleRes = R.string.dashboardTrendByYear;
                    p.deltaRes = 0;
                    return p;
                }
            }
        }

        /** A calendar selection of whole days [start, end); bar size depends on its length. */
        static Period custom(AppCompatActivity context, long start, long end) {
            Locale locale = Locale.getDefault();
            int days = (int) Math.round((end - start) / (double) DAY_MS);
            Bucket bucket = days <= 1 ? Bucket.HOUR
                    : days <= 31 ? Bucket.DAY
                    : days <= 182 ? Bucket.WEEK
                    : days <= 731 ? Bucket.MONTH
                    : Bucket.YEAR;

            Period p = range(start, end, bucket);
            DateFormat medium = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
            if (days <= 1) {
                p.rangeLabel = medium.format(start);
                p.deltaRes = R.string.dashboardVsPreviousDay;
            } else {
                p.rangeLabel = medium.format(start) + " – " + medium.format(end - 1);
                p.deltaRes = R.string.dashboardVsPreviousPeriod;
            }

            Calendar first = Calendar.getInstance();
            first.setTimeInMillis(start);
            Calendar last = Calendar.getInstance();
            last.setTimeInMillis(end - 1);
            boolean sameMonth = first.get(Calendar.MONTH) == last.get(Calendar.MONTH)
                    && first.get(Calendar.YEAR) == last.get(Calendar.YEAR);
            boolean sameYear = first.get(Calendar.YEAR) == last.get(Calendar.YEAR);

            switch (bucket) {
                case HOUR:
                    p.labels = new String[24];
                    for (int i = 0; i < 24; i++) p.labels[i] = i + "h";
                    p.trendTitleRes = R.string.dashboardTrendByHour;
                    break;
                case DAY:
                    p.labels = p.dayLabels(sameMonth ? "d" : "d/M");
                    p.trendTitleRes = R.string.dashboardTrendByDay;
                    break;
                case WEEK: {
                    p.labels = new String[(days + 6) / 7];
                    SimpleDateFormat f = new SimpleDateFormat("d MMM", locale);
                    for (int i = 0; i < p.labels.length; i++) p.labels[i] = f.format(addDays(start, i * 7));
                    p.trendTitleRes = R.string.dashboardTrendByWeek;
                    break;
                }
                case MONTH:
                    p.labels = p.monthLabels(sameYear ? "MMM" : "MMM yy");
                    p.trendTitleRes = R.string.dashboardTrendByMonth;
                    break;
                case YEAR:
                default:
                    p.firstYear = first.get(Calendar.YEAR);
                    p.labels = new String[last.get(Calendar.YEAR) - p.firstYear + 1];
                    for (int i = 0; i < p.labels.length; i++) p.labels[i] = String.valueOf(p.firstYear + i);
                    p.trendTitleRes = R.string.dashboardTrendByYear;
                    break;
            }
            return p;
        }

        /** Range with the previous period of the same length right before it. */
        private static Period range(long start, long end, Bucket bucket) {
            Period p = new Period();
            p.bucket = bucket;
            p.start = start;
            p.end = end;
            p.previousEnd = start;
            int days = (int) Math.round((end - start) / (double) DAY_MS);
            p.previousStart = addDays(start, -days);
            return p;
        }

        private static long monthsBefore(long millis, int months) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(millis);
            cal.add(Calendar.MONTH, -months);
            return cal.getTimeInMillis();
        }

        private String[] dayLabels(String pattern) {
            int days = (int) Math.round((end - start) / (double) DAY_MS);
            String[] out = new String[days];
            SimpleDateFormat f = new SimpleDateFormat(pattern, Locale.getDefault());
            for (int i = 0; i < days; i++) out[i] = f.format(addDays(start, i));
            return out;
        }

        private String[] monthLabels(String pattern) {
            int count = monthKey(end - 1) - monthKey(start) + 1;
            String[] out = new String[count];
            SimpleDateFormat f = new SimpleDateFormat(pattern, Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(start);
            for (int i = 0; i < count; i++) {
                out[i] = f.format(cal.getTime());
                cal.add(Calendar.MONTH, 1);
            }
            return out;
        }

        private int monthKey(long millis) {
            scratch.setTimeInMillis(millis);
            return scratch.get(Calendar.YEAR) * 12 + scratch.get(Calendar.MONTH);
        }

        boolean contains(long millis) {
            return allTime || (millis >= start && millis < end);
        }

        boolean containsPrevious(long millis) {
            return !allTime && millis >= previousStart && millis < previousEnd;
        }

        /** Index of the chart bar for this timestamp, or -1 if it falls outside the chart. */
        int bucketOf(long millis) {
            if (!contains(millis)) return -1;
            int index;
            switch (bucket) {
                case HOUR:
                    scratch.setTimeInMillis(millis);
                    index = scratch.get(Calendar.HOUR_OF_DAY);
                    break;
                case DAY:
                    index = daysFromStart(millis);
                    break;
                case WEEK:
                    index = daysFromStart(millis) / 7;
                    break;
                case MONTH:
                    index = monthKey(millis) - monthKey(start);
                    break;
                case YEAR:
                default:
                    scratch.setTimeInMillis(millis);
                    index = scratch.get(Calendar.YEAR) - firstYear;
                    break;
            }
            return index >= 0 && index < labels.length ? index : -1;
        }

        /** Whole local days between the start and this timestamp (DST-safe). */
        private int daysFromStart(long millis) {
            scratch.setTimeInMillis(millis);
            truncateToDay(scratch);
            return (int) Math.round((scratch.getTimeInMillis() - start) / (double) DAY_MS);
        }
    }
}
