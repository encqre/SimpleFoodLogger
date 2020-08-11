package com.untrustworthypillars.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.untrustworthypillars.simplefoodlogger.reusable.TutorialDialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for the Fragment of the food list/selection
 * If opened from a tab from the main activity - has a FAB that brings up AddFoodDialog.
 * If opened from PickFoodActivity, FAB will be hidden, but now it will have an app bar, which will contain
 * a button to go back to the main activity, words "Select Food" and a button to add new food to database (with text)
 *
 */

//TODO Figure out what is best way to visually separate custom/common/extended entries
//TODO Need to move on from those alternate color entries and figure out something that looks better
//TODO Change adding new food from FAB to something else (because might be confusing with add log). Possibly "add new item" as top row of cateogry list and inside each category?

public class FoodListFragment extends Fragment {

    private static final String DIALOG_TUTORIAL = "DialogTutorial";

    public static final int TAB_SELECT = 0;
    public static final int TAB_RECENT = 1;
    public static final int TAB_FAVORITES = 2;

    private static final int REQUEST_LOG = 0;
    private static final int REQUEST_ADD_FOOD = 1;
    private static final int REQUEST_EDIT_FOOD = 2;
    private static final int REQUEST_TUTORIAL = 3;

    private static final int NO_CATEGORY_SELECTED = 0;


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

    private Boolean mIsCalledByPickFoodActivity = false;
    private Date mDate;

    private int mSelectedCategory = NO_CATEGORY_SELECTED;
    private boolean mIsCategoryOpen = false;

    public int getSelectedCategory() {
        return mSelectedCategory;
    }

    private TabLayout mTabLayout;
    private Button addFoodButton;
    private Button backButton;
    private RecyclerView mFoodRecyclerView;
    private CategoryAdapter mCategoryAdapter;
    private FoodAdapter mFoodAdapter;
    private FoodManager mFoodManager;
    private SharedPreferences mPreferences;
    private String mUnits;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SearchView searchView;
    private LinearLayout noSearchResultsLayout;
    private TextView noSearchResultsTextView;
    private Button noSearchResultsAddFoodButton;

    private View v;
    private ConstraintLayout layout;

    /** Method to change the selected Tab programmatically*/
    public void setTab(int i) {
        TabLayout.Tab tab = mTabLayout.getTabAt(i);
        tab.select();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Checking to which activity this fragment belong to:
         * If parent activity is PickFoodActivity, we get the date from extras and set mIsCalledByPickFoodActivity to true*/
        if (getActivity().getClass() == PickFoodActivity.class) {
            mDate = (Date) getActivity().getIntent().getSerializableExtra(PickFoodActivity.EXTRA_DATE);
            mIsCalledByPickFoodActivity = true;
        } else {
            mDate = new Date();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_food_list, container, false);

        //setting layout for toolbar
        toolbar = (Toolbar) v.findViewById(R.id.food_list_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        View toolbarView = getLayoutInflater().inflate(R.layout.toolbar_food_list, toolbar);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(false);

        //This crap is for setting searchView underline color, wasted too much time trying to find a way to do it via XML
        searchView = (SearchView) v.findViewById(R.id.toolbar_food_list_searchview);
        View searchplate = (View) searchView.findViewById(androidx.appcompat.R.id.search_plate);

        //This one is needed to detect when search's editText becomes empty
        final EditText searchEditText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    android.util.Log.d("textwatches", "twas empty");
                    //animation for tabLayout fade in
                    Transition transition = new Fade();
                    transition.setDuration(300);
                    transition.addTarget(R.id.food_list_tabs);
                    TransitionManager.beginDelayedTransition((ViewGroup) mTabLayout.getParent(), transition);
                    mTabLayout.setVisibility(searchEditText.getText().toString().length() < 1 ? View.VISIBLE : View.GONE);
                    updateUI();
                }
            }
        });

        searchplate.getBackground().setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.MULTIPLY);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUnits = mPreferences.getString("pref_units", "Metric");

        mTabLayout = (TabLayout) v.findViewById(R.id.food_list_tabs);
        /* On Click listener for the tabs. Tab positions are numbered from 0. When selected Tab changes, we check what is the selected tab position
         * ( with TabLayout.getSelectedTabPosition), and then accordingly set the correct adapter for the recycler view and set the visibility of
         * searchview item accordingly as well.*/
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (mTabLayout.getSelectedTabPosition()) {
                    case TAB_SELECT:
                        mFoodRecyclerView.setAdapter(mCategoryAdapter);
                        mSelectedCategory = NO_CATEGORY_SELECTED;
                        toolbarTitle.setText(mIsCalledByPickFoodActivity ? "Select Food":"Food Database");
                        mIsCategoryOpen = false;
                        searchView.setQueryHint("Search in all foods");
                        noSearchResultsTextView.setText("No results found");
                        break;
                    case TAB_RECENT:
                        mFoodAdapter = new FoodAdapter(mFoodManager.getRecentFoods(""));
                        mFoodRecyclerView.setAdapter(mFoodAdapter);
                        mSelectedCategory = NO_CATEGORY_SELECTED;
                        toolbarTitle.setText("Recent Foods");
                        mIsCategoryOpen = false;
                        searchView.setQueryHint("Search in recent foods");
                        noSearchResultsTextView.setText("No results found in recent foods");
                        break;
                    case TAB_FAVORITES:
                        mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsFavorite());
                        mFoodRecyclerView.setAdapter(mFoodAdapter);
                        mSelectedCategory = NO_CATEGORY_SELECTED;
                        toolbarTitle.setText("Favorite Foods");
                        mIsCategoryOpen = false;
                        searchView.setQueryHint("Search in favorite foods");
                        noSearchResultsTextView.setText("No results found in favorite foods");
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
                        mFoodRecyclerView.setAdapter(mCategoryAdapter);
                        mSelectedCategory = NO_CATEGORY_SELECTED;
                        toolbarTitle.setText(mIsCalledByPickFoodActivity ? "Select Food":"Food Database");
                        mIsCategoryOpen = false;
                        searchView.setQueryHint("Search in all foods");
                        noSearchResultsTextView.setText("No results found");
                        break;
                }
            }
        });

        addFoodButton = (Button) v.findViewById(R.id.toolbar_food_list_add_button);
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddFoodActivity.newIntent(getActivity(), mSelectedCategory);
                startActivityForResult(intent, REQUEST_ADD_FOOD);
            }
        });

        backButton = (Button) v.findViewById(R.id.toolbar_food_list_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsCategoryOpen) {
                    mCategoryAdapter = new CategoryAdapter(FOOD_CATEGORIES);
                    mFoodAdapter = new FoodAdapter(new ArrayList<Food>());
                    mFoodRecyclerView.setAdapter(mCategoryAdapter);
                    mSelectedCategory = NO_CATEGORY_SELECTED;
                    toolbarTitle.setText(mIsCalledByPickFoodActivity ? "Select Food":"Food Database");
                    searchView.setQueryHint("Search in all foods");
                    noSearchResultsTextView.setText("No results found");
                    mIsCategoryOpen = false;

                } else {
                    getActivity().onBackPressed();
                }
            }
        });

        toolbarTitle = (TextView) v.findViewById(R.id.toolbar_food_list_title);
        toolbarTitle.setText(mIsCalledByPickFoodActivity ? "Select Food":"Food Database");

        layout = (ConstraintLayout) v.findViewById(R.id.toolbar_food_list_layout);

        noSearchResultsLayout = (LinearLayout) v.findViewById(R.id.fragment_food_list_no_results_layout);
        noSearchResultsTextView = (TextView) v.findViewById(R.id.fragment_food_list_no_results_textview);
        noSearchResultsAddFoodButton = (Button) v.findViewById(R.id.fragment_food_list_no_results_add_food_button);
        noSearchResultsAddFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddFoodActivity.newIntent(getActivity(), mSelectedCategory);
                startActivityForResult(intent, REQUEST_ADD_FOOD);
            }
        });


        if (!mIsCalledByPickFoodActivity) {
            //set various specific toolbar layout params if fragment is accesed from main activity
            backButton.setVisibility(View.INVISIBLE);

            float density = getActivity().getResources().getDisplayMetrics().density;

            int paddingDp = 16;
            int paddingPixel = (int)(paddingDp * density);
            toolbarTitle.setPadding(paddingPixel,0,0,0);

            int searchViewMaxWidthDp = 300;
            int maxWidthPixel = (int)(searchViewMaxWidthDp * density);
            searchView.setMaxWidth(maxWidthPixel);
        }

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarTitle.setVisibility(View.INVISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                toolbarTitle.setVisibility(View.VISIBLE);
                mTabLayout.setVisibility(View.VISIBLE);

                v.requestFocus(); //returning focus back to main View when searchview is collapsed
                updateUI();
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    v.requestFocus(); //returning focus back to main View once focus is away from query text
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsSearch(query, !mIsCategoryOpen,
                        mTabLayout.getSelectedTabPosition() == TAB_FAVORITES,
                        mTabLayout.getSelectedTabPosition() == TAB_RECENT,
                        mIsCategoryOpen ? FOOD_CATEGORIES[mSelectedCategory] : ""));
                noSearchResultsLayout.setVisibility(mFoodAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                mTabLayout.setVisibility(searchEditText.getText().toString().length() < 1 ? View.VISIBLE : View.GONE);
                mFoodRecyclerView.setAdapter(mFoodAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                android.util.Log.d("onquerytextchange", newText + " " + String.valueOf(newText.length()));
                if (newText.length() > 0) {
                    mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsSearch(newText, !mIsCategoryOpen,
                            mTabLayout.getSelectedTabPosition() == TAB_FAVORITES,
                            mTabLayout.getSelectedTabPosition() == TAB_RECENT,
                            mIsCategoryOpen ? FOOD_CATEGORIES[mSelectedCategory] : ""));
                    noSearchResultsLayout.setVisibility(mFoodAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                    mTabLayout.setVisibility(searchEditText.getText().toString().length() < 1 ? View.VISIBLE : View.GONE);
                    mFoodRecyclerView.setAdapter(mFoodAdapter);
                } else {
                    updateUI();
                }
                return false;
            }
        });



        mFoodRecyclerView = (RecyclerView) v.findViewById(R.id.food_recycler);
        mFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /**Overriding default action when back button is pressed. If category is open, then go back to category selection.
         * Otherwise, go back to home tab. Exception is, if fragment is created by PickFoodActivity, in that case,
         * leave the default action, which is to finish activity result.*/
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN )
                {
                    if (searchEditText.getText().toString().length() > 0) {
                        searchEditText.setText("");
                        return true;
                    }
                    if (mIsCategoryOpen) {
                        mCategoryAdapter = new CategoryAdapter(FOOD_CATEGORIES);
                        mFoodAdapter = new FoodAdapter(new ArrayList<Food>());
                        mFoodRecyclerView.setAdapter(mCategoryAdapter);
                        mSelectedCategory = NO_CATEGORY_SELECTED;
                        toolbarTitle.setText(mIsCalledByPickFoodActivity ? "Select Food":"Food Database");
                        searchView.setQueryHint("Search in all foods");
                        noSearchResultsTextView.setText("No results found");
                        mIsCategoryOpen = false;

                        return true;
                    } else if (!mIsCalledByPickFoodActivity){
                        LoggerActivity activity = (LoggerActivity) getActivity();
                        activity.setTab(0);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomePageFragment()).commit();
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });

        mFoodManager = FoodManager.get(getContext());

        updateUI();

        if (!mPreferences.getBoolean("tutorial_food_list_done", false)) {
            FragmentManager fm = getFragmentManager();
            TutorialDialog dialog = TutorialDialog.newInstance(getString(R.string.tutorial_food_list_text));
            dialog.setTargetFragment(FoodListFragment.this, REQUEST_TUTORIAL);
            dialog.show(fm, DIALOG_TUTORIAL);
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOG) {
            /*  If the activity is the main activity, then, if new log entry was successfully added via dialog,
             * change the fragment to home fragment, and also set the tab programmatically to the home tab
             * If the activity is PickFoodActivity, then if log was added, finish the activity to return to the parent activity*/
            if (resultCode != Activity.RESULT_CANCELED){
                if (!mIsCalledByPickFoodActivity) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.fragment_container, new HomePageFragment()).commit();
                    LoggerActivity act = (LoggerActivity) getActivity();
                    act.setTab(act.TAB_HOME);
                } else {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        } else if (requestCode == REQUEST_ADD_FOOD) {
            updateUI();
        } else if (requestCode == REQUEST_EDIT_FOOD) {
            updateUI();
        }
        if (requestCode == REQUEST_TUTORIAL) {
            mPreferences.edit().putBoolean("tutorial_food_list_done", true).apply();
        }
    }

    private void updateUI() {
        switch(mTabLayout.getSelectedTabPosition()){
            case TAB_SELECT:
                if (mIsCategoryOpen) {
                    mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsCategory(FOOD_CATEGORIES[mSelectedCategory]));
                    mFoodRecyclerView.setAdapter(mFoodAdapter);
                    toolbarTitle.setText(FOOD_CATEGORIES[mSelectedCategory]);
                } else {
                    mCategoryAdapter = new CategoryAdapter(FOOD_CATEGORIES);
                    mFoodAdapter = new FoodAdapter(new ArrayList<Food>());
                    mFoodRecyclerView.setAdapter(mCategoryAdapter);
                    mSelectedCategory = NO_CATEGORY_SELECTED;
                    toolbarTitle.setText(mIsCalledByPickFoodActivity ? "Select Food":"Food Database");
                }
                break;
            case TAB_RECENT:
                mFoodAdapter = new FoodAdapter(mFoodManager.getRecentFoods(""));
                mFoodRecyclerView.setAdapter(mFoodAdapter);
                mSelectedCategory = NO_CATEGORY_SELECTED;
                toolbarTitle.setText("Recent Foods");
                mIsCategoryOpen = false;
                break;
            case TAB_FAVORITES:
                mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsFavorite());
                mFoodRecyclerView.setAdapter(mFoodAdapter);
                mSelectedCategory = NO_CATEGORY_SELECTED;
                toolbarTitle.setText("Favorite Foods");
                mIsCategoryOpen = false;
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
        public void onClick(View view) {
            /*When category item is clicked, new FoodAdapter is set up with a list of foods, that is returned
             * by querying database with the category name. Also mSelectedCategory is changed to the clicked category*/
            mFoodAdapter = new FoodAdapter(mFoodManager.getFoodsCategory(mCategoryTitleTextView.getText().toString()));
            mSelectedCategory = getAdapterPosition();
            mIsCategoryOpen = true;
            searchView.setQueryHint("Search in " + FOOD_CATEGORIES[mSelectedCategory]);
            noSearchResultsTextView.setText("No results found in " + FOOD_CATEGORIES[mSelectedCategory]);
            toolbarTitle.setText(FOOD_CATEGORIES[mSelectedCategory]);
            v.requestFocus();
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

    private class FoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mFoodTitleTextView;
        private TextView mFoodCalories;
        private TextView mFoodProtein;
        private TextView mFoodCarbs;
        private TextView mFoodFat;
        private ImageView favoriteStar;
        private Button editButton;
        private Food mFood;

        public FoodHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_food, parent, false));
            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);

            mFoodTitleTextView = (TextView) itemView.findViewById(R.id.list_item_food_name);
            mFoodCalories = (TextView) itemView.findViewById(R.id.list_item_food_calories);
            mFoodProtein = (TextView) itemView.findViewById(R.id.list_item_food_protein);
            mFoodCarbs = (TextView) itemView.findViewById(R.id.list_item_food_carbs);
            mFoodFat = (TextView) itemView.findViewById(R.id.list_item_food_fat);
            favoriteStar = (ImageView) itemView.findViewById(R.id.list_item_food_favorite);
            editButton = (Button) itemView.findViewById(R.id.list_item_food_edit);

        }

        public void bind(Food food) {
            mFood = food;
            mFoodTitleTextView.setText(food.getTitle());
            if (mUnits.equals("Metric")) {
                mFoodCalories.setText(getString(R.string.food_list_fragment_kcal, food.getKcal().intValue()));
            } else {
                mFoodCalories.setText(getString(R.string.food_list_fragment_kcal_imperial, food.getKcal().intValue()));
            }
//            if (mFood.getType() == 0) {
//                mFoodCalories.setTextColor(getResources().getColor(R.color.colorPrimary));
//            } else {
//                mFoodCalories.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//            }
            mFoodProtein.setText(getString(R.string.food_list_fragment_protein, food.getProtein().toString()));
            mFoodCarbs.setText(getString(R.string.food_list_fragment_carbs, food.getCarbs().toString()));
            mFoodFat.setText(getString(R.string.food_list_fragment_fat, food.getFat().toString()));
            favoriteStar.setImageResource(food.isFavorite() ? R.drawable.star_filled : R.drawable.star_border);
            favoriteStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFood.setFavorite(!mFood.isFavorite());
                    FoodManager.get(getActivity()).updateFood(mFood);
                    favoriteStar.setImageResource(mFood.isFavorite() ? R.drawable.star_filled : R.drawable.star_border);
                    Toast.makeText(getActivity(), mFood.isFavorite() ? "Food added to favorites" : "Food removed from favorites", Toast.LENGTH_SHORT).show();
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = EditFoodActivity.newIntent(getActivity(), mFood.getFoodId(), mFood.getType());
                    startActivityForResult(intent, REQUEST_EDIT_FOOD);
                }
            });
        }

        /*When food item is clicked, AddLogActivity is launched, with arguments of foodId, foodType and date*/
        public void onClick(View v) {
            Intent intent = AddLogActivity.newIntent(getActivity(), mFood.getFoodId(), mFood.getType(), mDate);
            startActivityForResult(intent, REQUEST_LOG);
        }

        /*When food item is long clicked, EditFoodActivity is launched to edit food entry, with
         * arguments of FoodID and selected category*/
        public boolean onLongClick(View v) {
            Intent intent = EditFoodActivity.newIntent(getActivity(), mFood.getFoodId(), mFood.getType());
            startActivityForResult(intent, REQUEST_EDIT_FOOD);
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
//            if (mFoods.get(position).getType() == 0) {
//                holder.itemView.setBackgroundColor(Color.rgb(194, 217, 255));
//            } else if (mFoods.get(position).getType() == 1) {
//                holder.itemView.setBackgroundColor(Color.rgb(186, 224, 191));
//            } else {
//                holder.itemView.setBackgroundColor(Color.rgb(224, 197, 188));
//            }
        }

        @Override
        public int getItemCount() {
            return mFoods.size();
        }
    }
}
