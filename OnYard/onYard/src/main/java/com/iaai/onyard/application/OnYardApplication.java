package com.iaai.onyard.application;

import android.app.Application;
import android.location.Location;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.iaai.onyard.session.OnYardSessionData;
import com.squareup.otto.Bus;


public class OnYardApplication extends Application {

    private static final int LOCATION_VALIDITY_TIME = 30000;

    private static OnYardSessionData sSessionData;
    private static final Bus sBus = new Bus();
    private static Location sLastKnownLocation;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Crashlytics.start(this);
            Crashlytics.setString("serial", Build.SERIAL);
        }
        catch (final Exception e) {}
    }

    public void createSessionData(String stockNumber) {
        sSessionData = new OnYardSessionData(stockNumber, getApplicationContext(), sBus);
    }

    public OnYardSessionData getSessionData() {
        return sSessionData;
    }

    public Bus getEventBus() {
        return sBus;
    }

    public void setSessionData(OnYardSessionData sessionData) {
        sSessionData = sessionData;
    }

    public void setCurrentLocation(Location location) {
        sLastKnownLocation = location;
    }

    public Location getLastKnownLocation() {
        if (sLastKnownLocation != null) {
            final long msElapsed = System.currentTimeMillis() - sLastKnownLocation.getTime();
            if (msElapsed > LOCATION_VALIDITY_TIME) {
                sLastKnownLocation = null;
            }
        }

        return sLastKnownLocation;
    }
}
