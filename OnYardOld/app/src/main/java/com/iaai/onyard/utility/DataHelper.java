package com.iaai.onyard.utility;

import java.io.File;
import android.content.Context;
import android.os.Environment;
import com.iaai.onyard.R;

/**
 * Helper class containing methods that deal with data management.
 */
public class DataHelper {

	/**
	 * Delete all vehicle photos taken from OnYard, so that 
	 * subsequent users cannot access them.
	 * 
	 * @param context The current context.
	 */
	public static void deleteVehiclePhotos(Context context)
	{
		for (File file : getImageStorageDir(context).listFiles())
		{
			file.delete();
		}
	}
	
	/**
	 * Get the File in the given directory that was most recently modified.
	 * 
	 * @param filePath The path of the directory to search.
	 * @return The File object that was most recently modified at the given path.
	 */
	public static File getNewestFileInDirectory(String filePath)
	{
		File newestFile = null;
		
		File dir = new File(filePath);
		for (File file : dir.listFiles())
		{
			if (newestFile == null || file.lastModified() > newestFile.lastModified())
			{
				newestFile = file;
			}
		}
		
		return newestFile;
	}
	
	/**
	 * Get the directory in which to store OnYard images.
	 * 
	 * @param context The current context.
	 * @return A File object pointing to the OnYard image directory.
	 */
	public static File getImageStorageDir(Context context)
	{
		File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
			context.getString(R.string.onyard_image_dir));
		
		if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            LogHelper.logDebug("Failed to create OnYard image directory");
	            return null;
	        }
	    }
		
		return mediaStorageDir;
	}
}
