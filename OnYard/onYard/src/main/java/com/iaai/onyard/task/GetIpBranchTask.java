package com.iaai.onyard.task;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.event.IpBranchRetrievedEvent;
import com.iaai.onyard.event.IpBranchRetrievedEvent.IpBranchResult;
import com.iaai.onyard.http.BranchHttpGet;
import com.iaai.onyard.sync.HTTPHelper;
import com.iaai.onyard.utility.LogHelper;
import com.squareup.otto.Bus;

/**
 * AsyncTask to get the branch number matching this device's IP. This task will make a network call
 * to perform this check. If a bus is supplied, it will be notified when the branch number result is
 * obtained/changed.
 * <P>
 * Param 0: app context - Context <br>
 * Param 1: Otto event bus - Bus <br>
 * </P>
 * 
 * @author wferguso
 */
public class GetIpBranchTask extends AsyncTask<Object, Void, IpBranchResult> {

    private static Bus sBus;

    @Override
    protected IpBranchResult doInBackground(Object... params) {
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];

            if (!HTTPHelper.isNetworkAvailable(context) || !HTTPHelper.isServerAvailable()) {
                return IpBranchResult.NO_NETWORK;
            }

            final OnYardPreferences preferences = new OnYardPreferences(context);
            final BranchHttpGet branchRequest = new BranchHttpGet(context);
            final String ipBranchInfo = branchRequest.get();
            final String[] ipBranchParts = ipBranchInfo.split("\\|");
            final String newIpBranchNumber = ipBranchParts[0];

            if (newIpBranchNumber.isEmpty()) {
                if (!preferences.isBranchOverrideActive()) {
                    return IpBranchResult.NO_IP_BRANCH_FOUND;
                }
            }
            else {
                if (!newIpBranchNumber.equals(preferences.getIpBranchNumber())) {
                    final String newIpBranchName = ipBranchParts[1];

                    preferences.setIpBranchNumber(newIpBranchNumber);
                    preferences.setIpBranchName(newIpBranchName);

                    if (!preferences.isBranchOverrideActive()) {
                        return IpBranchResult.EFFECTIVE_BRANCH_CHANGED;
                    }
                }
            }

            return IpBranchResult.IP_BRANCH_SET;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
            return IpBranchResult.NO_IP_BRANCH_FOUND;
        }
    }

    @Override
    protected void onPostExecute(IpBranchResult result) {
        if (sBus != null) {
            sBus.post(new IpBranchRetrievedEvent(result));
        }
    }
}