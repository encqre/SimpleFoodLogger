package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class SetCaloriesProfileFragment extends Fragment {

    private static final String ARG_TITLE = "title";

    private SharedPreferences mPreferences;

    private TextView mTitleTextview;
    private RadioButton mProfileGenderMale;
    private RadioButton mProfileGenderFemale;
    private EditText mProfileAge;
    private EditText mProfileWeight;
    private RadioGroup mProfileWeightRadioGroup;
    private RadioButton mProfileWeightKg;
    private RadioButton mProfileWeightLbs;
    private EditText mProfileHeightCm;
    private EditText mProfileHeightFeet;
    private EditText mProfileHeightIn;
    private RadioGroup mProfileHeightRadioGroup;
    private RadioButton mProfileHeightCmButton;
    private RadioButton mProfileHeightFtinButton;
    private Spinner mProfileActivitySpinner;
    private Spinner mProfileGoalSpinner;
    private Button mProfileBackButton;
    private Button mProfileManualButton;
    private Button mProfileCalculateButton;

    public static SetCaloriesProfileFragment newInstance (boolean showTitle) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TITLE, showTitle);

        SetCaloriesProfileFragment fragment = new SetCaloriesProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_calories_profile, container, false);

        boolean showTitle = (boolean) getArguments().getSerializable(ARG_TITLE);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


        mTitleTextview = (TextView) v.findViewById(R.id.initial_setup_calories_profile_title_text);
        if (!showTitle) {
            mTitleTextview.setVisibility(View.GONE);
        }


        mProfileGenderMale = (RadioButton) v.findViewById(R.id.initial_setup_calories_profile_gender_male);
        mProfileGenderMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreferences.edit().putString("pref_gender", "Male").apply();
            }
        });
        mProfileGenderFemale = (RadioButton) v.findViewById(R.id.initial_setup_calories_profile_gender_female);
        mProfileGenderFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreferences.edit().putString("pref_gender", "Female").apply();
            }
        });

        if (mPreferences.getString("pref_gender", "not_set").equals("Male")) {
            mProfileGenderMale.setChecked(true);
        } else if (mPreferences.getString("pref_gender", "not_set").equals("Female")) {
            mProfileGenderFemale.setChecked(true);
        }

        mProfileAge = (EditText) v.findViewById(R.id.initial_setup_calories_profile_age_edittext);
        mProfileAge.setInputType(InputType.TYPE_CLASS_NUMBER);
        mProfileAge.setText(mPreferences.getString("pref_age", ""));

        mProfileWeightKg = (RadioButton) v.findViewById(R.id.initial_setup_calories_profile_weight_kg);
        mProfileWeightLbs = (RadioButton) v.findViewById(R.id.initial_setup_calories_profile_weight_lbs);
        mProfileWeightRadioGroup = (RadioGroup) v.findViewById(R.id.initial_setup_calories_profile_weight_radiogroup);
        if (mPreferences.getString("pref_units", "Metric").equals("Metric")) {
            mProfileWeightKg.setChecked(true);
        } else {
            mProfileWeightLbs.setChecked(true);
        }
        mProfileWeight = (EditText) v.findViewById(R.id.initial_setup_calories_profile_weight_edittext);
        mProfileWeight.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (mPreferences.getString("pref_units", "Metric").equals("Metric")) {
            mProfileWeight.setText(mPreferences.getString("pref_weight", ""));
        } else if (!mPreferences.getString("pref_weight", "").equals("")) {
            mProfileWeight.setText( String.valueOf(Math.round(Float.parseFloat(mPreferences.getString("pref_weight", "0")) * 2.2)));
        } else {
            mProfileWeight.setText("");
        }

        mProfileWeightRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.initial_setup_calories_profile_weight_kg:
                        if (!mProfileWeight.getText().toString().equals("")) {
                            mProfileWeight.setText(String.valueOf(Math.round((Float.parseFloat(mProfileWeight.getText().toString()) / 2.2))));
                        }
                        break;
                    case R.id.initial_setup_calories_profile_weight_lbs:
                        if (!mProfileWeight.getText().toString().equals("")) {
                            mProfileWeight.setText(String.valueOf(Math.round((Float.parseFloat(mProfileWeight.getText().toString()) * 2.2))));
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        mProfileHeightRadioGroup = (RadioGroup) v.findViewById(R.id.initial_setup_calories_profile_height_radiogroup);
        mProfileHeightCmButton = (RadioButton) v.findViewById(R.id.initial_setup_calories_profile_height_cm);
        mProfileHeightFtinButton = (RadioButton) v.findViewById(R.id.initial_setup_calories_profile_height_ftin);
        if (mPreferences.getString("pref_units", "Metric").equals("Metric")) {
            mProfileHeightCmButton.setChecked(true);
        } else {
            mProfileHeightFtinButton.setChecked(true);
        }

        mProfileHeightCm = (EditText) v.findViewById(R.id.initial_setup_calories_profile_height_edittext_cm);
        mProfileHeightCm.setInputType(InputType.TYPE_CLASS_NUMBER);

        mProfileHeightFeet = (EditText) v.findViewById(R.id.initial_setup_calories_profile_height_edittext_ft);
        mProfileHeightFeet.setInputType(InputType.TYPE_CLASS_NUMBER);

        mProfileHeightIn = (EditText) v.findViewById(R.id.initial_setup_calories_profile_height_edittext_in);
        mProfileHeightIn.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (mPreferences.getString("pref_units", "Metric").equals("Metric")) {
            mProfileHeightCm.setText(mPreferences.getString("pref_height", ""));
        } else {
            mProfileHeightCm.setVisibility(View.INVISIBLE);
            mProfileHeightFeet.setVisibility(View.VISIBLE);
            mProfileHeightIn.setVisibility(View.VISIBLE);
            if (!mPreferences.getString("pref_height", "").equals("")) {
                long [] ftin = cmToFtin(Integer.parseInt(mPreferences.getString("pref_height", "0")));
                mProfileHeightFeet.setText(String.valueOf(ftin[0]));
                mProfileHeightIn.setText(String.valueOf(ftin[1]));
            } else {
                mProfileHeightFeet.setText("");
                mProfileHeightIn.setText("");
            }
        }

        mProfileHeightRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.initial_setup_calories_profile_height_cm:
                        mProfileHeightFeet.setVisibility(View.INVISIBLE);
                        mProfileHeightIn.setVisibility(View.INVISIBLE);
                        mProfileHeightCm.setVisibility(View.VISIBLE);
                        mProfileHeightCm.setText(mPreferences.getString("pref_height", ""));
                        break;
                    case R.id.initial_setup_calories_profile_height_ftin:
                        mProfileHeightFeet.setVisibility(View.VISIBLE);
                        mProfileHeightIn.setVisibility(View.VISIBLE);
                        mProfileHeightCm.setVisibility(View.INVISIBLE);
                        if (mPreferences.getString("pref_height", "0").equals("")) {
                            mProfileHeightFeet.setText("");
                            mProfileHeightIn.setText("");
                        } else {
                            long[] ftin = cmToFtin(Integer.parseInt(mPreferences.getString("pref_height", "0")));
                            mProfileHeightFeet.setText(String.valueOf(ftin[0]));
                            mProfileHeightIn.setText(String.valueOf(ftin[1]));
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        mProfileActivitySpinner = (Spinner) v.findViewById(R.id.initial_setup_calories_profile_activity_spinner);
        mProfileActivitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.edit().putString("pref_activity_level", String.valueOf(position)).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }

        });
        mProfileActivitySpinner.setSelection(Integer.parseInt(mPreferences.getString("pref_activity_level", "0")));

        mProfileGoalSpinner = (Spinner) v.findViewById(R.id.initial_setup_calories_profile_goal_spinner);
        mProfileGoalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPreferences.edit().putString("pref_goal", String.valueOf(position)).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }

        });
        mProfileGoalSpinner.setSelection(Integer.parseInt(mPreferences.getString("pref_goal", "0")));

        mProfileBackButton = (Button) v.findViewById(R.id.initial_setup_calories_profile_button_back);
        mProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        mProfileManualButton = (Button) v.findViewById(R.id.initial_setup_calories_profile_button_manual);
        mProfileManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesFragment.newInstance(true)).commit();
            }
        });

        mProfileCalculateButton = (Button) v.findViewById(R.id.initial_setup_calories_profile_button_calculate);
        mProfileCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verify if all fields are filled
                if (mProfileAge.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please fill in your age", Toast.LENGTH_SHORT).show();
                } else if (mProfileWeight.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please fill in your weight", Toast.LENGTH_SHORT).show();
                } else if (mProfileHeightCmButton.isChecked() && mProfileHeightCm.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please fill in your height", Toast.LENGTH_SHORT).show();
                } else if (mProfileHeightFtinButton.isChecked() && mProfileHeightFeet.getText().toString().equals("") && mProfileHeightIn.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please fill in your height", Toast.LENGTH_SHORT).show();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.single_fragment_container, SetCaloriesFragment.newInstance(false)).commit();
                }
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        saveAgeWeightHeight();
        super.onDestroyView();
    }

    public static long ftinToCm(int ft, int in) {
        return Math.round((in * 2.54) + (ft * 12 * 2.54));
    }

    public static long[] cmToFtin(int cm) {
        long ft = (long)(cm / 30.48);
        long in = Math.round((cm%30.48) / 2.54);
        return new long[] {ft, in};
    }

    private void saveAgeWeightHeight() {
        //save stored age
        mPreferences.edit().putString("pref_age", mProfileAge.getText().toString()).apply();

        //save stored weight
        if (!mProfileWeight.getText().toString().equals("")) {
            if (mProfileWeightKg.isChecked()) {
                mPreferences.edit().putString("pref_weight", mProfileWeight.getText().toString()).apply();
            } else {
                long weight = Math.round((Float.parseFloat(mProfileWeight.getText().toString()) / 2.2));
                mPreferences.edit().putString("pref_weight", String.valueOf(weight)).apply();
            }
        } else {
            mPreferences.edit().putString("pref_weight", mProfileWeight.getText().toString()).apply();
        }

        //save stored height
        if (mProfileHeightCmButton.isChecked()) {
            mPreferences.edit().putString("pref_height", mProfileHeightCm.getText().toString()).apply();
        }
        if (mProfileHeightFtinButton.isChecked()) {
            int inches, feet;
            if (!mProfileHeightIn.getText().toString().equals("")) {
                inches = Integer.parseInt(mProfileHeightIn.getText().toString());
            } else {
                inches = 0;
            }
            if (!mProfileHeightFeet.getText().toString().equals("")) {
                feet = Integer.parseInt(mProfileHeightFeet.getText().toString());
            } else {
                feet = 0;
            }
            mPreferences.edit().putString("pref_height", String.valueOf(ftinToCm(feet, inches))).apply();
        }

    }

}
