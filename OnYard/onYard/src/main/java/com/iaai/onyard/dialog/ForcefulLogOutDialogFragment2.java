package com.iaai.onyard.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.iaai.onyard.activity.SearchPagerActivity;
import com.iaai.onyard.application.OnYard;

public class ForcefulLogOutDialogFragment2 extends DialogFragment {

    private static final String DEFAULT_LOGOUT_MESSAGE = OnYard.DEFAULT_FORCEFUL_LOGOUT_MESSAGE;
    private static final String LOGOUT_REASON_KEY = "logout_dialog_message";

    public static ForcefulLogOutDialogFragment2 newInstance(String reason) {
        final ForcefulLogOutDialogFragment2 dialog = new ForcefulLogOutDialogFragment2();
        final Bundle args = new Bundle();

        if (reason == null || reason.isEmpty()) {
            reason = DEFAULT_LOGOUT_MESSAGE;
        }
        args.putString(LOGOUT_REASON_KEY, reason);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return new AlertDialog.Builder(getActivity())
        .setMessage(getArguments().getString(LOGOUT_REASON_KEY))
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                final Intent intent = new Intent(getActivity(), SearchPagerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
                startActivity(intent);
                return;
            }
        }).create();
    }
}