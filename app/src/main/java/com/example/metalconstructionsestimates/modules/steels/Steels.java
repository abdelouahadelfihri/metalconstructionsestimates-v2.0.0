package com.example.metalconstructionsestimates.modules.steels;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.metalconstructionsestimates.R;
import com.example.metalconstructionsestimates.recyclerviewadapters.SteelsListAdapter;
import com.example.metalconstructionsestimates.databinding.ActivitySteelsBinding;
import com.example.metalconstructionsestimates.database.DBAdapter;
import com.example.metalconstructionsestimates.models.Steel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;


public class Steels extends AppCompatActivity {
    private static final String PREFS_LISTS = "list_prefs";
    private static final String KEY_SORT = "steels_sort";

    // Sort options, in the order shown in the sort dialog
    private static final int SORT_ID_DESC = 0;
    private static final int SORT_ID_ASC = 1;
    private static final int SORT_TYPE_ASC = 2;
    private static final int SORT_TYPE_DESC = 3;
    private static final int SORT_SHAPE = 4;
    private static final int SORT_WEIGHT_ASC = 5;
    private static final int SORT_WEIGHT_DESC = 6;

    Intent intent;
    SteelsListAdapter steelsListAdapter;
    TextInputEditText steelsSearchEditText;
    FloatingActionButton addSteel, clearSearchSteelForm, reloadSteelsList;
    ActivitySteelsBinding activitySteelsBinding;
    DBAdapter dbAdapter;
    private int sortOption;
    private ArrayList<Steel> displayedSteels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySteelsBinding = ActivitySteelsBinding.inflate(getLayoutInflater());
        dbAdapter = new DBAdapter(getApplicationContext());
        setContentView(activitySteelsBinding.getRoot());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        sortOption = getSharedPreferences(PREFS_LISTS, MODE_PRIVATE).getInt(KEY_SORT, SORT_ID_ASC);

        // One column on phones, more on tablets / landscape (res/values-w600dp, values-w900dp)
        int columns = getResources().getInteger(R.integer.list_columns);
        activitySteelsBinding.steelsRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        steelsListAdapter = new SteelsListAdapter(this, displayedSteels);
        activitySteelsBinding.steelsRecyclerView.setAdapter(steelsListAdapter);

        activitySteelsBinding.swipeRefresh.setColorSchemeResources(R.color.dashboard_brand);
        activitySteelsBinding.swipeRefresh.setOnRefreshListener(() -> {
            refreshList();
            activitySteelsBinding.swipeRefresh.setRefreshing(false);
        });

        steelsSearchEditText = findViewById(R.id.searchEditText);
        steelsSearchEditText.addTextChangedListener(new TextWatcher() {
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

        reloadSteelsList = findViewById(R.id.fab_refresh);
        reloadSteelsList.setOnClickListener(view -> refreshList());

        addSteel = findViewById(R.id.fab_add);
        addSteel.setOnClickListener(view -> {
            intent = new Intent(Steels.this, AddSteel.class);
            startActivity(intent);
        });

        clearSearchSteelForm = findViewById(R.id.fab_clear);
        clearSearchSteelForm.setOnClickListener(view -> steelsSearchEditText.getText().clear());

        refreshList();
    }

    /** Reloads steels from the database, applying the current search text and sort option. */
    private void refreshList() {
        String searchText = steelsSearchEditText == null ? "" : Objects.requireNonNull(steelsSearchEditText.getText()).toString().trim();
        boolean searching = !searchText.isEmpty();
        ArrayList<Steel> steels = searching ? dbAdapter.searchSteels(searchText) : dbAdapter.retrieveSteels();
        steels.sort(comparatorFor(sortOption));
        displayedSteels = steels;
        steelsListAdapter.updateSteels(steels);

        if (steels.isEmpty()) {
            activitySteelsBinding.steelsRecyclerView.setVisibility(View.GONE);
            if (searching) {
                showEmptyState(R.string.noResultTitle, R.string.noResultSubtitle);
            } else {
                showEmptyState(R.string.noSteelsTitle, R.string.noSteelsSubtitle);
            }
        } else {
            hideEmptyState();
            activitySteelsBinding.steelsRecyclerView.setVisibility(View.VISIBLE);
        }
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(
                getResources().getQuantityString(R.plurals.steelsCount, steels.size(), steels.size()));
    }

    private static Comparator<Steel> comparatorFor(int option) {
        Comparator<Steel> byId = Comparator.comparing(Steel::getId, Comparator.nullsLast(Comparator.naturalOrder()));
        Comparator<Steel> byType = Comparator.comparing(s -> s.getType() == null ? "" : s.getType(), String.CASE_INSENSITIVE_ORDER);
        Comparator<Steel> byWeight = Comparator.comparing(Steel::getWeight, Comparator.nullsLast(Comparator.naturalOrder()));
        switch (option) {
            case SORT_ID_ASC:      return byId;
            case SORT_TYPE_ASC:    return byType.thenComparing(byId);
            case SORT_TYPE_DESC:   return byType.reversed().thenComparing(byId);
            case SORT_SHAPE:       return Comparator.comparing((Steel s) -> s.getGeometricShape() == null ? "" : s.getGeometricShape(),
                                           String.CASE_INSENSITIVE_ORDER).thenComparing(byType);
            case SORT_WEIGHT_ASC:  return byWeight.thenComparing(byId);
            case SORT_WEIGHT_DESC: return Comparator.comparing(Steel::getWeight, Comparator.nullsLast(Comparator.<Float>reverseOrder()))
                                           .thenComparing(byId);
            case SORT_ID_DESC:
            default:               return byId.reversed();
        }
    }

    private void showSortDialog() {
        String[] options = {
                getString(R.string.sortIdDesc),
                getString(R.string.sortIdAsc),
                getString(R.string.sortTypeAsc),
                getString(R.string.sortTypeDesc),
                getString(R.string.sortShape),
                getString(R.string.sortWeightAsc),
                getString(R.string.sortWeightDesc)
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

    /** Shares the steels currently shown (after search and sort) as plain text. */
    private void shareList() {
        if (displayedSteels.isEmpty()) {
            Toast.makeText(this, R.string.listEmptyNothingToShare, Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder text = new StringBuilder(getString(R.string.steelsList)).append('\n');
        for (Steel steel : displayedSteels) {
            text.append("\n#").append(steel.getId()).append("  ").append(steel.getType() == null ? "" : steel.getType());
            if (steel.getGeometricShape() != null && !steel.getGeometricShape().isEmpty()
                    && !steel.getGeometricShape().equals("Geometric Shape")) {
                text.append("  |  ").append(steel.getGeometricShape());
            }
            if (steel.getWeight() != null) {
                text.append("  |  ").append(steel.getWeight());
            }
        }
        Intent share = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.steelsList))
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
        activitySteelsBinding.emptyStateLayout.setVisibility(View.VISIBLE);
        TextView title = activitySteelsBinding.emptyStateLayout.findViewById(R.id.emptyStateTitle);
        TextView subtitle = activitySteelsBinding.emptyStateLayout.findViewById(R.id.emptyStateSubtitle);
        title.setText(titleResId);
        subtitle.setText(subtitleResId);
    }

    private void hideEmptyState() {
        activitySteelsBinding.emptyStateLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Reload when coming back from AddSteel / SteelDetails so changes show up
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
