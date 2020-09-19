package lt.jasinevicius.simplefoodlogger;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Log {

    private UUID mLogId;
    private Date mDate;
    private String mDateText;
    private String mFood;
    private Float mSize;
    private Float mSizeImperial;
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

    public Float getSizeImperial() {
        return mSizeImperial;
    }

    public void setSizeImperial(Float sizeImperial) {
        mSizeImperial = sizeImperial;
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

    public static List<Log> sortByDateOld(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                int o1date = Calculations.dateToDateTextEqualLengthInteger(o1.getDate());
                int o2date = Calculations.dateToDateTextEqualLengthInteger(o2.getDate());
                if (o1date > o2date) {
                    return 1;
                } else if (o1date < o2date) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByDateNew(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                int o1date = Calculations.dateToDateTextEqualLengthInteger(o1.getDate());
                int o2date = Calculations.dateToDateTextEqualLengthInteger(o2.getDate());
                if (o1date > o2date) {
                    return -1;
                } else if (o1date < o2date) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByKcalHigh(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                if (o1.getKcal() > o2.getKcal()) {
                    return -1;
                } else if (o1.getKcal() < o2.getKcal()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByKcalLow(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                if (o1.getKcal() > o2.getKcal()) {
                    return 1;
                } else if (o1.getKcal() < o2.getKcal()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByProteinHigh(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                if (o1.getProtein() > o2.getProtein()) {
                    return -1;
                } else if (o1.getProtein() < o2.getProtein()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByProteinLow(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                if (o1.getProtein() > o2.getProtein()) {
                    return 1;
                } else if (o1.getProtein() < o2.getProtein()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByCarbsHigh(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                if (o1.getCarbs() > o2.getCarbs()) {
                    return -1;
                } else if (o1.getCarbs() < o2.getCarbs()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByCarbsLow(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                if (o1.getCarbs() > o2.getCarbs()) {
                    return 1;
                } else if (o1.getCarbs() < o2.getCarbs()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByFatHigh(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                if (o1.getFat() > o2.getFat()) {
                    return -1;
                } else if (o1.getFat() < o2.getFat()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }

    public static List<Log> sortByFatLow(List<Log> summaryLogs) {
        Collections.sort(summaryLogs, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                if (o1.getFat() > o2.getFat()) {
                    return 1;
                } else if (o1.getFat() < o2.getFat()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return summaryLogs;
    }
}
