package com.sidewindercookie.glance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
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
    private static final float PROXIMITY = 50000f; // proximity
    int counter = 001;

    private DiscountOffer[] discountsRaw = new DiscountOffer[] {
            new DiscountOffer("McDonalds","Enjoy a free Cheeseburger, Mayo Chicken or McFlurry Original® when you order an Extra Value or Wrap Meal at McDonald's.", "https://www.myunidays.com/GB/en-GB/partners/mcdonalds/view/in-store"),
            new DiscountOffer("SuperDrug", "10% off", "https://www.myunidays.com/GB/en-GB/partners/superdrug/view/in-store"),
    };

    private List<LocationTrigger> discounts = new ArrayList<LocationTrigger>();
    private List<LocationTrigger> triggers = new ArrayList<LocationTrigger>();

    private GoogleNearbyAPI nearbyAPI;

    Location lastLocation;

    GoogleNearbyAPI queue;

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

    public void checkVagueTriggers(Location location) {
        ensureNearbyAPI();
        if (location != null) {
            for (LocationTrigger trigger : triggers) {
                if (!trigger.getInformalLocation().getName().startsWith("$$")) {
                    continue;
                }
                nearbyAPI.findNearby((int) PROXIMITY, location, trigger);
            }
        }
    }

    public void checkTriggers(Location location) {
        if (location != null) {
            for (LocationTrigger trigger : triggers) {
                if (trigger.getInformalLocation().getName().startsWith("$$")) {
                    continue;
                }
                float distance = location.distanceTo(trigger.getInformalLocation().getLocation());
                Log.d(TAG, "distance to " + trigger.getName() + " is " + distance);
                if (distance < PROXIMITY) {
                    sendNotification(trigger);
                }
            }
        }
        checkVagueTriggers(location);
    }

    public void sendNotification(LocationTrigger trigger) {
        sendNotification(trigger, trigger.getInformalLocation().getName());
    }

    public void sendNotification(LocationTrigger trigger, String location) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "glance_channel");
        Intent ii = new Intent(getApplicationContext(), NoteViewer.class);
        ii.putExtra("name", trigger.getName());
        ii.putExtra("details", trigger.getDetails());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("View your \"" + trigger.getName() + "\" note");
        bigText.setBigContentTitle("Near " + location + "?");
        bigText.setSummaryText("View your \"" + trigger.getName() + "\" note");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("glance_channel",
                    "Glance",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        discounts.clear();
        for (DiscountOffer offer : discountsRaw) {
            discounts.add(new LocationTrigger(offer.getName(), offer.getOffer(), offer.getUrl()));
        }
        ensureNearbyAPI();
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

    private void ensureNearbyAPI() {
        if (nearbyAPI == null) {
            nearbyAPI = new GoogleNearbyAPI(this);
        }
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
