package com.example.metalconstructionsestimates.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    public DBHelper(Context context) { super(context, "estimatesdb", null, 1); }

    public void onCreate(SQLiteDatabase db){
        try{
            db.execSQL("CREATE TABLE customer(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT,email TEXT,tel TEXT,mobile TEXT,fax TEXT,address TEXT)");
            db.execSQL("CREATE TABLE steel(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "type TEXT,geometricShape TEXT,unit TEXT,weight FLOAT)");
            db.execSQL("INSERT INTO `steel` (`type`, `geometricShape`, `unit`, `weight`) VALUES\n" +
                    "('IPE 80', 'Profile', 'Meter', 6),\n" +
                    "('IPE 100', 'Profile', 'Meter', 8.1),\n" +
                    "('IPE 120', 'Profile', 'Meter', 10.4),\n" +
                    "('IPE 140', 'Profile', 'Meter', 12.9),\n" +
                    "('IPE 160', 'Profile', 'Meter', 15.8),\n" +
                    "('IPE 180', 'Profile', 'Meter', 18.8),\n" +
                    "('IPE 200', 'Profile', 'Meter', 22.4),\n" +
                    "('IPE 220', 'Profile', 'Meter', 26.2),\n" +
                    "('IPE 240', 'Profile', 'Meter', 30.7),\n" +
                    "('IPE 270', 'Profile', 'Meter', 36.1),\n" +
                    "('IPE 300', 'Profile', 'Meter', 42.2),\n" +
                    "('IPE 330', 'Profile', 'Meter', 49.1),\n" +
                    "('IPE 360', 'Profile', 'Meter', 57.1),\n" +
                    "('IPE 400', 'Profile', 'Meter', 66.3),\n" +
                    "('IPE 450', 'Profile', 'Meter', 77.6),\n" +
                    "('IPE 500', 'Profile', 'Meter', 90.7),\n" +
                    "('IPE 550', 'Profile', 'Meter', 106),\n" +
                    "('IPE 600', 'Profile', 'Meter', 122),\n" +
                    "('HEA 100', 'Profile', 'Meter', 16.7),\n" +
                    "('HEA 120', 'Profile', 'Meter', 19.9),\n" +
                    "('HEA 140', 'Profile', 'Meter', 24.7),\n" +
                    "('HEA 160', 'Profile', 'Meter', 30.4),\n" +
                    "('HEA 180', 'Profile', 'Meter', 35.5),\n" +
                    "('HEA 200', 'Profile', 'Meter', 42.3),\n" +
                    "('HEA 220', 'Profile', 'Meter', 50.5),\n" +
                    "('HEA 240', 'Profile', 'Meter', 60.3),\n" +
                    "('HEA 260', 'Profile', 'Meter', 68.2),\n" +
                    "('HEA 280', 'Profile', 'Meter', 76.4),\n" +
                    "('HEA 300', 'Profile', 'Meter', 88.3),\n" +
                    "('HEA 320', 'Profile', 'Meter', 97.6),\n" +
                    "('HEA 340', 'Profile', 'Meter', 105),\n" +
                    "('HEA 360', 'Profile', 'Meter', 112),\n" +
                    "('HEA 400', 'Profile', 'Meter', 125),\n" +
                    "('HEA 450', 'Profile', 'Meter', 140),\n" +
                    "('HEA 500', 'Profile', 'Meter', 155),\n" +
                    "('HEA 550', 'Profile', 'Meter', 166),\n" +
                    "('HEA 600', 'Profile', 'Meter', 178),\n" +
                    "('HEA 650', 'Profile', 'Meter', 190),\n" +
                    "('HEA 700', 'Profile', 'Meter', 204),\n" +
                    "('HEA 800', 'Profile', 'Meter', 224),\n" +
                    "('HEA 900', 'Profile', 'Meter', 252),\n" +
                    "('HEA 1000', 'Profile', 'Meter', 272),\n" +
                    "('HEB 100', 'Profile', 'Meter', 20.4),\n" +
                    "('HEB 120', 'Profile', 'Meter', 26.7),\n" +
                    "('HEB 140', 'Profile', 'Meter', 33.7),\n" +
                    "('HEB 160', 'Profile', 'Meter', 42.6),\n" +
                    "('HEB 180', 'Profile', 'Meter', 51.2),\n" +
                    "('HEB 200', 'Profile', 'Meter', 61.3),\n" +
                    "('HEB 220', 'Profile', 'Meter', 71.5),\n" +
                    "('HEB 240', 'Profile', 'Meter', 83.2),\n" +
                    "('HEB 260', 'Profile', 'Meter', 93),\n" +
                    "('HEB 280', 'Profile', 'Meter', 103),\n" +
                    "('HEB 300', 'Profile', 'Meter', 117),\n" +
                    "('HEB 320', 'Profile', 'Meter', 127),\n" +
                    "('HEB 340', 'Profile', 'Meter', 134),\n" +
                    "('HEB 360', 'Profile', 'Meter', 142),\n" +
                    "('HEB 400', 'Profile', 'Meter', 155),\n" +
                    "('HEB 450', 'Profile', 'Meter', 171),\n" +
                    "('HEB 500', 'Profile', 'Meter', 187),\n" +
                    "('HEB 550', 'Profile', 'Meter', 199),\n" +
                    "('HEB 600', 'Profile', 'Meter', 212),\n" +
                    "('HEB 650', 'Profile', 'Meter', 225),\n" +
                    "('HEB 700', 'Profile', 'Meter', 241),\n" +
                    "('HEB 800', 'Profile', 'Meter', 262),\n" +
                    "('HEB 900', 'Profile', 'Meter', 291),\n" +
                    "('HEB 1000', 'Profile', 'Meter', 314),\n" +
                    "('UPN 30x15', 'Profile', 'Meter', 1.74),\n" +
                    "('UPN 30x33', 'Profile', 'Meter', 4.27),\n" +
                    "('UPN 35x17,5', 'Profile', 'Meter', 2.16),\n" +
                    "('UPN 40x20', 'Profile', 'Meter', 2.87),\n" +
                    "('UPN 40x35', 'Profile', 'Meter', 4.87),\n" +
                    "('UPN 50x25', 'Profile', 'Meter', 3.86),\n" +
                    "('UPN 50x38', 'Profile', 'Meter', 5.59),\n" +
                    "('UPN 60', 'Profile', 'Meter', 5.07),\n" +
                    "('UPN 65', 'Profile', 'Meter', 7.09),\n" +
                    "('UPN 70', 'Profile', 'Meter', 6.77),\n" +
                    "('UPN 80', 'Profile', 'Meter', 8.64),\n" +
                    "('UPN 100', 'Profile', 'Meter', 10.6),\n" +
                    "('UPN 120', 'Profile', 'Meter', 13.4),\n" +
                    "('UPN 140', 'Profile', 'Meter', 16),\n" +
                    "('UPN 160', 'Profile', 'Meter', 18.8),\n" +
                    "('UPN 180', 'Profile', 'Meter', 22),\n" +
                    "('UPN 200', 'Profile', 'Meter', 25.3),\n" +
                    "('UPN 220', 'Profile', 'Meter', 29.4),\n" +
                    "('UPN 240', 'Profile', 'Meter', 33.2),\n" +
                    "('UPN 260', 'Profile', 'Meter', 37.9),\n" +
                    "('UPN 280', 'Profile', 'Meter', 41.8),\n" +
                    "('UPN 300', 'Profile', 'Meter', 46.2),\n" +
                    "('UAP 80', 'Profile', 'Meter', 8.38),\n" +
                    "('UAP 100', 'Profile', 'Meter', 10.5),\n" +
                    "('UAP 130', 'Profile', 'Meter', 13.7),\n" +
                    "('UAP 150', 'Profile', 'Meter', 17.9),\n" +
                    "('UAP 175', 'Profile', 'Meter', 21.2),\n" +
                    "('UAP 200', 'Profile', 'Meter', 25.1),\n" +
                    "('UAP 220', 'Profile', 'Meter', 28.5),\n" +
                    "('UAP 250', 'Profile', 'Meter', 34.4),\n" +
                    "('UAP 270', 'Profile', 'Meter', 39.4),\n" +
                    "('UAP 300', 'Profile', 'Meter', 46),\n" +
                    "('IPN 80', 'Profile', 'Meter', 5.95),\n" +
                    "('IPN 100', 'Profile', 'Meter', 8.32),\n" +
                    "('IPN 120', 'Profile', 'Meter', 11.2),\n" +
                    "('IPN 140', 'Profile', 'Meter', 14.4),\n" +
                    "('IPN 160', 'Profile', 'Meter', 17.9),\n" +
                    "('IPN 180', 'Profile', 'Meter', 21.9),\n" +
                    "('IPN 200', 'Profile', 'Meter', 26.3),\n" +
                    "('IPN 220', 'Profile', 'Meter', 31.1),\n" +
                    "('IPN 240', 'Profile', 'Meter', 36.2),\n" +
                    "('IPN 260', 'Profile', 'Meter', 41.9),\n" +
                    "('IPN 280', 'Profile', 'Meter', 48),\n" +
                    "('IPN 300', 'Profile', 'Meter', 54.2),\n" +
                    "('IPN 320', 'Profile', 'Meter', 61.1),\n" +
                    "('IPN 340', 'Profile', 'Meter', 68.1),\n" +
                    "('IPN 360', 'Profile', 'Meter', 76.2),\n" +
                    "('IPN 380', 'Profile', 'Meter', 84),\n" +
                    "('IPN 400', 'Profile', 'Meter', 92.6),\n" +
                    "('IPN 450', 'Profile', 'Meter', 115),\n" +
                    "('IPN 500', 'Profile', 'Meter', 141),\n" +
                    "('IPN 550', 'Profile', 'Meter', 167),\n" +
                    "('IPN 600', 'Profile', 'Meter', 199),\n" +
                    "('L20x20x3', 'Profile', 'Meter', 0.88),\n" +
                    "('L25x25x3', 'Profile', 'Meter', 1.12),\n" +
                    "('L30x30x3', 'Profile', 'Meter', 1.36),\n" +
                    "('L30x30x4', 'Profile', 'Meter', 1.78),\n" +
                    "('L35x35x3', 'Profile', 'Meter', 1.6),\n" +
                    "('L35x35x3,5', 'Profile', 'Meter', 1.85),\n" +
                    "('L35x35x4', 'Profile', 'Meter', 2.09),\n" +
                    "('L40x40x4', 'Profile', 'Meter', 2.42),\n" +
                    "('L40x40x4', 'Profile', 'Meter', 2.97),\n" +
                    "('L45x45x4', 'Profile', 'Meter', 2.74),\n" +
                    "('L45x45x4,5', 'Profile', 'Meter', 3.06),\n" +
                    "('L45x45x5', 'Profile', 'Meter', 3.38),\n" +
                    "('L50x50x4', 'Profile', 'Meter', 3.06),\n" +
                    "('L50x50x5', 'Profile', 'Meter', 3.77),\n" +
                    "('L50x50x6', 'Profile', 'Meter', 4.47),\n" +
                    "('L50x50x7', 'Profile', 'Meter', 5.15),\n" +
                    "('L60x60x5', 'Profile', 'Meter', 4.57),\n" +
                    "('L60x60x6', 'Profile', 'Meter', 5.42),\n" +
                    "('L60x60x8', 'Profile', 'Meter', 7.09),\n" +
                    "('L70x70x6', 'Profile', 'Meter', 6.38),\n" +
                    "('L70x70x7', 'Profile', 'Meter', 7.38),\n" +
                    "('L80x80x8', 'Profile', 'Meter', 9.63),\n" +
                    "('L90x90x8', 'Profile', 'Meter', 10.9),\n" +
                    "('L90x90x9', 'Profile', 'Meter', 12.2),\n" +
                    "('L100x100x8', 'Profile', 'Meter', 12.2),\n" +
                    "('L100x100x9', 'Profile', 'Meter', 13.6),\n" +
                    "('L100x100x10', 'Profile', 'Meter', 15),\n" +
                    "('L100x100x12', 'Profile', 'Meter', 17.8),\n" +
                    "('L120x120x11', 'Profile', 'Meter', 19.9),\n" +
                    "('L120x120x12', 'Profile', 'Meter', 21.6),\n" +
                    "('L120x120x14', 'Profile', 'Meter', 25),\n" +
                    "('L120x120x15', 'Profile', 'Meter', 26.6),\n" +
                    "('L150x150x14', 'Profile', 'Meter', 31.6),\n" +
                    "('L150x150x15', 'Profile', 'Meter', 33.8),\n" +
                    "('L150x150x18', 'Profile', 'Meter', 40.1),\n" +
                    "('L180x180x18', 'Profile', 'Meter', 48.6),\n" +
                    "('L200x200x18', 'Profile', 'Meter', 54.2),\n" +
                    "('L200x200x20', 'Profile', 'Meter', 59.9),\n" +
                    "('JL20x20x3', 'Profile', 'Meter', 4.48),\n" +
                    "('JL25x25x3', 'Profile', 'Meter', 2.24),\n" +
                    "('JL30x30x3', 'Profile', 'Meter', 2.72),\n" +
                    "('JL30x30x4', 'Profile', 'Meter', 3.56),\n" +
                    "('JL35x35x3', 'Profile', 'Meter', 3.2),\n" +
                    "('JL35x35x3,5', 'Profile', 'Meter', 3.7),\n" +
                    "('JL35x35x4', 'Profile', 'Meter', 4.18),\n" +
                    "('JL40x40x4', 'Profile', 'Meter', 4.84),\n" +
                    "('JL40x40x4', 'Profile', 'Meter', 5.94),\n" +
                    "('JL45x45x4', 'Profile', 'Meter', 5.48),\n" +
                    "('JL45x45x4,5', 'Profile', 'Meter', 6.12),\n" +
                    "('JL45x45x5', 'Profile', 'Meter', 6.76),\n" +
                    "('JL50x50x4', 'Profile', 'Meter', 6.12),\n" +
                    "('JL50x50x5', 'Profile', 'Meter', 7.54),\n" +
                    "('JL50x50x6', 'Profile', 'Meter', 8.94),\n" +
                    "('JL50x50x7', 'Profile', 'Meter', 10.3),\n" +
                    "('JL60x60x5', 'Profile', 'Meter', 9.14),\n" +
                    "('JL60x60x6', 'Profile', 'Meter', 10.84),\n" +
                    "('JL60x60x8', 'Profile', 'Meter', 14.18),\n" +
                    "('JL70x70x6', 'Profile', 'Meter', 12.76),\n" +
                    "('JL70x70x7', 'Profile', 'Meter', 14.76),\n" +
                    "('JL80x80x8', 'Profile', 'Meter', 19.26),\n" +
                    "('JL90x90x8', 'Profile', 'Meter', 21.8),\n" +
                    "('JL90x90x9', 'Profile', 'Meter', 24.4),\n" +
                    "('JL100x100x8', 'Profile', 'Meter', 24.4),\n" +
                    "('JL100x100x9', 'Profile', 'Meter', 27.2),\n" +
                    "('JL100x100x10', 'Profile', 'Meter', 30),\n" +
                    "('JL100x100x12', 'Profile', 'Meter', 35.6),\n" +
                    "('JL120x120x11', 'Profile', 'Meter', 39.8),\n" +
                    "('JL120x120x12', 'Profile', 'Meter', 43.2),\n" +
                    "('JL120x120x14', 'Profile', 'Meter', 50),\n" +
                    "('JL120x120x15', 'Profile', 'Meter', 53.2),\n" +
                    "('JL150x150x14', 'Profile', 'Meter', 63.2),\n" +
                    "('JL150x150x15', 'Profile', 'Meter', 67.6),\n" +
                    "('JL150x150x18', 'Profile', 'Meter', 80.2),\n" +
                    "('JL180x180x18', 'Profile', 'Meter', 97.2),\n" +
                    "('JL200x200x18', 'Profile', 'Meter', 108.4),\n" +
                    "('JL200x200x20', 'Profile', 'Meter', 119.8),\n" +
                    "('X20x20x3', 'Profile', 'Meter', 4.48),\n" +
                    "('X25x25x3', 'Profile', 'Meter', 2.24),\n" +
                    "('X30x30x3', 'Profile', 'Meter', 2.72),\n" +
                    "('X30x30x4', 'Profile', 'Meter', 3.56),\n" +
                    "('X35x35x3', 'Profile', 'Meter', 3.2),\n" +
                    "('X35x35x3,5', 'Profile', 'Meter', 3.7),\n" +
                    "('X35x35x4', 'Profile', 'Meter', 4.18),\n" +
                    "('X40x40x4', 'Profile', 'Meter', 4.84),\n" +
                    "('X40x40x4', 'Profile', 'Meter', 5.94),\n" +
                    "('X45x45x4', 'Profile', 'Meter', 5.48),\n" +
                    "('X45x45x4,5', 'Profile', 'Meter', 6.12),\n" +
                    "('X45x45x5', 'Profile', 'Meter', 6.76),\n" +
                    "('X50x50x4', 'Profile', 'Meter', 6.12),\n" +
                    "('X50x50x5', 'Profile', 'Meter', 7.54),\n" +
                    "('X50x50x6', 'Profile', 'Meter', 8.94),\n" +
                    "('X50x50x7', 'Profile', 'Meter', 10.3),\n" +
                    "('X60x60x5', 'Profile', 'Meter', 9.14),\n" +
                    "('X60x60x6', 'Profile', 'Meter', 10.84),\n" +
                    "('X60x60x8', 'Profile', 'Meter', 14.18),\n" +
                    "('X70x70x6', 'Profile', 'Meter', 12.76),\n" +
                    "('X70x70x7', 'Profile', 'Meter', 14.76),\n" +
                    "('X80x80x8', 'Profile', 'Meter', 19.26),\n" +
                    "('X90x90x8', 'Profile', 'Meter', 21.8),\n" +
                    "('X90x90x9', 'Profile', 'Meter', 24.4),\n" +
                    "('X100x100x8', 'Profile', 'Meter', 24.4),\n" +
                    "('X100x100x9', 'Profile', 'Meter', 27.2),\n" +
                    "('X100x100x10', 'Profile', 'Meter', 30),\n" +
                    "('X100x100x12', 'Profile', 'Meter', 35.6),\n" +
                    "('X120x120x11', 'Profile', 'Meter', 39.8),\n" +
                    "('X120x120x12', 'Profile', 'Meter', 43.2),\n" +
                    "('X120x120x14', 'Profile', 'Meter', 50),\n" +
                    "('X120x120x15', 'Profile', 'Meter', 53.2),\n" +
                    "('X150x150x14', 'Profile', 'Meter', 63.2),\n" +
                    "('X150x150x15', 'Profile', 'Meter', 67.6),\n" +
                    "('X150x150x18', 'Profile', 'Meter', 80.2),\n" +
                    "('X180x180x18', 'Profile', 'Meter', 97.2),\n" +
                    "('X200x200x18', 'Profile', 'Meter', 108.4),\n" +
                    "('X200x200x20', 'Profile', 'Meter', 119.8),\n" +
                    "('M8', 'Profile', 'Meter', 700),\n" +
                    "('M10', 'Profile', 'Meter', 1115),\n" +
                    "('M12', 'Profile', 'Meter', 1620),\n" +
                    "('M14', 'Profile', 'Meter', 2210),\n" +
                    "('M16', 'Profile', 'Meter', 3015),\n" +
                    "('M18', 'Profile', 'Meter', 3685),\n" +
                    "('M20', 'Profile', 'Meter', 4700),\n" +
                    "('M22', 'Profile', 'Meter', 5820),\n" +
                    "('M24', 'Profile', 'Meter', 6780),\n" +
                    "('M27', 'Profile', 'Meter', 8813),\n" +
                    "('M30', 'Profile', 'Meter', 10771),\n" +
                    "('M33', 'Profile', 'Meter', 13325),\n" +
                    "('M36', 'Profile', 'Meter', 15686),\n" +
                    "('M39', 'Profile', 'Meter', 18739),\n" +
                    "('M42', 'Profile', 'Meter', 21523),\n" +
                    "('M45', 'Profile', 'Meter', 25075),\n" +
                    "('M48', 'Profile', 'Meter', 28282),\n" +
                    "('M52', 'Profile', 'Meter', 33754),\n" +
                    "('M56', 'Profile', 'Meter', 38976),\n" +
                    "('HR1-8', 'Profile', 'Meter', 0),\n" +
                    "('HR1-10', 'Profile', 'Meter', 0),\n" +
                    "('HR1-12', 'Profile', 'Meter', 0),\n" +
                    "('HR1-14', 'Profile', 'Meter', 0),\n" +
                    "('HR1-16', 'Profile', 'Meter', 0),\n" +
                    "('HR1-18', 'Profile', 'Meter', 0),\n" +
                    "('HR1-20', 'Profile', 'Meter', 0),\n" +
                    "('HR1-22', 'Profile', 'Meter', 0),\n" +
                    "('HR1-24', 'Profile', 'Meter', 0),\n" +
                    "('HR1-27', 'Profile', 'Meter', 0),\n" +
                    "('HR1-30', 'Profile', 'Meter', 0),\n" +
                    "('HR1-33', 'Profile', 'Meter', 0),\n" +
                    "('HR1-36', 'Profile', 'Meter', 0),\n" +
                    "('HR1-39', 'Profile', 'Meter', 0),\n" +
                    "('HR1-42', 'Profile', 'Meter', 0),\n" +
                    "('HR1-45', 'Profile', 'Meter', 0),\n" +
                    "('HR1-48', 'Profile', 'Meter', 0),\n" +
                    "('HR1-52', 'Profile', 'Meter', 0),\n" +
                    "('HR1-56', 'Profile', 'Meter', 0),\n" +
                    "('HR2-8', 'Profile', 'Meter', 0),\n" +
                    "('HR2-10', 'Profile', 'Meter', 0),\n" +
                    "('HR2-12', 'Profile', 'Meter', 4316),\n" +
                    "('HR2-14', 'Profile', 'Meter', 5888),\n" +
                    "('HR2-16', 'Profile', 'Meter', 8038),\n" +
                    "('HR2-18', 'Profile', 'Meter', 9830),\n" +
                    "('HR2-20', 'Profile', 'Meter', 12544),\n" +
                    "('HR2-22', 'Profile', 'Meter', 15514),\n" +
                    "('HR2-24', 'Profile', 'Meter', 18074),\n" +
                    "('HR2-27', 'Profile', 'Meter', 23500),\n" +
                    "('HR2-30', 'Profile', 'Meter', 28723),\n" +
                    "('HR2-33', 'Profile', 'Meter', 35533),\n" +
                    "('HR2-36', 'Profile', 'Meter', 41830),\n" +
                    "('HR2-39', 'Profile', 'Meter', 0),\n" +
                    "('HR2-42', 'Profile', 'Meter', 0),\n" +
                    "('HR2-45', 'Profile', 'Meter', 0),\n" +
                    "('HR2-48', 'Profile', 'Meter', 0),\n" +
                    "('HR2-52', 'Profile', 'Meter', 0),\n" +
                    "('HR2-56', 'Profile', 'Meter', 0),\n" +
                    "('Rond 4', 'Profile', 'Meter', 0.099),\n" +
                    "('Rond 5', 'Profile', 'Meter', 0.154),\n" +
                    "('Rond 6', 'Profile', 'Meter', 0.222),\n" +
                    "('Rond 7', 'Profile', 'Meter', 0.302),\n" +
                    "('Rond 8', 'Profile', 'Meter', 0.395),\n" +
                    "('Rond 9', 'Profile', 'Meter', 0.499),\n" +
                    "('Rond 10', 'Profile', 'Meter', 0.617),\n" +
                    "('Rond 11', 'Profile', 'Meter', 0.746),\n" +
                    "('Rond 12', 'Profile', 'Meter', 0.888),\n" +
                    "('Rond 13', 'Profile', 'Meter', 1.042),\n" +
                    "('Rond 14', 'Profile', 'Meter', 1.208),\n" +
                    "('Rond 15', 'Profile', 'Meter', 1.387),\n" +
                    "('Rond 16', 'Profile', 'Meter', 1.578),\n" +
                    "('Rond 17', 'Profile', 'Meter', 1.782),\n" +
                    "('Rond 18', 'Profile', 'Meter', 1.998),\n" +
                    "('Rond 19', 'Profile', 'Meter', 2.226),\n" +
                    "('Rond 20', 'Profile', 'Meter', 2.466),\n" +
                    "('Rond 21', 'Profile', 'Meter', 2.719),\n" +
                    "('Rond 22', 'Profile', 'Meter', 2.984),\n" +
                    "('Rond 23', 'Profile', 'Meter', 3.261),\n" +
                    "('Rond 24', 'Profile', 'Meter', 3.551),\n" +
                    "('Rond 25', 'Profile', 'Meter', 3.853),\n" +
                    "('Rond 26', 'Profile', 'Meter', 4.168),\n" +
                    "('Rond 27', 'Profile', 'Meter', 4.495),\n" +
                    "('Rond 28', 'Profile', 'Meter', 4.834),\n" +
                    "('Rond 29', 'Profile', 'Meter', 5.185),\n" +
                    "('Rond 30', 'Profile', 'Meter', 5.549),\n" +
                    "('Rond 31', 'Profile', 'Meter', 5.925),\n" +
                    "('Rond 32', 'Profile', 'Meter', 6.313),\n" +
                    "('Rond 33', 'Profile', 'Meter', 6.714),\n" +
                    "('Rond 34', 'Profile', 'Meter', 7.127),\n" +
                    "('Rond 35', 'Profile', 'Meter', 7.553),\n" +
                    "('Rond 36', 'Profile', 'Meter', 7.99),\n" +
                    "('Rond 37', 'Profile', 'Meter', 8.44),\n" +
                    "('Rond 38', 'Profile', 'Meter', 8.903),\n" +
                    "('Rond 39', 'Profile', 'Meter', 9.378),\n" +
                    "('Rond 40', 'Profile', 'Meter', 9.865),\n" +
                    "('Rond 41', 'Profile', 'Meter', 10.364),\n" +
                    "('Rond 42', 'Profile', 'Meter', 10.876),\n" +
                    "('Rond 43', 'Profile', 'Meter', 11.4),\n" +
                    "('Rond 44', 'Profile', 'Meter', 11.936),\n" +
                    "('Rond 45', 'Profile', 'Meter', 12.485),\n" +
                    "('Rond 46', 'Profile', 'Meter', 13.046),\n" +
                    "('Rond 47', 'Profile', 'Meter', 13.619),\n" +
                    "('Rond 48', 'Profile', 'Meter', 14.205),\n" +
                    "('Rond 49', 'Profile', 'Meter', 14.803),\n" +
                    "('Rond 50', 'Profile', 'Meter', 15.413),\n" +
                    "('Rond 51', 'Profile', 'Meter', 16.036),\n" +
                    "('Rond 52', 'Profile', 'Meter', 16.671),\n" +
                    "('Rond 53', 'Profile', 'Meter', 17.319),\n" +
                    "('Rond 54', 'Profile', 'Meter', 17.978),\n" +
                    "('Rond 55', 'Profile', 'Meter', 18.65),\n" +
                    "('Rond 56', 'Profile', 'Meter', 19.335),\n" +
                    "('Rond 57', 'Profile', 'Meter', 20.031),\n" +
                    "('Rond 58', 'Profile', 'Meter', 20.74),\n" +
                    "('Rond 59', 'Profile', 'Meter', 21.462),\n" +
                    "('Rond 60', 'Profile', 'Meter', 22.195),\n" +
                    "('Rond 61', 'Profile', 'Meter', 22.941),\n" +
                    "('Rond 62', 'Profile', 'Meter', 23.7),\n" +
                    "('Rond 63', 'Profile', 'Meter', 24.47),\n" +
                    "('Rond 64', 'Profile', 'Meter', 25.253),\n" +
                    "('Rond 65', 'Profile', 'Meter', 26.049),\n" +
                    "('Rond 66', 'Profile', 'Meter', 26.856),\n" +
                    "('Rond 67', 'Profile', 'Meter', 27.676),\n" +
                    "('Rond 68', 'Profile', 'Meter', 28.509),\n" +
                    "('Rond 69', 'Profile', 'Meter', 29.353),\n" +
                    "('Rond 70', 'Profile', 'Meter', 30.21),\n" +
                    "('Rond 71', 'Profile', 'Meter', 31.08),\n" +
                    "('Rond 72', 'Profile', 'Meter', 31.961),\n" +
                    "('Rond 73', 'Profile', 'Meter', 32.855),\n" +
                    "('Rond 74', 'Profile', 'Meter', 33.762),\n" +
                    "('Rond 75', 'Profile', 'Meter', 34.68),\n" +
                    "('Rond 76', 'Profile', 'Meter', 35.611),\n" +
                    "('Rond 77', 'Profile', 'Meter', 36.555),\n" +
                    "('Rond 78', 'Profile', 'Meter', 37.51),\n" +
                    "('Rond 79', 'Profile', 'Meter', 38.478),\n" +
                    "('Rond 80', 'Profile', 'Meter', 39.458),\n" +
                    "('Rond 81', 'Profile', 'Meter', 40.451),\n" +
                    "('Rond 82', 'Profile', 'Meter', 41.456),\n" +
                    "('Rond 83', 'Profile', 'Meter', 42.473),\n" +
                    "('Rond 84', 'Profile', 'Meter', 43.503),\n" +
                    "('Rond 85', 'Profile', 'Meter', 44.545),\n" +
                    "('Rond 86', 'Profile', 'Meter', 45.599),\n" +
                    "('Rond 87', 'Profile', 'Meter', 46.666),\n" +
                    "('Rond 88', 'Profile', 'Meter', 47.745),\n" +
                    "('Rond 89', 'Profile', 'Meter', 48.836),\n" +
                    "('Rond 90', 'Profile', 'Meter', 49.94),\n" +
                    "('Rond 91', 'Profile', 'Meter', 51.055),\n" +
                    "('Rond 92', 'Profile', 'Meter', 52.184),\n" +
                    "('Rond 93', 'Profile', 'Meter', 53.324),\n" +
                    "('Rond 94', 'Profile', 'Meter', 54.477),\n" +
                    "('Rond 95', 'Profile', 'Meter', 55.643),\n" +
                    "('Rond 96', 'Profile', 'Meter', 56.82),\n" +
                    "('Rond 97', 'Profile', 'Meter', 58.01),\n" +
                    "('Rond 98', 'Profile', 'Meter', 59.212),\n" +
                    "('Rond 99', 'Profile', 'Meter', 60.427),\n" +
                    "('Rond 100', 'Profile', 'Meter', 61.654),\n" +
                    "('Rond 101', 'Profile', 'Meter', 62.893),\n" +
                    "('Rond 102', 'Profile', 'Meter', 64.145),\n" +
                    "('Rond 103', 'Profile', 'Meter', 65.408),\n" +
                    "('Rond 104', 'Profile', 'Meter', 66.685),\n" +
                    "('Rond 105', 'Profile', 'Meter', 67.973),\n" +
                    "('Rond 106', 'Profile', 'Meter', 69.274),\n" +
                    "('Rond 107', 'Profile', 'Meter', 70.587),\n" +
                    "('Rond 108', 'Profile', 'Meter', 71.913),\n" +
                    "('Rond 109', 'Profile', 'Meter', 73.251),\n" +
                    "('Rond 110', 'Profile', 'Meter', 74.601),\n" +
                    "('Rond 111', 'Profile', 'Meter', 75.964),\n" +
                    "('Rond 112', 'Profile', 'Meter', 77.338),\n" +
                    "('Rond 113', 'Profile', 'Meter', 78.726),\n" +
                    "('Rond 114', 'Profile', 'Meter', 80.125),\n" +
                    "('Rond 115', 'Profile', 'Meter', 81.537),\n" +
                    "('Rond 116', 'Profile', 'Meter', 82.961),\n" +
                    "('Rond 117', 'Profile', 'Meter', 84.398),\n" +
                    "('Rond 118', 'Profile', 'Meter', 85.847),\n" +
                    "('Rond 119', 'Profile', 'Meter', 87.308),\n" +
                    "('Rond 120', 'Profile', 'Meter', 88.781),\n" +
                    "('Rond 121', 'Profile', 'Meter', 90.267),\n" +
                    "('Rond 122', 'Profile', 'Meter', 91.765),\n" +
                    "('Rond 123', 'Profile', 'Meter', 93.276),\n" +
                    "('Rond 124', 'Profile', 'Meter', 94.799),\n" +
                    "('Rond 125', 'Profile', 'Meter', 96.334),\n" +
                    "('Rond 126', 'Profile', 'Meter', 97.882),\n" +
                    "('Rond 127', 'Profile', 'Meter', 99.441),\n" +
                    "('Rond 128', 'Profile', 'Meter', 101.014),\n" +
                    "('Rond 129', 'Profile', 'Meter', 102.598),\n" +
                    "('Rond 130', 'Profile', 'Meter', 104.195),\n" +
                    "('Rond 131', 'Profile', 'Meter', 105.804),\n" +
                    "('Rond 132', 'Profile', 'Meter', 107.426),\n" +
                    "('Rond 133', 'Profile', 'Meter', 109.059),\n" +
                    "('Rond 134', 'Profile', 'Meter', 110.705),\n" +
                    "('Rond 135', 'Profile', 'Meter', 112.364),\n" +
                    "('Rond 136', 'Profile', 'Meter', 114.035),\n" +
                    "('Rond 137', 'Profile', 'Meter', 115.718),\n" +
                    "('Rond 138', 'Profile', 'Meter', 117.413),\n" +
                    "('Rond 139', 'Profile', 'Meter', 119.121),\n" +
                    "('Rond 140', 'Profile', 'Meter', 120.841),\n" +
                    "('Rond 141', 'Profile', 'Meter', 122.574),\n" +
                    "('Rond 142', 'Profile', 'Meter', 124.319),\n" +
                    "('Rond 143', 'Profile', 'Meter', 126.076),\n" +
                    "('Rond 144', 'Profile', 'Meter', 127.845),\n" +
                    "('Rond 145', 'Profile', 'Meter', 129.627),\n" +
                    "('Rond 146', 'Profile', 'Meter', 131.421),\n" +
                    "('Rond 147', 'Profile', 'Meter', 133.228),\n" +
                    "('Rond 148', 'Profile', 'Meter', 135.046),\n" +
                    "('Rond 149', 'Profile', 'Meter', 136.878),\n" +
                    "('Rond 150', 'Profile', 'Meter', 138.721),\n" +
                    "('Rond 151', 'Profile', 'Meter', 140.577),\n" +
                    "('Rond 152', 'Profile', 'Meter', 142.445),\n" +
                    "('Rond 153', 'Profile', 'Meter', 144.325),\n" +
                    "('Rond 154', 'Profile', 'Meter', 146.218),\n" +
                    "('Rond 155', 'Profile', 'Meter', 148.123),\n" +
                    "('Rond 156', 'Profile', 'Meter', 150.041),\n" +
                    "('Rond 157', 'Profile', 'Meter', 151.97),\n" +
                    "('Rond 158', 'Profile', 'Meter', 153.912),\n" +
                    "('Rond 159', 'Profile', 'Meter', 155.867),\n" +
                    "('Rond 160', 'Profile', 'Meter', 157.834),\n" +
                    "('Rond 161', 'Profile', 'Meter', 159.813),\n" +
                    "('Rond 162', 'Profile', 'Meter', 161.804),\n" +
                    "('Rond 163', 'Profile', 'Meter', 163.808),\n" +
                    "('Rond 164', 'Profile', 'Meter', 165.824),\n" +
                    "('Rond 165', 'Profile', 'Meter', 167.852),\n" +
                    "('Rond 166', 'Profile', 'Meter', 169.893),\n" +
                    "('Rond 167', 'Profile', 'Meter', 171.946),\n" +
                    "('Rond 168', 'Profile', 'Meter', 174.012),\n" +
                    "('Rond 169', 'Profile', 'Meter', 176.089),\n" +
                    "('Rond 170', 'Profile', 'Meter', 178.179),\n" +
                    "('Rond 171', 'Profile', 'Meter', 180.282),\n" +
                    "('Rond 172', 'Profile', 'Meter', 182.396),\n" +
                    "('Rond 173', 'Profile', 'Meter', 184.524),\n" +
                    "('Rond 174', 'Profile', 'Meter', 186.663),\n" +
                    "('Rond 175', 'Profile', 'Meter', 188.815),\n" +
                    "('Rond 176', 'Profile', 'Meter', 190.979),\n" +
                    "('Rond 177', 'Profile', 'Meter', 193.155),\n" +
                    "('Rond 178', 'Profile', 'Meter', 195.344),\n" +
                    "('Rond 179', 'Profile', 'Meter', 197.545),\n" +
                    "('Rond 180', 'Profile', 'Meter', 199.758),\n" +
                    "('Rond 181', 'Profile', 'Meter', 201.984),\n" +
                    "('Rond 182', 'Profile', 'Meter', 204.222),\n" +
                    "('Rond 183', 'Profile', 'Meter', 206.472),\n" +
                    "('Rond 184', 'Profile', 'Meter', 208.735),\n" +
                    "('Rond 185', 'Profile', 'Meter', 211.01),\n" +
                    "('Rond 186', 'Profile', 'Meter', 213.297),\n" +
                    "('Rond 187', 'Profile', 'Meter', 215.597),\n" +
                    "('Rond 188', 'Profile', 'Meter', 217.909),\n" +
                    "('Rond 189', 'Profile', 'Meter', 220.233),\n" +
                    "('Rond 190', 'Profile', 'Meter', 222.57),\n" +
                    "('Rond 191', 'Profile', 'Meter', 224.919),\n" +
                    "('Rond 192', 'Profile', 'Meter', 227.28),\n" +
                    "('Rond 193', 'Profile', 'Meter', 229.654),\n" +
                    "('Rond 194', 'Profile', 'Meter', 232.04),\n" +
                    "('Rond 195', 'Profile', 'Meter', 234.438),\n" +
                    "('Rond 196', 'Profile', 'Meter', 236.849),\n" +
                    "('Rond 197', 'Profile', 'Meter', 239.272),\n" +
                    "('Rond 198', 'Profile', 'Meter', 241.707),\n" +
                    "('Rond 199', 'Profile', 'Meter', 244.155),\n" +
                    "('Rond 200', 'Profile', 'Meter', 246.615),\n" +
                    "('Rond 201', 'Profile', 'Meter', 249.087),\n" +
                    "('Rond 202', 'Profile', 'Meter', 251.572),\n" +
                    "('Rond 203', 'Profile', 'Meter', 254.069),\n" +
                    "('Rond 204', 'Profile', 'Meter', 256.578),\n" +
                    "('Rond 205', 'Profile', 'Meter', 259.1),\n" +
                    "('Rond 206', 'Profile', 'Meter', 261.634),\n" +
                    "('Rond 207', 'Profile', 'Meter', 264.18),\n" +
                    "('Rond 208', 'Profile', 'Meter', 266.739),\n" +
                    "('Rond 209', 'Profile', 'Meter', 269.31),\n" +
                    "('Rond 210', 'Profile', 'Meter', 271.893),\n" +
                    "('Rond 211', 'Profile', 'Meter', 274.489),\n" +
                    "('Rond 212', 'Profile', 'Meter', 277.097),\n" +
                    "('Rond 213', 'Profile', 'Meter', 279.717),\n" +
                    "('Rond 214', 'Profile', 'Meter', 282.35),\n" +
                    "('Rond 215', 'Profile', 'Meter', 284.994),\n" +
                    "('Rond 216', 'Profile', 'Meter', 287.652),\n" +
                    "('Rond 217', 'Profile', 'Meter', 290.321),\n" +
                    "('Rond 218', 'Profile', 'Meter', 293.003),\n" +
                    "('Rond 219', 'Profile', 'Meter', 295.698),\n" +
                    "('Rond 220', 'Profile', 'Meter', 298.404),\n" +
                    "('Rond 221', 'Profile', 'Meter', 301.123),\n" +
                    "('Rond 222', 'Profile', 'Meter', 303.854),\n" +
                    "('Rond 223', 'Profile', 'Meter', 306.598),\n" +
                    "('Rond 224', 'Profile', 'Meter', 309.354),\n" +
                    "('Rond 225', 'Profile', 'Meter', 312.122),\n" +
                    "('Rond 226', 'Profile', 'Meter', 314.903),\n" +
                    "('Rond 227', 'Profile', 'Meter', 317.696),\n" +
                    "('Rond 228', 'Profile', 'Meter', 320.501),\n" +
                    "('Rond 229', 'Profile', 'Meter', 323.318),\n" +
                    "('Rond 230', 'Profile', 'Meter', 326.148),\n" +
                    "('Rond 231', 'Profile', 'Meter', 328.991),\n" +
                    "('Rond 232', 'Profile', 'Meter', 331.845),\n" +
                    "('Rond 233', 'Profile', 'Meter', 334.712),\n" +
                    "('Rond 234', 'Profile', 'Meter', 337.591),\n" +
                    "('Rond 235', 'Profile', 'Meter', 340.483),\n" +
                    "('Rond 236', 'Profile', 'Meter', 343.387),\n" +
                    "('Rond 237', 'Profile', 'Meter', 346.303),\n" +
                    "('Rond 238', 'Profile', 'Meter', 349.232),\n" +
                    "('Rond 239', 'Profile', 'Meter', 352.172),\n" +
                    "('Rond 240', 'Profile', 'Meter', 355.126),\n" +
                    "('Rond 241', 'Profile', 'Meter', 358.091),\n" +
                    "('Rond 242', 'Profile', 'Meter', 361.069),\n" +
                    "('Rond 243', 'Profile', 'Meter', 364.059),\n" +
                    "('Rond 244', 'Profile', 'Meter', 367.062),\n" +
                    "('Rond 245', 'Profile', 'Meter', 370.077),\n" +
                    "('Rond 246', 'Profile', 'Meter', 373.104),\n" +
                    "('Rond 247', 'Profile', 'Meter', 376.143),\n" +
                    "('Rond 248', 'Profile', 'Meter', 379.195),\n" +
                    "('Rond 249', 'Profile', 'Meter', 382.259),\n" +
                    "('Rond 250', 'Profile', 'Meter', 385.336),\n" +
                    "('Rond 251', 'Profile', 'Meter', 388.425),\n" +
                    "('Rond 252', 'Profile', 'Meter', 391.526),\n" +
                    "('Rond 253', 'Profile', 'Meter', 394.64),\n" +
                    "('Rond 254', 'Profile', 'Meter', 397.765),\n" +
                    "('Rond 255', 'Profile', 'Meter', 400.904),\n" +
                    "('Rond 256', 'Profile', 'Meter', 404.054),\n" +
                    "('Rond 257', 'Profile', 'Meter', 407.217),\n" +
                    "('Rond 258', 'Profile', 'Meter', 410.392),\n" +
                    "('Rond 259', 'Profile', 'Meter', 413.58),\n" +
                    "('Rond 260', 'Profile', 'Meter', 416.779),\n" +
                    "('Rond 261', 'Profile', 'Meter', 419.992),\n" +
                    "('Rond 262', 'Profile', 'Meter', 423.216),\n" +
                    "('Rond 263', 'Profile', 'Meter', 426.453),\n" +
                    "('Rond 264', 'Profile', 'Meter', 429.702),\n" +
                    "('Rond 265', 'Profile', 'Meter', 432.964),\n" +
                    "('Rond 266', 'Profile', 'Meter', 436.237),\n" +
                    "('Rond 267', 'Profile', 'Meter', 439.523),\n" +
                    "('Rond 268', 'Profile', 'Meter', 442.822),\n" +
                    "('Rond 269', 'Profile', 'Meter', 446.133),\n" +
                    "('Rond 270', 'Profile', 'Meter', 449.456),\n" +
                    "('Rond 271', 'Profile', 'Meter', 452.791),\n" +
                    "('Rond 272', 'Profile', 'Meter', 456.139),\n" +
                    "('Rond 273', 'Profile', 'Meter', 459.499),\n" +
                    "('Rond 274', 'Profile', 'Meter', 462.872),\n" +
                    "('Rond 275', 'Profile', 'Meter', 466.257),\n" +
                    "('Rond 276', 'Profile', 'Meter', 469.654),\n" +
                    "('Rond 277', 'Profile', 'Meter', 473.063),\n" +
                    "('Rond 278', 'Profile', 'Meter', 476.485),\n" +
                    "('Rond 279', 'Profile', 'Meter', 479.919),\n" +
                    "('Rond 280', 'Profile', 'Meter', 483.365),\n" +
                    "('Rond 281', 'Profile', 'Meter', 486.824),\n" +
                    "('Rond 282', 'Profile', 'Meter', 490.295),\n" +
                    "('Rond 283', 'Profile', 'Meter', 493.779),\n" +
                    "('Rond 284', 'Profile', 'Meter', 497.275),\n" +
                    "('Rond 285', 'Profile', 'Meter', 500.783),\n" +
                    "('Rond 286', 'Profile', 'Meter', 504.303),\n" +
                    "('Rond 287', 'Profile', 'Meter', 507.836),\n" +
                    "('Rond 288', 'Profile', 'Meter', 511.381),\n" +
                    "('Rond 289', 'Profile', 'Meter', 514.938),\n" +
                    "('Rond 290', 'Profile', 'Meter', 518.508),\n" +
                    "('Rond 291', 'Profile', 'Meter', 522.09),\n" +
                    "('Rond 292', 'Profile', 'Meter', 525.685),\n" +
                    "('Rond 293', 'Profile', 'Meter', 529.291),\n" +
                    "('Rond 294', 'Profile', 'Meter', 532.91),\n" +
                    "('Rond 295', 'Profile', 'Meter', 536.542),\n" +
                    "('Rond 296', 'Profile', 'Meter', 540.186),\n" +
                    "('Rond 297', 'Profile', 'Meter', 543.842),\n" +
                    "('Rond 298', 'Profile', 'Meter', 547.51),\n" +
                    "('Rond 299', 'Profile', 'Meter', 551.191),\n" +
                    "('Rond 300', 'Profile', 'Meter', 554.884),\n" +
                    "('Rond 301', 'Profile', 'Meter', 558.589),\n" +
                    "('Rond 302', 'Profile', 'Meter', 562.307),\n" +
                    "('Rond 303', 'Profile', 'Meter', 566.037),\n" +
                    "('Rond 304', 'Profile', 'Meter', 569.779),\n" +
                    "('Rond 305', 'Profile', 'Meter', 573.534),\n" +
                    "('Rond 306', 'Profile', 'Meter', 577.301),\n" +
                    "('Rond 307', 'Profile', 'Meter', 581.08),\n" +
                    "('Rond 308', 'Profile', 'Meter', 584.872),\n" +
                    "('Rond 309', 'Profile', 'Meter', 588.676),\n" +
                    "('Rond 310', 'Profile', 'Meter', 592.493),\n" +
                    "('Rond 311', 'Profile', 'Meter', 596.321),\n" +
                    "('Rond 312', 'Profile', 'Meter', 600.162),\n" +
                    "('Rond 313', 'Profile', 'Meter', 604.016),\n" +
                    "('Rond 314', 'Profile', 'Meter', 607.881),\n" +
                    "('Rond 315', 'Profile', 'Meter', 611.759),\n" +
                    "('Rond 316', 'Profile', 'Meter', 615.65),\n" +
                    "('Rond 317', 'Profile', 'Meter', 619.552),\n" +
                    "('Rond 318', 'Profile', 'Meter', 623.467),\n" +
                    "('Rond 319', 'Profile', 'Meter', 627.395),\n" +
                    "('Rond 320', 'Profile', 'Meter', 631.334),\n" +
                    "('Rond 321', 'Profile', 'Meter', 635.286),\n" +
                    "('Rond 322', 'Profile', 'Meter', 639.251),\n" +
                    "('Rond 323', 'Profile', 'Meter', 643.227),\n" +
                    "('Rond 324', 'Profile', 'Meter', 647.216),\n" +
                    "('Rond 325', 'Profile', 'Meter', 651.218),\n" +
                    "('TCAR 80x6', 'Profile', 'Meter', 14.4),\n" +
                    "('Tube 21,3x2,3', 'Profile', 'Meter', 1.08),\n" +
                    "('Tube 26,9x2,3', 'Profile', 'Meter', 1.4),\n" +
                    "('Tube 33,7x2,6', 'Profile', 'Meter', 1.99),\n" +
                    "('Tube 42,4x2,6', 'Profile', 'Meter', 2.55),\n" +
                    "('Tube 48,3x2,9', 'Profile', 'Meter', 3.25),\n" +
                    "('Tube 48,3x3,2', 'Profile', 'Meter', 3.56),\n" +
                    "('Tube 60,3x2,9', 'Profile', 'Meter', 4.11),\n" +
                    "('Tube 60,3x5', 'Profile', 'Meter', 6.82),\n" +
                    "('Tube 76,1x2,9', 'Profile', 'Meter', 5.24),\n" +
                    "('Tube 76,1x5', 'Profile', 'Meter', 8.77),\n" +
                    "('Tube 88,9x3,2', 'Profile', 'Meter', 6.76),\n" +
                    "('Tube 101,6x3,6', 'Profile', 'Meter', 8.7),\n" +
                    "('Tube 101,6x5', 'Profile', 'Meter', 11.9),\n" +
                    "('Tube 114,3x3,6', 'Profile', 'Meter', 9.83),\n" +
                    "('Tube 114,3x6,3', 'Profile', 'Meter', 16.8),\n" +
                    "('Tube 139,7x4', 'Profile', 'Meter', 13.4),\n" +
                    "('Tube 139,7x6,3', 'Profile', 'Meter', 20.7),\n" +
                    "('Tube 168,3x4,5', 'Profile', 'Meter', 18.2),\n" +
                    "('Tube 168,3x6,3', 'Profile', 'Meter', 25.2),\n" +
                    "('Tube 193,7x3,6', 'Profile', 'Meter', 16.9),\n" +
                    "('Tube 193,7x6,3', 'Profile', 'Meter', 29.1),\n" +
                    "('Tube 219,1x3,6', 'Profile', 'Meter', 19.1),\n" +
                    "('Tube 219,1x6,3', 'Profile', 'Meter', 33.1),\n" +
                    "('Tube 244,5x6,3', 'Profile', 'Meter', 37),\n" +
                    "('Tube 244,5x8', 'Profile', 'Meter', 46.7),\n" +
                    "('Tube 273x3,6', 'Profile', 'Meter', 23.9),\n" +
                    "('Tube 273x6,3', 'Profile', 'Meter', 41.4),\n" +
                    "('Tube 323,9x4', 'Profile', 'Meter', 31.6),\n" +
                    "('Tube 323,9x6,3', 'Profile', 'Meter', 49.3),\n" +
                    "('Tube 355,6x8', 'Profile', 'Meter', 68.6),\n" +
                    "('Tube 355,6x10', 'Profile', 'Meter', 85.2),\n" +
                    "('Tube 406,4x4,5', 'Profile', 'Meter', 44.6),\n" +
                    "('Tube 406,4x6,3', 'Profile', 'Meter', 62.2),\n" +
                    "('Ω 30x60x120x60x', 'Profile', 'Meter', 7.2),\n" +
                    "('Fer plat 60x8', 'Profile', 'Meter', 3.84),\n" +
                    "('Fer plat 200x6', 'Profile', 'Meter', 1.6),\n" +
                    "('Tôle ep 10 mm', 'Profile', 'Meter', 80),\n" +
                    "('Tôle ep 5 mm', 'Profile', 'Meter', 40),\n" +
                    "('Tôle ep 20 mm', 'Profile', 'Meter', 160)");

            db.execSQL("CREATE TABLE estimate(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "doneIn TEXT,issueDate TEXT,expirationDate TEXT,dueDate TEXT,dueTerms TEXT,status TEXT,customer INTEGER,excludingTaxTotal Float,discount float,excludingTaxTotalAfterDiscount float," +
                    "vat FLOAT,allTaxIncludedTotal FLOAT,FOREIGN KEY (customer) REFERENCES customer(id) ON DELETE CASCADE)");

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
