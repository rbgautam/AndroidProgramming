package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.CheckinId;
import com.iaai.onyard.application.OnYard.DialogTitle;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.classes.OnYardField;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.dialog.ConfirmVinDialogFragment;
import com.iaai.onyard.dialog.InvalidVinDialogFragment;
import com.iaai.onyard.event.CheckinFieldOptionChosenEvent;
import com.iaai.onyard.event.CheckinInputEnteredEvent;
import com.iaai.onyard.event.KeyboardBackPressedEvent;
import com.iaai.onyard.event.SalvageTypeChangedEvent;
import com.iaai.onyard.event.VinScanCanceledEvent;
import com.iaai.onyard.event.VinScanEnteredEvent;
import com.squareup.otto.Subscribe;

public class CheckinInputFragment extends InputFragment {

    private static final int SCAN_BARCODE_REQUEST_CODE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (getActivity() != null) {
                if (requestCode == SCAN_BARCODE_REQUEST_CODE
                        && resultCode == Activity.RESULT_OK) {
                    String scannedVin = data.getStringExtra("SCAN_RESULT");

                    final Pattern pattern = Pattern.compile(".*\\W+.*");
                    final Matcher matcher = pattern.matcher(scannedVin);
                    if (matcher.find()) {
                        showErrorDialog(ErrorMessage.NON_ALPHANUM_VIN, DialogTitle.INVALID_VIN);
                        return;
                    }

                    if (scannedVin.length() != 17) {
                        if (scannedVin.length() == 18) {
                            if (scannedVin.charAt(0) == 'I' || scannedVin.charAt(0) == 'i') {
                                scannedVin = scannedVin.substring(1);
                            }
                            else {
                                scannedVin = scannedVin.substring(0, 17);
                            }
                        }
                        else {
                            showErrorDialog(ErrorMessage.INCORRECT_LENGTH_VIN, DialogTitle.INVALID_VIN);
                            return;
                        }
                    }

                    final String existingVin;
                    if (mCurrentField.hasSelection()) {
                        existingVin = mCurrentField.getEnteredValue();
                    }
                    else {
                        existingVin = getSessionData().getVehicleInfo().getVIN();
                    }
                    if (existingVin != null && existingVin.length() == 17) {
                        int numDifferentChars = 0;
                        for (int index = 11; index < 17; index++) {
                            if (existingVin.charAt(index) != scannedVin.charAt(index)) {
                                numDifferentChars++;
                            }
                        }

                        if (numDifferentChars > 2) {
                            ConfirmVinDialogFragment.newInstance(existingVin, scannedVin).show(
                                    getChildFragmentManager(), FragmentTag.CONFIRM_VIN_DIALOG);
                            return;
                        }
                    }

                    getEventBus().post(new VinScanEnteredEvent(scannedVin));
                }
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
        catch (final Exception e) {}
    }

    @Subscribe
    public void onVinScanCancel(VinScanCanceledEvent event) {
        resetLayout();
        createAlphaNumericEditText();
    }

    @Override
    public void createInputFields(OnYardField field, OnYardFieldOption guessedOption,
            int screenHeightPixels) {
        resetLayout();
        mCurrentField = field;

        switch (mCurrentField.getInputType()) {
            case LIST:
            case CHECKBOX:
                createListView(guessedOption);
                break;
            case NUMERIC:
                createNumericEditText();
                break;
            case ALPHANUMERIC:
                if (mCurrentField.getId() == CheckinId.VIN_WITH_SCAN) {
                    createVinScanButtons(getDesiredVinInputHeight(screenHeightPixels));
                }
                else {
                    createAlphaNumericEditText();
                }
                break;
            case TEXT:
                createEditText();
                break;
            default:
                break;
        }
    }

    private int getDesiredVinInputHeight(int screenHeightPixels) {
        return (int) (screenHeightPixels * 0.4);
    }

    private void createVinScanButtons(int desiredInputHeight) {
        hideSoftKeyboard();
        final LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0);
        buttonParams.weight = 1;

        final Activity activity = getActivity();
        if (activity != null) {
            final Button scanButton = new Button(activity);
            scanButton.setText(activity.getString(R.string.scan_vin_barcode));
            scanButton.setTextSize(25);
            scanButton.setLayoutParams(buttonParams);
            scanButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(OnYard.ZXING_SCAN_ACTION);
                    intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                    getParentFragment().startActivityForResult(intent, SCAN_BARCODE_REQUEST_CODE);
                }
            });

            final Button manualButton = new Button(activity);
            manualButton.setText(activity.getString(R.string.manual_verification));
            manualButton.setTextSize(25);
            manualButton.setLayoutParams(buttonParams);
            manualButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    resetLayout();
                    createAlphaNumericEditText();
                }
            });


            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, desiredInputHeight);
            mLayout.setLayoutParams(params);

            mLayout.addView(scanButton);
            mLayout.addView(manualButton);
        }
    }

    @Override
    protected void populateEditText(EditText editText) {
        showSoftKeyboard();
        editText.requestFocus();
        editText.setOnEditorActionListener(getEnterKeyListener());

        if (mCurrentField.hasSelection()) {
            editText.setText(mCurrentField.getEnteredValue());
            editText.setSelection(editText.getText().length());
        }
        else {
            if (mCurrentField.getId() == CheckinId.VIN_WITH_SCAN
                    || mCurrentField.getId() == CheckinId.VIN_WITH_NO_SCAN) {
                if (getActivity() != null) {
                    final String existingVin = getSessionData().getVehicleInfo().getVIN();
                    if (existingVin != null && !existingVin.isEmpty()) {
                        editText.setText(existingVin);
                        editText.setSelection(editText.getText().length());
                    }
                }
            }
        }
    }

    @Override
    @Subscribe
    public void onKeyboardBackPressed(KeyboardBackPressedEvent event) {
        try {
            hideSoftKeyboard();
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    protected OnItemClickListener getListItemClickListener(
            final ArrayList<OnYardFieldOption> optionsList) {
        return new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    final OnYardFieldOption clickedOption = optionsList.get(position);
                    if (getActivity() != null) {
                        if (mCurrentField.getId() == CheckinId.SALVAGE_TYPE) {
                            getEventBus().post(new SalvageTypeChangedEvent(clickedOption.getValue()));
                        }
                        else {
                            getEventBus().post(new CheckinFieldOptionChosenEvent(clickedOption));
                        }
                    }
                }
                catch (final Exception e) {
                    showFatalErrorDialog(ErrorMessage.CHECKIN);
                    logError(e);
                }
            }
        };
    }

    @Override
    protected OnEditorActionListener getEnterKeyListener() {
        return new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        final EditText editText = (EditText) v;
                        final String input = editText.getText().toString().trim();

                        if (getActivity() != null && isInputValid(input)) {
                            getEventBus().post(new CheckinInputEnteredEvent(input));
                        }

                        return true;
                    }
                    else {
                        return false;
                    }
                }
                catch (final Exception e) {
                    showFatalErrorDialog(ErrorMessage.CHECKIN);
                    logError(e);
                    return false;
                }
            }
        };
    }

    protected boolean isInputValid(String input) {
        if (getActivity() != null) {
            if ((mCurrentField.getId() == CheckinId.VIN_WITH_SCAN || mCurrentField.getId() == CheckinId.VIN_WITH_NO_SCAN)
                    && input.length() == 17
                    && getSessionData().getVehicleInfo().getYear() >= 1981
                    && (input.contains("I") || input.contains("O") || input.contains("Q")
                            || input.contains("i") || input.contains("o") || input.contains("q"))) {

                InvalidVinDialogFragment.newInstance(input).show(getChildFragmentManager(),
                        FragmentTag.INVALID_VIN_DIALOG);

                return false;
            }
        }
        return true;
    }
}
