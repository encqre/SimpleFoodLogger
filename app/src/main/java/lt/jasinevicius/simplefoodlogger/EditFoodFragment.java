package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.google.android.flexbox.FlexboxLayout;

import lt.jasinevicius.simplefoodlogger.reusable.DecimalDigitsInputFilter;
import lt.jasinevicius.simplefoodlogger.reusable.SimpleConfirmationDialog;
import lt.jasinevicius.simplefoodlogger.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EditFoodFragment extends Fragment {

    private static final String ARG_FOOD = "food";

    private static final int REQUEST_DELETE_FOOD = 0;
    private static final int REQUEST_HIDE_FOOD = 1;
    private static final int REQUEST_SELECT_TAG = 2;

    private static final String DIALOG_SELECT_TAG = "SelectTagDialog";

    private UUID foodId;
    private int foodType;
    private Food food;

    private EditText foodName;
    private EditText calories;
    private EditText protein;
    private EditText carbs;
    private EditText fat;
    private TextView nutritionInfoTextView;
    private TextView servingSizesTextView;
    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;
    private ImageButton selectTagButton;
    private FlexboxLayout tagLayout;
    private LinearLayout servingParentLayout;
    private ScrollView scrollView;
    private ArrayList<Tag> selectedTags;

    private SharedPreferences preferences;
    private String units;
    int orientation;

    @ColorInt int textOnTagColor;

    public static EditFoodFragment newInstance (UUID foodId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, foodId);

        EditFoodFragment fragment = new EditFoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_food, container, false);

        foodId = (UUID) getArguments().getSerializable(ARG_FOOD);
        food = FoodManager.get(getActivity()).getFood(foodId);
        foodType = food.getType();

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        units = preferences.getString(LoggerSettings.PREFERENCE_UNITS, LoggerSettings.PREFERENCE_UNITS_DEFAULT);
        orientation = getResources().getConfiguration().orientation;

        tagLayout = (FlexboxLayout) v.findViewById(R.id.fragment_edit_food_tag);
        servingParentLayout = (LinearLayout) v.findViewById(R.id.fragment_edit_food_serving_vertical_layout);
        scrollView = (ScrollView) v.findViewById(R.id.fragment_edit_food_scrollview);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.textOnTagColor, typedValue, true);
        textOnTagColor = typedValue.data;

        selectTagButton = (ImageButton) v.findViewById(R.id.fragment_edit_food_add_tag_button);
        selectTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                SelectTagDialog dialog = SelectTagDialog.newInstance(selectedTags);
                dialog.setTargetFragment(EditFoodFragment.this, REQUEST_SELECT_TAG);
                dialog.show(fragmentManager, DIALOG_SELECT_TAG);
            }
        });

        selectedTags = new ArrayList<Tag>();
        selectedTags.addAll(food.getTags());
        for (Tag tag : selectedTags) {
            createSelectedTagUIElements(tag);
        }

        List<Serving> servings = food.getServings();
        for (Serving serving : servings) {
            createServingUIElements(serving);
        }

        foodName = (EditText) v.findViewById(R.id.fragment_edit_food_name);

        calories = (EditText) v.findViewById(R.id.fragment_edit_food_calories);
        calories.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        calories.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});

        protein = (EditText) v.findViewById(R.id.fragment_edit_food_protein);
        protein.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        protein.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});

        carbs = (EditText) v.findViewById(R.id.fragment_edit_food_carbs);
        carbs.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        carbs.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});

        fat = (EditText) v.findViewById(R.id.fragment_edit_food_fat);
        fat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        fat.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});

        foodName.setText(food.getName());
        calories.setText(food.getKcal().toString());
        protein.setText(food.getProtein().toString());
        carbs.setText(food.getCarbs().toString());
        fat.setText(food.getFat().toString());


//        serving1Name.setText(food.getServings().get(0).getName());
//        serving2Name.setText(food.getServings().get(1).getName());
//        serving3Name.setText(food.getServings().get(2).getName());

        if (foodType != Food.TYPE_CUSTOM) {
            foodName.setEnabled(false);
            calories.setEnabled(false);
            protein.setEnabled(false);
            carbs.setEnabled(false);
            fat.setEnabled(false);
        }

        if (units.equals("Imperial")) {
            nutritionInfoTextView = (TextView) v.findViewById(R.id.fragment_edit_food_nutrition_textview);
            nutritionInfoTextView.setText(getString(R.string.dialog_add_food_nutrition_textview_imperial));
            servingSizesTextView = (TextView) v.findViewById(R.id.fragment_edit_food_servings_textview);
            servingSizesTextView.setText(getString(R.string.dialog_add_food_servings_textview_imperial));
//            serving1Size.setText(String.format("%.1f", food.getServings().get(0).getSize()/28.35f));
//            serving2Size.setText(String.format("%.1f", food.getServings().get(1).getSize()/28.35f));
//            serving3Size.setText(String.format("%.1f", food.getServings().get(2).getSize()/28.35f));
//        } else {
//            serving1Size.setText(String.format("%.1f", food.getServings().get(0).getSize()));
//            serving2Size.setText(String.format("%.1f", food.getServings().get(1).getSize()));
//            serving3Size.setText(String.format("%.1f", food.getServings().get(2).getSize()));
        }

        saveButton = (Button) v.findViewById(R.id.fragment_edit_food_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodName.getText().toString().equals("") || calories.getText().toString().equals("") ||
                        protein.getText().toString().equals("") || carbs.getText().toString().equals("") ||
                        fat.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (calories.getText().toString().equals(".") || protein.getText().toString().equals(".") ||
                        carbs.getText().toString().equals(".") || fat.getText().toString().equals(".")){
                    Toast.makeText(getActivity(), "Invalid value '.' entered in one or more numeric fields!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Float.parseFloat(calories.getText().toString()) >= 10000f) {
                    Toast.makeText(getActivity(), "Calories value must be lower than 10000!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Float.parseFloat(protein.getText().toString()) > 100f ||
                        Float.parseFloat(carbs.getText().toString()) > 100f ||
                        Float.parseFloat(fat.getText().toString()) > 100f) {
                    Toast.makeText(getActivity(), "Protein/Carbs/Fat values must not exceed 100!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (selectedTags.size() == 0) {
                    Toast.makeText(getActivity(), "Please select at least one tag!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (foodName.getText().toString().contains(";")) {
                    Toast.makeText(getActivity(), "Name contains illegal character ';'", Toast.LENGTH_SHORT).show();
                } else {
                    food.setName(foodName.getText().toString());
                    food.setKcal(Float.parseFloat(calories.getText().toString()));
                    food.setProtein(Float.parseFloat(protein.getText().toString()));
                    food.setCarbs(Float.parseFloat(carbs.getText().toString()));
                    food.setFat(Float.parseFloat(fat.getText().toString()));
                    food.setTags(selectedTags);

                    // set servings
                    List<Serving> servings = new ArrayList<>();
                    for (int i = 0; i < servingParentLayout.getChildCount(); i++) {
                        Serving serving = new Serving();

                        LinearLayout servingLayout = (LinearLayout) servingParentLayout.getChildAt(i);
                        EditText servingNameEditText = (EditText) servingLayout.getChildAt(0);
                        EditText servingSizeEditText = (EditText) servingLayout.getChildAt(1);
                        String servingName = servingNameEditText.getText().toString().equals("") ?
                            servingNameEditText.getHint().toString() :
                            servingNameEditText.getText().toString();
                        float servingSize = servingSizeEditText.getText().toString().equals("") ?
                            Float.parseFloat(getResources().getString(R.string.dialog_add_food_serving_size_hint_metric)) :
                            Float.parseFloat(servingSizeEditText.getText().toString());

                        serving.setName(servingName);
                        serving.setSize(servingSize);
                        serving.setFoodId(food.getFoodId());
                        serving.setType(Serving.TYPE_CUSTOM);
                        servings.add(serving);
                    }
                    food.setServings(servings);

                    if (food.getType() == Food.TYPE_DEFAULT) {
                        food.setType(Food.TYPE_DEFAULT_MODIFIED);
                        if (food.getPriority() == Food.PRIORITY_COMMON) {
                            food.setPriority(Food.PRIORITY_COMMON_MODIFIED);
                        }
                        if (food.getPriority() == Food.PRIORITY_DEFAULT) {
                            food.setPriority(Food.PRIORITY_DEFAULT_MODIFIED);
                        }
                    }

                    FoodManager.get(getActivity()).updateFood(food);
                    Toast.makeText(getActivity(), "Food info updated!", Toast.LENGTH_SHORT).show();
                }
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });

        cancelButton = (Button) v.findViewById(R.id.fragment_edit_food_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        deleteButton = (Button) v.findViewById(R.id.fragment_edit_food_delete_button);
        if (foodType == Food.TYPE_CUSTOM) {
            deleteButton.setText("DELETE");
        } else {
            deleteButton.setText("HIDE");
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodType == Food.TYPE_CUSTOM) {
                    String message = "Are you sure you want to delete food item '" + food.getName() + "'?";
                    String title = "Delete custom food?";
                    SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newInstance(message, title);
                    dialog.setTargetFragment(EditFoodFragment.this, REQUEST_DELETE_FOOD);
                    dialog.show(getFragmentManager(), "delete_food");
                } else {
                    String message = "Hide non-custom food '" + food.getName() + "' from food list and search results?";
                    message += " You can restore hidden foods later in the settings.";
                    String title = "Hide non-custom food?";
                    SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newInstance(message, title);
                    dialog.setTargetFragment(EditFoodFragment.this, REQUEST_HIDE_FOOD);
                    dialog.show(getFragmentManager(), "hide_food");
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_DELETE_FOOD) {
                FoodManager.get(getActivity()).deleteFood(food);
                Toast.makeText(getActivity(), "Food deleted!", Toast.LENGTH_SHORT).show();
            }
            if (requestCode == REQUEST_HIDE_FOOD) {
                if (food.getType() == Food.TYPE_DEFAULT) {
                    food.setType(Food.TYPE_DEFAULT_HIDDEN);
                } else if (food.getType() == Food.TYPE_DEFAULT_MODIFIED) {
                    food.setType(Food.TYPE_DEFAULT_MODIFIED_HIDDEN);
                }
                FoodManager.get(getActivity()).updateFood(food);
                Toast.makeText(getActivity(), "Food hidden!", Toast.LENGTH_SHORT).show();
            }
            if (requestCode == REQUEST_SELECT_TAG) {
                Tag selectedTag = (Tag) data.getParcelableExtra(SelectTagDialog.EXTRA_SELECTED_TAG);
                selectedTags.add(selectedTag);
                createSelectedTagUIElements(selectedTag);
                return;
            }
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    private void createSelectedTagUIElements(Tag tag) {
        LinearLayout tagContainer = new LinearLayout(getActivity());
        TextView tagTextview = new TextView(getActivity());
        ImageButton tagDeselectButton = new ImageButton(getActivity());

        // TODO currently flexbox width is hardcoded, see if that causes problems on different screens
        // Setup container
        FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        // +button has order of 1, and seems that tag elements with order 0 would
        // line up by addition order, so this is good enough. If not, set exact orders explicitly
        lp.setOrder(0);
        int marginDp = Utils.dpToPixels(getActivity(), 4);
        lp.setMargins(marginDp, marginDp, marginDp, marginDp);
        int paddingDp = Utils.dpToPixels(getActivity(), 8);
        tagContainer.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        tagContainer.setBackground(
            ResourcesCompat.getDrawable(
                getResources(), R.drawable.tag_background, getActivity().getTheme()
            )
        );
        tagContainer.setLayoutParams(lp);

        // Setup textview
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp2.setMargins(marginDp, 0, marginDp, 0);
        tagTextview.setTextAppearance(
            getActivity(), R.style.TextAppearance_MaterialComponents_Subtitle2
        );
        tagTextview.setText(tag.getName());
        tagTextview.setLayoutParams(lp2);
        tagTextview.setMaxWidth(Utils.dpToPixels(getActivity(), 240)); // TODO also hardcoded
        tagTextview.setTextSize(14);
        tagTextview.setTextColor(textOnTagColor);
        tagContainer.addView(tagTextview);

        // Setup deselect button
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
            Utils.spToPixels(getActivity(), 20),
            Utils.spToPixels(getActivity(), 20)
        );
        lp3.setMargins(0, 0, 0, 0);
        tagDeselectButton.setLayoutParams(lp3);
        tagDeselectButton.setImageResource(R.drawable.cancel_icon_on_tag);
        tagDeselectButton.setBackgroundTintList(AppCompatResources.getColorStateList(
            getActivity(), R.color.cancel_button_on_tag_color)
        );
        tagDeselectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout parent = (LinearLayout) view.getParent();
                TextView textView = (TextView) parent.getChildAt(0);
                String tagName = textView.getText().toString();
                for (int i=0; i < selectedTags.size(); i++) {
                    if (selectedTags.get(i).getName().equals(tagName)) {
                        selectedTags.remove(i);
                    }
                }

                parent.removeAllViews();
                tagLayout.removeView(parent);
            }
        });
        tagContainer.addView(tagDeselectButton);

        tagLayout.addView(tagContainer);
    }

    private void createServingUIElements(Serving serving) {
        LinearLayout servingLayout = new LinearLayout(getActivity());
        EditText servingNameEditText = new EditText(getActivity());
        EditText servingSizeEditText = new EditText(getActivity());
        ImageButton servingRemoveButton = new ImageButton(getActivity());
        ImageButton servingAddButton = new ImageButton(getActivity());

        int editTextPadding = Utils.dpToPixels(getActivity(), 6);
        int buttonPadding = Utils.dpToPixels(getActivity(), 4);

        // Setup serving layout
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        servingLayout.setLayoutParams(lp);

        // Setup serving name edit text
        LinearLayout.LayoutParams servingEditTextLP = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        servingEditTextLP.setMargins(
            Utils.dpToPixels(getActivity(), 8),
            Utils.dpToPixels(getActivity(), 4),
            0,0
        );
        servingNameEditText.setLayoutParams(servingEditTextLP);
        if (serving == null) {
            servingNameEditText.setHint(getString(R.string.dialog_add_food_serving_name_hint));
        } else {
            servingNameEditText.setText(serving.getName());
        }
        servingNameEditText.setEms(10);
        servingNameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        servingNameEditText.setInputType(
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        );

        servingNameEditText.setPadding(editTextPadding, editTextPadding, editTextPadding,editTextPadding);
        servingNameEditText.setTextSize(16);
        servingNameEditText.setBackground(ResourcesCompat.getDrawable(
            getResources(), R.drawable.card_border_grey, getActivity().getTheme()
        ));
        servingNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    Utils.scrollUpLayout(scrollView, 200);
                }
            }
        });
        servingNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (servingNameEditText.hasFocus() && orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    Utils.scrollUpLayout(scrollView, 200);
                }
            }
        });
        servingLayout.addView(servingNameEditText);

        // Setup serving size edit text
        servingSizeEditText.setLayoutParams(servingEditTextLP);
        if (serving == null) {
            if (units.equals("Imperial")) {
                servingSizeEditText.setHint(getString(R.string.dialog_add_food_serving_size_hint_imperial));
            } else {
                servingSizeEditText.setHint(getString(R.string.dialog_add_food_serving_size_hint_metric));
            }
        } else {
            if (units.equals("Imperial")) {
                servingSizeEditText.setText(String.format("%.1f", serving.getSize() / 28.35f));
            } else {
                servingSizeEditText.setText(String.format("%.1f", serving.getSize()));
            }
        }
        servingSizeEditText.setEms(4);
        servingSizeEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        servingSizeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        servingSizeEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
        servingSizeEditText.setPadding(editTextPadding, editTextPadding, editTextPadding,editTextPadding);
        servingSizeEditText.setTextSize(16);
        servingSizeEditText.setBackground(ResourcesCompat.getDrawable(
            getResources(), R.drawable.card_border_grey, getActivity().getTheme()
        ));
        servingSizeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    Utils.scrollUpLayout(scrollView, 200);
                }
            }
        });
        servingSizeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (servingSizeEditText.hasFocus() && orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    Utils.scrollUpLayout(scrollView, 200);
                }
            }
        });
        servingLayout.addView(servingSizeEditText);

        // Setup add/remove serving buttons
        LinearLayout.LayoutParams servingButtonLP = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        servingButtonLP.setMargins(
            Utils.dpToPixels(getActivity(), 8),
            Utils.dpToPixels(getActivity(), 6),
            0,0
        );

        servingRemoveButton.setLayoutParams(servingButtonLP);
        servingRemoveButton.setBackground(ResourcesCompat.getDrawable(
            getResources(), R.drawable.tag_background, getActivity().getTheme()
        ));
        servingRemoveButton.setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
        servingRemoveButton.setImageResource(R.drawable.cancel_icon);
        servingRemoveButton.setImageTintList(AppCompatResources.getColorStateList(
            getActivity(), R.color.serving_button_tint_color)
        );
        servingRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout parent = (LinearLayout) view.getParent();
                servingParentLayout.removeView(parent);

                // in case we removed last serving, make sure new last serving has visible add button
                LinearLayout lastServing = (LinearLayout) servingParentLayout.getChildAt(servingParentLayout.getChildCount()-1);
                lastServing.getChildAt(3).setVisibility(View.VISIBLE);

                // if after removing this serving there is only one left, hide it's remove button
                if (servingParentLayout.getChildCount() == 1) {
                    LinearLayout lastRemainingServing = (LinearLayout) servingParentLayout.getChildAt(0);
                    Utils.setImageButtonEnabled(
                        getActivity(),
                        false,
                        (ImageButton) lastRemainingServing.getChildAt(2),
                        R.drawable.cancel_icon
                    );
                }
            }
        });
        if (servingParentLayout.getChildCount() == 0) {
            // if this is the only one serving being created, hide it's remove button
            Utils.setImageButtonEnabled(
                getActivity(), false, servingRemoveButton, R.drawable.cancel_icon
            );
        } else if (servingParentLayout.getChildCount() >= 1) {
            // enable delete button for first serving because we are adding second serving
            LinearLayout firstServing = (LinearLayout) servingParentLayout.getChildAt(0);
            Utils.setImageButtonEnabled(
                getActivity(),
                true,
                (ImageButton) firstServing.getChildAt(2),
                R.drawable.cancel_icon
            );
            // hide add button of last serving, because this serving will be new last one
            LinearLayout lastServing = (LinearLayout) servingParentLayout.getChildAt(
                servingParentLayout.getChildCount() - 1
            );
            lastServing.getChildAt(3).setVisibility(View.GONE);
        }
        servingLayout.addView(servingRemoveButton);

        servingAddButton.setLayoutParams(servingButtonLP);
        servingAddButton.setBackground(ResourcesCompat.getDrawable(
            getResources(), R.drawable.tag_background, getActivity().getTheme()
        ));
        servingAddButton.setPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
        servingAddButton.setImageResource(R.drawable.plus_sign);
        servingAddButton.setImageTintList(AppCompatResources.getColorStateList(
            getActivity(), R.color.serving_button_tint_color)
        );
        servingAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createServingUIElements(null);
            }
        });
        servingLayout.addView(servingAddButton);

        servingParentLayout.addView(servingLayout);
        Utils.scrollUpLayout(scrollView, 100);
    }
}
