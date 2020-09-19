package lt.jasinevicius.simplefoodlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lt.jasinevicius.simplefoodlogger.database.LogCursorWrapper;
import lt.jasinevicius.simplefoodlogger.database.LogDbHelper;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.LogTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class LogManager {
    private static LogManager sLogManager;

    private Context mContext;
    private SQLiteDatabase mLogDatabase;

    public static LogManager get(Context context) {
        if (sLogManager == null) {
            sLogManager = new LogManager(context);
        }
        return sLogManager;
    }

    private LogManager(Context context) {
        mContext = context.getApplicationContext();
        mLogDatabase = new LogDbHelper(mContext).getWritableDatabase();
    }

    public void addLog(Log l) {
        ContentValues values = getContentValues(l);

        mLogDatabase.insert(LogTable.NAME, null, values);
    }

    public List<Log> getLogs() {
        List<Log> logs = new ArrayList<>();

        LogCursorWrapper cursor = queryLogs(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                logs.add(cursor.getLog());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return logs;
    }

    public List<Log> getLogsDay(Date date) {
        List<Log> logs = new ArrayList<>();

        /**
         * We create a string object dateText in the form of YYYYMMDD in order to search database for specific day's logs
         */
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        String dateText = year.toString() + month.toString() + day.toString();

        LogCursorWrapper cursor = queryLogs(LogTable.Cols.DATETEXT + " = ?", new String[] {dateText});

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                logs.add(cursor.getLog());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return logs;
    }

    public Log getLog(UUID id) {
        LogCursorWrapper cursor = queryLogs(LogTable.Cols.LOGID + " = ?", new String[] {id.toString()});

        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getLog();
        } finally {
            cursor.close();
        }
    }

    public void updateLog(Log log) {
        String uuidString = log.getLogId().toString();
        ContentValues values = getContentValues(log);

        mLogDatabase.update(LogTable.NAME, values, LogTable.Cols.LOGID + " = ?", new String[] {uuidString});
    }

    public void deleteLog(Log log) {
        String uuidString = log.getLogId().toString();

        mLogDatabase.delete(LogTable.NAME, LogTable.Cols.LOGID + " = ?", new String[] {uuidString});
    }

    private LogCursorWrapper queryLogs(String whereClause, String[] whereArgs) {
        Cursor cursor = mLogDatabase.query(
                LogTable.NAME,
                null, //columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new LogCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Log log) {
        ContentValues values = new ContentValues();
        values.put(LogTable.Cols.LOGID, log.getLogId().toString());
        values.put(LogTable.Cols.DATE, log.getDate().getTime());
        values.put(LogTable.Cols.DATETEXT, log.getDateText());
        values.put(LogTable.Cols.FOOD, log.getFood());
        values.put(LogTable.Cols.SIZE, log.getSize());
        values.put(LogTable.Cols.SIZEIMPERIAL, log.getSizeImperial());
        values.put(LogTable.Cols.KCAL, log.getKcal());
        values.put(LogTable.Cols.PROTEIN, log.getProtein());
        values.put(LogTable.Cols.CARBS, log.getCarbs());
        values.put(LogTable.Cols.FAT, log.getFat());

        return values;
    }
}
