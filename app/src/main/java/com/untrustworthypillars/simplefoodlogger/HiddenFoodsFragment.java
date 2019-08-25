package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class HiddenFoodsFragment extends Fragment {

    private static final int REQUEST_UNHIDE = 0;

    private static final String ARG_FOOD = "food";
    private static final String ARG_FOOD_TYPE = "foodtype";

    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private FoodAdapter mFoodAdapter;
    private FoodManager mFoodManager;

    private SharedPreferences mPreferences;
    private String mUnits;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hidden_foods, container, false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUnits = mPreferences.getString("pref_units", "Metric");

        mSearchView = (SearchView) v.findViewById(R.id.fragment_hidden_foods_searchview);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mFoodAdapter = new FoodAdapter(mFoodManager.getHiddenFoods(query));
                mRecyclerView.setAdapter(mFoodAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mFoodAdapter = new FoodAdapter(mFoodManager.getHiddenFoods(newText));
                if (mSearchView.getQuery().length() == 0) {
                    mFoodAdapter = new FoodAdapter(mFoodManager.getHiddenFoods(""));
                    android.util.Log.d("EMPTY TEXT CHANGE", "AA");
                }
                mRecyclerView.setAdapter(mFoodAdapter);
                return false;
            }
        });

        mFoodManager = FoodManager.get(getContext());

        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_hidden_foods_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFoodAdapter = new FoodAdapter(mFoodManager.getHiddenFoods(""));
        mRecyclerView.setAdapter(mFoodAdapter);


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_UNHIDE) {
            mFoodAdapter = new FoodAdapter(mFoodManager.getHiddenFoods(mSearchView.getQuery().toString()));
            mRecyclerView.setAdapter(mFoodAdapter);
        }
    }

    public void unhideAllFoods() {
        List<Food> allHiddenFoods = mFoodManager.getHiddenFoods("");
        for (int i = 0; i < allHiddenFoods.size(); i++) {
            Food food = allHiddenFoods.get(i);
            food.setHidden(false);
            mFoodManager.updateFood(food);
        }
        //Refreshing the list
        mFoodAdapter = new FoodAdapter(mFoodManager.getHiddenFoods(mSearchView.getQuery().toString()));
        mRecyclerView.setAdapter(mFoodAdapter);
    }

    private class FoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mFoodTitleTextView;
        private TextView mFoodCalories;
        private TextView mFoodProtein;
        private TextView mFoodCarbs;
        private TextView mFoodFat;
        private Food mFood;

        public FoodHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_food, parent, false));
            itemView.setOnClickListener(this);

            mFoodTitleTextView = (TextView) itemView.findViewById(R.id.list_item_food_name);
            mFoodCalories = (TextView) itemView.findViewById(R.id.list_item_food_calories);
            mFoodProtein = (TextView) itemView.findViewById(R.id.list_item_food_protein);
            mFoodCarbs = (TextView) itemView.findViewById(R.id.list_item_food_carbs);
            mFoodFat = (TextView) itemView.findViewById(R.id.list_item_food_fat);

        }

        public void bind(Food food) {
            mFood = food;
            mFoodTitleTextView.setText(food.getTitle());
            if (mUnits.equals("Metric")) {
                mFoodCalories.setText(getString(R.string.food_list_fragment_kcal, food.getKcal().intValue()));
            } else {
                mFoodCalories.setText(getString(R.string.food_list_fragment_kcal_imperial, food.getKcal().intValue()));
            }
            mFoodProtein.setText(getString(R.string.food_list_fragment_protein, food.getProtein().toString()));
            mFoodCarbs.setText(getString(R.string.food_list_fragment_carbs, food.getCarbs().toString()));
            mFoodFat.setText(getString(R.string.food_list_fragment_fat, food.getFat().toString()));
        }

        /*When food item is clicked, AddLogDialog is launched, with arguments of FoodID and Date*/
        public void onClick(View v) {
            SimpleDialog dialog = SimpleDialog.newInstance(mFood.getFoodId(), mFood.getType());
            dialog.setTargetFragment(HiddenFoodsFragment.this, REQUEST_UNHIDE);
            dialog.show(getFragmentManager(), "OnClick");
        }

    }

    private class FoodAdapter extends RecyclerView.Adapter<FoodHolder> {

        private List<Food> mFoods;

        public FoodAdapter(List<Food> foods) {
            mFoods = foods;
        }

        @Override
        public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new FoodHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FoodHolder holder, int position) {
            holder.bind(mFoods.get(position));
            if(position %2 == 1) {
                holder.itemView.setBackgroundColor(Color.rgb(245, 245, 245));
            } else {
                holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255));
            }
        }

        @Override
        public int getItemCount() {
            return mFoods.size();
        }
    }

    public static class SimpleDialog extends DialogFragment {
        Food food;
        public static SimpleDialog newInstance (UUID foodid, int foodType) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_FOOD, foodid);
            args.putInt(ARG_FOOD_TYPE, foodType);

            SimpleDialog fragment = new SimpleDialog();
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            UUID foodId = (UUID) getArguments().getSerializable(ARG_FOOD);
            int foodType = getArguments().getInt(ARG_FOOD_TYPE);
            food = FoodManager.get(getActivity()).getFood(foodId, foodType);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Restore/unhide food item?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            food.setHidden(false);
                            FoodManager.get(getActivity()).updateFood(food);
                            sendResult(Activity.RESULT_OK);
                        }
                    })
                    .setMessage(food.getTitle())
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
