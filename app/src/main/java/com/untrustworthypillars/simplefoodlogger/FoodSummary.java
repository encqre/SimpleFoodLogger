package com.untrustworthypillars.simplefoodlogger;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

    public static List<FoodSummary> sortByCountHigh(List<FoodSummary> summaryFoods) {
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getCount() > o2.getCount()) {
                    return -1;
                } else if (o1.getCount() < o2.getCount()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryFoods;
    }

    public static List<FoodSummary> sortByCountLow(List<FoodSummary> summaryFoods) {
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getCount() > o2.getCount()) {
                    return 1;
                } else if (o1.getCount() < o2.getCount()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryFoods;
    }

    public static List<FoodSummary> sortByKcalHigh(List<FoodSummary> summaryFoods) {
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getCalories() > o2.getCalories()) {
                    return -1;
                } else if (o1.getCalories() < o2.getCalories()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryFoods;
    }

    public static List<FoodSummary> sortByKcalLow(List<FoodSummary> summaryFoods) {
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getCalories() > o2.getCalories()) {
                    return 1;
                } else if (o1.getCalories() < o2.getCalories()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryFoods;
    }

    public static List<FoodSummary> sortByWeightHigh(List<FoodSummary> summaryFoods) {
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getWeight() > o2.getWeight()) {
                    return -1;
                } else if (o1.getWeight() < o2.getWeight()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryFoods;
    }

    public static List<FoodSummary> sortByWeightLow(List<FoodSummary> summaryFoods) {
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getWeight() > o2.getWeight()) {
                    return 1;
                } else if (o1.getWeight() < o2.getWeight()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryFoods;
    }

    public static List<FoodSummary> sortByDateNew(List<FoodSummary> summaryFoods) {
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getLastConsumedInteger() > o2.getLastConsumedInteger()) {
                    return -1;
                } else if (o1.getLastConsumedInteger() < o2.getLastConsumedInteger()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryFoods;
    }

    public static List<FoodSummary> sortByDateOld(List<FoodSummary> summaryFoods) {
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getLastConsumedInteger() > o2.getLastConsumedInteger()) {
                    return 1;
                } else if (o1.getLastConsumedInteger() < o2.getLastConsumedInteger()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryFoods;
    }
}
