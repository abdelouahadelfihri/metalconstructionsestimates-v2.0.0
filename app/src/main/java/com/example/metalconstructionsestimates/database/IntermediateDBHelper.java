package com.example.metalconstructionsestimates.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.metalconstructionsestimates.models.Customer;
import com.example.metalconstructionsestimates.models.Estimate;
import com.example.metalconstructionsestimates.models.EstimateLine;
import com.example.metalconstructionsestimates.models.Steel;

public class IntermediateDBHelper extends SQLiteOpenHelper {

    public IntermediateDBHelper(Context context) { super(context, "intermediateestimatesdb", null, 1); }

    private static final String TAG = "IntermediateDBHelper";

    public void onCreate(SQLiteDatabase db){
        try{
            db.execSQL("CREATE TABLE customer(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT,email TEXT,tel TEXT,mobile TEXT,fax TEXT,address TEXT)");

            db.execSQL("CREATE TABLE steel(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "type TEXT,geometricShape TEXT,unit TEXT,weight FLOAT)");

            db.execSQL("CREATE TABLE estimate(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "doneIn TEXT,issueDate TEXT,expirationDate TEXT,dueDate TEXT,dueTerms TEXT,status TEXT,customer INTEGER,excludingTaxTotal Float,discount float,excludingTaxTotalAfterDiscount float," +
                    "vat integer,allTaxIncludedTotal FLOAT,isPaid TEXT,FOREIGN KEY (customer) REFERENCES customer(id) ON DELETE CASCADE)");

            db.execSQL("CREATE TABLE estimateline(id INTEGER PRIMARY KEY AUTOINCREMENT,estimate INTEGER, steel INTEGER," +
                    "weight float,length float,width float,height float,quantity INTEGER,total Float,margin INTEGER," +
                    "quantityPlusMargin FLOAT,unitPrice FLOAT,totalPrice FLOAT," +
                    "FOREIGN KEY (steel) REFERENCES steel(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (estimate) REFERENCES estimate(id) ON DELETE CASCADE)");
        }
        catch(SQLException e){
            Log.e(TAG, "Database error occurred", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Customer buildCustomerFromCursor(Cursor cursor) {
        Customer customer = new Customer();
        customer.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        customer.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        customer.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        customer.setTelephone(cursor.getString(cursor.getColumnIndexOrThrow("tel")));
        customer.setMobile(cursor.getString(cursor.getColumnIndexOrThrow("mobile")));
        customer.setFax(cursor.getString(cursor.getColumnIndexOrThrow("fax")));
        customer.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
        return customer;
    }

    public Steel buildSteelFromCursor(Cursor cursor) {
        Steel steel = new Steel();
        steel.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        steel.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
        steel.setGeometricShape(cursor.getString(cursor.getColumnIndexOrThrow("geometricShape")));
        steel.setUnit(cursor.getString(cursor.getColumnIndexOrThrow("unit")));
        steel.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow("weight")));
        return steel;
    }

    public Estimate buildEstimateFromCursor(Cursor cursor) {
        Estimate estimate = new Estimate();
        estimate.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        estimate.setDoneIn(cursor.getString(cursor.getColumnIndexOrThrow("doneIn")));
        estimate.setIssueDate(cursor.getString(cursor.getColumnIndexOrThrow("issueDate")));
        estimate.setExpirationDate(cursor.getString(cursor.getColumnIndexOrThrow("expirationDate")));
        estimate.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow("dueDate")));
        estimate.setDueTerms(cursor.getString(cursor.getColumnIndexOrThrow("dueTerms")));
        estimate.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
        estimate.setCustomer(cursor.getInt(cursor.getColumnIndexOrThrow("customer")));
        estimate.setExcludingTaxTotal(cursor.getFloat(cursor.getColumnIndexOrThrow("excludingTaxTotal")));
        estimate.setDiscount(cursor.getFloat(cursor.getColumnIndexOrThrow("discount")));
        estimate.setExcludingTaxTotalAfterDiscount(cursor.getFloat(cursor.getColumnIndexOrThrow("excludingTaxTotalAfterDiscount")));
        estimate.setVat(cursor.getFloat(cursor.getColumnIndexOrThrow("vat")));
        estimate.setAllTaxIncludedTotal(cursor.getFloat(cursor.getColumnIndexOrThrow("allTaxIncludedTotal")));
        return estimate;
    }

    public EstimateLine buildEstimateLineFromCursor(Cursor cursor) {
        EstimateLine line = new EstimateLine();
        line.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        line.setEstimate(cursor.getInt(cursor.getColumnIndexOrThrow("estimate")));
        line.setSteel(cursor.getInt(cursor.getColumnIndexOrThrow("steel")));
        line.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow("weight")));
        line.setLength(cursor.getFloat(cursor.getColumnIndexOrThrow("length")));
        line.setWidth(cursor.getFloat(cursor.getColumnIndexOrThrow("width")));
        line.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow("height")));
        line.setQuantity(cursor.getLong(cursor.getColumnIndexOrThrow("quantity")));
        line.setTotal(cursor.getFloat(cursor.getColumnIndexOrThrow("total")));
        line.setMargin(cursor.getInt(cursor.getColumnIndexOrThrow("margin")));
        line.setNetQuantityPlusMargin(cursor.getFloat(cursor.getColumnIndexOrThrow("quantityPlusMargin")));
        line.setUnitPrice(cursor.getFloat(cursor.getColumnIndexOrThrow("unitPrice")));
        line.setTotalPrice(cursor.getFloat(cursor.getColumnIndexOrThrow("totalPrice")));
        return line;
    }

    public Cursor getAllCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM customer", null);
    }

    public Cursor getAllSteels() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM steel", null);
    }

    public Cursor getAllEstimates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM estimate", null);
    }

    public Cursor getAllEstimatesLines() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM estimateline", null);
    }
}