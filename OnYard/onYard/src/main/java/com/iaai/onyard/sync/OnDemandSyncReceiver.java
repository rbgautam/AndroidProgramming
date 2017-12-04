package com.iaai.onyard.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iaai.onyard.utility.LogHelper;


public class OnDemandSyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            SyncHelper.requestOnDemandSync(context);
        }
        catch (final Exception e) {
            LogHelper.logError(context, e, this.getClass().getSimpleName());
        }
    }

}
