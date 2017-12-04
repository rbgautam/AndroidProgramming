package com.iaai.onyard.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.iaai.onyard.task.DeleteImageTask;

public class ImageDirHelper {

    private static final String UNSAVED_IMAGE_DIR_NAME = "Unsaved";
    private static final String SAVED_IMAGE_DIR_NAME = "Saved";
    private static final String TIMESTAMP_SEPARATOR = "_";
    private static final String IMAGE_FILE_EXTENSION = ".jpg";

    public static byte[] getFileByteArray(String path, Context context) throws IOException {
        FileInputStream fin = null;
        try {
            final File file = new File(path);
            fin = new FileInputStream(path);
            final byte fileContent[] = new byte[(int) file.length()];
            fin.read(fileContent);

            return fileContent;
        }
        finally {
            if (fin != null) {
                fin.close();
            }
        }
    }

    public static String createRandomFile(Context context) throws IOException {
        final File imageDir = getSavedImageStorageDir(context);
        final String imageFileName = ImageDirHelper.getRandomFileName();
        return new File(imageDir, imageFileName).getPath();
    }

    public static File getUnsavedImageStorageDir(Context context) throws IOException {
        final File mediaStorageDir = context.getDir(UNSAVED_IMAGE_DIR_NAME, Context.MODE_PRIVATE);
        if (!mediaStorageDir.exists()) {

            throw new IOException("Could not create Unsaved image directory");
        }

        return mediaStorageDir;
    }

    public static File getSavedImageStorageDir(Context context) throws IOException {
        final File mediaStorageDir = context.getDir(SAVED_IMAGE_DIR_NAME, Context.MODE_PRIVATE);
        if (!mediaStorageDir.exists()) {

            throw new IOException("Could not create Saved image directory");
        }

        return mediaStorageDir;
    }

    public static boolean deleteImage(String imageFilePath) {
        return new File(imageFilePath).delete();
    }

    public static void deleteAllUnsavedImages(Context context) throws IOException {
        for (final File file : getUnsavedImageStorageDir(context).listFiles()) {
            new DeleteImageTask().execute(file.getPath(), context);
        }
    }

    public static String getRandomFileName() {
        final long utcTimeSeconds = DataHelper.getUnixUtcTimeStamp();
        final Random rand = new Random();
        final int randNum = rand.nextInt(99999);

        return String.valueOf(utcTimeSeconds) + TIMESTAMP_SEPARATOR + String.valueOf(randNum)
                + IMAGE_FILE_EXTENSION;
    }

    public static long getTimestampFromFile(String fileNameWithPath) {
        try {
            final String fileNameNoPath = fileNameWithPath.substring(fileNameWithPath.lastIndexOf(File.separator) + 1);
            final String timestampStr = fileNameNoPath.substring(0,
                    fileNameNoPath.indexOf(TIMESTAMP_SEPARATOR));
            return Long.parseLong(timestampStr);
        }
        catch (final Exception e) {
            return 0L;
        }
    }

    public static Bitmap getSampledBitmapFromImageDir(String filePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getSampledBitmapFromByteArray(byte[] imageData, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
