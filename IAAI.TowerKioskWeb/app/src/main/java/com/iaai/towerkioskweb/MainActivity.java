package com.iaai.towerkioskweb;

import java.lang.reflect.Method;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
	
	private static final String TOWER_KIOSK_URL = "http://dtowkiosk.iaai.com/";
	
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
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.activity_main);
	        WebView webView = (WebView) findViewById(R.id.web_towerkiosk);
	        webView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	        
//	        if(savedInstanceState != null)
//	        	webView.restoreState(savedInstanceState);
//	        else
//	        {
	        	CookieSyncManager.createInstance(this);
	        	CookieManager cookieManager = CookieManager.getInstance();
	        	cookieManager.removeAllCookie();
	        	
	        	webView.clearCache(true);
	        	webView.clearFormData();
	        	webView.clearHistory();
	        	webView.loadUrl(TOWER_KIOSK_URL);
//	        }
	        
	        WebSettings webSettings = webView.getSettings();
	        webSettings.setJavaScriptEnabled(true);
	        webView.setWebViewClient(new OnYardWebViewClient());
	        
	        
    	}
    	catch (Exception e)
    	{
    		Log.e(e.getMessage(), "TOWERKIOSKWEB");
    	}
    }

	public void onWindowFocusChanged(boolean hasFocus)
    {
            try
            {
               if(!hasFocus)
               {
                    Object service  = getSystemService("statusbar");
                    Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                    Method collapse = statusbarManager.getMethod("collapse");
                    collapse .setAccessible(true);
                    collapse .invoke(service);
               }
            }
            catch(Exception ex)
            {
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
			WebView webView = (WebView) findViewById(R.id.web_towerkiosk);
			webView.saveState(outState);
		}
		catch (Exception e)
		{

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
//		try
//		{
//			WebView webView = (WebView) findViewById(R.id.web_towerkiosk);
//
//			if ((keyCode == KeyEvent.KEYCODE_BACK))
//			{
//				if(webView.canGoBack())
//				{
//					webView.goBack();
//					return true;
//				}
//			}
//			return super.onKeyDown(keyCode, event);
//		}
//		catch (Exception e)
//		{
			return false;
//		}
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
}
