package com.iaai.onyard.task;

import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.http.CheckinMetricsHttpPost;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.DataPendingSync;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to commit device info for an image session. Device info and session data is inserted
 * into the database. Parameters for execute:
 * <P>
 * Param 0: application context - Context <br>
 * Param 1: user login - String <br>
 * Param 2: user branch number - Integer <br>
 * </P>
 * 
 * @author wferguso
 */
public class CommitCheckinMetricsTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        try {
            final Context context = (Context) params[0];
            final String userLogin = (String) params[1];
            final int userBranch = (Integer) params[2];

            DataPendingSync userLoginData, userBranchData;
            final String sessionID = UUID.randomUUID().toString();
            userLoginData = new DataPendingSync(OnYardContract.CHECKIN_METRICS_APP_ID, sessionID,
                    CheckinMetricsHttpPost.USER_LOGIN_KEY, userLogin, null, null);
            userBranchData = new DataPendingSync(OnYardContract.CHECKIN_METRICS_APP_ID, sessionID,
                    CheckinMetricsHttpPost.USER_BRANCH_KEY, null, (long) userBranch, null);

            context.getContentResolver().bulkInsert(
                    OnYardContract.DataPendingSync.CONTENT_URI,
                    new ContentValues[] { userLoginData.getContentValues(),
                            userBranchData.getContentValues(), });
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }

        return null;
    }
}
