package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.google.android.material.radiobutton.MaterialRadioButton;

import lt.jasinevicius.simplefoodlogger.reusable.DecimalDigitsInputFilter;
import lt.jasinevicius.simplefoodlogger.utils.Utils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddLogFragment extends Fragment {

    private static final String ARG_FOOD = "food";
    private static final String ARG_TYPE = "type";
    private static final String ARG_DATE = "date";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Food food;
    private Date date;
    private FoodManager fm = FoodManager.get(getContext());

    private Button dateButton;
    private EditText weightEditText;
    private TextView calories;
    private TextView protein;
    private TextView carbs;
    private TextView fat;
    private RadioGroup servingGroup;
    private TextView weightTextView;
    private TextView foodName;
    private Button addButton;
    private Button cancelButton;
    private Button deleteButton;

    private SharedPreferences preferences;
    private String units;
    private float unitMultiplier;
    private String unitSymbol;

    public static AddLogFragment newInstance (UUID foodId, Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, foodId);
        args.putSerializable(ARG_DATE, date);

        AddLogFragment fragment = new AddLogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_log, container, false);

        UUID uuid = (UUID) getArguments().getSerializable(ARG_FOOD);
        date = (Date) getArguments().getSerializable(ARG_DATE);

        food = fm.getFood(uuid);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        units = preferences.getString(LoggerSettings.PREFERENCE_UNITS, LoggerSettings.PREFERENCE_UNITS_DEFAULT);
        unitMultiplier = (units.equals("Metric")) ? 1.0f : 28.35f;
        unitSymbol = (units.equals("Metric")) ? "g" : "oz";

        dateButton = (Button) v.findViewById(R.id.fragment_edit_log_date_button);
        dateButton.setText(Calculations.dateDisplayString(date));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerDialog dialog = DatePickerDialog.newInstance(date);
                dialog.setTargetFragment(AddLogFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        calories = (TextView) v.findViewById(R.id.fragment_edit_log_calories);
        calories.setText(String.format("%.1f",(food.getKcal())) + " kcal");

        protein = (TextView) v.findViewById(R.id.fragment_edit_log_protein);
        protein.setText(String.format("%.1f",(food.getProtein())) + "g");

        carbs = (TextView) v.findViewById(R.id.fragment_edit_log_carbs);
        carbs.setText(String.format("%.1f",(food.getCarbs())) + "g");

        fat = (TextView) v.findViewById(R.id.fragment_edit_log_fat);
        fat.setText(String.format("%.1f",(food.getFat())) + "g");

        weightEditText = (EditText) v.findViewById(R.id.fragment_edit_log_weight);
        weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        weightEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(5,2)});
//        mWeight.requestFocus();

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Float weight = 0f;
                if (weightEditText.length() > 0) {
                    try {
                        weight = Float.parseFloat(weightEditText.getText().toString());
                    } catch (Exception err) {
                        weight = 0f;
                    }
                }

                weight *= unitMultiplier;

                calories.setText(String.format("%.1f",(food.getKcal() * weight/100)) + " kcal");
                protein.setText(String.format("%.1f",(food.getProtein() * weight/100)) + "g");
                carbs.setText(String.format("%.1f", (food.getCarbs() * weight/100)) + "g");
                fat.setText(String.format("%.1f", (food.getFat() * weight/100)) + "g");
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing
            }
        });

        servingGroup = (RadioGroup) v.findViewById(R.id.fragment_edit_log_serving_radio_group);
        List<Serving> servings = food.getServings();
        for (Serving serving : servings) {
            addServingRadioButton(serving);
        }

        if (units.equals("Imperial")) {
            weightTextView = (TextView) v.findViewById(R.id.fragment_edit_log_weight_textview);
            weightTextView.setText(getString(R.string.dialog_add_log_weight_textview_imperial));
            weightEditText.setHint("3.5");
        }

        foodName = (TextView) v.findViewById(R.id.fragment_edit_log_food_title);
        foodName.setText(food.getName());

        addButton = (Button) v.findViewById(R.id.fragment_edit_log_save_button);
        addButton.setText("Add");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weightEditText.getText().toString().equals("")) {
                    if (units.equals("Imperial")) {
                        weightEditText.setText("3.5");
                    } else {
                        weightEditText.setText("100");
                    }
                }
                if (weightEditText.getText().toString().equals(".")) {
                    Toast.makeText(getActivity(), "Invalid weight value '.' entered!", Toast.LENGTH_SHORT).show();
                } else if (Float.parseFloat(weightEditText.getText().toString()) == 0f) {
                    Toast.makeText(getActivity(), "Weight value is zero!", Toast.LENGTH_SHORT).show();
                } else {
                    Float weight = Float.parseFloat(AddLogFragment.this.weightEditText.getText().toString());
                    weight *= unitMultiplier;

                    Log log = new Log();
                    log.setDate(date);
                    log.setFood(food.getName());
                    log.setSize(weight);
                    log.setKcal(food.getKcal() * weight / 100);
                    log.setProtein(food.getProtein() * weight / 100);
                    log.setCarbs(food.getCarbs() * weight / 100);
                    log.setFat(food.getFat() * weight / 100);
                    LogManager.get(getActivity()).addLog(log);

                    // update food stats
                    food.setConsumedCount(food.getConsumedCount() + 1);
                    food.setLastConsumed(date);
                    FoodManager.get(getActivity()).updateFoodConsumptionStats(food);

                    Toast.makeText(getActivity(), "Meal logged!", Toast.LENGTH_SHORT).show();

                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        });

        cancelButton = (Button) v.findViewById(R.id.fragment_edit_log_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        });

        deleteButton = (Button) v.findViewById(R.id.fragment_edit_log_delete_button);
        deleteButton.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            date = (Date) data.getSerializableExtra(DatePickerDialog.EXTRA_DATE);
            dateButton.setText(Calculations.dateDisplayString(date));
        }
    }

    private void addServingRadioButton(Serving serving) {
        MaterialRadioButton servingButton = new MaterialRadioButton(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        servingButton.setLayoutParams(lp);
        servingButton.setMaxWidth(Utils.dpToPixels(getActivity(), 234));
        servingButton.setText(
            serving.getName() + " (" +
            String.format("%.1f", serving.getSize()/unitMultiplier) + "\u00A0" + unitSymbol + ")");
        servingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightEditText.setText(String.format("%.1f", serving.getSize()/unitMultiplier));
            }
        });
        servingGroup.addView(servingButton);
    }
}
