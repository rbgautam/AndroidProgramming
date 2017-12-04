package com.iaai.onyard.classes;

import android.database.Cursor;

/**
 * Class that represents one row of the Metrics table.
 */
public class MetricsInfo {
	/**
	 * The stock number of this metrics record.
	 */
	private String mStockNumber;
	/**
	 * The start time of this metrics record. This value is set when the
	 * user start the Details activity for a stock.
	 */
	private long mStockStartTime;
	/**
	 * The end time of this metrics record. This value is set when the
	 * user destroys the Details activity for a stock.
	 */
	private long mStockEndTime;
	/**
	 * The number of photos taken of this stock.
	 */
	private int mNumPhotos;
	/**
	 * The number of photos of this stock that the user annotated.
	 */
	private int mNumPhotosAnnotated;
	/**
	 * A flag indicating whether or not an email was sent for this stock.
	 */
	private boolean mIsEmailSent;
	/**
	 * The time that the user most recently started the CSA web activity, or
	 * 0 if they never started it.
	 */
	private long mCsaStartTime;
	/**
	 * The time that the user most recently destroyed the CSA web activity, or
	 * 0 if they never destroyed it.
	 */
	private long mCsaEndTime;
	/**
	 * The time that the user most recently started the map activity, or
	 * 0 if they never started it.
	 */
	private long mMapStartTime;
	/**
	 * The time that the user most recently destroyed the map activity, or
	 * 0 if they never destroyed it.
	 */
	private long mMapEndTime;
	
	/**
	 * Constructor that initializes Metrics values from a Cursor containing them.
	 * Values not contained in the Cursor are set to default values. This method
	 * does not change the Cursor row.
	 * 
	 * @param metricsCursor The Cursor containing Metrics values.
	 */
	public MetricsInfo(Cursor metricsCursor)
	{
		mStockNumber = "";
		mStockStartTime = 0L;
		mStockEndTime = 0L;
		mNumPhotos = 0;
		mNumPhotosAnnotated = 0;
		mIsEmailSent = false;
		mCsaStartTime = 0L;
		mCsaEndTime = 0L;
		mMapStartTime = 0L;
		mMapEndTime = 0L;

		if(metricsCursor == null || metricsCursor.isAfterLast())
			return;

		int colIndex;

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_STOCK_NUMBER);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mStockNumber = metricsCursor.getString(colIndex);

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_STOCK_START_TIME);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mStockStartTime = metricsCursor.getLong(colIndex);

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_STOCK_END_TIME);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mStockEndTime = metricsCursor.getLong(colIndex);

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mNumPhotos = metricsCursor.getInt(colIndex);

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS_ANNOTATED);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mNumPhotosAnnotated = metricsCursor.getInt(colIndex);

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_IS_EMAIL_SENT);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mIsEmailSent = metricsCursor.getInt(colIndex) == 1 ? true : false;

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_CSA_START_TIME);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mCsaStartTime = metricsCursor.getLong(colIndex);

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_CSA_END_TIME);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mCsaEndTime = metricsCursor.getLong(colIndex);

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_MAP_START_TIME);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mMapStartTime = metricsCursor.getLong(colIndex);

		colIndex = metricsCursor.getColumnIndex(OnYard.Metrics.COLUMN_NAME_MAP_END_TIME);
		if(colIndex != -1 && metricsCursor.getString(colIndex) != null)
			mMapEndTime = metricsCursor.getLong(colIndex);
	}

	public String getStockNumber() {
		return mStockNumber;
	}

	/**
	 * Gets total time spent on the stock, in seconds, for this Metrics record.
	 * 
	 * @return The time spent on the stock.
	 */
	public long getTotalStockTime() {
		if(mStockStartTime == 0L || mStockEndTime == 0L)
			return 0;
		else
			return (mStockEndTime - mStockStartTime) / 1000L;
	}

	public int getNumPhotos() {
		return mNumPhotos;
	}

	public int getNumPhotosAnnotated() {
		return mNumPhotosAnnotated;
	}

	public boolean getIsEmailSent() {
		return mIsEmailSent;
	}

	/**
	 * Gets time spent in CSAToday for this stock, in seconds.
	 * 
	 * @return The time spent in CSAToday.
	 */
	public long getCSATime() {
		if(mCsaStartTime == 0L || mCsaEndTime == 0L)
			return 0;
		else
			return (mCsaEndTime - mCsaStartTime) / 1000L;
	}
	
	/**
	 * Gets time spent on the map for this stock, in seconds.
	 * 
	 * @return The time spent on the map.
	 */
	public long getMapTime() {
		if(mMapStartTime == 0L || mMapEndTime == 0L)
			return 0;
		else
			return (mMapEndTime - mMapStartTime) / 1000L;
	}
}
