package lt.jasinevicius.simplefoodlogger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import lt.jasinevicius.simplefoodlogger.Food;
import lt.jasinevicius.simplefoodlogger.Serving;
import lt.jasinevicius.simplefoodlogger.Tag;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FoodCursorWrapper extends CursorWrapper {
    public FoodCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Food getFood() {
        String uuid = getString(getColumnIndex(Foods.Cols.FOOD_ID));
        String name = getString(getColumnIndex(Foods.Cols.NAME));
        float kcal = getFloat(getColumnIndex(Foods.Cols.KCAL));
        float protein = getFloat(getColumnIndex(Foods.Cols.PROTEIN));
        float carbs = getFloat(getColumnIndex(Foods.Cols.CARBS));
        float fat = getFloat(getColumnIndex(Foods.Cols.FAT));
        int type = getInt(getColumnIndex(Foods.Cols.TYPE));
        int priority = getInt(getColumnIndex(Foods.Cols.PRIORITY));
        int isFavorite = getInt(getColumnIndex(Foods.Cols.FAVORITE));
        int consumedCount = getInt(getColumnIndex(Foods.Cols.CONSUMED_COUNT));
        long lastConsumed = getLong(getColumnIndex(Foods.Cols.LAST_CONSUMED));

        String[] tagIds = new String[]{};
        String[] tagNames = new String[]{};
        String[] tagTypes = new String[]{};
        String[] tagOrderIds = new String[]{};
        String[] tagColors = new String[]{};

        if (getString(getColumnIndex("tag_ids")) != null) {
            tagIds = getString(getColumnIndex("tag_ids")).split(";");
            tagNames = getString(getColumnIndex("tag_names")).split(";");
            tagTypes = getString(getColumnIndex("tag_types")).split(";");
            tagOrderIds = getString(getColumnIndex("tag_order_ids")).split(";");
            tagColors = getString(getColumnIndex("tag_colors")).split(";");
        }

        Food food = new Food(UUID.fromString(uuid));
        food.setName(name);
        food.setKcal(kcal);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFat(fat);
        food.setType(type);
        food.setPriority(priority);
        food.setFavorite(isFavorite != 0);
        food.setConsumedCount(consumedCount);
        food.setLastConsumed(new Date(lastConsumed));

        List<Tag> tags = new ArrayList<>();
        for (int i=0; i<tagIds.length; i++) {
            Tag tag = new Tag(UUID.fromString(tagIds[i]));
            tag.setName(tagNames[i]);
            tag.setType(Integer.parseInt(tagTypes[i]));
            tag.setOrderId(Integer.parseInt(tagOrderIds[i]));
            tag.setColor(tagColors[i]);
            tags.add(tag);
        }
        food.setTags(tags);

        // Sort of lazy initialization. Servings will have to be loaded separately if needed.
        food.setServings(new ArrayList<>());

        return food;
    }
}
