package com.untrustworthypillars.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class HiddenFoodsActivity extends AppCompatActivity {

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, HiddenFoodsActivity.class);
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
        ab.setTitle("Hidden Foods");
        ab.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new HiddenFoodsFragment()).commit();

    }

    //TODO override toolbar back button to do the same as bottom back navigation button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_unhide_all:
                //TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hidden_foods_activity, menu);

        return true;
    }
}
