package com.untrustworthypillars.simplefoodlogger;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.PreferenceScreen;
/**
 * Class for the Fragment of the summary/statistics
 *
 */

public class SummaryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary, container, false);

        LogManager manager = LogManager.get(getContext());

        TextView tempTextView = (TextView) v.findViewById(R.id.temp_summary);
        tempTextView.setText(">watching Black Panther at the movies\n" +
                ">intense fighting scene between protag and antag\n" +
                ">slouch down my seat and yell, \"YO, THIS NIGGA BOUTTA DABBED ON\"\n" +
                ">everyone laughs\n" +
                ">black guy sucks his teeth and says, \"You aight, white boy\"\n" +
                ">Hear \"He cute\" from one of the girls");

        return v;



    }
}
