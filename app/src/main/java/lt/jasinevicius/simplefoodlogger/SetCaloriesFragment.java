package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import lt.jasinevicius.simplefoodlogger.R;

import lt.jasinevicius.simplefoodlogger.reusable.EditTextWithSuffix;

public class SetCaloriesFragment extends Fragment {

    private static final String ARG_MANUAL = "manual?";
    private static final String ARG_IN_SETUP = "are we in setup?";

    private SharedPreferences mPreferences;

    private boolean mManual;
    private boolean inSetup;
    private TextView mSetKcalUpperText;
    private EditTextWithSuffix mSetKcalEditText;
    private TextView mSetKcalLowerText;
    private Button mSetKcalBackButton;
    private Button mSetKcalContinueButton;

    public static SetCaloriesFragment newInstance (boolean manual, boolean inSetup) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MANUAL, manual);
        args.putSerializable(ARG_IN_SETUP, inSetup);

        SetCaloriesFragment fragment = new SetCaloriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_calories, container, false);

        mManual = (boolean) getArguments().getSerializable(ARG_MANUAL);
        inSetup = (boolean) getArguments().getSerializable(ARG_IN_SETUP);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String upperText;
        String lowerText;
        String recommendedKcal;
        if (mManual) {
            upperText = "Enter your daily calories target:";
            lowerText = "You can go back to estimate your daily calories target based on your gender, age, weight, height, activity level and goal, using Mifflin-St Jeor Equation.";
            recommendedKcal = mPreferences.getString(LoggerSettings.PREFERENCE_TARGET_CALORIES, "");
        } else {
            upperText = "Your recommended daily calories target*:";
            lowerText = "*Calculated using Mifflin-St Jeor Equation.";
            recommendedKcal = calculateKcalMifflinStJeor();
            mPreferences.edit().putString(LoggerSettings.PREFERENCE_TARGET_CALORIES_CALCULATED, recommendedKcal).apply();
        }

        mSetKcalUpperText = (TextView) v.findViewById(R.id.initial_setup_calories_set_text_upper);
        mSetKcalUpperText.setText(upperText);

        mSetKcalLowerText = (TextView) v.findViewById(R.id.initial_setup_calories_set_text_lower);
        mSetKcalLowerText.setText(lowerText);

        mSetKcalEditText = (EditTextWithSuffix) v.findViewById(R.id.initial_setup_calories_set_kcal_edittext);
        mSetKcalEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSetKcalEditText.setText(recommendedKcal);
        mSetKcalEditText.setHint(recommendedKcal);

        mSetKcalBackButton = (Button) v.findViewById(R.id.initial_setup_calories_set_button_back);
        mSetKcalBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesProfileFragment.newInstance(inSetup)).commit();
            }
        });

        mSetKcalContinueButton = (Button) v.findViewById(R.id.initial_setup_calories_set_button_continue);
        mSetKcalContinueButton.setText(inSetup ? "CONTINUE" : "SAVE");
        mSetKcalContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSetKcalEditText.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please fill daily target calories field", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(mSetKcalEditText.getText().toString()) <= 0) {
                    Toast.makeText(getActivity(), "Please enter a valid daily target calories number", Toast.LENGTH_SHORT).show();
                } else {
                    mPreferences.edit().putString(LoggerSettings.PREFERENCE_TARGET_CALORIES, mSetKcalEditText.getText().toString()).apply();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        });

        return v;
    }

    public String calculateKcalMifflinStJeor(){
        float [] activityMultiplierTable = {1.2f,1.375f,1.465f,1.55f,1.725f,1.9f};
        float [] goalMutliplierTable = {1f,0.9f,0.8f,1.1f,1.2f};

        String gender = mPreferences.getString(LoggerSettings.PREFERENCE_GENDER, LoggerSettings.PREFERENCE_GENDER_DEFAULT);
        int weight = Integer.parseInt(mPreferences.getString(LoggerSettings.PREFERENCE_WEIGHT, LoggerSettings.PREFERENCE_WEIGHT_DEFAULT));
        int height = Integer.parseInt(mPreferences.getString(LoggerSettings.PREFERENCE_HEIGHT, LoggerSettings.PREFERENCE_HEIGHT_DEFAULT));
        int age = Integer.parseInt(mPreferences.getString(LoggerSettings.PREFERENCE_AGE, LoggerSettings.PREFERENCE_AGE_DEFAULT));
        float activityMultiplier = activityMultiplierTable[Integer.parseInt(mPreferences.getString(LoggerSettings.PREFERENCE_ACTIVITY_LEVEL, LoggerSettings.PREFERENCE_ACTIVITY_LEVEL_DEFAULT))];
        float goalMultiplier = goalMutliplierTable[Integer.parseInt(mPreferences.getString(LoggerSettings.PREFERENCE_GOAL, LoggerSettings.PREFERENCE_GOAL_DEFAULT))];

        float estimatedKcal;
        if (gender.equals("Male")){
            estimatedKcal = activityMultiplier * goalMultiplier * ((10 * weight) + (6.25f * height) - (5 * age) + 5);
        } else {
            estimatedKcal = activityMultiplier * goalMultiplier * ((10 * weight) + (6.25f * height) - (5 * age) - 161);
        }

        return String.valueOf((int) estimatedKcal);
    }

}
