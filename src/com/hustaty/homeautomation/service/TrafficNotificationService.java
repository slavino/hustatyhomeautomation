package com.hustaty.homeautomation.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.hustaty.homeautomation.R;

/**
 * User: hustasl
 * Date: 3/1/13
 * Time: 9:30 PM
 */
public class TrafficNotificationService {

    public TrafficNotificationService(Context context, String notificationText) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.home, "Traffic: Police on way home!", System.currentTimeMillis());

        String notificationTitle = "Traffic Information";

        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(notificationText));
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);

        notificationManager.notify(notificationText.hashCode(), notification);

    }
}
