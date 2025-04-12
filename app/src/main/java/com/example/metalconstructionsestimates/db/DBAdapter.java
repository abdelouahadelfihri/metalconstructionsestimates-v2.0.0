package com.example.metalconstructionsestimates.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DBAdapter {
    Context context;
    SQLiteDatabase db;
    DBHelper helper;
    public DBAdapter(Context context){
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

    public Float getEstimatesTotal(){
        Float estimatesTotal = 0.0f;
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

    public Float getCurrentDayEstimatesTotal(){
        Float currentDayEstimatesTotal = 0.0f;
        Cursor cursor = null;
        String date;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if(day<10){
            if(month<10){
                date = year + "-0" + month + "-0" + day;
            }
            else{
                date = year + "-" + month + "-0" + day;
            }
        }
        else{
            if(month<10){
                date = year + "-0" + month + "-" + day;
            }
            else{
                date = year + "-" + month + "-" + day;
            }
        }
        try{
            db = helper.getReadableDatabase();
            String query = "select sum(allTaxIncludedTotal) as 'currentDayEstimatesTotal' from estimate where issuedate='" + date +"'";
            cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                currentDayEstimatesTotal = cursor.getFloat(0);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }
        return currentDayEstimatesTotal;
    }

    public int getCurrentDayEstimatesCount(){
        int currentDayEstimatesCount = 0;
        Cursor cursor = null;
        String date;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date = year + "-" + month + "-" + day;
        try{
            db = helper.getReadableDatabase();
            String query = "select * from estimate where issuedate='" + date +"'";
            cursor = db.rawQuery(query, null);
            currentDayEstimatesCount = cursor.getCount();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }
        return currentDayEstimatesCount;
    }

    public ArrayList<Estimate> getCurrentDayEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String date;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date = year + "-" + month + "-" + day;
        try{
            db = helper.getReadableDatabase();
            String query = "select * from estimate where issuedate ='" + date +"'";
            Cursor cursor = db.rawQuery(query,null);
            Estimate estimate;
            estimatesList.clear();
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                Integer customer = cursor.getInt(4);
                Float excludingTaxTotal = cursor.getFloat(5);
                Float discount = cursor.getFloat(6);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                Float vat = cursor.getFloat(8);
                Float allTaxIncludedTotal = cursor.getFloat(9);
                Float amountPaid = cursor.getFloat(10);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimate.setAmountPaid(amountPaid);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
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
        switch(dayOfWeek){
            case Calendar.MONDAY:
                try {
                    db = helper.getReadableDatabase();
                    String date = year + "-" + month + "-" + day;
                    String query = "select sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' from estimate where issueDate ='" + date + "'";
                    cursor = db.rawQuery(query, null);
                    while (cursor.moveToNext()) {
                        getCurrentWeekEstimatesTotal = cursor.getFloat(0);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    helper.close();
                }
                break;
            case Calendar.TUESDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 1;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    if(cursor != null){
                        cursor.close();
                    }
                    helper.close();
                }
                break;
            case Calendar.WEDNESDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 2;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    if(cursor != null){
                        cursor.close();
                    }
                    helper.close();
                }
                break;
            case Calendar.THURSDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 3;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    if(cursor != null){
                        cursor.close();
                    }
                    helper.close();
                }
                break;
            case Calendar.FRIDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 4;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    if(cursor != null){
                        cursor.close();
                    }
                    helper.close();
                }
                break;
            case Calendar.SATURDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 5;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    if(cursor != null){
                        cursor.close();
                    }
                    helper.close();
                }
                break;
            case Calendar.SUNDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 6;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select sum(allTaxIncludedTotal) as 'getCurrentWeekEstimatesTotal' from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        getCurrentWeekEstimatesTotal += cursor.getFloat(0);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    if(cursor != null){
                        cursor.close();
                    }
                    helper.close();
                }
                break;
        }
        return getCurrentWeekEstimatesTotal;
    }

    public ArrayList<Estimate> getCurrentWeekEstimates() {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        Cursor cursor = null;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch(dayOfWeek){
            case Calendar.MONDAY:
                try{
                    db = helper.getReadableDatabase();
                    String date = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issueDate ='" + date + "'";
                    cursor = db.rawQuery(query, null);
                    Estimate estimate;
                    estimatesList.clear();
                    while(cursor.moveToNext()){
                        Integer estimateId = cursor.getInt(0);
                        String doneIn = cursor.getString(1);
                        String issueDate = cursor.getString(2);
                        String expirationDate = cursor.getString(3);
                        Integer customer = cursor.getInt(4);
                        Float excludingTaxTotal = cursor.getFloat(5);
                        Float discount = cursor.getFloat(6);
                        Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                        Float vat = cursor.getFloat(8);
                        Float allTaxIncludedTotal = cursor.getFloat(9);
                        Float amountPaid = cursor.getFloat(10);
                        estimate = new Estimate();
                        estimate.setId(estimateId);
                        estimate.setDoneIn(doneIn);
                        estimate.setIssueDate(issueDate);
                        estimate.setExpirationDate(expirationDate);
                        estimate.setCustomer(customer);
                        estimate.setExcludingTaxTotal(excludingTaxTotal);
                        estimate.setDiscount(discount);
                        estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                        estimate.setVat(vat);
                        estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        estimate.setAmountPaid(amountPaid);
                        estimatesList.add(estimate);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.TUESDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 1;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    Log.i("query", query);
                    cursor = db.rawQuery(query, null);
                    Estimate estimate;
                    estimatesList.clear();
                    while(cursor.moveToNext()){
                        Integer estimateId = cursor.getInt(0);
                        String doneIn = cursor.getString(1);
                        String issueDate = cursor.getString(2);
                        String expirationDate = cursor.getString(3);
                        Integer customer = cursor.getInt(4);
                        Float excludingTaxTotal = cursor.getFloat(5);
                        Float discount = cursor.getFloat(6);
                        Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                        Float vat = cursor.getFloat(8);
                        Float allTaxIncludedTotal = cursor.getFloat(9);
                        Float amountPaid = cursor.getFloat(10);
                        estimate = new Estimate();
                        estimate.setId(estimateId);
                        estimate.setDoneIn(doneIn);
                        estimate.setIssueDate(issueDate);
                        estimate.setExpirationDate(expirationDate);
                        estimate.setCustomer(customer);
                        estimate.setExcludingTaxTotal(excludingTaxTotal);
                        estimate.setDiscount(discount);
                        estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                        estimate.setVat(vat);
                        estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        estimate.setAmountPaid(amountPaid);
                        estimatesList.add(estimate);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.WEDNESDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 2;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    Log.i("query", query);
                    cursor = db.rawQuery(query, null);
                    Estimate estimate;
                    estimatesList.clear();
                    while(cursor.moveToNext()){
                        Integer estimateId = cursor.getInt(0);
                        String doneIn = cursor.getString(1);
                        String issueDate = cursor.getString(2);
                        String expirationDate = cursor.getString(3);
                        Integer customer = cursor.getInt(4);
                        Float excludingTaxTotal = cursor.getFloat(5);
                        Float discount = cursor.getFloat(6);
                        Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                        Float vat = cursor.getFloat(8);
                        Float allTaxIncludedTotal = cursor.getFloat(9);
                        Float amountPaid = cursor.getFloat(10);
                        estimate = new Estimate();
                        estimate.setId(estimateId);
                        estimate.setDoneIn(doneIn);
                        estimate.setIssueDate(issueDate);
                        estimate.setExpirationDate(expirationDate);
                        estimate.setCustomer(customer);
                        estimate.setExcludingTaxTotal(excludingTaxTotal);
                        estimate.setDiscount(discount);
                        estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                        estimate.setVat(vat);
                        estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        estimate.setAmountPaid(amountPaid);
                        estimatesList.add(estimate);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.THURSDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 3;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    Log.i("query", query);
                    cursor = db.rawQuery(query, null);
                    Estimate estimate;
                    estimatesList.clear();
                    while(cursor.moveToNext()){
                        Integer estimateId = cursor.getInt(0);
                        String doneIn = cursor.getString(1);
                        String issueDate = cursor.getString(2);
                        String expirationDate = cursor.getString(3);
                        Integer customer = cursor.getInt(4);
                        Float excludingTaxTotal = cursor.getFloat(5);
                        Float discount = cursor.getFloat(6);
                        Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                        Float vat = cursor.getFloat(8);
                        Float allTaxIncludedTotal = cursor.getFloat(9);
                        Float amountPaid = cursor.getFloat(10);
                        estimate = new Estimate();
                        estimate.setId(estimateId);
                        estimate.setDoneIn(doneIn);
                        estimate.setIssueDate(issueDate);
                        estimate.setExpirationDate(expirationDate);
                        estimate.setCustomer(customer);
                        estimate.setExcludingTaxTotal(excludingTaxTotal);
                        estimate.setDiscount(discount);
                        estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                        estimate.setVat(vat);
                        estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        estimate.setAmountPaid(amountPaid);
                        estimatesList.add(estimate);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }

                break;
            case Calendar.FRIDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 4;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    Log.i("query", query);
                    cursor = db.rawQuery(query, null);
                    Estimate estimate;
                    estimatesList.clear();
                    while(cursor.moveToNext()){
                        Integer estimateId = cursor.getInt(0);
                        String doneIn = cursor.getString(1);
                        String issueDate = cursor.getString(2);
                        String expirationDate = cursor.getString(3);
                        Integer customer = cursor.getInt(4);
                        Float excludingTaxTotal = cursor.getFloat(5);
                        Float discount = cursor.getFloat(6);
                        Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                        Float vat = cursor.getFloat(8);
                        Float allTaxIncludedTotal = cursor.getFloat(9);
                        Float amountPaid = cursor.getFloat(10);
                        estimate = new Estimate();
                        estimate.setId(estimateId);
                        estimate.setDoneIn(doneIn);
                        estimate.setIssueDate(issueDate);
                        estimate.setExpirationDate(expirationDate);
                        estimate.setCustomer(customer);
                        estimate.setExcludingTaxTotal(excludingTaxTotal);
                        estimate.setDiscount(discount);
                        estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                        estimate.setVat(vat);
                        estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        estimate.setAmountPaid(amountPaid);
                        estimatesList.add(estimate);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.SATURDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 5;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    Log.i("query", query);
                    cursor = db.rawQuery(query, null);
                    Estimate estimate;
                    estimatesList.clear();
                    while(cursor.moveToNext()){
                        Integer estimateId = cursor.getInt(0);
                        String doneIn = cursor.getString(1);
                        String issueDate = cursor.getString(2);
                        String expirationDate = cursor.getString(3);
                        Integer customer = cursor.getInt(4);
                        Float excludingTaxTotal = cursor.getFloat(5);
                        Float discount = cursor.getFloat(6);
                        Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                        Float vat = cursor.getFloat(8);
                        Float allTaxIncludedTotal = cursor.getFloat(9);
                        Float amountPaid = cursor.getFloat(10);
                        estimate = new Estimate();
                        estimate.setId(estimateId);
                        estimate.setDoneIn(doneIn);
                        estimate.setIssueDate(issueDate);
                        estimate.setExpirationDate(expirationDate);
                        estimate.setCustomer(customer);
                        estimate.setExcludingTaxTotal(excludingTaxTotal);
                        estimate.setDiscount(discount);
                        estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                        estimate.setVat(vat);
                        estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        estimate.setAmountPaid(amountPaid);
                        estimatesList.add(estimate);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.SUNDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 6;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    Log.i("query", query);
                    cursor = db.rawQuery(query, null);
                    Estimate estimate;
                    estimatesList.clear();
                    while(cursor.moveToNext()){
                        Integer estimateId = cursor.getInt(0);
                        String doneIn = cursor.getString(1);
                        String issueDate = cursor.getString(2);
                        String expirationDate = cursor.getString(3);
                        Integer customer = cursor.getInt(4);
                        Float excludingTaxTotal = cursor.getFloat(5);
                        Float discount = cursor.getFloat(6);
                        Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                        Float vat = cursor.getFloat(8);
                        Float allTaxIncludedTotal = cursor.getFloat(9);
                        Float amountPaid = cursor.getFloat(10);
                        estimate = new Estimate();
                        estimate.setId(estimateId);
                        estimate.setDoneIn(doneIn);
                        estimate.setIssueDate(issueDate);
                        estimate.setExpirationDate(expirationDate);
                        estimate.setCustomer(customer);
                        estimate.setExcludingTaxTotal(excludingTaxTotal);
                        estimate.setDiscount(discount);
                        estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                        estimate.setVat(vat);
                        estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                        estimate.setAmountPaid(amountPaid);
                        estimatesList.add(estimate);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    if(cursor != null){
                        cursor.close();
                    }
                    helper.close();
                }
                break;
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
        switch(dayOfWeek){
            case Calendar.MONDAY:
                try{
                    db = helper.getReadableDatabase();
                    String date = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issueDate ='" + date + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.TUESDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 1;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.WEDNESDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 2;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.THURSDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 3;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }

                break;
            case Calendar.FRIDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 4;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.SATURDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 5;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    helper.close();
                }
                break;
            case Calendar.SUNDAY:
                try{
                    db = helper.getReadableDatabase();
                    String highDate = year + "-" + month + "-" + day;
                    day = day - 6;
                    String lowDate = year + "-" + month + "-" + day;
                    String query = "select * from estimate where issuedate between '" + lowDate + "' and + '" + highDate + "'";
                    cursor = db.rawQuery(query, null);
                    currentWeekEstimatesCount = cursor.getCount();
                }
                catch(SQLException e){
                    e.printStackTrace();
                }
                finally{
                    if(cursor != null){
                        cursor.close();
                    }
                    helper.close();
                }
                break;
        }
        return currentWeekEstimatesCount;
    }


    public Float getCurrentMonthEstimatesTotal(){
        Float currentMonthEstimatesTotal = 0.0f;
        String query;
        Cursor cursor = null;
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month++;
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String startDate = year + "-" + month + "-1";
        try{
            db = helper.getReadableDatabase();
            if(day == 1){
                query = "select sum(allTaxIncludedTotal) as 'currentMonthEstimatesTotal' from estimate where issueDate ='" + startDate +"'";
            }
            else{
                String highDate = year + "-" + month + "-" + day;
                query = "select sum(allTaxIncludedTotal) as 'currentMonthEstimatesTotal' from estimate where issuedate between '" + startDate + "' and '" + highDate +"'";
            }

            cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                currentMonthEstimatesTotal += cursor.getFloat(0);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }
        return currentMonthEstimatesTotal;
    }

    public ArrayList<Estimate> getCurrentMonthEstimates(){
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


        try{
            db = helper.getReadableDatabase();
            if(day == 1){
                query = "select * from estimate where issuedate='" + startOfMonth +"'";
            }
            else{
                query = "select * from estimate where issuedate between '" + startOfMonth + "' and '" + currentDate + "'";
            }

            cursor = db.rawQuery(query, null);
            Log.i("query", query);
            Estimate estimate;
            estimatesList.clear();
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                Integer customer = cursor.getInt(4);
                Float excludingTaxTotal = cursor.getFloat(5);
                Float discount = cursor.getFloat(6);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                Float vat = cursor.getFloat(8);
                Float allTaxIncludedTotal = cursor.getFloat(9);
                Float amountPaid = cursor.getFloat(10);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimate.setAmountPaid(amountPaid);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }
        return estimatesList;
    }

    public int getCurrentMonthEstimatesCount(){
        Cursor cursor = null;
        int currentMonthEstimatesCount = 0;
        String query;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String startDate = year + "-" + month + "-1";
        try{
            db = helper.getReadableDatabase();
            if(day == 1){
                query = "select * from estimate where issueDate ='" + startDate +"'";
            }
            else{
                String highDate = year + "-" + month + "-" + day;
                query = "select * from estimate where issuedate between '" + startDate + "' and '" + highDate +"'";
            }
            cursor = db.rawQuery(query, null);
            currentMonthEstimatesCount = cursor.getCount();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }
        return currentMonthEstimatesCount;
    }

    public Float getCurrentYearEstimatesTotal(){
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

        try{
            db = helper.getReadableDatabase();
            if(month == 1 && day == 1){
                query = "select sum(allTaxIncludedTotal) as 'currentYearEstimatesTotal' from estimate where issueDate ='" + currentDate + "'";
            }
            else{
                query = "select sum(allTaxIncludedTotal) as 'currentYearEstimatesTotal' from estimate where issuedate between '" + startDate + "' and '" + currentDate + "'";
            }
            cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                currentYearEstimatesTotal += cursor.getFloat(0);
            }
            cursor.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }
        return currentYearEstimatesTotal;
    }

    public ArrayList<Estimate> getCurrentYearEstimates(){
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
        try{
            db = helper.getReadableDatabase();
            if(month == 1 && day == 1){
                String date = year + "-" + month + "-" + day;
                query = "select * from estimate where date ='" + date + "'";
            }
            else{
                query = "select * from estimate where issuedate between '" + startDate + "' and '" + currentDate + "'";
            }
            cursor = db.rawQuery(query, null);
            Estimate estimate;
            estimatesList.clear();
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                Integer customer = cursor.getInt(4);
                Float excludingTaxTotal = cursor.getFloat(5);
                Float discount = cursor.getFloat(6);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                Float vat = cursor.getFloat(8);
                Float allTaxIncludedTotal = cursor.getFloat(9);
                Float amountPaid = cursor.getFloat(10);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimate.setAmountPaid(amountPaid);
                estimatesList.add(estimate);
            }
        }

        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }

        return estimatesList;
    }

    public int getCurrentYearEstimatesCount(){
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
        try{
            if(month == 1 && day == 1){
                query = "select * from estimate where issueDate ='" + startDate + "'";
            }
            else{
                query = "select * from estimate where issuedate between '" + startDate + "' and '" + currentDate + "'";
            }
            db = helper.getReadableDatabase();
            cursor = db.rawQuery(query, null);
            currentYearEstimatesCount = cursor.getCount();
        }

        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            if(cursor != null){
                cursor.close();
            }
            helper.close();
        }

        return currentYearEstimatesCount;
    }

    public ArrayList<Customer> searchCustomers(String searchText){
        ArrayList<Customer> customersList = new ArrayList<>();
        String selectQuery = "SELECT * FROM customer WHERE ";
        String whereQuery = "";

        try{
            String [] customerTableColumns = {"id","name","email","tel","mobile","fax","address"};
            if(searchText.contains(" ")){
                String[] searchTextArray = searchText.split(" ");
                for(int i = 0; i < searchTextArray.length; i++){
                    searchTextArray[i] = searchTextArray[i].replace(",", ".");
                    for(int j = 0; j < customerTableColumns.length; j++){
                        if(i == 0){
                            if(whereQuery.equals("")){
                                whereQuery = whereQuery + "("  + customerTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            }
                            else{
                                whereQuery = whereQuery + " OR " + customerTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            }
                        }
                        else{
                            if(whereQuery.charAt(whereQuery.length() - 1) == '('){
                                whereQuery = whereQuery + customerTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";

                            }
                            else{
                                whereQuery = whereQuery + " OR " + customerTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            }
                        }
                    }
                    if(i < searchTextArray.length - 1){
                        whereQuery = whereQuery + ") AND (";
                    }
                    else{
                        whereQuery = whereQuery + ")";
                    }
                }
            }
            else{
                for(int i = 0; i < customerTableColumns.length; i++){
                    if(whereQuery == ""){
                        whereQuery = whereQuery + " " + customerTableColumns[i] + " LIKE '%" + searchText + "%'";
                    }
                    else{
                        whereQuery = whereQuery + " OR " + customerTableColumns[i] + " LIKE '%" + searchText + "%'";
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = selectQuery + whereQuery;

            Cursor cursor = db.rawQuery(query,null);

            Customer customer;

            customersList.clear();

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

            e.printStackTrace();

        }
        finally{

            helper.close();

        }

        return customersList;
    }

    public ArrayList<Steel> searchSteels(String searchText){
        ArrayList<Steel> steelsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM steel WHERE ";
        String whereQuery = "";

        try{
            String[] steelsTableColumns = {"id","type","geometricShape","unit","weight"};
            if(searchText.contains(" ")){
                String[] searchTextArray = searchText.split(" ");
                for(int i = 0; i < searchTextArray.length; i++){
                    searchTextArray[i] = searchTextArray[i].replace(",", ".");
                    for(int j = 0; j < steelsTableColumns.length; j++){
                        if(i == 0){
                            if(whereQuery.equals("")){
                                whereQuery = whereQuery + "("  + steelsTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            }
                            else{
                                whereQuery = whereQuery + " OR " + steelsTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            }
                        }
                        else{
                            if(whereQuery.charAt(whereQuery.length() - 1) == '('){
                                whereQuery = whereQuery + steelsTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";

                            }
                            else{
                                whereQuery = whereQuery + " OR " + steelsTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            }
                        }
                    }
                    if(i < searchTextArray.length - 1){
                        whereQuery = whereQuery + ") AND (";
                    }
                    else{
                        whereQuery = whereQuery + ")";
                    }
                }
            }
            else{
                for(int i = 0; i < steelsTableColumns.length; i++){
                    if(whereQuery == ""){
                        whereQuery = whereQuery + " " + steelsTableColumns[i] + " LIKE '%" + searchText + "%'";
                    }
                    else{
                        whereQuery = whereQuery + " OR " + steelsTableColumns[i] + " LIKE '%" + searchText + "%'";
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = selectQuery + whereQuery;

            Cursor cursor = db.rawQuery(query,null);

            Steel steel;

            while(cursor.moveToNext()){
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

        }
        catch(SQLException e){

            e.printStackTrace();

        }
        finally{

            helper.close();

        }

        return steelsList;
    }

    public ArrayList<Estimate> searchEstimates(String searchText) {
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM estimate WHERE ";
        String whereQuery = "";

        try {

            String[] estimateTableColumns = {"id", "doneIn", "issueDate", "expirationDate", "customer", "excludingTaxTotal", "discount", "excludingTaxTotalAfterDiscount", "vat", "allTaxIncludedTotal", "amountPaid"};

            if (searchText.contains(" ")) {
                String[] searchTextArray = searchText.split(" ");
                for (int i = 0; i < searchTextArray.length; i++) {
                    searchTextArray[i] = searchTextArray[i].replace(",", ".");
                    for (int j = 0; j < estimateTableColumns.length; j++) {
                        if (i == 0) {
                            if (whereQuery.isEmpty()) {
                                whereQuery = whereQuery + "(" + estimateTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            } else {
                                whereQuery = whereQuery + " OR " + estimateTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            }
                        } else {
                            if (whereQuery.charAt(whereQuery.length() - 1) == '(') {
                                whereQuery = whereQuery + estimateTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";

                            } else {
                                whereQuery = whereQuery + " OR " + estimateTableColumns[j] + " LIKE '%" + searchTextArray[i] + "%'";
                            }
                        }
                    }
                    if (i < searchTextArray.length - 1) {
                        whereQuery = whereQuery + ") AND (";
                    } else {
                        whereQuery = whereQuery + ")";
                    }
                }
            } else {
                for (int i = 0; i < estimateTableColumns.length; i++) {
                    if (whereQuery.isEmpty()) {
                        whereQuery = whereQuery + " " + estimateTableColumns[i] + " LIKE '%" + searchText + "%'";
                    } else {
                        whereQuery = whereQuery + " OR " + estimateTableColumns[i] + " LIKE '%" + searchText + "%'";
                    }
                }
            }

            db = helper.getReadableDatabase();

            String query = selectQuery + whereQuery;

            Cursor cursor = db.rawQuery(query, null);

            Estimate estimate;

            while (cursor.moveToNext()) {
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                Integer customer = cursor.getInt(4);
                Float excludingTaxTotal = cursor.getFloat(5);
                Float discount = cursor.getFloat(6);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                Float vat = cursor.getFloat(8);
                Float allTaxIncludedTotal = cursor.getFloat(9);
                Float amountPaid = cursor.getFloat(10);
                estimatesList.clear();
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimate.setAmountPaid(amountPaid);
                estimatesList.add(estimate);
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.close();
        }

        return estimatesList;
    }

    public ArrayList<Customer> retrieveCustomers(){
        ArrayList<Customer> customersList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from customer",null);
            Customer customer;
            customersList.clear();
            db = helper.getReadableDatabase();
            customersList.clear();
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
            e.printStackTrace();
        }
        finally{
            helper.close();
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public Integer getCustomerIdByName(String customerName){
        Integer customerId = null;
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select id from customer where name=?" ,new String[]{customerName});
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                customerId = cursor.getInt(0);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
        return customerId;
    }
    public Customer getCustomerById(Integer customerId){
        Customer customer = new Customer();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from customer where id=?",new String []{customerId.toString()});
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
        return customer;
    }

    public Estimate getEstimateById(Integer estimateId){
        Estimate estimate = new Estimate();
        Cursor cursor = null;
        try{
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from estimate where id=?",new String []{estimateId.toString()});
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                estimate.setId(cursor.getInt(0));
                estimate.setDoneIn(cursor.getString(1));
                estimate.setIssueDate(cursor.getString(2));
                estimate.setExpirationDate(cursor.getString(3));
                estimate.setCustomer(cursor.getInt(4));
                estimate.setExcludingTaxTotal(cursor.getFloat(5));
                estimate.setDiscount(cursor.getFloat(6));
                estimate.setExcludingTaxTotalAfterDiscount(cursor.getFloat(7));
                estimate.setVat(cursor.getFloat(8));
                estimate.setAllTaxIncludedTotal(cursor.getFloat(9));
                estimate.setAmountPaid(cursor.getFloat(10));
            }
            else{
                estimate = null;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
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
            Cursor cursor = db.rawQuery("select * from estimateline where id=?",
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
                estimateLine.setQuantity(cursor.getInt(7));
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
        return estimateLine;
    }

    public Steel getSteelByType(String steelType){
        Steel steel = new Steel();
        Cursor cursor;
        try{
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from steel where lower(type)=?",new String []{steelType.toLowerCase()});
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
        return steel;
    }

    public Steel getSteelById(Integer steelId){
        Steel steel = new Steel();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from steel where id=?",new String []{steelId.toString()});
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
            e.printStackTrace();
        }
        finally{
            helper.close();
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public void saveEstimate(Estimate estimate){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("doneIn", estimate.getDoneIn());
            cv.put("issueDate", estimate.getIssueDate());
            cv.put("expirationDate", estimate.getExpirationDate());
            cv.put("customer", estimate.getCustomer());
            cv.put("excludingTaxTotal", estimate.getExcludingTaxTotal());
            cv.put("discount", estimate.getDiscount());
            cv.put("excludingTaxTotalAfterDiscount", estimate.getExcludingTaxTotalAfterDiscount());
            cv.put("vat", estimate.getVat());
            cv.put("allTaxIncludedTotal", estimate.getAllTaxIncludedTotal());
            cv.put("amountPaid", estimate.getAmountPaid());
            db.insert("estimate",null,cv);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public Float getEstimateExcludingTaxTotal(Integer estimateId){
        Float estimateExcludingTaxTotal = 0.0f;
        Cursor cursor = null;
        try{
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select sum(totalPrice) from estimateline inner join estimate on estimateline.estimate = estimate.id where estimate.id=? group by estimate.id", new String []{estimateId.toString()});
            while(cursor.moveToNext()){
                estimateExcludingTaxTotal = cursor.getFloat(0);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public void setSeqCustomers(){
        try{
            db = helper.getWritableDatabase();
            db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'customer'");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public void setSeqSteels(){
        try{
            db = helper.getWritableDatabase();
            db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'steel'");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public void setSeqEstimates(){
        try{
            db = helper.getWritableDatabase();
            db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'estimate'");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public void updateEstimate(Estimate estimate){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("doneIn", estimate.getDoneIn());
            cv.put("issueDate", estimate.getIssueDate());
            cv.put("expirationDate", estimate.getExpirationDate());
            cv.put("customer", estimate.getCustomer());
            cv.put("excludingTaxTotal", estimate.getExcludingTaxTotal());
            cv.put("discount", estimate.getDiscount());
            cv.put("excludingTaxTotalAfterDiscount", estimate.getExcludingTaxTotalAfterDiscount());
            cv.put("vat", estimate.getVat());
            cv.put("allTaxIncludedTotal", estimate.getAllTaxIncludedTotal());
            cv.put("amountPaid", estimate.getAmountPaid());
            db.update("estimate",cv,"id="+ estimate.getId(),null);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
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
            e.printStackTrace();
        }
        finally{
            helper.close();
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public ArrayList<Estimate> retrieveEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from estimate",null);
            Estimate estimate;
            estimatesList.clear();
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                Integer customer = cursor.getInt(4);
                Float excludingTaxTotal = cursor.getFloat(5);
                Float discount = cursor.getFloat(6);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                Float vat = cursor.getFloat(8);
                Float allTaxIncludedTotal = cursor.getFloat(9);
                Float amountPaid = cursor.getFloat(10);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimate.setAmountPaid(amountPaid);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }

        return estimatesList;
    }

    public ArrayList<Estimate> retrievePaidEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from estimate where amountPaid = allTaxIncludedTotal",null);
            Estimate estimate;
            estimatesList.clear();
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                Integer customer = cursor.getInt(4);
                Float excludingTaxTotal = cursor.getFloat(5);
                Float discount = cursor.getFloat(6);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                Float vat = cursor.getFloat(8);
                Float allTaxIncludedTotal = cursor.getFloat(9);
                Float amountPaid = cursor.getFloat(10);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimate.setAmountPaid(amountPaid);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }

        return estimatesList;
    }

    public ArrayList<Estimate> retrieveUnpaidEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from estimate where amountPaid = 0",null);
            Estimate estimate;
            estimatesList.clear();
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                Integer customer = cursor.getInt(4);
                Float excludingTaxTotal = cursor.getFloat(5);
                Float discount = cursor.getFloat(6);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                Float vat = cursor.getFloat(8);
                Float allTaxIncludedTotal = cursor.getFloat(9);
                Float amountPaid = cursor.getFloat(10);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimate.setAmountPaid(amountPaid);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }

        return estimatesList;
    }



    public ArrayList<Estimate> retrievePartiallyPaidEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from estimate where amountPaid < allTaxIncludedTotal and amountPaid != 0",null);
            Estimate estimate;
            estimatesList.clear();
            while(cursor.moveToNext()){
                Integer estimateId = cursor.getInt(0);
                String doneIn = cursor.getString(1);
                String issueDate = cursor.getString(2);
                String expirationDate = cursor.getString(3);
                Integer customer = cursor.getInt(4);
                Float excludingTaxTotal = cursor.getFloat(5);
                Float discount = cursor.getFloat(6);
                Float excludingTaxTotalAfterDiscount = cursor.getFloat(7);
                Float vat = cursor.getFloat(8);
                Float allTaxIncludedTotal = cursor.getFloat(9);
                Float amountPaid = cursor.getFloat(10);
                estimate = new Estimate();
                estimate.setId(estimateId);
                estimate.setDoneIn(doneIn);
                estimate.setIssueDate(issueDate);
                estimate.setExpirationDate(expirationDate);
                estimate.setCustomer(customer);
                estimate.setExcludingTaxTotal(excludingTaxTotal);
                estimate.setDiscount(discount);
                estimate.setExcludingTaxTotalAfterDiscount(excludingTaxTotalAfterDiscount);
                estimate.setVat(vat);
                estimate.setAllTaxIncludedTotal(allTaxIncludedTotal);
                estimate.setAmountPaid(amountPaid);
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }

        return estimatesList;
    }

    public ArrayList<Steel> retrieveSteels(){
        ArrayList<Steel> steelsList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from steel",null);
            Steel steel;
            steelsList.clear();
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
        return steelsList;
    }



    public ArrayList<EstimateLine> searchEstimateLines(Integer estimateId){
        ArrayList<EstimateLine> estimateLinesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            String query = "select * from estimateline where estimate=" + estimateId.toString();
            Cursor cursor = db.rawQuery(query,null);
            EstimateLine estimateLine;
            estimateLinesList.clear();
            while(cursor.moveToNext()){
                Integer estimateLineId = cursor.getInt(0);
                Integer estimate = cursor.getInt(1);
                Integer steel = cursor.getInt(2);
                Float weight  = cursor.getFloat(3);
                Float length = cursor.getFloat(4);
                Float width = cursor.getFloat(5);
                Float height = cursor.getFloat(6);
                Integer quantity = cursor.getInt(7);
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
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
        return estimateLinesList;
    }

    public void deleteEstimateLine(Integer estimateLineId){
        try{
            db = helper.getWritableDatabase();
            db.delete("estimateline", "id=? ", new String[] { estimateLineId.toString()});
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public void deleteCustomer(Integer customerId){
        try{
            db = helper.getWritableDatabase();
            db.delete("customer", "id=?",new String[] {customerId.toString()});
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }

    }

    public void deleteSteel(Integer steelId){

        try{
            db = helper.getWritableDatabase();
            db.delete("steel", "id=?",new String[] {steelId.toString()});
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }

    public void deleteEstimate(Integer estimateId){
        try{
            db = helper.getWritableDatabase();
            db.delete("estimate", "id=?",new String[] {estimateId.toString()});
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally{
            helper.close();
        }
    }
}