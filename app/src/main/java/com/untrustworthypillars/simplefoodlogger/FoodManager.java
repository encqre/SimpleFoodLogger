package com.untrustworthypillars.simplefoodlogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.untrustworthypillars.simplefoodlogger.database.DbSchema;
import com.untrustworthypillars.simplefoodlogger.database.FoodCursorWrapper;
import com.untrustworthypillars.simplefoodlogger.database.FoodDbHelper;
import com.untrustworthypillars.simplefoodlogger.database.DbSchema.FoodTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FoodManager {
    private static FoodManager sFoodManager;

    private Context mContext;
    private SQLiteDatabase mFoodDatabase;

    private int recentsFoodLength = 5;

    public static FoodManager get(Context context) {
        if (sFoodManager == null) {
            sFoodManager = new FoodManager(context);
        }
        return sFoodManager;
    }

    private FoodManager(Context context) {
        mContext = context.getApplicationContext();
        mFoodDatabase = new FoodDbHelper(mContext).getWritableDatabase();
    }

    public void addFood(Food f) {
        ContentValues values = getContentValues(f);

        mFoodDatabase.insert(FoodTable.NAME, null, values);
    }

    public List<Food> getFoods() {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryFoods(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                foods.add(cursor.getFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return foods;
    }

    public List<Food> getFoodsCategory(String category) {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryFoods(FoodTable.Cols.CATEGORY + " = ?", new String[] {category});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return foods;
    }

    public List<Food> getFoodsFavorite() {
        List<Food> foods = new ArrayList<>();

        FoodCursorWrapper cursor = queryFoods(FoodTable.Cols.FAVORITE + " = 1", null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return foods;
    }

    public List<Food> getFoodsSearch(String searchString) {
        List<Food> foods = new ArrayList<>();

        String q = "%" + searchString + "%";

        FoodCursorWrapper cursor = queryFoods(FoodTable.Cols.TITLE + " LIKE ?", new String[] {q});

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                foods.add(cursor.getFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return foods;
    }

    public Food getFood(UUID id) {
        FoodCursorWrapper cursor = queryFoods(FoodTable.Cols.FOODID + " = ?", new String[] {id.toString()});

        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getFood();
        } finally {
            cursor.close();
        }
    }

    public void updateFood(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = getContentValues(food);

        mFoodDatabase.update(FoodTable.NAME, values, FoodTable.Cols.FOODID + " = ?", new String[] {uuidString});
    }

    public void deleteFood(Food food) {
        String uuidString = food.getFoodId().toString();

        mFoodDatabase.delete(FoodTable.NAME, FoodTable.Cols.FOODID + " = ?", new String[] {uuidString});
    }

    private FoodCursorWrapper queryFoods(String whereClause, String[] whereArgs) {
        Cursor cursor = mFoodDatabase.query(
                FoodTable.NAME,
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
        values.put(FoodTable.Cols.FOODID, food.getFoodId().toString());
        values.put(FoodTable.Cols.TITLE, food.getTitle());
        values.put(FoodTable.Cols.CATEGORY, food.getCategory());
        values.put(FoodTable.Cols.KCAL, food.getKcal());
        values.put(FoodTable.Cols.PROTEIN, food.getProtein());
        values.put(FoodTable.Cols.CARBS, food.getCarbs());
        values.put(FoodTable.Cols.FAT, food.getFat());
        values.put(FoodTable.Cols.FAVORITE, food.isFavorite() ? 1 : 0);

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
                recentFoodList.add(getFood(UUID.fromString(el[i])));
            }
        }

        return recentFoodList;
    }

}
