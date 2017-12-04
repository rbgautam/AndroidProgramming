package com.iaai.onyard.task;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.event.HolidaysRetrievedEvent;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.HolidayInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

/**
 * AsyncTask to pull a list of the all holidays. Parameters for execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: Otto event bus - Bus <br>
 * </P>
 * 
 * @author rgautam
 */
public class GetHolidaysTask extends AsyncTask<Object, Void, ArrayList<HolidayInfo>> {

    private static Bus sBus;

    @Override
    protected ArrayList<HolidayInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];

            final String orderby = OnYardContract.Holiday.COLUMN_NAME_HOLIDAY_DATE + " DESC";
            queryResult = context.getContentResolver().query(OnYardContract.Holiday.CONTENT_URI,
                    null, null, null, orderby);

            final ArrayList<HolidayInfo> holidays = new ArrayList<HolidayInfo>();
            if (queryResult == null || !queryResult.moveToFirst()) {
                return holidays;
            }

            HolidayInfo holidayInfo;
            do {
                holidayInfo = new HolidayInfo(queryResult);

                holidays.add(holidayInfo);
            }
            while (queryResult.moveToNext());

            return holidays;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return new ArrayList<HolidayInfo>();
    }

    @Override
    protected void onPostExecute(ArrayList<HolidayInfo> holidays) {
        if (sBus != null) {
            sBus.post(new HolidaysRetrievedEvent(holidays));
        }
    }

}
