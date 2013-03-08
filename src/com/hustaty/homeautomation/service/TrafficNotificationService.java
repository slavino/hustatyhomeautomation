package com.hustaty.homeautomation.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.util.LogUtil;

/**
 * User: hustasl
 * Date: 3/1/13
 * Time: 9:30 PM
 */
public class TrafficNotificationService {

    //Logging support.
    private static final String LOG_TAG = TrafficNotificationService.class.getName();

    public TrafficNotificationService(Context context, String notificationText, boolean silentNotification) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationTitle = "Traffic Information";

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.home);
        Notification notification = new NotificationCompat.Builder(context).setContentTitle("Traffic Information")
                .setContentText(notificationTitle)
                .setSmallIcon(R.drawable.home_notification)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .build();

//        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(notificationText));
//        Launch traffic information provider's application
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.analyticadesign.eds");

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, launchIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        if(!silentNotification) {
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
