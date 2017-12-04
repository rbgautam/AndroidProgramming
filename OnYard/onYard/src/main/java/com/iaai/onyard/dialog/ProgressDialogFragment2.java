package com.iaai.onyard.dialog;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class ProgressDialogFragment2 extends DialogFragment {

    private static final String GENERIC_PROGRESS_MESSAGE = "Loading. Please wait...";
    private static final String DIALOG_MESSAGE_KEY = "error_dialog_message";
    private static final String DIALOG_TITLE_KEY = "error_dialog_title";

    public static ProgressDialogFragment newInstance(String message) {
        final ProgressDialogFragment dialog = new ProgressDialogFragment();
        final Bundle args = new Bundle();
        message = message != null ? message : GENERIC_PROGRESS_MESSAGE;
        args.putString(DIALOG_MESSAGE_KEY, message);
        dialog.setArguments(args);
        return dialog;
    }

    public static ProgressDialogFragment newInstance(String message, String title) {
        final ProgressDialogFragment dialog = new ProgressDialogFragment();
        final Bundle args = new Bundle();
        message = message != null ? message : GENERIC_PROGRESS_MESSAGE;
        args.putString(DIALOG_MESSAGE_KEY, message);
        args.putString(DIALOG_TITLE_KEY, title);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());

        dialog.setMessage(GENERIC_PROGRESS_MESSAGE);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        if (getArguments() != null) {
            final String message = getArguments().getString(DIALOG_MESSAGE_KEY);
            if (message != null) {
                dialog.setMessage(message);
            }

            final String title = getArguments().getString(DIALOG_TITLE_KEY);
            if (title != null) {
                dialog.setTitle(title);
            }
        }

        return dialog;
    }
}
