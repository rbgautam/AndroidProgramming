package com.iaai.onyard.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.listener.CreateReshootDataListener;
import com.iaai.onyard.listener.GetReshootsInfoListener;
import com.iaai.onyard.listener.PopulateCaptionsListener;
import com.iaai.onyard.listener.WriteImageListener;
import com.iaai.onyard.task.CommitImagerMetricsTask;
import com.iaai.onyard.task.CommitImagesTask;
import com.iaai.onyard.task.GetReshootsInfoTask;
import com.iaai.onyard.task.PopulateCaptionsTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyardproviderapi.classes.ImageCaptionInfo;
import com.iaai.onyardproviderapi.classes.ImageReshootInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Bus;

/**
 * Class that defines and contains all information required for a session of the Imager application.
 * This class should be parceled and passed between Imager Activities.
 * 
 * @author wferguso
 */
public class ReshootData extends OnYardImageData implements PopulateCaptionsListener,
WriteImageListener, GetReshootsInfoListener {

    private ArrayList<ImageReshootInfo> mReshootList;
    private int mImageSet;
    private boolean mCaptionsPopulated = false;
    private boolean mReshootsRetrieved = false;
    private boolean mReshootDataCreated = false;
    private final CreateReshootDataListener mCreateReshootDataListener;
    private ArrayList<ImageReshootInfo> mAllReshootsList;

    /**
     * An object mapping image order to image sequence. Image Order is the key and Image Sequence is
     * the value.
     */
    private HashMap<Integer, Integer> mOrderSequenceMap;

    public ReshootData(long startDateTime, long endDateTime, HashMap<Integer, String> orderPathMap,
            HashMap<Integer, ImageCaptionInfo> orderCaptionMap, int imageTypeId,
            ArrayList<ImageReshootInfo> reshootList, Bus bus, CreateReshootDataListener listener) {
        super(endDateTime, endDateTime, orderPathMap, orderCaptionMap, imageTypeId, bus);
        mReshootList = (ArrayList<ImageReshootInfo>) reshootList.clone();
        mCreateReshootDataListener = listener;
        populateOrderSequenceMap();
    }

    /**
     * Constructor with relevant vehicle data.
     * 
     * @param salvageType The salvage type of the chosen stock.
     * @param stockNumber The vehicle's stock number.
     * @param context The application context.
     */
    public ReshootData(int salvageType, int imageTypeId, String stockNumber, Context context,
            Bus bus, CreateReshootDataListener listener) {
        super(salvageType, imageTypeId, context, bus);
        mReshootList = new ArrayList<ImageReshootInfo>();
        mOrderSequenceMap = new HashMap<Integer, Integer>();
        mCreateReshootDataListener = listener;
        mCaptionsPopulated = false;
        mReshootsRetrieved = false;

        new PopulateCaptionsTask()
        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[] {
                salvageType, imageTypeId, context, this });
        new GetReshootsInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[] {
                stockNumber, context, this });
    }

    /**
     * Get the image sequence of the first untaken image.
     * 
     * @return The image sequence of the first untaken image, or the first image sequence if all images
     *         have been taken.
     */
    @Override
    public int getFirstUntakenImageSequence() {
        int firstUntakenSeq = 999;
        int firstSeq = 0;
        int reshootListImageSeq = 0;

        for (int index = 0; index < mReshootList.size(); index++) {
            reshootListImageSeq = mOrderSequenceMap.get(mReshootList.get(index).getImageOrder());

            if (reshootListImageSeq < firstSeq) {
                firstSeq = reshootListImageSeq;
            }

            if (!isImageTaken(reshootListImageSeq) && reshootListImageSeq < firstUntakenSeq) {
                firstUntakenSeq = reshootListImageSeq;
            }
        }

        if (firstUntakenSeq == 999) {
            return firstSeq;
        }
        else {
            return firstUntakenSeq;
        }
    }

    /**
     * Get the image sequence of the next untaken image. If current image sequence is the only one
     * not taken, return current image sequence. If all images are taken, return 0.
     * 
     * @param currentImageSeq
     * @return 0 if all images are taken, otherwise the sequence of the next image that needs to be
     *         taken.
     */
    @Override
    public int getNextUntakenImageSequence(int currentImageSeq) {
        int reshootListImageSeq;

        // check image sequence greater than current
        for (final ImageReshootInfo reshoot : mReshootList) {
            reshootListImageSeq = mOrderSequenceMap.get(reshoot.getImageOrder());
            if (!isImageTaken(reshootListImageSeq) && reshootListImageSeq > currentImageSeq) {
                return reshootListImageSeq;
            }
        }

        // check image sequence less than current
        final int nextUntakenOrder = getFirstUntakenImageSequence();
        if (!isImageTaken(nextUntakenOrder)) {
            return nextUntakenOrder;
        }
        else {
            return 0;
        }
    }

    /**
     * Get the image sequence of the next untaken image. If there is no image taken after current
     * sequence, returns current sequence. This method assumes that the current image is already
     * taken.
     * 
     * @param currentimageSeq
     * @return Sequence of next Image taken. If there is no image taken after current order, returns
     *         current sequence.
     */
    @Override
    public int getNextTakenImageSequence(int currentImageSeq) {
        int reshootListImageSeq;

        for (final ImageReshootInfo reshoot : mReshootList) {
            reshootListImageSeq = mOrderSequenceMap.get(reshoot.getImageOrder());
            if (isImageTaken(reshootListImageSeq) && reshootListImageSeq > currentImageSeq) {
                return reshootListImageSeq;
            }
        }

        return currentImageSeq;
    }

    /**
     * Get the image sequence of the previous taken Image. If there is no image taken prior to
     * current sequence, return current sequence. It assumes that the current image has already been
     * taken.
     * 
     * @param currentimageSeq
     * @return Sequence of previous taken image. If there is no image taken prior to current
     *         sequence, return current sequence.
     */
    @Override
    public int getPreviousTakenImageSequence(int currentImageSeq) {
        int reshootListImageSeq;
        int previousTakenSeq = currentImageSeq;

        for (final ImageReshootInfo reshoot : mReshootList) {
            reshootListImageSeq = mOrderSequenceMap.get(reshoot.getImageOrder());
            if (reshootListImageSeq == currentImageSeq) {
                break;
            }

            if (isImageTaken(reshootListImageSeq)) {
                previousTakenSeq = reshootListImageSeq;
            }
        }

        return previousTakenSeq;
    }

    public int getImageSet() {
        return mImageSet;
    }

    /**
     * Get an list of all image sequences which have had images taken.
     * 
     * @return A list of all image sequences which have had images taken. List will be empty if no
     *         images have been taken.
     */
    @Override
    public ArrayList<Integer> getTakenImageSequences() {
        final ArrayList<Integer> imageSeqList = new ArrayList<Integer>();

        int imageSeq;
        for (final ImageReshootInfo reshoot : mReshootList) {
            imageSeq = mOrderSequenceMap.get(reshoot.getImageOrder());
            if (isImageTaken(imageSeq)) {
                imageSeqList.add(imageSeq);
            }
        }

        return imageSeqList;
    }

    /**
     * Get a list of all image sequences regardless of whether or not they have been taken. The list
     * will be sorted by ascending image sequence.
     * 
     * @return A list of all reshoot image sequences.
     */
    @Override
    public ArrayList<Integer> getAllImageSequences() {
        final ArrayList<Integer> imageSeqList = new ArrayList<Integer>();

        for (final ImageReshootInfo reshoot : mReshootList) {
            imageSeqList.add(mOrderSequenceMap.get(reshoot.getImageOrder()));
        }
        Collections.sort(imageSeqList);

        return imageSeqList;
    }


    /**
     * Get the number of reshoot images for this stock.
     * 
     * @return Number of reshoot images.
     */
    @Override
    public int getTotalNumberOfImages() {
        return mReshootList.size();
    }

    /**
     * Check whether all REQUIRED images have been taken for this stock.
     * 
     * @param isEnhancement Does nothing for this class.
     * @return True if all required images have been taken, false otherwise.
     */
    @Override
    public boolean areAllRequiredImagesTaken(boolean isEnhancement) {
        for (final ImageReshootInfo reshoot : mReshootList) {
            if (!isImageTaken(mOrderSequenceMap.get(reshoot.getImageOrder()))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Insert all images for this stock into the database. Remove all images from the file system.
     * This should only be called when exiting the Imager workflow.
     */
    @Override
    public void commitImages(VehicleInfo vehicle, Context context) {
        markEndDateTime();
        final int branchNumber = Integer.parseInt(new OnYardPreferences(context)
        .getEffectiveBranchNumber());
        String userLogin = AuthenticationHelper.getLoggedInUser(context.getContentResolver());
        if (userLogin == null) {
            userLogin = OnYard.DEFAULT_USER_LOGIN;
        }

        new CommitImagesTask().execute(vehicle, mStartDateTime, mEndDateTime,
                new HashMap<Integer, String>(mOrderPathMap), context, branchNumber, mImageSet,
                getOrderQualityMap());
        new CommitImagerMetricsTask().execute(vehicle, mStartDateTime, mEndDateTime, context,
                getNumImagesTaken(), mImageSet, userLogin, branchNumber, mImageTypeId);
    }

    private ArrayList<ImageReshootInfo> getTakenReshootList() {
        final ArrayList<ImageReshootInfo> takenReshootList = new ArrayList<ImageReshootInfo>();

        for (final ImageReshootInfo reshoot : mReshootList) {
            if (isImageTaken(mOrderSequenceMap.get(reshoot.getImageOrder()))) {
                takenReshootList.add(reshoot);
            }
        }

        return takenReshootList;
    }

    public boolean equals(ReshootData reshootData) {
        for (int index = 0; index < mReshootList.size(); index++) {
            if (index < reshootData.mReshootList.size()) {
                if (!mReshootList.get(index).equals(reshootData.mReshootList.get(index))) {
                    return false;
                }
            }
        }

        return super.equals(reshootData);
    }

    @Override
    public ReshootData clone() {
        return new ReshootData(mStartDateTime, mEndDateTime, mOrderPathMap, mSequenceCaptionMap,
                mImageTypeId, mReshootList, sBus, mCreateReshootDataListener);
    }

    @Override
    public void onReshootsInfoRetrieved(ArrayList<ImageReshootInfo> allReshootsList) {
        mAllReshootsList = allReshootsList;
        pullAppropriateReshoots(allReshootsList);

        mReshootsRetrieved = true;

        if (isAllDataCreated()) {
            onReshootDataCreated();
        }
    }

    private void pullAppropriateReshoots(ArrayList<ImageReshootInfo> allReshootsList) {
        mImageSet = getMaxImageSet(allReshootsList);
        mReshootList = new ArrayList<ImageReshootInfo>();

        for (final ImageReshootInfo reshoot : allReshootsList) {
            if (reshoot.getImageSet() == mImageSet) {
                mReshootList.add(reshoot);
            }
        }
    }

    private void sortReshootListBySeq() {
        final ArrayList<ImageReshootInfo> newReshootList = new ArrayList<ImageReshootInfo>();

        final int numReshoots = mReshootList.size();
        for (int i = 0; i < numReshoots; i++) {
            ImageReshootInfo minRemainingReshoot = null;
            for (final ImageReshootInfo reshoot : mReshootList) {
                if (minRemainingReshoot == null) {
                    minRemainingReshoot = reshoot;
                    continue;
                }
                else {
                    if (mOrderSequenceMap.get(reshoot.getImageOrder()) <= mOrderSequenceMap
                            .get(minRemainingReshoot.getImageOrder())) {
                        minRemainingReshoot = reshoot;
                        continue;
                    }
                    else {
                        continue;
                    }
                }
            }

            mReshootList.remove(minRemainingReshoot);
            newReshootList.add(minRemainingReshoot);
        }

        mReshootList = newReshootList;
    }

    private int getMaxImageSet(ArrayList<ImageReshootInfo> reshootsList) {
        int maxImageSet = -1;

        for (final ImageReshootInfo reshoot : reshootsList) {
            if (reshoot.getImageSet() > maxImageSet) {
                maxImageSet = reshoot.getImageSet();
            }
        }

        return maxImageSet != -1 ? maxImageSet : 1;
    }

    private void populateOrderSequenceMap() {
        mOrderSequenceMap = new HashMap<Integer, Integer>();

        for (int imageSeq = mFirstImageSequence; imageSeq <= mLastImageSequence; imageSeq++) {
            mOrderSequenceMap.put(mSequenceCaptionMap.get(imageSeq).getImageOrder(), imageSeq);
        }
    }

    @Override
    public void onCaptionsPopulated(HashMap<Integer, ImageCaptionInfo> sequenceCaptionMap) {
        super.onCaptionsPopulated(sequenceCaptionMap);

        populateOrderSequenceMap();

        mCaptionsPopulated = true;

        if (isAllDataCreated()) {
            onReshootDataCreated();
        }
    }

    private boolean isAllDataCreated() {
        return mCaptionsPopulated && mReshootsRetrieved && !mReshootDataCreated;
    }

    private void onReshootDataCreated() {
        mReshootDataCreated = true;
        sortReshootListBySeq();

        if (mCreateReshootDataListener != null) {
            mCreateReshootDataListener.onReshootDataCreated();
        }
    }

    public void removeTakenReshoots() {
        markStartDateTime();
        final ArrayList<ImageReshootInfo> takenReshootList = getTakenReshootList();
        for (final ImageReshootInfo takenReshoot : takenReshootList) {
            mAllReshootsList.remove(takenReshoot);
        }
        mOrderPathMap = new HashMap<Integer, String>();

        pullAppropriateReshoots(mAllReshootsList);
    }
}
