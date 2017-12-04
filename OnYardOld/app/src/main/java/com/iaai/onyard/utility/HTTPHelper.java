package com.iaai.onyard.utility;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.iaai.onyard.R;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.ssl.TrustAllSSLSocketFactory;

/**
 * Helper class containing methods that deal with HTTP WCF service calls or network status.
 */
public class HTTPHelper {
	
	/**
	 * The timeout of the OnYard server ping.
	 */
	private static final int PING_TIMEOUT = 30 * 1000;
	/**
	 * The port of the OnYard server with which to communicate.
	 */
	private static final int ONYARD_SERVER_PORT = 443;
	
	/**
	 * Consume the OnYard WCF Service and write the result into a file.
	 * 
	 * @param context The current context.
	 * @param url The url of the service to consume.
	 * @throws IOException if error getting HTTP response, 
	 * 		or file output stream does not close properly.
	 */
	public static void writeFileFromHTTP(Context context, String url) throws IOException
	{		
		Timer t = new Timer("HTTPHelper.getJSONFileFromHTTP");
		t.start();
		
		//initialize
		HttpEntity entity = null;
		FileOutputStream osw = null;

		//http get
		entity = getHttpEntity(context, url);

		LogHelper.logDebug("web service consumed");

		//write response to file
		try
		{
			osw = context.openFileOutput(context.getString(R.string.json_file), 
					Context.MODE_PRIVATE); 

			entity.writeTo(osw);

			osw.flush();

			LogHelper.logDebug("written to file");
		}
		finally
		{
			osw.close();
		}
		
		t.end();
		t.logDebug();
	}

	/**
	 * Consume the OnYard WCF Service and return the result as a JSONArray.
	 * 
	 * @param context The current context.
	 * @param url The URL of the service to consume.
	 * @return The JSONArray created from the HTTP response.
	 * @throws IOException if error getting HTTP response or reading/closing input stream.
	 * @throws JSONException if error converting response to JSONArray.
	 */
	public static JSONArray getJSONArrayFromHTTP(Context context, String url) 
			throws IOException, JSONException
	{		
		Timer t = new Timer("HTTPHelper.getJSONArrayFromHTTP"); t.start();
		
		//initialize
		InputStream is = null;
		String result = "";
		JSONArray jArray = null;

		//http get
		HttpEntity entity = getHttpEntity(context, url);

		is = entity.getContent();

		//convert response to string
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line + "\n");
			}
			result=sb.toString();
		}
		finally
		{
			is.close();
		}

		//try parse the string to a JSON array
	    jArray = new JSONArray(result);
	        
		t.end(); t.logDebug();
		
		return jArray;
	}
	
	/**
	 * Create HttpClient with proper settings and consume the OnYard WCF service, returning
	 * the response entity.
	 * 
	 * @param context The current context.
	 * @param url The url of the WCF service.
	 * @return The Http response entity.
	 * @throws IOException if there is an error getting the HTTP response.
	 */
	private static HttpEntity getHttpEntity(Context context, String url) throws IOException
	{
		try
		{
			HttpEntity entity;
			HttpParams params = new BasicHttpParams(); 
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 60000);

			SchemeRegistry schreg = new SchemeRegistry();
			schreg.register(new Scheme("https", 
					new TrustAllSSLSocketFactory(), 443));
			ClientConnectionManager connManager = 
				new ThreadSafeClientConnManager(params, schreg);

			HttpClient httpClient = new DefaultHttpClient(connManager, params);
			HttpGet request = new HttpGet(url);
			request.setHeader("Accept", "application/json"); 
			request.setHeader("Content-type", "application/json"); 

			HttpResponse response = httpClient.execute(request);

			LogHelper.logDebug("Data Sync GET: " + response.getStatusLine().toString());

			entity = response.getEntity();
			return entity;
		}
		catch (Exception e)
		{
			throw new IOException("Error getting HTTP entity from WCF Service response");
		}
	}
	
    /**
     * Check whether wifi is currently available.
     * 
     * @param context The current context.
     * @return True if wifi is available, false otherwise.
     */
    public static boolean isNetworkAvailable(Context context)
    {
    	ConnectivityManager connectivityManager = 
    		(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    	NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

    	return (activeNetworkInfo != null);
    }
    
	/**
	 * Check if the OnYard server is available.
	 * 
	 * @param context The current context.
	 * @return True if the OnYard server is available, false otherwise.
	 */
	public static boolean isOnYardServerAvailable(Context context)
	{
		Socket socket = new Socket();
		try
		{
			socket.connect(new InetSocketAddress(context.getString(R.string.iaa_onyard_server), 
					ONYARD_SERVER_PORT), PING_TIMEOUT);
			return true;
		}
		catch(Exception e)
		{
			LogHelper.logError(context, e);
			return false;
		}
	}
}
