package com.iaai.onyard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import com.iaai.onyard.R;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.MetricsHelper;
import com.iaai.onyard.view.DrawView;

/**
 * Activity containing custom draw view with photo background. The user can draw
 * on the photo and save it or skip the drawing step and leave the photo unaltered.
 */
public class VehicleDrawActivity extends Activity {
	
	/**
	 * Dialog indicating error with drawing. This activity is finished when OK is
	 * pressed.
	 */
	private static final int DRAW_ERROR_ID = 1;
	/**
	 * Dialog showing a spinning animation and telling the user that the photo is
	 * being saved. Dialog closes when save is complete.
	 */
	private static final int PHOTO_SAVE_PROGRESS_DIALOG_ID = 2;

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
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.vehicle_draw);

	        hookupButtons();
    	}
    	catch (Exception e)
    	{
    		showDrawErrorDialog();
    		LogHelper.logError(this, e);
    	}
    }

	/**
	 * Set up listeners for all applicable views in the layout.
	 */
	private void hookupButtons() 
	{
		Button btnSavePhoto = (Button) findViewById(R.id.btn_save_drawn_photo);
		btnSavePhoto.setOnClickListener(onSavePhotoButtonClick());

		Button btnNoDrawPhoto = (Button) findViewById(R.id.btn_no_draw_photo);
		btnNoDrawPhoto.setOnClickListener(onNoDrawButtonClick());

		Spinner spinDrawColor = (Spinner) findViewById(R.id.spin_draw_color);
		spinDrawColor.setOnItemSelectedListener(onDrawColorItemSelected());
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
    		ProgressDialog progress;
    		switch(id)
    		{
	    		case DRAW_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading the Drawing " +
		    				"Screen. Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		        	   finish();
		    		           }
		    		       });
		    		dialog = builder.create();
	    			break;
	    		case PHOTO_SAVE_PROGRESS_DIALOG_ID:
		    		progress = ProgressDialog.show(this, null, "Saving Photo...");
		    		progress.setIndeterminate(true);
		    		progress.setCancelable(false);
		    		dialog = progress;
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
    		finish();
    		return new Dialog(this);
    	}
    }

	/**
	 * Get click listener for skip drawing button. When it is clicked, finish this
	 * activity.
	 * 
	 * @return The onClickListener for the Skip button.
	 */
    private View.OnClickListener onNoDrawButtonClick()
    {
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{				
					finish();
		    	}
		    	catch (Exception e)
		    	{
		    		showDrawErrorDialog();
		    		LogHelper.logWarning(VehicleDrawActivity.this, e);
		    	}
			}
		};
    }
    
	/**
	 * Get click listener for save drawing button. When it is clicked, save the photo
	 * with annotations.
	 * 
	 * @return The onClickListener for the Save button.
	 */
    private View.OnClickListener onSavePhotoButtonClick()
    {
    	final DrawView drawVehiclePhoto = (DrawView) findViewById(R.id.vehicle_photo_draw);
    	
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
					new Thread(new Runnable() {
			        	public void run() {
			        		try
			        		{
				        		showPhotoSaveProgressDialog();
				        		drawVehiclePhoto.savePhoto(); 
				        		dismissPhotoSaveProgressDialog();
				        		finish();
				        		
				        		MetricsHelper.addPhotoAnnotated(VehicleDrawActivity.this,
				        				getIntent().getStringExtra(getString(
				        						R.string.stock_number_extra)));
			        		}
			        		catch (Exception e)
			        		{
			        			LogHelper.logError(VehicleDrawActivity.this, e);
			        			finish();
			        		}
			        	}
			        }).start();
		    	}
		    	catch (Exception e)
		    	{
		    		showDrawErrorDialog();
		    		LogHelper.logError(VehicleDrawActivity.this, e);
		    	}
			}
		};
    }
    
	/**
	 * Get item selected listener for draw color spinner. When an item is selected, change the
	 * drawing color to the color represented by that item.
	 * 
	 * @return The onItemSelectedListener for the Draw Color button.
	 */
    private OnItemSelectedListener onDrawColorItemSelected()
    {
    	final DrawView drawVehiclePhoto = (DrawView) findViewById(R.id.vehicle_photo_draw);

    	return new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) 
			{
				try
				{
					drawVehiclePhoto.setDrawColor(parent.getItemAtPosition(pos).toString());
		    	}
		    	catch (Exception e)
		    	{
		    		LogHelper.logWarning(VehicleDrawActivity.this, e);
		    	}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) { 
			}
    	};
    }
    
    /**
     * Show a generic draw error dialog. This activity will
     * finish when "OK" is pressed.
     */
    private void showDrawErrorDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(DRAW_ERROR_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDrawActivity.this, e);
					finish();
				}
			}
		});
    }
    
    /**
     * Show photo save progress dialog. The save cannot be canceled.
     */
    private void showPhotoSaveProgressDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(PHOTO_SAVE_PROGRESS_DIALOG_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDrawActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Hide photo save progress dialog.
     */
    private void dismissPhotoSaveProgressDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					dismissDialog(PHOTO_SAVE_PROGRESS_DIALOG_ID);
					removeDialog(PHOTO_SAVE_PROGRESS_DIALOG_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDrawActivity.this, e);
				}
			}
		});
    }
}
