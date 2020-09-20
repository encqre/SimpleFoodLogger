package lt.jasinevicius.simplefoodlogger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**This fragment contains toolbar and an inner fragment (SettingsFragment). The reason why nested
 * fragments are used in this case is because i found no better way to add toolbar to SettingsFragment,
 * because it extends PreferenceFragment class, so it uses a layout of which i don't have much control of */

public class SettingsFragmentParent extends Fragment {

    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.fragment_settings_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Fragment childFragment = new SettingsFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_settings_preferences, childFragment).commit();
    }

}
