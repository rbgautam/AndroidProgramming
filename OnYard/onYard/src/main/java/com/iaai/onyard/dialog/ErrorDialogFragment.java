package com.iaai.onyard.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class ErrorDialogFragment extends BaseDialogFragment {

    private static final String GENERIC_ERROR_MESSAGE = "OnYard has encountered an error.";
    private static final String DIALOG_MESSAGE_KEY = "error_dialog_message";
    private static final String DIALOG_TITLE_KEY = "error_dialog_title";

    public static ErrorDialogFragment newInstance(String message) {
        final ErrorDialogFragment dialog = new ErrorDialogFragment();
        final Bundle args = new Bundle();
        message = message != null ? message : GENERIC_ERROR_MESSAGE;
        args.putString(DIALOG_MESSAGE_KEY, message);
        dialog.setArguments(args);
        return dialog;
    }

    public static ErrorDialogFragment newInstance(String message, String title) {
        final ErrorDialogFragment dialog = new ErrorDialogFragment();
        final Bundle args = new Bundle();
        message = message != null ? message : GENERIC_ERROR_MESSAGE;
        args.putString(DIALOG_MESSAGE_KEY, message);
        args.putString(DIALOG_TITLE_KEY, title);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        .setMessage(getArguments().getString(DIALOG_MESSAGE_KEY))
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        });

        final String title = getArguments().getString(DIALOG_TITLE_KEY);
        if (title != null) {
            builder.setTitle(title);
        }

        return builder.create();
    }
}
