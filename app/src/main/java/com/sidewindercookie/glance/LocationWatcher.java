package com.sidewindercookie.glance;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
https://developer.android.com/reference/android/app/Service.html#WhatIsAService
https://stackoverflow.com/questions/28535703/best-way-to-get-user-gps-location-in-background-in-android
 */

public class LocationWatcher extends Service {

    // Define constants
    private static final String TAG = "GLANCELOCATIONLISTENER";
    private LocationManager locationManager;
    private static final int INTERVAL = 5000; // informalLocation polling interval
    private static final float PROXIMITY = 10f; // proximity

    private List<LocationTrigger> triggers = new ArrayList<LocationTrigger>();

    Location lastLocation;

    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            lastLocation = new Location(provider);
        }


        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "locChange - " + location.getLatitude());
            lastLocation = location;
            checkTriggers(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d(TAG, "statusChanged - " + s);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d(TAG, "providerEnabled - " + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d(TAG, "providerDisabled - " + s);
        }
    }

    LocationListener[] locationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    public void checkTriggers(Location location) {
        if (location != null) {
            for (LocationTrigger trigger : triggers) {
                float distance = location.distanceTo(trigger.getInformalLocation().getLocation());
                Log.d(TAG, "distance to thing is " + distance);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "hi");
        triggers.clear();
        for (Parcelable parcelable : intent.getParcelableArrayExtra("note-triggers")) {
            triggers.add((LocationTrigger) parcelable);
        }
        checkTriggers(lastLocation);
        return START_STICKY;
    }

    private void ensureLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void registerUpdateRequests(String name, String provider, LocationListener listener) {
        ensureLocationManager();
        try {
            locationManager.requestLocationUpdates(provider, INTERVAL, PROXIMITY, listener);
        } catch (java.lang.SecurityException e) {
            Log.i(TAG, "failed to request informalLocation for " + name, e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, name + " provider doesn't exist - " + e.getMessage());
        }
    }

    @Override
    public void onCreate() {
        registerUpdateRequests("gps", LocationManager.GPS_PROVIDER, locationListeners[0]);
        registerUpdateRequests("network", LocationManager.NETWORK_PROVIDER, locationListeners[1]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            for (LocationListener listener : locationListeners) {
                try {
                    locationManager.removeUpdates(listener);
                } catch (Exception e) {
                    Log.i(TAG, "failed to remove listener", e);
                }
            }
        }
    }
}
