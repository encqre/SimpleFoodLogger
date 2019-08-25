package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    private EditText mServing1Name;
    private EditText mServing1Size;
    private EditText mServing2Name;
    private EditText mServing2Size;
    private EditText mServing3Name;
    private EditText mServing3Size;
    private TextView mNutritionInfoTextView;
    private TextView mServingSizesTextView;

    private SharedPreferences mPreferences;
    private String mUnits;

    private ConstraintLayout layout;
    private ConstraintLayout.LayoutParams bottomElementParams; //layout params of mServing3Name
    private int keyboardHeight = 0;

    public static AddFoodDialog newInstance (int category) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, category);

        AddFoodDialog fragment = new AddFoodDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUnits = mPreferences.getString("pref_units", "Metric");

        mProvidedCategory = (int) getArguments().getSerializable(ARG_CATEGORY);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_food, null);
        /**Below listener checks for layout changes to detect if keyboard is open or closed. This
         * is required to know how much to adjust the bottom margin of the bottom element of this layout,
         * which is required to make sure that keyboard doesn't hide some of the bottom EditTexts when
         * some EditText which is higher is selected. A bit hacky, but this seems to work well.*/
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

        mSpinner = (Spinner) v.findViewById(R.id.dialog_add_food_category_spinner);

        //Creating adapter for spinner, using resources array as list of items and default layout for single spinner item

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.food_categories_array, R.layout.spinner_category_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(mProvidedCategory);

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

        if (mUnits.equals("Imperial")) {
            mNutritionInfoTextView = (TextView) v.findViewById(R.id.dialog_add_food_nutrition_textview);
            mNutritionInfoTextView.setText(getString(R.string.dialog_add_food_nutrition_textview_imperial));
            mServingSizesTextView = (TextView) v.findViewById(R.id.dialog_add_food_servings_textview);
            mServingSizesTextView.setText(getString(R.string.dialog_add_food_servings_textview_imperial));
            mServing1Size.setHint(getString(R.string.dialog_add_food_serving1_hint_imperial));
            mServing2Size.setHint(getString(R.string.dialog_add_food_serving2_hint_imperial));
            mServing3Size.setHint(getString(R.string.dialog_add_food_serving3_hint_imperial));
        }



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
//      d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
