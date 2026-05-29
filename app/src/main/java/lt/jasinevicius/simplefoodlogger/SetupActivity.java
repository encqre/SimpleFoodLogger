package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lt.jasinevicius.simplefoodlogger.database.DbSchema;

public class SetupActivity extends BaseActivity {

    private static final int REQUEST_CALORIES = 0;
    private static final int REQUEST_MACROS = 1;

        public static final String[] DEFAULT_TAGS = new String[]{
            "Dairy & Eggs",
            "Meat",
            "Breads & Cereals",
            "Fast Food",
            "Soups & Salads",
            "Vegetables",
            "Fruits",
            "Beans & Legumes",
            "Pasta & Rice",
            "Fish & Seafood",
            "Sweets & Snacks",
            "Drinks",
            "Nuts & Seeds",
            "Sauces, Spices, Oils",
            "Other"
    };

    private Boolean databaseImportInProgress = false;
    private Boolean initialSetupIsOpen = false;

    private TextView progressTextview;
    private ProgressBar progressBar;

    private RadioButton unitsMetric;
    private RadioButton unitsImperial;
    private Button unitsContinueButton;

    private RadioButton themeLightButton;
    private RadioButton themeDarkButton;
    private Button themeContinueButton;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SetupActivity.class);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (databaseImportInProgress || initialSetupIsOpen) {
            //Not allowing to close activity while database import is running to prevent bad things
            //Also when theme setup is open, because that would close the setup
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_loading_progress);

        progressTextview = (TextView) findViewById(R.id.dialog_loading_progress_textview);
        progressTextview.setText("Performing setup..");
        progressBar = (ProgressBar) findViewById(R.id.dialog_loading_progress_bar);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        if (
            preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_DB_SETUP_NEEDED, true) ||
            preferences.getBoolean(LoggerSettings.PREFERENCE_MIGRATION_V1_TO_V2_NEEDED, true)
        ) {
            new databaseSetupTask().execute();
        } else if (preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_PROFILE_SETUP_NEEDED, true)) {
            setupTheme();
        } else {
            finish();
        }
    }

    private class databaseSetupTask extends AsyncTask<Void, Void, Void> {

        private static final int ESTIMATED_CSV_FOOD_COUNT = 8000;

        private FoodManager fm;
        private LogManager lm;

        private String oldCustomFoodDbPath;
        private String oldCommonFoodDbPath;
        private String oldExtendedFoodDbPath;
        private String oldLogDbPath;

        @Override
        protected void onPreExecute(){
            databaseImportInProgress = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            fm = FoodManager.get(SetupActivity.this);

            importDefaultTags();
            importFoodsFromCsv("CommonDb-v1.csv", Food.PRIORITY_COMMON);
            importFoodsFromCsv("FullDb-v2.csv", Food.PRIORITY_DEFAULT);

            // For existing users updating from version with old database format
            // import custom foods and logs, and then cleanup those old databases
            if (
                !preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_DB_SETUP_NEEDED, true) &&
                preferences.getBoolean(LoggerSettings.PREFERENCE_MIGRATION_V1_TO_V2_NEEDED, true)
            ) {


                android.util.Log.i("TKAJAS", "Starting data migration");
                lm = LogManager.get(SetupActivity.this);
                oldCustomFoodDbPath = getApplicationContext().getDatabasePath("customFoodDB.db").getPath();
                oldCommonFoodDbPath = getApplicationContext().getDatabasePath("commonFoodDB.db").getPath();
                oldExtendedFoodDbPath = getApplicationContext().getDatabasePath("extendedFoodDB.db").getPath();
                oldLogDbPath = getApplicationContext().getDatabasePath("logDB.db").getPath();

                migrateCustomFoodDataV1ToV2();
                migrateLogDataV1ToV2();
                deleteOldDatabases();
                android.util.Log.i("TKAJAS", "Data migration completed");
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... params) {
            progressBar.setProgress(progressBar.getProgress() + 1);
        }

        @Override
        protected void onPostExecute(Void result) {
            preferences.edit().putBoolean(LoggerSettings.PREFERENCE_INITIAL_DB_SETUP_NEEDED, false).apply();
            preferences.edit().putBoolean(LoggerSettings.PREFERENCE_MIGRATION_V1_TO_V2_NEEDED, false).apply();
            databaseImportInProgress = false;
//            Toast.makeText(InitialSetupActivity.this, "Initial database loading finished", Toast.LENGTH_LONG).show();
            if (preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_PROFILE_SETUP_NEEDED, true)) {
                setupTheme();
            } else {
                finish();
            }
        }

        private void importDefaultTags() {
            for (int i = 0; i < DEFAULT_TAGS.length; i++) {
                Tag tag = new Tag();
                tag.setName(DEFAULT_TAGS[i]);
                tag.setType(Tag.TYPE_DEFAULT);
                tag.setOrderId(i);
                tag.setColor("#1a1a1a");
                fm.addTag(tag);
            }
        }

        private void importFoodsFromCsv(String csvAssetPath, int priority) {
            int loopCounter = 0;
            List<Food> existingFoods = fm.getFoods(
                DbSchema.Foods.NAME + "." + DbSchema.Foods.Cols.TYPE + " = ?",
                new String[] {String.valueOf(Food.TYPE_DEFAULT)}, null, null
            );
            try {
                InputStream CSVStream = SetupActivity.this.getAssets().open(csvAssetPath);
                InputStreamReader reader = new InputStreamReader(CSVStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                while (true) {
                    if (loopCounter % ((int) Math.ceil(ESTIMATED_CSV_FOOD_COUNT/100.0)) == 0){
                        publishProgress();
                    }
                    String line = bufferedReader.readLine();
                    if (line == null) break;
                    String [] el = line.split(";");
                    boolean found = false;


                    for (int i = 0; i<existingFoods.size(); i++) {
                        if (el[0].equals(existingFoods.get(i).getFoodId().toString())) {
//                            android.util.Log.e("Logger", el[1] + " was found in current DB!");
                            found = true;
                        }
                    }
                    if (!found) {
                        Food newFood = new Food(UUID.fromString(el[0]));
                        newFood.setName(el[2]);

                        Tag tag = fm.getTagByName(el[3]);
                        List<Tag> tags = new ArrayList<>();
                        tags.add(tag);
                        newFood.setTags(tags);

                        newFood.setKcal(Float.parseFloat(el[4]));
                        newFood.setProtein(Float.parseFloat(el[5]));
                        newFood.setCarbs(Float.parseFloat(el[6]));
                        newFood.setFat(Float.parseFloat(el[7]));
                        newFood.setType(Food.TYPE_DEFAULT);
                        newFood.setPriority(priority);
                        newFood.setFavorite(Integer.valueOf(el[8]) == 1);

                        List<Serving> servings = new ArrayList<>();
                        Serving s1 = new Serving();
                        s1.setName(el[10]);
                        s1.setSize(Float.parseFloat(el[11]));
                        s1.setFoodId(newFood.getFoodId());
                        s1.setType(Serving.TYPE_DEFAULT);
                        Serving s2 = new Serving();
                        s2.setName(el[13]);
                        s2.setSize(Float.parseFloat(el[14]));
                        s2.setFoodId(newFood.getFoodId());
                        s2.setType(Serving.TYPE_DEFAULT);
                        Serving s3 = new Serving();
                        s3.setName(el[16]);
                        s3.setSize(Float.parseFloat(el[17]));
                        s3.setFoodId(newFood.getFoodId());
                        s3.setType(Serving.TYPE_DEFAULT);
                        servings.add(s1);
                        servings.add(s2);
                        servings.add(s3);
                        newFood.setServings(servings);

                        fm.addFood(newFood);
//                        android.util.Log.e("Logger", el[1] + " was not found in DB, now was added");
                    }
                    loopCounter++;
                }
                CSVStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void migrateCustomFoodDataV1ToV2() {
            // Only need to migrate custom foods, because default ones are imported from csv
            SQLiteDatabase oldCustomFoodDb = SQLiteDatabase.openDatabase(
                oldCustomFoodDbPath,  null, SQLiteDatabase.OPEN_READONLY,null
            );
            String sql = "SELECT * FROM customFoods";
            Cursor cursor = oldCustomFoodDb.rawQuery(sql, null);

            try {
                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    UUID foodId = UUID.fromString(cursor.getString(cursor.getColumnIndex("foodid")));
                    String foodName = cursor.getString(cursor.getColumnIndex("title"));
                    Float kcal = cursor.getFloat(cursor.getColumnIndex("kcal"));
                    Float protein = cursor.getFloat(cursor.getColumnIndex("protein"));
                    Float carbs = cursor.getFloat(cursor.getColumnIndex("carbs"));
                    Float fat = cursor.getFloat(cursor.getColumnIndex("fat"));
                    boolean isFavorite = cursor.getInt(cursor.getColumnIndex("favorite")) != 0;
                    String tagName = cursor.getString(cursor.getColumnIndex("category"));
                    String serving1Name = cursor.getString(cursor.getColumnIndex("portion1name"));
                    String serving2Name = cursor.getString(cursor.getColumnIndex("portion2name"));
                    String serving3Name = cursor.getString(cursor.getColumnIndex("portion3name"));
                    Float serving1Size = cursor.getFloat(cursor.getColumnIndex("portion1sizemetric"));
                    Float serving2Size = cursor.getFloat(cursor.getColumnIndex("portion2sizemetric"));
                    Float serving3Size = cursor.getFloat(cursor.getColumnIndex("portion3sizemetric"));

                    Food food = new Food(foodId);
                    food.setName(foodName);
                    food.setKcal(kcal);
                    food.setProtein(protein);
                    food.setCarbs(carbs);
                    food.setFat(fat);
                    food.setType(Food.TYPE_CUSTOM);
                    food.setPriority(Food.PRIORITY_CUSTOM);
                    food.setFavorite(isFavorite);
                    food.setConsumedCount(0);
                    food.setLastConsumed(null);

                    List<Tag> tags = new ArrayList<Tag>(){};
                    tags.add(fm.getTagByName(tagName));
                    food.setTags(tags);

                    List<Serving> servings = new ArrayList<Serving>();
                    Serving serving1 = new Serving();
                    Serving serving2 = new Serving();
                    Serving serving3 = new Serving();
                    serving1.setFoodId(food.getFoodId());
                    serving2.setFoodId(food.getFoodId());
                    serving3.setFoodId(food.getFoodId());
                    serving1.setName(serving1Name);
                    serving2.setName(serving2Name);
                    serving3.setName(serving3Name);
                    serving1.setSize(serving1Size);
                    serving2.setSize(serving2Size);
                    serving3.setSize(serving3Size);
                    serving1.setType(Serving.TYPE_CUSTOM);
                    serving2.setType(Serving.TYPE_CUSTOM);
                    serving3.setType(Serving.TYPE_CUSTOM);
                    servings.add(serving1);
                    servings.add(serving2);
                    servings.add(serving3);
                    food.setServings(servings);

                    fm.addFood(food);

                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
            oldCustomFoodDb.close();
        };
        private void migrateLogDataV1ToV2() {
            SQLiteDatabase oldLogDb = SQLiteDatabase.openDatabase(
                oldLogDbPath,  null, SQLiteDatabase.OPEN_READONLY,null
            );
            String sql = "SELECT * FROM logs";
            Cursor cursor = oldLogDb.rawQuery(sql, null);

            try {
                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    UUID logId = UUID.fromString(cursor.getString(cursor.getColumnIndex("logid")));
                    Date logDate = new Date(cursor.getLong(cursor.getColumnIndex("date")));
                    String foodName = cursor.getString(cursor.getColumnIndex("food"));
                    Float size = cursor.getFloat(cursor.getColumnIndex("size"));
                    Float kcal = cursor.getFloat(cursor.getColumnIndex("kcal"));
                    Float protein = cursor.getFloat(cursor.getColumnIndex("protein"));
                    Float carbs = cursor.getFloat(cursor.getColumnIndex("carbs"));
                    Float fat = cursor.getFloat(cursor.getColumnIndex("fat"));

                    Log log = new Log(logId);
                    log.setDate(logDate);
                    log.setFood(foodName);
                    log.setSize(size);
                    log.setKcal(kcal);
                    log.setProtein(protein);
                    log.setCarbs(carbs);
                    log.setFat(fat);

                    lm.addLog(log);

                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
            oldLogDb.close();
        }

        private void deleteOldDatabases() {
            SQLiteDatabase.deleteDatabase(new File(oldCustomFoodDbPath));
            SQLiteDatabase.deleteDatabase(new File(oldCommonFoodDbPath));
            SQLiteDatabase.deleteDatabase(new File(oldExtendedFoodDbPath));
            SQLiteDatabase.deleteDatabase(new File(oldLogDbPath));
        }
    }

    private void setupUnits() {
        // launch units setup
        initialSetupIsOpen = true;
        setContentView(R.layout.initial_setup_units);

        unitsMetric = (RadioButton) findViewById(R.id.initial_setup_units_metric);
        unitsMetric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString(LoggerSettings.PREFERENCE_UNITS, "Metric").apply();
            }
        });
        unitsImperial = (RadioButton) findViewById(R.id.initial_setup_units_imperial);
        unitsImperial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString(LoggerSettings.PREFERENCE_UNITS, "Imperial").apply();
            }
        });
        unitsContinueButton = (Button) findViewById(R.id.initial_setup_units_button_next);
        unitsContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initialSetupIsOpen = false;
                //launch profile setup
                Intent intent = SetCaloriesActivity.newIntent(SetupActivity.this, true, SetCaloriesActivity.STAGE_PROFILE);
                startActivityForResult(intent, REQUEST_CALORIES);

            }
        });

        if (preferences.getString(LoggerSettings.PREFERENCE_UNITS, "Metric").equals("Imperial")) {
            unitsImperial.setChecked(true);
        } else {
            unitsMetric.setChecked(true);
            preferences.edit().putString(LoggerSettings.PREFERENCE_UNITS, "Metric").apply();
        }
    }

    private void setupTheme(){
        // launch theme setup
        initialSetupIsOpen = true;

        //Setup some default numbers for units/calories/PFC if they are not set yet

        if (preferences.getString(LoggerSettings.PREFERENCE_UNITS, "not_set").equals("not_set")) {
            preferences.edit().putString(LoggerSettings.PREFERENCE_UNITS,
                    LoggerSettings.PREFERENCE_UNITS_DEFAULT).apply();
        }
        if (preferences.getString(LoggerSettings.PREFERENCE_TARGET_CALORIES, "not_set").equals("not_set")) {
            preferences.edit().putString(LoggerSettings.PREFERENCE_TARGET_CALORIES,
                    LoggerSettings.PREFERENCE_TARGET_CALORIES_DEFAULT).apply();
        }
        if (preferences.getString(LoggerSettings.PREFERENCE_TARGET_PROTEIN_PERCENT, "not_set").equals("not_set")) {
            preferences.edit().putString(LoggerSettings.PREFERENCE_TARGET_PROTEIN_PERCENT,
                    LoggerSettings.PREFERENCE_TARGET_PROTEIN_PERCENT_DEFAULT).apply();
        }
        if (preferences.getString(LoggerSettings.PREFERENCE_TARGET_CARBS_PERCENT, "not_set").equals("not_set")) {
            preferences.edit().putString(LoggerSettings.PREFERENCE_TARGET_CARBS_PERCENT,
                    LoggerSettings.PREFERENCE_TARGET_CARBS_PERCENT_DEFAULT).apply();
        }
        if (preferences.getString(LoggerSettings.PREFERENCE_TARGET_FAT_PERCENT, "not_set").equals("not_set")) {
            preferences.edit().putString(LoggerSettings.PREFERENCE_TARGET_FAT_PERCENT,
                    LoggerSettings.PREFERENCE_TARGET_FAT_PERCENT_DEFAULT).apply();
        }
        if (preferences.getString(LoggerSettings.PREFERENCE_THEME, "not_set").equals("not_set")) {
            preferences.edit().putString(LoggerSettings.PREFERENCE_THEME,
                    LoggerSettings.PREFERENCE_THEME_DEFAULT).apply();
        }
        if (preferences.getString(LoggerSettings.PREFERENCE_RECENT_FOODS_SIZE, "not_set").equals("not_set")) {
            preferences.edit().putString(LoggerSettings.PREFERENCE_RECENT_FOODS_SIZE,
                    LoggerSettings.PREFERENCE_RECENT_FOODS_SIZE_DEFAULT).apply();
        }

        setContentView(R.layout.initial_setup_theme);

        themeLightButton = (RadioButton) findViewById(R.id.initial_setup_theme_light);
        themeLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString(LoggerSettings.PREFERENCE_THEME, "Light theme").apply();
                recreate();
            }
        });
        themeDarkButton = (RadioButton) findViewById(R.id.initial_setup_theme_dark);
        themeDarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString(LoggerSettings.PREFERENCE_THEME, "Dark theme").apply();
                recreate();
            }
        });
        themeContinueButton = (Button) findViewById(R.id.initial_setup_theme_button_next);
        themeContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Once user presses continue here, since we already have some values set,
                //no need to launch the initial setup activity again when launching the app
                preferences.edit().putBoolean(LoggerSettings.PREFERENCE_INITIAL_PROFILE_SETUP_NEEDED, false).apply();
                // launch unit setup
                setupUnits();
            }
        });

        if (preferences.getString(LoggerSettings.PREFERENCE_THEME, LoggerSettings.PREFERENCE_THEME_DEFAULT).equals("Light theme")) {
            themeLightButton.setChecked(true);
        } else if (preferences.getString(LoggerSettings.PREFERENCE_THEME, LoggerSettings.PREFERENCE_THEME_DEFAULT).equals("Dark theme")){
            themeDarkButton.setChecked(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CALORIES) {
            if (resultCode == Activity.RESULT_OK) {
                //launch macros setup
                Intent intent = SetMacrosActivity.newIntent(SetupActivity.this, true);
                startActivityForResult(intent, REQUEST_MACROS);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setupUnits();
            }
        }
        if (requestCode == REQUEST_MACROS) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Intent intent = SetCaloriesActivity.newIntent(SetupActivity.this, true, SetCaloriesActivity.STAGE_CONFIRM_KCAL);
                startActivityForResult(intent, REQUEST_CALORIES);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
