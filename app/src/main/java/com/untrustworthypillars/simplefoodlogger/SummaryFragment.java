package com.untrustworthypillars.simplefoodlogger;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class for the Fragment of the summary/statistics
 *
 */

public class SummaryFragment extends Fragment {

    public static final int TAB_DAY_SUMMARY = 0;
    public static final int TAB_FOOD_SUMMARY = 1;

    private LogManager mLogManager;
    private TabLayout mTabLayout;
    private RecyclerView mSummaryRecyclerView;
    private DaySummaryAdapter mDaySummaryAdapter;
    private Spinner mPeriodSpinner;
    private Spinner mSortSpinner;
    private ArrayAdapter<CharSequence> mPeriodSpinnerAdapter;
    private ArrayAdapter<CharSequence> mDaySortSpinnerAdapter;
    private ArrayAdapter<CharSequence> mFoodSortSpinnerAdapter;
    private List<Log> mLogList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);

        mTabLayout = (TabLayout) v.findViewById(R.id.summary_tabs);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (mTabLayout.getSelectedTabPosition()) {
                    case TAB_DAY_SUMMARY:
                        mLogList = summarizeLogs(Calculations.incrementDay(new Date(), -29), Calculations.incrementDay(new Date(),1));
                        mDaySummaryAdapter = new DaySummaryAdapter(mLogList);
                        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                        mSortSpinner.setAdapter(mDaySortSpinnerAdapter);
                        mSortSpinner.setSelection(0);
                        break;
                    case TAB_FOOD_SUMMARY:
                        //TODO implement food stats
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

        mPeriodSpinner = (Spinner) v.findViewById(R.id.summary_spinner_period);

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
                    case 0:
                        mLogList = summarizeLogs(Calculations.incrementDay(new Date(), -29), Calculations.incrementDay(new Date(),1));
                        mDaySummaryAdapter = new DaySummaryAdapter(mLogList);
                        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                        break;
                    case 1:
                        mLogList = summarizeLogs(Calculations.incrementDay(new Date(), -89), Calculations.incrementDay(new Date(),1));
                        mDaySummaryAdapter = new DaySummaryAdapter(mLogList);
                        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                        break;
                    case 2:
                        mLogList = summarizeLogs(Calculations.incrementDay(new Date(), -364), Calculations.incrementDay(new Date(),1));
                        mDaySummaryAdapter = new DaySummaryAdapter(mLogList);
                        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                        break;
                    case 3:
                        //TODO implement custom length
                        mLogList = summarizeLogs(Calculations.incrementDay(new Date(), -29), Calculations.incrementDay(new Date(),1));
                        mDaySummaryAdapter = new DaySummaryAdapter(mLogList);
                        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSortSpinner = (Spinner) v.findViewById(R.id.summary_spinner_sort);

        //Creating adapters for sort spinner, using resources array as list of items and default android layout for single spinner item
        mDaySortSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_day_sort_options, android.R.layout.simple_spinner_item);
        mFoodSortSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.spinner_food_sort_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        mDaySortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFoodSortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSortSpinner.setAdapter(mDaySortSpinnerAdapter);
        mSortSpinner.setSelection(0);

        mSummaryRecyclerView = (RecyclerView) v.findViewById(R.id.summary_recycler);
        mSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLogManager = LogManager.get(getContext());

        mLogList = summarizeLogs(Calculations.incrementDay(new Date(), -29), Calculations.incrementDay(new Date(),1));
        mDaySummaryAdapter = new DaySummaryAdapter(mLogList);
        mSummaryRecyclerView.setAdapter(mDaySummaryAdapter);

        return v;
    }

    private class DaySummaryHolder extends RecyclerView.ViewHolder {
        private TextView mDateText;
        private TextView mCaloriesText;
        private TextView mMacrosText;


        public DaySummaryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_summary_log, parent, false));
            //itemView.setOnClickListener(this);  --Later when i'll implement onClick

            mDateText = (TextView) itemView.findViewById(R.id.list_item_summary_log_date);
            mCaloriesText = (TextView) itemView.findViewById(R.id.list_item_summary_log_calories);
            mMacrosText = (TextView) itemView.findViewById(R.id.list_item_summary_log_macros);
        }

        //Will need to update this when sorting is sorted
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

    public List<Log> summarizeLogs(Date start, Date end) {
        List<Log> summaryLogs = new ArrayList<>();

        String dayDateText, endDateText;
        dayDateText = Calculations.dateToDateText(start);
        endDateText = Calculations.dateToDateText(end);

        while (!dayDateText.equals(endDateText)) {
            List<Log> dayLogs = mLogManager.getLogsDay(start);
            Log newLog = Calculations.summarizeDayLogs(dayLogs, start);

            summaryLogs.add(newLog);
            start = Calculations.incrementDay(start, 1);
            dayDateText = Calculations.dateToDateText(start);
        }

        return summaryLogs;
    }

}
