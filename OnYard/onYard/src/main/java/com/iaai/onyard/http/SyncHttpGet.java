package com.iaai.onyard.http;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.iaai.onyard.application.OnYard;

public class SyncHttpGet extends OnYardHttpGet {

    public static final String LAST_UPDATE_TIME_KEY = "lastupdate";
    public static final String BRANCH_KEY = "branch";
    private static final String PASSWORD_KEY = "pass";
    private static final String ACTIVE_ONLY_KEY = "activeonly";

    /**
     * The base URL of the OnYard data web service.
     */
    private final String TARGET_BASE_URL = OnYard.DATA_SERVICE_URL_BASE;
    /**
     * The action and operation to get branch info.
     */
    private final String mTargetPathName;
    private final String mJsonFileName;

    /**
     * Default constructor. Initializes web service target URL.
     */
    public SyncHttpGet(Context context, String targetPathname, String jsonFileName) {
        super(context);

        mTargetPathName = targetPathname;
        mJsonFileName = jsonFileName;
        initTargetUrl();
    }

    public void setLastUpdateTimeUnix(String lastUpdateTimeUnix) {
        mQueryStringParams.put(LAST_UPDATE_TIME_KEY, lastUpdateTimeUnix);
    }

    public void setBranch(String branchNumber) {
        mQueryStringParams.put(BRANCH_KEY, branchNumber);
    }

    public void setPassword(String password) {
        mQueryStringParams.put(PASSWORD_KEY, password);
    }

    public void setActiveOnly(String activeOnly) {
        mQueryStringParams.put(ACTIVE_ONLY_KEY, activeOnly);
    }

    public void downloadToJsonFile(OnYardHttpClient client) throws IOException,
    InvalidParameterException, NetworkErrorException {
        FileOutputStream osw = null;
        try {
            osw = mContext.openFileOutput(mJsonFileName, Context.MODE_PRIVATE);

            super.submitRequest(client).getEntity().writeTo(osw);

            osw.flush();
        }
        finally {
            if (osw != null) {
                osw.close();
            }
        }
    }

    @Override
    protected void initQueryStringParams() {}

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = mTargetPathName;
    }
}
