package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.untrustworthypillars.simplefoodlogger.reusable.EditTextWithSuffix;

//TODO maybe add a question mark somewhere inside form, that would explain what is this, and what values are recommended.
//TODO need to write a function to evaluate better recommended ratios if profile was setup

public class SetMacrosFragment extends Fragment {

    private SharedPreferences mPreferences;

    private int mTargetCalories;
    private int mTargetProtein;
    private int mTargetProteinPercent;
    private int mTargetProteinPercentRecommended;
    private int mTargetCarbs;
    private int mTargetCarbsPercent;
    private int mTargetCarbsPercentRecommended;
    private int mTargetFat;
    private int mTargetFatPercent;
    private int mTargetFatPercentRecommended;

    private RadioGroup mInputRadioGroup;
    private RadioButton mRadioRecommended;
    private RadioButton mRadioPercentage;
    private EditTextWithSuffix mProteinInputPercent;
    private EditTextWithSuffix mCarbsInputPercent;
    private EditTextWithSuffix mFatInputPercent;
    private TextView mProteinWeightTextview;
    private TextView mCarbsWeightTextview;
    private TextView mFatWeightTextview;
    private TextView mBottomInfoText;
    private Button mSaveButton;
    private Button mCancelButton;

    private Drawable defaultEditTextBackgroundDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_macros, container, false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTargetCalories = Integer.parseInt(mPreferences.getString("pref_calories", "1999"));
        mTargetProteinPercent = Integer.parseInt(mPreferences.getString("pref_protein", mPreferences.getString("pref_protein_recommended", "25")));
        mTargetCarbsPercent = Integer.parseInt(mPreferences.getString("pref_carbs", mPreferences.getString("pref_carbs_recommended", "45")));
        mTargetFatPercent = Integer.parseInt(mPreferences.getString("pref_fat", mPreferences.getString("pref_fat_recommended", "30")));

        mTargetProteinPercentRecommended = Integer.parseInt(mPreferences.getString("pref_protein_recommended", "25"));
        mTargetCarbsPercentRecommended = Integer.parseInt(mPreferences.getString("pref_carbs_recommended", "45"));
        mTargetFatPercentRecommended = Integer.parseInt(mPreferences.getString("pref_fat_recommended", "30"));

        mTargetProtein = Math.round(mTargetCalories * mTargetProteinPercent / 400f);
        mTargetCarbs = Math.round(mTargetCalories * mTargetCarbsPercent / 400f);
        mTargetFat = Math.round(mTargetCalories * mTargetFatPercent / 900f);

        mInputRadioGroup = (RadioGroup) v.findViewById(R.id.fragment_set_macros_radio_group);
        mInputRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.fragment_set_macros_radio_recommended:
                        mProteinInputPercent.setText(String.valueOf(mTargetProteinPercentRecommended));
                        mCarbsInputPercent.setText(String.valueOf(mTargetCarbsPercentRecommended));
                        mFatInputPercent.setText(String.valueOf(mTargetFatPercentRecommended));
                        mProteinInputPercent.setBackgroundResource(android.R.color.transparent);
                        mCarbsInputPercent.setBackgroundResource(android.R.color.transparent);
                        mFatInputPercent.setBackgroundResource(android.R.color.transparent);
                        mProteinInputPercent.setInputType(InputType.TYPE_NULL);
                        mCarbsInputPercent.setInputType(InputType.TYPE_NULL);
                        mFatInputPercent.setInputType(InputType.TYPE_NULL);
                        mBottomInfoText.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.fragment_set_macros_radio_percent:
                        mProteinInputPercent.setText(String.valueOf(mTargetProteinPercent));
                        mCarbsInputPercent.setText(String.valueOf(mTargetCarbsPercent));
                        mFatInputPercent.setText(String.valueOf(mTargetFatPercent));
                        mProteinInputPercent.setBackground(defaultEditTextBackgroundDrawable);
                        mCarbsInputPercent.setBackground(defaultEditTextBackgroundDrawable);
                        mFatInputPercent.setBackground(defaultEditTextBackgroundDrawable);
                        mProteinInputPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mCarbsInputPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mFatInputPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mBottomInfoText.setVisibility(View.VISIBLE);
                        setBottomInfoTexts();
                        break;
                    default:
                        break;
                }
            }
        });

        mRadioPercentage = (RadioButton) v.findViewById(R.id.fragment_set_macros_radio_percent);
        mRadioRecommended = (RadioButton) v.findViewById(R.id.fragment_set_macros_radio_recommended);

        mProteinInputPercent = (EditTextWithSuffix) v.findViewById(R.id.fragment_set_macros_edit_protein_percent);
        mProteinInputPercent.setText(String.valueOf(mTargetProteinPercent));
        defaultEditTextBackgroundDrawable = mProteinInputPercent.getBackground();

        mProteinInputPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mRadioRecommended.isChecked()) {
                    mTargetProtein = Math.round((mTargetCalories * mTargetProteinPercentRecommended / 400));
                    mProteinWeightTextview.setText(mTargetProtein + "g");
                } else {
                    if (mProteinInputPercent.length() > 0) {
                        mTargetProteinPercent = Integer.parseInt(mProteinInputPercent.getText().toString());
                    } else {
                        mTargetProteinPercent = 0;
                    }
                    mTargetProtein = Math.round((mTargetCalories * mTargetProteinPercent / 400));
                    mProteinWeightTextview.setText(mTargetProtein + "g");
                    setBottomInfoTexts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mCarbsInputPercent = (EditTextWithSuffix) v.findViewById(R.id.fragment_set_macros_edit_carbs_percent);
        mCarbsInputPercent.setText(String.valueOf(mTargetCarbsPercent));
        mCarbsInputPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mRadioRecommended.isChecked()) {
                    mTargetCarbs = Math.round((mTargetCalories * mTargetCarbsPercentRecommended / 400));
                    mCarbsWeightTextview.setText(mTargetCarbs + "g");
                } else {
                    if (mCarbsInputPercent.length() > 0) {
                        mTargetCarbsPercent = Integer.parseInt(mCarbsInputPercent.getText().toString());
                    } else {
                        mTargetCarbsPercent = 0;
                    }
                    mTargetCarbs = Math.round((mTargetCalories * mTargetCarbsPercent / 400));
                    mCarbsWeightTextview.setText(mTargetCarbs + "g");
                    setBottomInfoTexts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mFatInputPercent = (EditTextWithSuffix) v.findViewById(R.id.fragment_set_macros_edit_fat_percent);
        mFatInputPercent.setText(String.valueOf(mTargetFatPercent));
        mFatInputPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mRadioRecommended.isChecked()) {
                    mTargetFat = Math.round((mTargetCalories * mTargetFatPercentRecommended / 900));
                    mFatWeightTextview.setText(mTargetFat + "g");
                } else {
                    if (mFatInputPercent.length() > 0) {
                        mTargetFatPercent = Integer.parseInt(mFatInputPercent.getText().toString());
                    } else {
                        mTargetFatPercent = 0;
                    }
                    mTargetFat = Math.round((mTargetCalories * mTargetFatPercent / 900));
                    mFatWeightTextview.setText(mTargetFat + "g");
                    setBottomInfoTexts();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mProteinWeightTextview = (TextView) v.findViewById(R.id.fragment_set_macros_protein_weight);
        mProteinWeightTextview.setText(mTargetProtein + "g");

        mCarbsWeightTextview = (TextView) v.findViewById(R.id.fragment_set_macros_edit_carbs_weight);
        mCarbsWeightTextview.setText(mTargetCarbs + "g");

        mFatWeightTextview = (TextView) v.findViewById(R.id.fragment_set_macros_edit_fat_weight);
        mFatWeightTextview.setText(mTargetFat + "g");


        mBottomInfoText = (TextView) v.findViewById(R.id.fragment_set_macros_warning_text);

        //If currently used macros ratio match recommended ratio (or are not set yet), show recommended values option at first
        if (mTargetProteinPercent == mTargetProteinPercentRecommended && mTargetCarbsPercent == mTargetCarbsPercentRecommended && mTargetFatPercent == mTargetFatPercentRecommended) {
            mRadioRecommended.setChecked(true);
        } else {
            mRadioPercentage.setChecked(true);
        }

        mSaveButton = (Button) v.findViewById(R.id.fragment_set_macros_save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(mProteinInputPercent.getText().toString()) +
                        Integer.parseInt(mCarbsInputPercent.getText().toString()) +
                        Integer.parseInt(mFatInputPercent.getText().toString())!= 100) {
                    Toast.makeText(getActivity(), "Percentages must add up to 100%!", Toast.LENGTH_SHORT).show();
                } else {
                    mPreferences.edit().putString("pref_protein", mProteinInputPercent.getText().toString()).apply();
                    mPreferences.edit().putString("pref_carbs", mCarbsInputPercent.getText().toString()).apply();
                    mPreferences.edit().putString("pref_fat", mFatInputPercent.getText().toString()).apply();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
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

        return v;
    }

    private void setBottomInfoTexts() {
        mBottomInfoText.setText(getString(R.string.set_macros_fragment_percent_bottom_info, mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent));
        if (Math.round(mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent) == 100) {
            mBottomInfoText.setTextColor(getResources().getColor(R.color.slightly_darker_green));
        } else {
            mBottomInfoText.setTextColor(getResources().getColor(R.color.red));
        }
    }
}
