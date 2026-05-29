package lt.jasinevicius.simplefoodlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lt.jasinevicius.simplefoodlogger.database.LogCursorWrapper;
import lt.jasinevicius.simplefoodlogger.database.LogDbHelper;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.Logs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class LogManager {
    private static LogManager sLogManager;

    private Context context;
    private SQLiteDatabase logDb;

    public static LogManager get(Context context) {
        if (sLogManager == null) {
            sLogManager = new LogManager(context);
        }
        return sLogManager;
    }

    private LogManager(Context ctx) {
        context = ctx.getApplicationContext();
        logDb = new LogDbHelper(context).getWritableDatabase();
    }

    public void addLog(Log l) {
        ContentValues values = getContentValues(l);

        logDb.insert(Logs.NAME, null, values);
    }

    public List<Log> getLogs(
        String whereClause,
        String[] whereArgs,
        String orderBy
    ) {
        List<Log> logs = new ArrayList<>();

        LogCursorWrapper cursor = queryLogs(whereClause, whereArgs, orderBy);

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

    public List<Log> getLogsForDay(Date date) {
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

        String whereClause = Logs.Cols.DATE + " >= " + startTime + " AND " +
            Logs.Cols.DATE + " < " + endTime;

        return getLogs(whereClause, null, null);
    }

    public Log getLog(UUID id) {

        String whereClause = Logs.Cols.LOG_ID + " = ?";
        String[] whereArgs = {id.toString()};

        List<Log> logs = getLogs(whereClause, whereArgs, null);

        if (logs.size() >= 1) {
            return logs.get(0);
        } else {
            return null;
        }
    }

    public void updateLog(Log log) {
        String uuidString = log.getLogId().toString();
        ContentValues values = getContentValues(log);

        logDb.update(            Logs.NAME,
            values,
            Logs.Cols.LOG_ID + " = ?",
            new String[] {uuidString}
        );
    }

    public void deleteLog(Log log) {
        String uuidString = log.getLogId().toString();

        logDb.delete(Logs.NAME, Logs.Cols.LOG_ID + " = ?", new String[] {uuidString});
    }

    private LogCursorWrapper queryLogs(String whereClause, String[] whereArgs, String orderBy) {
        Cursor cursor = logDb.query(
                Logs.NAME,
                null, //columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                orderBy
        );
        return new LogCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Log log) {
        ContentValues values = new ContentValues();
        values.put(Logs.Cols.LOG_ID, log.getLogId().toString());
        values.put(Logs.Cols.DATE, log.getDate().getTime());
        values.put(Logs.Cols.FOOD, log.getFood());
        values.put(Logs.Cols.SIZE, log.getSize());
        values.put(Logs.Cols.KCAL, log.getKcal());
        values.put(Logs.Cols.PROTEIN, log.getProtein());
        values.put(Logs.Cols.CARBS, log.getCarbs());
        values.put(Logs.Cols.FAT, log.getFat());

        return values;
    }
}
