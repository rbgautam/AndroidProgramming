package com.iaai.onyard.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

/*
 * Displays network error during login attempt
 */
public class NetworkErrorOnLoginDialogFragment extends BaseDialogFragment {

    private static final String NETWORK_ERROR_MESSAGE = "Your device has no network connection. A network connection is required to log in.";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setMessage(NETWORK_ERROR_MESSAGE)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        return;
                    }
                }).create();
    }
}