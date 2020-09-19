package lt.jasinevicius.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "logDB.db";

    public LogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.LogTable.NAME + "(" + "_id integer primary key autoincrement, " +
                DbSchema.LogTable.Cols.LOGID + ", " +
                DbSchema.LogTable.Cols.DATE + ", " +
                DbSchema.LogTable.Cols.DATETEXT + ", " +
                DbSchema.LogTable.Cols.FOOD + ", " +
                DbSchema.LogTable.Cols.SIZE + ", " +
                DbSchema.LogTable.Cols.SIZEIMPERIAL + ", " +
                DbSchema.LogTable.Cols.KCAL + ", " +
                DbSchema.LogTable.Cols.PROTEIN + ", " +
                DbSchema.LogTable.Cols.CARBS + ", " + DbSchema.LogTable.Cols.FAT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
