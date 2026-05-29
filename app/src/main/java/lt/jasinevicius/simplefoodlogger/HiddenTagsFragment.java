package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

import lt.jasinevicius.simplefoodlogger.reusable.SimpleConfirmationDialog;

//TODO AFTER RELEASE move searchview to toolbar

public class HiddenTagsFragment extends Fragment {

    private static final int REQUEST_RESTORE = 0;
    private static final int REQUEST_RESTORE_ALL = 1;

    private static final String ARG_TAG_ID = "tag_id";

    private SearchView searchView;
    private RecyclerView recyclerView;
    private TagAdapter tagAdapter;
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
        searchView.setQueryHint("Enter tag name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tagAdapter = new TagAdapter(fm.getHiddenTags(query));
                recyclerView.setAdapter(tagAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tagAdapter = new TagAdapter(fm.getHiddenTags(newText));
                if (searchView.getQuery().length() == 0) {
                    tagAdapter = new TagAdapter(fm.getHiddenTags(""));
                }
                recyclerView.setAdapter(tagAdapter);
                return false;
            }
        });

        fm = FoodManager.get(getContext());

        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_hidden_foods_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        tagAdapter = new TagAdapter(fm.getHiddenTags(null));
        recyclerView.setAdapter(tagAdapter);


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_RESTORE) {
            tagAdapter = new TagAdapter(fm.getHiddenTags(searchView.getQuery().toString()));
            recyclerView.setAdapter(tagAdapter);
            Toast.makeText(getActivity(), "Hidden tag was restored", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_RESTORE_ALL) {
            restoreAllTags();
            Toast.makeText(getActivity(), "All hidden tags have been restored", Toast.LENGTH_SHORT).show();
        }
    }

    public void restoreAllTags() {
        List<Tag> allHiddenTags = fm.getHiddenTags(null);
        for (Tag tag : allHiddenTags) {
            restoreTag(tag);
        }
        //Refreshing the list
        tagAdapter = new TagAdapter(fm.getHiddenTags(searchView.getQuery().toString()));
        recyclerView.setAdapter(tagAdapter);
    }

    public void restoreTag(Tag tag) {
        if (tag.getType() == Tag.TYPE_DEFAULT_HIDDEN) {
            tag.setType(Food.TYPE_DEFAULT);
            fm.updateTag(tag);
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
            optionsMenuButton.setVisibility(View.GONE);
        }

        public void bind(Tag tag) {
            holderTag = tag;
            tagNameTextView.setText(tag.getName());
        }

        @Override
        /*When tag is clicked, SimpleDialog is launched for confirmation*/
        public void onClick(View v) {
            HiddenTagsFragment.SimpleDialog dialog = HiddenTagsFragment.SimpleDialog.newInstance(holderTag.getTagId());
            dialog.setTargetFragment(HiddenTagsFragment.this, REQUEST_RESTORE);
            dialog.show(getFragmentManager(), "OnClick");
        }

    }

    private class TagAdapter extends RecyclerView.Adapter<HiddenTagsFragment.TagHolder> {

        private List<Tag> adapterTags;

        public TagAdapter(List<Tag> tags) {
            adapterTags = tags;
        }

        @Override
        public HiddenTagsFragment.TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new HiddenTagsFragment.TagHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(HiddenTagsFragment.TagHolder holder, int position) {
            holder.bind(adapterTags.get(position));
        }

        @Override
        public int getItemCount() {
            return adapterTags.size();
        }
    }

    public static class SimpleDialog extends DialogFragment {
        Tag tag;
        public static SimpleDialog newInstance (UUID tagId) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_TAG_ID, tagId);

            SimpleDialog fragment = new SimpleDialog();
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            UUID tagId = (UUID) getArguments().getSerializable(ARG_TAG_ID);
            tag = FoodManager.get(getActivity()).getTag(tagId);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Restore tag?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (tag.getType() == Tag.TYPE_DEFAULT_HIDDEN) {
                            tag.setType(Tag.TYPE_DEFAULT);
                        }
                        FoodManager.get(getActivity()).updateTag(tag);
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setMessage(tag.getName())
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
