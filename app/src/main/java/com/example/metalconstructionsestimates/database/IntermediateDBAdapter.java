package com.example.metalconstructionsestimates.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;

import java.util.ArrayList;

public class IntermediateDBAdapter {
    Context c;
    SQLiteDatabase db;
    IntermediateDBHelper helper;
    private static final String TAG = "IntermediateDBAdapter";
    public IntermediateDBAdapter(Context c){
        this.c = c;
        helper = new IntermediateDBHelper(c);
    }

    public ArrayList<Customer> retrieveCustomers(){
        ArrayList<Customer> customersList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("select * from customer",null);
            Customer customer;
            customersList.clear();
            while(c.moveToNext()){
                Integer idCustomer = c.getInt(0);
                String name = c.getString(1);
                String email = c.getString(2);
                String telephone = c.getString(3);
                String mobile = c.getString(4);
                String fax = c.getString(5);
                String address = c.getString(6);
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
            c.close();
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
        }

        return customersList;
    }

    public ArrayList<EstimateLine> retrieveEstimatesLines(){
        ArrayList<EstimateLine> estimatesLinesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("select * from estimateline",null);
            EstimateLine estimateLine;
            while(c.moveToNext()){
                Integer estimateLineId = c.getInt(0);
                Integer estimate = c.getInt(1);
                Integer steel = c.getInt(2);
                Float weight = c.getFloat(3);
                Float length = c.getFloat(4);
                Float width = c.getFloat(5);
                Float height = c.getFloat(6);
                Long quantity = c.getLong(7);
                Float total = c.getFloat(8);
                Integer margin = c.getInt(9);
                Float netQuantityPlusMargin = c.getFloat(10);
                Float unitPrice = c.getFloat(11);
                Float totalPrice = c.getFloat(12);
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
                estimateLine.setNetQuantityPlusMargin(netQuantityPlusMargin);
                estimateLine.setUnitPrice(unitPrice);
                estimateLine.setTotalPrice(totalPrice);
                estimatesLinesList.add(estimateLine);
            }
            c.close();
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
        }

        return estimatesLinesList;
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
        finally{
            helper.close();
        }
    }

    public Integer getCustomerIdByName(String customerName){
        Integer customerId = null;
        try{
            db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("select id from customer where name=?" ,new String[]{customerName});
            if(c.getCount() == 1){
                c.moveToFirst();
                customerId = c.getInt(0);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
        }
        return customerId;
    }

}