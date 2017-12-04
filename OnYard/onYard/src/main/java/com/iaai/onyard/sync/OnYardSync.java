package com.iaai.onyard.sync;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;

import org.json.JSONException;

import android.accounts.NetworkErrorException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.PilotFunctionId;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.http.CheckinHttpPost;
import com.iaai.onyard.http.CheckinMetricsHttpPost;
import com.iaai.onyard.http.EnhancementHttpPost;
import com.iaai.onyard.http.ImagerHttpPost;
import com.iaai.onyard.http.ImagerMetricsHttpPost;
import com.iaai.onyard.http.LocationHttpPost;
import com.iaai.onyard.http.OnYardHttpClient;
import com.iaai.onyard.http.SetSaleHttpPost;
import com.iaai.onyard.http.ShouldSyncRefHttpGet;
import com.iaai.onyard.http.SyncHttpGet;
import com.iaai.onyard.http.SyncLogHttpPost;
import com.iaai.onyard.http.TransferStocksHttpGet;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.sync.JSONHelper.ImportMode;
import com.iaai.onyard.sync.JSONHelper.ImportObject;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.BroadcastHelper;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.ImageDirHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.ConfigInfo;
import com.iaai.onyardproviderapi.classes.DataPendingSync;
import com.iaai.onyardproviderapi.classes.SyncWindowExceptionInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;


public class OnYardSync {

    private final Context mContext;
    private final ContentResolver mContentResolver;
    private final OnYardPreferences mPreferences;
    private final String mEffectiveBranchNumber;
    private final String mBranchTimeZone;
    private final OnYardHttpClient mHttpClient;

    public OnYardSync(Context context) throws KeyManagementException, UnrecoverableKeyException,
    NoSuchAlgorithmException, KeyStoreException {
        mContext = context;
        mContentResolver = context.getContentResolver();
        mPreferences = new OnYardPreferences(context);
        mEffectiveBranchNumber = mPreferences.getEffectiveBranchNumber();
        mBranchTimeZone = DataHelper.getBranchTimeZone(context);
        mHttpClient = new OnYardHttpClient();
    }

    public void run(boolean forceLogout, boolean isFullSync) throws NetworkErrorException,
    JSONException, IOException {
        final Timer t = new Timer("OnYardSync.run" + (isFullSync ? " (full)" : ""));
        t.start();

        if (isFullSync) {
            if (forceLogout && AuthenticationHelper.isAnyUserLoggedIn(mContentResolver)) {
                AuthenticationHelper.logCurrentUserOut(mContext);
                BroadcastHelper.sendLogoutBroadcast(mContext, false);
            }

            resetLastUpdateTime();
            mPreferences.resetSetSalePreferences();
        }

        if (!HTTPHelper.isNetworkAvailable(mContext)) {
            throw new NetworkErrorException("Device must be connected to a network");
        }

        if (AuthenticationHelper.isAnyUserLoggedIn(mContentResolver)
                && AuthenticationHelper.isUserAuthValid(mContext, mHttpClient)) {
            boolean deleteOldest = mPreferences.shouldDeleteOldestPendingSync();
            final ArrayList<PendingSyncSession> sessionsToSync = getSessionsToSync();

            for (final PendingSyncSession session : sessionsToSync) {
                try {
                    if (deleteOldest) {
                        deleteSessionData(session.getSessionId());
                        deleteOldest = false;
                        mPreferences.setDeleteOldestPendingSync(false);
                        continue;
                    }
                }
                catch (final Exception e) {
                    deleteOldest = false;
                    mPreferences.setDeleteOldestPendingSync(false);
                    throw new IOException("Error deleting oldest record: " + e.getMessage());
                }

                syncSessionData(session);

                BroadcastHelper.sendUpdatePendingSyncInfoBroadcast(mContext, false);
            }
        }

        boolean shouldUpdatePilotFunctions;
        if (!mPreferences.isDefaultBranchNumber()) {
            final Long lastUpdate = SyncHelper.getLastDBUpdateTime(mContext);
            int importMode;
            final ArrayList<SyncTable> syncTableList = new ArrayList<SyncTable>();
            if (lastUpdate.equals(0L)) {
                importMode = ImportMode.ALL_OBJECTS;

                syncTableList.add(SyncTable.VEHICLE);
                syncTableList.add(SyncTable.RESHOOT);
                syncTableList.add(SyncTable.SALVAGE_PROVIDER);
                syncTableList.add(SyncTable.COLOR);
                syncTableList.add(SyncTable.DAMAGE);
                syncTableList.add(SyncTable.STATUS);
                syncTableList.add(SyncTable.SALE_DOC_TYPE);
                syncTableList.add(SyncTable.IMAGE_CAPTION);
                syncTableList.add(SyncTable.IMAGE_TYPE);
                syncTableList.add(SyncTable.LOSS_TYPE);
                syncTableList.add(SyncTable.BRANCH);
                syncTableList.add(SyncTable.ENGINE_STATUS);
                syncTableList.add(SyncTable.FEATURE_VALUE);
                syncTableList.add(SyncTable.LICENSE_PLATE_CONDITION);
                syncTableList.add(SyncTable.ODOMETER_READING_TYPE);
                syncTableList.add(SyncTable.PUBLIC_VIN);
                syncTableList.add(SyncTable.SALVAGE_CONDITION);
                syncTableList.add(SyncTable.SALVAGE_TYPE);
                syncTableList.add(SyncTable.STATE);
                syncTableList.add(SyncTable.BODY_STYLE_SPECIALTY);
                syncTableList.add(SyncTable.CHECKIN_FIELD);
                syncTableList.add(SyncTable.CHECKIN_TEMPLATE);
                syncTableList.add(SyncTable.ENHANCEMENT);
                syncTableList.add(SyncTable.AUCTION_SCHEDULE);
                syncTableList.add(SyncTable.SLA_SALVAGE_ENHANCEMENT);
                syncTableList.add(SyncTable.PILOT_FUNCTION);
                syncTableList.add(SyncTable.SALVAGE_ENHANCEMENT);
                syncTableList.add(SyncTable.DISABLED_ENHANCEMENT);
                syncTableList.add(SyncTable.HOLIDAY);
                syncTableList.add(SyncTable.SYNC_WINDOW);
                syncTableList.add(SyncTable.SYNC_WINDOW_EXCEPTION);
                syncTableList.add(SyncTable.ONYARD_CONFIG);

                deleteAllRows(syncTableList);

                shouldUpdatePilotFunctions = true;
            }
            else {
                importMode = ImportMode.UPDATED_OBJECTS;

                removeTransferredStocks(lastUpdate);
                syncTableList.add(SyncTable.VEHICLE);
                syncTableList.add(SyncTable.RESHOOT);
                syncTableList.add(SyncTable.SALVAGE_PROVIDER);
                syncTableList.add(SyncTable.SLA_SALVAGE_ENHANCEMENT);
                syncTableList.add(SyncTable.SALVAGE_ENHANCEMENT);
                syncTableList.add(SyncTable.AUCTION_SCHEDULE);

                if (shouldSyncReferenceTables(lastUpdate)) {
                    syncTableList.add(SyncTable.COLOR);
                    syncTableList.add(SyncTable.DAMAGE);
                    syncTableList.add(SyncTable.STATUS);
                    syncTableList.add(SyncTable.SALE_DOC_TYPE);
                    syncTableList.add(SyncTable.IMAGE_CAPTION);
                    syncTableList.add(SyncTable.IMAGE_TYPE);
                    syncTableList.add(SyncTable.LOSS_TYPE);
                    syncTableList.add(SyncTable.BRANCH);
                    syncTableList.add(SyncTable.ENGINE_STATUS);
                    syncTableList.add(SyncTable.FEATURE_VALUE);
                    syncTableList.add(SyncTable.LICENSE_PLATE_CONDITION);
                    syncTableList.add(SyncTable.ODOMETER_READING_TYPE);
                    syncTableList.add(SyncTable.PUBLIC_VIN);
                    syncTableList.add(SyncTable.SALVAGE_CONDITION);
                    syncTableList.add(SyncTable.SALVAGE_TYPE);
                    syncTableList.add(SyncTable.STATE);
                    syncTableList.add(SyncTable.BODY_STYLE_SPECIALTY);
                    syncTableList.add(SyncTable.CHECKIN_FIELD);
                    syncTableList.add(SyncTable.CHECKIN_TEMPLATE);
                    syncTableList.add(SyncTable.ENHANCEMENT);
                    syncTableList.add(SyncTable.PILOT_FUNCTION);
                    syncTableList.add(SyncTable.DISABLED_ENHANCEMENT);
                    syncTableList.add(SyncTable.HOLIDAY);
                    syncTableList.add(SyncTable.SYNC_WINDOW);
                    syncTableList.add(SyncTable.SYNC_WINDOW_EXCEPTION);
                    syncTableList.add(SyncTable.ONYARD_CONFIG);

                    shouldUpdatePilotFunctions = true;
                }
                else {
                    shouldUpdatePilotFunctions = false;
                }
            }

            downloadToJsonFile(syncTableList, lastUpdate);
            readFromJsonFile(syncTableList, importMode);

            setLastDBUpdateTime();
            if (shouldUpdatePilotFunctions) {
                enablePilotFunctions();
            }
        }

        final SyncLogHttpPost syncDataPost = new SyncLogHttpPost(mContext);
        final String userLogin = AuthenticationHelper.getLoggedInUser(mContentResolver);
        if (userLogin != null) {
            syncDataPost.setUserLogin(userLogin);
        }
        syncDataPost.submit(mHttpClient);
        insertLastSyncTime(DataHelper.getUnixUtcTimeStamp());

        t.end();
        t.logInfo(mContext);

        final String syncInterval = SyncHelper.getSyncIntervalFromDb(mContext);
        SyncHelper.updateSyncInterval(mContext, syncInterval);
    }

    private void deleteAllRows(ArrayList<SyncTable> syncTableList) {
        for (final SyncTable syncTable : syncTableList) {
            if (syncTable.getContentUri() == OnYardContract.Config.CONTENT_URI) {
                clearConfigTable();
                continue;
            }

            mContentResolver.delete(syncTable.getContentUri(), null, null);
        }
    }

    private void clearConfigTable() {
        mContentResolver.delete(OnYardContract.Config.CONTENT_URI,
                OnYardContract.Config.COLUMN_NAME_KEY + " NOT IN ('"
                        + OnYardContract.Config.CONFIG_KEY_LAST_SYNC_DATE_TIME + "','"
                        + OnYardContract.Config.CONFIG_KEY_UPDATE_DATE_TIME + "','"
                        + OnYardContract.Config.CONFIG_KEY_OVERRIDE_BRANCH_NUMBER + "','"
                        + OnYardContract.Config.CONFIG_KEY_IP_BRANCH_NUMBER + "','"
                        + OnYardContract.Config.CONFIG_KEY_USER_LOGIN + "','"
                        + OnYardContract.Config.CONFIG_KEY_AUTH_TOKEN + "')", null);
    }

    private void resetLastUpdateTime() {
        mContentResolver.delete(OnYardContract.Config.CONTENT_URI,
                OnYardContract.Config.COLUMN_NAME_KEY + "=?",
                new String[] { OnYardContract.Config.CONFIG_KEY_UPDATE_DATE_TIME });
    }

    private void readFromJsonFile(ArrayList<SyncTable> syncTableList, int importMode)
            throws JSONException, IOException {
        for (final SyncTable syncTable : syncTableList) {
            JSONHelper.importObjectsFromJSONFile(mContext, syncTable.getJsonFileName(),
                    OnYard.SYNC_BATCH_SIZE, importMode, syncTable.getImportObject());
        }
    }

    private void downloadToJsonFile(ArrayList<SyncTable> syncTableList, long lastDBUpdateTime)
            throws InvalidParameterException, NetworkErrorException, IOException {
        SyncHttpGet sync;
        for (final SyncTable syncTable : syncTableList) {
            sync = new SyncHttpGet(mContext, syncTable.getWcfPathname(),
                    syncTable.getJsonFileName());
            sync.setBranch(mEffectiveBranchNumber);

            if (lastDBUpdateTime == 0) {
                sync.setLastUpdateTimeUnix(String.valueOf(lastDBUpdateTime));
                sync.setActiveOnly("1");
            }
            else {
                sync.setLastUpdateTimeUnix(String.valueOf(lastDBUpdateTime));
                sync.setActiveOnly("0");
            }

            if (syncTable.getImportObject() == ImportObject.VEHICLE) {
                sync.setPassword(OnYard.DATA_SERVICE_PASSWORD);
            }

            sync.downloadToJsonFile(mHttpClient);
        }
    }

    public String getOldestDataItemPendingSyncStockNum() {
        String stockNumber = "UNKNOWN";

        final ArrayList<PendingSyncSession> sessions = getSessionsToSync();
        if (sessions == null || sessions.isEmpty()) {
            return stockNumber;
        }
        final PendingSyncSession firstSession = getSessionsToSync().get(0);
        final Integer appId = firstSession == null ? null : firstSession.getAppId();
        final String sessionId = firstSession == null ? null : firstSession.getSessionId();

        if (appId != null && sessionId != null) {
            final ArrayList<DataPendingSync> sessionData = getSessionData(sessionId);
            for (final DataPendingSync data : sessionData) {
                if (appId == OnYardContract.IMAGER_APP_ID
                        && ImagerHttpPost.STOCK_NUMBER_KEY.equals(data.getJsonName())
                        || appId == OnYardContract.IMAGER_METRICS_APP_ID
                        && ImagerMetricsHttpPost.STOCK_NUMBER_KEY.equals(data.getJsonName())
                        || appId == OnYardContract.CHECKIN_APP_ID
                        && CheckinHttpPost.STOCK_NUMBER_KEY.equals(data.getJsonName())
                        || appId == OnYardContract.ENHANCEMENT_APP_ID
                        && EnhancementHttpPost.STOCK_NUMBER_KEY.equals(data.getJsonName())
                        || appId == OnYardContract.LOCATION_APP_ID
                        && LocationHttpPost.STOCK_NUMBER_KEY.equals(data.getJsonName())
                        || appId == OnYardContract.SETSALE_APP_ID
                        && SetSaleHttpPost.STOCK_NUMBER_KEY.equals(data.getJsonName())) {
                    stockNumber = data.getValueText() == null ? String.valueOf(data.getValueInt())
                            : data.getValueText();
                    break;
                }
            }
        }

        return stockNumber;
    }

    private void syncSessionData(PendingSyncSession session) throws JSONException, IOException,
    NetworkErrorException {
        switch (session.getAppId()) {
            case OnYardContract.IMAGER_APP_ID:
                syncPendingImagerData(session.getSessionId());
                break;
            case OnYardContract.IMAGER_METRICS_APP_ID:
                syncPendingImagerMetricsData(session.getSessionId());
                break;
            case OnYardContract.CHECKIN_APP_ID:
                syncPendingCheckinData(session.getSessionId());
                break;
            case OnYardContract.CHECKIN_METRICS_APP_ID:
                syncPendingCheckinMetricsData(session.getSessionId());
                break;
            case OnYardContract.LOCATION_APP_ID:
                syncPendingLocationData(session.getSessionId());
                break;
            case OnYardContract.ENHANCEMENT_APP_ID:
                syncPendingEnhancementData(session.getSessionId());
                break;
            case OnYardContract.SETSALE_APP_ID:
                syncPendingSetSaleData(session.getSessionId());
                break;
            default:
                throw new IllegalArgumentException("Invalid App ID");
        }
    }

    private ArrayList<PendingSyncSession> getSessionsToSync() {
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(OnYardContract.DataPendingSync.CONTENT_URI_DISTINCT,
                    new String[] { OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID,
                    OnYardContract.DataPendingSync.COLUMN_NAME_APP_ID }, null, null,
                    OnYardContract.DataPendingSync.COLUMN_NAME_ID + " ASC LIMIT 3000");

            final ArrayList<PendingSyncSession> sessionsToSync = new ArrayList<PendingSyncSession>();
            if (cursor == null || !cursor.moveToFirst()) {
                return sessionsToSync;
            }

            final ArrayList<String> sessionsDoNotSync = SyncHelper.isInSyncWindow(mContentResolver,
                    mBranchTimeZone) ? new ArrayList<String>()
                            : getSessionsSyncNotAllowed();

                    PendingSyncSession session = null;
                    DataPendingSync data = null;
                    do {
                        data = new DataPendingSync(cursor);
                        session = new PendingSyncSession(data.getSessionID(), data.getAppId());

                        if (!sessionsDoNotSync.contains(session.getSessionId())) {
                            sessionsToSync.add(session);
                        }
                    }
                    while (cursor.moveToNext());

                    return sessionsToSync;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private ArrayList<String> getSessionsSyncNotAllowed() {
        Cursor cursor = null;
        final ArrayList<String> sessionIdsDoNotSync = new ArrayList<String>();
        final ArrayList<String> sessionIdsSync = new ArrayList<String>();

        try {
            final int maxDelayMinutes = getMaxCheckInImageSyncDelay();
            final ArrayList<Integer> syncExceptions = getSyncExceptions();

            cursor = mContentResolver.query(
                    OnYardContract.DataPendingSync.CONTENT_URI,
                    null,
                    "(" + OnYardContract.DataPendingSync.COLUMN_NAME_APP_ID + "=? OR "
                            + OnYardContract.DataPendingSync.COLUMN_NAME_APP_ID + "=?) AND ("
                            + OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME + "=? OR "
                            + OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME + "=? OR "
                            + OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME + "=? OR "
                            + OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME + "=? OR "
                            + OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME + "=? OR "
                            + OnYardContract.DataPendingSync.COLUMN_NAME_JSON_NAME + "=?)",
                            new String[] { String.valueOf(OnYardContract.IMAGER_APP_ID),
                            String.valueOf(OnYardContract.IMAGER_METRICS_APP_ID),
                            String.valueOf(ImagerHttpPost.IMAGE_SET_KEY),
                            String.valueOf(ImagerHttpPost.END_DATETIME_KEY),
                            String.valueOf(ImagerHttpPost.SALVAGE_PROVIDER_ID_KEY),
                            String.valueOf(ImagerMetricsHttpPost.IMAGE_SET_KEY),
                            String.valueOf(ImagerMetricsHttpPost.END_DATETIME_KEY),
                            String.valueOf(ImagerMetricsHttpPost.SALVAGE_PROVIDER_ID_KEY) },
                            OnYardContract.DataPendingSync.COLUMN_NAME_ID + " ASC LIMIT 10000");

            if (cursor == null || !cursor.moveToFirst()) {
                return sessionIdsDoNotSync;
            }

            DataPendingSync data = null;
            String currentSessionId = null, currentJsonName = null;
            do {
                data = new DataPendingSync(cursor);

                if (!data.getSessionID().equals(currentSessionId)) {
                    // new session id
                    if (currentSessionId != null) {
                        // not first session id - decide on status of last session id
                        if (!sessionIdsSync.contains(currentSessionId)) {
                            sessionIdsDoNotSync.add(currentSessionId);
                        }
                    }
                }

                currentSessionId = data.getSessionID();
                currentJsonName = data.getJsonName();

                if (data.getAppId() == OnYardContract.IMAGER_APP_ID) {
                    if (ImagerHttpPost.IMAGE_SET_KEY.equals(currentJsonName)
                            && data.getValueInt() != 0) {
                        sessionIdsSync.add(currentSessionId);
                        continue;
                    }

                    if (ImagerHttpPost.END_DATETIME_KEY.equals(currentJsonName)
                            && DataHelper.getUnixUtcTimeStamp() - data.getValueInt() > maxDelayMinutes * 60) {
                        sessionIdsSync.add(currentSessionId);
                        continue;
                    }

                    if (ImagerHttpPost.SALVAGE_PROVIDER_ID_KEY.equals(currentJsonName)
                            && syncExceptions.contains((int) (long) data.getValueInt())) {
                        sessionIdsSync.add(currentSessionId);
                        continue;
                    }
                }


                if (data.getAppId() == OnYardContract.IMAGER_METRICS_APP_ID) {
                    if (ImagerMetricsHttpPost.IMAGE_SET_KEY.equals(currentJsonName)
                            && data.getValueInt() != 0) {
                        sessionIdsSync.add(currentSessionId);
                        continue;
                    }

                    if (ImagerMetricsHttpPost.END_DATETIME_KEY.equals(currentJsonName)
                            && DataHelper.getUnixUtcTimeStamp() - data.getValueInt() > maxDelayMinutes * 60) {
                        sessionIdsSync.add(currentSessionId);
                        continue;
                    }

                    if (ImagerMetricsHttpPost.SALVAGE_PROVIDER_ID_KEY.equals(currentJsonName)
                            && syncExceptions.contains((int) (long) data.getValueInt())) {
                        sessionIdsSync.add(currentSessionId);
                        continue;
                    }
                }
            }
            while (cursor.moveToNext());

            if (currentSessionId != null) {
                // decide on status of last session id
                if (!sessionIdsSync.contains(currentSessionId)) {
                    sessionIdsDoNotSync.add(currentSessionId);
                }
            }

            return sessionIdsDoNotSync;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private ArrayList<Integer> getSyncExceptions() {
        Cursor cursor = null;
        final ArrayList<Integer> exceptions = new ArrayList<Integer>();

        try {
            cursor = mContentResolver.query(OnYardContract.SyncWindowException.CONTENT_URI, null,
                    null, null, null);

            if (cursor == null || !cursor.moveToFirst()) {
                return exceptions;
            }

            do {
                exceptions.add(new SyncWindowExceptionInfo(cursor).getSalvageProviderId());
            }
            while (cursor.moveToNext());

            return exceptions;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private int getMaxCheckInImageSyncDelay() {
        Cursor configCursor = null;

        try {
            configCursor = mContentResolver.query(OnYardContract.Config.CONTENT_URI,
                    new String[] { OnYardContract.Config.COLUMN_NAME_VALUE },
                    OnYardContract.Config.COLUMN_NAME_KEY + "=?",
                    new String[] { OnYardContract.Config.CONFIG_KEY_MAX_SYNC_DELAY }, null);

            if (configCursor != null && configCursor.moveToFirst()) {
                return new ConfigInfo(configCursor).getConfigValueInt();
            }
            else {
                return -1;
            }
        }
        finally {
            if (configCursor != null) {
                configCursor.close();
            }
        }
    }

    private boolean shouldSyncReferenceTables(long lastUpdateTimeUnix)
            throws InvalidParameterException, NetworkErrorException, IOException {
        final ShouldSyncRefHttpGet shouldSync = new ShouldSyncRefHttpGet(mContext);
        shouldSync.setLastUpdateTimeUnix(lastUpdateTimeUnix);

        return shouldSync.get(mHttpClient);
    }

    /**
     * Set the Unix timestamp of the most recent WCF Service consumption that updated the Vehicles
     * table.
     * 
     * @param context The current context.
     */
    private void setLastDBUpdateTime() {
        Cursor cursor = null;
        long newLastUpdateTime = 0;
        final String configKeyPending = OnYardContract.Config.CONFIG_KEY_PENDING_UPDATE_DATE_TIME;
        final String configKeyFinal = OnYardContract.Config.CONFIG_KEY_UPDATE_DATE_TIME;
        try {
            cursor = mContentResolver.query(
                    Uri.withAppendedPath(OnYardContract.Config.CONTENT_URI, configKeyPending),
                    new String[] { OnYardContract.Config.COLUMN_NAME_VALUE }, null, null, null);

            if (cursor == null || !cursor.moveToFirst()) {
                return;
            }

            newLastUpdateTime = cursor.getLong(0);
        }
        finally {
            cursor.close();
        }

        final ContentValues values = new ContentValues();
        values.put(OnYardContract.Config.COLUMN_NAME_KEY, configKeyFinal);
        values.put(OnYardContract.Config.COLUMN_NAME_VALUE, newLastUpdateTime);

        if (DataHelper.isConfigKeyInDB(mContext, configKeyFinal)) {
            mContentResolver
            .update(Uri.withAppendedPath(OnYardContract.Config.CONTENT_KEY_URI_BASE,
                    configKeyFinal), values, null, null);
        }
        else {
            mContentResolver.insert(OnYardContract.Config.CONTENT_URI, values);
        }
    }

    private void enablePilotFunctions() {
        mPreferences
        .setIsEnhancementsEnabled(shouldEnablePilotFunction(PilotFunctionId.ENHANCEMENTS));
        mPreferences.setIsSetSaleEnabled(shouldEnablePilotFunction(PilotFunctionId.SET_SALE));
    }

    private boolean shouldEnablePilotFunction(int pilotFunctionId) {
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(OnYardContract.PilotFunction.CONTENT_URI,
                    new String[] { OnYardContract.PilotFunction.COLUMN_NAME_FUNCTION_ID },
                    OnYardContract.PilotFunction.COLUMN_NAME_FUNCTION_ID + "=?",
                    new String[] { String.valueOf(pilotFunctionId) }, null);

            if (cursor == null) {
                return false;
            }

            // No Pilot_Function records = enabled for all branches
            if (!cursor.moveToFirst()) {
                return true;
            }
            else {
                cursor = mContentResolver.query(
                        OnYardContract.PilotFunction.CONTENT_URI,
                        new String[] { OnYardContract.PilotFunction.COLUMN_NAME_FUNCTION_ID },
                        OnYardContract.PilotFunction.COLUMN_NAME_FUNCTION_ID + "=? AND "
                                + OnYardContract.PilotFunction.COLUMN_NAME_BRANCH_NUMBER + "=?",
                                new String[] { String.valueOf(pilotFunctionId), mEffectiveBranchNumber },
                                null);

                // Pilot_Function record found for this branch = enable
                if (cursor != null && cursor.moveToFirst()) {
                    return true;
                }
                // No Pilot_Function record found for this branch = disable
                else {
                    return false;
                }
            }
        }
        finally {
            cursor.close();
        }
    }

    private void deleteSessionData(String sessionId) {
        mContentResolver.delete(OnYardContract.DataPendingSync.CONTENT_URI,
                OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID + "=?",
                new String[] { String.valueOf(sessionId) });
    }

    private void deleteSessionData(String sessionId, String imagePath) {
        deleteSessionData(sessionId);

        if (imagePath != null && !imagePath.isEmpty()) {
            ImageDirHelper.deleteImage(imagePath);
        }
    }

    private ArrayList<DataPendingSync> getSessionData(String sessionId) {
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(OnYardContract.DataPendingSync.CONTENT_URI, null,
                    OnYardContract.DataPendingSync.COLUMN_NAME_SESSION_ID + "=?",
                    new String[] { sessionId }, OnYardContract.DataPendingSync.COLUMN_NAME_ID
                    + " ASC");

            final ArrayList<DataPendingSync> dataList = new ArrayList<DataPendingSync>();

            if (cursor == null) {
                return dataList;
            }

            while (cursor.moveToNext()) {
                dataList.add(new DataPendingSync(cursor));
            }

            return dataList;
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void insertLastSyncTime(Long syncTime) {
        if (syncTime == null) {
            return;
        }

        final ContentValues values = new ContentValues();
        values.put(OnYardContract.Config.COLUMN_NAME_KEY,
                OnYardContract.Config.CONFIG_KEY_LAST_SYNC_DATE_TIME);
        values.put(OnYardContract.Config.COLUMN_NAME_VALUE, syncTime);

        if (DataHelper.isConfigKeyInDB(mContext,
                OnYardContract.Config.CONFIG_KEY_LAST_SYNC_DATE_TIME)) {
            mContentResolver.update(Uri.withAppendedPath(
                    OnYardContract.Config.CONTENT_KEY_URI_BASE,
                    OnYardContract.Config.CONFIG_KEY_LAST_SYNC_DATE_TIME), values, null, null);
        }
        else {
            mContentResolver.insert(OnYardContract.Config.CONTENT_URI, values);
        }
    }

    private void syncPendingImagerData(String sessionId) throws IOException,
    NetworkErrorException {
        String imageFilePath = "";
        try {
            final ArrayList<DataPendingSync> imagerDataList = getSessionData(sessionId);

            final ImagerHttpPost sessionPost = new ImagerHttpPost(mContext);
            for (final DataPendingSync data : imagerDataList) {

                if (data.getJsonName().equals(ImagerHttpPost.ADMIN_BRANCH_KEY)) {
                    sessionPost.setAdminBranch(data.getValueInt().intValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerHttpPost.END_DATETIME_KEY)) {
                    sessionPost.setEndDateTime(data.getValueInt());
                    continue;
                }
                if (data.getJsonName().equals(ImagerHttpPost.FILE_CONTENTS_KEY)) {
                    imageFilePath = data.getValueText();
                    sessionPost.setFileContents(ImageDirHelper.getFileByteArray(imageFilePath,
                            mContext));
                    continue;
                }
                if (data.getJsonName().equals(ImagerHttpPost.IMAGE_ORDER_KEY)) {
                    sessionPost.setImageOrder(data.getValueInt().shortValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerHttpPost.IMAGE_SET_KEY)) {

                    sessionPost.setImageSet(data.getValueInt().shortValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerHttpPost.START_DATETIME_KEY)) {
                    sessionPost.setStartDateTime(data.getValueInt());
                    continue;
                }
                if (data.getJsonName().equals(ImagerHttpPost.STOCK_NUMBER_KEY)) {
                    sessionPost.setStockNumber(data.getValueInt().intValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerHttpPost.USER_BRANCH_KEY)) {
                    sessionPost.setUserBranch(data.getValueInt().intValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerHttpPost.VIN_KEY)) {
                    sessionPost.setVin(data.getValueText());
                    continue;
                }
            }

            sessionPost.submit(mHttpClient);
            LogHelper.logDebug("Image submitted to server: Stock #" + sessionPost.getStockNumber()
                    + ", ImageOrder: " + sessionPost.getImageOrder());

            deleteSessionData(sessionId, imageFilePath);
        }
        catch (final FileNotFoundException ex) {
            deleteSessionData(sessionId, imageFilePath);
            LogHelper.logError(mContext, ex, this.getClass().getSimpleName());
        }
    }

    private void syncPendingImagerMetricsData(String sessionId)
            throws IOException, NetworkErrorException {
        try {
            final ArrayList<DataPendingSync> imagerMetricsDataList = getSessionData(sessionId);

            final ImagerMetricsHttpPost sessionPost = new ImagerMetricsHttpPost(mContext);
            for (final DataPendingSync data : imagerMetricsDataList) {
                if (data.getJsonName().equals(ImagerMetricsHttpPost.USER_LOGIN_KEY)) {
                    sessionPost.setUserLogin(data.getValueText());
                    continue;
                }
                if (data.getJsonName().equals(ImagerMetricsHttpPost.END_DATETIME_KEY)) {
                    sessionPost.setEndDateTime(data.getValueInt());
                    continue;
                }
                if (data.getJsonName().equals(ImagerMetricsHttpPost.START_DATETIME_KEY)) {
                    sessionPost.setStartDateTime(data.getValueInt());
                    continue;
                }
                if (data.getJsonName().equals(ImagerMetricsHttpPost.STOCK_NUMBER_KEY)) {
                    sessionPost.setStockNumber(data.getValueInt().intValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerMetricsHttpPost.NUM_IMAGES_KEY)) {
                    sessionPost.setNumImagesTaken(data.getValueInt().shortValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerMetricsHttpPost.IMAGE_SET_KEY)) {
                    sessionPost.setImageSet(data.getValueInt().shortValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerMetricsHttpPost.USER_BRANCH_KEY)) {
                    sessionPost.setUserBranch(data.getValueInt().intValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerMetricsHttpPost.IMAGE_TYPE_KEY)) {
                    sessionPost.setImageType(data.getValueInt().intValue());
                    continue;
                }
                if (data.getJsonName().equals(ImagerMetricsHttpPost.ADMIN_BRANCH_KEY)) {
                    sessionPost.setAdminBranch(data.getValueInt().intValue());
                    continue;
                }
            }

            sessionPost.submit(mHttpClient);
            LogHelper.logDebug("Imager metrics submitted to server: Stock #"
                    + sessionPost.getStockNumber());

            deleteSessionData(sessionId);
        }
        catch (final FileNotFoundException ex) {
            deleteSessionData(sessionId);
            LogHelper.logError(mContext, ex, this.getClass().getSimpleName());
        }
    }

    private void syncPendingCheckinData(String sessionId)
            throws InvalidParameterException, NetworkErrorException, IOException {
        try {
            final ArrayList<DataPendingSync> checkinDataList = getSessionData(sessionId);

            final CheckinHttpPost sessionPost = new CheckinHttpPost(mContext);
            for (final DataPendingSync data : checkinDataList) {
                if (data.getValueDouble() == null && data.getValueInt() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueText());
                    continue;
                }
                if (data.getValueText() == null && data.getValueDouble() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueInt());
                    continue;
                }
                if (data.getValueText() == null && data.getValueInt() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueDouble());
                    continue;
                }
            }

            sessionPost.submit(mHttpClient);
            LogHelper.logDebug("Check-in data submitted to server: Stock #"
                    + sessionPost.getStockNumber());

            deleteSessionData(sessionId);
        }
        catch (final FileNotFoundException ex) {
            deleteSessionData(sessionId);
            LogHelper.logError(mContext, ex, this.getClass().getSimpleName());
        }
    }

    private void syncPendingCheckinMetricsData(String sessionId)
            throws IOException, NetworkErrorException {
        try {
            final ArrayList<DataPendingSync> checkinMetricsDataList = getSessionData(sessionId);

            final CheckinMetricsHttpPost sessionPost = new CheckinMetricsHttpPost(mContext);
            for (final DataPendingSync data : checkinMetricsDataList) {
                if (data.getJsonName().equals(CheckinMetricsHttpPost.USER_LOGIN_KEY)) {
                    sessionPost.setUserLogin(data.getValueText());
                    continue;
                }
                if (data.getJsonName().equals(CheckinMetricsHttpPost.USER_BRANCH_KEY)) {
                    sessionPost.setUserBranch(data.getValueInt().intValue());
                    continue;
                }
            }

            sessionPost.submit(mHttpClient);
            LogHelper.logDebug("Check-in metrics submitted to server");

            deleteSessionData(sessionId);
        }
        catch (final FileNotFoundException ex) {
            deleteSessionData(sessionId);
            LogHelper.logError(mContext, ex, this.getClass().getSimpleName());
        }
    }

    private void syncPendingLocationData(String sessionId)
            throws IOException, NetworkErrorException {
        try {
            final ArrayList<DataPendingSync> locationDataList = getSessionData(sessionId);

            final LocationHttpPost sessionPost = new LocationHttpPost(mContext);
            for (final DataPendingSync data : locationDataList) {
                if (data.getJsonName().equals(LocationHttpPost.STOCK_NUMBER_KEY)) {
                    sessionPost.setStockNumber(data.getValueText());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.OLD_AISLE_KEY)) {
                    sessionPost.setOldAisle(data.getValueText());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.OLD_STALL_KEY)) {
                    sessionPost.setOldStall(data.getValueInt().shortValue());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.NEW_AISLE_KEY)) {
                    sessionPost.setNewAisle(data.getValueText());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.NEW_STALL_KEY)) {
                    sessionPost.setNewStall(data.getValueInt().shortValue());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.USER_LOGIN_KEY)) {
                    sessionPost.setUserLogin(data.getValueText());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.USER_BRANCH_KEY)) {
                    sessionPost.setUserBranch(data.getValueInt().intValue());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.END_DATETIME_KEY)) {
                    sessionPost.setEndDateTime(data.getValueInt());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.START_DATETIME_KEY)) {
                    sessionPost.setStartDateTime(data.getValueInt());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.ADMIN_BRANCH_KEY)) {
                    sessionPost.setAdminBranch(data.getValueInt().intValue());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.LATITUDE_KEY)) {
                    sessionPost.setLatitude(data.getValueDouble());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.LONGITUDE_KEY)) {
                    sessionPost.setLongitude(data.getValueDouble());
                    continue;
                }
                if (data.getJsonName().equals(LocationHttpPost.BATTERY_LEVEL_KEY)) {
                    sessionPost.setBatteryLevel(data.getValueInt().shortValue());
                    continue;
                }
            }

            sessionPost.submit(mHttpClient);
            LogHelper.logDebug("Location data submitted to server: Stock #"
                    + sessionPost.getStockNumber());

            deleteSessionData(sessionId);
        }
        catch (final FileNotFoundException ex) {
            deleteSessionData(sessionId);
            LogHelper.logError(mContext, ex, this.getClass().getSimpleName());
        }
    }

    private void syncPendingEnhancementData(String sessionId)
            throws IOException, NetworkErrorException {
        try {
            final ArrayList<DataPendingSync> enhancementDataList = getSessionData(sessionId);

            final EnhancementHttpPost sessionPost = new EnhancementHttpPost(mContext);
            for (final DataPendingSync data : enhancementDataList) {
                if (data.getJsonName() == EnhancementHttpPost.NUM_ENHANCEMENTS_KEY) {
                    continue;
                }
                if (data.getValueDouble() == null && data.getValueInt() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueText());
                    continue;
                }
                if (data.getValueText() == null && data.getValueDouble() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueInt());
                    continue;
                }
                if (data.getValueText() == null && data.getValueInt() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueDouble());
                    continue;
                }
            }

            sessionPost.submit(mHttpClient);
            LogHelper.logDebug("Enhancement data submitted to server: Stock #"
                    + sessionPost.getStockNumber());

            deleteSessionData(sessionId);
        }
        catch (final FileNotFoundException ex) {
            deleteSessionData(sessionId);
            LogHelper.logError(mContext, ex, this.getClass().getSimpleName());
        }
    }

    private void syncPendingSetSaleData(String sessionId)
            throws InvalidParameterException, NetworkErrorException, IOException {
        try {
            final ArrayList<DataPendingSync> setSaleDataList = getSessionData(sessionId);

            final SetSaleHttpPost sessionPost = new SetSaleHttpPost(mContext);
            for (final DataPendingSync data : setSaleDataList) {
                if (data.getValueDouble() == null && data.getValueInt() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueText());
                    continue;
                }
                if (data.getValueText() == null && data.getValueDouble() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueInt());
                    continue;
                }
                if (data.getValueText() == null && data.getValueInt() == null) {
                    sessionPost.setParamValue(data.getJsonName(), data.getValueDouble());
                    continue;
                }
            }

            sessionPost.submit(mHttpClient);
            LogHelper.logDebug("Set Sale data submitted to server: Stock #"
                    + sessionPost.getStockNumber());

            deleteSessionData(sessionId);
        }
        catch (final FileNotFoundException ex) {
            deleteSessionData(sessionId);
            LogHelper.logError(mContext, ex, this.getClass().getSimpleName());
        }
    }


    private void removeTransferredStocks(long lastDbUpdateTime)
            throws InvalidParameterException, NetworkErrorException, IOException {
        final TransferStocksHttpGet request = new TransferStocksHttpGet(mContext);
        request.setQueryStringParams(mEffectiveBranchNumber,
                lastDbUpdateTime);
        request.get(mHttpClient);
    }
}
