package com.iaai.onyard.task;

import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;

import com.iaai.onyard.http.LocationHttpPost;
import com.iaai.onyard.utility.BroadcastHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.DataPendingSync;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to commit all location data for a stock. Location data is inserted into the database.
 * Parameters for execute:
 * <P>
 * Param 0: vehicle info - VehicleInfo <br>
 * Param 1: location start time - Long <br>
 * Param 2: location end time - Long <br>
 * Param 3: new aisle - String <br>
 * Param 4: new stall - Integer <br>
 * Param 5: context - Context <br>
 * Param 6: user login - String <br>
 * Param 7: user branch number - Integer <br>
 * Param 8: GPS location - Location <br>
 * </P>
 * 
 * @author wferguso
 */
public class CommitLocationDataTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        try {
            final VehicleInfo vehicle = (VehicleInfo) params[0];
            final long startDateTime = ((Long) params[1]).longValue();
            final long endDateTime = ((Long) params[2]).longValue();
            final String newAisle = (String) params[3];
            final int newStall = (Integer) params[4];
            final Context context = (Context) params[5];
            final String userLogin = (String) params[6];
            final int userBranch = (Integer) params[7];

            DataPendingSync stockNumData, newAisleData, newStallData, oldAisleData, oldStallData, userLoginData, userBranchData, startTimeData, endTimeData, adminBranchData, latitudeData, longitudeData, batteryLevelData;
            final String sessionId = UUID.randomUUID().toString();

            stockNumData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.STOCK_NUMBER_KEY, vehicle.getStockNumber(), null, null);
            newAisleData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.NEW_AISLE_KEY, newAisle, null, null);
            newStallData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.NEW_STALL_KEY, null, (long) newStall, null);
            oldAisleData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.OLD_AISLE_KEY, vehicle.getAisle(), null, null);
            oldStallData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.OLD_STALL_KEY, null, (long) vehicle.getStall(), null);
            userLoginData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.USER_LOGIN_KEY, userLogin, null, null);
            userBranchData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.USER_BRANCH_KEY, null, (long) userBranch, null);
            startTimeData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.START_DATETIME_KEY, null, startDateTime, null);
            endTimeData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.END_DATETIME_KEY, null, endDateTime, null);
            adminBranchData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.ADMIN_BRANCH_KEY, null, (long) vehicle.getAdminBranch(), null);
            batteryLevelData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                    LocationHttpPost.BATTERY_LEVEL_KEY, null, (long) getCurrentBatteryLevel(context), null);

            ContentValues[] locationValues = null;
            if (params[8] != null) {
                final Location location = (Location) params[8];

                latitudeData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                        LocationHttpPost.LATITUDE_KEY, null, null, location.getLatitude());
                longitudeData = new DataPendingSync(OnYardContract.LOCATION_APP_ID, sessionId,
                        LocationHttpPost.LONGITUDE_KEY, null, null, location.getLongitude());

                locationValues = new ContentValues[] { stockNumData.getContentValues(),
                        newAisleData.getContentValues(), newStallData.getContentValues(),
                        oldAisleData.getContentValues(), oldStallData.getContentValues(),
                        userLoginData.getContentValues(), userBranchData.getContentValues(),
                        startTimeData.getContentValues(), endTimeData.getContentValues(),
                        adminBranchData.getContentValues(), batteryLevelData.getContentValues(),
                        latitudeData.getContentValues(), longitudeData.getContentValues() };
            }
            else {
                locationValues = new ContentValues[] { stockNumData.getContentValues(),
                        newAisleData.getContentValues(), newStallData.getContentValues(),
                        oldAisleData.getContentValues(), oldStallData.getContentValues(),
                        userLoginData.getContentValues(), userBranchData.getContentValues(),
                        startTimeData.getContentValues(), endTimeData.getContentValues(),
                        adminBranchData.getContentValues(), batteryLevelData.getContentValues() };
            }
            context.getContentResolver().bulkInsert(OnYardContract.DataPendingSync.CONTENT_URI,
                    locationValues);

            final ContentValues values = new ContentValues();
            values.put(OnYardContract.Vehicles.COLUMN_NAME_AISLE, newAisle);
            values.put(OnYardContract.Vehicles.COLUMN_NAME_STALL, newStall);
            context.getContentResolver().update(OnYardContract.Vehicles.CONTENT_URI, values,
                    OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + "=?",
                    new String[] { vehicle.getStockNumber() });

            BroadcastHelper.sendUpdatePendingSyncInfoBroadcast(context, true);

            return null;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[5], e, this.getClass().getSimpleName());
            return null;
        }
    }

    private short getCurrentBatteryLevel(Context context) {
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        final int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        final float batteryPct = level / (float) scale;

        return (short) (batteryPct * 100);
    }
}
