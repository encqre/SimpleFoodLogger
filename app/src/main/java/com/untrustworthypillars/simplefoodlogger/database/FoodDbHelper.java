package com.untrustworthypillars.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.untrustworthypillars.simplefoodlogger.database.DbSchema.FoodTable;

public class FoodDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "foodDB.db";

    public FoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FoodTable.NAME + "(" + "_id integer primary key autoincrement, " +
                FoodTable.Cols.FOODID + ", " +
                FoodTable.Cols.TITLE + ", " +
                FoodTable.Cols.CATEGORY + ", " +
                FoodTable.Cols.KCAL + ", " +
                FoodTable.Cols.PROTEIN + ", " +
                FoodTable.Cols.CARBS + ", " +
                FoodTable.Cols.FAT + ", " + FoodTable.Cols.FAVORITE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
