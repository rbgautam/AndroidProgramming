package com.iaai.onyard.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iaai.onyard.tasks.PingOnYardServerTask;
import com.iaai.onyard.utility.HTTPHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.SyncHelper;

/**
 * BroadcastReceiver that triggers a nightly sync when the scheduled alarm is fired.
 */
public class AlarmReceiver extends BroadcastReceiver {
    
	/**
	 * Request a nightly sync.
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
	        LogHelper.logDebug("Nightly Sync alarm broadcast received.");
	
	        if(HTTPHelper.isNetworkAvailable(context) && new PingOnYardServerTask().execute(context).get())
	        {
	        	SyncHelper.requestNightlySync(context);
	        }
	        else
	        {
	        	throw new Exception("Nightly Sync canceled - network unavailable.");
	        }
    	}
    	catch (Exception e)
    	{
    		LogHelper.logWarning(context, e);
    	}
    }
}
