package com.iaai.onyard.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.listener.GetPendingCountListener;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to get the number of sessions with data pending sync.
 * <P>
 * Param 0: application context - Context <br>
 * Param 1: listener - GetPendingCountListener <br>
 * </P>
 * 
 * @author wferguso
 */
public class GetPendingCountTask extends AsyncTask<Object, Void, Integer> {

    private WeakReference<GetPendingCountListener> mWeakListener;

    @Override
    protected Integer doInBackground(Object... params) {
        Cursor sessionCountCursor = null;
        int sessionCount = -1;
        try {
            final Context context = (Context) params[0];
            mWeakListener = new WeakReference<GetPendingCountListener>(
                    (GetPendingCountListener) params[1]);

            sessionCountCursor = context.getContentResolver()
                    .query(OnYardContract.DataPendingSync.CONTENT_URI,
                            new String[] { "COUNT(DISTINCT "
                                    + OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID
                                    + ") AS count" },
                                    null, null, null);

            sessionCountCursor.moveToFirst();

            sessionCount = sessionCountCursor.getInt(0);
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }
        finally {
            if (sessionCountCursor != null) {
                sessionCountCursor.close();
            }
        }
        return sessionCount;
    }

    @Override
    protected void onPostExecute(Integer sessionCount) {
        if (mWeakListener.get() != null) {
            mWeakListener.get().onPendingCountRetrieved(sessionCount);
        }
    }
}
