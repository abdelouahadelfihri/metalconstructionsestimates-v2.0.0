package com.example.metalconstructionsestimates.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) { super(context, "estimatesdb", null, 1); }



    public void onCreate(SQLiteDatabase db){
        try{
            db.execSQL("CREATE TABLE customer(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT,email TEXT,tel TEXT,mobile TEXT,fax TEXT,address TEXT)");
            db.execSQL("CREATE TABLE steel(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "type TEXT,geometricShape TEXT,unit TEXT,weight FLOAT)");
            db.execSQL("INSERT INTO `steel` (`type`, `geometricShape`, `unit`, `weight`) VALUES\n" +
                    "('IPE 80', 'Profile', 'Métre', 6),\n" +
                    "('IPE 100', 'Profile', 'Métre', 8.1),\n" +
                    "('IPE 120', 'Profile', 'Métre', 10.4),\n" +
                    "('IPE 140', 'Profile', 'Métre', 12.9),\n" +
                    "('IPE 160', 'Profile', 'Métre', 15.8),\n" +
                    "('IPE 180', 'Profile', 'Métre', 18.8),\n" +
                    "('IPE 200', 'Profile', 'Métre', 22.4),\n" +
                    "('IPE 220', 'Profile', 'Métre', 26.2),\n" +
                    "('IPE 240', 'Profile', 'Métre', 30.7),\n" +
                    "('IPE 270', 'Profile', 'Métre', 36.1),\n" +
                    "('IPE 300', 'Profile', 'Métre', 42.2),\n" +
                    "('IPE 330', 'Profile', 'Métre', 49.1),\n" +
                    "('IPE 360', 'Profile', 'Métre', 57.1),\n" +
                    "('IPE 400', 'Profile', 'Métre', 66.3),\n" +
                    "('IPE 450', 'Profile', 'Métre', 77.6),\n" +
                    "('IPE 500', 'Profile', 'Métre', 90.7),\n" +
                    "('IPE 550', 'Profile', 'Métre', 106),\n" +
                    "('IPE 600', 'Profile', 'Métre', 122),\n" +
                    "('HEA 100', 'Profile', 'Métre', 16.7),\n" +
                    "('HEA 120', 'Profile', 'Métre', 19.9),\n" +
                    "('HEA 140', 'Profile', 'Métre', 24.7),\n" +
                    "('HEA 160', 'Profile', 'Métre', 30.4),\n" +
                    "('HEA 180', 'Profile', 'Métre', 35.5),\n" +
                    "('HEA 200', 'Profile', 'Métre', 42.3),\n" +
                    "('HEA 220', 'Profile', 'Métre', 50.5),\n" +
                    "('HEA 240', 'Profile', 'Métre', 60.3),\n" +
                    "('HEA 260', 'Profile', 'Métre', 68.2),\n" +
                    "('HEA 280', 'Profile', 'Métre', 76.4),\n" +
                    "('HEA 300', 'Profile', 'Métre', 88.3),\n" +
                    "('HEA 320', 'Profile', 'Métre', 97.6),\n" +
                    "('HEA 340', 'Profile', 'Métre', 105),\n" +
                    "('HEA 360', 'Profile', 'Métre', 112),\n" +
                    "('HEA 400', 'Profile', 'Métre', 125),\n" +
                    "('HEA 450', 'Profile', 'Métre', 140),\n" +
                    "('HEA 500', 'Profile', 'Métre', 155),\n" +
                    "('HEA 550', 'Profile', 'Métre', 166),\n" +
                    "('HEA 600', 'Profile', 'Métre', 178),\n" +
                    "('HEA 650', 'Profile', 'Métre', 190),\n" +
                    "('HEA 700', 'Profile', 'Métre', 204),\n" +
                    "('HEA 800', 'Profile', 'Métre', 224),\n" +
                    "('HEA 900', 'Profile', 'Métre', 252),\n" +
                    "('HEA 1000', 'Profile', 'Métre', 272),\n" +
                    "('HEB 100', 'Profile', 'Métre', 20.4),\n" +
                    "('HEB 120', 'Profile', 'Métre', 26.7),\n" +
                    "('HEB 140', 'Profile', 'Métre', 33.7),\n" +
                    "('HEB 160', 'Profile', 'Métre', 42.6),\n" +
                    "('HEB 180', 'Profile', 'Métre', 51.2),\n" +
                    "('HEB 200', 'Profile', 'Métre', 61.3),\n" +
                    "('HEB 220', 'Profile', 'Métre', 71.5),\n" +
                    "('HEB 240', 'Profile', 'Métre', 83.2),\n" +
                    "('HEB 260', 'Profile', 'Métre', 93),\n" +
                    "('HEB 280', 'Profile', 'Métre', 103),\n" +
                    "('HEB 300', 'Profile', 'Métre', 117),\n" +
                    "('HEB 320', 'Profile', 'Métre', 127),\n" +
                    "('HEB 340', 'Profile', 'Métre', 134),\n" +
                    "('HEB 360', 'Profile', 'Métre', 142),\n" +
                    "('HEB 400', 'Profile', 'Métre', 155),\n" +
                    "('HEB 450', 'Profile', 'Métre', 171),\n" +
                    "('HEB 500', 'Profile', 'Métre', 187),\n" +
                    "('HEB 550', 'Profile', 'Métre', 199),\n" +
                    "('HEB 600', 'Profile', 'Métre', 212),\n" +
                    "('HEB 650', 'Profile', 'Métre', 225),\n" +
                    "('HEB 700', 'Profile', 'Métre', 241),\n" +
                    "('HEB 800', 'Profile', 'Métre', 262),\n" +
                    "('HEB 900', 'Profile', 'Métre', 291),\n" +
                    "('HEB 1000', 'Profile', 'Métre', 314),\n" +
                    "('UPN 30x15', 'Profile', 'Métre', 1.74),\n" +
                    "('UPN 30x33', 'Profile', 'Métre', 4.27),\n" +
                    "('UPN 35x17,5', 'Profile', 'Métre', 2.16),\n" +
                    "('UPN 40x20', 'Profile', 'Métre', 2.87),\n" +
                    "('UPN 40x35', 'Profile', 'Métre', 4.87),\n" +
                    "('UPN 50x25', 'Profile', 'Métre', 3.86),\n" +
                    "('UPN 50x38', 'Profile', 'Métre', 5.59),\n" +
                    "('UPN 60', 'Profile', 'Métre', 5.07),\n" +
                    "('UPN 65', 'Profile', 'Métre', 7.09),\n" +
                    "('UPN 70', 'Profile', 'Métre', 6.77),\n" +
                    "('UPN 80', 'Profile', 'Métre', 8.64),\n" +
                    "('UPN 100', 'Profile', 'Métre', 10.6),\n" +
                    "('UPN 120', 'Profile', 'Métre', 13.4),\n" +
                    "('UPN 140', 'Profile', 'Métre', 16),\n" +
                    "('UPN 160', 'Profile', 'Métre', 18.8),\n" +
                    "('UPN 180', 'Profile', 'Métre', 22),\n" +
                    "('UPN 200', 'Profile', 'Métre', 25.3),\n" +
                    "('UPN 220', 'Profile', 'Métre', 29.4),\n" +
                    "('UPN 240', 'Profile', 'Métre', 33.2),\n" +
                    "('UPN 260', 'Profile', 'Métre', 37.9),\n" +
                    "('UPN 280', 'Profile', 'Métre', 41.8),\n" +
                    "('UPN 300', 'Profile', 'Métre', 46.2),\n" +
                    "('UAP 80', 'Profile', 'Métre', 8.38),\n" +
                    "('UAP 100', 'Profile', 'Métre', 10.5),\n" +
                    "('UAP 130', 'Profile', 'Métre', 13.7),\n" +
                    "('UAP 150', 'Profile', 'Métre', 17.9),\n" +
                    "('UAP 175', 'Profile', 'Métre', 21.2),\n" +
                    "('UAP 200', 'Profile', 'Métre', 25.1),\n" +
                    "('UAP 220', 'Profile', 'Métre', 28.5),\n" +
                    "('UAP 250', 'Profile', 'Métre', 34.4),\n" +
                    "('UAP 270', 'Profile', 'Métre', 39.4),\n" +
                    "('UAP 300', 'Profile', 'Métre', 46),\n" +
                    "('IPN 80', 'Profile', 'Métre', 5.95),\n" +
                    "('IPN 100', 'Profile', 'Métre', 8.32),\n" +
                    "('IPN 120', 'Profile', 'Métre', 11.2),\n" +
                    "('IPN 140', 'Profile', 'Métre', 14.4),\n" +
                    "('IPN 160', 'Profile', 'Métre', 17.9),\n" +
                    "('IPN 180', 'Profile', 'Métre', 21.9),\n" +
                    "('IPN 200', 'Profile', 'Métre', 26.3),\n" +
                    "('IPN 220', 'Profile', 'Métre', 31.1),\n" +
                    "('IPN 240', 'Profile', 'Métre', 36.2),\n" +
                    "('IPN 260', 'Profile', 'Métre', 41.9),\n" +
                    "('IPN 280', 'Profile', 'Métre', 48),\n" +
                    "('IPN 300', 'Profile', 'Métre', 54.2),\n" +
                    "('IPN 320', 'Profile', 'Métre', 61.1),\n" +
                    "('IPN 340', 'Profile', 'Métre', 68.1),\n" +
                    "('IPN 360', 'Profile', 'Métre', 76.2),\n" +
                    "('IPN 380', 'Profile', 'Métre', 84),\n" +
                    "('IPN 400', 'Profile', 'Métre', 92.6),\n" +
                    "('IPN 450', 'Profile', 'Métre', 115),\n" +
                    "('IPN 500', 'Profile', 'Métre', 141),\n" +
                    "('IPN 550', 'Profile', 'Métre', 167),\n" +
                    "('IPN 600', 'Profile', 'Métre', 199),\n" +
                    "('L20x20x3', 'Profile', 'Métre', 0.88),\n" +
                    "('L25x25x3', 'Profile', 'Métre', 1.12),\n" +
                    "('L30x30x3', 'Profile', 'Métre', 1.36),\n" +
                    "('L30x30x4', 'Profile', 'Métre', 1.78),\n" +
                    "('L35x35x3', 'Profile', 'Métre', 1.6),\n" +
                    "('L35x35x3,5', 'Profile', 'Métre', 1.85),\n" +
                    "('L35x35x4', 'Profile', 'Métre', 2.09),\n" +
                    "('L40x40x4', 'Profile', 'Métre', 2.42),\n" +
                    "('L40x40x4', 'Profile', 'Métre', 2.97),\n" +
                    "('L45x45x4', 'Profile', 'Métre', 2.74),\n" +
                    "('L45x45x4,5', 'Profile', 'Métre', 3.06),\n" +
                    "('L45x45x5', 'Profile', 'Métre', 3.38),\n" +
                    "('L50x50x4', 'Profile', 'Métre', 3.06),\n" +
                    "('L50x50x5', 'Profile', 'Métre', 3.77),\n" +
                    "('L50x50x6', 'Profile', 'Métre', 4.47),\n" +
                    "('L50x50x7', 'Profile', 'Métre', 5.15),\n" +
                    "('L60x60x5', 'Profile', 'Métre', 4.57),\n" +
                    "('L60x60x6', 'Profile', 'Métre', 5.42),\n" +
                    "('L60x60x8', 'Profile', 'Métre', 7.09),\n" +
                    "('L70x70x6', 'Profile', 'Métre', 6.38),\n" +
                    "('L70x70x7', 'Profile', 'Métre', 7.38),\n" +
                    "('L80x80x8', 'Profile', 'Métre', 9.63),\n" +
                    "('L90x90x8', 'Profile', 'Métre', 10.9),\n" +
                    "('L90x90x9', 'Profile', 'Métre', 12.2),\n" +
                    "('L100x100x8', 'Profile', 'Métre', 12.2),\n" +
                    "('L100x100x9', 'Profile', 'Métre', 13.6),\n" +
                    "('L100x100x10', 'Profile', 'Métre', 15),\n" +
                    "('L100x100x12', 'Profile', 'Métre', 17.8),\n" +
                    "('L120x120x11', 'Profile', 'Métre', 19.9),\n" +
                    "('L120x120x12', 'Profile', 'Métre', 21.6),\n" +
                    "('L120x120x14', 'Profile', 'Métre', 25),\n" +
                    "('L120x120x15', 'Profile', 'Métre', 26.6),\n" +
                    "('L150x150x14', 'Profile', 'Métre', 31.6),\n" +
                    "('L150x150x15', 'Profile', 'Métre', 33.8),\n" +
                    "('L150x150x18', 'Profile', 'Métre', 40.1),\n" +
                    "('L180x180x18', 'Profile', 'Métre', 48.6),\n" +
                    "('L200x200x18', 'Profile', 'Métre', 54.2),\n" +
                    "('L200x200x20', 'Profile', 'Métre', 59.9),\n" +
                    "('JL20x20x3', 'Profile', 'Métre', 4.48),\n" +
                    "('JL25x25x3', 'Profile', 'Métre', 2.24),\n" +
                    "('JL30x30x3', 'Profile', 'Métre', 2.72),\n" +
                    "('JL30x30x4', 'Profile', 'Métre', 3.56),\n" +
                    "('JL35x35x3', 'Profile', 'Métre', 3.2),\n" +
                    "('JL35x35x3,5', 'Profile', 'Métre', 3.7),\n" +
                    "('JL35x35x4', 'Profile', 'Métre', 4.18),\n" +
                    "('JL40x40x4', 'Profile', 'Métre', 4.84),\n" +
                    "('JL40x40x4', 'Profile', 'Métre', 5.94),\n" +
                    "('JL45x45x4', 'Profile', 'Métre', 5.48),\n" +
                    "('JL45x45x4,5', 'Profile', 'Métre', 6.12),\n" +
                    "('JL45x45x5', 'Profile', 'Métre', 6.76),\n" +
                    "('JL50x50x4', 'Profile', 'Métre', 6.12),\n" +
                    "('JL50x50x5', 'Profile', 'Métre', 7.54),\n" +
                    "('JL50x50x6', 'Profile', 'Métre', 8.94),\n" +
                    "('JL50x50x7', 'Profile', 'Métre', 10.3),\n" +
                    "('JL60x60x5', 'Profile', 'Métre', 9.14),\n" +
                    "('JL60x60x6', 'Profile', 'Métre', 10.84),\n" +
                    "('JL60x60x8', 'Profile', 'Métre', 14.18),\n" +
                    "('JL70x70x6', 'Profile', 'Métre', 12.76),\n" +
                    "('JL70x70x7', 'Profile', 'Métre', 14.76),\n" +
                    "('JL80x80x8', 'Profile', 'Métre', 19.26),\n" +
                    "('JL90x90x8', 'Profile', 'Métre', 21.8),\n" +
                    "('JL90x90x9', 'Profile', 'Métre', 24.4),\n" +
                    "('JL100x100x8', 'Profile', 'Métre', 24.4),\n" +
                    "('JL100x100x9', 'Profile', 'Métre', 27.2),\n" +
                    "('JL100x100x10', 'Profile', 'Métre', 30),\n" +
                    "('JL100x100x12', 'Profile', 'Métre', 35.6),\n" +
                    "('JL120x120x11', 'Profile', 'Métre', 39.8),\n" +
                    "('JL120x120x12', 'Profile', 'Métre', 43.2),\n" +
                    "('JL120x120x14', 'Profile', 'Métre', 50),\n" +
                    "('JL120x120x15', 'Profile', 'Métre', 53.2),\n" +
                    "('JL150x150x14', 'Profile', 'Métre', 63.2),\n" +
                    "('JL150x150x15', 'Profile', 'Métre', 67.6),\n" +
                    "('JL150x150x18', 'Profile', 'Métre', 80.2),\n" +
                    "('JL180x180x18', 'Profile', 'Métre', 97.2),\n" +
                    "('JL200x200x18', 'Profile', 'Métre', 108.4),\n" +
                    "('JL200x200x20', 'Profile', 'Métre', 119.8),\n" +
                    "('X20x20x3', 'Profile', 'Métre', 4.48),\n" +
                    "('X25x25x3', 'Profile', 'Métre', 2.24),\n" +
                    "('X30x30x3', 'Profile', 'Métre', 2.72),\n" +
                    "('X30x30x4', 'Profile', 'Métre', 3.56),\n" +
                    "('X35x35x3', 'Profile', 'Métre', 3.2),\n" +
                    "('X35x35x3,5', 'Profile', 'Métre', 3.7),\n" +
                    "('X35x35x4', 'Profile', 'Métre', 4.18),\n" +
                    "('X40x40x4', 'Profile', 'Métre', 4.84),\n" +
                    "('X40x40x4', 'Profile', 'Métre', 5.94),\n" +
                    "('X45x45x4', 'Profile', 'Métre', 5.48),\n" +
                    "('X45x45x4,5', 'Profile', 'Métre', 6.12),\n" +
                    "('X45x45x5', 'Profile', 'Métre', 6.76),\n" +
                    "('X50x50x4', 'Profile', 'Métre', 6.12),\n" +
                    "('X50x50x5', 'Profile', 'Métre', 7.54),\n" +
                    "('X50x50x6', 'Profile', 'Métre', 8.94),\n" +
                    "('X50x50x7', 'Profile', 'Métre', 10.3),\n" +
                    "('X60x60x5', 'Profile', 'Métre', 9.14),\n" +
                    "('X60x60x6', 'Profile', 'Métre', 10.84),\n" +
                    "('X60x60x8', 'Profile', 'Métre', 14.18),\n" +
                    "('X70x70x6', 'Profile', 'Métre', 12.76),\n" +
                    "('X70x70x7', 'Profile', 'Métre', 14.76),\n" +
                    "('X80x80x8', 'Profile', 'Métre', 19.26),\n" +
                    "('X90x90x8', 'Profile', 'Métre', 21.8),\n" +
                    "('X90x90x9', 'Profile', 'Métre', 24.4),\n" +
                    "('X100x100x8', 'Profile', 'Métre', 24.4),\n" +
                    "('X100x100x9', 'Profile', 'Métre', 27.2),\n" +
                    "('X100x100x10', 'Profile', 'Métre', 30),\n" +
                    "('X100x100x12', 'Profile', 'Métre', 35.6),\n" +
                    "('X120x120x11', 'Profile', 'Métre', 39.8),\n" +
                    "('X120x120x12', 'Profile', 'Métre', 43.2),\n" +
                    "('X120x120x14', 'Profile', 'Métre', 50),\n" +
                    "('X120x120x15', 'Profile', 'Métre', 53.2),\n" +
                    "('X150x150x14', 'Profile', 'Métre', 63.2),\n" +
                    "('X150x150x15', 'Profile', 'Métre', 67.6),\n" +
                    "('X150x150x18', 'Profile', 'Métre', 80.2),\n" +
                    "('X180x180x18', 'Profile', 'Métre', 97.2),\n" +
                    "('X200x200x18', 'Profile', 'Métre', 108.4),\n" +
                    "('X200x200x20', 'Profile', 'Métre', 119.8),\n" +
                    "('M8', 'Profile', 'Métre', 700),\n" +
                    "('M10', 'Profile', 'Métre', 1115),\n" +
                    "('M12', 'Profile', 'Métre', 1620),\n" +
                    "('M14', 'Profile', 'Métre', 2210),\n" +
                    "('M16', 'Profile', 'Métre', 3015),\n" +
                    "('M18', 'Profile', 'Métre', 3685),\n" +
                    "('M20', 'Profile', 'Métre', 4700),\n" +
                    "('M22', 'Profile', 'Métre', 5820),\n" +
                    "('M24', 'Profile', 'Métre', 6780),\n" +
                    "('M27', 'Profile', 'Métre', 8813),\n" +
                    "('M30', 'Profile', 'Métre', 10771),\n" +
                    "('M33', 'Profile', 'Métre', 13325),\n" +
                    "('M36', 'Profile', 'Métre', 15686),\n" +
                    "('M39', 'Profile', 'Métre', 18739),\n" +
                    "('M42', 'Profile', 'Métre', 21523),\n" +
                    "('M45', 'Profile', 'Métre', 25075),\n" +
                    "('M48', 'Profile', 'Métre', 28282),\n" +
                    "('M52', 'Profile', 'Métre', 33754),\n" +
                    "('M56', 'Profile', 'Métre', 38976),\n" +
                    "('HR1-8', 'Profile', 'Métre', 0),\n" +
                    "('HR1-10', 'Profile', 'Métre', 0),\n" +
                    "('HR1-12', 'Profile', 'Métre', 0),\n" +
                    "('HR1-14', 'Profile', 'Métre', 0),\n" +
                    "('HR1-16', 'Profile', 'Métre', 0),\n" +
                    "('HR1-18', 'Profile', 'Métre', 0),\n" +
                    "('HR1-20', 'Profile', 'Métre', 0),\n" +
                    "('HR1-22', 'Profile', 'Métre', 0),\n" +
                    "('HR1-24', 'Profile', 'Métre', 0),\n" +
                    "('HR1-27', 'Profile', 'Métre', 0),\n" +
                    "('HR1-30', 'Profile', 'Métre', 0),\n" +
                    "('HR1-33', 'Profile', 'Métre', 0),\n" +
                    "('HR1-36', 'Profile', 'Métre', 0),\n" +
                    "('HR1-39', 'Profile', 'Métre', 0),\n" +
                    "('HR1-42', 'Profile', 'Métre', 0),\n" +
                    "('HR1-45', 'Profile', 'Métre', 0),\n" +
                    "('HR1-48', 'Profile', 'Métre', 0),\n" +
                    "('HR1-52', 'Profile', 'Métre', 0),\n" +
                    "('HR1-56', 'Profile', 'Métre', 0),\n" +
                    "('HR2-8', 'Profile', 'Métre', 0),\n" +
                    "('HR2-10', 'Profile', 'Métre', 0),\n" +
                    "('HR2-12', 'Profile', 'Métre', 4316),\n" +
                    "('HR2-14', 'Profile', 'Métre', 5888),\n" +
                    "('HR2-16', 'Profile', 'Métre', 8038),\n" +
                    "('HR2-18', 'Profile', 'Métre', 9830),\n" +
                    "('HR2-20', 'Profile', 'Métre', 12544),\n" +
                    "('HR2-22', 'Profile', 'Métre', 15514),\n" +
                    "('HR2-24', 'Profile', 'Métre', 18074),\n" +
                    "('HR2-27', 'Profile', 'Métre', 23500),\n" +
                    "('HR2-30', 'Profile', 'Métre', 28723),\n" +
                    "('HR2-33', 'Profile', 'Métre', 35533),\n" +
                    "('HR2-36', 'Profile', 'Métre', 41830),\n" +
                    "('HR2-39', 'Profile', 'Métre', 0),\n" +
                    "('HR2-42', 'Profile', 'Métre', 0),\n" +
                    "('HR2-45', 'Profile', 'Métre', 0),\n" +
                    "('HR2-48', 'Profile', 'Métre', 0),\n" +
                    "('HR2-52', 'Profile', 'Métre', 0),\n" +
                    "('HR2-56', 'Profile', 'Métre', 0),\n" +
                    "('Rond 4', 'Profile', 'Métre', 0.099),\n" +
                    "('Rond 5', 'Profile', 'Métre', 0.154),\n" +
                    "('Rond 6', 'Profile', 'Métre', 0.222),\n" +
                    "('Rond 7', 'Profile', 'Métre', 0.302),\n" +
                    "('Rond 8', 'Profile', 'Métre', 0.395),\n" +
                    "('Rond 9', 'Profile', 'Métre', 0.499),\n" +
                    "('Rond 10', 'Profile', 'Métre', 0.617),\n" +
                    "('Rond 11', 'Profile', 'Métre', 0.746),\n" +
                    "('Rond 12', 'Profile', 'Métre', 0.888),\n" +
                    "('Rond 13', 'Profile', 'Métre', 1.042),\n" +
                    "('Rond 14', 'Profile', 'Métre', 1.208),\n" +
                    "('Rond 15', 'Profile', 'Métre', 1.387),\n" +
                    "('Rond 16', 'Profile', 'Métre', 1.578),\n" +
                    "('Rond 17', 'Profile', 'Métre', 1.782),\n" +
                    "('Rond 18', 'Profile', 'Métre', 1.998),\n" +
                    "('Rond 19', 'Profile', 'Métre', 2.226),\n" +
                    "('Rond 20', 'Profile', 'Métre', 2.466),\n" +
                    "('Rond 21', 'Profile', 'Métre', 2.719),\n" +
                    "('Rond 22', 'Profile', 'Métre', 2.984),\n" +
                    "('Rond 23', 'Profile', 'Métre', 3.261),\n" +
                    "('Rond 24', 'Profile', 'Métre', 3.551),\n" +
                    "('Rond 25', 'Profile', 'Métre', 3.853),\n" +
                    "('Rond 26', 'Profile', 'Métre', 4.168),\n" +
                    "('Rond 27', 'Profile', 'Métre', 4.495),\n" +
                    "('Rond 28', 'Profile', 'Métre', 4.834),\n" +
                    "('Rond 29', 'Profile', 'Métre', 5.185),\n" +
                    "('Rond 30', 'Profile', 'Métre', 5.549),\n" +
                    "('Rond 31', 'Profile', 'Métre', 5.925),\n" +
                    "('Rond 32', 'Profile', 'Métre', 6.313),\n" +
                    "('Rond 33', 'Profile', 'Métre', 6.714),\n" +
                    "('Rond 34', 'Profile', 'Métre', 7.127),\n" +
                    "('Rond 35', 'Profile', 'Métre', 7.553),\n" +
                    "('Rond 36', 'Profile', 'Métre', 7.99),\n" +
                    "('Rond 37', 'Profile', 'Métre', 8.44),\n" +
                    "('Rond 38', 'Profile', 'Métre', 8.903),\n" +
                    "('Rond 39', 'Profile', 'Métre', 9.378),\n" +
                    "('Rond 40', 'Profile', 'Métre', 9.865),\n" +
                    "('Rond 41', 'Profile', 'Métre', 10.364),\n" +
                    "('Rond 42', 'Profile', 'Métre', 10.876),\n" +
                    "('Rond 43', 'Profile', 'Métre', 11.4),\n" +
                    "('Rond 44', 'Profile', 'Métre', 11.936),\n" +
                    "('Rond 45', 'Profile', 'Métre', 12.485),\n" +
                    "('Rond 46', 'Profile', 'Métre', 13.046),\n" +
                    "('Rond 47', 'Profile', 'Métre', 13.619),\n" +
                    "('Rond 48', 'Profile', 'Métre', 14.205),\n" +
                    "('Rond 49', 'Profile', 'Métre', 14.803),\n" +
                    "('Rond 50', 'Profile', 'Métre', 15.413),\n" +
                    "('Rond 51', 'Profile', 'Métre', 16.036),\n" +
                    "('Rond 52', 'Profile', 'Métre', 16.671),\n" +
                    "('Rond 53', 'Profile', 'Métre', 17.319),\n" +
                    "('Rond 54', 'Profile', 'Métre', 17.978),\n" +
                    "('Rond 55', 'Profile', 'Métre', 18.65),\n" +
                    "('Rond 56', 'Profile', 'Métre', 19.335),\n" +
                    "('Rond 57', 'Profile', 'Métre', 20.031),\n" +
                    "('Rond 58', 'Profile', 'Métre', 20.74),\n" +
                    "('Rond 59', 'Profile', 'Métre', 21.462),\n" +
                    "('Rond 60', 'Profile', 'Métre', 22.195),\n" +
                    "('Rond 61', 'Profile', 'Métre', 22.941),\n" +
                    "('Rond 62', 'Profile', 'Métre', 23.7),\n" +
                    "('Rond 63', 'Profile', 'Métre', 24.47),\n" +
                    "('Rond 64', 'Profile', 'Métre', 25.253),\n" +
                    "('Rond 65', 'Profile', 'Métre', 26.049),\n" +
                    "('Rond 66', 'Profile', 'Métre', 26.856),\n" +
                    "('Rond 67', 'Profile', 'Métre', 27.676),\n" +
                    "('Rond 68', 'Profile', 'Métre', 28.509),\n" +
                    "('Rond 69', 'Profile', 'Métre', 29.353),\n" +
                    "('Rond 70', 'Profile', 'Métre', 30.21),\n" +
                    "('Rond 71', 'Profile', 'Métre', 31.08),\n" +
                    "('Rond 72', 'Profile', 'Métre', 31.961),\n" +
                    "('Rond 73', 'Profile', 'Métre', 32.855),\n" +
                    "('Rond 74', 'Profile', 'Métre', 33.762),\n" +
                    "('Rond 75', 'Profile', 'Métre', 34.68),\n" +
                    "('Rond 76', 'Profile', 'Métre', 35.611),\n" +
                    "('Rond 77', 'Profile', 'Métre', 36.555),\n" +
                    "('Rond 78', 'Profile', 'Métre', 37.51),\n" +
                    "('Rond 79', 'Profile', 'Métre', 38.478),\n" +
                    "('Rond 80', 'Profile', 'Métre', 39.458),\n" +
                    "('Rond 81', 'Profile', 'Métre', 40.451),\n" +
                    "('Rond 82', 'Profile', 'Métre', 41.456),\n" +
                    "('Rond 83', 'Profile', 'Métre', 42.473),\n" +
                    "('Rond 84', 'Profile', 'Métre', 43.503),\n" +
                    "('Rond 85', 'Profile', 'Métre', 44.545),\n" +
                    "('Rond 86', 'Profile', 'Métre', 45.599),\n" +
                    "('Rond 87', 'Profile', 'Métre', 46.666),\n" +
                    "('Rond 88', 'Profile', 'Métre', 47.745),\n" +
                    "('Rond 89', 'Profile', 'Métre', 48.836),\n" +
                    "('Rond 90', 'Profile', 'Métre', 49.94),\n" +
                    "('Rond 91', 'Profile', 'Métre', 51.055),\n" +
                    "('Rond 92', 'Profile', 'Métre', 52.184),\n" +
                    "('Rond 93', 'Profile', 'Métre', 53.324),\n" +
                    "('Rond 94', 'Profile', 'Métre', 54.477),\n" +
                    "('Rond 95', 'Profile', 'Métre', 55.643),\n" +
                    "('Rond 96', 'Profile', 'Métre', 56.82),\n" +
                    "('Rond 97', 'Profile', 'Métre', 58.01),\n" +
                    "('Rond 98', 'Profile', 'Métre', 59.212),\n" +
                    "('Rond 99', 'Profile', 'Métre', 60.427),\n" +
                    "('Rond 100', 'Profile', 'Métre', 61.654),\n" +
                    "('Rond 101', 'Profile', 'Métre', 62.893),\n" +
                    "('Rond 102', 'Profile', 'Métre', 64.145),\n" +
                    "('Rond 103', 'Profile', 'Métre', 65.408),\n" +
                    "('Rond 104', 'Profile', 'Métre', 66.685),\n" +
                    "('Rond 105', 'Profile', 'Métre', 67.973),\n" +
                    "('Rond 106', 'Profile', 'Métre', 69.274),\n" +
                    "('Rond 107', 'Profile', 'Métre', 70.587),\n" +
                    "('Rond 108', 'Profile', 'Métre', 71.913),\n" +
                    "('Rond 109', 'Profile', 'Métre', 73.251),\n" +
                    "('Rond 110', 'Profile', 'Métre', 74.601),\n" +
                    "('Rond 111', 'Profile', 'Métre', 75.964),\n" +
                    "('Rond 112', 'Profile', 'Métre', 77.338),\n" +
                    "('Rond 113', 'Profile', 'Métre', 78.726),\n" +
                    "('Rond 114', 'Profile', 'Métre', 80.125),\n" +
                    "('Rond 115', 'Profile', 'Métre', 81.537),\n" +
                    "('Rond 116', 'Profile', 'Métre', 82.961),\n" +
                    "('Rond 117', 'Profile', 'Métre', 84.398),\n" +
                    "('Rond 118', 'Profile', 'Métre', 85.847),\n" +
                    "('Rond 119', 'Profile', 'Métre', 87.308),\n" +
                    "('Rond 120', 'Profile', 'Métre', 88.781),\n" +
                    "('Rond 121', 'Profile', 'Métre', 90.267),\n" +
                    "('Rond 122', 'Profile', 'Métre', 91.765),\n" +
                    "('Rond 123', 'Profile', 'Métre', 93.276),\n" +
                    "('Rond 124', 'Profile', 'Métre', 94.799),\n" +
                    "('Rond 125', 'Profile', 'Métre', 96.334),\n" +
                    "('Rond 126', 'Profile', 'Métre', 97.882),\n" +
                    "('Rond 127', 'Profile', 'Métre', 99.441),\n" +
                    "('Rond 128', 'Profile', 'Métre', 101.014),\n" +
                    "('Rond 129', 'Profile', 'Métre', 102.598),\n" +
                    "('Rond 130', 'Profile', 'Métre', 104.195),\n" +
                    "('Rond 131', 'Profile', 'Métre', 105.804),\n" +
                    "('Rond 132', 'Profile', 'Métre', 107.426),\n" +
                    "('Rond 133', 'Profile', 'Métre', 109.059),\n" +
                    "('Rond 134', 'Profile', 'Métre', 110.705),\n" +
                    "('Rond 135', 'Profile', 'Métre', 112.364),\n" +
                    "('Rond 136', 'Profile', 'Métre', 114.035),\n" +
                    "('Rond 137', 'Profile', 'Métre', 115.718),\n" +
                    "('Rond 138', 'Profile', 'Métre', 117.413),\n" +
                    "('Rond 139', 'Profile', 'Métre', 119.121),\n" +
                    "('Rond 140', 'Profile', 'Métre', 120.841),\n" +
                    "('Rond 141', 'Profile', 'Métre', 122.574),\n" +
                    "('Rond 142', 'Profile', 'Métre', 124.319),\n" +
                    "('Rond 143', 'Profile', 'Métre', 126.076),\n" +
                    "('Rond 144', 'Profile', 'Métre', 127.845),\n" +
                    "('Rond 145', 'Profile', 'Métre', 129.627),\n" +
                    "('Rond 146', 'Profile', 'Métre', 131.421),\n" +
                    "('Rond 147', 'Profile', 'Métre', 133.228),\n" +
                    "('Rond 148', 'Profile', 'Métre', 135.046),\n" +
                    "('Rond 149', 'Profile', 'Métre', 136.878),\n" +
                    "('Rond 150', 'Profile', 'Métre', 138.721),\n" +
                    "('Rond 151', 'Profile', 'Métre', 140.577),\n" +
                    "('Rond 152', 'Profile', 'Métre', 142.445),\n" +
                    "('Rond 153', 'Profile', 'Métre', 144.325),\n" +
                    "('Rond 154', 'Profile', 'Métre', 146.218),\n" +
                    "('Rond 155', 'Profile', 'Métre', 148.123),\n" +
                    "('Rond 156', 'Profile', 'Métre', 150.041),\n" +
                    "('Rond 157', 'Profile', 'Métre', 151.97),\n" +
                    "('Rond 158', 'Profile', 'Métre', 153.912),\n" +
                    "('Rond 159', 'Profile', 'Métre', 155.867),\n" +
                    "('Rond 160', 'Profile', 'Métre', 157.834),\n" +
                    "('Rond 161', 'Profile', 'Métre', 159.813),\n" +
                    "('Rond 162', 'Profile', 'Métre', 161.804),\n" +
                    "('Rond 163', 'Profile', 'Métre', 163.808),\n" +
                    "('Rond 164', 'Profile', 'Métre', 165.824),\n" +
                    "('Rond 165', 'Profile', 'Métre', 167.852),\n" +
                    "('Rond 166', 'Profile', 'Métre', 169.893),\n" +
                    "('Rond 167', 'Profile', 'Métre', 171.946),\n" +
                    "('Rond 168', 'Profile', 'Métre', 174.012),\n" +
                    "('Rond 169', 'Profile', 'Métre', 176.089),\n" +
                    "('Rond 170', 'Profile', 'Métre', 178.179),\n" +
                    "('Rond 171', 'Profile', 'Métre', 180.282),\n" +
                    "('Rond 172', 'Profile', 'Métre', 182.396),\n" +
                    "('Rond 173', 'Profile', 'Métre', 184.524),\n" +
                    "('Rond 174', 'Profile', 'Métre', 186.663),\n" +
                    "('Rond 175', 'Profile', 'Métre', 188.815),\n" +
                    "('Rond 176', 'Profile', 'Métre', 190.979),\n" +
                    "('Rond 177', 'Profile', 'Métre', 193.155),\n" +
                    "('Rond 178', 'Profile', 'Métre', 195.344),\n" +
                    "('Rond 179', 'Profile', 'Métre', 197.545),\n" +
                    "('Rond 180', 'Profile', 'Métre', 199.758),\n" +
                    "('Rond 181', 'Profile', 'Métre', 201.984),\n" +
                    "('Rond 182', 'Profile', 'Métre', 204.222),\n" +
                    "('Rond 183', 'Profile', 'Métre', 206.472),\n" +
                    "('Rond 184', 'Profile', 'Métre', 208.735),\n" +
                    "('Rond 185', 'Profile', 'Métre', 211.01),\n" +
                    "('Rond 186', 'Profile', 'Métre', 213.297),\n" +
                    "('Rond 187', 'Profile', 'Métre', 215.597),\n" +
                    "('Rond 188', 'Profile', 'Métre', 217.909),\n" +
                    "('Rond 189', 'Profile', 'Métre', 220.233),\n" +
                    "('Rond 190', 'Profile', 'Métre', 222.57),\n" +
                    "('Rond 191', 'Profile', 'Métre', 224.919),\n" +
                    "('Rond 192', 'Profile', 'Métre', 227.28),\n" +
                    "('Rond 193', 'Profile', 'Métre', 229.654),\n" +
                    "('Rond 194', 'Profile', 'Métre', 232.04),\n" +
                    "('Rond 195', 'Profile', 'Métre', 234.438),\n" +
                    "('Rond 196', 'Profile', 'Métre', 236.849),\n" +
                    "('Rond 197', 'Profile', 'Métre', 239.272),\n" +
                    "('Rond 198', 'Profile', 'Métre', 241.707),\n" +
                    "('Rond 199', 'Profile', 'Métre', 244.155),\n" +
                    "('Rond 200', 'Profile', 'Métre', 246.615),\n" +
                    "('Rond 201', 'Profile', 'Métre', 249.087),\n" +
                    "('Rond 202', 'Profile', 'Métre', 251.572),\n" +
                    "('Rond 203', 'Profile', 'Métre', 254.069),\n" +
                    "('Rond 204', 'Profile', 'Métre', 256.578),\n" +
                    "('Rond 205', 'Profile', 'Métre', 259.1),\n" +
                    "('Rond 206', 'Profile', 'Métre', 261.634),\n" +
                    "('Rond 207', 'Profile', 'Métre', 264.18),\n" +
                    "('Rond 208', 'Profile', 'Métre', 266.739),\n" +
                    "('Rond 209', 'Profile', 'Métre', 269.31),\n" +
                    "('Rond 210', 'Profile', 'Métre', 271.893),\n" +
                    "('Rond 211', 'Profile', 'Métre', 274.489),\n" +
                    "('Rond 212', 'Profile', 'Métre', 277.097),\n" +
                    "('Rond 213', 'Profile', 'Métre', 279.717),\n" +
                    "('Rond 214', 'Profile', 'Métre', 282.35),\n" +
                    "('Rond 215', 'Profile', 'Métre', 284.994),\n" +
                    "('Rond 216', 'Profile', 'Métre', 287.652),\n" +
                    "('Rond 217', 'Profile', 'Métre', 290.321),\n" +
                    "('Rond 218', 'Profile', 'Métre', 293.003),\n" +
                    "('Rond 219', 'Profile', 'Métre', 295.698),\n" +
                    "('Rond 220', 'Profile', 'Métre', 298.404),\n" +
                    "('Rond 221', 'Profile', 'Métre', 301.123),\n" +
                    "('Rond 222', 'Profile', 'Métre', 303.854),\n" +
                    "('Rond 223', 'Profile', 'Métre', 306.598),\n" +
                    "('Rond 224', 'Profile', 'Métre', 309.354),\n" +
                    "('Rond 225', 'Profile', 'Métre', 312.122),\n" +
                    "('Rond 226', 'Profile', 'Métre', 314.903),\n" +
                    "('Rond 227', 'Profile', 'Métre', 317.696),\n" +
                    "('Rond 228', 'Profile', 'Métre', 320.501),\n" +
                    "('Rond 229', 'Profile', 'Métre', 323.318),\n" +
                    "('Rond 230', 'Profile', 'Métre', 326.148),\n" +
                    "('Rond 231', 'Profile', 'Métre', 328.991),\n" +
                    "('Rond 232', 'Profile', 'Métre', 331.845),\n" +
                    "('Rond 233', 'Profile', 'Métre', 334.712),\n" +
                    "('Rond 234', 'Profile', 'Métre', 337.591),\n" +
                    "('Rond 235', 'Profile', 'Métre', 340.483),\n" +
                    "('Rond 236', 'Profile', 'Métre', 343.387),\n" +
                    "('Rond 237', 'Profile', 'Métre', 346.303),\n" +
                    "('Rond 238', 'Profile', 'Métre', 349.232),\n" +
                    "('Rond 239', 'Profile', 'Métre', 352.172),\n" +
                    "('Rond 240', 'Profile', 'Métre', 355.126),\n" +
                    "('Rond 241', 'Profile', 'Métre', 358.091),\n" +
                    "('Rond 242', 'Profile', 'Métre', 361.069),\n" +
                    "('Rond 243', 'Profile', 'Métre', 364.059),\n" +
                    "('Rond 244', 'Profile', 'Métre', 367.062),\n" +
                    "('Rond 245', 'Profile', 'Métre', 370.077),\n" +
                    "('Rond 246', 'Profile', 'Métre', 373.104),\n" +
                    "('Rond 247', 'Profile', 'Métre', 376.143),\n" +
                    "('Rond 248', 'Profile', 'Métre', 379.195),\n" +
                    "('Rond 249', 'Profile', 'Métre', 382.259),\n" +
                    "('Rond 250', 'Profile', 'Métre', 385.336),\n" +
                    "('Rond 251', 'Profile', 'Métre', 388.425),\n" +
                    "('Rond 252', 'Profile', 'Métre', 391.526),\n" +
                    "('Rond 253', 'Profile', 'Métre', 394.64),\n" +
                    "('Rond 254', 'Profile', 'Métre', 397.765),\n" +
                    "('Rond 255', 'Profile', 'Métre', 400.904),\n" +
                    "('Rond 256', 'Profile', 'Métre', 404.054),\n" +
                    "('Rond 257', 'Profile', 'Métre', 407.217),\n" +
                    "('Rond 258', 'Profile', 'Métre', 410.392),\n" +
                    "('Rond 259', 'Profile', 'Métre', 413.58),\n" +
                    "('Rond 260', 'Profile', 'Métre', 416.779),\n" +
                    "('Rond 261', 'Profile', 'Métre', 419.992),\n" +
                    "('Rond 262', 'Profile', 'Métre', 423.216),\n" +
                    "('Rond 263', 'Profile', 'Métre', 426.453),\n" +
                    "('Rond 264', 'Profile', 'Métre', 429.702),\n" +
                    "('Rond 265', 'Profile', 'Métre', 432.964),\n" +
                    "('Rond 266', 'Profile', 'Métre', 436.237),\n" +
                    "('Rond 267', 'Profile', 'Métre', 439.523),\n" +
                    "('Rond 268', 'Profile', 'Métre', 442.822),\n" +
                    "('Rond 269', 'Profile', 'Métre', 446.133),\n" +
                    "('Rond 270', 'Profile', 'Métre', 449.456),\n" +
                    "('Rond 271', 'Profile', 'Métre', 452.791),\n" +
                    "('Rond 272', 'Profile', 'Métre', 456.139),\n" +
                    "('Rond 273', 'Profile', 'Métre', 459.499),\n" +
                    "('Rond 274', 'Profile', 'Métre', 462.872),\n" +
                    "('Rond 275', 'Profile', 'Métre', 466.257),\n" +
                    "('Rond 276', 'Profile', 'Métre', 469.654),\n" +
                    "('Rond 277', 'Profile', 'Métre', 473.063),\n" +
                    "('Rond 278', 'Profile', 'Métre', 476.485),\n" +
                    "('Rond 279', 'Profile', 'Métre', 479.919),\n" +
                    "('Rond 280', 'Profile', 'Métre', 483.365),\n" +
                    "('Rond 281', 'Profile', 'Métre', 486.824),\n" +
                    "('Rond 282', 'Profile', 'Métre', 490.295),\n" +
                    "('Rond 283', 'Profile', 'Métre', 493.779),\n" +
                    "('Rond 284', 'Profile', 'Métre', 497.275),\n" +
                    "('Rond 285', 'Profile', 'Métre', 500.783),\n" +
                    "('Rond 286', 'Profile', 'Métre', 504.303),\n" +
                    "('Rond 287', 'Profile', 'Métre', 507.836),\n" +
                    "('Rond 288', 'Profile', 'Métre', 511.381),\n" +
                    "('Rond 289', 'Profile', 'Métre', 514.938),\n" +
                    "('Rond 290', 'Profile', 'Métre', 518.508),\n" +
                    "('Rond 291', 'Profile', 'Métre', 522.09),\n" +
                    "('Rond 292', 'Profile', 'Métre', 525.685),\n" +
                    "('Rond 293', 'Profile', 'Métre', 529.291),\n" +
                    "('Rond 294', 'Profile', 'Métre', 532.91),\n" +
                    "('Rond 295', 'Profile', 'Métre', 536.542),\n" +
                    "('Rond 296', 'Profile', 'Métre', 540.186),\n" +
                    "('Rond 297', 'Profile', 'Métre', 543.842),\n" +
                    "('Rond 298', 'Profile', 'Métre', 547.51),\n" +
                    "('Rond 299', 'Profile', 'Métre', 551.191),\n" +
                    "('Rond 300', 'Profile', 'Métre', 554.884),\n" +
                    "('Rond 301', 'Profile', 'Métre', 558.589),\n" +
                    "('Rond 302', 'Profile', 'Métre', 562.307),\n" +
                    "('Rond 303', 'Profile', 'Métre', 566.037),\n" +
                    "('Rond 304', 'Profile', 'Métre', 569.779),\n" +
                    "('Rond 305', 'Profile', 'Métre', 573.534),\n" +
                    "('Rond 306', 'Profile', 'Métre', 577.301),\n" +
                    "('Rond 307', 'Profile', 'Métre', 581.08),\n" +
                    "('Rond 308', 'Profile', 'Métre', 584.872),\n" +
                    "('Rond 309', 'Profile', 'Métre', 588.676),\n" +
                    "('Rond 310', 'Profile', 'Métre', 592.493),\n" +
                    "('Rond 311', 'Profile', 'Métre', 596.321),\n" +
                    "('Rond 312', 'Profile', 'Métre', 600.162),\n" +
                    "('Rond 313', 'Profile', 'Métre', 604.016),\n" +
                    "('Rond 314', 'Profile', 'Métre', 607.881),\n" +
                    "('Rond 315', 'Profile', 'Métre', 611.759),\n" +
                    "('Rond 316', 'Profile', 'Métre', 615.65),\n" +
                    "('Rond 317', 'Profile', 'Métre', 619.552),\n" +
                    "('Rond 318', 'Profile', 'Métre', 623.467),\n" +
                    "('Rond 319', 'Profile', 'Métre', 627.395),\n" +
                    "('Rond 320', 'Profile', 'Métre', 631.334),\n" +
                    "('Rond 321', 'Profile', 'Métre', 635.286),\n" +
                    "('Rond 322', 'Profile', 'Métre', 639.251),\n" +
                    "('Rond 323', 'Profile', 'Métre', 643.227),\n" +
                    "('Rond 324', 'Profile', 'Métre', 647.216),\n" +
                    "('Rond 325', 'Profile', 'Métre', 651.218),\n" +
                    "('TCAR 80x6', 'Profile', 'Métre', 14.4),\n" +
                    "('Tube 21,3x2,3', 'Profile', 'Métre', 1.08),\n" +
                    "('Tube 26,9x2,3', 'Profile', 'Métre', 1.4),\n" +
                    "('Tube 33,7x2,6', 'Profile', 'Métre', 1.99),\n" +
                    "('Tube 42,4x2,6', 'Profile', 'Métre', 2.55),\n" +
                    "('Tube 48,3x2,9', 'Profile', 'Métre', 3.25),\n" +
                    "('Tube 48,3x3,2', 'Profile', 'Métre', 3.56),\n" +
                    "('Tube 60,3x2,9', 'Profile', 'Métre', 4.11),\n" +
                    "('Tube 60,3x5', 'Profile', 'Métre', 6.82),\n" +
                    "('Tube 76,1x2,9', 'Profile', 'Métre', 5.24),\n" +
                    "('Tube 76,1x5', 'Profile', 'Métre', 8.77),\n" +
                    "('Tube 88,9x3,2', 'Profile', 'Métre', 6.76),\n" +
                    "('Tube 101,6x3,6', 'Profile', 'Métre', 8.7),\n" +
                    "('Tube 101,6x5', 'Profile', 'Métre', 11.9),\n" +
                    "('Tube 114,3x3,6', 'Profile', 'Métre', 9.83),\n" +
                    "('Tube 114,3x6,3', 'Profile', 'Métre', 16.8),\n" +
                    "('Tube 139,7x4', 'Profile', 'Métre', 13.4),\n" +
                    "('Tube 139,7x6,3', 'Profile', 'Métre', 20.7),\n" +
                    "('Tube 168,3x4,5', 'Profile', 'Métre', 18.2),\n" +
                    "('Tube 168,3x6,3', 'Profile', 'Métre', 25.2),\n" +
                    "('Tube 193,7x3,6', 'Profile', 'Métre', 16.9),\n" +
                    "('Tube 193,7x6,3', 'Profile', 'Métre', 29.1),\n" +
                    "('Tube 219,1x3,6', 'Profile', 'Métre', 19.1),\n" +
                    "('Tube 219,1x6,3', 'Profile', 'Métre', 33.1),\n" +
                    "('Tube 244,5x6,3', 'Profile', 'Métre', 37),\n" +
                    "('Tube 244,5x8', 'Profile', 'Métre', 46.7),\n" +
                    "('Tube 273x3,6', 'Profile', 'Métre', 23.9),\n" +
                    "('Tube 273x6,3', 'Profile', 'Métre', 41.4),\n" +
                    "('Tube 323,9x4', 'Profile', 'Métre', 31.6),\n" +
                    "('Tube 323,9x6,3', 'Profile', 'Métre', 49.3),\n" +
                    "('Tube 355,6x8', 'Profile', 'Métre', 68.6),\n" +
                    "('Tube 355,6x10', 'Profile', 'Métre', 85.2),\n" +
                    "('Tube 406,4x4,5', 'Profile', 'Métre', 44.6),\n" +
                    "('Tube 406,4x6,3', 'Profile', 'Métre', 62.2),\n" +
                    "('Ω 30x60x120x60x', 'Profile', 'Métre', 7.2),\n" +
                    "('Fer plat 60x8', 'Profile', 'Métre', 3.84),\n" +
                    "('Fer plat 200x6', 'Profile', 'Métre', 1.6),\n" +
                    "('Tôle ep 10 mm', 'Profile', 'Métre', 80),\n" +
                    "('Tôle ep 5 mm', 'Profile', 'Métre', 40),\n" +
                    "('Tôle ep 20 mm', 'Profile', 'Métre', 160)");

            db.execSQL("CREATE TABLE estimate(id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "doneIn TEXT,issueDate TEXT,expirationDate TEXT,customer INTEGER,excludingTaxTotal Float,discount float,excludingTaxTotalAfterDiscount float," +
                    "vat FLOAT,allTaxIncludedTotal FLOAT,isPaid TEXT,FOREIGN KEY (customer) REFERENCES customer(id) ON DELETE CASCADE)");

            db.execSQL("CREATE TABLE estimateline(id INTEGER PRIMARY KEY AUTOINCREMENT,estimate INTEGER, steel INTEGER," +
                    "weight float,length float,width float,height float,quantity INTEGER,total Float,margin INTEGER," +
                    "quantityPlusMargin FLOAT,unitPrice FLOAT,totalPrice FLOAT," +
                    "FOREIGN KEY (steel) REFERENCES steel(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (estimate) REFERENCES estimate(id) ON DELETE CASCADE)");
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}