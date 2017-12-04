package com.iaai.onyard.classes;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

/**
 * Class that represents one row of the Color table.
 */
public class ColorInfo {

	private String mColorCode;
	private String mColorDescription;
	
	/**
	 * Constructor with initial values.
	 * 
	 * @param colorCode The initial color code.
	 * @param colorDescription The initial color description.
	 */
	public ColorInfo(String colorCode, String colorDescription)
	{
		mColorCode = colorCode;
		mColorDescription = colorDescription;
	}
	
	/**
	 * Default constructor.
	 */
	public ColorInfo()
	{
		mColorCode = "";
		mColorDescription = "";
	}
	
	/**
	 * Constructor that sets initial values from JSON object.
	 * 
	 * @param color The JSONObject containing color code/description.
	 * @throws JSONException if color code and/or description JSON Object mapping doesn't exist.
	 */
	public ColorInfo(JSONObject color) throws JSONException
	{
		mColorCode = color.getString(OnYard.Color.JSON_NAME_CODE);
		mColorDescription = color.getString(OnYard.Color.JSON_NAME_DESCRIPTION);
	}
	
	/**
	 * Gets ContentValues containing this object's values.
	 *
	 * @return The ContentValues object.
	 */
	public ContentValues getContentValues()
	{
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Color.COLUMN_NAME_CODE, mColorCode);
		values.put(OnYard.Color.COLUMN_NAME_DESCRIPTION, mColorDescription);
		
		return values;
	}
	
	public void setColorCode(String colorCode) {
		mColorCode = colorCode;
	}
	public String getColorCode() {
		return mColorCode;
	}
	public void setColorDescription(String colorDescription) {
		mColorDescription = colorDescription;
	}
	public String getColorDescription() {
		return mColorDescription;
	}
}
