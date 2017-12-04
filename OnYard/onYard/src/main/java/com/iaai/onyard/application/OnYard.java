package com.iaai.onyard.application;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.os.Build;

import com.iaai.onyard.R;


/**
 * Class that contains constants pertaining to the OnYard application.
 */
public final class OnYard {

    // DEV
    // public static final String SERVICE_URL_BASE = "https://dpal1.iaai.com";
    // public static final LogMode LOG_MODE = LogMode.VERBOSE;

    // DEV2
    public static final String SERVICE_URL_BASE = "https://dpal1.iaai.com:444";
    public static final LogMode LOG_MODE = LogMode.VERBOSE;

    // QA
    // public static final String SERVICE_URL_BASE = "https://qpal.iaai.com";
    // public static final LogMode LOG_MODE = LogMode.DEBUG;

    // STAGING
    // public static final String SERVICE_URL_BASE = "https://gpal.iaai.com";
    // public static final LogMode LOG_MODE = LogMode.DEBUG;

    // UAT
    // public static final String SERVICE_URL_BASE = "https://upal.iaai.com";
    // public static final LogMode LOG_MODE = LogMode.DEBUG;

    // PROD-----------------------------------------------------------------
    // public static final String SERVICE_URL_BASE = "https://onyard.iaai.com";
    // public static final LogMode LOG_MODE = LogMode.WARNING;
    // PROD-----------------------------------------------------------------

    // DR-------------------------------------------------------------------
    // public static final String SERVICE_URL_BASE = "https://ronyardweb01.iaai.com";
    // public static final LogMode LOG_MODE = LogMode.WARNING;
    // DR-------------------------------------------------------------------

    /**
     * The maximum number of vehicles that can be displayed in the ListView when
     * there are multiple search results.
     */
    public static final int MAX_LIST_STOCKS = 250;

    /**
     * The maximum number of pending enhancements that can be displayed in the ListView
     */
    public static final int MAX_LIST_ENHANCEMENTS = 500;

    /**
     * The size of the batches in which the JSON file containing vehicle info is parsed. Smaller
     * batches will use less memory but take more time.
     */
    public static final int SYNC_BATCH_SIZE = 250;

    /**
     * Enum describing all log modes, ordered from highest to lowest verbosity.
     */
    public static enum LogMode { VERBOSE, DEBUG, INFO, WARNING, ERROR, SUPPRESS }

    public static enum ImageMode {
        STANDARD, RESHOOT
    }

    public static enum SearchMode {
        GENERAL, IMAGER, CHECKIN, LOCATION, ENHANCEMENT, SETSALE
    }

    public static enum NavDrawerItemType {
        PRIMARY, SECONDARY
    }

    public static enum OnYardFieldInputType {
        ALPHANUMERIC, NUMERIC, LIST, TEXT, CHECKBOX, NOVALUE;

        public static OnYardFieldInputType toInputType(String str) {
            try {
                return valueOf(str.toUpperCase(Locale.US));
            }
            catch (final Exception ex) {
                return NOVALUE;
            }
        }
    }

    public static enum OnYardFieldType {
        STRING, INTEGER, DOUBLE, BOOLEAN, NOVALUE;

        public static OnYardFieldType toFieldType(String str) {
            try {
                return valueOf(str.toUpperCase(Locale.US));
            }
            catch (final Exception ex) {
                return NOVALUE;
            }
        }
    }

    public static class ImageSet {
        public static final int CHECK_IN = 0;
        public static final int ENHANCEMENT = 1;
    }

    public static class SalvageType {
        public static final int AUTOMOBILE = 1;
        public static final int MOTORCYCLE = 2;
        public static final int TRUCK = 3;
        public static final int TRAILER = 4;
        public static final int BOAT = 5;
        public static final int BUS = 6;
        public static final int CRANE = 7;
        public static final int FARM_EQUIPMENT = 8;
        public static final int EMERGENCY_EQUIPMENT = 9;
        public static final int FORESTRY_EQUIPMENT = 10;
        public static final int SNOWMOBILE = 11;
        public static final int MOTOR_HOME = 12;
        public static final int TRAVEL_TRAILER = 13;
        public static final int HEAVY_EQUIPMENT = 14;
        public static final int PERSONAL_WATERCRAFT = 15;
        public static final int OTHER = 99;
    }

    public static class NavDrawerTitle {
        public static final String SEARCH_GENERAL = "Home";
        public static final String SEARCH_IMAGER = "Imager";
        public static final String SEARCH_CHECKIN = "Check-In";
        public static final String SEARCH_ENHANCEMENTS = "Enhancements";
        public static final String SEARCH_LOCATION = "Location";
        public static final String SEARCH_SETSALE = "Set Sale";
        public static final String STOCK_DETAILS = "Details";
        public static final String STOCK_IMAGER = "Imager";
        public static final String STOCK_CHECKIN = "Check-In";
        public static final String STOCK_ENHANCEMENTS = "Enhancements";
        public static final String STOCK_LOCATION = "Location";
        public static final String STOCK_SETSALE = "Set Sale";
        public static final String MAP = "Map";
        public static final String SETTINGS = "Settings";
        public static final String CANCEL = "Cancel";
        public static final String LOG_IN = "Log In";
        public static final String LOG_OUT = "Log Out";
    }

    public static class ErrorMessage {
        public static final String INITIALIZATION = "There was an error initializing OnYard.";
        public static final String LOGIN = "There was an error logging in to OnYard.";
        public static final String LOGOUT = "There was an error logging out from OnYard.";
        public static final String PAGE_LOAD = "There was an error loading the page.";
        public static final String SAVE = "There was an error saving data.";
        public static final String VEHICLE_DATA_LOAD = "There was an error loading vehicle data. Please try again.";
        public static final String LOGOUT_NO_NETWORK = "You must have an active network connection to log out.";
        public static final String CHECKIN = "There was an error processing check-in data. Please try again.";
        public static final String ENHANCEMENTS = "There was an error processing enhancements data. Please try again.";
        public static final String SET_SALE = "There was an error processing sale data. Please try again.";
        public static final String NON_ALPHANUM_VIN = "The scanned barcode contains non-alphanumeric characters. Please make sure you are scanning a VIN barcode and try again.";
        public static final String INCORRECT_LENGTH_VIN = "The scanned barcode is not 17 characters long. Please make sure you are scanning a VIN barcode and try again.";
        public static final String INVALID_CHARS_VIN = "I, O, and Q are invalid characters for a VIN.\nThe entered VIN contains at least one invalid character. "
                + "Do you want to override the VIN rules and submit with invalid character(s)?";
        public static final String SEARCH_ON_ACTIVITY_RESULT = "There was an error while searching. Please close/re-open OnYard and try again.";
        public static final String INCOMPLETE_IMAGER = "10 check-in images are required. Press OK to proceed.";
        public static final String INCOMPLETE_CHECKIN = "Check-in is not complete. Press OK to proceed.";
        public static final String INCOMPLETE_LOCATION = "Location is not complete. Press OK to proceed.";
        public static final String FAILED_INITIAL_SYNC = "Sync could not complete. Please move to within wireless network range and press the \"Sync Now\" button.";
        public static final String FOCUS_FAILED = "There was an error while focusing. Please try again.";
    }

    public static class InfoMessage {
        public static final String INITIAL_SYNC_IN_PROGRESS = "Sync is in progress. Please stay within wireless network range.";
        public static final String LANE_ITEM_ASSIGNED_CURRENT_STOCK = "Lane %s, Item Number %d has already been assigned to this stock.";
        public static final String LANE_ITEM_ASSIGNED_DIFFERENT_STOCK = "Lane %s, Item Number %d is already in use. "
                + "It has been assigned to stock %s.";
    }

    public static class DialogTitle {
        public static final String INVALID_VIN = "Invalid VIN";
        public static final String INCOMPLETE_DATA = "Incomplete Data";
        public static final String FAILED_INITIAL_SYNC = "Sync Failed";
        public static final String LANE_ITEM_ASSIGNED = "Invalid item number";
    }

    public static class IntentExtraKey {
        public static final String SEARCH_VAL = "search_val";
        public static final String SEARCH_MODE = "search_mode";
        public static final String VEHICLE_MATCH_STOCK_NUM = "vehicle_match_stock_number";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String STOCK_NUMBER = "stock_number_extra";
        public static final String VIN = "vin_extra";
        public static final String YEAR_MAKE_MODEL = "year_make_model_extra";
        public static final String PREVIOUS_STOCK_NUM = "previous_stock_num";
        public static final String WEB_ACTIVITY_URL = "web_activity_url_extra";
        public static final String REVIEW_IMAGE_SEQUENCE = "review_image_order";
        public static final String VOLUNTARY_LOGOUT = "voluntary_logout";
        public static final String LOGOUT_MESSAGE = "logout_message";
        public static final String FORCE_LOGOUT = "force_logout";
        public static final String IMAGE_MODE = "image_mode";
        public static final String SYNC_TYPE = "sync_type";
        public static final String TAB_TAG = "tab_tag";
        public static final String NAV_DRAWER_ITEM = "nav_drawer_item";
        public static final String NAV_DRAWER_ITEMS = "nav_drawer_items";
        public static final String SYNC_BROADCAST_MESSAGE = "sync_completed";
        public static final String FORCE_SYNC_INFO_UPDATE = "force_sync_info_update";
        public static final String SYNC_SUCCESSFUL = "sync_successful";
    }

    public static class FragmentTag {
        public static final String MAP_TAB = "Map";
        public static final String ECI_TAB = "ECI";
        public static final String IMAGER_TAB = "Imager";
        public static final String RESHOOT_TAB = "Reshoot";
        public static final String FORCEFUL_LOGOUT_DIALOG = "logout_dialog";
        public static final String PROGRESS_DIALOG = "progress_dialog";
        public static final String FIRST_SYNC_PROGRESS_DIALOG = "first_sync_progress_dialog";
        public static final String LOGIN_DIALOG = "login_dialog";
        public static final String ERROR_DIALOG = "error_dialog";
        public static final String FATAL_ERROR_DIALOG = "fatal_error_dialog";
        public static final String NETWORK_ERROR_DIALOG = "network_error_dialog";
        public static final String DEFAULT_SEARCH_TAB = "Search";
        public static final String DEFAULT_DETAILS_TAB = "Details";
        public static final String CANCEL_CONFIRM_DIALOG = "cancel_confirm_dialog";
        public static final String LOGOUT_DIALOG = "logout_dialog";
        public static final String CONFIRM_VIN_DIALOG = "confirm_vin_dialog";
        public static final String INCOMPLETE_INPUT_DIALOG = "incomplete_input_dialog";
        public static final String INVALID_VIN_DIALOG = "invalid_vin_dialog";
        public static final String PASSWORD_DIALOG = "password_dialog";
        public static final String CONFIRM_FORCED_SYNC_DIALOG = "confirm_forced_sync_dialog";
    }

    // some field IDs must be hardcoded to implement custom rules
    public static class CheckinId {

        public static final int AXLE_STEERING = 94;
        public static final int BODY_STYLE = 109;
        public static final int CASSETTE = 11;
        public static final int CD_CHANGER = 13;
        public static final int CD_PLAYER = 14;
        public static final int DRIVER_AIRBAG = 16;
        public static final int ENGINE_STARTS = 76;
        public static final int ENGINE_STATUS = 21;
        public static final int EXTERIOR_COLOR = 22;
        public static final int HAS_OTHER = 121;
        public static final int INTERIOR_COLOR = 27;
        public static final int KEY_FOB = 28;
        public static final int KEYS = 29;
        public static final int LEFT_SIDE_AIRBAG = 32;
        public static final int LOSS_TYPE = 33;
        public static final int MAKE_KEYS = 34;
        public static final int NUMBER_OF_PLATES = 36;
        public static final int NUMBER_OF_TIRES_AUTOMOBILE = 37;
        public static final int NUMBER_OF_TIRES_MOTORCYCLE = 73;
        public static final int ODOMETER_STATUS = 39;
        public static final int ODOMETER_STATUS_RV = 217;
        public static final int OTHER = 122;
        public static final int PASSENGER_AIRBAG = 40;
        public static final int PLATE_CONDITION = 41;
        public static final int PRIMARY_DAMAGE = 42;
        public static final int RADIO_AUTOMOBILE = 46;
        public static final int RADIO_MOTORCYCLE = 75;
        public static final int RIGHT_SIDE_AIRBAG = 51;
        public static final int RUN_AND_DRIVE = 52;
        public static final int SALVAGE_CONDITION_AUTOMOBILE = 53;
        public static final int SALVAGE_TYPE = 54;
        public static final int SECONDARY_DAMAGE = 56;
        public static final int STALL = 66;
        public static final int STATE = 58;
        public static final int VIN_STATUS = 63;
        public static final int VIN_STATUS_NOT_REQUIRED = 221;
        public static final int VIN_WITH_SCAN = 9;
        public static final int VIN_WITH_NO_SCAN = 218;
        public static final int NUMBER_OF_WHEELS_AUTOMOBILE = 38;
        public static final int SALVAGE_CONDITION_MOTORCYCLE = 70;
        public static final int NUMBER_OF_WHEELS_MOTORCYCLE = 74;
        public static final int NUMBER_OF_AC_UNIT = 144;
        public static final int NUMBER_OF_TV = 145;
        public static final int NUMBER_OF_VCR = 146;
        public static final int NUMBER_OF_SLIDEOUT = 164;
        public static final int HAS_AM_FM_RADIO = 136;
        public static final int HAS_CD_PLAYER = 140;
        public static final int HAS_AC = 124;
        public static final int HAS_HEAT = 148;
        public static final int HEAT_TYPE = 149;
        public static final int HAS_REFRIGERATOR = 150;
        public static final int REFRIGERATOR_MODEL = 152;
        public static final int NUMBER_OF_AXLES = 179;
        public static final int NUMBER_OF_TRAILER_AXLES = 180;
        public static final int TRAILER_TYPE = 173;
        public static final int OTHER_TRAILER_TYPE = 174;
        public static final int WIDTH_TYPE = 175;
        public static final int OTHER_WIDTH_TYPE = 176;
        public static final int AXLE_TYPE = 181;
        public static final int OTHER_AXLE_TYPE = 182;

        public static final int HAS_SECOND_ENGINE = 238;
        public static final int ENGINE_2_STARTS = 248;
        public static final int HAS_GENERATOR = 249;
        public static final int GENERATOR_KILOWATTS = 135;
        public static final int HAS_SONAR = 253;
        public static final int SONAR_MODEL = 255;
        public static final int HAS_RADAR = 256;
        public static final int RADAR_MODEL = 258;
        public static final int HAS_TRAILER = 269;
        public static final int NUM_OF_MARINE_TRAILER_AXLES = 273;
        public static final int KEYS_NO_RULES = 274;

        public static final int HAS_VEHICLE_TURBOCHARGER = 322;
        public static final int HAS_VEHICLE_SUPERCHARGER = 323;
        public static final int HAS_EQUIPMENT_ENGINE = 332;
        public static final int EQUIPMENT_ENGINE_COOLANT_LEVEL = 342;
        public static final int HAS_EQUIPMENT_TURBOCHARGER = 334;
        public static final int HAS_EQUIPMENT_SUPERCHARGER = 335;
        public static final int HAS_OROPS = 343;
        public static final int HAS_EROPS = 344;

        public static final int EXTERIOR_COLOR_NOT_REQUIRED = 277;
        public static final int KEYS_NOT_REQUIRED = 278;
        public static final int STEERING_TYPE = 285;
        public static final int HAS_ALARM = 294;
        public static final int IS_ALARM_FACTORY_INSTALLED = 295;
        public static final int OTHER_ALARM = 296;
        public static final int HAS_OTHER_OPTIONS = 312;
        public static final int OTHER_OPTIONS = 313;
        public static final int DRIVER_AIRBAG_OTHER = 281;
        public static final int PASSENGER_AIRBAG_OTHER = 282;
        public static final int LEFT_SIDE_AIRBAG_OTHER = 283;
        public static final int RIGHT_SIDE_AIRBAG_OTHER = 284;
    }

    public static class SetSaleId {
        public static final int SALE_AISLE = 1;
        public static final int AUCTION_NUMBER = 2;
        public static final int ODD_EVEN_NUMBERING = 3;
        public static final int AUCTION_ITEM_SEQUENCE_NUMBER = 4;
    }

    public static class EnhancementOptions {
        public static final String COMPLETE_DISPLAY_NAME = "Complete";
        public static final String COMPLETE_VALUE = "WCP";
        public static final String NA_DISPLAY_NAME = "N/A";
        public static final String NA_VALUE = "WCN";
        public static final String REQUEST_APPROVAL_DISPLAY_NAME = "Request Approval";
        public static final String REQUEST_APPROVAL_VALUE = "APN";
        public static final String WORK_PENDING_VALUE = "WPN";
    }

    public static class Broadcast {
        public static final String LOGOUT = "com.iaai.onyard.USER_LOG_OUT";
        public static final String SYNC_COMPLETED = "com.iaai.onyard.SYNC_COMPLETED";
        public static final String UPDATE_SYNC_INFO = "com.iaai.onyard.UPDATE_SYNC_INFO";
    }

    public static class PilotFunctionId {
        public static final int SET_SALE = 1;
        public static final int ENHANCEMENTS = 2;
    }

    public static class EnhancementId {
        public static final int CAR_START = 273;
        public static final int DRIVE_THROUGH = 293;
        public static final int RUN_AND_DRIVE = 269;
    }

    /**
     * This class cannot be instantiated.
     */
    private OnYard() {
    }

    public static final String LEADTOOLS_LICENSE_FILENAME = "Insurance Auto Auctions, Inc.-IMGPRO18.lic";
    public static final String LEADTOOLS_LICENSE_KEY = "/8x+XsRTgJbKbR7DJtFwk96RAsBrLmAIkd2r3qdpnjOeoIPYAmOw1xiYk3qhCnFE";

    public static final int CAMERA_ACTIVITY_REQUEST_CODE = 5;

    private static final String GALAXY_S4_MANUFACTURER = "samsung";
    private static final String GALAXY_S4_MODEL = "SCH-I545";

    private static final String NOTE_3_MANUFACTURER = "samsung";
    private static final String NOTE_3_MODEL = "SM-N900";

    private static final String NEXUS_7_MANUFACTURER = "asus";
    private static final String NEXUS_7_MODEL = "Nexus 7";

    private static final String CN51_MANUFACTURER = "Intermec";
    private static final String CN51_MODEL = "CN51";

    private static final String FZ_X1_MANUFACTURER = "PANASONIC";
    private static final String FZ_X1_MODEL = "FZ-X1";

    public static boolean isDeviceGalaxyS4() {
        return Build.MANUFACTURER.equals(OnYard.GALAXY_S4_MANUFACTURER)
                && Build.MODEL.equals(OnYard.GALAXY_S4_MODEL);
    }

    public static boolean isDeviceNexus7() {
        return Build.MANUFACTURER.equals(OnYard.NEXUS_7_MANUFACTURER)
                && Build.MODEL.equals(OnYard.NEXUS_7_MODEL);
    }

    public static boolean isDeviceNote3() {
        return Build.MANUFACTURER.equals(OnYard.NOTE_3_MANUFACTURER)
                && Build.MODEL.contains(OnYard.NOTE_3_MODEL);
    }

    public static boolean isDeviceCN51() {
        return Build.MANUFACTURER.equals(OnYard.CN51_MANUFACTURER)
                && Build.MODEL.equals(OnYard.CN51_MODEL);
    }

    public static boolean isDeviceFzX1() {
        return Build.MANUFACTURER.equals(OnYard.FZ_X1_MANUFACTURER)
                && Build.MODEL.equals(OnYard.FZ_X1_MODEL);
    }

    public static boolean isKitKat() {
        return android.os.Build.VERSION.SDK_INT == 19;
    }

    public static final String ACTION_BAR_TITLE_ID = "action_bar_title";
    public static final String ACTION_BAR_SUBTITLE_ID = "action_bar_subtitle";

    public static final String ACCOUNT_TYPE = "com.iaai.OnYard.account";
    public static final String DEFAULT_FLASH_MODE = Camera.Parameters.FLASH_MODE_AUTO;

    public static final String DEFAULT_IMAGE_TYPE = "Standard";
    public static final String DEFAULT_BRANCH_NUMBER = "0";
    public static float DESIRED_ASPECT_RATIO = (float) 4 / 3;

    public static final String WAIT_CHECKIN_STATUS_CODE = "H05";
    public static final String POST_CHECKIN_STATUS_CODE = "J05";
    // Constants for 'k10', 'k13', 'k14'
    public static final String SCHEDULE_FOR_SALE = "K10";
    public static final String MANUAL_SALE_LOCKED_DOWN = "K13";
    public static final String SYSTEM_SALE_LOCKED_DOWN = "K14";

    public static final String POST_CHECKIN_STATUS_DESCRIPTION = "Wait Title Document";

    public static final int SYNC_FAILURE_CODE = -1;
    public static final int SYNC_SUCCESS_CODE = 0;

    /*
     * Tab swiping parameters
     */
    public static final int SWIPE_MIN_DISTANCE = 250;
    public static final int SWIPE_THRESHOLD_VELOCITY = 400;
    public static final int SWIPE_MAX_OFF_PATH = 250;

    /*
     * Tab tag definitions
     */

    public static int MAX_DISPLAYED_RESHOOTS = 250;

    public static final String ZXING_SCAN_ACTION = "com.google.zxing.client.android.SCAN";

    public static final String ADMIN_PASSWORD = "pass";

    public static final int SYNC_NOTIFICATION_ID = 1;

    public static final String ON_DEMAND_SYNC_TYPE_VALUE = "OnDemand";
    public static final String FULL_SYNC_TYPE_VALUE = "Full";
    public static final String DELETE_OLDEST_DATA_PASSWORD = "Pa5sW0rD";

    public static final String DATA_SERVICE_URL_BASE = SERVICE_URL_BASE;
    public static final String SUBMIT_SERVICE_URL_BASE = SERVICE_URL_BASE;
    public static final String LOGIN_URL = SERVICE_URL_BASE + "/authentication/windows/login.aspx";

    public static final String IAA_MAIL_SERVER = "Hub.iaai.com";
    public static final String METRICS_TO_ADDRESS = "wferguson@iaai.com";
    public static final String DATA_SERVICE_PASSWORD = "1a!A0Ny4r*D57";
    public static final String EMAIL_FROM_ADDRESS = "IAAOnYard@iaai.com";

    public static final String IMAGE_DIR = "OnYard";

    public static final String DEFAULT_USER_LOGIN = "logged_out";

    public static final String DEFAULT_FORCEFUL_LOGOUT_MESSAGE = "Your credentials are no longer valid. You will now be logged out.";

    public static final int DEFAULT_SYNC_INTERVAL_IN_MINUTES = 5;

    /**
     * Get the current application name.
     * 
     * @param context The current context.
     * @return The app name.
     */
    public static String getAppName(Context context) {
        try {
            return context.getString(R.string.app_name);
        }
        catch (final Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * Get the current application version.
     * 
     * @param context The current context.
     * @return The user-visible app version string.
     */
    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        }
        catch (final NameNotFoundException e) {
            return "UNKNOWN";
        }
    }
}