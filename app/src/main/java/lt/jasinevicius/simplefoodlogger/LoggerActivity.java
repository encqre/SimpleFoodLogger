package lt.jasinevicius.simplefoodlogger;

import com.google.android.material.tabs.TabLayout;

import android.content.Intent;
import android.os.Bundle;

import java.util.Date;

public class LoggerActivity extends BaseActivity {

    private static final String SAVED_OPEN_TAB = "open_tab";
    private static final String SAVED_SELECTED_DATE = "selected_date";
    public static final int TAB_HOME = 0;
    public static final int TAB_FOODS = 1;
    public static final int TAB_SUMMARY = 2;
    public static final int TAB_SETTINGS = 3;

    private static final int REQUEST_INITIAL_SETUP = 0;

    private TabLayout tabLayout;
    private Date selectedDay;

    /** Method to change the selected Tab programmatically*/
    public void setTab(int i) {
        TabLayout.Tab tab = tabLayout.getTabAt(i);
        tab.select();
    }

    public Date getSelectedDay() {
        return selectedDay;
    }

    public void setSelectedDay(Date selectedDay) {
        this.selectedDay = selectedDay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logger);

        if (preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_DB_SETUP_NEEDED, true) ||
                preferences.getBoolean(LoggerSettings.PREFERENCE_INITIAL_PROFILE_SETUP_NEEDED, true)) {
            Intent intent = InitialSetupActivity.newIntent(LoggerActivity.this);
            startActivityForResult(intent, REQUEST_INITIAL_SETUP);
        }

        if (savedInstanceState != null) {
            selectedDay = (Date) savedInstanceState.getSerializable(SAVED_SELECTED_DATE);
        } else {
            selectedDay = new Date();
        }

        /** Assigning object only to TabLayout and not separate tabs, because tabItems are just dummies for the layout apparently**/
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        /**
         * On Click listener for the tabs. Tab positions are numbered from 0. When selected Tab changes, we check what is the selected tab position
         * ( with TabLayout.getSelectedTabPosition), and then accordingly replace the layout for the fragment_container which respective fragment layout.
         */
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tabLayout.getSelectedTabPosition()) {
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragmentParent()).commit();
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

        /** Replacing blank fragment container with home fragment or previously open fragment if activity is recreated **/
        if (savedInstanceState != null) {
            setTab(savedInstanceState.getInt(SAVED_OPEN_TAB));
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomePageFragment()).commit();
        }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_OPEN_TAB, tabLayout.getSelectedTabPosition());
        outState.putSerializable(SAVED_SELECTED_DATE, selectedDay);
    }

}
