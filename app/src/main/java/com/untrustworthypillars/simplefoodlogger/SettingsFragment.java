package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.text.InputType;
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

    private static final int REQUEST_ANSWER = 0;

    private Preference mBackup;
    private Preference mImport;
    private Preference mImportCommonFoods;
    private Preference mImportExtendedFoods;
    private Preference mMacros;

    private EditTextPreference mCaloriesTarget;
    private EditTextPreference mProteinTarget;
    private EditTextPreference mCarbsTarget;
    private EditTextPreference mFatTarget;

    private CheckBoxPreference mStatsIgnoreZeroKcalDays;

    private EditTextPreference mRecentFoodsLength;

    //TODO Unit selection
    //TODO Initial launch dialogs / tutorials
    //TODO hidden food viewing/management/restoration



    private List<Food> importedCustomFoodList = new ArrayList<>();
    private List<Log> importedLogList = new ArrayList<>();


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_pref, rootKey);

        mBackup = (Preference) findPreference("pref_backup");
        mBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                backupDatabases();
                return true;

            }
        });

        mImport = (Preference) findPreference("pref_import");
        mImport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                importDatabases();
                return true;

            }
        });

        mImportCommonFoods = (Preference) findPreference("pref_import_common_foods");
        mImportCommonFoods.setEnabled(true);
        mImportCommonFoods.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                importCommonFoodDatabase();
                return true;
            }
        });

        mImportExtendedFoods = (Preference) findPreference("pref_import_extended_foods");
        mImportExtendedFoods.setEnabled(true);
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

        mMacros = (Preference) findPreference("pref_macros"); //TODO finish implementing dialog to set macros based on percentages
        mMacros.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = getFragmentManager();
                SetMacrosDialog dialog = SetMacrosDialog.newInstance();
                dialog.setTargetFragment(SettingsFragment.this, 0);
                dialog.show(fm, "SetMacros");
                return true;
            }
        });

        mProteinTarget = (EditTextPreference) findPreference("pref_protein");
        mProteinTarget.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER); //limiting input to numbers only
            }
        });
        mProteinTarget.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

        mCarbsTarget = (EditTextPreference) findPreference("pref_carbs");
        mCarbsTarget.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER); //limiting input to numbers only
            }
        });
        mCarbsTarget.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

        mFatTarget = (EditTextPreference) findPreference("pref_fat");
        mFatTarget.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER); //limiting input to numbers only
            }
        });
        mFatTarget.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

        mStatsIgnoreZeroKcalDays = (CheckBoxPreference) findPreference("pref_stats_ignore_zero_kcal_days");

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



    public void backupDatabases() {
        FoodManager fm = FoodManager.get(getContext());
        LogManager lm = LogManager.get(getContext());

        List<Food> fullFoodList = fm.getCustomFoods();
        List<Log> fullLogList = lm.getLogs();

        if(isExternalStorageWritable()) {
            android.util.Log.e("Logger", "External storage is writable!");
            File dir = getPrivateAlbumStorageDir(getContext(), "LoggerBackupsai");
            dir.mkdirs();
            File file = new File(dir, "FoodBackups.csv");
            File file2 = new File(dir, "LogBackups.csv");
            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                for (int i = 0; i<fullFoodList.size(); i++) {
                    Food food = fullFoodList.get(i);
                    pw.println(food.getFoodId().toString() + ";" + food.getSortID() + ";"
                            + food.getTitle() + ";" + food.getCategory() + ";"
                            + food.getKcal().toString() + ";" + food.getProtein().toString() + ";"
                            + food.getCarbs().toString() + ";" + food.getFat().toString() + ";"
                            + (food.isFavorite() ? 1 : 0) + ";" + (food.isHidden() ? 1 : 0) + ";"
                            + food.getPortion1Name() + ";" + food.getPortion1SizeMetric().toString() + ";" + food.getPortion1SizeImperial().toString() + ";"
                            + food.getPortion2Name() + ";" + food.getPortion2SizeMetric().toString() + ";" + food.getPortion2SizeImperial().toString() + ";"
                            + food.getPortion3Name() + ";" + food.getPortion3SizeMetric().toString() + ";" + food.getPortion3SizeImperial().toString() + ";");
                    pw.flush();
                }
                pw.close();
                f.close();
                f = new FileOutputStream(file2);
                pw = new PrintWriter(f);
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
                f.close();
                Toast.makeText(getActivity(), "Food items and logs exported to SD card!", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                android.util.Log.e("Logger", "file not found");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    public void importDatabases() {
        FoodManager fm = FoodManager.get(getContext());
        LogManager lm = LogManager.get(getContext());

        List<Food> fullFoodList = fm.getCustomFoods();
        List<Log> fullLogList = lm.getLogs();

        try {
            File dir = getPrivateAlbumStorageDir(getContext(), "LoggerBackupsai");
            File file = new File(dir, "FoodBackups.csv");
            File file2 = new File(dir, "LogBackups.csv");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader is = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(is);
            while (true) {
                String line = br.readLine();
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
                    foundNewFood.setType(0);
                    importedCustomFoodList.add(foundNewFood);
                    android.util.Log.e("Logger", el[1] + " was not found in DB, will ask to add");
                }
            }
            fis = new FileInputStream(file2);
            is = new InputStreamReader(fis);
            br = new BufferedReader(is);
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                String [] el = line.split(";");
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

            if (importedLogList.size() == 0 && importedCustomFoodList.size() == 0) {
                Toast.makeText(getActivity(), "No new Logs and Food database entries found in the imported data!", Toast.LENGTH_SHORT).show();
            } else {
                String message = importedCustomFoodList.size() + " new Food items and " + importedLogList.size() + " new Logs found in the imported data. Do you want to add this data?";
                SimpleDialog dialog = SimpleDialog.newInstance(message);
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_ANSWER);
                dialog.show(getFragmentManager(), "lol");
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = context.getExternalFilesDir(albumName);
        if (!file.mkdirs()) {
            android.util.Log.e("Logger", "Directory not created");
        }
        return file;
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ANSWER) {
                FoodManager fm = FoodManager.get(getContext());
                LogManager lm = LogManager.get(getContext());
                if (importedCustomFoodList.size() != 0) {
                    for (int i = 0; i<importedCustomFoodList.size(); i++) {
                        fm.addCustomFood(importedCustomFoodList.get(i));
                    }
                }
                importedCustomFoodList.clear();
                if (importedLogList.size() != 0) {
                    for (int i = 0; i<importedLogList.size(); i++) {
                        lm.addLog(importedLogList.get(i));
                    }
                }
                importedLogList.clear();
                Toast.makeText(getActivity(), "New items added!", Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_ANSWER) {
                importedCustomFoodList.clear();
                importedLogList.clear();
            }
        }

    }
}
