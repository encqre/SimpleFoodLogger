package lt.jasinevicius.simplefoodlogger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class FoodDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "foods.db";

    public FoodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

     // TODO use foreign key where applicable to prevent leaving trash when deleting?

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "create table " + DbSchema.Foods.NAME + "(" +
                DbSchema.Foods.Cols.FOOD_ID + " primary key, " +
                DbSchema.Foods.Cols.NAME + ", " +
                DbSchema.Foods.Cols.KCAL + ", " +
                DbSchema.Foods.Cols.PROTEIN + ", " +
                DbSchema.Foods.Cols.CARBS + ", " +
                DbSchema.Foods.Cols.FAT + ", " +
                DbSchema.Foods.Cols.TYPE + ", " +
                DbSchema.Foods.Cols.PRIORITY + ", " +
                DbSchema.Foods.Cols.FAVORITE + ", " +
                DbSchema.Foods.Cols.CONSUMED_COUNT + ", " +
                DbSchema.Foods.Cols.LAST_CONSUMED + ")"
        );

        db.execSQL(
            "create table " + DbSchema.Tags.NAME + "(" +
                DbSchema.Tags.Cols.TAG_ID + " primary key, " +
                DbSchema.Tags.Cols.NAME + ", " +
                DbSchema.Tags.Cols.TYPE + ", " +
                DbSchema.Tags.Cols.ORDER_ID + ", " +
                DbSchema.Tags.Cols.COLOR + ")"
        );

        db.execSQL(
            "create table " + DbSchema.FoodTags.NAME + "(" +
                DbSchema.FoodTags.Cols.FOOD_ID + ", " +
                DbSchema.FoodTags.Cols.TAG_ID + "," +
                "primary key(" +
                DbSchema.FoodTags.Cols.FOOD_ID + ", " +
                DbSchema.FoodTags.Cols.TAG_ID + ")" +
                ")"
        );

        db.execSQL(
            "create table " + DbSchema.FoodServings.NAME + "(" +
                DbSchema.FoodServings.Cols.SERVING_ID + " primary key, " +
                DbSchema.FoodServings.Cols.FOOD_ID + ", " +
                DbSchema.FoodServings.Cols.NAME + ", " +
                DbSchema.FoodServings.Cols.SIZE + ", " +
                DbSchema.FoodServings.Cols.TYPE + ")"
        );

        db.execSQL(
            "create table " + DbSchema.Meals.NAME + "(" +
                DbSchema.Meals.Cols.MEAL_ID + " primary key, " +
                DbSchema.Meals.Cols.NAME + ", " +
                DbSchema.Meals.Cols.FAVORITE + ", " +
                DbSchema.Meals.Cols.CONSUMED_COUNT + ", " +
                DbSchema.Meals.Cols.LAST_CONSUMED + ")"
        );

        db.execSQL(
            "create table " + DbSchema.MealFoods.NAME + "(" +
                DbSchema.MealFoods.Cols.MEAL_ID + ", " +
                DbSchema.MealFoods.Cols.FOOD_ID + ", " +
                DbSchema.MealFoods.Cols.DEFAULT_FOOD_SIZE + ", " +
                "primary key(" +
                DbSchema.MealFoods.Cols.MEAL_ID + ", " +
                DbSchema.MealFoods.Cols.FOOD_ID + ")" +
                ")"
        );

        db.execSQL(
            "create table " + DbSchema.MealTags.NAME + "(" +
                DbSchema.MealTags.Cols.MEAL_ID + ", " +
                DbSchema.MealTags.Cols.TAG_ID + ", " +
                "primary key(" +
                DbSchema.MealTags.Cols.MEAL_ID + ", " +
                DbSchema.MealTags.Cols.TAG_ID + ")" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
