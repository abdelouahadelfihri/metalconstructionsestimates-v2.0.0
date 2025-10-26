package com.example.metalconstructionsestimates.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.metalconstructionsestimates.models.Business;
import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DBAdapter {
    Context context;
    SQLiteDatabase db;
    DBHelper helper;
    private static final String TAG = "DBAdapter";
    public DBAdapter(Context context) {
        this.context = context;
        helper = new DBHelper(context);
    }

    public int getEstimatesCount() {
        String countQuery = "SELECT * FROM estimate";
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getSteelsCount() {
        String countQuery = "SELECT * FROM steel";
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getCustomersCount() {
        String countQuery = "SELECT * FROM customer";
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Float getEstimatesTotal() {
        float estimatesTotal = 0.0f;
        Cursor cursor = null;
        try {
            db = helper.getReadableDatabase();
            String query = "SELECT SUM(allTaxIncludedTotal) FROM estimate";
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {  // Ensure the cursor contains data
                estimatesTotal = cursor.isNull(0) ? 0 : cursor.getFloat(0);  // Handle NULL case
            } else {
                estimatesTotal = 0f;  // Default value if no rows exist
            }
        } finally {
            if (cursor != null) cursor.close();  // Close the cursor
            if (db != null) db.close();  // Close the database
        }

        return estimatesTotal;
    }

    public Float getCurrentDayEstimatesTotal() {
        float currentDayEstimatesTotal = 0.0f;
        Cursor cursor = null;
        String date;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date = year + "-" + month + "-" + day;
        try {
            db = helper.getReadableDatabase();
            String query = "SELECT sum(allTaxIncludedTotal) as 'currentDayEstimatesTotal' FROM estimate WHERE issueDate = '" + date + "'";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                currentDayEstimatesTotal = cursor.getFloat(0);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }
        return currentDayEstimatesTotal;
    }

    public int getCurrentDayEstimatesCount() {
        int currentDayEstimatesCount = 0;
        Cursor cursor = null;
        String date;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date = year + "-" + month + "-" + day;
        try {
            db = helper.getReadableDatabase();
            String query = "SELECT * FROM estimate WHERE issueDate='" + date + "'";
            cursor = db.rawQuery(query, null);
            currentDayEstimatesCount = cursor.getCount();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }
        return currentDayEstimatesCount;
    }

    public ArrayList<Estimate> getCurrentDayEstimates() {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String date;
        Calendar calendar = Calendar.getInstance();
        Cursor cursor = null;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date = year + "-" + month + "-" + day;
        try {
            db = helper.getReadableDatabase();
            String query = "SELECT * FROM estimate WHERE issueDate ='" + date + "'";
            cursor = db.rawQuery(query, null);
            Estimate estimate;
            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }

        return estimatesList;
    }

    public Float getCurrentWeekEstimatesTotal() {
        Cursor cursor = null;
        Float getCurrentWeekEstimatesTotal = 0.0f;
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                try {
                    db = helper.getReadableDatabase();
                    String date = year + "-" + month + "-" + day;
                    String query = "SELECT sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' FROM estimate WHERE issueDate ='" + date + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        getCurrentWeekEstimatesTotal = cursor.getFloat(0);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.TUESDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 1;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.WEDNESDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 2;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.THURSDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 3;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.FRIDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 4;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.SATURDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 5;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.SUNDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 6;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
        }
        return getCurrentWeekEstimatesTotal;
    }

    public ArrayList<Estimate> getCurrentWeekEstimates() {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        Cursor cursor = null;
        String query = null;
        String highDate, lowDate;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;

        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        switch (dayOfWeek) {
            case Calendar.MONDAY:
                db = helper.getReadableDatabase();
                String date = year + "-" + month + "-" + day;
                query = "SELECT * FROM estimate WHERE issueDate ='" + date + "'";
                break;
            case Calendar.TUESDAY:
                db = helper.getReadableDatabase();
                highDate = sdf.format(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, -1);
                lowDate = sdf.format(cal.getTime());
                query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                break;
            case Calendar.WEDNESDAY:
                db = helper.getReadableDatabase();
                highDate = sdf.format(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, -2);
                lowDate = sdf.format(cal.getTime());
                query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                break;
            case Calendar.THURSDAY:
                db = helper.getReadableDatabase();
                highDate = sdf.format(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, -3);
                lowDate = sdf.format(cal.getTime());
                query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";

                break;
            case Calendar.FRIDAY:
                db = helper.getReadableDatabase();
                highDate = sdf.format(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, -4);
                lowDate = sdf.format(cal.getTime());
                query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                break;
            case Calendar.SATURDAY:
                db = helper.getReadableDatabase();
                highDate = sdf.format(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, -5);
                lowDate = sdf.format(cal.getTime());
                query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                break;
            case Calendar.SUNDAY:
                db = helper.getReadableDatabase();
                highDate = sdf.format(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, -6);
                lowDate = sdf.format(cal.getTime());
                query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                break;
        }

        try{
            cursor = db.rawQuery(query, null);
            Estimate estimate;
            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }

        return estimatesList;
    }


    public int getCurrentWeekEstimatesCount() {
        Cursor cursor = null;
        int currentWeekEstimatesCount = 0;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                try {
                    db = helper.getReadableDatabase();
                    String date = year + "-" + month + "-" + day;
                    String query = "SELECT * FROM estimate WHERE issueDate ='" + date + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.TUESDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 1;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.WEDNESDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 2;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.THURSDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 3;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                break;
            case Calendar.FRIDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 4;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.SATURDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 5;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
            case Calendar.SUNDAY:
                try {
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 6;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "SELECT * FROM estimate WHERE issueDate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                } catch (SQLException e) {
                    Log.e(TAG, "Database error occurred", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                break;
        }
        return currentWeekEstimatesCount;
    }


    public Float getCurrentMonthEstimatesTotal() {
        Float currentMonthEstimatesTotal = 0.0f;
        String query;
        Cursor cursor = null;
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month++;
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String startDate = year + "-" + month + "-1";
        try {
            db = helper.getReadableDatabase();
            if (day == 1) {
                query = "SELECT sum(allTaxIncludedTotal) as 'currentMonthEstimatesTotal' FROM estimate WHERE issueDate ='" + startDate + "'";
            } else {
                String highDate = year + "-" + month + "-" + day;
                query = "SELECT sum(allTaxIncludedTotal) as 'currentMonthEstimatesTotal' FROM estimate WHERE issueDate between '" + startDate + "' and '" + highDate + "'";
            }

            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                currentMonthEstimatesTotal += cursor.getFloat(0);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }
        return currentMonthEstimatesTotal;
    }

    public ArrayList<Estimate> getCurrentMonthEstimates() {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String query;
        Cursor cursor = null;
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month++;
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String startOfMonth = year + "-" + month + "-1";
        String currentDate = year + "-" + month + "-" + day;


        try {
            db = helper.getReadableDatabase();
            if (day == 1) {
                query = "SELECT * FROM estimate WHERE issueDate='" + startOfMonth + "'";
            } else {
                query = "SELECT * FROM estimate WHERE issueDate between '" + startOfMonth + "' and '" + currentDate + "'";
            }

            cursor = db.rawQuery(query, null);
            
            Estimate estimate;
            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }
        return estimatesList;
    }

    public int getCurrentMonthEstimatesCount() {
        Cursor cursor = null;
        int currentMonthEstimatesCount = 0;
        String query;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String startDate = year + "-" + month + "-1";
        try {
            db = helper.getReadableDatabase();
            if (day == 1) {
                query = "SELECT * FROM estimate WHERE issueDate ='" + startDate + "'";
            } else {
                String highDate = year + "-" + month + "-" + day;
                query = "SELECT * FROM estimate WHERE issueDate between '" + startDate + "' and '" + highDate + "'";
            }
            cursor = db.rawQuery(query, null);
            currentMonthEstimatesCount = cursor.getCount();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }
        return currentMonthEstimatesCount;
    }

    public Float getCurrentYearEstimatesTotal() {
        Float currentYearEstimatesTotal = 0.0f;
        String query;
        Cursor cursor = null;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String startDate = year + "-1-1";
        String currentDate = year + "-" + month + "-" + day;

        try {
            db = helper.getReadableDatabase();
            if (month == 1 && day == 1) {
                query = "SELECT sum(allTaxIncludedTotal) as 'currentYearEstimatesTotal' FROM estimate WHERE issueDate ='" + currentDate + "'";
            } else {
                query = "SELECT sum(allTaxIncludedTotal) as 'currentYearEstimatesTotal' FROM estimate WHERE issueDate between '" + startDate + "' and '" + currentDate + "'";
            }
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                currentYearEstimatesTotal += cursor.getFloat(0);
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }
        return currentYearEstimatesTotal;
    }

    public ArrayList<Estimate> getCurrentYearEstimates() {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String query;
        Cursor cursor = null;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String startDate = year + "-1-1";
        String currentDate = year + "-" + month + "-" + day;
        try {
            db = helper.getReadableDatabase();
            if (month == 1 && day == 1) {
                String date = year + "-" + month + "-" + day;
                query = "SELECT * FROM estimate WHERE date ='" + date + "'";
            } else {
                query = "SELECT * FROM estimate WHERE issueDate between '" + startDate + "' and '" + currentDate + "'";
            }
            cursor = db.rawQuery(query, null);
            Estimate estimate;
            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }

        return estimatesList;
    }

    public int getCurrentYearEstimatesCount() {
        Cursor cursor = null;
        int currentYearEstimatesCount = 0;
        String query;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String startDate = year + "-1-1";
        String currentDate = year + "-" + month + "-" + day;
        try {
            if (month == 1 && day == 1) {
                query = "SELECT * FROM estimate WHERE issueDate ='" + startDate + "'";
            } else {
                query = "SELECT * FROM estimate WHERE issueDate between '" + startDate + "' and '" + currentDate + "'";
            }
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(query, null);
            currentYearEstimatesCount = cursor.getCount();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            helper.close();
        }

        return currentYearEstimatesCount;
    }

    public ArrayList<Customer> searchCustomers(String searchText) {
        ArrayList<Customer> customersList = new ArrayList<>();
        String SELECTQuery = "SELECT * FROM customer WHERE ";
        String WHEREQuery = "";
        try {
            String[] customerTableColumns = {"id", "name", "email", "tel", "mobile", "fax", "address"};
            searchText = searchText.replaceAll("^\\s+|\\s+$", "");
            if (!searchText.isEmpty()) {
                String[] searchTextArray = searchText.split(";");
                if (searchTextArray.length == 1) {
                    for (int i = 0; i < customerTableColumns.length; i++) {
                        if (WHEREQuery.isEmpty()) {
                            WHEREQuery = WHEREQuery + " " + customerTableColumns[i] + " LIKE '%" + searchTextArray[0] + "%'";
                        } else {
                            WHEREQuery = WHEREQuery + " OR " + customerTableColumns[i] + " LIKE '%" + searchTextArray[0] + "%'";
                        }
                    }
                } else {
                    for (int i = 0; i < searchTextArray.length; i++) {
                        searchTextArray[i] = searchTextArray[i].replaceAll("^\\s+|\\s+$", "");
                        searchTextArray[i] = searchTextArray[i].replace(",", ".");
                        if(!searchTextArray[i].isEmpty()){
                            for (int j = 0; j < customerTableColumns.length; j++) {
                                if (i == 0) {
                                    if (WHEREQuery.isEmpty()) {
                                        WHEREQuery = WHEREQuery + "(" + customerTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                                    } else {
                                        WHEREQuery = WHEREQuery + " OR " + customerTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                                    }
                                } else {
                                    if (WHEREQuery.charAt(WHEREQuery.length() - 1) == '(') {
                                        WHEREQuery = WHEREQuery + customerTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";

                                    } else {
                                        WHEREQuery = WHEREQuery + " OR " + customerTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                                    }
                                }
                            }
                        }
                        if (i < searchTextArray.length - 1) {
                            WHEREQuery = WHEREQuery + ") AND (";
                        } else {
                            WHEREQuery = WHEREQuery + ")";
                        }
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = SELECTQuery + WHEREQuery;
            Cursor cursor = db.rawQuery(query, null);

            Customer customer;

            while (cursor.moveToNext()) {
                Integer idCustomer = cursor.getInt(0);
                String name = cursor.getString(1);
                String email = cursor.getString(2);
                String telephone = cursor.getString(3);
                String mobile = cursor.getString(4);
                String fax = cursor.getString(5);
                String address = cursor.getString(6);
                customer = new Customer();
                customer.setId(idCustomer);
                customer.setName(name);
                customer.setEmail(email);
                customer.setTelephone(telephone);
                customer.setMobile(mobile);
                customer.setFax(fax);
                customer.setAddress(address);
                customersList.add(customer);
            }

            cursor.close();

        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        }

        return customersList;
    }

    public ArrayList<Steel> searchSteels(String searchText) {
        ArrayList<Steel> steelsList = new ArrayList<>();
        String SELECTQuery = "SELECT * FROM steel WHERE ";
        String WHEREQuery = "";
        try {
            String[] steelsTableColumns = {"id", "type", "geometricShape", "unit", "weight"};
            searchText = searchText.replaceAll("^\\s+|\\s+$", "");
            if (!searchText.isEmpty()) {
                String[] searchTextArray = searchText.split(";");
                if (searchTextArray.length == 1) {
                    for (int i = 0; i < steelsTableColumns.length; i++) {
                        if (WHEREQuery.isEmpty()) {
                            WHEREQuery = WHEREQuery + " " + steelsTableColumns[i] + " LIKE '%" + searchTextArray[0] + "%'";
                        } else {
                            WHEREQuery = WHEREQuery + " OR " + steelsTableColumns[i] + " LIKE '%" + searchTextArray[0] + "%'";
                        }
                    }
                }
                else {
                    for (int i = 0; i < searchTextArray.length; i++) {
                        searchTextArray[i] = searchTextArray[i].replaceAll("^\\s+|\\s+$", "");
                        searchTextArray[i] = searchTextArray[i].replace(",", ".");
                        if(!searchTextArray[i].isEmpty()){
                            for (int j = 0; j < steelsTableColumns.length; j++) {
                                if (i == 0) {
                                    if (WHEREQuery.isEmpty()) {
                                        WHEREQuery = WHEREQuery + "(" + steelsTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                                    } else {
                                        WHEREQuery = WHEREQuery + " OR " + steelsTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                                    }
                                } else {
                                    if (WHEREQuery.charAt(WHEREQuery.length() - 1) == '(') {
                                        WHEREQuery = WHEREQuery + steelsTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";

                                    } else {
                                        WHEREQuery = WHEREQuery + " OR " + steelsTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                                    }
                                }
                            }
                        }
                        if (i < searchTextArray.length - 1) {
                            WHEREQuery = WHEREQuery + ") AND (";
                        } else {
                            WHEREQuery = WHEREQuery + ")";
                        }
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = SELECTQuery + WHEREQuery;
            Cursor cursor = db.rawQuery(query, null);
            Steel steel;
            while (cursor.moveToNext()) {
                Integer steelId = cursor.getInt(0);
                String type = cursor.getString(1);
                String geometricShape = cursor.getString(2);
                String unit = cursor.getString(3);
                Float weight = cursor.getFloat(4);
                steel = new Steel();
                steel.setId(steelId);
                steel.setType(type);
                steel.setGeometricShape(geometricShape);
                steel.setUnit(unit);
                steel.setWeight(weight);
                steelsList.add(steel);
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        }

        return steelsList;
    }

    public ArrayList<Estimate> searchEstimates(String searchText) {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String SELECTQuery = "SELECT * FROM estimate WHERE ";
        StringBuilder WHEREQuery = new StringBuilder();

        try {
            searchText = searchText.replaceAll("^\\s+|\\s+$", "");
            String[] estimateTableColumns = {"id", "doneIn", "issueDate", "expirationDate","dueDate","dueTerms","status", "customer", "excludingTaxTotal", "discount", "excludingTaxTotalAfterDiscount", "vat", "allTaxIncludedTotal"};
            if (!searchText.isEmpty()) {
                String[] searchTextArray = searchText.split(";");
                if (searchTextArray.length == 1) {
                    for (int i = 0; i < estimateTableColumns.length; i++) {
                        if (WHEREQuery.toString().isEmpty()) {
                            WHEREQuery.append("(").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        } else {
                            WHEREQuery.append(" OR ").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        }
                    }
                    WHEREQuery.append(")");
                } else {
                    for (int i = 0; i < searchTextArray.length; i++) {
                        searchTextArray[i] = searchTextArray[i].replaceAll("^\\s+|\\s+$", "");
                        searchTextArray[i] = searchTextArray[i].replace(",", ".");
                        if(!searchTextArray[i].isEmpty()){
                            for (int j = 0; j < estimateTableColumns.length; j++) {
                                if (i == 0) {
                                    if (WHEREQuery.length() == 0) {
                                        WHEREQuery.append("(").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                } else {
                                    if (WHEREQuery.charAt(WHEREQuery.length() - 1) == '(') {
                                        WHEREQuery.append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");

                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                }
                            }
                        }
                        if (i < searchTextArray.length - 1) {
                            WHEREQuery.append(") AND (");
                        } else {
                            WHEREQuery.append(")");
                        }
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = SELECTQuery + WHEREQuery;
            query = query + " AND (status = 'Cancelled' OR status = 'Pending' OR status = 'Approved')";
            Cursor cursor = db.rawQuery(query, null);

            Estimate estimate;

            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        }
        return estimatesList;
    }

    public ArrayList<Estimate> searchCancelledEstimates(String searchText) {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String SELECTQuery = "SELECT * FROM estimate WHERE ";
        StringBuilder WHEREQuery = new StringBuilder();
        try {
            searchText = searchText.replaceAll("^\\s+|\\s+$", "");
            String[] estimateTableColumns = {"id", "doneIn", "issueDate", "expirationDate","dueDate","dueTerms","status", "customer", "excludingTaxTotal", "discount", "excludingTaxTotalAfterDiscount", "vat", "allTaxIncludedTotal"};
            if (!searchText.isEmpty()) {
                String[] searchTextArray = searchText.split(";");
                if (searchTextArray.length == 1) {
                    for (int i = 0; i < estimateTableColumns.length; i++) {
                        if (WHEREQuery.toString().isEmpty()) {
                            WHEREQuery.append("(").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        } else {
                            WHEREQuery.append(" OR ").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        }
                    }
                    WHEREQuery.append(")");
                } else {
                    for (int i = 0; i < searchTextArray.length; i++) {
                        searchTextArray[i] = searchTextArray[i].replaceAll("^\\s+|\\s+$", "");
                        searchTextArray[i] = searchTextArray[i].replace(",", ".");
                        if(!searchTextArray[i].isEmpty()){
                            for (int j = 0; j < estimateTableColumns.length; j++) {
                                if (i == 0) {
                                    if (WHEREQuery.length() == 0) {
                                        WHEREQuery.append("(").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                } else {
                                    if (WHEREQuery.charAt(WHEREQuery.length() - 1) == '(') {
                                        WHEREQuery.append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");

                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                }
                            }
                        }
                        if (i < searchTextArray.length - 1) {
                            WHEREQuery.append(") AND (");
                        } else {
                            WHEREQuery.append(")");
                        }
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = SELECTQuery + WHEREQuery;
            query = query + " AND (status = 'Cancelled')";
            Log.i(TAG, query);
            Cursor cursor = db.rawQuery(query, null);
            Estimate estimate;
            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        }

        return estimatesList;
    }

    public ArrayList<Estimate> searchApprovedEstimates(String searchText) {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String SELECTQuery = "SELECT * FROM estimate WHERE ";
        StringBuilder WHEREQuery = new StringBuilder();
        try {
            searchText = searchText.replaceAll("^\\s+|\\s+$", "");
            String[] estimateTableColumns = {"id", "doneIn", "issueDate", "expirationDate","dueDate","dueTerms","status", "customer", "excludingTaxTotal", "discount", "excludingTaxTotalAfterDiscount", "vat", "allTaxIncludedTotal"};
            if (!searchText.isEmpty()) {
                String[] searchTextArray = searchText.split(";");
                if (searchTextArray.length == 1) {
                    for (int i = 0; i < estimateTableColumns.length; i++) {
                        if (WHEREQuery.toString().isEmpty()) {
                            WHEREQuery.append("(").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        } else {
                            WHEREQuery.append(" OR ").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        }
                    }
                    WHEREQuery.append(")");
                } else {
                    for (int i = 0; i < searchTextArray.length; i++) {
                        searchTextArray[i] = searchTextArray[i].replaceAll("^\\s+|\\s+$", "");
                        searchTextArray[i] = searchTextArray[i].replace(",", ".");
                        if(!searchTextArray[i].isEmpty()){
                            for (int j = 0; j < estimateTableColumns.length; j++) {
                                if (i == 0) {
                                    if (WHEREQuery.length() == 0) {
                                        WHEREQuery.append("(").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                } else {
                                    if (WHEREQuery.charAt(WHEREQuery.length() - 1) == '(') {
                                        WHEREQuery.append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");

                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                }
                            }
                        }
                        if (i < searchTextArray.length - 1) {
                            WHEREQuery.append(") AND (");
                        } else {
                            WHEREQuery.append(")");
                        }
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = SELECTQuery + WHEREQuery;
            query = query + " AND (status = 'Approved')";
            Cursor cursor = db.rawQuery(query, null);

            Estimate estimate;

            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        }

        return estimatesList;
    }

    public ArrayList<Estimate> searchPendingEstimates(String searchText) {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String SELECTQuery = "SELECT * FROM estimate WHERE ";
        StringBuilder WHEREQuery = new StringBuilder();

        try {
            searchText = searchText.replaceAll("^\\s+|\\s+$", "");
            String[] estimateTableColumns = {"id", "doneIn", "issueDate", "expirationDate","dueDate","dueTerms","status", "customer", "excludingTaxTotal", "discount", "excludingTaxTotalAfterDiscount", "vat", "allTaxIncludedTotal"};
            if (!searchText.isEmpty()) {
                String[] searchTextArray = searchText.split(";");
                if (searchTextArray.length == 1) {
                    for (int i = 0; i < estimateTableColumns.length; i++) {
                        if (WHEREQuery.toString().isEmpty()) {
                            WHEREQuery.append("(").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        } else {
                            WHEREQuery.append(" OR ").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        }
                    }
                    WHEREQuery.append(")");
                } else {
                    for (int i = 0; i < searchTextArray.length; i++) {
                        searchTextArray[i] = searchTextArray[i].replaceAll("^\\s+|\\s+$", "");
                        searchTextArray[i] = searchTextArray[i].replace(",", ".");
                        if(!searchTextArray[i].isEmpty()){
                            for (int j = 0; j < estimateTableColumns.length; j++) {
                                if (i == 0) {
                                    if (WHEREQuery.length() == 0) {
                                        WHEREQuery.append("(").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                } else {
                                    if (WHEREQuery.charAt(WHEREQuery.length() - 1) == '(') {
                                        WHEREQuery.append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");

                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                }
                            }
                        }
                        if (i < searchTextArray.length - 1) {
                            WHEREQuery.append(") AND (");
                        } else {
                            WHEREQuery.append(")");
                        }
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = SELECTQuery + WHEREQuery;
            query = query + " AND status = 'Pending' and dueDate >= date('now')";
            Cursor cursor = db.rawQuery(query, null);

            Estimate estimate;

            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        }
        return estimatesList;
    }

    public ArrayList<Estimate> searchOverdueEstimates(String searchText) {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String SELECTQuery = "SELECT * FROM estimate WHERE ";
        StringBuilder WHEREQuery = new StringBuilder();
        try {
            searchText = searchText.replaceAll("^\\s+|\\s+$", "");
            String[] estimateTableColumns = {"id", "doneIn", "issueDate", "expirationDate","dueDate","dueTerms","status", "customer", "excludingTaxTotal", "discount", "excludingTaxTotalAfterDiscount", "vat", "allTaxIncludedTotal"};
            if (!searchText.isEmpty()) {
                String[] searchTextArray = searchText.split(";");
                if (searchTextArray.length == 1) {
                    for (int i = 0; i < estimateTableColumns.length; i++) {
                        if (WHEREQuery.toString().isEmpty()) {
                            WHEREQuery.append("(").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        } else {
                            WHEREQuery.append(" OR ").append(estimateTableColumns[i]).append(" LIKE '%").append(searchTextArray[0]).append("%'");
                        }
                    }
                    WHEREQuery.append(")");
                } else {
                    for (int i = 0; i < searchTextArray.length; i++) {
                        searchTextArray[i] = searchTextArray[i].replaceAll("^\\s+|\\s+$", "");
                        searchTextArray[i] = searchTextArray[i].replace(",", ".");
                        if(!searchTextArray[i].isEmpty()){
                            for (int j = 0; j < estimateTableColumns.length; j++) {
                                if (i == 0) {
                                    if (WHEREQuery.length() == 0) {
                                        WHEREQuery.append("(").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                } else {
                                    if (WHEREQuery.charAt(WHEREQuery.length() - 1) == '(') {
                                        WHEREQuery.append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");

                                    } else {
                                        WHEREQuery.append(" OR ").append(estimateTableColumns[j]).append(" LIKE '%").append(searchTextArray[i]).append("%'");
                                    }
                                }
                            }
                        }
                        if (i < searchTextArray.length - 1) {
                            WHEREQuery.append(") AND (");
                        } else {
                            WHEREQuery.append(")");
                        }
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = SELECTQuery + WHEREQuery;

            query = query + " dueDate < date('now') AND status = 'Pending'";
            Cursor cursor = db.rawQuery(query, null);

            Estimate estimate;

            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e(TAG, "Database error occurred", e);
        }
        return estimatesList;
    }

    public ArrayList<Customer> retrieveCustomers(){
        ArrayList<Customer> customersList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM customer",null);
            Customer customer;
            db = helper.getReadableDatabase();
            while(cursor.moveToNext()){
                Integer idCustomer = cursor.getInt(0);
                String name = cursor.getString(1);
                String email = cursor.getString(2);
                String telephone = cursor.getString(3);
                String mobile = cursor.getString(4);
                String fax = cursor.getString(5);
                String address = cursor.getString(6);
                customer = new Customer();
                customer.setId(idCustomer);
                customer.setName(name);
                customer.setEmail(email);
                customer.setTelephone(telephone);
                customer.setMobile(mobile);
                customer.setFax(fax);
                customer.setAddress(address);
                customersList.add(customer);
            }
            cursor.close();
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }

        return customersList;

    }

    public void saveCustomer(Customer customer){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("name", customer.getName());
            cv.put("email", customer.getEmail());
            cv.put("tel", customer.getTelephone());
            cv.put("mobile", customer.getMobile());
            cv.put("fax", customer.getFax());
            cv.put("address", customer.getAddress());
            db.insert("customer",null,cv);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public Integer getCustomerIdByName(String customerName){
        Integer customerId = null;
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT id FROM customer WHERE name=?" ,new String[]{customerName});
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                customerId = cursor.getInt(0);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
        return customerId;
    }
    public Customer getCustomerById(Integer customerId){
        Customer customer = new Customer();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM customer WHERE id=?",new String []{customerId.toString()});
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                customer.setId(cursor.getInt(0));
                customer.setName(cursor.getString(1));
                customer.setEmail(cursor.getString(2));
                customer.setTelephone(cursor.getString(3));
                customer.setMobile(cursor.getString(4));
                customer.setFax(cursor.getString(5));
                customer.setAddress(cursor.getString(6));
            }
            else{
                customer = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
        return customer;
    }

    public Estimate getEstimateById(Integer estimateId){
        Estimate estimate = new Estimate();
        Cursor cursor = null;
        try{
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM estimate WHERE id=?",new String []{estimateId.toString()});
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                estimate.setId(cursor.getInt(0));
                estimate.setDoneIn(cursor.getString(1));
                estimate.setIssueDate(cursor.getString(2));
                estimate.setExpirationDate(cursor.getString(3));
                estimate.setDueDate(cursor.getString(4));
                estimate.setDueTerms(cursor.getString(5));
                estimate.setStatus(cursor.getString(6));
                estimate.setCustomer(cursor.getInt(7));
                estimate.setExcludingTaxTotal(cursor.getFloat(8));
                estimate.setDiscount(cursor.getFloat(9));
                estimate.setExcludingTaxTotalAfterDiscount(cursor.getFloat(10));
                estimate.setVat(cursor.getFloat(11));
                estimate.setAllTaxIncludedTotal(cursor.getFloat(12));
            }
            else{
                estimate = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
            if(cursor != null){
                cursor.close();
            }
        }
        return estimate;
    }

    public EstimateLine getEstimateLineById(Integer estimateLineId){
        EstimateLine estimateLine = new EstimateLine();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM estimateline WHERE id=?",
                    new String []{estimateLineId.toString()});
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                estimateLine.setId(cursor.getInt(0));
                estimateLine.setEstimate(cursor.getInt(1));
                estimateLine.setSteel(cursor.getInt(2));
                estimateLine.setWeight(cursor.getFloat(3));
                estimateLine.setLength(cursor.getFloat(4));
                estimateLine.setWidth(cursor.getFloat(5));
                estimateLine.setHeight(cursor.getFloat(6));
                estimateLine.setQuantity(cursor.getLong(7));
                estimateLine.setTotal(cursor.getFloat(8));
                estimateLine.setMargin(cursor.getInt(9));
                estimateLine.setNetQuantityPlusMargin(cursor.getFloat(10));
                estimateLine.setUnitPrice(cursor.getFloat(11));
                estimateLine.setTotalPrice(cursor.getFloat(12));
            }
            else{
                estimateLine = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
        return estimateLine;
    }

    public Steel getSteelByType(String steelType){
        Steel steel = new Steel();
        Cursor cursor;
        try{
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM steel WHERE lower(type)=?",new String []{steelType.toLowerCase()});
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                steel.setId(cursor.getInt(0));
                steel.setType(cursor.getString(1));
                steel.setGeometricShape(cursor.getString(2));
                steel.setUnit(cursor.getString(3));
                steel.setWeight(cursor.getFloat(4));
            }
            else{
                steel = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
        return steel;
    }

    public Steel getSteelById(Integer steelId){
        Steel steel = new Steel();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM steel WHERE id=?",new String []{steelId.toString()});
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                steel.setId(cursor.getInt(0));
                steel.setType(cursor.getString(1));
                steel.setGeometricShape(cursor.getString(2));
                steel.setUnit(cursor.getString(3));
                steel.setWeight(cursor.getFloat(4));
            }
            else{
                steel = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }

        return steel;

    }

    public void saveSteel(Steel steel){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("type", steel.getType());
            cv.put("geometricShape", steel.getGeometricShape());
            cv.put("unit", steel.getUnit());
            cv.put("weight", steel.getWeight());
            db.insert("steel",null,cv);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void saveEstimate(Estimate estimate){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("doneIn", estimate.getDoneIn());
            cv.put("issueDate", estimate.getIssueDate());
            cv.put("dueDate", estimate.getDueDate());
            cv.put("dueTerms", estimate.getDueTerms());
            cv.put("status", estimate.getStatus());
            cv.put("expirationDate", estimate.getExpirationDate());
            cv.put("customer", estimate.getCustomer());
            cv.put("excludingTaxTotal", estimate.getExcludingTaxTotal());
            cv.put("discount", estimate.getDiscount());
            cv.put("excludingTaxTotalAfterDiscount", estimate.getExcludingTaxTotalAfterDiscount());
            cv.put("vat", estimate.getVat());
            cv.put("allTaxIncludedTotal", estimate.getAllTaxIncludedTotal());
            db.insert("estimate",null,cv);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public Float getEstimateExcludingTaxTotal(Integer estimateId){
        Float estimateExcludingTaxTotal = 0.0f;
        Cursor cursor = null;
        try{
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT sum(totalPrice) FROM estimateline inner join estimate on estimateline.estimate = estimate.id WHERE estimate.id=? group by estimate.id", new String []{estimateId.toString()});
            while(cursor.moveToNext()){
                estimateExcludingTaxTotal = cursor.getFloat(0);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }
        return estimateExcludingTaxTotal;
    }
    public void saveEstimateLine(EstimateLine estimateLine){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("estimate", estimateLine.getEstimate());
            cv.put("steel", estimateLine.getSteel());
            cv.put("weight", estimateLine.getWeight());
            cv.put("length", estimateLine.getLength());
            cv.put("width", estimateLine.getWidth());
            cv.put("height", estimateLine.getHeight());
            cv.put("quantity", estimateLine.getQuantity());
            cv.put("total", estimateLine.getTotal());
            cv.put("margin", estimateLine.getMargin());
            cv.put("quantityPlusMargin", estimateLine.getNetQuantityPlusMargin());
            cv.put("unitPrice", estimateLine.getUnitPrice());
            cv.put("totalPrice", estimateLine.getTotalPrice());
            db.insert("estimateline",null,cv);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void setSeqCustomers(){
        try{
            db = helper.getWritableDatabase();
            db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'customer'");
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void setSeqEstimateLines(){
        try{
            db = helper.getWritableDatabase();
            db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'estimateline'");
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void setSeqSteels(){
        try{
            db = helper.getWritableDatabase();
            db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'steel'");
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void setSeqEstimates(){
        try{
            db = helper.getWritableDatabase();
            db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'estimate'");
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void updateCustomer(Customer customer){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("name", customer.getName());
            cv.put("email", customer.getEmail());
            cv.put("tel", customer.getTelephone());
            cv.put("mobile", customer.getMobile());
            cv.put("fax", customer.getFax());
            cv.put("address", customer.getAddress());
            db.update("customer",cv,"id="+ customer.getId(),null);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void updateEstimate(Estimate estimate){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("doneIn", estimate.getDoneIn());
            cv.put("issueDate", estimate.getIssueDate());
            cv.put("dueDate", estimate.getDueDate());
            cv.put("dueTerms", estimate.getDueTerms());
            cv.put("status", estimate.getStatus());
            cv.put("expirationDate", estimate.getExpirationDate());
            cv.put("customer", estimate.getCustomer());
            cv.put("excludingTaxTotal", estimate.getExcludingTaxTotal());
            cv.put("discount", estimate.getDiscount());
            cv.put("excludingTaxTotalAfterDiscount", estimate.getExcludingTaxTotalAfterDiscount());
            cv.put("vat", estimate.getVat());
            cv.put("allTaxIncludedTotal", estimate.getAllTaxIncludedTotal());
            db.update("estimate",cv,"id="+ estimate.getId(),null);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void updateSteel(Steel steel){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("type", steel.getType());
            cv.put("geometricShape", steel.getGeometricShape());
            cv.put("unit", steel.getUnit());
            cv.put("weight", steel.getWeight());
            db.update("steel",cv,"id="+ steel.getId(),null);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void updateEstimateLine(EstimateLine estimateLine){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("steel", estimateLine.getSteel());
            cv.put("estimate", estimateLine.getEstimate());
            cv.put("weight", estimateLine.getWeight());
            cv.put("length", estimateLine.getLength());
            cv.put("width", estimateLine.getWidth());
            cv.put("height", estimateLine.getHeight());
            cv.put("quantity", estimateLine.getQuantity());
            cv.put("total", estimateLine.getTotal());
            cv.put("margin", estimateLine.getMargin());
            cv.put("quantityPlusMargin", estimateLine.getNetQuantityPlusMargin());
            cv.put("unitPrice", estimateLine.getUnitPrice());
            cv.put("totalPrice", estimateLine.getTotalPrice());
            db.update("estimateline", cv, "id = ?", new String[] { Integer.toString(estimateLine.getId()) } );
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public ArrayList<Estimate> retrieveEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        Cursor cursor = null;
        try{
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM estimate",null);
            Estimate estimate;
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }

        return estimatesList;
    }


    public int retrieveEstimatesLinesCount(){
        ArrayList<EstimateLine> estimatesLinesList = new ArrayList<>();
        Cursor cursor =  null;
        try{
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM estimateline",null);

            Estimate estimate;
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }

        return cursor.getCount();
    }

    public ArrayList<Estimate> retrieveCancelledEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM estimate WHERE status='Cancelled'",null);
            Estimate estimate;
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }

        return estimatesList;
    }

    public ArrayList<Estimate> retrieveOverdueEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM estimate WHERE dueDate < date('now') AND status = 'Pending'",null);
            Estimate estimate;
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }

        return estimatesList;
    }

    public ArrayList<Estimate> retrieveApprovedEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        Estimate estimate;
        Cursor cursor = null;
        try{

            db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM estimate WHERE status='Approved'",null);
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
            if(cursor == null){
                cursor.close();
            }
        }

        return estimatesList;
    }

    public ArrayList<Estimate> retrievePendingEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM estimate WHERE status = 'Pending' and dueDate >= date('now')",null);
            Estimate estimate;
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                String dueDate = cursor.getString(4);
                String dueTerms = cursor.getString(5);
                String status = cursor.getString(6);
                Integer customer = cursor.getInt(7);
                Float excludingTaxTotal = cursor.getFloat(8);
                Float discount = cursor.getFloat(9);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(10);
                Float vat = cursor.getFloat(11);
                Float allTaxIncludedTotal = cursor.getFloat(12);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setDueDate(dueDate);
                estimate.setDueTerms(dueTerms);
                estimate.setStatus(status);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        

        return estimatesList;
    }

    public ArrayList<Steel> retrieveSteels(){
        ArrayList<Steel> steelsList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM steel",null);
            Steel steel;
            while(cursor.moveToNext()){
                Integer steelId = cursor.getInt(0);
                String type = cursor.getString(1);
                String geometricShape = cursor.getString(2);
                String unit  = cursor.getString(3);
                Float weight = cursor.getFloat(4);
                steel = new Steel();
                steel.setId(steelId);
                steel.setType(type);
                steel.setGeometricShape(geometricShape);
                steel.setUnit(unit);
                steel.setWeight(weight);
                steelsList.add(steel);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
        return steelsList;
    }



    public ArrayList<EstimateLine> searchEstimateLines(Integer estimateId){
        ArrayList<EstimateLine> estimateLinesList = new ArrayList<>();
        Cursor cursor = null;
        try{
            db = helper.getReadableDatabase();
            String query = "SELECT * FROM estimateline WHERE estimate=" + estimateId.toString();
            cursor = db.rawQuery(query,null);
            EstimateLine estimateLine;
            while(cursor.moveToNext()){
                Integer estimateLineId = cursor.getInt(0);
                Integer estimate = cursor.getInt(1);
                Integer steel = cursor.getInt(2);
                Float weight  = cursor.getFloat(3);
                Float length = cursor.getFloat(4);
                Float width = cursor.getFloat(5);
                Float height = cursor.getFloat(6);
                Long quantity = cursor.getLong(7);
                Float total = cursor.getFloat(8);
                Integer margin = cursor.getInt(9);
                Float quantityPlusMargin = cursor.getFloat(10);
                Float unitPrice = cursor.getFloat(11);
                Float totalPrice = cursor.getFloat(12);

                estimateLine = new EstimateLine();

                estimateLine.setId(estimateLineId);
                estimateLine.setEstimate(estimate);
                estimateLine.setSteel(steel);
                estimateLine.setWeight(weight);
                estimateLine.setLength(length);
                estimateLine.setWidth(width);
                estimateLine.setHeight(height);
                estimateLine.setQuantity(quantity);
                estimateLine.setTotal(total);
                estimateLine.setMargin(margin);
                estimateLine.setNetQuantityPlusMargin(quantityPlusMargin);
                estimateLine.setUnitPrice(unitPrice);
                estimateLine.setTotalPrice(totalPrice);
                estimateLinesList.add(estimateLine);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
            if(cursor != null){
                cursor.close();
            }
        }
        return estimateLinesList;
    }

    public void deleteEstimateLine(Integer estimateLineId){
        try{
            db = helper.getWritableDatabase();
            db.delete("estimateline", "id=? ", new String[] { estimateLineId.toString()});
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void deleteCustomer(Integer customerId){
        try{
            db = helper.getWritableDatabase();
            db.delete("customer", "id=?",new String[] {customerId.toString()});
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        

    }

    public void deleteSteel(Integer steelId){

        try{
            db = helper.getWritableDatabase();
            db.delete("steel", "id=?",new String[] {steelId.toString()});
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public void deleteEstimate(Integer estimateId){
        try{
            db = helper.getWritableDatabase();
            db.delete("estimate", "id=?",new String[] {estimateId.toString()});
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        
    }

    public Steel findSteelByContent(String type, String geometricShape, String unit, float weight) {
        db = helper.getReadableDatabase();
        Cursor c = db.query("steel",
                null,
                "type=? AND geometricShape=? AND unit=? AND weight=?",
                new String[]{type, geometricShape, unit, String.valueOf(weight)},
                null, null, null);
        Steel steel = null;
        if (c.moveToFirst()) {
            steel = helper.buildSteelFromCursor(c);
        }
        c.close();
        return steel;
    }

    public Customer findCustomerByContent(String name, String email, String tel) {
        db = helper.getReadableDatabase();
        Cursor c = db.query("customer",
                null,
                "name=? AND email=? AND tel=?",
                new String[]{name, email, tel},
                null, null, null);
        Customer customer = null;
        if (c.moveToFirst()) {
            customer = helper.buildCustomerFromCursor(c);
        }
        c.close();
        return customer;
    }

    public Estimate findEstimateByContent(Estimate estimate) {
        db = helper.getReadableDatabase();
        String sql = "SELECT * FROM estimate WHERE doneIn = ? AND issueDate = ? AND customer = ? AND excludingTaxTotal = ?";
        String[] args = new String[]{
                estimate.getDoneIn(),
                estimate.getIssueDate(),
                String.valueOf(estimate.getCustomer()),
                String.valueOf(estimate.getExcludingTaxTotal())
        };

        Cursor cursor = db.rawQuery(sql, args);
        Estimate result = null;
        if (cursor.moveToFirst()) {
            result = helper.buildEstimateFromCursor(cursor);
        }
        cursor.close();
        return result;
    }

    public EstimateLine findEstimateLineByContent(EstimateLine line) {
        db = helper.getReadableDatabase();
        String sql = "SELECT * FROM estimateline WHERE estimate = ? AND steel = ? AND weight = ? AND length = ? AND width = ? AND height = ? AND quantity = ?";
        String[] args = new String[]{
                String.valueOf(line.getEstimate()),
                String.valueOf(line.getSteel()),
                String.valueOf(line.getWeight()),
                String.valueOf(line.getLength()),
                String.valueOf(line.getWidth()),
                String.valueOf(line.getHeight()),
                String.valueOf(line.getQuantity())
        };

        Cursor cursor = db.rawQuery(sql, args);
        EstimateLine result = null;
        if (cursor.moveToFirst()) {
            result = helper.buildEstimateLineFromCursor(cursor);
        }
        cursor.close();
        return result;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (helper != null) {
            helper.close();
        }
    }

    private Business buildBusinessFromCursor(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
        String mobile = cursor.getString(cursor.getColumnIndexOrThrow("mobile"));
        String fax = cursor.getString(cursor.getColumnIndexOrThrow("fax"));
        String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));

        return new Business(name, email, phone, mobile, fax, address);
    }

    // Get the single business from main DB
    public Business getBusiness() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM business LIMIT 1", null);
        Business business = null;
        if (cursor.moveToFirst()) {
            business = new Business();
            business.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            business.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            business.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            business.setMobile(cursor.getString(cursor.getColumnIndexOrThrow("mobile")));
            business.setFax(cursor.getString(cursor.getColumnIndexOrThrow("fax")));
            business.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        }
        cursor.close();
        return business;
    }

    // Save business
    public void saveBusiness(Business business) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", business.getName());
        values.put("email", business.getEmail());
        values.put("phone", business.getPhone());
        values.put("mobile", business.getMobile());
        values.put("fax", business.getFax());
        values.put("address", business.getAddress());
        db.insert("business", null, values);
    }

    public void updateBusiness(Business business) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", business.getName());
        values.put("email", business.getEmail());
        values.put("phone", business.getPhone());
        values.put("mobile", business.getMobile());
        values.put("fax", business.getFax());
        values.put("address", business.getAddress());

        // Update all rows (there should only be one)
        db.update("business", values, null, null);
    }

}