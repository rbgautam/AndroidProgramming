package com.iaai.onyard.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.event.CheckinVehiclesRetrievedEvent;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.HolidayInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

/**
 * AsyncTask to pull a list of the top 250 vehicles from the database needing checkin. The vehicles
 * are returned in an ArrayList of VehicleInfo objects once they have been extracted. Parameters for
 * execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: Otto event bus - Bus <br>
 * </P>
 * 
 * @author wferguso
 */
public class GetCheckinVehiclesTask extends AsyncTask<Object, Void, ArrayList<VehicleInfo>> {

    private static Bus sBus;
    private static ArrayList<HolidayInfo> mHolidayList;


    @Override
    protected ArrayList<VehicleInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final Context context = (Context) params[0];
            sBus = (Bus) params[1];
            mHolidayList = (ArrayList<HolidayInfo>) params[2];
            final String orderBy = OnYardContract.Vehicles.COLUMN_NAME_WAIT_CHECKIN_DATETIME
                    + " ASC";
            queryResult = context.getContentResolver().query(
                    OnYardContract.Vehicles.CONTENT_URI,
                    new String[] { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                            OnYardContract.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
                            OnYardContract.Vehicles.COLUMN_NAME_VIN,
                            OnYardContract.Vehicles.COLUMN_NAME_YEAR,
                            OnYardContract.Vehicles.COLUMN_NAME_MAKE,
                            OnYardContract.Vehicles.COLUMN_NAME_MODEL,
                            OnYardContract.Vehicles.COLUMN_NAME_WAIT_CHECKIN_DATETIME },
                            OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=?",
                            new String[] { OnYard.WAIT_CHECKIN_STATUS_CODE }, orderBy);

            final ArrayList<VehicleInfo> checkinVehicles = new ArrayList<VehicleInfo>();
            final ArrayList<VehicleInfo> checkinVehiclesWithZeroHrs = new ArrayList<VehicleInfo>();
            if (queryResult == null || !queryResult.moveToFirst()) {
                return checkinVehicles;
            }

            final String branchTimeZoneStr = DataHelper.getBranchTimeZone(context);

            VehicleInfo vehicleInfo;
            do {
                if (isCancelled()) {
                    return null;
                }

                vehicleInfo = new VehicleInfo(queryResult);

                // updating the elapsed hrs
                final long ElaspsedTime = getElapsedHRinfo(branchTimeZoneStr, vehicleInfo);

                vehicleInfo.setElaspsedTime(ElaspsedTime);
                if (ElaspsedTime > 0) {
                    checkinVehicles.add(vehicleInfo);
                }
                else {
                    checkinVehiclesWithZeroHrs.add(vehicleInfo);
                }

            }
            while (queryResult.moveToNext());
            // Collections.sort(checkinVehicles);
            // Adding vehicle with no Elapsed time to end of the list
            checkinVehicles.addAll(checkinVehiclesWithZeroHrs);

            if (checkinVehicles.size() > OnYard.MAX_LIST_STOCKS) {
                checkinVehicles.subList(OnYard.MAX_LIST_STOCKS, checkinVehicles.size()).clear();
            }

            return checkinVehicles;
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

    private int getElapsedHRinfo(String branchTimeZoneStr, VehicleInfo checkinVehicle) {

        long totalHrsSinceCheckIn = 0;

        final DataHelper.TimeZones branchTimeZone = branchTimeZoneStr == null ? DataHelper.TimeZones.CT
                : DataHelper.TimeZones.valueOf(branchTimeZoneStr);

        final String branchTimeZoneCity = DataHelper.GetTimeZoneCity(branchTimeZone);

        final Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone(branchTimeZoneCity));
        final Calendar checkedInDate = Calendar.getInstance(TimeZone
                .getTimeZone(branchTimeZoneCity));

        final long waitCheckinTime = checkinVehicle.getWaitCheckinDateTime() * 1000;

        if (waitCheckinTime == 0) {
            totalHrsSinceCheckIn = 0;
        }
        else {

            checkedInDate.setTimeInMillis(waitCheckinTime);
            currentDate.setTimeInMillis(DataHelper.getUnixUtcTimeStamp() * 1000);

            final int numHolidays = getHolidaysByCheckinDate(checkedInDate.getTimeInMillis(),
                    currentDate.getTimeInMillis());

            long totalDaysSinceCheckIn = getDateDiff(checkedInDate, currentDate);
            totalDaysSinceCheckIn = totalDaysSinceCheckIn
                    - getWeekendsBetweendates(checkedInDate, currentDate);
            totalDaysSinceCheckIn = totalDaysSinceCheckIn - numHolidays;

            totalHrsSinceCheckIn = totalDaysSinceCheckIn * 9;
            totalHrsSinceCheckIn = totalHrsSinceCheckIn
                    - startDateReduction(checkedInDate, isHoliday(checkedInDate));
            totalHrsSinceCheckIn = totalHrsSinceCheckIn
                    - endDateReduction(currentDate, isHoliday(currentDate));
        }

        return (int) totalHrsSinceCheckIn;
    }

    /**
     * Returns 1 is the passed date is a holiday
     * 
     * @param dt
     * @return 1 0r 0
     */
    private int isHoliday(Calendar dt) {
        int count = 0;

        final String stDate = DataHelper.getHolidayDateInStringFormat(dt.getTimeInMillis());

        for (final HolidayInfo holiday : mHolidayList) {
            if (stDate.equals(holiday.getHolidayDateInStringFormat())) {
                count++;
            }
        }
        return count;

    }

    /**
     * Query the Holiday table for count of days between a date range
     * 
     * @param searchVal The search value.
     * @return A cursor containing the query results.
     */
    private int getHolidaysByCheckinDate(long startDate, long endDate) {
        int count = 0;
        final String stDate = DataHelper.getHolidayDateInStringFormat(startDate);

        for (final HolidayInfo holiday : mHolidayList) {

            if (stDate.equals(holiday.getHolidayDateInStringFormat())) {
                count++;
            }

            if (holiday.getHolidayDateInMilliseconds() >= startDate
                    && holiday.getHolidayDateInMilliseconds() <= endDate) {
                count++;
            }
            if (holiday.getHolidayDateInMilliseconds() < startDate) {
                break;
            }
        }
        return count;
    }

    /**
     * Get a diff between two dates
     * 
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    private static long getDateDiff(Calendar date1, Calendar date2) {
        // final Calendar date = (Calendar) date1.clone();
        final Calendar startDate = Calendar.getInstance();
        startDate.set(date1.get(Calendar.YEAR), date1.get(Calendar.MONTH),
                date1.get(Calendar.DAY_OF_MONTH));
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        final Calendar endDate = Calendar.getInstance();
        endDate.set(date2.get(Calendar.YEAR), date2.get(Calendar.MONTH),
                date2.get(Calendar.DAY_OF_MONTH));
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);

        long daysBetween = 0;
        while (startDate.before(endDate)) {
            startDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween + 1;
    }

    /**
     * Returns number of weekends between 2 dates
     * 
     * @param startDate
     * @param endDate
     * @return weekCount
     */
    // TODO: UPDATE FOR CHECKIN DATE ON A SUNDAY
    private static int getWeekendsBetweendates(Calendar startDate, Calendar endDate) {
        int weekCount = 0;

        final Calendar startingDate = Calendar.getInstance();
        startingDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH));
        startingDate.set(Calendar.HOUR_OF_DAY, startDate.get(Calendar.HOUR_OF_DAY));
        startingDate.set(Calendar.MINUTE, startDate.get(Calendar.MINUTE));
        startingDate.set(Calendar.SECOND, startDate.get(Calendar.SECOND));
        startingDate.set(Calendar.MILLISECOND, startDate.get(Calendar.MILLISECOND));

        while (startingDate.compareTo(endDate) <= 0) {
            if (startingDate.get(Calendar.DAY_OF_WEEK) == 1
                    || startingDate.get(Calendar.DAY_OF_WEEK) == 7) {
                weekCount++;
            }
            startingDate.add(Calendar.DATE, 1);
        }
        return weekCount;
    }

    /**
     * Returns the number of hours to be reduced from total business hrs Depending on the startDate
     * (CheckinTime)
     * 
     * @param startDate
     * @return reduceHrs
     */
    private static int startDateReduction(Calendar startDate, int isHoliday) {
        int reduceHrs = 0;

        if (startDate.get(Calendar.DAY_OF_WEEK) != 1 && startDate.get(Calendar.DAY_OF_WEEK) != 7
                && isHoliday != 1) {
            // Calculate reduction hrs based on checkin time

            final Date stTime = new Date(startDate.get(Calendar.YEAR),
                    startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE),
                    startDate.get(Calendar.HOUR_OF_DAY), startDate.get(Calendar.MINUTE),
                    startDate.get(Calendar.SECOND));

            final Date loginTime = new Date(stTime.getYear(), stTime.getMonth(), stTime.getDate(),
                    8, 0, 0);

            final Date logoutTime = new Date(stTime.getYear(), stTime.getMonth(), stTime.getDate(),
                    17, 0, 0);

            if (stTime.after(logoutTime)) {
                reduceHrs = 9;
            }
            else
                if (stTime.before(loginTime)) {
                    reduceHrs = 0;
                }
                else {

                    long hrsDiff = stTime.getTime() - loginTime.getTime();
                    hrsDiff = hrsDiff / (60 * 60 * 1000);

                    reduceHrs = (int) hrsDiff;

                }

        }
        else {
            reduceHrs = 0;
        }

        return reduceHrs;
    }

    /**
     * returns the number of hrs to be reduced from total business hrs Depending on the endDate
     * 
     * @param endDate
     * @return reduceHrs
     */
    private static int endDateReduction(Calendar endDate, int isHoliday) {
        int reduceHrs = 0;

        if (endDate.get(Calendar.DAY_OF_WEEK) != 1 && endDate.get(Calendar.DAY_OF_WEEK) != 7
                && isHoliday != 1) {
            // Calculate reduction hrs based on current time
            final Date endTime = new Date(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DATE), endDate.get(Calendar.HOUR_OF_DAY),
                    endDate.get(Calendar.MINUTE), endDate.get(Calendar.SECOND));

            final Date loginTime = new Date(endTime.getYear(), endTime.getMonth(),
                    endTime.getDate(), 8, 0, 0);

            final Date logoutTime = new Date(endTime.getYear(), endTime.getMonth(),
                    endTime.getDate(), 17, 0, 0);

            if (endTime.after(logoutTime)) {
                reduceHrs = 0;
            }
            else
                if (endTime.before(loginTime)) {
                    reduceHrs = 9;
                }
                else {

                    long hrsDiff = endTime.getTime() - loginTime.getTime();
                    hrsDiff = hrsDiff / (60 * 60 * 1000);
                    reduceHrs = 9 - (int) hrsDiff;

                }

        }
        else {
            reduceHrs = 0;
        }

        return reduceHrs;
    }


    @Override
    protected void onPostExecute(ArrayList<VehicleInfo> checkinVehicles) {
        if (sBus != null) {
            sBus.post(new CheckinVehiclesRetrievedEvent(checkinVehicles));
        }
    }

    @Override
    protected void onCancelled(ArrayList<VehicleInfo> checkinVehicles) {}
}
