package com.untrustworthypillars.simplefoodlogger;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class EditFoodFragment extends Fragment {

    public static EditFoodFragment newInstance () {
        Bundle args = new Bundle();

        EditFoodFragment fragment = new EditFoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
