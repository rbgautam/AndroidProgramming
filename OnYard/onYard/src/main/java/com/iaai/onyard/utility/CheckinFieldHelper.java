package com.iaai.onyard.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.iaai.onyard.application.OnYard.CheckinId;
import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyardproviderapi.classes.BodyStyleSpecialtyInfo;
import com.iaai.onyardproviderapi.classes.ColorInfo;
import com.iaai.onyardproviderapi.classes.DamageInfo;
import com.iaai.onyardproviderapi.classes.EngineStatusInfo;
import com.iaai.onyardproviderapi.classes.FeatureValueInfo;
import com.iaai.onyardproviderapi.classes.LicensePlateConditionInfo;
import com.iaai.onyardproviderapi.classes.LossTypeInfo;
import com.iaai.onyardproviderapi.classes.OdometerReadingTypeInfo;
import com.iaai.onyardproviderapi.classes.PublicVinInfo;
import com.iaai.onyardproviderapi.classes.SalvageConditionInfo;
import com.iaai.onyardproviderapi.classes.SalvageTypeInfo;
import com.iaai.onyardproviderapi.classes.StateInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;


public class CheckinFieldHelper {

    public static ArrayList<OnYardFieldOption> getOptionsById(int fieldId,
            String featureCode, ContentResolver contentResolver, int salvageType,
            OnYardFieldInputType inputType) {
        if (featureCode != null) {
            final ArrayList<OnYardFieldOption> optionList = getFeatureOptions(contentResolver,
                    featureCode);

            if (optionList != null && !optionList.isEmpty()) {
                return optionList;
            }
        }

        if (inputType == OnYardFieldInputType.CHECKBOX) {
            return getYesNoOptions();
        }

        if (fieldId == CheckinId.PRIMARY_DAMAGE || fieldId == CheckinId.SECONDARY_DAMAGE) {
            return getDamageOptions(contentResolver);
        }
        if (fieldId == CheckinId.EXTERIOR_COLOR || fieldId == CheckinId.INTERIOR_COLOR
                || fieldId == CheckinId.EXTERIOR_COLOR_NOT_REQUIRED) {
            return getColorOptions(contentResolver);
        }
        if (fieldId == CheckinId.LOSS_TYPE) {
            return getLossTypeOptions(contentResolver);
        }
        if (fieldId == CheckinId.VIN_STATUS || fieldId == CheckinId.VIN_STATUS_NOT_REQUIRED) {
            return getVinStatusOptions(contentResolver);
        }
        if (fieldId == CheckinId.KEYS || fieldId == CheckinId.KEYS_NO_RULES
                || fieldId == CheckinId.KEYS_NOT_REQUIRED) {
            return getKeysOptions();
        }
        if (fieldId == CheckinId.ODOMETER_STATUS || fieldId == CheckinId.ODOMETER_STATUS_RV) {
            return getOdometerStatusOptions(contentResolver);
        }
        if (fieldId == CheckinId.PLATE_CONDITION) {
            return getPlateConditionOptions(contentResolver);
        }
        if (fieldId == CheckinId.SALVAGE_CONDITION_AUTOMOBILE
                || fieldId == CheckinId.SALVAGE_CONDITION_MOTORCYCLE) {
            return getSalvageConditionOptions(contentResolver);
        }
        if (fieldId == CheckinId.ENGINE_STATUS) {
            return getEngineStatusOptions(contentResolver);
        }
        if (fieldId == CheckinId.SALVAGE_TYPE) {
            return getSalvageTypeOptions(contentResolver);
        }
        if (fieldId == CheckinId.STATE) {
            return getStateOptions(contentResolver);
        }
        if (fieldId == CheckinId.BODY_STYLE) {
            return getBodyStyleSpecialtyOptions(contentResolver, salvageType);
        }
        if (fieldId == CheckinId.NUMBER_OF_PLATES) {
            return getNumericOptions(0, 2);
        }
        if (fieldId == CheckinId.NUMBER_OF_TIRES_AUTOMOBILE
                || fieldId == CheckinId.NUMBER_OF_TIRES_MOTORCYCLE) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.NUMBER_OF_WHEELS_AUTOMOBILE
                || fieldId == CheckinId.NUMBER_OF_WHEELS_MOTORCYCLE) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.AXLE_STEERING) {
            return getAxleSteeringOptions();
        }

        if (fieldId == CheckinId.NUMBER_OF_AC_UNIT) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.NUMBER_OF_TV) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.NUMBER_OF_VCR) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.NUMBER_OF_SLIDEOUT) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.NUMBER_OF_AXLES) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.NUMBER_OF_TRAILER_AXLES) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.NUM_OF_MARINE_TRAILER_AXLES) {
            return getNumericOptions(0, 9);
        }
        if (fieldId == CheckinId.STEERING_TYPE) {
            return getSteeringOptions();
        }

        return new ArrayList<OnYardFieldOption>();
    }

    private static ArrayList<OnYardFieldOption> getFeatureOptions(ContentResolver contentResolver,
            String featureCode) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.FeatureValue.CONTENT_URI, null,
                    OnYardContract.FeatureValue.COLUMN_NAME_CODE + "=?",
                    new String[] { featureCode }, OnYardContract.FeatureValue.COLUMN_NAME_VALUE
                    + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            OnYardFieldOption presentOption = null;
            OnYardFieldOption damagedOption = null;
            do {
                final FeatureValueInfo featureValue = new FeatureValueInfo(cursor);
                final OnYardFieldOption option = new OnYardFieldOption(
                        featureValue.getFeatureValue(), featureValue.getFeatureValue());

                options.add(option);

                if (featureValue.getFeatureValue().equals("Present")) {
                    presentOption = option;

                }
                if (featureValue.getFeatureValue().equals("Damaged")) {
                    damagedOption = option;
                }
            }
            while (cursor.moveToNext());

            // manual reordering of fields if list of options contains "Present"
            if (presentOption != null) {
                final ArrayList<OnYardFieldOption> reorderedOptions = new ArrayList<OnYardFieldOption>();
                if (damagedOption != null) {
                    reorderedOptions.add(damagedOption);
                    reorderedOptions.add(presentOption);

                    for (final OnYardFieldOption option : options) {
                        if (!option.equals(presentOption) && !option.equals(damagedOption)) {
                            reorderedOptions.add(option);
                        }
                    }

                    return reorderedOptions;
                }
                else {
                    reorderedOptions.add(presentOption);

                    for (final OnYardFieldOption option : options) {
                        if (!option.equals(presentOption)) {
                            reorderedOptions.add(option);
                        }
                    }

                    return reorderedOptions;
                }
            }

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getAxleSteeringOptions() {
        final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();
        options.add(new OnYardFieldOption("Manual", "Manual"));
        options.add(new OnYardFieldOption("Power", "Power"));

        return options;
    }

    private static ArrayList<OnYardFieldOption> getSteeringOptions() {
        final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();
        options.add(new OnYardFieldOption("4-Wheel Steering", "4-Wheel Steering"));
        options.add(new OnYardFieldOption("Manual", "Manual"));
        options.add(new OnYardFieldOption("Power", "Power"));
        options.add(new OnYardFieldOption("Rack & Pinion", "Rack & Pinion"));
        options.add(new OnYardFieldOption("Tilt", "Tilt"));
        return options;
    }

    private static ArrayList<OnYardFieldOption> getLossTypeOptions(ContentResolver contentResolver) {

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.LossType.CONTENT_URI, null, null, null,
                    OnYardContract.LossType.COLUMN_NAME_DESCRIPTION + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final LossTypeInfo lossType = new LossTypeInfo(cursor);

                options.add(new OnYardFieldOption(lossType.getLossTypeDescription(), lossType
                        .getLossTypeCode()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getVinStatusOptions(ContentResolver contentResolver) {

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.PublicVIN.CONTENT_URI, null, null, null,
                    OnYardContract.PublicVIN.COLUMN_NAME_DESCRIPTION + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final PublicVinInfo vinStatus = new PublicVinInfo(cursor);

                options.add(new OnYardFieldOption(vinStatus.getPublicVinDescription(), vinStatus
                        .getPublicVinCode()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getYesNoOptions() {
        final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();
        options.add(new OnYardFieldOption("Yes", "1"));
        options.add(new OnYardFieldOption("No", "0"));

        return options;
    }

    private static ArrayList<OnYardFieldOption> getKeysOptions() {
        final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();
        options.add(new OnYardFieldOption("Present", "1"));
        options.add(new OnYardFieldOption("Missing", "0"));
        options.add(new OnYardFieldOption("N/A", "2"));

        return options;
    }

    private static ArrayList<OnYardFieldOption> getOdometerStatusOptions(
            ContentResolver contentResolver) {

        Cursor cursor = null;
        try {
            cursor = contentResolver
                    .query(OnYardContract.OdometerReadingType.CONTENT_URI, null, null, null,
                            OnYardContract.OdometerReadingType.COLUMN_NAME_DESCRIPTION + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final OdometerReadingTypeInfo odometerStatus = new OdometerReadingTypeInfo(cursor);

                options.add(new OnYardFieldOption(odometerStatus
                        .getOdometerReadingTypeDescription(), odometerStatus
                        .getOdometerReadingTypeCode()));
            }
            while (cursor.moveToNext());

            // added option not defined in ASAP DB
            final OdometerReadingTypeInfo odometerStatus = new OdometerReadingTypeInfo("P",
                    "Probed/InOp", false);
            options.add(new OnYardFieldOption(odometerStatus.getOdometerReadingTypeDescription(),
                    odometerStatus.getOdometerReadingTypeCode()));
            // sort ArrayList
            Collections.sort(options, new Comparator<OnYardFieldOption>() {

                @Override
                public int compare(OnYardFieldOption op1, OnYardFieldOption op2) {
                    return op1.getDisplayName().compareTo(op2.getDisplayName());
                }
            });

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getPlateConditionOptions(
            ContentResolver contentResolver) {

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.LicensePlateCondition.CONTENT_URI, null,
                    null, null, OnYardContract.LicensePlateCondition.COLUMN_NAME_DESCRIPTION
                    + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final LicensePlateConditionInfo plateCondition = new LicensePlateConditionInfo(
                        cursor);

                options.add(new OnYardFieldOption(plateCondition
                        .getLicensePlateConditionDescription(), plateCondition
                        .getLicensePlateConditionCode()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getNumericOptions(int min, int max) {
        final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

        for (int i = min; i <= max; i++) {
            options.add(new OnYardFieldOption(String.valueOf(i), String.valueOf(i)));
        }
        return options;
    }

    private static ArrayList<OnYardFieldOption> getSalvageConditionOptions(
            ContentResolver contentResolver) {

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.SalvageCondition.CONTENT_URI, null, null,
                    null, OnYardContract.SalvageCondition.COLUMN_NAME_DESCRIPTION + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final SalvageConditionInfo salvageCondition = new SalvageConditionInfo(cursor);

                options.add(new OnYardFieldOption(salvageCondition
                        .getSalvageConditionDescription(), salvageCondition
                        .getSalvageConditionCode()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getColorOptions(ContentResolver contentResolver) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.Color.CONTENT_URI, null, null, null,
                    OnYardContract.Color.COLUMN_NAME_DESCRIPTION + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final ColorInfo color = new ColorInfo(cursor);

                options.add(new OnYardFieldOption(color.getColorDescription(), color
                        .getColorCode()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getDamageOptions(ContentResolver contentResolver) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.Damage.CONTENT_URI, null, null, null,
                    OnYardContract.Damage.COLUMN_NAME_DESCRIPTION + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final DamageInfo damage = new DamageInfo(cursor);

                options.add(new OnYardFieldOption(damage.getDamageDescription(), damage
                        .getDamageCode()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getEngineStatusOptions(
            ContentResolver contentResolver) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.EngineStatus.CONTENT_URI, null, null,
                    null, OnYardContract.EngineStatus.COLUMN_NAME_DESCRIPTION + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final EngineStatusInfo engineStatus = new EngineStatusInfo(cursor);

                options.add(new OnYardFieldOption(engineStatus.getEngineStatusDescription(),
                        engineStatus.getEngineStatusCode()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getSalvageTypeOptions(
            ContentResolver contentResolver) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.SalvageType.CONTENT_URI, null, null,
                    null, OnYardContract.SalvageType.COLUMN_NAME_DESCRIPTION + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final SalvageTypeInfo salvageType = new SalvageTypeInfo(cursor);

                options.add(new OnYardFieldOption(salvageType.getSalvageDescription(), salvageType
                        .getSalvageType()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getStateOptions(ContentResolver contentResolver) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.State.CONTENT_URI, null, null, null,
                    OnYardContract.State.COLUMN_NAME_NAME + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final StateInfo state = new StateInfo(cursor);

                options.add(new OnYardFieldOption(state.getStateName(), state.getStateAbbr()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static ArrayList<OnYardFieldOption> getBodyStyleSpecialtyOptions(
            ContentResolver contentResolver, int salvageType) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(OnYardContract.BodyStyleSpecialty.CONTENT_URI, null,
                    OnYardContract.BodyStyleSpecialty.COLUMN_NAME_SALVAGE_TYPE + "=?",
                    new String[] { String.valueOf(salvageType) },
                    OnYardContract.BodyStyleSpecialty.COLUMN_NAME_BODY_STYLE_NAME + " ASC");

            final ArrayList<OnYardFieldOption> options = new ArrayList<OnYardFieldOption>();

            if (cursor == null || !cursor.moveToFirst()) {
                return options;
            }

            do {
                final BodyStyleSpecialtyInfo bodyStyle = new BodyStyleSpecialtyInfo(cursor);

                options.add(new OnYardFieldOption(bodyStyle.getBodyStyleName(), bodyStyle
                        .getBodyStyleName()));
            }
            while (cursor.moveToNext());

            return options;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static OnYardFieldOption getAirbagNoneOption() {
        return new OnYardFieldOption("None", "None");
    }

    public static OnYardFieldOption getPlateConditionDestroyedOption() {
        return new OnYardFieldOption("Destroyed", "1");
    }

    public static OnYardFieldOption getPlateConditionNaOption() {
        return new OnYardFieldOption("Not Available", "2");
    }

    public static OnYardFieldOption getNumPlates0Option() {
        return new OnYardFieldOption("0", "0");
    }

    public static OnYardFieldOption getKeysPresentOption() {
        return new OnYardFieldOption("Present", "1");
    }

    public static OnYardFieldOption getKeysMissingOption() {
        return new OnYardFieldOption("Missing", "0");
    }

    public static OnYardFieldOption getKeyFobMissingOption() {
        return getMissingOption();
    }

    public static OnYardFieldOption getMakeKeysNoOption() {
        return getNoOption();
    }

    public static OnYardFieldOption getEngineStatusCantTestOption() {
        return new OnYardFieldOption("Can't Test", "CTS");
    }

    public static OnYardFieldOption getRunDriveNoOption() {
        return getNoOption();
    }

    public static OnYardFieldOption getHasOtherNoOption() {
        return getNoOption();
    }

    public static OnYardFieldOption getEngineStartsNoOption() {
        return getNoOption();
    }

    public static OnYardFieldOption getRadioNaOption() {
        return getNaOption();
    }

    public static OnYardFieldOption getCdPlayerNaOption() {
        return getNaOption();
    }

    public static OnYardFieldOption getCdChangerNaOption() {
        return getNaOption();
    }

    public static OnYardFieldOption getCasetteNaOption() {
        return getNaOption();
    }

    public static OnYardFieldOption getNumeric4Option() {
        return new OnYardFieldOption("4", "4");
    }

    public static OnYardFieldOption getSalvageTypeOption(Context context, String value) {
        final ArrayList<OnYardFieldOption> options = getSalvageTypeOptions(context
                .getContentResolver());

        for (final OnYardFieldOption option : options) {
            if (option.getValue().equals(value)) {
                return option;
            }
        }

        return null;
    }

    public static OnYardFieldOption getVinStatusOkOption() {
        return new OnYardFieldOption("OK", "O");
    }

    public static OnYardFieldOption getVinStatusRetaggedOption() {
        return new OnYardFieldOption("Retagged", "R");
    }

    private static OnYardFieldOption getNoOption() {
        return new OnYardFieldOption("No", "0");
    }

    private static OnYardFieldOption getYesOption() {
        return new OnYardFieldOption("Yes", "1");
    }

    private static OnYardFieldOption getMissingOption() {
        return new OnYardFieldOption("Missing", "Missing");
    }

    private static OnYardFieldOption getNaOption() {
        return new OnYardFieldOption("N/A", "N/A");
    }

    public static OnYardFieldOption getHasNoOption() {
        return getNoOption();
    }

    public static OnYardFieldOption getHasYesOption() {
        return getYesOption();
    }

    public static OnYardFieldOption geTrailerTypeOtherOption() {
        return new OnYardFieldOption("Other", "Other");
    }

    public static OnYardFieldOption geWidthTypeOtherOption() {
        return new OnYardFieldOption("Other", "Other");
    }

    public static OnYardFieldOption geAxleTypeOtherOption() {
        return new OnYardFieldOption("Other", "Other");
    }
}
