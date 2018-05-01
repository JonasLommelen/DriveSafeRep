package com.example.jonaslommelen.drivesafe.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BeerListDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "beerList.db";
    private static final int DATABASE_VERSION = 1;

    public BeerListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_BEERLIST_TABLE = "CREATE TABLE " + BeerListContract.BeerListEntry.TABLE_NAME + " (" +
                BeerListContract.BeerListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BeerListContract.BeerListEntry.COLUMN_BEER_NAME + " TEXT NOT NULL, " +
                BeerListContract.BeerListEntry.COLUMN_QUANTITY_IN_CL + " INTEGER NOT NULL, " +
                BeerListContract.BeerListEntry.COLUMN_ABV + " DOUBLE NOT NULL, " +
                BeerListContract.BeerListEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                BeerListContract.BeerListEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_BEERLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BeerListContract.BeerListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
