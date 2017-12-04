package com.iaai.onyard.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import com.iaai.onyard.R;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.MetricsHelper;

/**
 * Activity that contains only a MapView which will display the user's location
 * and the vehicle's location as map markers.
 */
public class VehicleMapActivity extends Activity {
	
	/**
	 * Dialog indicating error with map activity. Map activity is finished when OK is
	 * pressed.
	 */
	private static final int MAP_LOAD_ERROR_ID = 1;
	
	/**
	 * The number of milliseconds in 30 seconds.
	 */
	private static final int THIRTY_SECONDS = 1000 * 30;
	/**
	 * The smoothing factor of the low-pass filter used to prevent the map from 
	 * violently jumping around when the compass readings change. A lower
	 * value means smoother rotation but longer adjustment times.
	 */
	private static final float FILTER_ALPHA = 1F;
	
	/**
	 * Object used to request location updates.
	 */
	private LocationManager mLocationManager;
	/**
	 * Object used to handle location updates.
	 */
	private GeoUpdateHandler mGeoUpdateHandler;
	/**
	 * The user location that has been deemed "best" (a combination of accuracy
	 * and currentness) and is being displayed on the map.
	 */
	private Location mCurrentBestLocation;
	
	/**
	 * Object used to request sensor (accelerometer and compass) updates.
	 */
	private SensorManager mSensorManager;
	/**
	 * Sensor representing the accelerometer.
	 */
	private Sensor mSensorGrav;
	/**
	 * Sensor representing the compass.
	 */
	private Sensor mSensorMag;
	/**
	 * Array containing the latest accelerometer readings.
	 */
	private float[] mGravValues;
	/**
	 * Array containing the latest compass readings.
	 */
	private float[] mMagValues;
	/**
	 * Main Google Maps object - entry point for all methods related to the map.
	 */
	private GoogleMap mMap;
	/**
	 * Map Marker to be placed at vehicle's location.
	 */
	private Marker mVehicleMarker;
	/**
	 * Map Marker to be placed at user's current location.
	 */
	private Marker mUserMarker;
	/**
	 * Map Marker to be placed between user and vehicle. This marker shows the distance between
	 * the user and vehicle markers.
	 */
	private Marker mDistanceMarker;
	/**
	 * Timestamp of the last time sensor reading data was allowed to rotate the map.
	 */
	private long mLastSensorPing;


	/**
	 * Sensor Event Listener that reacts to new sensor readings by converting them to bearing
	 * and calling {@link #rotateMapToBearing}.
	 */
	private final SensorEventListener mSensorListener = new SensorEventListener() {
		
		public void onSensorChanged(SensorEvent event) 
		{
			try
			{
				if(event == null) return;
				
				float[] rotation = new float[9];
				float[] outRotation = new float[9];
				float[] orientation = new float[3];
	
				if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				{
					mGravValues[0] = mGravValues[0] + FILTER_ALPHA * (event.values[0] - 
							mGravValues[0]);
					mGravValues[1] = mGravValues[1] + FILTER_ALPHA * (event.values[1] - 
							mGravValues[1]);
					mGravValues[2] = mGravValues[2] + FILTER_ALPHA * (event.values[2] - 
							mGravValues[2]);
				}
				else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				{
					mMagValues[0] = mMagValues[0] + FILTER_ALPHA * (event.values[0] - 
							mMagValues[0]);
					mMagValues[1] = mMagValues[1] + FILTER_ALPHA * (event.values[1] - 
							mMagValues[1]);
					mMagValues[2] = mMagValues[2] + FILTER_ALPHA * (event.values[2] - 
							mMagValues[2]);
				}
	
				boolean success = SensorManager.getRotationMatrix(rotation, null, 
						mGravValues, mMagValues);
				SensorManager.remapCoordinateSystem(rotation, SensorManager.AXIS_X, 
						SensorManager.AXIS_Z, outRotation);
				if(!success) return;
				SensorManager.getOrientation(outRotation, orientation);
	
				float degreesBearing = (float) Math.toDegrees(orientation[0]);
	
				//account for difference between magnetic north and true north
				if(mCurrentBestLocation != null)
				{
					GeomagneticField gmf = new GeomagneticField((float) mCurrentBestLocation.getLatitude(), 
							(float) mCurrentBestLocation.getLongitude(),
							(float) mCurrentBestLocation.getAltitude(), 
							System.currentTimeMillis());
					degreesBearing -= gmf.getDeclination();
				}
				
				if(degreesBearing < 0F)
					degreesBearing += 360F;
	
				rotateMapToBearing(degreesBearing);
				
				LogHelper.logVerbose("Bearing in degrees: " + degreesBearing);
			}
			catch (Exception e)
			{
				showMapLoadErrorDialog();
				LogHelper.logError(VehicleMapActivity.this, e);
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) 
		{
			if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && 
					accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
				LogHelper.logVerbose("Compass data unreliable");
			}
		}
	};
	
	/**
	 * Inflate Activity UI, initialize the MapView settings, request location
	 * updates from the Network/GPS Providers, and create a point on the map
	 * at the vehicle's location.
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
			setContentView(R.layout.vehicle_map_v2);

			setUpMap();
			startCompassUpdates();
			
			createVehiclePoint();			
		}
		catch (Exception e)
		{
			showMapLoadErrorDialog();
			LogHelper.logError(this, e);
		}
	}
	
	/**
	 * Set up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.
	 */
	private void setUpMap()
	{
		if(mMap == null)
		{
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			
			if(mMap != null)
			{
				mMap.setMyLocationEnabled(true);
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}
			else
			{
				throw new UnsupportedOperationException("Google Play Services not found.");
			}
		}
	}
	/**
	 * Rotate the map camera position around the user location to the given bearing. If the user's
	 * location is unknown, this method does nothing.
	 * 
	 * @param bearing The direction to point the camera in, in degrees clockwise from north.
	 */
	private void rotateMapToBearing(float bearing)
	{		
		if((System.currentTimeMillis() - mLastSensorPing) > 500 && mUserMarker != null)
		{
	        CameraPosition cameraPosition = new CameraPosition.Builder()
	        	.target(mUserMarker.getPosition())
		    	.bearing(bearing)
		    	.zoom(18)
		    	.build();
		    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500, null);
		    
		    mLastSensorPing = System.currentTimeMillis();
		}
	}

	/**
	 * Get vehicle location and info from the intent extras bundle and create a
	 * vehicle point on the map represented by the vehicle marker.
	 */
	private void createVehiclePoint() 
	{
		float lat = getIntent().getFloatExtra(getString(
				R.string.latitude_extra), 0);
		float lng = getIntent().getFloatExtra(getString(
				R.string.longitude_extra), 0);
		//TODO: display stock #, VIN, and ymm when vehicle marker pressed
//		String stockNum = getIntent().getStringExtra(getString(
//				R.string.stock_number_extra));
//		String VIN = getIntent().getStringExtra(getString(
//				R.string.vin_extra));
//		String yearMakeModel = getIntent().getStringExtra(getString(
//				R.string.year_make_model_extra));

		if(lat != 0 && lng != 0)
		{
			mVehicleMarker = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(lat, lng))
						.title("Vehicle")
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_vehicle_location)));
			
			if(mUserMarker == null)
			{
				CameraPosition cameraPosition = new CameraPosition.Builder()
		        	.target(new LatLng(mVehicleMarker.getPosition().latitude, mVehicleMarker.getPosition().longitude))
			    	.zoom(18)
			    	.build();
			    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
			}
		}
	}
	
	/**
	 * Stop all activity threads and free resources. Stop requesting location 
	 * and compass updates.
	 */
	@Override
	protected void onDestroy()
	{
		try
		{
			super.onDestroy();
			
			stopCompassUpdates();
			stopLocationUpdates();
		}
		catch (Exception e)
		{
			LogHelper.logWarning(this, e);
		}
	}

	/**
	 * Stop all activity threads and free resources. Stop requesting location 
	 * and compass updates.
	 */
	@Override
	protected void onPause()
	{
		try
		{
			super.onPause();
			
			stopCompassUpdates();
			stopLocationUpdates();
		}
		catch (Exception e)
		{
			LogHelper.logWarning(this, e);
		}
	}
	
	/**
	 * Start all activity threads. Resume requesting location and compass updates.
	 */
	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			
			startCompassUpdates();
			startLocationUpdates();
		}
		catch (Exception e)
		{
			LogHelper.logError(this, e);
		}
	}
	
	/**
	 * Start requesting location updates from both network and GPS location service providers.
	 */
	private void startLocationUpdates()
	{
		if(mLocationManager == null || mGeoUpdateHandler == null)
		{
			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mGeoUpdateHandler = new GeoUpdateHandler(this);
		}
		
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5,
				mGeoUpdateHandler);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5,
				mGeoUpdateHandler);
	}
	
	/**
	 * Stop requesting location updates from network and GPS location service providers.
	 */
	private void stopLocationUpdates()
	{
		if(mLocationManager != null && mGeoUpdateHandler != null)
			mLocationManager.removeUpdates(mGeoUpdateHandler);
	}
	
	/**
	 * Start requesting updates from accelerometer and magnetic field sensors.
	 */
	private void startCompassUpdates()
	{
		mLastSensorPing = System.currentTimeMillis();
		
		if(mSensorManager == null || mSensorGrav == null || mSensorMag == null)
		{
			List<Sensor> sensors = null;
			
			mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	        sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
	        if(sensors.size() > 0)
	        	mSensorGrav = sensors.get(0);
	        
	        sensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
	        if(sensors.size() > 0)
	        	mSensorMag = sensors.get(0);
	        
	        mGravValues = new float[3];
	        mMagValues = new float[3];
		}
        
        mSensorManager.registerListener(mSensorListener, mSensorGrav, 5000000);
        mSensorManager.registerListener(mSensorListener, mSensorMag, 5000000);
        
	}

	/**
	 * Stop requesting updates from accelerometer and magnetic field sensors.
	 */
	private void stopCompassUpdates()
	{
		if(mSensorManager != null)
			mSensorManager.unregisterListener(mSensorListener);
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
		    	case MAP_LOAD_ERROR_ID:
		    		builder = new AlertDialog.Builder(this);
		    		builder.setMessage("There was an error while loading the map. Please try again.")
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
			finish();
			return new Dialog(this);
		}
    }
    
	/**
	 * If back button pressed, update map end time metrics value.
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
			if ((keyCode == KeyEvent.KEYCODE_BACK))
			{
				new Thread(new Runnable() {
					public void run() {
						try
						{
							MetricsHelper.updateMapEndTime(VehicleMapActivity.this, 
									getIntent().getStringExtra(getString(
											R.string.stock_number_extra)));
							if(mSensorManager != null)
								mSensorManager.unregisterListener(mSensorListener);
						}
						catch (Exception e)
						{
							LogHelper.logWarning(VehicleMapActivity.this, e);
						}
					}
				}).start();
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
	 * Class to handle Location updates.
	 */
	public class GeoUpdateHandler implements LocationListener
	{
		private Context mContext;
		
		/**
		 * Constructor.
		 * 
		 * @param context The current context.
		 */
		public GeoUpdateHandler(Context context)
		{
			super();
			try
			{
				mContext = context;
			}
			catch (Exception e)
			{
				showMapLoadErrorDialog();
				LogHelper.logError(VehicleMapActivity.this, e);
			}
		}
		
		/**
		 * If new user location is better than old, remove old location marker and mark
		 * the new location on the map.
		 * 
		 * Called when the user location has changed.
		 * 
		 * @param location The new location, as a Location object.
		 */
		@Override
		public void onLocationChanged(Location location) 
		{
			try
			{
				if(isBetterLocation(location, mCurrentBestLocation))
				{
					removeLastUserLocMarker();
 
					mCurrentBestLocation = location;
					
					mUserMarker = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(location.getLatitude(), location.getLongitude()))
						.title("User")
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_user_location)));
					
					addDistancePoint(mUserMarker, mVehicleMarker);
					
					CameraPosition cameraPosition = new CameraPosition.Builder()
				        .target(new LatLng(mCurrentBestLocation.getLatitude(), mCurrentBestLocation.getLongitude()))
					    .zoom(18)
					    .build();
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
				}
			}
			catch (Exception e)
			{
				showMapLoadErrorDialog();
				LogHelper.logError(mContext, e);
			}
		}
		
		/**
		 * Place a marker on the map between the user and vehicle marker with a snippet stating the
		 * distance (in feet) to the vehicle from the user's current position. If user marker or
		 * vehicle marker does not exist, do nothing.
		 * 
		 * @param userMarker Map marker at user location.
		 * @param vehicleMarker Map marker at vehicle location.
		 */
		private void addDistancePoint(Marker userMarker, Marker vehicleMarker)
		{
			try
			{
				if(userMarker == null || vehicleMarker == null)
					return;
				
				if(mDistanceMarker != null)
					mDistanceMarker.remove();
				
				mDistanceMarker = mMap.addMarker(new MarkerOptions()
					.position(new LatLng(
							.8 * userMarker.getPosition().latitude + .2 * vehicleMarker.getPosition().latitude, 
							.8 * userMarker.getPosition().longitude + .2 * vehicleMarker.getPosition().longitude))
					.title("Distance to Vehicle: ")
					.snippet((int)calcDistance(userMarker, vehicleMarker) + " feet")
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
				
				mDistanceMarker.showInfoWindow();
			}
			catch (Exception e)
			{
				showMapLoadErrorDialog();
				LogHelper.logError(mContext, e);
			}
		}
		
		/**
		 * Calculate the distance, in feet, between two map markers.
		 * 
		 * @param userMarker Map marker at user location.
		 * @param vehicleMarker Map marker at vehicle location.
		 * @return The distance, in feet, between the two map markers.
		 */
		private double calcDistance(Marker userMarker, Marker vehicleMarker)
		{
		    try 
		    {
		    	//see "Haversine Formula" for more details
				double earthRadius = 20902231;
				double userLat = userMarker.getPosition().latitude;
				double userLng = userMarker.getPosition().longitude;
				double vehLat = vehicleMarker.getPosition().latitude;
				double vehLng = vehicleMarker.getPosition().longitude;
				
				double dLat = Math.toRadians(vehLat - userLat);
				double dLng = Math.toRadians(vehLng - userLng);
				double rLat1 = Math.toRadians(userLat);
				double rLat2 = Math.toRadians(vehLat);
				
				double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
					Math.sin(dLng / 2) * Math.sin(dLng / 2) * Math.cos(rLat1) * Math.cos(rLat2);
				double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
				
				return earthRadius * c;
			} 
		    catch (Exception e) 
		    {
		    	showMapLoadErrorDialog();
				LogHelper.logError(mContext, e);
				return 0F;
			}
		}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
		 */
		@Override
		public void onProviderDisabled(String provider) {
			LogHelper.logVerbose("Provider " + provider + " is now disabled.");
		}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
		 */
		@Override
		public void onProviderEnabled(String provider) {
			LogHelper.logVerbose("Provider " + provider + " is now enabled.");
		}

		/* (non-Javadoc)
		 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			LogHelper.logVerbose("Provider " + provider + " has changed status to " + status);
		}
		
		/** 
		 * Determine whether one location reading is better than the current best location.
		 * 
		 * @param location  The new location that you want to evaluate.
		 * @param currentBestLocation  The current best location, to which we want to 
		 * compare the new one.
		 */
		private boolean isBetterLocation(Location location, Location currentBestLocation) 
		{
			try
			{
				if (currentBestLocation == null)
					return true;

				long timeDelta = location.getTime() - currentBestLocation.getTime();
				boolean isSignificantlyNewer = timeDelta > THIRTY_SECONDS;
				boolean isSignificantlyOlder = timeDelta < -THIRTY_SECONDS;
				boolean isNewer = timeDelta > 0;

				if (isSignificantlyNewer) 
					return true;
				else if (isSignificantlyOlder) 
					return false;

				int accuracyDelta = (int) (location.getAccuracy() - 
						currentBestLocation.getAccuracy());
				boolean isLessAccurate = accuracyDelta > 0;
				boolean isMoreAccurate = accuracyDelta < 0;
				boolean isSignificantlyLessAccurate = accuracyDelta > 200;

				boolean isFromSameProvider = isSameProvider(location,
						currentBestLocation);
				
				if(currentBestLocation.getProvider() == LocationManager.GPS_PROVIDER
						&& location.getProvider() == LocationManager.NETWORK_PROVIDER
						&& !isSignificantlyNewer)
					return false;
				else if (isMoreAccurate) 
					return true;
				else if (isNewer && !isLessAccurate) 
					return true;
				else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) 
					return true;
				else
					return false;
			}
			catch (Exception e)
			{
				showMapLoadErrorDialog();
				LogHelper.logError(mContext, e);
				return false;
			}
		}
		
		/**
		 * Check whether two locations are from the same provider.
		 * 
		 * @param loc1 The first location to compare.
		 * @param loc2 The second location to compare.
		 * @return True if the locations are from the same provider, false otherwise.
		 */
		private boolean isSameProvider(Location loc1, Location loc2) 
		{
			try
			{
				if (loc1.getProvider() == null)
					return loc2.getProvider() == null;

				return loc1.getProvider().equals(loc2.getProvider());
			}
			catch (Exception e)
			{
				showMapLoadErrorDialog();
				LogHelper.logError(mContext, e);
				return false;
			}
		}
		
		/**
		 * Remove most recent user location marker from map itemized overlay.
		 */
		private void removeLastUserLocMarker()
		{
			try
			{
				if(mUserMarker != null)
					mUserMarker.remove();
			}
			catch (Exception e)
			{
				showMapLoadErrorDialog();
				LogHelper.logError(mContext, e);
			}
		}
	}
	
    /**
     * Show a generic map load error dialog. The map activity will 
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
					LogHelper.logWarning(VehicleMapActivity.this, e);
				}
			}
		});
    }
}
