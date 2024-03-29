package lt.jasinevicius.simplefoodlogger;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import lt.jasinevicius.simplefoodlogger.database.FoodCursorWrapper;
import lt.jasinevicius.simplefoodlogger.database.CustomFoodDbHelper;
import lt.jasinevicius.simplefoodlogger.database.CommonFoodDbHelper;
import lt.jasinevicius.simplefoodlogger.database.ExtendedFoodDbHelper;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lt.jasinevicius.simplefoodlogger.database.DbSchema;

public class FoodManager {
    private static FoodManager sFoodManager;

    private Context mContext;
    private SQLiteDatabase mCustomFoodDatabase;
    private SQLiteDatabase mCommonFoodDatabase;
    private SQLiteDatabase mExtendedFoodDatabase;

    private SharedPreferences mPreferences;
    private int mRecentFoodsLength;

    public static FoodManager get(Context context) {
        if (sFoodManager == null) {
            sFoodManager = new FoodManager(context);
        }
        return sFoodManager;
    }

    private FoodManager(Context context) {
        mContext = context.getApplicationContext();
        mCustomFoodDatabase = new CustomFoodDbHelper(mContext).getWritableDatabase();
        mCommonFoodDatabase = new CommonFoodDbHelper(mContext).getWritableDatabase();
        mExtendedFoodDatabase = new ExtendedFoodDbHelper(mContext).getWritableDatabase();
        mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        mRecentFoodsLength = Integer.parseInt(
                mPreferences.getString(
                        LoggerSettings.PREFERENCE_RECENT_FOODS_SIZE,
                        LoggerSettings.PREFERENCE_RECENT_FOODS_SIZE_DEFAULT
                )
        );
    }

    //Add a custom food to CustomFoodDatabase
    public void addCustomFood(Food f) {
        ContentValues values = getContentValues(f);

        mCustomFoodDatabase.insert(DbSchema.CustomFoodTable.NAME, null, values);
    }

    public void addCommonFood(Food f) {
        ContentValues values = getContentValues(f);

        mCommonFoodDatabase.insert(DbSchema.CommonFoodTable.NAME, null, values);
    }

    public void addExtendedFood(Food f) {
        ContentValues values = getContentValues(f);

        mExtendedFoodDatabase.insert(DbSchema.ExtendedFoodTable.NAME, null, values);
    }

    public List<Food> getCustomFoods() {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryCustomFoods(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                foods.add(cursor.getCustomFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return foods;
    }

    public List<Food> getCommonFoods() {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryCommonFoods(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                foods.add(cursor.getCommonFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return foods;

    }

    public List<Food> getExtendedFoods() {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryExtendedFoods(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                foods.add(cursor.getExtendedFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return foods;

    }

    public List<Food> getFoodsCategory(String category) {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryCustomFoods(
            DbSchema.CustomFoodTable.Cols.CATEGORY + " = ?",
            new String[] {category}
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCustomFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryCommonFoods(
            DbSchema.CommonFoodTable.Cols.CATEGORY + " = ? AND " + DbSchema.CommonFoodTable.Cols.HIDDEN + " = 0",
            new String[] {category}
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCommonFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryExtendedFoods(
            DbSchema.ExtendedFoodTable.Cols.CATEGORY + " = ? AND " + DbSchema.ExtendedFoodTable.Cols.HIDDEN + " = 0",
            new String[] {category}
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getExtendedFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return foods;
    }

    public List<Food> getFoodsFavorite() {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryCustomFoods(
                DbSchema.CustomFoodTable.Cols.FAVORITE + " = 1",
                null
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCustomFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryCommonFoods(
                DbSchema.CommonFoodTable.Cols.FAVORITE + " = 1 AND " + DbSchema.CommonFoodTable.Cols.HIDDEN + " = 0",
                null
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCommonFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryExtendedFoods(
                DbSchema.ExtendedFoodTable.Cols.FAVORITE + " = 1 AND " + DbSchema.ExtendedFoodTable.Cols.HIDDEN + " = 0",
                null
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getExtendedFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return foods;
    }

    public List<Food> getFoodsSearch(
            String searchString,
            boolean includeExtended,
            boolean onlyIncludeFavorites,
            boolean onlyIncludeRecent,
            String category
    ) {
        List<Food> foods = new ArrayList<>();

        String [] searchWordsArray = searchString.split("\\s+");
        String queryWhereClause = "";
        for (int i=0; i<searchWordsArray.length; i++) {
            //Not including empty strings or single letter words into search words
            if (searchWordsArray[i] != "" && searchWordsArray[i].length() > 0) {
                searchWordsArray[i] = "\"%" + searchWordsArray[i] + "%\"";
                if (queryWhereClause.length() < 1) {
                    queryWhereClause += DbSchema.CustomFoodTable.Cols.TITLE + " LIKE " + searchWordsArray[i];
                } else {
                    queryWhereClause += " AND " + DbSchema.CustomFoodTable.Cols.TITLE + " LIKE " + searchWordsArray[i];
                }
            }
        }

        if (queryWhereClause.equals("")) {
            return foods;
        } else {
            if (onlyIncludeRecent) {
                return getRecentFoods(searchString);
            }
            if (onlyIncludeFavorites) {
                queryWhereClause += " AND " + DbSchema.CustomFoodTable.Cols.FAVORITE + " = 1";
            }
            if (!category.equals("")) {
                queryWhereClause += " AND " + DbSchema.CustomFoodTable.Cols.CATEGORY + " LIKE \"%" + category + "%\"";
            }
        }
        FoodCursorWrapper cursor = queryCustomFoods(queryWhereClause, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCustomFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryCommonFoods(
                queryWhereClause + " AND " + DbSchema.CommonFoodTable.Cols.HIDDEN + " = 0",
                null
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCommonFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        if (includeExtended) {
            cursor = queryExtendedFoods(
                    queryWhereClause + " AND " + DbSchema.ExtendedFoodTable.Cols.HIDDEN + " = 0",
                    null
            );

            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    foods.add(cursor.getExtendedFood());
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }

        return foods;
    }

    public Food getFood(UUID id, int foodType) {
        Food food;
        if (foodType == 0) {
            food = getCustomFood(id);
        } else if (foodType == 1) {
            food = getCommonFood(id);
        } else if (foodType == 2) {
            food = getExtendedFood(id);
        } else {
            food = null;
        }
        return food;
    }

    public Food getCustomFood(UUID id) {
        FoodCursorWrapper cursor = queryCustomFoods(
                DbSchema.CustomFoodTable.Cols.FOODID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCustomFood();
        } finally {
            cursor.close();
        }
    }

    public Food getCommonFood(UUID id) {
        FoodCursorWrapper cursor = queryCommonFoods(
                DbSchema.CommonFoodTable.Cols.FOODID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCommonFood();
        } finally {
            cursor.close();
        }
    }

    public Food getExtendedFood(UUID id) {
        FoodCursorWrapper cursor = queryExtendedFoods(
                DbSchema.ExtendedFoodTable.Cols.FOODID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getExtendedFood();
        } finally {
            cursor.close();
        }
    }

    public Food getFoodByName(String foodName) {
        FoodCursorWrapper cursor = queryCustomFoods(
                DbSchema.CustomFoodTable.Cols.TITLE + " = ?",
                new String[] {foodName}
        );

        try {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getCustomFood();
            }
        } finally {
            cursor.close();
        }
        cursor = queryCommonFoods(DbSchema.CommonFoodTable.Cols.TITLE + " = ?", new String[] {foodName});

        try {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getCommonFood();
            }
        } finally {
            cursor.close();
        }

        cursor = queryExtendedFoods(DbSchema.ExtendedFoodTable.Cols.TITLE + " = ?", new String[] {foodName});

        try {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getExtendedFood();
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }

    }

    public List<Food> getHiddenFoods(String filterString) {
        List<Food> foods = new ArrayList<>();

        String [] searchWordsArray = filterString.split("\\s+");
        String queryWhereClause = "";
        for (int i=0; i<searchWordsArray.length; i++) {
            //Not including empty strings or single letter words into search words
            if (searchWordsArray[i] != "" && searchWordsArray[i].length() > 1) {
                searchWordsArray[i] = "\"%" + searchWordsArray[i] + "%\"";
                if (queryWhereClause.length() < 1) {
                    queryWhereClause += DbSchema.CustomFoodTable.Cols.HIDDEN + " = 1 AND " +
                            DbSchema.CustomFoodTable.Cols.TITLE + " LIKE " + searchWordsArray[i];
                } else {
                    queryWhereClause += " AND " + DbSchema.CustomFoodTable.Cols.TITLE + " LIKE " + searchWordsArray[i];
                }
            }
        }

        queryWhereClause = queryWhereClause.equals("") ? DbSchema.CommonFoodTable.Cols.HIDDEN + " = 1" : queryWhereClause;

        FoodCursorWrapper cursor = queryCommonFoods(queryWhereClause, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCommonFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryExtendedFoods(queryWhereClause, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getExtendedFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        
        return foods;
    }

    public void updateFood(Food food) {
        if (food.getType() == 0) {
            updateCustomFood(food);
        } else if (food.getType() == 1) {
            updateCommonFood(food);
        } else if (food.getType() == 2) {
            updateExtendedFood(food);
        }
    }

    public void updateCustomFood(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = getContentValues(food);

        mCustomFoodDatabase.update(
                DbSchema.CustomFoodTable.NAME,
                values,
                DbSchema.CustomFoodTable.Cols.FOODID + " = ?",
                new String[] {uuidString}
        );
    }

    public void updateCommonFood(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = getContentValues(food);

        mCommonFoodDatabase.update(
                DbSchema.CommonFoodTable.NAME,
                values,
                DbSchema.CommonFoodTable.Cols.FOODID + " = ?",
                new String[] {uuidString}
        );
    }

    public void updateExtendedFood(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = getContentValues(food);

        mExtendedFoodDatabase.update(
                DbSchema.ExtendedFoodTable.NAME,
                values,
                DbSchema.ExtendedFoodTable.Cols.FOODID + " = ?",
                new String[] {uuidString}
        );
    }

    public void deleteCustomFood(Food food) {
        String uuidString = food.getFoodId().toString();

        mCustomFoodDatabase.delete(
                DbSchema.CustomFoodTable.NAME,
                DbSchema.CustomFoodTable.Cols.FOODID + " = ?",
                new String[] {uuidString}
        );
    }

    private FoodCursorWrapper queryCustomFoods(String whereClause, String[] whereArgs) {
        Cursor cursor = mCustomFoodDatabase.query(
                DbSchema.CustomFoodTable.NAME,
                null, //columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new FoodCursorWrapper(cursor);
    }

    private FoodCursorWrapper queryCommonFoods(String whereClause, String[] whereArgs) {
        Cursor cursor = mCommonFoodDatabase.query(
                DbSchema.CommonFoodTable.NAME,
                null, //columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new FoodCursorWrapper(cursor);
    }

    private FoodCursorWrapper queryExtendedFoods(String whereClause, String[] whereArgs) {
        Cursor cursor = mExtendedFoodDatabase.query(
                DbSchema.ExtendedFoodTable.NAME,
                null, //columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new FoodCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Food food) {
        ContentValues values = new ContentValues();

        values.put(DbSchema.CustomFoodTable.Cols.FOODID, food.getFoodId().toString());
        values.put(DbSchema.CustomFoodTable.Cols.SORTID, food.getSortID());
        values.put(DbSchema.CustomFoodTable.Cols.TITLE, food.getTitle());
        values.put(DbSchema.CustomFoodTable.Cols.CATEGORY, food.getCategory());
        values.put(DbSchema.CustomFoodTable.Cols.KCAL, food.getKcal());
        values.put(DbSchema.CustomFoodTable.Cols.PROTEIN, food.getProtein());
        values.put(DbSchema.CustomFoodTable.Cols.CARBS, food.getCarbs());
        values.put(DbSchema.CustomFoodTable.Cols.FAT, food.getFat());
        values.put(DbSchema.CustomFoodTable.Cols.FAVORITE, food.isFavorite() ? 1 : 0);
        values.put(DbSchema.CustomFoodTable.Cols.HIDDEN, food.isHidden() ? 1 : 0);
        values.put(DbSchema.CustomFoodTable.Cols.PORTION1NAME, food.getPortion1Name());
        values.put(DbSchema.CustomFoodTable.Cols.PORTION1SIZEMETRIC, food.getPortion1SizeMetric());
        values.put(DbSchema.CustomFoodTable.Cols.PORTION1SIZEIMPERIAL, food.getPortion1SizeImperial());
        values.put(DbSchema.CustomFoodTable.Cols.PORTION2NAME, food.getPortion2Name());
        values.put(DbSchema.CustomFoodTable.Cols.PORTION2SIZEMETRIC, food.getPortion2SizeMetric());
        values.put(DbSchema.CustomFoodTable.Cols.PORTION2SIZEIMPERIAL, food.getPortion2SizeImperial());
        values.put(DbSchema.CustomFoodTable.Cols.PORTION3NAME, food.getPortion3Name());
        values.put(DbSchema.CustomFoodTable.Cols.PORTION3SIZEMETRIC, food.getPortion3SizeMetric());
        values.put(DbSchema.CustomFoodTable.Cols.PORTION3SIZEIMPERIAL, food.getPortion3SizeImperial());

        return values;
    }

    /*Function to add a food to the recent Food List, which is stored as a string of Food UUIDs separated by ";"
      Shared Preferences - key: "recent_foods" */
    public void addToRecentFoods(Food food) {
        String foodId = food.getFoodId().toString();

        /*Retrieving currently stored recent food list string from SharedPreferences, also,
        * creating new string to which new food will be added/updated, because SharedPreferences
        * does not really allow to edit the retrieved value or some bullshit*/
        String recentFoodString = PreferenceManager.getDefaultSharedPreferences(mContext).getString("recent_foods", null);
        String recentFoodString2;


        if (recentFoodString == null) {
            recentFoodString2 = foodId + ";"; //if recent foods string does not exist, just create new one and add this one food
        } else {
            String[] el = recentFoodString.split(";"); //splitting the string of UUIDs into array

            /*If food item is already on the list, then the list size will stay the same. Add this food to the
            * beggining and then loop through the array, ignoring the one element which contains that food*/
            if (recentFoodString.contains(foodId)) {
                recentFoodString2 = foodId + ";";

                for (int i = 0; i<el.length; i++) {
                    if (!el[i].equals(foodId)) {
                        recentFoodString2 = recentFoodString2 + el[i] + ";";
                    }
                }

                /*If food item is not on the list, then there are two options  - if list size is less than max length,
                * then we can just add food to beggining and append previous list to the end. Otherwise,
                * add food item to the beggining and iterate through all other foods, except last one and add them to the string*/
            } else {
                if (el.length < mRecentFoodsLength) {
                    recentFoodString2 = foodId + ";" + recentFoodString;
                } else {
                    recentFoodString2 = foodId + ";";
                    for (int i = 0; i<(mRecentFoodsLength -1); i++) {
                        recentFoodString2 = recentFoodString2 + el[i] + ";";
                    }
                }
            }
        }

        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("recent_foods", recentFoodString2).apply();
    }

    public List<Food> getRecentFoods(String query) {

        String recentFoodString = PreferenceManager.getDefaultSharedPreferences(mContext).getString("recent_foods", null);
        List<Food> recentFoodList = new ArrayList<>();
        if (recentFoodString != null) {
            String[] el = recentFoodString.split(";");
            /* Checking if any food was found with the UUID in custom, common and extended DBs.
            If yes, and if it is not hidden, it is added to recent foods list. If no, nothing is added */
            for (int i =0; i<el.length; i++) {
                if (getCustomFood(UUID.fromString(el[i])) != null) {
                    recentFoodList.add(getCustomFood(UUID.fromString(el[i])));
                }
                else if (getCommonFood(UUID.fromString(el[i])) != null) {
                    if (!getCommonFood(UUID.fromString(el[i])).isHidden()) {
                        recentFoodList.add(getCommonFood(UUID.fromString(el[i])));
                    }
                }
                else if (getExtendedFood(UUID.fromString(el[i])) != null){
                    if (!getExtendedFood(UUID.fromString(el[i])).isHidden()) {
                        recentFoodList.add(getExtendedFood(UUID.fromString(el[i])));
                    }
                }
            }
        }
        //if 'query' is not empty, filter out only those recent foods that contain the 'query' words
        if (!query.equals("")) {
            String [] filterWordsArray = query.toLowerCase().split("\\s+");
            List<Food> filteredRecentFoodList = new ArrayList<>();
            for (int i = 0; i<recentFoodList.size(); i++) {
                boolean remove = false;
                for (int z = 0; z<filterWordsArray.length; z++) {
                    if (!recentFoodList.get(i).getTitle().toLowerCase().contains(filterWordsArray[z])) {
                        remove = true;
                        break;
                    }
                }
                if (!remove) {
                    filteredRecentFoodList.add(recentFoodList.get(i));
                }
            }
            recentFoodList = filteredRecentFoodList;
        }
        return recentFoodList;
    }

}
