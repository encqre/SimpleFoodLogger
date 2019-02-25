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



}
