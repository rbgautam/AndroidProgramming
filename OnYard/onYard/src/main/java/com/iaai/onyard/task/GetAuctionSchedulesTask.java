package com.iaai.onyard.task;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.event.AuctionSchedulesRetrievedEvent;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.AuctionScheduleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

/**
 * AsyncTask to pull a list of future auction dates for this branch. The auction dates are returned
 * in an ArrayList of AuctionScheduleInfo objects once they have been extracted. Parameters for
 * execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: Otto event bus - Bus <br>
 * </P>
 * 
 * @author wferguso
 */
public class GetAuctionSchedulesTask extends AsyncTask<Object, Void, ArrayList<AuctionScheduleInfo>> {

    private static Bus sBus;

    @Override
    protected ArrayList<AuctionScheduleInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];

            queryResult = context.getContentResolver().query(
                    OnYardContract.AuctionSchedule.WITH_VEHICLES_URI,
                    new String[] { OnYardContract.AuctionSchedule.COLUMN_NAME_AUCTION_DATE,
                            OnYardContract.AuctionSchedule.COLUMN_NAME_NUM_AUCTIONS },
                    "auc." + OnYardContract.AuctionSchedule.COLUMN_NAME_AUCTION_DATE + ">?",
                            new String[] { String.valueOf(DataHelper.getUnixUtcTimeStamp()) }, null);

            final ArrayList<AuctionScheduleInfo> auctionSchedules = new ArrayList<AuctionScheduleInfo>();
            if (queryResult == null || !queryResult.moveToFirst()) {
                return auctionSchedules;
            }

            AuctionScheduleInfo auctionInfo;
            do {
                auctionInfo = new AuctionScheduleInfo(queryResult);

                auctionSchedules.add(auctionInfo);
            }
            while (queryResult.moveToNext());

            return auctionSchedules;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return new ArrayList<AuctionScheduleInfo>();
    }

    @Override
    protected void onPostExecute(ArrayList<AuctionScheduleInfo> auctionSchedules) {
        if (sBus != null) {
            sBus.post(new AuctionSchedulesRetrievedEvent(auctionSchedules));
        }
    }

}
