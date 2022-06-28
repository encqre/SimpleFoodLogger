package lt.jasinevicius.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class AddFoodActivity extends BaseActivity {

    private static final String EXTRA_CATEGORY = "simplefoodlogger.category";

    private Toolbar toolbar;
    private Fragment addFoodFragment;


    public static Intent newIntent(Context packageContext, int category) {
        Intent intent = new Intent(packageContext, AddFoodActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_fragment);

        toolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(getString(R.string.add_food_activity_title));
        ab.setDisplayHomeAsUpEnabled(true);

        Integer category = (Integer) getIntent().getSerializableExtra(EXTRA_CATEGORY);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            addFoodFragment = getSupportFragmentManager().getFragment(savedInstanceState, "addFoodFragment");
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.single_fragment_container, AddFoodFragment.newInstance(category))
                    .commit();
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
        addFoodFragment = getSupportFragmentManager().findFragmentById(R.id.single_fragment_container);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "addFoodFragment", addFoodFragment);
    }
}
