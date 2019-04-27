package com.untrustworthypillars.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.untrustworthypillars.simplefoodlogger.database.DbSchema.CommonFoodTable;

public class CommonFoodDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "commonFoodDB.db";

    public CommonFoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CommonFoodTable.NAME + "(" + "_id integer primary key autoincrement, " +
                CommonFoodTable.Cols.FOODID + ", " +
                CommonFoodTable.Cols.SORTID + ", " +
                CommonFoodTable.Cols.TITLE + ", " +
                CommonFoodTable.Cols.CATEGORY + ", " +
                CommonFoodTable.Cols.KCAL + ", " +
                CommonFoodTable.Cols.PROTEIN + ", " +
                CommonFoodTable.Cols.CARBS + ", " +
                CommonFoodTable.Cols.FAT + ", " +
                CommonFoodTable.Cols.FAVORITE + ", " +
                CommonFoodTable.Cols.HIDDEN + ", " +
                CommonFoodTable.Cols.PORTION1NAME + ", " +
                CommonFoodTable.Cols.PORTION1SIZEMETRIC + ", " +
                CommonFoodTable.Cols.PORTION1SIZEIMPERIAL + ", " +
                CommonFoodTable.Cols.PORTION2NAME + ", " +
                CommonFoodTable.Cols.PORTION2SIZEMETRIC + ", " +
                CommonFoodTable.Cols.PORTION2SIZEIMPERIAL + ", " +
                CommonFoodTable.Cols.PORTION3NAME + ", " +
                CommonFoodTable.Cols.PORTION3SIZEMETRIC + ", " +
                CommonFoodTable.Cols.PORTION3SIZEIMPERIAL + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
