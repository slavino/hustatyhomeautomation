package com.hustaty.homeautomation.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
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

//        Intent intent = new Intent(AlarmManagerBroadcastReceiver.LOCATION_UPDATE_INTENT);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //Register for broadcast intents
//        int minTime = 300000;
//        int minDistance = 1000;
//        if(isGPSLocationAvailable(context)) {
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, pendingIntent);
//        }
//        if(isNetworkLocationAvailable(context)) {
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, pendingIntent);
//        }
        if (myLocation != null
                && (new Date()).before(new Date(myLocation.getTime() + GPS_TIMEOUT))) {
            return myLocation;
        }

        return null;

    }

//    private static boolean isGPSLocationAvailable(Context context) {
//        PackageManager pm = context.getPackageManager();
//        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
//    }

//    private static boolean isNetworkLocationAvailable(Context context) {
//        PackageManager pm = context.getPackageManager();
//        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
//    }

}
