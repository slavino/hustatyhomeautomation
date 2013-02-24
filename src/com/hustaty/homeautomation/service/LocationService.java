package com.hustaty.homeautomation.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import com.hustaty.homeautomation.receiver.AlarmManagerBroadcastReceiver;

public class LocationService {

    // logger entry
    private final static String LOG_TAG = LocationService.class.getName();

    private static Location myLocation;

	public static Location obtainCurrentLocation(final Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        LocationService.myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(LocationService.myLocation == null) {
        }

        Log.d(LOG_TAG, "#obtainCurrentLocation(): " + myLocation);

        Intent intent = new Intent(AlarmManagerBroadcastReceiver.LOCATION_UPDATE_INTENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //Register for broadcast intents
        int minTime = 300000;
        int minDistance = 1000;


        if(isGPSLocationAvailable(context)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, pendingIntent);
        }

        if(isNetworkLocationAvailable(context)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, pendingIntent);
        }

        return myLocation;

	}

    private static boolean isGPSLocationAvailable(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isNetworkLocationAvailable(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)) {
            return true;
        } else {
            return false;
        }
    }

}
