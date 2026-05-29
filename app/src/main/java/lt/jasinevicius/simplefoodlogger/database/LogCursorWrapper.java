package lt.jasinevicius.simplefoodlogger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import lt.jasinevicius.simplefoodlogger.Log;

import java.util.Date;
import java.util.UUID;

public class LogCursorWrapper extends CursorWrapper {
    public LogCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Log getLog() {
        String uuidString = getString(getColumnIndex(DbSchema.Logs.Cols.LOG_ID));
        long date = getLong(getColumnIndex(DbSchema.Logs.Cols.DATE));
        String food = getString(getColumnIndex(DbSchema.Logs.Cols.FOOD));
        float size = getFloat(getColumnIndex(DbSchema.Logs.Cols.SIZE));
        float kcal = getFloat(getColumnIndex(DbSchema.Logs.Cols.KCAL));
        float protein = getFloat(getColumnIndex(DbSchema.Logs.Cols.PROTEIN));
        float carbs = getFloat(getColumnIndex(DbSchema.Logs.Cols.CARBS));
        float fat = getFloat(getColumnIndex(DbSchema.Logs.Cols.FAT));

        Log log = new Log(UUID.fromString(uuidString));
        log.setDate(new Date(date));
        log.setFood(food);
        log.setSize(size);
        log.setKcal(kcal);
        log.setProtein(protein);
        log.setCarbs(carbs);
        log.setFat(fat);

        return log;
    }
}