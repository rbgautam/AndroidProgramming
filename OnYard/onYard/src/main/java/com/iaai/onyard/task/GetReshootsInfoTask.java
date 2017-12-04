package com.iaai.onyard.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.listener.GetReshootsInfoListener;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.ImageReshootInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to pull a list of image reshoots for a stock from the database. The image reshoots are
 * returned in an ArrayList of ImageReshootInfo objects once they have been extracted. Parameters
 * for execute:
 * <P>
 * Param 0: stock number - String <br>
 * Param 1: context - Context <br>
 * Param 2: listener - GetReshootsInfoListener <br>
 * </P>
 * 
 * @author wferguso
 */
public class GetReshootsInfoTask extends AsyncTask<Object, Void, ArrayList<ImageReshootInfo>> {

    private WeakReference<GetReshootsInfoListener> mWeakListener;

    @Override
    protected ArrayList<ImageReshootInfo> doInBackground(Object... params) {
        Cursor queryResult = null;
        try {
            final String stockNumber = (String) params[0];
            final Context context = (Context) params[1];
            mWeakListener = new WeakReference<GetReshootsInfoListener>(
                    (GetReshootsInfoListener) params[2]);

            queryResult = context.getContentResolver().query(
                    OnYardContract.ImageReshoot.CONTENT_URI,
                    null,
                    OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER + "=?",
                    new String[] { stockNumber },
                    OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_SET + " ASC,"
                            + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER + " ASC");

            final ArrayList<ImageReshootInfo> reshoots = new ArrayList<ImageReshootInfo>();

            // if stock has no reshoots, default to automobile captions
            if (queryResult == null || !queryResult.moveToFirst()) {
                return reshoots;
            }

            do {
                final ImageReshootInfo reshootInfo = new ImageReshootInfo(queryResult);

                reshoots.add(reshootInfo);
            }
            while (queryResult.moveToNext());

            return reshoots;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[1], e, this.getClass().getSimpleName());
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
        return new ArrayList<ImageReshootInfo>();
    }

    @Override
    protected void onPostExecute(ArrayList<ImageReshootInfo> reshoots) {
        if (mWeakListener != null) {
            mWeakListener.get().onReshootsInfoRetrieved(reshoots);
        }
    }
}
