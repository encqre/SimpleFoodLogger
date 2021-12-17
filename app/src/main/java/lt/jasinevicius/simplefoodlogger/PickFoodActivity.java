package lt.jasinevicius.simplefoodlogger;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import java.util.Date;

public class PickFoodActivity extends BaseActivity {

    public static final String EXTRA_DATE = "com.untrustworthypillars.simplefoodlogger.homepagefragment.date";

    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, PickFoodActivity.class);
        intent.putExtra(EXTRA_DATE, date);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_fragment_no_toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new FoodListFragment()).commit();
    }
}




