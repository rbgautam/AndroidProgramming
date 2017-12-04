package com.iaai.onyard.utility;

import java.io.IOException;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.LogMode;
import com.iaai.onyard.http.ProcessLogHttpPost;

/**
 * Helper class containing methods that deal with event logging.
 */
public class LogHelper {
    /**
     * The app name to be used when events are logged.
     */
    private static final String LOG_APP_NAME = "com.iaai.onyard";

    /**
     * Enum describing the log event levels of this app.
     */
    public static enum EventLevel
    {
        /**
         * Events that provide information about what is happening in the code.
         */
        VERBOSE("Verbose"),

        /**
         * Events that provide information which aids in development or in identification
         * of a bug.
         */
        DEBUG("Debug"),

        /**
         * Events that provide information that is useful for monitoring.
         */
        INFO("Info"),

        /**
         * Exceptions that do not negatively affect the user's experience of the app. For example,
         * an exception in the code to show a progress dialog would warrant a warning, since the
         * only consequence is the dialog not being displayed.
         */
        WARNING("Warning"),

        /**
         * Exceptions that directly and negatively affect the user's experience of the app. Errors
         * should be looked into immediately.
         */
        ERROR("Error");

        private final String name;

        EventLevel(String name) {
            this.name = name;
        }
        @Override
        public String toString() { return name; }
    }

    /**
     * Handle logging of a verbose event.
     * 
     * @param message The message to log.
     */
    public static void logVerbose(String message)
    {
        try
        {
            if(OnYard.LOG_MODE == LogMode.VERBOSE) {
                Log.v(LOG_APP_NAME, message == null ? "No Message" : message);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handle logging of a debug event.
     * 
     * @param message The message to log.
     */
    public static void logDebug(String message)
    {
        try
        {
            if(OnYard.LOG_MODE == LogMode.DEBUG ||
                    OnYard.LOG_MODE == LogMode.VERBOSE) {
                Log.d(LOG_APP_NAME, message == null ? "No Message" : message);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handle logging of an informational monitoring event.
     * 
     * @param context The current context.
     * @param message The message to log.
     */
    public static void logInfo(final Context context, final String message, final String logger)
    {
        try
        {
            if(OnYard.LOG_MODE == LogMode.DEBUG ||
                    OnYard.LOG_MODE == LogMode.VERBOSE ||
                    OnYard.LOG_MODE == LogMode.INFO)
            {
                if (isOnMainThread()) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                postEventToHTTP(context, EventLevel.INFO, null,
                                        message == null ? "No Message" : message, logger);
                            }
                            catch (final IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else {
                    postEventToHTTP(context, EventLevel.INFO, null, message, logger);
                }
                Log.i(LOG_APP_NAME, message);
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handle logging of a less severe exception.
     * 
     * @param context The current context.
     * @param ex The exception to log.
     */
    public static void logWarning(final Context context, final Exception ex, final String logger)
    {
        try
        {
            if(OnYard.LOG_MODE == LogMode.DEBUG ||
                    OnYard.LOG_MODE == LogMode.VERBOSE ||
                    OnYard.LOG_MODE == LogMode.INFO ||
                    OnYard.LOG_MODE == LogMode.WARNING)
            {
                if (isOnMainThread()) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                postEventToHTTP(context, EventLevel.WARNING, ex, null, logger);
                            }
                            catch (final IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else {
                    postEventToHTTP(context, EventLevel.WARNING, ex, null, logger);
                }
                Log.w(LOG_APP_NAME, ex.getMessage() != null ? ex.getMessage() : "No Message");
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handle logging of a fatal error.
     * 
     * @param context The current context.
     * @param ex The exception to log.
     */
    public static void logError(final Context context, final Exception ex, final String logger)
    {
        try
        {
            if(OnYard.LOG_MODE != LogMode.SUPPRESS)
            {
                if (isOnMainThread()) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                postEventToHTTP(context, EventLevel.ERROR, ex, null, logger);
                            }
                            catch (final IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else {
                    postEventToHTTP(context, EventLevel.ERROR, ex, null, logger);
                }
                Log.e(LOG_APP_NAME, ex.getMessage() != null ? ex.getMessage() : "No Message");
            }
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Create JSON string containing event details and send via HTTP POST to
     * WCF service, which will log the event on a database server.
     * 
     * @param context The current context.
     * @param level The severity level of the event.
     * @param ex The exception to log, or null if this event was <b>not</b> triggered by an exception.
     * @param message The message to log, or null if this event <b>was</b> triggered by an exception.
     * @throws IOException There is an error sending the event to the server.
     */
    private static void postEventToHTTP(Context context, EventLevel level, Exception ex,
            String message, String logger) throws IOException
            {
        try
        {
            final ProcessLogHttpPost post = new ProcessLogHttpPost(context);
            post.setLogger(logger);
            post.setEventLevel(level.toString());

            if(level == EventLevel.WARNING || level == EventLevel.ERROR) {
                post.setEventInfo(getExceptionDetails(ex));
            }
            else {
                post.setEventInfo(message);
            }

            post.submit();
        }
        catch (final Exception e)
        {
            // if exception thrown when posting to server, write to device log
            Log.e("com.iaai.onyard", "Exception when POSTing error to server: " + e.getMessage());
        }
            }

    /**
     * Format a string containing the specified exception's message and
     * full stack trace.
     * 
     * @param ex The exception from which to get message/stack trace.
     * @return The exception's formatted message and stack trace.
     */
    private static String getExceptionDetails(Exception ex)
    {
        try
        {
            final StringBuilder exDetails = new StringBuilder();

            exDetails.append(ex.toString());

            final StackTraceElement[] stackTrace = ex.getStackTrace();
            for(final StackTraceElement element : stackTrace)
            {
                exDetails.append("    at " + element.toString());
            }

            return exDetails.toString();
        }
        catch (final Exception e)
        {
            return "Exception caught; no details could be pulled.";
        }
    }

    private static boolean isOnMainThread()
    {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}
