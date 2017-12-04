package com.iaai.onyard.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.utility.ResolutionHelper;

public class OnYardCamera {

    private final Camera mCamera;
    private final Camera.Parameters mParameters;
    private int mCurrentZoomLevel;
    private final int mZoomIncrement;
    private ArrayList<String> mFocusList;
    private ArrayList<String> mFlashList;

    private static String DRAWABLE = "drawable";
    private static String FLASH_ON = "ic_action_flash_on";
    private static String FLASH_OFF = "ic_action_flash_off";
    private static String FLASH_AUTO = "ic_action_flash_automatic";

    private static String FOCUS_AUTO = "focus_auto";
    private static String FOCUS_INFINITY = "focus_infinity";
    private static String FOCUS_MACRO = "focus_macro";
    private static String FOCUS_CONTINUOUS = "focus_continuous";

    private static int NUM_OF_ZOOM_STOPS = 10;

    public OnYardCamera(Context context) {
        mCamera = getCameraInstance();
        mParameters = mCamera.getParameters();
        mCurrentZoomLevel = 0;

        // Number of zoom stops/2 added for more accurate rounding
        mZoomIncrement = (mParameters.getMaxZoom() + NUM_OF_ZOOM_STOPS / 2) / NUM_OF_ZOOM_STOPS;

        init(context);
    }

    private void init(Context context) {
        // set preview orientation and size
        final Size largestPreview = getLargestPreview(mParameters.getSupportedPreviewSizes());
        mParameters.set("orientation", "landscape");
        mParameters.setPreviewSize(largestPreview.width, largestPreview.height);
        commitParameters();

        // construct focus list
        mFocusList = new ArrayList<String>();
        final List<String> supportedFocusModes = mParameters.getSupportedFocusModes();
        if (supportedFocusModes != null) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mFocusList.add(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)
                    && !OnYard.isDeviceNote3()) {
                mFocusList.add(Camera.Parameters.FOCUS_MODE_INFINITY);
            }
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_MACRO)) {
                mFocusList.add(Camera.Parameters.FOCUS_MODE_MACRO);
            }
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mFocusList.add(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        // construct flash list
        mFlashList = new ArrayList<String>();
        final List<String> supportedFlashModes = mParameters.getSupportedFlashModes();
        if (supportedFlashModes != null) {
            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                mFlashList.add(Camera.Parameters.FLASH_MODE_AUTO);
            }
            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                mFlashList.add(Camera.Parameters.FLASH_MODE_ON);
            }
            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                mFlashList.add(Camera.Parameters.FLASH_MODE_OFF);
            }


            initFlashMode(context);
        }
    }

    /**
     * Try to set the camera flash to the specified flash mode. If device does not support the
     * specified flash mode, camera flash mode is not changed.
     * 
     * @param flashMode The desired flash mode.
     */
    private void initFlashMode(Context context) {
        final OnYardPreferences pref = new OnYardPreferences(context);
        final String flashModePref = pref.getFlashMode();
        final List<String> flashModes = mParameters.getSupportedFlashModes();
        if (flashModes.contains(flashModePref)) {
            mParameters.setFlashMode(flashModePref);
            commitParameters();
        }
        else {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                commitParameters();
                pref.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
            else {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                commitParameters();
                pref.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
        }
    }

    /**
     * Try to set the camera focus parameter to the specified focus mode. If the device doesn't
     * support the specified focus mode, camera parameters are not changed.
     * 
     * @param focusMode The focus mode constant from Camera.Parameters.
     */
    public void enableFocusMode(String focusMode) {
        final List<String> supportedFocusModes = mParameters.getSupportedFocusModes();
        if (supportedFocusModes.contains(focusMode)) {
            mCamera.cancelAutoFocus();
            mParameters.setFocusMode(focusMode);
            commitParameters();
        }
        else {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mCamera.cancelAutoFocus();
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                commitParameters();
            }
            else {
                mCamera.cancelAutoFocus();
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                commitParameters();
            }
        }
    }

    public boolean isFlashSupported() {
        return mFlashList.size() > 1;
    }

    public boolean isFocusSupported() {
        return mFocusList.size() > 1;
    }

    /**
     * Check whether the camera has focus mode set to the specified mode.
     * 
     * @param focusMode The focus mode to compare to the currently enabled mode.
     * @return True if the camera focus mode is set to the specified mode, false otherwise.
     */
    public boolean isFocusModeEnabled(String focusMode) {
        try {
            if (mParameters.getFocusMode().equals(focusMode)) {
                return true;
            }
            return false;
        }
        catch (final Exception e) {
            return false;
        }
    }

    /**
     * Display the specified resource as the background of the specified ImageView.
     * 
     * @param overlayView The view to display the image.
     * @param resourceId The id of the resource to be displayed.
     */
    public void enableOverlayImage(ImageView overlayView, int resourceId) {
        overlayView.setImageResource(resourceId);
        overlayView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the specified ImageView.
     * 
     * @param overlayView the view to hide.
     */
    public void disableOverlayImage(ImageView overlayView) {
        overlayView.setVisibility(View.GONE);
    }

    public void setJpegQuality(int jpegQuality) {
        mParameters.setJpegQuality(jpegQuality);
        commitParameters();
    }

    /**
     * Set the image resolution to the lowest supported 4:3 resolution that is greater than the
     * specified minimum.
     * 
     * @param minImageRes
     * @return
     */
    public void setImageResolution(Resolution minRes) {
        final List<Size> supportedResList = mParameters.getSupportedPictureSizes();

        Size selectedRes = null;
        for (final Size res : supportedResList) {
            // if this res matches min, select this res and break
            if (res.width == minRes.getWidth() && res.height == minRes.getHeight()) {
                selectedRes = res;
                break;
            }

            // if this res is less than min, skip this res
            if (res.width < minRes.getWidth() || res.height < minRes.getHeight()) {
                continue;
            }

            // if no res has been selected and this res is 4:3, select this res
            if (selectedRes == null && ResolutionHelper.isDesiredRatio(res)) {
                selectedRes = res;
                continue;
            }

            // if this res is 4:3 and this res is smaller than selected, select this res
            if (ResolutionHelper.isDesiredRatio(res) && res.width < selectedRes.width
                    && res.height < selectedRes.height) {
                selectedRes = res;
                continue;
            }
        }

        if (selectedRes == null) {
            throw new UnsupportedOperationException(
                    "There is no supported image resolution with the desired ratio.");
        }

        mParameters.setPictureSize(selectedRes.width, selectedRes.height);
        commitParameters();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void disableShutterSound() {
        if (Build.VERSION.SDK_INT >= 17) {
            mCamera.enableShutterSound(false);
        }
    }

    public void setRotation(int rotation) {
        mParameters.setRotation(rotation);
        commitParameters();
    }

    public void autoFocus(AutoFocusCallback focusCallback) {
        mCamera.autoFocus(focusCallback);
    }

    public void takePicture(ShutterCallback shutterCallback, PictureCallback jpegCallback) {
        mCamera.takePicture(shutterCallback, null, jpegCallback);
    }

    public void startPreview() {
        mCamera.startPreview();
    }

    public void stopPreview() {
        mCamera.stopPreview();
    }

    public void setDisplayOrientation(int degrees) {
        mCamera.setDisplayOrientation(degrees);
    }

    public void release() {
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.release();
    }

    public void zoomIn() {
        if (mParameters.isZoomSupported() && mCurrentZoomLevel < mParameters.getMaxZoom()) {
            mCurrentZoomLevel = Math.min(mCurrentZoomLevel + mZoomIncrement,
                    mParameters.getMaxZoom());
            mParameters.setZoom(mCurrentZoomLevel);
            commitParameters();
        }
    }

    public void zoomOut() {
        if (mParameters.isZoomSupported() && mCurrentZoomLevel > 0) {
            mCurrentZoomLevel = Math.max(mCurrentZoomLevel - mZoomIncrement, 0);
            mParameters.setZoom(mCurrentZoomLevel);
            commitParameters();
        }
    }

    public void resetZoom() {
        if (mParameters.isZoomSupported()) {
            mCurrentZoomLevel = 0;
            mParameters.setZoom(mCurrentZoomLevel);
            commitParameters();
        }
    }

    public boolean isMaxZoom() {
        return mParameters.isZoomSupported() && mCurrentZoomLevel == mParameters.getMaxZoom();
    }

    public boolean isMinZoom() {
        return mCurrentZoomLevel == 0;
    }

    public void setPreviewDisplay(SurfaceHolder holder) throws IOException {
        mCamera.setPreviewDisplay(holder);
    }

    /**
     * Sets next mode from list of flash modes supported by device
     * 
     * @param context
     * @return ID of drawable resource for flash mode icon
     */
    public int toggleFlashMode(Context context) {
        final String flashParam = mParameters.getFlashMode();
        final int currentFlashIndex = mFlashList.indexOf(flashParam);

        final String newFlashMode = getNextStringInList(mFlashList, currentFlashIndex);

        mParameters.setFlashMode(newFlashMode);
        commitParameters();
        new OnYardPreferences(context).setFlashMode(newFlashMode);

        return getFlashModeDrawableId(context);
    }

    /**
     * @param context
     * @return resource Id for the flash mode icon
     */
    public int getFlashModeDrawableId(Context context) {
        int drawableId = 0;

        final String flashMode = mParameters.getFlashMode();

        if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
            drawableId = context.getResources().getIdentifier(FLASH_ON, DRAWABLE,
                    context.getPackageName());
        }
        if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
            drawableId = context.getResources().getIdentifier(FLASH_OFF, DRAWABLE,
                    context.getPackageName());
        }
        if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
            drawableId = context.getResources().getIdentifier(FLASH_AUTO, DRAWABLE,
                    context.getPackageName());
        }
        return drawableId;
    }

    /**
     * @param context
     * @return resource Id for the flash mode icon
     */
    public int getFocusModeDrawableId(Context context) {
        int drawableId = 0;
        final String focusMode = mParameters.getFocusMode();

        if (focusMode.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_AUTO)) {
            drawableId = context.getResources().getIdentifier(FOCUS_AUTO, DRAWABLE,
                    context.getPackageName());
        }
        if (focusMode.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_INFINITY)) {
            drawableId = context.getResources().getIdentifier(FOCUS_INFINITY, DRAWABLE,
                    context.getPackageName());
        }
        if (focusMode.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_MACRO)) {
            drawableId = context.getResources().getIdentifier(FOCUS_MACRO, DRAWABLE,
                    context.getPackageName());
        }
        if (focusMode.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            drawableId = context.getResources().getIdentifier(FOCUS_CONTINUOUS, DRAWABLE,
                    context.getPackageName());
        }
        return drawableId;
    }

    public int toggleFocusMode(Context context) {
        final int currentFocusIndex = mFocusList.indexOf(mParameters.getFocusMode());
        final String newFocusMode = getNextStringInList(mFocusList, currentFocusIndex);

        enableFocusMode(newFocusMode);

        return getFocusModeDrawableId(context);
    }

    private String getNextStringInList(ArrayList<String> list, int currentIndex) {
        final int newIndex = (currentIndex + 1) % list.size();

        return list.get(newIndex);
    }

    private Size getLargestPreview(List<Camera.Size> sizes) {
        Size largest = null;
        for (final Size size : sizes) {
            if (ResolutionHelper.isDesiredRatio(size)) {
                if (largest == null) {
                    largest = size;
                }
                else
                    if (size.width * size.height > largest.width * largest.height) {
                        largest = size;
                    }
            }
        }
        return largest;
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            if (Camera.getNumberOfCameras() == 1) {
                camera = Camera.open(0);
            }
            else {
                camera = Camera.open();
            }
        }
        catch (final Exception e) {
            camera = null;
        }
        return camera;
    }

    private void commitParameters() {
        mCamera.setParameters(mParameters);
    }
}
