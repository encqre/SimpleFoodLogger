package com.untrustworthypillars.simplefoodlogger;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.untrustworthypillars.simplefoodlogger.database.FoodCursorWrapper;
import com.untrustworthypillars.simplefoodlogger.database.CustomFoodDbHelper;
import com.untrustworthypillars.simplefoodlogger.database.CommonFoodDbHelper;
import com.untrustworthypillars.simplefoodlogger.database.ExtendedFoodDbHelper;
import com.untrustworthypillars.simplefoodlogger.database.DbSchema.CustomFoodTable;
import com.untrustworthypillars.simplefoodlogger.database.DbSchema.CommonFoodTable;
import com.untrustworthypillars.simplefoodlogger.database.DbSchema.ExtendedFoodTable;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        mRecentFoodsLength = Integer.parseInt(mPreferences.getString("pref_recent_foods_size", "10"));
    }

    //Add a custom food to CustomFoodDatabase
    public void addCustomFood(Food f) {
        ContentValues values = getContentValues(f);

        mCustomFoodDatabase.insert(CustomFoodTable.NAME, null, values);
    }

    public void addCommonFood(Food f) {
        ContentValues values = getContentValues(f);

        mCommonFoodDatabase.insert(CommonFoodTable.NAME, null, values);
    }

    public void addExtendedFood(Food f) {
        ContentValues values = getContentValues(f);

        mExtendedFoodDatabase.insert(ExtendedFoodTable.NAME, null, values);
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

        FoodCursorWrapper cursor = queryCustomFoods(CustomFoodTable.Cols.CATEGORY + " = ?", new String[] {category});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCustomFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryCommonFoods(CommonFoodTable.Cols.CATEGORY + " = ? AND " + CommonFoodTable.Cols.HIDDEN + " = 0", new String[] {category});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCommonFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return foods;
    }

    public List<Food> getFoodsFavorite() {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryCustomFoods(CustomFoodTable.Cols.FAVORITE + " = 1", null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCustomFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryCommonFoods(CommonFoodTable.Cols.FAVORITE + " = 1 AND " + CommonFoodTable.Cols.HIDDEN + " = 0", null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCommonFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryExtendedFoods(ExtendedFoodTable.Cols.FAVORITE + " = 1 AND " + ExtendedFoodTable.Cols.HIDDEN + " = 0", null);

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

    public List<Food> getFoodsSearch(String searchString, boolean includeExtended) {
        List<Food> foods = new ArrayList<>();

        String [] searchWordsArray = searchString.split("\\s+");
        String queryWhereClause = "";
        for (int i=0; i<searchWordsArray.length; i++) {
            android.util.Log.d("SEARCHARRAY", searchWordsArray[i]);
            if (searchWordsArray[i] != "" && searchWordsArray[i].length() > 1) { //Not including empty strings or single letter words into search words
                searchWordsArray[i] = "\"%" + searchWordsArray[i] + "%\"";
                if (queryWhereClause.length() < 1) {
                    queryWhereClause = queryWhereClause + CustomFoodTable.Cols.TITLE + " LIKE " + searchWordsArray[i];
                } else {
                    queryWhereClause = queryWhereClause + " AND " + CustomFoodTable.Cols.TITLE + " LIKE " + searchWordsArray[i];
                }
            }
        }

        if (queryWhereClause.equals("")) {
            return foods;
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

        cursor = queryCommonFoods(queryWhereClause + " AND " + CommonFoodTable.Cols.HIDDEN + " = 0", null);

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
            cursor = queryExtendedFoods(queryWhereClause + " AND " + ExtendedFoodTable.Cols.HIDDEN + " = 0", null);

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
        FoodCursorWrapper cursor = queryCustomFoods(CustomFoodTable.Cols.FOODID + " = ?", new String[] {id.toString()});

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
        FoodCursorWrapper cursor = queryCommonFoods(CommonFoodTable.Cols.FOODID + " = ?", new String[] {id.toString()});

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
        FoodCursorWrapper cursor = queryExtendedFoods(ExtendedFoodTable.Cols.FOODID + " = ?", new String[] {id.toString()});

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
        FoodCursorWrapper cursor = queryCustomFoods(CustomFoodTable.Cols.TITLE + " = ?", new String[] {foodName});

        try {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getCustomFood();
            }
        } finally {
            cursor.close();
        }
        cursor = queryCommonFoods(CommonFoodTable.Cols.TITLE + " = ?", new String[] {foodName});

        try {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getCommonFood();
            }
        } finally {
            cursor.close();
        }

        cursor = queryExtendedFoods(ExtendedFoodTable.Cols.TITLE + " = ?", new String[] {foodName});

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
            android.util.Log.d("SEARCHARRAY", searchWordsArray[i]);
            if (searchWordsArray[i] != "" && searchWordsArray[i].length() > 1) { //Not including empty strings or single letter words into search words
                searchWordsArray[i] = "\"%" + searchWordsArray[i] + "%\"";
                if (queryWhereClause.length() < 1) {
                    queryWhereClause = queryWhereClause + CustomFoodTable.Cols.HIDDEN + " = 1 AND " + CustomFoodTable.Cols.TITLE + " LIKE " + searchWordsArray[i];
                } else {
                    queryWhereClause = queryWhereClause + " AND " + CustomFoodTable.Cols.TITLE + " LIKE " + searchWordsArray[i];
                }
            }
        }

        FoodCursorWrapper cursor;

        if (queryWhereClause.equals("")) {
            cursor = queryCommonFoods(CommonFoodTable.Cols.HIDDEN + " = 1", null);

            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    foods.add(cursor.getCommonFood());
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }

            cursor = queryExtendedFoods(ExtendedFoodTable.Cols.HIDDEN + " = 1", null);

            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    foods.add(cursor.getExtendedFood());
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        } else {

            cursor = queryCommonFoods(queryWhereClause, null);

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
        } else {
            android.util.Log.e("FAIL", "Failed to update food - incorrect type value: " + String.valueOf(food.getType()));
        }

    }

    public void updateCustomFood(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = getContentValues(food);

        mCustomFoodDatabase.update(CustomFoodTable.NAME, values, CustomFoodTable.Cols.FOODID + " = ?", new String[] {uuidString});
    }

    public void updateCommonFood(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = getContentValues(food);

        mCommonFoodDatabase.update(CommonFoodTable.NAME, values, CommonFoodTable.Cols.FOODID + " = ?", new String[] {uuidString});
    }

    public void updateExtendedFood(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = getContentValues(food);

        mExtendedFoodDatabase.update(ExtendedFoodTable.NAME, values, ExtendedFoodTable.Cols.FOODID + " = ?", new String[] {uuidString});
    }

    public void deleteCustomFood(Food food) {
        String uuidString = food.getFoodId().toString();

        mCustomFoodDatabase.delete(CustomFoodTable.NAME, CustomFoodTable.Cols.FOODID + " = ?", new String[] {uuidString});
    }

    private FoodCursorWrapper queryCustomFoods(String whereClause, String[] whereArgs) {
        Cursor cursor = mCustomFoodDatabase.query(
                CustomFoodTable.NAME,
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
                CommonFoodTable.NAME,
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
                ExtendedFoodTable.NAME,
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

        values.put(CustomFoodTable.Cols.FOODID, food.getFoodId().toString());
        values.put(CustomFoodTable.Cols.SORTID, food.getSortID());
        values.put(CustomFoodTable.Cols.TITLE, food.getTitle());
        values.put(CustomFoodTable.Cols.CATEGORY, food.getCategory());
        values.put(CustomFoodTable.Cols.KCAL, food.getKcal());
        values.put(CustomFoodTable.Cols.PROTEIN, food.getProtein());
        values.put(CustomFoodTable.Cols.CARBS, food.getCarbs());
        values.put(CustomFoodTable.Cols.FAT, food.getFat());
        values.put(CustomFoodTable.Cols.FAVORITE, food.isFavorite() ? 1 : 0);
        values.put(CustomFoodTable.Cols.HIDDEN, food.isHidden() ? 1 : 0);
        values.put(CustomFoodTable.Cols.PORTION1NAME, food.getPortion1Name());
        values.put(CustomFoodTable.Cols.PORTION1SIZEMETRIC, food.getPortion1SizeMetric());
        values.put(CustomFoodTable.Cols.PORTION1SIZEIMPERIAL, food.getPortion1SizeImperial());
        values.put(CustomFoodTable.Cols.PORTION2NAME, food.getPortion2Name());
        values.put(CustomFoodTable.Cols.PORTION2SIZEMETRIC, food.getPortion2SizeMetric());
        values.put(CustomFoodTable.Cols.PORTION2SIZEIMPERIAL, food.getPortion2SizeImperial());
        values.put(CustomFoodTable.Cols.PORTION3NAME, food.getPortion3Name());
        values.put(CustomFoodTable.Cols.PORTION3SIZEMETRIC, food.getPortion3SizeMetric());
        values.put(CustomFoodTable.Cols.PORTION3SIZEIMPERIAL, food.getPortion3SizeImperial());

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

    public List<Food> getRecentFoods() {

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

        android.util.Log.i("ayyy", String.valueOf(recentFoodList.size()));
        return recentFoodList;
    }

}
