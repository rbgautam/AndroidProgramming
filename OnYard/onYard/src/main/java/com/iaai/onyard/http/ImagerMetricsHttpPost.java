package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Build;

import com.iaai.onyard.application.OnYard;


public class ImagerMetricsHttpPost extends OnYardHttpPost {

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
     * Data contract name for the stock number.
     */
    public static final String STOCK_NUMBER_KEY = "g";
    /**
     * Data contract name for the branch number set in account preferences.
     */
    public static final String USER_BRANCH_KEY = "h";
    /**
     * Data contract name for the image set of the images taken.
     */
    public static final String IMAGE_SET_KEY = "i";
    /**
     * Data contract name for the number of images taken in the imaging session.
     */
    public static final String NUM_IMAGES_KEY = "j";
    /**
     * Data contract name for the Unix timestamp at which imaging began.
     */
    public static final String START_DATETIME_KEY = "k";
    /**
     * Data contract name for the Unix timestamp at which imaging ended.
     */
    public static final String END_DATETIME_KEY = "l";
    /**
     * Data contract name for the selected image type.
     */
    public static final String IMAGE_TYPE_KEY = "m";
    /**
     * Data contract name for the admin branch of the vehicle.
     */
    public static final String ADMIN_BRANCH_KEY = "n";

    /**
     * Data contract name for the salvage provider Id
     */
    public static final String SALVAGE_PROVIDER_ID_KEY = "o";

    /**
     * The base URL of the OnYard submit web service.
     */
    private final String TARGET_BASE_URL = OnYard.SUBMIT_SERVICE_URL_BASE;
    /**
     * The action and operation to submit device info.
     */
    private final String TARGET_OPERATION = "/onyardsubmit/uploadimagermetrics";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public ImagerMetricsHttpPost(Context context) {
        super(context);
    }

    public ImagerMetricsHttpPost(Context context, int stockNumber, short imageSet,
            long startDateTime, long endDateTime, short numImagesTaken, String userLogin,
            int userBranch, int imageTypeId, int adminBranch) {
        super(context);

        mJsonDataContract.put(STOCK_NUMBER_KEY, stockNumber);
        mJsonDataContract.put(IMAGE_SET_KEY, imageSet);
        mJsonDataContract.put(NUM_IMAGES_KEY, numImagesTaken);
        mJsonDataContract.put(START_DATETIME_KEY, startDateTime);
        mJsonDataContract.put(END_DATETIME_KEY, endDateTime);
        mJsonDataContract.put(USER_LOGIN_KEY, userLogin);
        mJsonDataContract.put(USER_BRANCH_KEY, userBranch);
        mJsonDataContract.put(IMAGE_TYPE_KEY, imageTypeId);
        mJsonDataContract.put(ADMIN_BRANCH_KEY, adminBranch);
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

    public void setStockNumber(int stockNumber) {
        mJsonDataContract.put(STOCK_NUMBER_KEY, stockNumber);
    }

    public int getStockNumber() {
        return (Integer) mJsonDataContract.get(STOCK_NUMBER_KEY);
    }

    public void setImageSet(short imageSet) {
        mJsonDataContract.put(IMAGE_SET_KEY, imageSet);
    }

    public void setStartDateTime(long startDateTime) {
        mJsonDataContract.put(START_DATETIME_KEY, startDateTime);
    }

    public void setEndDateTime(long endDateTime) {
        mJsonDataContract.put(END_DATETIME_KEY, endDateTime);
    }

    public void setNumImagesTaken(short numImagesTaken) {
        mJsonDataContract.put(NUM_IMAGES_KEY, numImagesTaken);
    }

    public void setUserLogin(String userLogin) {
        mJsonDataContract.put(USER_LOGIN_KEY, userLogin);
    }

    public void setUserBranch(int userBranch) {
        mJsonDataContract.put(USER_BRANCH_KEY, userBranch);
    }

    public void setImageType(int imageTypeId) {
        mJsonDataContract.put(IMAGE_TYPE_KEY, imageTypeId);
    }

    public void setAdminBranch(int adminBranch) {
        mJsonDataContract.put(ADMIN_BRANCH_KEY, adminBranch);
    }

    @Override
    protected void initJsonDataContract() {
        mJsonDataContract.put(APP_NAME_KEY, OnYard.getAppName(mContext));
        mJsonDataContract.put(APP_VERSION_KEY, OnYard.getAppVersion(mContext));
        mJsonDataContract.put(DEVICE_MANUFACTURER_KEY, Build.MANUFACTURER);
        mJsonDataContract.put(DEVICE_MODEL_KEY, Build.MODEL);
        mJsonDataContract.put(DEVICE_SERIAL_KEY, Build.SERIAL);
        mJsonDataContract.put(USER_LOGIN_KEY, OnYard.DEFAULT_USER_LOGIN);
        mJsonDataContract.put(STOCK_NUMBER_KEY, 0);
        mJsonDataContract.put(USER_BRANCH_KEY, 0);
        mJsonDataContract.put(IMAGE_SET_KEY, 0);
        mJsonDataContract.put(NUM_IMAGES_KEY, 0);
        mJsonDataContract.put(START_DATETIME_KEY, 0);
        mJsonDataContract.put(END_DATETIME_KEY, 0);
        mJsonDataContract.put(IMAGE_TYPE_KEY, 0);
        mJsonDataContract.put(ADMIN_BRANCH_KEY, 0);
    }

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
