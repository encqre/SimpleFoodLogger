package lt.jasinevicius.simplefoodlogger;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lt.jasinevicius.simplefoodlogger.database.FoodCursorWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lt.jasinevicius.simplefoodlogger.database.DbSchema.Foods;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.FoodTags;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.Tags;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.FoodServings;
import lt.jasinevicius.simplefoodlogger.database.FoodDbHelper;
import lt.jasinevicius.simplefoodlogger.database.ServingCursorWrapper;
import lt.jasinevicius.simplefoodlogger.database.TagCursorWrapper;

public class FoodManager {
    private static FoodManager sFoodManager;

    private Context context;
    private SQLiteDatabase foodDb;

    private SharedPreferences preferences;

    public static FoodManager get(Context context) {
        if (sFoodManager == null) {
            sFoodManager = new FoodManager(context);
        }
        return sFoodManager;
    }

    private FoodManager(Context ctx) {
        context = ctx.getApplicationContext();
        foodDb = new FoodDbHelper(context).getWritableDatabase();
        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    private int getRecentFoodsLength() {
        return Integer.parseInt(
            preferences.getString(
                LoggerSettings.PREFERENCE_RECENT_FOODS_SIZE,
                LoggerSettings.PREFERENCE_RECENT_FOODS_SIZE_DEFAULT
            )
        );
    }

    public List<Food> getFoods(
        String whereClause,
        String[] whereArgs,
        String orderBy,
        String limit
    ) {
        List<Food> foods = new ArrayList<>();
        android.util.Log.e("TKAJAS", "getFoods start");
        FoodCursorWrapper cursor = queryFoods(whereClause, whereArgs, orderBy, limit);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                foods.add(cursor.getFood());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        android.util.Log.e("TKAJAS", "getFoods end. Size: " + foods.size());
        return foods;
    }

    public List<Food> searchFoods(
        String searchString,
        boolean onlyIncludeFavorites,
        boolean onlyIncludeRecent,
        boolean onlyIncludeHidden,
        String tag
    ) {
        String whereClause = "";
        String orderBy = Foods.NAME + "." + Foods.Cols.CONSUMED_COUNT + " DESC";
        String limit = null;
        String[] searchWordsArray = searchString.split("\\s+");

        for (int i=0; i<searchWordsArray.length; i++) {
            // Protection against empty strings
            if (searchWordsArray[i].length() > 0) {
                searchWordsArray[i] = "\"%" + searchWordsArray[i] + "%\"";
                if (whereClause.length() > 1) {
                    whereClause += " AND ";
                }
                whereClause += Foods.NAME + "." + Foods.Cols.NAME + " LIKE " + searchWordsArray[i];
            }
        }

        if (whereClause.equals("")) {
            return new ArrayList<>();
        } else {
            if (onlyIncludeRecent) {
                orderBy = Foods.NAME + "." + Foods.Cols.LAST_CONSUMED + " DESC";
                limit = String.valueOf(getRecentFoodsLength());
            }
            if (onlyIncludeFavorites) {
                whereClause += " AND " + Foods.NAME + "." + Foods.Cols.FAVORITE + " = 1";
            }
            if (onlyIncludeHidden) {
                whereClause += " AND " + Foods.NAME + "." + Foods.Cols.TYPE + " IN (" +
                    Food.TYPE_DEFAULT_HIDDEN + ", " +
                    Food.TYPE_DEFAULT_MODIFIED_HIDDEN + ")";
            } else {
                whereClause += " AND " + Foods.NAME + "." + Foods.Cols.TYPE + " NOT IN (" +
                    Food.TYPE_DEFAULT_HIDDEN + ", " +
                    Food.TYPE_DEFAULT_MODIFIED_HIDDEN + ")";
            }
            if (tag != null && !tag.equals("")) {
                whereClause += " AND " + Tags.NAME + "." + Tags.Cols.NAME + " = ?";// + tag;
                return getFoods(whereClause, new String[]{tag}, orderBy, limit);
            }
        }

        return getFoods(whereClause, null, orderBy, limit);
    }

    public List<Food> getFoodsWithTag(String tagName) {
        // WARNING: this returns Food objects with only 1 tag, even if Food has multiple tags
        String whereClause = Tags.NAME + "." + Tags.Cols.NAME + " = ?";
        whereClause += " AND " + Foods.NAME + "." + Foods.Cols.TYPE + " NOT IN (" +
            Food.TYPE_DEFAULT_HIDDEN + ", " +
            Food.TYPE_DEFAULT_MODIFIED_HIDDEN + ")";
        String[] whereArgs = {tagName};

        return getFoods(whereClause, whereArgs, null, null);
    }

    public List<Food> getCustomFoods() {
        String whereClause = Foods.NAME + "." + Foods.Cols.TYPE + " = 0";

        return getFoods(whereClause, null, null, null);
    }

    public List<Food> getFavoriteFoods() {
        String whereClause = Foods.NAME + "." + Foods.Cols.FAVORITE + " = 1";
        whereClause += " AND " + Foods.NAME + "." + Foods.Cols.TYPE + " NOT IN (" +
            Food.TYPE_DEFAULT_HIDDEN + ", " +
            Food.TYPE_DEFAULT_MODIFIED_HIDDEN + ")";

        return getFoods(whereClause, null, null, null);
    }

    public Food getFood(UUID id) {
        Food food;

        String whereClause = Foods.NAME + "." + Foods.Cols.FOOD_ID + " = ?";
        String[] whereArgs = {id.toString()};

        List<Food> foods = getFoods(whereClause, whereArgs, null, null);

        if (foods.size() >= 1) {
            food = foods.get(0);
        } else {
            return null;
        }
        // query and set the servings
        List<Serving> foodServings = getFoodServings(id);
        food.setServings(foodServings);

        return food;
    }

    public Food getFoodByName(String foodName) {
        Food food;

        String whereClause = Foods.NAME + "." + Foods.Cols.NAME + " = ?";
        String[] whereArgs = {foodName};

        List<Food> foods = getFoods(whereClause, whereArgs, null, null);
        if (foods.size() >= 1) {
            food = foods.get(0);
        } else {
            return null;
        }
        // query and set the servings
        List<Serving> foodServings = getFoodServings(food.getFoodId());
        food.setServings(foodServings);

        return food;
    }

    public List<Food> getRecentFoods() {
        String orderBy = Foods.NAME + "." + Foods.Cols.LAST_CONSUMED + " DESC";
        String limit = String.valueOf(getRecentFoodsLength());
        String whereClause = Foods.NAME + "." + Foods.Cols.TYPE + " NOT IN (" +
            Food.TYPE_DEFAULT_HIDDEN + ", " +
            Food.TYPE_DEFAULT_MODIFIED_HIDDEN + ") AND " +
            Foods.NAME + "." + Foods.Cols.CONSUMED_COUNT + " > 0";

        return getFoods(whereClause, null, orderBy, limit);
    }

    public List<Food> getHiddenFoods(String filterString) {
        if (filterString == null || filterString.equals("")) {
            String whereClause = Foods.NAME + "." + Foods.Cols.TYPE + " IN (" +
                Food.TYPE_DEFAULT_HIDDEN + ", " +
                Food.TYPE_DEFAULT_MODIFIED_HIDDEN + ")";
            return getFoods(whereClause, null, null, null);
        } else {
            return searchFoods(
                filterString,
                false,
                false,
                true,
                null
            );
        }
    }

    private FoodCursorWrapper queryFoods(
        String whereClause,
        String[] whereArgs,
        String orderBy,
        String limit
    ) {
        whereClause = (whereClause != null) ? " WHERE " + whereClause : "";
        orderBy = (orderBy != null) ? orderBy + ", " : "";
        // append the default ordering - by priority and addition order
        orderBy += Foods.NAME + "." + Foods.Cols.PRIORITY + " ASC, ";
        orderBy += Foods.NAME + ".rowid ASC";
        String limitClause = (limit != null) ? " LIMIT " + limit : "";

        // select food with their tags aggregated into single column (semicolon separated)
        // TODO using group_concats here like this is a bit sketchy (ids and names are not guaranteed to be ordered the same)
        String sql = "SELECT " +
            Foods.NAME + ".*, " +
            "GROUP_CONCAT(" + Tags.NAME + "." + Tags.Cols.TAG_ID + ", ';') tag_ids, " +
            "GROUP_CONCAT(" + Tags.NAME + "." + Tags.Cols.NAME + ", ';') tag_names, " +
            "GROUP_CONCAT(" + Tags.NAME + "." + Tags.Cols.TYPE + ", ';') tag_types, " +
            "GROUP_CONCAT(" + Tags.NAME + "." + Tags.Cols.ORDER_ID + ", ';') tag_order_ids, " +
            "GROUP_CONCAT(" + Tags.NAME + "." + Tags.Cols.COLOR + ", ';') tag_colors " +
            "FROM " + Foods.NAME +
            " LEFT JOIN " + FoodTags.NAME +
            " ON " + Foods.NAME + "." + Foods.Cols.FOOD_ID + " = " +
            FoodTags.NAME + "." + FoodTags.Cols.FOOD_ID +
            " LEFT JOIN " + Tags.NAME +
            " ON " + FoodTags.NAME + "." + FoodTags.Cols.TAG_ID + " = " +
            Tags.NAME + "." + Tags.Cols.TAG_ID +
            whereClause +
            " GROUP BY " + Foods.NAME + "." + Foods.Cols.FOOD_ID +
            " ORDER BY " + orderBy +
            limitClause;

        android.util.Log.e("TKAJAS", "SQL:" + sql);

        Cursor cursor = foodDb.rawQuery(sql, whereArgs);
        android.util.Log.e("TKAJAS", "SQL completed");
        return new FoodCursorWrapper(cursor);
    }

    public void addFood(Food f) {
        ContentValues values = getContentValues(f);

        foodDb.insert(Foods.NAME, null, values);

        // Add entry for each food's tag to FoodTags
        for (int i = 0; i < f.getTags().size(); i++) {
            Tag tag = f.getTags().get(i);

            ContentValues foodTagValues = new ContentValues();
            foodTagValues.put(FoodTags.Cols.FOOD_ID, f.getFoodId().toString());
            foodTagValues.put(FoodTags.Cols.TAG_ID, tag.getTagId().toString());

            foodDb.insert(FoodTags.NAME, null, foodTagValues);
        }

        // Add entry for each food's serving to FoodServings
        for (int i = 0; i < f.getServings().size(); i++) {
            Serving serving = f.getServings().get(i);
            ContentValues servingValues = getContentValues(serving);
            foodDb.insert(FoodServings.NAME, null, servingValues);
        }
    }

    public void updateFood(Food food) {
        UUID foodId = food.getFoodId();
        ContentValues values = getContentValues(food);

        foodDb.update(
            Foods.NAME,
            values,
            Foods.Cols.FOOD_ID + " = ?",
            new String[] {foodId.toString()}
        );

        // Update FoodTags - delete removed tags, add new ones
        List<Tag> newTags = food.getTags();
        List<Tag> currentTags = getFoodTags(foodId);
        List<Tag> tagsToAdd = Tag.listDiff(newTags, currentTags);
        List<Tag> tagsToRemove = Tag.listDiff(currentTags, newTags);
        for (Tag tag : tagsToAdd) {
            ContentValues foodTagValues = new ContentValues();
            foodTagValues.put(FoodTags.Cols.FOOD_ID, food.getFoodId().toString());
            foodTagValues.put(FoodTags.Cols.TAG_ID, tag.getTagId().toString());
            foodDb.insert(FoodTags.NAME, null, foodTagValues);
        }
        for (Tag tag : tagsToRemove) {
            foodDb.delete(
                FoodTags.NAME,
                FoodTags.Cols.FOOD_ID + " = ? AND " + FoodTags.Cols.TAG_ID + " = ?",
                new String[]{food.getFoodId().toString(), tag.getTagId().toString()}
            );
        }

        // Update servings
        List<Serving> newServings = food.getServings();
        List<Serving> currentServings = getFoodServings(foodId);
        List<Serving> servingsToAdd = Serving.listDiff(newServings, currentServings);
        List<Serving> servingsToRemove = Serving.listDiff(currentServings, newServings);
        List<Serving> servingsToUpdate = Serving.listOverlap(newServings, currentServings);

        for (Serving serving : servingsToAdd) {
            foodDb.insert(FoodServings.NAME, null, getContentValues(serving));
        }
        for (Serving serving : servingsToRemove) {
            foodDb.delete(
                FoodServings.NAME,
                FoodServings.Cols.SERVING_ID + " = ?",
                new String[]{serving.getServingId().toString()}
            );
        }
        for (Serving serving : servingsToUpdate) {
            foodDb.update(
                FoodServings.NAME,
                getContentValues(serving),
                FoodServings.Cols.SERVING_ID + " = ?",
                new String[]{serving.getServingId().toString()}
            );
        }
    }

    public void updateFoodConsumptionStats(Food food) {
        String uuidString = food.getFoodId().toString();
        ContentValues values = new ContentValues();

        values.put(Foods.Cols.CONSUMED_COUNT, food.getConsumedCount());
        values.put(Foods.Cols.LAST_CONSUMED, food.getLastConsumed().getTime());

        foodDb.update(
            Foods.NAME,
            values,
            Foods.Cols.FOOD_ID + " = ?",
            new String[] {uuidString}
        );
    }

    public void deleteFood(Food food) {
        String uuidString = food.getFoodId().toString();

         foodDb.delete(
             Foods.NAME,
             Foods.Cols.FOOD_ID + " = ?",
             new String[] {uuidString}
         );
        // cleanup tags and servings
        foodDb.delete(
            FoodTags.NAME,
            FoodTags.Cols.FOOD_ID + " = ?",
            new String[]{uuidString}
        );
        foodDb.delete(
            FoodServings.NAME,
            FoodServings.Cols.FOOD_ID + " = ?",
            new String[] {uuidString}
        );
    }

    public List<Serving> getFoodServings(UUID foodId) {
        List<Serving> servings = new ArrayList<>();

        String whereClause = FoodServings.Cols.FOOD_ID + " = ?";
        String[] whereArgs = {foodId.toString()};

        ServingCursorWrapper cursor = queryServings(whereClause, whereArgs, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                servings.add(cursor.getServing());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return servings;
    }

    private ServingCursorWrapper queryServings(
        String whereClause,
        String[] whereArgs,
        String orderBy) {
        Cursor cursor = foodDb.query(
            FoodServings.NAME,
            null, //columns - null selects all columns
            whereClause,
            whereArgs,
            null,
            null,
            orderBy
        );
        return new ServingCursorWrapper(cursor);
    }

    public List<Tag> getFoodTags(UUID foodId) {
        List<Tag> tags = new ArrayList<>();
        String[] whereArgs = new String[]{foodId.toString()};

        String sql = "SELECT * FROM " + Tags.NAME +
            " LEFT JOIN " + FoodTags.NAME +
            " ON " + Tags.NAME + "." + Tags.Cols.TAG_ID + " = " +
            FoodTags.NAME + "." + FoodTags.Cols.TAG_ID +
            " WHERE " + FoodTags.NAME + "." + FoodTags.Cols.FOOD_ID + " = ?";

        try (TagCursorWrapper cursor = new TagCursorWrapper(foodDb.rawQuery(sql, whereArgs))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tags.add(cursor.getTag());
                cursor.moveToNext();
            }
        }

        return tags;
    }

    public List<Tag> getTags(String whereClause, String[] whereArgs, String orderBy) {
        List<Tag> tags = new ArrayList<>();

        TagCursorWrapper cursor = queryTags(whereClause, whereArgs, orderBy);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                tags.add(cursor.getTag());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return tags;
    }

    public Tag getTag(UUID id) {
        String whereClause = Tags.NAME + "." + Tags.Cols.TAG_ID + " = ?";
        String[] whereArgs = {id.toString()};

        List<Tag> tags = getTags(whereClause, whereArgs, null);
        if (tags.size() >= 1) {
            return tags.get(0);
        } else {
            return null;
        }
    }

    public Tag getTagByName(String tagName) {
        String whereClause = Tags.NAME + "." + Tags.Cols.NAME + " = ?";
        String[] whereArgs = {tagName};

        List<Tag> tags = getTags(whereClause, whereArgs, null);
        if (tags.size() >= 1) {
            return tags.get(0);
        } else {
            return null;
        }
    }

    public int getTagMaxOrderId() {
        int maxOrderId = 0;
        String sql = "SELECT MAX(" + Tags.Cols.ORDER_ID + ") max_order_id FROM " + Tags.NAME;
        Cursor cursor = foodDb.rawQuery(sql, null);
        try {
            cursor.moveToFirst();
            maxOrderId = cursor.getInt(cursor.getColumnIndex("max_order_id"));
        } finally {
            cursor.close();
        }
        return maxOrderId;
    }

    public List<Tag> getNonHiddenTags() {
        String whereClause = Tags.NAME + "." + Tags.Cols.TYPE + " NOT IN (" +
            Tag.TYPE_DEFAULT_HIDDEN + ")";

        return getTags(whereClause, null, null);
    }

    public List<Tag> getHiddenTags(String filterString) {
        if (filterString == null || filterString.equals("")) {
            String whereClause = Tags.NAME + "." + Tags.Cols.TYPE + " IN (" +
                Tag.TYPE_DEFAULT_HIDDEN + ")";
            return getTags(whereClause, null, null);
        } else {
            return searchTags(filterString,null,true);
        }
    }

    public List<Tag> searchTags(
        String searchString,
        ArrayList<Tag> filteredTags,
        boolean onlyIncludeHidden
    ) {
        String whereClause = "";
        String[] searchWordsArray = searchString.split("\\s+");

        for (int i=0; i<searchWordsArray.length; i++) {
            if (searchWordsArray[i].length() > 0) {
                searchWordsArray[i] = "\"%" + searchWordsArray[i] + "%\"";
                if (whereClause.length() > 1) {
                    whereClause += " AND ";
                }
                whereClause += Tags.NAME + "." + Tags.Cols.NAME + " LIKE " + searchWordsArray[i];
            }
        }

        if (filteredTags != null && filteredTags.size() > 0) {
            if (!whereClause.equals("")) {
                whereClause += " AND ";
            }
            whereClause += Tags.NAME + "." + Tags.Cols.NAME + " NOT IN (";
            for (int i = 0; i<filteredTags.size(); i++) {
                if (i != 0) {
                    whereClause += ", ";
                }
                whereClause += "\"" + filteredTags.get(i).getName() + "\"";
            }
            whereClause += ")";
        }

        if (onlyIncludeHidden) {
            if (!whereClause.equals("")) {
                whereClause += " AND ";
            }
            whereClause += Tags.NAME + "." + Tags.Cols.TYPE + " IN (" +
                Tag.TYPE_DEFAULT_HIDDEN + ")";
        }

        if (whereClause.equals("")) {
            // get all tags for empty text search
            whereClause = null;
        }

        return getTags(whereClause, null, null);
    }

    private TagCursorWrapper queryTags(
        String whereClause,
        String[] whereArgs,
        String orderBy
    ) {
        orderBy = (orderBy != null) ? orderBy + ", " : "";
        // append the default ordering - by  order_id and addition order
        orderBy += Tags.NAME + "." + Tags.Cols.ORDER_ID + " ASC, ";
        orderBy += Tags.NAME + ".rowid ASC";
        Cursor cursor = foodDb.query(
            Tags.NAME,
            null, //columns - null selects all columns
            whereClause,
            whereArgs,
            null,
            null,
            orderBy
        );
        return new TagCursorWrapper(cursor);
    }

    public void addTag(Tag t) {
        ContentValues values = getContentValues(t);

        foodDb.insert(Tags.NAME, null, values);
    }

    public void updateTag(Tag t) {
        UUID tagId = t.getTagId();
        ContentValues values = getContentValues(t);

        foodDb.update(
            Tags.NAME,
            values,
            Tags.Cols.TAG_ID + " = ?",
            new String[]{tagId.toString()}
        );
    }

    public void deleteTag(UUID id) {
        foodDb.delete(
            Tags.NAME,
            Tags.Cols.TAG_ID + " = ?",
            new String[] {id.toString()}
        );
        // cleanup food tags as well
        foodDb.delete(
            FoodTags.NAME,
            FoodTags.Cols.TAG_ID + " = ?",
            new String[]{id.toString()}
        );
    }

    private static ContentValues getContentValues(Food food) {
        ContentValues values = new ContentValues();

        values.put(Foods.Cols.FOOD_ID, food.getFoodId().toString());
        values.put(Foods.Cols.NAME, food.getName());
        values.put(Foods.Cols.KCAL, food.getKcal());
        values.put(Foods.Cols.PROTEIN, food.getProtein());
        values.put(Foods.Cols.CARBS, food.getCarbs());
        values.put(Foods.Cols.FAT, food.getFat());
        values.put(Foods.Cols.TYPE, food.getType());
        values.put(Foods.Cols.PRIORITY, food.getPriority());
        values.put(Foods.Cols.FAVORITE, food.isFavorite() ? 1 : 0);
        values.put(Foods.Cols.CONSUMED_COUNT, food.getConsumedCount());
        values.put(
            Foods.Cols.LAST_CONSUMED,
            food.getLastConsumed() != null ? food.getLastConsumed().getTime() : null
        );

        return values;
    }

    private static ContentValues getContentValues(Serving serving) {
        ContentValues values = new ContentValues();

        values.put(FoodServings.Cols.SERVING_ID, serving.getServingId().toString());
        values.put(FoodServings.Cols.FOOD_ID, serving.getFoodId().toString());
        values.put(FoodServings.Cols.NAME, serving.getName());
        values.put(FoodServings.Cols.SIZE, serving.getSize());
        values.put(FoodServings.Cols.TYPE, serving.getType());

        return values;
    }

    private static ContentValues getContentValues(Tag tag) {
        ContentValues values = new ContentValues();

        values.put(Tags.Cols.TAG_ID, tag.getTagId().toString());
        values.put(Tags.Cols.NAME, tag.getName());
        values.put(Tags.Cols.TYPE, tag.getType());
        values.put(Tags.Cols.ORDER_ID, tag.getOrderId());
        values.put(Tags.Cols.COLOR, tag.getColor());

        return values;
    }
}
