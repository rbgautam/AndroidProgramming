package com.iaai.onyard.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.dialog.ErrorDialogFragment2;
import com.iaai.onyard.dialog.FullSyncConfirmDialogFragment;
import com.iaai.onyard.dialog.PasswordDialogFragment2;
import com.iaai.onyard.event.ConnectivityCheckedEvent;
import com.iaai.onyard.event.CorrectPasswordEnteredEvent;
import com.iaai.onyard.event.ForceFullSyncEvent;
import com.iaai.onyard.sync.HTTPHelper;
import com.iaai.onyard.sync.OnYardSync;
import com.iaai.onyard.sync.SyncHelper;
import com.iaai.onyard.task.CheckConnectivityTask;
import com.iaai.onyard.task.GetPendingCountTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.LogHelper;
import com.squareup.otto.Subscribe;

/**
 * Activity that allows the user to customize their sync preferences.
 */
public class AccountPreferencesActivity extends BasePreferenceActivity {

    /**
     * Generic error dialog for this activity. Finish activity when OK is pressed.
     */
    private static final int SYNC_SETTINGS_ERROR_ID = 1;
    private static final String BRANCH_CHANGE_NETWORK_ERROR_MESSAGE = "Your device has no network connection. "
            + "A network connection is required to choose a branch.";
    private static final String FULL_SYNC_NETWORK_ERROR_MESSAGE = "Your device has no network connection. "
            + "A network connection is required to force a data reset.";
    private static final String PASSWORD_PROMPT_MESSAGE = "Some or all data for stock %s will be lost. To continue, please "
            + "enter the password below and press OK.";
    private static final String PASSWORD_PROMPT_TITLE = "Password required";
    private String newBranchNumber;

    private Dialog dialog;
    /**
     * Inflate Activity UI and set View initial values and listeners.
     * 
     * Called when the activity is starting.
     * 
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);

            final OnYardPreferences preferences = new OnYardPreferences(this);

            addPreferencesFromResource(R.xml.account_sync_preferences);
            preferences.setDefaultSyncPrefs();

            final EditTextPreference voiceCaptureKeywordPref = (EditTextPreference) findPreference(this
                    .getString(R.string.capture_keyword_caption));
            final String voiceCaptureDialog = "Enter voice activation command for camera capture.";
            voiceCaptureKeywordPref.setText(preferences.getCameraCaptureKeyword());
            voiceCaptureKeywordPref.setDialogMessage(voiceCaptureDialog);
            voiceCaptureKeywordPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue){
                    try {
                        preferences.setCameraCaptureKeyword((String) newValue);

                        return true;
                    }
                    catch (final Exception e)
                    {
                        LogHelper.logWarning(getApplicationContext(), e, this.getClass()
                                .getSimpleName());
                        return false;
                    }
                }

            });

            final CheckBoxPreference voiceCommandEnabledPref = (CheckBoxPreference) findPreference(this
                    .getString(R.string.voice_commands_caption));
            voiceCaptureKeywordPref.setEnabled(voiceCommandEnabledPref.isChecked());

            voiceCommandEnabledPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        preferences.setIsVoiceCommandEnabled(((Boolean) newValue).booleanValue());
                        voiceCaptureKeywordPref.setEnabled(((Boolean) newValue).booleanValue());
                        return true;
                    }
                    catch (final Exception e) {
                        LogHelper.logWarning(getApplicationContext(), e, this.getClass()
                                .getSimpleName());
                        return false;
                    }
                }
            });

            final Preference fullSyncPref = findPreference(this
                    .getString(R.string.full_sync_pref));

            fullSyncPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference pref) {

                    final boolean isConnected = HTTPHelper
                            .isNetworkAvailable(getApplicationContext());

                    if (!isConnected) {
                        final ErrorDialogFragment2 errDialog = ErrorDialogFragment2
                                .newInstance(FULL_SYNC_NETWORK_ERROR_MESSAGE);
                        errDialog.show(getFragmentManager(), FragmentTag.NETWORK_ERROR_DIALOG);

                        return false;
                    }

                    final FullSyncConfirmDialogFragment dialog = new FullSyncConfirmDialogFragment();
                    dialog.show(getFragmentManager(), FragmentTag.CONFIRM_FORCED_SYNC_DIALOG);

                    return false;
                }
            });

            final EditTextPreference branchNumberPref = (EditTextPreference) findPreference(
                    this
                    .getString(R.string.override_branch_number_pref));

            String branchPrefDialog = "Choose 0 to use automatic branch.";
            if (preferences.getIpBranchNumber().equals(OnYard.DEFAULT_BRANCH_NUMBER)) {
                branchPrefDialog += "\nAutomatic branch will be set at login.";
            }
            else {
                branchPrefDialog += "\nCurrent automatic branch: "
                        + preferences.getIpBranchName();
            }
            branchNumberPref.setDialogMessage(branchPrefDialog);

            branchNumberPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference pref) {
                    final boolean isLoggedIn = AuthenticationHelper
                            .isAnyUserLoggedIn(getApplicationContext().getContentResolver());

                    final boolean isConnected = HTTPHelper
                            .isNetworkAvailable(getApplicationContext());

                    if (isLoggedIn && !isConnected) {
                        final ErrorDialogFragment2 errDialog = ErrorDialogFragment2
                                .newInstance(BRANCH_CHANGE_NETWORK_ERROR_MESSAGE);
                        errDialog.show(getFragmentManager(),
                                FragmentTag.NETWORK_ERROR_DIALOG);

                        branchNumberPref.getDialog().dismiss();
                    }
                    return false;
                }
            });

            branchNumberPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    final boolean isLoggedIn = AuthenticationHelper
                            .isAnyUserLoggedIn(getApplicationContext().getContentResolver());
                    final boolean isConnected = HTTPHelper
                            .isNetworkAvailable(getApplicationContext());

                    if (isLoggedIn) {
                        if (isConnected) {
                            newBranchNumber = newValue.toString();
                            if (!newBranchNumber.equals(preferences.getOverrideBranchNumber())) {
                                new CheckConnectivityTask().execute(getApplicationContext(),
                                        getEventBus());
                            }
                        }
                        else {
                            final ErrorDialogFragment2 errDialog = ErrorDialogFragment2
                                    .newInstance(BRANCH_CHANGE_NETWORK_ERROR_MESSAGE);
                            errDialog.show(getFragmentManager(), FragmentTag.NETWORK_ERROR_DIALOG);
                        }
                    }
                    else {
                        newBranchNumber = newValue.toString();
                        if (!newBranchNumber.equals(preferences.getOverrideBranchNumber())) {
                            new CheckConnectivityTask().execute(getApplicationContext(),
                                    getEventBus());
                        }
                    }
                    return false;
                }
            });

            final CheckBoxPreference deleteOldestPendingDataPref = (CheckBoxPreference) findPreference(this
                    .getString(R.string.delete_oldest_pending_sync_pref));
            deleteOldestPendingDataPref.setChecked(preferences.shouldDeleteOldestPendingSync());
            deleteOldestPendingDataPref
            .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        if (((Boolean) newValue).booleanValue() == true) {
                            final String stockNumber = new OnYardSync(
                                    getApplicationContext())
                            .getOldestDataItemPendingSyncStockNum();
                            final PasswordDialogFragment2 dialog = PasswordDialogFragment2
                                    .newInstance(String.format(PASSWORD_PROMPT_MESSAGE,
                                            stockNumber), PASSWORD_PROMPT_TITLE,
                                            OnYard.DELETE_OLDEST_DATA_PASSWORD);
                            dialog.show(getFragmentManager(), FragmentTag.PASSWORD_DIALOG);
                        }
                        else {
                            preferences.setDeleteOldestPendingSync(false);
                            deleteOldestPendingDataPref.setChecked(false);
                        }

                        return false;
                    }
                    catch (final Exception e) {
                        LogHelper.logWarning(getApplicationContext(), e, this.getClass()
                                .getSimpleName());
                        return false;
                    }
                }
            });

            new GetPendingCountTask().execute(new Object[] { getApplicationContext(), this });
        }
        catch (final Exception e)
        {
            showSyncSettingsErrorDialog();
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!new OnYardPreferences(this).getEffectiveBranchNumber().equals(
                OnYard.DEFAULT_BRANCH_NUMBER)) {
        }
    }

    @Subscribe
    public void onCorrectPasswordEntered(CorrectPasswordEnteredEvent event) {
        final OnYardPreferences preferences = new OnYardPreferences(getApplicationContext());
        final CheckBoxPreference deleteOldestPendingDataPref = (CheckBoxPreference) findPreference(this
                .getString(R.string.delete_oldest_pending_sync_pref));

        preferences.setDeleteOldestPendingSync(true);
        deleteOldestPendingDataPref.setChecked(true);
    }

    /**
     * If using showDialog(id), the activity will call through to this method the first
     * time, and hang onto it thereafter. Any dialog that is created by this method will
     * automatically be saved and restored, including whether it is showing.
     * 
     * @param id The id of the dialog.
     * @param args The dialog arguments provided to showDialog(int, Bundle).
     * @return The dialog. If null is returned, the dialog will not be created.
     */
    @Override
    protected Dialog onCreateDialog(int id, Bundle args)
    {
        try
        {
            AlertDialog.Builder builder;
            switch(id)
            {
                case SYNC_SETTINGS_ERROR_ID:
                    builder = new AlertDialog.Builder(this);
                    builder.setMessage("There was an error while loading the Sync " +
                            "Settings. Please try again.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    dialog = builder.create();
                    break;

                default:
                    dialog = null;
                    break;
            }
            return dialog;
        }
        catch (final Exception e)
        {
            LogHelper.logWarning(getApplicationContext(), e, this.getClass().getSimpleName());
            finish();
            return new Dialog(this);
        }
    }

    /**
     * Show a generic sync error dialog. The activity will finish when "OK" is pressed.
     */
    private void showSyncSettingsErrorDialog()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    showDialog(SYNC_SETTINGS_ERROR_ID);
                }
                catch (final Exception e)
                {
                    LogHelper.logWarning(getApplicationContext(), e, this.getClass()
                            .getSimpleName());
                    finish();
                }
            }
        });
    }

    @Subscribe
    public void onConnectivityChecked(ConnectivityCheckedEvent event) {
        try {
            final OnYardPreferences preferences = new OnYardPreferences(this);

            if (AuthenticationHelper
                    .isAnyUserLoggedIn(getApplicationContext().getContentResolver())
                    && !event.isConnected()) {

                final ErrorDialogFragment2 errDialog = ErrorDialogFragment2
                        .newInstance(BRANCH_CHANGE_NETWORK_ERROR_MESSAGE);
                errDialog.show(getFragmentManager(),
                        FragmentTag.NETWORK_ERROR_DIALOG);
            }
            else {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            preferences.setOverrideBranchNumber(newBranchNumber);
                            SyncHelper.requestFullSync(getApplicationContext(), true);
                        }
                        catch (final Exception e) {
                            showSyncSettingsErrorDialog();
                            LogHelper.logError(getApplicationContext(), e, this.getClass()
                                    .getSimpleName());
                        }
                    }
                }).start();
                final Toast branchChangedToast = Toast.makeText(
                        AccountPreferencesActivity.this, "Branch number changed.",
                        Toast.LENGTH_SHORT);
                branchChangedToast.show();

                final EditTextPreference branchNumberPref = (EditTextPreference) findPreference(getString(R.string.override_branch_number_pref));
                branchNumberPref.setText(newBranchNumber);
            }
        }
        catch (final Exception e) {
            showSyncSettingsErrorDialog();
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
        }
    }

    @Subscribe
    public void onFullSyncRequested(ForceFullSyncEvent event) {
        SyncHelper.requestFullSync(getApplicationContext(), true);
    }

    /**
     * @param listPref
     * @param listValue
     * @return listEntry
     */
    private String findListPrefEntryByValue(ListPreference listPref, String listValue) {

        final int newValueIndex = listPref.findIndexOfValue(listValue);
        final String listEntry = listPref.getEntries()[newValueIndex].toString();
        return listEntry;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            final MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_settings, menu);

            final ActionBar actionBar = getActionBar();
            actionBar.setDisplayShowHomeEnabled(true);

            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                    R.color.settings_gray)));

            actionBar.setDisplayShowTitleEnabled(true);

            actionBar.setTitle("Settings");

            actionBar.setIcon(R.drawable.ic_action_settings_light);

            return true;
        }
        catch (final Exception e) {
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
            return false;
        }
    }

}
