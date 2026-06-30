package com.yourpackage.metalestimates; // adjust to your actual package

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Arrays;

/**
 * Single SettingsActivity driving all user preferences.
 * Uses SharedPreferences (PREFS_SETTINGS) for all keys.
 *
 * Reads / writes:
 *   KEY_CURRENCY_CODE      — active currency code, e.g. "MAD"
 *   KEY_CURRENCY_MANUAL    — boolean; true = user override
 *   KEY_DEFAULT_VAT        — float, e.g. 20.0
 *   KEY_EXPIRATION_DAYS    — int,   e.g. 30
 *   KEY_DUE_TERMS          — String, e.g. "Net 30"
 *   KEY_DATE_FORMAT        — String, e.g. "dd/MM/yyyy"
 *   KEY_BACKUP_REMINDER    — boolean
 */
public class SettingsActivity extends AppCompatActivity {

    // ── Shared preferences keys (use these everywhere in the app) ──────────
    public static final String PREFS_SETTINGS      = "app_settings";
    public static final String KEY_CURRENCY_CODE   = "currency_code";
    public static final String KEY_CURRENCY_MANUAL = "currency_is_manual";
    public static final String KEY_DEFAULT_VAT     = "default_vat";
    public static final String KEY_EXPIRATION_DAYS = "expiration_days";
    public static final String KEY_DUE_TERMS       = "due_terms";
    public static final String KEY_DATE_FORMAT     = "date_format";
    public static final String KEY_BACKUP_REMINDER = "backup_reminder";

    // ── Spinner options ─────────────────────────────────────────────────────
    private static final String[] CURRENCY_CODES = {
            "MAD", "USD", "EUR", "GBP", "SAR", "AED",
            "EGP", "TND", "DZD", "QAR", "KWD"
    };
    private static final String[] DUE_TERMS_OPTIONS = {
            "Immediate", "Net 15", "Net 30", "Net 45", "Net 60", "On delivery"
    };
    private static final String[] DATE_FORMATS = {
            "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd-MM-yyyy"
    };

    // ── Views ───────────────────────────────────────────────────────────────
    private SwitchMaterial   switchAutoDetect;
    private View             rowCurrencyPicker;
    private Spinner          spinnerCurrency;
    private TextInputEditText etVat;
    private TextInputEditText etExpirationDays;
    private Spinner          spinnerDueTerms;
    private Spinner          spinnerDateFormat;
    private SwitchMaterial   switchBackupReminder;
    private MaterialButton   btnSave;

    private SharedPreferences prefs;
    private CurrencyManager  currencyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs           = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        currencyManager = new CurrencyManager(this);

        // Toolbar with back navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        bindViews();
        populateSpinners();
        restoreSavedValues();
        wireListeners();
    }

    private void bindViews() {
        switchAutoDetect    = findViewById(R.id.switch_auto_detect);
        rowCurrencyPicker   = findViewById(R.id.row_currency_picker);
        spinnerCurrency     = findViewById(R.id.spinner_currency);
        etVat               = findViewById(R.id.et_vat);
        etExpirationDays    = findViewById(R.id.et_expiration_days);
        spinnerDueTerms     = findViewById(R.id.spinner_due_terms);
        spinnerDateFormat   = findViewById(R.id.spinner_date_format);
        switchBackupReminder = findViewById(R.id.switch_backup_reminder);
        btnSave             = findViewById(R.id.btn_save_settings);
    }

    private void populateSpinners() {
        spinnerCurrency.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, CURRENCY_CODES));
        spinnerDueTerms.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, DUE_TERMS_OPTIONS));
        spinnerDateFormat.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, DATE_FORMATS));
    }

    private void restoreSavedValues() {
        // Currency
        boolean isManual = prefs.getBoolean(KEY_CURRENCY_MANUAL, false);
        switchAutoDetect.setChecked(!isManual);
        setCurrencyPickerEnabled(isManual);

        String savedCode = prefs.getString(KEY_CURRENCY_CODE, currencyManager.getActiveCurrencyCode());
        int currPos = Arrays.asList(CURRENCY_CODES).indexOf(savedCode);
        if (currPos >= 0) spinnerCurrency.setSelection(currPos);

        // Estimates
        float vat = prefs.getFloat(KEY_DEFAULT_VAT, 20.0f);
        etVat.setText(String.valueOf(vat % 1 == 0 ? (int) vat : vat));

        int expirationDays = prefs.getInt(KEY_EXPIRATION_DAYS, 30);
        etExpirationDays.setText(String.valueOf(expirationDays));

        String savedTerms = prefs.getString(KEY_DUE_TERMS, DUE_TERMS_OPTIONS[0]);
        int termsPos = Arrays.asList(DUE_TERMS_OPTIONS).indexOf(savedTerms);
        if (termsPos >= 0) spinnerDueTerms.setSelection(termsPos);

        // Display
        String savedFormat = prefs.getString(KEY_DATE_FORMAT, DATE_FORMATS[0]);
        int formatPos = Arrays.asList(DATE_FORMATS).indexOf(savedFormat);
        if (formatPos >= 0) spinnerDateFormat.setSelection(formatPos);

        // Data
        switchBackupReminder.setChecked(prefs.getBoolean(KEY_BACKUP_REMINDER, true));
    }

    private void wireListeners() {
        // Auto-detect switch enables / disables the currency picker
        switchAutoDetect.setOnCheckedChangeListener((btn, checked) ->
                setCurrencyPickerEnabled(!checked));

        // Save button persists everything at once
        btnSave.setOnClickListener(v -> saveAllSettings());
    }

    private void setCurrencyPickerEnabled(boolean enabled) {
        spinnerCurrency.setEnabled(enabled);
        rowCurrencyPicker.setAlpha(enabled ? 1.0f : 0.4f);
    }

    private void saveAllSettings() {
        SharedPreferences.Editor editor = prefs.edit();

        // ── Currency ────────────────────────────────────────────────────────
        boolean isManual = !switchAutoDetect.isChecked();
        editor.putBoolean(KEY_CURRENCY_MANUAL, isManual);
        if (isManual) {
            String selectedCode = CURRENCY_CODES[spinnerCurrency.getSelectedItemPosition()];
            editor.putString(KEY_CURRENCY_CODE, selectedCode);
            currencyManager.setManualCurrency(selectedCode);
        } else {
            currencyManager.resetToAutoDetect();
            editor.putString(KEY_CURRENCY_CODE, currencyManager.getActiveCurrencyCode());
        }

        // ── Estimates ───────────────────────────────────────────────────────
        String vatStr = etVat.getText() != null ? etVat.getText().toString().trim() : "";
        if (!vatStr.isEmpty()) {
            try { editor.putFloat(KEY_DEFAULT_VAT, Float.parseFloat(vatStr)); }
            catch (NumberFormatException ignored) {}
        }

        String expStr = etExpirationDays.getText() != null
                ? etExpirationDays.getText().toString().trim() : "";
        if (!expStr.isEmpty()) {
            try { editor.putInt(KEY_EXPIRATION_DAYS, Integer.parseInt(expStr)); }
            catch (NumberFormatException ignored) {}
        }

        editor.putString(KEY_DUE_TERMS,
                DUE_TERMS_OPTIONS[spinnerDueTerms.getSelectedItemPosition()]);

        // ── Display ─────────────────────────────────────────────────────────
        editor.putString(KEY_DATE_FORMAT,
                DATE_FORMATS[spinnerDateFormat.getSelectedItemPosition()]);

        // ── Data ─────────────────────────────────────────────────────────────
        editor.putBoolean(KEY_BACKUP_REMINDER, switchBackupReminder.isChecked());

        editor.apply();
        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
    }

    // ── How to READ settings anywhere in the app ────────────────────────────
    // SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_SETTINGS, MODE_PRIVATE);
    // float vat            = prefs.getFloat(SettingsActivity.KEY_DEFAULT_VAT, 20f);
    // int   expDays        = prefs.getInt(SettingsActivity.KEY_EXPIRATION_DAYS, 30);
    // String dueTerm       = prefs.getString(SettingsActivity.KEY_DUE_TERMS, "Net 30");
    // String dateFormat    = prefs.getString(SettingsActivity.KEY_DATE_FORMAT, "dd/MM/yyyy");
    // boolean backupRem    = prefs.getBoolean(SettingsActivity.KEY_BACKUP_REMINDER, true);
    // CurrencyManager cm   = new CurrencyManager(context);
    // String formatted     = cm.formatAmount(15000.0);   // e.g. "MAD 15,000.00"
}
