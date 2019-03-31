package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

public class AddLogDialog extends DialogFragment {

    private static final String ARG_FOOD = "food";
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

    public static AddLogDialog newInstance (UUID foodid, Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, foodid);
        args.putSerializable(ARG_DATE, date);

        AddLogDialog fragment = new AddLogDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        UUID uuid = (UUID) getArguments().getSerializable(ARG_FOOD);
        mDate = (Date) getArguments().getSerializable(ARG_DATE);
        mFood = fm.getCustomFood(uuid);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_log, null);

        mDateButton = (Button) v.findViewById(R.id.dialog_add_log_date_button);
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
        mCalories.setText(mFood.getKcal() + " kcal");

        mProtein = (TextView) v.findViewById(R.id.dialog_add_log_protein);
        mProtein.setText(mFood.getProtein() + "g");

        mCarbs = (TextView) v.findViewById(R.id.dialog_add_log_carbs);
        mCarbs.setText(mFood.getCarbs() + "g");

        mFat = (TextView) v.findViewById(R.id.dialog_add_log_fat);
        mFat.setText(mFood.getFat() + "g");

        mWeight = (EditText) v.findViewById(R.id.dialog_add_log_weight); //TODO limit input to like two digits after dot
        mWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
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


        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(mFood.getTitle())
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mWeight.getText().toString().equals("")) {
                            mWeight.setText("100");
                        }
                        Float weight = Float.parseFloat(mWeight.getText().toString());
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
