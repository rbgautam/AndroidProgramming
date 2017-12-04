package com.iaai.onyard.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.application.OnYard.SetSaleId;
import com.iaai.onyard.classes.SetSaleField;
import com.iaai.onyard.http.SetSaleHttpPost;
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
 * Param 1: set sale data - List[SetSaleField] <br>
 * Param 2: set sale start time - Long <br>
 * Param 3: set sale end time - Long <br>
 * Param 4: context - Context <br>
 * Param 5: user branch number - Integer <br>
 * Param 6: user login - String <br>
 * Param 7: GPS location - Location <br>
 * </P>
 * 
 * @author wferguso
 */
public class CommitSetSaleDataTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        final Context context = (Context) params[4];
        try {
            final VehicleInfo vehInfo = (VehicleInfo) params[0];
            final List<SetSaleField> setSaleFields = (List<SetSaleField>) params[1];
            final long startDateTime = ((Long) params[2]).longValue();
            final long endDateTime = ((Long) params[3]).longValue();
            final int userBranchNumber = (Integer) params[5];
            final String userLogin = (String) params[6];
            final Location location = (Location) params[7];

            final List<DataPendingSync> syncDataList = new ArrayList<DataPendingSync>();
            final String sessionId = UUID.randomUUID().toString();
            String newAisle = "";
            int newAuctionItemSeqNumber = 0, newAuctionNumber = 0;
            for (final SetSaleField field : setSaleFields) {
                if (field.hasSelection() && field.getDataMemberName() != null) {
                    String value = null;
                    if (field.getInputType() == OnYardFieldInputType.LIST
                            || field.getInputType() == OnYardFieldInputType.CHECKBOX) {
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
                            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID,
                                    sessionId, field.getDataMemberName(), null, (long) Integer
                                    .parseInt(value), null));
                            break;
                        case DOUBLE:
                            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID,
                                    sessionId, field.getDataMemberName(), null, null, Double
                                    .parseDouble(value)));
                            break;
                        case STRING:
                            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID,
                                    sessionId, field.getDataMemberName(), value, null, null));
                            break;
                        default:
                            break;
                    }

                    if (field.getId() == SetSaleId.AUCTION_ITEM_SEQUENCE_NUMBER) {
                        newAuctionItemSeqNumber = Integer.parseInt(value);
                    }
                    else {
                        if (field.getId() == SetSaleId.AUCTION_NUMBER) {
                            newAuctionNumber = Integer.parseInt(value);
                        }
                        else {
                            if (field.getId() == SetSaleId.SALE_AISLE) {
                                newAisle = value;
                            }
                        }
                    }
                }
            }
            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                    SetSaleHttpPost.AUCTION_DATE_KEY, null, vehInfo.getAuctionDate(), null));
            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                    SetSaleHttpPost.OLD_AISLE_KEY, vehInfo.getAisle(), null, null));
            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                    SetSaleHttpPost.OLD_STALL_KEY, null, (long) vehInfo.getStall(), null));
            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                    SetSaleHttpPost.USER_BRANCH_KEY, null, (long) userBranchNumber, null));
            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                    SetSaleHttpPost.STOCK_NUMBER_KEY, vehInfo.getStockNumber(), null, null));
            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                    SetSaleHttpPost.START_DATETIME_KEY, null, startDateTime, null));
            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                    SetSaleHttpPost.END_DATETIME_KEY, null, endDateTime, null));
            syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                    SetSaleHttpPost.USER_LOGIN_KEY, userLogin, null, null));

            if (location != null) {
                syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                        SetSaleHttpPost.LATITUDE_KEY, null, null, location.getLatitude()));
                syncDataList.add(new DataPendingSync(OnYardContract.SETSALE_APP_ID, sessionId,
                        SetSaleHttpPost.LONGITUDE_KEY, null, null, location.getLongitude()));
            }

            final int dataListLength = syncDataList.size();
            final ContentValues[] contentValues = new ContentValues[dataListLength];

            for (int index = 0; index < dataListLength; index++) {
                contentValues[index] = syncDataList.get(index).getContentValues();
            }

            context.getContentResolver().bulkInsert(OnYardContract.DataPendingSync.CONTENT_URI,
                    contentValues);

            final ContentValues values = new ContentValues();
            values.put(OnYardContract.Vehicles.COLUMN_NAME_AISLE, newAisle);
            values.put(OnYardContract.Vehicles.COLUMN_NAME_STALL, newAuctionItemSeqNumber);
            values.put(OnYardContract.Vehicles.COLUMN_NAME_AUCTION_ITEM_SEQ_NUMBER,
                    newAuctionItemSeqNumber);
            values.put(OnYardContract.Vehicles.COLUMN_NAME_AUCTION_NUMBER, newAuctionNumber);
            context.getContentResolver().update(OnYardContract.Vehicles.CONTENT_URI, values,
                    OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + "=?",
                    new String[] { vehInfo.getStockNumber() });

            BroadcastHelper.sendUpdatePendingSyncInfoBroadcast(context, true);

            return null;
        }
        catch (final Exception e) {
            LogHelper.logError(context, e, this.getClass().getSimpleName());
            return null;
        }
    }
}
