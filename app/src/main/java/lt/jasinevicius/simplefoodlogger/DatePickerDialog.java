package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import lt.jasinevicius.simplefoodlogger.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerDialog extends DialogFragment {

    private static final String ARG_DATE = "date";
    private static final String ARG_TITLE = "title";
    private static final String ARG_STARTDATE = "startdate";
    private static final String ARG_COMPARE_NEEDED = "is_date_comparison_needed";

    public static final String EXTRA_DATE = "com.untrustworthypillars.simplefoodlogger.date";

    private DatePicker mDatePicker;

    public static DatePickerDialog newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putSerializable(ARG_TITLE, "Select date");
        args.putSerializable(ARG_STARTDATE, date);
        args.putBoolean(ARG_COMPARE_NEEDED, false);

        DatePickerDialog fragment = new DatePickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerDialog newInstance(Date date, String title) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putSerializable(ARG_TITLE, title);
        args.putSerializable(ARG_STARTDATE, date);
        args.putBoolean(ARG_COMPARE_NEEDED, false);

        DatePickerDialog fragment = new DatePickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerDialog newInstance(Date date, String title, Date startDate) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putSerializable(ARG_TITLE, title);
        args.putSerializable(ARG_STARTDATE, startDate);
        args.putBoolean(ARG_COMPARE_NEEDED, true);

        DatePickerDialog fragment = new DatePickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        String title = (String) getArguments().getSerializable(ARG_TITLE);
        final Date startDate = (Date) getArguments().getSerializable(ARG_STARTDATE);
        final Boolean compareNeeded = getArguments().getBoolean(ARG_COMPARE_NEEDED);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(title).
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        if (!compareNeeded) {
                            sendResult(Activity.RESULT_OK, date);
                        } else {
                            if (Calculations.dateToDateTextEqualLengthInteger(date) >= Calculations.dateToDateTextEqualLengthInteger(startDate)) {
                                sendResult(Activity.RESULT_OK, date);
                            } else {
                                Toast.makeText(getActivity(), "End date must be after selected start date!", Toast.LENGTH_LONG).show();
                                sendResult(Activity.RESULT_CANCELED, date);
                            }
                        }
                    }
                }).setNeutralButton("Today", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date date =  new Date();
                        if (!compareNeeded) {
                            sendResult(Activity.RESULT_OK, date);
                        } else {
                            if (Calculations.dateToDateTextEqualLengthInteger(date) >= Calculations.dateToDateTextEqualLengthInteger(startDate)) {
                                sendResult(Activity.RESULT_OK, date);
                            } else {
                                Toast.makeText(getActivity(), "End date must be after selected start date!", Toast.LENGTH_LONG).show();
                                sendResult(Activity.RESULT_CANCELED, date);
                            }
                        }
                    }
                }).create();
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
