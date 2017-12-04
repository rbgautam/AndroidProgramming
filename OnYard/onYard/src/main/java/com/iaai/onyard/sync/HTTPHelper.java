package com.iaai.onyard.sync;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.LogHelper;

/**
 * Helper class containing methods that deal with HTTP WCF service calls or network status.
 */
public class HTTPHelper {

    /**
     * The timeout of the OnYard server ping.
     */
    private static final int PING_TIMEOUT = 10 * 1000;
    /**
     * The port of the OnYard server with which to communicate.
     */
    private static final int ONYARD_SERVER_PORT = 443;

    /**
     * Check whether wifi is currently available.
     * 
     * @param context The current context.
     * @return True if wifi is available, false otherwise.
     */
    public static boolean isNetworkAvailable(Context context)
    {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null;
    }

    public static boolean isServerAvailable() throws IOException {
        Socket socket = null;
        try {
            socket = new Socket();
            String serverName = OnYard.SERVICE_URL_BASE.replace("http://", "")
                    .replace("https://", "").replace("/", "");
            int portNumber = ONYARD_SERVER_PORT;
            if (serverName.indexOf(':') != -1) {
                portNumber = Integer.parseInt(serverName.substring(serverName.indexOf(':') + 1,
                        serverName.length()));
                serverName = serverName.substring(0, serverName.indexOf(':'));
            }

            socket.connect(new InetSocketAddress(serverName, portNumber), PING_TIMEOUT);
            return true;
        }
        catch (final Exception e) {
            LogHelper.logDebug(e.toString());
            return false;
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
