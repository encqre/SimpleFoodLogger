package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class for the Fragment of the food list/selection
 * If opened from a tab from the main activity - has a FAB that brings up AddFoodDialog.
 * If opened from AddLogActivity, FAB will be hidden, but now it will have an app bar, which will contain
 * a button to go back to the main activity, words "Select Food" and a button to add new food to database (with text)
 *
 */

//TODO Coloring foods by type

public class FoodListFragment extends Fragment {
    public static final int TAB_SELECT = 0;
    public static final int TAB_RECENT = 1;
    public static final int TAB_FAVORITES = 2;
    public static final int TAB_SEARCH = 3;

    private static final int REQUEST_LOG = 0;
    private static final int REQUEST_ADD_FOOD = 1;

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

    private Boolean mIsCalledByAddLogActivity = false;
    private Date mDate;

    private int mSelectedCategory = 0;
    private boolean mIsCategoryOpen = false;

    public int getSelectedCategory() {
        return mSelectedCategory;
    }

    private TabLayout mTabLayout;
    private SearchView mSearchView;
    private CheckBox mExtendedSearch;
    private FloatingActionButton mAddFoodFAB;

    private RecyclerView mFoodRecyclerView;
    private CategoryAdapter mCategoryAdapter;
    private FoodAdapter mFoodAdapter;
    private FoodManager mFoodManager;


    /** Method to change the selected Tab programmatically*/
    public void setTab(int i) {
        TabLayout.Tab tab = mTabLayout.getTabAt(i);
        tab.select();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Checking to which activity this fragment belong to:
         * If parent activity is AddLogActivity, we get the date from extras and set mIsCalledByAddLogActivity to true*/
        if (getActivity().getClass() == AddLogActivity.class) {
            mDate = (Date) getActivity().getIntent().getSerializableExtra(AddLogActivity.EXTRA_DATE);
            mIsCalledByAddLogActivity = true;
        } else {
            mDate = new Date();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_food_list, container, false);

        mTabLayout = (TabLayout) v.findViewById(R.id.food_list_tabs);
        /* On Click listener for the tabs. Tab positions are numbered from 0. When selected Tab changes, we check what is the selected tab position
         * ( with TabLayout.getSelectedTabPosition), and then accordingly set the correct adapter for the recycler view and set the visibility of
         * searchview item accordingly as well.*/
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (mTabLayout.getSelectedTabPosition()) {
                    case TAB_SELECT:
                        mSearchView.setVisibility(View.GONE);
                        mExtendedSearch.setVisibility(View.GONE);
                        mFoodRecyclerView.setAdapter(mCategoryAdapter);
                        mSelectedCategory = 0;
                        mIsCategoryOpen = false;
                        break;
                    case TAB_RECENT:
                        mSearchView.setVisibility(View.GONE);
                        mExtendedSearch.setVisibility(View.GONE);
                        mFoodAdapter = new FoodAdapter(mFoodManager.getRecentFoods());
                        mFoodRecyclerView.setAdapter(mFoodAdapter);
                        mSelectedCategory = 0;
                        mIsCategoryOpen = false;
                        break;
                    case TAB_FAVORITES:
                        mSearchView.setVisibility(View.GONE);
                        mExtendedSearch.setVisibility(View.GONE);
                        mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsFavorite());
                        mFoodRecyclerView.setAdapter(mFoodAdapter);
                        mSelectedCategory = 0;
                        mIsCategoryOpen = false;
                        break;
                    case TAB_SEARCH:
                        mSearchView.setVisibility(View.VISIBLE);
                        mExtendedSearch.setVisibility(View.VISIBLE);
                        mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsSearch(mSearchView.getQuery().toString(), mExtendedSearch.isChecked()));
                        mFoodRecyclerView.setAdapter(mFoodAdapter);
                        mSelectedCategory = 0;
                        mIsCategoryOpen = false;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //not implemented yet, might be necessary in the future?
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                /*If SELECT tab gets reselected then return to the category selector*/
                switch (mTabLayout.getSelectedTabPosition()) {
                    case TAB_SELECT:
                        mSearchView.setVisibility(View.GONE);
                        mFoodRecyclerView.setAdapter(mCategoryAdapter);
                        mSelectedCategory = 0;
                        mIsCategoryOpen = false;
                        break;
                }
            }
        });

        /* When FAB is clicked, launching the dialog for adding new food to the database.
         * If the activity is AddLogActivity, then hide the FAB.
         */
        mAddFoodFAB = (FloatingActionButton) v.findViewById(R.id.floating_button_createfood);
        if (mIsCalledByAddLogActivity) {
            mAddFoodFAB.setVisibility(View.GONE);
        }
        mAddFoodFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                AddFoodDialog dialog = AddFoodDialog.newInstance(mSelectedCategory);
                dialog.setTargetFragment(FoodListFragment.this, REQUEST_ADD_FOOD);
                dialog.show(fm, "noob2");
            }
        });

        /*By default SearchView is made invisible and gone (because first tab by default is categories)*/
        mSearchView = (SearchView) v.findViewById(R.id.searchview_food);
        mSearchView.setVisibility(View.GONE);
        mSearchView.setQueryHint("Start typing food name to search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsSearch(query, mExtendedSearch.isChecked()));
                mFoodRecyclerView.setAdapter(mFoodAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsSearch(newText, mExtendedSearch.isChecked()));
                if (mSearchView.getQuery().length() == 0) {
                    mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsSearch("", mExtendedSearch.isChecked()));
                    android.util.Log.d("EMPTY TEXT CHANGE", "AA");
                }
                mFoodRecyclerView.setAdapter(mFoodAdapter);
                return false;
            }
        });

        mExtendedSearch = (CheckBox) v.findViewById(R.id.fragment_food_list_extended_search_checkbox);
        mExtendedSearch.setVisibility(View.GONE);
        mExtendedSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsSearch(mSearchView.getQuery().toString(), isChecked));
                mFoodRecyclerView.setAdapter(mFoodAdapter);
            }
        });

        mFoodRecyclerView = (RecyclerView) v.findViewById(R.id.food_recycler);
        mFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFoodManager = FoodManager.get(getContext());

        updateUI();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOG) {
            /*  If the activity is the main activity, then, if new log entry was successfully added via dialog,
             * change the fragment to home fragment, and also set the tab programmatically to the home tab
             * If the activity is AddLogActivity, then finish the activity to return to the parent activity*/
            if (!mIsCalledByAddLogActivity) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.fragment_container, new HomePageFragment()).commit();

                LoggerActivity act = (LoggerActivity) getActivity();
                act.setTab(act.TAB_HOME);
            } else {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();

            }
        } else if (requestCode == REQUEST_ADD_FOOD) {
            updateUI();
        }
    }

    private void updateUI() {
        switch(mTabLayout.getSelectedTabPosition()){
            case TAB_SELECT:
                if (mIsCategoryOpen) {
                    mSearchView.setVisibility(View.GONE);
                    mExtendedSearch.setVisibility(View.GONE);
                    mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsCategory(FOOD_CATEGORIES[mSelectedCategory]));
                    mFoodRecyclerView.setAdapter(mFoodAdapter);
                } else {
                    mSearchView.setVisibility(View.GONE);
                    mExtendedSearch.setVisibility(View.GONE);
                    mCategoryAdapter = new CategoryAdapter(FOOD_CATEGORIES);
                    mFoodAdapter = new FoodAdapter(new ArrayList<Food>());
                    mFoodRecyclerView.setAdapter(mCategoryAdapter);
                    mSelectedCategory = 0;
                }
                break;
            case TAB_RECENT:
                mSearchView.setVisibility(View.GONE);
                mExtendedSearch.setVisibility(View.GONE);
                mFoodAdapter = new FoodAdapter(mFoodManager.getRecentFoods());
                mFoodRecyclerView.setAdapter(mFoodAdapter);
                mSelectedCategory = 0;
                mIsCategoryOpen = false;
                break;
            case TAB_FAVORITES:
                mSearchView.setVisibility(View.GONE);
                mExtendedSearch.setVisibility(View.GONE);
                mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsFavorite());
                mFoodRecyclerView.setAdapter(mFoodAdapter);
                mSelectedCategory = 0;
                mIsCategoryOpen = false;
                break;
            case TAB_SEARCH:
                mSearchView.setVisibility(View.VISIBLE);
                mExtendedSearch.setVisibility(View.VISIBLE);
                mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsSearch(mSearchView.getQuery().toString(), mExtendedSearch.isChecked()));
                mFoodRecyclerView.setAdapter(mFoodAdapter);
                mSelectedCategory = 0;
                mIsCategoryOpen = false;
                mSearchView.clearFocus();
                break;
        }

    }


    private class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mCategoryTitleTextView;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_category, parent, false));
            itemView.setOnClickListener(this);

            mCategoryTitleTextView = (TextView) itemView.findViewById(R.id.list_item_category);
        }

        public void bind(String category) {
            mCategoryTitleTextView.setText(category);
        }

        @Override
        public void onClick(View v) {
            /*When category item is clicked, new FoodAdapter is set up with a list of foods, that is returned
             * by querying database with the category name. Also mSelectedCategory is changed to the clicked category*/
            mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsCategory(mCategoryTitleTextView.getText().toString()));
            mSelectedCategory = getAdapterPosition();
            mIsCategoryOpen = true;
            mFoodRecyclerView.setAdapter(mFoodAdapter);
        }

    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {

        public CategoryAdapter(String[] categories) {
            categories = FOOD_CATEGORIES;
        }

        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new CategoryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {
            holder.bind(FOOD_CATEGORIES[position]);
        }

        @Override
        public int getItemCount() {
            return FOOD_CATEGORIES.length;
        }
    }


    //TODO maybe possible to override what 'back' button does when specific category is open, because now it closes the app (because its the same activity)
    private class FoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mFoodTitleTextView;
        private TextView mFoodCalories;
        private TextView mFoodProtein;
        private TextView mFoodCarbs;
        private TextView mFoodFat;
        private Food mFood;

        public FoodHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_food, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mFoodTitleTextView = (TextView) itemView.findViewById(R.id.list_item_food_name);
            mFoodCalories = (TextView) itemView.findViewById(R.id.list_item_food_calories);
            mFoodProtein = (TextView) itemView.findViewById(R.id.list_item_food_protein);
            mFoodCarbs = (TextView) itemView.findViewById(R.id.list_item_food_carbs);
            mFoodFat = (TextView) itemView.findViewById(R.id.list_item_food_fat);

        }

        public void bind(Food food) {
            mFood = food;
            mFoodTitleTextView.setText(food.getTitle());
            mFoodCalories.setText(getString(R.string.food_list_fragment_kcal, food.getKcal().intValue()));
            mFoodProtein.setText(getString(R.string.food_list_fragment_protein, food.getProtein().toString()));
            mFoodCarbs.setText(getString(R.string.food_list_fragment_carbs, food.getCarbs().toString()));
            mFoodFat.setText(getString(R.string.food_list_fragment_fat, food.getFat().toString()));
        }

        /*When food item is clicked, AddLogDialog is launched, with arguments of FoodID and Date*/
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            AddLogDialog dialog = AddLogDialog.newInstance(mFood.getFoodId(), mFood.getType(), mDate);
            dialog.setTargetFragment(FoodListFragment.this, REQUEST_LOG);
            dialog.show(fm, "OnClick");
        }

        /*When food item is long clicked, AddFoodDialog is launched to edit food entry, with
         * arguments of FoodID and selected category*/
        public boolean onLongClick(View v) {
            FragmentManager fm = getFragmentManager();
            EditFoodDialog dialog = EditFoodDialog.newInstance(mFood.getFoodId(), mFood.getType());
            dialog.setTargetFragment(FoodListFragment.this, REQUEST_ADD_FOOD);
            dialog.show(fm, "OnLongClick");
            return true;
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
        }

        @Override
        public int getItemCount() {
            return mFoods.size();
        }
    }
}
