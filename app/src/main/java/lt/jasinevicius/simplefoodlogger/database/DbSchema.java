package lt.jasinevicius.simplefoodlogger.database;

public class DbSchema {
    public static final class CustomFoodTable {
        public static final String NAME = "customFoods";

        public static final class Cols {
            public static final String FOODID = "foodid";
            public static final String SORTID = "sortid";
            public static final String TITLE = "title";
            public static final String CATEGORY = "category";
            public static final String KCAL = "kcal";
            public static final String PROTEIN = "protein";
            public static final String CARBS = "carbs";
            public static final String FAT = "fat";
            public static final String FAVORITE = "favorite";
            public static final String HIDDEN = "hidden";
            public static final String PORTION1NAME = "portion1name";
            public static final String PORTION1SIZEMETRIC = "portion1sizemetric";
            public static final String PORTION1SIZEIMPERIAL = "portion1sizeimperial";
            public static final String PORTION2NAME = "portion2name";
            public static final String PORTION2SIZEMETRIC = "portion2sizemetric";
            public static final String PORTION2SIZEIMPERIAL = "portion2sizeimperial";
            public static final String PORTION3NAME = "portion3name";
            public static final String PORTION3SIZEMETRIC = "portion3sizemetric";
            public static final String PORTION3SIZEIMPERIAL = "portion3sizeimperial";

        }
    }

    public static final class CommonFoodTable {
        public static final String NAME = "commonFoods";

        public static final class Cols {
            public static final String FOODID = "foodid";
            public static final String SORTID = "sortid";
            public static final String TITLE = "title";
            public static final String CATEGORY = "category";
            public static final String KCAL = "kcal";
            public static final String PROTEIN = "protein";
            public static final String CARBS = "carbs";
            public static final String FAT = "fat";
            public static final String FAVORITE = "favorite";
            public static final String HIDDEN = "hidden";
            public static final String PORTION1NAME = "portion1name";
            public static final String PORTION1SIZEMETRIC = "portion1sizemetric";
            public static final String PORTION1SIZEIMPERIAL = "portion1sizeimperial";
            public static final String PORTION2NAME = "portion2name";
            public static final String PORTION2SIZEMETRIC = "portion2sizemetric";
            public static final String PORTION2SIZEIMPERIAL = "portion2sizeimperial";
            public static final String PORTION3NAME = "portion3name";
            public static final String PORTION3SIZEMETRIC = "portion3sizemetric";
            public static final String PORTION3SIZEIMPERIAL = "portion3sizeimperial";

        }
    }

    public static final class ExtendedFoodTable {
        public static final String NAME = "extendedFoods";

        public static final class Cols {
            public static final String FOODID = "foodid";
            public static final String SORTID = "sortid";
            public static final String TITLE = "title";
            public static final String CATEGORY = "category";
            public static final String KCAL = "kcal";
            public static final String PROTEIN = "protein";
            public static final String CARBS = "carbs";
            public static final String FAT = "fat";
            public static final String FAVORITE = "favorite";
            public static final String HIDDEN = "hidden";
            public static final String PORTION1NAME = "portion1name";
            public static final String PORTION1SIZEMETRIC = "portion1sizemetric";
            public static final String PORTION1SIZEIMPERIAL = "portion1sizeimperial";
            public static final String PORTION2NAME = "portion2name";
            public static final String PORTION2SIZEMETRIC = "portion2sizemetric";
            public static final String PORTION2SIZEIMPERIAL = "portion2sizeimperial";
            public static final String PORTION3NAME = "portion3name";
            public static final String PORTION3SIZEMETRIC = "portion3sizemetric";
            public static final String PORTION3SIZEIMPERIAL = "portion3sizeimperial";

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
            public static final String SIZEIMPERIAL = "sizeimperial";
            public static final String KCAL = "kcal";
            public static final String PROTEIN = "protein";
            public static final String CARBS = "carbs";
            public static final String FAT = "fat";
        }
    }
}
