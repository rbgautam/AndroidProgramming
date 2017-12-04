package com.iaai.onyard.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.listener.GetImageTypeByNameListener;
import com.iaai.onyard.sync.SyncHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.ImageTypeInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to pull an image type from the database by image type name. Parameters for execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: image type name <br>
 * Param 2: listener - GetImageTypeByNameListener <br>
 * </P>
 * 
 * @author vcherche
 */

public class GetImageTypeTask extends AsyncTask<Object, Void, ImageTypeInfo> {

    private WeakReference<Context> mContext;
    private WeakReference<GetImageTypeByNameListener> mWeakListener;

    @Override
    protected ImageTypeInfo doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            mContext = new WeakReference<Context>((Context) params[0]);
            mWeakListener = new WeakReference<GetImageTypeByNameListener>(
                    (GetImageTypeByNameListener) params[1]);

            final String imageType = SyncHelper.getImageTypeFromDb(mContext.get());
            queryResult = mContext
                    .get()
                    .getContentResolver()
                    .query(OnYardContract.ImageType.CONTENT_URI, null,
                            OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME + "=?",
                            new String[] { imageType }, null);



            // if requested image time is not found return default image type
            if (queryResult == null || !queryResult.moveToFirst()) {
                queryResult = mContext
                        .get()
                        .getContentResolver()
                        .query(OnYardContract.ImageType.CONTENT_URI, null,
                                OnYardContract.ImageType.COLUMN_NAME_IMAGE_TYPE_NAME + "=?",
                                new String[] { OnYard.DEFAULT_IMAGE_TYPE }, null);
                if (queryResult == null || !queryResult.moveToFirst()) {
                    return null;
                }
            }
            return new ImageTypeInfo(queryResult);

        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return new ImageTypeInfo();
    }

    @Override
    protected void onPostExecute(ImageTypeInfo imageType) {
        if (mWeakListener != null && mContext != null) {
            mWeakListener.get().onImageTypeRetrieved(imageType, mContext.get());
        }
    }
}
