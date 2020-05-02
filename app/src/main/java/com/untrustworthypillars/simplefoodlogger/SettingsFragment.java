package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class for the Fragment of the settings page. Uses the AndroidX preferences library.
 *
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String ARG_MESSAGE = "message";

    private static final int REQUEST_ANSWER_IMPORT_LOGS = 0;
    private static final int REQUEST_ANSWER_IMPORT_CUSTOM_FOODS = 1;
    private static final int REQUEST_MACROS = 2;
    private static final int REQUEST_HIDDEN_FOODS = 3;
    private static final int REQUEST_WRITE_LOG_BACKUP = 4;
    private static final int REQUEST_LOAD_LOG_BACKUP = 5;
    private static final int REQUEST_WRITE_CUSTOM_FOODS_BACKUP = 6;
    private static final int REQUEST_LOAD_CUSTOM_FOODS_BACKUP = 7;



    private Preference mBackupLogs;
    private Preference mBackupCustomFoods;
    private Preference mImportLogs;
    private Preference mImportCustomFoods;
    private Preference mImportCommonFoods;
    private Preference mImportExtendedFoods;
    private Preference mMacros;
    private Preference mHiddenFoods;

    private EditTextPreference mCaloriesTarget;

    private CheckBoxPreference mStatsIgnoreZeroKcalDays;

    private EditTextPreference mRecentFoodsLength;

    private ListPreference mUnits;

    private SharedPreferences mPreferences;
    private int mTargetCalories;
    private int mTargetProtein;
    private int mTargetCarbs;
    private int mTargetFat;
    private float mTargetProteinPercent;
    private float mTargetCarbsPercent;
    private float mTargetFatPercent;

    //TODO Dark theme and switching between themes

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
                mPreferences.edit().putString("pref_last_backup_logs_date", DateFormat.format("dd MMM yyyy", new Date()).toString()).apply();
                mBackupLogs.setSummary("Last backup date: " + mPreferences.getString("pref_last_backup_logs_date", "never"));
                return true;

            }
        });
        mBackupLogs.setSummary("Last backup date: " + mPreferences.getString("pref_last_backup_logs_date", "never"));

        mBackupCustomFoods = (Preference) findPreference("pref_backup_custom_foods");
        mBackupCustomFoods.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                backupDatabase(REQUEST_WRITE_CUSTOM_FOODS_BACKUP, "Custom_foods_backup_" + DateFormat.format("yyyy-MM-dd", new Date()).toString() + ".csv");
                mPreferences.edit().putString("pref_last_backup_custom_foods_date", DateFormat.format("dd MMM yyyy", new Date()).toString()).apply();
                mBackupCustomFoods.setSummary("Last backup date: " + mPreferences.getString("pref_last_backup_custom_foods_date", "never"));
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

        mImportCommonFoods = (Preference) findPreference("pref_import_common_foods");
        mImportCommonFoods.setEnabled(false);
        mImportCommonFoods.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                importCommonFoodDatabase();
                return true;
            }
        });

        mImportExtendedFoods = (Preference) findPreference("pref_import_extended_foods");
        mImportExtendedFoods.setEnabled(false);
        mImportExtendedFoods.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                importExtendedFoodDatabase();
                return true;
            }
        });

        mCaloriesTarget = (EditTextPreference) findPreference("pref_calories");
        mCaloriesTarget.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER); //limiting input to numbers only
            }
        });
        mCaloriesTarget.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

        mMacros = (Preference) findPreference("pref_macros");
        mMacros.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = getFragmentManager();
                SetMacrosDialog dialog = SetMacrosDialog.newInstance();
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_MACROS);
                dialog.show(fm, "SetMacros");
                return true;
            }
        });
        setMacrosPreferenceSummary();

        mUnits = (ListPreference) findPreference("pref_units");
        mUnits.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

        mStatsIgnoreZeroKcalDays = (CheckBoxPreference) findPreference("pref_stats_ignore_zero_kcal_days");


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

    private void writeLogDatabaseToFileCSV(Uri uri) {
        LogManager lm = LogManager.get(getContext());
        List<Log> fullLogList = lm.getLogs();

        try{
            ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =  new FileOutputStream(pfd.getFileDescriptor());
            PrintWriter pw = new PrintWriter(fileOutputStream);
            for (int i = 0; i<fullLogList.size(); i++) {
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
    }

    private void writeCustomFoodsDatabaseToFileCSV(Uri uri) {
        FoodManager fm = FoodManager.get(getContext());
        List<Food> fullFoodList = fm.getCustomFoods();

        try{
            ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =  new FileOutputStream(pfd.getFileDescriptor());
            PrintWriter pw = new PrintWriter(fileOutputStream);
            for (int i = 0; i<fullFoodList.size(); i++) {
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
    }

    //TODO maybe add some display element saying "please wait, reading file.." or something, because it might take a noticable time if 1k+ items are in the list
    public void importLogsFromFileCSV(Uri uri) {
        LogManager lm = LogManager.get(getContext());
        List<Log> fullLogList = lm.getLogs();

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
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
            }
            inputStream.close();
            if (importedLogList.size() == 0) {
                Toast.makeText(getActivity(), "No new Logs found in the file!", Toast.LENGTH_LONG).show();
            } else {
                String message = importedLogList.size() + " new Logs found in the file. Do you want to import these Logs to the database?";
                SimpleDialog dialog = SimpleDialog.newInstance(message);
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_ANSWER_IMPORT_LOGS);
                dialog.show(getFragmentManager(), "lol");
            }
        } catch (Exception e) {
            android.util.Log.e("Logger", e.getMessage());
            Toast.makeText(getActivity(), "There was a problem reading the file", Toast.LENGTH_LONG).show();
        }
    }

    public void importCustomFoodsFromFileCSV(Uri uri) {
        FoodManager fm = FoodManager.get(getContext());
        List<Food> fullFoodList = fm.getCustomFoods();
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                String [] el = line.split(";");
                if (el.length != 19) {
                    throw new Exception("Bad CSV format - expecting 19 values in a line");
                }
                boolean found = false;
                for (int i = 0; i<fullFoodList.size(); i++) {
                    if (el[0].equals(fullFoodList.get(i).getFoodId().toString())) {
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
                    foundNewFood.setType(0);
                    importedCustomFoodList.add(foundNewFood);
                    android.util.Log.e("Logger", el[1] + " was not found in DB, will ask to add");
                }
            }
            inputStream.close();
            if (importedCustomFoodList.size() == 0) {
                Toast.makeText(getActivity(), "No new Custom Foods found in the file!", Toast.LENGTH_LONG).show();
            } else {
                String message = importedCustomFoodList.size() + " new Custom Foods found in the file. Do you want to import these Items to the database?";
                SimpleDialog dialog = SimpleDialog.newInstance(message);
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_ANSWER_IMPORT_CUSTOM_FOODS);
                dialog.show(getFragmentManager(), "lol");
            }
        } catch (Exception e) {
            android.util.Log.e("Logger", e.getMessage());
            Toast.makeText(getActivity(), "There was a problem reading the file", Toast.LENGTH_LONG).show();
        }
    }

    public void importCommonFoodDatabase() {
        FoodManager fm = FoodManager.get(getContext());
        List<Food> fullFoodList = fm.getCommonFoods();
        try {
            InputStream CSVstream = getContext().getAssets().open("CommonDb-v1.csv");
            InputStreamReader reader = new InputStreamReader(CSVstream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) break;
                String [] el = line.split(";");
                boolean found = false;
                for (int i = 0; i<fullFoodList.size(); i++) {
                    if (el[0].equals(fullFoodList.get(i).getFoodId().toString())) {
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
            }
            CSVstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void importExtendedFoodDatabase() {
        FoodManager fm = FoodManager.get(getContext());
        List<Food> fullFoodList = fm.getExtendedFoods();
        try {
            InputStream CSVstream = getContext().getAssets().open("FullDb-v1.csv");
            InputStreamReader reader = new InputStreamReader(CSVstream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) break;
                String [] el = line.split(";");
                boolean found = false;
                for (int i = 0; i<fullFoodList.size(); i++) {
                    if (el[0].equals(fullFoodList.get(i).getFoodId().toString())) {
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
            }
            CSVstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class SimpleDialog extends DialogFragment {
        public static SimpleDialog newInstance (String message) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_MESSAGE, message);

            SimpleDialog fragment = new SimpleDialog();
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("New Items Found")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResult(Activity.RESULT_OK);
                        }
                    })
                    .setMessage((String) getArguments().getSerializable(ARG_MESSAGE))
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResult(Activity.RESULT_CANCELED);
                        }
                    });
            return builder.create();
        }

        private void sendResult(int resultCode) {
            if (getTargetFragment() == null) {
                return;
            }

            Intent intent = new Intent();
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
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
            }
            if (requestCode == REQUEST_WRITE_LOG_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    writeLogDatabaseToFileCSV(backupUri);
                    Toast.makeText(getActivity(), "Food logs saved to file", Toast.LENGTH_LONG).show();
                }
            }
            if (requestCode == REQUEST_LOAD_LOG_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    importLogsFromFileCSV(backupUri);
                }
            }
            if (requestCode == REQUEST_WRITE_CUSTOM_FOODS_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    writeCustomFoodsDatabaseToFileCSV(backupUri);
                    Toast.makeText(getActivity(), "Custom foods saved to file", Toast.LENGTH_LONG).show();
                }
            }
            if (requestCode == REQUEST_LOAD_CUSTOM_FOODS_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    importCustomFoodsFromFileCSV(backupUri);
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
        }
        if (requestCode == REQUEST_HIDDEN_FOODS) {
            Toast.makeText(getActivity(), "Hidden foods updated", Toast.LENGTH_SHORT).show();
            mHiddenFoods.setSummary("Number of hidden foods: " + FoodManager.get(getActivity()).getHiddenFoods("").size());
        }

    }

    public void setMacrosPreferenceSummary() {
        mTargetCalories = Integer.parseInt(mPreferences.getString("pref_calories", "2500"));
        mTargetProtein = Integer.parseInt(mPreferences.getString("pref_protein", "200"));
        mTargetCarbs = Integer.parseInt(mPreferences.getString("pref_carbs", "300"));
        mTargetFat = Integer.parseInt(mPreferences.getString("pref_fat", "90"));
        mTargetProteinPercent = (float) (mTargetProtein * 4) / mTargetCalories * 100;
        mTargetCarbsPercent = (float) (mTargetCarbs * 4) / mTargetCalories * 100;
        mTargetFatPercent = (float) (mTargetFat * 9) / mTargetCalories * 100;
        mMacros.setSummary(getString(R.string.settings_fragment_macros_summary,mTargetProtein,
                Math.round(mTargetProteinPercent),mTargetCarbs,Math.round(mTargetCarbsPercent),mTargetFat,Math.round(mTargetFatPercent)));
    }
}
