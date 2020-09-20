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

public class SetMacrosActivity extends AppCompatActivity {

    private static final String EXTRA_IN_SETUP = "simplefoodlogger.insetup";

    public static Intent newIntent(Context packageContext, boolean inSetup) {
        Intent intent = new Intent(packageContext, SetMacrosActivity.class);
        intent.putExtra(EXTRA_IN_SETUP, inSetup);
        return intent;
    }

    private Toolbar mToolbar;
    private SharedPreferences mPreferences;
    private boolean inSetup;
    private Fragment setMacrosFragment;

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

        inSetup = (boolean) getIntent().getSerializableExtra(EXTRA_IN_SETUP);

        mToolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Set Macronutrient Targets");
        ab.setDisplayHomeAsUpEnabled(true);
        if (inSetup) {
            ab.hide();
        }

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            setMacrosFragment = getSupportFragmentManager().getFragment(savedInstanceState, "setMacrosFragment");
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetMacrosFragment.newInstance(inSetup)).commit();
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setMacrosFragment = getSupportFragmentManager().findFragmentById(R.id.single_fragment_container);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "setMacrosFragment", setMacrosFragment);
    }
}
