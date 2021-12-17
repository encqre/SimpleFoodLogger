package lt.jasinevicius.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class SetCaloriesActivity extends BaseActivity {

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

    private Toolbar toolbar;
    private boolean showTitle;
    private Fragment openFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_fragment);

        showTitle = (boolean) getIntent().getSerializableExtra(EXTRA_TITLE);
        Integer stage = (Integer) getIntent().getSerializableExtra(EXTRA_STAGE);

        toolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Set Daily Calories Target");
        ab.setDisplayHomeAsUpEnabled(true);
        if (showTitle) {
            ab.hide();
        }

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            openFragment = getSupportFragmentManager().getFragment(savedInstanceState, "openFragment");
        } else {
            switch (stage) {
                case STAGE_PROFILE:
                    getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesProfileFragment.newInstance(showTitle)).commit();
                    break;
                case STAGE_CONFIRM_KCAL:
                    getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesFragment.newInstance(true, showTitle)).commit();
                    break;
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesProfileFragment.newInstance(showTitle)).commit();
            }
        }
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().findFragmentById(R.id.single_fragment_container).getClass().getName().equals(SetCaloriesFragment.class.getName())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesProfileFragment.newInstance(showTitle)).commit();
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
