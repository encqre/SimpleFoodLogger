package com.untrustworthypillars.simplefoodlogger;

import android.content.ContentValues;
import android.content.Context;
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

    private int recentsFoodLength = 5; //TODO should make an option in settings

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

        cursor = queryCommonFoods(CommonFoodTable.Cols.CATEGORY + " = ?", new String[] {category});

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

        //TODO add another cursor here to also query common and extended tables for favorites

        return foods;
    }

    public List<Food> getFoodsSearch(String searchString) {
        List<Food> foods = new ArrayList<>();

        String q = "%" + searchString + "%";

        FoodCursorWrapper cursor = queryCustomFoods(CustomFoodTable.Cols.TITLE + " LIKE ?", new String[] {q});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getCustomFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        cursor = queryExtendedFoods(ExtendedFoodTable.Cols.TITLE + " LIKE ?", new String[] {q});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getExtendedFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        //TODO add another cursor for common foods. Also will need a checkbox to also search extended library, so if that one is checked will need cursor for extended db (maybe a different method then)

        return foods;
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


    public void updateCustomFood(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = getContentValues(food);

        mCustomFoodDatabase.update(CustomFoodTable.NAME, values, CustomFoodTable.Cols.FOODID + " = ?", new String[] {uuidString});
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
                if (el.length < recentsFoodLength) {
                    recentFoodString2 = foodId + ";" + recentFoodString;
                } else {
                    recentFoodString2 = foodId + ";";
                    for (int i = 0; i<(recentsFoodLength-1); i++) {
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
            for (int i =0; i<el.length; i++) {
                recentFoodList.add(getCustomFood(UUID.fromString(el[i]))); //TODO need to fix here: don't use getCustomFood method, but to use method to query all 3 tables/DBS - custom,common, and then extended if no find
            }
        }

        return recentFoodList;
    }

}
