package com.iaai.onyard.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.event.VinScanCanceledEvent;
import com.iaai.onyard.event.VinScanEnteredEvent;


public class ConfirmVinDialogFragment extends BaseDialogFragment {

    @InjectView(R.id.existing_vin)
    TextView mExistingVin;
    @InjectView(R.id.scanned_vin)
    TextView mScannedVin;

    private static final String DIALOG_MESSAGE = "The scanned VIN is different from the existing VIN. Do you want to overwrite the existing VIN?";
    private static final String EXISTING_VIN_KEY = "existing_vin";
    private static final String SCANNED_VIN_KEY = "scanned_vin";

    public static ConfirmVinDialogFragment newInstance(String existingVin, String scannedVin) {
        final ConfirmVinDialogFragment dialog = new ConfirmVinDialogFragment();
        final Bundle args = new Bundle();
        args.putString(EXISTING_VIN_KEY, existingVin);
        args.putString(SCANNED_VIN_KEY, scannedVin);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.confirm_vin_dialog, null);
        ButterKnife.inject(this, view);

        final String existingVin = getArguments().getString(EXISTING_VIN_KEY);
        final String scannedVin = getArguments().getString(SCANNED_VIN_KEY);

        mExistingVin.setText(existingVin);
        mScannedVin.setText(scannedVin);

        return  new AlertDialog.Builder(getActivity())
        .setView(view)
        .setMessage(DIALOG_MESSAGE)
        .setPositiveButton(getActivity().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                getEventBus().post(new VinScanEnteredEvent(scannedVin));
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
