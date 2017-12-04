package com.iaai.onyard.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyardproviderapi.classes.SelectionClause;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * Helper class containing methods that deal with data management.
 */
public class DataHelper {

    /**
     * Get the File in the given directory that was most recently modified.
     * 
     * @param filePath The path of the directory to search.
     * @return The File object that was most recently modified at the given path.
     */
    public static File getNewestFileInDirectory(String filePath)
    {
        File newestFile = null;

        final File dir = new File(filePath);
        for (final File file : dir.listFiles())
        {
            if (newestFile == null || file.lastModified() > newestFile.lastModified())
            {
                newestFile = file;
            }
        }

        return newestFile;
    }

    public static long getUnixUtcTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static long convertUnixUtcToLocal(long unixTime) {
        return unixTime + TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        Scanner s = null;
        try {
            s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        finally {
            if (s != null) {
                s.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isWordOnlyNumeric(String word) {
        return getNumDigitChars(word) == word.length();
    }

    public static boolean isWordPartlyNumeric(String word) {
        return getNumDigitChars(word) > 0;
    }

    private static int getNumDigitChars(String string) {
        int numNumbers = 0;

        for (int charIndex = 0, strLen = string.length(); charIndex < strLen; charIndex++) {
            if (Character.isDigit(string.charAt(charIndex))) {
                numNumbers++;
            }
        }

        return numNumbers;
    }

    public static boolean isAsciiLetterOrNumber(char c) {
        return isAsciiLetter(c) || isNumber(c);
    }

    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAsciiLetter(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }

    /**
     * Check if key exists in Config table.
     * 
     * @param context The current context.
     * @param configKey The config key for which to search.
     * @return True if key exists, false otherwise.
     */
    public static boolean isConfigKeyInDB(Context context, String configKey) {
        Cursor cursor = null;
        try {
            if (configKey == null) {
                return false;
            }

            cursor = context.getContentResolver().query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_KEY_URI_BASE, configKey),
                    new String[] { OnYardContract.Config.COLUMN_NAME_KEY }, null, null, null);

            return cursor.moveToFirst();
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*
     * Get a calendar object for specified time zone
     * @param timeZoneAbbr - time zone abbreviation such as ET, CT, PT
     * @return Calendar object
     */

    public static Calendar getTzCalendar(String timeZoneAbbr) {
        final TimeZone tz = TimeZone
                .getTimeZone(GetTimeZoneCity(TimeZones.valueOf(timeZoneAbbr)));
        return Calendar.getInstance(tz);
    }

    public static Calendar StringDateToDate(String strDate) {
        final Calendar dateToReturn = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            final java.util.Date parsedDate = dateFormat.parse(strDate);
            dateToReturn.setTime(parsedDate);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dateToReturn;
    }

    public static String getHolidayDateInStringFormat(long timeInMillis) {
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        final String currhr = f.format(new java.util.Date(timeInMillis));
        return currhr;
    }

    public enum TimeZones {
        CT, ET, AKT, HT, PT, MT, MST
    };

    public static String GetTimeZoneCity(TimeZones tZone) {

        switch (tZone) {
            case CT:
                return "America/Chicago";
            case ET:
                return "America/Indianapolis";
            case AKT:
                return "America/Anchorage";
            case HT:
                return "Pacific/Honolulu";
            case PT:
                return "America/Los_Angeles";
            case MT:
                return "America/Denver";
            case MST:
                return "America/Phoenix";
            default:
                return "America/Chicago";
        }
    }

    public static String getBranchTimeZone(Context context) {

        final OnYardPreferences pref = new OnYardPreferences(context);

        if (pref.isDefaultBranchNumber()) {
            return null;
        }
        Cursor queryResult = null;
        try {
            queryResult = context.getContentResolver().query(OnYardContract.Branch.CONTENT_URI,
                    null, OnYardContract.Branch.COLUMN_NAME_BRANCH_NUMBER + "=?",
                    new String[] { pref.getEffectiveBranchNumber() }, null);

            if (queryResult != null && queryResult.moveToFirst()) {
                return queryResult.getString(queryResult
                        .getColumnIndex(OnYardContract.Branch.COLUMN_NAME_TIME_ZONE));
            }
            else {
                return null;
            }
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }

    public static SelectionClause getStatusFilterClause(ArrayList<String> statusCodes) {
        final StringBuilder builder = new StringBuilder();
        final String[] selectionArgs = new String[statusCodes.size()];
        for (int index = 0; index < statusCodes.size(); index++) {
            if (index == 0) {
                builder.append(OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=?");
            }
            else {
                builder.append(" OR " + OnYardContract.Vehicles.COLUMN_NAME_STATUS + "=?");
            }

            selectionArgs[index] = statusCodes.get(index);
        }
        return new SelectionClause(builder.toString(), selectionArgs);
    }

    public static SelectionClause getAuctionDateFilterClause(Long auctionDate) {
        return new SelectionClause(OnYardContract.Vehicles.COLUMN_NAME_AUCTION_DATE_UNIX + "=?",
                new String[] { String.valueOf(auctionDate) });
    }

    public static SelectionClause getAdminBranchFilterClause(String adminBranch) {
        return new SelectionClause(OnYardContract.Vehicles.COLUMN_NAME_ADMIN_BRANCH + "=?",
                new String[] { adminBranch });
    }

}
