package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import android.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SelectTagDialog extends DialogFragment {

    private static final int REQUEST_ADD_TAG = 0;

    private static final String ARG_SELECTED_TAGS = "selected_tags";
    public static final String EXTRA_SELECTED_TAG = "selected_tag";
    private static final String DIALOG_ADD_TAG = "AddTagDialog";

    private SearchView searchView;
    private Button addNewTagButton;
    private Button noSearchResultsAddNewTagButton;
    private LinearLayout noSearchResultsLayout;
    private RecyclerView tagRecyclerView;
    private TagAdapter tagAdapter;
    private FoodManager fm;

    public static SelectTagDialog newInstance(ArrayList<Tag> selectedTags) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_SELECTED_TAGS, selectedTags);

        SelectTagDialog fragment = new SelectTagDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ArrayList<Tag> selectedTags = getArguments().getParcelableArrayList(ARG_SELECTED_TAGS);
        fm = FoodManager.get(getContext());

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_select_tag, null);

        noSearchResultsLayout = (LinearLayout) v.findViewById(R.id.dialog_select_tag_no_results_layout);

        searchView = (SearchView) v.findViewById(R.id.dialog_select_tag_searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tagAdapter = new TagAdapter(
                    fm.searchTags(query, selectedTags, false)
                );
                noSearchResultsLayout.setVisibility(tagAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                tagRecyclerView.setAdapter(tagAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    tagAdapter = new TagAdapter(
                        fm.searchTags(newText, selectedTags, false)
                    );
                } else {
                    tagAdapter = new TagAdapter(
                        fm.searchTags("", selectedTags, false)
                    );
                }
                noSearchResultsLayout.setVisibility(tagAdapter.getItemCount() < 1 ? View.VISIBLE : View.GONE);
                tagRecyclerView.setAdapter(tagAdapter);
                return false;
            }
        });

        addNewTagButton = (Button) v.findViewById(R.id.dialog_select_tag_add_new_button);
        addNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                AddTagDialog dialog = new AddTagDialog();
                dialog.setTargetFragment(SelectTagDialog.this, REQUEST_ADD_TAG);
                dialog.show(fragmentManager, DIALOG_ADD_TAG);
            }
        });

        noSearchResultsAddNewTagButton = (Button) v.findViewById(R.id.dialog_select_tag_no_results_add_tag_button);
        noSearchResultsAddNewTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                AddTagDialog dialog = new AddTagDialog();
                dialog.setTargetFragment(SelectTagDialog.this, REQUEST_ADD_TAG);
                dialog.show(fragmentManager, DIALOG_ADD_TAG);
            }
        });

        tagRecyclerView = (RecyclerView) v.findViewById(R.id.dialog_select_tag_recyclerview);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        tagAdapter = new TagAdapter(
            fm.searchTags("", selectedTags, false)
        );
        tagRecyclerView.setAdapter(tagAdapter);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Select tag").create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_ADD_TAG) {
            Tag createdTag = (Tag) data.getParcelableExtra(AddTagDialog.EXTRA_CREATED_TAG);
            // Immediately return new created tag as selected tag
            sendResult(Activity.RESULT_OK, createdTag);
        }
    }

    private void sendResult(int resultCode, Tag selectedTag) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTED_TAG, selectedTag);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);

        dismiss();
    }

    private class TagHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tagNameTextView;
        private TextView optionsMenuButton;
        private Tag tag;

        public TagHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_category, parent, false));
            itemView.setOnClickListener(this);

            tagNameTextView = (TextView) itemView.findViewById(R.id.list_item_category);
            optionsMenuButton = (TextView) itemView.findViewById(R.id.list_item_options_menu);
            optionsMenuButton.setVisibility(View.GONE);
        }

        public void bind(Tag tag) {
            this.tag = tag;
            tagNameTextView.setText(tag.getName());
        }

        @Override
        public void onClick(View view) {
            /* When tag is clicked, finish dialog and send the selected tag*/
            sendResult(Activity.RESULT_OK, tag);
        }

    }

    private class TagAdapter extends RecyclerView.Adapter<SelectTagDialog.TagHolder> {

        private List<Tag> tags;

        public TagAdapter(List<Tag> tags) {
            this.tags = tags;
        }

        @Override
        public SelectTagDialog.TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new SelectTagDialog.TagHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(SelectTagDialog.TagHolder holder, int position) {
            holder.bind(tags.get(position));
        }

        @Override
        public int getItemCount() {
            return tags.size();
        }
    }
}
