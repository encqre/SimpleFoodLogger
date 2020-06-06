package com.untrustworthypillars.simplefoodlogger.reusable;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.untrustworthypillars.simplefoodlogger.R;

public class LoadingProgressDialog extends DialogFragment {

    private static final String ARG_MESSAGE = "loading_message";
    private static final String ARG_TITLE = "loading_title";
    private static final String ARG_PROGRESS = "loading_progress";
    private static final String ARG_MAX = "loading_max";

    private String mLoadingText;
    private String mLoadingTextWithCounters;
    private String mTitle;
    private int mProgress;
    private int mMax;

    private TextView mProgressTextview;
    private ProgressBar mProgressBar;

    public static LoadingProgressDialog newInstance (String message, String title, int progress, int max) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MESSAGE, message);
        args.putSerializable(ARG_TITLE, title);
        args.putSerializable(ARG_PROGRESS, progress);
        args.putSerializable(ARG_MAX, max);

        LoadingProgressDialog fragment = new LoadingProgressDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mLoadingText = (String) getArguments().getSerializable(ARG_MESSAGE);
        mLoadingTextWithCounters = mLoadingText + " " + String.valueOf(mProgress) + " of " + String.valueOf(mMax);
        mTitle = (String) getArguments().getSerializable(ARG_TITLE);
        mProgress = (int) getArguments().getSerializable(ARG_PROGRESS);
        mMax = (int) getArguments().getSerializable(ARG_MAX);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_loading_progress, null);

        mProgressTextview = (TextView) v.findViewById(R.id.dialog_loading_progress_textview);
        mProgressBar = (ProgressBar) v.findViewById(R.id.dialog_loading_progress_bar);

        if (mMax == 0) {
            mProgressBar.setVisibility(View.GONE);
            mLoadingTextWithCounters = mLoadingText;
        }
        updateProgress();

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(mTitle).create();
    }

    private void updateProgress(){
        mProgressTextview.setText(mLoadingTextWithCounters);
        mProgressBar.setMax(mMax);
        mProgressBar.setProgress(mProgress);

    }

    public void updateProgress(int progress, int max) {
        if (mMax == 0 && max > 0) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        if (mMax > 0 && max == 0) {
            mProgressBar.setVisibility(View.GONE);
        }
        mMax = max;
        mProgress = progress;
        mLoadingTextWithCounters = mLoadingText + " " + String.valueOf(mProgress) + " of " + String.valueOf(mMax);
        updateProgress();
    }

}
