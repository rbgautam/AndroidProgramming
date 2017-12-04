package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Build;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.classes.OnYardPreferences;


public class SyncLogHttpPost extends OnYardHttpPost {

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
    public static final String BRANCH_NUMBER_KEY = "g";

    /**
     * The base URL of the OnYard data web service.
     */
    private final String TARGET_BASE_URL = OnYard.DATA_SERVICE_URL_BASE;
    /**
     * The action and operation to submit sync info.
     */
    private final String TARGET_OPERATION = "/onyarddata/sync";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public SyncLogHttpPost(Context context) {
        super(context);
    }

    public SyncLogHttpPost(Context context, String userLogin) {
        super(context);

        mJsonDataContract.put(USER_LOGIN_KEY, userLogin);
    }

    public void setUserLogin(String userLogin) {
        mJsonDataContract.put(USER_LOGIN_KEY, userLogin);
    }

    /**
     * Submit the HTTP request.
     */
    public void submit(OnYardHttpClient client) throws IOException, InvalidParameterException,
            NetworkErrorException {
        super.submitRequest(client);
    }

    @Override
    protected void initJsonDataContract() {
        mJsonDataContract.put(APP_NAME_KEY, OnYard.getAppName(mContext));
        mJsonDataContract.put(APP_VERSION_KEY, OnYard.getAppVersion(mContext));
        mJsonDataContract.put(DEVICE_MANUFACTURER_KEY, Build.MANUFACTURER);
        mJsonDataContract.put(DEVICE_MODEL_KEY, Build.MODEL);
        mJsonDataContract.put(DEVICE_SERIAL_KEY, Build.SERIAL);
        mJsonDataContract.put(USER_LOGIN_KEY, OnYard.DEFAULT_USER_LOGIN);
        mJsonDataContract.put(BRANCH_NUMBER_KEY,
                new OnYardPreferences(mContext).getEffectiveBranchNumber());
    }

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
