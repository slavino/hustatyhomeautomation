package com.hustaty.homeautomation.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: hustasl
 * Date: 3/1/13
 * Time: 9:30 PM
 */
public class TrafficNotificationService {

    //Logging support.
    private static final String LOG_TAG = TrafficNotificationService.class.getName();

    public TrafficNotificationService(Context context, String notificationText) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.home, "Traffic: Police on way home!", System.currentTimeMillis());

        String notificationTitle = "Traffic Information";

//        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(notificationText));
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.analyticadesign.eds");

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, launchIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
        Integer hourOfDay = new Integer(simpleDateFormat.format(new Date()));

        if(hourOfDay > 7) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        } else {
            notification.defaults = Notification.DEFAULT_LIGHTS;
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);

        Log.d(LOG_TAG, "#TrafficNotificationService(): " + notificationText);
        LogUtil.appendLog(LOG_TAG + "#TrafficNotificationService():" + notificationText);

        notificationManager.notify(notificationText.hashCode(), notification);

    }
}