package com.iaai.onyard.utility;

import android.location.Location;
import android.location.LocationManager;


public class LocationHelper {

    /**
     * The number of milliseconds in 30 seconds.
     */
    private static final int THIRTY_SECONDS = 1000 * 30;

    /**
     * Determine whether one location reading is better than the current best location.
     * 
     * @param location The new location that you want to evaluate.
     * @param currentBestLocation The current best location, to which we want to compare the new
     *            one.
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        final long timeDelta = location.getTime() - currentBestLocation.getTime();
        final boolean isSignificantlyNewer = timeDelta > THIRTY_SECONDS;
        final boolean isSignificantlyOlder = timeDelta < -THIRTY_SECONDS;
        final boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        }
        else
            if (isSignificantlyOlder) {
                return false;
            }

        final int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        final boolean isLessAccurate = accuracyDelta > 0;
        final boolean isMoreAccurate = accuracyDelta < 0;
        final boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        final boolean isFromSameProvider = isSameProvider(location, currentBestLocation);

        if (currentBestLocation.getProvider() == LocationManager.GPS_PROVIDER
                && location.getProvider() == LocationManager.NETWORK_PROVIDER
                && !isSignificantlyNewer) {
            return false;
        }
        else
            if (isMoreAccurate) {
                return true;
            }
            else
                if (isNewer && !isLessAccurate) {
                    return true;
                }
                else
                    if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
                        return true;
                    }
                    else {
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
    private static boolean isSameProvider(Location loc1, Location loc2) {
        if (loc1.getProvider() == null) {
            return loc2.getProvider() == null;
        }

        return loc1.getProvider().equals(loc2.getProvider());
    }
}
