package com.untrustworthypillars.simplefoodlogger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.untrustworthypillars.simplefoodlogger.Food;
import com.untrustworthypillars.simplefoodlogger.database.DbSchema.FoodTable;

import java.util.UUID;

public class FoodCursorWrapper extends CursorWrapper {
    public FoodCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Food getFood() {
        String uuidString = getString(getColumnIndex(FoodTable.Cols.FOODID));
        String title = getString(getColumnIndex(FoodTable.Cols.TITLE));
        String category = getString(getColumnIndex(FoodTable.Cols.CATEGORY));
        float kcal = getFloat(getColumnIndex(FoodTable.Cols.KCAL));
        float protein = getFloat(getColumnIndex(FoodTable.Cols.PROTEIN));
        float carbs = getFloat(getColumnIndex(FoodTable.Cols.CARBS));
        float fat = getFloat(getColumnIndex(FoodTable.Cols.FAT));
        int isFavorite = getInt(getColumnIndex(FoodTable.Cols.FAVORITE));

        Food food = new Food(UUID.fromString(uuidString));
        food.setTitle(title);
        food.setCategory(category);
        food.setKcal(kcal);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFat(fat);
        food.setFavorite(isFavorite != 0);

        return food;
    }
}
