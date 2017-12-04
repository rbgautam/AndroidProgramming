package com.iaai.onyard.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.classes.VehicleInfo;
import com.iaai.onyard.performancetest.Timer;

/**
 * Helper class containing methods that deal with JSON parsing.
 */
public class JSONHelper {
	/**
	 * Flag indicating whether or not the end of the JSON file has been reached.
	 */
	private static boolean mIsEndOfFile = false;
	
	/**
	 * Parse a file containing Vehicles data in JSON format and batch insert all Vehicles within
	 * into the Vehicles table in the OnYard ContentProvider. The JSON file is deleted after
	 * it has been read all the way through.
	 * 
	 * @param context The current context.
	 * @param fileName The name of the JSON file to parse.
	 * @param batchSize The number of vehicles to read per batch read/insert operation.
	 * @throws JSONException if any Vehicles values are missing from the JSON file.
	 * @throws IOException if an error occurs while setting a mark in the file reader.
	 */
	public static void insertAllVehiclesFromJSONFile(Context context, String fileName, int batchSize)
		throws JSONException, IOException
	{
		Timer t = new Timer("JSONHelper.insertAllVehiclesFromJSONFile"); t.start();
		
		JSONArray vehicleArray = null;
		ContentValues[] values = null;
		int numVehicles = 0;
		
		InputStreamReader isr = new InputStreamReader(
				new FileInputStream(context.getString(R.string.json_file_path) + 
						context.getString(R.string.json_file)), "iso-8859-1");
		Reader reader = new BufferedReader(isr, 8);
		
		try
		{
			while(!mIsEndOfFile)
			{
				vehicleArray = getJSONArrayFromFile(reader, batchSize);
				
				numVehicles = vehicleArray.length();
				values = new ContentValues[numVehicles];
				
				for (int index = 0; index < numVehicles; index++)
				{
					values[index] = new VehicleInfo(vehicleArray.getJSONObject(index)).getContentValues();
					
					if(index == 0)
						insertUpdatedSyncTime(context, vehicleArray.getJSONObject(index).getLong(
								OnYard.Vehicles.JSON_NAME_UPDATE_TIME_UNIX));
				}
				
				context.getContentResolver().bulkInsert(OnYard.Vehicles.CONTENT_URI, values);
				LogHelper.logDebug("Inserted " + vehicleArray.length() + " Records.");
			}
			
			mIsEndOfFile = false; //in case another sync starts right away
			context.deleteFile(context.getString(R.string.json_file));
		}
		finally
		{
			isr.close();
			reader.close();
		}

		t.end(); t.logDebug();
	}
	
	/**
	 * Parse a file containing vehicles data in JSON format. For each vehicle read, check if it already 
	 * exists in the OnYard ContentProvider. If it does exist, update the record. If it does not exist, 
	 * insert a new record. The JSON file is deleted after it has been read all the way through.
	 * 
	 * @param context The current context.
	 * @param fileName The name of the JSON file to parse.
	 * @param batchSize The number of vehicles to read per batch read/update/insert operation.
	 * @throws IOException if any Vehicles values are missing from the JSON file.
	 * @throws JSONException if an error occurs while setting a mark in the file reader.
	 */
	public static void insertUpdatedVehiclesFromJSONFile(Context context, String fileName, int batchSize)
		throws IOException, JSONException
	{
		Timer t = new Timer("JSONHelper.insertUpdatedVehiclesFromJSONFile"); t.start();

		JSONArray vehicleArray = null;
		VehicleInfo vehicle = null;
		int numVehicles = 0;

		InputStreamReader isr = new InputStreamReader(
				new FileInputStream(context.getString(R.string.json_file_path) + 
						context.getString(R.string.json_file)), "iso-8859-1");
		Reader reader = new BufferedReader(isr, 8);
		
		try
		{
			while(!mIsEndOfFile)
			{
				vehicleArray = getJSONArrayFromFile(reader, batchSize);
				
				numVehicles = vehicleArray.length();
				
				for (int index = 0; index < numVehicles; index++)
				{
					vehicle = new VehicleInfo(vehicleArray.getJSONObject(index));
					
					if(isStockNumberInDB(context, vehicle.getStockNumber()))
					{
						context.getContentResolver().update(
								Uri.withAppendedPath(OnYard.Vehicles
										.CONTENT_STOCK_NUMBER_URI_BASE, vehicle.getStockNumber()), 
								vehicle.getContentValues(), 
								null, 
								null
						);
					}
					else
					{
						context.getContentResolver().insert(
								OnYard.Vehicles.CONTENT_URI, 
								vehicle.getContentValues()
						);
					}
					
					if(index == 0)
						insertUpdatedSyncTime(context, vehicleArray.getJSONObject(index).getLong(
								OnYard.Vehicles.JSON_NAME_UPDATE_TIME_UNIX));
				}
				
				LogHelper.logDebug("OnDemandSync updated " + vehicleArray.length() + " records.");
			}
	
			mIsEndOfFile = false; //in case another sync starts right away
			context.deleteFile(context.getString(R.string.json_file));
		}
		finally
		{
			isr.close();
			reader.close();
		}

		t.end(); t.logDebug();
	}

	/**
	 * Read a specified number of JSON objects and return a valid JSONArray containing them.
	 * 
	 * @param reader The Reader object from which to read.
	 * @param numObjects The number of JSON Objects to read.
	 * @return A valid JSON array containing the read objects.
	 * @throws IOException if an error occurs while setting a mark in the reader.
	 * @throws JSONException if the JSONArray parse fails or doesn't yield a JSONArray.
	 */
	private static JSONArray getJSONArrayFromFile(Reader reader, int numObjects) 
		throws IOException, JSONException
	{
		StringBuilder sb = new StringBuilder();
		int rightBraceIndex = getRightBraceIndex(reader, numObjects);
		char[] jsonChars = new char[rightBraceIndex];

		reader.read();
		sb.append('[');
		
		reader.read(jsonChars, 0, rightBraceIndex);
		sb.append(jsonChars);
		
		sb.append(']');

		return new JSONArray(sb.toString());
	}
	
	/**
	 * Gets the reader index of the nth right brace in the Reader, where n is the number of
	 * JSON objects to read.
	 * 
	 * @param reader The Reader object from which to read.
	 * @param numObjects The number of JSON objects to read.
	 * @return The index of the nth right brace in the Reader.
	 * @throws IOException if an error occurs while setting a mark in the reader.
	 */
	private static int getRightBraceIndex(Reader reader, int numObjects) throws IOException
	{
		char fileChar = ' ';
		int intChar = 0;
		int numObjectsFound = 0;
		int fileIndex = 0;
		
		reader.mark(Integer.MAX_VALUE);
		
		while (numObjectsFound < numObjects && (intChar = reader.read()) != -1)
		{
			fileChar = (char) intChar;
			
			if(fileChar == '}')
				numObjectsFound++;
			
			fileIndex++;
		}
		
		reader.reset();
		
		if (intChar == -1)
		{
			mIsEndOfFile = true;
			return (fileIndex - 2); //account for EOF and ']'
		}
		else
			return fileIndex - 1;	//account for ','
	}
	
	/**
	 * Check if record with specified stock number exists in the Vehicles table of the OnYard
	 * ContentProvider.
	 * 
	 * @param context The current context.
	 * @param stockNumber The stock number for which to search.
	 * @return True if the stock number exists in the Vehicles table, false if it does not exist.
	 */
	private static boolean isStockNumberInDB(Context context, String stockNumber)
	{
	    Cursor cursor = null;
	    try
	    {
    		cursor = context.getContentResolver().query(
    				Uri.withAppendedPath(OnYard.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, stockNumber), 
    				new String[]{ OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER }, 
    				null, 
    				null, 
    				null
    		);
    
    		boolean isStockNumberInDB = (cursor.getCount() > 0);
    
    		cursor.close();
    
    		return isStockNumberInDB;
	    }
	    finally
	    {
	        cursor.close();
	    }
	}
	
	/**
	 * If update time already exists in Config table, update it with specified value. Otherwise,
	 * insert a new update time record with the specified value.
	 * 
	 * @param context The current context.
	 * @param updateTime The update time value to update/insert.
	 */
	private static void insertUpdatedSyncTime(Context context, Long updateTime)
	{
		if(updateTime == null)
			return;
		
		ContentValues values = new ContentValues();
		values.put(OnYard.Config.COLUMN_NAME_KEY, OnYard.Config.CONFIG_KEY_UPDATE_DATE_TIME);
		values.put(OnYard.Config.COLUMN_NAME_VALUE, updateTime);
		
		if(SyncHelper.isConfigKeyInDB(context, OnYard.Config.CONFIG_KEY_UPDATE_DATE_TIME))
		{
			context.getContentResolver().update(
					Uri.withAppendedPath(OnYard.Config.CONTENT_KEY_URI_BASE, 
							OnYard.Config.CONFIG_KEY_UPDATE_DATE_TIME), 
					values, 
					null, 
					null
			);
		}
		else
		{		
			context.getContentResolver().insert(
					OnYard.Config.CONTENT_URI, 
					values
			);
		}
	}
	

}
