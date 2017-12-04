package com.iaai.onyard.sync;

import java.io.BufferedReader;
import java.io.File;
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

import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.AuctionScheduleInfo;
import com.iaai.onyardproviderapi.classes.BodyStyleSpecialtyInfo;
import com.iaai.onyardproviderapi.classes.BranchInfo;
import com.iaai.onyardproviderapi.classes.CheckinFieldInfo;
import com.iaai.onyardproviderapi.classes.CheckinTemplateInfo;
import com.iaai.onyardproviderapi.classes.ColorInfo;
import com.iaai.onyardproviderapi.classes.ConfigInfo;
import com.iaai.onyardproviderapi.classes.DamageInfo;
import com.iaai.onyardproviderapi.classes.DisabledEnhancementInfo;
import com.iaai.onyardproviderapi.classes.EngineStatusInfo;
import com.iaai.onyardproviderapi.classes.EnhancementInfo;
import com.iaai.onyardproviderapi.classes.FeatureValueInfo;
import com.iaai.onyardproviderapi.classes.HolidayInfo;
import com.iaai.onyardproviderapi.classes.ImageCaptionInfo;
import com.iaai.onyardproviderapi.classes.ImageReshootInfo;
import com.iaai.onyardproviderapi.classes.ImageTypeInfo;
import com.iaai.onyardproviderapi.classes.LicensePlateConditionInfo;
import com.iaai.onyardproviderapi.classes.LossTypeInfo;
import com.iaai.onyardproviderapi.classes.OdometerReadingTypeInfo;
import com.iaai.onyardproviderapi.classes.PilotFunctionInfo;
import com.iaai.onyardproviderapi.classes.PublicVinInfo;
import com.iaai.onyardproviderapi.classes.SaleDocTypeInfo;
import com.iaai.onyardproviderapi.classes.SalvageConditionInfo;
import com.iaai.onyardproviderapi.classes.SalvageEnhancementInfo;
import com.iaai.onyardproviderapi.classes.SalvageProviderInfo;
import com.iaai.onyardproviderapi.classes.SalvageTypeInfo;
import com.iaai.onyardproviderapi.classes.SlaSalvageEnhancementInfo;
import com.iaai.onyardproviderapi.classes.StateInfo;
import com.iaai.onyardproviderapi.classes.StatusInfo;
import com.iaai.onyardproviderapi.classes.SyncWindowExceptionInfo;
import com.iaai.onyardproviderapi.classes.SyncWindowInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * Helper class containing methods that deal with JSON parsing.
 */
public class JSONHelper {

    private static final String CHARACTER_SET = "iso-8859-1";

    public static class ImportMode {

        /**
         * This mode performs a straight insert of all JSON objects into the corresponding database
         * table.
         */
        public static final int ALL_OBJECTS = 1;
        /**
         * This mode will iterate through each JSON object and check if a matching record exists in
         * the database. If a matching record exists, that record will be updated. If no matching
         * record exists, the JSON object is inserted into the corresponding database table.
         */
        public static final int UPDATED_OBJECTS = 2;
    }

    public static class ImportObject {

        /**
         * This constant indicates that the JSON being imported contains objects of type VehicleInfo
         * that should be inserted into the Vehicles table.
         */
        public static final int VEHICLE = 1;
        /**
         * This constant indicates that the JSON being imported contains objects of type
         * ImageReshootInfo that should be inserted into the ImageReshoot table.
         */
        public static final int RESHOOT = 2;
        /**
         * This constant indicates that the JSON being imported contains objects of type
         * SalvageProviderInfo that should be inserted into the SalvageProvider table.
         */
        public static final int SALVAGE_PROVIDER = 3;
        /**
         * This constant indicates that the JSON being imported contains objects of type ColorInfo
         * that should be inserted into the Color table.
         */
        public static final int COLOR = 4;
        /**
         * This constant indicates that the JSON being imported contains objects of type StatusInfo
         * that should be inserted into the Status table.
         */
        public static final int STATUS = 5;
        /**
         * This constant indicates that the JSON being imported contains objects of type DamageInfo
         * that should be inserted into the Damage table.
         */
        public static final int DAMAGE = 6;
        /**
         * This constant indicates that the JSON being imported contains objects of type
         * SaleDocTypeInfo that should be inserted into the SaleDocType table.
         */
        public static final int SALE_DOC_TYPE = 7;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * ImageCaptionInfo that should be inserted into the ImageCaption table.
         */
        public static final int IMAGE_CAPTION = 8;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * ImageTypeInfo that should be inserted into the ImageType table.
         */
        public static final int IMAGE_TYPE = 9;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * LossTypeInfo that should be inserted into the LossType table.
         */
        public static final int LOSS_TYPE = 10;

        /**
         * This constant indicates that the JSON being imported contains objects of type BranchInfo
         * that should be inserted into the Branch table.
         */
        public static final int BRANCH = 11;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * EngineStatusInfo that should be inserted into the EngineStatus table.
         */
        public static final int ENGINE_STATUS = 12;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * FeatureValueInfo that should be inserted into the FeatureValue table.
         */
        public static final int FEATURE_VALUE = 13;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * LicensePlateConditionInfo that should be inserted into the LicensePlateCondition table.
         */
        public static final int LICENSE_PLATE_CONDITION = 14;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * OdometerReadingTypeInfo that should be inserted into the OdometerReadingType table.
         */
        public static final int ODOMETER_READING_TYPE = 15;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * PublicVinInfo that should be inserted into the PublicVIN table.
         */
        public static final int PUBLIC_VIN = 16;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * SalvageConditionInfo that should be inserted into the SalvageCondition table.
         */
        public static final int SALVAGE_CONDITION = 17;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * SalvageTypeInfo that should be inserted into the SalvageType table.
         */
        public static final int SALVAGE_TYPE = 18;

        /**
         * This constant indicates that the JSON being imported contains objects of type StateInfo
         * that should be inserted into the State table.
         */
        public static final int STATE = 19;

        /**
         * This constant indicates that the JSON being imported contains objects of type
         * BodyStyleSpecialtyInfo that should be inserted into the BodyStyleSpecialty table.
         */
        public static final int BODY_STYLE_SPECIALTY = 20;
        public static final int CHECKIN_FIELD = 21;
        public static final int CHECKIN_TEMPLATE = 22;
        public static final int ENHANCEMENT = 23;
        public static final int AUCTION_SCHEDULE = 24;
        public static final int SLA_SALVAGE_ENHANCEMENT = 25;
        public static final int PILOT_FUNCTION = 26;
        public static final int SALVAGE_ENHANCEMENT = 27;
        public static final int DISABLED_ENHANCEMENT = 28;

        /**
         * This constant indicates that the JSON being imported contains objects of type HolidayInfo
         * that should be inserted into the Holiday table.
         */
        public static final int HOLIDAY = 29;

        public static final int SYNC_WINDOW = 30;
        public static final int SYNC_WINDOW_EXCEPTION = 31;
        public static final int ONYARD_CONFIG = 32;
    }
    /**
     * Flag indicating whether or not the end of the JSON file has been reached.
     */
    private static boolean mIsEndOfFile = false;

    /**
     * INSERT MODE: Parse a file containing Vehicle or Reshoot objects in JSON format and batch
     * insert all objects within into the corresponding table in the OnYard ContentProvider. The
     * JSON file is deleted after it has been read all the way through. <br>
     * <br>
     * UPDATE MODE: Parse a file containing vehicles or reshoot objects in JSON format. For each
     * object read, check if it already exists in the OnYard ContentProvider. If it does exist,
     * update the record. If it does not exist, insert a new record. The JSON file is deleted after
     * it has been read all the way through.
     * 
     * @param context The current context.
     * @param fileName The name of the temporary text file which will be created to store JSON data
     *            while it is being read.
     * @param batchSize The number of JSON objects to read per batch read/insert operation.
     * @param importMode The method of importing the data - see class ImportMode.
     * @param importObject The type of object being imported - see class ImportObject.
     * @throws JSONException if any object values are missing from the JSON file.
     * @throws IOException if an error occurs while setting a mark in the file reader.
     */
    public static void importObjectsFromJSONFile(Context context, String fileName, int batchSize,
            int importMode, int importObject) throws JSONException, IOException {
        final Timer t = new Timer("JSONHelper.importObjectsFromJSONFile");
        t.start();

        JSONArray objectArray = null;
        ContentValues[] values = null;
        int numObjects = 0;

        final InputStreamReader isr = new InputStreamReader(new FileInputStream(context
                .getFilesDir().getPath() + File.separator + fileName), CHARACTER_SET);
        final Reader reader = new BufferedReader(isr, 8);

        try
        {
            while(!mIsEndOfFile)
            {
                objectArray = getJSONArrayFromFile(reader, batchSize);
                numObjects = objectArray.length();
                String recordType = "";

                if (importMode == ImportMode.ALL_OBJECTS) {
                    values = new ContentValues[numObjects];

                    switch (importObject) {
                        case ImportObject.VEHICLE:
                            importAllVehicles(context, objectArray, numObjects, values);
                            recordType = " vehicle";
                            break;
                        case ImportObject.RESHOOT:
                            importAllReshoots(context, objectArray, numObjects, values);
                            recordType = " reshoot";
                            break;
                        case ImportObject.SALVAGE_PROVIDER:
                            importAllSalvageProviders(context, objectArray, numObjects, values);
                            recordType = " salvage provider";
                            break;
                        case ImportObject.COLOR:
                            importAllColors(context, objectArray, numObjects, values);
                            recordType = " color";
                            break;
                        case ImportObject.STATUS:
                            importAllStatuses(context, objectArray, numObjects, values);
                            recordType = " status";
                            break;
                        case ImportObject.DAMAGE:
                            importAllDamages(context, objectArray, numObjects, values);
                            recordType = " damage";
                            break;
                        case ImportObject.SALE_DOC_TYPE:
                            importAllSaleDocTypes(context, objectArray, numObjects, values);
                            recordType = " sale document type";
                            break;
                        case ImportObject.IMAGE_CAPTION:
                            importAllImageCaptions(context, objectArray, numObjects, values);
                            recordType = " image caption";
                            break;
                        case ImportObject.IMAGE_TYPE:
                            importAllImageTypes(context, objectArray, numObjects, values);
                            recordType = " image type";
                            break;
                        case ImportObject.LOSS_TYPE:
                            importAllLossTypes(context, objectArray, numObjects, values);
                            recordType = " loss type";
                            break;
                        case ImportObject.BRANCH:
                            importAllBranches(context, objectArray, numObjects, values);
                            recordType = " branch";
                            break;
                        case ImportObject.ENGINE_STATUS:
                            importAllEngineStatuses(context, objectArray, numObjects, values);
                            recordType = " engine status";
                            break;
                        case ImportObject.FEATURE_VALUE:
                            importAllFeatureValues(context, objectArray, numObjects, values);
                            recordType = " feature value";
                            break;
                        case ImportObject.LICENSE_PLATE_CONDITION:
                            importAllLicensePlateConditions(context, objectArray, numObjects,
                                    values);
                            recordType = " license plate condition";
                            break;
                        case ImportObject.ODOMETER_READING_TYPE:
                            importAllOdometerReadingTypes(context, objectArray, numObjects, values);
                            recordType = " odometer reading type";
                            break;
                        case ImportObject.PUBLIC_VIN:
                            importAllPublicVINs(context, objectArray, numObjects, values);
                            recordType = " public VIN";
                            break;
                        case ImportObject.SALVAGE_CONDITION:
                            importAllSalvageConditions(context, objectArray, numObjects, values);
                            recordType = " salvage condition";
                            break;
                        case ImportObject.SALVAGE_TYPE:
                            importAllSalvageTypes(context, objectArray, numObjects, values);
                            recordType = " salvage type";
                            break;
                        case ImportObject.STATE:
                            importAllStates(context, objectArray, numObjects, values);
                            recordType = " state";
                            break;
                        case ImportObject.BODY_STYLE_SPECIALTY:
                            importAllBodyStyleSpecialties(context, objectArray, numObjects, values);
                            recordType = " body style specialty";
                            break;
                        case ImportObject.CHECKIN_FIELD:
                            importAllCheckinFields(context, objectArray, numObjects, values);
                            recordType = " checkin field";
                            break;
                        case ImportObject.CHECKIN_TEMPLATE:
                            importAllCheckinTemplates(context, objectArray, numObjects, values);
                            recordType = " checkin template";
                            break;
                        case ImportObject.ENHANCEMENT:
                            importAllEnhancements(context, objectArray, numObjects, values);
                            recordType = " enhancement";
                            break;
                        case ImportObject.AUCTION_SCHEDULE:
                            importAllAuctionSchedules(context, objectArray, numObjects, values);
                            recordType = " auction schedule";
                            break;
                        case ImportObject.SLA_SALVAGE_ENHANCEMENT:
                            importAllSlaSalvageEnhancements(context, objectArray, numObjects,
                                    values);
                            recordType = " sla salvage enhancement";
                            break;
                        case ImportObject.SALVAGE_ENHANCEMENT:
                            importAllSalvageEnhancements(context, objectArray, numObjects, values);
                            recordType = " salvage enhancement";
                            break;
                        case ImportObject.PILOT_FUNCTION:
                            importAllPilotFunctions(context, objectArray, numObjects, values);
                            recordType = " pilot function";
                            break;
                        case ImportObject.DISABLED_ENHANCEMENT:
                            importAllDisabledEnhancements(context, objectArray, numObjects, values);
                            recordType = " disabled enhancement";
                            break;
                        case ImportObject.HOLIDAY:
                            importAllHolidays(context, objectArray, numObjects, values);
                            recordType = " holiday";
                            break;
                        case ImportObject.SYNC_WINDOW:
                            importAllSyncWindows(context, objectArray, numObjects, values);
                            recordType = " sync window";
                            break;
                        case ImportObject.SYNC_WINDOW_EXCEPTION:
                            importAllSyncWindowExceptions(context, objectArray, numObjects, values);
                            recordType = " sync exception";
                            break;
                        case ImportObject.ONYARD_CONFIG:
                            importAllConfigs(context, objectArray, numObjects, values);
                            recordType = " onyard configs";
                            break;
                        default:
                            break;
                    }

                    LogHelper.logVerbose("Inserted " + objectArray.length() + recordType
                            + " records.");
                }
                if (importMode == ImportMode.UPDATED_OBJECTS) {
                    switch (importObject) {
                        case ImportObject.VEHICLE:
                            importUpdatedVehicles(context, objectArray, numObjects);
                            recordType = " vehicle";
                            break;
                        case ImportObject.RESHOOT:
                            importUpdatedReshoots(context, objectArray, numObjects);
                            recordType = " reshoot";
                            break;
                        case ImportObject.SALVAGE_PROVIDER:
                            importUpdatedSalvageProviders(context, objectArray, numObjects);
                            recordType = " salvage provider";
                            break;
                        case ImportObject.COLOR:
                            importUpdatedColors(context, objectArray, numObjects);
                            recordType = " color";
                            break;
                        case ImportObject.STATUS:
                            importUpdatedStatuses(context, objectArray, numObjects);
                            recordType = " status";
                            break;
                        case ImportObject.DAMAGE:
                            importUpdatedDamages(context, objectArray, numObjects);
                            recordType = " damage";
                            break;
                        case ImportObject.SALE_DOC_TYPE:
                            importUpdatedSaleDocTypes(context, objectArray, numObjects);
                            recordType = " sale document type";
                            break;
                        case ImportObject.IMAGE_CAPTION:
                            importUpdatedImageCaptions(context, objectArray, numObjects);
                            recordType = " image caption";
                            break;
                        case ImportObject.IMAGE_TYPE:
                            importUpdatedImageTypes(context, objectArray, numObjects);
                            recordType = " image type";
                            break;
                        case ImportObject.LOSS_TYPE:
                            importUpdatedLossTypes(context, objectArray, numObjects);
                            recordType = " loss type";
                            break;
                        case ImportObject.BRANCH:
                            importUpdatedBranches(context, objectArray, numObjects);
                            recordType = " branch";
                            break;
                        case ImportObject.ENGINE_STATUS:
                            importUpdatedEngineStatuses(context, objectArray, numObjects);
                            recordType = " engine status";
                            break;
                        case ImportObject.FEATURE_VALUE:
                            importUpdatedFeatureValues(context, objectArray, numObjects);
                            recordType = " feature value";
                            break;
                        case ImportObject.LICENSE_PLATE_CONDITION:
                            importUpdatedLicensePlateConditions(context, objectArray, numObjects);
                            recordType = " license plate condition";
                            break;
                        case ImportObject.ODOMETER_READING_TYPE:
                            importUpdatedOdometerReadingTypes(context, objectArray, numObjects);
                            recordType = " odometer reading type";
                            break;
                        case ImportObject.PUBLIC_VIN:
                            importUpdatedPublicVINs(context, objectArray, numObjects);
                            recordType = " public VIN";
                            break;
                        case ImportObject.SALVAGE_CONDITION:
                            importUpdatedSalvageConditions(context, objectArray, numObjects);
                            recordType = " salvage condition";
                            break;
                        case ImportObject.SALVAGE_TYPE:
                            importUpdatedSalvageTypes(context, objectArray, numObjects);
                            recordType = " salvage type";
                            break;
                        case ImportObject.STATE:
                            importUpdatedStates(context, objectArray, numObjects);
                            recordType = " state";
                            break;
                        case ImportObject.BODY_STYLE_SPECIALTY:
                            importUpdatedBodyStyleSpecialties(context, objectArray, numObjects);
                            recordType = " body style specialty";
                            break;
                        case ImportObject.CHECKIN_FIELD:
                            importUpdatedCheckinFields(context, objectArray, numObjects);
                            recordType = " checkin field";
                            break;
                        case ImportObject.CHECKIN_TEMPLATE:
                            importUpdatedCheckinTemplates(context, objectArray, numObjects);
                            recordType = " checkin template";
                            break;
                        case ImportObject.ENHANCEMENT:
                            importUpdatedEnhancements(context, objectArray, numObjects);
                            recordType = " enhancement";
                            break;
                        case ImportObject.AUCTION_SCHEDULE:
                            importUpdatedAuctionSchedules(context, objectArray, numObjects);
                            recordType = " auction schedule";
                            break;
                        case ImportObject.SLA_SALVAGE_ENHANCEMENT:
                            importUpdatedSlaSalvageEnhancements(context, objectArray, numObjects);
                            recordType = " sla salvage enhancement";
                            break;
                        case ImportObject.SALVAGE_ENHANCEMENT:
                            importUpdatedSalvageEnhancements(context, objectArray, numObjects);
                            recordType = " salvage enhancement";
                            break;
                        case ImportObject.PILOT_FUNCTION:
                            importUpdatedPilotFunctions(context, objectArray, numObjects);
                            recordType = " pilot function";
                            break;
                        case ImportObject.DISABLED_ENHANCEMENT:
                            importUpdatedDisabledEnhancements(context, objectArray, numObjects);
                            recordType = " disabled enhancement";
                            break;
                        case ImportObject.HOLIDAY:
                            importUpdatedHolidays(context, objectArray, numObjects);
                            recordType = " holiday";
                            break;
                        case ImportObject.SYNC_WINDOW:
                            importUpdatedSyncWindows(context, objectArray, numObjects);
                            recordType = " sync window";
                            break;
                        case ImportObject.SYNC_WINDOW_EXCEPTION:
                            importUpdatedSyncWindowExceptions(context, objectArray,
                                    numObjects);
                            recordType = " sync exception";
                            break;
                        case ImportObject.ONYARD_CONFIG:
                            importUpdatedConfigs(context, objectArray, numObjects);
                            recordType = " onyard configs";
                            break;
                        default:
                            break;
                    }

                    LogHelper.logVerbose("OnDemandSync updated " + objectArray.length()
                            + recordType + " records.");
                }
            }

            mIsEndOfFile = false; //in case another sync starts right away
            context.deleteFile(fileName);
        }
        finally
        {
            isr.close();
            reader.close();
        }

        t.end();
        t.logVerbose();
    }

    private static void importAllVehicles(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        Long pendingSyncTime = null;
        for (int index = 0; index < numObjects; index++) {
            values[index] = new VehicleInfo(objectArray.getJSONObject(index)).getContentValues();

            if (index == 0) {
                pendingSyncTime = objectArray.getJSONObject(index).getLong(
                        OnYardContract.Vehicles.JSON_NAME_UPDATE_TIME_UNIX);
            }
        }

        context.getContentResolver().bulkInsert(OnYardContract.Vehicles.CONTENT_URI, values);

        if (pendingSyncTime != null) {
            insertPendingSyncTime(context, pendingSyncTime);
        }
    }

    private static void importAllReshoots(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new ImageReshootInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.ImageReshoot.CONTENT_URI, values);
    }

    private static void importAllSalvageProviders(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new SalvageProviderInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.SalvageProvider.CONTENT_URI, values);
    }

    private static void importAllColors(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new ColorInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.Color.CONTENT_URI, values);
    }

    private static void importAllStatuses(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new StatusInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.Status.CONTENT_URI, values);
    }

    private static void importAllDamages(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new DamageInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.Damage.CONTENT_URI, values);
    }

    private static void importAllSaleDocTypes(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new SaleDocTypeInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.SaleDocType.CONTENT_URI, values);
    }

    private static void importAllImageCaptions(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new ImageCaptionInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }
        context.getContentResolver().bulkInsert(OnYardContract.ImageCaption.CONTENT_URI, values);
    }

    private static void importAllImageTypes(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new ImageTypeInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.ImageType.CONTENT_URI, values);
    }

    private static void importAllLossTypes(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new LossTypeInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.LossType.CONTENT_URI, values);
    }

    private static void importAllBranches(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new BranchInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.Branch.CONTENT_URI, values);
    }

    private static void importAllEngineStatuses(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new EngineStatusInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.EngineStatus.CONTENT_URI, values);
    }

    private static void importAllFeatureValues(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new FeatureValueInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.FeatureValue.CONTENT_URI, values);
    }

    private static void importAllLicensePlateConditions(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new LicensePlateConditionInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.LicensePlateCondition.CONTENT_URI,
                values);
    }

    private static void importAllOdometerReadingTypes(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new OdometerReadingTypeInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.OdometerReadingType.CONTENT_URI,
                values);
    }

    private static void importAllPublicVINs(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new PublicVinInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.PublicVIN.CONTENT_URI, values);
    }

    private static void importAllSalvageConditions(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new SalvageConditionInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver()
        .bulkInsert(OnYardContract.SalvageCondition.CONTENT_URI, values);
    }

    private static void importAllSalvageTypes(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new SalvageTypeInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.SalvageType.CONTENT_URI, values);
    }

    private static void importAllStates(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new StateInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.State.CONTENT_URI, values);
    }

    private static void importAllBodyStyleSpecialties(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new BodyStyleSpecialtyInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.BodyStyleSpecialty.CONTENT_URI,
                values);
    }

    private static void importAllCheckinFields(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new CheckinFieldInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.CheckinField.CONTENT_URI, values);
    }

    private static void importAllCheckinTemplates(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new CheckinTemplateInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.CheckinTemplate.CONTENT_URI, values);
    }

    private static void importAllEnhancements(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new EnhancementInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.Enhancement.CONTENT_URI, values);
    }

    private static void importAllAuctionSchedules(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new AuctionScheduleInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.AuctionSchedule.CONTENT_URI, values);
    }

    private static void importAllSlaSalvageEnhancements(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new SlaSalvageEnhancementInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.SlaSalvageEnhancement.CONTENT_URI,
                values);
    }

    private static void importAllSalvageEnhancements(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new SalvageEnhancementInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.SalvageEnhancement.CONTENT_URI,
                values);
    }
    private static void importAllPilotFunctions(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new PilotFunctionInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.PilotFunction.CONTENT_URI, values);
    }

    private static void importAllDisabledEnhancements(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new DisabledEnhancementInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.DisabledEnhancement.CONTENT_URI,
                values);
    }

    private static void importAllHolidays(Context context, JSONArray objectArray, int numObjects,
            ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new HolidayInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.Holiday.CONTENT_URI, values);
    }

    private static void importAllSyncWindows(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new SyncWindowInfo(objectArray.getJSONObject(index)).getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.SyncWindow.CONTENT_URI, values);
    }

    private static void importAllSyncWindowExceptions(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new SyncWindowExceptionInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.SyncWindowException.CONTENT_URI,
                values);
    }

    private static void importAllConfigs(Context context, JSONArray objectArray,
            int numObjects, ContentValues[] values) throws JSONException {
        for (int index = 0; index < numObjects; index++) {
            values[index] = new ConfigInfo(objectArray.getJSONObject(index))
            .getContentValues();
        }

        context.getContentResolver().bulkInsert(OnYardContract.Config.CONTENT_URI, values);
    }

    private static void importUpdatedVehicles(Context context, JSONArray objectArray, int numObjects)
            throws JSONException {
        VehicleInfo vehicle = null;
        Long pendingSyncTime = null;
        for (int index = 0; index < numObjects; index++) {
            vehicle = new VehicleInfo(objectArray.getJSONObject(index));

            if (index == 0) {
                pendingSyncTime = objectArray.getJSONObject(index).getLong(
                        OnYardContract.Vehicles.JSON_NAME_UPDATE_TIME_UNIX);
            }

            if (vehicle.isDeleted()) {
                context.getContentResolver().delete(
                        Uri.withAppendedPath(OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE,
                                vehicle.getStockNumber()), null, null);
            }
            else {
                if (isVehicleInDB(context, vehicle)) {
                    context.getContentResolver().update(
                            Uri.withAppendedPath(OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE,
                                    vehicle.getStockNumber()), vehicle.getContentValues(), null, null);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.Vehicles.CONTENT_URI,
                            vehicle.getContentValues());
                }
            }
        }

        if (pendingSyncTime != null) {
            insertPendingSyncTime(context, pendingSyncTime);
        }
    }

    private static void importUpdatedReshoots(Context context, JSONArray objectArray, int numObjects)
            throws JSONException {
        ImageReshootInfo reshoot = null;
        for (int index = 0; index < numObjects; index++) {
            reshoot = new ImageReshootInfo(objectArray.getJSONObject(index));

            if (reshoot.isDeleted()) {
                final String selection = OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER
                        + "=? AND " + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER
                        + "=? AND " + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_SET + "=?";
                final String[] selectionArgs = new String[] { reshoot.getStockNumber(),
                        String.valueOf(reshoot.getImageOrder()),
                        String.valueOf(reshoot.getImageSet()) };

                context.getContentResolver().delete(OnYardContract.ImageReshoot.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isReshootInDB(context, reshoot)) {
                    final String selection = OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER
                            + "=? AND " + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER
                            + "=? AND " + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_SET + "=?";
                    final String[] selectionArgs = new String[] { reshoot.getStockNumber(),
                            String.valueOf(reshoot.getImageOrder()),
                            String.valueOf(reshoot.getImageSet()) };

                    context.getContentResolver().update(OnYardContract.ImageReshoot.CONTENT_URI,
                            reshoot.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.ImageReshoot.CONTENT_URI,
                            reshoot.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedSalvageProviders(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        SalvageProviderInfo provider = null;
        for (int index = 0; index < numObjects; index++) {
            provider = new SalvageProviderInfo(objectArray.getJSONObject(index));

            if (provider.isDeleted()) {
                final String selection = OnYardContract.SalvageProvider.COLUMN_NAME_ID + "=?";
                final String[] selectionArgs = new String[] { String.valueOf(provider
                        .getSalvageProviderId()) };

                context.getContentResolver().delete(OnYardContract.SalvageProvider.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isSalvageProviderInDB(context, provider)) {
                    final String selection = OnYardContract.SalvageProvider.COLUMN_NAME_ID + "=?";
                    final String[] selectionArgs = new String[] { String.valueOf(provider
                            .getSalvageProviderId()) };

                    context.getContentResolver().update(OnYardContract.SalvageProvider.CONTENT_URI,
                            provider.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.SalvageProvider.CONTENT_URI,
                            provider.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedColors(Context context, JSONArray objectArray, int numObjects)
            throws JSONException {
        ColorInfo color = null;
        for (int index = 0; index < numObjects; index++) {
            color = new ColorInfo(objectArray.getJSONObject(index));

            if (color.isDeleted()) {
                final String selection = OnYardContract.Color.COLUMN_NAME_CODE + "=?";
                final String[] selectionArgs = new String[] { color.getColorCode() };

                context.getContentResolver().delete(OnYardContract.Color.CONTENT_URI, selection,
                        selectionArgs);
            }
            else {
                if (isColorInDB(context, color)) {
                    final String selection = OnYardContract.Color.COLUMN_NAME_CODE + "=?";
                    final String[] selectionArgs = new String[] { color.getColorCode() };

                    context.getContentResolver().update(OnYardContract.Color.CONTENT_URI,
                            color.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.Color.CONTENT_URI,
                            color.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedStatuses(Context context, JSONArray objectArray, int numObjects)
            throws JSONException {
        StatusInfo status = null;
        for (int index = 0; index < numObjects; index++) {
            status = new StatusInfo(objectArray.getJSONObject(index));

            if (isStatusInDB(context, status)) {
                final String selection = OnYardContract.Status.COLUMN_NAME_CODE + "=?";
                final String[] selectionArgs = new String[] { status.getStatusCode() };

                context.getContentResolver().update(OnYardContract.Status.CONTENT_URI,
                        status.getContentValues(), selection, selectionArgs);
            }
            else {
                context.getContentResolver().insert(OnYardContract.Status.CONTENT_URI,
                        status.getContentValues());
            }
        }
    }

    private static void importUpdatedDamages(Context context, JSONArray objectArray, int numObjects)
            throws JSONException {
        DamageInfo damage = null;
        for (int index = 0; index < numObjects; index++) {
            damage = new DamageInfo(objectArray.getJSONObject(index));

            if (damage.isDeleted()) {
                final String selection = OnYardContract.Damage.COLUMN_NAME_CODE + "=?";
                final String[] selectionArgs = new String[] { damage.getDamageCode() };

                context.getContentResolver().delete(OnYardContract.Damage.CONTENT_URI, selection,
                        selectionArgs);
            }
            else {
                if (isDamageInDB(context, damage)) {
                    final String selection = OnYardContract.Damage.COLUMN_NAME_CODE + "=?";
                    final String[] selectionArgs = new String[] { damage.getDamageCode() };

                    context.getContentResolver().update(OnYardContract.Damage.CONTENT_URI,
                            damage.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.Damage.CONTENT_URI,
                            damage.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedSaleDocTypes(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        SaleDocTypeInfo saleDocType = null;
        for (int index = 0; index < numObjects; index++) {
            saleDocType = new SaleDocTypeInfo(objectArray.getJSONObject(index));

            if (isSaleDocTypeInDB(context, saleDocType)) {
                final String selection = OnYardContract.SaleDocType.COLUMN_NAME_ID + "=?";
                final String[] selectionArgs = new String[] { String.valueOf(saleDocType
                        .getSaleDocTypeId()) };

                context.getContentResolver().update(OnYardContract.SaleDocType.CONTENT_URI,
                        saleDocType.getContentValues(), selection, selectionArgs);
            }
            else {
                context.getContentResolver().insert(OnYardContract.SaleDocType.CONTENT_URI,
                        saleDocType.getContentValues());
            }
        }
    }

    private static void importUpdatedImageCaptions(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {

        ImageCaptionInfo imageCaption = null;

        for (int index = 0; index < numObjects; index++) {
            imageCaption = new ImageCaptionInfo(objectArray.getJSONObject(index));

            if (imageCaption.isDeleted()) {
                final String selection = OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_ORDER
                        + "=? AND " + OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE
                        + "=? AND " + OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_TYPE_ID + "=?";
                final String[] selectionArgs = new String[] {
                        String.valueOf(imageCaption.getImageOrder()),
                        String.valueOf(imageCaption.getSalvageType()),
                        String.valueOf(imageCaption.getImageTypeId()) };

                context.getContentResolver().delete(OnYardContract.ImageCaption.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isImageCaptionInDB(context, imageCaption)) {
                    final String selection = OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_ORDER
                            + "=? AND " + OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE
                            + "=? AND " + OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_TYPE_ID + "=?";
                    final String[] selectionArgs = new String[] {
                            String.valueOf(imageCaption.getImageOrder()),
                            String.valueOf(imageCaption.getSalvageType()),
                            String.valueOf(imageCaption.getImageTypeId()) };

                    context.getContentResolver().update(OnYardContract.ImageCaption.CONTENT_URI,
                            imageCaption.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.ImageCaption.CONTENT_URI,
                            imageCaption.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedImageTypes(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        ImageTypeInfo imageType = null;
        for (int index = 0; index < numObjects; index++) {
            imageType = new ImageTypeInfo(objectArray.getJSONObject(index));

            if (imageType.isDeleted()) {
                final String selection = OnYardContract.ImageType.COLUMN_NAME_ID + "=?";
                final String[] selectionArgs = new String[] { String.valueOf(imageType
                        .getImageTypeId()) };

                context.getContentResolver().delete(OnYardContract.ImageType.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isImageTypeInDB(context, imageType)) {
                    final String selection = OnYardContract.ImageType.COLUMN_NAME_ID + "=?";
                    final String[] selectionArgs = new String[] { String.valueOf(imageType
                            .getImageTypeId()) };

                    context.getContentResolver().update(OnYardContract.ImageType.CONTENT_URI,
                            imageType.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.ImageType.CONTENT_URI,
                            imageType.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedLossTypes(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        LossTypeInfo lossType = null;
        for (int index = 0; index < numObjects; index++) {
            lossType = new LossTypeInfo(objectArray.getJSONObject(index));

            if (lossType.isDeleted()) {
                final String selection = OnYardContract.LossType.COLUMN_NAME_CODE + "=?";
                final String[] selectionArgs = new String[] { lossType.getLossTypeCode() };

                context.getContentResolver().delete(OnYardContract.LossType.CONTENT_URI, selection,
                        selectionArgs);
            }
            else {
                if (isLossTypeInDB(context, lossType)) {
                    final String selection = OnYardContract.LossType.COLUMN_NAME_CODE + "=?";
                    final String[] selectionArgs = new String[] { lossType.getLossTypeCode() };

                    context.getContentResolver().update(OnYardContract.LossType.CONTENT_URI,
                            lossType.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.LossType.CONTENT_URI,
                            lossType.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedBranches(Context context, JSONArray objectArray, int numObjects)
            throws JSONException {
        BranchInfo branch = null;
        for (int index = 0; index < numObjects; index++) {
            branch = new BranchInfo(objectArray.getJSONObject(index));

            if (branch.isDeleted()) {
                final String selection = OnYardContract.Branch.COLUMN_NAME_BRANCH_NUMBER + "=?";
                final String[] selectionArgs = new String[] { branch.getBranchNumber() };

                context.getContentResolver().delete(OnYardContract.Branch.CONTENT_URI, selection,
                        selectionArgs);
            }
            else {
                if (isBranchInDB(context, branch)) {
                    final String selection = OnYardContract.Branch.COLUMN_NAME_BRANCH_NUMBER + "=?";
                    final String[] selectionArgs = new String[] { branch.getBranchNumber() };

                    context.getContentResolver().update(OnYardContract.Branch.CONTENT_URI,
                            branch.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.Branch.CONTENT_URI,
                            branch.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedEngineStatuses(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        EngineStatusInfo engineStatus = null;
        for (int index = 0; index < numObjects; index++) {
            engineStatus = new EngineStatusInfo(objectArray.getJSONObject(index));

            if (engineStatus.isDeleted()) {
                final String selection = OnYardContract.EngineStatus.COLUMN_NAME_CODE + "=?";
                final String[] selectionArgs = new String[] { engineStatus.getEngineStatusCode() };

                context.getContentResolver().delete(OnYardContract.EngineStatus.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isEngineStatusInDB(context, engineStatus)) {
                    final String selection = OnYardContract.EngineStatus.COLUMN_NAME_CODE + "=?";
                    final String[] selectionArgs = new String[] { engineStatus
                            .getEngineStatusCode() };

                    context.getContentResolver().update(OnYardContract.EngineStatus.CONTENT_URI,
                            engineStatus.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.EngineStatus.CONTENT_URI,
                            engineStatus.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedFeatureValues(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        FeatureValueInfo featureValue = null;
        for (int index = 0; index < numObjects; index++) {
            featureValue = new FeatureValueInfo(objectArray.getJSONObject(index));

            if (featureValue.isDeleted()) {
                final String selection = OnYardContract.FeatureValue.COLUMN_NAME_CODE + "=? AND "
                        + OnYardContract.FeatureValue.COLUMN_NAME_VALUE + "=?";
                final String[] selectionArgs = new String[] { featureValue.getFeatureCode(),
                        featureValue.getFeatureValue() };

                context.getContentResolver().delete(OnYardContract.FeatureValue.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isFeatureValueInDB(context, featureValue)) {
                    final String selection = OnYardContract.FeatureValue.COLUMN_NAME_CODE
                            + "=? AND "
                            + OnYardContract.FeatureValue.COLUMN_NAME_VALUE + "=?";
                    final String[] selectionArgs = new String[] { featureValue.getFeatureCode(),
                            featureValue.getFeatureValue() };

                    context.getContentResolver().update(OnYardContract.FeatureValue.CONTENT_URI,
                            featureValue.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.FeatureValue.CONTENT_URI,
                            featureValue.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedLicensePlateConditions(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        LicensePlateConditionInfo licensePlateCondition = null;
        for (int index = 0; index < numObjects; index++) {
            licensePlateCondition = new LicensePlateConditionInfo(objectArray.getJSONObject(index));

            if (licensePlateCondition.isDeleted()) {
                final String selection = OnYardContract.LicensePlateCondition.COLUMN_NAME_CODE
                        + "=?";
                final String[] selectionArgs = new String[] { licensePlateCondition
                        .getLicensePlateConditionCode() };

                context.getContentResolver().delete(
                        OnYardContract.LicensePlateCondition.CONTENT_URI, selection, selectionArgs);
            }
            else {
                if (isLicensePlateConditionInDB(context, licensePlateCondition)) {
                    final String selection = OnYardContract.LicensePlateCondition.COLUMN_NAME_CODE
                            + "=?";
                    final String[] selectionArgs = new String[] { licensePlateCondition
                            .getLicensePlateConditionCode() };

                    context.getContentResolver().update(
                            OnYardContract.LicensePlateCondition.CONTENT_URI,
                            licensePlateCondition.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(
                            OnYardContract.LicensePlateCondition.CONTENT_URI,
                            licensePlateCondition.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedOdometerReadingTypes(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        OdometerReadingTypeInfo odometerReadingType = null;
        for (int index = 0; index < numObjects; index++) {
            odometerReadingType = new OdometerReadingTypeInfo(objectArray.getJSONObject(index));

            if (odometerReadingType.isDeleted()) {
                final String selection = OnYardContract.OdometerReadingType.COLUMN_NAME_CODE + "=?";
                final String[] selectionArgs = new String[] { odometerReadingType
                        .getOdometerReadingTypeCode() };

                context.getContentResolver().delete(OnYardContract.OdometerReadingType.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isOdometerReadingTypeInDB(context, odometerReadingType)) {
                    final String selection = OnYardContract.OdometerReadingType.COLUMN_NAME_CODE
                            + "=?";
                    final String[] selectionArgs = new String[] { odometerReadingType
                            .getOdometerReadingTypeCode() };

                    context.getContentResolver().update(
                            OnYardContract.OdometerReadingType.CONTENT_URI,
                            odometerReadingType.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(
                            OnYardContract.OdometerReadingType.CONTENT_URI,
                            odometerReadingType.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedPublicVINs(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        PublicVinInfo publicVIN = null;
        for (int index = 0; index < numObjects; index++) {
            publicVIN = new PublicVinInfo(objectArray.getJSONObject(index));

            if (publicVIN.isDeleted()) {
                final String selection = OnYardContract.PublicVIN.COLUMN_NAME_CODE + "=?";
                final String[] selectionArgs = new String[] { publicVIN.getPublicVinCode() };

                context.getContentResolver().delete(OnYardContract.PublicVIN.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isPublicVinInDB(context, publicVIN)) {
                    final String selection = OnYardContract.PublicVIN.COLUMN_NAME_CODE + "=?";
                    final String[] selectionArgs = new String[] { publicVIN.getPublicVinCode() };

                    context.getContentResolver().update(OnYardContract.PublicVIN.CONTENT_URI,
                            publicVIN.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.PublicVIN.CONTENT_URI,
                            publicVIN.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedSalvageConditions(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        SalvageConditionInfo salvageCondition = null;
        for (int index = 0; index < numObjects; index++) {
            salvageCondition = new SalvageConditionInfo(objectArray.getJSONObject(index));

            if (salvageCondition.isDeleted()) {
                final String selection = OnYardContract.SalvageCondition.COLUMN_NAME_CODE + "=?";
                final String[] selectionArgs = new String[] { salvageCondition
                        .getSalvageConditionCode() };

                context.getContentResolver().delete(OnYardContract.SalvageCondition.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isSalvageConditionInDB(context, salvageCondition)) {
                    final String selection = OnYardContract.SalvageCondition.COLUMN_NAME_CODE
                            + "=?";
                    final String[] selectionArgs = new String[] { salvageCondition
                            .getSalvageConditionCode() };

                    context.getContentResolver().update(
                            OnYardContract.SalvageCondition.CONTENT_URI,
                            salvageCondition.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(
                            OnYardContract.SalvageCondition.CONTENT_URI,
                            salvageCondition.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedSalvageTypes(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        SalvageTypeInfo salvageType = null;
        for (int index = 0; index < numObjects; index++) {
            salvageType = new SalvageTypeInfo(objectArray.getJSONObject(index));

            if (salvageType.isDeleted()) {
                final String selection = OnYardContract.SalvageType.COLUMN_NAME_TYPE + "=?";
                final String[] selectionArgs = new String[] { salvageType.getSalvageType() };

                context.getContentResolver().delete(OnYardContract.SalvageType.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isSalvageTypeInDB(context, salvageType)) {
                    final String selection = OnYardContract.SalvageType.COLUMN_NAME_TYPE + "=?";
                    final String[] selectionArgs = new String[] { salvageType.getSalvageType() };

                    context.getContentResolver().update(OnYardContract.SalvageType.CONTENT_URI,
                            salvageType.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.SalvageType.CONTENT_URI,
                            salvageType.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedStates(Context context, JSONArray objectArray, int numObjects)
            throws JSONException {
        StateInfo state = null;
        for (int index = 0; index < numObjects; index++) {
            state = new StateInfo(objectArray.getJSONObject(index));

            if (state.isDeleted()) {
                final String selection = OnYardContract.State.COLUMN_NAME_ABBREVIATION + "=?";
                final String[] selectionArgs = new String[] { state.getStateAbbr() };

                context.getContentResolver().delete(OnYardContract.State.CONTENT_URI, selection,
                        selectionArgs);
            }
            else {
                if (isStateInDB(context, state)) {
                    final String selection = OnYardContract.State.COLUMN_NAME_ABBREVIATION + "=?";
                    final String[] selectionArgs = new String[] { state.getStateAbbr() };

                    context.getContentResolver().update(OnYardContract.State.CONTENT_URI,
                            state.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.State.CONTENT_URI,
                            state.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedBodyStyleSpecialties(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        BodyStyleSpecialtyInfo bodyStyleSpecialty = null;
        for (int index = 0; index < numObjects; index++) {
            bodyStyleSpecialty = new BodyStyleSpecialtyInfo(objectArray.getJSONObject(index));

            if (bodyStyleSpecialty.isDeleted()) {
                final String selection = OnYardContract.BodyStyleSpecialty.COLUMN_NAME_SALVAGE_TYPE
                        + "=? AND " + OnYardContract.BodyStyleSpecialty.COLUMN_NAME_BODY_STYLE_NAME
                        + "=?";
                final String[] selectionArgs = new String[] { bodyStyleSpecialty.getSalvageType(),
                        bodyStyleSpecialty.getBodyStyleName() };

                context.getContentResolver().delete(OnYardContract.BodyStyleSpecialty.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isBodyStyleSpecialtyInDB(context, bodyStyleSpecialty)) {
                    final String selection = OnYardContract.BodyStyleSpecialty.COLUMN_NAME_SALVAGE_TYPE
                            + "=? AND "
                            + OnYardContract.BodyStyleSpecialty.COLUMN_NAME_BODY_STYLE_NAME + "=?";
                    final String[] selectionArgs = new String[] {
                            bodyStyleSpecialty.getSalvageType(),
                            bodyStyleSpecialty.getBodyStyleName() };

                    context.getContentResolver().update(
                            OnYardContract.BodyStyleSpecialty.CONTENT_URI,
                            bodyStyleSpecialty.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(
                            OnYardContract.BodyStyleSpecialty.CONTENT_URI,
                            bodyStyleSpecialty.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedCheckinFields(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        CheckinFieldInfo checkinField = null;
        for (int index = 0; index < numObjects; index++) {
            checkinField = new CheckinFieldInfo(objectArray.getJSONObject(index));

            final String selection = OnYardContract.CheckinField.COLUMN_NAME_ID + "=?";
            final String[] selectionArgs = new String[] { String.valueOf(checkinField.getId()) };
            if (checkinField.isDeleted()) {
                context.getContentResolver().delete(OnYardContract.CheckinField.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isCheckinFieldInDB(context, checkinField)) {
                    context.getContentResolver().update(OnYardContract.CheckinField.CONTENT_URI,
                            checkinField.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.CheckinField.CONTENT_URI,
                            checkinField.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedCheckinTemplates(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        CheckinTemplateInfo checkinTemplate = null;
        for (int index = 0; index < numObjects; index++) {
            checkinTemplate = new CheckinTemplateInfo(objectArray.getJSONObject(index));

            final String selection = OnYardContract.CheckinTemplate.COLUMN_NAME_SALVAGE_TYPE
                    + "=? AND " + OnYardContract.CheckinTemplate.COLUMN_NAME_CHECKIN_FIELD_ID
                    + "=?";
            final String[] selectionArgs = new String[] { checkinTemplate.getSalvageType(),
                    String.valueOf(checkinTemplate.getCheckinFieldId()) };
            if (checkinTemplate.isDeleted()) {
                context.getContentResolver().delete(OnYardContract.CheckinTemplate.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isCheckinTemplateInDB(context, checkinTemplate)) {
                    context.getContentResolver().update(OnYardContract.CheckinTemplate.CONTENT_URI,
                            checkinTemplate.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.CheckinTemplate.CONTENT_URI,
                            checkinTemplate.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedEnhancements(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        EnhancementInfo enhancement = null;
        for (int index = 0; index < numObjects; index++) {
            enhancement = new EnhancementInfo(objectArray.getJSONObject(index));

            if (enhancement.isDeleted()) {
                final String selection = OnYardContract.Enhancement.COLUMN_NAME_ID + "=?";
                final String[] selectionArgs = new String[] { String.valueOf(enhancement
                        .getEnhancementId()) };

                context.getContentResolver().delete(OnYardContract.Enhancement.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isEnhancementInDB(context, enhancement)) {
                    final String selection = OnYardContract.Enhancement.COLUMN_NAME_ID + "=?";
                    final String[] selectionArgs = new String[] { String.valueOf(enhancement
                            .getEnhancementId()) };

                    context.getContentResolver().update(OnYardContract.Enhancement.CONTENT_URI,
                            enhancement.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.Enhancement.CONTENT_URI,
                            enhancement.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedAuctionSchedules(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        AuctionScheduleInfo auctionSchedule = null;
        for (int index = 0; index < numObjects; index++) {
            auctionSchedule = new AuctionScheduleInfo(objectArray.getJSONObject(index));

            if (auctionSchedule.isDeleted()) {
                final String selection = OnYardContract.AuctionSchedule.COLUMN_NAME_ID
                        + "=?";
                final String[] selectionArgs = new String[] { String.valueOf(auctionSchedule
                        .getAuctionScheduleId()) };

                context.getContentResolver().delete(OnYardContract.AuctionSchedule.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isAuctionScheduleInDB(context, auctionSchedule)) {
                    final String selection = OnYardContract.AuctionSchedule.COLUMN_NAME_ID
                            + "=?";
                    final String[] selectionArgs = new String[] { String.valueOf(auctionSchedule
                            .getAuctionScheduleId()) };

                    context.getContentResolver().update(OnYardContract.AuctionSchedule.CONTENT_URI,
                            auctionSchedule.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.AuctionSchedule.CONTENT_URI,
                            auctionSchedule.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedSlaSalvageEnhancements(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        SlaSalvageEnhancementInfo slaEnhancement = null;
        for (int index = 0; index < numObjects; index++) {
            slaEnhancement = new SlaSalvageEnhancementInfo(objectArray.getJSONObject(index));

            final String selection = OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_STOCK_NUMBER
                    + "=? AND " + OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_ENHANCEMENT_ID
                    + "=?";
            final String[] selectionArgs = new String[] { slaEnhancement.getStockNumber(),
                    String.valueOf(slaEnhancement.getEnhancementId()) };

            if (slaEnhancement.isDeleted()) {
                context.getContentResolver().delete(
                        OnYardContract.SlaSalvageEnhancement.CONTENT_URI, selection, selectionArgs);
            }
            else {
                if (isSlaSalvageEnhancementInDB(context, slaEnhancement)) {
                    context.getContentResolver().update(
                            OnYardContract.SlaSalvageEnhancement.CONTENT_URI,
                            slaEnhancement.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(
                            OnYardContract.SlaSalvageEnhancement.CONTENT_URI,
                            slaEnhancement.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedSalvageEnhancements(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        SalvageEnhancementInfo salvageEnhancement = null;
        for (int index = 0; index < numObjects; index++) {
            salvageEnhancement = new SalvageEnhancementInfo(objectArray.getJSONObject(index));

            final String selection = OnYardContract.SalvageEnhancement.COLUMN_NAME_STOCK_NUMBER
                    + "=? AND " + OnYardContract.SalvageEnhancement.COLUMN_NAME_ENHANCEMENT_ID
                    + "=?";
            final String[] selectionArgs = new String[] { salvageEnhancement.getStockNumber(),
                    String.valueOf(salvageEnhancement.getEnhancementId()) };

            if (salvageEnhancement.isDeleted()) {
                context.getContentResolver().delete(OnYardContract.SalvageEnhancement.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isSalvageEnhancementInDB(context, salvageEnhancement)) {
                    context.getContentResolver().update(
                            OnYardContract.SalvageEnhancement.CONTENT_URI,
                            salvageEnhancement.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(
                            OnYardContract.SalvageEnhancement.CONTENT_URI,
                            salvageEnhancement.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedPilotFunctions(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        PilotFunctionInfo pilotFunction = null;
        for (int index = 0; index < numObjects; index++) {
            pilotFunction = new PilotFunctionInfo(objectArray.getJSONObject(index));

            final String selection = OnYardContract.PilotFunction.COLUMN_NAME_FUNCTION_ID
                    + "=? AND " + OnYardContract.PilotFunction.COLUMN_NAME_BRANCH_NUMBER + "=?";
            final String[] selectionArgs = new String[] {
                    String.valueOf(pilotFunction.getFunctionId()),
                    String.valueOf(pilotFunction.getBranchNumber()) };

            if (pilotFunction.isDeleted()) {
                context.getContentResolver().delete(OnYardContract.PilotFunction.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isPilotFunctionInDB(context, pilotFunction)) {
                    context.getContentResolver().update(OnYardContract.PilotFunction.CONTENT_URI,
                            pilotFunction.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.PilotFunction.CONTENT_URI,
                            pilotFunction.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedDisabledEnhancements(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {
        DisabledEnhancementInfo enhancement = null;


        for (int index = 0; index < numObjects; index++) {
            enhancement = new DisabledEnhancementInfo(objectArray.getJSONObject(index));
            final String selection = OnYardContract.DisabledEnhancement.COLUMN_NAME_ID + "=?";
            final String[] selectionArgs = new String[] { String.valueOf(enhancement
                    .getEnhancementId()) };

            if (enhancement.isDeleted()) {
                context.getContentResolver().delete(OnYardContract.DisabledEnhancement.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isDisabledEnhancementInDB(context, enhancement)) {
                    context.getContentResolver().update(
                            OnYardContract.DisabledEnhancement.CONTENT_URI,
                            enhancement.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(
                            OnYardContract.DisabledEnhancement.CONTENT_URI,
                            enhancement.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedHolidays(Context context, JSONArray objectArray, int numObjects)
            throws JSONException {
        HolidayInfo holiday = null;
        for (int index = 0; index < numObjects; index++) {
            holiday = new HolidayInfo(objectArray.getJSONObject(index));

            if (holiday.isDeleted()) {
                final String selection = OnYardContract.Holiday.COLUMN_NAME_ID + "=?";
                final String[] selectionArgs = new String[] { String.format("%d",
                        holiday.getHolidayId()) };

                context.getContentResolver().delete(OnYardContract.Holiday.CONTENT_URI, selection,
                        selectionArgs);
            }
            else {
                if (isHolidayInDB(context, holiday)) {
                    final String selection = OnYardContract.Holiday.COLUMN_NAME_ID + "=?";
                    final String[] selectionArgs = new String[] { String.format("%d",
                            holiday.getHolidayId()) };

                    context.getContentResolver().update(OnYardContract.Holiday.CONTENT_URI,
                            holiday.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.Holiday.CONTENT_URI,
                            holiday.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedSyncWindows(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {

        SyncWindowInfo syncWindow = null;
        for (int index = 0; index < numObjects; index++) {
            syncWindow = new SyncWindowInfo(objectArray.getJSONObject(index));

            final String selection = OnYardContract.SyncWindow.COLUMN_NAME_DAY_OF_WEEK + "=? AND "
                    + OnYardContract.SyncWindow.COLUMN_NAME_START_TIME + "=?";
            final String[] selectionArgs = new String[] {
                    String.valueOf(syncWindow.getDayOfWeek()),
                    String.valueOf(syncWindow.getStartTime()) };

            if (syncWindow.isDeleted()) {
                context.getContentResolver().delete(OnYardContract.SyncWindow.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isSyncWindowInDB(context, syncWindow)) {
                    context.getContentResolver().update(OnYardContract.SyncWindow.CONTENT_URI,
                            syncWindow.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.SyncWindow.CONTENT_URI,
                            syncWindow.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedSyncWindowExceptions(Context context,
            JSONArray objectArray,
            int numObjects) throws JSONException {

        SyncWindowExceptionInfo syncWindowException = null;
        for (int index = 0; index < numObjects; index++) {
            syncWindowException = new SyncWindowExceptionInfo(objectArray.getJSONObject(index));

            final String selection = OnYardContract.SyncWindowException.COLUMN_NAME_SALVAGE_PROVIDER_ID
                    + "=?";
            final String[] selectionArgs = new String[] {
                    String.valueOf(syncWindowException
                            .getSalvageProviderId()) };

            if (syncWindowException.isDeleted()) {
                context.getContentResolver().delete(OnYardContract.SyncWindowException.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isSyncWindowExceptionInDB(context, syncWindowException)) {
                    context.getContentResolver().update(
                            OnYardContract.SyncWindowException.CONTENT_URI,
                            syncWindowException.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(
                            OnYardContract.SyncWindowException.CONTENT_URI,
                            syncWindowException.getContentValues());
                }
            }
        }
    }

    private static void importUpdatedConfigs(Context context, JSONArray objectArray,
            int numObjects) throws JSONException {

        ConfigInfo config = null;
        for (int index = 0; index < numObjects; index++) {
            config = new ConfigInfo(objectArray.getJSONObject(index));

            final String selection = OnYardContract.Config.COLUMN_NAME_KEY + "=?";
            final String[] selectionArgs = new String[] { config.getConfigKey() };

            if (config.isDeleted()) {
                context.getContentResolver().delete(OnYardContract.Config.CONTENT_URI,
                        selection, selectionArgs);
            }
            else {
                if (isConfigInDB(context, config)) {
                    context.getContentResolver().update(OnYardContract.Config.CONTENT_URI,
                            config.getContentValues(), selection, selectionArgs);
                }
                else {
                    context.getContentResolver().insert(OnYardContract.Config.CONTENT_URI,
                            config.getContentValues());
                }
            }
        }
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
            throws IOException, JSONException {
        final StringBuilder sb = new StringBuilder();
        final int rightBraceIndex = getRightBraceIndex(reader, numObjects);

        if (rightBraceIndex >= 0) {
            final char[] jsonChars = new char[rightBraceIndex];

            reader.read();
            sb.append('[');

            reader.read(jsonChars, 0, rightBraceIndex);
            sb.append(jsonChars);

            sb.append(']');

            return new JSONArray(sb.toString());
        }
        else {
            return new JSONArray();
        }
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

            if(fileChar == '}') {
                numObjectsFound++;
            }

            fileIndex++;
        }

        reader.reset();

        if (intChar == -1)
        {
            mIsEndOfFile = true;
            return fileIndex - 2; //account for EOF and ']'
        }
        else {
            return fileIndex - 1;	//account for ','
        }
    }

    /**
     * Check if record with specified stock number exists in the Vehicles table of the OnYard
     * ContentProvider.
     * 
     * @param context The current context.
     * @param vehicle The vehicle for which to search.
     * @return True if the stock number exists in the Vehicles table, false if it does not exist.
     */
    private static boolean isVehicleInDB(Context context, VehicleInfo vehicle) {
        final Uri uri = Uri.withAppendedPath(OnYardContract.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE,
                vehicle.getStockNumber());
        final String[] projection = new String[] { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER };
        final String selection = null;
        final String[] selectionArgs = null;

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    /**
     * Check if the specified reshoot exists in the ImageReshoot table of the OnYard
     * ContentProvider.
     * 
     * @param context The current context.
     * @param reshoot The reshoot for which to search.
     * @return True if the stock number exists in the ImageReshoot table, false if it does not
     *         exist.
     */
    private static boolean isReshootInDB(Context context, ImageReshootInfo reshoot) {
        final Uri uri = OnYardContract.ImageReshoot.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER };
        final String selection = OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER
                + "=? AND " + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER + "=? AND "
                + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_SET + "=?";
        final String[] selectionArgs = new String[] { reshoot.getStockNumber(),
                String.valueOf(reshoot.getImageOrder()), String.valueOf(reshoot.getImageSet()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    /**
     * Check if the specified salvage provider exists in the SalvageProvider table of the OnYard
     * ContentProvider.
     * 
     * @param context The current context.
     * @param provider The salvage provider for which to search.
     * @return True if the provider exists in the SalvageProvider table, false if it does not exist.
     */
    private static boolean isSalvageProviderInDB(Context context, SalvageProviderInfo provider) {
        final Uri uri = OnYardContract.SalvageProvider.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.SalvageProvider.COLUMN_NAME_ID };
        final String selection = OnYardContract.SalvageProvider.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = new String[] { String.valueOf(provider
                .getSalvageProviderId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isColorInDB(Context context, ColorInfo color) {
        final Uri uri = OnYardContract.Color.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.Color.COLUMN_NAME_CODE };
        final String selection = OnYardContract.Color.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { color.getColorCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isStatusInDB(Context context, StatusInfo status) {
        final Uri uri = OnYardContract.Status.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.Status.COLUMN_NAME_CODE };
        final String selection = OnYardContract.Status.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { status.getStatusCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isDamageInDB(Context context, DamageInfo damage) {
        final Uri uri = OnYardContract.Damage.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.Damage.COLUMN_NAME_CODE };
        final String selection = OnYardContract.Damage.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { damage.getDamageCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isSaleDocTypeInDB(Context context, SaleDocTypeInfo saleDocType) {
        final Uri uri = OnYardContract.SaleDocType.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.SaleDocType.COLUMN_NAME_ID };
        final String selection = OnYardContract.SaleDocType.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = new String[] { String.valueOf(saleDocType
                .getSaleDocTypeId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isImageCaptionInDB(Context context, ImageCaptionInfo imageCaption) {
        final Uri uri = OnYardContract.ImageCaption.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.ImageCaption.COLUMN_NAME_ID };
        final String selection = OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_ORDER
                + "=? AND " + OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE + "=? AND "
                + OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_TYPE_ID + "=?";
        final String[] selectionArgs = new String[] {
                String.valueOf(imageCaption.getImageOrder()),
                String.valueOf(imageCaption.getSalvageType()),
                String.valueOf(imageCaption.getImageTypeId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    /**
     * Check if the specified image type exists in the ImageType table of the OnYard
     * ContentProvider.
     * 
     * @param context The current context.
     * @param imageType The image type for which to search.
     * @return True if the record exists in the ImageType table, false if it does not exist.
     */
    private static boolean isImageTypeInDB(Context context, ImageTypeInfo imageType) {
        final Uri uri = OnYardContract.ImageType.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.ImageType.COLUMN_NAME_ID };
        final String selection = OnYardContract.ImageType.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = new String[] { String
                .valueOf(imageType.getImageTypeId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isLossTypeInDB(Context context, LossTypeInfo lossType) {
        final Uri uri = OnYardContract.LossType.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.LossType.COLUMN_NAME_CODE };
        final String selection = OnYardContract.LossType.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { lossType.getLossTypeCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isBranchInDB(Context context, BranchInfo branch) {
        final Uri uri = OnYardContract.Branch.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.Branch.COLUMN_NAME_BRANCH_NUMBER };
        final String selection = OnYardContract.Branch.COLUMN_NAME_BRANCH_NUMBER + "=?";
        final String[] selectionArgs = new String[] { branch.getBranchNumber() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isEngineStatusInDB(Context context, EngineStatusInfo engineStatus) {
        final Uri uri = OnYardContract.EngineStatus.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.EngineStatus.COLUMN_NAME_CODE };
        final String selection = OnYardContract.EngineStatus.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { engineStatus.getEngineStatusCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isFeatureValueInDB(Context context, FeatureValueInfo featureValue) {
        final Uri uri = OnYardContract.FeatureValue.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.FeatureValue.COLUMN_NAME_CODE };
        final String selection = OnYardContract.FeatureValue.COLUMN_NAME_CODE + "=? AND "
                + OnYardContract.FeatureValue.COLUMN_NAME_VALUE + "=?";
        final String[] selectionArgs = new String[] { featureValue.getFeatureCode(),
                featureValue.getFeatureValue() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isLicensePlateConditionInDB(Context context,
            LicensePlateConditionInfo licensePlateCondition) {
        final Uri uri = OnYardContract.LicensePlateCondition.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.LicensePlateCondition.COLUMN_NAME_CODE };
        final String selection = OnYardContract.LicensePlateCondition.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { licensePlateCondition
                .getLicensePlateConditionCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isOdometerReadingTypeInDB(Context context,
            OdometerReadingTypeInfo odometerReadingType) {
        final Uri uri = OnYardContract.OdometerReadingType.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.OdometerReadingType.COLUMN_NAME_CODE };
        final String selection = OnYardContract.OdometerReadingType.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { odometerReadingType
                .getOdometerReadingTypeCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isPublicVinInDB(Context context, PublicVinInfo publicVIN) {
        final Uri uri = OnYardContract.PublicVIN.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.PublicVIN.COLUMN_NAME_CODE };
        final String selection = OnYardContract.PublicVIN.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { publicVIN.getPublicVinCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isSalvageConditionInDB(Context context,
            SalvageConditionInfo salvageCondition) {
        final Uri uri = OnYardContract.SalvageCondition.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.SalvageCondition.COLUMN_NAME_CODE };
        final String selection = OnYardContract.SalvageCondition.COLUMN_NAME_CODE + "=?";
        final String[] selectionArgs = new String[] { salvageCondition.getSalvageConditionCode() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isSalvageTypeInDB(Context context, SalvageTypeInfo salvageType) {
        final Uri uri = OnYardContract.SalvageType.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.SalvageType.COLUMN_NAME_TYPE };
        final String selection = OnYardContract.SalvageType.COLUMN_NAME_TYPE + "=?";
        final String[] selectionArgs = new String[] { salvageType.getSalvageType() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isStateInDB(Context context, StateInfo state) {
        final Uri uri = OnYardContract.State.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.State.COLUMN_NAME_ABBREVIATION };
        final String selection = OnYardContract.State.COLUMN_NAME_ABBREVIATION + "=?";
        final String[] selectionArgs = new String[] { state.getStateAbbr() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isBodyStyleSpecialtyInDB(Context context,
            BodyStyleSpecialtyInfo bodyStyleSpecialty) {
        final Uri uri = OnYardContract.BodyStyleSpecialty.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.BodyStyleSpecialty.COLUMN_NAME_SALVAGE_TYPE };
        final String selection = OnYardContract.BodyStyleSpecialty.COLUMN_NAME_SALVAGE_TYPE
                + "=? AND " + OnYardContract.BodyStyleSpecialty.COLUMN_NAME_BODY_STYLE_NAME + "=?";
        final String[] selectionArgs = new String[] { bodyStyleSpecialty.getSalvageType(),
                bodyStyleSpecialty.getBodyStyleName() };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isCheckinFieldInDB(Context context, CheckinFieldInfo checkinField) {
        final Uri uri = OnYardContract.CheckinField.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.CheckinField.COLUMN_NAME_ID };
        final String selection = OnYardContract.CheckinField.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = new String[] { String.valueOf(checkinField.getId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isCheckinTemplateInDB(Context context,
            CheckinTemplateInfo checkinTemplate) {
        final Uri uri = OnYardContract.CheckinTemplate.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.CheckinTemplate.COLUMN_NAME_SALVAGE_TYPE };
        final String selection = OnYardContract.CheckinTemplate.COLUMN_NAME_SALVAGE_TYPE
                + "=? AND " + OnYardContract.CheckinTemplate.COLUMN_NAME_CHECKIN_FIELD_ID + "=?";
        final String[] selectionArgs = new String[] { checkinTemplate.getSalvageType(),
                String.valueOf(checkinTemplate.getCheckinFieldId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isEnhancementInDB(Context context, EnhancementInfo enhancement) {
        final Uri uri = OnYardContract.Enhancement.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.Enhancement.COLUMN_NAME_ID };
        final String selection = OnYardContract.Enhancement.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = new String[] { String
                .valueOf(enhancement.getEnhancementId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isAuctionScheduleInDB(Context context,
            AuctionScheduleInfo auctionSchedule) {
        final Uri uri = OnYardContract.AuctionSchedule.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.AuctionSchedule.COLUMN_NAME_ID };
        final String selection = OnYardContract.AuctionSchedule.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = new String[] { String.valueOf(auctionSchedule
                .getAuctionScheduleId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isSlaSalvageEnhancementInDB(Context context,
            SlaSalvageEnhancementInfo slaEnhancement) {
        final Uri uri = OnYardContract.SlaSalvageEnhancement.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_STOCK_NUMBER };
        final String selection = OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_STOCK_NUMBER
                + "=? AND " + OnYardContract.SlaSalvageEnhancement.COLUMN_NAME_ENHANCEMENT_ID
                + "=?";
        final String[] selectionArgs = new String[] { slaEnhancement.getStockNumber(),
                String.valueOf(slaEnhancement.getEnhancementId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isSalvageEnhancementInDB(Context context,
            SalvageEnhancementInfo salvageEnhancement) {
        final Uri uri = OnYardContract.SalvageEnhancement.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.SalvageEnhancement.COLUMN_NAME_STOCK_NUMBER };
        final String selection = OnYardContract.SalvageEnhancement.COLUMN_NAME_STOCK_NUMBER
                + "=? AND " + OnYardContract.SalvageEnhancement.COLUMN_NAME_ENHANCEMENT_ID + "=?";
        final String[] selectionArgs = new String[] { salvageEnhancement.getStockNumber(),
                String.valueOf(salvageEnhancement.getEnhancementId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }
    private static boolean isPilotFunctionInDB(Context context, PilotFunctionInfo pilotFunction) {
        final Uri uri = OnYardContract.PilotFunction.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.PilotFunction.COLUMN_NAME_FUNCTION_ID };
        final String selection = OnYardContract.PilotFunction.COLUMN_NAME_FUNCTION_ID + "=? AND "
                + OnYardContract.PilotFunction.COLUMN_NAME_BRANCH_NUMBER + "=?";
        final String[] selectionArgs = new String[] {
                String.valueOf(pilotFunction.getFunctionId()),
                String.valueOf(pilotFunction.getBranchNumber()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isDisabledEnhancementInDB(Context context,
            DisabledEnhancementInfo enhancement) {
        final Uri uri = OnYardContract.DisabledEnhancement.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.DisabledEnhancement.COLUMN_NAME_ID };
        final String selection = OnYardContract.DisabledEnhancement.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = new String[] { String
                .valueOf(enhancement.getEnhancementId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isHolidayInDB(Context context, HolidayInfo holiday) {
        final Uri uri = OnYardContract.Holiday.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.Holiday.COLUMN_NAME_ID };
        final String selection = OnYardContract.Holiday.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = new String[] { String.format("%d", holiday.getHolidayId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isSyncWindowInDB(Context context, SyncWindowInfo syncWindow) {
        final Uri uri = OnYardContract.SyncWindow.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.SyncWindow.COLUMN_NAME_START_TIME };
        final String selection = OnYardContract.SyncWindow.COLUMN_NAME_DAY_OF_WEEK + "=? AND "
                + OnYardContract.SyncWindow.COLUMN_NAME_START_TIME + "=?";
        final String[] selectionArgs = new String[] { String.valueOf(syncWindow.getDayOfWeek()),
                String.valueOf(syncWindow.getStartTime()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isSyncWindowExceptionInDB(Context context,
            SyncWindowExceptionInfo syncWindowException) {
        final Uri uri = OnYardContract.SyncWindowException.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.SyncWindowException.COLUMN_NAME_SALVAGE_PROVIDER_ID };
        final String selection = OnYardContract.SyncWindowException.COLUMN_NAME_SALVAGE_PROVIDER_ID
                + "=?";
        final String[] selectionArgs = new String[] {
                String.valueOf(syncWindowException
                        .getSalvageProviderId()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean isConfigInDB(Context context, ConfigInfo config) {
        final Uri uri = OnYardContract.Config.CONTENT_URI;
        final String[] projection = new String[] { OnYardContract.Config.COLUMN_NAME_VALUE };
        final String selection = OnYardContract.Config.COLUMN_NAME_KEY + "=?";
        final String[] selectionArgs = new String[] { String.valueOf(config.getConfigKey()) };

        return recordExists(context, uri, projection, selection, selectionArgs);
    }

    private static boolean recordExists(Context context, Uri uri, String[] projection,
            String selection, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);

            return cursor.getCount() > 0;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * If pending update time already exists in Config table, update it with specified value.
     * Otherwise, insert a new pending update time record with the specified value.
     * 
     * @param context The current context.
     * @param updateTime The update time value to update/insert.
     */
    private static void insertPendingSyncTime(Context context, Long updateTime) {
        if(updateTime == null) {
            throw new IllegalArgumentException("Could not get pending update datetime from JSON");
        }

        final String configKey = OnYardContract.Config.CONFIG_KEY_PENDING_UPDATE_DATE_TIME;

        final ContentValues values = new ContentValues();
        values.put(OnYardContract.Config.COLUMN_NAME_KEY, configKey);
        values.put(OnYardContract.Config.COLUMN_NAME_VALUE, updateTime);

        if (DataHelper.isConfigKeyInDB(context, configKey)) {
            context.getContentResolver().update(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_KEY_URI_BASE, configKey),
                    values,
                    null,
                    null
                    );
        }
        else {
            context.getContentResolver().insert(
                    OnYardContract.Config.CONTENT_URI,
                    values
                    );
        }
    }
}
