package com.iaai.onyard.utility;

import android.content.Context;
import android.content.Intent;

import com.iaai.onyard.application.OnYard.Broadcast;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.classes.UnauthorizedReason;


public class BroadcastHelper {

    private static final String BROADCAST_SYNC_SUCCESS = "";
    private static final String BROADCAST_SYNC_FAILED = "Sync Failed";

    /**
     * Notify the current OnYard user that they have been logged out on account of failed
     * authorization. This method ONLY notifies the user of logout and does not modify the database
     * in any way. AuthenticationHelper.logCurrentUserOut should be used to modify the database.
     * 
     * @param context The application context.
     * @param logoutReason The object which encapsulates the reason that the user is being logged
     *            out.
     */
    public static void sendLogoutBroadcast(Context context, UnauthorizedReason logoutReason) {
        final Intent intent = new Intent(Broadcast.LOGOUT);
        intent.putExtra(IntentExtraKey.VOLUNTARY_LOGOUT, false);
        intent.putExtra(IntentExtraKey.LOGOUT_MESSAGE, logoutReason.getUserFriendlyMessage());
        context.sendBroadcast(intent);
    }

    /**
     * Notify the current OnYard user that they have been logged out. This method ONLY notifies the
     * user of logout and does not modify the database in any way.
     * AuthenticationHelper.logCurrentUserOut should be used to modify the database.
     * 
     * @param context The application context.
     * @param isVoluntary A boolean indicating whether or not the logout is a result of the user
     *            hitting the "Log Out" button.
     */
    public static void sendLogoutBroadcast(Context context, boolean isVoluntary) {
        final Intent intent = new Intent(Broadcast.LOGOUT);
        intent.putExtra(IntentExtraKey.VOLUNTARY_LOGOUT, isVoluntary);
        context.sendBroadcast(intent);
    }

    /**
     * Notify the current OnYard user that sync on demand has been completed of failed
     * 
     * @param context The application context.
     * @param failReason The string which encapsulates the reason that the sync has faild
     */
    public static void sendSyncCompletedBroadcast(Context context, boolean syncSuccessful,
            String failReason) {
        final Intent intent = new Intent(Broadcast.SYNC_COMPLETED);

        intent.putExtra(IntentExtraKey.SYNC_BROADCAST_MESSAGE,
                syncSuccessful ? BROADCAST_SYNC_SUCCESS : BROADCAST_SYNC_FAILED);
        intent.putExtra(IntentExtraKey.SYNC_SUCCESSFUL, syncSuccessful);
        context.sendBroadcast(intent);
    }

    public static void sendUpdatePendingSyncInfoBroadcast(Context context, boolean forceUpdate) {
        final Intent intent = new Intent(Broadcast.UPDATE_SYNC_INFO);
        intent.putExtra(IntentExtraKey.FORCE_SYNC_INFO_UPDATE, forceUpdate);
        context.sendBroadcast(intent);
    }
}
