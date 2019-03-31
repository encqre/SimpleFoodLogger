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

public class EditLogDialog extends DialogFragment {
    private static final String ARG_LOG = "log";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Log mLog;
    private Date mDate;
    private LogManager lm = LogManager.get(getContext());

    private Button mDateButton;
    private EditText mWeight;
    private TextView mCalories;
    private TextView mProtein;
    private TextView mCarbs;
    private TextView mFat;

    public static EditLogDialog newInstance (UUID logid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOG, logid);

        EditLogDialog fragment = new EditLogDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        UUID uuid = (UUID) getArguments().getSerializable(ARG_LOG);
        mLog = lm.getLog(uuid);
        mDate = mLog.getDate();

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_log, null);

        mDateButton = (Button) v.findViewById(R.id.dialog_add_log_date_button);
        mDateButton.setText(Calculations.dateDisplayString(mDate));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerDialog dialog = DatePickerDialog.newInstance(mDate);
                dialog.setTargetFragment(EditLogDialog.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mCalories = (TextView) v.findViewById(R.id.dialog_add_log_calories);
        mCalories.setText(String.format("%.1f", mLog.getKcal()) + " kcal");

        mProtein = (TextView) v.findViewById(R.id.dialog_add_log_protein);
        mProtein.setText(String.format("%.1f", mLog.getProtein()) + "g");

        mCarbs = (TextView) v.findViewById(R.id.dialog_add_log_carbs);
        mCarbs.setText(String.format("%.1f", mLog.getCarbs()) + "g");

        mFat = (TextView) v.findViewById(R.id.dialog_add_log_fat);
        mFat.setText(String.format("%.1f", mLog.getFat()) + "g");

        mWeight = (EditText) v.findViewById(R.id.dialog_add_log_weight); //TODO limit input to like two digits after dot
        mWeight.setText(mLog.getSize().toString());
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


        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(mLog.getFood())
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mWeight.getText().toString().equals("")) {
                            mWeight.setText("100");
                        }
                        Float weight = Float.parseFloat(mWeight.getText().toString());
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
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lm.deleteLog(mLog);
                        Toast.makeText(getActivity(), "Log deleted!", Toast.LENGTH_SHORT).show();
                        sendResult(Activity.RESULT_OK);
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
}
