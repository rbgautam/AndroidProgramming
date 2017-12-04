package com.iaai.onyard.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.iaai.onyard.event.ToggleProgressDialogEvent;
import com.iaai.onyard.task.UserLogOutTask;

public class LogoutConfirmDialogFragment extends BaseDialogFragment {

    private static final String DIALOG_MESSAGE = "Please press OK to confirm logout.";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle("Logout Confirmation")
                .setMessage(DIALOG_MESSAGE).setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getEventBus().post(new ToggleProgressDialogEvent(true));

                        new UserLogOutTask().execute(getActivity().getApplicationContext(),
                                getEventBus());
                    }
                }).setNegativeButton("Cancel", null).show();
    }
}
