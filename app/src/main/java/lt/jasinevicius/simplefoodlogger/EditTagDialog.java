package lt.jasinevicius.simplefoodlogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.UUID;

public class EditTagDialog extends DialogFragment {

//    public static final String EXTRA_CREATED_TAG = "created_tag";
    private static final String ARG_TAG_ID = "tag_id";

    private EditText tagNameEditText;
    private UUID tagId;
    private Tag tag;

    public static EditTagDialog newInstance (UUID tagId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAG_ID, tagId);

        EditTagDialog dialog = new EditTagDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_tag, null);

        tagId = (UUID) getArguments().getSerializable(ARG_TAG_ID);
        tag = FoodManager.get(getActivity()).getTag(tagId);

        tagNameEditText = (EditText) v.findViewById(R.id.dialog_add_tag_edittext);
        tagNameEditText.setText(tag.getName());
        if (tag.getType() != Tag.TYPE_CUSTOM) {
            tagNameEditText.setEnabled(false);
        }


        return new AlertDialog.Builder(getActivity())
            .setView(v)
            .setTitle("Edit tag")
            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // OnClick logic handled in OnResume method to allow for validation
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendResult(Activity.RESULT_CANCELED);
                }
            })
            .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();

        if (dialog != null) {
            Button addButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tagNameEditText.getText().toString().trim().equals("")) {
                        Toast.makeText(
                            getActivity(),
                            "Tag name can't be empty!",
                            Toast.LENGTH_SHORT
                        ).show();
                    } else if (tagNameEditText.getText().toString().contains(";")) {
                        Toast.makeText(
                            getActivity(),
                            "Tag name contains illegal character ';'",
                            Toast.LENGTH_SHORT
                        ).show();
                    } else if (
                        !tagNameEditText.getText().toString().equals(tag.getName()) &&
                        FoodManager.get(getActivity()).getTagByName(tagNameEditText.getText().toString()) != null
                    ) {
                        Toast.makeText(
                            getActivity(),
                            "Tag '" + tagNameEditText.getText().toString() + "' already exists!",
                            Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        tag.setName(tagNameEditText.getText().toString());
                        FoodManager.get(getActivity()).updateTag(tag);

                        sendResult(Activity.RESULT_OK);
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

//        Intent intent = new Intent();
//        intent.putExtra(EXTRA_CREATED_TAG, createdTag);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, null);
    }

}
