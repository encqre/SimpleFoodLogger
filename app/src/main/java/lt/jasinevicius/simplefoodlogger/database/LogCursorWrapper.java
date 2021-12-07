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
        String uuidString = getString(getColumnIndex(DbSchema.LogTable.Cols.LOGID));
        long date = getLong(getColumnIndex(DbSchema.LogTable.Cols.DATE));
        String datetext = getString(getColumnIndex(DbSchema.LogTable.Cols.DATETEXT));
        String food = getString(getColumnIndex(DbSchema.LogTable.Cols.FOOD));
        float size = getFloat(getColumnIndex(DbSchema.LogTable.Cols.SIZE));
        float sizeImperial = getFloat(getColumnIndex(DbSchema.LogTable.Cols.SIZEIMPERIAL));
        float kcal = getFloat(getColumnIndex(DbSchema.LogTable.Cols.KCAL));
        float protein = getFloat(getColumnIndex(DbSchema.LogTable.Cols.PROTEIN));
        float carbs = getFloat(getColumnIndex(DbSchema.LogTable.Cols.CARBS));
        float fat = getFloat(getColumnIndex(DbSchema.LogTable.Cols.FAT));

        Log log = new Log(UUID.fromString(uuidString));
        log.setDate(new Date(date));
        log.setDateText(new Date(date));
        log.setFood(food);
        log.setSize(size);
        log.setSizeImperial(sizeImperial);
        log.setKcal(kcal);
        log.setProtein(protein);
        log.setCarbs(carbs);
        log.setFat(fat);

        return log;
    }
}