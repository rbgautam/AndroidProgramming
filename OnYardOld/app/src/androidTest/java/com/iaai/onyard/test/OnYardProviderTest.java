package com.iaai.onyard.test;

import com.iaai.onyard.classes.ColorInfo;
import com.iaai.onyard.classes.DamageInfo;
import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.classes.StatusInfo;
import com.iaai.onyard.classes.VehicleInfo;
import com.iaai.onyard.provider.OnYardProvider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

public class OnYardProviderTest extends ProviderTestCase2<OnYardProvider> {

    private static final Uri INVALID_URI =
        Uri.withAppendedPath(OnYard.Vehicles.CONTENT_URI, "invalid");
    private MockContentResolver mMockResolver; 

    private final VehicleInfo[] TEST_VEHICLES = {
        new VehicleInfo("Stock0", "VIN0", "Claim0", 10, 20, "A", 30, "Col0", 40, "Make0", "Model0", "SP0", "St0", "D0"),
        new VehicleInfo("Stock1", "VIN1", "Claim1", 11, 21, "B", 31, "Col1", 41, "Make1", "Model1", "SP1", "St1", "D1"),
        new VehicleInfo("Stock2", "VIN2", "Claim2", 12, 22, "C", 32, "Col2", 42, "Make2", "Model2", "SP2", "St2", "D2"),
        new VehicleInfo("Stock3", "VIN3", "Claim3", 13, 23, "D", 33, "Col3", 43, "Make3", "Model3", "SP3", "St3", "D3"),
        new VehicleInfo("Stock4", "VIN4", "Claim4", 14, 24, "E", 34, "Col4", 44, "Make4", "Model4", "SP4", "St4", "D4"),
        new VehicleInfo("Stock5", "VIN5", "Claim5", 15, 25, "F", 35, "Col5", 45, "Make5", "Model5", "SP5", "St5", "D5"),
        new VehicleInfo("Stock6", "VIN6", "Claim6", 16, 26, "G", 36, "Col6", 46, "Make6", "Model6", "SP6", "St6", "D6"),
        new VehicleInfo("Stock7", "VIN7", "Claim7", 17, 27, "H", 37, "Col7", 47, "Make7", "Model7", "SP7", "St7", "D7"),
        new VehicleInfo("", "", "", 8, 8, "", 8, "", 8, "", "", "", "", ""),
        new VehicleInfo("Stock8", null, null, 0, 0, null, 0, null, 0, null, null, null, null, null) };
    
    private final StatusInfo[] TEST_STATUS = {
            new StatusInfo("St0", "StDesc0"),
            new StatusInfo("St1", "StDesc1"),
            new StatusInfo("St2", "StDesc2"),
            new StatusInfo("St3", "StDesc3"),
            new StatusInfo("St4", "StDesc4")};
    
    private final ColorInfo[] TEST_COLOR = {
            new ColorInfo("Col0", "ColDesc0"),
            new ColorInfo("Col1", "ColDesc1"),
            new ColorInfo("Col2", "ColDesc2"),
            new ColorInfo("Col3", "ColDesc3"),
            new ColorInfo("Col4", "ColDesc4")};
    
    private final DamageInfo[] TEST_DAMAGE = {
            new DamageInfo("D0", "DDesc0"),
            new DamageInfo("D1", "DDesc1"),
            new DamageInfo("D2", "DDesc2"),
            new DamageInfo("D3", "DDesc3"),
            new DamageInfo("D4", "DDesc4")};

	public OnYardProviderTest(Class<OnYardProvider> providerClass,
			String providerAuthority) {
		super(OnYardProvider.class, OnYard.AUTHORITY);
	}
	
	public OnYardProviderTest() {
		super(OnYardProvider.class, OnYard.AUTHORITY);
	}
	
    /*
     * Sets up the test environment before each test method. Creates a mock content resolver,
     * gets the provider under test, and creates a new database for the provider.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mMockResolver = getMockContentResolver();
    }

    /*
     *  This method is called after each test method, to clean up the current fixture. Since
     *  the test case runs in an isolated context, no cleanup is necessary.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /*
     * Sets up test data.
     * The test data is in an SQL database. It is created in setUp() without any data,
     * and populated in insertData if necessary.
     */
    private void insertData() {
        ContentValues[] vehicleValues = new ContentValues[TEST_VEHICLES.length];
        ContentValues[] colorValues = new ContentValues[TEST_COLOR.length];
        ContentValues[] statusValues = new ContentValues[TEST_STATUS.length];
        ContentValues[] damageValues = new ContentValues[TEST_DAMAGE.length];
        
        for (int index = 0; index < TEST_VEHICLES.length; index++) 
        {
        	vehicleValues[index] = TEST_VEHICLES[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYard.Vehicles.CONTENT_URI, vehicleValues);
        
        for (int index = 0; index < TEST_STATUS.length; index++) 
        {
        	statusValues[index] = TEST_STATUS[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYard.Status.CONTENT_URI, statusValues);
        
        for (int index = 0; index < TEST_COLOR.length; index++) 
        {
        	colorValues[index] = TEST_COLOR[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYard.Color.CONTENT_URI, colorValues);
        
        for (int index = 0; index < TEST_DAMAGE.length; index++) 
        {
        	damageValues[index] = TEST_DAMAGE[index].getContentValues();
        }
        mMockResolver.bulkInsert(OnYard.Damage.CONTENT_URI, damageValues);
    }
    
    /*
     * Tests the provider's publicly available URIs. If the URI is not one that the provider
     * understands, the provider should throw an exception. It also tests the provider's getType()
     * method for each URI, which should return the MIME type associated with the URI.
     */
    public void testUriAndGetType() {
        String mimeType = mMockResolver.getType(OnYard.Vehicles.CONTENT_URI);
        assertEquals(OnYard.Vehicles.CONTENT_TYPE, mimeType);

        Uri vehicleIdUri = ContentUris.withAppendedId(OnYard.Vehicles.CONTENT_ID_URI_BASE, 1);

        mimeType = mMockResolver.getType(vehicleIdUri);
        assertEquals(OnYard.Vehicles.CONTENT_ITEM_TYPE, mimeType);

        mimeType = mMockResolver.getType(INVALID_URI);
    }
    
    /*
     * Tests the provider's public API for querying data in the table, using the URI for
     * a dataset of records.
     */
    public void testQueriesOnVehiclesUri() {
        // Defines a projection of column names to return for a query - everything except lat and long
        final String[] TEST_PROJECTION = {
        		OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER,
        		OnYard.Vehicles.COLUMN_NAME_VIN,
        		OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER,
        		OnYard.Vehicles.COLUMN_NAME_AISLE,
        		OnYard.Vehicles.COLUMN_NAME_STALL,
        		OnYard.Vehicles.COLUMN_NAME_COLOR,
        		OnYard.Vehicles.COLUMN_NAME_YEAR,
        		OnYard.Vehicles.COLUMN_NAME_MAKE,
        		OnYard.Vehicles.COLUMN_NAME_MODEL,
        		OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
        		OnYard.Vehicles.COLUMN_NAME_STATUS,
        		OnYard.Vehicles.COLUMN_NAME_DAMAGE
        };

        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String STOCK_NUMBER_SELECTION = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = " + "?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS =
        	STOCK_NUMBER_SELECTION + " OR " + STOCK_NUMBER_SELECTION + " OR " + STOCK_NUMBER_SELECTION;

         // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "Stock8", "Stock5", "Stock0" };

         // Defines a query sort order
        final String SORT_ORDER = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " DESC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(
        	OnYard.Vehicles.CONTENT_URI,  // the URI for the main data table
            null,                       // no projection, get all columns
            null,                       // no selection criteria, get all records
            null,                       // no selection arguments
            null                        // use default sort order
        );

         // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

         // Query subtest 2.
         // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(
        	OnYard.Vehicles.CONTENT_URI,  // the URI for the main data table
            null,                       // no projection, get all columns
            null,                       // no selection criteria, get all records
            null,                       // no selection arguments
            null                        // use default sort order
        );

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_VEHICLES.length, cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(
        	  OnYard.Vehicles.CONTENT_URI,  // the URI for the main data table
              TEST_PROJECTION,            // get all columns except id, lat, and long
              null,                       // no selection columns, get all the records
              null,                       // no selection criteria
              null                        // use default sort order
        );

        // Asserts that the number of columns in the cursor is the same as in the projection
        assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
        assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(
        	OnYard.Vehicles.CONTENT_URI, // the URI for the main data table
            TEST_PROJECTION,           // get all columns except id, lat, and long
            SELECTION_COLUMNS,         // select on the stock number column
            SELECTION_ARGS,            // select stock numbers "Stock8", "Stock5", or "Stock0"
            SORT_ORDER                 // sort descending on the stock number column
        );

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) {

            // Asserts that the selection argument at the current index matches the value of
            // the stock number column (column 0) in the current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(0));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }
    
    /*
     * Tests the provider's public API for querying data in the table, using the URI for
     * a dataset of records.
     */
    public void testJoinQueriesOnVehiclesUri() {
        // Defines a projection of column names to return for a query - stock number and join columns
        final String[] TEST_PROJECTION = {
        		OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER,
        		OnYard.Vehicles.COLUMN_NAME_DAMAGE,
        		OnYard.Vehicles.COLUMN_NAME_COLOR,
        		OnYard.Vehicles.COLUMN_NAME_STATUS
        };

        // Defines a selection column for the query. When the selection columns are passed
        // to the query, the selection arguments replace the placeholders.
        final String STOCK_NUMBER_SELECTION = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = " + "?";

        // Defines the selection columns for a query.
        final String SELECTION_COLUMNS =
        	STOCK_NUMBER_SELECTION + " OR " + STOCK_NUMBER_SELECTION + " OR " + STOCK_NUMBER_SELECTION;

         // Defines the arguments for the selection columns.
        final String[] SELECTION_ARGS = { "Stock4", "Stock2", "Stock0" };
        
        // Defines the arguments for the corresponding joined description columns
        final String[] STATUS_ARGS = { "StDesc4", "StDesc2", "StDesc0" };
        final String[] COLOR_ARGS = { "ColDesc4", "ColDesc2", "ColDesc0" };
        final String[] DAMAGE_ARGS = { "DDesc4", "DDesc2", "DDesc0" };

         // Defines a query sort order
        final String SORT_ORDER = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " DESC";

        // Query subtest 1.
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(
        	OnYard.Vehicles.CONTENT_URI,  // the URI for the main data table
            null,                       // no projection, get all columns
            null,                       // no selection criteria, get all records
            null,                       // no selection arguments
            null                        // use default sort order
        );

         // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

         // Query subtest 2.
         // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(
        	OnYard.Vehicles.CONTENT_URI,  // the URI for the main data table
            null,                       // no projection, get all columns
            null,                       // no selection criteria, get all records
            null,                       // no selection arguments
            null                        // use default sort order
        );

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_VEHICLES.length, cursor.getCount());

        // Query subtest 3.
        // A query that uses a projection should return a cursor with the same number of columns
        // as the projection, with the same names, in the same order.
        Cursor projectionCursor = mMockResolver.query(
        	  OnYard.Vehicles.CONTENT_URI,  // the URI for the main data table
              TEST_PROJECTION,            // get stock number and join columns
              null,                       // no selection columns, get all the records
              null,                       // no selection criteria
              null                        // use default sort order
        );

        // Asserts that the number of columns in the cursor is the same as in the projection
        assertEquals(TEST_PROJECTION.length, projectionCursor.getColumnCount());

        // Asserts that the names of the columns in the cursor and in the projection are the same.
        // This also verifies that the names are in the same order.
        assertEquals(TEST_PROJECTION[0], projectionCursor.getColumnName(0));
        assertEquals(TEST_PROJECTION[1], projectionCursor.getColumnName(1));
        assertEquals(TEST_PROJECTION[2], projectionCursor.getColumnName(2));

        // Query subtest 4
        // A query that uses selection criteria should return only those rows that match the
        // criteria. Use a projection so that it's easy to get the data in a particular column.
        projectionCursor = mMockResolver.query(
        	OnYard.Vehicles.CONTENT_URI, // the URI for the main data table
            TEST_PROJECTION,           // get stock number and join columns
            SELECTION_COLUMNS,         // select on the stock number column
            SELECTION_ARGS,            // select stock numbers "Stock4", "Stock2", or "Stock0"
            SORT_ORDER                 // sort descending on the stock number column
        );

        // Asserts that the cursor has the same number of rows as the number of selection arguments
        assertEquals(SELECTION_ARGS.length, projectionCursor.getCount());

        int index = 0;

        while (projectionCursor.moveToNext()) 
        {
            // Asserts that the selection argument at the current index matches the value of
            // the columns in the current record of the cursor
            assertEquals(SELECTION_ARGS[index], projectionCursor.getString(0));
            assertEquals(DAMAGE_ARGS[index], projectionCursor.getString(1));
            assertEquals(COLOR_ARGS[index], projectionCursor.getString(2));
            assertEquals(STATUS_ARGS[index], projectionCursor.getString(3));

            index++;
        }

        // Asserts that the index pointer is now the same as the number of selection arguments, so
        // that the number of arguments tested is exactly the same as the number of rows returned.
        assertEquals(SELECTION_ARGS.length, index);
    }
    
    /*
     * Tests queries against the provider, using the vehicle id URI. This URI encodes a single
     * record ID. The provider should only return 0 or 1 record.
     */
    public void testQueriesOnVehicleIdUri() {
      // Defines the selection column for a query. The "?" is replaced by entries in the
      // selection argument array
      final String SELECTION_COLUMNS = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = " + "?";

      // Defines the argument for the selection column.
      
      final String[] SELECTION_ARGS = { "Stock1" };

      // A sort order for the query.
      final String SORT_ORDER = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " ASC";

      // Creates a projection includes the vehicle id column, so that vehicle id can be retrieved.
      final String[] VEHICLE_ID_PROJECTION = {
    		  OnYard.Vehicles._ID,                 // The Vehicles class extends BaseColumns,
                                              // which includes _ID as the column name for the
                                              // record's id in the data model
    		  OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER};  // The vehicle's stock number

      // Query subtest 1.
      // Tests that a query against an empty table returns null.

      // Constructs a URI that matches the provider's vehicles id URI pattern, using an arbitrary
      // value of 1 as the vehicle ID.
      Uri vehicleIdUri = ContentUris.withAppendedId(OnYard.Vehicles.CONTENT_ID_URI_BASE, 1);

      // Queries the table with the vehicles ID URI. This should return an empty cursor.
      Cursor cursor = mMockResolver.query(
    	  vehicleIdUri, // URI pointing to a single record
          null,      // no projection, get all the columns for each record
          null,      // no selection criteria, get all the records in the table
          null,      // no need for selection arguments
          null       // default sort, by ascending stock number
      );

      // Asserts that the cursor is null.
      assertEquals(0,cursor.getCount());

      // Query subtest 2.
      // Tests that a query against a table containing records returns a single record whose ID
      // is the one requested in the URI provided.

      // Inserts the test data into the provider's underlying data source.
      insertData();

      // Queries the table using the URI for the full table.
      cursor = mMockResolver.query(
    	  OnYard.Vehicles.CONTENT_URI, // the base URI for the table
          VEHICLE_ID_PROJECTION,        // returns the ID and stock number columns of rows
          SELECTION_COLUMNS,         // select based on the stock number column
          SELECTION_ARGS,            // select stock of "Stock1"
          SORT_ORDER                 // sort order returned is by stock number, ascending
      );

      // Asserts that the cursor contains only one row.
      assertEquals(1, cursor.getCount());

      // Moves to the cursor's first row, and asserts that this did not fail.
      assertTrue(cursor.moveToFirst());

      // Saves the record's vehicle ID.
      int inputVehicleId = cursor.getInt(0);

      // Builds a URI based on the provider's content ID URI base and the saved vehicle ID.
      vehicleIdUri = ContentUris.withAppendedId(OnYard.Vehicles.CONTENT_ID_URI_BASE, inputVehicleId);

      // Queries the table using the content ID URI, which returns a single record with the
      // specified vehicle ID, matching the selection criteria provided.
      cursor = mMockResolver.query(vehicleIdUri, // the URI for a single vehicle
          VEHICLE_ID_PROJECTION,                 // same projection, get ID and stock number columns
          SELECTION_COLUMNS,                  // same selection, based on stock number column
          SELECTION_ARGS,                     // same selection arguments, stock number = "Stock1"
          SORT_ORDER                          // same sort order returned, by stock number, ascending
      );

      // Asserts that the cursor contains only one row.
      assertEquals(1, cursor.getCount());

      // Moves to the cursor's first row, and asserts that this did not fail.
      assertTrue(cursor.moveToFirst());

      // Asserts that the vehicle ID passed to the provider is the same as the vehicle ID returned.
      assertEquals(inputVehicleId, cursor.getInt(0));
    }
    
    /*
     * Tests queries against the provider, using the vehicle id URI. This URI encodes a single
     * record ID. The provider should only return 0 or 1 record.
     */
    public void testQueriesOnVehicleStockNumUri() {
      // Defines the selection column for a query. The "?" is replaced by entries in the
      // selection argument array
      final String SELECTION_COLUMNS = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = " + "?";

      // Defines the argument for the selection column.
      final String[] SELECTION_ARGS = { "Stock6" };

      // A sort order for the query.
      final String SORT_ORDER = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " ASC";

      // Creates a projection including the vehicle id column, so that vehicle stock number can be retrieved.
      final String[] VEHICLE_ID_PROJECTION = {
      		OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER,
      		OnYard.Vehicles.COLUMN_NAME_VIN,
      		OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER,
      		OnYard.Vehicles.COLUMN_NAME_AISLE,
      		OnYard.Vehicles.COLUMN_NAME_STALL,
      		OnYard.Vehicles.COLUMN_NAME_COLOR,
      		OnYard.Vehicles.COLUMN_NAME_YEAR,
      		OnYard.Vehicles.COLUMN_NAME_MAKE,
      		OnYard.Vehicles.COLUMN_NAME_MODEL,
      		OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
      		OnYard.Vehicles.COLUMN_NAME_STATUS,
      		OnYard.Vehicles.COLUMN_NAME_DAMAGE
      };

      // Query subtest 1.
      // Tests that a query against an empty table returns null.

      // Constructs a URI that matches the provider's vehicles id URI pattern, using an arbitrary
      // value of Stock1 as the vehicle stock number.
      Uri vehicleIdUri = Uri.withAppendedPath(OnYard.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, "Stock6");

      // Queries the table with the vehicles ID URI. This should return an empty cursor.
      Cursor cursor1 = mMockResolver.query(
    	  vehicleIdUri, // URI pointing to a single record
          null,      // no projection, get all the columns for each record
          null,      // no selection criteria, get all the records in the table
          null,      // no need for selection arguments
          null       // default sort, by ascending stock number
      );

      // Asserts that the cursor is null.
      assertEquals(0,cursor1.getCount());

      // Query subtest 2.
      // Tests that a query against a table containing records returns a single record whose ID
      // is the one requested in the URI provided.

      // Inserts the test data into the provider's underlying data source.
      insertData();

      // Queries the table using the URI for the full table.
      cursor1 = mMockResolver.query(
    	  OnYard.Vehicles.CONTENT_URI, // the base URI for the table
          VEHICLE_ID_PROJECTION,        // returns the ID and stock number columns of rows
          SELECTION_COLUMNS,         // select based on the stock number column
          SELECTION_ARGS,            // select stock of "Stock1"
          SORT_ORDER                 // sort order returned is by stock number, ascending
      );

      // Asserts that the cursor contains only one row.
      assertEquals(1, cursor1.getCount());

      // Moves to the cursor's first row, and asserts that this did not fail.
      assertTrue(cursor1.moveToFirst());

      // Saves the record's vehicle ID.
      String inputVehicleStock = cursor1.getString(0);

      // Queries the table using the content ID URI, which returns a single record with the
      // specified vehicle ID, matching the selection criteria provided.
      Cursor cursor2 = mMockResolver.query(vehicleIdUri, // the URI for a single vehicle
          VEHICLE_ID_PROJECTION,                 // same projection, get ID and stock number columns
          null,                  // same selection, based on stock number column
          null,                     // same selection arguments, stock number = "Stock1"
          SORT_ORDER                          // same sort order returned, by stock number, ascending
      );
      
      // Asserts that the cursor contains only one row.
      assertEquals(1, cursor2.getCount());

      // Moves to the cursor's first row, and asserts that this did not fail.
      assertTrue(cursor2.moveToFirst());

      // Asserts that the vehicle ID passed to the provider is the same as the vehicle ID returned.
      assertEquals(inputVehicleStock, cursor2.getString(0));
      
      assertEquals(cursor1.getString(0), cursor2.getString(0));
      assertEquals(cursor1.getString(1), cursor2.getString(1));
      assertEquals(cursor1.getString(2), cursor2.getString(2));
      assertEquals(cursor1.getString(3), cursor2.getString(3));
      assertEquals(cursor1.getInt(4), cursor2.getInt(4));
      assertEquals(cursor1.getString(5), cursor2.getString(5));
      assertEquals(cursor1.getInt(6), cursor2.getInt(6));
      assertEquals(cursor1.getString(7), cursor2.getString(7));
      assertEquals(cursor1.getString(8), cursor2.getString(8));
      assertEquals(cursor1.getString(9), cursor2.getString(9));
      assertEquals(cursor1.getString(10), cursor2.getString(10));
      assertEquals(cursor1.getString(11), cursor2.getString(11));
    }    

    /*
     *  Tests inserts into the data model.
     */
    public void testInserts() {
        // Creates a new vehicle instance with ID of 30.
        VehicleInfo vehicle = new VehicleInfo("Stock30", "VIN30", "Claim30", 
        		30, 30, "Z", 30, "Col30", 30, "Make30", "Model30", "SP30", "St30", "D30");

        // Insert subtest 1.
        // Inserts a row using the new vehicle instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        Uri rowUri = mMockResolver.insert(
            OnYard.Vehicles.CONTENT_URI,  // the main table URI
            vehicle.getContentValues()     // the map of values to insert as a new record
        );

        // Parses the returned URI to get the vehicle ID of the new vehicle. The ID is used in subtest 2.
        long vehicleId = ContentUris.parseId(rowUri);

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(
            OnYard.Vehicles.CONTENT_URI, // the main table URI
            null,                      // no projection, return all the columns
            null,                      // no selection criteria, return all the rows in the model
            null,                      // no selection arguments
            null                       // default sort order
        );

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        // Since no projection was used, get the column indexes of the returned columns
        int stockIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER);
        int VINIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_VIN);
        int claimIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_CLAIM_NUMBER);
        int latIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_LATITUDE);
        int longIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_LONGITUDE);
        int aisleIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_AISLE);
        int stallIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_STALL);
        int colorIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_COLOR);
        int yearIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_YEAR);
        int makeIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_MAKE);
        int modelIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_MODEL);
        int provIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER);
        int statusIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_STATUS);
        int dmgIndex = cursor.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_DAMAGE);

        // Tests each column in the returned cursor against the data that was inserted, comparing
        // the field in the vehicleInfo object to the data at the column index in the cursor.
        assertEquals(vehicle.getStockNumber(), cursor.getString(stockIndex));
        assertEquals(vehicle.getVIN(), cursor.getString(VINIndex));
        assertEquals(vehicle.getClaimNumber(), cursor.getString(claimIndex));
        assertEquals(vehicle.getLatitude(), cursor.getFloat(latIndex));
        assertEquals(vehicle.getLongitude(), cursor.getFloat(longIndex));
        assertEquals(vehicle.getAisle(), cursor.getString(aisleIndex));
        assertEquals(vehicle.getStall(), cursor.getInt(stallIndex));
        assertEquals(vehicle.getColor(), cursor.getString(colorIndex));
        assertEquals(vehicle.getYear(), cursor.getInt(yearIndex));
        assertEquals(vehicle.getMake(), cursor.getString(makeIndex));
        assertEquals(vehicle.getModel(), cursor.getString(modelIndex));
        assertEquals(vehicle.getSalvageProvider(), cursor.getString(provIndex));
        assertEquals(vehicle.getStatus(), cursor.getString(statusIndex));
        assertEquals(vehicle.getDamage(), cursor.getString(dmgIndex));

        // Insert subtest 2.
        // Tests that we can't insert a record whose id value already exists.

        // Defines a ContentValues object so that the test can add a vehicle ID to it.
        ContentValues values = vehicle.getContentValues();

        // Adds the vehicle ID retrieved in subtest 1 to the ContentValues object.
        values.put(OnYard.Vehicles._ID, (int) vehicleId);

        // Tries to insert this record into the table. This should fail and drop into the
        // catch block. If it succeeds, issue a failure message.
        try {
            rowUri = mMockResolver.insert(OnYard.Vehicles.CONTENT_URI, values);
            fail("Expected insert failure for existing record but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }

    /*
     * Tests deletions from the data model.
     */
    public void testDeletes() {
        // Subtest 1.
        // Tries to delete a record from a data model that is empty.

        // Sets the selection column to "Stock_Number"
        final String SELECTION_COLUMNS = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = " + "?";

        // Sets the selection argument "Stock0"
        final String[] SELECTION_ARGS = { "Stock0" };

        // Tries to delete rows matching the selection criteria from the data model.
        int rowsDeleted = mMockResolver.delete(
        	OnYard.Vehicles.CONTENT_URI, // the base URI of the table
            SELECTION_COLUMNS,         // select based on the stock number column
            SELECTION_ARGS             // select stock number = "Stock0"
        );

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        // Subtest 2.
        // Tries to delete an existing record. Repeats the previous subtest, but inserts data first.

        // Inserts data into the model.
        insertData();

        // Uses the same parameters to try to delete the row with stock number "Stock0"
        rowsDeleted = mMockResolver.delete(
        	OnYard.Vehicles.CONTENT_URI, // the base URI of the table
            SELECTION_COLUMNS,         // same selection column, "Stock_Number"
            SELECTION_ARGS             // same selection arguments, stock number = "Stock0"
        );

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(
        	OnYard.Vehicles.CONTENT_URI, // the base URI of the table
            null,                      // no projection, return all columns
            SELECTION_COLUMNS,         // select based on the stock number column
            SELECTION_ARGS,            // select stock number = "Stock0"
            null                       // use the default sort order
        );

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    /*
     * Tests updates to the data model.
     */
    public void testUpdates() {
        // Selection column for identifying a record in the data model.
        final String SELECTION_COLUMNS = OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER + " = " + "?";

        // Selection argument for the selection column.
        final String[] selectionArgs = { "Stock1" };

        // Defines a map of column names and values
        ContentValues values = new ContentValues();

        // Subtest 1.
        // Tries to update a record in an empty table.

        // Sets up the update by putting the "Branch_Number" column and a value into the values map.
        values.put(OnYard.Vehicles.COLUMN_NAME_AISLE, "BK");

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(
        	OnYard.Vehicles.CONTENT_URI,  // the URI of the data table
            values,                     // a map of the updates to do (column stock number and value)
            SELECTION_COLUMNS,           // select based on the stock number column
            selectionArgs               // select "stock_number = Stock1"
        );

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        // Subtest 2.
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        //  Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(
        	OnYard.Vehicles.CONTENT_URI,   // The URI of the data table
            values,                      // the same map of updates
            SELECTION_COLUMNS,            // same selection, based on the stock number column
            selectionArgs                // same selection argument, to select "stock_Number = Stock1"
        );

        // Asserts that only one row was updated. The selection criteria evaluated to
        // "stock_Number = Stock1", and the test data should only contain one row that matches that.
        assertEquals(1, rowsUpdated);
        
        rowsUpdated = mMockResolver.update(
        	Uri.withAppendedPath(OnYard.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, "Stock2"),
        	values,
        	null,
        	null
        );
        
        Cursor cursor = mMockResolver.query(
        	Uri.withAppendedPath(OnYard.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, "Stock2"),
            new String[]{ OnYard.Vehicles.COLUMN_NAME_AISLE },
            null,
            null,
            null
        );
        
        cursor.moveToFirst();
        
        assertEquals("BK", cursor.getString(0));
    }
    
    public void testInsertWithNullStockNum()
    {
    	VehicleInfo vehicle = new VehicleInfo(null, "VIN0", "Claim0", 10, 20, "A", 30, 
        		"Col0", 40, "Make0", "Model0", "SP0", "St0", "D0");
    	        
        try {
            mMockResolver.insert(
                    OnYard.Vehicles.CONTENT_URI,
                    vehicle.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testInsertWithNullColorCode()
    {
    	ColorInfo color = new ColorInfo(null, "Desc0");
    	        
        try {
            mMockResolver.insert(
                    OnYard.Color.CONTENT_URI,
                    color.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testInsertWithNullColorDesc()
    {
    	ColorInfo color = new ColorInfo("Code0", null);
    	        
        try {
            mMockResolver.insert(
                    OnYard.Color.CONTENT_URI,
                    color.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testInsertWithNullStatusCode()
    {
    	StatusInfo status = new StatusInfo(null, "Desc0");
    	        
        try {
            mMockResolver.insert(
                    OnYard.Status.CONTENT_URI,
                    status.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testInsertWithNullStatusDesc()
    {
    	StatusInfo status = new StatusInfo("Code0", null);
    	        
        try {
            mMockResolver.insert(
                    OnYard.Status.CONTENT_URI,
                    status.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testInsertWithNullDamageCode()
    {
    	DamageInfo damage = new DamageInfo(null, "Desc0");
    	        
        try {
            mMockResolver.insert(
                    OnYard.Damage.CONTENT_URI,
                    damage.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testInsertWithNullDamageDesc()
    {
    	DamageInfo damage = new DamageInfo("Code0", null);
    	        
        try {
            mMockResolver.insert(
                    OnYard.Damage.CONTENT_URI,
                    damage.getContentValues()
                );
            
            fail("Expected insert failure for missing NOT NULL column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testDuplicateStockNumInserts()
    {
    	VehicleInfo[] TEST_VEHICLES_DUPLICATE_STOCK_NUMBER = {
                new VehicleInfo("Stock1", "VIN0", "Claim0",10, 20, "A", 30, 
                		"Col0", 40, "Make0", "Model0", "SP0", "St0", "D0"),
                new VehicleInfo("Stock1", "VIN1", "Claim1",11, 21, "B", 31, 
                		"Col1", 41, "Make1", "Model1", "SP1", "St1", "D1")};
    	
        mMockResolver.insert(
                OnYard.Vehicles.CONTENT_URI,
                TEST_VEHICLES_DUPLICATE_STOCK_NUMBER[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(
                    OnYard.Vehicles.CONTENT_URI,
                    TEST_VEHICLES_DUPLICATE_STOCK_NUMBER[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testDuplicateColorCodeInserts()
    {
    	ColorInfo[] TEST_COLOR_DUPLICATE_CODE = {
                new ColorInfo("BL", "Black"),
                new ColorInfo("BL", "Green")};
    	
        mMockResolver.insert(
                OnYard.Color.CONTENT_URI,
                TEST_COLOR_DUPLICATE_CODE[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(
                    OnYard.Color.CONTENT_URI,
                    TEST_COLOR_DUPLICATE_CODE[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testDuplicateDamageCodeInserts()
    {
    	DamageInfo[] TEST_DAMAGE_DUPLICATE_CODE = {
                new DamageInfo("ABC", "Black"),
                new DamageInfo("ABC", "Green")};
    	
        mMockResolver.insert(
                OnYard.Damage.CONTENT_URI,
                TEST_DAMAGE_DUPLICATE_CODE[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(
                    OnYard.Damage.CONTENT_URI,
                    TEST_DAMAGE_DUPLICATE_CODE[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
    
    public void testDuplicateStatusCodeInserts()
    {
    	StatusInfo[] TEST_STATUS_DUPLICATE_CODE = {
                new StatusInfo("BL", "Black"),
                new StatusInfo("BL", "Green")};
    	
        mMockResolver.insert(
                OnYard.Status.CONTENT_URI,
                TEST_STATUS_DUPLICATE_CODE[0].getContentValues()
            );
        
        try {
            mMockResolver.insert(
                    OnYard.Status.CONTENT_URI,
                    TEST_STATUS_DUPLICATE_CODE[1].getContentValues()
                );
            
            fail("Expected insert failure for duplicate UNIQUE column but insert succeeded.");
        } catch (Exception e) {
          // succeeded, so do nothing.
        }
    }
}
