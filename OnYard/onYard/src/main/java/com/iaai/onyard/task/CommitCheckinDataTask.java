package com.iaai.onyard.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.classes.CheckinField;
import com.iaai.onyard.http.CheckinHttpPost;
import com.iaai.onyard.utility.BroadcastHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.DataPendingSync;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to commit all check-in data for a stock. Check-in data is inserted into the database.
 * Parameters for execute:
 * <P>
 * Param 0: stock info - VehicleInfo <br>
 * Param 1: check-in data - List[CheckinField] <br>
 * Param 2: check-in start time - Long <br>
 * Param 3: check-in end time - Long <br>
 * Param 4: context - Context <br>
 * Param 5: branch number - Integer <br>
 * Param 6: user login - String <br>
 * </P>
 * 
 * @author wferguso
 */
public class CommitCheckinDataTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        try {
            final VehicleInfo vehInfo = (VehicleInfo) params[0];
            final List<CheckinField> checkinFields = (List<CheckinField>) params[1];
            final long startDateTime = ((Long) params[2]).longValue();
            final long endDateTime = ((Long) params[3]).longValue();
            final Context context = (Context) params[4];
            final int userBranchNumber = (Integer) params[5];
            final String userLogin = (String) params[6];

            final List<DataPendingSync> syncDataList = new ArrayList<DataPendingSync>();
            final String sessionId = UUID.randomUUID().toString();
            for (final CheckinField field : checkinFields) {
                if (field.hasSelection()) {
                    String value = null;
                    if(field.getInputType() == OnYardFieldInputType.LIST
                            || field.getInputType() == OnYardFieldInputType.CHECKBOX)
                    {
                        value = field.getSelectedOption().getValue();
                    }
                    if (field.getInputType() == OnYardFieldInputType.NUMERIC
                            || field.getInputType() == OnYardFieldInputType.ALPHANUMERIC
                            || field.getInputType() == OnYardFieldInputType.TEXT) {
                        value = field.getEnteredValue();
                    }

                    if (value == null) {
                        continue;
                    }

                    switch (field.getFieldType()) {
                        case BOOLEAN:
                        case INTEGER:
                            syncDataList.add(new DataPendingSync(OnYardContract.CHECKIN_APP_ID,
                                    sessionId, field.getDataMemberName(), null, (long) Integer
                                    .parseInt(value),
                                    null));
                            break;
                        case DOUBLE:
                            syncDataList
                            .add(new DataPendingSync(OnYardContract.CHECKIN_APP_ID,
                                    sessionId, field.getDataMemberName(), null, null, Double
                                    .parseDouble(value)));
                            break;
                        case STRING:
                            syncDataList.add(new DataPendingSync(OnYardContract.CHECKIN_APP_ID,
                                    sessionId, field.getDataMemberName(), value, null, null));
                            break;
                        default:
                            break;
                    }
                }
            }

            syncDataList.add(new DataPendingSync(OnYardContract.CHECKIN_APP_ID, sessionId,
                    CheckinHttpPost.USER_BRANCH_KEY, null, (long) userBranchNumber, null));
            syncDataList.add(new DataPendingSync(OnYardContract.CHECKIN_APP_ID, sessionId,
                    CheckinHttpPost.STOCK_NUMBER_KEY, vehInfo.getStockNumber(), null, null));
            syncDataList.add(new DataPendingSync(OnYardContract.CHECKIN_APP_ID, sessionId,
                    CheckinHttpPost.START_DATETIME_KEY, null, startDateTime, null));
            syncDataList.add(new DataPendingSync(OnYardContract.CHECKIN_APP_ID, sessionId,
                    CheckinHttpPost.END_DATETIME_KEY, null, endDateTime, null));
            syncDataList.add(new DataPendingSync(OnYardContract.CHECKIN_APP_ID, sessionId,
                    CheckinHttpPost.INSPECTION_DONE_BY_KEY, userLogin, null, null));

            final int dataListLength = syncDataList.size();
            final ContentValues[] contentValues = new ContentValues[dataListLength];

            for (int index = 0; index < dataListLength; index++) {
                contentValues[index] = syncDataList.get(index).getContentValues();
            }

            context.getContentResolver().bulkInsert(OnYardContract.DataPendingSync.CONTENT_URI,
                    contentValues);

            final ContentValues salvageTypeValues = new ContentValues();
            salvageTypeValues.put(OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_TYPE,
                    vehInfo.getSalvageType());
            context.getContentResolver().update(OnYardContract.Vehicles.CONTENT_URI,
                    salvageTypeValues, OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + "=?",
                    new String[] { vehInfo.getStockNumber() });

            BroadcastHelper.sendUpdatePendingSyncInfoBroadcast(context, true);

            return null;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[4], e, this.getClass().getSimpleName());
            return null;
        }
    }
}
