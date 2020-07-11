package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.untrustworthypillars.simplefoodlogger.reusable.TutorialDialog;

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
import android.widget.Toast;

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
    private static final String DIALOG_TUTORIAL = "DialogTutorial";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_ADD_LOG = 1;
    private static final int REQUEST_EDIT_LOG = 2;
    private static final int REQUEST_TUTORIAL = 3;

    private LogManager mLogManager;
    private RecyclerView mLogRecyclerView;
    private LogAdapter mLogAdapter;
    private FloatingActionButton mLogMealFAB;
    private Button mDateButton;
    private Button mPreviousDay;
    private Button mNextDay;
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
    private String mUnits;

    private LoggerActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_page, container, false);

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
        mProteinGoal = String.valueOf(Math.round(Integer.parseInt(mCaloriesGoal) * Integer.parseInt(mPreferences.getString("pref_protein", "185")) / 400f));
        mCarbsGoal = String.valueOf(Math.round(Integer.parseInt(mCaloriesGoal) * Integer.parseInt(mPreferences.getString("pref_carbs", "300")) / 400f));
        mFatGoal = String.valueOf(Math.round(Integer.parseInt(mCaloriesGoal) * Integer.parseInt(mPreferences.getString("pref_fat", "75")) / 900f));
        mUnits = mPreferences.getString("pref_units", "Metric");

        /*Floating action button starts a new Activity, which will launch FoodListFragment. Passing selected day as an argument */
        mLogMealFAB = (FloatingActionButton) v.findViewById(R.id.floating_button_logmeal);
        mLogMealFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = PickFoodActivity.newIntent(getActivity(), mSelectedDay);
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

        mPreviousDay = (Button) v.findViewById(R.id.image_button_previousday);
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

        mNextDay = (Button) v.findViewById(R.id.image_button_nextday);
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
        mCaloriesProgress.setMax(Integer.parseInt(mCaloriesGoal));
        mProteinProgress = (ProgressBar) v.findViewById(R.id.progress_bar_protein);
        mProteinProgress.setMax(Integer.parseInt(mProteinGoal));
        mCarbsProgress = (ProgressBar) v.findViewById(R.id.progress_bar_carbs);
        mCarbsProgress.setMax(Integer.parseInt(mCarbsGoal));
        mFatProgress = (ProgressBar) v.findViewById(R.id.progress_bar_fat);
        mFatProgress.setMax(Integer.parseInt(mFatGoal));

        updateUI();

        if (!mPreferences.getBoolean("tutorial_home_page_done", false)) {
            FragmentManager fm = getFragmentManager();
            TutorialDialog dialog = TutorialDialog.newInstance(getString(R.string.tutorial_home_page_text));
            dialog.setTargetFragment(HomePageFragment.this, REQUEST_TUTORIAL);
            dialog.show(fm, DIALOG_TUTORIAL);
        }

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
        }
        if (requestCode == REQUEST_EDIT_LOG) {
            updateUI();
        }
        if (requestCode == REQUEST_TUTORIAL) {
            mPreferences.edit().putBoolean("tutorial_home_page_done", true).apply();
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

    public void updateTargets(){
        mCaloriesGoal = mPreferences.getString("pref_calories", "2500");
        mProteinGoal = String.valueOf(Math.round(Integer.parseInt(mCaloriesGoal) * Integer.parseInt(mPreferences.getString("pref_protein", "185")) / 400f));
        mCarbsGoal = String.valueOf(Math.round(Integer.parseInt(mCaloriesGoal) * Integer.parseInt(mPreferences.getString("pref_carbs", "300")) / 400f));
        mFatGoal = String.valueOf(Math.round(Integer.parseInt(mCaloriesGoal) * Integer.parseInt(mPreferences.getString("pref_fat", "75")) / 900f));
        mUnits = mPreferences.getString("pref_units", "Metric");

        mCaloriesProgress.setMax(Integer.parseInt(mCaloriesGoal));
        mProteinProgress.setMax(Integer.parseInt(mProteinGoal));
        mCarbsProgress.setMax(Integer.parseInt(mCarbsGoal));
        mFatProgress.setMax(Integer.parseInt(mFatGoal));

        updateUI();
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
            mFoodCalories.setText(getString(R.string.home_fragment_log_kcal, log.getKcal().intValue()));
            mFoodProtein.setText(getString(R.string.food_list_fragment_protein, String.format("%.1f", log.getProtein())));
            mFoodCarbs.setText(getString(R.string.food_list_fragment_carbs, String.format("%.1f", log.getCarbs())));
            mFoodFat.setText(getString(R.string.food_list_fragment_fat, String.format("%.1f", log.getFat())));
            if (mUnits.equals("Metric")) {
                mFoodTitleTextView.setText(getString(R.string.home_fragment_log_title, log.getFood(), String.valueOf(log.getSize().intValue()), "g"));
            } else {
                mFoodTitleTextView.setText(getString(R.string.home_fragment_log_title, log.getFood(), String.format("%.1f", log.getSizeImperial()), " oz"));
            }
        }

        public boolean onLongClick(View v) {
            Intent intent = EditLogActivity.newIntent(getActivity(), mLogId);
            startActivityForResult(intent, REQUEST_EDIT_LOG);

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
//            if(position %2 == 1) {
//                holder.itemView.setBackgroundColor(Color.rgb(245, 245, 245));
//            } else {
//                holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255));
//            }
        }

        @Override
        public int getItemCount() {
            return mSelectedDayLogs.size();
        }
    }

}

//TODO make this whole fragment layout a scrollpane - need to test on a low res phone first if its really needed
//TODO fix issues arising switching to landscape mode
//TODO verify if landscape layouts are ok
//TODO possibly replace FAB with a different button? Maybe like first entry in log list with plus sign and "add a new log" or etc.
//TODO fill tutorial texts once layout/design refresh is done
//TODO Adding timestamps to food logs maybe?

