package com.iaai.onyard.task;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.utility.ImageDirHelper;
import com.iaai.onyard.utility.LogHelper;

/**
 * AsyncTask to delete a single image from the file system. Parameters for execute:
 * <P>
 * Param 0: image path - String <br>
 * Param 1: context - Context <br>
 * </P>
 * 
 * @author wferguso
 */
public class DeleteImageTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        final FileOutputStream fos = null;
        try {
            final String filePath = (String) params[0];

            ImageDirHelper.deleteImage(filePath);

            return null;
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final IOException e) {
                LogHelper.logError((Context) params[1], e, this.getClass().getSimpleName());
            }
        }
    }
}
