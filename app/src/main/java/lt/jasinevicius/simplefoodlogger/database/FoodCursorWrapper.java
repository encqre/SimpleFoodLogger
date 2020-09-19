package lt.jasinevicius.simplefoodlogger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import lt.jasinevicius.simplefoodlogger.Food;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.CustomFoodTable;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.CommonFoodTable;
import lt.jasinevicius.simplefoodlogger.database.DbSchema.ExtendedFoodTable;

import java.util.UUID;

public class FoodCursorWrapper extends CursorWrapper {
    public FoodCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Food getCustomFood() {
        String uuidString = getString(getColumnIndex(CustomFoodTable.Cols.FOODID));
        int sortId = getInt(getColumnIndex(CustomFoodTable.Cols.SORTID));
        String title = getString(getColumnIndex(CustomFoodTable.Cols.TITLE));
        String category = getString(getColumnIndex(CustomFoodTable.Cols.CATEGORY));
        float kcal = getFloat(getColumnIndex(CustomFoodTable.Cols.KCAL));
        float protein = getFloat(getColumnIndex(CustomFoodTable.Cols.PROTEIN));
        float carbs = getFloat(getColumnIndex(CustomFoodTable.Cols.CARBS));
        float fat = getFloat(getColumnIndex(CustomFoodTable.Cols.FAT));
        int isFavorite = getInt(getColumnIndex(CustomFoodTable.Cols.FAVORITE));
        int isHidden = getInt(getColumnIndex(CustomFoodTable.Cols.HIDDEN));
        String portion1Name = getString(getColumnIndex(CustomFoodTable.Cols.PORTION1NAME));
        float portion1SizeMetric = getFloat(getColumnIndex(CustomFoodTable.Cols.PORTION1SIZEMETRIC));
        float portion1SizeImperial = getFloat(getColumnIndex(CustomFoodTable.Cols.PORTION1SIZEIMPERIAL));
        String portion2Name = getString(getColumnIndex(CustomFoodTable.Cols.PORTION2NAME));
        float portion2SizeMetric = getFloat(getColumnIndex(CustomFoodTable.Cols.PORTION2SIZEMETRIC));
        float portion2SizeImperial = getFloat(getColumnIndex(CustomFoodTable.Cols.PORTION2SIZEIMPERIAL));
        String portion3Name = getString(getColumnIndex(CustomFoodTable.Cols.PORTION3NAME));
        float portion3SizeMetric = getFloat(getColumnIndex(CustomFoodTable.Cols.PORTION3SIZEMETRIC));
        float portion3SizeImperial = getFloat(getColumnIndex(CustomFoodTable.Cols.PORTION3SIZEIMPERIAL));

        Food food = new Food(UUID.fromString(uuidString));
        food.setType(0);
        food.setSortID(sortId);
        food.setTitle(title);
        food.setCategory(category);
        food.setKcal(kcal);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFat(fat);
        food.setFavorite(isFavorite != 0);
        food.setHidden(isHidden != 0);
        food.setPortion1Name(portion1Name);
        food.setPortion1SizeMetric(portion1SizeMetric);
        food.setPortion1SizeImperial(portion1SizeImperial);
        food.setPortion2Name(portion2Name);
        food.setPortion2SizeMetric(portion2SizeMetric);
        food.setPortion2SizeImperial(portion2SizeImperial);
        food.setPortion3Name(portion3Name);
        food.setPortion3SizeMetric(portion3SizeMetric);
        food.setPortion3SizeImperial(portion3SizeImperial);

        return food;
    }

    public Food getCommonFood() {
        String uuidString = getString(getColumnIndex(CommonFoodTable.Cols.FOODID));
        int sortId = getInt(getColumnIndex(CommonFoodTable.Cols.SORTID));
        String title = getString(getColumnIndex(CommonFoodTable.Cols.TITLE));
        String category = getString(getColumnIndex(CommonFoodTable.Cols.CATEGORY));
        float kcal = getFloat(getColumnIndex(CommonFoodTable.Cols.KCAL));
        float protein = getFloat(getColumnIndex(CommonFoodTable.Cols.PROTEIN));
        float carbs = getFloat(getColumnIndex(CommonFoodTable.Cols.CARBS));
        float fat = getFloat(getColumnIndex(CommonFoodTable.Cols.FAT));
        int isFavorite = getInt(getColumnIndex(CommonFoodTable.Cols.FAVORITE));
        int isHidden = getInt(getColumnIndex(CommonFoodTable.Cols.HIDDEN));
        String portion1Name = getString(getColumnIndex(CommonFoodTable.Cols.PORTION1NAME));
        float portion1SizeMetric = getFloat(getColumnIndex(CommonFoodTable.Cols.PORTION1SIZEMETRIC));
        float portion1SizeImperial = getFloat(getColumnIndex(CommonFoodTable.Cols.PORTION1SIZEIMPERIAL));
        String portion2Name = getString(getColumnIndex(CommonFoodTable.Cols.PORTION2NAME));
        float portion2SizeMetric = getFloat(getColumnIndex(CommonFoodTable.Cols.PORTION2SIZEMETRIC));
        float portion2SizeImperial = getFloat(getColumnIndex(CommonFoodTable.Cols.PORTION2SIZEIMPERIAL));
        String portion3Name = getString(getColumnIndex(CommonFoodTable.Cols.PORTION3NAME));
        float portion3SizeMetric = getFloat(getColumnIndex(CommonFoodTable.Cols.PORTION3SIZEMETRIC));
        float portion3SizeImperial = getFloat(getColumnIndex(CommonFoodTable.Cols.PORTION3SIZEIMPERIAL));

        Food food = new Food(UUID.fromString(uuidString));
        food.setType(1);
        food.setSortID(sortId);
        food.setTitle(title);
        food.setCategory(category);
        food.setKcal(kcal);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFat(fat);
        food.setFavorite(isFavorite != 0);
        food.setHidden(isHidden != 0);
        food.setPortion1Name(portion1Name);
        food.setPortion1SizeMetric(portion1SizeMetric);
        food.setPortion1SizeImperial(portion1SizeImperial);
        food.setPortion2Name(portion2Name);
        food.setPortion2SizeMetric(portion2SizeMetric);
        food.setPortion2SizeImperial(portion2SizeImperial);
        food.setPortion3Name(portion3Name);
        food.setPortion3SizeMetric(portion3SizeMetric);
        food.setPortion3SizeImperial(portion3SizeImperial);

        return food;
    }

    public Food getExtendedFood() {
        String uuidString = getString(getColumnIndex(ExtendedFoodTable.Cols.FOODID));
        int sortId = getInt(getColumnIndex(ExtendedFoodTable.Cols.SORTID));
        String title = getString(getColumnIndex(ExtendedFoodTable.Cols.TITLE));
        String category = getString(getColumnIndex(ExtendedFoodTable.Cols.CATEGORY));
        float kcal = getFloat(getColumnIndex(ExtendedFoodTable.Cols.KCAL));
        float protein = getFloat(getColumnIndex(ExtendedFoodTable.Cols.PROTEIN));
        float carbs = getFloat(getColumnIndex(ExtendedFoodTable.Cols.CARBS));
        float fat = getFloat(getColumnIndex(ExtendedFoodTable.Cols.FAT));
        int isFavorite = getInt(getColumnIndex(ExtendedFoodTable.Cols.FAVORITE));
        int isHidden = getInt(getColumnIndex(ExtendedFoodTable.Cols.HIDDEN));
        String portion1Name = getString(getColumnIndex(ExtendedFoodTable.Cols.PORTION1NAME));
        float portion1SizeMetric = getFloat(getColumnIndex(ExtendedFoodTable.Cols.PORTION1SIZEMETRIC));
        float portion1SizeImperial = getFloat(getColumnIndex(ExtendedFoodTable.Cols.PORTION1SIZEIMPERIAL));
        String portion2Name = getString(getColumnIndex(ExtendedFoodTable.Cols.PORTION2NAME));
        float portion2SizeMetric = getFloat(getColumnIndex(ExtendedFoodTable.Cols.PORTION2SIZEMETRIC));
        float portion2SizeImperial = getFloat(getColumnIndex(ExtendedFoodTable.Cols.PORTION2SIZEIMPERIAL));
        String portion3Name = getString(getColumnIndex(ExtendedFoodTable.Cols.PORTION3NAME));
        float portion3SizeMetric = getFloat(getColumnIndex(ExtendedFoodTable.Cols.PORTION3SIZEMETRIC));
        float portion3SizeImperial = getFloat(getColumnIndex(ExtendedFoodTable.Cols.PORTION3SIZEIMPERIAL));

        Food food = new Food(UUID.fromString(uuidString));
        food.setType(2);
        food.setSortID(sortId);
        food.setTitle(title);
        food.setCategory(category);
        food.setKcal(kcal);
        food.setProtein(protein);
        food.setCarbs(carbs);
        food.setFat(fat);
        food.setFavorite(isFavorite != 0);
        food.setHidden(isHidden != 0);
        food.setPortion1Name(portion1Name);
        food.setPortion1SizeMetric(portion1SizeMetric);
        food.setPortion1SizeImperial(portion1SizeImperial);
        food.setPortion2Name(portion2Name);
        food.setPortion2SizeMetric(portion2SizeMetric);
        food.setPortion2SizeImperial(portion2SizeImperial);
        food.setPortion3Name(portion3Name);
        food.setPortion3SizeMetric(portion3SizeMetric);
        food.setPortion3SizeImperial(portion3SizeImperial);

        return food;
    }
}
