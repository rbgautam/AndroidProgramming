package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Build;

import com.iaai.onyard.application.OnYard;


public class CheckinMetricsHttpPost extends OnYardHttpPost {

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
    // /**
    // * Data contract name for the stock number.
    // */
    // public static final String STOCK_NUMBER_KEY = "g";
    /**
     * Data contract name for the branch number set in account preferences.
     */
    public static final String USER_BRANCH_KEY = "g";
    // /**
    // * Data contract name for the Unix timestamp at which imaging began.
    // */
    // public static final String START_DATETIME_KEY = "k";
    // /**
    // * Data contract name for the Unix timestamp at which imaging ended.
    // */
    // public static final String END_DATETIME_KEY = "l";
    // /**
    // * Data contract name for the admin branch of the vehicle.
    // */
    // public static final String ADMIN_BRANCH_KEY = "n";

    /**
     * The base URL of the OnYard submit web service.
     */
    private final String TARGET_BASE_URL = OnYard.SUBMIT_SERVICE_URL_BASE;
    /**
     * The action and operation to submit device info.
     */
    private final String TARGET_OPERATION = "/onyardsubmit/uploadcheckinmetrics";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public CheckinMetricsHttpPost(Context context) {
        super(context);
    }

    public CheckinMetricsHttpPost(Context context, String userLogin, int userBranch) {
        super(context);

        mJsonDataContract.put(USER_LOGIN_KEY, userLogin);
        mJsonDataContract.put(USER_BRANCH_KEY, userBranch);
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

    public void setUserLogin(String userLogin) {
        mJsonDataContract.put(USER_LOGIN_KEY, userLogin);
    }

    public void setUserBranch(int userBranch) {
        mJsonDataContract.put(USER_BRANCH_KEY, userBranch);
    }

    @Override
    protected void initJsonDataContract() {
        mJsonDataContract.put(APP_NAME_KEY, OnYard.getAppName(mContext));
        mJsonDataContract.put(APP_VERSION_KEY, OnYard.getAppVersion(mContext));
        mJsonDataContract.put(DEVICE_MANUFACTURER_KEY, Build.MANUFACTURER);
        mJsonDataContract.put(DEVICE_MODEL_KEY, Build.MODEL);
        mJsonDataContract.put(DEVICE_SERIAL_KEY, Build.SERIAL);
        mJsonDataContract.put(USER_LOGIN_KEY, OnYard.DEFAULT_USER_LOGIN);
        mJsonDataContract.put(USER_BRANCH_KEY, 0);
    }

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
