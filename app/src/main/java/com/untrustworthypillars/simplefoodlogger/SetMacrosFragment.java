package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageItemInfo;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.untrustworthypillars.simplefoodlogger.reusable.EditTextWithSuffix;

//TODO maybe add a question mark somewhere inside form, that would explain what is this, and what values are recommended.

public class SetMacrosFragment extends Fragment {

    private static final String ARG_IN_SETUP = "are we in setup?";

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
    private TextView setMacrosTitle;
    private ScrollView scrollView;

    private boolean viewCreateCompleted = false;
    private boolean inSetup;

    public static SetMacrosFragment newInstance (boolean inSetup) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IN_SETUP, inSetup);

        SetMacrosFragment fragment = new SetMacrosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_macros, container, false);

        inSetup = (boolean) getArguments().getSerializable(ARG_IN_SETUP);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTargetCalories = Integer.parseInt(mPreferences.getString("pref_calories", "1999"));

        setRecommendedMacros();

        mTargetProteinPercent = Integer.parseInt(mPreferences.getString("pref_protein", String.valueOf(mTargetProteinPercentRecommended)));
        mTargetCarbsPercent = Integer.parseInt(mPreferences.getString("pref_carbs", String.valueOf(mTargetCarbsPercentRecommended)));
        mTargetFatPercent = Integer.parseInt(mPreferences.getString("pref_fat", String.valueOf(mTargetFatPercentRecommended)));

        mTargetProtein = Math.round(mTargetCalories * mTargetProteinPercent / 400f);
        mTargetCarbs = Math.round(mTargetCalories * mTargetCarbsPercent / 400f);
        mTargetFat = Math.round(mTargetCalories * mTargetFatPercent / 900f);

        setMacrosTitle = (TextView) v.findViewById(R.id.fragment_set_macros_title);
        scrollView = (ScrollView) v.findViewById(R.id.fragment_set_macros_scrollview);

        mInputRadioGroup = (RadioGroup) v.findViewById(R.id.fragment_set_macros_radio_group);
        mInputRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.fragment_set_macros_radio_recommended:
                        mProteinInputPercent.setText(String.valueOf(mTargetProteinPercentRecommended));
                        mCarbsInputPercent.setText(String.valueOf(mTargetCarbsPercentRecommended));
                        mFatInputPercent.setText(String.valueOf(mTargetFatPercentRecommended));
                        mProteinInputPercent.setTextColor(getResources().getColor(R.color.Gray));
                        mCarbsInputPercent.setTextColor(getResources().getColor(R.color.Gray));
                        mFatInputPercent.setTextColor(getResources().getColor(R.color.Gray));
                        mProteinInputPercent.setInputType(InputType.TYPE_NULL);
                        mCarbsInputPercent.setInputType(InputType.TYPE_NULL);
                        mFatInputPercent.setInputType(InputType.TYPE_NULL);
                        mBottomInfoText.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.fragment_set_macros_radio_percent:
                        mProteinInputPercent.setText(String.valueOf(mTargetProteinPercent));
                        mCarbsInputPercent.setText(String.valueOf(mTargetCarbsPercent));
                        mFatInputPercent.setText(String.valueOf(mTargetFatPercent));
                        mProteinInputPercent.setTextColor(getResources().getColor(R.color.black));
                        mCarbsInputPercent.setTextColor(getResources().getColor(R.color.black));
                        mFatInputPercent.setTextColor(getResources().getColor(R.color.black));
                        mProteinInputPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mCarbsInputPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mFatInputPercent.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mBottomInfoText.setVisibility(View.VISIBLE);
                        setBottomInfoTexts();
                        if (viewCreateCompleted) {
                            mProteinInputPercent.requestFocus();
                        }
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
        mProteinInputPercent.setHint(String.valueOf(mTargetProteinPercentRecommended));
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

        mProteinInputPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout();
            }
        });

        mCarbsInputPercent = (EditTextWithSuffix) v.findViewById(R.id.fragment_set_macros_edit_carbs_percent);
        mCarbsInputPercent.setText(String.valueOf(mTargetCarbsPercent));
        mCarbsInputPercent.setHint(String.valueOf(mTargetCarbsPercentRecommended));
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

        mCarbsInputPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout();
            }
        });

        mCarbsInputPercent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mCarbsInputPercent.hasFocus()) {
                    scrollUpLayout();
                }
            }
        });

        mFatInputPercent = (EditTextWithSuffix) v.findViewById(R.id.fragment_set_macros_edit_fat_percent);
        mFatInputPercent.setText(String.valueOf(mTargetFatPercent));
        mFatInputPercent.setHint(String.valueOf(mTargetFatPercentRecommended));
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
        mFatInputPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout();
            }
        });
        mFatInputPercent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mFatInputPercent.hasFocus()) {
                    scrollUpLayout();
                }
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

        if (!inSetup){
            setMacrosTitle.setVisibility(View.GONE);
            mCancelButton.setText("Cancel");
        }

        viewCreateCompleted = true;

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

    //This function scrolls down to bottom after keyboard comes up so that all field for input are visible
    private void scrollUpLayout(){
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View lastChild = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int bottom = lastChild.getBottom() + scrollView.getPaddingBottom();
                int sy = scrollView.getScrollY();
                int sh = scrollView.getHeight();
                int delta = bottom - (sy + sh);
                scrollView.smoothScrollBy(0, delta);
            }
        }, 200);
    }

    private void setRecommendedMacros(){
        float proteinPerKiloLow = 1.43f;
        float proteinPerKiloMid = 1.82f;
        float proteinPerKiloHigh = 2.2f;
        float proteinPerKilo;
        String weight = mPreferences.getString("pref_weight", "");
        String targetCalories = mPreferences.getString("pref_calories", "1969");
        int activityLevel = Integer.parseInt(mPreferences.getString("pref_activity_level", "0"));

        if (weight.equals("") || Integer.parseInt(weight) == 0) {
            //If no bodyweight was entered, defaulting to recommended PFC percentages of 25/30/45
            mTargetProteinPercentRecommended = 25;
            mTargetCarbsPercentRecommended = 45;
            mTargetFatPercentRecommended = 30;
        } else {
            if (activityLevel == 0) {
                proteinPerKilo = proteinPerKiloLow;
            } else if (activityLevel == 1 || activityLevel == 2) {
                proteinPerKilo = proteinPerKiloMid;
            } else {
                proteinPerKilo = proteinPerKiloHigh;
            }
            mTargetProteinPercentRecommended = Math.round(Integer.parseInt(weight) * proteinPerKilo * 400f / Integer.parseInt(targetCalories));
            if (mTargetProteinPercentRecommended > 50) {
                mTargetProteinPercentRecommended = 50;
            }
            mTargetFatPercentRecommended = 30;
            mTargetCarbsPercentRecommended = 100 - mTargetFatPercentRecommended - mTargetProteinPercentRecommended;
        }
    }
}
