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

public class AddTagDialog extends DialogFragment {

    public static final String EXTRA_CREATED_TAG = "created_tag";

    private EditText tagNameEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_tag, null);

        tagNameEditText = (EditText) v.findViewById(R.id.dialog_add_tag_edittext);

        return new AlertDialog.Builder(getActivity())
            .setView(v)
            .setTitle("Add a new tag")
            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // OnClick logic handled in OnResume method to allow for validation
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendResult(Activity.RESULT_CANCELED, null);
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
                    } else if (FoodManager.get(getActivity()).getTagByName(tagNameEditText.getText().toString()) != null) {
                        Toast.makeText(
                            getActivity(),
                            "Tag '" + tagNameEditText.getText().toString() + "' already exists!",
                            Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Tag tag = new Tag();
                        tag.setName(tagNameEditText.getText().toString());
                        tag.setType(Tag.TYPE_CUSTOM);
                        tag.setOrderId(FoodManager.get(getActivity()).getTagMaxOrderId() + 1);
                        tag.setColor("#1a1a1a"); // TODO add color picker if i ever decide to implement colored tags

                        FoodManager.get(getActivity()).addTag(tag);

                        sendResult(Activity.RESULT_OK, tag);
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void sendResult(int resultCode, Tag createdTag) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_CREATED_TAG, createdTag);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
