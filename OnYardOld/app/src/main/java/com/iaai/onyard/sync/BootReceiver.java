package com.iaai.onyard.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.PreferenceHelper;
import com.iaai.onyard.utility.SyncHelper;

/**
 * BroadcastReceiver that re-sets the Nightly Sync alarm after the device is rebooted.
 */
public class BootReceiver extends BroadcastReceiver {

	/**
	 * Set the Nightly Sync alarm.
	 * 
	 * Called when the BroadcastReceiver is receiving an Intent broadcast.
	 * 
	 * @param context The Context in which the receiver is running.
	 * @param intent The Intent being received.
	 */
	@Override
	public void onReceive(Context context, Intent intent) 
	{		
		try
		{
			SyncHelper.setNightlySync(context);
			PreferenceHelper.setAlarmIsSet(context, true);
		}
		catch (Exception e)
		{
			LogHelper.logWarning(context, e);
		}
	}
}
