package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.DataHelper;


public class UserLogoutHttpPost extends OnYardHttpPost {

    /**
     * Data contract name for the user's AD login name.
     */
    public static final String USER_LOGIN_KEY = "a";
    /**
     * Data contract name for the Unix timestamp of logout.
     */
    public static final String LOGOUT_DATETIME_KEY = "b";
    /**
     * Data contract name for the user's auth token.
     */
    public static final String AUTH_TOKEN_KEY = "c";

    /**
     * The base URL of the OnYard submit web service.
     */
    private final String TARGET_BASE_URL = OnYard.SUBMIT_SERVICE_URL_BASE;
    /**
     * The action and operation to submit login info.
     */
    private final String TARGET_OPERATION = "/onyardsubmit/logout";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public UserLogoutHttpPost(Context context) {
        super(context);
    }

    public UserLogoutHttpPost(Context context, String authUser, String authToken) {
        super(context);

        mJsonDataContract.put(USER_LOGIN_KEY, authUser);
        mJsonDataContract.put(AUTH_TOKEN_KEY, authToken);
    }

    /**
     * Submit the HTTP request.
     */
    public void submit() throws IOException, InvalidParameterException, NetworkErrorException {
        super.submitRequest();
    }

    @Override
    protected void initJsonDataContract() {
        mJsonDataContract.put(LOGOUT_DATETIME_KEY, DataHelper.getUnixUtcTimeStamp());

        final String authUser = AuthenticationHelper.getLoggedInUser(mContext.getContentResolver());
        mJsonDataContract.put(USER_LOGIN_KEY, authUser);

        final String authToken = AuthenticationHelper.getAuthToken(mContext.getContentResolver());
        mJsonDataContract.put(AUTH_TOKEN_KEY, authToken == null ? "" : authToken);
    }

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
