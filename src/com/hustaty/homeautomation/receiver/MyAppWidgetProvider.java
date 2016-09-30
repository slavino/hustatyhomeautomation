package com.hustaty.homeautomation.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.widget.RemoteViews;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hustaty.homeautomation.MainActivity;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.model.TrafficInformation;
import com.hustaty.homeautomation.service.SamsungRichNotificationService;
import com.hustaty.homeautomation.util.LogUtil;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * User: hustasl
 * Date: 3/8/13
 * Time: 8:57 AM
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = MyAppWidgetProvider.class.getName();

    public static final String SRN_TEXTID = "WIDGET UPDATE";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int widgetCount = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget_bedroom_temperature, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AlarmManagerBroadcastReceiver.UI_LOCATION_UPDATE_INTENT.equals(intent.getAction())) {
            if (intent.hasExtra(AlarmManagerBroadcastReceiver.UI_INTENT_EXTRA_THERMOSTATUS_ID)) {
                String thermoServerStatusString = intent.getStringExtra(AlarmManagerBroadcastReceiver.UI_INTENT_EXTRA_THERMOSTATUS_ID);

                Log.d(LOG_TAG, "#onReceive(): UI_INTENT_EXTRA_THERMOSTATUS_ID: " + thermoServerStatusString);

                Gson gson = new Gson();

                ArduinoThermoServerStatus arduinoThermoServerStatus = null;
                try {
                    arduinoThermoServerStatus = gson.fromJson(thermoServerStatusString, ArduinoThermoServerStatus.class);
                    SamsungRichNotificationService samsungRichNotificationService = new SamsungRichNotificationService(context, arduinoThermoServerStatus, SRN_TEXTID);
                } catch (JsonSyntaxException e) {
                    Log.e(LOG_TAG, "#onReceive(): " + e.getMessage());
                    LogUtil.appendLog(LOG_TAG + "#onReceive(): " + e.getMessage());
                }

                List<TrafficInformation> trafficInformationList = null;
                Type trafficInformationListType = new TypeToken<List<TrafficInformation>>() {
                }.getType();
                try {
                    trafficInformationList = gson.fromJson(intent.getStringExtra(AlarmManagerBroadcastReceiver.UI_INTENT_EXTRA_TRAFFICINFO_ID), trafficInformationListType);
                } catch (JsonSyntaxException e) {
                    Log.e(LOG_TAG, "#onReceive(): " + e.getMessage());
                }

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisWidget = new ComponentName(context, MyAppWidgetProvider.class);
                int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                if (allWidgetIds.length > 0) {
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                    if (arduinoThermoServerStatus != null) {

                        if ("1".equals(arduinoThermoServerStatus.getSecurityArmed())) {
                            if("1".equals(arduinoThermoServerStatus.getSecurityPgY())) {
                                remoteViews.setImageViewResource(R.id.widgetLeftIcon, R.drawable.home_alarm_state_locked_ab);
                            } else {
                                remoteViews.setImageViewResource(R.id.widgetLeftIcon, R.drawable.home_alarm_state_locked);
                            }
                        } else if ("0".equals(arduinoThermoServerStatus.getSecurityArmed())) {
                            remoteViews.setImageViewResource(R.id.widgetLeftIcon, R.drawable.home_alarm_state_unlocked);
                        } else {
                            remoteViews.setImageViewResource(R.id.widgetLeftIcon, R.drawable.home_alarm_state_unknown);
                            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.nice_cut);
                            mediaPlayer.start();
                        }

                        if(arduinoThermoServerStatus.getNightHour() != null) {
                            if(arduinoThermoServerStatus.getNightHour() == true) {
                                remoteViews.setImageViewResource(R.id.widgetRightIcon, R.drawable.home_heating_mode_night);
                            } else if(arduinoThermoServerStatus.getNightHour() == false) {
                                remoteViews.setImageViewResource(R.id.widgetRightIcon, R.drawable.home_heating_mode_day);
                            }
                        } else {
                            remoteViews.setImageViewResource(R.id.widgetRightIcon, R.drawable.home_heating_mode_unknown);
                        }


                        if("1".equals(arduinoThermoServerStatus.getSecurityAlarm())) {
                            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.nice_cut);
                            mediaPlayer.start();
                        }

                        if (arduinoThermoServerStatus.getT28B79F8504000082() != null) {
                            remoteViews.setTextViewText(R.id.widget_bedroom_temperature, arduinoThermoServerStatus.getT28B79F8504000082() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_bedroom_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getT28205B850400008B() != null) {
                            remoteViews.setTextViewText(R.id.widget_upperlobby_temperature, arduinoThermoServerStatus.getT28205B850400008B() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_upperlobby_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getT280F5B8504000019() != null) {
                            remoteViews.setTextViewText(R.id.widget_workroom_temperature, arduinoThermoServerStatus.getT280F5B8504000019() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_workroom_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getT28F82D850400001F() != null) {
                            remoteViews.setTextViewText(R.id.widget_outside_temperature, arduinoThermoServerStatus.getT28F82D850400001F() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_outside_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getT28F1E685040000DB() != null) {
                            remoteViews.setTextViewText(R.id.widget_entrancelobby_temperature, arduinoThermoServerStatus.getT28F1E685040000DB() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_entrancelobby_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getT28C9C9AA040000EA() != null) {
                            remoteViews.setTextViewText(R.id.widget_kitchen_temperature, arduinoThermoServerStatus.getT28C9C9AA040000EA() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_kitchen_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getT282a54ab0400004e() != null) {
                            remoteViews.setTextViewText(R.id.widget_bedroom2_temperature, arduinoThermoServerStatus.getT282a54ab0400004e() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_bedroom2_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getT28e6c455050000d4() != null) {
                            remoteViews.setTextViewText(R.id.widget_northchldroom_temperature, arduinoThermoServerStatus.getT28e6c455050000d4() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_northchldroom_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getT288b4c5605000020() != null) {
                            remoteViews.setTextViewText(R.id.widget_southchldroom_temperature, arduinoThermoServerStatus.getT288b4c5605000020() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_southchldroom_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getKidzTemp() != null) {
                            remoteViews.setTextViewText(R.id.widget_kidzPortable_temperature, arduinoThermoServerStatus.getKidzTemp() + "\u00b0C");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_kidzPortable_temperature, "--.--\u00b0C");
                        }

                        if (arduinoThermoServerStatus.getKidzHum() != null) {
                            remoteViews.setTextViewText(R.id.widget_kidzPortable_humidity, arduinoThermoServerStatus.getKidzHum() + "%");
                        } else {
                            remoteViews.setTextViewText(R.id.widget_kidzPortable_humidity, "--.--%");
                        }
                    }

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM. HH:mm:ss");

                    StringBuilder stringBuilder = new StringBuilder();

                    if (trafficInformationList != null) {
                        for (TrafficInformation ti : trafficInformationList) {
                            stringBuilder.append(ti.getType() + ": ");
                            stringBuilder.append(ti.getDescription() + "\n");
                        }
                        stringBuilder.append(" - " + simpleDateFormat.format(new Date()));
                    } else {
                        stringBuilder.append(simpleDateFormat.format(new Date()) + " No traffic information");
                    }
                    remoteViews.setTextViewText(R.id.widget_traffic_information, stringBuilder.toString());
                    appWidgetManager.updateAppWidget(allWidgetIds, remoteViews);
                }
            }
        }
    }
}
