package com.untrustworthypillars.simplefoodlogger.database;

public class DbSchema {
    public static final class FoodTable {
        public static final String NAME = "foods";

        public static final class Cols {
            public static final String FOODID = "foodid";
            public static final String TITLE = "title";
            public static final String CATEGORY = "category";
            public static final String KCAL = "kcal";
            public static final String PROTEIN = "protein";
            public static final String CARBS = "carbs";
            public static final String FAT = "fat";
            public static final String FAVORITE = "favorite";
        }
    }

    public static final class LogTable {
        public static final String NAME = "logs";

        public static final class Cols {
            public static final String LOGID = "logid";
            public static final String DATE = "date";
            public static final String DATETEXT = "datetext";
            public static final String FOOD = "food";
            public static final String SIZE = "size";
            public static final String KCAL = "kcal";
            public static final String PROTEIN = "protein";
            public static final String CARBS = "carbs";
            public static final String FAT = "fat";
        }
    }
}
