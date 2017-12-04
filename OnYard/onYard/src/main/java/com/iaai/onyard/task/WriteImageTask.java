package com.iaai.onyard.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.listener.WriteImageListener;
import com.iaai.onyard.utility.ImageDirHelper;
import com.iaai.onyard.utility.LogHelper;

/**
 * AsyncTask to save a single image in the file system. Parameters for execute:
 * <P>
 * Param 0: stock number - String <br>
 * Param 1: image order - Integer <br>
 * Param 2: image data - byte[] <br>
 * Param 3: context - Context <br>
 * Param 4: callback listener - SaveImageListener <br>
 * </P>
 * 
 * @author wferguso
 */
public class WriteImageTask extends AsyncTask<Object, Void, Object[]> {

    private WeakReference<WriteImageListener> mWeakListener;

    @Override
    protected Object[] doInBackground(Object... params) {
        FileOutputStream fos = null;
        try {
            final int imageOrder = ((Integer) params[1]).intValue();
            final Context context = (Context) params[3];
            mWeakListener = new WeakReference<WriteImageListener>((WriteImageListener) params[4]);

            final File imageDir = ImageDirHelper.getUnsavedImageStorageDir(context);
            final String imageFileName = ImageDirHelper.getRandomFileName();
            final String imageFilePath = new File(imageDir, imageFileName).getPath();

            final File internalFile = new File(imageFilePath);
            fos = new FileOutputStream(internalFile);
            fos.write((byte[]) params[2]);

            return new Object[] { imageOrder, imageFilePath };
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[3], e, this.getClass().getSimpleName());
            return new Object[] { -1, "" };
        } finally {
            try {
                if(fos != null) {
                    fos.close();
                }
            } catch (final IOException e) {
                LogHelper.logError((Context) params[3], e, this.getClass().getSimpleName());
            }
        }
    }

    @Override
    protected void onPostExecute(Object[] params) {
        final int imageOrder = (Integer) params[0];
        final String imageFilePath = (String) params[1];

        if (mWeakListener.get() != null) {
            mWeakListener.get().onImageWritten(imageOrder, imageFilePath);
        }
    }
}
