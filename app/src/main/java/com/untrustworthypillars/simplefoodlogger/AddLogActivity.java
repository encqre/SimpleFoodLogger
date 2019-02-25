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

public class AddLogActivity extends AppCompatActivity {

    public static final String EXTRA_DATE = "com.untrustworthypillars.simplefoodlogger.homepagefragment.date";
    public static Intent newIntent(Context packageContext, Date date) {
        Intent intent = new Intent(packageContext, AddLogActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_log);

        mToolbar = (Toolbar) findViewById(R.id.add_log_toolbar);
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
                AddFoodDialog dialog = AddFoodDialog.newInstance(0); //TODO need to pass correct category somehow
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



