package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

//TODO AFTER RELEASE move searchview to toolbar

public class HiddenFoodsFragment extends Fragment {

    private static final int REQUEST_RESTORE = 0;
    private static final int REQUEST_RESTORE_ALL = 1;

    private static final String ARG_FOOD = "food";
    private static final String ARG_FOOD_TYPE = "foodtype";

    private SearchView searchView;
    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private FoodManager fm;

    private SharedPreferences preferences;
    private String units;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hidden_foods, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        units = preferences.getString(
            LoggerSettings.PREFERENCE_UNITS,
            LoggerSettings.PREFERENCE_UNITS_DEFAULT
        );

        searchView = (SearchView) v.findViewById(R.id.fragment_hidden_foods_searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                foodAdapter = new FoodAdapter(fm.getHiddenFoods(query));
                recyclerView.setAdapter(foodAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                foodAdapter = new FoodAdapter(fm.getHiddenFoods(newText));
                if (searchView.getQuery().length() == 0) {
                    foodAdapter = new FoodAdapter(fm.getHiddenFoods(""));
                }
                recyclerView.setAdapter(foodAdapter);
                return false;
            }
        });

        fm = FoodManager.get(getContext());

        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_hidden_foods_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        foodAdapter = new FoodAdapter(fm.getHiddenFoods(null));
        recyclerView.setAdapter(foodAdapter);


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_RESTORE) {
            foodAdapter = new FoodAdapter(fm.getHiddenFoods(searchView.getQuery().toString()));
            recyclerView.setAdapter(foodAdapter);
            Toast.makeText(getActivity(), "Food item was restored", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_RESTORE_ALL) {
            restoreAllFoods();
            Toast.makeText(getActivity(), "All hidden foods have been restored", Toast.LENGTH_SHORT).show();
        }
    }

    public void restoreAllFoods() {
        List<Food> allHiddenFoods = fm.getHiddenFoods(null);
        for (Food food : allHiddenFoods) {
            restoreFood(food);
        }
        //Refreshing the list
        foodAdapter = new FoodAdapter(fm.getHiddenFoods(searchView.getQuery().toString()));
        recyclerView.setAdapter(foodAdapter);
    }

    public void restoreFood(Food food) {
        if (food.getType() == Food.TYPE_DEFAULT_HIDDEN) {
            food.setType(Food.TYPE_DEFAULT);
        } else if (food.getType() == Food.TYPE_DEFAULT_MODIFIED_HIDDEN) {
            food.setType(Food.TYPE_DEFAULT_MODIFIED);
        }
        food.setServings(fm.getFoodServings(food.getFoodId())); // due to lazy loading of servings
        fm.updateFood(food);
    }

    private class FoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView foodNameTextView;
        private TextView foodKcal;
        private TextView foodProtein;
        private TextView foodCarbs;
        private TextView foodFat;
        private ImageView favoriteStar;
        private FrameLayout editButton;
        private TextView foodTypeText;
        private Food food;

        public FoodHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_food, parent, false));
            itemView.setOnClickListener(this);

            foodNameTextView = (TextView) itemView.findViewById(R.id.list_item_food_name);
            foodKcal = (TextView) itemView.findViewById(R.id.list_item_food_calories);
            foodProtein = (TextView) itemView.findViewById(R.id.list_item_food_protein);
            foodCarbs = (TextView) itemView.findViewById(R.id.list_item_food_carbs);
            foodFat = (TextView) itemView.findViewById(R.id.list_item_food_fat);

            favoriteStar = (ImageView) itemView.findViewById(R.id.list_item_food_favorite);
            editButton = (FrameLayout) itemView.findViewById(R.id.list_item_food_edit);
            foodTypeText = (TextView) itemView.findViewById(R.id.list_item_food_type);

        }

        public void bind(Food food) {
            this.food = food;
            foodNameTextView.setText(food.getName());
            favoriteStar.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            foodTypeText.setVisibility(View.GONE);
            if (units.equals("Metric")) {
                foodKcal.setText(getString(R.string.food_list_fragment_kcal, food.getKcal().intValue()));
            } else {
                foodKcal.setText(getString(R.string.food_list_fragment_kcal_imperial, food.getKcal().intValue()));
            }
            foodProtein.setText(getString(R.string.food_list_fragment_protein, food.getProtein().toString()));
            foodCarbs.setText(getString(R.string.food_list_fragment_carbs, food.getCarbs().toString()));
            foodFat.setText(getString(R.string.food_list_fragment_fat, food.getFat().toString()));
        }

        /*When food item is clicked, SimpleDialog is launched for confirmation*/
        public void onClick(View v) {
            SimpleDialog dialog = SimpleDialog.newInstance(food.getFoodId(), food.getType());
            dialog.setTargetFragment(HiddenFoodsFragment.this, REQUEST_RESTORE);
            dialog.show(getFragmentManager(), "OnClick");
        }

    }

    private class FoodAdapter extends RecyclerView.Adapter<FoodHolder> {

        private List<Food> foods;

        public FoodAdapter(List<Food> foods) {
            this.foods = foods;
        }

        @Override
        public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new FoodHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FoodHolder holder, int position) {
            holder.bind(foods.get(position));
        }

        @Override
        public int getItemCount() {
            return foods.size();
        }
    }

    public static class SimpleDialog extends DialogFragment {
        Food food;
        public static SimpleDialog newInstance (UUID foodId, int foodType) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_FOOD, foodId);
            args.putInt(ARG_FOOD_TYPE, foodType);

            SimpleDialog fragment = new SimpleDialog();
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            UUID foodId = (UUID) getArguments().getSerializable(ARG_FOOD);
            food = FoodManager.get(getActivity()).getFood(foodId);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Restore food item?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (food.getType() == Food.TYPE_DEFAULT_HIDDEN) {
                            food.setType(Food.TYPE_DEFAULT);
                        } else if (food.getType() == Food.TYPE_DEFAULT_MODIFIED_HIDDEN) {
                            food.setType(Food.TYPE_DEFAULT_MODIFIED);
                        }
                        food.setServings(
                            FoodManager.get(getActivity()).getFoodServings(food.getFoodId())
                        ); // due to lazy loading of servings
                        FoodManager.get(getActivity()).updateFood(food);
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setMessage(food.getName())
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED);
                    }
                });
            return builder.create();
        }

        private void sendResult(int resultCode) {
            if (getTargetFragment() == null) {
                return;
            }

            Intent intent = new Intent();
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        }
    }


}
