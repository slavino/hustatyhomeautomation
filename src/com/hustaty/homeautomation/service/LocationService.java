package com.hustaty.homeautomation.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import com.hustaty.homeautomation.receiver.AlarmManagerBroadcastReceiver;
import com.hustaty.homeautomation.util.LogUtil;

import java.util.Date;

public class LocationService {

    // logger entry
    private final static String LOG_TAG = LocationService.class.getName();

    public static final long GPS_TIMEOUT = 30L * 60L * 1000L; //30 minutes in millis

    private static Location myLocation;

    public static Location obtainCurrentLocation(final Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        LocationService.myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (LocationService.myLocation == null) {
            LocationService.myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        Log.d(LOG_TAG, "#obtainCurrentLocation(): " + myLocation);
        LogUtil.appendLog(LOG_TAG + "#obtainCurrentLocation(): " + myLocation);

        Intent intent = new Intent(AlarmManagerBroadcastReceiver.LOCATION_UPDATE_INTENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        Register for broadcast intent
//        long minTime = 10*60*1000; //millis
//        float minDistance = 100*1000; //meters set too high to avoid extreme battery drain in background
//        if(isGPSLocationAvailable(context)) {
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, pendingIntent);
//        }
//        if (isNetworkLocationAvailable(context)) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, pendingIntent);
//        }
        if (myLocation != null
                && (new Date()).before(new Date(myLocation.getTime() + GPS_TIMEOUT))) {
            return myLocation;
        }

//      TEST
//        if (isNetworkLocationAvailable(context)) {
//            Log.i(LOG_TAG, "Location update too old. Requesting new update from NETWORK.");
//            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, pendingIntent);
//        }
//        if(isGPSLocationAvailable(context)) {
//            Log.i(LOG_TAG, "Location update too old. Requesting new update from GPS.");
//            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, pendingIntent);
//        }
//      END OF TEST

        return null;

    }

    private static boolean isGPSLocationAvailable(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    private static boolean isNetworkLocationAvailable(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK);
    }

}
