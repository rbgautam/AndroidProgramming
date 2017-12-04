package com.iaai.onyard.task;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.ImageReshootInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to soft delete a set of image reshoots for a stock from the database. The image
 * reshoots to delete are specified in an ArrayList. Parameters for execute:
 * <P>
 * Param 0: reshoots to delete - ArrayList[ImageReshootInfo] <br>
 * Param 1: context - Context <br>
 * </P>
 * 
 * @author wferguso
 */
public class ClearTakenReshootsTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        try {
            final ArrayList<ImageReshootInfo> reshootList = (ArrayList<ImageReshootInfo>) params[0];
            final Context context = (Context) params[1];

            for (final ImageReshootInfo reshoot : reshootList) {
                final String selection = OnYardContract.ImageReshoot.COLUMN_NAME_STOCK_NUMBER
                        + "=? AND " + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_ORDER
                        + "=? AND " + OnYardContract.ImageReshoot.COLUMN_NAME_IMAGE_SET + "=?";
                final String[] selectionArgs = new String[] { reshoot.getStockNumber(),
                        String.valueOf(reshoot.getImageOrder()),
                        String.valueOf(reshoot.getImageSet()) };

                context.getContentResolver().delete(OnYardContract.ImageReshoot.CONTENT_URI,
                        selection, selectionArgs);
            }
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[1], e, this.getClass().getSimpleName());
        }
        return null;
    }
}
