package com.iaai.onyard.activity;

import com.iaai.onyard.R;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.MetricsHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Activity that contains only a WebView to display Mobile CSAToday.
 */
public class CSAWebActivity extends Activity {
	
	/**
	 * Generic error dialog for this activity. Finish activity when OK is pressed.
	 */
	private static final int WEB_LOAD_ERROR_ID = 1;
	
	/**
	 * Inflate Activity UI and initialize settings for CSA WebView. If this
	 * is the first time the WebView is created (i.e., not on rotation), cookies,
	 * cache, and history are cleared.
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
	        setContentView(R.layout.csa_web_layout);
	        WebView webView = (WebView) findViewById(R.id.web_csa);
	        
	        if(savedInstanceState != null)
	        	webView.restoreState(savedInstanceState);
	        else
	        {
	        	CookieSyncManager.createInstance(this);
	        	CookieManager cookieManager = CookieManager.getInstance();
	        	cookieManager.removeAllCookie();
	        	
	        	webView.clearCache(true);
	        	webView.clearFormData();
	        	webView.clearHistory();
	        	webView.loadUrl(getIntent().getStringExtra(
	        			getString(R.string.web_activity_url_extra)));
	        }
	        
	        WebSettings webSettings = webView.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        webSettings.setSavePassword(false);
	        webSettings.setSaveFormData(false);
	        webView.setWebViewClient(new OnYardWebViewClient());
	        
	        
    	}
    	catch (Exception e)
    	{
    		showWebErrorDialog();
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
	    	Dialog dialog;
	    	AlertDialog.Builder builder;
	    	switch(id)
	    	{
		    	case WEB_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading Mobile CSAToday. " +
		    				"Please try again.")
		    		       .setCancelable(false)
		    		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		           public void onClick(DialogInterface dialog, int id) {
		    		        	   dialog.dismiss();
		    		        	   finish();
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
	 * If using showDialog(id), the activity will call through to this method the first 
	 * time, and hang onto it thereafter. Any dialog that is created by this method will 
	 * automatically be saved and restored, including whether it is showing. 
	 * 
	 * @param id The id of the dialog.
	 * @param args The dialog arguments provided to showDialog(int, Bundle).
	 * @return The dialog. If null is returned, the dialog will not be created.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		try
		{
			WebView webView = (WebView) findViewById(R.id.web_csa);
			webView.saveState(outState);
		}
		catch (Exception e)
		{
			LogHelper.logWarning(this, e);
		}
	}
	
	/**
	 * If back button pressed, update CSA end time for this stock and go to the previous
	 * page inside the WebView, if possible.
	 * 
	 * Called when a key was pressed down and not handled by any of the views inside of 
	 * the activity. So, for example, key presses while the cursor is inside a TextView 
	 * will not trigger the event (unless it is a navigation to another object) because 
	 * TextView handles its own key presses. 
	 * 
	 * @param keyCode The value in event.getKeyCode().
	 * @param event Description of the key event.
	 * @return True if the keyDown event has been handled and should not be propagated
	 * further. Otherwise, the result of the superclass onKeyDown method.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		try
		{
			WebView webView = (WebView) findViewById(R.id.web_csa);

			if ((keyCode == KeyEvent.KEYCODE_BACK))
			{
				if(getIntent().hasExtra(getString(R.string.stock_number_extra)))
				{
					new Thread(new Runnable() {
						public void run() {
							try
							{
								MetricsHelper.updateCSAEndTime(CSAWebActivity.this, 
										getIntent().getStringExtra(getString(
												R.string.stock_number_extra)));
							}
							catch (Exception e)
							{
								LogHelper.logWarning(CSAWebActivity.this, e);
							}
						}
			        }).start();
				}
				
				if(webView.canGoBack())
				{
					webView.goBack();
					return true;
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
	 * Class that prevents loading new URLs in the default browser.
	 */
	private class OnYardWebViewClient extends WebViewClient 
	{
		/**
		 * Signal to the WebViewClient that this application handles the loading a new URL.
		 * Load the new URL in the WebView.
		 * 
		 * Called when a new URL is loaded from the WebView.
		 * 
		 * @param view The WebView that is initiating the callback.
		 * @param url The url to be loaded.
		 * @return True, meaning our application will handle the new URL itself.
		 */
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) 
	    {
	        view.loadUrl(url);
	        return true;
	    }
	}
	
    /**
     * Show a generic web load error dialog. The activity will finish when "OK" is pressed.
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
					LogHelper.logWarning(CSAWebActivity.this, e);
				}
			}
		});
    }
}
