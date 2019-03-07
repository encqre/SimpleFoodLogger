package com.untrustworthypillars.simplefoodlogger;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Calculations {

    /**
     * Returns formated date string for the provided Date object. If the year is not current year,
     * then the year is included in the string. If the date is today, then TODAY is returned instead
     * of date. Otherwise, returns day of the month, Month and day of the week.
     * @param date
     * @return formatted date string
     *
     */
    public static String dateDisplayString(Date date) {

        String output;

        Calendar cal =  Calendar.getInstance();
        Calendar calnow = Calendar.getInstance();
        cal.setTime(date);
        calnow.setTime(new Date());
        Integer year = cal.get(Calendar.YEAR);
        Integer yearnow = calnow.get(Calendar.YEAR);
        Integer day = cal.get(Calendar.DAY_OF_YEAR);
        Integer daynow = calnow.get(Calendar.DAY_OF_YEAR);
        android.util.Log.i("TAG",year.toString() + yearnow.toString() + day.toString() + daynow.toString());


        if (!year.toString().equals(yearnow.toString())) {
            output = DateFormat.format("dd MMMM yyyy, E", date).toString();
        } else if (day.toString().equals(daynow.toString())){
            output = "Today, " + DateFormat.format("E", date).toString();
        } else {
            output = DateFormat.format("dd MMMM, E", date).toString();
        }

        return output;
    }

    /**
     * Calculates total amount of calories, protein, carbs and fat for the provided list of Logs.
     * Result is a string array.
     * @param logs
     * @return total calories,protein,carbs and fats for provided logs (string format)
     */
    public static Float[] calculateKcalAndMacros(List<Log> logs) {
        Float[] result = new Float[4];
        Float kcal = 0f;
        Float protein = 0f;
        Float carbs = 0f;
        Float fat = 0f;

        for (int i = 0; i<logs.size(); i++) {
            Log log = logs.get(i);
            kcal = kcal + log.getKcal();
            protein = protein + log.getProtein();
            carbs = carbs + log.getCarbs();
            fat = fat + log.getFat();
        }

        result[0] = kcal;
        result[1] = protein;
        result[2] = carbs;
        result[3] = fat;

        return result;
    }

    /**
     * Converts date into a String in the format which is used in the Log Object (and used for database queries)
     * @param date
     * @return date in the string format - for example 2019.03.25 would be 20190225
     */
    public static String dateToDateText(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        String dateText = year.toString() + month.toString() + day.toString();

        return dateText;
    }

    public static Integer dateToDateTextEqualLengthInteger(Date date) {
        Calendar cal =  Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH) + 1;
        String monthString = month < 10 ? "0"+month.toString() : month.toString();
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        String dayString = day < 10 ? "0"+day.toString() : day.toString();
        String dateText = year.toString() + monthString + dayString;

        return Integer.parseInt(dateText);

    }

    public static String dateToDateTextEqualLengthString(Date date) {
        Calendar cal =  Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH) + 1;
        String monthString = month < 10 ? "0"+month.toString() : month.toString();
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        String dayString = day < 10 ? "0"+day.toString() : day.toString();
        String dateText = year.toString() + "." + monthString + "." + dayString;

        return dateText;

    }

    /**
     * Increments or decrements Date object by number of days specified by the amount variable
     * @param date
     * @param amount - amount of days by which to increment (or decrement) provided date
     * @return date which is incremented(decremented) by the specified number of days
     */
    public static Date incrementDay(Date date, Integer amount) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, amount);

        return cal.getTime();
    }

    /**
     * Summarizes List of Log objects (from the same day!!) into one summary Log object.
     * @param logs
     * @param logDate
     * @return summary Log object, which will contain total amount of calories, protein, carbs and fat.
     * Also will contain Date object of the day and DateText in the format of (for example) 2019.03.05 for march 5th.
     */
    public static Log summarizeDayLogs(List<Log> logs, Date logDate) {

        Float kcal = 0f;
        Float protein = 0f;
        Float carbs = 0f;
        Float fat = 0f;

        Calendar cal =  Calendar.getInstance();
        cal.setTime(logDate);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH) + 1;
        String monthString = month < 10 ? "0"+month.toString() : month.toString();
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        String dayString = day < 10 ? "0"+day.toString() : day.toString();
        String dateText = year.toString() + "." + monthString + "." + dayString;


        for (int i = 0; i<logs.size(); i++) {
            Log log = logs.get(i);
            kcal = kcal + log.getKcal();
            protein = protein + log.getProtein();
            carbs = carbs + log.getCarbs();
            fat = fat + log.getFat();
        }

        Log daysLog = new Log();
        daysLog.setKcal(kcal);
        daysLog.setProtein(protein);
        daysLog.setCarbs(carbs);
        daysLog.setFat(fat);
        daysLog.setDate(logDate);
        daysLog.setDateText(dateText);

        return daysLog;
    }

}
