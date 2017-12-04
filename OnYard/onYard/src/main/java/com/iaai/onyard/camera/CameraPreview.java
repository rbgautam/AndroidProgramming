package com.iaai.onyard.camera;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.classes.OnYardCamera;
import com.iaai.onyard.utility.LogHelper;

/**
 * Previews the camera screen before an image is taken.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private final SurfaceHolder mHolder;
    private final OnYardCamera mCamera;

    /**
     * Initializes the Camera Preview.
     * 
     * @param context
     * @param camera
     */
    public CameraPreview(Context context, OnYardCamera camera, FrameLayout frame) {
        super(context);
        mCamera = camera;
        // Install SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    /**
     * Overrides surfaceCreated and initializes preview.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (final IOException e) {
            LogHelper.logError(getContext(), e, this.getClass().getSimpleName());
            ((Activity) getContext()).finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder == null || mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        }
        catch (final Exception e) {
            LogHelper.logError(getContext(), e, this.getClass().getSimpleName());
            ((Activity) getContext()).finish();
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        try {
            if (OnYard.isDeviceCN51()) {
                mCamera.setDisplayOrientation(180);
            }
        }
        catch (final Exception e) {
            LogHelper.logError(getContext(), e, this.getClass().getSimpleName());
            ((Activity) getContext()).finish();
        }

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch (final Exception e) {
            LogHelper.logError(getContext(), e, this.getClass().getSimpleName());
            ((Activity) getContext()).finish();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }
}
