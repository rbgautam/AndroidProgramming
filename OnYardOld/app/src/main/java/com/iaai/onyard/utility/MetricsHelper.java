package com.iaai.onyard.utility;

import java.net.InetSocketAddress;
import java.net.Socket;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.MetricsInfo;
import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.mail.EmailSender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Helper class containing methods that deal with metrics recording.
 */
public class MetricsHelper {
	
	/**
	 * The timeout of the Exchange server ping.
	 */
	private static final int PING_TIMEOUT = 30 * 1000;
	/**
	 * The port of the Exchange server with which to communicate.
	 */
	private static final int EXCHANGE_SERVER_PORT = 25;
	
	/**
	 * If the most recent stock record exists in the Metrics table with null end time, 
	 * do nothing. Otherwise, create a new record in the table with the specified 
	 * stock number and set Stock Start Time as the current system time. 
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to insert.
	 */
	public static void createRecord(Context context, String stockNum)
	{
		if(!isStockInProgress(context, stockNum))
		{
			ContentValues values = new ContentValues();

			values.put(OnYard.Metrics.COLUMN_NAME_STOCK_NUMBER, stockNum);
			values.put(OnYard.Metrics.COLUMN_NAME_STOCK_START_TIME, getUnixTimeStamp());

			context.getContentResolver().insert(
					OnYard.Metrics.CONTENT_URI,
					values
			);
		}
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by setting the Stock
	 * End Time to the current system time.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 */
	public static void updateStockEndTime(Context context, String stockNum)
	{		
		ContentValues values = new ContentValues();

		values.put(OnYard.Metrics.COLUMN_NAME_STOCK_END_TIME, getUnixTimeStamp());

		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by setting the Number
	 * of Photos to the specified value.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 * @param numPhotos The number of photos.
	 */
	public static void updateNumPhotos(Context context, String stockNum, int numPhotos)
	{		
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS, numPhotos);
		
		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by setting the Number
	 * of Annotated Photos to the specified value.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 * @param numPhotosAnnotated The number of photos annotated.
	 */
	public static void updateNumPhotosAnnotated(Context context, String stockNum, 
			int numPhotosAnnotated)
	{		
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS_ANNOTATED, numPhotosAnnotated);
		
		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by increasing the Number
	 * of Annotated Photos by 1.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 */
	public static void addPhotoAnnotated(Context context, String stockNum)
	{		
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS_ANNOTATED, 
				(getNumPhotosAnnotated(context, stockNum) + 1));
		
		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by setting the Is
	 * Email Sent field.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 * @param isEmailSent The flag indicating whether email was sent. If true, the
	 * Is Email Sent field will be set to 1. Otherwise, it will be set to 0.
	 */
	public static void updateIsEmailSent(Context context, String stockNum,
			boolean isEmailSent)
	{		
		ContentValues values = new ContentValues();
		int emailSentVal = isEmailSent == true ? 1 : 0;
		
		values.put(OnYard.Metrics.COLUMN_NAME_IS_EMAIL_SENT, emailSentVal);
		
		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by setting the CSA
	 * Start Time to the current system time.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 */
	public static void updateCSAStartTime(Context context, String stockNum)
	{		
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Metrics.COLUMN_NAME_CSA_START_TIME, getUnixTimeStamp());
		
		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by setting the CSA
	 * End Time to the current system time.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 */
	public static void updateCSAEndTime(Context context, String stockNum)
	{		
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Metrics.COLUMN_NAME_CSA_END_TIME, getUnixTimeStamp());
		
		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by setting the Map
	 * Start Time to the current system time.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 */
	public static void updateMapStartTime(Context context, String stockNum)
	{		
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Metrics.COLUMN_NAME_MAP_START_TIME, getUnixTimeStamp());
		
		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number by setting the Map
	 * End Time to the current system time.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 */
	public static void updateMapEndTime(Context context, String stockNum)
	{		
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Metrics.COLUMN_NAME_MAP_END_TIME, getUnixTimeStamp());
		
		updateMetricsRecord(context, stockNum, values);
	}
	
	/**
	 * Create the Metrics report. If the IAA Exchange Server is available, send the report via
	 * email and delete all records from the Metrics table. The email address to which to send 
	 * is set in the strings.xml file.
	 * 
	 * @param context The current context.
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public static void sendMetrics(Context context) throws AddressException, MessagingException
	{
	    Cursor cursor = null;
	    try
	    {
    		cursor = context.getContentResolver().query(
    				OnYard.Metrics.CONTENT_URI, 
    				null, 
    				null, 
    				null, 
    				null
    		);
    
    		if(cursor.moveToFirst() == false)
    			return;
    
    		String emailBody = "Stock Number | Time On Stock (s) | Num Photos Taken | Num Photos " +
    		"Annotated | Was Email Sent | Time On Mobile CSA (s) | Time On Map (s)\n\n";
    
    		MetricsInfo metrics;
    		do
    		{
    			metrics = new MetricsInfo(cursor);
    
    			emailBody += metrics.getStockNumber() + ",";
    			emailBody += metrics.getTotalStockTime() + ",";
    			emailBody += metrics.getNumPhotos() + ",";
    			emailBody += metrics.getNumPhotosAnnotated() + ",";
    			emailBody += metrics.getIsEmailSent() + ",";
    			emailBody += metrics.getCSATime() + ",";
    			emailBody += metrics.getMapTime() + "\n";
    		} while(cursor.moveToNext());
    
    		if(isExchangeServerAvailable(context))
    		{
    			new EmailSender(context).sendMail("OnYard Daily Metrics Report for " +
    					android.os.Build.SERIAL,
    					emailBody,
    					context.getString(R.string.email_from_address),
    					context.getString(R.string.metrics_to_address)
    			);
    
    			deleteAllMetricsRecords(context);
    		}
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * Check whether the user has sent an email for the most recent Metrics record
	 * with the specified stock number.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record.
	 * @return True if an email was sent, false otherwise.
	 */
	public static boolean getIsEmailSent(Context context, String stockNum)
	{
	    Cursor cursor = null;
	    try
	    {
    		int id = getMostRecentMetricsID(context, stockNum);
    
    		cursor = context.getContentResolver().query(
    				OnYard.Metrics.CONTENT_URI, 
    				new String[]{ OnYard.Metrics.COLUMN_NAME_IS_EMAIL_SENT }, 
    				OnYard.Metrics.COLUMN_NAME_ID + "=?", 
    				new String[]{ Integer.toString(id) }, 
    				null
    		);
    
    		if (cursor.moveToFirst() == false)
    			return false;
    		else
    			return cursor.getInt(0) == 1 ? true : false;
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * Update the most recent Metrics record with the specified stock number.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number of the record to update.
	 * @param values The values to update.
	 */
	private static void updateMetricsRecord(Context context, String stockNum, 
			ContentValues values)
	{
		int id = getMostRecentMetricsID(context, stockNum);
		
		if(id != 0)
		{
			context.getContentResolver().update(
					OnYard.Metrics.CONTENT_URI,
					values, 
					OnYard.Metrics.COLUMN_NAME_ID + "=?", 
					new String[]{ Integer.toString(id) }
			);
		}
	}
	
	/**
	 * Delete all records in the Metrics table.
	 * 
	 * @param context The current context.
	 */
	private static void deleteAllMetricsRecords(Context context)
	{
		context.getContentResolver().delete(
				OnYard.Metrics.CONTENT_URI,
				null, 
				null
		);
	}
	
	/**
	 * Get the _id of the most recent Metrics record with the specified stock number.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number for which to search.
	 * @return The _id of the record with the specified stock number, or 0 if the stock
	 * number was not found.
	 */
	private static int getMostRecentMetricsID(Context context, String stockNum)
	{
	    Cursor cursor = null;
	    try
	    {
    		cursor = context.getContentResolver().query(
    				OnYard.Metrics.CONTENT_URI,
    				new String[]{ OnYard.Metrics.COLUMN_NAME_ID },
    				OnYard.Metrics.COLUMN_NAME_STOCK_NUMBER + "=?",
    				new String[]{ stockNum },
    				OnYard.Metrics.COLUMN_NAME_ID + " DESC"
    		);
    		
    		if (!cursor.moveToFirst())
    			return 0;
    		else
    			return cursor.getInt(0);
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * Get the Number of Photos annotated for the most recent Metrics record with the
	 * specified stock number.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number for which to search.
	 * @return The Number of Photos Annotated for the specified stock number, or 0
	 * if the stock number was not found.
	 */
	private static int getNumPhotosAnnotated(Context context, String stockNum)
	{
	    Cursor cursor = null;
	    try
	    {
    		cursor = context.getContentResolver().query(
    				OnYard.Metrics.CONTENT_URI,
    				new String[]{ OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS_ANNOTATED },
    				OnYard.Metrics.COLUMN_NAME_ID + "=?",
    				new String[]{ Integer.toString(getMostRecentMetricsID(context, stockNum)) },
    				OnYard.Metrics.COLUMN_NAME_ID + " DESC"
    		);
    		
    		if (!cursor.moveToFirst())
    			return 0;
    		else
    			return cursor.getInt(0);
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * Check if there exists in the Metrics table a record with the specified stock number
	 * and a null Stock End Time.
	 * 
	 * @param context The current context.
	 * @param stockNum The stock number for which to search.
	 * @return True if the stock has a record with null end time, false otherwise.
	 */
	private static boolean isStockInProgress(Context context, String stockNum)
	{
	    Cursor cursor = null;
	    try
	    {
    		cursor = context.getContentResolver().query(
    				OnYard.Metrics.CONTENT_URI,
    				new String[]{ OnYard.Metrics.COLUMN_NAME_ID },
    				OnYard.Metrics.COLUMN_NAME_STOCK_NUMBER + "=? AND " +
    					OnYard.Metrics.COLUMN_NAME_STOCK_END_TIME + " ISNULL",
    				new String[]{ stockNum },
    				null
    		);
    		
    		if (cursor.moveToFirst())
    			return true;
    		else
    			return false;
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * Get the current system time, in milliseconds.
	 * 
	 * @return The current system time, in milliseconds.
	 */
	private static long getUnixTimeStamp()
	{
		return System.currentTimeMillis();
	}
	
	/**
	 * Check if the IAA Exchange server is available.
	 * 
	 * @param context The current context.
	 * @return True if the Exchange server is available, false otherwise.
	 */
	private static boolean isExchangeServerAvailable(Context context)
	{
		Socket socket = new Socket();
		try
		{
			socket.connect(new InetSocketAddress(context.getString(
					R.string.iaa_mail_server), EXCHANGE_SERVER_PORT), PING_TIMEOUT);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
