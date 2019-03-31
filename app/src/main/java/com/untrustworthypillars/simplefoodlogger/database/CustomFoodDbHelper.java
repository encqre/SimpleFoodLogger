package com.untrustworthypillars.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.untrustworthypillars.simplefoodlogger.database.DbSchema.CustomFoodTable;

public class CustomFoodDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "customFoodDB.db";

    public CustomFoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CustomFoodTable.NAME + "(" + "_id integer primary key autoincrement, " +
                CustomFoodTable.Cols.FOODID + ", " +
                CustomFoodTable.Cols.SORTID + ", " +
                CustomFoodTable.Cols.TITLE + ", " +
                CustomFoodTable.Cols.CATEGORY + ", " +
                CustomFoodTable.Cols.KCAL + ", " +
                CustomFoodTable.Cols.PROTEIN + ", " +
                CustomFoodTable.Cols.CARBS + ", " +
                CustomFoodTable.Cols.FAT + ", " +
                CustomFoodTable.Cols.FAVORITE + ", " +
                CustomFoodTable.Cols.HIDDEN + ", " +
                CustomFoodTable.Cols.PORTION1NAME + ", " +
                CustomFoodTable.Cols.PORTION1SIZEMETRIC + ", " +
                CustomFoodTable.Cols.PORTION1SIZEIMPERIAL + ", " +
                CustomFoodTable.Cols.PORTION2NAME + ", " +
                CustomFoodTable.Cols.PORTION2SIZEMETRIC + ", " +
                CustomFoodTable.Cols.PORTION2SIZEIMPERIAL + ", " +
                CustomFoodTable.Cols.PORTION3NAME + ", " +
                CustomFoodTable.Cols.PORTION3SIZEMETRIC + ", " +
                CustomFoodTable.Cols.PORTION3SIZEIMPERIAL + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
