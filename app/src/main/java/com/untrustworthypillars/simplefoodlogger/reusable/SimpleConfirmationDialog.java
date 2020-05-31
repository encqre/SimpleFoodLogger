package com.untrustworthypillars.simplefoodlogger.reusable;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Simple reusable dialog when OK/CANCEL confirmation from user is required.
 */

public class SimpleConfirmationDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "confirmation_message";
    private static final String ARG_TITLE = "confirmation_title";

    public static SimpleConfirmationDialog newInstance (String message, String title) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MESSAGE, message);
        args.putSerializable(ARG_TITLE, title);

        SimpleConfirmationDialog fragment = new SimpleConfirmationDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle((String) getArguments().getSerializable(ARG_TITLE))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setMessage((String) getArguments().getSerializable(ARG_MESSAGE))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED);
                    }
                });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog){
        super.onCancel(dialog);
        sendResult(Activity.RESULT_CANCELED);
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
