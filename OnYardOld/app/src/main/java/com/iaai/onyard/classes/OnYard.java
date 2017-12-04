package com.iaai.onyard.classes;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Class that contains constants pertaining to the OnYard content provider.
 */
public final class OnYard {
	
    /**
     * The authority for the OnYard Content Provider.
     */
    public static final String AUTHORITY = "com.iaai.provider.OnYard";
    /**
     * The size of the batches in which the JSON file containing vehicle info is
     * parsed. Smaller batches will use less memory but take more time.
     */
    public static final int VEHICLES_BATCH_SIZE = 250;
    /**
     * The maximum number of vehicles that can be displayed in the ListView when
     * there are multiple search results.
     */
    public static final int MAX_LIST_STOCKS = 250;
    /**
     * The app's current log mode. Events that are marked as more verbose than this 
     * will not be logged.
     */
    public static final LogMode LOG_MODE = LogMode.VERBOSE;
    
    /**
     * Enum describing all log modes, ordered from highest to lowest verbosity.
     */
    public static enum LogMode { VERBOSE, DEBUG, INFO, WARNING, ERROR, SUPPRESS }

    /**
     * This class cannot be instantiated.
     */
    private OnYard() {
    }

    /**
     * Vehicles table contract.
     */
    public static final class Vehicles implements BaseColumns {


        /**
         * This class cannot be instantiated.
         */
        private Vehicles() {}

        /**
         * The table name offered by this provider.
         */
        public static final String TABLE_NAME = "Vehicles";

        /*
         * URI definitions
         */

        /**
         * The scheme part for this provider's URI.
         */
        private static final String SCHEME = "content://";

        /**
         * Path parts for the URIs
         */

        /**
         * Path part for the Vehicles URI.
         */
        private static final String PATH_VEHICLES = "/vehicles";

        /**
         * Path part for the Vehicle stock number URI.
         */
        private static final String PATH_VEHICLE_STOCK_NUMBER = "/vehicles/stocknumber/";

        /**
         * 0-relative position of a vehicle stock number segment in the path part of 
         * a vehicle stock number URI.
         */
        public static final int VEHICLE_STOCK_NUMBER_PATH_POSITION = 2;

        /**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_VEHICLES);

        /**
         * The content URI base for a single vehicle. Callers must
         * append a text vehicle stock number to this Uri to retrieve a vehicle.
         */
        public static final Uri CONTENT_STOCK_NUMBER_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_VEHICLE_STOCK_NUMBER);

        /**
         * The content URI match pattern for a single vehicle, specified by its 
         * stock number. Use this to match incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_STOCK_NUMBER_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_VEHICLE_STOCK_NUMBER + "/*");

        /*
         * MIME type definitions
         */

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of vehicles.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.iaai.vehicles";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * vehicle.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.iaai.vehicle";

        /*
         * Column definitions
         */

        /**
         * Column name for the vehicle stock number.
         * <P>Type: TEXT PRIMARY KEY</P>
         */
        public static final String COLUMN_NAME_STOCK_NUMBER = "Stock_Number";
        /**
         * Name denoting stock number in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_STOCK_NUMBER = "a";

        /**
         * Column name for the vehicle VIN
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_VIN = "VIN";
        /**
         * Name denoting VIN in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_VIN = "b";

        /**
         * Column name for the vehicle Claim Number
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_CLAIM_NUMBER = "Claim_Number";
        /**
         * Name denoting claim number in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_CLAIM_NUMBER = "c";

        /**
         * Column name for the vehicle Latitude
         * <P>Type: REAL</P>
         */
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        /**
         * Name denoting latitude in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_LATITUDE = "e";

        /**
         * Column name for the vehicle Longitude
         * <P>Type: REAL</P>
         */
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        /**
         * Name denoting longitude in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_LONGITUDE = "f";

        /**
         * Column name for the vehicle Aisle
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_AISLE = "Aisle";
        /**
         * Name denoting aisle in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_AISLE = "g";

        /**
         * Column name for the vehicle Stall
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_STALL = "Stall";
        /**
         * Name denoting stall in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_STALL = "h";

        /**
         * Column name for the vehicle Color Code
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_COLOR = "Color";
        /**
         * Name denoting color in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_COLOR = "i";

        /**
         * Column name for the vehicle Year
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_YEAR = "Year";
        /**
         * Name denoting year in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_YEAR = "j";

        /**
         * Column name for the vehicle Make
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_MAKE = "Make";
        /**
         * Name denoting make in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_MAKE = "k";

        /**
         * Column name for the vehicle Model
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_MODEL = "Model";
        /**
         * Name denoting model in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_MODEL = "l";

        /**
         * Column name for the vehicle Salvage Provider
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_SALVAGE_PROVIDER = "Salvage_Provider";
        /**
         * Name denoting provider in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_SALVAGE_PROVIDER = "m";

        /**
         * Column name for the vehicle Status Code
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_STATUS = "Status";
        /**
         * Name denoting status in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_STATUS = "n";

        /**
         * Column name for the vehicle Damage Code
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DAMAGE = "Damage";
        /**
         * Name denoting damage in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_DAMAGE = "o";

        /**
         * Name denoting update time in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_UPDATE_TIME_UNIX = "p";
        
        /**
         * Default sort order of vehicles table.
         */
        public static final String DEFAULT_SORT_ORDER = COLUMN_NAME_STOCK_NUMBER + " ASC";
    }
    
    /**
     * Color table contract.
     */
    public static final class Color implements BaseColumns {
    	
        /**
         * This class cannot be instantiated.
         */
        private Color() {}
        
        /**
         * The scheme part for this provider's URI.
         */
        private static final String SCHEME = "content://";
        
        /**
         * Path part for the Color URI.
         */
        private static final String PATH_COLOR = "/color";
        
        /**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_COLOR);
        
        /**
         * The name of this table.
         */
        public static final String TABLE_NAME = "Color";

        /**
         * Column name for the color code.
         * <P>Type: TEXT PRIMARY KEY</P>
         */
        public static final String COLUMN_NAME_CODE = "Color_Code";
        /**
         * Name denoting color code in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_CODE = "a";

        /**
         * Column name for the color description.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DESCRIPTION = "Color_Description";
        /**
         * Name denoting color description in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_DESCRIPTION = "b";
    }
    
    /**
     * Status table contract.
     */
    public static final class Status implements BaseColumns {
    	
        /**
         * This class cannot be instantiated.
         */
        private Status() {}
        
        /**
         * The scheme part for this provider's URI.
         */
        private static final String SCHEME = "content://";
        
        /**
         * Path part for the Status URI.
         */
        private static final String PATH_STATUS = "/status";
        
        /**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_STATUS);
        
        /**
         * The name of this table.
         */
        public static final String TABLE_NAME = "Status";

        /**
         * Column name for the status code.
         * <P>Type: TEXT PRIMARY KEY</P>
         */
        public static final String COLUMN_NAME_CODE = "Status_Code";
        /**
         * Name denoting status code in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_CODE = "a";

        /**
         * Column name for the status description.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DESCRIPTION = "Status_Description";
        /**
         * Name denoting status description in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_DESCRIPTION = "b";
    }
    
    /**
     * Damage table contract.
     */
    public static final class Damage implements BaseColumns {
    	

        /**
         * This class cannot be instantiated.
         */
        private Damage() {}
        
        /**
         * The scheme part for this provider's URI.
         */
        private static final String SCHEME = "content://";
        
        /**
         * Path part for the Damage URI.
         */
        private static final String PATH_DAMAGE = "/damage";
        
        /**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_DAMAGE);
    	
        /**
         * The name of the table.
         */
        public static final String TABLE_NAME = "Damage";

        /**
         * Column name for the damage code.
         * <P>Type: TEXT PRIMARY KEY</P>
         */
        public static final String COLUMN_NAME_CODE = "Damage_Code";
        /**
         * Name denoting damage code in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_CODE = "a";

        /**
         * Column name for the damage description.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DESCRIPTION = "Damage_Description";
        /**
         * Name denoting damage description in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_DESCRIPTION = "b";
    }
    
    /**
     * Config table contract.
     */
    public static final class Config implements BaseColumns {
    	
        /**
         * This class cannot be instantiated
         */
        private Config() {}
        
        /**
         * The scheme part for this provider's URI.
         */
        private static final String SCHEME = "content://";
        
        /**
         * Path part for the Config URI.
         */
        private static final String PATH_CONFIG = "/config";
        
        /**
         * Path part for the Config Key URI.
         */
        private static final String PATH_CONFIG_KEY = "/config/";
        
        /**
         * 0-relative position of a config key segment in the path part of a config key URI.
         */
        public static final int CONFIG_KEY_PATH_POSITION = 1;
        
        /**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_CONFIG);
        
        /**
         * The content URI base for a single config. Callers must
         * append a text config key to this Uri to retrieve a value.
         */
        public static final Uri CONTENT_KEY_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_CONFIG_KEY);

        /**
         * The content URI match pattern for a single config, specified by its key. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_KEY_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_CONFIG_KEY + "/*");
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of config values.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.iaai.config";
        
        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * config value.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.iaai.config";
    	
        /**
         * The name of the table.
         */
        public static final String TABLE_NAME = "Config";

        /**
         * Column name for the config key.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_KEY = "Config_Key";

        /**
         * Column name for the config value.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_VALUE = "Config_Value";
        
        /**
         * Column name for the config update time.
         * <P>Type: INTEGER</P>
         */
        public static final String CONFIG_KEY_UPDATE_DATE_TIME = "Update_DateTime";
    }
    
    /**
     * Metrics table contract.
     */
    public static final class Metrics implements BaseColumns {
    	
        /**
         * This class cannot be instantiated.
         */
        private Metrics() {}
        
        /**
         * The scheme part for this provider's URI.
         */
        private static final String SCHEME = "content://";
        
        /**
         * Path part for the Metrics URI.
         */
        private static final String PATH_METRICS = "/metrics";
        
        /**
         * The content:// style URL for this table.
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_METRICS);
        
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of metrics values.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.iaai.metrics";
    	
        /**
         * The name of the table.
         */
        public static final String TABLE_NAME = "Metrics";
        
        /**
         * Column name for the id of the metrics.
         * <P>Type: INTEGER PRIMARY KEY AUTOINCREMENT</P>
         */
        public static final String COLUMN_NAME_ID = "_id";

        /**
         * Column name for the metrics stock number.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_STOCK_NUMBER = "Stock_Number";
        
        /**
         * Column name for the start time.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_STOCK_START_TIME = "Stock_Start_Time";
        
        /**
         * Column name for the end time.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_STOCK_END_TIME = "Stock_End_Time";

        /**
         * Column name for the number of photos taken.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_NUM_PHOTOS = "Num_Photos";
        
        /**
         * Column name for the number of photos annotated.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_NUM_PHOTOS_ANNOTATED = "Num_Photos_Annotated";
        
        /**
         * Column name for whether or not an email was sent.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_IS_EMAIL_SENT = "Is_Email_Sent";
        
        /**
         * Column name for the CSA start time.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CSA_START_TIME = "CSA_Start_Time";
        
        /**
         * Column name for the CSA end time.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_CSA_END_TIME = "CSA_End_Time";
        
        /**
         * Column name for the map start time.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_MAP_START_TIME = "Map_Start_Time";
        
        /**
         * Column name for the map end time.
         * <P>Type: INTEGER</P>
         */
        public static final String COLUMN_NAME_MAP_END_TIME = "Map_End_Time";
    }
    
    /**
     * Contract for Log Events sent back to the server via HTTP POST.
     */
    public static final class LogEvent implements BaseColumns {
    	
        /**
         * This class cannot be instantiated.
         */
        private LogEvent() {}
        
        /**
         * Name of the event object in the JSON string sent by the WCF service.
         */
        public static final String JSON_OBJECT_NAME = "eventData";
        
        /**
         * Name denoting app version in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_APP_VERSION = "a";
        /**
         * Name denoting device serial number in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_DEVICE_SERIAL = "b";
        /**
         * Name denoting event severity level in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_EVENT_LEVEL = "c";
        /**
         * Name denoting event description in the JSON string sent by the WCF service.
         */
        public static final String JSON_NAME_EVENT_DESCRIPTION = "d";
    }
}