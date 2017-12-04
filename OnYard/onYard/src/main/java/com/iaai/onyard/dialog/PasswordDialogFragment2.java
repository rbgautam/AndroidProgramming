package com.iaai.onyard.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.iaai.onyard.event.CorrectPasswordEnteredEvent;


public class PasswordDialogFragment2 extends BaseDialogFragment2 {

    private static final String DIALOG_MESSAGE_KEY = "dialog_message";
    private static final String DIALOG_TITLE_KEY = "dialog_title";
    private static final String PASSWORD_KEY = "dialog_password";

    public static PasswordDialogFragment2 newInstance(String message, String title, String password) {
        final PasswordDialogFragment2 dialog = new PasswordDialogFragment2();
        final Bundle args = new Bundle();
        args.putString(DIALOG_MESSAGE_KEY, message);
        args.putString(DIALOG_TITLE_KEY, title);
        args.putString(PASSWORD_KEY, password);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity != null) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(activity);

            alert.setTitle(getArguments().getString(DIALOG_TITLE_KEY));
            alert.setMessage(getArguments().getString(DIALOG_MESSAGE_KEY));
            final String password = getArguments().getString(PASSWORD_KEY);

            final EditText passwordInput = new EditText(activity);
            passwordInput.setHint("Password");
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            alert.setView(passwordInput);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    final String enteredPassword = passwordInput.getText().toString().trim();

                    if (password.equals(enteredPassword)) {
                        getEventBus().post(new CorrectPasswordEnteredEvent());
                    }
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int whichButton) {}
            });

            return alert.create();
        }
        else {
            return null;
        }
    }
}
