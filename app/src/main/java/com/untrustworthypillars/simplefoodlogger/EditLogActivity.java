package com.untrustworthypillars.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.UUID;

public class EditLogActivity extends AppCompatActivity {

    private static final String EXTRA_LOG_ID = "simplefoodlogger.log_id";

    public static Intent newIntent(Context packageContext, UUID logId) {
        Intent intent = new Intent(packageContext, EditLogActivity.class);
        intent.putExtra(EXTRA_LOG_ID, logId);

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
        ab.setTitle("Edit log entry");
        ab.setDisplayHomeAsUpEnabled(true);

        UUID logId = (UUID) getIntent().getSerializableExtra(EXTRA_LOG_ID);

//        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, new EditLogFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, EditLogFragment.newInstance(logId)).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //override toolbar back button to do same as bottom(hard) back button
                InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                //TODO fix so no keyboard blip when going back with arrow when keyboard was closed already
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
