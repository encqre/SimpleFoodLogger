package com.untrustworthypillars.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

public class PickFoodActivity extends AppCompatActivity {

    public static final String EXTRA_DATE = "com.untrustworthypillars.simplefoodlogger.homepagefragment.date";
    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, PickFoodActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }
    private static final int REQUEST_ADD_FOOD = 1;
    private Toolbar mToolbar;
    private int mSelectedFoodCategory = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        mToolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Select Food");
        ab.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new FoodListFragment()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_food:
                //Launching AddFoodDialog when "New Food" is pressed on the Toolbar
                FragmentManager fm = getSupportFragmentManager();
                FoodListFragment foodListFragment = (FoodListFragment) fm.findFragmentById(R.id.single_fragment_container); //Finding foodListFragment fragment instance
                mSelectedFoodCategory = foodListFragment.getSelectedCategory(); //getting what is the currently selected food category in the fragment
                AddFoodDialog dialog = AddFoodDialog.newInstance(mSelectedFoodCategory);
                dialog.setTargetFragment(foodListFragment, REQUEST_ADD_FOOD); //setting foodListFragment fragment as a target, so that it can updateUI after food is added/deleted etc.
                dialog.show(fm, "AddFoodDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_log_activity, menu);

        return true;
        }
}




