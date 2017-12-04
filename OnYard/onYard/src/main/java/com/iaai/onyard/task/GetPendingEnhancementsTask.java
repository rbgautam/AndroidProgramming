package com.iaai.onyard.task;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.EnhancementId;
import com.iaai.onyard.event.SalvageEnhancementsRetrievedEvent;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.SalvageEnhancementInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

/**
 * AsyncTask to pull a list of pending enhancements from the database. The enhancements are returned
 * in an ArrayList of SalvageEnhancementInfo objects once they have been extracted. Parameters for
 * execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: Otto event bus - Bus <br>
 * </P>
 * 
 * @author vcherce
 */
public class GetPendingEnhancementsTask extends
AsyncTask<Object, Void, ArrayList<SalvageEnhancementInfo>> {

    private static Bus sBus;

    @Override
    protected ArrayList<SalvageEnhancementInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];

            queryResult = context.getContentResolver().query(
                    OnYardContract.SalvageEnhancement.PENDING_ENHANCEMENT_URI, null, null, null,
                    null);

            final ArrayList<SalvageEnhancementInfo> salEnhancements = new ArrayList<SalvageEnhancementInfo>();
            if (queryResult == null || !queryResult.moveToFirst()) {
                return salEnhancements;
            }

            SalvageEnhancementInfo enhInfo;
            do {
                if (isCancelled()) {
                    return null;
                }

                enhInfo = new SalvageEnhancementInfo(queryResult);

                // hardcoded exclusion of certain enhancements if stock is ready for check-in
                if (enhInfo.getEnhancementId() == EnhancementId.CAR_START
                        || enhInfo.getEnhancementId() == EnhancementId.DRIVE_THROUGH
                        || enhInfo.getEnhancementId() == EnhancementId.RUN_AND_DRIVE) {
                    if (VehicleInfo.isCheckinEligible(enhInfo.getVehicleStatusCode())) {
                        continue;
                    }
                }

                salEnhancements.add(enhInfo);
            }
            while (queryResult.moveToNext());

            if (salEnhancements.size() > OnYard.MAX_LIST_ENHANCEMENTS) {
                salEnhancements.subList(OnYard.MAX_LIST_ENHANCEMENTS, salEnhancements.size())
                .clear();
            }

            return salEnhancements;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return new ArrayList<SalvageEnhancementInfo>();
    }

    @Override
    protected void onPostExecute(ArrayList<SalvageEnhancementInfo> salEnhancements) {
        if (sBus != null) {
            sBus.post(new SalvageEnhancementsRetrievedEvent(salEnhancements));
        }
    }

    @Override
    protected void onCancelled(ArrayList<SalvageEnhancementInfo> salEnhancements) {}
}
