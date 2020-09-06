package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

//TODO some graphs maybe
//TODO searchview in toolbar in the 'food stats' section?

public class SummaryFragment extends Fragment {

    public static final int TAB_DAY_SUMMARY = 0;
    public static final int TAB_FOOD_SUMMARY = 1;

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
    private TextView mSummaryText;
    private Date mStartDate;
    private Date mEndDate;
    private List<FoodSummary> mFoodSummaryList;
    private List<Log> mLogSummaryList;
    private Toolbar toolbar;

    private SharedPreferences mPreferences;
    private String mUnits;

    private int selectedTimeRangeId;
    private int selectedSortSummaryId;
    private int selectedSortSummaryParentId;
    private int selectedSortFoodId;
    private int selectedSortFoodParentId;

    @ColorInt int accentColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.fragment_summary_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Stats (Past 7 days)");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        selectedTimeRangeId = R.id.toolbar_stats_range_7days;
        selectedSortSummaryId = R.id.toolbar_stats_sort_date_old;
        selectedSortSummaryParentId = R.id.toolbar_stats_sort_date;
        selectedSortFoodId = R.id.toolbar_stats_sort_food_count_high;
        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_count;

        mStartDate = Calculations.incrementDay(new Date(), -7);
        mEndDate = new Date();

        mTabLayout = (TabLayout) v.findViewById(R.id.summary_tabs);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**Set correct adapters and visible elements for specific tabs*/
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updatePeriod();
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

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        accentColor = typedValue.data;

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

        updatePeriod();

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
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_stats, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem filterItemSummary = menu.findItem(R.id.toolbar_stats_sort);
        MenuItem filterItemFood = menu.findItem(R.id.toolbar_stats_sort_food);

        filterItemSummary.setVisible(mTabLayout.getSelectedTabPosition() == TAB_DAY_SUMMARY);
        filterItemFood.setVisible(mTabLayout.getSelectedTabPosition() == TAB_FOOD_SUMMARY);

        menu.findItem(selectedTimeRangeId).setChecked(true);
        menu.findItem(selectedSortSummaryId).setChecked(true);
        menu.findItem(selectedSortSummaryParentId).setChecked(true);
        menu.findItem(selectedSortFoodId).setChecked(true);
        menu.findItem(selectedSortFoodParentId).setChecked(true);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            //Time range options
            case R.id.toolbar_stats_range_7days:
                mStartDate = Calculations.incrementDay(new Date(), -7);
                mEndDate = new Date();
                toolbar.setTitle("Stats ("+item.getTitle()+")");
                selectedTimeRangeId = item.getItemId();
                item.setChecked(true);
                updatePeriod();
                break;
            case R.id.toolbar_stats_range_30days:
                mStartDate = Calculations.incrementDay(new Date(), -30);
                mEndDate = new Date();
                toolbar.setTitle("Stats ("+item.getTitle()+")");
                selectedTimeRangeId = item.getItemId();
                item.setChecked(true);
                updatePeriod();
                break;
            case R.id.toolbar_stats_range_90days:
                mStartDate = Calculations.incrementDay(new Date(), -90);
                mEndDate = new Date();
                toolbar.setTitle("Stats ("+item.getTitle()+")");
                selectedTimeRangeId = item.getItemId();
                item.setChecked(true);
                updatePeriod();
                break;
            case R.id.toolbar_stats_range_year:
                mStartDate = Calculations.incrementDay(new Date(), -365);
                mEndDate = new Date();
                toolbar.setTitle("Stats ("+item.getTitle()+")");
                selectedTimeRangeId = item.getItemId();
                item.setChecked(true);
                updatePeriod();
                break;
            case R.id.toolbar_stats_range_custom:
                mStartDate = Calculations.incrementDay(new Date(), -7); //setting start and end for 1 week period just in case
                mEndDate = new Date(); //setting start and end for 1 week period just in case
                FragmentManager fm = getFragmentManager();
                DatePickerDialog dialog = DatePickerDialog.newInstance(new Date(), "Select start date");
                dialog.setTargetFragment(SummaryFragment.this, REQUEST_START_DATE);
                dialog.show(fm, DIALOG_DATE);
                item.setChecked(true);
                break;
            //Handle all sorting options under updateSorting method
            case R.id.toolbar_stats_sort_date_old:
            case R.id.toolbar_stats_sort_date_new:
            case R.id.toolbar_stats_sort_calories_high:
            case R.id.toolbar_stats_sort_calories_low:
            case R.id.toolbar_stats_sort_protein_high:
            case R.id.toolbar_stats_sort_protein_low:
            case R.id.toolbar_stats_sort_carbs_high:
            case R.id.toolbar_stats_sort_carbs_low:
            case R.id.toolbar_stats_sort_fat_high:
            case R.id.toolbar_stats_sort_fat_low:
            case R.id.toolbar_stats_sort_food_count_high:
            case R.id.toolbar_stats_sort_food_count_low:
            case R.id.toolbar_stats_sort_food_calories_high:
            case R.id.toolbar_stats_sort_food_calories_low:
            case R.id.toolbar_stats_sort_food_weight_high:
            case R.id.toolbar_stats_sort_food_weight_low:
            case R.id.toolbar_stats_sort_food_date_new:
            case R.id.toolbar_stats_sort_food_date_old:
                updateSorting(item.getItemId());
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            /**If setting the custom date fails, default to 1 week and call updatePeriod()*/
                mStartDate = Calculations.incrementDay(new Date(), -7);
                mEndDate = new Date();
                selectedTimeRangeId = R.id.toolbar_stats_range_7days;
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
            toolbar.setTitle("Stats (Custom period)");
            selectedTimeRangeId = R.id.toolbar_stats_range_custom;
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
        private TextView mProteinText;
        private TextView mCarbsText;
        private TextView mFatText;

        public DaySummaryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_summary_log, parent, false));
            //itemView.setOnClickListener(this);  --Later maybe i'll implement onClick

            mDateText = (TextView) itemView.findViewById(R.id.list_item_summary_log_date);
            mCaloriesText = (TextView) itemView.findViewById(R.id.list_item_summary_log_calories);
            mProteinText = (TextView) itemView.findViewById(R.id.list_item_summary_log_protein);
            mCarbsText = (TextView) itemView.findViewById(R.id.list_item_summary_log_carbs);
            mFatText = (TextView) itemView.findViewById(R.id.list_item_summary_log_fat);
        }

        public void bind(Log summaryLog) {
            mDateText.setText(summaryLog.getDateText());
            mCaloriesText.setText(summaryLog.getKcal().intValue() + " kcal");
            mProteinText.setText(summaryLog.getProtein().intValue() + "g");
            mCarbsText.setText(summaryLog.getCarbs().intValue() + "g");
            mFatText.setText(summaryLog.getFat().intValue() + "g");
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
//            if(position %2 == 1) {
//                holder.itemView.setBackgroundColor(Color.rgb(245, 245, 245));
//            } else {
//                holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255));
//            }
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
//            if(position %2 == 1) {
//                holder.itemView.setBackgroundColor(Color.rgb(245, 245, 245));
//            } else {
//                holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255));
//            }
        }

        @Override
        public int getItemCount() {
            return mSelectedPeriodFoods.size();
        }
    }

    /**Method to call when selected time period changes.*/
    private void updatePeriod() {
        switch (mTabLayout.getSelectedTabPosition()) {
            case TAB_DAY_SUMMARY:
                mLogSummaryList = summarizeLogs(mStartDate, mEndDate);
                updateSorting(selectedSortSummaryId);
                break;
            case TAB_FOOD_SUMMARY:
                mFoodSummaryList = summarizeFoods(mStartDate, mEndDate);
                updateSorting(selectedSortFoodId);
                break;
        }
    }

    /**Update sorting of currently set summary log/food list based on which option menu sorting item id is provided*/
    private void updateSorting(int optionMenuItemId) {
        switch (mTabLayout.getSelectedTabPosition()){
            case TAB_DAY_SUMMARY:
                switch (optionMenuItemId) {
                    case R.id.toolbar_stats_sort_date_old:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_date;
                        mLogSummaryList = Log.sortByDateOld(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_date_new:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_date;
                        mLogSummaryList = Log.sortByDateNew(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_calories_high:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_calories;
                        mLogSummaryList = Log.sortByKcalHigh(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_calories_low:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_calories;
                        mLogSummaryList = Log.sortByKcalLow(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_protein_high:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_protein;
                        mLogSummaryList = Log.sortByProteinHigh(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_protein_low:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_protein;
                        mLogSummaryList = Log.sortByProteinLow(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_carbs_high:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_carbs;
                        mLogSummaryList = Log.sortByCarbsHigh(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_carbs_low:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_carbs;
                        mLogSummaryList = Log.sortByCarbsLow(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_fat_high:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_fat;
                        mLogSummaryList = Log.sortByFatHigh(mLogSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_fat_low:
                        selectedSortSummaryParentId = R.id.toolbar_stats_sort_fat;
                        mLogSummaryList = Log.sortByFatLow(mLogSummaryList);
                        break;
                }
                selectedSortSummaryId = optionMenuItemId;
                mDaySummaryAdapter = new DaySummaryAdapter(mLogSummaryList);
                mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                break;
            case TAB_FOOD_SUMMARY:
                switch (optionMenuItemId) {
                    case R.id.toolbar_stats_sort_food_count_high:
                        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_count;
                        mFoodSummaryList = FoodSummary.sortByCountHigh(mFoodSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_food_count_low:
                        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_count;
                        mFoodSummaryList = FoodSummary.sortByCountLow(mFoodSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_food_calories_high:
                        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_calories;
                        mFoodSummaryList = FoodSummary.sortByKcalHigh(mFoodSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_food_calories_low:
                        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_calories;
                        mFoodSummaryList = FoodSummary.sortByKcalLow(mFoodSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_food_weight_high:
                        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_weight;
                        mFoodSummaryList = FoodSummary.sortByWeightHigh(mFoodSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_food_weight_low:
                        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_weight;
                        mFoodSummaryList = FoodSummary.sortByWeightLow(mFoodSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_food_date_new:
                        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_date;
                        mFoodSummaryList = FoodSummary.sortByDateNew(mFoodSummaryList);
                        break;
                    case R.id.toolbar_stats_sort_food_date_old:
                        selectedSortFoodParentId = R.id.toolbar_stats_sort_food_date;
                        mFoodSummaryList = FoodSummary.sortByDateOld(mFoodSummaryList);
                        break;
                }
                selectedSortFoodId = optionMenuItemId;
                mFoodSummaryAdapter = new FoodSummaryAdapter(mFoodSummaryList);
                mSummaryRecyclerView.setAdapter(mFoodSummaryAdapter);
                break;
        }
        getActivity().invalidateOptionsMenu();
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


        String text1 = "Selected period: ";
        String text2 = summaryLogs.get(0).getDateText() + " - " + summaryLogs.get(summaryLogs.size() - 1).getDateText() + "\n";
        String text3 = "Averages (" + (mPreferences.getBoolean("pref_stats_ignore_zero_kcal_days", false) ? "ex" : "in") + "cluding days with 0 kcal):\n";
        String text4 = "Calories: " + avgKcal.toString() + " kcal (";
        String kcalDeltaString = ((kcalDelta > 0) ? "+":"") + kcalDelta.toString();
        String text5 = " compared to daily target)\n";
        String text6 = "Protein: " + avgProtein + "g, Carbs: " + avgCarbs + "g, Fat: " + avgFat + "g";

        //Spannable allows to color only certain part of Text/Textview
        Spannable spannable = new SpannableString(text1 + text2 + text3 + text4 + kcalDeltaString + text5 + text6);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, (text1+text2).length(), 0);
        spannable.setSpan(new ForegroundColorSpan(accentColor), text1.length(), (text1 + text2).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(kcalDelta > 0 ? R.color.red : R.color.green)), (text1+text2+text3+text4).length(), (text1+text2+text3+text4+kcalDeltaString).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSummaryText.setText(spannable, TextView.BufferType.SPANNABLE);

        return summaryLogs;
    }

    /**Method which outputs a List of FoodSummaries for period between provided start and end date*/
    public List<FoodSummary> summarizeFoods(Date start, Date end) {
        List<FoodSummary> summaryFoods = new ArrayList<>();

        String text1 = "Selected period: ";
        String text2 = Calculations.dateToDateTextEqualLengthString(start) + " - " + Calculations.dateToDateTextEqualLengthString(Calculations.incrementDay(end, -1));

        //Spannable allows to color only certain part of Text/Textview
        Spannable spannable = new SpannableString(text1 + text2);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, (text1+text2).length(), 0);
        spannable.setSpan(new ForegroundColorSpan(accentColor), text1.length(), (text1 + text2).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
