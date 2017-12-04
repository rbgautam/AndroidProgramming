package com.iaai.onyard.classes;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

/**
 * Class that represents one row of the Status table.
 */
public class StatusInfo {

	private String mStatusCode;
	private String mStatusDescription;
	
	/**
	 * Constructor with initial values.
	 * 
	 * @param statusCode The initial status code.
	 * @param statusDescription The initial status description.
	 */
	public StatusInfo(String statusCode, String statusDescription)
	{
		mStatusCode = statusCode;
		mStatusDescription = statusDescription;
	}
	
	/**
	 * Default constructor.
	 */
	public StatusInfo()
	{
		mStatusCode = "";
		mStatusDescription = "";
	}
	
	/**
	 * Constructor that sets initial values from JSON object.
	 * 
	 * @param status The JSONObject containing status code/description.
	 * @throws JSONException if status code and/or description JSON Object mapping doesn't exist.
	 */
	public StatusInfo(JSONObject status) throws JSONException
	{
		mStatusCode = status.getString(OnYard.Status.JSON_NAME_CODE);
		mStatusDescription = status.getString(OnYard.Status.JSON_NAME_DESCRIPTION);
	}
	
	/**
	 * Gets ContentValues containing this object's values.
	 *
	 * @return The ContentValues object.
	 */
	public ContentValues getContentValues()
	{
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Status.COLUMN_NAME_CODE, mStatusCode);
		values.put(OnYard.Status.COLUMN_NAME_DESCRIPTION, mStatusDescription);
		
		return values;
	}
	
	public void setStatusCode(String statusCode) {
		mStatusCode = statusCode;
	}
	public String getStatusCode() {
		return mStatusCode;
	}
	public void setStatusDescription(String statusDescription) {
		mStatusDescription = statusDescription;
	}
	public String getStatusDescription() {
		return mStatusDescription;
	}
}
