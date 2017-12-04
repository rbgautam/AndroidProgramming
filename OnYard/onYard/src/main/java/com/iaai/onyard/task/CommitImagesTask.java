package com.iaai.onyard.task;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import leadtools.LeadStreamFactory;
import leadtools.RasterImage;
import leadtools.RasterImageFormat;
import leadtools.codecs.RasterCodecs;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.http.ImagerHttpPost;
import com.iaai.onyard.utility.BroadcastHelper;
import com.iaai.onyard.utility.ImageDirHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.DataPendingSync;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to commit all images for a stock. Images and session data are inserted into the
 * database and then removed from the file system. Parameters for execute:
 * <P>
 * Param 0: stock info - VehicleInfo <br>
 * Param 1: start time - Long <br>
 * Param 2: end time - Long <br>
 * Param 3: image order to image path map - HashMap[Integer, String] <br>
 * Param 4: context - Context <br>
 * Param 5: branch number - Integer <br>
 * Param 6: image set - Integer <br>
 * Param 7: image order to jpeg quality map - HashMap[Integer, Integer] <br>
 * </P>
 * 
 * @author wferguso
 */
public class CommitImagesTask extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object... params) {
        try {
            final VehicleInfo vehInfo = (VehicleInfo) params[0];
            final long startTime = ((Long) params[1]).longValue();
            final long endTime = ((Long) params[2]).longValue();
            final HashMap<Integer, String> orderPathMap = (HashMap<Integer, String>) params[3];
            final Context context = (Context) params[4];
            final int branchNumber = (Integer) params[5];
            final int imageSet = (Integer) params[6];
            final HashMap<Integer, Integer> orderQualityMap = (HashMap<Integer, Integer>) params[7];

            DataPendingSync adminBranchData, userBranchData, stockNumData, imagePathData;
            DataPendingSync vinData, imageSetData, imageOrderData, startTimeData, endTimeData;
            DataPendingSync salvageProviderIdData;

            for (final Entry<Integer, String> entry : orderPathMap.entrySet())
            {
                final String sessionID = UUID.randomUUID().toString();
                final int imageOrder = entry.getKey();
                final String imagePath = entry.getValue();
                final int jpegQuality = orderQualityMap.get(imageOrder);
                if(imagePath != null)
                {
                    adminBranchData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.ADMIN_BRANCH_KEY, null, (long) vehInfo.getAdminBranch(),
                            null);
                    userBranchData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.USER_BRANCH_KEY, null, (long) branchNumber, null);
                    stockNumData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.STOCK_NUMBER_KEY, null,
                            (long) vehInfo.getStockNumberInt(), null);
                    vinData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.VIN_KEY, vehInfo.getVIN(), null, null);
                    imageSetData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.IMAGE_SET_KEY, null, (long) imageSet, null);
                    imageOrderData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.IMAGE_ORDER_KEY, null, (long) imageOrder, null);
                    startTimeData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.START_DATETIME_KEY, null, startTime, null);
                    endTimeData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.END_DATETIME_KEY, null, endTime, null);

                    final File imageDir = ImageDirHelper.getSavedImageStorageDir(context);
                    final String imageFileName = ImageDirHelper.getRandomFileName();
                    final String newImageFilePath = new File(imageDir, imageFileName).getPath();

                    compressImage(context, imagePath, jpegQuality, newImageFilePath);

                    imagePathData = new DataPendingSync(OnYardContract.IMAGER_APP_ID, sessionID,
                            ImagerHttpPost.FILE_CONTENTS_KEY, newImageFilePath, null, null);
                    salvageProviderIdData = new DataPendingSync(OnYardContract.IMAGER_APP_ID,
                            sessionID, ImagerHttpPost.SALVAGE_PROVIDER_ID_KEY, null,
                            (long) vehInfo.getSalvageProviderId(), null);

                    context.getContentResolver().bulkInsert(
                            OnYardContract.DataPendingSync.CONTENT_URI,
                            new ContentValues[] { adminBranchData.getContentValues(),
                                    userBranchData.getContentValues(),
                                    stockNumData.getContentValues(), vinData.getContentValues(),
                                    imageSetData.getContentValues(),
                                    imageOrderData.getContentValues(),
                                    startTimeData.getContentValues(),
                                    endTimeData.getContentValues(),
                                    imagePathData.getContentValues(),
                                    salvageProviderIdData.getContentValues() });

                    ImageDirHelper.deleteImage(imagePath);
                    BroadcastHelper.sendUpdatePendingSyncInfoBroadcast(context, false);
                }
            }
            BroadcastHelper.sendUpdatePendingSyncInfoBroadcast(context, true);

            return null;
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[4], e, this.getClass().getSimpleName());
            return null;
        }
    }

    private void compressImage(Context context, String imagePath, int jpegQuality,
            String newFilePath) {
        final RasterCodecs codecs = new RasterCodecs(String.format("%s/lib/",
                context.getApplicationInfo().dataDir));
        final RasterImage image = codecs.load(LeadStreamFactory.create(imagePath));

        codecs.getOptions().getJpeg().getSave().setQualityFactor(jpegQuality);
        codecs.save(image, LeadStreamFactory.create(newFilePath), RasterImageFormat.JPEG, 24);
    }
}
