package com.iaai.onyard.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.iaai.onyard.application.OnYardApplication;
import com.iaai.onyard.event.ForceFullSyncEvent;

public class FullSyncConfirmDialogFragment extends DialogFragment {

    private static final String DIALOG_MESSAGE = "You will now be logged off and data will be reset. Do you want to continue?";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
        .setMessage(DIALOG_MESSAGE)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                ((OnYardApplication) getActivity().getApplicationContext()).getEventBus()
                .post(new ForceFullSyncEvent());
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                return;
            }
        }).create();
    }
}
