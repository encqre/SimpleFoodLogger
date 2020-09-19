package lt.jasinevicius.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CustomFoodDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "customFoodDB.db";

    public CustomFoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.CustomFoodTable.NAME + "(" + "_id integer primary key autoincrement, " +
                DbSchema.CustomFoodTable.Cols.FOODID + ", " +
                DbSchema.CustomFoodTable.Cols.SORTID + ", " +
                DbSchema.CustomFoodTable.Cols.TITLE + ", " +
                DbSchema.CustomFoodTable.Cols.CATEGORY + ", " +
                DbSchema.CustomFoodTable.Cols.KCAL + ", " +
                DbSchema.CustomFoodTable.Cols.PROTEIN + ", " +
                DbSchema.CustomFoodTable.Cols.CARBS + ", " +
                DbSchema.CustomFoodTable.Cols.FAT + ", " +
                DbSchema.CustomFoodTable.Cols.FAVORITE + ", " +
                DbSchema.CustomFoodTable.Cols.HIDDEN + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION1NAME + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION1SIZEMETRIC + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION1SIZEIMPERIAL + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION2NAME + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION2SIZEMETRIC + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION2SIZEIMPERIAL + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION3NAME + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION3SIZEMETRIC + ", " +
                DbSchema.CustomFoodTable.Cols.PORTION3SIZEIMPERIAL + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
