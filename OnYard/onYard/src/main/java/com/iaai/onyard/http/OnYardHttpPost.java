package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.classes.UnauthorizedReason;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.BroadcastHelper;
import com.iaai.onyard.utility.LogHelper;

/**
 * Abstract class defining an HTTP POST to a web service.
 * 
 * @author wferguso
 */
public abstract class OnYardHttpPost extends OnYardHttpRequest {

    /**
     * The key-value map of the web service data contract. This map is converted to a JSON string
     * and added to the body of the POST request.
     */
    protected HashMap<String, Object> mJsonDataContract;

    /**
     * Default constructor. Calls initialization methods for target URL, query string params, and
     * JSON data contract.
     */
    public OnYardHttpPost(Context context) {
        super(context);

        mJsonDataContract = new HashMap<String, Object>();
        initJsonDataContract();
    }

    /**
     * Submit the HTTP request.
     * 
     * @return The HttpResponse returned from the server.
     * @throws IOException if an error is encountered while submitting the request.
     * @throws InvalidParameterException if body is empty.
     * @throws NetworkErrorException if device is not connected to a network or OnYard server could
     *             not be contacted.
     */
    @Override
    protected HttpResponse submitRequest() throws NetworkErrorException, IOException {
        try {
            return submitRequest(new OnYardHttpClient());
        }
        catch (final Exception e) {
            throw new IOException("Error creating HTTP client: " + e.getMessage());
        }
    }

    /**
     * Submit the HTTP request using an existing HttpClient connection.
     * 
     * @return Null, because this is a POST method and the response content will already be
     *         consumed.
     * @throws IOException if an error is encountered while submitting the request.
     * @throws InvalidParameterException if body is empty.
     * @throws NetworkErrorException if device is not connected to a network or OnYard server could
     *             not be contacted.
     */
    @Override
    protected HttpResponse submitRequest(OnYardHttpClient client) throws NetworkErrorException,
    IOException {
        if (!isConnectedToNetwork()) {
            throw new NetworkErrorException("Device must be connected to a network");
        }

        if (mJsonDataContract == null) {
            throw new InvalidParameterException("Cannot POST with empty body");
        }

        HttpResponse response = null;
        try {
            final StringEntity entity = new StringEntity(new JSONObject(mJsonDataContract).toString());
            // LogHelper.logDebug(new JSONObject(mJsonDataContract).toString());

            final HttpPost request = new HttpPost(mTargetBaseUrl + mTargetOperation);
            request.setHeader("Content-type", mContentType);
            request.setHeader("Accept", mAcceptType);
            request.setEntity(entity);

            response = client.execute(request);
        }
        catch (final Exception e) {
            if (response != null && response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
            throw new IOException("Error getting HTTP response: " + e.getMessage());
        }

        try {
            if (response == null) {
                throw new Exception("HTTP response was null");
            }

            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_UNAUTHORIZED:
                    final UnauthorizedReason reason = new UnauthorizedReason(response,
                            AuthenticationHelper.getLoggedInUser(mContext.getContentResolver()));
                    AuthenticationHelper.logCurrentUserOut(mContext);
                    BroadcastHelper.sendLogoutBroadcast(mContext, reason);

                    LogHelper.logVerbose(mTargetBaseUrl + mTargetOperation + " POST: "
                            + response.getStatusLine().toString());
                    throw new Exception("401 Unauthorized - Token was invalid");
                case HttpStatus.SC_OK:
                    LogHelper.logVerbose(mTargetBaseUrl + mTargetOperation + " POST: "
                            + response.getStatusLine().toString());
                    break;
                default:
                    throw new Exception(mTargetBaseUrl + mTargetOperation + " POST: "
                            + response.getStatusLine().toString());
            }

            return null;
        }
        catch (final Exception e) {
            String jsonString = "-";
            try {
                jsonString = new JSONObject(mJsonDataContract).toString();
                jsonString = jsonString.substring(0, Math.min(499, jsonString.length()));
            }
            catch (final Exception e2) {}

            throw new IOException("Error getting HTTP status code: " + e.getMessage() + " | JSON: "
                    + jsonString);
        }
        finally {
            if (response != null && response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        }
    }

    /**
     * Initialize JSON data contract Map with key strings and initial values.
     */
    protected abstract void initJsonDataContract();
}
