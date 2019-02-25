package com.untrustworthypillars.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.untrustworthypillars.simplefoodlogger.database.DbSchema.LogTable;

public class LogDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "logDB.db";

    public LogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LogTable.NAME + "(" + "_id integer primary key autoincrement, " +
                LogTable.Cols.LOGID + ", " +
                LogTable.Cols.DATE + ", " +
                LogTable.Cols.DATETEXT + ", " +
                LogTable.Cols.FOOD + ", " +
                LogTable.Cols.SIZE + ", " +
                LogTable.Cols.KCAL + ", " +
                LogTable.Cols.PROTEIN + ", " +
                LogTable.Cols.CARBS + ", " + LogTable.Cols.FAT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
