package com.iaai.onyard.activity;

import com.iaai.onyard.R;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.PreferenceHelper;
import com.iaai.onyard.utility.SyncHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//TODO: don't show OnYard activities when back button pressed
/**
 * Activity that allows the user to customize their sync preferences.
 */
public class AccountPreferencesActivity extends PreferenceActivity {
	
	/**
	 * Generic error dialog for this activity. Finish activity when OK is pressed.
	 */
	private static final int SYNC_SETTINGS_ERROR_ID = 1;
	/**
	 * Dialog asking the user for a password to change the branch number.
	 */
	private static final int BRANCH_NUMBER_PASSWORD_DIALOG = 2;
	
	private String newBranchNumber;
	
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

			addPreferencesFromResource(R.xml.account_sync_preferences);
			PreferenceHelper.setDefaultSyncPrefs(this);
			
			final CheckBoxPreference syncOnStartPref = (CheckBoxPreference) findPreference(
					this.getString(R.string.sync_when_opened_pref));
			syncOnStartPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() 
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {		
					try
					{
						PreferenceHelper.setSyncOnStart(AccountPreferencesActivity.this,
								((Boolean) newValue).booleanValue());
						return true;
					}
					catch (Exception e)
					{
						LogHelper.logWarning(AccountPreferencesActivity.this, e);
						return false;
					}
				}
			});

			final ListPreference intervalPref = (ListPreference) findPreference(
					this.getString(R.string.sync_interval_pref));
			intervalPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() 
			{
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {		
					try
					{
						SyncHelper.updateSyncInterval(AccountPreferencesActivity.this, 
								newValue.toString());
	
						return true;
					}
					catch (Exception e)
					{
						LogHelper.logWarning(AccountPreferencesActivity.this, e);
						return false;
					}
				}
			});

			final EditTextPreference branchNumberPref = (EditTextPreference) findPreference(
			        this.getString(R.string.branch_number_pref));
			branchNumberPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    newBranchNumber = newValue.toString();
                    showBranchNumberPasswordDialog();
                    
                    return false;
                }
            });
		}
		catch (Exception e)
		{
			showSyncSettingsErrorDialog();
			LogHelper.logError(this, e);
		}
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
    		final Dialog dialog;
    		AlertDialog.Builder builder;
    		switch(id)
    		{
	    		case SYNC_SETTINGS_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading the Sync " +
		    				"Settings. Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		        	   finish();
		    		           }
		    		       });
		    		dialog = builder.create();
	    			break;
                case BRANCH_NUMBER_PASSWORD_DIALOG:
                    dialog = new Dialog(this);
                    dialog.setContentView(R.layout.custom_password_dialog);
                    dialog.setTitle("Change Branch Number");
    
                    final Button btnOK = (Button) dialog.findViewById(R.id.btn_password_ok);
                    Button btnCancel = (Button) dialog.findViewById(R.id.btn_password_cancel);
                    final EditText txtPassword = (EditText) dialog.findViewById(R.id.txt_password);
                    final TextView lblInvalid = (TextView) dialog.findViewById(R.id.lbl_invalid_password);
                    
                    txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(hasFocus)
                                dialog.getWindow().setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            
                        }
                    });
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try
                            {
                                final String password = txtPassword.getText().toString();
                                if(isCorrectPassword(password))
                                {
                                    lblInvalid.setVisibility(View.INVISIBLE);
                                    dialog.dismiss();
                                    new Thread(new Runnable() {
                                        public void run() {
                                            try
                                            {
                                                PreferenceHelper.setBranchNumber(
                                                        AccountPreferencesActivity.this, newBranchNumber);
                                                SyncHelper.requestNightlySync(AccountPreferencesActivity.this);
                                            }
                                            catch (Exception e)
                                            {
                                                showSyncSettingsErrorDialog();
                                                LogHelper.logError(AccountPreferencesActivity.this, e);
                                            }
                                        }
                                    }).start();
                                    Toast branchChangedToast = Toast.makeText(AccountPreferencesActivity.this,
                                            "Branch number changed.", Toast.LENGTH_SHORT);
                                    branchChangedToast.show();
                                    
                                    EditTextPreference branchNumberPref = (EditTextPreference) findPreference(
                                    		AccountPreferencesActivity.this.getString(R.string.branch_number_pref));
                                    branchNumberPref.setText(newBranchNumber);
                                }
                                else
                                {
                                    lblInvalid.setVisibility(View.VISIBLE);
                                }
                            }
                            catch (Exception e)
                            {
                                showSyncSettingsErrorDialog();
                                LogHelper.logError(AccountPreferencesActivity.this, e);
                            }
                        }
                    });
                    txtPassword.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                    (keyCode == KeyEvent.KEYCODE_ENTER)) 
                            {
                                        btnOK.performClick();
                                return true;
                            }
                                return false;
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            lblInvalid.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });
                    break;
	    		default:
	    			dialog = null;
	    			break;
    		}
    		return dialog;
    	}
    	catch (Exception e)
    	{
    		LogHelper.logWarning(this, e);
    		finish();
    		return new Dialog(this);
    	}
    }
    
    /**
     * Check if the password entered matches the OnYard admin password.
     * 
     * @param passwordEntered The password that the user entered.
     * @return True the password is correct, false otherwise.
     */
    private boolean isCorrectPassword(String passwordEntered)
    {
        return passwordEntered.equals(this.getString(R.string.admin_password));
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
				catch (Exception e)
				{
					LogHelper.logWarning(AccountPreferencesActivity.this, e);
					finish();
				}
			}
		});
    }
    
    /**
     * Show a dialog asking the user for a password to change the branch number.
     */
    private void showBranchNumberPasswordDialog()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    showDialog(BRANCH_NUMBER_PASSWORD_DIALOG);
                }
                catch (Exception e)
                {
                    LogHelper.logWarning(AccountPreferencesActivity.this, e);
                    finish();
                }
            }
        });
    }
}
