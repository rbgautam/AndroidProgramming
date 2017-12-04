package com.iaai.onyard.sync;

import java.io.IOException;

import org.json.JSONException;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.utility.SyncHelper;

/**
 * Helper class containing the method to perform an OnDemand Sync.
 */
public class OnDemandSync {
	
	/**
	 * Perform an OnDemand Sync by pulling updated Vehicles from the OnYard WCF Service. Color,
	 * Damage, and/or Status are pulled if the respective tables are empty.
	 * 
	 * @param context The current context.
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static void performSync(Context context) throws JSONException, IOException
	{
		Timer t = new Timer("OnDemandSync.performSync"); t.start();
		
		Long lastUpdate = getLastDBUpdateTime(context);
		
		if(lastUpdate.equals(0L))
			SyncHelper.syncAllVehicles(context);
		else
			SyncHelper.syncUpdatedVehicles(context, lastUpdate);
		
		if(isColorEmpty(context))
			SyncHelper.syncColors(context);
		if(isDamageEmpty(context))
			SyncHelper.syncDamages(context);
		if(isStatusEmpty(context))
			SyncHelper.syncStatuses(context);
		
		t.end(); t.logInfo(context);
	}
	
	/**
	 * Check if there are any records in the Color table.
	 * 
	 * @param context The current context.
	 * @return True if the table is empty, false otherwise.
	 */
	private static boolean isColorEmpty(Context context)
	{
	    Cursor cursor = null;
	    try
	    {
    		cursor = context.getContentResolver().query(
    				OnYard.Color.CONTENT_URI, 
    				new String[]{ OnYard.Color.COLUMN_NAME_CODE }, 
    				null, 
    				null, 
    				null
    		);
    
    		return cursor == null || !cursor.moveToFirst();
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * Check if there are any records in the Status table.
	 * 
	 * @param context The current context.
	 * @return True if the table is empty, false otherwise.
	 */
	private static boolean isStatusEmpty(Context context)
	{
	    Cursor cursor = null;
	    try
	    {
    		cursor = context.getContentResolver().query(
    				OnYard.Status.CONTENT_URI, 
    				new String[]{ OnYard.Status.COLUMN_NAME_CODE }, 
    				null, 
    				null, 
    				null
    		);
    
    		return cursor == null || !cursor.moveToFirst();
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * Check if there are any records in the Damage table.
	 * 
	 * @param context The current context.
	 * @return True if the table is empty, false otherwise.
	 */
	private static boolean isDamageEmpty(Context context)
	{
	    Cursor cursor = null;
	    try
	    {
    		cursor = context.getContentResolver().query(
    				OnYard.Damage.CONTENT_URI, 
    				new String[]{ OnYard.Damage.COLUMN_NAME_CODE }, 
    				null, 
    				null, 
    				null
    		);
    
    		return cursor == null || !cursor.moveToFirst();
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * Get the Unix timestamp of the most recent WCF Service consumption that
	 * updated the Vehicles table.
	 * 
	 * @param context The current context.
	 * @return The Unix timestamp of the update.
	 */
	private static Long getLastDBUpdateTime(Context context)
	{
	    Cursor cursor = null;
	    try
	    {
    		Timer t = new Timer("OnDemandSync.getLastDBUpdateTime"); t.start();
    
    		cursor = context.getContentResolver().query(
    				Uri.withAppendedPath(OnYard.Config.CONTENT_URI, 
    						OnYard.Config.CONFIG_KEY_UPDATE_DATE_TIME), 
    						new String[]{ OnYard.Config.COLUMN_NAME_VALUE }, 
    						null, 
    						null, 
    						null
    		);
    
    		t.end(); t.logDebug();
    
    		if(cursor == null)
    			return 0L;
    
    		return cursor.moveToFirst() ? cursor.getLong(0) : (long) 0;
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
}
