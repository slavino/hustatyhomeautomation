package com.hustaty.homeautomation.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.util.ApplicationPreferences;
import com.hustaty.homeautomation.util.LogUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * User: hustasl
 * Date: 12/7/13
 * Time: 4:38 PM
 */
public class HomeAutomationNotificationService {

    //Logging support.
    private static final String LOG_TAG = HomeAutomationNotificationService.class.getName();

    public HomeAutomationNotificationService(Context context, String notificationText, boolean silentNotification) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final int ringerMode = audioManager.getRingerMode();

        String notificationTitle = "Home Automation Info";

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.home);
        Notification notification = new NotificationCompat.Builder(context).setContentTitle("Home Automation Info")
                .setContentText(notificationTitle)
                .setSmallIcon(R.drawable.home_notification)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .build();

//        Launch traffic information provider's application
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.hustaty.homeautomation");

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, launchIntent, PendingIntent.FLAG_ONE_SHOT);

        if (!silentNotification) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        } else {
            notification.defaults = Notification.DEFAULT_LIGHTS;
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);

        Log.d(LOG_TAG, "#HomeAutomationNotificationService(): " + notificationText);
        LogUtil.appendLog(LOG_TAG + "#HomeAutomationNotificationService():" + notificationText);

        notificationManager.notify(notificationText.hashCode(), notification);

        Boolean ttsNotificationsSilenced = (Boolean) ApplicationPreferences.getPreferences(context).get("ttsNotifications");
        if (!ttsNotificationsSilenced) {

            final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            StringBuilder stringBuilder = new StringBuilder();

            if (notificationText.contains("DISARMED") && !notificationText.contains("PGY ENDED")) {
                stringBuilder.append("House is disarmed. Welcome home.");
                notificationText = notificationText.replace("DISARMED;", "");
            }
            if (notificationText.contains("ARMED") && !notificationText.contains("DISARMED")) {
                stringBuilder.append("Your house just got armed. ");
                notificationText = notificationText.replace("ARMED;", "");
            }
            if (notificationText.contains("PGY STARTED")) {
                if(hour > 20
                        || hour < 5) {
                    stringBuilder.append("Armed is only garage and ground floor. Good night.");
                } else {
                    stringBuilder.append("Armed is only garage and ground floor. Are you sure you armed the correct zones?");
                }
                notificationText = notificationText.replace("PGY STARTED;", "");
            }
            if (notificationText.contains("PGY ENDED")) {
                if(hour > 4 && hour < 10) {
                    stringBuilder.append("Your house is disarmed. Good morning.");
                } else {
                    stringBuilder.append("Your house is disarmed.");
                }
                notificationText = notificationText.replace("PGY ENDED;", "");
                notificationText = notificationText.replace("DISARMED;", "");
            }
            if (notificationText.contains("ALARM STARTED")) {
                stringBuilder.append("Your house reports alarm.");
                notificationText = notificationText.replace("ALARM STARTED;", "");
            }
            if (notificationText.contains("ALARM ENDED")) {
                stringBuilder.append("Your house reports that alarm ended.");
                notificationText = notificationText.replace("ALARM ENDED;", "");
            }

            if (notificationText.trim().length() == "yyyy-MM-dd HH:mm:ss".length()) { // || notificationTextMatcher(notificationText.trim())) {
                //ok, nothing else to bo spoken
            } else {
                //make an awful long message
                stringBuilder.append(notificationText);
            }

            if(ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                TTSService ttsService = new TTSService(context);
                ttsService.setTextToBeSpoken(stringBuilder.toString());
            }

        }
    }

}
