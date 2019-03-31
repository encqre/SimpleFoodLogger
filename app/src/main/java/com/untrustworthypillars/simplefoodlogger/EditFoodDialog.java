package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.UUID;

public class EditFoodDialog extends DialogFragment {
    private static final String ARG_FOOD = "food";

    private UUID mFoodId;
    private Food food;

    private Spinner mSpinner;
    private EditText mFoodTitle;
    private EditText mCalories;
    private EditText mProtein;
    private EditText mCarbs;
    private EditText mFat;
    private CheckBox mFavorite;

    public static EditFoodDialog newInstance (UUID foodid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, foodid);

        EditFoodDialog fragment = new EditFoodDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mFoodId = (UUID) getArguments().getSerializable(ARG_FOOD);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_food, null);

        mSpinner = (Spinner) v.findViewById(R.id.dialog_add_food_category_spinner);

        //Creating adapter for spinner, using resources array as list of items and default android layout for single spinner item
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.food_categories_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(0); //TODO proper category

        mFoodTitle = (EditText) v.findViewById(R.id.dialog_add_food_name);
        mCalories = (EditText) v.findViewById(R.id.dialog_add_food_calories);
        mCalories.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mProtein = (EditText) v.findViewById(R.id.dialog_add_food_protein);
        mProtein.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mCarbs = (EditText) v.findViewById(R.id.dialog_add_food_carbs);
        mCarbs.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mFat = (EditText) v.findViewById(R.id.dialog_add_food_fat);
        mFat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mFavorite = (CheckBox) v.findViewById(R.id.dialog_add_food_isfavorite);

        food = FoodManager.get(getActivity()).getCustomFood(mFoodId);
        mFoodTitle.setText(food.getTitle());
        mCalories.setText(food.getKcal().toString());
        mProtein.setText(food.getProtein().toString());
        mCarbs.setText(food.getCarbs().toString());
        mFat.setText(food.getFat().toString());
        mFavorite.setChecked(food.isFavorite());

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Edit food information")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }

                })
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FoodManager.get(getActivity()).deleteCustomFood(food);
                        Toast.makeText(getActivity(), "Food item deleted from the database!", Toast.LENGTH_SHORT).show();
                        sendResult(Activity.RESULT_OK);
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
                    if (mFoodTitle.getText().toString().equals("") || mCalories.getText().toString().equals("") ||
                            mProtein.getText().toString().equals("") || mCarbs.getText().toString().equals("") ||
                            mFat.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (mFoodTitle.getText().toString().contains(";")) {
                            Toast.makeText(getActivity(), "Name contains illegal character ';'", Toast.LENGTH_SHORT).show();
                        } else {
                            Food food = FoodManager.get(getActivity()).getCustomFood(mFoodId);
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
                            FoodManager.get(getActivity()).updateCustomFood(food);
                            Toast.makeText(getActivity(), "Food item info updated!", Toast.LENGTH_SHORT).show();

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

}
