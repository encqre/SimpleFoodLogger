package com.untrustworthypillars.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.untrustworthypillars.simplefoodlogger.database.DbSchema.ExtendedFoodTable;

public class ExtendedFoodDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "extendedFoodDB.db";

    public ExtendedFoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ExtendedFoodTable.NAME + "(" + "_id integer primary key autoincrement, " +
                ExtendedFoodTable.Cols.FOODID + ", " +
                ExtendedFoodTable.Cols.SORTID + ", " +
                ExtendedFoodTable.Cols.TITLE + ", " +
                ExtendedFoodTable.Cols.CATEGORY + ", " +
                ExtendedFoodTable.Cols.KCAL + ", " +
                ExtendedFoodTable.Cols.PROTEIN + ", " +
                ExtendedFoodTable.Cols.CARBS + ", " +
                ExtendedFoodTable.Cols.FAT + ", " +
                ExtendedFoodTable.Cols.FAVORITE + ", " +
                ExtendedFoodTable.Cols.HIDDEN + ", " +
                ExtendedFoodTable.Cols.PORTION1NAME + ", " +
                ExtendedFoodTable.Cols.PORTION1SIZEMETRIC + ", " +
                ExtendedFoodTable.Cols.PORTION1SIZEIMPERIAL + ", " +
                ExtendedFoodTable.Cols.PORTION2NAME + ", " +
                ExtendedFoodTable.Cols.PORTION2SIZEMETRIC + ", " +
                ExtendedFoodTable.Cols.PORTION2SIZEIMPERIAL + ", " +
                ExtendedFoodTable.Cols.PORTION3NAME + ", " +
                ExtendedFoodTable.Cols.PORTION3SIZEMETRIC + ", " +
                ExtendedFoodTable.Cols.PORTION3SIZEIMPERIAL + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
