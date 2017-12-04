package com.iaai.onyard.utility;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.ColorInfo;
import com.iaai.onyard.classes.DamageInfo;
import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.classes.StatusInfo;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.sync.AlarmReceiver;

/**
 * Helper class containing methods that deal with data syncing.
 */
public class SyncHelper {
	
    /**
     * Check if key exists in Config table.
     * 
     * @param context The current context.
     * @param configKey The config key for which to search.
     * @return True if key exists, false otherwise.
     */
    public static boolean isConfigKeyInDB(Context context, String configKey)
    {
        Cursor cursor = null;
        try
        {
            if(configKey == null)
                return false;
            
            cursor = context.getContentResolver().query(
                    Uri.withAppendedPath(OnYard.Config.CONTENT_KEY_URI_BASE, 
                            configKey),
                    new String[]{ OnYard.Config.COLUMN_NAME_KEY },
                    null,
                    null,
                    null
            );
            
            return cursor.moveToFirst();
        }
        finally
        {
            cursor.close();
        }
    }
    
    /**
     * Create the OnYard account used for syncing.
     * 
     * @param context The current context.
     */
    public static void createOnYardAccount(Context context)
    {
    	Account account = new Account(context.getString(R.string.app_name), 
    			context.getString(R.string.account_type));
    	AccountManager am = AccountManager.get(context);

    	am.addAccountExplicitly(account, null, null);
    }
	
	/**
	 * Enable the background syncing for OnYard.
	 * 
	 * @param context The current context.
	 */
	public static void enableSync(Context context)
	{
		ContentResolver.setSyncAutomatically(getOnYardAccount(context), OnYard.AUTHORITY, true);
	}
	
	/**
	 * Create the Nightly Sync by scheduling an alarm to wake up the device and trigger
	 * the AlarmReceiver at 5:30 UTC plus a random number of milliseconds between 0 and 360000.
	 * 
	 * @param context The current context.
	 */
	public static void setNightlySync(Context context)
	{
	    Calendar updateTime = Calendar.getInstance();
	    updateTime.setTimeZone(TimeZone.getTimeZone("UTC"));
	    updateTime.set(Calendar.HOUR_OF_DAY, 5); 
	    updateTime.set(Calendar.MINUTE, 30);
	 
	    Intent downloader = new Intent(context, AlarmReceiver.class);
	    PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
	            0, downloader, PendingIntent.FLAG_UPDATE_CURRENT);
	    
	    AlarmManager alarms = (AlarmManager) context.getSystemService(
	            Context.ALARM_SERVICE);
	    
	    alarms.setRepeating(AlarmManager.RTC_WAKEUP,
	            updateTime.getTimeInMillis() + (new Random().nextInt() % 360000),
	            AlarmManager.INTERVAL_DAY, recurringDownload);
	}
	
	/**
	 * Tell the OS SyncManager to perform an OnDemand Sync.
	 * 
	 * @param context The current context.
	 */
	public static void requestOnDemandSync(Context context)
	{
		Bundle extras = new Bundle();
		extras.putString(context.getString(R.string.sync_type_key), 
				context.getString(R.string.ondemand_sync_type_value));
		
		ContentResolver.requestSync(getOnYardAccount(context), OnYard.AUTHORITY, extras);
	}
	
	/**
	 * Tell the OS SyncManager to perform a Nightly Sync.
	 * 
	 * @param context The current context.
	 */
	public static void requestNightlySync(Context context)
	{
		Bundle extras = new Bundle();
		extras.putString(context.getString(R.string.sync_type_key), 
				context.getString(R.string.nightly_sync_type_value));
		
		ContentResolver.requestSync(getOnYardAccount(context), OnYard.AUTHORITY, extras);
	}
	
	/**
	 * Change the sync interval of the OnDemand Sync.
	 * 
	 * @param context The current context.
	 * @param newInterval The desired OnDemand Sync interval.
	 */
	public static void updateSyncInterval(Context context, String newInterval)
	{		
		Bundle extras = new Bundle();
		extras.putString(context.getString(R.string.sync_type_key), 
				context.getString(R.string.ondemand_sync_type_value));
		
		updateSyncInterval(context, getOnYardAccount(context), OnYard.AUTHORITY, extras, newInterval);
	}
	
	/**
	 * Get all colors from the OnYard WCF Service and insert them into the Color table.
	 * 
	 * @param context The current context.
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static void syncColors(Context context) throws JSONException, IOException
	{
		Timer t = new Timer("SyncHelper.SyncColors"); t.start();

		insertAllColors(context, getColors(context));

		t.end(); t.logDebug();
	}
	
	/**
	 * Get all statuses from the OnYard WCF Service and insert them into the Status table.
	 * 
	 * @param context The current context.
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static void syncStatuses(Context context) throws JSONException, IOException
	{
		Timer t = new Timer("SyncHelper.SyncStatuses"); t.start();
		
		insertAllStatuses(context, getStatuses(context));

		t.end(); t.logDebug();
	}
	
	/**
	 * Get all damages from the OnYard WCF Service and insert them into the Damage table.
	 * 
	 * @param context The current context.
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static void syncDamages(Context context) throws JSONException, IOException
	{
		Timer t = new Timer("SyncHelper.SyncDamages"); t.start();
		
		insertAllDamages(context, getDamages(context));

		t.end(); t.logDebug();
	}
	
	/**
	 * Get all vehicles from the OnYard WCF Service and insert them into the Damage table.
	 * 
	 * @param context The current context.
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static void syncAllVehicles(Context context) throws JSONException, IOException
	{
		String url = context.getString(R.string.onyard_service_url_base) + "vehicles" +
				"?pass=" + context.getString(R.string.onyard_service_password) + 
				"&branch=" + PreferenceHelper.getBranchNumber(context);

		HTTPHelper.writeFileFromHTTP(context, url);
		JSONHelper.insertAllVehiclesFromJSONFile(context, context.getString(R.string.json_file), 
				OnYard.VEHICLES_BATCH_SIZE);

		LogHelper.logDebug(url);
	}
	
	/**
	 * Get updated vehicles from the OnYard WCF Service and either insert (if new)
	 * or update (if existing) them in the Vehicles table.
	 * 
	 * @param context The current context.
	 * @param lastDBUpdateTime The Unix timestamp of the most recent sync that performed
	 * a database update or insert.
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public static void syncUpdatedVehicles(Context context, long lastDBUpdateTime) 
			throws IOException, JSONException
	{
		String url = context.getString(R.string.onyard_service_url_base) + "vehicles" +
				"?pass=" + context.getString(R.string.onyard_service_password) + 
				"&branch=" + PreferenceHelper.getBranchNumber(context) + 
				"&lastupdate=" + lastDBUpdateTime;

		HTTPHelper.writeFileFromHTTP(context, url);
		JSONHelper.insertUpdatedVehiclesFromJSONFile(context, context.getString(R.string.json_file), 
				OnYard.VEHICLES_BATCH_SIZE);

		LogHelper.logDebug(url);
	}
	
	/**
	 * Get a JSONArray of all Colors from the OnYard WCF Service.
	 * 
	 * @param context The current context.
	 * @return A JSONArray containing all Colors.
	 * @throws JSONException 
	 * @throws IOException 
	 */
	private static JSONArray getColors(Context context) throws JSONException, IOException
	{
		return HTTPHelper.getJSONArrayFromHTTP(context,
				context.getString(R.string.onyard_service_url_base) + "color");
	}
	
	/**
	 * Get a JSONArray of all Statuses from the OnYard WCF Service.
	 * 
	 * @param context The current context.
	 * @return A JSONArray containing all Statuses.
	 * @throws JSONException 
	 * @throws IOException 
	 */
	private static JSONArray getStatuses(Context context) throws JSONException, IOException
	{
		return HTTPHelper.getJSONArrayFromHTTP(context, 
				context.getString(R.string.onyard_service_url_base) + "status");
	}
	
	/**
	 * Get a JSONArray of all Damages from the OnYard WCF Service.
	 * 
	 * @param context The current context.
	 * @return A JSONArray containing all Damages.
	 * @throws JSONException 
	 * @throws IOException 
	 */
	private static JSONArray getDamages(Context context) throws JSONException, IOException
	{
		return HTTPHelper.getJSONArrayFromHTTP(context, 
				context.getString(R.string.onyard_service_url_base) + "damage");
	}

	/**
	 * Insert a JSONArray of Colors into the Color table in the OnYard ContentProvider.
	 * 
	 * @param context The current context.
	 * @param colorArray The JSONArray containing the Colors to be inserted.
	 * @throws JSONException 
	 */
	private static void insertAllColors(Context context, JSONArray colorArray) throws JSONException
	{
		int numColors = colorArray.length();
		ContentValues[] values = new ContentValues[numColors];

		for (int index = 0; index < numColors; index++)
		{
			values[index] = new ColorInfo(colorArray.getJSONObject(index)).getContentValues();
		}

		context.getContentResolver().bulkInsert(OnYard.Color.CONTENT_URI, values);
	}
	
	/**
	 * Insert a JSONArray of Statuses into the Status table in the OnYard ContentProvider.
	 * 
	 * @param context The current context.
	 * @param statusArray The JSONArray containing the Statuses to be inserted.
	 * @throws JSONException 
	 */
	private static void insertAllStatuses(Context context, JSONArray statusArray) throws JSONException
	{
		int numStatuses = statusArray.length();
		ContentValues[] values = new ContentValues[numStatuses];

		for (int index = 0; index < numStatuses; index++)
		{
			values[index] = new StatusInfo(statusArray.getJSONObject(index)).getContentValues();
		}

		context.getContentResolver().bulkInsert(OnYard.Status.CONTENT_URI, values);
	}
	
	/**
	 * Insert a JSONArray of Damages into the Damage table in the OnYard ContentProvider.
	 * 
	 * @param context The current context.
	 * @param statusArray The JSONArray containing the Damages to be inserted.
	 * @throws JSONException 
	 */
	private static void insertAllDamages(Context context, JSONArray damageArray) throws JSONException
	{
		int numDamages = damageArray.length();
		ContentValues[] values = new ContentValues[numDamages];

		for (int index = 0; index < numDamages; index++)
		{
			values[index] = new DamageInfo(damageArray.getJSONObject(index)).getContentValues();
		}

		context.getContentResolver().bulkInsert(OnYard.Damage.CONTENT_URI, values);
	}
	
	/**
	 * Update the periodic sync interval for the specified account.
	 * 
	 * @param context The current context.
	 * @param account The account of the periodic sync to update.
	 * @param authority The authority of the periodic sync to update.
	 * @param extras The extras of the periodic sync to update.
	 * @param newValue The desired interval (in seconds) of the periodic sync, 
	 * or 0 to remove the sync.
	 */
	private static void updateSyncInterval(Context context, Account account, String authority, 
			Bundle extras, String newValue)
	{
		int syncInterval = Integer.parseInt(newValue);
		
		if(syncInterval == 0)
			ContentResolver.removePeriodicSync(account, authority, extras);
		else
			ContentResolver.addPeriodicSync(account, authority, extras, syncInterval);
		
		LogHelper.logDebug("New Sync Interval = " + syncInterval + " seconds");
	}
	
	/**
	 * Get the Account associated with OnYard.
	 * 
	 * @param context The current context.
	 * @return The OnYard Account object.
	 */
	private static Account getOnYardAccount(Context context)
	{
		AccountManager am = AccountManager.get(context);
		Account account =  null;
		Account[] accounts = am.getAccountsByType(context.getString(R.string.account_type));
		for(Account a : accounts) 
		{
			if (a != null && context.getString(R.string.app_name).equals(a.name))
			{
				account = a;
				break;
			}
		}
		
		return account;
	}
}
