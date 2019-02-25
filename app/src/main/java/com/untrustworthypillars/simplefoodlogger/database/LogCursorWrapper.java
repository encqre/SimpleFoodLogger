package com.untrustworthypillars.simplefoodlogger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.untrustworthypillars.simplefoodlogger.Log;
import com.untrustworthypillars.simplefoodlogger.database.DbSchema.LogTable;

import java.util.Date;
import java.util.UUID;

public class LogCursorWrapper extends CursorWrapper {
    public LogCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Log getLog() {
        String uuidString = getString(getColumnIndex(LogTable.Cols.LOGID));
        long date = getLong(getColumnIndex(LogTable.Cols.DATE));
        String datetext = getString(getColumnIndex(LogTable.Cols.DATETEXT));
        String food = getString(getColumnIndex(LogTable.Cols.FOOD));
        float size = getFloat(getColumnIndex(LogTable.Cols.SIZE));
        float kcal = getFloat(getColumnIndex(LogTable.Cols.KCAL));
        float protein = getFloat(getColumnIndex(LogTable.Cols.PROTEIN));
        float carbs = getFloat(getColumnIndex(LogTable.Cols.CARBS));
        float fat = getFloat(getColumnIndex(LogTable.Cols.FAT));

        Log log = new Log(UUID.fromString(uuidString));
        log.setDate(new Date(date));
        log.setDateText();
        log.setFood(food);
        log.setSize(size);
        log.setKcal(kcal);
        log.setProtein(protein);
        log.setCarbs(carbs);
        log.setFat(fat);

        return log;
    }
}