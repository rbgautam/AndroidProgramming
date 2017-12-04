package com.iaai.onyard.session;

import java.util.HashMap;

import android.content.Context;

import com.iaai.onyard.listener.CreateImagerDataListener;
import com.iaai.onyard.listener.PopulateCaptionsListener;
import com.iaai.onyard.listener.WriteImageListener;
import com.iaai.onyardproviderapi.classes.ImageCaptionInfo;
import com.squareup.otto.Bus;

/**
 * Class that defines and contains all information required for a session of the Imager application.
 * This class should be parceled and passed between Imager Activities.
 * 
 * @author wferguso
 */
public class ImagerData extends OnYardImageData implements PopulateCaptionsListener,
WriteImageListener {

    private CreateImagerDataListener mCreateImagerDataListener;

    public ImagerData(long startDateTime, long endDateTime, HashMap<Integer, String> orderPathMap,
            HashMap<Integer, ImageCaptionInfo> sequenceCaptionMap, int imageTypeId, Bus bus) {
        super(endDateTime, endDateTime, orderPathMap, sequenceCaptionMap, imageTypeId, bus);
    }

    /**
     * Constructor with vehicle info data. Vehicle info cannot be changed after this point.
     * 
     * @param salvageType The salvage type of the chosen stock.
     * @param context The application context.
     */
    public ImagerData(int salvageType, int imageTypeId, Context context, Bus bus,
            CreateImagerDataListener listener) {
        super(salvageType, imageTypeId, context, bus);

        mCreateImagerDataListener = listener;
    }

    @Override
    public void onCaptionsPopulated(HashMap<Integer, ImageCaptionInfo> sequenceCaptionMap) {
        super.onCaptionsPopulated(sequenceCaptionMap);

        if (mCreateImagerDataListener != null) {
            mCreateImagerDataListener.onImagerDataCreated();
        }
    }

    @Override
    public ImagerData clone() {
        return new ImagerData(mStartDateTime, mEndDateTime, mOrderPathMap, mSequenceCaptionMap,
                mImageTypeId, sBus);
    }
}
