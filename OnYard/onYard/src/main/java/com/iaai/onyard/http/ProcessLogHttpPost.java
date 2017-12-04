package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Build;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.AuthenticationHelper;

public class ProcessLogHttpPost extends OnYardHttpPost {

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

    public static final String LOGGER_KEY = "g";
    public static final String EVENT_LEVEL_KEY = "h";
    public static final String EVENT_INFO_KEY = "i";

    /**
     * The base URL of the OnYard data web service.
     */
    private final String TARGET_BASE_URL = OnYard.DATA_SERVICE_URL_BASE;
    /**
     * The action and operation to submit log info.
     */
    private final String TARGET_OPERATION = "/onyarddata/log2";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public ProcessLogHttpPost(Context context) {
        super(context);
    }

    public void setLogger(String logger) {
        mJsonDataContract.put(LOGGER_KEY, logger);
    }

    public void setEventLevel(String eventLevel) {
        mJsonDataContract.put(EVENT_LEVEL_KEY, eventLevel);
    }

    public void setEventInfo(String eventInfo) {
        mJsonDataContract.put(EVENT_INFO_KEY, eventInfo);
    }

    /**
     * Submit the HTTP request.
     */
    public void submit() throws IOException, InvalidParameterException,
    NetworkErrorException {
        super.submitRequest();
    }

    @Override
    protected void initJsonDataContract() {
        mJsonDataContract.put(APP_NAME_KEY, OnYard.getAppName(mContext));
        mJsonDataContract.put(APP_VERSION_KEY, OnYard.getAppVersion(mContext));
        mJsonDataContract.put(DEVICE_MANUFACTURER_KEY, Build.MANUFACTURER);
        mJsonDataContract.put(DEVICE_MODEL_KEY, Build.MODEL);
        mJsonDataContract.put(DEVICE_SERIAL_KEY, Build.SERIAL);
        final String userLogin = AuthenticationHelper
                .getLoggedInUser(mContext.getContentResolver());
        mJsonDataContract.put(USER_LOGIN_KEY, userLogin == null ? OnYard.DEFAULT_USER_LOGIN
                : userLogin);
    }

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
