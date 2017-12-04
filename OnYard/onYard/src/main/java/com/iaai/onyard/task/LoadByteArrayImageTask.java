package com.iaai.onyard.task;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.iaai.onyard.utility.ImageDirHelper;


public class LoadByteArrayImageTask extends AsyncTask<Void, Void, Bitmap> {

    private final WeakReference<ImageView> mImageViewReference;
    private final byte[] mImageData;
    private final int mReqWidth;
    private final int mReqHeight;

    public LoadByteArrayImageTask(ImageView imageView, byte[] imageData, int reqWidth, int reqHeight) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        mImageViewReference = new WeakReference<ImageView>(imageView);
        mImageData = imageData;
        mReqHeight = reqHeight;
        mReqWidth = reqWidth;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Void... params) {
        return ImageDirHelper.getSampledBitmapFromByteArray(mImageData, mReqWidth, mReqHeight);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mImageViewReference != null) {
            final ImageView imageView = mImageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
