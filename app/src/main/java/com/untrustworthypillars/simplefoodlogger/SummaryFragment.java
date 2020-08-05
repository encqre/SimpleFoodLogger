package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.untrustworthypillars.simplefoodlogger.reusable.TutorialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class for the Fragment of the summary/statistics
 *
 */


//TODO whole UI for statistics page really needs improvement
//TODO some graphs maybe
//TODO searchview in toolbar in the 'food stats' section?

public class SummaryFragment extends Fragment {

    public static final int TAB_DAY_SUMMARY = 0;
    public static final int TAB_FOOD_SUMMARY = 1;

    public static final int SPINNER_PERIOD_7_DAYS = 0;
    public static final int SPINNER_PERIOD_30_DAYS = 1;
    public static final int SPINNER_PERIOD_90_DAYS = 2;
    public static final int SPINNER_PERIOD_YEAR = 3;
    public static final int SPINNER_PERIOD_CUSTOM = 4;

    public static final int SPINNER_SORT_DAY_DATE_OLD = 0;
    public static final int SPINNER_SORT_DAY_DATE_NEW = 1;
    public static final int SPINNER_SORT_DAY_KCAL_HIGH = 2;
    public static final int SPINNER_SORT_DAY_KCAL_LOW = 3;
    public static final int SPINNER_SORT_DAY_PROTEIN_HIGH = 4;
    public static final int SPINNER_SORT_DAY_PROTEIN_LOW = 5;
    public static final int SPINNER_SORT_DAY_CARBS_HIGH = 6;
    public static final int SPINNER_SORT_DAY_CARBS_LOW = 7;
    public static final int SPINNER_SORT_DAY_FAT_HIGH = 8;
    public static final int SPINNER_SORT_DAY_FAT_LOW = 9;

    public static final int SPINNER_SORT_FOOD_COUNT_HIGH = 0;
    public static final int SPINNER_SORT_FOOD_COUNT_LOW = 1;
    public static final int SPINNER_SORT_FOOD_KCAL_HIGH = 2;
    public static final int SPINNER_SORT_FOOD_KCAL_LOW = 3;
    public static final int SPINNER_SORT_FOOD_WEIGHT_HIGH = 4;
    public static final int SPINNER_SORT_FOOD_WEIGHT_LOW = 5;
    public static final int SPINNER_SORT_FOOD_DATE_NEW = 6;
    public static final int SPINNER_SORT_FOOD_DATE_OLD = 7;

    private static final int REQUEST_START_DATE = 0;
    private static final int REQUEST_END_DATE = 1;
    private static final int REQUEST_TUTORIAL = 2;

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TUTORIAL = "DialogTutorial";

    private LogManager mLogManager;
    private TabLayout mTabLayout;
    private RecyclerView mSummaryRecyclerView;
    private DaySummaryAdapter mDaySummaryAdapter;
    private FoodSummaryAdapter mFoodSummaryAdapter;
    private Spinner mPeriodSpinner;
    private Spinner mSortSpinner;
    private TextView mSummaryText;
    private ArrayAdapter<CharSequence> mPeriodSpinnerAdapter;
    private ArrayAdapter<CharSequence> mDaySortSpinnerAdapter;
    private ArrayAdapter<CharSequence> mFoodSortSpinnerAdapter;
    private Date mStartDate;
    private Date mEndDate;
    private List<FoodSummary> mFoodSummaryList;
    private List<Log> mLogSummaryList;
    private Toolbar toolbar;

    private SharedPreferences mPreferences;
    private String mUnits;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("sveikas", "setHasOptionsMenu tuoj leis");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.fragment_summary_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        toolbar.inflateMenu(R.menu.toolbar_stats);
        toolbar.setTitle("Stats (Past 7 days)");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == R.id.toolbar_stats_sort_calories_high){
//                    Toast.makeText(getActivity(), "DATA ble", Toast.LENGTH_SHORT).show();
//                }
//                return false;
//            }
//        });

//        android.util.Log.d("hey karoli", toolbar.findViewById(R.id.toolbar_stats_date_filter).getClass().getName());

        mTabLayout = (TabLayout) v.findViewById(R.id.summary_tabs);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**Set correct adapters and visible elements for specific tabs*/
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (mTabLayout.getSelectedTabPosition()) {
                    case TAB_DAY_SUMMARY:
                        mLogSummaryList = summarizeLogs(mStartDate, mEndDate);
                        mDaySummaryAdapter = new DaySummaryAdapter(mLogSummaryList);
                        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                        mSortSpinner.setAdapter(mDaySortSpinnerAdapter);
                        mSortSpinner.setSelection(0);
                        getActivity().invalidateOptionsMenu();
                        break;
                    case TAB_FOOD_SUMMARY:
                        mFoodSummaryList = summarizeFoods(mStartDate, mEndDate);
                        mFoodSummaryAdapter = new FoodSummaryAdapter(mFoodSummaryList);
                        mSummaryRecyclerView.setAdapter(mFoodSummaryAdapter);
                        mSortSpinner.setAdapter(mFoodSortSpinnerAdapter);
                        mSortSpinner.setSelection(0);
                        getActivity().invalidateOptionsMenu();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUnits = mPreferences.getString("pref_units", "Metric");
        mLogManager = LogManager.get(getContext());
        mSummaryRecyclerView = (RecyclerView) v.findViewById(R.id.summary_recycler);
        mSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPeriodSpinner = (Spinner) v.findViewById(R.id.summary_spinner_period); //Spinner that provides selections for time periods

        //Creating adapter for period spinner, using resources array as list of items and default android layout for single spinner item
        mPeriodSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_period_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        mPeriodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPeriodSpinner.setAdapter(mPeriodSpinnerAdapter);

        mPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case SPINNER_PERIOD_7_DAYS:
                        mStartDate = Calculations.incrementDay(new Date(), -7);
                        mEndDate = new Date();
                        updatePeriod();
                        break;
                    case SPINNER_PERIOD_30_DAYS:
                        mStartDate = Calculations.incrementDay(new Date(), -30);
                        mEndDate = new Date();
                        updatePeriod();
                        break;
                    case SPINNER_PERIOD_90_DAYS:
                        mStartDate = Calculations.incrementDay(new Date(), -90);
                        mEndDate = new Date();
                        updatePeriod();
                        break;
                    case SPINNER_PERIOD_YEAR:
                        mStartDate = Calculations.incrementDay(new Date(), -365);
                        mEndDate = new Date();
                        updatePeriod();
                        break;
                    case SPINNER_PERIOD_CUSTOM:
                        mStartDate = Calculations.incrementDay(new Date(), -7); //setting start and end for 1 week period just in case
                        mEndDate = new Date(); //setting start and end for 1 week period just in case
                        FragmentManager fm = getFragmentManager();
                        DatePickerDialog dialog = DatePickerDialog.newInstance(new Date(), "Select start date");
                        dialog.setTargetFragment(SummaryFragment.this, REQUEST_START_DATE);
                        dialog.show(fm, DIALOG_DATE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        mSortSpinner = (Spinner) v.findViewById(R.id.summary_spinner_sort); //Spinner that provides selections for sort methods

        //Creating adapters for sort spinner, using resources array as list of items and default android layout for single spinner item,
        //Two adapters are required because there are different sorting options for summary and food stats tabs
        mDaySortSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_day_sort_options, android.R.layout.simple_spinner_item);
        mFoodSortSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_food_sort_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        mDaySortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFoodSortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSortSpinner.setAdapter(mDaySortSpinnerAdapter); //By default set day summary adapter, because it's the first tab

        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePeriod(); //Calling update period, which in turn will call updateSorting() and will set up the adapter.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSummaryText = (TextView) v.findViewById(R.id.summary_text_summary);

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

        if (!mPreferences.getBoolean("tutorial_statistics_done", false)) {
            FragmentManager fm = getFragmentManager();
            TutorialDialog dialog = TutorialDialog.newInstance(getString(R.string.tutorial_statistics_text));
            dialog.setTargetFragment(SummaryFragment.this, REQUEST_TUTORIAL);
            dialog.show(fm, DIALOG_TUTORIAL);
        }

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        android.util.Log.d("sveikas", "onCreateOptionsMenu paleido");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_stats, menu);
        MenuItem filterItemSummary = menu.findItem(R.id.toolbar_stats_sort);
        MenuItem filterItemFood = menu.findItem(R.id.toolbar_stats_sort_food);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        android.util.Log.d("sveikas", "onPrepareOptionsMenu paleido");

        MenuItem filterItemSummary = menu.findItem(R.id.toolbar_stats_sort);
        MenuItem filterItemFood = menu.findItem(R.id.toolbar_stats_sort_food);

        filterItemSummary.setVisible(mTabLayout.getSelectedTabPosition() == TAB_DAY_SUMMARY);
        filterItemFood.setVisible(mTabLayout.getSelectedTabPosition() == TAB_FOOD_SUMMARY);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.toolbar_stats_date_filter){
            Toast.makeText(getActivity(), "DATA ble", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            /**If setting the custom date fails, default to 1 week and call updatePeriod()*/
                mStartDate = Calculations.incrementDay(new Date(), -7);
                mEndDate = new Date();
                mPeriodSpinner.setSelection(0);
                updatePeriod();
                return;
        }
        if (requestCode == REQUEST_START_DATE) {
            mStartDate = (Date) data.getSerializableExtra(DatePickerDialog.EXTRA_DATE);
            FragmentManager fm = getFragmentManager();
            DatePickerDialog dialog = DatePickerDialog.newInstance(new Date(), "Select end date", mStartDate);
            dialog.setTargetFragment(SummaryFragment.this, REQUEST_END_DATE);
            dialog.show(fm, DIALOG_DATE);
        }
        if (requestCode == REQUEST_END_DATE) {
            mEndDate = (Date) data.getSerializableExtra(DatePickerDialog.EXTRA_DATE);
            mEndDate = Calculations.incrementDay(mEndDate, 1);
            updatePeriod();
        }
        if (requestCode == REQUEST_TUTORIAL) {
            mPreferences.edit().putBoolean("tutorial_statistics_done", true).apply();
        }
    }

    /**RecyclerView Holder and Adapter for day summary entries*/
    private class DaySummaryHolder extends RecyclerView.ViewHolder {
        private TextView mDateText;
        private TextView mCaloriesText;
        private TextView mMacrosText;


        public DaySummaryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_summary_log, parent, false));
            //itemView.setOnClickListener(this);  --Later maybe i'll implement onClick

            mDateText = (TextView) itemView.findViewById(R.id.list_item_summary_log_date);
            mCaloriesText = (TextView) itemView.findViewById(R.id.list_item_summary_log_calories);
            mMacrosText = (TextView) itemView.findViewById(R.id.list_item_summary_log_macros);
        }

        public void bind(Log summaryLog) {
            mDateText.setText(summaryLog.getDateText());
            mCaloriesText.setText(summaryLog.getKcal().intValue() + " kcal");
            mMacrosText.setText("P: " + summaryLog.getProtein().intValue() + "g C: " + summaryLog.getCarbs().intValue() + "g F: " + summaryLog.getFat().intValue() + "g");
        }

    }

    private class DaySummaryAdapter extends RecyclerView.Adapter<DaySummaryHolder> {
        private List<Log> mSelectedPeriodLogs;

        public DaySummaryAdapter(List<Log> logs) {
            mSelectedPeriodLogs = logs;
        }

        @Override
        public DaySummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new DaySummaryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(DaySummaryHolder holder, int position) {
            holder.bind(mSelectedPeriodLogs.get(position));
            if(position %2 == 1) {
                holder.itemView.setBackgroundColor(Color.rgb(245, 245, 245));
            } else {
                holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255));
            }
        }

        @Override
        public int getItemCount() {
            return mSelectedPeriodLogs.size();
        }
    }

    /**RecyclerView Holder and Adapter for food summary entries*/
    private class FoodSummaryHolder extends RecyclerView.ViewHolder {
        private TextView mNameText;
        private TextView mCountText;
        private TextView mCaloriesText;
        private TextView mWeightText;
        private TextView mDateText;


        public FoodSummaryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_summary_food, parent, false));
            //itemView.setOnClickListener(this);  --Later maybe i'll implement onClick

            mNameText = (TextView) itemView.findViewById(R.id.list_item_summary_food_name);
            mCountText = (TextView) itemView.findViewById(R.id.list_item_summary_food_count);
            mCaloriesText = (TextView) itemView.findViewById(R.id.list_item_summary_food_calories);
            mWeightText = (TextView) itemView.findViewById(R.id.list_item_summary_food_weight);
            mDateText = (TextView) itemView.findViewById(R.id.list_item_summary_food_date);
        }

        public void bind(FoodSummary summaryFood) {
            mNameText.setText(summaryFood.getName());
            mCountText.setText("Count: " + summaryFood.getCount());
            mCaloriesText.setText(summaryFood.getCalories().intValue() + " kcal");
            if (mUnits.equals("Metric")) {
                mWeightText.setText(String.format("%.1f", summaryFood.getWeight() / 1000) + " kg");
            } else {
                mWeightText.setText(String.format("%.1f", summaryFood.getWeight() * 2.205f / 1000) + " lbs");
            }
            mDateText.setText("Last consumed: " + Calculations.dateToDateTextEqualLengthString(summaryFood.getLastConsumed()));
        }

    }

    private class FoodSummaryAdapter extends RecyclerView.Adapter<FoodSummaryHolder> {
        private List<FoodSummary> mSelectedPeriodFoods;

        public FoodSummaryAdapter(List<FoodSummary> foods) {
            mSelectedPeriodFoods = foods;
        }

        @Override
        public FoodSummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new FoodSummaryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FoodSummaryHolder holder, int position) {
            holder.bind(mSelectedPeriodFoods.get(position));
            if(position %2 == 1) {
                holder.itemView.setBackgroundColor(Color.rgb(245, 245, 245));
            } else {
                holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255));
            }
        }

        @Override
        public int getItemCount() {
            return mSelectedPeriodFoods.size();
        }
    }

    /**Method to call when selected time period changes. Checks which tab is selected, and then creates and sets appropriate new Adapter*/
    private void updatePeriod() {
        switch (mTabLayout.getSelectedTabPosition()) {
            case TAB_DAY_SUMMARY:
                mLogSummaryList = summarizeLogs(mStartDate, mEndDate);
                updateSorting(mSortSpinner.getSelectedItemPosition());
                mDaySummaryAdapter = new DaySummaryAdapter(mLogSummaryList);
                mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                break;
            case TAB_FOOD_SUMMARY:
                mFoodSummaryList = summarizeFoods(mStartDate, mEndDate);
                updateSorting(mSortSpinner.getSelectedItemPosition());
                mFoodSummaryAdapter = new FoodSummaryAdapter(mFoodSummaryList);
                mSummaryRecyclerView.setAdapter(mFoodSummaryAdapter);
                break;
        }
    }

    private void updateSorting(int position) {
        switch (mTabLayout.getSelectedTabPosition()) {
            case TAB_DAY_SUMMARY:
                switch(position) {
                    case SPINNER_SORT_DAY_DATE_OLD:
                        mLogSummaryList = Log.sortByDateOld(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_DATE_NEW:
                        mLogSummaryList = Log.sortByDateNew(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_KCAL_HIGH:
                        mLogSummaryList = Log.sortByKcalHigh(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_KCAL_LOW:
                        mLogSummaryList = Log.sortByKcalLow(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_PROTEIN_HIGH:
                        mLogSummaryList = Log.sortByProteinHigh(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_PROTEIN_LOW:
                        mLogSummaryList = Log.sortByProteinLow(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_CARBS_HIGH:
                        mLogSummaryList = Log.sortByCarbsHigh(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_CARBS_LOW:
                        mLogSummaryList = Log.sortByCarbsLow(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_FAT_HIGH:
                        mLogSummaryList = Log.sortByFatHigh(mLogSummaryList);
                        break;
                    case SPINNER_SORT_DAY_FAT_LOW:
                        mLogSummaryList = Log.sortByFatLow(mLogSummaryList);
                        break;
                }
                break;
            case TAB_FOOD_SUMMARY:
                switch(position) {
                    case SPINNER_SORT_FOOD_COUNT_HIGH:
                        mFoodSummaryList = FoodSummary.sortByCountHigh(mFoodSummaryList);
                        break;
                    case SPINNER_SORT_FOOD_COUNT_LOW:
                        mFoodSummaryList = FoodSummary.sortByCountLow(mFoodSummaryList);
                        break;
                    case SPINNER_SORT_FOOD_KCAL_HIGH:
                        mFoodSummaryList = FoodSummary.sortByKcalHigh(mFoodSummaryList);
                        break;
                    case SPINNER_SORT_FOOD_KCAL_LOW:
                        mFoodSummaryList = FoodSummary.sortByKcalLow(mFoodSummaryList);
                        break;
                    case SPINNER_SORT_FOOD_WEIGHT_HIGH:
                        mFoodSummaryList = FoodSummary.sortByWeightHigh(mFoodSummaryList);
                        break;
                    case SPINNER_SORT_FOOD_WEIGHT_LOW:
                        mFoodSummaryList = FoodSummary.sortByWeightLow(mFoodSummaryList);
                        break;
                    case SPINNER_SORT_FOOD_DATE_NEW:
                        mFoodSummaryList = FoodSummary.sortByDateNew(mFoodSummaryList);
                        break;
                    case SPINNER_SORT_FOOD_DATE_OLD:
                        mFoodSummaryList = FoodSummary.sortByDateOld(mFoodSummaryList);
                        break;
                }
                break;
        }
    }

    /**Method which outputs a List of Summary Logs for period between provided start and end date*/
    public List<Log> summarizeLogs(Date start, Date end) {
        List<Log> summaryLogs = new ArrayList<>();

        String dayDateText = Calculations.dateToDateText(start); //convert to dateText to allow easy comparison
        String endDateText = Calculations.dateToDateText(end); //convert to dateText to allow easy comparison

        while (!dayDateText.equals(endDateText)) {
            List<Log> dayLogs = mLogManager.getLogsDay(start); //fetches list of logs for one day
            Log newLog = Calculations.summarizeDayLogs(dayLogs, start); //call method to summarize one day list of logs into one Summary Log

            summaryLogs.add(newLog);
            start = Calculations.incrementDay(start, 1);
            dayDateText = Calculations.dateToDateText(start);
        }

        Float kcal = 0f;
        Float protein = 0f;
        Float carbs= 0f;
        Float fat = 0f;
        int zeroKcalDays = 0;

        if (mPreferences.getBoolean("pref_stats_ignore_zero_kcal_days", false)) {
            for (int i = 0; i < summaryLogs.size(); i++) {
                if (summaryLogs.get(i).getKcal() == 0) {
                    zeroKcalDays++;
                } else {
                    kcal += summaryLogs.get(i).getKcal();
                    protein += summaryLogs.get(i).getProtein();
                    carbs += summaryLogs.get(i).getCarbs();
                    fat += summaryLogs.get(i).getFat();
                }
            }

        } else {
            for (int i = 0; i < summaryLogs.size(); i++) {
                kcal += summaryLogs.get(i).getKcal();
                protein += summaryLogs.get(i).getProtein();
                carbs += summaryLogs.get(i).getCarbs();
                fat += summaryLogs.get(i).getFat();

            }
        }
        //Calculate averages for the list of Summary Logs
        kcal = (kcal/(summaryLogs.size() - zeroKcalDays));
        Integer avgKcal = kcal.intValue();
        protein = protein/(summaryLogs.size() - zeroKcalDays);
        Integer avgProtein = protein.intValue();
        carbs = carbs/(summaryLogs.size() - zeroKcalDays);
        Integer avgCarbs = carbs.intValue();
        fat = fat/(summaryLogs.size() - zeroKcalDays);
        Integer avgFat = fat.intValue();

        Integer kcalDelta = avgKcal - Integer.parseInt(mPreferences.getString("pref_calories", "2500"));

        String text1 = "Daily averages between ";
        String text2 = summaryLogs.get(0).getDateText() + " - " + summaryLogs.get(summaryLogs.size() - 1).getDateText() + "\n";
        String text3 = "";
        if (mPreferences.getBoolean("pref_stats_ignore_zero_kcal_days", false)) {
            text3 = "(Ignoring days with 0 kcal)\n";
        }
        String text4 = "Calories: " + avgKcal.toString() + " kcal (" + kcalDelta.toString() + " kcal compared to daily target)\n";
        String text5 = "Protein: " + avgProtein + "g, Carbs: " + avgCarbs + "g, Fat: " + avgFat + "g";

        //Spannable allows to color only certain part of Text/Textview
        Spannable spannable = new SpannableString(text1 + text2 + text3 + text4 + text5);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), text1.length(), (text1 + text2).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSummaryText.setText(spannable, TextView.BufferType.SPANNABLE);

        return summaryLogs;
    }

    /**Method which outputs a List of FoodSummaries for period between provided start and end date*/
    public List<FoodSummary> summarizeFoods(Date start, Date end) {
        List<FoodSummary> summaryFoods = new ArrayList<>();

        String text1 = "Food stats between ";
        String text2 = Calculations.dateToDateTextEqualLengthString(start) + " - " + Calculations.dateToDateTextEqualLengthString(Calculations.incrementDay(end, -1));

        //Spannable allows to color only certain part of Text/Textview
        Spannable spannable = new SpannableString(text1 + text2);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), text1.length(), (text1 + text2).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSummaryText.setText(spannable, TextView.BufferType.SPANNABLE);

        String dayDateText = Calculations.dateToDateText(start); //convert to dateText to allow easy comparison
        String endDateText = Calculations.dateToDateText(end); //convert to dateText to allow easy comparison

        while (!dayDateText.equals(endDateText)) {
            List<Log> dayLogs = mLogManager.getLogsDay(start); //fetches list of logs for one day

            /**Logic for the below for loop which cycles through list of one day logs - grab the food name of each log,
             * and cycle through List of FoodSummary. If we find a match for the food - increment count and add calories and weight.
             * If no match found, create new FoodSummary, populate fields and add to the list.*/
            for (int i = 0; i<dayLogs.size(); i++) {

                String tempName = dayLogs.get(i).getFood();
                boolean in = false;
                for (int x = 0; x<summaryFoods.size();x++) {
                    if (tempName.equals(summaryFoods.get(x).getName())) {
                        in = true;
                        summaryFoods.get(x).setCalories(summaryFoods.get(x).getCalories() + dayLogs.get(i).getKcal());
                        summaryFoods.get(x).setCount(summaryFoods.get(x).getCount() + 1);
                        summaryFoods.get(x).setWeight(summaryFoods.get(x).getWeight() + dayLogs.get(i).getSize());
                        summaryFoods.get(x).setLastConsumed(dayLogs.get(i).getDate());
                        break;
                    }
                }

                if (!in) {
                    FoodSummary newFood = new FoodSummary(tempName);
                    newFood.setCount(1);
                    newFood.setCalories(dayLogs.get(i).getKcal());
                    newFood.setWeight(dayLogs.get(i).getSize());
                    newFood.setLastConsumed(dayLogs.get(i).getDate());
                    summaryFoods.add(newFood);
                }

            }

            start = Calculations.incrementDay(start, 1);
            dayDateText = Calculations.dateToDateText(start);
        }

        //Sorting by count high to low as a default sort order
        Collections.sort(summaryFoods, new Comparator<FoodSummary>() {
            @Override
            public int compare(FoodSummary o1, FoodSummary o2) {
                if (o1.getCount() > o2.getCount()) {
                    return -1;
                } else if (o1.getCount() < o2.getCount()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return summaryFoods;
    }

}
