package com.iaai.onyard.task;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.event.MoveToSaleVehiclesRetrievedEvent;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

public class GetPendingMoveToSaleTask extends AsyncTask<Object, Void, ArrayList<VehicleInfo>> {

    private static Bus sBus;

    @Override
    protected ArrayList<VehicleInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];
            final ArrayList<VehicleInfo> locationVehicles = new ArrayList<VehicleInfo>();

            queryResult = context.getContentResolver().query(
                    OnYardContract.Vehicles.CONTENT_URI,
                    new String[] { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                            OnYardContract.Vehicles.COLUMN_NAME_VIN,
                            OnYardContract.Vehicles.COLUMN_NAME_YEAR,
                            OnYardContract.Vehicles.COLUMN_NAME_MAKE,
                            OnYardContract.Vehicles.COLUMN_NAME_MODEL,
                            OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
                            OnYardContract.Vehicles.COLUMN_NAME_SALE_DOC_TYPE,
                            OnYardContract.Vehicles.COLUMN_NAME_AISLE,
                            OnYardContract.Vehicles.COLUMN_NAME_STALL,
                            OnYardContract.Vehicles.COLUMN_NAME_RUN_DRIVE_IND },
                            OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=? OR "
                                    + OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=? OR "
                                    + OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=?",
                                    new String[] { OnYard.SCHEDULE_FOR_SALE, OnYard.MANUAL_SALE_LOCKED_DOWN,
                            OnYard.SYSTEM_SALE_LOCKED_DOWN },
                            OnYardContract.Vehicles.DEFAULT_SORT_ORDER);

            if (queryResult == null || !queryResult.moveToFirst()) {
                return locationVehicles;
            }

            VehicleInfo vehicle;
            do {
                if (isCancelled()) {
                    return null;
                }

                vehicle = new VehicleInfo(queryResult);

                locationVehicles.add(vehicle);
            }
            while (queryResult.moveToNext());

            if (locationVehicles.size() > OnYard.MAX_LIST_STOCKS) {
                locationVehicles.subList(OnYard.MAX_LIST_STOCKS, locationVehicles.size())
                .clear();
            }

            return locationVehicles;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return new ArrayList<VehicleInfo>();
    }

    @Override
    protected void onPostExecute(ArrayList<VehicleInfo> locationVehicles) {
        if (sBus != null) {
            sBus.post(new MoveToSaleVehiclesRetrievedEvent(locationVehicles));
        }
    }

}
