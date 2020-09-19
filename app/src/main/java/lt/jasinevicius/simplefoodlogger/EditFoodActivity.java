package lt.jasinevicius.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import lt.jasinevicius.simplefoodlogger.R;

import java.util.UUID;

public class EditFoodActivity extends AppCompatActivity {

    private static final String EXTRA_FOOD_ID = "simplefoodlogger.food_id";
    private static final String EXTRA_FOOD_TYPE = "simplefoodlogger.food_type";

    public static Intent newIntent(Context packageContext, UUID foodId, int foodType) {
        Intent intent = new Intent(packageContext, EditFoodActivity.class);
        intent.putExtra(EXTRA_FOOD_ID, foodId);
        intent.putExtra(EXTRA_FOOD_TYPE, foodType);

        return intent;
    }

    private Toolbar mToolbar;
    private SharedPreferences mPreferences;

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
        setContentView(R.layout.activity_single_fragment);

        mToolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Edit food");
        ab.setDisplayHomeAsUpEnabled(true);

        UUID foodId = (UUID) getIntent().getSerializableExtra(EXTRA_FOOD_ID);
        int foodType = (int) getIntent().getSerializableExtra(EXTRA_FOOD_TYPE);

        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, EditFoodFragment.newInstance(foodId, foodType)).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //override toolbar back button to do same as bottom(hard) back button
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
