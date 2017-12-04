package com.iaai.onyard.activity;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.VehicleArrayAdapter;
import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.classes.SelectionClause;
import com.iaai.onyard.classes.VehicleInfo;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.MetricsHelper;
import com.iaai.onyard.utility.SearchHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * Activity that contains a list of vehicles for the user to choose from.
 */
public class VehicleListActivity extends ListActivity {
	
	/**
	 * Dialog indicating search error. Dialog closes when OK pressed.
	 */
	private static final int SEARCH_ERROR_ID = 1;
	/**
	 * Dialog showing a spinning animation and telling the user that the search is
	 * being performed. Dialog closes when search is complete.
	 */
	private static final int SEARCHING_ID = 2;

	/**
	 * Inflate Activity UI and set View initial values and listeners.
	 * 
	 * Called when the activity is starting.
	 * 
	 * @param savedInstanceState If the activity is being re-initialized after 
	 * previously being shut down then this Bundle contains the data it most 
	 * recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.vehicle_list);
			
			final String searchVal = getIntent().getExtras().getString(
					getString(R.string.search_val));
			
			VehicleInfo[] vehicles = getVehicleArrayFromCursor(getVehiclesBySearchVal(searchVal));	
			
			VehicleArrayAdapter adapter = new VehicleArrayAdapter(this, vehicles);
			setListAdapter(adapter);
		}
		catch (Exception e)
		{
			LogHelper.logError(this, e);
    		showErrorDialogAndExit();
		}
	}
	
	/**
	 * Get the vehicle that was clicked, construct an intent with the vehicle's 
	 * stock number as an extra, and send the user to the VehicleDetailsActivity.
	 * 
	 * Called when an item in the list is selected. Subclasses 
	 * should override. Subclasses can call getListView().getItemAtPosition(position) 
	 * if they need to access the data associated with the selected item.
	 * 
	 * @param l The ListView where the click happened.
	 * @param v The view that was clicked within the ListView.
	 * @param position The position of the view in the list.
	 * @param id The row id of the item that was clicked.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		try
		{
			super.onListItemClick(l, v, position, id);
			
			new Thread(new Runnable() {
	        	public void run() {
	        		try
	        		{
		        		DataHelper.deleteVehiclePhotos(VehicleListActivity.this);
		        		if(getIntent().hasExtra(getString(R.string.previous_stock_num)))
		        		{
		        			MetricsHelper.updateStockEndTime(VehicleListActivity.this,
		        					getIntent().getStringExtra(getString(
		        	        				R.string.previous_stock_num)));
		        		}
	        		}
	        		catch (Exception e)
	        		{
	        			LogHelper.logError(VehicleListActivity.this, e);
	        		}
	        	}
	        }).start();
			
			VehicleInfo vehicle = (VehicleInfo) this.getListAdapter().getItem(position);
	
			Intent intent = new Intent(this, VehicleDetailsActivity.class);
			intent.putExtra(getString(R.string.vehicle_match_stock_number), vehicle.getStockNumber());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		catch (Exception e)
		{
			LogHelper.logError(this, e);
    		showErrorDialogAndExit();
		}
	}
	
	/**
	 * If using showDialog(id), the activity will call through to this method the first 
	 * time, and hang onto it thereafter. Any dialog that is created by this method will 
	 * automatically be saved and restored, including whether it is showing. 
	 * 
	 * @param id The id of the dialog.
	 * @param args The dialog arguments provided to showDialog(int, Bundle).
	 * @return The dialog. If null is returned, the dialog will not be created.
	 */
    @Override
    protected Dialog onCreateDialog(int id, Bundle args)
    {
    	try
    	{
    		AlertDialog dialog;
    		AlertDialog.Builder builder;
    		switch(id)
    		{
	    		case SEARCHING_ID:
	    			builder = new ProgressDialog.Builder(this);
	    			builder.setMessage("Searching. Please wait...")
	    			.setCancelable(true);
	    			dialog = builder.create();
	    			break;
	    		case SEARCH_ERROR_ID:
	    			builder = new AlertDialog.Builder(this);
	    			builder.setMessage("There was an error while searching. Please try again.")
	    			.setCancelable(false)
	    			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int id) {
	    					VehicleListActivity.this.finish();
	    				}
	    			});
	    			dialog = builder.create();
	    			break;
	    		default:
	    			dialog = null;
	    			break;
    		}
    		return dialog;
    	}
    	catch (Exception e)
    	{
    		LogHelper.logWarning(this, e);
    		return new Dialog(this);
    	}
    }

    /**
     * Query the Vehicles database for details where corresponding VIN and/or stock number
     * contain the search value.
     * 
     * @param searchVal The search value.
     * @return A cursor containing the query results.
     */
    private Cursor getVehiclesBySearchVal(String searchVal)
    { 	
    	String detailsProj[] = new String[]{
    			OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER,
    			OnYard.Vehicles.COLUMN_NAME_VIN,
    			OnYard.Vehicles.COLUMN_NAME_YEAR,
    			OnYard.Vehicles.COLUMN_NAME_MAKE,
    			OnYard.Vehicles.COLUMN_NAME_MODEL
    	};
    	
    	SelectionClause selection = SearchHelper.getVehicleSearchSelection(searchVal);
    	
    	Cursor queryResult = getContentResolver().query(
    			OnYard.Vehicles.CONTENT_URI, 
    			detailsProj, 
    			selection.getSelection(), 
    			selection.getSelectionArgs(), 
    			null);

    	startManagingCursor(queryResult);

    	return queryResult;
    }
    
    /**
     * Construct an array of VehicleInfo objects from a cursor containing vehicle details.
     * 
     * @param cursor The cursor containing vehicle details.
     * @return The array of VehicleInfo objects.
     */
    private VehicleInfo[] getVehicleArrayFromCursor(Cursor cursor)
    {
        VehicleInfo[] vehicles = new VehicleInfo[cursor.getCount()];
        int vehIndex = 0;

        cursor.moveToFirst();

        do
        {
            vehicles[vehIndex] = new VehicleInfo(cursor);

            vehIndex++;
        } while(cursor.moveToNext());

        return vehicles;
    }
    
    /**
     * Show a generic search error dialog. This activity will finish when "OK" is pressed.
     */
    private void showErrorDialogAndExit()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(SEARCH_ERROR_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleListActivity.this, e);
					finish();
				}
			}
		});
    }
}
