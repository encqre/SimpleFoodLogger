package lt.jasinevicius.simplefoodlogger;

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
import android.util.JsonWriter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import lt.jasinevicius.simplefoodlogger.reusable.LoadingProgressDialog;
import lt.jasinevicius.simplefoodlogger.reusable.SimpleConfirmationDialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for the Fragment of the settings page. Uses the AndroidX preferences library.
 *
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_ANSWER_IMPORT_BACKUP = 0;
    private static final int REQUEST_MACROS = 1;
    private static final int REQUEST_BACKUP = 2;
    private static final int REQUEST_IMPORT_BACKUP = 3;
    private static final int REQUEST_READ_PROGRESS = 4;
    private static final int REQUEST_CALORIES = 5;
    private static final int REQUEST_HIDDEN_FOODS = 6;
    private static final int REQUEST_HIDDEN_TAGS = 7;

    private static final int EXPORT_VERSION = 2;

    private static final String TAG_READ_PROGRESS = "loading_progress_dialog";

    private Preference kcalTarget;
    private Preference macros;
    private Preference backup;
    private Preference importBackup;
    private Preference hiddenFoods;
    private Preference hiddenTags;


    private CheckBoxPreference statsIgnoreZeroKcalDays;
    private EditTextPreference recentFoodsLength;
    private ListPreference units;
    private ListPreference theme;

    private SharedPreferences preferences;

    private List<Food> customFoodsToImport = new ArrayList<>();
    private List<Log> logsToImport = new ArrayList<>();
    private List<Tag> tagsToImport = new ArrayList<>();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_pref, rootKey);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        backup = (Preference) findPreference(LoggerSettings.PREFERENCE_BACKUP);
        backup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Initiate backup by getting Uri for the backup file
                String backupFileName = "SimpleFoodLogger_backup_" +
                    DateFormat.format("yyyy-MM-dd", new Date()).toString() + ".json";
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_TITLE, backupFileName);
                startActivityForResult(intent, REQUEST_BACKUP);
                return true;
            }
        });
        backup.setSummary(
            "Last backup date: " + preferences.getString(
                LoggerSettings.PREFERENCE_BACKUP_LAST_DATE, "never"
            )
        );

        importBackup = (Preference) findPreference(LoggerSettings.PREFERENCE_IMPORT_BACKUP);
        importBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Initiate import by getting Uri for the backup file
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, REQUEST_IMPORT_BACKUP);
                return true;
            }
        });

        kcalTarget = (Preference) findPreference(LoggerSettings.PREFERENCE_TARGET_CALORIES);
        kcalTarget.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = SetCaloriesActivity.newIntent(
                    getActivity(), false, SetCaloriesActivity.STAGE_PROFILE
                );
                startActivityForResult(intent, REQUEST_CALORIES);
                return true;
            }
        });
        kcalTarget.setSummary(
            preferences.getString(
                LoggerSettings.PREFERENCE_TARGET_CALORIES,
                LoggerSettings.PREFERENCE_TARGET_CALORIES_DEFAULT
            )
        );

        macros = (Preference) findPreference(LoggerSettings.PREFERENCE_SET_MACROS);
        macros.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = SetMacrosActivity.newIntent(getActivity(), false);
                startActivityForResult(intent, REQUEST_MACROS);
                return true;
            }
        });
        setMacrosPreferenceSummary();

        units = (ListPreference) findPreference(LoggerSettings.PREFERENCE_UNITS );
        units.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

        theme = (ListPreference) findPreference(LoggerSettings.PREFERENCE_THEME);
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getActivity().recreate();
                return true;

            }
        });
        theme.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

        statsIgnoreZeroKcalDays = (CheckBoxPreference) findPreference(LoggerSettings.PREFERENCE_STATS_IGNORE_ZERO_KCAL_DAYS);

        hiddenFoods = (Preference) findPreference(LoggerSettings.PREFERENCE_MANAGE_HIDDEN_FOODS);
        hiddenFoods.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = HiddenFoodsActivity.newIntent(getActivity());
                startActivityForResult(intent, REQUEST_HIDDEN_FOODS);
                return true;
            }
        });
        hiddenFoods.setSummary("Number of hidden foods: " + FoodManager.get(getActivity()).getHiddenFoods(null).size());

        hiddenTags = (Preference) findPreference(LoggerSettings.PREFERENCE_MANAGE_HIDDEN_TAGS);
        hiddenTags.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = HiddenTagsActivity.newIntent(getActivity());
                startActivityForResult(intent, REQUEST_HIDDEN_TAGS);
                return true;
            }
        });
        hiddenTags.setSummary("Number of hidden tags: " + FoodManager.get(getActivity()).getHiddenTags(null).size());

        recentFoodsLength = (EditTextPreference) findPreference(LoggerSettings.PREFERENCE_RECENT_FOODS_SIZE);
        recentFoodsLength.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER); //limiting input to numbers only
            }
        });
        recentFoodsLength.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        // Overriding default action when back button is pressed, to go back to the home tab
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

    // TODO might want to refactor and move all these async tasks to some BackupActivity/fragment
    // TODO might want to create additional Dialog to ask which data to export (etc. only logs)
    private class exportDataToFileTask extends AsyncTask<Uri, Integer, Void> {

        private final String[] PROGRESS_MESSAGES = new String[]{
            "Exporting logs",
            "Exporting custom foods",
            "Exporting tags"
        };

        LogManager lm;
        FoodManager fm;

        @Override
        protected void onPreExecute(){
            LoadingProgressDialog progressDialog = LoadingProgressDialog.newInstance(
                "Exporting data", "Backing up data...", 0, 0
            );
            progressDialog.setCancelable(false);
            progressDialog.setTargetFragment(SettingsFragment.this, REQUEST_READ_PROGRESS);
            progressDialog.show(getFragmentManager(), TAG_READ_PROGRESS);
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            lm = LogManager.get(getContext());
            fm = FoodManager.get(getContext());
            List<Log> fullLogList = lm.getLogs(null, null, null);
            List<Food> fullCustomFoodList = fm.getCustomFoods();
            List<Tag> fullTagList = fm.getTags(null, null, null);

            try {
                ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(
                    uris[0], "w"
                );
                FileOutputStream fileOutputStream =  new FileOutputStream(pfd.getFileDescriptor());

                JsonWriter writer = new JsonWriter(
                    new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8)
                );
                writer.setIndent("  ");
                writer.beginObject();
                writer.name("version").value(EXPORT_VERSION);

                writeLogsToJson(writer, fullLogList);
                writeCustomFoodsToJson(writer, fullCustomFoodList);
                writeTagsToJson(writer, fullTagList);

                writer.endObject();
                writer.flush();
                writer.close();
                fileOutputStream.close();
                pfd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void writeLogsToJson(JsonWriter writer, List<Log> logs) throws IOException {
            publishProgress(0, logs.size(), 0);

            writer.name("logs").beginArray();
            for (int i = 0; i < logs.size(); i++) {
                if (i % ((int) Math.ceil(logs.size()/100.0)) == 0){
                    publishProgress(i, logs.size());
                }
                Log log = logs.get(i);
                writer.beginObject();
                writer.name("id").value(log.getLogId().toString());
                writer.name("date").value(log.getDate().getTime());
                writer.name("food").value(log.getFood());
                writer.name("size").value(log.getSize());
                writer.name("kcal").value(log.getKcal());
                writer.name("protein").value(log.getProtein());
                writer.name("carbs").value(log.getCarbs());
                writer.name("fat").value(log.getFat());
                writer.endObject();
            }
            writer.endArray();
        }

        private void writeCustomFoodsToJson(JsonWriter writer, List<Food> foods) throws IOException {
            publishProgress(0, foods.size(), 1);

            writer.name("customFoods").beginArray();
            for (int i = 0; i < foods.size(); i++) {
                if (i % ((int) Math.ceil(foods.size()/100.0)) == 0){
                    publishProgress(i, foods.size());
                }
                Food food = foods.get(i);
                food.setServings(fm.getFoodServings(food.getFoodId())); // due to "lazy loading"

                writer.beginObject();
                writer.name("id").value(food.getFoodId().toString());
                writer.name("name").value(food.getName());
                writer.name("kcal").value(food.getKcal());
                writer.name("protein").value(food.getProtein());
                writer.name("carbs").value(food.getCarbs());
                writer.name("fat").value(food.getFat());
                writer.name("favorite").value(food.isFavorite());
                writer.name("consumedCount").value(food.getConsumedCount());
                writer.name("lastConsumed").value(food.getLastConsumed().getTime());
                writer.name("tags").beginArray();
                for (Tag tag : food.getTags()) {
                    writer.beginObject();
                    writer.name("id").value(tag.getTagId().toString());
                    writer.name("name").value(tag.getName());
                    writer.endObject();
                }
                writer.endArray();
                writer.name("servings").beginArray();
                for (Serving serving : food.getServings()) {
                    writer.beginObject();
                    writer.name("id").value(serving.getServingId().toString());
                    writer.name("name").value(serving.getName());
                    writer.name("size").value(serving.getSize());
                    writer.endObject();
                }
                writer.endArray();
                writer.endObject();
            }
            writer.endArray();
        }

        private void writeTagsToJson(JsonWriter writer, List<Tag> tags) throws IOException {
            publishProgress(0, tags.size(), 2);

            writer.name("tags").beginArray();
            for (int i = 0; i < tags.size(); i++) {
                if (i % ((int) Math.ceil(tags.size()/100.0)) == 0){
                    publishProgress(i, tags.size());
                }
                Tag tag = tags.get(i);
                writer.beginObject();
                writer.name("id").value(tag.getTagId().toString());
                writer.name("name").value(tag.getName());
                writer.name("color").value(tag.getColor());
                writer.endObject();
            }
            writer.endArray();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager()
                .findFragmentByTag(TAG_READ_PROGRESS);
            if (progress.length == 3) {
                progressDialog.updateProgress(progress[0], progress[1], PROGRESS_MESSAGES[progress[2]]);
            } else {
                progressDialog.updateProgress(progress[0], progress[1]);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager()
                .findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.dismiss();
            preferences.edit().putString(
                LoggerSettings.PREFERENCE_BACKUP_LAST_DATE,
                DateFormat.format("dd MMM yyyy", new Date()).toString()
            ).apply();
            backup.setSummary(
                "Last backup date: " + preferences.getString(
                    LoggerSettings.PREFERENCE_BACKUP_LAST_DATE, "never"
                )
            );
            Toast.makeText(getActivity(), "Data saved to file", Toast.LENGTH_LONG).show();
        }

    }

    private class importDataFromFileTask extends AsyncTask<Uri, Integer, Void> {

        private final String[] PROGRESS_MESSAGES = new String[]{
            "Importing logs",
            "Importing custom foods",
            "Importing tags"
        };

        String toastText;
        List<Log> fullLogList;
        List<Food> fullCustomFoodList;
        List<Tag> fullTagList;
        LogManager lm;
        FoodManager fm;

        @Override
        protected void onPreExecute(){
            LoadingProgressDialog progressDialog = LoadingProgressDialog.newInstance(
                "Importing data", "Importing data...", 0, 0
            );
            progressDialog.setCancelable(false);
            progressDialog.setTargetFragment(SettingsFragment.this, REQUEST_READ_PROGRESS);
            progressDialog.show(getFragmentManager(), TAG_READ_PROGRESS);
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            logsToImport.clear();
            customFoodsToImport.clear();
            tagsToImport.clear();

            lm = LogManager.get(getContext());
            fm = FoodManager.get(getContext());

            fullLogList = lm.getLogs(null, null, null);
            fullCustomFoodList = fm.getCustomFoods();
//            fullCustomFoodList = fm.getFoods(null, null, null, null);
            fullTagList = fm.getTags(null, null, null);

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(uris[0]);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();

                String fileContent = new String(buffer, StandardCharsets.UTF_8).trim();

                int backupVersion = detectBackupVersion(fileContent);
                android.util.Log.e("TKAJAS", "Backup version detected: " + backupVersion);

                switch (backupVersion) {
                    case 1:
                        importDataFromV1Backup(fileContent);
                        break;
                    case 2:
                        importDataFromV2Backup(fileContent);
                        break;
                    default:
                        toastText = "Unrecognized backup file format";
                        return null;
                }
            } catch (Exception e) {
                android.util.Log.e("Logger", e.getMessage());
                toastText = "There was a problem reading the file";
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager()
                .findFragmentByTag(TAG_READ_PROGRESS);
            if (progress.length == 3) {
                progressDialog.updateProgress(progress[0], progress[1], PROGRESS_MESSAGES[progress[2]]);
            } else {
                progressDialog.updateProgress(progress[0], progress[1]);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            LoadingProgressDialog progressDialog = (LoadingProgressDialog) getFragmentManager()
                .findFragmentByTag(TAG_READ_PROGRESS);
            progressDialog.dismiss();

            if (toastText != null) {
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
            } else if (logsToImport.size() > 0 || customFoodsToImport.size() > 0 || tagsToImport.size() > 0) {
                String message = "Found ";
                message += logsToImport.size() + " new logs, ";
                message += customFoodsToImport.size() + " new custom foods and ";
                message += tagsToImport.size() + " new tags ";
                message += "in the file. Do you want to import this data?";
                String title = "New data found";
                SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newInstance(message, title);
                dialog.setTargetFragment(SettingsFragment.this, REQUEST_ANSWER_IMPORT_BACKUP);
                dialog.show(getFragmentManager(), "lol");
            } else {
                toastText = "No new logs, foods or tags found in the file!";
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
            }
        }

        private int detectBackupVersion(String fileContent) {
            if (fileContent.length() == 0) {
                return 0;
            }

            if (
                fileContent.charAt(0) == '{' &&
                fileContent.charAt(fileContent.length() - 1) == '}'
            ) {
                // assuming this is JSON
                try {
                    JSONObject json = new JSONObject(fileContent);
                    return json.getInt("version");
                } catch (JSONException e) {
                    return 0;
                }
            } else {
                // assuming this is CSV otherwise.
                return 1;
            }
        }

        private void importDataFromV1Backup(String fileContent) throws Exception {
            // V1 is a CSV
            String[] rows = fileContent.split("\n");
            // determine if this is a log or custom food backup
            String[] cols = rows[0].split(";");
            if (cols.length == 10) {
                importLogsFromV1Backup(rows);
            } else if (cols.length == 19) {
                importCustomFoodsFromV1Backup(rows);
            } else {
                toastText = "Unrecognized backup file format";
            }
            return;
        }

        private void importLogsFromV1Backup(String[] rows) throws Exception {
            publishProgress(0, rows.length, 0);
            android.util.Log.e("TKAJAS", "importLogsFromV1Backup loop start");
            for (int i = 0; i < rows.length; i++) {
                if (i % ((int) Math.ceil(rows.length/100.0)) == 0){
                    publishProgress(i, rows.length);
                }

                String [] cols = rows[i].split(";");
                if (cols.length != 10) {
                    throw new Exception("Bad CSV format - was expecting 10 columns");
                }

                // check if log is already present in DB
                UUID logId = UUID.fromString(cols[0]);
                boolean found = false;
                for (int j = 0; j<fullLogList.size(); j++) {
                    if (logId.equals(fullLogList.get(j).getLogId())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }

                Log log = new Log(logId);
                log.setDate(new Date(Long.parseLong(cols[1])));
                log.setFood(cols[3]);
                log.setSize(Float.parseFloat(cols[4]));
                log.setKcal(Float.parseFloat(cols[6]));
                log.setProtein(Float.parseFloat(cols[7]));
                log.setCarbs(Float.parseFloat(cols[8]));
                log.setFat(Float.parseFloat(cols[9]));

                logsToImport.add(log);
            }
            android.util.Log.e("TKAJAS", "importLogsFromV1Backup loop end");
        }

        private void importCustomFoodsFromV1Backup(String[] rows) throws Exception {
            publishProgress(0, rows.length, 1);
            android.util.Log.e("TKAJAS", "importCustomFoodsFromV1Backup loop start");
            for (int i = 0; i < rows.length; i++) {
                if (i % ((int) Math.ceil(rows.length/100.0)) == 0){
                    publishProgress(i, rows.length);
                }

                String [] cols = rows[i].split(";");
                if (cols.length != 19) {
                    throw new Exception("Bad CSV format - was expecting 19 columns");
                }

                // check if food is already present in DB
                UUID foodId = UUID.fromString(cols[0]);
                boolean found = false;
                for (int j = 0; j<fullCustomFoodList.size(); j++) {
                    if (foodId.equals(fullCustomFoodList.get(j).getFoodId())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }

                Food food = new Food(foodId);
                food.setName(cols[2]);

                Tag tag = fm.getTagByName(cols[3]);
                List<Tag> tags = new ArrayList<>();
                tags.add(tag);
                food.setTags(tags);

                food.setKcal(Float.parseFloat(cols[4]));
                food.setProtein(Float.parseFloat(cols[5]));
                food.setCarbs(Float.parseFloat(cols[6]));
                food.setFat(Float.parseFloat(cols[7]));
                food.setType(Food.TYPE_CUSTOM);
                food.setPriority(Food.PRIORITY_CUSTOM);
                food.setFavorite(Integer.valueOf(cols[8]) == 1);

                List<Serving> servings = new ArrayList<>();
                Serving s1 = new Serving();
                s1.setName(cols[10]);
                s1.setSize(Float.parseFloat(cols[11]));
                s1.setFoodId(food.getFoodId());
                s1.setType(Serving.TYPE_CUSTOM);
                Serving s2 = new Serving();
                s2.setName(cols[13]);
                s2.setSize(Float.parseFloat(cols[14]));
                s2.setFoodId(food.getFoodId());
                s2.setType(Serving.TYPE_CUSTOM);
                Serving s3 = new Serving();
                s3.setName(cols[16]);
                s3.setSize(Float.parseFloat(cols[17]));
                s3.setFoodId(food.getFoodId());
                s3.setType(Serving.TYPE_CUSTOM);
                servings.add(s1);
                servings.add(s2);
                servings.add(s3);
                food.setServings(servings);

                customFoodsToImport.add(food);
            }
            android.util.Log.e("TKAJAS", "importCustomFoodsFromV1Backup loop end");
        }

        private void importDataFromV2Backup(String fileContent) throws JSONException {
            JSONObject json = new JSONObject(fileContent);

            JSONArray logs = json.getJSONArray("logs");
            importLogsFromV2Backup(logs);

            JSONArray tags = json.getJSONArray("tags");
            importTagsFromV2Backup(tags);

            JSONArray customFoods = json.getJSONArray("customFoods");
            importCustomFoodsFromV2Backup(customFoods);
        }

        private void importLogsFromV2Backup(JSONArray logs) throws JSONException {
            publishProgress(0, logs.length(), 0);
            android.util.Log.e("TKAJAS", "importLogsFromV2Backup loop start");
            for (int i = 0; i < logs.length(); i++) {
                if (i % ((int) Math.ceil(logs.length()/100.0)) == 0){
                    publishProgress(i, logs.length());
                }

                JSONObject item = logs.getJSONObject(i);
                // check if log is already present in DB
                UUID logId = UUID.fromString(item.getString("id"));
                boolean found = false;
                for (int j = 0; j<fullLogList.size(); j++) {
                    if (logId.equals(fullLogList.get(j).getLogId())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }

                Log log = new Log(logId);
                log.setDate(new Date(item.getLong("date")));
                log.setFood(item.getString("food"));
                log.setSize((float) item.getDouble("size"));
                log.setKcal((float) item.getDouble("kcal"));
                log.setProtein((float) item.getDouble("protein"));
                log.setCarbs((float) item.getDouble("carbs"));
                log.setFat((float) item.getDouble("fat"));

                logsToImport.add(log);
            }
            android.util.Log.e("TKAJAS", "importLogsFromV2Backup loop end");
        }

        private void importTagsFromV2Backup(JSONArray tags) throws JSONException {
            publishProgress(0, tags.length(), 2);
            android.util.Log.e("TKAJAS", "importTagsFromV2Backup loop start");
            for (int i = 0; i < tags.length(); i++) {
                if (i % ((int) Math.ceil(tags.length()/100.0)) == 0){
                    publishProgress(i, tags.length());
                }

                JSONObject item = tags.getJSONObject(i);
                // check if tag with matching ID or name is already present
                UUID tagId = UUID.fromString(item.getString("id"));
                String tagName = item.getString("name");
                boolean found = false;
                for (int j = 0; j<fullTagList.size(); j++) {
                    if (
                        tagId.equals(fullTagList.get(j).getTagId()) ||
                        tagName.equals(fullTagList.get(j).getName())
                    ) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }

                Tag tag = new Tag(tagId);
                tag.setName(tagName);
                tag.setType(Tag.TYPE_CUSTOM);
                tag.setColor(item.getString("color"));

                // for orderIds - put new imported tags at the end of existing order
                if (tagsToImport.size() == 0) {
                    tag.setOrderId(fm.getTagMaxOrderId());
                } else {
                    tag.setOrderId(tagsToImport.get(tagsToImport.size()-1).getOrderId());
                }

                tagsToImport.add(tag);
            }
            android.util.Log.e("TKAJAS", "importTagsFromV2Backup loop end");
        }

        private void importCustomFoodsFromV2Backup(JSONArray foods) throws JSONException {
            publishProgress(0, foods.length(), 1);
            android.util.Log.e("TKAJAS", "importCustomFoodsFromV2Backup loop start");
            for (int i = 0; i < foods.length(); i++) {
                if (i % ((int) Math.ceil(foods.length()/100.0)) == 0){
                    publishProgress(i, foods.length());
                }

                JSONObject item = foods.getJSONObject(i);
                // check if food is already present in DB
                UUID foodId = UUID.fromString(item.getString("id"));
                boolean found = false;
                for (int j = 0; j<fullCustomFoodList.size(); j++) {
                    if (foodId.equals(fullCustomFoodList.get(j).getFoodId())) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }

                Food food = new Food(foodId);
                food.setName(item.getString("name"));
                food.setKcal((float) item.getDouble("kcal"));
                food.setProtein((float) item.getDouble("protein"));
                food.setCarbs((float) item.getDouble("carbs"));
                food.setFat((float) item.getDouble("fat"));
                food.setType(Food.TYPE_CUSTOM);
                food.setPriority(Food.PRIORITY_CUSTOM);
                food.setFavorite(item.getBoolean("favorite"));
                food.setConsumedCount(item.getInt("consumedCount"));
                food.setLastConsumed(new Date(item.getLong("lastConsumed")));
                food.setTags(new ArrayList<>());
                food.setServings(new ArrayList<>());

                JSONArray jsonTags = item.getJSONArray("tags");
                for (int j = 0; j<jsonTags.length(); j++) {
                    JSONObject jsonTag = jsonTags.getJSONObject(j);
                    UUID tagId = UUID.fromString(jsonTag.getString("id"));
                    String tagName = jsonTag.getString("name");

                    // if tag ID OR name is found in DB, set that tag for the food
                    boolean foundTag = false;
                    for (int k = 0; k<fullTagList.size(); k++) {
                        if (
                            tagId.equals(fullTagList.get(k).getTagId()) ||
                            tagName.equals(fullTagList.get(k).getName())
                        ) {
                            food.addTag(fullTagList.get(k));
                            foundTag = true;
                            break;
                        }
                    }
                    if (foundTag) {
                        continue;
                    }
                    // otherwise, find the tag from backup file, which will be imported
                    for (int k = 0; k<tagsToImport.size(); k++) {
                        if (tagId.equals(tagsToImport.get(k).getTagId())) {
                            food.addTag(tagsToImport.get(k));
                            break;
                        }
                    }
                }

                JSONArray jsonServings = item.getJSONArray("servings");
                for (int j=0; j<jsonServings.length(); j++) {
                    JSONObject jsonServing = jsonServings.getJSONObject(j);
                    UUID servingId = UUID.fromString(jsonServing.getString("id"));
                    Serving serving = new Serving(servingId);
                    serving.setFoodId(foodId);
                    serving.setName(jsonServing.getString("name"));
                    serving.setSize((float) jsonServing.getDouble("size"));
                    serving.setType(Serving.TYPE_CUSTOM);
                    food.addServing(serving);
                }

                customFoodsToImport.add(food);
            }
            android.util.Log.e("TKAJAS", "importCustomFoodsFromV2Backup loop end");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri backupUri = null;

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ANSWER_IMPORT_BACKUP) {
                LogManager lm = LogManager.get(getContext());
                FoodManager fm = FoodManager.get(getContext());

                if (logsToImport.size() > 0) {
                    for (int i = 0; i< logsToImport.size(); i++) {
                        lm.addLog(logsToImport.get(i));
                    }
                }
                if (tagsToImport.size() > 0) {
                    for (int i = 0; i< tagsToImport.size(); i++) {
                        fm.addTag(tagsToImport.get(i));
                    }
                }
                if (customFoodsToImport.size() > 0) {
                    for (int i = 0; i< customFoodsToImport.size(); i++) {
                        fm.addFood(customFoodsToImport.get(i));
                    }
                }

                logsToImport.clear();
                customFoodsToImport.clear();
                tagsToImport.clear();
                Toast.makeText(getActivity(), "Data successfully imported!", Toast.LENGTH_LONG).show();
            }
            if (requestCode == REQUEST_MACROS) {
                setMacrosPreferenceSummary(); //updating with new macro target values
                Toast.makeText(getActivity(), "Macronutrient targets have been updated", Toast.LENGTH_LONG).show(); //TEMP DEBUG
            }
            if (requestCode == REQUEST_CALORIES) {
                kcalTarget.setSummary(preferences.getString(LoggerSettings.PREFERENCE_TARGET_CALORIES, LoggerSettings.PREFERENCE_TARGET_CALORIES_DEFAULT));
                setMacrosPreferenceSummary();
                Toast.makeText(getActivity(), "Daily calories target has been updated", Toast.LENGTH_LONG).show(); //TEMP DEBUG
            }
            if (requestCode == REQUEST_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    new exportDataToFileTask().execute(backupUri);
                }
            }
            if (requestCode == REQUEST_IMPORT_BACKUP) {
                if (data != null) {
                    backupUri = data.getData();
                    new importDataFromFileTask().execute(backupUri);
                }
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_ANSWER_IMPORT_BACKUP) {
                logsToImport.clear();
                customFoodsToImport.clear();
                tagsToImport.clear();
            }
        }
        if (requestCode == REQUEST_HIDDEN_FOODS) {
            hiddenFoods.setSummary(
                "Number of hidden foods: " +
                    FoodManager.get(getActivity()).getHiddenFoods(null).size()
            );
        }
        if (requestCode == REQUEST_HIDDEN_TAGS) {
            hiddenTags.setSummary(
                "Number of hidden tags: " +
                    FoodManager.get(getActivity()).getHiddenTags(null).size()
            );
        }
    }

    public void setMacrosPreferenceSummary() {
        int targetKcal = Integer.parseInt(
            preferences.getString(
                LoggerSettings.PREFERENCE_TARGET_CALORIES,
                LoggerSettings.PREFERENCE_TARGET_CALORIES_DEFAULT
            )
        );
        int targetProteinPercent = Integer.parseInt(
            preferences.getString(
                LoggerSettings.PREFERENCE_TARGET_PROTEIN_PERCENT,
                LoggerSettings.PREFERENCE_TARGET_PROTEIN_PERCENT_DEFAULT
            )
        );
        int targetCarbsPercent = Integer.parseInt(
            preferences.getString(
                LoggerSettings.PREFERENCE_TARGET_CARBS_PERCENT,
                LoggerSettings.PREFERENCE_TARGET_CARBS_PERCENT_DEFAULT
            )
        );
        int targetFatPercent = Integer.parseInt(
            preferences.getString(
                LoggerSettings.PREFERENCE_TARGET_FAT_PERCENT,
                LoggerSettings.PREFERENCE_TARGET_FAT_PERCENT_DEFAULT
            )
        );
        int targetProtein = Math.round(targetKcal * targetProteinPercent / 400f);
        int targetCarbs = Math.round(targetKcal * targetCarbsPercent / 400f);
        int targetFat = Math.round(targetKcal * targetFatPercent / 900f);
        macros.setSummary(
            getString(
                R.string.settings_fragment_macros_summary,
                targetProtein,
                Math.round(targetProteinPercent),
                targetCarbs,
                Math.round(targetCarbsPercent),
                targetFat,
                Math.round(targetFatPercent)
            )
        );
    }
}
