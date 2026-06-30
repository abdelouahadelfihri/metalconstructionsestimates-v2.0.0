package com.example.metalconstructionsestimates.util; // adjust to your actual package

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import java.util.Currency;
import java.util.Locale;

/**
 * Detects the user's country/currency automatically and lets them override it manually.
 * No special permissions required.
 *
 * Usage:
 *   CurrencyManager cm = new CurrencyManager(context);
 *   String code = cm.getActiveCurrencyCode();      // e.g. "MAD"
 *   String label = cm.formatAmount(1500.0);        // e.g. "MAD 1,500.00"
 */
public class CurrencyManager {

    private static final String PREFS_NAME = "currency_prefs";
    private static final String KEY_CURRENCY_CODE = "currency_code";
    private static final String KEY_IS_MANUAL = "currency_is_manual";

    private final Context context;
    private final SharedPreferences prefs;

    public CurrencyManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Detect the device's country (ISO 3166-1 alpha-2), SIM/network first, then system locale. */
    public String detectCountryCode() {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String simCountry = tm.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) {
                    return simCountry.toUpperCase(Locale.ROOT);
                }
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) {
                    return networkCountry.toUpperCase(Locale.ROOT);
                }
            }
        } catch (Exception e) {
            // No SIM, airplane mode, restricted access, etc. — fall through to locale.
        }
        String localeCountry = Locale.getDefault().getCountry();
        if (localeCountry != null && localeCountry.length() == 2) {
            return localeCountry.toUpperCase(Locale.ROOT);
        }
        return "MA"; // final fallback — set to your primary market
    }

    /** Map a country code to its currency code, e.g. "MA" -> "MAD", "FR" -> "EUR". */
    public String getCurrencyCodeForCountry(String countryCode) {
        try {
            Currency currency = Currency.getInstance(new Locale("", countryCode));
            return currency.getCurrencyCode();
        } catch (Exception e) {
            return "USD";
        }
    }

    /** The currency code actually in effect: manual override if set, otherwise auto-detected. */
    public String getActiveCurrencyCode() {
        if (prefs.getBoolean(KEY_IS_MANUAL, false)) {
            String saved = prefs.getString(KEY_CURRENCY_CODE, null);
            if (saved != null) return saved;
        }
        String code = getCurrencyCodeForCountry(detectCountryCode());
        prefs.edit().putString(KEY_CURRENCY_CODE, code).apply(); // cache so we don't redetect every call
        return code;
    }

    /** Call when the user manually picks a currency in Settings. */
    public void setManualCurrency(String currencyCode) {
        prefs.edit()
                .putString(KEY_CURRENCY_CODE, currencyCode)
                .putBoolean(KEY_IS_MANUAL, true)
                .apply();
    }

    /** Call if the user wants to go back to automatic detection. */
    public void resetToAutoDetect() {
        prefs.edit().putBoolean(KEY_IS_MANUAL, false).apply();
    }

    public boolean isManual() {
        return prefs.getBoolean(KEY_IS_MANUAL, false);
    }

    /** Format an amount using the active currency's symbol, e.g. "MAD 1,500.00". */
    public String formatAmount(double amount) {
        String code = getActiveCurrencyCode();
        try {
            Currency currency = Currency.getInstance(code);
            return currency.getSymbol(Locale.getDefault()) + " "
                    + String.format(Locale.getDefault(), "%,.2f", amount);
        } catch (Exception e) {
            return String.format(Locale.getDefault(), "%,.2f %s", amount, code);
        }
    }
}
