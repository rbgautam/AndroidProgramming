package com.iaai.onyard.activity;

import java.util.ArrayList;
import java.util.List;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.OnYard;
import com.iaai.onyard.classes.SelectionClause;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.HTTPHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.PreferenceHelper;
import com.iaai.onyard.utility.SearchHelper;
import com.iaai.onyard.utility.SyncHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity for the Home screen that allows the user to search for a vehicle.
 */
public class MainActivity extends Activity {
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
	 * Dialog indicating error with activity initialization. User has the choice of
	 * restarting the app or continuing anyway.
	 */
	private static final int INITIALIZATION_ERROR_ID = 6;
	/**
	 * Dialog indicating error with barcode scanning. Barcode scanning activity is
	 * finished when OK pressed.
	 */
	private static final int SCAN_ERROR_ID = 7;
	/**
	 * Dialog indicating error with CSAToday WebView. CSA web activity is finished
	 * when OK pressed.
	 */
	private static final int WEB_LOAD_ERROR_ID = 8;
	
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
	        setContentView(R.layout.main);
	        
	        getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

	        PreferenceHelper.setDefaultSyncPrefs(this);
	        new Thread(new Runnable() {
	        	public void run() {
	        		try
	        		{
	        			initializeOnYard(); 
	        		}
	        		catch (Exception e)
	        		{
	        			showInitializationErrorDialog();
	            		LogHelper.logError(MainActivity.this, e);
	        		}
	        	}
	        }).start();
			
	        hookupButtons();
    	}
    	catch (Exception e)
    	{
    		showInitializationErrorDialog();
    		LogHelper.logError(this, e);
    	}
    }

	/**
	 * Set up listeners for all applicable views in the layout.
	 */
	private void hookupButtons() 
	{
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
		    	case SEARCH_PROGRESS_DIALOG_ID:
		    		progress = ProgressDialog.show(this, null, "Searching. Please wait...");
		    		progress.setIndeterminate(true);
		    		progress.setCancelable(true);
		    		dialog = progress;
		    		break;
		    	case NO_STOCKS_FOUND_DIALOG_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There were no vehicles found that matched your search " +
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
		    	case INITIALIZATION_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error initializing OnYard. Press " +
		    				"\"Restart\" to restart the application or \"Ignore\" to continue.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   Intent intent = getIntent();
		    		        	   finish();
		    		        	   startActivity(intent);
		    		           }
		    		       })
		    		       .setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		           }
		    		       });
		    		dialog = builder.create();
		    		break;
		    	case SCAN_ERROR_ID:
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
		    	case TOO_MANY_STOCKS_FOUND_DIALOG_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There were more than " + OnYard.MAX_LIST_STOCKS + " vehicles found " +
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
	 * if no internet connection is available.
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
			
			new Thread(new Runnable() {
	        	public void run() {
	        		try
	        		{
		        		updateVoiceSearchButton();
		        		updateCSAButton();
	        		}
	        		catch (Exception e)
	        		{
	        			showInitializationErrorDialog();
	        			LogHelper.logError(MainActivity.this, e);
	        		}
	        	}
	        }).start();
		}
		catch (Exception e)
		{
			LogHelper.logError(this, e);
		}
	}
	
	/**
	 * If a result is returned from either the voice search or barcode scan activities,
	 * automatically perform a vehicle search on said result.
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
	        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
	            // Fill the list view with the strings the recognizer thought it could have heard
	            ArrayList<String> matches = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	
	            setSearchFieldVal(matches.get(0));
	            performSearch();
	        }
	        if (requestCode == SCAN_BARCODE_REQUEST_CODE && resultCode == RESULT_OK)
	        {
	        	String contents = data.getStringExtra("SCAN_RESULT");
	        	
	        	setSearchFieldVal(contents);
				performSearch();
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
					if(HTTPHelper.isNetworkAvailable(MainActivity.this) && canRecognizeSpeechInput())		
						voiceButton.setEnabled(true);
					else
						voiceButton.setEnabled(false);
				}
				catch (Exception e)
				{
        			LogHelper.logError(MainActivity.this, e);
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
					if(HTTPHelper.isNetworkAvailable(MainActivity.this))
					{			
						csaButton.setEnabled(true);
					}
					else
						csaButton.setEnabled(false);
				}
				catch (Exception e)
				{
        			LogHelper.logError(MainActivity.this, e);
				}
			}
		});
	}

    /**
     * Perform necessary OnYard initialization. Ensure that sync account exists and nightly
     * sync is set, and perform on demand sync if associated preference is enabled.
     */
    private void initializeOnYard()
    {
    	SyncHelper.createOnYardAccount(this);
    	SyncHelper.enableSync(this);

    	if(PreferenceHelper.getSyncOnStart(this) == true)
    		SyncHelper.requestOnDemandSync(this);

    	if(PreferenceHelper.getAlarmIsSet(this) == false)
    	{
    		SyncHelper.setNightlySync(this);
    		PreferenceHelper.setAlarmIsSet(this, true);
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
					Intent intent = new Intent(MainActivity.this, CSAWebActivity.class);
					intent.putExtra(getString(R.string.web_activity_url_extra), 
							getString(R.string.mobile_csa_url));
					startActivity(intent);
				}
				catch (Exception e)
				{
					showWebErrorDialog();
					LogHelper.logError(MainActivity.this, e);
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
					LogHelper.logError(MainActivity.this, e);
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
					Intent intent = new Intent("com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "ONE_D_MODE");
					startActivityForResult(intent, SCAN_BARCODE_REQUEST_CODE);
				}
				catch (Exception e)
				{
					showScanErrorDialog();
					LogHelper.logError(MainActivity.this, e);
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
						performSearch();
						return true;
					}
	                    return false;
				}
				catch (Exception e)
				{
					dismissSearchProgressDialog();
					showSearchErrorDialog();
					LogHelper.logError(MainActivity.this, e);
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
					performSearch();
				}
				catch (Exception e)
				{
					dismissSearchProgressDialog();
					showSearchErrorDialog();
					LogHelper.logError(MainActivity.this, e);
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
    				showScanErrorDialog();
    				LogHelper.logError(MainActivity.this, e);
    			}
    		}
    	}).start();
    }
    
    /**
     * Determine a course of action based on the number of vehicles (x) found in the search.
     * If x=0: show a dialog stating that no vehicles were found. If x=1: construct an intent
     * and send the user to the VehicleDetailsActivity. If max_list_stocks>x>1: construct an
     * intent and send the user to a list containing all stocks found. If x>max_list_stocks: 
     * show a dialog stating that too many vehicles were found.
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

    		new Thread(new Runnable() {
    			public void run() {
    				try
    				{
    					DataHelper.deleteVehiclePhotos(MainActivity.this);
    				}
    				catch (Exception e)
    				{
    					LogHelper.logError(MainActivity.this, e);
    				}
    			}
    		}).start();

    		dismissSearchProgressDialog();
    		intent = new Intent(this, VehicleDetailsActivity.class);
    		intent.putExtra(getString(R.string.vehicle_match_stock_number), stockNum);
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
     * Show a generic initialization error dialog. The user will be able to choose whether
     * to restart the app or continue anyway.
     */
    private void showInitializationErrorDialog()
    {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try
				{
					showDialog(INITIALIZATION_ERROR_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(MainActivity.this, e);
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
					LogHelper.logWarning(MainActivity.this, e);
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
					showDialog(SCAN_ERROR_ID);
				}
				catch (Exception e)
				{
					LogHelper.logWarning(MainActivity.this, e);
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
					LogHelper.logWarning(MainActivity.this, e);
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
					LogHelper.logDebug(e.toString());
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
					LogHelper.logWarning(MainActivity.this, e);
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
					LogHelper.logWarning(MainActivity.this, e);
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
					LogHelper.logWarning(MainActivity.this, e);
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
					LogHelper.logWarning(MainActivity.this, e);
				}
			}
		});
    }
}
