package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class AddFoodFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    private int mProvidedCategory = 0;

    private Spinner mSpinner;
    private EditText mFoodTitle;
    private EditText mCalories;
    private EditText mProtein;
    private EditText mCarbs;
    private EditText mFat;
    private EditText mServing1Name;
    private EditText mServing1Size;
    private EditText mServing2Name;
    private EditText mServing2Size;
    private EditText mServing3Name;
    private EditText mServing3Size;
    private TextView mNutritionInfoTextView;
    private TextView mServingSizesTextView;
    private Button mAddButton;
    private Button mCancelButton;


    private SharedPreferences mPreferences;
    private String mUnits;

    public static AddFoodFragment newInstance (int category) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, category);

        AddFoodFragment fragment = new AddFoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_food, container, false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUnits = mPreferences.getString("pref_units", "Metric");

        mProvidedCategory = (int) getArguments().getSerializable(ARG_CATEGORY);

        mSpinner = (Spinner) v.findViewById(R.id.fragment_add_food_category_spinner);

        //Creating adapter for spinner, using resources array as list of items and default layout for single spinner item

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.food_categories_array, R.layout.spinner_category_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(mProvidedCategory);

        mFoodTitle = (EditText) v.findViewById(R.id.fragment_add_food_name);

        mCalories = (EditText) v.findViewById(R.id.fragment_add_food_calories);
        mCalories.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mProtein = (EditText) v.findViewById(R.id.fragment_add_food_protein);
        mProtein.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mCarbs = (EditText) v.findViewById(R.id.fragment_add_food_carbs);
        mCarbs.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mFat = (EditText) v.findViewById(R.id.fragment_add_food_fat);
        mFat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mServing1Name = (EditText) v.findViewById(R.id.fragment_add_food_serving1_name);
        mServing1Size = (EditText) v.findViewById(R.id.fragment_add_food_serving1_size);
        mServing1Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mServing2Name = (EditText) v.findViewById(R.id.fragment_add_food_serving2_name);
        mServing2Size = (EditText) v.findViewById(R.id.fragment_add_food_serving2_size);
        mServing2Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mServing3Name = (EditText) v.findViewById(R.id.fragment_add_food_serving3_name);
        mServing3Size = (EditText) v.findViewById(R.id.fragment_add_food_serving3_size);
        mServing3Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (mUnits.equals("Imperial")) {
            mNutritionInfoTextView = (TextView) v.findViewById(R.id.fragment_add_food_nutrition_textview);
            mNutritionInfoTextView.setText(getString(R.string.dialog_add_food_nutrition_textview_imperial));
            mServingSizesTextView = (TextView) v.findViewById(R.id.fragment_add_food_servings_textview);
            mServingSizesTextView.setText(getString(R.string.dialog_add_food_servings_textview_imperial));
            mServing1Size.setHint(getString(R.string.dialog_add_food_serving1_hint_imperial));
            mServing2Size.setHint(getString(R.string.dialog_add_food_serving2_hint_imperial));
            mServing3Size.setHint(getString(R.string.dialog_add_food_serving3_hint_imperial));
        }

        mAddButton = (Button) v.findViewById(R.id.fragment_add_food_add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
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
                        Food food = new Food();
                        food.setSortID(0);
                        food.setCategory(mSpinner.getSelectedItem().toString());
                        food.setTitle(mFoodTitle.getText().toString());
                        food.setKcal(Float.parseFloat(mCalories.getText().toString()));
                        food.setProtein(Float.parseFloat(mProtein.getText().toString()));
                        food.setCarbs(Float.parseFloat(mCarbs.getText().toString()));
                        food.setFat(Float.parseFloat(mFat.getText().toString()));
                        food.setFavorite(false);
                        food.setHidden(false);
                        if (mServing1Name.getText().toString().equals("")) {
                            food.setPortion1Name("Small");
                        } else {
                            food.setPortion1Name(mServing1Name.getText().toString());
                        }
                        if (mServing2Name.getText().toString().equals("")) {
                            food.setPortion2Name("Medium");
                        } else {
                            food.setPortion2Name(mServing2Name.getText().toString());
                        }
                        if (mServing3Name.getText().toString().equals("")) {
                            food.setPortion3Name("Large");
                        } else {
                            food.setPortion3Name(mServing3Name.getText().toString());
                        }
                        if (mUnits.equals("Metric")) {
                            if (mServing1Size.getText().toString().equals("")) {
                                food.setPortion1SizeMetric(50.0f);
                                food.setPortion1SizeImperial(50.0f / 28.35f);
                            } else {
                                food.setPortion1SizeMetric(Float.parseFloat(mServing1Size.getText().toString()));
                                food.setPortion1SizeImperial(Float.parseFloat(mServing1Size.getText().toString()) / 28.35f);
                            }
                            if (mServing2Size.getText().toString().equals("")) {
                                food.setPortion2SizeMetric(100.0f);
                                food.setPortion2SizeImperial(100.0f / 28.35f);
                            } else {
                                food.setPortion2SizeMetric(Float.parseFloat(mServing2Size.getText().toString()));
                                food.setPortion2SizeImperial(Float.parseFloat(mServing2Size.getText().toString()) / 28.35f);
                            }
                            if (mServing3Size.getText().toString().equals("")) {
                                food.setPortion3SizeMetric(250.0f);
                                food.setPortion3SizeImperial(250.0f / 28.35f);
                            } else {
                                food.setPortion3SizeMetric(Float.parseFloat(mServing3Size.getText().toString()));
                                food.setPortion3SizeImperial(Float.parseFloat(mServing3Size.getText().toString()) / 28.35f);
                            }
                        } else {
                            if (mServing1Size.getText().toString().equals("")) {
                                food.setPortion1SizeImperial(1.0f);
                                food.setPortion1SizeMetric(28.35f);
                            } else {
                                food.setPortion1SizeImperial(Float.parseFloat(mServing1Size.getText().toString()));
                                food.setPortion1SizeMetric(Float.parseFloat(mServing1Size.getText().toString()) * 28.35f);
                            }
                            if (mServing2Size.getText().toString().equals("")) {
                                food.setPortion2SizeImperial(3.0f);
                                food.setPortion2SizeMetric(3.0f * 28.35f);
                            } else {
                                food.setPortion2SizeImperial(Float.parseFloat(mServing2Size.getText().toString()));
                                food.setPortion2SizeMetric(Float.parseFloat(mServing2Size.getText().toString()) * 28.35f);
                            }
                            if (mServing3Size.getText().toString().equals("")) {
                                food.setPortion3SizeImperial(8.0f);
                                food.setPortion3SizeMetric(8.0f * 28.35f);
                            } else {
                                food.setPortion3SizeImperial(Float.parseFloat(mServing3Size.getText().toString()));
                                food.setPortion3SizeMetric(Float.parseFloat(mServing3Size.getText().toString()) * 28.35f);
                            }
                        }
                        food.setType(0);
                        FoodManager.get(getActivity()).addCustomFood(food);
                        Toast.makeText(getActivity(), "Food item added to the database!", Toast.LENGTH_SHORT).show();

                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }}
            }
        });

        mCancelButton = (Button) v.findViewById(R.id.fragment_add_food_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        return v;
    }

}
