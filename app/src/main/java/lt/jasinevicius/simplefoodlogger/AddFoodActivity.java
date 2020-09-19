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

public class AddFoodActivity extends AppCompatActivity {

    private static final String EXTRA_CATEGORY = "simplefoodlogger.category";

    private Toolbar mToolbar;
    private SharedPreferences mPreferences;


    public static Intent newIntent(Context packageContext, int category) {
        Intent intent = new Intent(packageContext, AddFoodActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);

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
        setContentView(R.layout.activity_single_fragment);

        mToolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Add new custom food");
        ab.setDisplayHomeAsUpEnabled(true);

        Integer category = (Integer) getIntent().getSerializableExtra(EXTRA_CATEGORY);

        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, AddFoodFragment.newInstance(category)).commit();
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
