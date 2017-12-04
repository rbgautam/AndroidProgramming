package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.http.HttpResponse;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.contract.OnYardContract;


public class TransferStocksHttpGet extends OnYardHttpGet {

    public static final String BRANCH_KEY = "branch";
    public static final String LAST_UPDATE_TIME_KEY = "lastupdate";

    /**
     * The base URL of the OnYard data web service.
     */
    private final String TARGET_BASE_URL = OnYard.DATA_SERVICE_URL_BASE;
    private final String TARGET_OPERATION = "/onyarddata/transferstocks";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public TransferStocksHttpGet(Context context) {
        super(context);
    }

    public void setQueryStringParams(String branch, long lastUpdateTimeUnix) {
        mQueryStringParams.put(BRANCH_KEY, branch);
        mQueryStringParams.put(LAST_UPDATE_TIME_KEY, String.valueOf(lastUpdateTimeUnix));
    }

    public void get(OnYardHttpClient client) throws IOException, InvalidParameterException,
    NetworkErrorException {
        final HttpResponse response = super.submitRequest(client);

        final String body = DataHelper.convertStreamToString(response.getEntity().getContent());
        final String stockNumList = body.replace("[", "").replace("]", "");

        if (!stockNumList.trim().isEmpty()) {
            mContext.getContentResolver().delete(OnYardContract.Vehicles.CONTENT_URI,
                    OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER + " IN (" + stockNumList + ")",
                    null);
        }
    }

    @Override
    protected void initQueryStringParams() {}

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
