package com.iaai.onyard.utility;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONStringer;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;
import android.util.Log;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.classes.OnYard.LogMode;
import com.iaai.onyard.ssl.TrustAllSSLSocketFactory;

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
	private static enum EventLevel 
	{
		/**
		 * Events that provide information about what is happening in the code.
		 */
		VERBOSE ("VERBOSE"),
		
		/**
		 * Events that provide information which aids in development or in identification
		 * of a bug.
		 */
		DEBUG ("DEBUG"), 
		
		/**
		 * Events that provide information that is useful for monitoring.
		 */
		INFO ("INFO"), 
		
		/**
		 * Exceptions that do not negatively affect the user's experience of the app. For example,
		 * an exception in the code to show a progress dialog would warrant a warning, since the
		 * only consequence is the dialog not being displayed.
		 */
		WARNING ("WARNING"), 

		/**
		 * Exceptions that directly and negatively affect the user's experience of the app. Errors
		 * should be looked into immediately.
		 */
		ERROR ("ERROR");
		
		private final String name;
		
		EventLevel(String name) {
			this.name = name;
		}
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
			if(OnYard.LOG_MODE == LogMode.VERBOSE)
				Log.v(LOG_APP_NAME, message);
		}
		catch (Exception e)
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
					OnYard.LOG_MODE == LogMode.VERBOSE)
				Log.d(LOG_APP_NAME, message);
		}
		catch (Exception e)
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
	public static void logInfo(final Context context, final String message)
	{
		try
		{
		    if(OnYard.LOG_MODE == LogMode.DEBUG ||
                    OnYard.LOG_MODE == LogMode.VERBOSE ||
                    OnYard.LOG_MODE == LogMode.INFO)
		    {
		    	//TODO: uncomment this and remove the default logging. this is a temporary change
		    	//	so that we can test against production data.
//    		    if(isOnMainThread())
//    		    {
//    		        new Thread(new Runnable() {
//    	                public void run() {
//    	                    try
//                            {
//                                postEventToHTTP(context, EventLevel.INFO, null, message);
//                            }
//                            catch (IOException e)
//                            {
//                                e.printStackTrace();
//                            }
//    	                }
//    	            }).start();
//    		    }
//    		    else
//    		    {
//        			postEventToHTTP(context, EventLevel.INFO, null, message);
//    		    }
		    	Log.i(LOG_APP_NAME, message);
		    }
		}
		catch (Exception e)
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
	public static void logWarning(final Context context, final Exception ex)
	{
		try
		{
		    if(OnYard.LOG_MODE == LogMode.DEBUG ||
                    OnYard.LOG_MODE == LogMode.VERBOSE ||
                    OnYard.LOG_MODE == LogMode.INFO ||
                    OnYard.LOG_MODE == LogMode.WARNING)
		    {
		    	//TODO: uncomment this and remove the default logging. this is a temporary change
		    	//	so that we can test against production data.
//		    	if(isOnMainThread())
//		    	{
//		    		new Thread(new Runnable() {
//		    			public void run() {
//		    				try
//		    				{
//		    					postEventToHTTP(context, EventLevel.WARNING, ex, null);
//		    				}
//		    				catch (IOException e)
//		    				{
//		    					e.printStackTrace();
//		    				}
//		    			}
//		    		}).start();
//		    	}
//		    	else
//		    	{
//		    		postEventToHTTP(context, EventLevel.WARNING, ex, null);
//		    	}
		    	Log.w(LOG_APP_NAME, ex.getMessage());
		    }
		}
		catch (Exception e)
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
	public static void logError(final Context context, final Exception ex)
	{
		try
		{
		    if(OnYard.LOG_MODE != LogMode.SUPPRESS)
		    {
		    	//TODO: uncomment this and remove the default logging. this is a temporary change
		    	//	so that we can test against production data.
//    		    if(isOnMainThread())
//    		    {
//                    new Thread(new Runnable() {
//                        public void run() {
//                            try
//                            {
//                                postEventToHTTP(context, EventLevel.ERROR, ex, null);
//                            }
//                            catch (IOException e)
//                            {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//    		    }
//    		    else
//    		    {
//        			postEventToHTTP(context, EventLevel.ERROR, ex, null);
//    		    }
		    	Log.e(LOG_APP_NAME, ex.getMessage());
		    }
		}
		catch (Exception e)
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
			String message) throws IOException
	{
		try
		{
			HttpPost request = new HttpPost(context.getString(R.string.onyard_service_url_base) + 
			"log");
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");

			JSONStringer event;
			if(level == EventLevel.WARNING || level == EventLevel.ERROR)
				event = getEventJsonString(context, level, ex);
			else
				event = getEventJsonString(context, level, message);

			StringEntity entity = new StringEntity(event.toString());

			request.setEntity(entity);

			HttpParams params = new BasicHttpParams(); 
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 60000);

			SchemeRegistry schreg = new SchemeRegistry();
			schreg.register(new Scheme("https", 
					new TrustAllSSLSocketFactory(), 443));
			ClientConnectionManager connManager = 
				new ThreadSafeClientConnManager(params, schreg);

			DefaultHttpClient httpClient = new DefaultHttpClient(connManager, params);
			HttpResponse response = httpClient.execute(request);

			LogHelper.logDebug("Event Details POST: " + response.getStatusLine().toString());
		}
		catch (Exception e)
		{
			throw new IOException("Error posting event to WCF Service");
		}
	}
	
	/**
	 * Get the current application name and version.
	 * 
	 * @param context The current context.
	 * @return The app name and version.
	 */
	private static String getAppVersion(Context context)
	{
		try 
		{
			return context.getPackageManager().
					getPackageInfo(context.getPackageName(), 0).versionName;
		} 
		catch (NameNotFoundException e) 
		{
			return "VERSION UNKNOWN";
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
			StringBuilder exDetails = new StringBuilder();
			
			exDetails.append(ex.toString());
			
			StackTraceElement[] stackTrace = ex.getStackTrace();
			for(StackTraceElement element : stackTrace)
			{
				exDetails.append("    at " + element.toString());
			}
			
			return exDetails.toString();
		}
		catch (Exception e)
		{
			return "Exception caught; no details could be pulled.";
		}
	}
	
	/**
	 * Create the JSON string to be sent to the server containing the specified event's
	 * information.
	 * 
	 * @param context The current context.
	 * @param level The severity level of the event.
	 * @param ex The exception that triggered the event.
	 * @return A JSON string containing the event details.
	 * @throws JSONException If there is an error forming the JSON string.
	 */
	private static JSONStringer getEventJsonString(Context context, EventLevel level, 
			Exception ex) throws JSONException
	{
		JSONStringer event = new JSONStringer();
			
		event.object()
			.key(OnYard.LogEvent.JSON_OBJECT_NAME)
				.object()
					.key(OnYard.LogEvent.JSON_NAME_APP_VERSION)
						.value(context.getString(R.string.app_name) + "_" +
								getAppVersion(context))
					.key(OnYard.LogEvent.JSON_NAME_DEVICE_SERIAL)
						.value(android.os.Build.SERIAL)
					.key(OnYard.LogEvent.JSON_NAME_EVENT_LEVEL)
						.value(level.toString())
					.key(OnYard.LogEvent.JSON_NAME_EVENT_DESCRIPTION)
						.value(getExceptionDetails(ex))
				.endObject()
		.endObject();
		
		return event;
	}
	
	/**
	 * Create the JSON string to be sent to the server containing the specified event's
	 * information.
	 * 
	 * @param context The current context.
	 * @param level The severity level of the event.
	 * @param message The message to log.
	 * @return A JSON string containing the event details.
	 * @throws JSONException If there is an error forming the JSON string.
	 */
	private static JSONStringer getEventJsonString(Context context, EventLevel level, 
			String message) throws JSONException
	{
		JSONStringer event = new JSONStringer();

		event.object()
			.key(OnYard.LogEvent.JSON_OBJECT_NAME)
				.object()
					.key(OnYard.LogEvent.JSON_NAME_APP_VERSION)
						.value(context.getString(R.string.app_name) + "_" +
								getAppVersion(context))
					.key(OnYard.LogEvent.JSON_NAME_DEVICE_SERIAL)
						.value(android.os.Build.SERIAL)
					.key(OnYard.LogEvent.JSON_NAME_EVENT_LEVEL)
						.value(level.toString())
					.key(OnYard.LogEvent.JSON_NAME_EVENT_DESCRIPTION)
						.value(message)
				.endObject()
		.endObject();
		
		return event;
	}
	
	private static boolean isOnMainThread()
	{
	    return (Thread.currentThread() == Looper.getMainLooper().getThread());
	}
}
