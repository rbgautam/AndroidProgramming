package com.iaai.onyard.provider;

import com.iaai.onyard.classes.OnYard;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

/**
 * Class that acts as a wrapper around the OnYard database and allows it to exist
 * independently of the OnYard application.
 */
public class OnYardProvider extends ContentProvider {

	/**
	 * The URI of the Content Provider.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + OnYard.AUTHORITY);
	/**
	 * The name of the Content Provider's database.
	 */
	private static final String DATABASE_NAME = "OnYardDB.db";
	/**
	 * The current database version.
	 */
	private static final int DATABASE_VERSION = 2;
	
    /**
     * Code returned when the Vehicles URI is matched.
     */
    private static final int VEHICLES = 1;
    /**
     * Code returned when the Vehicles Stock Number URI is matched.
     */
    private static final int VEHICLE_STOCK_NUMBER = 2;
    /**
     * Code returned when the Color URI is matched.
     */
    private static final int COLOR = 4;
    /**
     * Code returned when the Status URI is matched.
     */
    private static final int STATUS = 5;
    /**
     * Code returned when the Damage URI is matched.
     */
    private static final int DAMAGE = 6;
    /**
     * Code returned when the Config URI is matched.
     */
    private static final int CONFIG = 7;
    /**
     * Code returned when the Config Key URI is matched.
     */
    private static final int CONFIG_KEY = 8;
    /**
     * Code returned when the Metrics URI is matched.
     */
    private static final int METRICS = 9;
	
	/**
	 * The object used to match the incoming URI to its respective code.
	 */
	private static final UriMatcher sUriMatcher;
	/**
	 * The database object used to perform DB operations.
	 */
	private OnYardDatabase mOnYardDB;
	
    static 
    {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(OnYard.AUTHORITY, "vehicles", VEHICLES);
        sUriMatcher.addURI(OnYard.AUTHORITY, "vehicles/stocknumber/*", VEHICLE_STOCK_NUMBER);
        sUriMatcher.addURI(OnYard.AUTHORITY, "color", COLOR);
        sUriMatcher.addURI(OnYard.AUTHORITY, "status", STATUS);
        sUriMatcher.addURI(OnYard.AUTHORITY, "damage", DAMAGE);
        sUriMatcher.addURI(OnYard.AUTHORITY, "config", CONFIG);
        sUriMatcher.addURI(OnYard.AUTHORITY, "config/*", CONFIG_KEY);
        sUriMatcher.addURI(OnYard.AUTHORITY, "metrics", METRICS);
    }
    
	/**
	 * Class that accommodates creation and update of OnYard Database
	 */
	private static class OnYardDatabase extends SQLiteOpenHelper {
		
		/**
		 * Create an OnYardDatabase object.
		 *
		 * @param context The current context.
		 */
		OnYardDatabase(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * Create all OnYard database tables. Called when the database 
		 * is created for the first time.
		 * 
		 * @param db The database.
		 */
		@Override
		public void onCreate(SQLiteDatabase db) 
		{		
			CreateVehiclesTable(db);
			CreateColorTable(db);
			CreateStatusTable(db);
			CreateDamageTable(db);
			CreateConfigTable(db);
			CreateMetricsTable(db);
		}

		/**
		 * Drop and re-create all OnYard database tables except Metrics. 
		 * Called when the database needs to be upgraded.
		 * 
		 * @param db The database.
		 * @param oldVersion The old database version.
		 * @param newVersion The new database version.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL("DROP TABLE IF EXISTS " + OnYard.Vehicles.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + OnYard.Status.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + OnYard.Color.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + OnYard.Damage.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + OnYard.Config.TABLE_NAME);

			CreateVehiclesTable(db);
			CreateColorTable(db);
			CreateStatusTable(db);
			CreateDamageTable(db);
			CreateConfigTable(db);
		}
		
		/**
		 * Create the Metrics table.
		 * 
		 * @param db The database.
		 */
		private void CreateMetricsTable(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE " + OnYard.Metrics.TABLE_NAME + " (" +
					OnYard.Metrics.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					OnYard.Metrics.COLUMN_NAME_STOCK_NUMBER + " INTEGER NOT NULL," +
					OnYard.Metrics.COLUMN_NAME_STOCK_START_TIME + " INTEGER," +
					OnYard.Metrics.COLUMN_NAME_STOCK_END_TIME + " INTEGER," +
					OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS + " INTEGER," +
					OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS_ANNOTATED + " INTEGER," +
					OnYard.Metrics.COLUMN_NAME_IS_EMAIL_SENT + " INTEGER," +
					OnYard.Metrics.COLUMN_NAME_CSA_START_TIME + " INTEGER," +
					OnYard.Metrics.COLUMN_NAME_CSA_END_TIME + " INTEGER," +
					OnYard.Metrics.COLUMN_NAME_MAP_START_TIME + " INTEGER," +
					OnYard.Metrics.COLUMN_NAME_MAP_END_TIME + " INTEGER" +
			");");
		}
		
		/**
		 * Create the Config table.
		 * 
		 * @param db The database.
		 */
		private void CreateConfigTable(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE " + OnYard.Config.TABLE_NAME + " (" +
					OnYard.Config.COLUMN_NAME_KEY + " TEXT PRIMARY KEY NOT NULL COLLATE NOCASE," +
					OnYard.Config.COLUMN_NAME_VALUE + " TEXT NOT NULL" +
			");");
		}
		
		/**
		 * Create the Vehicles table and non-clustered index.
		 * 
		 * @param db The database.
		 */
		private void CreateVehiclesTable(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE " + OnYard.Vehicles.TABLE_NAME + " (" +
					OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " TEXT PRIMARY KEY NOT NULL," +
					OnYard.Vehicles.COLUMN_NAME_VIN + " TEXT TEXT COLLATE NOCASE," +
					OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER + " TEXT COLLATE NOCASE," +
					OnYard.Vehicles.COLUMN_NAME_LATITUDE + " REAL," +
					OnYard.Vehicles.COLUMN_NAME_LONGITUDE + " REAL," +
					OnYard.Vehicles.COLUMN_NAME_AISLE + " TEXT COLLATE NOCASE," +
					OnYard.Vehicles.COLUMN_NAME_STALL + " INTEGER," +
					OnYard.Vehicles.COLUMN_NAME_COLOR + " TEXT," +
					OnYard.Vehicles.COLUMN_NAME_YEAR + " INTEGER," +
					OnYard.Vehicles.COLUMN_NAME_MAKE + " TEXT COLLATE NOCASE," +
					OnYard.Vehicles.COLUMN_NAME_MODEL + " TEXT COLLATE NOCASE," +
					OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER + " TEXT COLLATE NOCASE," +
					OnYard.Vehicles.COLUMN_NAME_STATUS + " TEXT," +
					OnYard.Vehicles.COLUMN_NAME_DAMAGE + " TEXT" +
			");");
		}
		
		/**
		 * Create the Color table.
		 * 
		 * @param db The database.
		 */
		private void CreateColorTable(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE " + OnYard.Color.TABLE_NAME + " (" +
					OnYard.Color.COLUMN_NAME_CODE + " TEXT PRIMARY KEY NOT NULL," +
					OnYard.Color.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL COLLATE NOCASE" +
			");");
		}
		
		/**
		 * Create the Status table.
		 * 
		 * @param db The database.
		 */
		private void CreateStatusTable(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE " + OnYard.Status.TABLE_NAME + " (" +
					OnYard.Status.COLUMN_NAME_CODE + " TEXT PRIMARY KEY NOT NULL," +
					OnYard.Status.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL COLLATE NOCASE" +
			");");
		}
		
		/**
		 * Create the Damage table.
		 * 
		 * @param db The database.
		 */
		private void CreateDamageTable(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE " + OnYard.Damage.TABLE_NAME + " (" +
					OnYard.Damage.COLUMN_NAME_CODE + " TEXT PRIMARY KEY NOT NULL," +
					OnYard.Damage.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL COLLATE NOCASE" +
			");");
		}
	}
	
	/**
	 *
	 * Initializes the provider by creating a new DatabaseHelper. onCreate() is called
	 * automatically when Android creates the provider in response to a resolver request from a
	 * client.
	 */
	@Override
	public boolean onCreate() 
	{
		mOnYardDB = new OnYardDatabase(getContext());

		return true;
	}
	
	/**
	 * Query the OnYard database and return a cursor containing the results.
	 * 
	 * @param uri The URI to query. This will be the full URI sent by the client.
	 * @param projection The list of columns to put into the cursor. If null all columns 
	 * are included.
	 * @param selection A selection criteria to apply when filtering rows. If null then 
	 * all rows are included.
	 * @param selectionArgs You may include ?s in selection, which will be replaced by the 
	 * values from selectionArgs, in order that they appear in the selection. The values will be 
	 * bound as Strings.
	 * @param sortOrder How the rows in the cursor should be sorted. If null then the provider 
	 * is free to define the sort order.
	 * @return A cursor containing the results of the query. The cursor exists but is empty if
	 * the query returns no results or an exception occurs.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) 
	{		
		// Constructs a new query builder
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

		//get joins if querying Vehicles table
		if(sUriMatcher.match(uri) == VEHICLES || 
				sUriMatcher.match(uri) == VEHICLE_STOCK_NUMBER)
		{
			String queryTables = OnYard.Vehicles.TABLE_NAME + " AS v";
			String[] projectCols = projection != null ? projection.clone() : null; 
	
			if (projectCols != null)
			{
				for (int index = 0; index < projectCols.length; index++)
				{
					//if damage code requested, add join with damage and replace with damage description
					if (projectCols[index].equals(OnYard.Vehicles.COLUMN_NAME_DAMAGE))
					{
						queryTables = queryTables.concat(" LEFT JOIN " + OnYard.Damage.TABLE_NAME + " AS d ON v." +
								OnYard.Vehicles.COLUMN_NAME_DAMAGE + " = d." + OnYard.Damage.COLUMN_NAME_CODE);
						projectCols[index] = "d." + OnYard.Damage.COLUMN_NAME_DESCRIPTION + " AS " + 
							OnYard.Vehicles.COLUMN_NAME_DAMAGE;
					}
	
					//if color code requested, add join with color and replace with color description
					if (projectCols[index].equals(OnYard.Vehicles.COLUMN_NAME_COLOR))
					{
						queryTables = queryTables.concat(" LEFT JOIN " + OnYard.Color.TABLE_NAME + " AS c ON v." +
								OnYard.Vehicles.COLUMN_NAME_COLOR + " = c." + OnYard.Color.COLUMN_NAME_CODE);
						projectCols[index] = "c." + OnYard.Color.COLUMN_NAME_DESCRIPTION + " AS " + 
								OnYard.Vehicles.COLUMN_NAME_COLOR;
					}
	
					//if status code requested, add join with status and replace with status description
					if (projectCols[index].equals(OnYard.Vehicles.COLUMN_NAME_STATUS))
					{
						queryTables = queryTables.concat(" LEFT JOIN " + OnYard.Status.TABLE_NAME + " AS s ON v." +
								OnYard.Vehicles.COLUMN_NAME_STATUS + " = s." + OnYard.Status.COLUMN_NAME_CODE);
						projectCols[index] = "s." + OnYard.Status.COLUMN_NAME_DESCRIPTION + " AS " + 
								OnYard.Vehicles.COLUMN_NAME_STATUS;
					}
				}
				projection = projectCols.clone();
			}
			sqlBuilder.setTables(queryTables);
		}
		
		/**
		 * Choose the projection and adjust the "where" clause based on URI pattern-matching.
		 */
		switch (sUriMatcher.match(uri)) 
		{
			case VEHICLES:
				if(projection == null)
					sqlBuilder.setTables(OnYard.Vehicles.TABLE_NAME);
				break;

			case CONFIG:
				sqlBuilder.setTables(OnYard.Config.TABLE_NAME);
				break;
				
			case METRICS:
				sqlBuilder.setTables(OnYard.Metrics.TABLE_NAME);
				break;
				
			case COLOR:
				sqlBuilder.setTables(OnYard.Color.TABLE_NAME);
				break;
				
			case STATUS:
				sqlBuilder.setTables(OnYard.Status.TABLE_NAME);
				break;
				
			case DAMAGE:
				sqlBuilder.setTables(OnYard.Damage.TABLE_NAME);
				break;
				
			case CONFIG_KEY:
				sqlBuilder.setTables(OnYard.Config.TABLE_NAME);
				if(selection != null && selection.length() > 0)
					selection = "(" + selection + ") AND " + OnYard.Config.COLUMN_NAME_KEY +
							"=?";
				else
					selection = OnYard.Config.COLUMN_NAME_KEY + "=?";
				selectionArgs = appendStringToArray(selectionArgs, 
						uri.getPathSegments().get(OnYard.Config.CONFIG_KEY_PATH_POSITION)).clone();
				break;

			case VEHICLE_STOCK_NUMBER:
				if(selection != null && selection.length() > 0)
					selection = "(" + selection + ") AND v." + OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER +
							"=?";
				else
					selection = "v." + OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + "=?";
				selectionArgs = appendStringToArray(selectionArgs, 
						uri.getPathSegments().get(OnYard.Vehicles.VEHICLE_STOCK_NUMBER_PATH_POSITION)).clone();
				break;

			default:
				// If the URI doesn't match any of the known patterns, throw an exception.
				throw new IllegalArgumentException("Unknown URI " + uri);
		}	

		/*
		 * Performs the query. If no problems occur trying to read the database, then a Cursor
		 * object is returned; otherwise, the cursor variable contains null. If no records were
		 * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
		 */
		Cursor c = sqlBuilder.query(
				mOnYardDB.getReadableDatabase(),            // The database to query
				projection,   // The columns to return from the query
				selection,     // The columns for the where clause
				selectionArgs, // The values for the where clause
				null,          // don't group the rows
				null,          // don't filter by row groups
				sortOrder        // The sort order
		);

		// Tells the Cursor what URI to watch, so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	/**
	 * Increase the specified string array length by 1 and append the specified string
	 * to it.
	 * 
	 * @param oldArray The array to be added on to.
	 * @param appendString The string to be added to the array.
	 * @return The lengthened string array with the specified string value as the
	 * last element.
	 */
	private String[] appendStringToArray(String[] oldArray, String appendString) 
	{
		String[] newArray;
		if(oldArray != null)
		{
			newArray = new String[oldArray.length + 1];
			for(int index = 0; index < oldArray.length; index++)
			{
				newArray[index] = oldArray[index];
			}
			newArray[oldArray.length] = appendString;
		}
		else
		{
			newArray = new String[]{ appendString };
		}

		return newArray;
	}

	/**
	 * Returns the MIME data type of the URI given as a parameter.
	 *
	 * @param uri The URI whose MIME type is desired.
	 * @return The MIME type of the URI.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public String getType(Uri uri) 
	{
		switch (sUriMatcher.match(uri))
		{
		case VEHICLES:
			return OnYard.Vehicles.CONTENT_TYPE;
		case VEHICLE_STOCK_NUMBER:
			return OnYard.Vehicles.CONTENT_ITEM_TYPE;
		case CONFIG:
			return OnYard.Config.CONTENT_TYPE;
		case CONFIG_KEY:
			return OnYard.Config.CONTENT_ITEM_TYPE;
		case METRICS:
			return OnYard.Metrics.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	/**
	 * Insert a new row into the database. This method sets up default values for any
	 * columns that are not included in the incoming map.
	 * If rows were inserted, then listeners are notified of the change.
	 * 
	 * @param uri The content:// URI of the insertion request.
	 * @param initialValues A set of column name/value pairs to add to the database.
	 * @return The row ID of the inserted row.
	 * @throws SQLException if the insertion fails.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) 
	{
		ContentValues values;

		if (initialValues != null)
			values = new ContentValues(initialValues);
		else 
			values = new ContentValues();

		String tableName;

		//sets up default values for any columns not included in incoming map
		switch (sUriMatcher.match(uri))
		{
		case VEHICLES:
			values = validateVehiclesValues(values);
			tableName = OnYard.Vehicles.TABLE_NAME;
			break;

		case COLOR:
			values = validateColorValues(values);
			tableName = OnYard.Color.TABLE_NAME;
			break;

		case STATUS:
			values = validateStatusValues(values);
			tableName = OnYard.Status.TABLE_NAME;
			break;

		case DAMAGE:
			values = validateDamageValues(values);
			tableName = OnYard.Damage.TABLE_NAME;
			break;
			
		case CONFIG:
			values = validateConfigValues(values);
			tableName = OnYard.Config.TABLE_NAME;
			break;
			
		case METRICS:
			values = validateMetricsValues(values);
			tableName = OnYard.Metrics.TABLE_NAME;
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Performs the insert and returns the ID of the new record.
		long rowId = mOnYardDB.getWritableDatabase().insert(
				tableName,
				null,
				values
		);

		// If the insert succeeded, the row ID exists.
		if (rowId > 0) 
		{
			// Creates a URI with the vehicle ID pattern and the new row ID appended to it.
//			Uri vehicleUri = ContentUris.withAppendedId(OnYard.Vehicles.CONTENT_ID_URI_BASE, rowId);
			Uri vehicleUri = Uri.parse(OnYard.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE + 
				values.getAsString(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER));

			// Notifies observers registered against this provider that the data changed.
			getContext().getContentResolver().notifyChange(vehicleUri, null, false);
			return vehicleUri;
		}

		// If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
		throw new SQLException("Failed to insert row into " + uri);
	}


	
	/**
	 * Delete records from the database. If the incoming URI matches the vehicle ID URI pattern,
	 * this method deletes the one record specified by the ID in the URI. Otherwise, it deletes a
	 * a set of records. The record or records must also match the input selection criteria
	 * specified by where and whereArgs.
	 * If rows were deleted, then listeners are notified of the change.
	 * 
	 * @param uri The full URI to query, including a row ID (if a specific record is requested).
	 * @param selection An optional restriction to apply to rows when deleting.
	 * @param selectionArgs The values corresponding to ?s in the selection.
	 * @return If a "where" clause is used, the number of rows affected is returned, otherwise
	 * 0 is returned. To delete all rows and get a row count, use "1" as the where clause.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) 
	{
		// Opens the database object in "write" mode.
		SQLiteDatabase db = mOnYardDB.getWritableDatabase();
		int count;

		// Does the delete based on the incoming URI pattern.
		switch (sUriMatcher.match(uri)) 
		{

		// If the incoming pattern matches the general pattern for vehicles, does a delete
		// based on the incoming "where" columns and arguments.
		case VEHICLES:
			count = db.delete(
					OnYard.Vehicles.TABLE_NAME,  // The database table name
					selection,                     // The incoming where clause column names
					selectionArgs                  // The incoming where clause values
			);
			break;

		case COLOR:
			// Does the update and returns the number of rows updated.
			count = db.delete(
					OnYard.Color.TABLE_NAME,  // The database table name
					selection,                     // The incoming where clause column names
					selectionArgs                  // The incoming where clause values
			);
			break;

		case STATUS:
			// Does the update and returns the number of rows updated.
			count = db.delete(
					OnYard.Status.TABLE_NAME,  // The database table name
					selection,                     // The incoming where clause column names
					selectionArgs                  // The incoming where clause values
			);
			break;

		case DAMAGE:
			// Does the update and returns the number of rows updated.
			count = db.delete(
					OnYard.Damage.TABLE_NAME,  // The database table name
					selection,                     // The incoming where clause column names
					selectionArgs                  // The incoming where clause values
			);
			break;
			
		case CONFIG:
			// Does the update and returns the number of rows updated.
			count = db.delete(
					OnYard.Config.TABLE_NAME,  // The database table name
					selection,                     // The incoming where clause column names
					selectionArgs                  // The incoming where clause values
			);
			break;
			
		case METRICS:
			// Does the update and returns the number of rows updated.
			count = db.delete(
					OnYard.Metrics.TABLE_NAME,  // The database table name
					selection,                     // The incoming where clause column names
					selectionArgs                  // The incoming where clause values
			);
			break;
			
		case CONFIG_KEY:
			/*
			 * Starts a final WHERE clause by restricting it to the
			 * desired Config Key.
			 */
			if(selection != null && selection.length() > 0)
				selection = "(" + selection + ") AND " + OnYard.Config.COLUMN_NAME_KEY +
						"=?";
			else
				selection = OnYard.Config.COLUMN_NAME_KEY + "=?";
			
			selectionArgs = appendStringToArray(selectionArgs, 
					uri.getPathSegments().get(OnYard.Config.CONFIG_KEY_PATH_POSITION)).clone();
			
			// Performs the delete.
			count = db.delete(
					OnYard.Config.TABLE_NAME,  // The database table name.
					selection,                // The final WHERE clause
					selectionArgs                  // The incoming where clause values.
			);
			break;

			// If the incoming URI matches a single vehicle ID, does the delete based on the
			// incoming data, but modifies the where clause to restrict it to the
			// particular vehicle ID.
		case VEHICLE_STOCK_NUMBER:
			/*
			 * Starts a final WHERE clause by restricting it to the
			 * desired vehicle Stock Number.
			 */
			if(selection != null && selection.length() > 0)
				selection = "(" + selection + ") AND " + OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER +
						"=?";
			else
				selection = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + "=?";
			
			selectionArgs = appendStringToArray(selectionArgs, 
					uri.getPathSegments().get(OnYard.Vehicles.VEHICLE_STOCK_NUMBER_PATH_POSITION)).clone();

			// Performs the delete.
			count = db.delete(
					OnYard.Vehicles.TABLE_NAME,  // The database table name.
					selection,                // The final WHERE clause
					selectionArgs                  // The incoming where clause values.
			);
			break;

			// If the incoming pattern is invalid, throws an exception.
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		/*Gets a handle to the content resolver object for the current context, and notifies it
		 * that the incoming URI changed. The object passes this along to the resolver framework,
		 * and observers that have registered themselves for the provider are notified.
		 */
		getContext().getContentResolver().notifyChange(uri, null, false);

		// Returns the number of rows deleted.
		return count;
	}

	/**
	 * Update records in the database. The column names specified by the keys in the values map
	 * are updated with new data specified by the values in the map. If the incoming URI matches the
	 * vehicle ID URI pattern, then the method updates the one record specified by the ID in the URI;
	 * otherwise, it updates a set of records. The record or records must match the input
	 * selection criteria specified by selection and selectionArgs.
	 * If rows were updated, then listeners are notified of the change.
	 *
	 * @param uri The URI to query. This can potentially have a record ID if this is an update 
	 * request for a specific record.
	 * @param values A Bundle mapping from column names to new column values (NULL is a valid value).
	 * @param selection An SQL "WHERE" clause that selects records based on their column values. If this
	 * is null, then all records that match the URI pattern are selected.
	 * @param selectionArgs An array of selection criteria. If the "selection" param contains value
	 * placeholders ("?"), then each placeholder is replaced by the corresponding element in the
	 * array.
	 * @return The number of rows updated.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) 
	{
		// Opens the database object in "write" mode.
		SQLiteDatabase db = mOnYardDB.getWritableDatabase();
		int count;

		// Does the update based on the incoming URI pattern
		switch (sUriMatcher.match(uri)) 
		{

		// If the incoming URI matches the general vehicles pattern, does the update based on
		// the incoming data.
		case VEHICLES:
			// Does the update and returns the number of rows updated.
			count = db.update(
					OnYard.Vehicles.TABLE_NAME, // The database table name.
					values,                   // A map of column names and new values to use.
					selection,                    // The where clause column names.
					selectionArgs                 // The where clause column values to select on.
			);
			break;

		case COLOR:
			// Does the update and returns the number of rows updated.
			count = db.update(
					OnYard.Color.TABLE_NAME, // The database table name.
					values,                   // A map of column names and new values to use.
					selection,                    // The where clause column names.
					selectionArgs                 // The where clause column values to select on.
			);
			break;

		case STATUS:
			// Does the update and returns the number of rows updated.
			count = db.update(
					OnYard.Status.TABLE_NAME, // The database table name.
					values,                   // A map of column names and new values to use.
					selection,                    // The where clause column names.
					selectionArgs                 // The where clause column values to select on.
			);
			break;

		case DAMAGE:
			// Does the update and returns the number of rows updated.
			count = db.update(
					OnYard.Damage.TABLE_NAME, // The database table name.
					values,                   // A map of column names and new values to use.
					selection,                    // The where clause column names.
					selectionArgs                 // The where clause column values to select on.
			);
			break;
			
		case CONFIG:
			// Does the update and returns the number of rows updated.
			count = db.update(
					OnYard.Config.TABLE_NAME, // The database table name.
					values,                   // A map of column names and new values to use.
					selection,                    // The where clause column names.
					selectionArgs                 // The where clause column values to select on.
			);
			break;
			
		case METRICS:
			// Does the update and returns the number of rows updated.
			count = db.update(
					OnYard.Metrics.TABLE_NAME, // The database table name.
					values,                   // A map of column names and new values to use.
					selection,                    // The where clause column names.
					selectionArgs                 // The where clause column values to select on.
			);
			break;
			
		case CONFIG_KEY:
			/*
			 * Starts creating the final WHERE clause by restricting it to the incoming
			 * config Key.
			 */			
			if(selection != null && selection.length() > 0)
				selection = "(" + selection + ") AND " + OnYard.Config.COLUMN_NAME_KEY +
						"=?";
			else
				selection = OnYard.Config.COLUMN_NAME_KEY + "=?";
			
			selectionArgs = appendStringToArray(selectionArgs, 
					uri.getPathSegments().get(OnYard.Config.CONFIG_KEY_PATH_POSITION)).clone();

			// Does the update and returns the number of rows updated.
			count = db.update(
					OnYard.Config.TABLE_NAME, // The database table name.
					values,                   // A map of column names and new values to use.
					selection,               // The final WHERE clause to use
					selectionArgs                 // The where clause column values to select on, or
					// null if the values are in the where argument.
			);
			break;
			
			// If the incoming URI matches a single vehicle ID, does the update based on the incoming
			// data, but modifies the where clause to restrict it to the particular vehicle ID.
		case VEHICLE_STOCK_NUMBER:
			/*
			 * Starts creating the final WHERE clause by restricting it to the incoming
			 * vehicle Stock Number.
			 */			
			if(selection != null && selection.length() > 0)
				selection = "(" + selection + ") AND " + OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER +
						"=?";
			else
				selection = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + "=?";
			
			selectionArgs = appendStringToArray(selectionArgs, 
					uri.getPathSegments().get(OnYard.Vehicles.VEHICLE_STOCK_NUMBER_PATH_POSITION)).clone();

			// Does the update and returns the number of rows updated.
			count = db.update(
					OnYard.Vehicles.TABLE_NAME, // The database table name.
					values,                   // A map of column names and new values to use.
					selection,               // The final WHERE clause to use
					selectionArgs                 // The where clause column values to select on, or
					// null if the values are in the where argument.
			);
			break;
			// If the incoming pattern is invalid, throws an exception.
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		/*Gets a handle to the content resolver object for the current context, and notifies it
		 * that the incoming URI changed. The object passes this along to the resolver framework,
		 * and observers that have registered themselves for the provider are notified.
		 */
		getContext().getContentResolver().notifyChange(uri, null, false);

		// Returns the number of rows updated.
		return count;
	}
	
	/**
	 * Insert a set of new rows. This method uses a precompiled SQL statement and iterates over
	 * each ContentValues bundle to increase insert efficiency.
	 *
	 * @param uri The content:// URI of the insertion request.
	 * @param values An array of sets of column name/value pairs to add to the database.
	 * @return The number of values that were inserted. 
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values)
	{
		SQLiteDatabase db = mOnYardDB.getWritableDatabase();
		SQLiteStatement insert = null;
		int numInserted= 0;
		
		switch(sUriMatcher.match(uri))
		{
		case VEHICLES:
			db.beginTransaction();
			try 
			{
				String str = null;
				insert =
					db.compileStatement("insert into " + OnYard.Vehicles.TABLE_NAME + 
							"(" + OnYard.Vehicles.COLUMN_NAME_AISLE + 
							"," + OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER + 
							"," + OnYard.Vehicles.COLUMN_NAME_COLOR + 
							"," + OnYard.Vehicles.COLUMN_NAME_DAMAGE + 
							"," + OnYard.Vehicles.COLUMN_NAME_LATITUDE + 
							"," + OnYard.Vehicles.COLUMN_NAME_LONGITUDE + 
							"," + OnYard.Vehicles.COLUMN_NAME_MAKE + 
							"," + OnYard.Vehicles.COLUMN_NAME_MODEL + 
							"," + OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER + 
							"," + OnYard.Vehicles.COLUMN_NAME_STALL + 
							"," + OnYard.Vehicles.COLUMN_NAME_STATUS + 
							"," + OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + 
							"," + OnYard.Vehicles.COLUMN_NAME_VIN + 
							"," + OnYard.Vehicles.COLUMN_NAME_YEAR + ")" +
							" values " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

				for (ContentValues value : values)
				{
					value = validateVehiclesValues(value);
					
					str = (String) value.get(OnYard.Vehicles.COLUMN_NAME_AISLE);
					if (str == null)
						insert.bindNull(1);
					else
						insert.bindString(1, str);
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER);
					if (str == null)
						insert.bindNull(2);
					else
						insert.bindString(2, str);
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_COLOR);
					if (str == null)
						insert.bindNull(3);
					else
						insert.bindString(3, str);
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_DAMAGE);
					if (str == null)
						insert.bindNull(4);
					else
						insert.bindString(4, str);
					
					insert.bindDouble(5, value.getAsFloat(OnYard.Vehicles.COLUMN_NAME_LATITUDE));
					insert.bindDouble(6, value.getAsFloat(OnYard.Vehicles.COLUMN_NAME_LONGITUDE));
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_MAKE);
					if (str == null)
						insert.bindNull(7);
					else
						insert.bindString(7, str);
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_MODEL);
					if (str == null)
						insert.bindNull(8);
					else
						insert.bindString(8, str);
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER);
					if (str == null)
						insert.bindNull(9);
					else
						insert.bindString(9, str);
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_STALL);
					if (str == null)
						insert.bindNull(10);
					else
						insert.bindString(10, str);
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_STATUS);
					if (str == null)
						insert.bindNull(11);
					else
						insert.bindString(11, str);
					
					insert.bindString(12, value.getAsString(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER));
					
					str = (String) value.getAsString(OnYard.Vehicles.COLUMN_NAME_VIN);
					if (str == null)
						insert.bindNull(13);
					else
						insert.bindString(13, str);
					
					insert.bindLong(14, value.getAsInteger(OnYard.Vehicles.COLUMN_NAME_YEAR));
					insert.execute();
				}
				db.setTransactionSuccessful();
				numInserted = values.length;
			} 
			finally 
			{
				db.endTransaction();
				insert.close();
			}
			break;
			
		case COLOR:
			db.beginTransaction();
			try 
			{
				insert =
					db.compileStatement("insert into " + OnYard.Color.TABLE_NAME + 
							"(" + OnYard.Color.COLUMN_NAME_CODE + 
							"," + OnYard.Color.COLUMN_NAME_DESCRIPTION + ")" +
							" values " + "(?,?);");

				for (ContentValues value : values)
				{
					value = validateColorValues(value);
					insert.bindString(1, value.getAsString(OnYard.Color.COLUMN_NAME_CODE));
					insert.bindString(2, value.getAsString(OnYard.Color.COLUMN_NAME_DESCRIPTION));
					insert.execute();
				}
				db.setTransactionSuccessful();
				numInserted = values.length;
			} 
			finally 
			{
				db.endTransaction();
                insert.close();
			}
			break;
			
		case STATUS:
			db.beginTransaction();
			try 
			{
				insert =
					db.compileStatement("insert into " + OnYard.Status.TABLE_NAME + 
							"(" + OnYard.Status.COLUMN_NAME_CODE + 
							"," + OnYard.Status.COLUMN_NAME_DESCRIPTION + ")" +
							" values " + "(?,?);");

				for (ContentValues value : values)
				{
					value = validateStatusValues(value);
					insert.bindString(1, value.getAsString(OnYard.Status.COLUMN_NAME_CODE));
					insert.bindString(2, value.getAsString(OnYard.Status.COLUMN_NAME_DESCRIPTION));
					insert.execute();
				}
				db.setTransactionSuccessful();
				numInserted = values.length;
			} 
			finally 
			{
				db.endTransaction();
                insert.close();
			}
			break;
			
		case DAMAGE:
			db.beginTransaction();
			try 
			{
				insert =
					db.compileStatement("insert into " + OnYard.Damage.TABLE_NAME + 
							"(" + OnYard.Damage.COLUMN_NAME_CODE + 
							"," + OnYard.Damage.COLUMN_NAME_DESCRIPTION + ")" +
							" values " + "(?,?);");

				for (ContentValues value : values)
				{
					value = validateDamageValues(value);
					insert.bindString(1, value.getAsString(OnYard.Damage.COLUMN_NAME_CODE));
					insert.bindString(2, value.getAsString(OnYard.Damage.COLUMN_NAME_DESCRIPTION));
					insert.execute();
				}
				db.setTransactionSuccessful();
				numInserted = values.length;
			} 
			finally 
			{
				db.endTransaction();
                insert.close();
			}
			break;
			
		default:
			throw new UnsupportedOperationException("unsupported uri: " + uri);
		}
		
		return numInserted;
	}
	
	/**
	 * Validates a Vehicles ContentValues bundle by checking for each column. If a
	 * column does not exist in the bundle, it is added with a default value.
	 * 
	 * @param values The column name/value pairs being validated.
	 * @return The ContentValues bundle with missing columns added.
	 * @throws IllegalArgumentException if required fields are missing from the
	 * ContentValues bundle.
	 */
	private ContentValues validateVehiclesValues(ContentValues values) 
	{
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER) == false) {
			throw new IllegalArgumentException("Stock Number is a required field");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_VIN) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_VIN, "");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER, "");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_LATITUDE) == false) {
			values.putNull(OnYard.Vehicles.COLUMN_NAME_LATITUDE);
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_LONGITUDE) == false) {
			values.putNull(OnYard.Vehicles.COLUMN_NAME_LONGITUDE);
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_AISLE) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_AISLE, "");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_STALL) == false) {
			values.putNull(OnYard.Vehicles.COLUMN_NAME_STALL);
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_COLOR) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_COLOR, "");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_YEAR) == false) {
			values.putNull(OnYard.Vehicles.COLUMN_NAME_YEAR);
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_MAKE) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_MAKE, "");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_MODEL) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_MODEL, "");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER, "");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_STATUS) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_STATUS, "");
		}
		if (values.containsKey(OnYard.Vehicles.COLUMN_NAME_DAMAGE) == false) {
			values.put(OnYard.Vehicles.COLUMN_NAME_DAMAGE, "");
		}
		
		return values;
	}
	
	/**
	 * Validates a Metrics ContentValues bundle by checking for each column. If a
	 * column does not exist in the bundle, it is added with a default value.
	 * 
	 * @param values The column name/value pairs being validated.
	 * @return The ContentValues bundle with missing columns added.
	 * @throws IllegalArgumentException if required fields are missing from the
	 * ContentValues bundle.
	 */
	private ContentValues validateMetricsValues(ContentValues values) 
	{
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_STOCK_NUMBER) == false) {
			throw new IllegalArgumentException("Stock Number is a required field");
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_STOCK_START_TIME) == false) {
			values.putNull(OnYard.Metrics.COLUMN_NAME_STOCK_START_TIME);
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_STOCK_END_TIME) == false) {
			values.putNull(OnYard.Metrics.COLUMN_NAME_STOCK_END_TIME);
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS) == false) {
			values.put(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS, 0);
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS_ANNOTATED) == false) {
			values.put(OnYard.Metrics.COLUMN_NAME_NUM_PHOTOS_ANNOTATED, 0);
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_IS_EMAIL_SENT) == false) {
			values.put(OnYard.Metrics.COLUMN_NAME_IS_EMAIL_SENT, 0);
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_CSA_START_TIME) == false) {
			values.putNull(OnYard.Metrics.COLUMN_NAME_CSA_START_TIME);
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_CSA_END_TIME) == false) {
			values.putNull(OnYard.Metrics.COLUMN_NAME_CSA_END_TIME);
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_MAP_START_TIME) == false) {
			values.putNull(OnYard.Metrics.COLUMN_NAME_MAP_START_TIME);
		}
		if (values.containsKey(OnYard.Metrics.COLUMN_NAME_MAP_END_TIME) == false) {
			values.putNull(OnYard.Metrics.COLUMN_NAME_MAP_END_TIME);
		}
		
		return values;
	}

	/**
	 * Validates a Color ContentValues bundle by checking for each column. If a
	 * column does not exist in the bundle, it is added with a default value.
	 * 
	 * @param values The column name/value pairs being validated.
	 * @return The ContentValues bundle with missing columns added.
	 * @throws IllegalArgumentException if required fields are missing from the
	 * ContentValues bundle.
	 */
	private ContentValues validateColorValues(ContentValues values) 
	{
		if (values.containsKey(OnYard.Color.COLUMN_NAME_CODE) == false) {
			throw new IllegalArgumentException("Color Code is a required field");
		}
		if (values.containsKey(OnYard.Color.COLUMN_NAME_DESCRIPTION) == false) {
			throw new IllegalArgumentException("Color Description is a required field");
		}
		
		return values;
	}
	
	/**
	 * Validates a Status ContentValues bundle by checking for each column. If a
	 * column does not exist in the bundle, it is added with a default value.
	 * 
	 * @param values The column name/value pairs being validated.
	 * @return The ContentValues bundle with missing columns added.
	 * @throws IllegalArgumentException if required fields are missing from the
	 * ContentValues bundle.
	 */
	private ContentValues validateStatusValues(ContentValues values) 
	{
		if (values.containsKey(OnYard.Status.COLUMN_NAME_CODE) == false) {
			throw new IllegalArgumentException("Status Code is a required field");
		}
		if (values.containsKey(OnYard.Status.COLUMN_NAME_DESCRIPTION) == false) {
			throw new IllegalArgumentException("Status Description is a required field");
		}
		
		return values;
	}
	
	/**
	 * Validates a Damage ContentValues bundle by checking for each column. If a
	 * column does not exist in the bundle, it is added with a default value.
	 * 
	 * @param values The column name/value pairs being validated.
	 * @return The ContentValues bundle with missing columns added.
	 * @throws IllegalArgumentException if required fields are missing from the
	 * ContentValues bundle.
	 */
	private ContentValues validateDamageValues(ContentValues values) 
	{
		if (values.containsKey(OnYard.Damage.COLUMN_NAME_CODE) == false) {
			throw new IllegalArgumentException("Damage Code is a required field");
		}
		if (values.containsKey(OnYard.Damage.COLUMN_NAME_DESCRIPTION) == false) {
			throw new IllegalArgumentException("Damage Description is a required field");
		}
		
		return values;
	}
	
	/**
	 * Validates a Config ContentValues bundle by checking for each column. If a
	 * column does not exist in the bundle, it is added with a default value.
	 * 
	 * @param values The column name/value pairs being validated.
	 * @return The ContentValues bundle with missing columns added.
	 * @throws IllegalArgumentException if required fields are missing from the
	 * ContentValues bundle.
	 */
	private ContentValues validateConfigValues(ContentValues values) 
	{
		if (values.containsKey(OnYard.Config.COLUMN_NAME_KEY) == false) {
			throw new IllegalArgumentException("Config Key is a required field");
		}
		if (values.containsKey(OnYard.Config.COLUMN_NAME_VALUE) == false) {
			values.putNull(OnYard.Config.COLUMN_NAME_VALUE);
		}
		
		return values;
	}
}
