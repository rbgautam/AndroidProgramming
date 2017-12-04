package com.iaai.onyard.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.MetricsHelper;

public class EndStockSessionTask extends AsyncTask<Object, Void, Void> {

	@Override
	protected Void doInBackground(Object... params) 
	{
		Context context = (Context) params[0];
		String stockNumber = (String) params[1];
		try
		{
			MetricsHelper.updateStockEndTime(context, stockNumber);
			DataHelper.deleteVehiclePhotos(context);
			
			return null;
		}
		catch (Exception e)
		{
			LogHelper.logError(context, e);
			return null;
		}
	}
	
}
