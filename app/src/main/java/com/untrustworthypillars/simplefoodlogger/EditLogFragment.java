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
import com.untrustworthypillars.simplefoodlogger.reusable.SimpleConfirmationDialog;

import java.util.Date;
import java.util.UUID;

public class EditLogFragment extends Fragment {

    private static final String ARG_LOG = "log";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_DELETE_LOG = 1;

    private Log mLog;
    private Food mFood;
    private Date mDate;
    private LogManager lm = LogManager.get(getContext());
    private FoodManager fm;
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
    private Button mSaveButton;
    private Button mCancelButton;
    private Button mDeleteButton;

    private SharedPreferences mPreferences;
    private String mUnits;

    public static EditLogFragment newInstance (UUID logId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOG, logId);

        EditLogFragment fragment = new EditLogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_log, container, false);

        UUID uuid = (UUID) getArguments().getSerializable(ARG_LOG);

        mLog = lm.getLog(uuid);
        mDate = mLog.getDate();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUnits = mPreferences.getString("pref_units", "Metric");

        mDateButton = (Button) v.findViewById(R.id.fragment_edit_log_date_button);
        mDateButton.setText(Calculations.dateDisplayString(mDate));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerDialog dialog = DatePickerDialog.newInstance(mDate);
                dialog.setTargetFragment(EditLogFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mCalories = (TextView) v.findViewById(R.id.fragment_edit_log_calories);
        mCalories.setText(String.format("%.1f", mLog.getKcal()) + " kcal");

        mProtein = (TextView) v.findViewById(R.id.fragment_edit_log_protein);
        mProtein.setText(String.format("%.1f", mLog.getProtein()) + "g");

        mCarbs = (TextView) v.findViewById(R.id.fragment_edit_log_carbs);
        mCarbs.setText(String.format("%.1f", mLog.getCarbs()) + "g");

        mFat = (TextView) v.findViewById(R.id.fragment_edit_log_fat);
        mFat.setText(String.format("%.1f", mLog.getFat()) + "g");

        mWeight = (EditText) v.findViewById(R.id.fragment_edit_log_weight);
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
                mCalories.setText(String.format("%.1f",(mLog.getKcal()/mLog.getSize() * weight)) + " kcal");
                mProtein.setText(String.format("%.1f",(mLog.getProtein()/mLog.getSize() * weight)) + "g");
                mCarbs.setText(String.format("%.1f", (mLog.getCarbs()/mLog.getSize() * weight)) + "g");
                mFat.setText(String.format("%.1f", (mLog.getFat()/mLog.getSize() * weight)) + "g");
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing
            }
        });

        /**Trying to get Food by food name in the log to set correct serving sizes. If not found,
         * will set portion sizes to defaults - 50/100/250g*/
        fm = FoodManager.get(getContext());
        mFood = fm.getFoodByName(mLog.getFood());

        mServingGroup = (RadioGroup) v.findViewById(R.id.fragment_edit_log_serving_radio_group);
        mServing1 = (RadioButton) v.findViewById(R.id.fragment_edit_log_serving1_radio_button);
        mServing2 = (RadioButton) v.findViewById(R.id.fragment_edit_log_serving2_radio_button);
        mServing3 = (RadioButton) v.findViewById(R.id.fragment_edit_log_serving3_radio_button);


        if (mUnits.equals("Metric")) {
            mWeight.setText(String.format("%.1f", mLog.getSize()));
            if (mFood != null) {
                mServing1.setText(mFood.getPortion1Name() + " (" + mFood.getPortion1SizeMetric().toString() + "\u00A0g)");
                mServing1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText(mFood.getPortion1SizeMetric().toString());
                    }
                });
                mServing2.setText(mFood.getPortion2Name() + " (" + mFood.getPortion2SizeMetric().toString() + "\u00A0g)");
                mServing2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText(mFood.getPortion2SizeMetric().toString());
                    }
                });
                mServing3.setText(mFood.getPortion3Name() + " (" + mFood.getPortion3SizeMetric().toString() + "\u00A0g)");
                mServing3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText(mFood.getPortion3SizeMetric().toString());
                    }
                });

                /**If food weight in the log matches one of the portion sizes, set its radio button as checked*/
                if (mLog.getSize().equals(mFood.getPortion1SizeMetric())) {
                    mServing1.setChecked(true);
                } else if (mLog.getSize().equals(mFood.getPortion2SizeMetric())) {
                    mServing2.setChecked(true);
                } else if (mLog.getSize().equals(mFood.getPortion3SizeMetric())) {
                    mServing3.setChecked(true);
                }
            } else {
                mServing1.setText("Small (50.0\u00A0g)");
                mServing1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText("50.0");
                    }
                });
                mServing2.setText("Medium (100.0\u00A0g)");
                mServing2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText("100.0");
                    }
                });
                mServing3.setText("Large (250.0\u00A0g)");
                mServing3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText("250.0");
                    }
                });
            }
        } else {
            mWeight.setText(String.format("%.1f", mLog.getSizeImperial()));
            mWeightTextView = (TextView) v.findViewById(R.id.fragment_edit_log_weight_textview);
            mWeightTextView.setText(getString(R.string.dialog_add_log_weight_textview_imperial));

            if (mFood != null) {
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

                /**If food weight in the log matches one of the portion sizes, set its radio button as checked*/
                if (mLog.getSizeImperial().equals(mFood.getPortion1SizeImperial())) {
                    mServing1.setChecked(true);
                } else if (mLog.getSizeImperial().equals(mFood.getPortion2SizeImperial())) {
                    mServing2.setChecked(true);
                } else if (mLog.getSizeImperial().equals(mFood.getPortion3SizeImperial())) {
                    mServing3.setChecked(true);
                }
            } else {
                mServing1.setText("Small (1.0\u00A0oz)");
                mServing1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText("1.0");
                    }
                });
                mServing2.setText("Medium (3.0\u00A0oz)");
                mServing2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText("3.0");
                    }
                });
                mServing3.setText("Large (8.0\u00A0oz)");
                mServing3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWeight.setText("8.0");
                    }
                });
            }
        }

        mFoodTitle = (TextView) v.findViewById(R.id.fragment_edit_log_food_title);
        mFoodTitle.setText(mLog.getFood());

        mSaveButton = (Button) v.findViewById(R.id.fragment_edit_log_save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
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
                mLog.setDate(mDate);
                mLog.setDateText();
                mLog.setKcal(mLog.getKcal()/mLog.getSize() * weight);
                mLog.setProtein(mLog.getProtein()/mLog.getSize() * weight);
                mLog.setCarbs(mLog.getCarbs()/mLog.getSize() * weight);
                mLog.setFat(mLog.getFat()/mLog.getSize() * weight);
                mLog.setSize(weight);
                mLog.setSizeImperial(weight/28.35f);
                lm.get(getActivity()).updateLog(mLog);
                Toast.makeText(getActivity(), "Meal log updated!", Toast.LENGTH_SHORT).show();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        mCancelButton = (Button) v.findViewById(R.id.fragment_edit_log_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        mDeleteButton = (Button) v.findViewById(R.id.fragment_edit_log_delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Are you sure you want to delete this log?";
                String title = "Delete log?";
                SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newInstance(message, title);
                dialog.setTargetFragment(EditLogFragment.this, REQUEST_DELETE_LOG);
                dialog.show(getFragmentManager(), "delete_log");
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
        if (requestCode == REQUEST_DELETE_LOG) {
            lm.deleteLog(mLog);
            Toast.makeText(getActivity(), "Log deleted!", Toast.LENGTH_SHORT).show();

            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }
}
