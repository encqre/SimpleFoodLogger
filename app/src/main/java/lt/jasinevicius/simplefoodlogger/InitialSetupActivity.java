package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

public class InitialSetupActivity extends BaseActivity {

    private static final int REQUEST_CALORIES = 0;
    private static final int REQUEST_MACROS = 1;

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
        Intent intent = new Intent(packageContext, InitialSetupActivity.class);
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
        progressTextview.setText("Performing initial setup..");
        progressBar = (ProgressBar) findViewById(R.id.dialog_loading_progress_bar);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        if (preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_DB_SETUP_NEEDED, true)) {
            new initialDatabaseImportTask().execute();
        } else if (preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_PROFILE_SETUP_NEEDED, true)) {
            setupTheme();
        } else {
            finish();
        }
    }

    private class initialDatabaseImportTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            databaseImportInProgress = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            FoodManager fm = FoodManager.get(InitialSetupActivity.this);
            int loopCounter = 0;
            //Import common foods
            List<Food> fullCommonFoodList = fm.getCommonFoods();
            try {
                InputStream CSVStream = InitialSetupActivity.this.getAssets().open("CommonDb-v1.csv");
                InputStreamReader reader = new InputStreamReader(CSVStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                while (true) {
                    if (loopCounter % ((int) Math.ceil(8000/100.0)) == 0){
                        publishProgress();
                    }
                    String line = bufferedReader.readLine();
                    if (line == null) break;
                    String [] el = line.split(";");
                    boolean found = false;
                    for (int i = 0; i<fullCommonFoodList.size(); i++) {
                        if (el[0].equals(fullCommonFoodList.get(i).getFoodId().toString())) {
//                            android.util.Log.e("Logger", el[1] + " was found in current DB!");
                            found = true;
                        }
                    }
                    if (!found) {
                        Food foundNewFood = new Food(UUID.fromString(el[0]));
                        foundNewFood.setSortID(Integer.valueOf(el[1]));
                        foundNewFood.setTitle(el[2]);
                        foundNewFood.setCategory(el[3]);
                        foundNewFood.setKcal(Float.parseFloat(el[4]));
                        foundNewFood.setProtein(Float.parseFloat(el[5]));
                        foundNewFood.setCarbs(Float.parseFloat(el[6]));
                        foundNewFood.setFat(Float.parseFloat(el[7]));
                        foundNewFood.setFavorite(Integer.valueOf(el[8]) == 1);
                        foundNewFood.setHidden(Integer.valueOf(el[9]) == 1);
                        foundNewFood.setPortion1Name(el[10]);
                        foundNewFood.setPortion1SizeMetric(Float.parseFloat(el[11]));
                        foundNewFood.setPortion1SizeImperial(Float.parseFloat(el[12]));
                        foundNewFood.setPortion2Name(el[13]);
                        foundNewFood.setPortion2SizeMetric(Float.parseFloat(el[14]));
                        foundNewFood.setPortion2SizeImperial(Float.parseFloat(el[15]));
                        foundNewFood.setPortion3Name(el[16]);
                        foundNewFood.setPortion3SizeMetric(Float.parseFloat(el[17]));
                        foundNewFood.setPortion3SizeImperial(Float.parseFloat(el[18]));
                        foundNewFood.setType(1);
                        fm.addCommonFood(foundNewFood);
//                        android.util.Log.e("Logger", el[1] + " was not found in DB, now was added");
                    }
                    loopCounter++;
                }
                CSVStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Import extended foods

            List<Food> fullExtendedFoodList = fm.getExtendedFoods();
            try {
                InputStream CSVStream = InitialSetupActivity.this.getAssets().open("FullDb-v2.csv");
                InputStreamReader reader = new InputStreamReader(CSVStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                while (true) {
                    if (loopCounter % ((int) Math.ceil(8000/100.0)) == 0){
                        publishProgress();
                    }
                    String line = bufferedReader.readLine();
                    if (line == null) break;
                    String [] el = line.split(";");
                    boolean found = false;
                    for (int i = 0; i<fullExtendedFoodList.size(); i++) {
                        if (el[0].equals(fullExtendedFoodList.get(i).getFoodId().toString())) {
//                            android.util.Log.e("Logger", el[1] + " was found in current DB!");
                            found = true;
                        }
                    }
                    if (!found) {
                        Food foundNewFood = new Food(UUID.fromString(el[0]));
                        foundNewFood.setSortID(Integer.valueOf(el[1]));
                        foundNewFood.setTitle(el[2]);
                        foundNewFood.setCategory(el[3]);
                        foundNewFood.setKcal(Float.parseFloat(el[4]));
                        foundNewFood.setProtein(Float.parseFloat(el[5]));
                        foundNewFood.setCarbs(Float.parseFloat(el[6]));
                        foundNewFood.setFat(Float.parseFloat(el[7]));
                        foundNewFood.setFavorite(Integer.valueOf(el[8]) == 1);
                        foundNewFood.setHidden(Integer.valueOf(el[9]) == 1);
                        foundNewFood.setPortion1Name(el[10]);
                        foundNewFood.setPortion1SizeMetric(Float.parseFloat(el[11]));
                        foundNewFood.setPortion1SizeImperial(Float.parseFloat(el[12]));
                        foundNewFood.setPortion2Name(el[13]);
                        foundNewFood.setPortion2SizeMetric(Float.parseFloat(el[14]));
                        foundNewFood.setPortion2SizeImperial(Float.parseFloat(el[15]));
                        foundNewFood.setPortion3Name(el[16]);
                        foundNewFood.setPortion3SizeMetric(Float.parseFloat(el[17]));
                        foundNewFood.setPortion3SizeImperial(Float.parseFloat(el[18]));
                        foundNewFood.setType(2);
                        fm.addExtendedFood(foundNewFood);
//                        android.util.Log.e("Logger", el[1] + " was not found in DB, now was added");
                    }
                    loopCounter++;
                }
                CSVStream.close();
            } catch (Exception e) {
                e.printStackTrace();
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
            databaseImportInProgress = false;
//            Toast.makeText(InitialSetupActivity.this, "Initial database loading finished", Toast.LENGTH_LONG).show();
            if (preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_PROFILE_SETUP_NEEDED, true)) {
                setupTheme();
            } else {
                finish();
            }
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
                Intent intent = SetCaloriesActivity.newIntent(InitialSetupActivity.this, true, SetCaloriesActivity.STAGE_PROFILE);
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
                Intent intent = SetMacrosActivity.newIntent(InitialSetupActivity.this, true);
                startActivityForResult(intent, REQUEST_MACROS);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setupUnits();
            }
        }
        if (requestCode == REQUEST_MACROS) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Intent intent = SetCaloriesActivity.newIntent(InitialSetupActivity.this, true, SetCaloriesActivity.STAGE_CONFIRM_KCAL);
                startActivityForResult(intent, REQUEST_CALORIES);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
