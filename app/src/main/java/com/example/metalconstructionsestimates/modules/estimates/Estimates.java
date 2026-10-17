package com.example.metalconstructionsestimates.modules.estimates;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.example.metalconstructionsestimates.recyclerviewadapters.EstimatesListAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.SettingsActivity;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.util.CurrencyManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.example.metalconstructionsestimates.databinding.ActivityEstimatesBinding;

public class Estimates extends AppCompatActivity {

    private static final String PREFS_LISTS = "list_prefs";
    private static final String KEY_SORT = "estimates_sort";

    // Sort options, in the order shown in the sort dialog
    private static final int SORT_DATE_DESC = 0;
    private static final int SORT_DATE_ASC = 1;
    private static final int SORT_TOTAL_DESC = 2;
    private static final int SORT_TOTAL_ASC = 3;
    private static final int SORT_ID_DESC = 4;
    private static final int SORT_ID_ASC = 5;
    private static final int SORT_LOCATION = 6;
    private static final int SORT_STATUS = 7;

    public ActivityResultLauncher<Intent> activityResultLauncher;
    TextInputEditText estimatesSearchEditText;
    Button allEstimatesButton, pendingEstimatesButton, approvedEstimatesButton,
            overdueEstimatesButton, cancelledEstimatesButton;
    String selectedEstimateStatus = "All";
    DBAdapter dbAdapter;

    private ActivityEstimatesBinding activityEstimatesBinding;
    private EstimatesListAdapter estimateListAdapter;
    private int sortOption;
    private ArrayList<Estimate> displayedEstimates = new ArrayList<>();
    private Map<Integer, String> customerNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEstimatesBinding = ActivityEstimatesBinding.inflate(getLayoutInflater());
        setContentView(activityEstimatesBinding.getRoot());

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DBAdapter(getApplicationContext());
        sortOption = getSharedPreferences(PREFS_LISTS, MODE_PRIVATE).getInt(KEY_SORT, SORT_ID_ASC);

        // One column on phones, more on tablets / landscape (res/values-w600dp, values-w900dp)
        int columns = getResources().getInteger(R.integer.list_columns);
        activityEstimatesBinding.estimatesRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        estimateListAdapter = new EstimatesListAdapter(this, displayedEstimates);
        activityEstimatesBinding.estimatesRecyclerView.setAdapter(estimateListAdapter);

        activityEstimatesBinding.swipeRefresh.setColorSchemeResources(R.color.dashboard_brand);
        activityEstimatesBinding.swipeRefresh.setOnRefreshListener(() -> {
            refreshList();
            activityEstimatesBinding.swipeRefresh.setRefreshing(false);
        });

        allEstimatesButton = findViewById(R.id.buttonAll);
        pendingEstimatesButton = findViewById(R.id.buttonPending);
        approvedEstimatesButton = findViewById(R.id.buttonApproved);
        overdueEstimatesButton = findViewById(R.id.buttonOverdue);
        cancelledEstimatesButton = findViewById(R.id.buttonCancel);

        allEstimatesButton.setOnClickListener(view -> selectStatus("All"));
        pendingEstimatesButton.setOnClickListener(view -> selectStatus("Pending"));
        approvedEstimatesButton.setOnClickListener(view -> selectStatus("Approved"));
        overdueEstimatesButton.setOnClickListener(view -> selectStatus("Overdue"));
        cancelledEstimatesButton.setOnClickListener(view -> selectStatus("Cancelled"));

        FloatingActionButton clearEstimateForm = findViewById(R.id.fab_clear);
        FloatingActionButton addEstimate = findViewById(R.id.fab_add);
        FloatingActionButton reloadEstimatesList = findViewById(R.id.fab_refresh);

        estimatesSearchEditText = findViewById(R.id.searchEditText);
        estimatesSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                refreshList();
            }
        });

        reloadEstimatesList.setOnClickListener(view -> refreshList());

        addEstimate.setOnClickListener(view -> {
            Intent intent = new Intent(Estimates.this, AddEstimate.class);
            startActivity(intent);
        });

        // Clearing the search also goes back to showing every status
        clearEstimateForm.setOnClickListener(view -> {
            estimatesSearchEditText.getText().clear();
            selectStatus("All");
        });

        selectStatus("All");
    }

    /** Highlights the chosen status filter button and reloads the list for it. */
    private void selectStatus(String status) {
        selectedEstimateStatus = status;
        styleStatusButton(allEstimatesButton, "All".equals(status));
        styleStatusButton(pendingEstimatesButton, "Pending".equals(status));
        styleStatusButton(approvedEstimatesButton, "Approved".equals(status));
        styleStatusButton(overdueEstimatesButton, "Overdue".equals(status));
        styleStatusButton(cancelledEstimatesButton, "Cancelled".equals(status));
        refreshList();
    }

    private void styleStatusButton(Button button, boolean selected) {
        button.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(),
                selected ? R.color.button_bg_selected : R.color.button_bg_default));
        button.setTextColor(ContextCompat.getColor(getApplicationContext(),
                selected ? R.color.white : R.color.button_text_default));
    }

    /** Reloads estimates for the selected status, applying the current search text and sort option. */
    private void refreshList() {
        String searchText = estimatesSearchEditText == null ? "" : Objects.requireNonNull(estimatesSearchEditText.getText()).toString().trim();
        boolean searching = !searchText.isEmpty();
        ArrayList<Estimate> estimates;
        switch (selectedEstimateStatus) {
            case "Pending":
                estimates = searching ? dbAdapter.searchPendingEstimates(searchText) : dbAdapter.retrievePendingEstimates();
                break;
            case "Approved":
                estimates = searching ? dbAdapter.searchApprovedEstimates(searchText) : dbAdapter.retrieveApprovedEstimates();
                break;
            case "Overdue":
                estimates = searching ? dbAdapter.searchOverdueEstimates(searchText) : dbAdapter.retrieveOverdueEstimates();
                break;
            case "Cancelled":
                estimates = searching ? dbAdapter.searchCancelledEstimates(searchText) : dbAdapter.retrieveCancelledEstimates();
                break;
            default:
                estimates = searching ? dbAdapter.searchEstimates(searchText) : dbAdapter.retrieveEstimates();
                break;
        }

        customerNames = new HashMap<>();
        for (Customer customer : dbAdapter.retrieveCustomers()) {
            customerNames.put(customer.getId(), customer.getName());
        }

        estimates.sort(comparatorFor(sortOption));
        displayedEstimates = estimates;
        estimateListAdapter.setCustomerNames(customerNames);
        estimateListAdapter.updateEstimates(estimates);

        if (estimates.isEmpty()) {
            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.GONE);
            if (searching || !"All".equals(selectedEstimateStatus)) {
                showEmptyState(R.string.noResultTitle, R.string.noResultSubtitle);
            } else {
                showEmptyState(R.string.noEstimatesTitle, R.string.noEstimatesSubtitle);
            }
        } else {
            hideEmptyState();
            activityEstimatesBinding.estimatesRecyclerView.setVisibility(View.VISIBLE);
        }

        // Subtitle: how many estimates are shown and what they add up to (incl. VAT)
        float total = 0f;
        for (Estimate estimate : estimates) {
            if (estimate.getAllTaxIncludedTotal() != null) {
                total += estimate.getAllTaxIncludedTotal();
            }
        }
        String count = getResources().getQuantityString(R.plurals.estimatesCount, estimates.size(), estimates.size());
        String currency = new CurrencyManager(this).getActiveCurrencyCode();
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(estimates.isEmpty()
                ? count
                : count + "  ·  " + EstimatesListAdapter.formatLargeNumber(total) + " " + currency);
    }

    private static Comparator<Estimate> comparatorFor(int option) {
        Comparator<Estimate> byId = Comparator.comparing(Estimate::getId, Comparator.nullsLast(Comparator.naturalOrder()));
        switch (option) {
            case SORT_DATE_DESC:
                return Comparator.comparing(Estimate::getIssueDate, Comparator.nullsLast(Comparator.<Long>reverseOrder()))
                        .thenComparing(byId.reversed());
            case SORT_DATE_ASC:
                return Comparator.comparing(Estimate::getIssueDate, Comparator.nullsLast(Comparator.<Long>naturalOrder()))
                        .thenComparing(byId);
            case SORT_TOTAL_DESC:
                return Comparator.comparing(Estimate::getAllTaxIncludedTotal, Comparator.nullsLast(Comparator.<Float>reverseOrder()))
                        .thenComparing(byId);
            case SORT_TOTAL_ASC:
                return Comparator.comparing(Estimate::getAllTaxIncludedTotal, Comparator.nullsLast(Comparator.<Float>naturalOrder()))
                        .thenComparing(byId);
            case SORT_ID_DESC:
                return byId.reversed();
            case SORT_LOCATION:
                return Comparator.comparing((Estimate e) -> e.getDoneIn() == null ? "" : e.getDoneIn(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(byId);
            case SORT_STATUS:
                return Comparator.comparingInt((Estimate e) -> statusRank(EstimatesListAdapter.displayStatus(e)))
                        .thenComparing(byId);
            case SORT_ID_ASC:
            default:
                return byId;
        }
    }

    // Open work first: pending, overdue, approved, cancelled
    private static int statusRank(String status) {
        switch (status) {
            case "Pending":   return 0;
            case "Overdue":   return 1;
            case "Approved":  return 2;
            case "Cancelled": return 3;
            default:          return 4;
        }
    }

    private void showSortDialog() {
        String[] options = {
                getString(R.string.sortDateDesc),
                getString(R.string.sortDateAsc),
                getString(R.string.sortTotalDesc),
                getString(R.string.sortTotalAsc),
                getString(R.string.sortIdDesc),
                getString(R.string.sortIdAsc),
                getString(R.string.sortLocation),
                getString(R.string.sortStatus)
        };
        new AlertDialog.Builder(this)
                .setTitle(R.string.sortBy)
                .setSingleChoiceItems(options, sortOption, (dialog, which) -> {
                    sortOption = which;
                    getSharedPreferences(PREFS_LISTS, MODE_PRIVATE).edit().putInt(KEY_SORT, which).apply();
                    refreshList();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    /** Shares the estimates currently shown (after filter, search and sort) as a plain-text summary. */
    private void shareList() {
        if (displayedEstimates.isEmpty()) {
            Toast.makeText(this, R.string.listEmptyNothingToShare, Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_SETTINGS, MODE_PRIVATE);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                prefs.getString(SettingsActivity.KEY_DATE_FORMAT, "dd/MM/yyyy"), Locale.getDefault());
        String currency = new CurrencyManager(this).getActiveCurrencyCode();

        StringBuilder text = new StringBuilder(getString(R.string.estimatesList));
        if (!"All".equals(selectedEstimateStatus)) {
            text.append(" (").append(selectedEstimateStatus).append(')');
        }
        text.append('\n');
        for (Estimate estimate : displayedEstimates) {
            text.append("\n#").append(estimate.getId());
            String customer = estimate.getCustomer() == null ? null : customerNames.get(estimate.getCustomer());
            if (customer != null && !customer.isEmpty()) {
                text.append("  ").append(customer);
            }
            if (estimate.getIssueDate() != null && estimate.getIssueDate() != 0) {
                text.append("  |  ").append(dateFormat.format(new Date(estimate.getIssueDate())));
            }
            text.append("  |  ").append(EstimatesListAdapter.displayStatus(estimate));
            if (estimate.getAllTaxIncludedTotal() != null) {
                text.append("  |  ").append(String.format(Locale.getDefault(), "%.2f", estimate.getAllTaxIncludedTotal()))
                        .append(' ').append(currency);
            }
        }
        Intent share = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.estimatesList))
                .putExtra(Intent.EXTRA_TEXT, text.toString());
        startActivity(Intent.createChooser(share, getString(R.string.shareListChooser)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        } else if (id == R.id.action_share_list) {
            shareList();
            return true;
        } else if (id == R.id.action_refresh_list) {
            refreshList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEmptyState(int titleResId, int subtitleResId) {
        activityEstimatesBinding.emptyStateLayout.setVisibility(View.VISIBLE);
        TextView title = activityEstimatesBinding.emptyStateLayout.findViewById(R.id.emptyStateTitle);
        TextView subtitle = activityEstimatesBinding.emptyStateLayout.findViewById(R.id.emptyStateSubtitle);
        title.setText(titleResId);
        subtitle.setText(subtitleResId);
    }

    private void hideEmptyState() {
        activityEstimatesBinding.emptyStateLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Reload when coming back from AddEstimate / EstimateDetails so changes show up,
        // keeping the selected status filter, search text and sort order
        refreshList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close DBAdapter to release database resources
        if (dbAdapter != null) {
            dbAdapter.close();
        }
    }
}
