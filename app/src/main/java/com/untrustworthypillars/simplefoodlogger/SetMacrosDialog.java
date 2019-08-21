package com.untrustworthypillars.simplefoodlogger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import org.w3c.dom.Text;

public class SetMacrosDialog extends DialogFragment {

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

    private int mTargetCalories;
    private int mTargetProtein;
    private int mTargetCarbs;
    private int mTargetFat;
    private float mTargetProteinPercent;
    private float mTargetCarbsPercent;
    private float mTargetFatPercent;

    private boolean mIsInputPercent = true;

    private SharedPreferences mPreferences;

    public static SetMacrosDialog newInstance () {
        Bundle args = new Bundle();

        SetMacrosDialog fragment = new SetMacrosDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_set_macros, null);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTargetCalories = Integer.parseInt(mPreferences.getString("pref_calories", "2500"));
        mTargetProtein = Integer.parseInt(mPreferences.getString("pref_protein", "200"));
        mTargetCarbs = Integer.parseInt(mPreferences.getString("pref_carbs", "300"));
        mTargetFat = Integer.parseInt(mPreferences.getString("pref_fat", "90"));

        mTargetProteinPercent = (float) (mTargetProtein * 4) / mTargetCalories * 100;
        mTargetCarbsPercent = (float) (mTargetCarbs * 4) / mTargetCalories * 100;
        mTargetFatPercent = (float) (mTargetFat * 9) / mTargetCalories * 100;


        mCurrentCalorieTargetTextView = (TextView) v.findViewById(R.id.dialog_set_macros_calories_target);
        mCurrentCalorieTargetTextView.setText(String.valueOf(mTargetCalories));

        mInputRadioGroup = (RadioGroup) v.findViewById(R.id.dialog_set_macros_radio_group);
        mRadioPercentage = (RadioButton) v.findViewById(R.id.dialog_set_macros_radio_percent);
        mRadioPercentage.setChecked(true);
        mRadioPercentage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsInputPercent = true;
                    mProteinInput.setText(String.valueOf((int) mTargetProteinPercent));
                    mCarbsInput.setText(String.valueOf((int) mTargetCarbsPercent));
                    mFatInput.setText(String.valueOf((int) mTargetFatPercent));
                    mProteinText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetProtein, 4, (mTargetProtein*4)));
                    mCarbsText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetCarbs, 4, (mTargetCarbs*4)));
                    mFatText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetFat, 9, (mTargetFat*9)));
                    mBottomInfoText.setText(getString(R.string.set_macros_dialog_percent_bottom_info, (int) (mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent)));
                }
            }
        });

        mRadioManual = (RadioButton) v.findViewById(R.id.dialog_set_macros_radio_manual);
        mRadioManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsInputPercent = false;
                    mProteinInput.setText(String.valueOf(mTargetProtein));
                    mCarbsInput.setText(String.valueOf(mTargetCarbs));
                    mFatInput.setText(String.valueOf(mTargetFat));
                    mProteinText.setText(getString(R.string.set_macros_dialog_manual_macro_info, 4, (mTargetProtein * 4), (int) mTargetProteinPercent));
                    mCarbsText.setText(getString(R.string.set_macros_dialog_manual_macro_info, 4, (mTargetCarbs * 4), (int) mTargetCarbsPercent));
                    mFatText.setText(getString(R.string.set_macros_dialog_manual_macro_info, 9, (mTargetFat * 9), (int) mTargetFatPercent));
                    mBottomInfoText.setText(getString(R.string.set_macros_dialog_manual_bottom_info, (4*mTargetProtein + 4*mTargetCarbs + 9*mTargetFat), mTargetCalories));
                }
            }
        });

        mProteinInput = (EditText) v.findViewById(R.id.dialog_set_macros_edit_protein);
        mProteinInput.setText(String.valueOf((int) mTargetProteinPercent));
        mProteinInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsInputPercent) {
                    if (mProteinInput.length() > 0) {
                        mTargetProteinPercent = Float.parseFloat(mProteinInput.getText().toString());
                    } else {
                        mTargetProteinPercent = 0f;
                    }
                    mTargetProtein = (int) (mTargetCalories * mTargetProteinPercent / 400);
                    mProteinText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetProtein, 4, (mTargetProtein*4)));
                    mBottomInfoText.setText(getString(R.string.set_macros_dialog_percent_bottom_info, (int) (mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent)));
                } else {
                    if (mProteinInput.length() > 0) {
                        mTargetProtein = Integer.parseInt(mProteinInput.getText().toString());
                    } else {
                        mTargetProtein = 0;
                    }
                    mTargetProteinPercent = (float) (mTargetProtein * 4) / mTargetCalories * 100;
                    mProteinText.setText(getString(R.string.set_macros_dialog_manual_macro_info, 4, (mTargetProtein * 4), (int) mTargetProteinPercent));
                    mBottomInfoText.setText(getString(R.string.set_macros_dialog_manual_bottom_info, (4*mTargetProtein + 4*mTargetCarbs + 9*mTargetFat), mTargetCalories));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mCarbsInput = (EditText) v.findViewById(R.id.dialog_set_macros_edit_carbs);
        mCarbsInput.setText(String.valueOf((int) mTargetCarbsPercent));
        mCarbsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsInputPercent) {
                    if (mCarbsInput.length() > 0) {
                        mTargetCarbsPercent = Float.parseFloat(mCarbsInput.getText().toString());
                    } else {
                        mTargetCarbsPercent = 0f;
                    }
                    mTargetCarbs = (int) (mTargetCalories * mTargetCarbsPercent / 400);
                    mCarbsText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetCarbs, 4, (mTargetCarbs*4)));
                    mBottomInfoText.setText(getString(R.string.set_macros_dialog_percent_bottom_info, (int) (mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent)));
                } else {
                    if (mCarbsInput.length() > 0) {
                        mTargetCarbs = Integer.parseInt(mCarbsInput.getText().toString());
                    } else {
                        mTargetCarbs = 0;
                    }
                    mTargetCarbsPercent = (float) (mTargetCarbs * 4) / mTargetCalories * 100;
                    mCarbsText.setText(getString(R.string.set_macros_dialog_manual_macro_info, 4, (mTargetCarbs * 4), (int) mTargetCarbsPercent));
                    mBottomInfoText.setText(getString(R.string.set_macros_dialog_manual_bottom_info, (4*mTargetProtein + 4*mTargetCarbs + 9*mTargetFat), mTargetCalories));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mFatInput = (EditText) v.findViewById(R.id.dialog_set_macros_edit_fat);
        mFatInput.setText(String.valueOf((int) mTargetFatPercent));
        mFatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mIsInputPercent) {
                    if (mFatInput.length() > 0) {
                        mTargetFatPercent = Float.parseFloat(mFatInput.getText().toString());
                    } else {
                        mTargetFatPercent = 0f;
                    }
                    mTargetFat = (int) (mTargetCalories * mTargetFatPercent / 900);
                    mFatText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetFat, 9, (mTargetFat*9)));
                    mBottomInfoText.setText(getString(R.string.set_macros_dialog_percent_bottom_info, (int) (mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent)));
                } else {
                    if (mFatInput.length() > 0) {
                        mTargetFat = Integer.parseInt(mFatInput.getText().toString());
                    } else {
                        mTargetFat = 0;
                    }
                    mTargetFatPercent = (float) (mTargetFat * 9) / mTargetCalories * 100;
                    mFatText.setText(getString(R.string.set_macros_dialog_manual_macro_info, 9, (mTargetFat * 9), (int) mTargetFatPercent));
                    mBottomInfoText.setText(getString(R.string.set_macros_dialog_manual_bottom_info, (4*mTargetProtein + 4*mTargetCarbs + 9*mTargetFat), mTargetCalories));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        mProteinText = (TextView) v.findViewById(R.id.dialog_set_macros_info_protein);
        mProteinText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetProtein, 4, (mTargetProtein*4)));

        mCarbsText = (TextView) v.findViewById(R.id.dialog_set_macros_info_carbs);
        mCarbsText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetCarbs, 4, (mTargetCarbs*4)));

        mFatText = (TextView) v.findViewById(R.id.dialog_set_macros_info_fat);
        mFatText.setText(getString(R.string.set_macros_dialog_percent_macro_info, mTargetFat, 9, (mTargetFat*9)));

        mBottomInfoText = (TextView) v.findViewById(R.id.dialog_set_macros_warning_text);
        mBottomInfoText.setText(getString(R.string.set_macros_dialog_percent_bottom_info, (int) (mTargetProteinPercent + mTargetCarbsPercent + mTargetFatPercent)));


        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Set Macronutrient Targets")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();
    }
}

//TODO maybe add a question mark somewhere inside dialog, that would explain what is this, and what values are recommended.
//TODO fix keyboard hiding stuff here too