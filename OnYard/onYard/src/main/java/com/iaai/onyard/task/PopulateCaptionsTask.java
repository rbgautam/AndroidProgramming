package com.iaai.onyard.task;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.listener.PopulateCaptionsListener;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.ImageCaptionInfo;
import com.iaai.onyardproviderapi.classes.ImageTypeInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to pull a list of image captions from the database. The image captions are returned in
 * a HashMap once they have been extracted. Parameters for execute:
 * <P>
 * Param 0: salvage type - Integer <br>
 * Param 1: image type ID - Integer <br>
 * Param 2: context - Context <br>
 * Param 3: Otto event bus - Bus <br>
 * </P>
 * 
 * @author wferguso
 */
public class PopulateCaptionsTask extends
AsyncTask<Object, Void, HashMap<Integer, ImageCaptionInfo>> {

    private WeakReference<PopulateCaptionsListener> mWeakListener;
    private static final int DEFAULT_CAPTIONS_SALVAGE_TYPE = 1;

    @Override
    protected HashMap<Integer, ImageCaptionInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final int salvageType = ((Integer) params[0]).intValue();
            final int imageTypeId = ((Integer) params[1]).intValue();
            final Context context = (Context) params[2];
            mWeakListener = new WeakReference<PopulateCaptionsListener>(
                    (PopulateCaptionsListener) params[3]);

            final HashMap<Integer, ImageCaptionInfo> sequenceCaptionsMap = new HashMap<Integer, ImageCaptionInfo>();

            // check if image type has captions for this salvage type
            queryResult = context.getContentResolver().query(OnYardContract.ImageCaption.CONTENT_URI, null,
                    OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE + "=?"
                            + " AND " + OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_TYPE_ID + "=?",
                            new String[] { String.valueOf(salvageType), String.valueOf(imageTypeId) },
                            null);

            if (queryResult == null || !queryResult.moveToFirst()) {
                // check if standard image type has captions for this salvage type
                final int standardImageTypeId = getStandardImageTypeId(context.getContentResolver());

                queryResult = context.getContentResolver().query(OnYardContract.ImageCaption.CONTENT_URI, null,
                        OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE + "=?"
                                + " AND " + OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_TYPE_ID + "=?",
                                new String[] { String.valueOf(salvageType),
                        String.valueOf(standardImageTypeId) },
                        null);

                if (queryResult == null || !queryResult.moveToFirst()) {
                    // use standard image type and default salvage type
                    queryResult = context.getContentResolver().query(
                            OnYardContract.ImageCaption.CONTENT_URI,
                            null,
                            OnYardContract.ImageCaption.COLUMN_NAME_SALVAGE_TYPE + "=?" + " AND "
                                    + OnYardContract.ImageCaption.COLUMN_NAME_IMAGE_TYPE_ID + "=?",
                                    new String[] { String.valueOf(DEFAULT_CAPTIONS_SALVAGE_TYPE),
                                    String.valueOf(standardImageTypeId) }, null);

                    if (queryResult == null || !queryResult.moveToFirst()) {
                        return sequenceCaptionsMap;
                    }
                }
            }

            do {
                final ImageCaptionInfo captionInfo = new ImageCaptionInfo(queryResult);
                if (captionInfo.getSalvageType() != salvageType) {
                    captionInfo.setOverlayFileName("");
                }

                sequenceCaptionsMap.put(captionInfo.getImageSequence(), captionInfo);
            }
            while (queryResult.moveToNext());

            return sequenceCaptionsMap;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[2], e, this.getClass().getSimpleName());
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return new HashMap<Integer, ImageCaptionInfo>();
    }

    @Override
    protected void onPostExecute(HashMap<Integer, ImageCaptionInfo> sequenceCaptionMap) {
        if (mWeakListener != null) {
            mWeakListener.get().onCaptionsPopulated(sequenceCaptionMap);
        }
    }

    private int getStandardImageTypeId(ContentResolver contentResolver) {
        Cursor queryResult = null;
        try {
            queryResult = contentResolver.query(OnYardContract.ImageType.CONTENT_URI,
                    null, OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME + "=?",
                    new String[] { OnYard.DEFAULT_IMAGE_TYPE }, null);

            queryResult.moveToFirst();

            return new ImageTypeInfo(queryResult).getImageTypeId();
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }
}
