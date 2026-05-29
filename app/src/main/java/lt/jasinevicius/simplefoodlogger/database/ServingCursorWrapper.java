package lt.jasinevicius.simplefoodlogger.database;

    import android.database.Cursor;
    import android.database.CursorWrapper;

    import lt.jasinevicius.simplefoodlogger.Serving;
    import lt.jasinevicius.simplefoodlogger.database.DbSchema.*;

    import java.util.UUID;

public class ServingCursorWrapper extends CursorWrapper {
    public ServingCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Serving getServing() {
        String servingId = getString(getColumnIndex(FoodServings.Cols.SERVING_ID));
        String foodId = getString(getColumnIndex(DbSchema.FoodServings.Cols.FOOD_ID));
        String name = getString(getColumnIndex(DbSchema.FoodServings.Cols.NAME));
        float size = getFloat(getColumnIndex(FoodServings.Cols.SIZE));
        int type = getInt(getColumnIndex(FoodServings.Cols.TYPE));

        Serving serving = new Serving(UUID.fromString(servingId));
        serving.setFoodId(UUID.fromString(foodId));
        serving.setName(name);
        serving.setSize(size);
        serving.setType(type);

        return serving;
    }
}
