package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.http.HttpResponse;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.DataHelper;


public class ShouldSyncRefHttpGet extends OnYardHttpGet {

    public static final String LAST_UPDATE_TIME_KEY = "lastupdate";

    /**
     * The base URL of the OnYard data web service.
     */
    private final String TARGET_BASE_URL = OnYard.DATA_SERVICE_URL_BASE;
    /**
     * The action and operation to get branch info.
     */
    private final String TARGET_OPERATION = "/onyarddata/shouldsyncref";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public ShouldSyncRefHttpGet(Context context) {
        super(context);
    }

    public void setLastUpdateTimeUnix(long lastUpdateTimeUnix) {
        mQueryStringParams.put(LAST_UPDATE_TIME_KEY, lastUpdateTimeUnix);
    }

    /**
     * Determine whether a sync is required for reference tables.
     * 
     * @return True if sync is necessary for at least one reference table, false otherwise.
     * @throws IOException if an error is encountered while submitting the request.
     * @throws NetworkErrorException if device is not connected to a network or OnYard server could
     *             not be contacted.
     */
    public boolean get(OnYardHttpClient client) throws IOException, InvalidParameterException,
    NetworkErrorException {
        final HttpResponse response = super.submitRequest(client);

        return Boolean.parseBoolean(DataHelper.convertStreamToString(response.getEntity()
                .getContent()));
    }

    @Override
    protected void initQueryStringParams() {}

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
