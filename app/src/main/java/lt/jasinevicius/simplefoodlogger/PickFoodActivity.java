package lt.jasinevicius.simplefoodlogger;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import java.util.Date;

public class PickFoodActivity extends AppCompatActivity {

    public static final String EXTRA_DATE = "com.untrustworthypillars.simplefoodlogger.homepagefragment.date";

    private SharedPreferences mPreferences;

    private int mSelectedFoodCategory = 0;

    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, PickFoodActivity.class);
        intent.putExtra(EXTRA_DATE, date);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String theme = mPreferences.getString(LoggerSettings.PREFERENCE_THEME, LoggerSettings.PREFERENCE_THEME_DEFAULT);
        if (theme.equals("Light theme")) {
            setTheme(R.style.AppTheme);
        } else if (theme.equals("Dark theme")) {
            setTheme(R.style.AppThemeDark);
        }
        setContentView(R.layout.activity_single_fragment_no_toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new FoodListFragment()).commit();

    }

}




