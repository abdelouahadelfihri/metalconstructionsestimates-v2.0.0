package com.example.metalconstructionsestimates;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.metalconstructionsestimates.util.CurrencyManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    // ── SharedPreferences keys ─────────────────────────────────────────────
    public static final String PREFS_SETTINGS      = "app_settings";
    public static final String KEY_CURRENCY_CODE   = "currency_code";
    public static final String KEY_CURRENCY_MANUAL = "currency_is_manual";
    public static final String KEY_DEFAULT_VAT     = "default_vat";
    public static final String KEY_EXPIRATION_DAYS = "expiration_days";
    public static final String KEY_DUE_TERMS       = "due_terms";
    public static final String KEY_DATE_FORMAT     = "date_format";
    public static final String KEY_BACKUP_REMINDER = "backup_reminder";

    // ── Currency codes (used for saving/reading) ───────────────────────────
    private static final String[] CURRENCY_CODES = {
            // North Africa & Middle East
            "MAD", "DZD", "TND", "LYD", "EGP", "SAR", "AED", "QAR",
            "KWD", "BHD", "OMR", "JOD", "LBP", "SYP", "IQD", "YER",
            "ILS", "SDG",
            // Europe
            "EUR", "GBP", "CHF", "NOK", "SEK", "DKK", "PLN", "CZK",
            "HUF", "RON", "BGN", "HRK", "RSD", "UAH", "RUB", "TRY",
            "ISK", "ALL", "MKD", "BAM", "MDL", "GEL", "AMD", "AZN",
            // Americas
            "USD", "CAD", "MXN", "BRL", "ARS", "CLP", "COP", "PEN",
            "VES", "UYU", "BOB", "PYG", "GTQ", "CRC", "DOP", "CUP",
            "JMD", "TTD", "BBD",
            // Asia & Pacific
            "JPY", "CNY", "HKD", "SGD", "KRW", "INR", "PKR", "BDT",
            "LKR", "NPR", "MYR", "THB", "IDR", "PHP", "VND", "TWD",
            "MMK", "KHR", "LAK", "BND", "AUD", "NZD", "FJD", "PGK",
            "AFN", "IRR", "KZT", "UZS", "TMT", "TJS", "KGS", "MNT",
            "MVR", "BTN",
            // Sub-Saharan Africa
            "ZAR", "NGN", "GHS", "KES", "ETB", "TZS", "UGX", "XOF",
            "XAF", "AOA", "MZN", "ZMW", "BWP", "MUR", "MGA", "RWF",
            "BIF", "DJF", "ERN", "SOS", "GMD", "GNF", "SLL", "LRD",
            "CVE", "STN", "CDF", "SZL", "LSL", "NAD", "MWK", "ZWL",
    };

    // ── Currency labels (shown in Spinner) ─────────────────────────────────
    private static final String[] CURRENCY_LABELS = {
            // North Africa & Middle East
            "MAD - Moroccan Dirham",
            "DZD - Algerian Dinar",
            "TND - Tunisian Dinar",
            "LYD - Libyan Dinar",
            "EGP - Egyptian Pound",
            "SAR - Saudi Riyal",
            "AED - UAE Dirham",
            "QAR - Qatari Riyal",
            "KWD - Kuwaiti Dinar",
            "BHD - Bahraini Dinar",
            "OMR - Omani Rial",
            "JOD - Jordanian Dinar",
            "LBP - Lebanese Pound",
            "SYP - Syrian Pound",
            "IQD - Iraqi Dinar",
            "YER - Yemeni Rial",
            "ILS - Israeli Shekel",
            "SDG - Sudanese Pound",
            // Europe
            "EUR - Euro",
            "GBP - British Pound",
            "CHF - Swiss Franc",
            "NOK - Norwegian Krone",
            "SEK - Swedish Krona",
            "DKK - Danish Krone",
            "PLN - Polish Zloty",
            "CZK - Czech Koruna",
            "HUF - Hungarian Forint",
            "RON - Romanian Leu",
            "BGN - Bulgarian Lev",
            "HRK - Croatian Kuna",
            "RSD - Serbian Dinar",
            "UAH - Ukrainian Hryvnia",
            "RUB - Russian Ruble",
            "TRY - Turkish Lira",
            "ISK - Icelandic Krona",
            "ALL - Albanian Lek",
            "MKD - Macedonian Denar",
            "BAM - Bosnian Mark",
            "MDL - Moldovan Leu",
            "GEL - Georgian Lari",
            "AMD - Armenian Dram",
            "AZN - Azerbaijani Manat",
            // Americas
            "USD - US Dollar",
            "CAD - Canadian Dollar",
            "MXN - Mexican Peso",
            "BRL - Brazilian Real",
            "ARS - Argentine Peso",
            "CLP - Chilean Peso",
            "COP - Colombian Peso",
            "PEN - Peruvian Sol",
            "VES - Venezuelan Bolivar",
            "UYU - Uruguayan Peso",
            "BOB - Bolivian Boliviano",
            "PYG - Paraguayan Guarani",
            "GTQ - Guatemalan Quetzal",
            "CRC - Costa Rican Colon",
            "DOP - Dominican Peso",
            "CUP - Cuban Peso",
            "JMD - Jamaican Dollar",
            "TTD - Trinidad Dollar",
            "BBD - Barbadian Dollar",
            // Asia & Pacific
            "JPY - Japanese Yen",
            "CNY - Chinese Yuan",
            "HKD - Hong Kong Dollar",
            "SGD - Singapore Dollar",
            "KRW - South Korean Won",
            "INR - Indian Rupee",
            "PKR - Pakistani Rupee",
            "BDT - Bangladeshi Taka",
            "LKR - Sri Lankan Rupee",
            "NPR - Nepalese Rupee",
            "MYR - Malaysian Ringgit",
            "THB - Thai Baht",
            "IDR - Indonesian Rupiah",
            "PHP - Philippine Peso",
            "VND - Vietnamese Dong",
            "TWD - Taiwan Dollar",
            "MMK - Myanmar Kyat",
            "KHR - Cambodian Riel",
            "LAK - Lao Kip",
            "BND - Brunei Dollar",
            "AUD - Australian Dollar",
            "NZD - New Zealand Dollar",
            "FJD - Fijian Dollar",
            "PGK - Papua New Guinea Kina",
            "AFN - Afghan Afghani",
            "IRR - Iranian Rial",
            "KZT - Kazakhstani Tenge",
            "UZS - Uzbekistani Som",
            "TMT - Turkmenistani Manat",
            "TJS - Tajikistani Somoni",
            "KGS - Kyrgyzstani Som",
            "MNT - Mongolian Tugrik",
            "MVR - Maldivian Rufiyaa",
            "BTN - Bhutanese Ngultrum",
            // Sub-Saharan Africa
            "ZAR - South African Rand",
            "NGN - Nigerian Naira",
            "GHS - Ghanaian Cedi",
            "KES - Kenyan Shilling",
            "ETB - Ethiopian Birr",
            "TZS - Tanzanian Shilling",
            "UGX - Ugandan Shilling",
            "XOF - West African CFA Franc",
            "XAF - Central African CFA Franc",
            "AOA - Angolan Kwanza",
            "MZN - Mozambican Metical",
            "ZMW - Zambian Kwacha",
            "BWP - Botswanan Pula",
            "MUR - Mauritian Rupee",
            "MGA - Malagasy Ariary",
            "RWF - Rwandan Franc",
            "BIF - Burundian Franc",
            "DJF - Djiboutian Franc",
            "ERN - Eritrean Nakfa",
            "SOS - Somali Shilling",
            "GMD - Gambian Dalasi",
            "GNF - Guinean Franc",
            "SLL - Sierra Leonean Leone",
            "LRD - Liberian Dollar",
            "CVE - Cape Verdean Escudo",
            "STN - São Tomé Príncipe Dobra",
            "CDF - Congolese Franc",
            "SZL - Swazi Lilangeni",
            "LSL - Lesotho Loti",
            "NAD - Namibian Dollar",
            "MWK - Malawian Kwacha",
            "ZWL - Zimbabwean Dollar",
    };

    private static final String[] DUE_TERMS_OPTIONS = {
            "Due on receipt",
            "Next day",
            "2 days",
            "3 days",
            "4 days",
            "5 days",
            "6 days",
            "7 days",
            "10 days",
            "14 days",
            "15 days",
            "21 days",
            "28 days",
            "30 days",
            "45 days",
            "60 days",
            "90 days",
            "120 days",
            "180 days"
    };

    private static final String[] DATE_FORMATS = {
            "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd-MM-yyyy"
    };

    // ── Views ──────────────────────────────────────────────────────────────
    private SwitchMaterial    switchAutoDetect;
    private View              rowCurrencyPicker;
    private Spinner           spinnerCurrency;
    private TextInputEditText etVat;
    private TextInputEditText etExpirationDays;
    private Spinner           spinnerDueTerms;
    private Spinner           spinnerDateFormat;
    private SwitchMaterial    switchBackupReminder;
    private MaterialButton    btnSave;

    private SharedPreferences prefs;
    private CurrencyManager   currencyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs           = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        currencyManager = new CurrencyManager(this);

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
        switchAutoDetect     = findViewById(R.id.switch_auto_detect);
        rowCurrencyPicker    = findViewById(R.id.row_currency_picker);
        spinnerCurrency      = findViewById(R.id.spinner_currency);
        etVat                = findViewById(R.id.et_vat);
        etExpirationDays     = findViewById(R.id.et_expiration_days);
        spinnerDueTerms      = findViewById(R.id.spinner_due_terms);
        spinnerDateFormat    = findViewById(R.id.spinner_date_format);
        switchBackupReminder = findViewById(R.id.switch_backup_reminder);
        btnSave              = findViewById(R.id.btn_save_settings);
    }

    private void populateSpinners() {
        spinnerCurrency.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, CURRENCY_LABELS));
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

        // Always show the active currency in the spinner (detected or manual)
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
        switchAutoDetect.setOnCheckedChangeListener((btn, checked) -> {
            setCurrencyPickerEnabled(!checked);
            if (checked) {
                // Auto-detect ON: detect currency and reflect it in the spinner
                currencyManager.resetToAutoDetect();
                String detectedCode = currencyManager.getActiveCurrencyCode();
                int pos = Arrays.asList(CURRENCY_CODES).indexOf(detectedCode);
                if (pos >= 0) spinnerCurrency.setSelection(pos);
            }
        });
        btnSave.setOnClickListener(v -> saveAllSettings());
    }

    private void setCurrencyPickerEnabled(boolean enabled) {
        // Spinner is always visible so user can see the detected currency
        // Only editable when auto-detect is OFF
        spinnerCurrency.setEnabled(enabled);
        rowCurrencyPicker.setAlpha(1.0f);
    }

    private void saveAllSettings() {
        SharedPreferences.Editor editor = prefs.edit();

        // Currency — save the CODE not the label
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

        // Estimates
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

        // Display
        editor.putString(KEY_DATE_FORMAT,
                DATE_FORMATS[spinnerDateFormat.getSelectedItemPosition()]);

        // Data
        editor.putBoolean(KEY_BACKUP_REMINDER, switchBackupReminder.isChecked());

        editor.apply();
        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
    }

    // ── Reading settings in other activities ───────────────────────────────
    // SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS_SETTINGS, MODE_PRIVATE);
    // float vat         = prefs.getFloat(SettingsActivity.KEY_DEFAULT_VAT, 20f);
    // int   expDays     = prefs.getInt(SettingsActivity.KEY_EXPIRATION_DAYS, 30);
    // String dueTerm    = prefs.getString(SettingsActivity.KEY_DUE_TERMS, "Due on receipt");
    // String dateFormat = prefs.getString(SettingsActivity.KEY_DATE_FORMAT, "dd/MM/yyyy");
    // boolean backupRem = prefs.getBoolean(SettingsActivity.KEY_BACKUP_REMINDER, true);
    // CurrencyManager cm = new CurrencyManager(context);
    // String formatted  = cm.formatAmount(15000.0);
}