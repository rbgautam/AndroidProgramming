package com.iaai.onyard.session;

import java.io.IOException;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.ImageMode;
import com.iaai.onyard.application.OnYard.ImageSet;
import com.iaai.onyard.application.OnYard.SetSaleId;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.classes.SaveAction;
import com.iaai.onyard.event.SessionDataCreatedEvent;
import com.iaai.onyard.listener.CreateCheckinDataListener;
import com.iaai.onyard.listener.CreateEnhancementDataListener;
import com.iaai.onyard.listener.CreateImagerDataListener;
import com.iaai.onyard.listener.CreateReshootDataListener;
import com.iaai.onyard.listener.GetImageTypeByNameListener;
import com.iaai.onyard.listener.GetVehicleInfoListener;
import com.iaai.onyard.sync.SyncHelper;
import com.iaai.onyard.task.GetImageTypeTask;
import com.iaai.onyard.task.GetVehicleInfoTask;
import com.iaai.onyard.utility.ImageDirHelper;
import com.iaai.onyardproviderapi.classes.ImageTypeInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Bus;


public class OnYardSessionData implements GetVehicleInfoListener, GetImageTypeByNameListener,
CreateReshootDataListener, CreateCheckinDataListener, CreateImagerDataListener,
CreateEnhancementDataListener {

    private VehicleInfo mVehicleInfo;
    private ImagerData mImagerData;
    private ReshootData mReshootData;
    private CheckinData mCheckinData;
    private LocationData mLocationData;
    private SetSaleData mSetSaleData;
    private EnhancementData mEnhancementData;
    private boolean mIsVehiclePopulated;
    private boolean mAreReshootsPopulated;
    private boolean mIsCheckinPopulated;
    private boolean mIsImagerDataPopulated;
    private boolean mIsEnhancementDataPopulated;
    private int mImageTypeId;
    private boolean mWasImagerCommitted;
    private boolean mWasCheckinCommitted;
    private boolean mWasEnhancementCommitted;
    private boolean mWasLocationCommitted;
    private boolean mWasSetSaleCommitted;
    private static Bus sBus;

    public OnYardSessionData(String stockNumber, Context appContext, Bus bus) {
        mImagerData = null;
        mVehicleInfo = null;
        mReshootData = null;
        mLocationData = new LocationData();
        mSetSaleData = null;
        mIsVehiclePopulated = false;
        mAreReshootsPopulated = false;
        mIsCheckinPopulated = false;
        mIsImagerDataPopulated = false;
        mWasImagerCommitted = false;
        mWasCheckinCommitted = false;
        mWasEnhancementCommitted = false;
        mWasLocationCommitted = false;
        mWasSetSaleCommitted = false;
        sBus = bus;
        new GetVehicleInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, appContext,
                this, stockNumber);
    }

    @Override
    public void onVehicleInfoRetrieved(VehicleInfo vehicle, Context appContext) {
        mVehicleInfo = vehicle;

        new GetImageTypeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, appContext, this);

        mIsVehiclePopulated = true;
    }

    @Override
    public void onImageTypeRetrieved(ImageTypeInfo imageType, Context context) {
        final Context appContext = context;
        mImageTypeId = imageType.getImageTypeId();

        mImagerData = new ImagerData(mVehicleInfo.getSalvageType(), mImageTypeId, appContext, sBus,
                this);
        mReshootData = new ReshootData(mVehicleInfo.getSalvageType(), mImageTypeId,
                mVehicleInfo.getStockNumber(), appContext, sBus, this);
        mCheckinData = new CheckinData(mVehicleInfo.getSalvageType(),
                mVehicleInfo.getColorDescription(), appContext, this);
        mSetSaleData = new SetSaleData(mVehicleInfo, appContext);
        mEnhancementData = new EnhancementData(appContext, this, mVehicleInfo);
    }

    public VehicleInfo getVehicleInfo() {
        return mVehicleInfo;
    }

    public OnYardImageData getOnYardImageData(ImageMode imageMode) {
        switch (imageMode) {
            case STANDARD:
                return mImagerData;
            case RESHOOT:
                return mReshootData;
            default:
                return mImagerData;
        }
    }

    public CheckinData getCheckinData() {
        return mCheckinData;
    }

    public LocationData getLocationData() {
        return mLocationData;
    }

    public EnhancementData getEnhancementData() {
        return mEnhancementData;
    }

    public SetSaleData getSetSaleData() {
        return mSetSaleData;
    }

    public void setImagerData(ImagerData data) {
        mImagerData = data;
    }

    public void setReshootData(ReshootData data) {
        mReshootData = data;
    }

    public void setLocationData(LocationData data) {
        mLocationData = data;
    }

    public void setSetSaleData(SetSaleData data) {
        mSetSaleData = data;
    }

    public void setCheckinData(CheckinData data) {
        mCheckinData = data;
    }

    public void setEnhancementData(EnhancementData data) {
        mEnhancementData = data;
    }

    public void recreateCheckinData(Context appContext, int newSalvageType) {
        mVehicleInfo.setSalvageType(newSalvageType);
        mCheckinData = new CheckinData(mVehicleInfo.getSalvageType(), mVehicleInfo.getColorDescription(),
                appContext, this);
    }

    public void recreateCheckinData(Context appContext) {
        mCheckinData = new CheckinData(mVehicleInfo.getSalvageType(),
                mVehicleInfo.getColorDescription(), appContext, this);
    }

    public void recreateImagerData(Context appContext) {
        mReshootData.removeTakenReshoots();
        mImagerData = new ImagerData(mVehicleInfo.getSalvageType(), mImageTypeId, appContext, sBus,
                this);
    }

    public void recreateLocationData() {
        mLocationData = new LocationData();

        onSessionDataCreated();
    }

    public void recreateEnhancementData() {
        mEnhancementData.removeSelectedEnhancements();

        onSessionDataCreated();
    }

    public void recreateSetSaleData(Context appContext) {
        mSetSaleData = new SetSaleData(mVehicleInfo, appContext);

        onSessionDataCreated();
    }

    public void saveImage(int imageSeq, byte[] imageData, Context context, Bus bus,
            ImageMode imageMode) throws IOException {
        switch (imageMode) {
            case STANDARD:
                if (mImagerData != null && mVehicleInfo != null) {
                    mImagerData.saveImage(mVehicleInfo.getStockNumber(), imageSeq, imageData,
                            context);
                }
                break;
            case RESHOOT:
                if (mReshootData != null && mVehicleInfo != null) {
                    mReshootData.saveImage(mVehicleInfo.getStockNumber(), imageSeq, imageData,
                            context);
                }
                break;
            default:
                break;
        }
    }

    public boolean isImagerCommitAllowed() {
        final int totalImagesTaken = mImagerData.getNumImagesTaken()
                + mReshootData.getNumImagesTaken();

        return totalImagesTaken > 0
                && mImagerData.areAllRequiredImagesTaken(mVehicleInfo
                        .hasImages());
    }

    public boolean isImagerIncomplete() {
        if (isImagerCommitAllowed()) {
            return false;
        }
        else {
            final int totalImagesTaken = mImagerData.getNumImagesTaken()
                    + mReshootData.getNumImagesTaken();

            return totalImagesTaken > 0;
        }
    }

    public boolean wasImagerCommitted() {
        return mWasImagerCommitted;
    }

    public boolean isCheckinCommitAllowed() {
        return mCheckinData.areAllRequiredFieldsPopulated() && mCheckinData.isAnyDataEntered();
    }

    public boolean isCheckinIncomplete() {
        if (isCheckinCommitAllowed()) {
            return false;
        }
        else {
            return mCheckinData.isAnyDataEntered();
        }
    }

    public boolean wasCheckinCommitted() {
        return mWasCheckinCommitted;
    }

    public boolean isLocationCommitAllowed() {
        return mLocationData.areAllRequiredFieldsPopulated();
    }

    public boolean isLocationIncomplete() {
        if (isLocationCommitAllowed()) {
            return false;
        }
        else {
            return mLocationData.isAnyDataEntered();
        }
    }

    public boolean wasLocationCommitted() {
        return mWasLocationCommitted;
    }

    public boolean isEnhancementIncomplete() {
        return false;
    }

    public boolean wasEnhancementCommitted() {
        return mWasEnhancementCommitted;
    }

    public boolean isEnhancementCommitAllowed() {
        return mEnhancementData.isAnyDataEntered();
    }

    public boolean isSetSaleIncomplete() {
        return false;
    }

    public boolean wasSetSaleCommitted() {
        return mWasSetSaleCommitted;
    }

    /**
     * Check whether enough Set Sale fields are populated to allow a commit (either verify or save).
     * 
     * @return True if enough fields are populated, false otherwise.
     */
    public boolean isSetSaleCommitAllowed() {
        return mSetSaleData.areAllRequiredFieldsPopulated() && mSetSaleData.isAnyDataEntered();
    }

    /**
     * Check whether Set Sale should be committed along with the specified save action. This method
     * does NOT take validation into account.
     * 
     * @param saveAction The class containing information about which button (verify or save) was
     *            pressed and which page the button was pressed from.
     * @return True if the specified save action should attempt to include Set Sale in the commit,
     *         false otherwise.
     */
    public boolean shouldTrySetSaleCommit(SaveAction saveAction) {
        return saveAction.wasOnSetSalePage() || !saveAction.wasOnSetSalePage()
                && shouldIncludeSetSaleInSave();
    }

    /**
     * Check whether Set Sale should be included in a commit that is triggered by pressing the Save
     * button (not verify). This also determines whether the Save button is displayed on the Set
     * Sale page.
     * 
     * @return True if Set Sale should be committed with the Save button is pressed, false
     *         otherwise.
     */
    public boolean shouldIncludeSetSaleInSave() {
        return mSetSaleData.shouldIncludeInSave();
    }

    public String getStockNumUsingLaneItem(Context context) {
        return mSetSaleData.getStockNumUsingLaneItem(mVehicleInfo, context);
    }

    /**
     * Insert all entered data for this stock into the database. Remove all images from the file
     * system. Should be called when the user hits the save button.
     * 
     * @param context The application context.
     */
    public void commitData(Context context, Location location, SaveAction saveAction) {
        if (mReshootData != null && mVehicleInfo != null && isImagerCommitAllowed()) {
            if (mVehicleInfo.hasImages() && mReshootData.getImageSet() == ImageSet.ENHANCEMENT
                    || !mVehicleInfo.hasImages() && mReshootData.getImageSet() == ImageSet.CHECK_IN) {
                resolveDuplicateImages(context);
            }
        }

        mWasImagerCommitted = false;
        mWasCheckinCommitted = false;
        mWasEnhancementCommitted = false;
        mWasLocationCommitted = false;
        mWasSetSaleCommitted = false;

        if (mImagerData != null && isImagerCommitAllowed() && mImagerData.getNumImagesTaken() > 0) {
            mImagerData.commitImages(mVehicleInfo, context);
            mVehicleInfo.setHasImages(true);
            mWasImagerCommitted = true;
        }
        if (mReshootData != null && isImagerCommitAllowed() && mReshootData.getNumImagesTaken() > 0) {
            mReshootData.commitImages(mVehicleInfo, context);
            mWasImagerCommitted = true;
        }
        if (mCheckinData != null && isCheckinCommitAllowed()) {
            mCheckinData.commitData(mVehicleInfo, context);
            mVehicleInfo.setStatusCode(OnYard.POST_CHECKIN_STATUS_CODE);
            mVehicleInfo.setStatusDescription(OnYard.POST_CHECKIN_STATUS_DESCRIPTION);
            mWasCheckinCommitted = true;
        }
        if (mEnhancementData != null && isEnhancementCommitAllowed()) {
            mEnhancementData.commitData(mVehicleInfo, context);
            mWasEnhancementCommitted = true;
        }
        if (mSetSaleData != null && isSetSaleCommitAllowed() && shouldTrySetSaleCommit(saveAction)
                && getStockNumUsingLaneItem(context) == null) {
            mSetSaleData.commitData(mVehicleInfo, context, location);
            final OnYardPreferences preferences = new OnYardPreferences(context);
            preferences.setSelectedAuctionDatePref(mVehicleInfo.getAuctionDate());
            preferences.setLastAuctionItemSeqNumber(Integer.parseInt(mSetSaleData.getFieldById(
                    SetSaleId.AUCTION_ITEM_SEQUENCE_NUMBER).getEnteredValue()));
            preferences.setLastSaleAisle(mSetSaleData.getFieldById(SetSaleId.SALE_AISLE)
                    .getEnteredValue());
            preferences.setLastAuctionNumber(Integer.parseInt(mSetSaleData
                    .getFieldById(SetSaleId.AUCTION_NUMBER).getSelectedOption().getValue()));
            if (mSetSaleData.getFieldById(SetSaleId.ODD_EVEN_NUMBERING) != null) {
                preferences
                .setIsOddEvenNumberingEnabled(Boolean.parseBoolean(mSetSaleData
                        .getFieldById(SetSaleId.ODD_EVEN_NUMBERING).getSelectedOption()
                        .getValue()));
            }
            mWasSetSaleCommitted = true;
        }
        else {
            if (mLocationData != null && isLocationCommitAllowed()) {
                mLocationData.commitData(mVehicleInfo, context, location);
                mVehicleInfo.setAisle(mLocationData.getNewAisle());
                mVehicleInfo.setStall(mLocationData.getNewStall());
                mWasLocationCommitted = true;
            }
        }
        SyncHelper.requestOnDemandSync(context);
    }

    private void resolveDuplicateImages(Context context) {
        final int numImages = Math.max(mImagerData.getTotalNumberOfImages(),
                mReshootData.getTotalNumberOfImages());

        for (int imageSeq = mImagerData.getFirstImageSequence(); imageSeq < numImages
                + mImagerData.getFirstImageSequence(); imageSeq++) {
            final long StandardImageTimestamp = ImageDirHelper.getTimestampFromFile(mImagerData
                    .getImagePath(imageSeq));
            final long ReshootImageTimestamp = ImageDirHelper.getTimestampFromFile(mReshootData
                    .getImagePath(imageSeq));

            if (StandardImageTimestamp > ReshootImageTimestamp) {
                if (mReshootData.isImageTaken(imageSeq)) {
                    mReshootData.deleteUncommittedImage(imageSeq, context);
                }
            }
            else {
                if (ReshootImageTimestamp > StandardImageTimestamp) {
                    if (mImagerData.isImageTaken(imageSeq)) {
                        mImagerData.deleteUncommittedImage(imageSeq, context);
                    }
                }
            }
        }
    }

    /**
     * Check whether this stock has had any data entered for it that has not yet been saved.
     * 
     * @return True if the user has entered data; false otherwise.
     */
    public boolean hasUnsavedData() {
        if (mImagerData != null
                && mReshootData != null
                && mCheckinData != null
                && mLocationData != null
                && mEnhancementData != null
                && mSetSaleData != null
                && (mImagerData.getNumImagesTaken() > 0 || mReshootData.getNumImagesTaken() > 0
                        || mCheckinData.isAnyDataEntered() || mLocationData.isAnyDataEntered()
                        || mEnhancementData.isAnyDataEntered() || mSetSaleData
                        .shouldIncludeInSave())) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onReshootDataCreated() {
        mAreReshootsPopulated = true;

        if (isAllDataCreated()) {
            onSessionDataCreated();
        }
    }

    private void onSessionDataCreated() {
        if (sBus != null) {
            sBus.post(new SessionDataCreatedEvent());
        }
    }

    private boolean isAllDataCreated() {
        return mIsVehiclePopulated && mAreReshootsPopulated && mIsCheckinPopulated
                && mIsImagerDataPopulated && mIsEnhancementDataPopulated;
    }

    @Override
    public void onCheckinDataCreated() {
        mIsCheckinPopulated = true;

        if (isAllDataCreated()) {
            onSessionDataCreated();
        }
    }

    @Override
    public void onImagerDataCreated() {
        mIsImagerDataPopulated = true;

        if (isAllDataCreated()) {
            onSessionDataCreated();
        }
    }

    @Override
    public void onEnhancementDataCreated() {
        mIsEnhancementDataPopulated = true;

        if (isAllDataCreated()) {
            onSessionDataCreated();
        }
    }
}
