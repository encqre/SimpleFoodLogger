package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.untrustworthypillars.simplefoodlogger.reusable.DecimalDigitsInputFilter;

import java.util.Date;
import java.util.UUID;

public class AddLogFragment extends Fragment {

    private static final String ARG_FOOD = "food";
    private static final String ARG_TYPE = "type";
    private static final String ARG_DATE = "date";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Food mFood;
    private Date mDate;
    private FoodManager fm = FoodManager.get(getContext());

    private Button mDateButton;
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
    private TextView mFoodTitle;
    private Button mAddButton;
    private Button mCancelButton;

    private SharedPreferences mPreferences;
    private String mUnits;

    public static AddLogFragment newInstance (UUID foodId, int foodType, Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, foodId);
        args.putInt(ARG_TYPE, foodType);
        args.putSerializable(ARG_DATE, date);

        AddLogFragment fragment = new AddLogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_log, container, false);

        UUID uuid = (UUID) getArguments().getSerializable(ARG_FOOD);
        mDate = (Date) getArguments().getSerializable(ARG_DATE);

        if (getArguments().getInt(ARG_TYPE) == 0) {
            mFood = fm.getCustomFood(uuid);
        } else if (getArguments().getInt(ARG_TYPE) == 1) {
            mFood = fm.getCommonFood(uuid);
        } else if (getArguments().getInt(ARG_TYPE) == 2) {
            mFood = fm.getExtendedFood(uuid);
        }

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUnits = mPreferences.getString("pref_units", "Metric");

        mDateButton = (Button) v.findViewById(R.id.fragment_add_log_date_button);
        mDateButton.setText(Calculations.dateDisplayString(mDate));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerDialog dialog = DatePickerDialog.newInstance(mDate);
                dialog.setTargetFragment(AddLogFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mCalories = (TextView) v.findViewById(R.id.fragment_add_log_calories);
        mCalories.setText(String.format("%.1f",(mFood.getKcal())) + " kcal");

        mProtein = (TextView) v.findViewById(R.id.fragment_add_log_protein);
        mProtein.setText(String.format("%.1f",(mFood.getProtein())) + "g");

        mCarbs = (TextView) v.findViewById(R.id.fragment_add_log_carbs);
        mCarbs.setText(String.format("%.1f",(mFood.getCarbs())) + "g");

        mFat = (TextView) v.findViewById(R.id.fragment_add_log_fat);
        mFat.setText(String.format("%.1f",(mFood.getFat())) + "g");

        mWeight = (EditText) v.findViewById(R.id.fragment_add_log_weight);
        mWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mWeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
//        mWeight.requestFocus();

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

        mServingGroup = (RadioGroup) v.findViewById(R.id.fragment_add_log_serving_radio_group);
        mServing1 = (RadioButton) v.findViewById(R.id.fragment_add_log_serving1_radio_button);
        mServing2 = (RadioButton) v.findViewById(R.id.fragment_add_log_serving2_radio_button);
        mServing3 = (RadioButton) v.findViewById(R.id.fragment_add_log_serving3_radio_button);

        if (mUnits.equals("Metric")) {
            mServing1.setText(mFood.getPortion1Name() + " (" + String.format("%.1f", mFood.getPortion1SizeMetric()) + "\u00A0g)");
            mServing1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(mFood.getPortion1SizeMetric().toString());
                }
            });
            mServing2.setText(mFood.getPortion2Name() + " (" + String.format("%.1f", mFood.getPortion2SizeMetric()) + "\u00A0g)");
            mServing2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(mFood.getPortion2SizeMetric().toString());
                }
            });
            mServing3.setText(mFood.getPortion3Name() + " (" + String.format("%.1f", mFood.getPortion3SizeMetric()) + "\u00A0g)");
            mServing3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(mFood.getPortion3SizeMetric().toString());
                }
            });

        } else {
            mServing1.setText(mFood.getPortion1Name() + " (" + String.format("%.1f", mFood.getPortion1SizeImperial()) + "\u00A0oz)");
            mServing1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(String.format("%.1f", mFood.getPortion1SizeImperial()));
                }
            });
            mServing2.setText(mFood.getPortion2Name() + " (" + String.format("%.1f", mFood.getPortion2SizeImperial()) + "\u00A0oz)");
            mServing2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(String.format("%.1f", mFood.getPortion2SizeImperial()));
                }
            });
            mServing3.setText(mFood.getPortion3Name() + " (" + String.format("%.1f", mFood.getPortion3SizeImperial()) + "\u00A0oz)");
            mServing3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeight.setText(String.format("%.1f", mFood.getPortion3SizeImperial()));
                }
            });
            mWeightTextView = (TextView) v.findViewById(R.id.fragment_add_log_weight_textview);
            mWeightTextView.setText(getString(R.string.dialog_add_log_weight_textview_imperial));
            mWeight.setHint("3.5");
        }

        mFoodTitle = (TextView) v.findViewById(R.id.fragment_add_log_food_title);
        mFoodTitle.setText(mFood.getTitle());

        mAddButton = (Button) v.findViewById(R.id.fragment_add_log_add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        mCancelButton = (Button) v.findViewById(R.id.fragment_add_log_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        return v;
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
}
