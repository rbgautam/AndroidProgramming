package com.iaai.onyard.utility;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.classes.Resolution;



public class ResolutionHelper {

    private static float PRECISION = 0.001F;

    public static Resolution getLargest4by3Res(int maxWidth, int maxHeight) {
        int width = 400;
        int height = 300;

        while (width <= maxWidth && height <= maxHeight) {
            width += 4;
            height += 3;
        }

        return new Resolution(width, height);
    }

    public static Camera.Size getMaxSupportedPictureSize() {

        int maxWidth = 0;
        int maxHeight = 0;
        Camera.Size maxSize = null;

        final Camera camera = Camera.open();
        final List<Size> sizes = camera.getParameters().getSupportedPictureSizes();
        camera.release();

        for (final Size size : sizes) {
            if (isDesiredRatio(size) && size.width >= maxWidth && size.height >= maxHeight) {
                maxWidth = size.width;
                maxHeight = size.height;
                maxSize = size;
            }
        }
        return maxSize;
    }

    public static boolean isDesiredRatio(Size res) {
        return Math.abs((float) res.width / res.height - OnYard.DESIRED_ASPECT_RATIO) < PRECISION;
    }
}
