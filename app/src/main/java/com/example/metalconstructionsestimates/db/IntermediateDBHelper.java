package com.example.metalconstructionsestimates.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
                    + "doneIn TEXT,issueDate TEXT,expirationDate TEXT,customer INTEGER,excludingTaxTotal Float,discount float,excludingTaxTotalAfterDiscount float," +
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
}