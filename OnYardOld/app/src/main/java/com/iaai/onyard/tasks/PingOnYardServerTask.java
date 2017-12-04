package com.iaai.onyard.tasks;

import com.iaai.onyard.utility.HTTPHelper;

import android.content.Context;
import android.os.AsyncTask;

public class PingOnYardServerTask extends AsyncTask<Context, Void, Boolean> {

	@Override
	protected Boolean doInBackground(Context... params) 
	{
		return HTTPHelper.isOnYardServerAvailable(params[0]);
	}

}
