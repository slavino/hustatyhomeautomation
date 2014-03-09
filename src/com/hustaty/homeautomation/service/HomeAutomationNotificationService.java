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
import com.hustaty.homeautomation.util.ApplicationPreferences;
import com.hustaty.homeautomation.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * User: hustasl
 * Date: 12/7/13
 * Time: 4:38 PM
 */
public class HomeAutomationNotificationService {

    //Logging support.
    private static final String LOG_TAG = HomeAutomationNotificationService.class.getName();

    public HomeAutomationNotificationService(Context context, String notificationText, boolean silentNotification) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationTitle = "Home Automation Info";

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.home);
        Notification notification = new NotificationCompat.Builder(context).setContentTitle("Home Automation Info")
                .setContentText(notificationTitle)
                .setSmallIcon(R.drawable.home_notification)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .build();

//        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(notificationText));
//        Launch traffic information provider's application
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.hustaty.homeautomation");

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, launchIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

        if(!silentNotification) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        } else {
            notification.defaults = Notification.DEFAULT_LIGHTS;
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);

        Log.d(LOG_TAG, "#HomeAutomationNotificationService(): " + notificationText);
        LogUtil.appendLog(LOG_TAG + "#HomeAutomationNotificationService():" + notificationText);

        notificationManager.notify(notificationText.hashCode(), notification);

        //TEST with TextToSpeech
        Boolean ttsNotifications = (Boolean)ApplicationPreferences.getPreferences(context).get("ttsNotifications");
        if (!ttsNotifications) {
            TTSService ttsService = new TTSService(context);

            StringBuilder stringBuilder = new StringBuilder();

            if (notificationText.contains("DISARMED") && !notificationText.contains("PGY ENDED")) {
                stringBuilder.append("House is disarmed. Welcome home.");
                notificationText.replace("DISARMED;", "");
            }
            if (notificationText.contains("ARMED") && !notificationText.contains("DISARMED")) {
                stringBuilder.append("House just got armed.");
                notificationText.replace("ARMED;", "");
            }
            if (notificationText.contains("PGY STARTED")) {
                stringBuilder.append("Armed is only garage and ground floor.");
                notificationText.replace("PGY STARTED;", "");
            }
            if (notificationText.contains("PGY ENDED")) {
                stringBuilder.append("House is disarmed. Good morning.");
                notificationText.replace("PGY ENDED;", "");
            }
            if (notificationText.trim().length() == "yyyy-MM-dd HH:mm:ss".length()) { // || notificationTextMatcher(notificationText.trim())) {
                //ok, nothing else to bo spoken
            } else {
                //make an awful long message
                stringBuilder.append(notificationText);
            }

            ttsService.setTextToBeSpoken(stringBuilder.toString());
        }
    }

    private boolean notificationTextMatcher(String notificationText) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.parse(notificationText);
            return true;
        }
        catch(ParseException e){
            return false;
        }
    }

}
