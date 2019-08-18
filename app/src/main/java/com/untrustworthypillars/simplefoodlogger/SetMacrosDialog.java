package com.untrustworthypillars.simplefoodlogger;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SetMacrosDialog extends DialogFragment {

    public static SetMacrosDialog newInstance () {
        Bundle args = new Bundle();

        SetMacrosDialog fragment = new SetMacrosDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_set_macros, null);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Set Macronutrient Targets")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();
    }
}
