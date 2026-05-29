package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.IDNA;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import lt.jasinevicius.simplefoodlogger.reusable.SimpleConfirmationDialog;
import lt.jasinevicius.simplefoodlogger.reusable.InfoDialog;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
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
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//TODO async queries to db when searching? So that input wouldn't lag on 1-2 letter queries
// TODO tag sorting, item sorting (might remove the need for recent/frequent tabs)
// TODO show stats in food edit page AND/OR add log page (last consumed, consumed count, etc.)
// TODO Food list item UI redesign: Food tags on item
// TODO remember from which tab last time food was added and open start with that tab next time
// TODO when returning from edit food activity with some search query, run query again (works with add food activity now)

/* TODO Before next release:
    1. Get rid of common/extended food separation, renew the non-custom database to new format.
    +?2. Variable serving count implementation
    +?3. Tag delete/update. Through context menu. Handle renames (careful on backup import)
    4. Food list context menu with edit/delete options. Get rid of non-custom tags on list item?
    5. Get rid of recent foods list size setting. Only include items with consumed_count > 0 in recent foods. Or maybe change setting to like last month, last week, etc.
    6. some kind of animation/highlight when pressing add tag button
    8. Frequent tab? Could be postponed for later, will see
* */

public class FoodListFragment extends Fragment {

    private static final String DIALOG_TUTORIAL = "DialogTutorial";
    private static final String DIALOG_EDIT_TAG = "DialogEditTag";

    public static final int TAB_SELECT = 0;
    public static final int TAB_RECENT = 1;
    public static final int TAB_FAVORITES = 2;

    private static final int REQUEST_LOG = 0;
    private static final int REQUEST_ADD_FOOD = 1;
    private static final int REQUEST_EDIT_FOOD = 2;
    private static final int REQUEST_TUTORIAL = 3;
    private static final int REQUEST_EDIT_TAG = 4;
    private static final int REQUEST_DELETE_TAG = 5;
    private static final int REQUEST_HIDE_TAG = 6;
    private static final int REQUEST_SHOW_INFO = 7;

    private Boolean isCalledByPickFoodActivity = false;
    private Date date;

    public List<Tag> tags;
    private Tag selectedTag = null;
    private boolean isTagOpen = false;

    public Tag getSelectedTag() {
        return selectedTag;
    }


    private TabLayout tabLayout;
    private Button addFoodButton;
    private Button backButton;
    private RecyclerView foodRecyclerView;
    private TagAdapter tagAdapter;
    private FoodAdapter foodAdapter;
    private FoodManager fm;
    private SharedPreferences preferences;
    private String units;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SearchView searchView;
    private LinearLayout noSearchResultsLayout;
    private TextView noSearchResultsTextView;
    private Button noSearchResultsAddFoodButton;

    private View v;
    private ConstraintLayout layout;

    private float logicalDensity;

    @ColorInt int foodTypeCustomColor;

    /** Method to change the selected Tab programmatically*/
    public void setTab(int i) {
        TabLayout.Tab tab = tabLayout.getTabAt(i);
        tab.select();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Checking to which activity this fragment belong to:
         * If parent activity is PickFoodActivity, we get the date from extras and set mIsCalledByPickFoodActivity to true*/
        if (getActivity().getClass() == PickFoodActivity.class) {
            date = (Date) getActivity().getIntent().getSerializableExtra(PickFoodActivity.EXTRA_DATE);
            isCalledByPickFoodActivity = true;
        } else {
            date = new Date();
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

        fm = FoodManager.get(getContext());
        tags = fm.getNonHiddenTags();

        //This crap is for setting searchView underline color, wasted too much time trying to find a way to do it via XML
        searchView = (SearchView) v.findViewById(R.id.toolbar_food_list_searchview);
        View searchPlate = (View) searchView.findViewById(androidx.appcompat.R.id.search_plate);

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
                    //animation for tabLayout fade in
                    Transition transition = new Fade();
                    transition.setDuration(300);
                    transition.addTarget(R.id.food_list_tabs);
                    TransitionManager.beginDelayedTransition((ViewGroup) tabLayout.getParent(), transition);
                    tabLayout.setVisibility(searchEditText.getText().toString().length() < 1 ? View.VISIBLE : View.GONE);
                    updateUI();
                }
            }
        });

        searchPlate.getBackground().setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.MULTIPLY);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        units = preferences.getString(LoggerSettings.PREFERENCE_UNITS, LoggerSettings.PREFERENCE_UNITS_DEFAULT);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.foodTypeCustomColor, typedValue, true);
        foodTypeCustomColor = typedValue.data;

        tabLayout = (TabLayout) v.findViewById(R.id.food_list_tabs);
        /* On Click listener for the tabs. Tab positions are numbered from 0. When selected Tab changes, we check what is the selected tab position
         * ( with TabLayout.getSelectedTabPosition), and then accordingly set the correct adapter for the recycler view and set the visibility of
         * searchView item accordingly as well.*/
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case TAB_SELECT:
                        foodRecyclerView.setAdapter(tagAdapter);
                        selectedTag = null;
                        toolbarTitle.setText(isCalledByPickFoodActivity ? "Select Food":"Food Database");
                        isTagOpen = false;
                        searchView.setQueryHint("Search in all foods");
                        noSearchResultsTextView.setText("No results found");
                        break;
                    case TAB_RECENT:
                        foodAdapter = new FoodAdapter(fm.getRecentFoods());
                        foodRecyclerView.setAdapter(foodAdapter);
                        selectedTag = null;
                        toolbarTitle.setText("Recent Foods");
                        isTagOpen = false;
                        searchView.setQueryHint("Search in recent foods");
                        noSearchResultsTextView.setText("No results found in recent foods");
                        break;
                    case TAB_FAVORITES:
                        foodAdapter = new FoodAdapter(fm.getFavoriteFoods());
                        foodRecyclerView.setAdapter(foodAdapter);
                        selectedTag = null;
                        toolbarTitle.setText("Favorite Foods");
                        isTagOpen = false;
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
                switch (tabLayout.getSelectedTabPosition()) {
                    case TAB_SELECT:
                        foodRecyclerView.setAdapter(tagAdapter);
                        selectedTag = null;
                        toolbarTitle.setText(isCalledByPickFoodActivity ? "Select Food":"Food Database");
                        isTagOpen = false;
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
                Intent intent = AddFoodActivity.newIntent(getActivity(), selectedTag);
                startActivityForResult(intent, REQUEST_ADD_FOOD);
            }
        });

        backButton = (Button) v.findViewById(R.id.toolbar_food_list_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTagOpen) {
                    tagAdapter = new TagAdapter(tags);
                    foodAdapter = new FoodAdapter(new ArrayList<Food>());
                    foodRecyclerView.setAdapter(tagAdapter);
                    selectedTag = null;
                    toolbarTitle.setText(isCalledByPickFoodActivity ? "Select Food":"Food Database");
                    searchView.setQueryHint("Search in all foods");
                    noSearchResultsTextView.setText("No results found");
                    isTagOpen = false;

                } else {
                    getActivity().onBackPressed();
                }
            }
        });

        toolbarTitle = (TextView) v.findViewById(R.id.toolbar_food_list_title);
        toolbarTitle.setText(isCalledByPickFoodActivity ? "Select Food":"Food Database");

        layout = (ConstraintLayout) v.findViewById(R.id.toolbar_food_list_layout);

        noSearchResultsLayout = (LinearLayout) v.findViewById(R.id.fragment_food_list_no_results_layout);
        noSearchResultsTextView = (TextView) v.findViewById(R.id.fragment_food_list_no_results_textview);
        noSearchResultsAddFoodButton = (Button) v.findViewById(R.id.fragment_food_list_no_results_add_food_button);
        noSearchResultsAddFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddFoodActivity.newIntent(getActivity(), selectedTag);
                startActivityForResult(intent, REQUEST_ADD_FOOD);
            }
        });

        float density = getActivity().getResources().getDisplayMetrics().density;
        int screenWidthDp = getActivity().getResources().getConfiguration().screenWidthDp;

        if (!isCalledByPickFoodActivity) {
            //set various specific toolbar layout params if fragment is accessed from main activity
            backButton.setVisibility(View.INVISIBLE);

            int paddingDp = 16;
            int paddingPixel = (int)(paddingDp * density);
            toolbarTitle.setPadding(paddingPixel,0,0,0);

            int searchViewMaxWidthDp = screenWidthDp - 60;
            int maxWidthPixel = (int)(searchViewMaxWidthDp * density);
            searchView.setMaxWidth(maxWidthPixel);
        } else {
            int searchViewMaxWidthDp = screenWidthDp - 100;
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
                tabLayout.setVisibility(View.VISIBLE);

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
                foodAdapter = new FoodAdapter(
                    fm.searchFoods(
                        query,
                        tabLayout.getSelectedTabPosition() == TAB_FAVORITES,
                        tabLayout.getSelectedTabPosition() == TAB_RECENT,
                        false,
                        isTagOpen ? selectedTag.getName() : ""
                    )
                );
                noSearchResultsLayout.setVisibility(foodAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                tabLayout.setVisibility(searchEditText.getText().toString().length() < 1 ? View.VISIBLE : View.GONE);
                foodRecyclerView.setAdapter(foodAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    foodAdapter = new FoodAdapter(
                        fm.searchFoods(
                            newText,
                            tabLayout.getSelectedTabPosition() == TAB_FAVORITES,
                            tabLayout.getSelectedTabPosition() == TAB_RECENT,
                            false,
                            isTagOpen ? selectedTag.getName() : ""
                        )
                    );
                    noSearchResultsLayout.setVisibility(foodAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                    tabLayout.setVisibility(searchEditText.getText().toString().length() < 1 ? View.VISIBLE : View.GONE);
                    foodRecyclerView.setAdapter(foodAdapter);
                } else {
                    updateUI();
                }
                return false;
            }
        });



        foodRecyclerView = (RecyclerView) v.findViewById(R.id.food_recycler);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                    if (isTagOpen) {
                        tagAdapter = new TagAdapter(tags);
                        foodAdapter = new FoodAdapter(new ArrayList<Food>());
                        foodRecyclerView.setAdapter(tagAdapter);
                        selectedTag = null;
                        toolbarTitle.setText(isCalledByPickFoodActivity ? "Select Food":"Food Database");
                        searchView.setQueryHint("Search in all foods");
                        noSearchResultsTextView.setText("No results found");
                        isTagOpen = false;

                        return true;
                    } else if (!isCalledByPickFoodActivity){
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



        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        logicalDensity = metrics.density; //this density represents number of pixels per 1 dp unit

        updateUI();

        if (!preferences.getBoolean(LoggerSettings.PREFERENCE_TUTORIAL_FOOD_LIST_DONE, false)) {
            FragmentManager fm = getFragmentManager();
            InfoDialog dialog = InfoDialog.newInstance(getString(R.string.tutorial_food_list_text), getString(R.string.tutorial_food_list_title));
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
                if (!isCalledByPickFoodActivity) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.fragment_container, new HomePageFragment()).commit();
                    LoggerActivity act = (LoggerActivity) getActivity();
                    act.setTab(act.TAB_HOME);
                } else {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            }
        }
        if (requestCode == REQUEST_ADD_FOOD) {
            // refresh list of tags after add food request, as new tags may have been added
            tags = fm.getNonHiddenTags();
            updateUI();
            if (isCalledByPickFoodActivity && resultCode == Activity.RESULT_OK) {
                //If food was added when adding log, launch addLogActivity right after it's created
                UUID newFoodID = (UUID) data.getSerializableExtra(AddFoodFragment.EXTRA_NEW_FOOD_ID);
                Intent intent = AddLogActivity.newIntent(getActivity(), newFoodID, date);
                startActivityForResult(intent, REQUEST_LOG);
            }
        }
        if (requestCode == REQUEST_EDIT_FOOD ||
            (requestCode == REQUEST_EDIT_TAG && resultCode != Activity.RESULT_CANCELED)) {
            // refresh list of tags after edit food request, as new tags may have been added
            tags = fm.getNonHiddenTags();
            updateUI();
        }
        if (requestCode == REQUEST_DELETE_TAG && resultCode == Activity.RESULT_OK) {
            UUID tagId = (UUID) data.getSerializableExtra(SimpleConfirmationDialog.EXTRA_DATA);
            Tag tag = fm.getTag(tagId);
            List<Food> tagFoods = fm.getFoodsWithTag(tag.getName());
            int foodsOnlyWithThisTagCount = 0;
            for (Food food: tagFoods) {
                if (fm.getFoodTags(food.getFoodId()).size() < 2) {
                    foodsOnlyWithThisTagCount++;
                }
            }
            if (foodsOnlyWithThisTagCount > 0) {
                String title = "Unable to delete tag";
                String message = "Tag '" + tag.getName() + "' cannot be deleted because ";
                message += foodsOnlyWithThisTagCount + " food(s) with this tag have no other tags. ";
                message += "Please add at least one more tag to these foods before deleting this tag.";
                InfoDialog dialog = InfoDialog.newInstance(message, title);
                dialog.setTargetFragment(FoodListFragment.this, REQUEST_SHOW_INFO);
                dialog.show(getFragmentManager(), "show_info");
            } else {
                fm.deleteTag(tagId);
                Toast.makeText(getActivity(),"Tag deleted!", Toast.LENGTH_SHORT).show();
                tags = fm.getNonHiddenTags();
                updateUI();
            }
        }
        if (requestCode == REQUEST_HIDE_TAG && resultCode == Activity.RESULT_OK) {
            UUID tagId = (UUID) data.getSerializableExtra(SimpleConfirmationDialog.EXTRA_DATA);
            Tag tag = fm.getTag(tagId);
            if (tag.getType() == Tag.TYPE_DEFAULT) {
                tag.setType(Tag.TYPE_DEFAULT_HIDDEN);
                fm.updateTag(tag);
                Toast.makeText(getActivity(), "Tag hidden!", Toast.LENGTH_SHORT).show();
                tags = fm.getNonHiddenTags();
                updateUI();
            }
        }
        if (requestCode == REQUEST_TUTORIAL) {
            preferences.edit().putBoolean(LoggerSettings.PREFERENCE_TUTORIAL_FOOD_LIST_DONE, true).apply();
        }
    }

    private void updateUI() {
        switch(tabLayout.getSelectedTabPosition()){
            case TAB_SELECT:
                if (isTagOpen) {
                    foodAdapter = new FoodAdapter(fm.getFoodsWithTag(selectedTag.getName()));
                    foodRecyclerView.setAdapter(foodAdapter);
                    toolbarTitle.setText(selectedTag.getName());
                } else {
                    tagAdapter = new TagAdapter(tags);
                    foodAdapter = new FoodAdapter(new ArrayList<Food>());
                    foodRecyclerView.setAdapter(tagAdapter);
                    selectedTag = null;
                    toolbarTitle.setText(isCalledByPickFoodActivity ? "Select Food":"Food Database");
                }
                break;
            case TAB_RECENT:
                foodAdapter = new FoodAdapter(fm.getRecentFoods());
                foodRecyclerView.setAdapter(foodAdapter);
                selectedTag = null;
                toolbarTitle.setText("Recent Foods");
                isTagOpen = false;
                break;
            case TAB_FAVORITES:
                foodAdapter = new FoodAdapter(fm.getFavoriteFoods());
                foodRecyclerView.setAdapter(foodAdapter);
                selectedTag = null;
                toolbarTitle.setText("Favorite Foods");
                isTagOpen = false;
                break;
        }

    }

    private class TagHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tagNameTextView;
        private TextView optionsMenuButton;
        private Tag holderTag;

        public TagHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_category, parent, false));
            itemView.setOnClickListener(this);

            tagNameTextView = (TextView) itemView.findViewById(R.id.list_item_category);
            optionsMenuButton = (TextView) itemView.findViewById(R.id.list_item_options_menu);
            optionsMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(getContext(), optionsMenuButton);
                    popup.inflate(R.menu.tag_options);

                    MenuItem editOption = popup.getMenu().findItem(R.id.tag_option_edit);
                    MenuItem deleteOption = popup.getMenu().findItem(R.id.tag_option_delete);

                    if (holderTag.getType() == Tag.TYPE_CUSTOM) {
                        editOption.setEnabled(true);
                        deleteOption.setTitle("Delete");
                    } else {
                        editOption.setEnabled(false);
                        deleteOption.setTitle("Hide");
                    }

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.tag_option_edit) {
                                FragmentManager fragmentManager = getFragmentManager();
                                EditTagDialog dialog = EditTagDialog.newInstance(holderTag.getTagId());
                                dialog.setTargetFragment(FoodListFragment.this, REQUEST_EDIT_TAG);
                                dialog.show(fragmentManager, DIALOG_EDIT_TAG);
                                return true;
                            } else if (id == R.id.tag_option_delete) {
                                String title;
                                String message;
                                int requestId;
                                if (holderTag.getType() == Tag.TYPE_CUSTOM) {
                                    title = "Delete custom tag?";
                                    message = "Are you sure you want to delete tag '" + holderTag.getName() + "'? ";
                                    message += "There are " + fm.getFoodsWithTag(holderTag.getName()).size() + " food(s) with this tag";
                                    requestId = REQUEST_DELETE_TAG;
                                } else {
                                    title = "Hide default tag?";
                                    message = "Are you sure you want to hide default tag '" + holderTag.getName() + "'? ";
                                    message += "Food items with this tag will still be visible. ";
                                    message += "You can restore hidden tags later in the settings";
                                    requestId = REQUEST_HIDE_TAG;
                                }
                                SimpleConfirmationDialog dialog = SimpleConfirmationDialog.newInstance(
                                    message, title, holderTag.getTagId()
                                );
                                dialog.setTargetFragment(FoodListFragment.this, requestId);
                                dialog.show(getFragmentManager(), "delete_tag");
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
        }

        public void bind(Tag tag) {
            holderTag = tag;
            tagNameTextView.setText(tag.getName());
        }

        @Override
        public void onClick(View view) {
            /*When category item is clicked, new FoodAdapter is set up with a list of foods, that is returned
             * by querying database with the category name. Also mSelectedCategory is changed to the clicked category*/
            foodAdapter = new FoodAdapter(fm.getFoodsWithTag(holderTag.getName()));
            selectedTag = holderTag;
            isTagOpen = true;
            searchView.setQueryHint("Search in " + selectedTag.getName());
            noSearchResultsTextView.setText("No results found in " + selectedTag.getName());
            toolbarTitle.setText(selectedTag.getName());
            v.requestFocus();
            foodRecyclerView.setAdapter(foodAdapter);
        }

    }

    private class TagAdapter extends RecyclerView.Adapter<TagHolder> {

        private List<Tag> adapterTags;

        public TagAdapter(List<Tag> tags) {
            adapterTags = tags;
        }

        @Override
        public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new TagHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TagHolder holder, int position) {
            holder.bind(adapterTags.get(position));
        }

        @Override
        public int getItemCount() {
            return adapterTags.size();
        }
    }

    private class FoodHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView foodTitleTextView;
        private TextView foodCalories;
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

            foodTitleTextView = (TextView) itemView.findViewById(R.id.list_item_food_name);
            foodCalories = (TextView) itemView.findViewById(R.id.list_item_food_calories);
            foodProtein = (TextView) itemView.findViewById(R.id.list_item_food_protein);
            foodCarbs = (TextView) itemView.findViewById(R.id.list_item_food_carbs);
            foodFat = (TextView) itemView.findViewById(R.id.list_item_food_fat);
            favoriteStar = (ImageView) itemView.findViewById(R.id.list_item_food_favorite);
            editButton = (FrameLayout) itemView.findViewById(R.id.list_item_food_edit);
            foodTypeText = (TextView) itemView.findViewById(R.id.list_item_food_type);
        }

        public void bind(Food food) {
            this.food = food;
            foodTitleTextView.setText(food.getName());
            if (units.equals("Metric")) {
                foodCalories.setText(getString(R.string.food_list_fragment_kcal, food.getKcal().intValue()));
            } else {
                foodCalories.setText(getString(R.string.food_list_fragment_kcal_imperial, food.getKcal().intValue()));
            }
            GradientDrawable gradientDrawable = (GradientDrawable) foodTypeText.getBackground();
            if (food.getType() == Food.TYPE_CUSTOM) {
                foodTypeText.setVisibility(View.VISIBLE);
                foodTypeText.setText(getString(R.string.list_item_food_type_custom));
                foodTypeText.setTextColor(foodTypeCustomColor);
                gradientDrawable.setStroke((int)Math.ceil(1 * logicalDensity), foodTypeCustomColor);
            } else {
                foodTypeText.setVisibility(View.GONE);
            }
            foodProtein.setText(getString(R.string.food_list_fragment_protein, food.getProtein().toString()));
            foodCarbs.setText(getString(R.string.food_list_fragment_carbs, food.getCarbs().toString()));
            foodFat.setText(getString(R.string.food_list_fragment_fat, food.getFat().toString()));
            favoriteStar.setImageResource(food.isFavorite() ? R.drawable.star_filled : R.drawable.star_border);
            favoriteStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FoodHolder.this.food.setFavorite(!FoodHolder.this.food.isFavorite());
                    FoodHolder.this.food.setServings(fm.getFoodServings(food.getFoodId())); // due to lazy loading of servings
                    FoodHolder.this.food.setTags(fm.getFoodTags(food.getFoodId()));
                    FoodManager.get(getActivity()).updateFood(FoodHolder.this.food);
                    favoriteStar.setImageResource(
                        FoodHolder.this.food.isFavorite() ?
                        R.drawable.star_filled :
                        R.drawable.star_border
                    );
                    Toast.makeText(
                        getActivity(),
                        FoodHolder.this.food.isFavorite() ? "Food added to favorites" : "Food removed from favorites",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = EditFoodActivity.newIntent(getActivity(), FoodHolder.this.food.getFoodId(), FoodHolder.this.food.getType());
                    startActivityForResult(intent, REQUEST_EDIT_FOOD);
                }
            });
        }

        /*When food item is clicked, AddLogActivity is launched, with arguments of foodId, foodType and date*/
        public void onClick(View v) {
            Intent intent = AddLogActivity.newIntent(getActivity(), food.getFoodId(), date);
            startActivityForResult(intent, REQUEST_LOG);
        }

        /*When food item is long clicked, EditFoodActivity is launched to edit food entry, with
         * arguments of FoodID and selected category*/
        public boolean onLongClick(View v) {
            Intent intent = EditFoodActivity.newIntent(getActivity(), food.getFoodId(), food.getType());
            startActivityForResult(intent, REQUEST_EDIT_FOOD);
            return true;
        }

    }

    private class FoodAdapter extends RecyclerView.Adapter<FoodHolder> {

        private List<Food> adapterFoods;

        public FoodAdapter(List<Food> foods) {
            adapterFoods = foods;
        }

        @Override
        public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new FoodHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FoodHolder holder, int position) {
            holder.bind(adapterFoods.get(position));
        }

        @Override
        public int getItemCount() {
            return adapterFoods.size();
        }
    }
}
