package lt.jasinevicius.simplefoodlogger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import lt.jasinevicius.simplefoodlogger.reusable.SimpleConfirmationDialog;


public class HiddenFoodsActivity extends BaseActivity {

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, HiddenFoodsActivity.class);
        return intent;
    }

    private static final int REQUEST_UNHIDE_ALL = 1;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_fragment);

        toolbar = (Toolbar) findViewById(R.id.single_fragment_activity_toolbar);
        setSupportActionBar(toolbar);

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
                String message = "Are you sure you want to restore all hidden food items?";
                String title = "Restore all hidden foods?";
                SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newInstance(message, title);
                dialog.setTargetFragment(frag, REQUEST_UNHIDE_ALL);
                dialog.show(getSupportFragmentManager(), "delete_log");
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
