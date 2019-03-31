package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//TODO portion sizes
public class AddFoodDialog extends DialogFragment {

    private static final String ARG_CATEGORY = "category";

    private int mProvidedCategory = 0;

    private Spinner mSpinner;
    private EditText mFoodTitle;
    private EditText mCalories;
    private EditText mProtein;
    private EditText mCarbs;
    private EditText mFat;
    private CheckBox mFavorite;

    public static AddFoodDialog newInstance (int category) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, category);

        AddFoodDialog fragment = new AddFoodDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mProvidedCategory = (int) getArguments().getSerializable(ARG_CATEGORY);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_food, null);

        mSpinner = (Spinner) v.findViewById(R.id.dialog_add_food_category_spinner);

        //Creating adapter for spinner, using resources array as list of items and default android layout for single spinner item
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.food_categories_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(mProvidedCategory);

        mFoodTitle = (EditText) v.findViewById(R.id.dialog_add_food_name);
        showKeyboard();

        mCalories = (EditText) v.findViewById(R.id.dialog_add_food_calories);
        mCalories.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mProtein = (EditText) v.findViewById(R.id.dialog_add_food_protein);
        mProtein.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mCarbs = (EditText) v.findViewById(R.id.dialog_add_food_carbs);
        mCarbs.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mFat = (EditText) v.findViewById(R.id.dialog_add_food_fat);
        mFat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mFavorite = (CheckBox) v.findViewById(R.id.dialog_add_food_isfavorite);



        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Add a new food")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          //
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

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null) {
            Button button = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeKeyboard();
                    if (mFoodTitle.getText().toString().equals("") || mCalories.getText().toString().equals("") ||
                            mProtein.getText().toString().equals("") || mCarbs.getText().toString().equals("") ||
                            mFat.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (mFoodTitle.getText().toString().contains(";")) {
                            Toast.makeText(getActivity(), "Name contains illegal character ';'", Toast.LENGTH_SHORT).show();
                        } else {
                            Food food = new Food();
                            food.setSortID(0);
                            food.setCategory(mSpinner.getSelectedItem().toString());
                            food.setTitle(mFoodTitle.getText().toString());
                            food.setKcal(Float.parseFloat(mCalories.getText().toString()));
                            food.setProtein(Float.parseFloat(mProtein.getText().toString()));
                            food.setCarbs(Float.parseFloat(mCarbs.getText().toString()));
                            food.setFat(Float.parseFloat(mFat.getText().toString()));
                            food.setFavorite(mFavorite.isChecked());
                            food.setHidden(false);
                            food.setPortion1Name("Small");
                            food.setPortion1SizeMetric(50.0f);
                            food.setPortion1SizeImperial(50.0f/28.35f);
                            food.setPortion2Name("Medium");
                            food.setPortion2SizeMetric(100.0f);
                            food.setPortion2SizeImperial(100.0f/28.35f);
                            food.setPortion3Name("Large");
                            food.setPortion3SizeMetric(250.0f);
                            food.setPortion3SizeImperial(250.0f/28.35f);
                            FoodManager.get(getActivity()).addFood(food);
                            Toast.makeText(getActivity(), "Food item added to the database!", Toast.LENGTH_SHORT).show();

                            sendResult(Activity.RESULT_OK);
                            d.dismiss();
                    }}
                }
            });
        }
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
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
