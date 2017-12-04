package com.iaai.onyard.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.event.OverrideVinEvent;
import com.iaai.onyard.event.VinScanCanceledEvent;

public class InvalidVinDialogFragment extends BaseDialogFragment {

    private static final String DIALOG_MESSAGE = OnYard.ErrorMessage.INVALID_CHARS_VIN;

    private static final String ENTERED_VIN_KEY = "entered_vin";

    public static InvalidVinDialogFragment newInstance(String enteredVin) {
        final InvalidVinDialogFragment dialog = new InvalidVinDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ENTERED_VIN_KEY, enteredVin);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String enteredVin = getArguments().getString(ENTERED_VIN_KEY);

        return new AlertDialog.Builder(getActivity())
        .setMessage(DIALOG_MESSAGE)
        .setPositiveButton(getActivity().getString(R.string.override_vin),
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                getEventBus().post(new OverrideVinEvent(enteredVin));
            }
        })
        .setNegativeButton(getActivity().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                getEventBus().post(new VinScanCanceledEvent());
            }
        }).create();
    }
}

