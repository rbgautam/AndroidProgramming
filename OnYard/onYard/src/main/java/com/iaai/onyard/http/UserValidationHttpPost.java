package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.AuthenticationHelper;


public class UserValidationHttpPost extends OnYardHttpPost {

    /**
     * Data contract name for the user's AD login name.
     */
    public static final String AUTH_USER_KEY = "a";
    /**
     * Data contract name for the user's auth token.
     */
    public static final String AUTH_TOKEN_KEY = "b";

    /**
     * The base URL of the OnYard submit web service.
     */
    private final String TARGET_BASE_URL = OnYard.SUBMIT_SERVICE_URL_BASE;
    /**
     * The action and operation to submit login info.
     */
    private final String TARGET_OPERATION = "/onyardsubmit/validate";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public UserValidationHttpPost(Context context) {
        super(context);
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
        final String authUser = AuthenticationHelper.addCorporatePrefix(AuthenticationHelper
                .getLoggedInUser(mContext.getContentResolver()));
        mJsonDataContract.put(AUTH_USER_KEY, authUser);

        final String authToken = AuthenticationHelper.getAuthToken(mContext.getContentResolver());
        mJsonDataContract.put(AUTH_TOKEN_KEY, authToken == null ? "" : authToken);
    }

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
