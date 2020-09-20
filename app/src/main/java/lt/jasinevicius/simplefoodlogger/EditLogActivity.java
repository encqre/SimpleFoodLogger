package lt.jasinevicius.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.util.UUID;

public class EditLogActivity extends AppCompatActivity {

    private static final String EXTRA_LOG_ID = "simplefoodlogger.log_id";

    public static Intent newIntent(Context packageContext, UUID logId) {
        Intent intent = new Intent(packageContext, EditLogActivity.class);
        intent.putExtra(EXTRA_LOG_ID, logId);

        return intent;
    }

    private Toolbar mToolbar;
    private SharedPreferences mPreferences;
    private Fragment editLogFragment;

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
        ab.setTitle("Edit log entry");
        ab.setDisplayHomeAsUpEnabled(true);

        UUID logId = (UUID) getIntent().getSerializableExtra(EXTRA_LOG_ID);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            editLogFragment = getSupportFragmentManager().getFragment(savedInstanceState, "editLogFragment");
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, EditLogFragment.newInstance(logId)).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //force hide keyboard and override toolbar back button to do same as bottom(hard) back button
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = this.getCurrentFocus();
                if (view == null) {
                    view = new View(this);
                }
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        editLogFragment = getSupportFragmentManager().findFragmentById(R.id.single_fragment_container);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "editLogFragment", editLogFragment);
    }
}
