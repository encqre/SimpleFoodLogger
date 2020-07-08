package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.untrustworthypillars.simplefoodlogger.reusable.LoadingProgressDialog;
import com.untrustworthypillars.simplefoodlogger.reusable.SimpleConfirmationDialog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.os.AsyncTask;

/**
 * Class for the Fragment of the settings page. Uses the AndroidX preferences library.
 *
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_ANSWER_IMPORT_LOGS = 0;
    private static final int REQUEST_ANSWER_IMPORT_CUSTOM_FOODS = 1;
    private static final int REQUEST_MACROS = 2;
    private static final int REQUEST_HIDDEN_FOODS = 3;
    private static final int REQUEST_WRITE_LOG_BACKUP = 4;
    private static final int REQUEST_LOAD_LOG_BACKUP = 5;
    private static final int REQUEST_WRITE_CUSTOM_FOODS_BACKUP = 6;
    private static final int REQUEST_LOAD_CUSTOM_FOODS_BACKUP = 7;
    private static final int REQUEST_READ_PROGRESS = 8;
    private static final int REQUEST_CALORIES = 9;

    private static final String TAG_READ_PROGRESS = "loading_progress_dialog";

    private Preference mCaloriesTarget;
    private Preference mMacros;
    private Preference mBackupLogs;
    private Preference mBackupCustomFoods;
    private Preference mImportLogs;
    private Preference mImportCustomFoods;
    private Preference mHiddenFoods;


    private CheckBoxPreference mStatsIgnoreZeroKcalDays;
    private EditTextPreference mRecentFoodsLength;
    private ListPreference mUnits;
    private ListPreference mTheme;

    private SharedPreferences mPreferences;
    private int mTargetCalories;
    private int mTargetProtein;
    private int mTargetCarbs;
    private int mTargetFat;
    private int mTargetProteinPercent;
    private int mTargetCarbsPercent;
    private int mTargetFatPercent;

    //TODO Restart logger activity after theme switch, so that change would take effect.
    //TODO move preference key strings into strings.xml

    private List<Food> importedCustomFoodList = new ArrayList<>();
    private List<Log> importedLogList = new ArrayList<>();


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_pref, rootKey);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mBackupLogs = (Preference) findPreference("pref_backup_logs");
        mBackupLogs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                backupDatabase(REQUEST_WRITE_LOG_BACKUP, "Food_logs_backup_" + DateFormat.format("yyyy-MM-dd", new Date()).toString() + ".csv");
                return true;

            }
        });
        mBackupLogs.setSummary("Last backup date: " + mPreferences.getString("pref_last_backup_logs_date", "never"));

        mBackupCustomFoods = (Preference) findPreference("pref_backup_custom_foods");
        mBackupCustomFoods.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                backupDatabase(REQUEST_WRITE_CUSTOM_FOODS_BACKUP, "Custom_foods_backup_" + DateFormat.format("yyyy-MM-dd", new Date()).toString() + ".csv");
                return true;

            }
        });
        mBackupCustomFoods.setSummary("Last backup date: " + mPreferences.getString("pref_last_backup_custom_foods_date", "never"));

        mImportLogs = (Preference) findPreference("pref_import_logs");
        mImportLogs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                importDatabase(REQUEST_LOAD_LOG_BACKUP);
                return true;

            }
        });

        mImportCustomFoods = (Preference) findPreference("pref_import_custom_foods");
        mImportCustomFoods.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                importDatabase(REQUEST_LOAD_CUSTOM_FOODS_BACKUP);
                return true;

            }
        });

        mCaloriesTarget = (Preference) findPreference("pref_calories");
        mCaloriesTarget.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = SetCaloriesActivity.newIntent(getActivity(), false, SetCaloriesActivity.STAGE_PROFILE);
                startActivityForResult(intent, REQUEST_CALORIES);
                return true;
            }
        });
        mCaloriesTarget.setSummary(mPreferences.getString("pref_calories", "2500"));

        mMacros = (Preference) findPreference("pref_macros");
        mMacros.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = SetMacrosActivity.newIntent(getActivity());
                startActivityForResult(intent, REQUEST_MACROS);
                return true;
            }
        });
        setMacrosPreferenceSummary();

        mUnits = (ListPreference) findPreference("pref_units");
        mUnits.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

        mTheme = (ListPreference) findPreference("pref_theme");
        mTheme.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

        mStatsIgnoreZeroKcalDays = (CheckBoxPreference) findPreference("pref_stats_ignore_zero_kcal_days");
        CheckBoxPreference mProfileNeeded = (CheckBoxPreference) findPreference("initial_profile_setup_needed");


        mHiddenFoods = (Preference) findPreference("pref_hidden_food_list");
        mHiddenFoods.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = HiddenFoodsActivity.newIntent(getActivity());
                startActivityForResult(intent, REQUEST_HIDDEN_FOODS);
                return true;
            }
        });
        mHiddenFoods.setSummary("Number of hidden foods: " + FoodManager.get(getActivity()).getHiddenFoods("").size());

        mRecentFoodsLength = (EditTextPreference) findPreference("pref_recent_foods_size");
        mRecentFoodsLength.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER); //limiting input to numbers only
            }
        });
        mRecentFoodsLength.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        /**Overriding default action when back button is pressed, to go back to the home tab.*/
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN )
                {
                    LoggerActivity activity = (LoggerActivity) getActivity();
                    activity.setTab(0);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomePageFragment()).commit();
                    return true;
                }
                return false;
            }
        });

        return v;
    }


    public void backupDatabase(int requestCode, String backupFileName){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, backupFileName);
        startActivityForResult(intent, requestCode);
    }

    public void importDatabase(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, requestCode);
    }

    private class writeLogsToCSVFileTask extends AsyncTask<Uri, Integer, Void> {

        @Override
        protected void onPreExecute(){
            LoadingProgressDialog progressDialog = LoadingProgressDialog.newInstance("Writing logs", "Writing...", 0, 0);
            progressDialog.setCancelable(false);
            progressDialog.setTargetFragment(SettingsFragment.this, REQUEST_READ_PROGRESS);
            progressDialog.show(getFragmentManager(), TAG_READ_PROGRESS);
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            LogManager lm = LogManager.get(getContext());
            List<Log> fullLogList = lm.getLogs();
            publishProgress(0, fullLogList.size());

            try{
                ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uris[0], "w");
                FileOutputStream fileOutputStream =  new FileOutputStream(pfd.getFileDescriptor());
                PrintWriter pw = new PrintWriter(fileOutputStream);
                for (int i = 0; i<fullLogList.size(); i++) {
                    if (i % ((int) Math.ceil(fullLogList.size()/100.0)) == 0){
                        publishProgress(i,fullLogList.size());
                    }
                    Log log = fullLogList.get(i);
                    Float impSize = log.getSize()/28.35f; //////////////////////////////////////////////////////TEMPORARY
                    pw.println(log.getLogId().toString() + ";" + log.getDate().getTime() + ";"
                            + log.getDateText() + ";" + log.getFood() + ";"
                            + log.getSize().toString() + ";" + impSize.toString() + ";" ///////////////////////TEMPORARY
                            + log.getKcal().toString() + ";" + log.getProtein().toString() + ";"
                            + log.getCarbs().toString() + ";" + log.getFat().toString());
                    pw.flush();
                }
                pw.close();
                fileOutputStream.close();
                pfd.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager().findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.updateProgress(progress[0], progress[1]);
        }

        @Override
        protected void onPostExecute(Void result) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager().findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.dismiss();
            mPreferences.edit().putString("pref_last_backup_logs_date", DateFormat.format("dd MMM yyyy", new Date()).toString()).apply();
            mBackupLogs.setSummary("Last backup date: " + mPreferences.getString("pref_last_backup_logs_date", "never"));
            Toast.makeText(getActivity(), "Food logs saved to file", Toast.LENGTH_LONG).show();

        }

    }

    private class writeCustomFoodsToCSVFileTask extends AsyncTask<Uri, Integer, Void> {

        @Override
        protected void onPreExecute(){
            LoadingProgressDialog progressDialog = LoadingProgressDialog.newInstance("Writing custom foods", "Writing...", 0, 0);
            progressDialog.setCancelable(false);
            progressDialog.setTargetFragment(SettingsFragment.this, REQUEST_READ_PROGRESS);
            progressDialog.show(getFragmentManager(), TAG_READ_PROGRESS);
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            FoodManager fm = FoodManager.get(getContext());
            List<Food> fullFoodList = fm.getCustomFoods();
            publishProgress(0, fullFoodList.size());

            try{
                ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uris[0], "w");
                FileOutputStream fileOutputStream =  new FileOutputStream(pfd.getFileDescriptor());
                PrintWriter pw = new PrintWriter(fileOutputStream);
                for (int i = 0; i<fullFoodList.size(); i++) {
                    if (i % ((int) Math.ceil(fullFoodList.size()/100.0)) == 0){
                        publishProgress(i,fullFoodList.size());
                    }
                    Food food = fullFoodList.get(i);
                    pw.println(food.getFoodId().toString() + ";" + food.getSortID() + ";"
                            + food.getTitle() + ";" + food.getCategory() + ";"
                            + food.getKcal().toString() + ";" + food.getProtein().toString() + ";"
                            + food.getCarbs().toString() + ";" + food.getFat().toString() + ";"
                            + (food.isFavorite() ? 1 : 0) + ";" + (food.isHidden() ? 1 : 0) + ";"
                            + food.getPortion1Name() + ";" + food.getPortion1SizeMetric().toString() + ";" + food.getPortion1SizeImperial().toString() + ";"
                            + food.getPortion2Name() + ";" + food.getPortion2SizeMetric().toString() + ";" + food.getPortion2SizeImperial().toString() + ";"
                            + food.getPortion3Name() + ";" + food.getPortion3SizeMetric().toString() + ";" + food.getPortion3SizeImperial().toString());
                    pw.flush();
                }
                pw.close();
                fileOutputStream.close();
                pfd.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager().findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.updateProgress(progress[0], progress[1]);
        }

        @Override
        protected void onPostExecute(Void result) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager().findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.dismiss();
            mPreferences.edit().putString("pref_last_backup_custom_foods_date", DateFormat.format("dd MMM yyyy", new Date()).toString()).apply();
            mBackupCustomFoods.setSummary("Last backup date: " + mPreferences.getString("pref_last_backup_custom_foods_date", "never"));
            Toast.makeText(getActivity(), "Custom foods saved to file", Toast.LENGTH_LONG).show();
        }

    }

    private class importLogsFromCSVFileTask extends AsyncTask<Uri, Integer, Void> {

        String toastText = "";

        @Override
        protected void onPreExecute(){
            LoadingProgressDialog progressDialog = LoadingProgressDialog.newInstance("Reading logs", "Loading...", 0, 0);
            progressDialog.setCancelable(false);
            progressDialog.setTargetFragment(SettingsFragment.this, REQUEST_READ_PROGRESS);
            progressDialog.show(getFragmentManager(), TAG_READ_PROGRESS);
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            importedLogList.clear();
            LogManager lm = LogManager.get(getContext());
            List<Log> fullLogList = lm.getLogs();

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uris[0]);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                int logCount = 0;
                while (br.readLine() != null) logCount++;
                inputStream.close();
                if (logCount == 0) {
                    toastText = "Provided file is empty!";
                    return null;
                }
                publishProgress(0,logCount);
                inputStream = getContext().getContentResolver().openInputStream(uris[0]);
                br = new BufferedReader(new InputStreamReader(inputStream));
                int loopCounter = 0;
                while (true) {
                    if (loopCounter % ((int) Math.ceil(logCount/100.0)) == 0){
                        publishProgress(loopCounter,logCount);
                    }
                    String line = br.readLine();
                    if (line == null) break;
                    String [] el = line.split(";");
                    if (el.length != 10) {
                        throw new Exception("Bad CSV format - expecting 10 values in a line");
                    }
                    boolean found = false;
                    for (int i = 0; i<fullLogList.size(); i++) {
                        if (el[0].equals(fullLogList.get(i).getLogId().toString())) {
                            android.util.Log.e("Logger", el[0] + " UUID log was found in current DB!");
                            found = true;
                        }
                    }
                    if (!found) {
                        Log foundNewLog = new Log(UUID.fromString(el[0]));
                        foundNewLog.setDate(new Date(Long.valueOf(el[1])));
                        foundNewLog.setDateText();
                        foundNewLog.setFood(el[3]);
                        foundNewLog.setSize(Float.parseFloat(el[4]));
                        foundNewLog.setSizeImperial(Float.parseFloat(el[5]));
                        foundNewLog.setKcal(Float.parseFloat(el[6]));
                        foundNewLog.setProtein(Float.parseFloat(el[7]));
                        foundNewLog.setCarbs(Float.parseFloat(el[8]));
                        foundNewLog.setFat(Float.parseFloat(el[9]));
                        importedLogList.add(foundNewLog);
                        android.util.Log.e("Logger", el[0] + " UUID log was not found in DB, will ask to add it");
                    }
                    loopCounter++;
                }
                inputStream.close();
                if (importedLogList.size() == 0) {
                    toastText = "No new Logs found in the file!";
                    return null;
                }
                return null;
            } catch (Exception e) {
                android.util.Log.e("Logger", e.getMessage());
                toastText = "There was a problem reading the file";
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager().findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.updateProgress(progress[0], progress[1]);
        }

        @Override
        protected void onPostExecute(Void result) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager().findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.dismiss();
            if (toastText.equals("") && importedLogList.size() > 0) {
                String message = importedLogList.size() + " new Logs found in the file. Do you want to import these Logs to the database?";
                String title = "New logs found";
                SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newInstance(message, title);
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_ANSWER_IMPORT_LOGS);
                dialog.show(getFragmentManager(), "lol");
            } else if (!toastText.equals("")) {
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class importFoodsFromCSVFileTask extends AsyncTask<Uri, Integer, Void> {

        String toastText = "";

        @Override
        protected void onPreExecute(){
            LoadingProgressDialog progressDialog = LoadingProgressDialog.newInstance("Reading foods", "Loading...", 0, 0);
            progressDialog.setCancelable(false);
            progressDialog.setTargetFragment(SettingsFragment.this, REQUEST_READ_PROGRESS);
            progressDialog.show(getFragmentManager(), TAG_READ_PROGRESS);
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            importedCustomFoodList.clear();
            FoodManager fm = FoodManager.get(getContext());
            List<Food> fullFoodList = fm.getCustomFoods();
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uris[0]);
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                int foodCount = 0;
                while (br.readLine() != null) foodCount++;
                inputStream.close();
                if (foodCount == 0) {
                    toastText = "Provided file is empty!";
                    return null;
                }
                publishProgress(0,foodCount);
                inputStream = getContext().getContentResolver().openInputStream(uris[0]);
                br = new BufferedReader(new InputStreamReader(inputStream));
                int loopCounter = 0;
                while (true) {
                    if (loopCounter % ((int) Math.ceil(foodCount/100.0)) == 0){
                        publishProgress(loopCounter,foodCount);
                    }
                    String line = br.readLine();
                    if (line == null) break;
                    String [] el = line.split(";");
                    if (el.length != 19) {
                        throw new Exception("Bad CSV format - expecting 19 values in a line");
                    }
                    boolean found = false;
                    for (int i = 0; i<fullFoodList.size(); i++) {
                        if (el[0].equals(fullFoodList.get(i).getFoodId().toString())) {
                            android.util.Log.e("Logger", el[2] + " was found in current DB!");
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
                        foundNewFood.setType(0);
                        importedCustomFoodList.add(foundNewFood);
                        android.util.Log.e("Logger", el[2] + " was not found in DB, will ask to add");
                    }
                    loopCounter++;
                }
                inputStream.close();
                if (importedCustomFoodList.size() == 0) {
                    toastText = "No new custom foods found in the file!";
                    return null;
                }
                return null;
            } catch (Exception e) {
                android.util.Log.e("Logger", e.getMessage());
                toastText = "There was a problem reading the file";
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager().findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.updateProgress(progress[0], progress[1]);
        }

        @Override
        protected void onPostExecute(Void result) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager().findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.dismiss();
            if (toastText.equals("") && importedCustomFoodList.size() > 0) {
                String message = importedCustomFoodList.size() + " new Custom Foods found in the file. Do you want to import these Items to the database?";
                String title = "New custom foods found";
                SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newInstance(message, title);
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_ANSWER_IMPORT_CUSTOM_FOODS);
                dialog.show(getFragmentManager(), "lol");
            } else if (!toastText.equals("")) {
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri backupUri = null;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ANSWER_IMPORT_LOGS) {
                LogManager lm = LogManager.get(getContext());

                if (importedLogList.size() != 0) {
                    for (int i = 0; i<importedLogList.size(); i++) {
                        lm.addLog(importedLogList.get(i));
                    }
                }
                importedLogList.clear();
                Toast.makeText(getActivity(), "New Logs added!", Toast.LENGTH_LONG).show();
            }
            if (requestCode == REQUEST_ANSWER_IMPORT_CUSTOM_FOODS) {
                FoodManager fm = FoodManager.get(getContext());
                if (importedCustomFoodList.size() != 0) {
                    for (int i = 0; i<importedCustomFoodList.size(); i++) {
                        fm.addCustomFood(importedCustomFoodList.get(i));
                    }
                }
                importedCustomFoodList.clear();
                Toast.makeText(getActivity(), "New Custom Foods added!", Toast.LENGTH_LONG).show();
            }
            if (requestCode == REQUEST_MACROS) {
                setMacrosPreferenceSummary(); //updating with new macro target values
                Toast.makeText(getActivity(), "Macronutrient targets have been updated", Toast.LENGTH_LONG).show(); //TEMP DEBUG
            }
            if (requestCode == REQUEST_CALORIES) {
                mCaloriesTarget.setSummary(mPreferences.getString("pref_calories", "2500"));
                setMacrosPreferenceSummary();
                Toast.makeText(getActivity(), "Daily calories target has been updated", Toast.LENGTH_LONG).show(); //TEMP DEBUG
            }
            if (requestCode == REQUEST_WRITE_LOG_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    new writeLogsToCSVFileTask().execute(backupUri);
                }
            }
            if (requestCode == REQUEST_LOAD_LOG_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    new importLogsFromCSVFileTask().execute(backupUri);
                }
            }
            if (requestCode == REQUEST_WRITE_CUSTOM_FOODS_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    new writeCustomFoodsToCSVFileTask().execute(backupUri);
                }
            }
            if (requestCode == REQUEST_LOAD_CUSTOM_FOODS_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    new importFoodsFromCSVFileTask().execute(backupUri);
                }
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_ANSWER_IMPORT_LOGS) {
                importedLogList.clear();
            }
            if (requestCode == REQUEST_ANSWER_IMPORT_CUSTOM_FOODS) {
                importedCustomFoodList.clear();
            }
            if (requestCode == REQUEST_MACROS) {
            //Nothing to do really
            }
        }
        if (requestCode == REQUEST_HIDDEN_FOODS) {
            Toast.makeText(getActivity(), "Hidden food list has been updated", Toast.LENGTH_SHORT).show();
            mHiddenFoods.setSummary("Number of hidden foods: " + FoodManager.get(getActivity()).getHiddenFoods("").size());
        }

    }

    public void setMacrosPreferenceSummary() {
        mTargetCalories = Integer.parseInt(mPreferences.getString("pref_calories", "2500"));
        mTargetProteinPercent = Integer.parseInt(mPreferences.getString("pref_protein", "25"));
        mTargetCarbsPercent = Integer.parseInt(mPreferences.getString("pref_carbs", "45"));
        mTargetFatPercent = Integer.parseInt(mPreferences.getString("pref_fat", "30"));
        mTargetProtein = Math.round(mTargetCalories * mTargetProteinPercent / 400f);
        mTargetCarbs = Math.round(mTargetCalories * mTargetCarbsPercent / 400f);
        mTargetFat = Math.round(mTargetCalories * mTargetFatPercent / 900f);
        mMacros.setSummary(getString(R.string.settings_fragment_macros_summary,mTargetProtein,
                Math.round(mTargetProteinPercent),mTargetCarbs,Math.round(mTargetCarbsPercent),mTargetFat,Math.round(mTargetFatPercent)));
    }
}
