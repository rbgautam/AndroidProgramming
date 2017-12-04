package com.iaai.onyard.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.classes.SelectionClause;
import com.iaai.onyard.classes.VehicleInfo;
import com.iaai.onyard.mail.EmailSender;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.tasks.EndStockSessionTask;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.HTTPHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.MetricsHelper;
import com.iaai.onyard.utility.SearchHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity that displays vehicle details and offers links to take 
 * vehicle photos, email vehicle photos/notes, view the vehicle's
 * location on a map, and search for another vehicle.
 */
public class VehicleDetailsActivity extends Activity {

	/**
	 * Dialog indicating no stocks were found by the search. Dialog closes when OK pressed.
	 */
	private static final int NO_STOCKS_FOUND_DIALOG_ID = 1;
	/**
	 * Dialog indicating search error. Dialog closes when OK pressed.
	 */
	private static final int SEARCH_ERROR_ID = 2;
	/**
	 * Dialog indicating that more search results were found than are allowed to be
	 * displayed in the ListView. Dialog closes when OK pressed.
	 */
	private static final int TOO_MANY_STOCKS_FOUND_DIALOG_ID = 3;
	/**
	 * Dialog showing a spinning animation and telling the user that the search is
	 * being performed. Dialog closes when search is complete.
	 */
	private static final int SEARCH_PROGRESS_DIALOG_ID = 4;
	/**
	 * Dialog indicating error with speech recognition. Speech recognition activity is
	 * finished when OK pressed.
	 */
	private static final int SPEECH_RECOGNITION_ERROR_ID = 5;
	/**
	 * Dialog prompting user to enter email address and notes for email that is sent
	 * to them about a specific stock. Email is sent when OK is pressed, assuming email
	 * address entered is valid.
	 */
	private static final int EMAIL_PROMPT_DIALOG_ID = 6;
	/**
	 * Dialog indicating error with activity initialization. Activity is finished when
	 * OK is pressed.
	 */
	private static final int DETAILS_LOAD_ERROR_ID = 7;
	/**
	 * Dialog indicating error with drawing. Drawing activity is finished when OK is
	 * pressed.
	 */
	private static final int DRAW_LOAD_ERROR_ID = 8;
	/**
	 * Dialog indicating error while sending email. Dialog closes and email sending is
	 * canceled when OK is pressed.
	 */
	private static final int EMAIL_LOAD_ERROR_ID = 9;
	/**
	 * Dialog indicating error with camera activity. Camera activity is finished when
	 * OK is pressed.
	 */
	private static final int CAMERA_LOAD_ERROR_ID = 10;
	/**
	 * Dialog indicating error with map activity. Map activity is finished when OK is
	 * pressed.
	 */
	private static final int MAP_LOAD_ERROR_ID = 11;
	/**
	 * Dialog indicating error with barcode scanning. Barcode scanning activity is
	 * finished when OK pressed.
	 */
	private static final int SCAN_LOAD_ERROR_ID = 12;
	/**
	 * Dialog indicating error with CSAToday WebView. CSA web activity is finished
	 * when OK pressed.
	 */
	private static final int WEB_LOAD_ERROR_ID = 13;
	/**
	 * Dialog displayed when pictures have been taken but not emailed and the user
	 * presses the Back button. Warns user that the photos will be lost if the user 
	 * continues. User has choice of canceling the operation or continuing anyway.
	 */
	private static final int BACK_BTN_DELETE_DIALOG_ID = 14;
	/**
	 * Dialog displayed when pictures have been taken but not emailed and the user
	 * presses the Clear button. Warns user that the photos will be lost if the user 
	 * continues. User has choice of canceling the operation or continuing anyway.
	 */
	private static final int CLEAR_BTN_DELETE_DIALOG_ID = 15;
	/**
	 * Dialog displayed when pictures have been taken but not emailed and the user
	 * performs a new Search. Warns user that the photos will be lost if the user 
	 * continues. User has choice of canceling the operation or continuing anyway.
	 */
	private static final int NEW_SEARCH_DELETE_DIALOG_ID = 16;
	
	/**
	 * Request code sent to speech recognition activity and returned when activity
	 * sends result back.
	 */
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	/**
	 * Request code sent to barcode scanner activity and returned when activity
	 * sends result back.
	 */
	private static final int SCAN_BARCODE_REQUEST_CODE = 2;
	/**
	 * Request code sent to camera activity and returned when activity
	 * sends result back.
	 */
	private static final int PHOTO_REQUEST_CODE = 3;
	
	/**
	 * Info about this activity's current vehicle.
	 */
	private static VehicleInfo mVehicle;
	
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int MEDIA_TYPE_VIDEO = 2;

	/**
	 * Inflate Activity UI and set View listeners and initial values based on
	 * the vehicle ID or stock number in the Intent extras bundle.
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
	        setContentView(R.layout.vehicle_details);
	        
	        getWindow().setSoftInputMode(
	        	    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
	        final String detailsProj[] = new String[]{
	        		OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER,
	        		OnYard.Vehicles.COLUMN_NAME_VIN,
	        		OnYard.Vehicles.COLUMN_NAME_YEAR,
	        		OnYard.Vehicles.COLUMN_NAME_MAKE,
	        		OnYard.Vehicles.COLUMN_NAME_MODEL,
	        		OnYard.Vehicles.COLUMN_NAME_COLOR,
	        		OnYard.Vehicles.COLUMN_NAME_DAMAGE,
	        		OnYard.Vehicles.COLUMN_NAME_SALVAGE_PROVIDER,
	        		OnYard.Vehicles.COLUMN_NAME_LATITUDE,
	        		OnYard.Vehicles.COLUMN_NAME_LONGITUDE,
	        		OnYard.Vehicles.COLUMN_NAME_AISLE,
	        		OnYard.Vehicles.COLUMN_NAME_STALL,
	        		OnYard.Vehicles.COLUMN_NAME_STATUS
	        };

        	final String vehicleStockNum = getIntent().getStringExtra(
					getString(R.string.vehicle_match_stock_number));
	        
			new Thread(new Runnable() {
				public void run() {
					try
					{
						mVehicle = getVehicleByStockNum(vehicleStockNum, detailsProj);
						assignDetailValues(mVehicle);
						MetricsHelper.createRecord(VehicleDetailsActivity.this, 
								mVehicle.getStockNumber());
					}
					catch (Exception e)
					{
						showDetailsLoadErrorDialog();
			    		LogHelper.logError(VehicleDetailsActivity.this, e);
					}
				}
	        }).start();
        
	        hookupButtons();
    	}
    	catch (Exception e)
    	{
    		showDetailsLoadErrorDialog();
    		LogHelper.logError(this, e);
    	}
    }

	/**
	 * Set up listeners for all applicable views in the layout.
	 */
	private void hookupButtons() 
	{
		Button viewMapButton = (Button) findViewById(R.id.btn_view_map);
		viewMapButton.setOnClickListener(onViewMapButtonClick());

		Button takePhotoButton = (Button) findViewById(R.id.btn_take_photo);
		takePhotoButton.setOnClickListener(onTakePhotoButtonClick());

		Button sendPhotoButton = (Button) findViewById(R.id.btn_send_photos);
		sendPhotoButton.setOnClickListener(onSendPhotosButtonClick());

		Button restartAppButton = (Button) findViewById(R.id.btn_restart_app);
		restartAppButton.setOnClickListener(onClearButtonClick());

		TextView txtSearch = (TextView) findViewById(R.id.txt_stock_search);
		txtSearch.setOnKeyListener(onEnterPerformSearch());

		Button searchButton = (Button) findViewById(R.id.btn_vehicle_search);
		searchButton.setOnClickListener(onVehicleSearchButtonClick());

		Button voiceButton = (Button) findViewById(R.id.btn_vehicle_voice_search);
		voiceButton.setOnClickListener(onVoiceSearchButtonClick());

		Button scanButton = (Button) findViewById(R.id.btn_vehicle_barcode_search);
		scanButton.setOnClickListener(onScanBarcodeButtonClick());

		Button mobileCSAButton = (Button) findViewById(R.id.btn_csa);
		mobileCSAButton.setOnClickListener(onMobileCSAButtonClick());
	}
	
	/**
	 * If back button pressed and user chooses to leave activity, update Metrics stock
	 * end time and delete all photos on the device.
	 * 
	 * Called when a key was pressed down and not handled by any of the views inside of 
	 * the activity. So, for example, key presses while the cursor is inside a TextView 
	 * will not trigger the event (unless it is a navigation to another object) because 
	 * TextView handles its own key presses. 
	 * 
	 * @param keyCode The value in event.getKeyCode().
	 * @param event Description of the key event.
	 * @return The result of the superclass onKeyDown method.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		try
		{
		    if (keyCode == KeyEvent.KEYCODE_BACK) 
		    {
		    	if(getNumPhotos() > 0 && 
		    			!MetricsHelper.getIsEmailSent(this, mVehicle.getStockNumber()))
		    		showBackButtonPhotoDeleteDialog();
		    	else
		    	{
			    	new EndStockSessionTask().execute(VehicleDetailsActivity.this, 
			    			mVehicle.getStockNumber());
		    	}
		    }
		    return super.onKeyDown(keyCode, event);
		}
		catch (Exception e)
		{
			LogHelper.logWarning(this, e);
			return false;
		}
	}
	
	/**
	 * If a result is returned from either the voice search or barcode scan activities,
	 * automatically perform a vehicle search on said result. If a result is returned
	 * from the camera activity, update the camera button accordingly.
	 * 
	 * Called when an activity you launched exits, giving you the requestCode 
	 * you started it with, the resultCode it returned, and any additional data 
	 * from it. The resultCode will be RESULT_CANCELED if the activity explicitly 
	 * returned that, didn't return any result, or crashed during its operation.
	 * 
	 * @param requestCode The integer request code originally supplied to 
	 * startActivityForResult(), allowing you to identify who this result came from. 
	 * @param resultCode The integer result code returned by the child activity 
	 * through its setResult().
	 * @param data An Intent, which can return result data to the caller 
	 * (various data can be attached to Intent "extras").
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	try
    	{
	        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) 
	        {
	            ArrayList<String> matches = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);

	            setSearchFieldVal(matches.get(0));
	            
	            if(getNumPhotos() > 0 && 
		    			!MetricsHelper.getIsEmailSent(this, mVehicle.getStockNumber()))
	            	showNewSearchPhotoDeleteDialog();
		    	else
		    	{
		    		performSearch();
		    	}
	        }
	        if (requestCode == SCAN_BARCODE_REQUEST_CODE && resultCode == RESULT_OK)
	        {
	        	String contents = data.getStringExtra("SCAN_RESULT");
	        	
	        	setSearchFieldVal(contents);
	        	
	            if(getNumPhotos() > 0 && 
		    			!MetricsHelper.getIsEmailSent(this, mVehicle.getStockNumber()))
	            	showNewSearchPhotoDeleteDialog();
		    	else
		    	{
		    		performSearch();
		    	}
	        }
	        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK)
	        {
	        	Intent intent = new Intent(VehicleDetailsActivity.this, 
	        			VehicleDrawActivity.class);
	        	intent.putExtra(getString(R.string.stock_number_extra), 
	        			mVehicle.getStockNumber());
				startActivity(intent);
	        }

	        super.onActivityResult(requestCode, resultCode, data);
    	}
    	catch (Exception e)
    	{
    		dismissSearchProgressDialog();
    		showSearchErrorDialog();
    		LogHelper.logError(this, e);
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
	    	final Dialog dialog;
	    	ProgressDialog progress;
	    	AlertDialog.Builder builder;
	    	switch(id)
	    	{
		    	case EMAIL_PROMPT_DIALOG_ID:
		    		dialog = new Dialog(this);
		    		dialog.setContentView(R.layout.custom_email_dialog);
		    		dialog.setTitle("Email Photos");
			    	dialog.setCancelable(false);
	
			    	final Button btnOK = (Button) dialog.findViewById(R.id.btn_email_ok);
			    	Button btnCancel = (Button) dialog.findViewById(R.id.btn_email_cancel);
			    	final EditText txtEmail = (EditText) dialog.findViewById(R.id.txt_email_address);
			    	final EditText txtNotes = (EditText) dialog.findViewById(R.id.txt_email_notes);
			    	final TextView lblInvalid = (TextView) dialog.findViewById(R.id.lbl_invalid_email);
			    	
			        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if(hasFocus)
								dialog.getWindow().setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
							
						}
					});
			    	btnOK.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							try
							{
								final String recipients = txtEmail.getText().toString();
								final String notes = txtNotes.getText().toString();
								if(isValidEmail(recipients))
								{
									lblInvalid.setVisibility(View.INVISIBLE);
									dialog.dismiss();
									new Thread(new Runnable() {
				    					public void run() {
				    						try
				    						{
				    							sendEmailWithPhotos(recipients, notes);	
				    						}
				    						catch (Exception e)
				    						{
				    							showEmailErrorDialog();
				    							LogHelper.logError(VehicleDetailsActivity.this, e);
				    						}
				    					}
				    				}).start();
									Toast emailSentToast = Toast.makeText(VehicleDetailsActivity.this,
											"Sending email...", Toast.LENGTH_SHORT);
									emailSentToast.show();
								}
								else
								{
									lblInvalid.setVisibility(View.VISIBLE);
								}
							}
							catch (Exception e)
							{
								showEmailErrorDialog();
								LogHelper.logError(VehicleDetailsActivity.this, e);
							}
						}
					});
			        txtEmail.setOnKeyListener(new View.OnKeyListener() {
						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
			                        (keyCode == KeyEvent.KEYCODE_ENTER)) 
							{
			                            btnOK.performClick();
			                    return true;
							}
			                    return false;
						}
					});
			    	btnCancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							lblInvalid.setVisibility(View.GONE);
							dialog.dismiss();
						}
					});
		    		break;
		    	case SEARCH_PROGRESS_DIALOG_ID:
		    		progress = ProgressDialog.show(this, null, "Searching. Please wait...");
		    		progress.setIndeterminate(true);
		    		progress.setCancelable(true);
		    		dialog = progress;
		    		break;
		    	case BACK_BTN_DELETE_DIALOG_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("If you leave this screen, all photos you have taken of " +
		    				"the current vehicle will be deleted. Continue anyway?")
		    		       .setCancelable(false)
		    		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   new EndStockSessionTask().execute(VehicleDetailsActivity.this, 
		    		        			   mVehicle.getStockNumber());
		    		        	   finish();
		    		           }
		    		       })
		    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case CLEAR_BTN_DELETE_DIALOG_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("If you leave this screen, all photos you have taken of " +
		    				"the current vehicle will be deleted. Continue anyway?")
		    		       .setCancelable(false)
		    		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   new EndStockSessionTask().execute(VehicleDetailsActivity.this, 
		    		        			   mVehicle.getStockNumber());

		    		        	   Intent intent = new Intent(VehicleDetailsActivity.this, MainActivity.class);
		    		        	   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    		        	   startActivity(intent);
		    		           }
		    		       })
		    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case NEW_SEARCH_DELETE_DIALOG_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("If you select a new vehicle, all photos you have taken of " +
		    				"the current vehicle will be deleted. Continue anyway?")
		    		       .setCancelable(false)
		    		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		        				   performSearch();
		    		           }
		    		       })
		    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case NO_STOCKS_FOUND_DIALOG_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There were no stocks found that matched your search " +
		    				"criteria. Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case SEARCH_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while searching. Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case TOO_MANY_STOCKS_FOUND_DIALOG_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There were more than " + OnYard.MAX_LIST_STOCKS + " stocks found " +
		    				"that matched your search criteria. Please narrow down your search.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case SPEECH_RECOGNITION_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while performing speech recognition. " +
		    				"Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case DETAILS_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading vehicle details. Please try" +
		    				"again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		        	   finish();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case DRAW_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading the Drawing " +
		    				"Screen. Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
	    			break;
		    	case EMAIL_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while sending your email." +
		    				" Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
	    			break;
		    	case CAMERA_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading the camera." +
		    				" Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
	    			break;
		    	case MAP_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading the map." +
		    				" Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
	    			break;
		    	case SCAN_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading the barcode scanner. " +
		    				"Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case WEB_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading Mobile CSAToday. " +
		    				"Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
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
	 * Perform default resume actions and update buttons that may be disabled
	 * if no internet connection is available or need to display a number image
	 * next to them.
	 * 
	 * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for the
	 * activity to start interacting with the user.
	 */
	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			
			updateNumPhotosButton();
			updateSendPhotosButton();
			updateVoiceSearchButton();
			updateMapButton();
			updateCSAButton();
		}
		catch (Exception e)
		{
			showDetailsLoadErrorDialog();
			LogHelper.logError(this, e);
		}
	}
	
	/**
	 * Get click listener for Mobile CSAToday button. When it is clicked, construct intent
	 * and send user to CSAWebActivity.
	 * 
	 * @return The onClickListener for the Mobile CSA button.
	 */
	private View.OnClickListener onMobileCSAButtonClick()
	{
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{					
					new Thread(new Runnable() {
						public void run() {
							try
							{
								MetricsHelper.updateCSAStartTime(VehicleDetailsActivity.this, 
										mVehicle.getStockNumber());
							}
							catch (Exception e)
							{
								LogHelper.logWarning(VehicleDetailsActivity.this, e);
							}
						}
			        }).start();
					
					Intent intent = new Intent(VehicleDetailsActivity.this, CSAWebActivity.class);
					intent.putExtra(getString(R.string.web_activity_url_extra), 
							getString(R.string.mobile_csa_url));
					intent.putExtra(getString(R.string.stock_number_extra), 
							mVehicle.getStockNumber());
					startActivity(intent);
				}
				catch (Exception e)
				{
					showWebErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		};
	}
	
	/**
	 * Get click listener for email button. When it is clicked, show dialog prompting
	 * user for email address and notes.
	 * 
	 * @return The onClickListener for the email button.
	 */
	private View.OnClickListener onSendPhotosButtonClick()
	{
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
					showEmailPromptDialog();
				}
				catch (Exception e)
				{
					showEmailErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		};
	}
	
	/**
	 * Get click listener for clear button. When it is clicked and user agrees to
	 * proceed (if necessary), update stock end time, delete photos, and
	 * send user to home activity. All activities on top of the home activity will
	 * be destroyed.
	 * 
	 * @return The onClickListener for the clear button.
	 */
	private View.OnClickListener onClearButtonClick()
	{
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
					if(getNumPhotos() > 0 && 
			    			!MetricsHelper.getIsEmailSent(VehicleDetailsActivity.this, 
			    					mVehicle.getStockNumber()))
			    		showClearButtonPhotoDeleteDialog();
			    	else
			    	{
			    		new EndStockSessionTask().execute(VehicleDetailsActivity.this, 
			    				mVehicle.getStockNumber());
						
						Intent intent = new Intent(VehicleDetailsActivity.this, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
			    	}
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		};
	}
	
	/**
	 * Get click listener for camera button. When it is clicked, construct intent
	 * and send user to camera activity.
	 * 
	 * @return The onClickListener for the camera button.
	 */
	private View.OnClickListener onTakePhotoButtonClick()
	{
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				try
				{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(MEDIA_TYPE_IMAGE));

					startActivityForResult(intent, PHOTO_REQUEST_CODE);
				}
				catch (Exception e)
				{
					showCameraErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		};
	}
	
	/**
	 * Create a File for saving an image or video and return the Uri.
	 * 
	 * @param type The type of media for which to create a file - image or video.
	 * @return A Uri in the OnYard image directory with a name corresponding to a newly
	 * created file.
	 */
	private Uri getOutputMediaFileUri(int type)
	{
	    // check that the SDCard is mounted before doing this?
	    // Environment.getExternalStorageState()

	    File mediaStorageDir = DataHelper.getImageStorageDir(this);
	    
	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE)
	    {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } 
	    else if(type == MEDIA_TYPE_VIDEO) 
	    {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } 
	    else 
	    {
	        return null;
	    }

	    return Uri.fromFile(mediaFile);
	}
	
	/**
	 * Get click listener for map button. When it is clicked, construct intent
	 * and send user to map activity.
	 * 
	 * @return The onClickListener for the map button.
	 */
	private View.OnClickListener onViewMapButtonClick()
	{
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
					Intent intent = new Intent(VehicleDetailsActivity.this, VehicleMapActivity.class);
					intent.putExtra(getString(R.string.latitude_extra), mVehicle.getLatitude());
					intent.putExtra(getString(R.string.longitude_extra), mVehicle.getLongitude());
					intent.putExtra(getString(R.string.stock_number_extra), mVehicle.getStockNumber());
					intent.putExtra(getString(R.string.vin_extra), mVehicle.getVIN());
					intent.putExtra(getString(R.string.year_make_model_extra), mVehicle.getYearMakeModel());
					startActivity(intent);
					
					new Thread(new Runnable() {
						public void run() {
							try
							{
								MetricsHelper.updateMapStartTime(VehicleDetailsActivity.this, 
										mVehicle.getStockNumber());
							}
							catch (Exception e)
							{
								LogHelper.logWarning(VehicleDetailsActivity.this, e);
							}
						}
			        	
			        }).start();
				}
				catch (Exception e)
				{
					showMapLoadErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		};
	}
	
	/**
	 * Get click listener for voice search button. When it is clicked, construct intent
	 * and begin voice recognition activity, expecting a text value in return.
	 * 
	 * @return The onClickListener for the voice search button.
	 */
    private View.OnClickListener onVoiceSearchButtonClick()
    {
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
					if(canRecognizeSpeechInput())
					{
						Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	
						// Specify the calling package to identify your application
						intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
						intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say part of a Stock Number or VIN");
						intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
								RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		
						// Specify how many results you want to receive. The results will be sorted
						// where the first result is the one with higher confidence.
						intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		
						startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
					}
				}
				catch (Exception e)
				{
					showSpeechRecognitionErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		};
    }
    
	/**
	 * Get click listener for barcode scanner button. When it is clicked, construct intent
	 * and begin barcode scan activity, expecting a text value in return.
	 * 
	 * @return The onClickListener for the barcode scanner button.
	 */
    private View.OnClickListener onScanBarcodeButtonClick()
    {
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
					Intent intent = new Intent(getString(R.string.zxing_scan_action));
					intent.putExtra("SCAN_MODE", "ONE_D_MODE");
					startActivityForResult(intent, SCAN_BARCODE_REQUEST_CODE);
				}
				catch (Exception e)
				{
					showScanErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		};
    }
    
	/**
	 * Get key listener for search textview. When enter key is pressed, perform search.
	 * 
	 * @return The onKeyListener for the barcode scanner button.
	 */
    private View.OnKeyListener onEnterPerformSearch()
    {
    	return new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				try
				{
					if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
							(keyCode == KeyEvent.KEYCODE_ENTER)) 
					{
			            if(getNumPhotos() > 0 && 
				    			!MetricsHelper.getIsEmailSent(VehicleDetailsActivity.this, 
				    					mVehicle.getStockNumber()))
			            	showNewSearchPhotoDeleteDialog();
				    	else
				    	{
				    		performSearch();
				    	}
						return true;
					}
					return false;
				}
				catch (Exception e)
				{
					dismissSearchProgressDialog();
					showSearchErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
					return false;
				}
			}
		};
    }
    
	/**
	 * Get click listener for search button. When it is clicked, perform search.
	 * 
	 * @return The onClickListener for the search button.
	 */
    private View.OnClickListener onVehicleSearchButtonClick()
    {
    	return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
		            if(getNumPhotos() > 0 && 
			    			!MetricsHelper.getIsEmailSent(VehicleDetailsActivity.this, 
			    					mVehicle.getStockNumber()))
		            	showNewSearchPhotoDeleteDialog();
			    	else
			    	{
			    		performSearch();
			    	}
				}
				catch (Exception e)
				{
					dismissSearchProgressDialog();
					showSearchErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		};
    }
    
	/**
	 * Set search field text to a specified value.
	 * 
	 * @param val The value to which to set the search field text.
	 */
    private void setSearchFieldVal(String val)
    {
    	EditText searchBox = (EditText) findViewById(R.id.txt_stock_search);
    	searchBox.setText(val);
    }
    
    /**
     * Determine whether the system offers speech recognition.
     * 
     * @return True if speech recognition available, false otherwise.
     */
    private boolean canRecognizeSpeechInput()
    {
    	List<ResolveInfo> activities = getPackageManager().queryIntentActivities(
    			new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

    	if (activities.size() > 0) 
    		return true;
    	else 
    		return false;
    }
    
    /**
     * Perform a vehicle search on the text value contained in the search field. Display the
     * search progress dialog while this happens.
     */
    private void performSearch()
    {
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showSearchProgressDialog();
					
					EditText searchBox = (EditText) findViewById(R.id.txt_stock_search);
					final String searchVal = searchBox.getText().toString();
					
					if(searchVal.trim().equals(""))
    				{
    					dismissSearchProgressDialog();
    					showNoStocksFoundDialog();
    				}
    				else
    				{
	    				sendSearchResultIntent(searchVal);
	
	    				dismissSearchProgressDialog();
    				}
				}
				catch (Exception e)
				{
					dismissSearchProgressDialog();
					showSearchErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		}).start();
    }
    
    /**
     * Determine a course of action based on the number of vehicles (x) found in the search.
     * If x=0: show a dialog stating that no vehicles were found. If x=1: construct an intent
     * and send the user to a new VehicleDetailsActivity, destroying this one in the process. 
     * If max_list_stocks>x>1: construct an intent and send the user to a list containing 
     * all stocks found. If x>max_list_stocks: show a dialog stating that too many vehicles were found.
     * 
     * @param searchVal The value on which to perform the vehicle search.
     */
    private void sendSearchResultIntent(String searchVal)
    {    	
    	Timer t = new Timer("MainActivity.performSearch"); t.start();
    	
    	Intent intent;
    	Cursor queryResult = getVehicleSearchStocks(searchVal);
    	startManagingCursor(queryResult);
    	int resultCount = queryResult.getCount();

    	switch (resultCount)
    	{
    	case 0:
    		showNoStocksFoundDialog();
    		break;

    	case 1:
    		queryResult.moveToFirst();
    		String stockNum = queryResult.getString(queryResult.getColumnIndex(OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER));

    		new EndStockSessionTask().execute(VehicleDetailsActivity.this, mVehicle.getStockNumber());

    		dismissSearchProgressDialog();
    		intent = new Intent(this, VehicleDetailsActivity.class);
    		intent.putExtra(getString(R.string.vehicle_match_stock_number), stockNum);
    		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(intent);
    		break;

    	default:
    		if(resultCount > OnYard.MAX_LIST_STOCKS)
    		{
    			showTooManyStocksFoundDialog();
    		}
    		else
    		{
    			dismissSearchProgressDialog();
    			intent = new Intent(this, VehicleListActivity.class);
    			intent.putExtra(getString(R.string.previous_stock_num), 
    					mVehicle.getStockNumber());
    			intent.putExtra(getString(R.string.search_val), searchVal);
    			startActivity(intent);
    		}
    		break;
    	}
    	
    	t.end(); t.logDebug();
    }
    
    /**
     * Query the Vehicles database for stock numbers that match the search value.
     * 
     * @param searchVal The search value.
     * @return A cursor containing the query results.
     */
    private Cursor getVehicleSearchStocks(String searchVal)
    {
    	SelectionClause selection = SearchHelper.getVehicleSearchSelection(searchVal);
    	
    	return getContentResolver().query(
    			OnYard.Vehicles.CONTENT_URI, 
    			new String[]{ OnYard.Vehicles.COLUMN_NAME_STOCK_NUMBER }, 
    			selection.getSelection(), 
    			selection.getSelectionArgs(), 
    			null);
    }
    

	
	/**
	 * Validate all emails (comma delimited) in the address string.
	 * 
	 * @param address The address(es) to validate.
	 * @return True if all emails in the address string are valid, false otherwise.
	 */
	private boolean isValidEmail(String address)
	{
		address = address.replace(" ", "");

		if(address.indexOf(',') == -1)
		{
			return Patterns.EMAIL_ADDRESS.matcher(address).matches();
		}
		else
		{
			return (isValidEmail(address.substring(0, address.indexOf(','))) &&
					isValidEmail(address.substring(address.indexOf(',') + 1, 
							address.length())));
		}
	}
	
	/**
	 * Send email containing vehicle details, notes, and photos to the specified
	 * email address(es).
	 * 
	 * @param emailRecipients The recipient(s) of the email.
	 * @param notes Optional notes to include in the email.
	 * @throws MessagingException 
	 */
	private void sendEmailWithPhotos(String emailRecipients, String notes) 
			throws MessagingException
	{
		EmailSender sender = new EmailSender(this);
		
		for (File file : DataHelper.getImageStorageDir(this).listFiles())
		{
			sender.addAttachment(file.getPath());
		}

		String emailBody = "Vehicle Details:\n\n" +
		"Stock Number: " + mVehicle.getStockNumber() + "\n" +
		"VIN: " + mVehicle.getVIN() + "\n" +
		"Year/Make/Model: " + mVehicle.getYearMakeModel() + "\n" +
		"Color: " + mVehicle.getColor() + "\n" +
		"Damage: " + mVehicle.getDamage() + "\n" +
		"Status: " + mVehicle.getStatus();

		if(!notes.equals(""))
			emailBody += "\n\nNotes:\n" + notes;

		sender.sendMail("Photos/Notes for stock #" + mVehicle.getStockNumber(),
				emailBody,
				getString(R.string.email_from_address),
				emailRecipients
		);

		MetricsHelper.updateIsEmailSent(VehicleDetailsActivity.this,
				mVehicle.getStockNumber(), true);
	}
	
	/**
	 * Get the number of photos taken from OnYard.
	 * 
	 * @return The number of photos.
	 */
	private int getNumPhotos()
	{
		return DataHelper.getImageStorageDir(this).listFiles().length;
	}
	
	/**
	 * Update the camera button to reflect the number of photos taken. This is accomplished
	 * by displaying a small number image over the button.
	 */
	private void updateNumPhotosButton()
	{
		final int numPhotos = getNumPhotos();
		final ImageView numPhotosImage = (ImageView) findViewById(R.id.img_num_photos);
		final Button takePhotoButton = (Button) findViewById(R.id.btn_take_photo);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					takePhotoButton.setEnabled(true);
					switch(numPhotos)
					{
					case 0:
						numPhotosImage.setImageResource(R.drawable.blank);
						break;
					case 1:
						numPhotosImage.setImageResource(
								R.drawable.number_1_circle);
						break;
					case 2:
						numPhotosImage.setImageResource(
								R.drawable.number_2_circle);
						break;
					case 3:
						numPhotosImage.setImageResource(
								R.drawable.number_3_circle);
						break;
					case 4:
						numPhotosImage.setImageResource(
								R.drawable.number_4_circle);
						break;
					case 5:
						numPhotosImage.setImageResource(
								R.drawable.number_5_circle);
						break;
					case 6:
						numPhotosImage.setImageResource(
								R.drawable.number_6_circle);
						break;
					case 7:
						numPhotosImage.setImageResource(
								R.drawable.number_7_circle);
						break;
					case 8:
						numPhotosImage.setImageResource(
								R.drawable.number_8_circle);
						break;
					case 9:
						numPhotosImage.setImageResource(
								R.drawable.number_9_circle);
						break;
					case 10:
						numPhotosImage.setImageResource(
								R.drawable.number_10_circle);
						takePhotoButton.setEnabled(false);
						break;
					default:
						break;
					}
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});

		if(numPhotos > 0)
			MetricsHelper.updateNumPhotos(VehicleDetailsActivity.this, 
					mVehicle.getStockNumber(), numPhotos);
	}
	
	/**
	 * Check internet connection and set email button to enabled/disabled accordingly.
	 */
	private void updateSendPhotosButton()
	{
		final Button sendPhotoButton = (Button) findViewById(R.id.btn_send_photos);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					if(HTTPHelper.isNetworkAvailable(VehicleDetailsActivity.this))
						sendPhotoButton.setEnabled(true);
					else
						sendPhotoButton.setEnabled(false);
				}
				catch (Exception e)
				{
					showDetailsLoadErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		});
	}
	
	/**
	 * Check internet connection and speech recognition capability. Set voice search
	 * button to enabled/disabled accordingly.
	 */
	private void updateVoiceSearchButton()
	{
		final Button voiceButton = (Button) findViewById(R.id.btn_vehicle_voice_search);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					if(HTTPHelper.isNetworkAvailable(VehicleDetailsActivity.this) && 
							canRecognizeSpeechInput())
					{			
						voiceButton.setEnabled(true);
					}
					else
						voiceButton.setEnabled(false);
				}
				catch (Exception e)
				{
					showDetailsLoadErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		});
	}
	
	/**
	 * Check internet connection and set map button to enabled/disabled accordingly.
	 */
	private void updateMapButton()
	{
		final Button mapButton = (Button) findViewById(R.id.btn_view_map);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					if(HTTPHelper.isNetworkAvailable(VehicleDetailsActivity.this))
					{			
						mapButton.setEnabled(true);
					}
					else
						mapButton.setEnabled(false);
				}
				catch(Exception e)
				{
					showDetailsLoadErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		});
	}
	
	/**
	 * Check internet connection and set Mobile CSA button to enabled/disabled accordingly.
	 */
	private void updateCSAButton()
	{
		final Button csaButton = (Button) findViewById(R.id.btn_csa);

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					if(HTTPHelper.isNetworkAvailable(VehicleDetailsActivity.this))
					{			
						csaButton.setEnabled(true);
					}
					else
						csaButton.setEnabled(false);
				}
				catch (Exception e)
				{
					showDetailsLoadErrorDialog();
					LogHelper.logError(VehicleDetailsActivity.this, e);
				}
			}
		});
	}
    
    /**
     * Query the Vehicles database for the specified vehicle details with the specified stock
     * number. If multiple matching vehicles are found, only the first is returned.
     * 
     * @param stockNum The stock number of the desired vehicle.
     * @param projection The vehicle details to return.
     * @return A VehicleInfo object containing the desired vehicle's details.
     */
    private VehicleInfo getVehicleByStockNum(String stockNum, String[] projection)
    {
    	Cursor queryResult = getContentResolver().query(
    			Uri.withAppendedPath(
    					OnYard.Vehicles.CONTENT_STOCK_NUMBER_URI_BASE, stockNum),
    					projection, 
    					null, 
    					null, 
    					null
    	);
    	startManagingCursor(queryResult);

    	queryResult.moveToFirst();

    	return new VehicleInfo(queryResult);
    }
    
    /**
     * Set details TextViews to contain the corresponding vehicle details.
     * 
     * @param vehicle A VehicleInfo object containing the details.
     */
    private void assignDetailValues(final VehicleInfo vehicle)
    {
    	final TextView txtYMM = (TextView) findViewById(R.id.lbl_year_make_model);
    	final TextView txtStockNum = (TextView) findViewById(R.id.lbl_stock_number);
    	final TextView txtVIN = (TextView) findViewById(R.id.lbl_vin);
    	final TextView txtColor = (TextView) findViewById(R.id.lbl_color);
    	final TextView txtDamage = (TextView) findViewById(R.id.lbl_damage);
    	final TextView txtSalvageProvider = (TextView) findViewById(R.id.lbl_salvage_provider);
    	final TextView txtLocation = (TextView) findViewById(R.id.lbl_aisle_stall);
    	final TextView txtStatus = (TextView) findViewById(R.id.lbl_status);

    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {		
    			try
    			{
    				txtYMM.setText(vehicle.getYearMakeModel());
    				txtStockNum.setText(txtStockNum.getText() + vehicle.getStockNumber());
    				txtVIN.setText(txtVIN.getText() + vehicle.getVIN());
    				txtColor.setText(txtColor.getText() + vehicle.getColor());
    				txtDamage.setText(txtDamage.getText() + vehicle.getDamage());
    				txtSalvageProvider.setText(txtSalvageProvider.getText() + 
    						vehicle.getSalvageProvider());
    				txtLocation.setText(txtLocation.getText() + vehicle.getAisle() + " - " +
    						vehicle.getStall());
    				txtStatus.setText(txtStatus.getText() + vehicle.getStatus());
    			}
    			catch (Exception e) 
    			{
    				showDetailsLoadErrorDialog();
    				LogHelper.logError(VehicleDetailsActivity.this, e);
    			}
    		}
    	});
    }
    
    /**
     * Show a dialog prompting the user to enter their email address(es) and vehicle notes. 
     * The user can press "OK" to send the vehicle photos and notes to the specified address(es),
     * or "Cancel" to dismiss the dialog.
     */
    private void showEmailPromptDialog()
    {
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			try
    			{
    				showDialog(EMAIL_PROMPT_DIALOG_ID);
    			}
    			catch (Exception e) 
    			{
    				LogHelper.logError(VehicleDetailsActivity.this, e);
    			}
    		}
    	});
    }
    
    /**
     * Show a generic email error dialog. The dialog will close when "OK" is pressed.
     */
    private void showEmailErrorDialog()
    {
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			try
    			{
    				showDialog(EMAIL_LOAD_ERROR_ID);
    			}
    			catch (Exception e) 
    			{
    				LogHelper.logWarning(VehicleDetailsActivity.this, e);
    			}
    		}
    	});
    }
    
    /**
     * Show a generic email error dialog. The camera activity will finish when "OK" is pressed.
     */
    private void showCameraErrorDialog()
    {
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			try
    			{
    				showDialog(CAMERA_LOAD_ERROR_ID);
    			}
    			catch (Exception e) 
    			{
    				LogHelper.logWarning(VehicleDetailsActivity.this, e);
    			}
    		}
    	});
    }
    
    /**
     * Show a generic speech recognition error dialog. The speech recognition 
     * activity will finish when "OK" is pressed.
     */
    private void showSpeechRecognitionErrorDialog()
    {
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			try
    			{
    				showDialog(SPEECH_RECOGNITION_ERROR_ID);
    			}
    			catch (Exception e) 
    			{
    				LogHelper.logWarning(VehicleDetailsActivity.this, e);
    			}
    		}
    	});
    }
    
    /**
     * Show search progress dialog. The search can be canceled by hitting the
     * back button.
     */
    private void showSearchProgressDialog()
    {
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			try
    			{
    				showDialog(SEARCH_PROGRESS_DIALOG_ID);
    			}
    			catch (Exception e) 
    			{
    				LogHelper.logWarning(VehicleDetailsActivity.this, e);
    			}
    		}
    	});
    }
    
    /**
     * Hide search progress dialog.
     */
    private void dismissSearchProgressDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					dismissDialog(SEARCH_PROGRESS_DIALOG_ID);
					removeDialog(SEARCH_PROGRESS_DIALOG_ID);
				}
				catch (Exception e)
				{
					//exception thrown when method called multiple times,
					// but we don't care as long as the dialog is hidden
				}
			}
		});
    }
    
    /**
     * Show a generic search error dialog. The search will be canceled when "OK"
     * is pressed.
     */
    private void showSearchErrorDialog()
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
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Show a dialog stating that the search returned no results. The user will be
     * able to search again when "OK" is pressed.
     */
    private void showNoStocksFoundDialog()
    {
    	runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    			try
    			{
    				showDialog(NO_STOCKS_FOUND_DIALOG_ID);
    			}
    			catch (Exception e)
    			{
    				LogHelper.logWarning(VehicleDetailsActivity.this, e);
    			}
    		}
    	});
    }
    
    /**
     * Show a dialog stating that the search returned more results than can be
     * displayed in the vehicle list. The user will be able to search again when
     * "OK" is pressed.
     */
    private void showTooManyStocksFoundDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(TOO_MANY_STOCKS_FOUND_DIALOG_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Show a generic details error dialog. The details activity will
     * finish when "OK" is pressed.
     */
    private void showDetailsLoadErrorDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(DETAILS_LOAD_ERROR_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Show a generic map error dialog. The map activity will
     * finish when "OK" is pressed.
     */
    private void showMapLoadErrorDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(MAP_LOAD_ERROR_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Show a generic barcode scan error dialog. The barcode scan activity will
     * finish when "OK" is pressed.
     */
    private void showScanErrorDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(SCAN_LOAD_ERROR_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Show a generic web load error dialog. The Mobile CSAToday activity will 
     * finish when "OK" is pressed.
     */
    private void showWebErrorDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(WEB_LOAD_ERROR_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Show a dialog informing the user that their photos will be deleted if they
     * continue. The user will be given the option of canceling the operation or
     * continuing anyway.
     */
    private void showBackButtonPhotoDeleteDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(BACK_BTN_DELETE_DIALOG_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Show a dialog informing the user that their photos will be deleted if they
     * continue. The user will be given the option of canceling the operation or
     * continuing anyway.
     */
    private void showClearButtonPhotoDeleteDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(CLEAR_BTN_DELETE_DIALOG_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
    
    /**
     * Show a dialog informing the user that their photos will be deleted if they
     * continue. The user will be given the option of canceling the operation or
     * continuing anyway.
     */
    private void showNewSearchPhotoDeleteDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(NEW_SEARCH_DELETE_DIALOG_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(VehicleDetailsActivity.this, e);
				}
			}
		});
    }
}
