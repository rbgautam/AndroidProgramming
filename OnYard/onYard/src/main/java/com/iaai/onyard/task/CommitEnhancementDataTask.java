package com.iaai.onyard.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.classes.EnhancementField;
import com.iaai.onyard.http.EnhancementHttpPost;
import com.iaai.onyard.utility.BroadcastHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.DataPendingSync;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to commit all enhancement data for a stock. Check-in data is inserted into the
 * database. Parameters for execute:
 * <P>
 * Param 0: stock info - VehicleInfo <br>
 * Param 1: enhancement data - List[EnhancementField] <br>
 * Param 2: enhancement start time - Long <br>
 * Param 3: enhancement end time - Long <br>
 * Param 4: context - Context <br>
 * Param 5: user branch number - Integer <br>
 * Param 6: user login - String <br>
 * </P>
 * 
 * @author wferguso
 */
public class CommitEnhancementDataTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        final Context context = (Context) params[4];
        try {
            final VehicleInfo vehInfo = (VehicleInfo) params[0];
            final List<EnhancementField> enhancementFields = (List<EnhancementField>) params[1];
            final long startDateTime = ((Long) params[2]).longValue();
            final long endDateTime = ((Long) params[3]).longValue();
            final int userBranchNumber = (Integer) params[5];
            final String userLogin = (String) params[6];

            final String sessionId = UUID.randomUUID().toString();
            final JSONArray enhancementListJson = new JSONArray();
            for (int index = 0; index < enhancementFields.size(); index++) {
                final EnhancementField field = enhancementFields.get(index);
                if (field.hasSelection() && !field.isInitalOptionSelected()) {
                    final String value = field.getSelectedOption().getValue();
                    if (value == null) {
                        continue;
                    }

                    final JSONObject singleEnhancementJson = new JSONObject();
                    singleEnhancementJson.put(EnhancementHttpPost.ID_KEY, field.getId());
                    singleEnhancementJson.put(EnhancementHttpPost.STATUS_CODE_KEY, value);

                    enhancementListJson.put(singleEnhancementJson);
                }
            }

            final ArrayList<DataPendingSync> syncDataList = new ArrayList<DataPendingSync>();
            syncDataList.add(new DataPendingSync(OnYardContract.ENHANCEMENT_APP_ID, sessionId,
                    EnhancementHttpPost.ENHANCEMENT_ID_STATUS_LIST_KEY, enhancementListJson.toString(), null, null));
            syncDataList.add(new DataPendingSync(OnYardContract.ENHANCEMENT_APP_ID, sessionId,
                    EnhancementHttpPost.USER_BRANCH_KEY, null, (long) userBranchNumber, null));
            syncDataList.add(new DataPendingSync(OnYardContract.ENHANCEMENT_APP_ID, sessionId,
                    EnhancementHttpPost.STOCK_NUMBER_KEY, vehInfo.getStockNumber(), null, null));
            syncDataList.add(new DataPendingSync(OnYardContract.ENHANCEMENT_APP_ID, sessionId,
                    EnhancementHttpPost.START_DATETIME_KEY, null, startDateTime, null));
            syncDataList.add(new DataPendingSync(OnYardContract.ENHANCEMENT_APP_ID, sessionId,
                    EnhancementHttpPost.END_DATETIME_KEY, null, endDateTime, null));
            syncDataList.add(new DataPendingSync(OnYardContract.ENHANCEMENT_APP_ID, sessionId,
                    EnhancementHttpPost.USER_LOGIN_KEY, userLogin, null, null));
            syncDataList.add(new DataPendingSync(OnYardContract.ENHANCEMENT_APP_ID, sessionId,
                    EnhancementHttpPost.NUM_ENHANCEMENTS_KEY, null,
                    (long) enhancementListJson.length(), null));

            final int dataListLength = syncDataList.size();
            final ContentValues[] contentValues = new ContentValues[dataListLength];

            for (int index = 0; index < dataListLength; index++) {
                contentValues[index] = syncDataList.get(index).getContentValues();
            }

            context.getContentResolver().bulkInsert(OnYardContract.DataPendingSync.CONTENT_URI,
                    contentValues);

            BroadcastHelper.sendUpdatePendingSyncInfoBroadcast(context, true);

            return null;
        }
        catch (final Exception e) {
            LogHelper.logError(context, e, this.getClass().getSimpleName());
            return null;
        }
    }
}
