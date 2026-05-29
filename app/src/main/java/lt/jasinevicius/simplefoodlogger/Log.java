package lt.jasinevicius.simplefoodlogger;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Log {

    private UUID logId;
    private Date date;
    private String food;
    private Float size;
    private Float kcal;
    private Float protein;
    private Float carbs;
    private Float fat;

    public Log() {
        this(UUID.randomUUID());
    }

    public Log(UUID id) {
        logId = id;
    }

    public UUID getLogId() {
        return logId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public Float getKcal() {
        return kcal;
    }

    public void setKcal(Float kcal) {
        this.kcal = kcal;
    }

    public Float getProtein() {
        return protein;
    }

    public void setProtein(Float protein) {
        this.protein = protein;
    }

    public Float getCarbs() {
        return carbs;
    }

    public void setCarbs(Float carbs) {
        this.carbs = carbs;
    }

    public Float getFat() {
        return fat;
    }

    public void setFat(Float fat) {
        this.fat = fat;
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
