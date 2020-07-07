package com.untrustworthypillars.simplefoodlogger.reusable;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class TutorialDialog extends DialogFragment {

    private static final String ARG_TEXT = "text";

    public static TutorialDialog newInstance(String text) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TEXT, text);

        TutorialDialog fragment = new TutorialDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String text = (String) getArguments().getSerializable(ARG_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tutorial")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}})
                .setMessage((String) getArguments().getSerializable(ARG_TEXT));
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        sendResult(Activity.RESULT_OK);
        super.onDestroyView();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
