package com.iaai.onyard.task;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.accounts.NetworkErrorException;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.event.AuthCompleteEvent;
import com.iaai.onyard.event.AuthCompleteEvent.AuthResult;
import com.iaai.onyard.http.UserLoginHttpPost;
import com.iaai.onyard.ntlm.NTLMSchemeFactory;
import com.iaai.onyard.ssl.TrustAllSSLSocketFactory;
import com.iaai.onyard.sync.HTTPHelper;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Bus;

/**
 * AsyncTask to authenticate a user against AD. This task will make a network call to perform this
 * authentication. If a listener is supplied, it will be notified when the authentication result is
 * obtained. If no listener is supplied, a broadcast will be sent ONLY if auth fails.
 * <P>
 * Param 0: user login - String <br>
 * Param 1: user password - String <br>
 * Param 2: app context - Context <br>
 * Param 3: Otto event bus - Bus <br>
 * </P>
 * 
 * @author wferguso
 */
public class AuthenticateUserTask extends AsyncTask<Object, Void, AuthResult> {

    private static Bus sBus;
    private static final String AUTH_DOMAIN = "iaai.com";
    private static final String TOKEN_HEADER_KEY = "Token";
    private static final String PASSWORD_EXPIRED_KEY = "PasswordExpired";
    private static final String EXPIRED_VALUE = "Expired";
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 60000;

    @Override
    protected AuthResult doInBackground(Object... params) {
        try {
            final String userLogin = (String) params[0];
            final Context context = (Context) params[2];
            sBus = (Bus) params[3];

            if (!HTTPHelper.isNetworkAvailable(context) || !HTTPHelper.isServerAvailable()) {
                return AuthResult.NO_NETWORK;
            }

            final String authToken = getLogInToken(context, userLogin, (String) params[1]);
            params[1] = null;

            if (authToken != null && authToken == EXPIRED_VALUE) {
                return AuthResult.EXPIRED;
            }

            final boolean isSuccessful = authToken != null;
            if (isSuccessful) {
                AuthenticationHelper.logCurrentUserOut(context);

                final ContentValues loginValues = new ContentValues();
                loginValues.put(OnYardContract.Config.COLUMN_NAME_KEY,
                        OnYardContract.Config.CONFIG_KEY_USER_LOGIN);
                loginValues.put(OnYardContract.Config.COLUMN_NAME_VALUE, userLogin);

                context.getContentResolver().insert(OnYardContract.Config.CONTENT_URI, loginValues);

                final ContentValues tokenValues = new ContentValues();
                tokenValues.put(OnYardContract.Config.COLUMN_NAME_KEY,
                        OnYardContract.Config.CONFIG_KEY_AUTH_TOKEN);
                tokenValues.put(OnYardContract.Config.COLUMN_NAME_VALUE, authToken);

                context.getContentResolver().insert(OnYardContract.Config.CONTENT_URI, tokenValues);

                try {
                    new UserLoginHttpPost(context).submit();
                }
                catch (final InvalidParameterException e) {
                    LogHelper.logError(context, e, this.getClass().getSimpleName());
                }
                catch (final IllegalArgumentException e) {
                    LogHelper.logError(context, e, this.getClass().getSimpleName());
                }
                catch (final NetworkErrorException e) {
                    LogHelper.logWarning(context, e, this.getClass().getSimpleName());
                }
                catch (final IOException e) {
                    LogHelper.logError(context, e, this.getClass().getSimpleName());
                }
            }

            return isSuccessful ? AuthResult.SUCCESS : AuthResult.FAILURE;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[2], e, this.getClass().getSimpleName());
            return AuthResult.FAILURE;
        }
    }

    @Override
    protected void onPostExecute(AuthResult result) {
        if (sBus != null) {
            sBus.post(new AuthCompleteEvent(result));
        }
    }

    private String getLogInToken(Context context, String userLogin, String password) {
        try {
            final HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);

            final HttpPost request = new HttpPost(OnYard.LOGIN_URL);
            request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

            final SchemeRegistry schreg = new SchemeRegistry();
            schreg.register(new Scheme("https", new TrustAllSSLSocketFactory(), 443));
            final ClientConnectionManager connManager = new ThreadSafeClientConnManager(params,
                    schreg);

            final DefaultHttpClient httpClient = new DefaultHttpClient(connManager, params);

            httpClient.getAuthSchemes().register(AuthPolicy.NTLM, new NTLMSchemeFactory());
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
                    new NTCredentials(userLogin, password, Build.SERIAL, AUTH_DOMAIN));
            password = null;

            HttpResponse response = null;
            try {
                response = httpClient.execute(request);
            }
            catch (final ClientProtocolException e) {
                LogHelper.logWarning(context, e, this.getClass().getSimpleName());
                return null;
            }
            catch (final IOException e) {
                LogHelper.logWarning(context, e, this.getClass().getSimpleName());
                return null;
            }

            LogHelper.logDebug("Auth GET: " + response.getStatusLine().toString());

            if (response.getFirstHeader(PASSWORD_EXPIRED_KEY) != null) {
                return EXPIRED_VALUE;
            }

            if (response.getFirstHeader(TOKEN_HEADER_KEY) == null) {
                throw new AuthenticationException("Login failed for user " + userLogin);
            }
            else {
                return response.getFirstHeader(TOKEN_HEADER_KEY).getValue();
            }
        }
        catch (final Exception e) {
            LogHelper.logWarning(context, e, this.getClass().getSimpleName());
            return null;
        }
    }
}
