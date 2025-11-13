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

    public Customer getCustomerById(Integer customerId){
        Customer customer = new Customer();
        try{
            db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("select * from customer where id=?",new String []{customerId.toString()});
            if(c.getCount() == 1){
                c.moveToFirst();
                customer.setId(c.getInt(0));
                customer.setName(c.getString(1));
                customer.setEmail(c.getString(2));
                customer.setTelephone(c.getString(3));
                customer.setMobile(c.getString(4));
                customer.setFax(c.getString(5));
                customer.setAddress(c.getString(6));
            }
            else{
                customer = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
        }
        return customer;
    }

    public Estimate getEstimateById(Integer estimateId){
        Estimate estimate = new Estimate();
        Cursor c = null;
        try{
            db = helper.getReadableDatabase();
            c = db.rawQuery("select * from estimate where id=?",new String []{estimateId.toString()});
            if(c.getCount() == 1){
                c.moveToFirst();
                estimate.setId(c.getInt(0));
                estimate.setDoneIn(c.getString(1));
                estimate.setIssueDate(c.getString(2));
                estimate.setExpirationDate(c.getString(3));
                estimate.setCustomer(c.getInt(4));
                estimate.setExcludingTaxTotal(c.getFloat(5));
                estimate.setDiscount(c.getFloat(6));
                estimate.setExcludingTaxTotalAfterDiscount(c.getFloat(7));
                estimate.setVat(c.getFloat(8));
                estimate.setAllTaxIncludedTotal(c.getFloat(9));
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
            if(c != null){
                c.close();
            }
        }
        return estimate;
    }

    public EstimateLine getEstimateLineByEstimateAndSteelIds(Integer estimateId, Integer steelId){
        EstimateLine estimateLine = new EstimateLine();
        try{
            db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("select * from estimateline where estimate=? and steel=?",
                    new String []{estimateId.toString(), steelId.toString()});
            if(c.getCount() == 1){
                c.moveToFirst();
                estimateLine.setEstimate(c.getInt(0));
                estimateLine.setSteel(c.getInt(1));
                estimateLine.setWeight(c.getFloat(6));
                estimateLine.setLength(c.getFloat(7));
                estimateLine.setWidth(c.getFloat(8));
                estimateLine.setHeight(c.getFloat(9));
                estimateLine.setQuantity(c.getLong(10));
                estimateLine.setTotal(c.getFloat(11));
                estimateLine.setMargin(c.getInt(12));
                estimateLine.setNetQuantityPlusMargin(c.getFloat(13));
                estimateLine.setUnitPrice(c.getFloat(14));
                estimateLine.setTotalPrice(c.getFloat(15));
            }
            else{
                estimateLine = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
        }
        return estimateLine;
    }

    public Steel getSteelByType(String steelType){
        Steel steel = new Steel();
        Cursor c;
        try{
            db = helper.getReadableDatabase();
            c = db.rawQuery("select * from steel where lower(type)=?",new String []{steelType.toLowerCase()});
            if(c.getCount() == 1){
                c.moveToFirst();
                steel.setId(c.getInt(0));
                steel.setType(c.getString(1));
                steel.setGeometricShape(c.getString(2));
                steel.setUnit(c.getString(3));
                steel.setWeight(c.getFloat(4));
            }
            else{
                steel = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
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
            Cursor c = db.rawQuery("select * from steel where id=?",new String []{steelId.toString()});
            if(c.getCount() == 1){
                c.moveToFirst();
                steel.setId(c.getInt(0));
                steel.setType(c.getString(1));
                steel.setGeometricShape(c.getString(2));
                steel.setUnit(c.getString(3));
                steel.setWeight(c.getFloat(4));
            }
            else{
                steel = null;
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
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
            Log.e(TAG, "Database error occurred", e);
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
            db.insert("estimate",null,cv);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
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
            Log.e(TAG, "Database error occurred", e);
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
            Log.e(TAG, "Database error occurred", e);
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
            Log.e(TAG, "Database error occurred", e);
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
            Log.e(TAG, "Database error occurred", e);
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
            db.update("estimate",cv,"id="+ estimate.getId(),null);
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
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
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
        }
    }

    public void updateEstimateLine(EstimateLine estimateLine){
        try{
            db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
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
            db.update("estimateline", cv, "estimate = ? and steel = ?", new String[] { Integer.toString(estimateLine.getEstimate()), Integer.toString(estimateLine.getSteel()) } );
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
        finally{
            helper.close();
        }
    }

    public ArrayList<Estimate> retrieveEstimates(){
        ArrayList<Estimate> estimatesList = new ArrayList<>();
        try{
            db = helper.getReadableDatabase();
            Cursor c = db.rawQuery("select * from estimate",null);
            Estimate estimate;
            estimatesList.clear();
            while(c.moveToNext()){
                Integer estimateId = c.getInt(0);
                String doneIn = c.getString(1);
                String issueDate = c.getString(2);
                String expirationDate = c.getString(3);
                Integer customer = c.getInt(4);
                Float excludingTaxTotal = c.getFloat(5);
                Float discount = c.getFloat(6);
                Float excludingTaxTotalAfterDiscount = c.getFloat(7);
                Float vat = c.getFloat(8);
                Float allTaxIncludedTotal = c.getFloat(9);
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
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
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
            Cursor c = db.rawQuery("select * from estimate where isPaid = 'true'",null);
            Estimate estimate;
            estimatesList.clear();
            while(c.moveToNext()){
                Integer estimateId = c.getInt(0);
                String doneIn = c.getString(1);
                String issueDate = c.getString(2);
                String expirationDate = c.getString(3);
                Integer customer = c.getInt(4);
                Float excludingTaxTotal = c.getFloat(5);
                Float discount = c.getFloat(6);
                Float excludingTaxTotalAfterDiscount = c.getFloat(7);
                Float vat = c.getFloat(8);
                Float allTaxIncludedTotal = c.getFloat(9);
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
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
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
            Cursor c = db.rawQuery("select * from estimate where isPaid = 'false'",null);
            Estimate estimate;
            estimatesList.clear();
            while(c.moveToNext()){
                Integer estimateId = c.getInt(0);
                String doneIn = c.getString(1);
                String issueDate = c.getString(2);
                String expirationDate = c.getString(3);
                Integer customer = c.getInt(4);
                Float excludingTaxTotal = c.getFloat(5);
                Float discount = c.getFloat(6);
                Float excludingTaxTotalAfterDiscount = c.getFloat(7);
                Float vat = c.getFloat(8);
                Float allTaxIncludedTotal = c.getFloat(9);
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
                estimatesList.add(estimate);
            }
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
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
            Cursor c = db.rawQuery("select * from steel",null);
            Steel steel;
            steelsList.clear();
            while(c.moveToNext()){
                Integer steelId = c.getInt(0);
                String type = c.getString(1);
                String geometricShape = c.getString(2);
                String unit  = c.getString(3);
                Float weight = c.getFloat(4);
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
        finally{
            helper.close();
        }
        return steelsList;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (helper != null) {
            helper.close();
        }
    }

}