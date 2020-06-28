package com.untrustworthypillars.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SetCaloriesActivity extends AppCompatActivity {

    private static final String EXTRA_TITLE = "simplefoodlogger.title";
    private static final String EXTRA_STAGE = "simplefoodlogger.stage";

    public static final int STAGE_PROFILE = 0;
    public static final int STAGE_CONFIRM_KCAL = 1;

    public static Intent newIntent(Context packageContext, String title, int stage) {
        Intent intent = new Intent(packageContext, SetCaloriesActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_STAGE, stage);
        return intent;
    }

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        String title = (String) getIntent().getSerializableExtra(EXTRA_TITLE);
        Integer stage = (Integer) getIntent().getSerializableExtra(EXTRA_STAGE);

        mToolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Set Daily Calories Target");
        ab.setDisplayHomeAsUpEnabled(true);
        if (title.equals("")) {
            ab.hide();
        }

        switch(stage) {
            case STAGE_PROFILE:
                getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new SetCaloriesProfileFragment()).commit();
                break;
            case STAGE_CONFIRM_KCAL:
                getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesFragment.newInstance(true)).commit();
                break;
            default:
                getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new SetCaloriesProfileFragment()).commit();
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

}
