package com.iaai.onyard.classes;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyardproviderapi.contract.OnYardContract;


public class OnYardPreferences {

    private final Context mContext;
    private final SharedPreferences mSharedPrefs;

    public OnYardPreferences(Context context) {
        mContext = context;
        mSharedPrefs = mContext.getSharedPreferences(
                mContext.getString(R.string.onyard_shared_prefs_name), Context.MODE_MULTI_PROCESS);
    }

    /**
     * Update the Override Branch Number preference.
     * 
     * @param value The value to which to set the Override Branch Number preference.
     */
    public void setOverrideBranchNumber(String value) {
        mSharedPrefs.edit()
        .putString(mContext.getString(R.string.override_branch_number_pref), value).apply();

        storeOverrideBranchNumberInDb(value);
    }

    /**
     * Get the effective branch number from shared preferences. This method will apply logic to the
     * two branch values to determine which is currently being used.
     * 
     * @return The branch number if one has been set, or 0 otherwise.
     */
    public String getEffectiveBranchNumber() {
        if (isBranchOverrideActive()) {
            return getOverrideBranchNumber();
        }
        else {
            return getIpBranchNumber();
        }
    }

    /**
     * Check whether an override branch has been set.
     * 
     * @return True if a branch override has been set, false otherwise.
     */
    public boolean isBranchOverrideActive() {
        return !getOverrideBranchNumber().equals(OnYard.DEFAULT_BRANCH_NUMBER);
    }

    public String getOverrideBranchNumber() {
        // pull branch from db if preference does not exist or is 0
        if (!mSharedPrefs.contains(mContext.getString(R.string.override_branch_number_pref))
                || mSharedPrefs.getString(mContext.getString(R.string.override_branch_number_pref),
                        OnYard.DEFAULT_BRANCH_NUMBER).equals(OnYard.DEFAULT_BRANCH_NUMBER)) {
            String branchInDb = getOverrideBranchNumberFromDb();
            if (branchInDb == null) {
                branchInDb = OnYard.DEFAULT_BRANCH_NUMBER;
            }

            setOverrideBranchNumber(branchInDb);
        }

        return mSharedPrefs.getString(mContext.getString(R.string.override_branch_number_pref),
                OnYard.DEFAULT_BRANCH_NUMBER);
    }

    /**
     * Update the IP Branch Number preference.
     * 
     * @param value The value to which to set the IP Branch Number preference.
     */
    public void setIpBranchNumber(String value) {
        mSharedPrefs.edit().putString(mContext.getString(R.string.ip_branch_number_pref), value)
        .apply();

        storeIpBranchNumberInDb(value);
    }

    /**
     * Get the IP Branch Number from shared preferences.
     * 
     * @return The branch number if one has been set, or 0 otherwise.
     */
    public String getIpBranchNumber() {
        // pull branch from db if preference does not exist or is 0
        if (!mSharedPrefs.contains(mContext.getString(R.string.ip_branch_number_pref))
                || mSharedPrefs.getString(mContext.getString(R.string.ip_branch_number_pref),
                        OnYard.DEFAULT_BRANCH_NUMBER).equals(OnYard.DEFAULT_BRANCH_NUMBER)) {
            String branchInDb = getIpBranchNumberFromDb();
            if (branchInDb == null) {
                branchInDb = OnYard.DEFAULT_BRANCH_NUMBER;
            }

            setIpBranchNumber(branchInDb);
        }

        return mSharedPrefs.getString(mContext.getString(R.string.ip_branch_number_pref),
                OnYard.DEFAULT_BRANCH_NUMBER);
    }

    public boolean isDefaultBranchNumber() {
        return getEffectiveBranchNumber().equals(OnYard.DEFAULT_BRANCH_NUMBER);
    }

    /**
     * Get the user-friendly name of the IP Branch Number from shared preferences.
     * 
     * @return The name of the IP branch, or "unknown" if no IP branch has been set.
     */
    public String getIpBranchName() {
        if (!mSharedPrefs.contains(mContext.getString(R.string.ip_branch_name_pref))) {
            setIpBranchName(getIpBranchNumber());
        }

        return mSharedPrefs.getString(mContext.getString(R.string.ip_branch_name_pref),
                getIpBranchNumber());
    }

    /**
     * Set the user-friendly name of the IP Branch Number from shared preferences.
     * 
     * @param value The user-friendly branch name to save.
     */
    public void setIpBranchName(String value) {
        mSharedPrefs.edit().putString(mContext.getString(R.string.ip_branch_name_pref), value)
        .apply();
    }

    private void storeOverrideBranchNumberInDb(String branchNumber) {
        final ContentValues values = new ContentValues();
        values.put(OnYardContract.Config.COLUMN_NAME_KEY,
                OnYardContract.Config.CONFIG_KEY_OVERRIDE_BRANCH_NUMBER);
        values.put(OnYardContract.Config.COLUMN_NAME_VALUE, branchNumber);

        final String branchInDb = getOverrideBranchNumberFromDb();
        final boolean branchNumberExistsInDb = branchInDb != null;
        if (branchNumberExistsInDb) {
            final boolean branchChanged = !branchInDb.equals(branchNumber);
            if (branchChanged) {
                mContext.getContentResolver().update(OnYardContract.Config.CONTENT_URI, values,
                        OnYardContract.Config.COLUMN_NAME_KEY + "=?",
                        new String[] { OnYardContract.Config.CONFIG_KEY_OVERRIDE_BRANCH_NUMBER });
            }
        }
        else {
            mContext.getContentResolver().insert(OnYardContract.Config.CONTENT_URI, values);
        }
    }

    private String getOverrideBranchNumberFromDb() {
        Cursor cursor = null;
        try {

            cursor = mContext.getContentResolver().query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                            OnYardContract.Config.CONFIG_KEY_OVERRIDE_BRANCH_NUMBER),
                            new String[] { OnYardContract.Config.COLUMN_NAME_VALUE }, null, null, null);

            if (cursor == null) {
                return null;
            }

            return cursor.moveToFirst() ? cursor.getString(0) : null;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void storeIpBranchNumberInDb(String branchNumber) {
        final ContentValues values = new ContentValues();
        values.put(OnYardContract.Config.COLUMN_NAME_KEY,
                OnYardContract.Config.CONFIG_KEY_IP_BRANCH_NUMBER);
        values.put(OnYardContract.Config.COLUMN_NAME_VALUE, branchNumber);

        final String branchInDb = getIpBranchNumberFromDb();
        final boolean branchNumberExistsInDb = branchInDb != null;
        if (branchNumberExistsInDb) {
            final boolean branchChanged = !branchInDb.equals(branchNumber);
            if (branchChanged) {
                mContext.getContentResolver().update(OnYardContract.Config.CONTENT_URI, values,
                        OnYardContract.Config.COLUMN_NAME_KEY + "=?",
                        new String[] { OnYardContract.Config.CONFIG_KEY_IP_BRANCH_NUMBER });
            }
        }
        else {
            mContext.getContentResolver().insert(OnYardContract.Config.CONTENT_URI, values);
        }
    }

    private String getIpBranchNumberFromDb() {
        Cursor cursor = null;
        try {

            cursor = mContext.getContentResolver().query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI,
                            OnYardContract.Config.CONFIG_KEY_IP_BRANCH_NUMBER),
                            new String[] { OnYardContract.Config.COLUMN_NAME_VALUE }, null, null, null);

            if (cursor == null) {
                return null;
            }

            return cursor.moveToFirst() ? cursor.getString(0) : null;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Update the Camera flash mode preference.
     * 
     * @param value The value to which to set the Camera Flash preference.
     */
    public void setFlashMode(String value) {
        mSharedPrefs.edit().putString(mContext.getString(R.string.camera_flash_pref), value)
        .apply();
    }

    /**
     * Check whether the Camera flash mode preference has been set.
     * 
     * @return The flash mode saved in the preference, or "auto" as the default mode.
     */
    public String getFlashMode() {
        if (!mSharedPrefs.contains(mContext.getString(R.string.camera_flash_pref))) {
            setFlashMode(OnYard.DEFAULT_FLASH_MODE);
        }

        return mSharedPrefs.getString(mContext.getString(R.string.camera_flash_pref),
                OnYard.DEFAULT_FLASH_MODE);
    }

    /**
     * Check whether the Camera Voice Command preference has been set.
     * 
     * @return The value of the Camera Voice Command preference.
     */
    public boolean getIsVoiceCommandEnabled() {
        return getBooleanPref(mContext.getString(R.string.voice_command_pref), mContext, false);
    }

    /**
     * Update the Camera Voice Command preference.
     * 
     * @param value The value to which to set the Camera Voice Command preference.
     */
    public void setIsVoiceCommandEnabled(boolean value) {
        setBooleanPref(mContext.getString(R.string.voice_command_pref), mContext, value);
    }

    /**
     * Get the string that is stored as the Camera Capture keyword.
     * 
     * @return The camera capture keyword.
     */
    public String getCameraCaptureKeyword() {
        if (!mSharedPrefs.contains(mContext.getString(R.string.capture_keyword_pref))) {
            setCameraCaptureKeyword("click");
        }

        return mSharedPrefs.getString(mContext.getString(R.string.capture_keyword_pref), "click");
    }

    /**
     * Update the Camera Capture keyword preference.
     * 
     * @param value The value to which to set the Camera Capture keyword preference.
     */
    public void setCameraCaptureKeyword(String value) {
        mSharedPrefs.edit().putString(mContext.getString(R.string.capture_keyword_pref), value)
        .apply();
    }

    /**
     * Initialize the user preferences by setting the default preference values.
     */
    public void setDefaultSyncPrefs() {
        PreferenceManager.setDefaultValues(mContext,
                mContext.getString(R.string.onyard_shared_prefs_name), Context.MODE_MULTI_PROCESS,
                R.xml.account_sync_preferences, false);
    }

    public Long getSelectedAuctionDate() {
        if (!mSharedPrefs.contains(mContext.getString(R.string.auction_date_pref))) {
            return 0L;
        }

        return mSharedPrefs.getLong(mContext.getString(R.string.auction_date_pref), 0L);
    }

    public void setSelectedAuctionDatePref(Long value) {
        mSharedPrefs.edit().putLong(mContext.getString(R.string.auction_date_pref), value).apply();
    }

    public int getLastAuctionNumber() {
        if (!mSharedPrefs.contains(mContext.getString(R.string.auction_number_pref))) {
            return -1;
        }

        return mSharedPrefs.getInt(mContext.getString(R.string.auction_number_pref), -1);
    }

    public void setLastAuctionNumber(int value) {
        mSharedPrefs.edit().putInt(mContext.getString(R.string.auction_number_pref), value).apply();
    }

    public int getLastAuctionItemSeqNumber() {
        if (!mSharedPrefs.contains(mContext.getString(R.string.auction_item_sequence_number_pref))) {
            return 0;
        }

        return mSharedPrefs.getInt(mContext.getString(R.string.auction_item_sequence_number_pref),
                0);
    }

    public void setLastAuctionItemSeqNumber(int value) {
        mSharedPrefs.edit()
        .putInt(mContext.getString(R.string.auction_item_sequence_number_pref), value)
        .apply();
    }

    public String getLastSaleAisle() {
        if (!mSharedPrefs.contains(mContext.getString(R.string.sale_aisle_pref))) {
            return "";
        }

        return mSharedPrefs.getString(mContext.getString(R.string.sale_aisle_pref), "");
    }

    public void setLastSaleAisle(String value) {
        mSharedPrefs.edit().putString(mContext.getString(R.string.sale_aisle_pref), value)
        .apply();
    }

    public boolean getIsOddEvenNumberingEnabled() {
        return getBooleanPref(mContext.getString(R.string.odd_even_numbering_pref), mContext, false);
    }

    public void setIsOddEvenNumberingEnabled(boolean value) {
        setBooleanPref(mContext.getString(R.string.odd_even_numbering_pref), mContext, value);
    }

    public void resetSetSalePreferences() {
        mSharedPrefs.edit().remove(mContext.getString(R.string.sale_aisle_pref)).commit();
        mSharedPrefs.edit().remove(mContext.getString(R.string.auction_number_pref)).commit();
        mSharedPrefs.edit().remove(mContext.getString(R.string.odd_even_numbering_pref)).commit();
        mSharedPrefs.edit().remove(mContext.getString(R.string.auction_item_sequence_number_pref))
        .commit();
    }

    public boolean isSetSaleEnabled() {
        return getBooleanPref(mContext.getString(R.string.set_sale_enabled_pref), mContext, false);
    }

    public void setIsSetSaleEnabled(boolean value) {
        setBooleanPref(mContext.getString(R.string.set_sale_enabled_pref), mContext, value);
    }

    public boolean isEnhancementsEnabled() {
        return getBooleanPref(mContext.getString(R.string.enhancements_enabled_pref), mContext,
                false);
    }

    public void setIsEnhancementsEnabled(boolean value) {
        setBooleanPref(mContext.getString(R.string.enhancements_enabled_pref), mContext, value);
    }

    public void setDeleteOldestPendingSync(boolean value) {
        setBooleanPref(mContext.getString(R.string.delete_oldest_pending_sync_pref), mContext,
                value);
    }

    public boolean shouldDeleteOldestPendingSync() {
        return getBooleanPref(mContext.getString(R.string.delete_oldest_pending_sync_pref),
                mContext, false);
    }

    private void setBooleanPref(String prefName, Context mContext, boolean value) {
        mSharedPrefs.edit().putBoolean(prefName, value).apply();
    }

    private boolean getBooleanPref(String prefName, Context mContext, boolean defaultVal) {
        if (!mSharedPrefs.contains(prefName)) {
            setBooleanPref(prefName, mContext, defaultVal);
        }

        return mSharedPrefs.getBoolean(prefName, defaultVal);
    }
}
