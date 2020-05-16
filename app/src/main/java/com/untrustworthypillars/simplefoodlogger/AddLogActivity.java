package com.untrustworthypillars.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;
import java.util.UUID;

public class AddLogActivity extends AppCompatActivity {

    private static final String EXTRA_FOOD_ID = "simplefoodlogger.food_id";
    private static final String EXTRA_FOOD_TYPE = "simplefoodlogger.food_type";
    private static final String EXTRA_DATE = "simplefoodlogger.date";

    public static Intent newIntent(Context packageContext, UUID foodId, int foodType, Date date) {
        Intent intent = new Intent(packageContext, AddLogActivity.class);
        intent.putExtra(EXTRA_FOOD_ID, foodId);
        intent.putExtra(EXTRA_FOOD_TYPE, foodType);
        intent.putExtra(EXTRA_DATE, date);

        return intent;
    }

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        mToolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Add new log entry");
        ab.setDisplayHomeAsUpEnabled(true);

        UUID foodId = (UUID) getIntent().getSerializableExtra(EXTRA_FOOD_ID);
        Integer foodType = (Integer) getIntent().getSerializableExtra(EXTRA_FOOD_TYPE);
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_DATE);

//        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new AddLogFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, AddLogFragment.newInstance(foodId, foodType, date)).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //override toolbar back button to do same as bottom(hard) back button
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                //TODO fix so no keyboard blip when going back with arrow when keyboard was closed
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
