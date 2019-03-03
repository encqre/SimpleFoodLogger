package com.untrustworthypillars.simplefoodlogger;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class for the Fragment of the summary/statistics
 *
 */

public class SummaryFragment extends Fragment {

    public static final int TAB_DAY_SUMMARY = 0;
    public static final int TAB_FOOD_SUMMARY = 1;

    public static final int SPINNER_PERIOD_7_DAYS = 0;
    public static final int SPINNER_PERIOD_30_DAYS = 1;
    public static final int SPINNER_PERIOD_90_DAYS = 2;
    public static final int SPINNER_PERIOD_YEAR = 3;
    public static final int SPINNER_PERIOD_CUSTOM = 4;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);

        mTabLayout = (TabLayout) v.findViewById(R.id.summary_tabs);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**Set correct adapters and visible elements for specific tabs*/
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (mTabLayout.getSelectedTabPosition()) {
                    case TAB_DAY_SUMMARY:
                        mSummaryText.setVisibility(View.VISIBLE);
                        mDaySummaryAdapter = new DaySummaryAdapter(summarizeLogs(mStartDate, mEndDate));
                        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                        mSortSpinner.setAdapter(mDaySortSpinnerAdapter);
                        mSortSpinner.setSelection(0);
                        break;
                    case TAB_FOOD_SUMMARY:
                        mSummaryText.setVisibility(View.GONE);
                        mFoodSummaryAdapter = new FoodSummaryAdapter(summarizeFoods(mStartDate, mEndDate));
                        mSummaryRecyclerView.setAdapter(mFoodSummaryAdapter);
                        mSortSpinner.setAdapter(mFoodSortSpinnerAdapter);
                        mSortSpinner.setSelection(0);
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

        mPeriodSpinner = (Spinner) v.findViewById(R.id.summary_spinner_period); //Spinner that provides selections for time periods

        //Creating adapter for period spinner, using resources array as list of items and default android layout for single spinner item
        mPeriodSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_period_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        mPeriodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPeriodSpinner.setAdapter(mPeriodSpinnerAdapter);
        mPeriodSpinner.setSelection(0);

        mPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case SPINNER_PERIOD_7_DAYS:
                        mStartDate = Calculations.incrementDay(new Date(), -6);
                        mEndDate = Calculations.incrementDay(new Date(), 1);
                        updatePeriod();
                        break;
                    case SPINNER_PERIOD_30_DAYS:
                        mStartDate = Calculations.incrementDay(new Date(), -29);
                        mEndDate = Calculations.incrementDay(new Date(), 1);
                        updatePeriod();
                        break;
                    case SPINNER_PERIOD_90_DAYS:
                        mStartDate = Calculations.incrementDay(new Date(), -89);
                        mEndDate = Calculations.incrementDay(new Date(), 1);
                        updatePeriod();
                        break;
                    case SPINNER_PERIOD_YEAR:
                        mStartDate = Calculations.incrementDay(new Date(), -364);
                        mEndDate = Calculations.incrementDay(new Date(), 1);
                        updatePeriod();
                        break;
                    case SPINNER_PERIOD_CUSTOM:
                        //TODO implement custom length
                        mStartDate = Calculations.incrementDay(new Date(), -29);
                        mEndDate = Calculations.incrementDay(new Date(), 1);
                        updatePeriod();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO implement sorting
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

        mSortSpinner.setAdapter(mDaySortSpinnerAdapter); //By default set day summary adapter, because it's the first ttab
        mSortSpinner.setSelection(0);

        mSummaryText = (TextView) v.findViewById(R.id.summary_text_summary);

        mSummaryRecyclerView = (RecyclerView) v.findViewById(R.id.summary_recycler);
        mSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLogManager = LogManager.get(getContext());

        //By default first time period option is selected - 7 days
        mStartDate = Calculations.incrementDay(new Date(), -6);
        mEndDate = Calculations.incrementDay(new Date(), 1);

        mDaySummaryAdapter = new DaySummaryAdapter(summarizeLogs(mStartDate, mEndDate));
        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);

        return v;
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


        public FoodSummaryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_summary_food, parent, false));
            //itemView.setOnClickListener(this);  --Later maybe i'll implement onClick

            mNameText = (TextView) itemView.findViewById(R.id.list_item_summary_food_name);
            mCountText = (TextView) itemView.findViewById(R.id.list_item_summary_food_count);
            mCaloriesText = (TextView) itemView.findViewById(R.id.list_item_summary_food_calories);
            mWeightText = (TextView) itemView.findViewById(R.id.list_item_summary_food_weight);
        }

        public void bind(FoodSummary summaryFood) {
            mNameText.setText(summaryFood.getName());
            mCountText.setText("Count: " + summaryFood.getCount());
            mCaloriesText.setText(summaryFood.getCalories().intValue() + " kcal");
            mWeightText.setText(String.format("%.1f", summaryFood.getWeight()/1000) + " kg");
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
                mDaySummaryAdapter = new DaySummaryAdapter(summarizeLogs(mStartDate, mEndDate));
                mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                break;
            case TAB_FOOD_SUMMARY:
                mFoodSummaryAdapter = new FoodSummaryAdapter(summarizeFoods(mStartDate, mEndDate));
                mSummaryRecyclerView.setAdapter(mFoodSummaryAdapter);
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

        for (int i = 0; i<summaryLogs.size(); i++) {
            kcal += summaryLogs.get(i).getKcal();
            protein += summaryLogs.get(i).getProtein();
            carbs += summaryLogs.get(i).getCarbs();
            fat += summaryLogs.get(i).getFat();

        }
        //Calculate averages for the list of Summary Logs
        kcal = (kcal/summaryLogs.size());
        Integer avgKcal = kcal.intValue();
        protein = protein/summaryLogs.size();
        Integer avgProtein = protein.intValue();
        carbs = carbs/summaryLogs.size();
        Integer avgCarbs = carbs.intValue();
        fat = fat/summaryLogs.size();
        Integer avgFat = fat.intValue();

        Integer kcalDelta = avgKcal - Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("pref_calories", "2500"));

        String text1 = "Daily averages between ";
        String text2 = summaryLogs.get(0).getDateText() + " - " + summaryLogs.get(summaryLogs.size() - 1).getDateText() + "\n";
        String text3 = "Calories: " + avgKcal.toString() + " kcal (" + kcalDelta.toString() + " kcal compared to daily target)\n";
        String text4 = "Protein: " + avgProtein + "g, Carbs: " + avgCarbs + "g, Fat: " + avgFat + "g";

        //Spannable allows to color only certain part of Text/Textview
        Spannable spannable = new SpannableString(text1 + text2 + text3 + text4);
        spannable.setSpan(new ForegroundColorSpan(Color.BLUE), text1.length(), (text1 + text2).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSummaryText.setText(spannable, TextView.BufferType.SPANNABLE);

        return summaryLogs;
    }

    /**Method which outputs a List of FoodSummaries for period between provided start and end date*/
    public List<FoodSummary> summarizeFoods(Date start, Date end) {
        List<FoodSummary> summaryFoods = new ArrayList<>();

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

        return summaryFoods;
    }

}
