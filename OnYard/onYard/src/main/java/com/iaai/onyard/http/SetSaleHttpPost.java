package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Build;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.AuthenticationHelper;


/**
 * Class defining a POST of Set Sale data to the OnYard Submit Service.
 * 
 * @author wferguso
 */
public class SetSaleHttpPost extends OnYardHttpPost {

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
     * Data contract name for the old aisle.
     */
    public static final String OLD_AISLE_KEY = "h";
    /**
     * Data contract name for the old stall.
     */
    public static final String OLD_STALL_KEY = "i";
    /**
     * Data contract name for the auction date.
     */
    public static final String AUCTION_DATE_KEY = "j";
    /**
     * Data contract name for the auction number.
     */
    public static final String AUCTION_NUMBER_KEY = "k";
    /**
     * Data contract name for the sale aisle.
     */
    public static final String SALE_AISLE_KEY = "l";
    /**
     * Data contract name for the auction item sequence number.
     */
    public static final String AUCTION_ITEM_SEQUENCE_NUMBER_KEY = "m";
    /**
     * Data contract name for the current latitude coordinate.
     */
    public static final String LATITUDE_KEY = "s";
    /**
     * Data contract name for the current longitude coordinate.
     */
    public static final String LONGITUDE_KEY = "t";
    /**
     * Data contract name for the logged in user name.
     */
    public static final String AUTH_USER_KEY = "n";
    /**
     * Data contract name for the logged in user's authentication token.
     */
    public static final String AUTH_TOKEN_KEY = "o";
    /**
     * Data contract name for the stock number.
     */
    public static final String STOCK_NUMBER_KEY = "p";
    /**
     * Data contract name for the Unix timestamp at which Set Sale entry began.
     */
    public static final String START_DATETIME_KEY = "q";
    /**
     * Data contract name for the Unix timestamp at which Set Sale entry ended.
     */
    public static final String END_DATETIME_KEY = "r";

    /**
     * The base URL of the OnYard submit web service.
     */
    private final String TARGET_BASE_URL = OnYard.SUBMIT_SERVICE_URL_BASE;
    /**
     * The action and operation to submit Set Sale records.
     */
    private final String TARGET_OPERATION = "/onyardsubmit/uploadsetsale";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public SetSaleHttpPost(Context context) {
        super(context);
    }

    /**
     * Submit the HTTP request.
     * 
     */
    public void submit(OnYardHttpClient client) throws IOException, InvalidParameterException,
            NetworkErrorException {
        super.submitRequest(client);
    }

    public void setParamValue(String key, int value) {
        mJsonDataContract.put(key, value);
    }

    public void setParamValue(String key, long value) {
        mJsonDataContract.put(key, value);
    }

    public void setParamValue(String key, double value) {
        mJsonDataContract.put(key, value);
    }

    public void setParamValue(String key, String value) {
        mJsonDataContract.put(key, value);
    }

    public String getStockNumber() {
        return (String) mJsonDataContract.get(STOCK_NUMBER_KEY);
    }

    @Override
    protected void initJsonDataContract() {
        mJsonDataContract.put(APP_NAME_KEY, OnYard.getAppName(mContext));
        mJsonDataContract.put(APP_VERSION_KEY, OnYard.getAppVersion(mContext));
        mJsonDataContract.put(DEVICE_MANUFACTURER_KEY, Build.MANUFACTURER);
        mJsonDataContract.put(DEVICE_MODEL_KEY, Build.MODEL);
        mJsonDataContract.put(DEVICE_SERIAL_KEY, Build.SERIAL);
        mJsonDataContract.put(USER_LOGIN_KEY, OnYard.DEFAULT_USER_LOGIN);

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
