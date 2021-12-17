package lt.jasinevicius.simplefoodlogger;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String theme = preferences.getString(
                LoggerSettings.PREFERENCE_THEME,
                LoggerSettings.PREFERENCE_THEME_DEFAULT
        );
        if (theme.equals("Light theme")) {
            setTheme(R.style.AppTheme);
        } else if (theme.equals("Dark theme")) {
            setTheme(R.style.AppThemeDark);
        }
    }
}
