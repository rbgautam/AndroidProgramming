package com.iaai.onyard.sync;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.json.JSONException;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

import com.iaai.onyard.R;
import com.iaai.onyard.utility.LogHelper;

/**
 * The service that performs OnYard syncs.
 */
public class OnYardSyncAdapterService extends Service {
	
	/**
	 * Object that represents the Sync Adapter used by this service.
	 */
	private static SyncAdapterImpl sSyncAdapter = null;

	/**
	 * Default constructor.
	 */
	public OnYardSyncAdapterService() 
	{
		super();
	}

	/**
	 * The SyncAdapter with which the OS SyncManager communicates in order to perform
	 * OnYard syncs.
	 */
	private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter 
	{
		/**
		 * The current context.
		 */
		private Context mContext;

		
		/**
		 * Default constructor.
		 * 
		 * @param context The current context.
		 */
		public SyncAdapterImpl(Context context) 
		{
			super(context, true);
			mContext = context;
			
		}

		/**
		 * Perform an OnYard sync.
		 * 
		 * Called when the OS SyncManager orders a sync for this account.
		 * 
		 * @param account The account that should be synced.
		 * @param extras Bundle containing the type of sync that was requested.
		 * @param authority The authority of this sync request.
		 * @param provider A ContentProviderClient that points to the ContentProvider 
		 * for this authority.
		 * @param syncResult The sync result (unused).
		 */
		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, 
				ContentProviderClient provider, SyncResult syncResult) 
		{
			try
			{
				OnYardSyncAdapterService.performSync(mContext, account, extras, 
						authority, provider, syncResult);
			}
			catch (Exception e)
			{
				LogHelper.logError(mContext, e);
			}
		}
	}

	/**
	 * Return the communication channel to the service. May return null 
	 * if clients can not bind to the service. The returned IBinder is 
	 * usually for a complex interface that has been described using aidl. 
	 * 
	 * @param intent The Intent that was used to bind to this service, 
	 * as given to Context.bindService. Note that any extras that were 
	 * included with the Intent at that point will not be seen here.
	 * @return Return an IBinder through which clients can call on to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) 
	{
		IBinder ret = null;
		ret = getSyncAdapter().getSyncAdapterBinder();
		return ret;
	}

	/**
	 * Get an instance of the OnYard SyncAdapter.
	 * 
	 * @return An instance of the OnYard SyncAdapter.
	 */
	private SyncAdapterImpl getSyncAdapter() 
	{
		if (sSyncAdapter == null)
			sSyncAdapter = new SyncAdapterImpl(this);
		return sSyncAdapter;
	}

	/**
	 * Performs an OnYard sync. The sync type is determined by the extras that are sent.
	 * 
	 * @param context The current context.
	 * @param account The account that should be synced.
	 * @param extras The type of sync requested.
	 * @param authority The authority of this sync request
	 * @param provider The sync provider.
	 * @param syncResult The sync result (unused).
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	private static void performSync(Context context, Account account, Bundle extras, 
			String authority, ContentProviderClient provider, SyncResult syncResult) 
			throws JSONException, IOException, AddressException, MessagingException
	{		
		String type = extras.getString(context.getString(R.string.sync_type_key));

		if(type != null && type.equals(context.getString(R.string.nightly_sync_type_value)))
		{
			LogHelper.logDebug("Nightly Sync Starting...");
			NightlySync.performSync(context);
		}
		else
		{
			LogHelper.logDebug("OnDemand Sync Starting...");
			OnDemandSync.performSync(context);
		}
	}
}
