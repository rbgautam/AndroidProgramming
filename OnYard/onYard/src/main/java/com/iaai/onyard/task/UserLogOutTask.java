package com.iaai.onyard.task;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.event.LogoutCompleteEvent;
import com.iaai.onyard.event.LogoutCompleteEvent.LogoutResult;
import com.iaai.onyard.sync.HTTPHelper;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.BroadcastHelper;
import com.iaai.onyard.utility.LogHelper;
import com.squareup.otto.Bus;

/**
 * AsyncTask to log user out from device and update server accordingly.
 * <P>
 * Param 0: application context - Context <br>
 * Param 1: Otto event bus - Bus
 * <P>
 * 
 * @author wferguso
 */
public class UserLogOutTask extends AsyncTask<Object, Void, LogoutResult> {

    private static Bus sBus;

    @Override
    protected LogoutResult doInBackground(Object... params) {
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];

            if (HTTPHelper.isNetworkAvailable(context) && HTTPHelper.isServerAvailable()) {
                AuthenticationHelper.logCurrentUserOut(context);
                BroadcastHelper.sendLogoutBroadcast(context, true);
                return LogoutResult.SUCCESS;
            }
            else {
                return LogoutResult.NO_NETWORK;
            }
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
            return LogoutResult.FAILURE;
        }
    }

    @Override
    protected void onPostExecute(LogoutResult result) {
        if (sBus != null) {
            sBus.post(new LogoutCompleteEvent(result));
        }
    }
}
