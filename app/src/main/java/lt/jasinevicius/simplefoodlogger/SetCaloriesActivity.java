package lt.jasinevicius.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class SetCaloriesActivity extends AppCompatActivity {

    private static final String EXTRA_TITLE = "simplefoodlogger.title";
    private static final String EXTRA_STAGE = "simplefoodlogger.stage";

    public static final int STAGE_PROFILE = 0;
    public static final int STAGE_CONFIRM_KCAL = 1;

    public static Intent newIntent(Context packageContext, boolean showTitle, int stage) {
        Intent intent = new Intent(packageContext, SetCaloriesActivity.class);
        intent.putExtra(EXTRA_TITLE, showTitle);
        intent.putExtra(EXTRA_STAGE, stage);
        return intent;
    }

    private Toolbar mToolbar;
    private SharedPreferences mPreferences;
    private boolean mShowTitle;
    private Fragment openFragment;

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

        mShowTitle = (boolean) getIntent().getSerializableExtra(EXTRA_TITLE);
        Integer stage = (Integer) getIntent().getSerializableExtra(EXTRA_STAGE);

        mToolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Set Daily Calories Target");
        ab.setDisplayHomeAsUpEnabled(true);
        if (mShowTitle) {
            ab.hide();
        }

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            openFragment = getSupportFragmentManager().getFragment(savedInstanceState, "openFragment");
        } else {
            switch (stage) {
                case STAGE_PROFILE:
                    getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesProfileFragment.newInstance(mShowTitle)).commit();
                    break;
                case STAGE_CONFIRM_KCAL:
                    getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesFragment.newInstance(true, mShowTitle)).commit();
                    break;
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesProfileFragment.newInstance(mShowTitle)).commit();
            }
        }
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().findFragmentById(R.id.single_fragment_container).getClass().getName().equals(SetCaloriesFragment.class.getName())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesProfileFragment.newInstance(mShowTitle)).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //override toolbar back button
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        openFragment = getSupportFragmentManager().findFragmentById(R.id.single_fragment_container);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "openFragment", openFragment);
    }
}
