package com.iaai.onyard.task;

import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.http.ImagerMetricsHttpPost;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.DataPendingSync;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to commit device info for an image session. Device info and session data is inserted
 * into the database. Parameters for execute:
 * <P>
 * Param 0: stock info - VehicleInfo <br>
 * Param 1: start time - Long <br>
 * Param 2: end time - Long <br>
 * Param 3: application context - Context <br>
 * Param 4: number of images taken - Integer <br>
 * Param 5: image set - Integer <br>
 * Param 6: user login - String <br>
 * Param 7: user branch number - Integer <br>
 * Param 8: image type id - String <br>
 * </P>
 * 
 * @author wferguso
 */
public class CommitImagerMetricsTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        try {
            final VehicleInfo vehicle = (VehicleInfo) params[0];
            final long startTime = ((Long) params[1]).longValue();
            final long endTime = ((Long) params[2]).longValue();
            final Context context = (Context) params[3];
            final int numImages = (Integer) params[4];
            final int imageSet = (Integer) params[5];
            final String userLogin = (String) params[6];
            final int userBranch = (Integer) params[7];
            final int imageTypeId = (Integer) params[8];

            DataPendingSync stockNumData, userLoginData, userBranchData, imageTypeData, salvageProviderIdData;
            DataPendingSync startTimeData, endTimeData, numImagesData, imageSetData, adminBranchData;
            final String sessionID = UUID.randomUUID().toString();
            userLoginData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.USER_LOGIN_KEY, userLogin, null, null);
            stockNumData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.STOCK_NUMBER_KEY, null,
                    (long) vehicle.getStockNumberInt(), null);
            startTimeData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.START_DATETIME_KEY, null, startTime, null);
            endTimeData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.END_DATETIME_KEY, null, endTime, null);
            numImagesData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.NUM_IMAGES_KEY, null, (long) numImages, null);
            imageSetData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.IMAGE_SET_KEY, null, (long) imageSet, null);
            userBranchData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.USER_BRANCH_KEY, null, (long) userBranch, null);
            imageTypeData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.IMAGE_TYPE_KEY, null, (long) imageTypeId, null);
            adminBranchData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID, sessionID,
                    ImagerMetricsHttpPost.ADMIN_BRANCH_KEY, null, (long) vehicle.getAdminBranch(),
                    null);
            salvageProviderIdData = new DataPendingSync(OnYardContract.IMAGER_METRICS_APP_ID,
                    sessionID, ImagerMetricsHttpPost.SALVAGE_PROVIDER_ID_KEY, null,
                    (long) vehicle.getSalvageProviderId(), null);

            context.getContentResolver().bulkInsert(
                    OnYardContract.DataPendingSync.CONTENT_URI,
                    new ContentValues[] { stockNumData.getContentValues(),
                            userLoginData.getContentValues(), startTimeData.getContentValues(),
                            endTimeData.getContentValues(), numImagesData.getContentValues(),
                            imageSetData.getContentValues(), userBranchData.getContentValues(),
                            imageTypeData.getContentValues(), adminBranchData.getContentValues(),
                            salvageProviderIdData.getContentValues() });
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[3], e, this.getClass().getSimpleName());
        }

        return null;
    }
}
