package com.untrustworthypillars.simplefoodlogger;

import java.util.Date;

public class FoodSummary {

    private String mName;
    private Integer mCount;
    private Float mWeight;
    private Float mCalories;
    private Date mLastConsumed;
    private Integer mLastConsumedInteger;

    public FoodSummary(String name) {
        mName = name;
        mCount = 0;
        mWeight = 0f;
        mCalories = 0f;
        mLastConsumed = new Date(0);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Integer getCount() {
        return mCount;
    }

    public void setCount(Integer count) {
        mCount = count;
    }

    public Float getWeight() {
        return mWeight;
    }

    public void setWeight(Float weight) {
        mWeight = weight;
    }

    public Float getCalories() {
        return mCalories;
    }

    public void setCalories(Float calories) {
        mCalories = calories;
    }

    public Date getLastConsumed() {
        return mLastConsumed;
    }

    public void setLastConsumed(Date lastConsumed) {
        mLastConsumed = lastConsumed;
        mLastConsumedInteger = Calculations.dateToDateTextEqualLengthInteger(lastConsumed);
    }

    public Integer getLastConsumedInteger() {
        return mLastConsumedInteger;
    }
}
