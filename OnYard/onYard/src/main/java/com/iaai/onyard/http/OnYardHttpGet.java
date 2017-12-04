package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.utility.LogHelper;

/**
 * Abstract class defining an HTTP POST to a web service.
 * 
 * @author wferguso
 */
public abstract class OnYardHttpGet extends OnYardHttpRequest {

    /**
     * The key-value map of query string parameters and their values.
     */
    protected HashMap<String, Object> mQueryStringParams;

    /**
     * Default constructor. Calls initialization methods for target URL, query string params, and
     * JSON data contract.
     */
    public OnYardHttpGet(Context context) {
        super(context);

        mQueryStringParams = new HashMap<String, Object>();
        initQueryStringParams();
    }

    /**
     * Submit the HTTP request.
     * 
     * @return The HttpResponse returned from the server.
     * @throws IOException if an error is encountered while submitting the request.
     * @throws NetworkErrorException if device is not connected to a network or OnYard server could
     *             not be contacted.
     */
    @Override
    protected HttpResponse submitRequest() throws IOException, InvalidParameterException,
    NetworkErrorException {
        try {
            return submitRequest(new OnYardHttpClient());
        }
        catch (final Exception e) {
            throw new IOException("Error creating HTTP client: " + e.getMessage());
        }
    }

    @Override
    protected HttpResponse submitRequest(OnYardHttpClient client) throws IOException,
    InvalidParameterException, NetworkErrorException {
        if (!isConnectedToNetwork()) {
            throw new NetworkErrorException("Device must be connected to a network");
        }

        HttpResponse response = null;
        try {
            final String requestString = mTargetBaseUrl + mTargetOperation + getQueryString();
            final HttpGet request = new HttpGet(requestString);
            request.setHeader("Content-type", mContentType);
            request.setHeader("Accept", mAcceptType);

            response = client.execute(request);

            if (response == null) {
                throw new Exception("HTTP response was null");
            }

            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    LogHelper.logVerbose(requestString + " GET: "
                            + response.getStatusLine().toString());
                    break;
                default:
                    throw new Exception(requestString + " GET: "
                            + response.getStatusLine().toString());
            }

            return response;
        }
        catch (final Exception e) {
            if (response != null && response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
            throw new IOException("Error retrieving data from WCF service: " + e.getMessage());
        }
    }

    private String getQueryString() {
        final StringBuilder queryString = new StringBuilder();
        if (!mQueryStringParams.isEmpty()) {
            String prefix = "?";
            for (final Entry<String, Object> param : mQueryStringParams.entrySet()) {
                final String key = param.getKey();
                final String value = String.valueOf(param.getValue());

                queryString.append(prefix);
                prefix = "&";

                queryString.append(key);
                queryString.append("=");
                queryString.append(value);
            }

            return queryString.toString();
        }
        else {
            return "";
        }
    }

    abstract void initQueryStringParams();
}
