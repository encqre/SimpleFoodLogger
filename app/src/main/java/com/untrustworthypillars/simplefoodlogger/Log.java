package com.untrustworthypillars.simplefoodlogger;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Log {

    private UUID mLogId;
    private Date mDate;
    private String mDateText;
    private String mFood;
    private Float mSize;
    private Float mKcal;
    private Float mProtein;
    private Float mCarbs;
    private Float mFat;

    public Log() {
        this(UUID.randomUUID());
    }

    public Log(UUID id) {
        mLogId = id;
    }

    public UUID getLogId() {
        return mLogId;
    }

    public Date getDate() {
        return mDate;
    }

    public String getDateText() {

        return mDateText;
    }

    public void setDateText() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH);
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        mDateText = year.toString() + month.toString() + day.toString();
    }

    public void setDateText(String text) {
        mDateText = text;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getFood() {
        return mFood;
    }

    public void setFood(String food) {
        mFood = food;
    }

    public Float getSize() {
        return mSize;
    }

    public void setSize(Float size) {
        mSize = size;
    }

    public Float getKcal() {
        return mKcal;
    }

    public void setKcal(Float kcal) {
        mKcal = kcal;
    }

    public Float getProtein() {
        return mProtein;
    }

    public void setProtein(Float protein) {
        mProtein = protein;
    }

    public Float getCarbs() {
        return mCarbs;
    }

    public void setCarbs(Float carbs) {
        mCarbs = carbs;
    }

    public Float getFat() {
        return mFat;
    }

    public void setFat(Float fat) {
        mFat = fat;
    }
}
