package com.iaai.onyard.classes;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Class that represents one row of the Vehicles table.
 */
public class VehicleInfo {
	
	private String mStockNumber;
	private String mVIN;
	private String mClaimNumber;
	private float mLatitude;
	private float mLongitude;
	private String mAisle;
	private int mStall;
	private String mColor;
	private int mYear;
	private String mMake;
	private String mModel;
	private String mSalvageProvider;
	private String mStatus;
	private String mDamage;

	/**
	 * Default constructor.
	 */
	public VehicleInfo()
	{
		mStockNumber = "";
		mVIN = "";
		mClaimNumber = "";
		mLatitude = (float) 0;
		mLongitude = (float) 0;
		mAisle = "";
		mStall = 0;
		mColor = "";
		mYear = 0;
		mMake = "";
		mModel = "";
		mSalvageProvider = "";
		mStatus = "";
		mDamage = "";
	}
	
	/**
	 * Constructor with initial values.
	 * 
	 * @param stockNumber The initial stock number.
	 * @param vin The initial VIN.
	 * @param claimNumber The initial claim number.
	 * @param latitude The initial latitude.
	 * @param longitude The initial longitude.
	 * @param aisle The initial aisle.
	 * @param stall The initial stall.
	 * @param color The initial color.
	 * @param year The initial year.
	 * @param make The initial make.
	 * @param model The initial model.
	 * @param salvageProvider The initial salvage provider.
	 * @param status The initial status.
	 * @param damage The initial damage.
	 */
	public VehicleInfo(String stockNumber, String vin, String claimNumber,
			float latitude, float longitude, String aisle, int stall, String color, int year,
			String make, String model, String salvageProvider, String status, String damage)
	{
		mStockNumber = stockNumber;
		mVIN = vin;
		mClaimNumber = claimNumber;
		mLatitude = latitude;
		mLongitude = longitude;
		mAisle = aisle;
		mStall = stall;
		mColor = color;
		mYear = year;
		mMake = make;
		mModel = model;
		mSalvageProvider = salvageProvider;
		mStatus = status;
		mDamage = damage;
	}
	
	/**
	 * Constructor that sets initial values from JSON object.
	 * 
	 * @param vehicle The JSONObject containing vehicles values.
	 * @throws JSONException if any vehicles value JSON Object mapping doesn't exist.
	 */
	public VehicleInfo(JSONObject vehicle) throws JSONException
	{
		mStockNumber = vehicle.getString(OnYard.Vehicles.JSON_NAME_STOCK_NUMBER);
		mVIN = vehicle.getString(OnYard.Vehicles.JSON_NAME_VIN);
		mClaimNumber = vehicle.getString(OnYard.Vehicles.JSON_NAME_CLAIM_NUMBER);
		mLatitude = (float)vehicle.getDouble(OnYard.Vehicles.JSON_NAME_LATITUDE);
		mLongitude = (float)vehicle.getDouble(OnYard.Vehicles.JSON_NAME_LONGITUDE);
		mAisle = vehicle.getString(OnYard.Vehicles.JSON_NAME_AISLE);
		mStall = vehicle.optInt(OnYard.Vehicles.JSON_NAME_STALL);
		mColor = vehicle.getString(OnYard.Vehicles.JSON_NAME_COLOR);
		mYear = vehicle.optInt(OnYard.Vehicles.JSON_NAME_YEAR);
		mMake = vehicle.getString(OnYard.Vehicles.JSON_NAME_MAKE);
		mModel = vehicle.getString(OnYard.Vehicles.JSON_NAME_MODEL);
		mSalvageProvider = vehicle.getString(OnYard.Vehicles.JSON_NAME_SALVAGE_PROVIDER);
		mStatus = vehicle.getString(OnYard.Vehicles.JSON_NAME_STATUS);
		mDamage = vehicle.getString(OnYard.Vehicles.JSON_NAME_DAMAGE);
	}
	
	/**
	 * Constructor that initializes Vehicles values from a Cursor containing them.
	 * Values not contained in the Cursor are set to default values. This method
	 * does not change the Cursor row.
	 * 
	 * @param vehicleCursor The Cursor containing Vehicles values.
	 */
	public VehicleInfo(Cursor vehicleCursor)
	{
		mStockNumber = "";
		mVIN = "";
		mClaimNumber = "";
		mLatitude = (float) 0;
		mLongitude = (float) 0;
		mAisle = "";
		mStall = 0;
		mColor = "";
		mYear = 0;
		mMake = "";
		mModel = "";
		mSalvageProvider = "";
		mStatus = "";
		mDamage = "";

		if(vehicleCursor == null || vehicleCursor.isAfterLast())
			return;

		int colIndex;

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mStockNumber = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_VIN);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mVIN = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mClaimNumber = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_LATITUDE);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mLatitude = vehicleCursor.getFloat(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_LONGITUDE);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mLongitude = vehicleCursor.getFloat(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_AISLE);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mAisle = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_STALL);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mStall = vehicleCursor.getInt(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_COLOR);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mColor = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_YEAR);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mYear = vehicleCursor.getInt(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_MAKE);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mMake = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_MODEL);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mModel = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mSalvageProvider = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_STATUS);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mStatus = vehicleCursor.getString(colIndex);

		colIndex = vehicleCursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_DAMAGE);
		if(colIndex != -1 && vehicleCursor.getString(colIndex) != null)
			mDamage = vehicleCursor.getString(colIndex);
	}
	
	/**
	 * Gets ContentValues containing this object's values.
	 *
	 * @return The ContentValues object.
	 */
	public ContentValues getContentValues()
	{
		ContentValues values = new ContentValues();
		
		values.put(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER, mStockNumber);
		values.put(OnYard.Vehicles.COLUMN_NAME_VIN, mVIN);
		values.put(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER, mClaimNumber);
		values.put(OnYard.Vehicles.COLUMN_NAME_LATITUDE, mLatitude);
		values.put(OnYard.Vehicles.COLUMN_NAME_LONGITUDE, mLongitude);
		values.put(OnYard.Vehicles.COLUMN_NAME_AISLE, mAisle);
		values.put(OnYard.Vehicles.COLUMN_NAME_STALL, mStall);
		values.put(OnYard.Vehicles.COLUMN_NAME_COLOR, mColor);
		values.put(OnYard.Vehicles.COLUMN_NAME_YEAR, mYear);
		values.put(OnYard.Vehicles.COLUMN_NAME_MAKE, mMake);
		values.put(OnYard.Vehicles.COLUMN_NAME_MODEL, mModel);
		values.put(OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER, mSalvageProvider);
		values.put(OnYard.Vehicles.COLUMN_NAME_STATUS, mStatus);
		values.put(OnYard.Vehicles.COLUMN_NAME_DAMAGE, mDamage);
		
		return values;
	}

	public void setStockNumber(String stockNumber) {
		mStockNumber = stockNumber;
	}

	public String getStockNumber() {
		return mStockNumber;
	}

	public void setVIN(String vin) {
		mVIN = vin;
	}

	public String getVIN() {
		return mVIN;
	}

	public void setClaimNumber(String claimNumber) {
		mClaimNumber = claimNumber;
	}

	public String getClaimNumber() {
		return mClaimNumber;
	}

	public void setLatitude(float latitude) {
		mLatitude = latitude;
	}

	public float getLatitude() {
		return mLatitude;
	}

	public void setLongitude(float longitude) {
		mLongitude = longitude;
	}

	public float getLongitude() {
		return mLongitude;
	}

	public void setAisle(String aisle) {
		mAisle = aisle;
	}

	public String getAisle() {
		return mAisle;
	}

	public void setStall(int stall) {
		mStall = stall;
	}

	public int getStall() {
		return mStall;
	}
	
	public String getStallString() {
		return mStall == 0 ? "" : Integer.toString(mStall);
	}

	public void setColor(String color) {
		mColor = color;
	}

	public String getColor() {
		return mColor;
	}

	public void setYear(int year) {
		mYear = year;
	}

	public int getYear() {
		return mYear;
	}
	
	public String getYearString() {
		return mYear == 0 ? "" : Integer.toString(mYear);
	}

	public void setMake(String make) {
		mMake = make;
	}

	public String getMake() {
		return mMake;
	}

	public void setModel(String model) {
		mModel = model;
	}

	public String getModel() {
		return mModel;
	}

	public void setSalvageProvider(String salvageProvider) {
		mSalvageProvider = salvageProvider;
	}

	public String getSalvageProvider() {
		return mSalvageProvider;
	}

	public void setStatus(String status) {
		mStatus = status;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setDamage(String damage) {
		mDamage = damage;
	}

	public String getDamage() {
		return mDamage;
	}	
	
	public String getYearMakeModel() {
		if(mYear == 0)
			return mMake + " " + mModel;
		else
			return mYear + " " + mMake + " " + mModel;
	}
}
