package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.untrustworthypillars.simplefoodlogger.formatting.ScrollableDialogTitle;

import java.util.Date;
import java.util.UUID;

public class AddLogDialog extends DialogFragment {

    private static final String ARG_FOOD = "food";
    private static final String ARG_TYPE = "type";
    private static final String ARG_DATE = "date";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Food mFood;
    private Date mDate;
    private FoodManager fm = FoodManager.get(getContext());

    private TextView mDateButton;
    private EditText mWeight;
    private TextView mCalories;
    private TextView mProtein;
    private TextView mCarbs;
    private TextView mFat;
    private RadioGroup mServingGroup;
    private RadioButton mServing1;
    private RadioButton mServing2;
    private RadioButton mServing3;
    private TextView mWeightTextView;

    private SharedPreferences mPreferences;
    private String mUnits;

    public static AddLogDialog newInstance (UUID foodid, int foodType, Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, foodid);
        args.putInt(ARG_TYPE, foodType);
        args.putSerializable(ARG_DATE, date);

        AddLogDialog fragment = new AddLogDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        UUID uuid = (UUID) getArguments().getSerializable(ARG_FOOD);
        mDate = (Date) getArguments().getSerializable(ARG_DATE);

        if (getArguments().getInt(ARG_TYPE) == 0) {
            mFood = fm.getCustomFood(uuid);
        } else if (getArguments().getInt(ARG_TYPE) == 1) {
            mFood = fm.getCommonFood(uuid);
        } else if (getArguments().getInt(ARG_TYPE) == 2) {
            mFood = fm.getExtendedFood(uuid);
        }

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_log, null);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUnits = mPreferences.getString("pref_units", "Metric");

        mDateButton = (TextView) v.findViewById(R.id.dialog_add_log_date_button);
        mDateButton.setPaintFlags(mDateButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mDateButton.setText(Calculations.dateDisplayString(mDate));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerDialog dialog = DatePickerDialog.newInstance(mDate);
                dialog.setTargetFragment(AddLogDialog.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mCalories = (TextView) v.findViewById(R.id.dialog_add_log_calories);
        mCalories.setText(String.format("%.1f",(mFood.getKcal())) + " kcal");

        mProtein = (TextView) v.findViewById(R.id.dialog_add_log_protein);
        mProtein.setText(String.format("%.1f",(mFood.getProtein())) + "g");

        mCarbs = (TextView) v.findViewById(R.id.dialog_add_log_carbs);
        mCarbs.setText(String.format("%.1f",(mFood.getCarbs())) + "g");

        mFat = (TextView) v.findViewById(R.id.dialog_add_log_fat);
        mFat.setText(String.format("%.1f",(mFood.getFat())) + "g");

        mWeight = (EditText) v.findViewById(R.id.dialog_add_log_weight);
        mWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mWeight.requestFocus();
        showKeyboard();
        mWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Float weight = 0f;
                if (mWeight.length() > 0) {
                    weight = Float.parseFloat(mWeight.getText().toString());
                }
                if (mUnits.equals("Imperial")) {
                    weight = weight*28.35f;
                }
                mCalories.setText(String.format("%.1f",(mFood.getKcal() * weight/100)) + " kcal");
                mProtein.setText(String.format("%.1f",(mFood.getProtein() * weight/100)) + "g");
                mCarbs.setText(String.format("%.1f", (mFood.getCarbs() * weight/100)) + "g");
                mFat.setText(String.format("%.1f", (mFood.getFat() * weight/100)) + "g");
            }

            @Override
            public void afterTextChanged(Editable s) {
            //nothing
            }
        });

        mServingGroup = (RadioGroup) v.findViewById(R.id.dialog_add_log_serving_radio_group);
        mServing1 = (RadioButton) v.findViewById(R.id.dialog_add_log_serving1_radio_button);
        mServing2 = (RadioButton) v.findViewById(R.id.dialog_add_log_serving2_radio_button);
        mServing3 = (RadioButton) v.findViewById(R.id.dialog_add_log_serving3_radio_button);

        if (mUnits.equals("Metric")) {
            mServing1.setText(mFood.getPortion1Name() + " (" + String.format("%.1f", mFood.getPortion1SizeMetric()) + "g)");
            mServing1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(mFood.getPortion1SizeMetric().toString());
                }
            });
            mServing2.setText(mFood.getPortion2Name() + " (" + String.format("%.1f", mFood.getPortion2SizeMetric()) + "g)");
            mServing2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(mFood.getPortion2SizeMetric().toString());
                }
            });
            mServing3.setText(mFood.getPortion3Name() + " (" + String.format("%.1f", mFood.getPortion3SizeMetric()) + "g)");
            mServing3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(mFood.getPortion3SizeMetric().toString());
                }
            });

        } else {
            mServing1.setText(mFood.getPortion1Name() + " (" + String.format("%.1f", mFood.getPortion1SizeImperial()) + " oz)");
            mServing1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(String.format("%.1f", mFood.getPortion1SizeImperial()));
                }
            });
            mServing2.setText(mFood.getPortion2Name() + " (" + String.format("%.1f", mFood.getPortion2SizeImperial()) + " oz)");
            mServing2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(String.format("%.1f", mFood.getPortion2SizeImperial()));
                }
            });
            mServing3.setText(mFood.getPortion3Name() + " (" + String.format("%.1f", mFood.getPortion3SizeImperial()) + " oz)");
            mServing3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(String.format("%.1f", mFood.getPortion3SizeImperial()));
                }
            });
            mWeightTextView = (TextView) v.findViewById(R.id.dialog_add_log_weight_textview);
            mWeightTextView.setText(getString(R.string.dialog_add_log_weight_textview_imperial));
            mWeight.setHint("3.5");
        }


        TextView mScrollableTitle = new ScrollableDialogTitle(getContext(), mFood.getTitle()).ScrollableTitle;


        return new AlertDialog.Builder(getActivity()).setView(v).setCustomTitle(mScrollableTitle)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mWeight.getText().toString().equals("")) {
                            if (mUnits.equals("Imperial")) {
                                mWeight.setText("3.5");
                            } else {
                                mWeight.setText("100");
                            }
                        }
                        Float weight = Float.parseFloat(mWeight.getText().toString());
                        if (mUnits.equals("Imperial")) {
                            weight = weight*28.35f;
                        }
                        Log log = new Log();
                        log.setDate(mDate);
                        log.setDateText();
                        log.setFood(mFood.getTitle());
                        log.setSize(weight);
                        log.setSizeImperial(weight/28.35f);
                        log.setKcal(mFood.getKcal() * weight / 100 );
                        log.setProtein(mFood.getProtein() * weight / 100);
                        log.setCarbs(mFood.getCarbs() * weight / 100 );
                        log.setFat(mFood.getFat() * weight / 100 );
                        LogManager.get(getActivity()).addLog(log);
                        FoodManager.get(getActivity()).addToRecentFoods(mFood);
                        Toast.makeText(getActivity(), "Meal logged!", Toast.LENGTH_SHORT).show();
                        sendResult(Activity.RESULT_OK);
                        closeKeyboard();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeKeyboard();
                    }
                })
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            mDate = (Date) data.getSerializableExtra(DatePickerDialog.EXTRA_DATE);
            mDateButton.setText(Calculations.dateDisplayString(mDate));
        }
    }

    /***
     * Used to show keyboard automatically once dialog opens up
     */
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void closeKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
