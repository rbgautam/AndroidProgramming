package com.iaai.onyard.sync;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.json.JSONException;

import android.accounts.Account;
import android.accounts.NetworkErrorException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.utility.BroadcastHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * The service that performs OnYard syncs.
 */
public class OnYardSyncAdapterService extends Service {

    private static final String CLASS_NAME = "OnYardSyncAdapterService";

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
    private class SyncAdapterImpl extends AbstractThreadedSyncAdapter
    {
        /**
         * The current context.
         */
        private final Context mContext;


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
            catch (final Exception e)
            {
                LogHelper.logError(mContext, e, this.getClass().getSimpleName());
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
        if (sSyncAdapter == null) {
            sSyncAdapter = new SyncAdapterImpl(this);
        }
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
     * @throws RemoteException
     * @throws NetworkErrorException
     */
    private static void performSync(Context context, Account account, Bundle extras,
            String authority, ContentProviderClient provider, SyncResult syncResult) {
        final NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification.Builder syncNotification = new Notification.Builder(context);
        boolean success = false;
        String message = null;

        try {
            syncNotification.setSmallIcon(R.drawable.sync_notify_green)
            .setContentTitle(context.getString(R.string.sync_in_progress_message))
            .setContentText("Sync is in progress").setOngoing(true);
            notificationManager.notify(OnYard.SYNC_NOTIFICATION_ID, syncNotification.build());

            final String type = extras.getString(IntentExtraKey.SYNC_TYPE);

            boolean isFullSync = false;
            if (type != null && type.equals(OnYard.FULL_SYNC_TYPE_VALUE)) {
                isFullSync = true;
                LogHelper.logDebug("Full Sync Starting...");
            }
            else {
                isFullSync = false;
                LogHelper.logDebug("OnDemand Sync Starting...");
            }
            final OnYardSync sync = new OnYardSync(context);
            sync.run(extras.getBoolean(IntentExtraKey.FORCE_LOGOUT), isFullSync);

            success = true;
            SyncHelper.updateSyncNotification(context, getPendingSyncCount(context), false);
        }
        catch (final NetworkErrorException ne) {
            success = false;
            message = ne.getMessage();
            LogHelper.logWarning(context, ne, CLASS_NAME);
            SyncHelper.updateSyncNotification(context, getPendingSyncCount(context), true);
        }
        catch (final Exception e) {
            LogHelper.logWarning(context, e, CLASS_NAME);
            SyncHelper.updateSyncNotification(context, getPendingSyncCount(context), true);
        }
        finally {
            BroadcastHelper.sendSyncCompletedBroadcast(context, success, message);
        }
    }

    private static int getPendingSyncCount(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                    .query(OnYardContract.DataPendingSync.CONTENT_URI,
                            new String[] { "COUNT(DISTINCT "
                                    + OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID
                                    + ") AS count" }, null, null, null);

            cursor.moveToFirst();

            return cursor.getInt(0);
        }
        catch (final Exception e) {
            return -1;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
