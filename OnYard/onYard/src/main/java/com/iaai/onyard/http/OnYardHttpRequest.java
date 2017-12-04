package com.iaai.onyard.http;

import java.io.IOException;

import org.apache.http.HttpResponse;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Abstract class defining an HTTP POST to a web service.
 * 
 * @author wferguso
 */
public abstract class OnYardHttpRequest {

    /**
     * The base URL of the web service - server and port. Should exclude trailing slash.
     */
    protected String mTargetBaseUrl;
    /**
     * The action and operation of the web service to be invoked. Should include leading slash.
     */
    protected String mTargetOperation;
    /**
     * The content-type of the request. Default value is "application/json".
     */
    protected String mContentType = "application/json";
    /**
     * The accept type of the request. Default value is "application/json".
     */
    protected String mAcceptType = "application/json";
    /**
     * The connection timeout of the request. Default value is 10 seconds.
     */
    protected int mConnectionTimeout = 10 * 1000;
    /**
     * The socket timeout of the request. Default value is 60 seconds.
     */
    protected int mSocketTimeout = 60 * 1000;

    /**
     * The application context. Used to check network status.
     */
    protected Context mContext;

    /**
     * Default constructor. Calls initialization methods for target URL, query string params, and
     * JSON data contract.
     */
    public OnYardHttpRequest(Context context) {
        mContext = context;

        initTargetUrl();
    }

    /**
     * Submit the HTTP request.
     * 
     * @return The HttpResponse returned from the server.
     */
    protected abstract HttpResponse submitRequest() throws NetworkErrorException, IOException;

    /**
     * Submit the HTTP request using an existing HttpClient connection.
     * 
     * @return The HttpResponse returned from the server.
     */
    protected abstract HttpResponse submitRequest(OnYardHttpClient client)
            throws NetworkErrorException, IOException;

    /**
     * Initialize target base URL and operation. The values set in this method will be used when
     * submit() is called.
     */
    protected abstract void initTargetUrl();

    /**
     * Check whether device is connected to any network.
     * 
     * @return True if the device is connected, false otherwise.
     */
    protected boolean isConnectedToNetwork() {
        final ConnectivityManager connMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}
