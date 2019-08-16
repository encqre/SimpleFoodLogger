package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.UUID;

public class EditFoodDialog extends DialogFragment {
    private static final String ARG_FOOD = "food";
    private static final String ARG_FOOD_TYPE = "foodtype";

    private UUID mFoodId;
    private int mFoodType;
    private Food food;

    private String mFoodCategory;
    private int mFoodCategoryId = 0;
    public static final String[] FOOD_CATEGORIES = new String[]{
            "Dairy & Eggs",
            "Meat",
            "Breads & Cereals",
            "Fast Food",
            "Soups & Salads",
            "Vegetables",
            "Fruits",
            "Beans & Legumes",
            "Pasta & Rice",
            "Fish & Seafood",
            "Sweets & Snacks",
            "Drinks",
            "Nuts & Seeds",
            "Sauces, Spices, Oils",
            "Other"
    };

    private Spinner mSpinner;
    private EditText mFoodTitle;
    private EditText mCalories;
    private EditText mProtein;
    private EditText mCarbs;
    private EditText mFat;
    private CheckBox mFavorite;
    private EditText mServing1Name;
    private EditText mServing1Size;
    private EditText mServing2Name;
    private EditText mServing2Size;
    private EditText mServing3Name;
    private EditText mServing3Size;

    private ConstraintLayout layout;
    private ConstraintLayout.LayoutParams bottomElementParams; //layout params of mServing3Name
    private int keyboardHeight = 0;

    public static EditFoodDialog newInstance (UUID foodid, int foodType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, foodid);
        args.putInt(ARG_FOOD_TYPE, foodType);

        EditFoodDialog fragment = new EditFoodDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mFoodId = (UUID) getArguments().getSerializable(ARG_FOOD);
        mFoodType = getArguments().getInt(ARG_FOOD_TYPE);

        if (mFoodType == 0) {
            food = FoodManager.get(getActivity()).getCustomFood(mFoodId);
        } else if (mFoodType == 1) {
            food = FoodManager.get(getActivity()).getCommonFood(mFoodId);
        } else if (mFoodType == 2) {
            food = FoodManager.get(getActivity()).getExtendedFood(mFoodId);
        }

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_food, null);

        layout = v.findViewById(R.id.dialog_add_food_layout);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                layout.getWindowVisibleDisplayFrame(r);
                int screenHeight = layout.getRootView().getHeight();
                int newKeyboardHeight = screenHeight - r.bottom;
                if (newKeyboardHeight != keyboardHeight) {
                    keyboardHeight = newKeyboardHeight;
                    if (newKeyboardHeight > screenHeight * 0.15) {
                        if (newKeyboardHeight>100){
                            newKeyboardHeight = newKeyboardHeight -100;
                        }
                        bottomElementParams.bottomMargin = newKeyboardHeight;
                        mServing3Name.setLayoutParams(bottomElementParams);
                        android.util.Log.d("TESTTAG","Keyboard is showing. Height of the bottom margin is " + newKeyboardHeight);
                    } else {
                        bottomElementParams.bottomMargin = 0;
                        mServing3Name.setLayoutParams(bottomElementParams);
                        android.util.Log.d("TESTTAG","Keyboard is closed");
                    }
                }

            }
        });

        mFoodCategory = food.getCategory();
        for (int i =0; i< FOOD_CATEGORIES.length; i++) {
            if (mFoodCategory.equals(FOOD_CATEGORIES[i])) {
                mFoodCategoryId = i;
            }
        }

        mSpinner = (Spinner) v.findViewById(R.id.dialog_add_food_category_spinner);

        //Creating adapter for spinner, using resources array as list of items and default android layout for single spinner item
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.food_categories_array, R.layout.spinner_category_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(mFoodCategoryId);

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
        mServing1Name = (EditText) v.findViewById(R.id.dialog_add_food_serving1_name);
        mServing1Size = (EditText) v.findViewById(R.id.dialog_add_food_serving1_size);
        mServing1Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mServing2Name = (EditText) v.findViewById(R.id.dialog_add_food_serving2_name);
        mServing2Size = (EditText) v.findViewById(R.id.dialog_add_food_serving2_size);
        mServing2Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mServing3Name = (EditText) v.findViewById(R.id.dialog_add_food_serving3_name);
        bottomElementParams = (ConstraintLayout.LayoutParams) mServing3Name.getLayoutParams();
        mServing3Size = (EditText) v.findViewById(R.id.dialog_add_food_serving3_size);
        mServing3Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


        mFoodTitle.setText(food.getTitle());
        mCalories.setText(food.getKcal().toString());
        mProtein.setText(food.getProtein().toString());
        mCarbs.setText(food.getCarbs().toString());
        mFat.setText(food.getFat().toString());
        mFavorite.setChecked(food.isFavorite());
        mServing1Name.setText(food.getPortion1Name());
        mServing1Size.setText(food.getPortion1SizeMetric().toString());
        mServing2Name.setText(food.getPortion2Name());
        mServing2Size.setText(food.getPortion2SizeMetric().toString());
        mServing3Name.setText(food.getPortion3Name());
        mServing3Size.setText(food.getPortion3SizeMetric().toString());


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
                            if (mServing1Name.getText().toString().equals("")) {
                                food.setPortion1Name("Small");
                            } else {
                                food.setPortion1Name(mServing1Name.getText().toString());
                            }
                            if (mServing1Size.getText().toString().equals("")) {
                                food.setPortion1SizeMetric(50.0f);
                                food.setPortion1SizeImperial(50.0f/28.35f);
                            } else {
                                food.setPortion1SizeMetric(Float.parseFloat(mServing1Size.getText().toString()));
                                food.setPortion1SizeImperial(Float.parseFloat(mServing1Size.getText().toString())/28.35f);
                            }

                            if (mServing2Name.getText().toString().equals("")) {
                                food.setPortion2Name("Medium");
                            } else {
                                food.setPortion2Name(mServing2Name.getText().toString());
                            }
                            if (mServing2Size.getText().toString().equals("")) {
                                food.setPortion2SizeMetric(100.0f);
                                food.setPortion2SizeImperial(100.0f/28.35f);
                            } else {
                                food.setPortion2SizeMetric(Float.parseFloat(mServing2Size.getText().toString()));
                                food.setPortion2SizeImperial(Float.parseFloat(mServing2Size.getText().toString())/28.35f);
                            }
                            if (mServing3Name.getText().toString().equals("")) {
                                food.setPortion3Name("Large");
                            } else {
                                food.setPortion3Name(mServing3Name.getText().toString());
                            }
                            if (mServing3Size.getText().toString().equals("")) {
                                food.setPortion3SizeMetric(250.0f);
                                food.setPortion3SizeImperial(250.0f/28.35f);
                            } else {
                                food.setPortion3SizeMetric(Float.parseFloat(mServing3Size.getText().toString()));
                                food.setPortion3SizeImperial(Float.parseFloat(mServing3Size.getText().toString())/28.35f);
                            }
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
