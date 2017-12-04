package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.http.HttpResponse;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.DataHelper;


public class BranchHttpGet extends OnYardHttpGet {

    /**
     * The base URL of the OnYard data web service.
     */
    private final String TARGET_BASE_URL = OnYard.DATA_SERVICE_URL_BASE;
    /**
     * The action and operation to get branch info.
     */
    private final String TARGET_OPERATION = "/onyarddata/branch";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public BranchHttpGet(Context context) {
        super(context);
    }

    /**
     * Get the branch number assigned to this device's IP address.
     * 
     * @return The branch number assigned to this device's IP address, or an empty string if no
     *         matching IP address was found.
     * @throws IOException if an error is encountered while submitting the request.
     * @throws NetworkErrorException if device is not connected to a network or OnYard server could
     *             not be contacted.
     */
    public String get() throws IOException, InvalidParameterException,
    NetworkErrorException {
        final HttpResponse response = super.submitRequest();

        return DataHelper.convertStreamToString(response.getEntity().getContent())
                .replace("\"", "");
    }

    @Override
    protected void initQueryStringParams() {}

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
