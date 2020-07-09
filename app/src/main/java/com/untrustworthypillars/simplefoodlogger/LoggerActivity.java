package com.untrustworthypillars.simplefoodlogger;

import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Date;
import java.util.logging.Logger;

public class LoggerActivity extends AppCompatActivity {

    private static final String TAG = "LoggerActivity";
    public static final int TAB_HOME = 0;
    public static final int TAB_FOODS = 1;
    public static final int TAB_SUMMARY = 2;
    public static final int TAB_SETTINGS = 3;

    private static final int REQUEST_INITIAL_SETUP= 0;

    private TabLayout mTabLayout;
    private SharedPreferences mPreferences;
    private Date mSelectedDay;

    /** Method to change the selected Tab programmatically*/
    public void setTab(int i) {
        TabLayout.Tab tab = mTabLayout.getTabAt(i);
        tab.select();
    }

    public Date getSelectedDay() {
        return mSelectedDay;
    }

    public void setSelectedDay(Date selectedDay) {
        mSelectedDay = selectedDay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String theme = mPreferences.getString("pref_theme", "Light theme");
        if (theme.equals("Light theme")) {
            setTheme(R.style.AppTheme);
        } else if (theme.equals("Dark theme")) {
            setTheme(R.style.AppThemeDark);
        }
        setContentView(R.layout.activity_logger);


        if (mPreferences.getBoolean("initial_database_setup_needed", true) || mPreferences.getBoolean("initial_profile_setup_needed", true)) {
            Intent intent = InitialSetupActivity.newIntent(LoggerActivity.this);
            startActivityForResult(intent, REQUEST_INITIAL_SETUP);
        }

        mSelectedDay = new Date();

        /** Replacing blank fragment container with home fragment at start **/
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomePageFragment()).commit();

        /** Assigning object only to TabLayout and not separate tabs, because tabItems are just dummies for the layout apparently**/
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        /**
         * On Click listener for the tabs. Tab positions are numbered from 0. When selected Tab changes, we check what is the selected tab position
         * ( with TabLayout.getSelectedTabPosition), and then accordingly replace the layout for the fragment_container which respective fragment layout.
         */
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (mTabLayout.getSelectedTabPosition()) {
                    case TAB_HOME:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomePageFragment()).commit();
                        break;
                    case TAB_FOODS:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FoodListFragment()).commit();
                        break;
                    case TAB_SUMMARY:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SummaryFragment()).commit();
                        break;
                    case TAB_SETTINGS:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //not implemented yet, might be necessary in the future?
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //nothing and probably keeping it this way
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_INITIAL_SETUP) {
            HomePageFragment homePageFragment = (HomePageFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            homePageFragment.updateTargets();
        }

        //passing unhandled results to child fragment's onActivityResult
        super.onActivityResult(requestCode, resultCode, data);
    }

}
