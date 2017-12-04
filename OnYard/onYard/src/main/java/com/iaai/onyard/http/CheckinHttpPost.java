package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.AuthenticationHelper;


/**
 * Class defining a POST of check-in data to the OnYard Submit Service.
 * 
 * @author wferguso
 */
public class CheckinHttpPost extends OnYardHttpPost {

    /**
     * Data contract name for the user's branch number.
     */
    public static final String USER_BRANCH_KEY = "g1";
    /**
     * Data contract name for the stock number.
     */
    public static final String STOCK_NUMBER_KEY = "c";
    /**
     * Data contract name for the Unix timestamp at which check-in began.
     */
    public static final String START_DATETIME_KEY = "c1";
    /**
     * Data contract name for the Unix timestamp at which check-in ended.
     */
    public static final String END_DATETIME_KEY = "b1";
    /**
     * Data contract name for the logged in user's authentication token.
     */
    public static final String AUTH_TOKEN_KEY = "l1";
    /**
     * Data contract name for the logged in user name.
     */
    public static final String AUTH_USER_KEY = "m1";
    public static final String INSPECTION_DONE_BY_KEY = "a1";

    /**
     * The base URL of the OnYard submit web service.
     */
    private final String TARGET_BASE_URL = OnYard.SUBMIT_SERVICE_URL_BASE;
    /**
     * The action and operation to submit images.
     */
    private final String TARGET_OPERATION = "/onyardsubmit/uploadcheckin";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public CheckinHttpPost(Context context) {
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
