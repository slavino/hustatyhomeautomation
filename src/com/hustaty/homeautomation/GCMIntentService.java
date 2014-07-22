package com.hustaty.homeautomation;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Patterns;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hustaty.homeautomation.enums.GCMMessageContent;
import com.hustaty.homeautomation.receiver.GcmBroadcastReceiver;
import com.hustaty.homeautomation.service.HomeAutomationNotificationService;
import com.hustaty.homeautomation.util.ApplicationPreferences;
import com.hustaty.homeautomation.util.LogUtil;

import java.util.regex.Matcher;

/**
 * User: hustasl
 * Date: 3/28/13
 * Time: 4:36 PM
 */
public class GCMIntentService extends IntentService {

    private static final String LOG_TAG = GCMIntentService.class.getName();

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                String message = extras.getString(GCMMessageContent.MESSAGE.getKey());
                if((message == null) || (message.isEmpty())) {
                    sendNotification(extras.toString());
                } else {
                    sendNotification(message);
                }

                //
                String ipAddress = extras.getString(GCMMessageContent.IP_ADDRESS.getKey());
                if(ipAddress == null || ipAddress.isEmpty()) {

                } else {
                    ipAddress = ipAddress.trim();
                    Matcher matcher = Patterns.IP_ADDRESS.matcher(ipAddress);
                    if(matcher.matches()) {
                        ApplicationPreferences.setValue(this.getApplicationContext(), GCMMessageContent.IP_ADDRESS.getKey(), ipAddress);
                        Log.i(LOG_TAG, "IP address obtained via GCM - '" + ipAddress + "'");
                    } else {
                        Log.i(LOG_TAG, "Incorrect IP address obtained via GCM - '" + ipAddress + "'");
                        LogUtil.appendLog(LOG_TAG + "#onHandleIntent(): Incorrect IP address obtained via GCM - '" + ipAddress + "'");
                    }
                }
                Log.i(LOG_TAG, "Received: " + extras.toString());
                LogUtil.appendLog(LOG_TAG + "#onHandleIntent(): Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        new HomeAutomationNotificationService(this.getApplicationContext(), msg, false);
    }

}
