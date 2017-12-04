package com.iaai.onyard.task;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.event.SetSaleAuctionVehiclesRetrievedEvent;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

/**
 * AsyncTask to pull a list of the vehicles from the database for the current branch which have the
 * selected Auction_Date and Status is 'k10', 'k13', 'k14' and (Auction_Number == null and
 * Auction_item_sequence_number == null) . The vehicles are returned in an ArrayList of VehicleInfo
 * objects once they have been extracted. Parameters for execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: Otto event bus - Bus <br>
 * Param 2: AuctionDate - Long <br>
 * </P>
 * 
 * @author rgautam
 */
public class GetAuctionVehiclesTask extends AsyncTask<Object, Void, ArrayList<VehicleInfo>> {

    private static Bus sBus;

    @Override
    protected ArrayList<VehicleInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];
            // Pass the Auction Date
            final Long auction_Date = (Long) params[2];

            final ArrayList<VehicleInfo> auctionVehicles = new ArrayList<VehicleInfo>();
            if (auction_Date == null || auction_Date == 0L) {
                return auctionVehicles;
            }

            // Modify the selection to include the criteria
            // selected Auction_Date
            // and Status is 'k10', 'k13', 'k14' and (Auction_Number == null
            // and Auction_item_sequence_number == null)
            queryResult = context.getContentResolver().query(
                    OnYardContract.Vehicles.CONTENT_URI,
                    new String[] { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                            OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
                            OnYardContract.Vehicles.COLUMN_NAME_VIN,
                            OnYardContract.Vehicles.COLUMN_NAME_YEAR,
                            OnYardContract.Vehicles.COLUMN_NAME_MAKE,
                            OnYardContract.Vehicles.COLUMN_NAME_MODEL },
                            "(" + OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=? " + " OR "
                                    + OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=? " + " OR "
                                    + OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=? " + ")" + " AND "
                                    + OnYardContract.Vehicles.COLUMN_NAME_AUCTION_DATE_UNIX + "=? "
                                    + " AND " + OnYardContract.Vehicles.COLUMN_NAME_AUCTION_NUMBER
                                    + " is null " + " AND "
                                    + OnYardContract.Vehicles.COLUMN_NAME_AUCTION_ITEM_SEQ_NUMBER
                                    + " is null ",
                                    new String[] { OnYard.SCHEDULE_FOR_SALE, OnYard.MANUAL_SALE_LOCKED_DOWN,
                            OnYard.SYSTEM_SALE_LOCKED_DOWN, String.valueOf(auction_Date) },
                            OnYardContract.Vehicles.DEFAULT_SORT_ORDER);

            if (queryResult == null || !queryResult.moveToFirst()) {
                return auctionVehicles;
            }

            VehicleInfo vehicleInfo;
            do {
                if (isCancelled()) {
                    return null;
                }

                vehicleInfo = new VehicleInfo(queryResult);

                auctionVehicles.add(vehicleInfo);
            }
            while (queryResult.moveToNext());

            if (auctionVehicles.size() > OnYard.MAX_LIST_STOCKS) {
                auctionVehicles.subList(OnYard.MAX_LIST_STOCKS, auctionVehicles.size()).clear();
            }

            return auctionVehicles;
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
    protected void onPostExecute(ArrayList<VehicleInfo> checkinVehicles) {
        if (sBus != null) {
            sBus.post(new SetSaleAuctionVehiclesRetrievedEvent(checkinVehicles));
        }
    }

    @Override
    protected void onCancelled(ArrayList<VehicleInfo> checkinVehicles) {}
}
