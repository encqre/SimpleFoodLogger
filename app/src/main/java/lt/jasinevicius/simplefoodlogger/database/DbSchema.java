package lt.jasinevicius.simplefoodlogger.database;

public class DbSchema {
    public static final class Logs {
        public static final String NAME = "logs";

        public static final class Cols {
            public static final String LOG_ID = "log_id";
            public static final String DATE = "date";
            public static final String FOOD = "food";
            public static final String SIZE = "size";
            public static final String KCAL = "kcal";
            public static final String PROTEIN = "protein";
            public static final String CARBS = "carbs";
            public static final String FAT = "fat";
        }
    }

    public static final class Foods {
        public static final String NAME = "foods";

        public static final class Cols {
            public static final String FOOD_ID = "food_id";
            public static final String NAME = "name";
            public static final String KCAL = "kcal";
            public static final String PROTEIN = "protein";
            public static final String CARBS = "carbs";
            public static final String FAT = "fat";
            public static final String TYPE = "type";
            public static final String PRIORITY = "priority";
            public static final String FAVORITE = "favorite";
            public static final String CONSUMED_COUNT = "consumed_count";
            public static final String LAST_CONSUMED = "last_consumed";
        }
    }

    public static final class Tags {
        public static final String NAME = "tags";

        public static final class Cols {
            public static final String TAG_ID = "tag_id";
            public static final String NAME = "name";
            public static final String TYPE = "type";
            public static final String ORDER_ID = "order_id";
            public static final String COLOR = "color";
        }
    }

    // Join table for many-to-many relationship
    public static final class FoodTags {
        public static final String NAME = "food_tags";

        public static final class Cols {
            public static final String FOOD_ID = "food_id";
            public static final String TAG_ID = "tag_id";
        }
    }

    public static final class FoodServings {
        public static final String NAME = "food_servings";

        public static final class Cols {
            public static final String SERVING_ID = "serving_id";
            public static final String FOOD_ID = "food_id";
            public static final String NAME = "name";
            public static final String SIZE = "size";
            public static final String TYPE = "type";
        }
    }

    public static final class Meals {
        public static final String NAME = "meals";

        public static final class Cols {
            public static final String MEAL_ID = "meal_id";
            public static final String NAME = "name";
            public static final String FAVORITE = "favorite";
            public static final String CONSUMED_COUNT = "consumed_count";
            public static final String LAST_CONSUMED = "last_consumed";
        }
    }

    // Join table for many-to-many relationship
    public static final class MealFoods {
        public static final String NAME = "meal_foods";

        public static final class Cols {
            public static final String MEAL_ID = "meal_id";
            public static final String FOOD_ID = "food_id";
            public static final String DEFAULT_FOOD_SIZE = "default_food_size";
        }
    }

    // Join table for many-to-many relationship
    public static final class MealTags {
        public static final String NAME = "meal_tags";

        public static final class Cols {
            public static final String MEAL_ID = "meal_id";
            public static final String TAG_ID = "tag_id";
        }
    }

    public static final class History {
        public static final String NAME = "history";

        public static final class Cols {
            public static final String DATE = "date";
            public static final String WEIGHT = "weight";
            public static final String KCAL_TARGET = "kcal_target";
            public static final String PROTEIN_TARGET = "protein_target";
            public static final String CARBS_TARGET = "carbs_target";
            public static final String FAT_TARGET = "fat_target";
        }
    }
}
