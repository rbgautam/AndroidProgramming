package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Build;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.AuthenticationHelper;


public class LocationHttpPost extends OnYardHttpPost {

    /**
     * Data contract name for the application name.
     */
    public static final String APP_NAME_KEY = "a";
    /**
     * Data contract name for the application version.
     */
    public static final String APP_VERSION_KEY = "b";
    /**
     * Data contract name for the device manufacturer.
     */
    public static final String DEVICE_MANUFACTURER_KEY = "c";
    /**
     * Data contract name for the device model.
     */
    public static final String DEVICE_MODEL_KEY = "d";
    /**
     * Data contract name for the device serial number.
     */
    public static final String DEVICE_SERIAL_KEY = "e";
    /**
     * Data contract name for the user's login name.
     */
    public static final String USER_LOGIN_KEY = "f";
    /**
     * Data contract name for the branch number set in account preferences.
     */
    public static final String USER_BRANCH_KEY = "g";
    /**
     * Data contract name for the previous ASAP aisle.
     */
    public static final String OLD_AISLE_KEY = "h";
    /**
     * Data contract name for the previous ASAP stall.
     */
    public static final String OLD_STALL_KEY = "i";
    /**
     * Data contract name for the new (user-entered) aisle.
     */
    public static final String NEW_AISLE_KEY = "j";
    /**
     * Data contract name for the new (user-entered) stall.
     */
    public static final String NEW_STALL_KEY = "k";
    /**
     * Data contract name for the logged in user name.
     */
    public static final String AUTH_USER_KEY = "l";
    /**
     * Data contract name for the logged in user's authentication token.
     */
    public static final String AUTH_TOKEN_KEY = "m";
    /**
     * Data contract name for the stock number.
     */
    public static final String STOCK_NUMBER_KEY = "n";
    /**
     * Data contract name for the Unix timestamp at which location entry began.
     */
    public static final String START_DATETIME_KEY = "o";
    /**
     * Data contract name for the Unix timestamp at which location entry ended.
     */
    public static final String END_DATETIME_KEY = "p";
    /**
     * Data contract name for the admin branch of the vehicle.
     */
    public static final String ADMIN_BRANCH_KEY = "q";
    /**
     * Data contract name for the latitude of the location move.
     */
    public static final String LATITUDE_KEY = "r";
    /**
     * Data contract name for the longitude of the location move.
     */
    public static final String LONGITUDE_KEY = "s";
    /**
     * Data contract name for the current battery level percentage.
     */
    public static final String BATTERY_LEVEL_KEY = "t";

    /**
     * The base URL of the OnYard submit web service.
     */
    private final String TARGET_BASE_URL = OnYard.SUBMIT_SERVICE_URL_BASE;
    /**
     * The action and operation to submit device info.
     */
    private final String TARGET_OPERATION = "/onyardsubmit/uploadlocation";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public LocationHttpPost(Context context) {
        super(context);
    }

    public LocationHttpPost(Context context, int stockNumber, long startDateTime, long endDateTime,
            String userLogin, int userBranch, int adminBranch, String oldAisle, short oldStall,
            String newAisle, short newStall, float latitude, float longitude, short batteryLevel) {
        super(context);

        mJsonDataContract.put(USER_LOGIN_KEY, userLogin);
        mJsonDataContract.put(USER_BRANCH_KEY, userBranch);
        mJsonDataContract.put(OLD_AISLE_KEY, oldAisle);
        mJsonDataContract.put(OLD_STALL_KEY, oldStall);
        mJsonDataContract.put(NEW_AISLE_KEY, newAisle);
        mJsonDataContract.put(NEW_STALL_KEY, newStall);
        mJsonDataContract.put(STOCK_NUMBER_KEY, stockNumber);
        mJsonDataContract.put(START_DATETIME_KEY, startDateTime);
        mJsonDataContract.put(END_DATETIME_KEY, endDateTime);
        mJsonDataContract.put(ADMIN_BRANCH_KEY, adminBranch);
        mJsonDataContract.put(LATITUDE_KEY, latitude);
        mJsonDataContract.put(LONGITUDE_KEY, longitude);
        mJsonDataContract.put(BATTERY_LEVEL_KEY, batteryLevel);
    }

    /**
     * Submit the HTTP request.
     * 
     * @throws IllegalArgumentException if image file contents are empty.
     */
    public void submit(OnYardHttpClient client) throws IOException, InvalidParameterException,
            NetworkErrorException {
        super.submitRequest(client);
    }

    public void setStockNumber(String stockNumber) {
        mJsonDataContract.put(STOCK_NUMBER_KEY, stockNumber);
    }

    public String getStockNumber() {
        return (String) mJsonDataContract.get(STOCK_NUMBER_KEY);
    }

    public void setStartDateTime(long startDateTime) {
        mJsonDataContract.put(START_DATETIME_KEY, startDateTime);
    }

    public void setEndDateTime(long endDateTime) {
        mJsonDataContract.put(END_DATETIME_KEY, endDateTime);
    }

    public void setUserLogin(String userLogin) {
        mJsonDataContract.put(USER_LOGIN_KEY, userLogin);
    }

    public void setUserBranch(int userBranch) {
        mJsonDataContract.put(USER_BRANCH_KEY, userBranch);
    }

    public void setOldAisle(String oldAisle) {
        mJsonDataContract.put(OLD_AISLE_KEY, oldAisle);
    }

    public void setOldStall(short oldStall) {
        mJsonDataContract.put(OLD_STALL_KEY, oldStall);
    }

    public void setNewAisle(String newAisle) {
        mJsonDataContract.put(NEW_AISLE_KEY, newAisle);
    }

    public void setNewStall(short newStall) {
        mJsonDataContract.put(NEW_STALL_KEY, newStall);
    }

    public void setLatitude(double latitude) {
        mJsonDataContract.put(LATITUDE_KEY, latitude);
    }

    public void setLongitude(double longitude) {
        mJsonDataContract.put(LONGITUDE_KEY, longitude);
    }

    public void setAdminBranch(int adminBranch) {
        mJsonDataContract.put(ADMIN_BRANCH_KEY, adminBranch);
    }

    public void setBatteryLevel(short batteryLevel) {
        mJsonDataContract.put(BATTERY_LEVEL_KEY, batteryLevel);
    }

    @Override
    protected void initJsonDataContract() {
        mJsonDataContract.put(APP_NAME_KEY, OnYard.getAppName(mContext));
        mJsonDataContract.put(APP_VERSION_KEY, OnYard.getAppVersion(mContext));
        mJsonDataContract.put(DEVICE_MANUFACTURER_KEY, Build.MANUFACTURER);
        mJsonDataContract.put(DEVICE_MODEL_KEY, Build.MODEL);
        mJsonDataContract.put(DEVICE_SERIAL_KEY, Build.SERIAL);
        mJsonDataContract.put(USER_LOGIN_KEY, OnYard.DEFAULT_USER_LOGIN);
        mJsonDataContract.put(USER_BRANCH_KEY, null);
        mJsonDataContract.put(OLD_AISLE_KEY, null);
        mJsonDataContract.put(OLD_STALL_KEY, null);
        mJsonDataContract.put(NEW_AISLE_KEY, null);
        mJsonDataContract.put(NEW_STALL_KEY, null);
        mJsonDataContract.put(STOCK_NUMBER_KEY, null);
        mJsonDataContract.put(START_DATETIME_KEY, null);
        mJsonDataContract.put(END_DATETIME_KEY, null);
        mJsonDataContract.put(ADMIN_BRANCH_KEY, null);
        mJsonDataContract.put(LATITUDE_KEY, null);
        mJsonDataContract.put(LONGITUDE_KEY, null);
        mJsonDataContract.put(BATTERY_LEVEL_KEY, null);

        final String authToken = AuthenticationHelper.getAuthToken(mContext.getContentResolver());
        mJsonDataContract.put(AUTH_TOKEN_KEY, authToken == null ? "" : authToken);

        final String loggedInUser = AuthenticationHelper.getLoggedInUser(mContext
                .getContentResolver());
        mJsonDataContract.put(AUTH_USER_KEY, AuthenticationHelper.addCorporatePrefix(loggedInUser));
    }

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
