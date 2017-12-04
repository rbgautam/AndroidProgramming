package com.iaai.onyard.classes;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

/**
 * Class that represents one row of the Damage table.
 */
public class DamageInfo {

	private String mDamageCode;
	private String mDamageDescription;
	
	/**
	 * Constructor with initial values.
	 * 
	 * @param damageCode The initial damage code.
	 * @param damageDescription The initial damage description.
	 */
	public DamageInfo(String damageCode, String damageDescription)
	{
		mDamageCode = damageCode;
		mDamageDescription = damageDescription;
	}
	
	/**
	 * Default constructor.
	 */
	public DamageInfo()
	{
		mDamageCode = "";
		mDamageDescription = "";
	}
	
	/**
	 * Constructor that sets initial values from JSON object.
	 * 
	 * @param damage The JSONObject containing damage code/description.
	 * @throws JSONException if damage code and/or description JSON Object mapping doesn't exist.
	 */
	public DamageInfo(JSONObject damage) throws JSONException
	{
		mDamageCode = damage.getString(OnYard.Damage.JSON_NAME_CODE);
		mDamageDescription = damage.getString(OnYard.Damage.JSON_NAME_DESCRIPTION);
	}
	
	/**
	 * Gets ContentValues containing this object's values.
	 *
	 * @return The ContentValues object.
	 */
	public ContentValues getContentValues()
	{
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Damage.COLUMN_NAME_CODE, mDamageCode);
		values.put(OnYard.Damage.COLUMN_NAME_DESCRIPTION, mDamageDescription);
		
		return values;
	}
	
	public void setDamageCode(String damageCode) {
		mDamageCode = damageCode;
	}
	public String getDamageCode() {
		return mDamageCode;
	}
	public void setDamageDescription(String damageDescription) {
		mDamageDescription = damageDescription;
	}
	public String getDamageDescription() {
		return mDamageDescription;
	}
}
