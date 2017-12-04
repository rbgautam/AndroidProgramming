package com.iaai.onyard.event;

import android.location.Location;

public class LocationChangedEvent {

    private final Location mNewLocation;

    public LocationChangedEvent(Location location) {
        mNewLocation = location;
    }

    public Location getNewLocation() {
        return mNewLocation;
    }
}
