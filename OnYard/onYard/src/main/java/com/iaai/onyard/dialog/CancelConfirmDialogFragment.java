package com.iaai.onyard.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.iaai.onyard.event.CancelStockEvent;


public class CancelConfirmDialogFragment extends BaseDialogFragment {

    private static final String DIALOG_MESSAGE = "This action will clear all data that has been collected for this stock. " +
            "Press OK to continue anyway.";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
        .setMessage(DIALOG_MESSAGE)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                getEventBus().post(new CancelStockEvent());
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
