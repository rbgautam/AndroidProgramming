package com.iaai.onyard.http;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Base64;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.AuthenticationHelper;


/**
 * Class defining a POST of an image and associated data to the OnYard Submit Service.
 * 
 * @author wferguso
 */
public class ImagerHttpPost extends OnYardHttpPost {

    /**
     * Data contract name for the stock's administrative branch number.
     */
    public static final String ADMIN_BRANCH_KEY = "AdminBranch";
    /**
     * Data contract name for the user's branch number.
     */
    public static final String USER_BRANCH_KEY = "UserBranch";
    /**
     * Data contract name for the stock number.
     */
    public static final String STOCK_NUMBER_KEY = "StockNumber";
    /**
     * Data contract name for the stock's VIN.
     */
    public static final String VIN_KEY = "VIN";
    /**
     * Data contract name for the imaging device serial number.
     */
    public static final String DEVICE_SERIAL_KEY = "DeviceSerial";
    /**
     * Data contract name for the image set - 0 for check-in, 1 for enhancement.
     */
    public static final String IMAGE_SET_KEY = "ImageSet";
    /**
     * Data contract name for the image order.
     */
    public static final String IMAGE_ORDER_KEY = "ImageOrder";
    /**
     * Data contract name for the Unix timestamp at which imaging began.
     */
    public static final String START_DATETIME_KEY = "StartDateTime";
    /**
     * Data contract name for the Unix timestamp at which imaging ended.
     */
    public static final String END_DATETIME_KEY = "EndDateTime";
    /**
     * Data contract name for the image bytes, encoded as a Base64 string.
     */
    public static final String FILE_CONTENTS_KEY = "FileContents";
    /**
     * Data contract name for the logged in user's authentication token.
     */
    public static final String AUTH_TOKEN_KEY = "AuthToken";
    /**
     * Data contract name for the logged in user name.
     */
    public static final String AUTH_USER_KEY = "AuthUser";
    /**
     * Data contract name for the salvage provider ID.
     */
    public static final String SALVAGE_PROVIDER_ID_KEY = "SalvageProviderId";

    /**
     * The base URL of the OnYard submit web service.
     */
    private final String TARGET_BASE_URL = OnYard.SUBMIT_SERVICE_URL_BASE;
    /**
     * The action and operation to submit images.
     */
    private final String TARGET_OPERATION = "/onyardsubmit/uploadimage";

    /**
     * Default constructor. Initializes web service target URL.
     */
    public ImagerHttpPost(Context context) {
        super(context);
    }

    /**
     * Constructor with initial values for JSON data contract map.
     * 
     * @param userName The initial user login name.
     * @param adminBranch The initial stock administrative branch number.
     * @param userBranch The initial user branch number.
     * @param stockNumber The initial stock number.
     * @param vin The initial stock VIN.
     * @param imageSet The initial image set of the image to be POSTed - 0 for check-in, 1 for
     *            enhancement.
     * @param imageOrder The initial image order.
     * @param startDateTime The initial Unix timestamp at which imaging began.
     * @param endDateTime The initial Unix timestamp at which imaging ended.
     * @param fileContents The initial image bytes.
     */
    public ImagerHttpPost(Context context, int adminBranch, int userBranch, int stockNumber,
            String vin, int imageSet, int imageOrder, long startDateTime, long endDateTime,
            byte[] fileContents, String authToken, String authUser) {
        super(context);

        mJsonDataContract.put(ADMIN_BRANCH_KEY, adminBranch);
        mJsonDataContract.put(USER_BRANCH_KEY, userBranch);
        mJsonDataContract.put(STOCK_NUMBER_KEY, stockNumber);
        mJsonDataContract.put(VIN_KEY, vin);
        mJsonDataContract.put(IMAGE_SET_KEY, imageSet);
        mJsonDataContract.put(IMAGE_ORDER_KEY, imageOrder);
        mJsonDataContract.put(START_DATETIME_KEY, startDateTime);
        mJsonDataContract.put(END_DATETIME_KEY, endDateTime);
        mJsonDataContract.put(FILE_CONTENTS_KEY, encodeByteArrayToString(fileContents));
        mJsonDataContract.put(AUTH_TOKEN_KEY, authToken);
        mJsonDataContract.put(AUTH_USER_KEY, authUser);
    }

    /**
     * Submit the HTTP request.
     * 
     * @throws IllegalArgumentException if image file contents are empty.
     */
    public void submit(OnYardHttpClient client) throws IOException, InvalidParameterException,
            IllegalArgumentException,
    NetworkErrorException {
        if (mJsonDataContract.get(FILE_CONTENTS_KEY) == null) {
            throw new IllegalArgumentException("Submitted image should not be blank");
        }

        super.submitRequest(client);
    }

    public void setAdminBranch(int adminBranch) {
        mJsonDataContract.put(ADMIN_BRANCH_KEY, adminBranch);
    }

    public void setUserBranch(int userBranch) {
        mJsonDataContract.put(USER_BRANCH_KEY, userBranch);
    }

    public void setStockNumber(int stockNumber) {
        mJsonDataContract.put(STOCK_NUMBER_KEY, stockNumber);
    }

    public int getStockNumber() {
        return (Integer) mJsonDataContract.get(STOCK_NUMBER_KEY);
    }

    public void setVin(String vin) {
        mJsonDataContract.put(VIN_KEY, vin);
    }

    public void setImageSet(short imageSet) {
        mJsonDataContract.put(IMAGE_SET_KEY, imageSet);
    }

    public void setImageOrder(short imageOrder) {
        mJsonDataContract.put(IMAGE_ORDER_KEY, imageOrder);
    }

    public short getImageOrder() {
        return (Short) mJsonDataContract.get(IMAGE_ORDER_KEY);
    }

    public void setStartDateTime(long startDateTime) {
        mJsonDataContract.put(START_DATETIME_KEY, startDateTime);
    }

    public void setEndDateTime(long endDateTime) {
        mJsonDataContract.put(END_DATETIME_KEY, endDateTime);
    }

    public void setFileContents(byte[] fileContents) {
        mJsonDataContract.put(FILE_CONTENTS_KEY, encodeByteArrayToString(fileContents));
    }

    private String encodeByteArrayToString(byte[] byteArray) {
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    protected void initJsonDataContract() {
        mJsonDataContract.put(ADMIN_BRANCH_KEY, 0);
        mJsonDataContract.put(USER_BRANCH_KEY, 0);
        mJsonDataContract.put(STOCK_NUMBER_KEY, 0);
        mJsonDataContract.put(VIN_KEY, "");
        mJsonDataContract.put(DEVICE_SERIAL_KEY, android.os.Build.SERIAL);
        mJsonDataContract.put(IMAGE_SET_KEY, 0);
        mJsonDataContract.put(IMAGE_ORDER_KEY, 0);
        mJsonDataContract.put(START_DATETIME_KEY, 0);
        mJsonDataContract.put(END_DATETIME_KEY, 0);
        mJsonDataContract.put(FILE_CONTENTS_KEY, "");

        final String authToken = AuthenticationHelper.getAuthToken(mContext.getContentResolver());
        mJsonDataContract.put(AUTH_TOKEN_KEY, authToken == null ? "" : authToken);

        final String authUser = AuthenticationHelper.addCorporatePrefix(AuthenticationHelper
                .getLoggedInUser(mContext.getContentResolver()));
        mJsonDataContract.put(AUTH_USER_KEY, authUser);
    }

    @Override
    protected void initTargetUrl() {
        mTargetBaseUrl = TARGET_BASE_URL;
        mTargetOperation = TARGET_OPERATION;
    }
}
