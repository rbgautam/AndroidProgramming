package com.iaai.onyard.utility;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.iaai.onyard.http.OnYardHttpClient;
import com.iaai.onyard.http.UserLogoutHttpPost;
import com.iaai.onyard.http.UserValidationHttpPost;
import com.iaai.onyard.task.AuthenticateUserTask;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

public class AuthenticationHelper {

    private static final String CLASS_NAME = "AuthenticationHelper";
    private static final String CORPORATE_PREFIX = "CORPORATE\\";

    /**
     * Validate user credentials against AD through a web service.<br>
     * NOTE: ONLY CALL FROM NON-UI THREAD.
     * 
     * @param context The application context.
     * @return True if the current user and token have been deemed valid, false otherwise.
     */
    public static boolean isUserAuthValid(Context context, OnYardHttpClient client) {
        boolean isSuccessful = true;

        try {
            new UserValidationHttpPost(context).submit(client);
        }
        catch (final Exception e) {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    /**
     * Hit the authentication web service to log the user in to OnYard and get a token in response.
     * Actual network call is done by an AsyncTask.
     * 
     * @param appContext The application context.
     * @param userLogin The user's AD login name.
     * @param password The user's password.
     * @param bus
     */
    public static void logUserIn(Context appContext, String userLogin, String password, Bus bus) {
        userLogin = stripCorporatePrefix(userLogin);

        new AuthenticateUserTask().execute(userLogin, password, appContext, bus);
        password = null;
    }

    /**
     * Delete the user/token records from the database and notify the server of logout. This method
     * does not notify the user of logout. AuthenticationHelper.sendLogoutBroadcast should be used
     * to notify the user.
     * 
     * @param context The application context.
     */
    public static void logCurrentUserOut(Context context) {
        final String userLogin = getLoggedInUser(context.getContentResolver());
        if (userLogin == null) {
            return;
        }
        final String authToken = getAuthToken(context.getContentResolver());

        context.getContentResolver().delete(
                Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                        OnYardContract.Config.CONFIG_KEY_USER_LOGIN), null, null);
        context.getContentResolver().delete(
                Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                        OnYardContract.Config.CONFIG_KEY_AUTH_TOKEN), null, null);

        try {
            new UserLogoutHttpPost(context, userLogin, authToken).submit();
        }
        catch (final InvalidParameterException e) {
            LogHelper.logWarning(context, e, CLASS_NAME);
        }
        catch (final IllegalArgumentException e) {
            LogHelper.logWarning(context, e, CLASS_NAME);
        }
        catch (final NetworkErrorException e) {
            LogHelper.logWarning(context, e, CLASS_NAME);
        }
        catch (final IOException e) {
            LogHelper.logWarning(context, e, CLASS_NAME);
        }
    }

    /**
     * Get the login name of the user that is currently logged in to OnYard.
     * 
     * @param contentResolver A contentResolver instance for this application.
     * @return The login name of the current user, or null if no user is logged in. CORPORATE\ is
     *         not included.
     */
    public static String getLoggedInUser(ContentResolver contentResolver) {
        Cursor queryResult = null;
        try {
            queryResult = contentResolver.query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                            OnYardContract.Config.CONFIG_KEY_USER_LOGIN),
                            new String[] { OnYardContract.Config.COLUMN_NAME_VALUE }, null, null, null);

            if (queryResult == null || !queryResult.moveToFirst()) {
                return null;
            }
            else {
                return queryResult.getString(0);
            }
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }

    /**
     * Get the authentication token of the user that is currently logged in to OnYard.
     * 
     * @param contentResolver A contentResolver instance for this application.
     * @return The auth token of the current user, or null if no user is logged in.
     */
    public static String getAuthToken(ContentResolver contentResolver) {
        Cursor queryResult = null;
        try {
            queryResult = contentResolver.query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                            OnYardContract.Config.CONFIG_KEY_AUTH_TOKEN),
                            new String[] { OnYardContract.Config.COLUMN_NAME_VALUE }, null, null, null);

            if (queryResult == null || !queryResult.moveToFirst()) {
                return null;
            }
            else {
                return queryResult.getString(0);
            }
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }

    public static boolean isAnyUserLoggedIn(ContentResolver contentResolver) {
        return getLoggedInUser(contentResolver) != null;
    }

    private static String stripCorporatePrefix(String userLogin) {
        return userLogin.replace(CORPORATE_PREFIX, "");
    }

    public static String addCorporatePrefix(String userLogin) {
        if (!userLogin.contains(CORPORATE_PREFIX)) {
            return CORPORATE_PREFIX + userLogin;
        }
        else {
            return userLogin;
        }
    }
}
