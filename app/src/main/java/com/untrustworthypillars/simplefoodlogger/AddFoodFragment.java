package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

//TODO fix autoscroll distances when in landscape mode (and probably on smaller res screens too)

public class AddFoodFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    public static final String EXTRA_NEW_FOOD_ID = "simplefoodlogger.extra_new_food_id";

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
    private ScrollView scrollView;
    private ConstraintLayout layoutSectionGeneral;
    private ConstraintLayout layoutSectionNutrition;
    private ConstraintLayout layoutSectionServings;
    private ConstraintLayout layoutSectionButtons;

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

        scrollView = (ScrollView) v.findViewById(R.id.fragment_add_food_scrollview);
        layoutSectionGeneral = (ConstraintLayout) v.findViewById(R.id.fragment_add_food_section_general);
        layoutSectionNutrition = (ConstraintLayout) v.findViewById(R.id.fragment_add_food_section_nutrition);
        layoutSectionServings = (ConstraintLayout) v.findViewById(R.id.fragment_add_food_section_servings);
        layoutSectionButtons = (ConstraintLayout) v.findViewById(R.id.fragment_add_food_section_buttons);

        mFoodTitle = (EditText) v.findViewById(R.id.fragment_add_food_name);
        mFoodTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionGeneral);
            }
        });
        mFoodTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mFoodTitle.hasFocus()) {
                    scrollUpLayout(layoutSectionGeneral);
                }
            }
        });

        mCalories = (EditText) v.findViewById(R.id.fragment_add_food_calories);
        mCalories.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionNutrition);
            }
        });
        mCalories.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mCalories.hasFocus()) {
                    scrollUpLayout(layoutSectionNutrition);
                }
            }
        });
        mProtein = (EditText) v.findViewById(R.id.fragment_add_food_protein);
        mProtein.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mProtein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionNutrition);
            }
        });
        mProtein.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mProtein.hasFocus()) {
                    scrollUpLayout(layoutSectionNutrition);
                }
            }
        });
        mCarbs = (EditText) v.findViewById(R.id.fragment_add_food_carbs);
        mCarbs.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mCarbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionServings);
            }
        });
        mCarbs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mCarbs.hasFocus()) {
                    scrollUpLayout(layoutSectionServings);
                }
            }
        });
        mFat = (EditText) v.findViewById(R.id.fragment_add_food_fat);
        mFat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionButtons);
            }
        });
        mFat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mFat.hasFocus()) {
                    scrollUpLayout(layoutSectionButtons);
                }
            }
        });
        mServing1Name = (EditText) v.findViewById(R.id.fragment_add_food_serving1_name);
        mServing1Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionButtons);
            }
        });
        mServing1Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mServing1Name.hasFocus()) {
                    scrollUpLayout(layoutSectionButtons);
                }
            }
        });
        mServing1Size = (EditText) v.findViewById(R.id.fragment_add_food_serving1_size);
        mServing1Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mServing1Size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionButtons);
            }
        });
        mServing1Size.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mServing1Size.hasFocus()) {
                    scrollUpLayout(layoutSectionButtons);
                }
            }
        });
        mServing2Name = (EditText) v.findViewById(R.id.fragment_add_food_serving2_name);
        mServing2Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionButtons);
            }
        });
        mServing2Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mServing2Name.hasFocus()) {
                    scrollUpLayout(layoutSectionButtons);
                }
            }
        });
        mServing2Size = (EditText) v.findViewById(R.id.fragment_add_food_serving2_size);
        mServing2Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mServing2Size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionButtons);
            }
        });
        mServing2Size.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mServing2Size.hasFocus()) {
                    scrollUpLayout(layoutSectionButtons);
                }
            }
        });
        mServing3Name = (EditText) v.findViewById(R.id.fragment_add_food_serving3_name);
        mServing3Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionButtons);
            }
        });
        mServing3Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mServing3Name.hasFocus()) {
                    scrollUpLayout(layoutSectionButtons);
                }
            }
        });
        mServing3Size = (EditText) v.findViewById(R.id.fragment_add_food_serving3_size);
        mServing3Size.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mServing3Size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollUpLayout(layoutSectionButtons);
            }
        });
        mServing3Size.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (mServing3Size.hasFocus()) {
                    scrollUpLayout(layoutSectionButtons);
                }
            }
        });

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

                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_NEW_FOOD_ID, food.getFoodId());
                        getActivity().setResult(Activity.RESULT_OK, intent);
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

    //This function scrolls down/up to bottom of provided layout (has to be child of either scrollview itself or child its child relativeLayout)
    private void scrollUpLayout(final ConstraintLayout section){
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int bottom = section.getBottom() + scrollView.getPaddingBottom();
                int sy = scrollView.getScrollY();
                int sh = scrollView.getHeight();
                int delta = bottom - (sy + sh);
                scrollView.smoothScrollBy(0, delta);
            }
        }, 200);
    }

}
