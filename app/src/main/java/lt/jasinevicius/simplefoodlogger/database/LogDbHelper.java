package lt.jasinevicius.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LogDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "logs.db";

    public LogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "create table " + DbSchema.Logs.NAME + "(" +
                DbSchema.Logs.Cols.LOG_ID + " primary key, " +
                DbSchema.Logs.Cols.DATE + ", " +
                DbSchema.Logs.Cols.FOOD + ", " +
                DbSchema.Logs.Cols.SIZE + ", " +
                DbSchema.Logs.Cols.KCAL + ", " +
                DbSchema.Logs.Cols.PROTEIN + ", " +
                DbSchema.Logs.Cols.CARBS + ", " +
                DbSchema.Logs.Cols.FAT + ")"
        );

        db.execSQL(
            "create table " + DbSchema.History.NAME + "(" +
                DbSchema.History.Cols.DATE + " primary key, " +
                DbSchema.History.Cols.WEIGHT + ", " +
                DbSchema.History.Cols.KCAL_TARGET + ", " +
                DbSchema.History.Cols.PROTEIN_TARGET + ", " +
                DbSchema.History.Cols.CARBS_TARGET + ", " +
                DbSchema.History.Cols.FAT_TARGET + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("TKAJAS", "db upgraded from v" + oldVersion + " to v" + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("TKAJAS", "db downgraded from v" + oldVersion + " to v" + newVersion);
    }
}
