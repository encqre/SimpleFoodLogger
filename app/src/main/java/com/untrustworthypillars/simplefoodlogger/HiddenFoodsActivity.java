package com.untrustworthypillars.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;


public class HiddenFoodsActivity extends AppCompatActivity {

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, HiddenFoodsActivity.class);
        return intent;
    }

    private Toolbar mToolbar;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String theme = mPreferences.getString("pref_theme", "Light theme");
        if (theme.equals("Light theme")) {
            setTheme(R.style.AppTheme);
        } else if (theme.equals("Dark theme")) {
            setTheme(R.style.AppThemeDark);
        }
        setContentView(R.layout.activity_single_fragment);

        mToolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("Hidden Foods");
        ab.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new HiddenFoodsFragment()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_unhide_all:
                HiddenFoodsFragment frag = (HiddenFoodsFragment) getSupportFragmentManager().findFragmentById(R.id.single_fragment_container);
                frag.unhideAllFoods();
                return true;
            case android.R.id.home: //override toolbar back button to do same as bottom(hard) back button
                this.onBackPressed();
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
