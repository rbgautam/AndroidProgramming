package com.iaai.onyard.sync;

import android.net.Uri;

import com.iaai.onyard.sync.JSONHelper.ImportObject;
import com.iaai.onyardproviderapi.contract.OnYardContract;


public class SyncTable {

    private final String mJsonFileName;
    private final String mWcfPathname;
    private final int mImportObject;
    private final Uri mContentUri;

    public SyncTable(String jsonFileName, String wcfPathname, int importObject, Uri contentUri) {
        mJsonFileName = jsonFileName;
        mWcfPathname = wcfPathname;
        mImportObject = importObject;
        mContentUri = contentUri;
    }

    public String getJsonFileName() {
        return mJsonFileName;
    }

    public String getWcfPathname() {
        return mWcfPathname;
    }

    public int getImportObject() {
        return mImportObject;
    }

    public Uri getContentUri() {
        return mContentUri;
    }

    private static final String VEHICLES_PATHNAME = "/onyarddata/vehicles2";
    private static final String COLOR_PATHNAME = "/onyarddata/color2";
    private static final String STATUS_PATHNAME = "/onyarddata/status2";
    private static final String DAMAGE_PATHNAME = "/onyarddata/damage2";
    private static final String SALE_DOC_TYPE_PATHNAME = "/onyarddata/saledoctype2";
    private static final String RESHOOTS_PATHNAME = "/onyarddata/reshoots2";
    private static final String SALVAGE_PROVIDER_PATHNAME = "/onyarddata/salvageproviders2";
    private static final String IMAGE_CAPTION_PATHNAME = "/onyarddata/imagecaption";
    private static final String IMAGE_TYPE_PATHNAME = "/onyarddata/imagetype";
    private static final String LOSS_TYPE_PATHNAME = "/onyarddata/losstype";
    private static final String BRANCH_PATHNAME = "/onyarddata/branches";
    private static final String ENGINE_STATUS_PATHNAME = "/onyarddata/enginestatus";
    private static final String FEATURE_VALUE_PATHNAME = "/onyarddata/featurevalue";
    private static final String LICENSE_PLATE_CONDITION_PATHNAME = "/onyarddata/licenseplatecondition";
    private static final String ODOMETER_READING_TYPE_PATHNAME = "/onyarddata/odometerreadingtype";
    private static final String PUBLIC_VIN_PATHNAME = "/onyarddata/publicvin";
    private static final String SALVAGE_CONDITION_PATHNAME = "/onyarddata/salvagecondition";
    private static final String SALVAGE_TYPE_PATHNAME = "/onyarddata/salvagetype";
    private static final String STATE_PATHNAME = "/onyarddata/state";
    private static final String BODY_STYLE_SPECIALTY_PATHNAME = "/onyarddata/bodystylespecialty";
    private static final String CHECKIN_FIELD_PATHNAME = "/onyarddata/checkinfield";
    private static final String CHECKIN_TEMPLATE_PATHNAME = "/onyarddata/checkintemplate";
    private static final String ENHANCEMENT_PATHNAME = "/onyarddata/enhancement";
    private static final String AUCTION_SCHEDULE_PATHNAME = "/onyarddata/auctionschedule";
    private static final String SLA_SALVAGE_ENHANCEMENT_PATHNAME = "/onyarddata/slabasedenhancement";
    private static final String SALVAGE_ENHANCEMENT_PATHNAME = "/onyarddata/salvageenhancement";
    private static final String PILOT_FUNCTION_PATHNAME = "/onyarddata/pilotfunction";
    private static final String DISABLED_ENHANCEMENT_PATHNAME = "/onyarddata/onyarddisabledenhancement";
    private static final String HOLIDAY_PATHNAME = "/onyarddata/holiday";
    private static final String SYNC_WINDOW_PATHNAME = "/onyarddata/syncwindow";
    private static final String SYNC_WINDOW_EXCEPTION_PATHNAME = "/onyarddata/syncexception";
    private static final String ONYARD_CONFIG_PATHNAME = "/onyarddata/onyardconfig";

    private static final String JSON_FILE_VEHICLES = "json_vehicles.txt";
    private static final String JSON_FILE_RESHOOTS = "json_reshoots.txt";
    private static final String JSON_FILE_SALVAGE_PROVIDERS = "json_salvage_providers.txt";
    private static final String JSON_FILE_COLORS = "json_colors.txt";
    private static final String JSON_FILE_STATUSES = "json_statuses.txt";
    private static final String JSON_FILE_DAMAGES = "json_damages.txt";
    private static final String JSON_FILE_SALE_DOC_TYPES = "json_sale_doc_types.txt";
    private static final String JSON_FILE_IMAGE_CAPTIONS = "json_image_captions.txt";
    private static final String JSON_FILE_IMAGE_TYPES = "json_image_types.txt";
    private static final String JSON_FILE_LOSS_TYPES = "json_loss_types.txt";
    private static final String JSON_FILE_BRANCHES = "json_branches.txt";
    private static final String JSON_FILE_ENGINE_STATUS = "json_engine_status.txt";
    private static final String JSON_FILE_FEATURE_VALUE = "json_feature_value.txt";
    private static final String JSON_FILE_LICENSE_PLATE_CONDITION = "json_license_plate_condition.txt";
    private static final String JSON_FILE_ODOMETER_READING_TYPE = "json_odometer_reading_type.txt";
    private static final String JSON_FILE_PUBLIC_VIN = "json_public_vin.txt";
    private static final String JSON_FILE_SALVAGE_CONDITION = "json_salvage_condition.txt";
    private static final String JSON_FILE_SALVAGE_TYPE = "json_salvage_type.txt";
    private static final String JSON_FILE_STATE = "json_state.txt";
    private static final String JSON_FILE_BODY_STYLE_SPECIALTY = "json_body_style_specialty.txt";
    private static final String JSON_FILE_CHECKIN_FIELD = "json_checkin_field.txt";
    private static final String JSON_FILE_CHECKIN_TEMPLATE = "json_checkin_template.txt";
    private static final String JSON_FILE_ENHANCEMENT = "json_enhancement.txt";
    private static final String JSON_FILE_AUCTION_SCHEDULE = "json_auctionschedule.txt";
    private static final String JSON_FILE_SLA_SALVAGE_ENHANCEMENT = "json_sla_salvage_enhancement.txt";
    private static final String JSON_FILE_SALVAGE_ENHANCEMENT = "json_salvage_enhancement.txt";
    private static final String JSON_FILE_PILOT_FUNCTION = "json_pilot_function.txt";
    private static final String JSON_FILE_DISABLED_ENHANCEMENT = "json_disabled_enhancement.txt";
    private static final String JSON_FILE_HOLIDAYS = "json_holidays.txt";
    private static final String JSON_FILE_SYNC_WINDOW = "json_sync_window.txt";
    private static final String JSON_FILE_SYNC_WINDOW_EXCEPTION = "json_sync_window_exception.txt";
    private static final String JSON_FILE_ONYARD_CONFIG = "json_onyard_config.txt";

    public static final SyncTable VEHICLE = new SyncTable(JSON_FILE_VEHICLES, VEHICLES_PATHNAME,
            ImportObject.VEHICLE, OnYardContract.Vehicles.CONTENT_URI);
    public static final SyncTable RESHOOT = new SyncTable(JSON_FILE_RESHOOTS, RESHOOTS_PATHNAME,
            ImportObject.RESHOOT, OnYardContract.ImageReshoot.CONTENT_URI);
    public static final SyncTable SALVAGE_PROVIDER = new SyncTable(JSON_FILE_SALVAGE_PROVIDERS,
            SALVAGE_PROVIDER_PATHNAME, ImportObject.SALVAGE_PROVIDER,
            OnYardContract.SalvageProvider.CONTENT_URI);
    public static final SyncTable COLOR = new SyncTable(JSON_FILE_COLORS, COLOR_PATHNAME,
            ImportObject.COLOR, OnYardContract.Color.CONTENT_URI);
    public static final SyncTable DAMAGE = new SyncTable(JSON_FILE_DAMAGES, DAMAGE_PATHNAME,
            ImportObject.DAMAGE, OnYardContract.Damage.CONTENT_URI);
    public static final SyncTable STATUS = new SyncTable(JSON_FILE_STATUSES, STATUS_PATHNAME,
            ImportObject.STATUS, OnYardContract.Status.CONTENT_URI);
    public static final SyncTable SALE_DOC_TYPE = new SyncTable(JSON_FILE_SALE_DOC_TYPES,
            SALE_DOC_TYPE_PATHNAME, ImportObject.SALE_DOC_TYPE,
            OnYardContract.SaleDocType.CONTENT_URI);
    public static final SyncTable IMAGE_CAPTION = new SyncTable(JSON_FILE_IMAGE_CAPTIONS,
            IMAGE_CAPTION_PATHNAME, ImportObject.IMAGE_CAPTION,
            OnYardContract.ImageCaption.CONTENT_URI);
    public static final SyncTable IMAGE_TYPE = new SyncTable(JSON_FILE_IMAGE_TYPES,
            IMAGE_TYPE_PATHNAME, ImportObject.IMAGE_TYPE, OnYardContract.ImageType.CONTENT_URI);
    public static final SyncTable LOSS_TYPE = new SyncTable(JSON_FILE_LOSS_TYPES,
            LOSS_TYPE_PATHNAME, ImportObject.LOSS_TYPE, OnYardContract.LossType.CONTENT_URI);
    public static final SyncTable BRANCH = new SyncTable(JSON_FILE_BRANCHES, BRANCH_PATHNAME,
            ImportObject.BRANCH, OnYardContract.Branch.CONTENT_URI);
    public static final SyncTable ENGINE_STATUS = new SyncTable(JSON_FILE_ENGINE_STATUS,
            ENGINE_STATUS_PATHNAME, ImportObject.ENGINE_STATUS,
            OnYardContract.EngineStatus.CONTENT_URI);
    public static final SyncTable FEATURE_VALUE = new SyncTable(JSON_FILE_FEATURE_VALUE,
            FEATURE_VALUE_PATHNAME, ImportObject.FEATURE_VALUE,
            OnYardContract.FeatureValue.CONTENT_URI);
    public static final SyncTable LICENSE_PLATE_CONDITION = new SyncTable(
            JSON_FILE_LICENSE_PLATE_CONDITION, LICENSE_PLATE_CONDITION_PATHNAME,
            ImportObject.LICENSE_PLATE_CONDITION, OnYardContract.LicensePlateCondition.CONTENT_URI);
    public static final SyncTable ODOMETER_READING_TYPE = new SyncTable(
            JSON_FILE_ODOMETER_READING_TYPE, ODOMETER_READING_TYPE_PATHNAME,
            ImportObject.ODOMETER_READING_TYPE, OnYardContract.OdometerReadingType.CONTENT_URI);
    public static final SyncTable PUBLIC_VIN = new SyncTable(JSON_FILE_PUBLIC_VIN,
            PUBLIC_VIN_PATHNAME, ImportObject.PUBLIC_VIN, OnYardContract.PublicVIN.CONTENT_URI);
    public static final SyncTable SALVAGE_CONDITION = new SyncTable(JSON_FILE_SALVAGE_CONDITION,
            SALVAGE_CONDITION_PATHNAME, ImportObject.SALVAGE_CONDITION,
            OnYardContract.SalvageCondition.CONTENT_URI);
    public static final SyncTable SALVAGE_TYPE = new SyncTable(JSON_FILE_SALVAGE_TYPE,
            SALVAGE_TYPE_PATHNAME, ImportObject.SALVAGE_TYPE,
            OnYardContract.SalvageType.CONTENT_URI);
    public static final SyncTable STATE = new SyncTable(JSON_FILE_STATE, STATE_PATHNAME,
            ImportObject.STATE, OnYardContract.State.CONTENT_URI);
    public static final SyncTable BODY_STYLE_SPECIALTY = new SyncTable(
            JSON_FILE_BODY_STYLE_SPECIALTY, BODY_STYLE_SPECIALTY_PATHNAME,
            ImportObject.BODY_STYLE_SPECIALTY, OnYardContract.BodyStyleSpecialty.CONTENT_URI);
    public static final SyncTable CHECKIN_FIELD = new SyncTable(JSON_FILE_CHECKIN_FIELD,
            CHECKIN_FIELD_PATHNAME, ImportObject.CHECKIN_FIELD,
            OnYardContract.CheckinField.CONTENT_URI);
    public static final SyncTable CHECKIN_TEMPLATE = new SyncTable(JSON_FILE_CHECKIN_TEMPLATE,
            CHECKIN_TEMPLATE_PATHNAME, ImportObject.CHECKIN_TEMPLATE,
            OnYardContract.CheckinTemplate.CONTENT_URI);
    public static final SyncTable ENHANCEMENT = new SyncTable(JSON_FILE_ENHANCEMENT,
            ENHANCEMENT_PATHNAME, ImportObject.ENHANCEMENT, OnYardContract.Enhancement.CONTENT_URI);
    public static final SyncTable AUCTION_SCHEDULE = new SyncTable(JSON_FILE_AUCTION_SCHEDULE,
            AUCTION_SCHEDULE_PATHNAME, ImportObject.AUCTION_SCHEDULE,
            OnYardContract.AuctionSchedule.CONTENT_URI);
    public static final SyncTable SLA_SALVAGE_ENHANCEMENT = new SyncTable(
            JSON_FILE_SLA_SALVAGE_ENHANCEMENT, SLA_SALVAGE_ENHANCEMENT_PATHNAME,
            ImportObject.SLA_SALVAGE_ENHANCEMENT, OnYardContract.SlaSalvageEnhancement.CONTENT_URI);
    public static final SyncTable PILOT_FUNCTION = new SyncTable(JSON_FILE_PILOT_FUNCTION,
            PILOT_FUNCTION_PATHNAME, ImportObject.PILOT_FUNCTION,
            OnYardContract.PilotFunction.CONTENT_URI);
    public static final SyncTable SALVAGE_ENHANCEMENT = new SyncTable(
            JSON_FILE_SALVAGE_ENHANCEMENT, SALVAGE_ENHANCEMENT_PATHNAME,
            ImportObject.SALVAGE_ENHANCEMENT, OnYardContract.SalvageEnhancement.CONTENT_URI);
    public static final SyncTable DISABLED_ENHANCEMENT = new SyncTable(
            JSON_FILE_DISABLED_ENHANCEMENT, DISABLED_ENHANCEMENT_PATHNAME,
            ImportObject.DISABLED_ENHANCEMENT, OnYardContract.DisabledEnhancement.CONTENT_URI);
    public static final SyncTable HOLIDAY = new SyncTable(JSON_FILE_HOLIDAYS, HOLIDAY_PATHNAME,
            ImportObject.HOLIDAY, OnYardContract.Holiday.CONTENT_URI);
    public static final SyncTable SYNC_WINDOW = new SyncTable(JSON_FILE_SYNC_WINDOW,
            SYNC_WINDOW_PATHNAME, ImportObject.SYNC_WINDOW, OnYardContract.SyncWindow.CONTENT_URI);
    public static final SyncTable SYNC_WINDOW_EXCEPTION = new SyncTable(
            JSON_FILE_SYNC_WINDOW_EXCEPTION, SYNC_WINDOW_EXCEPTION_PATHNAME,
            ImportObject.SYNC_WINDOW_EXCEPTION, OnYardContract.SyncWindowException.CONTENT_URI);
    public static final SyncTable ONYARD_CONFIG = new SyncTable(JSON_FILE_ONYARD_CONFIG,
            ONYARD_CONFIG_PATHNAME, ImportObject.ONYARD_CONFIG,
            OnYardContract.Config.CONTENT_URI);
}
