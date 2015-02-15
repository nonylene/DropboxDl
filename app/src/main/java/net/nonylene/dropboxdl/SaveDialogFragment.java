package net.nonylene.dropboxdl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SaveDialogFragment extends DialogFragment {

    public interface SaveDialogListener {
        public void onPositive(String filename);
        public void onCancel();
    }

    private SaveDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (SaveDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle = getArguments();
        String dirname = bundle.getString("dir");
        // set filename (follow setting)
        String filename = bundle.getString("filename");
        // set custom view
        View view = View.inflate(getActivity(), R.layout.save_path, null);
        // set directory to text
        TextView textView = (TextView) view.findViewById(R.id.path_TextView);
        textView.setText(dirname);
        // set pre_name to edit_text
        EditText editText = (EditText) view.findViewById(R.id.path_EditText);
        editText.setText(filename);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(getString(R.string.save_dialog_title))
                .setPositiveButton(getString(R.string.save_dialog_positive), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get filename
                        EditText editText = (EditText) getDialog().findViewById(R.id.path_EditText);
                        String filename = editText.getText().toString();
                        listener.onPositive(filename);
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancel();
                    }
                });
        return builder.create();
    }
}
