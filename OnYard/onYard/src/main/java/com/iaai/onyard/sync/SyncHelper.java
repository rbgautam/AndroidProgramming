package com.iaai.onyard.sync;

import java.util.Calendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.http.EnhancementHttpPost;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.ConfigInfo;
import com.iaai.onyardproviderapi.classes.SyncWindowInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * Helper class containing methods that deal with data syncing.
 */
public class SyncHelper {

    public static void updateSyncNotification(Context appContext, int pendingCount,
            boolean syncFailed) {
        final Notification.Builder syncNotification = new Notification.Builder(appContext)
        .setOngoing(true);
        final String syncDetailText = pendingCount == 0 ? "All items synced" : pendingCount
                + " items to sync";

        final Intent intent = new Intent(appContext, OnDemandSyncReceiver.class);
        final PendingIntent syncIntent = PendingIntent.getBroadcast(appContext, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        syncNotification.setContentIntent(syncIntent);

        switch (pendingCount) {
            case 0:
                if (syncFailed) {
                    syncNotification.setSmallIcon(R.drawable.sync_notify_red)
                    .setContentTitle(appContext.getString(R.string.sync_failed_message))
                    .setContentText(syncDetailText);
                    break;
                }

                if (!HTTPHelper.isNetworkAvailable(appContext)) {
                    syncNotification
                    .setSmallIcon(R.drawable.sync_notify_yellow)
                    .setContentTitle(
                            appContext.getString(R.string.network_unavailable_message))
                            .setContentText(syncDetailText);
                    break;
                }

                if (!SyncHelper.isSyncEnabled()) {
                    syncNotification
                    .setSmallIcon(R.drawable.sync_notify_gray)
                    .setContentTitle(
                            appContext.getString(R.string.sync_disabled_message))
                            .setContentText(syncDetailText);
                    break;
                }

                syncNotification.setSmallIcon(R.drawable.sync_notify_black)
                .setContentTitle(appContext.getString(R.string.sync_idle_message))
                .setContentText(syncDetailText);

                break;
            default:
                if (syncFailed) {
                    syncNotification.setSmallIcon(R.drawable.sync_notify_red)
                    .setContentTitle(appContext.getString(R.string.sync_failed_message))
                    .setContentText(syncDetailText);
                    break;
                }

                if (!HTTPHelper.isNetworkAvailable(appContext)) {
                    syncNotification
                    .setSmallIcon(R.drawable.sync_notify_red)
                    .setContentTitle(
                            appContext.getString(R.string.network_unavailable_message))
                            .setContentText(syncDetailText);
                    break;
                }

                if (!SyncHelper.isSyncEnabled()) {
                    syncNotification
                    .setSmallIcon(R.drawable.sync_notify_red)
                    .setContentTitle(
                            appContext.getString(R.string.sync_disabled_message))
                            .setContentText(syncDetailText);
                    break;
                }

                syncNotification.setSmallIcon(R.drawable.sync_notify_white)
                .setContentTitle(appContext.getString(R.string.sync_tap_prompt))
                .setContentText(syncDetailText);
                break;
        }

        final NotificationManager notificationManager = (NotificationManager) appContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(OnYard.SYNC_NOTIFICATION_ID, syncNotification.build());
    }

    /**
     * Check if an OnYard sync operation is either pending or in progress.
     * 
     * @param context The current context - can be activity context.
     * @return True if sync is pending or in process, false otherwise.
     */
    public static boolean isCurrentlySyncing(Context context) {
        return ContentResolver.isSyncActive(getOnYardAccount(context), OnYardContract.AUTHORITY);
    }

    /**
     * Check if sync is enabled in the device Settings.
     * 
     * @return True if sync is enabled, false otherwise.
     */
    private static boolean isSyncEnabled() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    /**
     * Create the OnYard account used for syncing.
     * 
     * @param context The current context.
     */
    public static void createOnYardAccount(Context context)
    {
        final Account account = new Account(context.getString(R.string.app_name),
                OnYard.ACCOUNT_TYPE);
        final AccountManager am = AccountManager.get(context);

        am.addAccountExplicitly(account, null, null);
    }

    /**
     * Enable the background syncing for OnYard.
     * 
     * @param context The current context.
     */
    public static void enableSync(Context context)
    {
        ContentResolver.setSyncAutomatically(getOnYardAccount(context), OnYardContract.AUTHORITY,
                true);
    }

    /**
     * Tell the OS SyncManager to perform an OnDemand Sync.
     * 
     * @param context The current context.
     */
    public static void requestOnDemandSync(Context context)
    {
        final Bundle extras = new Bundle();
        extras.putString(IntentExtraKey.SYNC_TYPE, OnYard.ON_DEMAND_SYNC_TYPE_VALUE);

        ContentResolver.requestSync(getOnYardAccount(context), OnYardContract.AUTHORITY, extras);
    }

    /**
     * Tell the OS SyncManager to perform a Full Sync.
     * 
     * @param context The current context.
     * @param shouldForceLogout A boolean indicating whether or not user should be logged out as
     *            part of this sync.
     */
    public static void requestFullSync(Context context, boolean shouldForceLogout)
    {
        final Bundle extras = new Bundle();
        extras.putString(IntentExtraKey.SYNC_TYPE, OnYard.FULL_SYNC_TYPE_VALUE);
        extras.putBoolean(IntentExtraKey.FORCE_LOGOUT, shouldForceLogout);

        ContentResolver.requestSync(getOnYardAccount(context), OnYardContract.AUTHORITY, extras);
    }

    /**
     * Change the sync interval of the OnDemand Sync.
     * 
     * @param context The current context.
     * @param newInterval The desired OnDemand Sync interval.
     */
    public static void updateSyncInterval(Context context, String newInterval)
    {
        final Bundle extras = new Bundle();
        extras.putString(IntentExtraKey.SYNC_TYPE, OnYard.ON_DEMAND_SYNC_TYPE_VALUE);

        updateSyncInterval(context, getOnYardAccount(context), OnYardContract.AUTHORITY, extras,
                newInterval);
    }

    public static String getTimeSinceLastSync(ContentResolver contentResolver) {
        int interval = 0;
        final long currTime = DataHelper.getUnixUtcTimeStamp();

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                            OnYardContract.Config.CONFIG_KEY_LAST_SYNC_DATE_TIME),
                            new String[] { OnYardContract.Config.COLUMN_NAME_VALUE }, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                interval = (int) (currTime - cursor.getLong(0));
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return timeDisplayed(interval);
    }

    /*
     * Get scheduled sync interval from Config table If it is not there get default sync interval
     * from OnYard_Config table If OnYard_Config table does not exists or default sync interval is
     * not defined get it from a constant
     */

    public static String getSyncIntervalFromDb(Context context) {
        Cursor cursor = null;
        String interval = null;
        try {

            cursor = context.getContentResolver().query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                            OnYardContract.Config.CONFIG_KEY_SYNC_INTERVAL),
                            new String[] { OnYardContract.Config.COLUMN_NAME_VALUE }, null, null, null);

            if (cursor != null) {
                interval = cursor.moveToFirst() ? cursor.getString(0) : null;
            }

            if (interval == null) {
                interval = Integer.toString(OnYard.DEFAULT_SYNC_INTERVAL_IN_MINUTES);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // convert minutes to seconds
        interval = Integer.toString(Integer.parseInt(interval) * 60);
        return interval;
    }

    public static int getPendingSyncCount(ContentResolver contentResolver, int appId) {
        Cursor pendingCountCursor = null;
        try {
            if (appId == OnYardContract.ENHANCEMENT_APP_ID) {
                pendingCountCursor = contentResolver.query(
                        OnYardContract.DataPendingSync.CONTENT_URI,
                        new String[] { OnYardContract.DataPendingSync.COLUMN_NAME_VALUE_INT },
                        OnYardContract.DataPendingSync.COLUMN_NAME_APP_ID + "=? AND "
                                + OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME
                                + "=?",
                                new String[] { String.valueOf(appId),
                                EnhancementHttpPost.NUM_ENHANCEMENTS_KEY }, null);

                int numPending = 0;
                if (pendingCountCursor != null && pendingCountCursor.moveToFirst()) {
                    do {
                        numPending += pendingCountCursor.getInt(0);
                    }
                    while (pendingCountCursor.moveToNext());
                }
                return numPending;
            }
            else {
                pendingCountCursor = contentResolver.query(
                        OnYardContract.DataPendingSync.CONTENT_URI,
                        new String[] { "COUNT(DISTINCT "
                                + OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID
                                + ") AS count" },
                                OnYardContract.DataPendingSync.COLUMN_NAME_APP_ID + "=?",
                                new String[] { String.valueOf(appId) }, null);

                if (pendingCountCursor != null && pendingCountCursor.moveToFirst()) {
                    return pendingCountCursor.getInt(0);
                }
                else {
                    return 0;
                }
            }
        }
        finally {
            if (pendingCountCursor != null) {
                pendingCountCursor.close();
            }
        }
    }

    public static boolean isInSyncWindow(ContentResolver contentResolver, String branchTZ) {

        if (branchTZ == null) {
            return true;
        }

        final Calendar todayLocal = DataHelper.getTzCalendar(branchTZ);
        final int dayOfWeekLocal = todayLocal.get(Calendar.DAY_OF_WEEK);

        Cursor syncWindowCursor = null;
        try {
            syncWindowCursor = contentResolver.query(OnYardContract.SyncWindow.CONTENT_URI, null,
                    OnYardContract.SyncWindow.COLUMN_NAME_DAY_OF_WEEK + "=?",
                    new String[] { String.valueOf(dayOfWeekLocal) }, null);

            // no windows defined for current day - sync allowed
            if (syncWindowCursor == null || !syncWindowCursor.moveToFirst()) {
                return true;
            }

            final int minutesSinceMidnight = getMinutesSinceMidnight(todayLocal, branchTZ);
            SyncWindowInfo window = null;
            do {
                window = new SyncWindowInfo(syncWindowCursor);

                if (minutesSinceMidnight > window.getStartTime()
                        && minutesSinceMidnight < window.getStartTime()
                        + window.getDurationMinutes()) {
                    return true;
                }
            }
            while (syncWindowCursor.moveToNext());

            return false;
        }
        finally {
            if (syncWindowCursor != null) {
                syncWindowCursor.close();
            }
        }
    }

    public static String getNextSyncWindowStart(Context context) {
        String winStart = "";

        final ContentResolver contentResolver = context.getContentResolver();
        final String branchTZ = DataHelper.getBranchTimeZone(context);
        final Calendar todayLocal = DataHelper.getTzCalendar(branchTZ);
        final int dayOfWeekLocal = todayLocal.get(Calendar.DAY_OF_WEEK);

        Cursor syncWindowCursor = null;
        try {
            syncWindowCursor = contentResolver.query(OnYardContract.SyncWindow.CONTENT_URI, null,
                    OnYardContract.SyncWindow.COLUMN_NAME_DAY_OF_WEEK + "=?",
                    new String[] { String.valueOf(dayOfWeekLocal) }, null);

            // no windows defined for current day - sync allowed
            if (syncWindowCursor == null || !syncWindowCursor.moveToFirst()) {
                return winStart;
            }

            final long minutes = getMinutesSinceMidnight(todayLocal, branchTZ);

            syncWindowCursor = null;
            syncWindowCursor = contentResolver.query(
                    OnYardContract.SyncWindow.CONTENT_URI,
                    new String[] { OnYardContract.SyncWindow.COLUMN_NAME_START_TIME },
                    OnYardContract.SyncWindow.COLUMN_NAME_DAY_OF_WEEK + "=? AND "
                            + OnYardContract.SyncWindow.COLUMN_NAME_START_TIME + " >?  ",
                            new String[] { String.valueOf(dayOfWeekLocal), String.valueOf(minutes) },
                            OnYardContract.SyncWindow.COLUMN_NAME_START_TIME + " ASC LIMIT 1");

            if (syncWindowCursor != null && syncWindowCursor.moveToFirst()) {

                final int winStartMinutes = syncWindowCursor.getInt(0);

                int winStartHrs = winStartMinutes / 60;
                final int winStartMin = winStartMinutes % 60;
                String ampm = "";
                if (winStartHrs < 12) {
                    ampm = "AM";
                }
                else {
                    ampm = "PM";
                    if (winStartHrs > 12 || winStartMin > 0) {
                        winStartHrs = winStartHrs - 12;
                    }
                }

                winStart = String.valueOf(winStartHrs)
                        + ":"
                        + (winStartMin < 10 ? "0" : "")
                        + String.valueOf(winStartMin) + ampm;
            }
            else {
                winStart = "midnight";
            }
        }
        finally {
            if (syncWindowCursor != null) {
                syncWindowCursor.close();
            }
        }

        return winStart;
    }

    /**
     * Get image type
     * 
     * @return image type (for example "Standard")
     */

    public static String getImageTypeFromDb(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(OnYardContract.Config.CONTENT_URI,
                    new String[] { OnYardContract.Config.COLUMN_NAME_VALUE },
                    OnYardContract.Config.COLUMN_NAME_KEY + "=?",
                    new String[] { OnYardContract.Config.CONFIG_KEY_IMAGE_TYPE }, null);

            if (cursor != null && cursor.moveToFirst()) {
                return new ConfigInfo(cursor).getConfigValueString();
            }
            else {
                return OnYard.DEFAULT_IMAGE_TYPE;
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private static int getMinutesSinceMidnight(final Calendar today, String tZ) {

        final long now = today.getTimeInMillis();

        final Calendar todayMidnight = (Calendar) today.clone();

        todayMidnight.set(Calendar.HOUR_OF_DAY, 0);
        todayMidnight.set(Calendar.MINUTE, 0);
        todayMidnight.set(Calendar.SECOND, 0);
        todayMidnight.set(Calendar.MILLISECOND, 0);

        return (int) ((now - todayMidnight.getTimeInMillis()) / 60000);
    }

    /**
     * Update the periodic sync interval for the specified account.
     * 
     * @param context The current context.
     * @param account The account of the periodic sync to update.
     * @param authority The authority of the periodic sync to update.
     * @param extras The extras of the periodic sync to update.
     * @param newValue The desired interval (in seconds) of the periodic sync, or 0 to remove the
     *            sync.
     */
    private static void updateSyncInterval(Context context, Account account, String authority,
            Bundle extras, String newValue)
    {
        final int syncInterval = Integer.parseInt(newValue);

        if(syncInterval == 0) {
            ContentResolver.removePeriodicSync(account, authority, extras);
        }
        else {
            ContentResolver.addPeriodicSync(account, authority, extras, syncInterval);
        }

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
        final AccountManager am = AccountManager.get(context);
        Account account =  null;
        final Account[] accounts = am.getAccountsByType(OnYard.ACCOUNT_TYPE);
        for(final Account a : accounts)
        {
            if (a != null && context.getString(R.string.app_name).equals(a.name))
            {
                account = a;
                break;
            }
        }

        return account;
    }

    /**
     * @param interval
     * @return formatted string
     */
    private static String timeDisplayed(int interval) {
        String strSinceSync;

        int minutes;
        int hours;

        if (interval < 3600) {
            minutes = interval / 60;
            strSinceSync = String.format("%d min", minutes);
        }
        else
            if (interval < 86400) {
                hours = interval / 3600;
                minutes = (interval - hours * 3600) / 60;
                strSinceSync = String.format("%d hr, %d min", hours, minutes);
            }
            else
                if (interval < 31536000) {
                    strSinceSync = String.format("%d day(s)", interval / 86400);
                }
                else {
                    strSinceSync = "more than one year";
                }
        return strSinceSync;
    }

    /**
     * Get the Unix timestamp of the most recent WCF Service consumption that updated the Vehicles
     * table.
     * 
     * @param context The current context.
     * @return The Unix timestamp of the update.
     */
    public static Long getLastDBUpdateTime(Context context) {
        Cursor cursor = null;
        try {
            final Timer t = new Timer("OnDemandSync.getLastDBUpdateTime");
            t.start();

            cursor = context.getContentResolver().query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                            OnYardContract.Config.CONFIG_KEY_UPDATE_DATE_TIME),
                            new String[] { OnYardContract.Config.COLUMN_NAME_VALUE }, null, null, null);

            t.end();
            t.logVerbose();

            if (cursor == null) {
                return 0L;
            }

            return cursor.moveToFirst() ? cursor.getLong(0) : (long) 0;
        }
        finally {
            cursor.close();
        }
    }
}
