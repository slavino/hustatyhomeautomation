package com.hustaty.homeautomation.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hustaty.homeautomation.MainActivity;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.model.TrafficInformation;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: hustasl
 * Date: 3/8/13
 * Time: 8:57 AM
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = MyAppWidgetProvider.class.getName();

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
                } catch (JsonSyntaxException e) {
                    Log.e(LOG_TAG, "#onReceive(): " + e.getMessage());
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
