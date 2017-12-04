package com.iaai.onyard.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaai.onyard.R;

/**
 * Helper class that deals with saving and modifying preferences.
 */
public class PreferenceHelper {
	
	/**
	 * Check if user has enabled sync-on-start.
	 * 
	 * @param context The current context.
	 * @return False if the user has chosen not to sync on app start, true otherwise.
	 */
	public static boolean getSyncOnStart(Context context)
	{
		SharedPreferences sp = getSharedPrefs(context);
        
        boolean tmp = sp.getBoolean(context.getString(R.string.sync_when_opened_pref), true);
        return tmp;
	}
	
	public static void setSyncOnStart(Context context, boolean value)
	{
        SharedPreferences sp = getSharedPrefs(context);
		
		sp.edit().putBoolean(context.getString(R.string.sync_when_opened_pref), value).apply();
	}
	
	/**
	 * Check whether the nightly sync alarm has been set.
	 * 
	 * @param context The current context.
	 * @return True if the alarm has been set, false otherwise.
	 */
	public static boolean getAlarmIsSet(Context context)
	{		
		SharedPreferences sp = getSharedPrefs(context);
		
		if(!sp.contains(context.getString(R.string.is_alarm_set_pref)))
			setAlarmIsSet(context, false);
		
		return sp.getBoolean(context.getString(R.string.is_alarm_set_pref), false);
	}
	
	/**
	 * Update the IsAlarmSet preference.
	 * 
	 * @param context The current context.
	 * @param value The value to which to set the IsAlarmSet preference.
	 */
	public static void setAlarmIsSet(Context context, boolean value)
	{		
		SharedPreferences sp = getSharedPrefs(context);
		
		sp.edit().putBoolean(context.getString(R.string.is_alarm_set_pref), value).apply();
	}
	
	/**
	 * Update the Branch Number preference.
	 * 
	 * @param context The current context.
	 * @param value The value to which to set the Branch Number preference.
	 */
	public static void setBranchNumber(Context context, String value)
	{
	    SharedPreferences sp = getSharedPrefs(context);

	    sp.edit().putString(context.getString(R.string.branch_number_pref), value).apply();
	}
	
    /**
     * Check whether the Branch Number preference has been set.
     * 
     * @param context The current context.
     * @return True if the branch number has been set, false otherwise.
     */
	public static String getBranchNumber(Context context)
	{
	    SharedPreferences sp = getSharedPrefs(context);

	    if(!sp.contains(context.getString(R.string.branch_number_pref)))
	        setBranchNumber(context, "0");

	    return sp.getString(context.getString(R.string.branch_number_pref), "0");
	}
	
	/**
	 * Initialize the user preferences by setting the default preference values.
	 * 
	 * @param context The current context.
	 */
	public static void setDefaultSyncPrefs(Context context)
	{
		PreferenceManager.setDefaultValues(context, 
				context.getString(R.string.onyard_shared_prefs_name), Context.MODE_MULTI_PROCESS, 
				R.xml.account_sync_preferences, false);
	}

	/**
	 * Get the default user preferences.
	 * 
	 * @param context The current context.
	 * @return The default preferences as a SharedPreferences object.
	 */
	private static SharedPreferences getSharedPrefs(Context context)
	{
		return context.getSharedPreferences(context.getString(R.string.onyard_shared_prefs_name), 
				Context.MODE_MULTI_PROCESS);
	}
}
