package com.iaai.onyard.sync;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;

import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.utility.MetricsHelper;
import com.iaai.onyard.utility.SyncHelper;

/**
 * Helper class containing the method to perform a Nightly Sync.
 */
public class NightlySync {
	
	/**
	 * Perform a Nightly Sync by deleting all database tables (except Metrics) and
	 * repopulating them from the OnYard WCF Service.
	 * 
	 * @param context The current context.
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public static void performSync(Context context) 
			throws JSONException, IOException, AddressException, MessagingException
	{
		Timer t = new Timer("NightlySync.performSync"); t.start();
		
		deleteAllVehicles(context);
		deleteAllColors(context);
		deleteAllStatuses(context);
		deleteAllDamages(context);
		deleteAllConfigs(context);

		insertAllConfigs(context);
		SyncHelper.syncColors(context);
		SyncHelper.syncDamages(context);
		SyncHelper.syncStatuses(context);
		SyncHelper.syncAllVehicles(context);
		
		MetricsHelper.sendMetrics(context);
		
		t.end(); t.logInfo(context);
	}
	
	/**
	 * Delete all records from the Vehicles table.
	 * 
	 * @param context The current context.
	 */
	private static void deleteAllVehicles(Context context)
	{
		Timer t = new Timer("NightlySync.deleteAllVehicles"); t.start();
		
		context.getContentResolver().delete(
				OnYard.Vehicles.CONTENT_URI, 
				null, 
				null);
			
		t.end(); t.logDebug();
	}
	
	/**
	 * Delete all records from the Color table.
	 * 
	 * @param context The current context.
	 */
	private static void deleteAllColors(Context context)
	{
		Timer t = new Timer("NightlySync.deleteAllColors"); t.start();

		context.getContentResolver().delete(
				OnYard.Color.CONTENT_URI, 
				null, 
				null);

		t.end(); t.logDebug();
	}
	
	/**
	 * Delete all records from the Status table.
	 * 
	 * @param context The current context.
	 */
	private static void deleteAllStatuses(Context context)
	{
		Timer t = new Timer("NightlySync.deleteAllStatuses"); t.start();

		context.getContentResolver().delete(
				OnYard.Status.CONTENT_URI, 
				null, 
				null);
			
		t.end(); t.logDebug();
	}
	
	/**
	 * Delete all records from the Damage table.
	 * 
	 * @param context The current context.
	 */
	private static void deleteAllDamages(Context context)
	{
		Timer t = new Timer("NightlySync.deleteAllDamages"); t.start();

		context.getContentResolver().delete(
				OnYard.Damage.CONTENT_URI, 
				null, 
				null);

		t.end(); t.logDebug();
	}
	
	/**
	 * Delete all records from the Config table.
	 * 
	 * @param context The current context.
	 */
	private static void deleteAllConfigs(Context context)
	{
		Timer t = new Timer("NightlySync.deleteAllConfigs"); t.start();

		context.getContentResolver().delete(
				OnYard.Config.CONTENT_URI, 
				null, 
				null);

		t.end(); t.logDebug();
	}
	
	/**
	 * Create records for all required Config keys and initial values.
	 * 
	 * @param context The current context.
	 */
	private static void insertAllConfigs(Context context)
	{
		Timer t = new Timer("NightlySync.insertAllConfigs"); t.start();

		ContentValues values = new ContentValues();
		values.put(OnYard.Config.COLUMN_NAME_KEY, OnYard.Config.CONFIG_KEY_UPDATE_DATE_TIME);
		values.put(OnYard.Config.COLUMN_NAME_VALUE, 0);

		context.getContentResolver().insert(
				OnYard.Config.CONTENT_URI, 
				values
		);

		t.end(); t.logDebug();
	}
}
