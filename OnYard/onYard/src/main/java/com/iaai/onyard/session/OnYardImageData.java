package com.iaai.onyard.session;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.ImageSet;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.classes.Resolution;
import com.iaai.onyard.event.ImageSavedEvent;
import com.iaai.onyard.listener.PopulateCaptionsListener;
import com.iaai.onyard.listener.WriteImageListener;
import com.iaai.onyard.task.CommitImagerMetricsTask;
import com.iaai.onyard.task.CommitImagesTask;
import com.iaai.onyard.task.DeleteImageTask;
import com.iaai.onyard.task.PopulateCaptionsTask;
import com.iaai.onyard.task.WriteImageTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.ImageCaptionInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Bus;

/**
 * Class that defines and contains all information required for a session of the Imager application.
 * This class should be parceled and passed between Imager Activities.
 * 
 * @author wferguso
 */
public abstract class OnYardImageData implements PopulateCaptionsListener, WriteImageListener {

    protected static final int NUM_CHECKIN_IMAGES = 10;
    protected static int mFirstImageSequence = 0;
    protected static int mLastImageSequence = NUM_CHECKIN_IMAGES + mFirstImageSequence - 1;
    protected long mStartDateTime;
    protected long mEndDateTime;
    protected static Bus sBus;

    /**
     * Mapping of image order to image path - index is image order.
     */
    protected HashMap<Integer, String> mOrderPathMap;
    /**
     * Mapping of sequence number to ImageCaptionInfo object.
     */
    protected HashMap<Integer, ImageCaptionInfo> mSequenceCaptionMap;
    protected int mImageTypeId;

    public OnYardImageData(long startDateTime, long endDateTime,
            HashMap<Integer, String> orderPathMap,
            HashMap<Integer, ImageCaptionInfo> sequenceCaptionMap, int imageTypeId, Bus bus) {
        mStartDateTime = startDateTime;
        mEndDateTime = endDateTime;
        mOrderPathMap = (HashMap<Integer, String>) orderPathMap.clone();
        mSequenceCaptionMap = (HashMap<Integer, ImageCaptionInfo>) sequenceCaptionMap.clone();
        mImageTypeId = imageTypeId;
        sBus = bus;
    }

    /**
     * Constructor with relevant vehicle data.
     * 
     * @param salvageType The salvage type of the chosen stock.
     * @param imageTypeId Image type ID for the chosen stock.
     * @param context The application context.
     */
    public OnYardImageData(int salvageType, int imageTypeId, Context context, Bus bus) {
        markStartDateTime();
        markEndDateTime();
        mOrderPathMap = new HashMap<Integer, String>();
        mSequenceCaptionMap = new HashMap<Integer, ImageCaptionInfo>();
        mImageTypeId = imageTypeId;
        sBus = bus;

        new PopulateCaptionsTask()
        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[] {
                salvageType, imageTypeId, context, this });
    }

    /**
     * Get the image sequence of the first untaken image.
     * 
     * @return The image sequence of the first untaken image, or the first image sequence if all
     *         images have been taken.
     */
    public int getFirstUntakenImageSequence() {
        for (int imageSeq = mFirstImageSequence; imageSeq <= mLastImageSequence; imageSeq++) {
            if (!isImageTaken(imageSeq)) {
                return imageSeq;
            }
        }

        return mFirstImageSequence;
    }

    /**
     * Get the image sequence of the next untaken image. If current image sequence is the only one
     * not taken, return current image sequence. If all images are taken, return 0.
     * 
     * @param currentImageSq
     * @return 0 if all images are taken, otherwise the sequence of the next image that needs to be
     *         taken.
     */
    public int getNextUntakenImageSequence(int currentImageSeq) {
        if (areAllRequiredImagesTaken(false)) {
            return 0;
        }
        else {
            // check image sequence greater than current
            for (int nextImageSeq = currentImageSeq + 1; nextImageSeq <= mLastImageSequence; nextImageSeq++) {
                if (!isImageTaken(nextImageSeq)) {
                    return nextImageSeq;
                }
            }
            // check image sequence less than current
            for (int nextImageSeq = mFirstImageSequence; nextImageSeq <= currentImageSeq; nextImageSeq++) {
                if (!isImageTaken(nextImageSeq)) {
                    return nextImageSeq;
                }
            }
        }
        return 0;
    }

    public int getFirstImageSequence() {
        return mFirstImageSequence;
    }

    /**
     * Get the image sequence of the next untaken image. If there is no image taken after current
     * sequence, return current sequence. This method assumes that the current image is already
     * taken.
     * 
     * @param currentImageSeq
     * @return Sequence of next taken image. If there is no image taken after current sequence,
     *         return current sequence.
     */
    public int getNextTakenImageSequence(int currentImageSeq) {
        int nextImageSeq = currentImageSeq;
        for (int imageSeq = currentImageSeq + 1; imageSeq <= mLastImageSequence; imageSeq++) {
            if (isImageTaken(imageSeq)) {
                nextImageSeq = imageSeq;
                break;
            }

        }
        return nextImageSeq;
    }

    /**
     * Get the image sequence of the previous taken Image. If there is no image taken prior to
     * current sequence, return current sequence. This method assumes that the current image has
     * already been taken.
     * 
     * @param currentimageSeq
     * @return Sequence of previous taken image. If there is no image taken prior to current
     *         sequence, return current sequence.
     */
    public int getPreviousTakenImageSequence(int currentimageSeq) {
        int nextImageSeq = currentimageSeq;
        for (int imageSeq = currentimageSeq - 1; imageSeq >= mFirstImageSequence; imageSeq--) {
            if (isImageTaken(imageSeq)) {
                nextImageSeq = imageSeq;
                break;
            }

        }
        return nextImageSeq;
    }

    /**
     * Get the number of images taken.
     * 
     * @return The number of images taken.
     */
    public int getNumImagesTaken() {
        int numImagesTaken = 0;
        for (int imageSeq = mFirstImageSequence; imageSeq <= mLastImageSequence; imageSeq++) {
            if (isImageTaken(imageSeq)) {
                numImagesTaken++;
            }
        }

        return numImagesTaken;
    }

    /**
     * Get an list of all image sequences which have had images taken.
     * 
     * @return A list of all image sequences which have had images taken. List will be empty if no
     *         images have been taken.
     */
    public ArrayList<Integer> getTakenImageSequences() {
        final ArrayList<Integer> imageSeqList = new ArrayList<Integer>();

        for (int imageSeq = mFirstImageSequence; imageSeq <= mLastImageSequence; imageSeq++) {
            if (isImageTaken(imageSeq)) {
                imageSeqList.add(imageSeq);
            }
        }

        return imageSeqList;
    }

    /**
     * Get a list of all image sequences regardless of whether or not they have been taken.
     * 
     * @return A list of all image sequences.
     */
    public ArrayList<Integer> getAllImageSequences() {
        final ArrayList<Integer> imageSeqList = new ArrayList<Integer>();

        for (int imageSeq = mFirstImageSequence; imageSeq <= mLastImageSequence; imageSeq++) {
            imageSeqList.add(imageSeq);
        }

        return imageSeqList;
    }

    /**
     * Get the number of check-in images for this salvage type.
     * 
     * @return Number of required check-in images
     */
    public int getTotalNumberOfImages() {
        return NUM_CHECKIN_IMAGES;
    }

    /**
     * Check whether all REQUIRED images have been taken for this stock.
     * 
     * @return True if all required images have been taken, false otherwise.
     */
    public boolean areAllRequiredImagesTaken(boolean isEnhancement) {
        if (isEnhancement) {
            return true;
        }

        for (int imageSeq = mFirstImageSequence; imageSeq <= mLastImageSequence; imageSeq++) {
            if (!isImageTaken(imageSeq)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Save the image data with the specified image sequence. If image exists for that image
     * sequence, existing image will be deleted prior to save.
     * 
     * @param stockNumber The full stock number of the vehicle.
     * @param imageSeq The sequence of the image to be saved.
     * @param imageData The image data to save.
     * @param context The application context.
     * @throws IOException When there was an error creating the unsaved image storage directory.
     */
    public void saveImage(String stockNumber, int imageSeq, byte[] imageData, Context context)
            throws IOException {
        if (isImageTaken(imageSeq))
        {
            deleteUncommittedImage(imageSeq, context);
        }

        new WriteImageTask().execute(stockNumber, getOrderFromSeq(imageSeq), imageData, context,
                this);
    }

    /**
     * Insert all images for this stock into the database. Remove all images from the file system.
     * This should only be called when exiting the Imager workflow.
     */
    public void commitImages(VehicleInfo vehicle, Context context) {
        markEndDateTime();
        final int branchNumber = Integer.parseInt(new OnYardPreferences(context)
        .getEffectiveBranchNumber());
        final int imageSet = vehicle.hasImages() ? ImageSet.ENHANCEMENT : ImageSet.CHECK_IN;
        String userLogin = AuthenticationHelper.getLoggedInUser(context.getContentResolver());
        if (userLogin == null) {
            userLogin = OnYard.DEFAULT_USER_LOGIN;
        }

        new CommitImagesTask().execute(new VehicleInfo(vehicle), mStartDateTime, mEndDateTime,
                new HashMap<Integer, String>(mOrderPathMap), context, branchNumber, imageSet,
                getOrderQualityMap());
        new CommitImagerMetricsTask().execute(vehicle, mStartDateTime, mEndDateTime, context,
                getNumImagesTaken(), imageSet, userLogin, branchNumber, mImageTypeId);
    }

    protected HashMap<Integer, Integer> getOrderQualityMap() {
        final HashMap<Integer, Integer> orderQualityMap = new HashMap<Integer, Integer>();
        for (int seq = mFirstImageSequence; seq <= mLastImageSequence; seq++) {
            final ImageCaptionInfo caption = mSequenceCaptionMap.get(seq);
            orderQualityMap.put(caption.getImageOrder(), caption.getJpegQuality());
        }

        return orderQualityMap;
    }

    /**
     * Delete all images for this stock from the file system. This should only be called when
     * exiting the Imager workflow.
     * 
     * @param context The application context.
     */
    public void deleteAllUncommittedImages(Context context) {
        for (int imageSeq = mFirstImageSequence; imageSeq <= mLastImageSequence; imageSeq++) {
            if (isImageTaken(imageSeq)) {
                deleteUncommittedImage(imageSeq, context);
            }
        }
    }

    /**
     * Delete the image with the specified image sequence.
     * 
     * @param imageSeq The sequence of the image to be deleted.
     * @param context The application context.
     */
    public void deleteUncommittedImage(int imageSeq, Context context) {
        if (isImageTaken(imageSeq) && !mOrderPathMap.isEmpty()) {
            final int imageOrder = getOrderFromSeq(imageSeq);
            new DeleteImageTask().execute(mOrderPathMap.get(imageOrder), context);
            mOrderPathMap.remove(imageOrder);
        }
    }

    protected void markEndDateTime() {
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
    }

    protected void markStartDateTime() {
        mStartDateTime = DataHelper.getUnixUtcTimeStamp();
    }

    /**
     * Get the image caption text for the specified image sequence.
     * 
     * @param imageSeq The sequence of the image for which to get the caption text.
     * @return The caption text, or an empty string if caption text has not yet been pulled from the
     *         database.
     */
    public String getImageCaption(int imageSeq) {
        return mSequenceCaptionMap.containsKey(imageSeq) ? mSequenceCaptionMap.get(imageSeq)
                .getCaption() : "";
    }

    /**
     * Get the default focus mode for the specified image sequence.
     * 
     * @param imageSeq The sequence of the image for which to get default focus mode.
     * @return Default focus mode
     */
    public String getDefaultFocusMode(int imageSeq) {
        return mSequenceCaptionMap.get(imageSeq).getDefaultFocusMode();
    }

    /**
     * Get the JPEG quality for the specified image sequence.
     * 
     * @param imageSeq The sequence of the image for which to get JPEG quality.
     * @return The JPEG quality for the specified image.
     */
    public int getJpegQuality(int imageSeq) {
        return mSequenceCaptionMap.get(imageSeq).getJpegQuality();
    }

    /**
     * Check whether or not the level line should be shown for the specified image sequence.
     * 
     * @param imageSeq The sequence of the image for which to check the level line setting.
     * @return True if the level line is enabled for this image, false otherwise.
     */
    public boolean isLevelLineEnabled(int imageSeq) {
        return mSequenceCaptionMap.get(imageSeq).isLevelLineEnabled();
    }

    /**
     * Check whether there exists an overlay image for the specified image sequence.
     * 
     * @param imageSeq The sequence of the image for which to check if an overlay image exists.
     * @return True if an overlay image exists, false otherwise.
     */
    public boolean hasOverlayImage(int imageSeq) {
        return mSequenceCaptionMap.containsKey(imageSeq) ? mSequenceCaptionMap.get(imageSeq)
                .hasOverlay() : false;
    }

    /**
     * Get the resource id of the overlay image corresponding to the specified image sequence.
     * 
     * @param context The current context - can be activity context.
     * @param imageSeq The sequence of the image for which to check if an overlay image exists.
     * @return The overlay resource id.
     */
    public int getOverlayImageResourceId(Context context, int imageSeq) {
        try {
            if (mSequenceCaptionMap.containsKey(imageSeq)) {
                String fileName = "";
                fileName = mSequenceCaptionMap.get(imageSeq).getOverlayFileName();

                if (fileName == null) {
                    throw new FileNotFoundException();
                }
                final int resId = context.getResources().getIdentifier(fileName, "drawable",
                        context.getPackageName());
                if (resId == 0) {
                    throw new FileNotFoundException();
                }
                return resId;
            }
            else {
                throw new FileNotFoundException();
            }
        }
        catch (final Exception e) {
            LogHelper.logDebug("Overlay resource not found");
            return 0;
        }
    }

    public int getThumbOverlayImageResourceId(Context context, int imageSeq) {
        try {
            if (mSequenceCaptionMap.containsKey(imageSeq)) {
                String fileName = "";
                fileName = mSequenceCaptionMap.get(imageSeq).getThumbOverlayFileName();

                if (fileName == null) {
                    throw new FileNotFoundException();
                }
                final int resId = context.getResources().getIdentifier(fileName, "drawable",
                        context.getPackageName());
                if (resId == 0) {
                    throw new FileNotFoundException();
                }
                return resId;
            }
            else {
                throw new FileNotFoundException();
            }
        }
        catch (final Exception e) {
            LogHelper.logDebug("Thumbnail overlay resource not found");
            return 0;
        }
    }

    /**
     * Get the file system path of the image with the specified sequence.
     * 
     * @param imageSeq The sequence of the image for which to get the path.
     * @return The path of the image in the file system, or null if the image has not been taken.
     */
    public String getImagePath(int imageSeq) {
        return mOrderPathMap.get(getOrderFromSeq(imageSeq));
    }

    /**
     * Check whether image with specified image sequence has been taken.
     * 
     * @param imageSeq The image sequence to check.
     * @return True if the image has been taken, false otherwise.
     */
    public boolean isImageTaken(int imageSeq) {
        final int imageOrder = getOrderFromSeq(imageSeq);
        if (mOrderPathMap == null) {
            return false;
        }
        else {
            return mOrderPathMap.get(imageOrder) != null;
        }
    }

    public Resolution getMinImageRes(int imageSeq) {
        final int width = mSequenceCaptionMap.get(imageSeq).getMinImageWidth();
        final int height = mSequenceCaptionMap.get(imageSeq).getMinImageHeight();

        return new Resolution(width, height);
    }

    protected int getOrderFromSeq(int imageSeq) {
        if (mSequenceCaptionMap.get(imageSeq) == null) {
            return 0;
        }
        else {
            return mSequenceCaptionMap.get(imageSeq).getImageOrder();
        }
    }

    @Override
    public void onCaptionsPopulated(HashMap<Integer, ImageCaptionInfo> sequenceCaptionMap) {
        mSequenceCaptionMap = sequenceCaptionMap;

        if (mSequenceCaptionMap.isEmpty()) {
            return;
        }

        mImageTypeId = mSequenceCaptionMap.get(mFirstImageSequence).getImageTypeId();

        int firstKey = 0;
        while (!mSequenceCaptionMap.containsKey(firstKey)) {
            firstKey++;
        }
        mFirstImageSequence = firstKey;

        int lastKey = firstKey;
        while (mSequenceCaptionMap.containsKey(lastKey)) {
            lastKey++;
        }
        mLastImageSequence = lastKey - 1;
    }

    @Override
    public void onImageWritten(int imageOrder, String path) {
        mOrderPathMap.put(imageOrder, path);

        if (sBus != null) {
            sBus.post(new ImageSavedEvent(imageOrder != -1));
        }
    }

    public boolean equals(OnYardImageData imagerData) {
        if (!(mStartDateTime == imagerData.mStartDateTime
                && mEndDateTime == imagerData.mEndDateTime
                && mImageTypeId == imagerData.mImageTypeId)) {
            return false;
        }

        for (final Entry<Integer, String> path : mOrderPathMap.entrySet()) {
            if (!path.getValue().equals(imagerData.mOrderPathMap.get(path.getKey()))) {
                return false;
            }
        }

        for (final Entry<Integer, ImageCaptionInfo> caption : mSequenceCaptionMap.entrySet()) {
            if (!caption.getValue().equals(imagerData.mSequenceCaptionMap.get(caption.getKey()))) {
                return false;
            }
        }

        return true;
    }
}
