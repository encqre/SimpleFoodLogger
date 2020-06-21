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

    private SharedPreferences mPreferences;

    private Boolean mDatabaseImportInProgress = false;

    private TextView mProgressTextview;
    private ProgressBar mProgressBar;

    private RadioButton mUnitsMetric;
    private RadioButton mUnitsImperial;
    private Button mUnitsContinueButton;

    private RadioButton mProfileGenderMale;
    private RadioButton mProfileGenderFemale;
    private EditText mProfileAge;
    private EditText mProfileWeight;
    private RadioGroup mProfileWeightRadioGroup;
    private RadioButton mProfileWeightKg;
    private RadioButton mProfileWeightLbs;
    private EditText mProfileHeightCm;
    private EditText mProfileHeightFeet;
    private EditText mProfileHeightIn;
    private RadioGroup mProfileHeightRadioGroup;
    private RadioButton mProfileHeightCmButton;
    private RadioButton mProfileHeightFtinButton;
    private Spinner mProfileActivitySpinner;
    private Spinner mProfileGoalSpinner;
    private Button mProfileBackButton;
    private Button mProfileManualButton;
    private Button mProfileCalculateButton;

    private TextView mSetKcalUpperText;
    private EditText mSetKcalEditText;
    private TextView mSetKcalLowerText;
    private Button mSetKcalBackButton;
    private Button mSetKcalContinueButton;

    private Button mSetPFCCancelButton;
    private Button mSetPFCSaveButton;

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
//                finish();//////////// temp
                setupProfile();
            }
        });

        if (mPreferences.getString("pref_units", "Metric").equals("Imperial")) {
            mUnitsImperial.setChecked(true);
        } else {
            mUnitsMetric.setChecked(true);
            mPreferences.edit().putString("pref_units", "Metric").apply();
        }

    }

    private void setupProfile() {

        // launch units setup

        setContentView(R.layout.initial_setup_calories_profile);

        mProfileGenderMale = (RadioButton) findViewById(R.id.initial_setup_calories_profile_gender_male);
        mProfileGenderMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreferences.edit().putString("pref_gender", "Male").apply();
            }
        });
        mProfileGenderFemale = (RadioButton) findViewById(R.id.initial_setup_calories_profile_gender_female);
        mProfileGenderFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreferences.edit().putString("pref_gender", "Female").apply();
            }
        });

        if (mPreferences.getString("pref_gender", "not_set").equals("Male")) {
            mProfileGenderMale.setChecked(true);
        } else if (mPreferences.getString("pref_gender", "not_set").equals("Female")) {
            mProfileGenderFemale.setChecked(true);
        }

        mProfileAge = (EditText) findViewById(R.id.initial_setup_calories_profile_age_edittext);
        mProfileAge.setInputType(InputType.TYPE_CLASS_NUMBER);
        mProfileAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mPreferences.edit().putString("pref_age", mProfileAge.getText().toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing
            }
        });
        mProfileAge.setText(mPreferences.getString("pref_age", ""));

        mProfileWeightKg = (RadioButton) findViewById(R.id.initial_setup_calories_profile_weight_kg);
        mProfileWeightLbs = (RadioButton) findViewById(R.id.initial_setup_calories_profile_weight_lbs);
        mProfileWeightRadioGroup = (RadioGroup) findViewById(R.id.initial_setup_calories_profile_weight_radiogroup);
        if (mPreferences.getString("pref_units", "Metric").equals("Metric")) {
            mProfileWeightKg.setChecked(true);
        } else {
            mProfileWeightLbs.setChecked(true);
        }
        mProfileWeight = (EditText) findViewById(R.id.initial_setup_calories_profile_weight_edittext);
        mProfileWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
        mProfileWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mProfileWeight.getText().toString().equals("")) {
                    if (mProfileWeightKg.isChecked()) {
                        mPreferences.edit().putString("pref_weight", mProfileWeight.getText().toString()).apply();
                    } else {
                        long weight = Math.round((Float.parseFloat(mProfileWeight.getText().toString()) / 2.2));
                        mPreferences.edit().putString("pref_weight", String.valueOf(weight)).apply();
                    }
                } else {
                    mPreferences.edit().putString("pref_weight", mProfileWeight.getText().toString()).apply();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing
            }
        });


        if (mPreferences.getString("pref_units", "Metric").equals("Metric")) {
            mProfileWeight.setText(mPreferences.getString("pref_weight", ""));
        } else if (!mPreferences.getString("pref_weight", "").equals("")) {
            mProfileWeight.setText( String.valueOf(Math.round(Float.parseFloat(mPreferences.getString("pref_weight", "0")) * 2.2)));
        } else {
            mProfileWeight.setText("");
        }

        mProfileWeightRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.initial_setup_calories_profile_weight_kg:
                        if (!mProfileWeight.getText().toString().equals("")) {
                            mProfileWeight.setText(String.valueOf(Math.round((Float.parseFloat(mProfileWeight.getText().toString()) / 2.2))));
                        }
                        break;
                    case R.id.initial_setup_calories_profile_weight_lbs:
                        if (!mProfileWeight.getText().toString().equals("")) {
                            mProfileWeight.setText(String.valueOf(Math.round((Float.parseFloat(mProfileWeight.getText().toString()) * 2.2))));
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        mProfileHeightRadioGroup = (RadioGroup) findViewById(R.id.initial_setup_calories_profile_height_radiogroup);
        mProfileHeightCmButton = (RadioButton) findViewById(R.id.initial_setup_calories_profile_height_cm);
        mProfileHeightFtinButton = (RadioButton) findViewById(R.id.initial_setup_calories_profile_height_ftin);
        if (mPreferences.getString("pref_units", "Metric").equals("Metric")) {
            mProfileHeightCmButton.setChecked(true);
        } else {
            mProfileHeightFtinButton.setChecked(true);
        }

        mProfileHeightCm = (EditText) findViewById(R.id.initial_setup_calories_profile_height_edittext_cm);
        mProfileHeightCm.setInputType(InputType.TYPE_CLASS_NUMBER);
        mProfileHeightCm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mProfileHeightCmButton.isChecked()) {
                    mPreferences.edit().putString("pref_height", mProfileHeightCm.getText().toString()).apply();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing
            }
        });

        mProfileHeightFeet = (EditText) findViewById(R.id.initial_setup_calories_profile_height_edittext_ft);
        mProfileHeightFeet.setInputType(InputType.TYPE_CLASS_NUMBER);
        mProfileHeightFeet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mProfileHeightFtinButton.isChecked()) {
                    int inches, feet;
                    if (!mProfileHeightIn.getText().toString().equals("")) {
                        inches = Integer.parseInt(mProfileHeightIn.getText().toString());
                    } else {
                        inches = 0;
                    }
                    if (!mProfileHeightFeet.getText().toString().equals("")) {
                        feet = Integer.parseInt(mProfileHeightFeet.getText().toString());
                    } else {
                        feet = 0;
                    }
                    mPreferences.edit().putString("pref_height", String.valueOf(ftinToCm(feet, inches))).apply();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing
            }
        });

        mProfileHeightIn = (EditText) findViewById(R.id.initial_setup_calories_profile_height_edittext_in);
        mProfileHeightIn.setInputType(InputType.TYPE_CLASS_NUMBER);
        mProfileHeightIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mProfileHeightFtinButton.isChecked()) {
                    int inches, feet;
                    if (!mProfileHeightIn.getText().toString().equals("")) {
                        inches = Integer.parseInt(mProfileHeightIn.getText().toString());
                    } else {
                        inches = 0;
                    }
                    if (!mProfileHeightFeet.getText().toString().equals("")) {
                        feet = Integer.parseInt(mProfileHeightFeet.getText().toString());
                    } else {
                        feet = 0;
                    }
                    mPreferences.edit().putString("pref_height", String.valueOf(ftinToCm(feet, inches))).apply();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing
            }
        });


        if (mPreferences.getString("pref_units", "Metric").equals("Metric")) {
            mProfileHeightCm.setText(mPreferences.getString("pref_height", ""));
        } else {
            mProfileHeightCm.setVisibility(View.INVISIBLE);
            mProfileHeightFeet.setVisibility(View.VISIBLE);
            mProfileHeightIn.setVisibility(View.VISIBLE);
            if (!mPreferences.getString("pref_height", "").equals("")) {
                long [] ftin = cmToFtin(Integer.parseInt(mPreferences.getString("pref_height", "0")));
                mProfileHeightFeet.setText(String.valueOf(ftin[0]));
                mProfileHeightIn.setText(String.valueOf(ftin[1]));
            } else {
                mProfileHeightFeet.setText("");
                mProfileHeightIn.setText("");
            }
        }

        mProfileHeightRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.initial_setup_calories_profile_height_cm:
                        mProfileHeightFeet.setVisibility(View.INVISIBLE);
                        mProfileHeightIn.setVisibility(View.INVISIBLE);
                        mProfileHeightCm.setVisibility(View.VISIBLE);
                        mProfileHeightCm.setText(mPreferences.getString("pref_height", ""));
                        break;
                    case R.id.initial_setup_calories_profile_height_ftin:
                        mProfileHeightFeet.setVisibility(View.VISIBLE);
                        mProfileHeightIn.setVisibility(View.VISIBLE);
                        mProfileHeightCm.setVisibility(View.INVISIBLE);
                        if (mPreferences.getString("pref_height", "0").equals("")) {
                            mProfileHeightFeet.setText("");
                            mProfileHeightIn.setText("");
                        } else {
                            long[] ftin = cmToFtin(Integer.parseInt(mPreferences.getString("pref_height", "0")));
                            mProfileHeightFeet.setText(String.valueOf(ftin[0]));
                            mProfileHeightIn.setText(String.valueOf(ftin[1]));
                        }
                        break;
                    default:
                        break;
                }
            }
        });


        mProfileActivitySpinner = (Spinner) findViewById(R.id.initial_setup_calories_profile_activity_spinner);
        mProfileActivitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.edit().putString("pref_activity_level", String.valueOf(position)).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }

        });
        mProfileActivitySpinner.setSelection(Integer.parseInt(mPreferences.getString("pref_activity_level", "0")));


        mProfileGoalSpinner = (Spinner) findViewById(R.id.initial_setup_calories_profile_goal_spinner);
        mProfileGoalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.edit().putString("pref_goal", String.valueOf(position)).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }

        });
        mProfileGoalSpinner.setSelection(Integer.parseInt(mPreferences.getString("pref_goal", "0")));

        //TODO only save preferences on clicking one of the three buttons, not after character changes in edittexts for simplicity
        mProfileBackButton = (Button) findViewById(R.id.initial_setup_calories_profile_button_back);
        mProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupUnits();
            }
        });

        mProfileManualButton = (Button) findViewById(R.id.initial_setup_calories_profile_button_manual);
        mProfileManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch manual entry
                setKcal(true);
            }
        });

        mProfileCalculateButton = (Button) findViewById(R.id.initial_setup_calories_profile_button_calculate);
        mProfileCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verify if all fields are filled and calculate kcal
                if (mProfileAge.getText().toString().equals("")) {
                    Toast.makeText(InitialSetupActivity.this, "Please fill in your age", Toast.LENGTH_SHORT).show();
                } else if (mProfileWeight.getText().toString().equals("")) {
                    Toast.makeText(InitialSetupActivity.this, "Please fill in your weight", Toast.LENGTH_SHORT).show();
                } else if (mProfileHeightCmButton.isChecked() && mProfileHeightCm.getText().toString().equals("")) {
                    Toast.makeText(InitialSetupActivity.this, "Please fill in your height", Toast.LENGTH_SHORT).show();
                } else if (mProfileHeightFtinButton.isChecked() && mProfileHeightFeet.getText().toString().equals("") && mProfileHeightIn.getText().toString().equals("")) {
                    Toast.makeText(InitialSetupActivity.this, "Please fill in your height", Toast.LENGTH_SHORT).show();
                } else {
                    setKcal(false);
                }
            }
        });

    }

    public void setKcal(boolean manual){
        String upperText;
        String lowerText;
        String recommendedKcal;
        if (manual) {
            upperText = "Enter your daily calories target:";
            lowerText = "You can go back to estimate your daily calories target based on your gender, age, weight, height, activity level and goal, using Mifflin-St Jeor Equation.";
            recommendedKcal = mPreferences.getString("pref_calories", "");
        } else {
            upperText = "Your recommended daily calories target*:";
            lowerText = "*Calculated based on your gender, age, weight, height, activity level and goal, using Mifflin-St Jeor Equation.";
            recommendedKcal = calculateKcalMifflinStJeor();
            mPreferences.edit().putString("pref_calories_calculated", recommendedKcal).apply();
        }

        setContentView(R.layout.initial_setup_calories_set);

        mSetKcalUpperText = (TextView) findViewById(R.id.initial_setup_calories_set_text_upper);
        mSetKcalUpperText.setText(upperText);

        mSetKcalLowerText = (TextView) findViewById(R.id.initial_setup_calories_set_text_lower);
        mSetKcalLowerText.setText(lowerText);

        mSetKcalEditText = (EditText) findViewById(R.id.initial_setup_calories_set_kcal_edittext);
        mSetKcalEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetKcalEditText.setText(recommendedKcal);
        mSetKcalEditText.setHint(recommendedKcal);

        mSetKcalBackButton = (Button) findViewById(R.id.initial_setup_calories_set_button_back);
        mSetKcalBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.edit().putString("pref_calories", mSetKcalEditText.getText().toString()).apply();
                setupProfile();
            }
        });

        mSetKcalContinueButton = (Button) findViewById(R.id.initial_setup_calories_set_button_continue);
        mSetKcalContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSetKcalEditText.getText().toString().equals("")) {
                    Toast.makeText(InitialSetupActivity.this, "Please fill daily target calories field", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(mSetKcalEditText.getText().toString()) <= 0) {
                    Toast.makeText(InitialSetupActivity.this, "Please enter a valid daily target calories number", Toast.LENGTH_SHORT).show();
                } else {
                    mPreferences.edit().putString("pref_calories", mSetKcalEditText.getText().toString()).apply();
                    setPFC();
                }
            }
        });

    }

    public void setPFC() {

        //TODO use SetMacrosActivity here
        //TODO add 3 more radio buttons for recommended preset PFC ratios - moderate/high/very high protein
        setContentView(R.layout.fragment_set_macros);

        mSetPFCSaveButton = (Button) findViewById(R.id.fragment_set_macros_save_button);
        mSetPFCSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSetPFCCancelButton = (Button) findViewById(R.id.fragment_set_macros_cancel_button);
        mSetPFCCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKcal(true);
            }
        });
    }

    public static long ftinToCm(int ft, int in) {
        return Math.round((in * 2.54) + (ft * 12 * 2.54));
    }

    public static long[] cmToFtin(int cm) {
        long ft = (long)(cm / 30.48);
        long in = Math.round((cm%30.48) / 2.54);
        return new long[] {ft, in};
    }

    public String calculateKcalMifflinStJeor(){
        float [] activityMultiplierTable = {1.2f,1.375f,1.465f,1.55f,1.725f};
        float [] goalMutliplierTable = {1f,0.9f,0.8f,1.1f,1.2f};

        String gender = mPreferences.getString("pref_gender", "Male");
        int weight = Integer.parseInt(mPreferences.getString("pref_weight", "80"));
        int height = Integer.parseInt(mPreferences.getString("pref_height", "175"));
        int age = Integer.parseInt(mPreferences.getString("pref_age", "25"));
        float activityMultiplier = activityMultiplierTable[Integer.parseInt(mPreferences.getString("pref_activity_level", "1"))];
        float goalMultiplier = goalMutliplierTable[Integer.parseInt(mPreferences.getString("pref_goal", "1"))];

        float estimatedKcal;
        if (gender.equals("Male")){
            estimatedKcal = activityMultiplier * goalMultiplier * ((10 * weight) + (6.25f * height) - (5 * age) + 5);
        } else {
            estimatedKcal = activityMultiplier * goalMultiplier * ((10 * weight) + (6.25f * height) - (5 * age) - 161);
        }

        return String.valueOf((int) estimatedKcal);
    }

}
