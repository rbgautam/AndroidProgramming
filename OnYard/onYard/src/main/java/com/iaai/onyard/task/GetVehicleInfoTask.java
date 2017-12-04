package com.iaai.onyard.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.iaai.onyard.listener.GetVehicleInfoListener;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to get vehicle info from stock number.
 * <P>
 * Param 0: application context - Context <br>
 * Param 1: listener - GetVehicleInfoListener <br>
 * Param 2: stock number - String <br>
 * </P>
 * 
 * @author wferguso
 */
public class GetVehicleInfoTask extends AsyncTask<Object, Void, Object[]> {

    private WeakReference<GetVehicleInfoListener> mWeakListener;

    @Override
    protected Object[] doInBackground(Object... params) {
        Cursor vehicleInfoCursor = null;
        final Context context = (Context) params[0];
        try {
            mWeakListener = new WeakReference<GetVehicleInfoListener>(
                    (GetVehicleInfoListener) params[1]);
            final String stockNum = (String) params[2];

            final String[] detailsProj = new String[] {
                    OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                    OnYardContract.Vehicles.COLUMN_NAME_VIN,
                    OnYardContract.Vehicles.COLUMN_NAME_YEAR,
                    OnYardContract.Vehicles.COLUMN_NAME_MAKE,
                    OnYardContract.Vehicles.COLUMN_NAME_MODEL,
                    OnYardContract.Vehicles.COLUMN_NAME_COLOR,
                    OnYardContract.Vehicles.COLUMN_NAME_DAMAGE,
                    OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
                    OnYardContract.Vehicles.COLUMN_NAME_LATITUDE,
                    OnYardContract.Vehicles.COLUMN_NAME_LONGITUDE,
                    OnYardContract.Vehicles.COLUMN_NAME_AISLE,
                    OnYardContract.Vehicles.COLUMN_NAME_STALL,
                    OnYardContract.Vehicles.COLUMN_NAME_STATUS,
                    OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_TYPE,
                    OnYardContract.Vehicles.COLUMN_NAME_HAS_IMAGES,
                    OnYardContract.Vehicles.COLUMN_NAME_SALE_DOC_TYPE,
                    OnYardContract.Vehicles.COLUMN_NAME_RUN_DRIVE_IND,
                    OnYardContract.Vehicles.COLUMN_NAME_AUCTION_DATE_UNIX,
                    OnYardContract.Vehicles.COLUMN_NAME_ADMIN_BRANCH,
                    OnYardContract.Vehicles.COLUMN_NAME_LOSS_TYPE,
                    OnYardContract.Vehicles.COLUMN_NAME_AUCTION_NUMBER,
                    OnYardContract.Vehicles.COLUMN_NAME_AUCTION_ITEM_SEQ_NUMBER,
                    OnYardContract.Vehicles.COLUMN_NAME_WAIT_CHECKIN_DATETIME };

            vehicleInfoCursor = context.getContentResolver().query(
                    Uri.withAppendedPath(OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE,
                            stockNum), detailsProj, null, null, null);

            if (vehicleInfoCursor != null && vehicleInfoCursor.moveToFirst()) {
                return new Object[] { new VehicleInfo(vehicleInfoCursor), context };
            }
        }
        catch (final Exception e) {
            LogHelper.logError(context, e, this.getClass().getSimpleName());
        }
        finally {
            if (vehicleInfoCursor != null) {
                vehicleInfoCursor.close();
            }
        }
        return new Object[] { new VehicleInfo(), context };
    }

    @Override
    protected void onPostExecute(Object[] returnVals) {
        final VehicleInfo vehicle = (VehicleInfo) returnVals[0];
        final Context context = (Context) returnVals[1];
        if (mWeakListener.get() != null) {
            mWeakListener.get().onVehicleInfoRetrieved(vehicle, context);
        }
    }

}
