package com.iaai.onyard.task;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.event.ReshootVehiclesRetrievedEvent;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

/**
 * AsyncTask to pull a list of the top 250 vehicles from the database needing reshoots. The vehicles
 * are returned in an ArrayList of VehicleInfo objects once they have been extracted. Parameters for
 * execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: Otto event bus - Bus <br>
 * </P>
 * 
 * @author wferguso
 */
public class GetReshootVehiclesTask extends AsyncTask<Object, Void, ArrayList<VehicleInfo>> {

    private static Bus sBus;

    @Override
    protected ArrayList<VehicleInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];

            queryResult = context.getContentResolver().query(OnYardContract.Vehicles.RESHOOT_URI,
                    null, null, null, null);

            final ArrayList<VehicleInfo> reshootVehicles = new ArrayList<VehicleInfo>();
            if (queryResult == null || !queryResult.moveToFirst()) {
                return reshootVehicles;
            }

            VehicleInfo vehicleInfo;
            do {
                if (isCancelled()) {
                    return null;
                }

                vehicleInfo = new VehicleInfo(queryResult);

                reshootVehicles.add(vehicleInfo);
            }
            while (queryResult.moveToNext());

            if (reshootVehicles.size() > OnYard.MAX_LIST_STOCKS) {
                reshootVehicles.subList(OnYard.MAX_LIST_STOCKS, reshootVehicles.size() - 1).clear();
            }

            return reshootVehicles;
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
    protected void onPostExecute(ArrayList<VehicleInfo> reshootVehicles) {
        if (sBus != null) {
            sBus.post(new ReshootVehiclesRetrievedEvent(reshootVehicles));
        }
    }

    @Override
    protected void onCancelled(ArrayList<VehicleInfo> reshootVehicles) {}
}
