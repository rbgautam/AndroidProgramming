package com.iaai.onyard.task;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.event.ConnectivityCheckedEvent;
import com.iaai.onyard.sync.HTTPHelper;
import com.squareup.otto.Bus;


public class CheckConnectivityTask extends AsyncTask<Object, Void, Boolean> {

    private static Bus sBus;

    @Override
    protected Boolean doInBackground(Object... params) {

        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];

            Boolean isConnected = false;

            if (HTTPHelper.isNetworkAvailable(context) && HTTPHelper.isServerAvailable()) {
                isConnected = true;
            }
            return isConnected;
        }
        catch (final Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isConnected) {
        if (sBus != null) {
            sBus.post(new ConnectivityCheckedEvent(isConnected));
        }
    }

}
