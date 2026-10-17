package com.example.metalconstructionsestimates.modules.customers;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.recyclerviewadapters.CustomersListAdapter;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.metalconstructionsestimates.databinding.ActivityCustomersBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Customers extends AppCompatActivity {

    private static final String PREFS_LISTS = "list_prefs";
    private static final String KEY_SORT = "customers_sort";

    // Sort options, in the order shown in the sort dialog
    private static final int SORT_ID_DESC = 0;
    private static final int SORT_ID_ASC = 1;
    private static final int SORT_NAME_ASC = 2;
    private static final int SORT_NAME_DESC = 3;

    TextInputEditText customerSearchEditText;
    FloatingActionButton addCustomer,clearCustomerForm,reloadCustomersList;

    private ActivityCustomersBinding binding;
    private CustomersListAdapter customersListAdapter;
    private int sortOption;
    private ArrayList<Customer> displayedCustomers = new ArrayList<>();

    DBAdapter dbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        dbAdapter = new DBAdapter(getApplicationContext());

        sortOption = getSharedPreferences(PREFS_LISTS, MODE_PRIVATE).getInt(KEY_SORT, SORT_ID_ASC);

        // One column on phones, more on tablets / landscape (res/values-w600dp, values-w900dp)
        int columns = getResources().getInteger(R.integer.list_columns);
        binding.customerRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        customersListAdapter = new CustomersListAdapter(this, displayedCustomers);
        binding.customerRecyclerView.setAdapter(customersListAdapter);

        binding.swipeRefresh.setColorSchemeResources(R.color.dashboard_brand);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            refreshList();
            binding.swipeRefresh.setRefreshing(false);
        });

        addCustomer = findViewById(R.id.fab_add_customer);
        clearCustomerForm = findViewById(R.id.fab_clear_customers);
        reloadCustomersList = findViewById(R.id.fab_refresh_customers);

        addCustomer.setOnClickListener(view -> {
            Intent intent = new Intent(Customers.this, AddCustomer.class);
            startActivity(intent);
        });

        customerSearchEditText = findViewById(R.id.searchEditText_customers);
        customerSearchEditText.addTextChangedListener(new TextWatcher() {
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

        reloadCustomersList.setOnClickListener(view -> refreshList());

        clearCustomerForm.setOnClickListener(view -> customerSearchEditText.getText().clear());

        refreshList();
    }

    /** Reloads customers from the database, applying the current search text and sort option. */
    private void refreshList() {
        String searchText = customerSearchEditText == null ? "" : Objects.requireNonNull(customerSearchEditText.getText()).toString().trim();
        boolean searching = !searchText.isEmpty();
        ArrayList<Customer> customers = searching ? dbAdapter.searchCustomers(searchText) : dbAdapter.retrieveCustomers();
        customers.sort(comparatorFor(sortOption));
        displayedCustomers = customers;
        customersListAdapter.updateCustomers(customers);

        if (customers.isEmpty()) {
            binding.customerRecyclerView.setVisibility(View.GONE);
            if (searching) {
                showEmptyState(R.string.noResultTitle, R.string.noResultSubtitle);
            } else {
                showEmptyState(R.string.noCustomersTitle, R.string.noCustomersSubtitle);
            }
        } else {
            hideEmptyState();
            binding.customerRecyclerView.setVisibility(View.VISIBLE);
        }
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(
                getResources().getQuantityString(R.plurals.customersCount, customers.size(), customers.size()));
    }

    private static Comparator<Customer> comparatorFor(int option) {
        Comparator<Customer> byId = Comparator.comparing(Customer::getId, Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<Customer> byName = Comparator.comparing(c -> c.getName() == null ? "" : c.getName().trim(), String.CASE_INSENSITIVE_ORDER);
        switch (option) {
            case SORT_ID_ASC:    return byId;
            case SORT_NAME_ASC:  return byName.thenComparing(byId);
            case SORT_NAME_DESC: return byName.reversed().thenComparing(byId);
            case SORT_ID_DESC:
            default:             return byId.reversed();
        }
    }

    private void showSortDialog() {
        String[] options = {
                getString(R.string.sortIdDesc),
                getString(R.string.sortIdAsc),
                getString(R.string.sortNameAsc),
                getString(R.string.sortNameDesc)
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

    /** Shares the customers currently shown (after search and sort) as a plain-text contact list. */
    private void shareList() {
        if (displayedCustomers.isEmpty()) {
            Toast.makeText(this, R.string.listEmptyNothingToShare, Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder text = new StringBuilder(getString(R.string.customersList)).append('\n');
        for (Customer customer : displayedCustomers) {
            text.append("\n#").append(customer.getId()).append("  ").append(customer.getName() == null ? "" : customer.getName());
            appendIfPresent(text, "Tel", customer.getTelephone());
            appendIfPresent(text, "Mobile", customer.getMobile());
            appendIfPresent(text, "Email", customer.getEmail());
        }
        Intent share = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.customersList))
                .putExtra(Intent.EXTRA_TEXT, text.toString());
        startActivity(Intent.createChooser(share, getString(R.string.shareListChooser)));
    }

    private static void appendIfPresent(StringBuilder text, String label, String value) {
        if (value != null && !value.trim().isEmpty()) {
            text.append("  |  ").append(label).append(" : ").append(value.trim());
        }
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
        binding.emptyStateLayout.setVisibility(View.VISIBLE);
        TextView title = binding.emptyStateLayout.findViewById(R.id.noCustomersTitle);
        TextView subtitle = binding.emptyStateLayout.findViewById(R.id.noCustomersSubtitle);
        title.setText(titleResId);
        subtitle.setText(subtitleResId);
    }

    private void hideEmptyState() {
        binding.emptyStateLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Reload when coming back from AddCustomer / CustomerDetails so changes show up
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
