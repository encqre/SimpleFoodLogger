package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

//TODO maybe add a question mark somewhere inside form, that would explain what is this, and what values are recommended.

public class SetMacrosFragment extends Fragment {

    private SharedPreferences mPreferences;

    private int mTargetCalories;
    private int mTargetProtein;
    private int mTargetCarbs;
    private int mTargetFat;
    private float mTargetProteinPercent;
    private float mTargetCarbsPercent;
    private float mTargetFatPercent;

    private TextView mCurrentCalorieTargetTextView;
    private RadioGroup mInputRadioGroup;
    private RadioButton mRadioPercentage;
    private RadioButton mRadioManual;
    private EditText mProteinInput;
    private EditText mCarbsInput;
    private EditText mFatInput;
    private TextView mProteinText;
    private TextView mCarbsText;
    private TextView mFatText;
    private TextView mBottomInfoText;
    private TextView mBottomInfoText2;
    private Button mSaveButton;
    private Button mCancelButton;

    private boolean mIsInputPercent = true;
    private boolean mRecalcLock = true; //need this to avoid some rounding errors when changing input mode between percent/manual

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_macros, container, false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTargetCalories = Integer.parseInt(mPreferences.getString("pref_calories", "2500"));
        mTargetProtein = Integer.parseInt(mPreferences.getString("pref_protein", "200"));
        mTargetCarbs = Integer.parseInt(mPreferences.getString("pref_carbs", "300"));
        mTargetFat = Integer.parseInt(mPreferences.getString("pref_fat", "90"));

        mTargetProteinPercent = (float) (mTargetProtein * 4) / mTargetCalories * 100;
        mTargetCarbsPercent = (float) (mTargetCarbs * 4) / mTargetCalories * 100;
        mTargetFatPercent = (float) (mTargetFat * 9) / mTargetCalories * 100;


        mCurrentCalorieTargetTextView = (TextView) v.findViewById(R.id.fragment_set_macros_calories_target);
        mCurrentCalorieTargetTextView.setText(String.valueOf(mTargetCalories));

        mInputRadioGroup = (RadioGroup) v.findViewById(R.id.fragment_set_macros_radio_group);
        mRadioPercentage = (RadioButton) v.findViewById(R.id.fragment_set_macros_radio_percent);
        mRadioPercentage.setChecked(true);
        mRadioPercentage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsInputPercent = true;
                    mRecalcLock = true;
                    mProteinInput.setText(String.valueOf(Math.round(mTargetProteinPercent)));
                    mCarbsInput.setText(String.valueOf(Math.round(mTargetCarbsPercent)));
                    mFatInput.setText(String.valueOf(Math.round(mTargetFatPercent)));
                    mProteinText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetProtein, 4, (mTargetProtein*4)));
                    mCarbsText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetCarbs, 4, (mTargetCarbs*4)));
                    mFatText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetFat, 9, (mTargetFat*9)));
                    setBottomInfoTexts();
                    mRecalcLock = false;
                }
            }
        });

        mRadioManual = (RadioButton) v.findViewById(R.id.fragment_set_macros_radio_manual);
        mRadioManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsInputPercent = false;
                    mRecalcLock = true;
                    mProteinInput.setText(String.valueOf(mTargetProtein));
                    mCarbsInput.setText(String.valueOf(mTargetCarbs));
                    mFatInput.setText(String.valueOf(mTargetFat));
                    mProteinText.setText(getString(R.string.set_macros_fragment_manual_macro_info, 4, (mTargetProtein * 4), Math.round(mTargetProteinPercent)));
                    mCarbsText.setText(getString(R.string.set_macros_fragment_manual_macro_info, 4, (mTargetCarbs * 4), Math.round(mTargetCarbsPercent)));
                    mFatText.setText(getString(R.string.set_macros_fragment_manual_macro_info, 9, (mTargetFat * 9), Math.round(mTargetFatPercent)));
                    setBottomInfoTexts();
                    mRecalcLock = false;
                }
            }
        });

        mProteinInput = (EditText) v.findViewById(R.id.fragment_set_macros_edit_protein);
        mProteinInput.setText(String.valueOf(Math.round(mTargetProteinPercent)));
        mProteinInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsInputPercent && !mRecalcLock) {
                    if (mProteinInput.length() > 0) {
                        mTargetProteinPercent = Float.parseFloat(mProteinInput.getText().toString());
                    } else {
                        mTargetProteinPercent = 0f;
                    }
                    mTargetProtein = Math.round((mTargetCalories * mTargetProteinPercent / 400));
                    mProteinText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetProtein, 4, (mTargetProtein*4)));
                    setBottomInfoTexts();
                } else if (!mIsInputPercent && !mRecalcLock){
                    if (mProteinInput.length() > 0) {
                        mTargetProtein = Integer.parseInt(mProteinInput.getText().toString());
                    } else {
                        mTargetProtein = 0;
                    }
                    mTargetProteinPercent = (float) (mTargetProtein * 4) / mTargetCalories * 100;
                    mProteinText.setText(getString(R.string.set_macros_fragment_manual_macro_info, 4, (mTargetProtein * 4), Math.round(mTargetProteinPercent)));
                    setBottomInfoTexts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mCarbsInput = (EditText) v.findViewById(R.id.fragment_set_macros_edit_carbs);
        mCarbsInput.setText(String.valueOf(Math.round(mTargetCarbsPercent)));
        mCarbsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsInputPercent && !mRecalcLock) {
                    if (mCarbsInput.length() > 0) {
                        mTargetCarbsPercent = Float.parseFloat(mCarbsInput.getText().toString());
                    } else {
                        mTargetCarbsPercent = 0f;
                    }
                    mTargetCarbs = Math.round(mTargetCalories * mTargetCarbsPercent / 400);
                    mCarbsText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetCarbs, 4, (mTargetCarbs*4)));
                    setBottomInfoTexts();
                } else if (!mIsInputPercent && !mRecalcLock){
                    if (mCarbsInput.length() > 0) {
                        mTargetCarbs = Integer.parseInt(mCarbsInput.getText().toString());
                    } else {
                        mTargetCarbs = 0;
                    }
                    mTargetCarbsPercent = (float) (mTargetCarbs * 4) / mTargetCalories * 100;
                    mCarbsText.setText(getString(R.string.set_macros_fragment_manual_macro_info, 4, (mTargetCarbs * 4), Math.round(mTargetCarbsPercent)));
                    setBottomInfoTexts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mFatInput = (EditText) v.findViewById(R.id.fragment_set_macros_edit_fat);
        mFatInput.setText(String.valueOf(Math.round(mTargetFatPercent)));
        mFatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsInputPercent && !mRecalcLock) {
                    if (mFatInput.length() > 0) {
                        mTargetFatPercent = Float.parseFloat(mFatInput.getText().toString());
                    } else {
                        mTargetFatPercent = 0f;
                    }
                    mTargetFat = Math.round(mTargetCalories * mTargetFatPercent / 900);
                    mFatText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetFat, 9, (mTargetFat*9)));
                    setBottomInfoTexts();
                } else if (!mIsInputPercent && !mRecalcLock){
                    if (mFatInput.length() > 0) {
                        mTargetFat = Integer.parseInt(mFatInput.getText().toString());
                    } else {
                        mTargetFat = 0;
                    }
                    mTargetFatPercent = (float) (mTargetFat * 9) / mTargetCalories * 100;
                    mFatText.setText(getString(R.string.set_macros_fragment_manual_macro_info, 9, (mTargetFat * 9), Math.round(mTargetFatPercent)));
                    setBottomInfoTexts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mProteinText = (TextView) v.findViewById(R.id.fragment_set_macros_info_protein);
        mProteinText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetProtein, 4, (mTargetProtein*4)));

        mCarbsText = (TextView) v.findViewById(R.id.fragment_set_macros_info_carbs);
        mCarbsText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetCarbs, 4, (mTargetCarbs*4)));

        mFatText = (TextView) v.findViewById(R.id.fragment_set_macros_info_fat);
        mFatText.setText(getString(R.string.set_macros_fragment_percent_macro_info, mTargetFat, 9, (mTargetFat*9)));


        mBottomInfoText = (TextView) v.findViewById(R.id.fragment_set_macros_warning_text);
        mBottomInfoText.setText(getString(R.string.set_macros_fragment_percent_bottom_info, Math.round(mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent)));

        mBottomInfoText2 = (TextView) v.findViewById(R.id.fragment_set_macros_warning_text2);
        mBottomInfoText2.setText(getString(R.string.set_macros_fragment_percent_bottom_info2));

        mSaveButton = (Button) v.findViewById(R.id.fragment_set_macros_save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreferences.edit().putString("pref_protein", String.valueOf(mTargetProtein)).apply();
                mPreferences.edit().putString("pref_carbs", String.valueOf(mTargetCarbs)).apply();
                mPreferences.edit().putString("pref_fat", String.valueOf(mTargetFat)).apply();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        mCancelButton = (Button) v.findViewById(R.id.fragment_set_macros_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        setBottomInfoTexts();

        mRecalcLock = false;


        return v;
    }

    private void setBottomInfoTexts() {
        if (mIsInputPercent) {
            mBottomInfoText.setText(getString(R.string.set_macros_fragment_percent_bottom_info, Math.round(mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent)));
            mBottomInfoText2.setText(getString(R.string.set_macros_fragment_percent_bottom_info2));
            if (Math.round(mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent) == 100) {
                mBottomInfoText.setTextColor(getResources().getColor(R.color.slightly_darker_green));
                mBottomInfoText2.setVisibility(TextView.INVISIBLE);
            } else {
                mBottomInfoText.setTextColor(getResources().getColor(R.color.red));
                mBottomInfoText2.setVisibility(TextView.VISIBLE);
            }
        } else {
            mBottomInfoText.setText(getString(R.string.set_macros_fragment_manual_bottom_info, (4*mTargetProtein + 4*mTargetCarbs + 9*mTargetFat)));
            mBottomInfoText2.setText(getString(R.string.set_macros_fragment_manual_bottom_info2, mTargetCalories));
            if (mTargetCalories - mTargetProtein*4 - mTargetCarbs*4 - mTargetFat*9 <= 100 && mTargetCalories - mTargetProtein*4 - mTargetCarbs*4 - mTargetFat*9 >= -100) {
                mBottomInfoText.setTextColor(getResources().getColor(R.color.slightly_darker_green));
                mBottomInfoText2.setVisibility(TextView.INVISIBLE);
            } else {
                mBottomInfoText.setTextColor(getResources().getColor(R.color.red));
                mBottomInfoText2.setVisibility(TextView.VISIBLE);
            }
        }
    }

}