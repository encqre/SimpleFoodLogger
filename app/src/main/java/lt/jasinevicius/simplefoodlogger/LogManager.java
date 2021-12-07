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

        // Get the timestamps for start and end of the day
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // zeroing out hours/minutes, etc. to get the exact start of the day timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long startTime = cal.getTimeInMillis();
        long endTime = startTime + 24 * 3600 * 1000;

        String queryWhereClause = LogTable.Cols.DATE + " >= " + startTime + " AND " +
                LogTable.Cols.DATE + " < " + endTime;
        LogCursorWrapper cursor = queryLogs(queryWhereClause,null);

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
