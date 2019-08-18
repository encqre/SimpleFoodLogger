package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class for the Fragment of the home page
 *
 */

public class HomePageFragment extends Fragment {

    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_ADD_LOG = 1;
    private static final int REQUEST_EDIT_LOG = 2;

    private LogManager mLogManager;
    private RecyclerView mLogRecyclerView;
    private LogAdapter mLogAdapter;
    private FloatingActionButton mLogMealFAB;
    private Button mDateButton;
    private ImageButton mPreviousDay;
    private ImageButton mNextDay;
    private TextView mCaloriesText;
    private TextView mProteinText;
    private TextView mCarbsText;
    private TextView mFatText;
    private ProgressBar mCaloriesProgress;
    private ProgressBar mProteinProgress;
    private ProgressBar mCarbsProgress;
    private ProgressBar mFatProgress;



    private SharedPreferences mPreferences;
    private Date mSelectedDay;
    private String mCaloriesGoal;
    private String mProteinGoal;
    private String mCarbsGoal;
    private String mFatGoal;

    private LoggerActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);

//        mSelectedDay = new Date();
        mActivity = (LoggerActivity) getActivity();

        try {
            mSelectedDay = mActivity.getSelectedDay();
        } catch (Exception e){
            mSelectedDay = new Date();
        }

        mLogManager = LogManager.get(getContext());

        mLogRecyclerView = (RecyclerView) v.findViewById(R.id.log_recycler_view);
        mLogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mCaloriesGoal = mPreferences.getString("pref_calories", "2500");
        mProteinGoal = mPreferences.getString("pref_protein", "200");
        mCarbsGoal = mPreferences.getString("pref_carbs", "300");
        mFatGoal = mPreferences.getString("pref_fat", "90");

        /*Floating action button starts a new Activity, which will launch FoodListFragment. Passing selected day as an argument */
        mLogMealFAB = (FloatingActionButton) v.findViewById(R.id.floating_button_logmeal);
        mLogMealFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddLogActivity.newIntent(getActivity(), mSelectedDay);
                startActivityForResult(intent, REQUEST_ADD_LOG);

            }
        });

        mDateButton = (Button) v.findViewById(R.id.date_picker_button);
        mDateButton.setText(Calculations.dateDisplayString(mSelectedDay));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerDialog dialog = DatePickerDialog.newInstance(mSelectedDay);
                dialog.setTargetFragment(HomePageFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mPreviousDay = (ImageButton) v.findViewById(R.id.image_button_previousday);
        mPreviousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(mSelectedDay);
                cal.add(Calendar.DATE, -1);
                mSelectedDay = cal.getTime();
                mActivity.setSelectedDay(mSelectedDay);
                mDateButton.setText(Calculations.dateDisplayString(mSelectedDay));
                updateUI();
            }
        });

        mNextDay = (ImageButton) v.findViewById(R.id.image_button_nextday);
        mNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(mSelectedDay);
                cal.add(Calendar.DATE, +1);
                mSelectedDay = cal.getTime();
                mActivity.setSelectedDay(mSelectedDay);
                mDateButton.setText(Calculations.dateDisplayString(mSelectedDay));
                updateUI();
            }
        });

        mCaloriesText = (TextView) v.findViewById(R.id.textview_calories);
        mProteinText = (TextView) v.findViewById(R.id.textview_protein);
        mCarbsText = (TextView) v.findViewById(R.id.textview_carbs);
        mFatText = (TextView) v.findViewById(R.id.textview_fat);
        mCaloriesProgress = (ProgressBar) v.findViewById(R.id.progress_bar_calories);
        mCaloriesProgress.setMax((int) Float.parseFloat(mCaloriesGoal));
        mProteinProgress = (ProgressBar) v.findViewById(R.id.progress_bar_protein);
        mProteinProgress.setMax((int) Float.parseFloat(mProteinGoal));
        mCarbsProgress = (ProgressBar) v.findViewById(R.id.progress_bar_carbs);
        mCarbsProgress.setMax((int) Float.parseFloat(mCarbsGoal));
        mFatProgress = (ProgressBar) v.findViewById(R.id.progress_bar_fat);
        mFatProgress.setMax((int) Float.parseFloat(mFatGoal));

        updateUI();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            mSelectedDay = (Date) data.getSerializableExtra(DatePickerDialog.EXTRA_DATE);
            mActivity.setSelectedDay(mSelectedDay);
            mDateButton.setText(Calculations.dateDisplayString(mSelectedDay));
            updateUI();
        }
        if (requestCode == REQUEST_ADD_LOG) {
            updateUI();
            //Toast.makeText(getActivity(), "Returned from another activity", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_EDIT_LOG) {
            updateUI();
        }
    }

    /**Method used to update UI elements of home page after something changes */
    private void updateUI() {
        List<Log> logs = mLogManager.getLogsDay(mSelectedDay);
        mLogAdapter = new LogAdapter(logs);
        mLogRecyclerView.setAdapter(mLogAdapter);

        Float[] result = Calculations.calculateKcalAndMacros(logs);
        mCaloriesText.setText(getString(R.string.home_fragment_goal_kcal, result[0].intValue(), mCaloriesGoal));
        mCaloriesProgress.setProgress(result[0].intValue());
        mProteinText.setText(getString(R.string.home_fragment_goal_protein, result[1].intValue(), mProteinGoal));
        mProteinProgress.setProgress(result[1].intValue());
        mCarbsText.setText(getString(R.string.home_fragment_goal_carbs, result[2].intValue(), mCarbsGoal));
        mCarbsProgress.setProgress(result[2].intValue());
        mFatText.setText(getString(R.string.home_fragment_goal_fat, result[3].intValue(), mFatGoal));
        mFatProgress.setProgress(result[3].intValue());
    }

    private class LogHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private TextView mFoodTitleTextView;
        private TextView mFoodCalories;
        private TextView mFoodProtein;
        private TextView mFoodCarbs;
        private TextView mFoodFat;
        private UUID mLogId;

        public LogHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_log, parent, false));
            itemView.setOnLongClickListener(this);

            mFoodTitleTextView = (TextView) itemView.findViewById(R.id.list_item_log_name);
            mFoodCalories = (TextView) itemView.findViewById(R.id.list_item_log_calories);
            mFoodProtein = (TextView) itemView.findViewById(R.id.list_item_log_protein);
            mFoodCarbs = (TextView) itemView.findViewById(R.id.list_item_log_carbs);
            mFoodFat = (TextView) itemView.findViewById(R.id.list_item_log_fat);
        }

        public void bind(Log log) {
            mLogId = log.getLogId();
            mFoodTitleTextView.setText(getString(R.string.home_fragment_log_title, log.getFood(), log.getSize().intValue()));
            mFoodCalories.setText(getString(R.string.home_fragment_log_kcal, log.getKcal().intValue()));
            mFoodProtein.setText(getString(R.string.food_list_fragment_protein, String.format("%.1f", log.getProtein())));
            mFoodCarbs.setText(getString(R.string.food_list_fragment_carbs, String.format("%.1f", log.getCarbs())));
            mFoodFat.setText(getString(R.string.food_list_fragment_fat, String.format("%.1f", log.getFat())));
        }

        public boolean onLongClick(View v) {
            FragmentManager fm = getFragmentManager();
            EditLogDialog dialog = EditLogDialog.newInstance(mLogId);
            dialog.setTargetFragment(HomePageFragment.this, REQUEST_EDIT_LOG);
            dialog.show(fm, "OnLongClick");
            return true;
        }
    }

    private class LogAdapter extends RecyclerView.Adapter<LogHolder> {

        private List<Log> mSelectedDayLogs;

        public LogAdapter(List<Log> logs) {
            mSelectedDayLogs = logs;
        }

        @Override
        public LogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new LogHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(LogHolder holder, int position) {
        holder.bind(mSelectedDayLogs.get(position));
        }

        @Override
        public int getItemCount() {
            return mSelectedDayLogs.size();
        }
    }

}

//TODO make this whole fragment layout a scrollpane
//TODO also would be probably a good idea to make this a PagerView
//TODO fix issues arising switching to landscape mode
//TODO verify if landscape layouts are ok

