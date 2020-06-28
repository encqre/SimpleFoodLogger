package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

//List of things to do on initial launch:
//TODO 4. Tutorials - probably some dialogs with text when opening some tab for the first time.

public class InitialSetupActivity extends AppCompatActivity {

    private static final int REQUEST_CALORIES = 0;
    private static final int REQUEST_MACROS = 1;

    private SharedPreferences mPreferences;

    private Boolean mDatabaseImportInProgress = false;

    private TextView mProgressTextview;
    private ProgressBar mProgressBar;

    private RadioButton mUnitsMetric;
    private RadioButton mUnitsImperial;
    private Button mUnitsContinueButton;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, InitialSetupActivity.class);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (mDatabaseImportInProgress) {
            //Not allowing to close activity while database import is running to prevent bad things
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_progress);

        mPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(InitialSetupActivity.this);

        mProgressTextview = (TextView) findViewById(R.id.dialog_loading_progress_textview);
        mProgressTextview.setText("Performing initial setup..");
        mProgressBar = (ProgressBar) findViewById(R.id.dialog_loading_progress_bar);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);

        Toast.makeText(this, "Somebody launched initial setup!", Toast.LENGTH_LONG).show();

        if (mPreferences.getBoolean("initial_database_setup_needed", true)) {
            new initialDatabaseImportTask().execute();
        } else if (mPreferences.getBoolean("initial_profile_setup_needed", true)) {
            setupUnits();
        } else {
            finish();
        }
    }

    private class initialDatabaseImportTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            mDatabaseImportInProgress = true;
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
                            android.util.Log.e("Logger", el[1] + " was found in current DB!");
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
                        android.util.Log.e("Logger", el[1] + " was not found in DB, now was added");
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
                InputStream CSVStream = InitialSetupActivity.this.getAssets().open("FullDb-v1.csv");
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
                            android.util.Log.e("Logger", el[1] + " was found in current DB!");
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
                        android.util.Log.e("Logger", el[1] + " was not found in DB, now was added");
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
            mProgressBar.setProgress(mProgressBar.getProgress() + 1);
        }

        @Override
        protected void onPostExecute(Void result) {
            mPreferences.edit().putBoolean("initial_database_setup_needed", false).apply();
            mDatabaseImportInProgress = false;
            Toast.makeText(InitialSetupActivity.this, "Initial database loading finished", Toast.LENGTH_LONG).show();
            if (mPreferences.getBoolean("initial_profile_setup_needed", true)) {
                setupUnits();
            } else {
                finish();
            }
        }
    }

    private void setupUnits() {
        //Setup some default numbers for units/calories/PFC if they are not set yet

        if (mPreferences.getString("pref_units", "not_set").equals("not_set")) {
            mPreferences.edit().putString("pref_units", "Metric").apply();
        }
        if (mPreferences.getString("pref_calories", "not_set").equals("not_set")) {
            mPreferences.edit().putString("pref_calories", "2500").apply();
        }
        if (mPreferences.getString("pref_protein", "not_set").equals("not_set")) {
            mPreferences.edit().putString("pref_protein", "155").apply();
        }
        if (mPreferences.getString("pref_carbs", "not_set").equals("not_set")) {
            mPreferences.edit().putString("pref_carbs", "300").apply();
        }
        if (mPreferences.getString("pref_fat", "not_set").equals("not_set")) {
            mPreferences.edit().putString("pref_fat", "75").apply();
        }

        //if user closes the initial setup activity from this point, since we already have some
        // values set, no need to launch the initial setup activity again when launching the app

//        mPreferences.edit().putBoolean("initial_profile_setup_needed", false).apply();

        // launch units setup

        setContentView(R.layout.initial_setup_units);

        mUnitsMetric = (RadioButton) findViewById(R.id.initial_setup_units_metric);
        mUnitsMetric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.edit().putString("pref_units", "Metric").apply();
            }
        });
        mUnitsImperial = (RadioButton) findViewById(R.id.initial_setup_units_imperial);
        mUnitsImperial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.edit().putString("pref_units", "Imperial").apply();
            }
        });
        mUnitsContinueButton = (Button) findViewById(R.id.initial_setup_units_button_next);
        mUnitsContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch calories setup
                Intent intent = SetCaloriesActivity.newIntent(InitialSetupActivity.this, "", SetCaloriesActivity.STAGE_PROFILE);
                startActivityForResult(intent, REQUEST_CALORIES);
            }
        });

        if (mPreferences.getString("pref_units", "Metric").equals("Imperial")) {
            mUnitsImperial.setChecked(true);
        } else {
            mUnitsMetric.setChecked(true);
            mPreferences.edit().putString("pref_units", "Metric").apply();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CALORIES) {
            if (resultCode == Activity.RESULT_OK) {
                //launch macros setup
                Intent intent = SetMacrosActivity.newIntent(InitialSetupActivity.this);
                startActivityForResult(intent, REQUEST_MACROS);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setupUnits();
            }
        }
        if (requestCode == REQUEST_MACROS) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Intent intent = SetCaloriesActivity.newIntent(InitialSetupActivity.this, "", SetCaloriesActivity.STAGE_CONFIRM_KCAL);
                startActivityForResult(intent, REQUEST_CALORIES);
            }
        }
    }
}
