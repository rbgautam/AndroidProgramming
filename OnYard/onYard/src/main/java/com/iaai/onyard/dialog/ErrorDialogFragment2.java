package com.iaai.onyard.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

public class ErrorDialogFragment2 extends DialogFragment {

    private static final String GENERIC_ERROR_MESSAGE = "OnYard has encountered an error.";
    private static final String DIALOG_MESSAGE_KEY = "error_dialog_message";
    private static final String DIALOG_HANDLER_KEY = "dialog_handler";

    public static ErrorDialogFragment2 newInstance(String message) {
        final ErrorDialogFragment2 dialog = new ErrorDialogFragment2();
        final Bundle args = new Bundle();
        message = message != null ? message : GENERIC_ERROR_MESSAGE;
        args.putString(DIALOG_MESSAGE_KEY, message);
        dialog.setArguments(args);
        return dialog;
    }

    public static ErrorDialogFragment2 newInstance(String message, Handler handler) {
        final ErrorDialogFragment2 dialog = new ErrorDialogFragment2();
        final Bundle args = new Bundle();

        message = message != null ? message : GENERIC_ERROR_MESSAGE;
        args.putString(DIALOG_MESSAGE_KEY, message);

        if (handler != null) {
            args.putParcelable(DIALOG_HANDLER_KEY, handler.obtainMessage());
        }

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
        .setMessage(getArguments().getString(DIALOG_MESSAGE_KEY))
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        }).create();
    }
}
