package com.hustaty.homeautomation.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.enums.Appliance;
import com.hustaty.homeautomation.enums.Command;
import com.hustaty.homeautomation.enums.SharedPreferencesKeys;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.http.MyHttpClient;
import com.hustaty.homeautomation.model.CommonResult;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by user on 2/26/14.
 */
public class HotWaterWidgetProvider extends AppWidgetProvider {

    public static final String HOTWATER_WIDGET_CLICK = "com.hustaty.homeautomation.HOTWATER_WIDGET_CLICK";
    public static final String HOTWATER_STATE = "com.hustaty.homeautomation.HOTWATER_STATE";

    public static final String HOTWATER_STATE_OFF = "0";
    public static final String HOTWATER_STATE_ON = "1";
    public static final String HOTWATER_STATE_UNKNOWN = "UNKNOWN";

    public static final String RESULT_OK = "OK";

    private static final String LOG_TAG = HotWaterWidgetProvider.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "#onReceive(): method started with " + intent.getAction() + " --> " + intent.getExtras());

        //TEST
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> currentState = sharedPreferences.getAll();
        String hotWaterStoredState = (String)currentState.get(SharedPreferencesKeys.HEATINGSYSTEM_HOTWATERSUPPLY.getKey());
        Log.d(LOG_TAG, "hotwater stored State is: " + hotWaterStoredState);
        //TEST

        RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.waterwidget);
        ComponentName thisWidget = new ComponentName(context, HotWaterWidgetProvider.class);

        if (HotWaterWidgetProvider.HOTWATER_WIDGET_CLICK.equals(intent.getAction())) {
            //toggle widget according to current state
            Intent clickIntent = new Intent(HotWaterWidgetProvider.HOTWATER_WIDGET_CLICK);
            clickIntent.putExtra(HOTWATER_STATE, HOTWATER_STATE_OFF); //just to have default value
            updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_onoff_state);

            //TEST - to view interaction - trigger update through AppWidgetManager
            AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, updateView);

            MyHttpClient myHttpClient = new MyHttpClient(context);

            if(HOTWATER_STATE_OFF.equals(hotWaterStoredState)) {
//                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_onoff_state);
                clickIntent.putExtra(HOTWATER_STATE, HOTWATER_STATE_OFF);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR, 1);
                try {
                    CommonResult commonResult = myHttpClient.addStoredEvent(Appliance.HOTWATER, Command.HOTWATER_ON, new Date(), cal.getTime(), true);
                    if(commonResult != null
                        && commonResult.getResult() != null
                        && (commonResult.getResult().startsWith(RESULT_OK)
                            || RESULT_OK.equals(commonResult.getResult()))) {
                        //everything is OK
                        Log.d(LOG_TAG, "#onReceive(HOTWATER_WIDGET_CLICK): adding stored event for switching on hotwater SUCCEEDED");
                        updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_on_state);
                        sharedPreferences.edit().putString(SharedPreferencesKeys.HEATINGSYSTEM_HOTWATERSUPPLY.getKey(), HOTWATER_STATE_ON).commit();
                    } else {
                        //something went wrong and API returned other than OK
                        Log.d(LOG_TAG, "#onReceive(HOTWATER_WIDGET_CLICK): adding stored event for switching on hotwater FAILED" + commonResult.getResult());
                        updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_unknown_state);
                    }
                    Toast.makeText(context, commonResult.getResult(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } catch (HomeAutomationException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            } else if(HOTWATER_STATE_ON.equals(hotWaterStoredState)) {
                clickIntent.putExtra(HOTWATER_STATE, HOTWATER_STATE_ON);
                try {
                    CommonResult commonResult = myHttpClient.removeStoredEvent(Appliance.HOTWATER, true);
                    if(commonResult != null
                        && commonResult.getResult() != null
                        && (commonResult.getResult().startsWith(RESULT_OK)
                            || RESULT_OK.equals(commonResult.getResult()))) {
                        //everything is OK
                        Log.d(LOG_TAG, "#onReceive(HOTWATER_WIDGET_CLICK): removing stored event for hotwater SUCCEEDED");
                        updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_off_state);
                        sharedPreferences.edit().putString(SharedPreferencesKeys.HEATINGSYSTEM_HOTWATERSUPPLY.getKey(), HOTWATER_STATE_OFF).commit();
                    } else {
                        //something went wrong and API returned other than OK
                        Log.d(LOG_TAG, "#onReceive(HOTWATER_WIDGET_CLICK): removing stored event for hotwater FAILED " + commonResult.getResult());
                        updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_unknown_state);
                    }
                    Toast.makeText(context, commonResult.getResult(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } catch (HomeAutomationException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }

            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
            updateView.setOnClickPendingIntent(R.id.hotwater_widget_imagebutton, pendingIntentClick);

            Log.d(LOG_TAG, "#onReceive(HOTWATER_WIDGET_CLICK): Setting pending intent");

        } else if(HOTWATER_STATE.equals(intent.getAction())) {
            String hotwaterinfo = intent.getStringExtra(HOTWATER_STATE);
            Log.d(LOG_TAG, "#onReceive(HOTWATER_STATE): --> Hotwater state is " + hotwaterinfo);

            Intent clickIntent = new Intent(HotWaterWidgetProvider.HOTWATER_WIDGET_CLICK);

            if(HOTWATER_STATE_OFF.equals(hotwaterinfo))  {
                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_off_state);
                clickIntent.putExtra(HOTWATER_STATE, HOTWATER_STATE_OFF);
            } else if(HOTWATER_STATE_ON.equals(hotwaterinfo)) {
                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_on_state);
                clickIntent.putExtra(HOTWATER_STATE, HOTWATER_STATE_ON);
            } else {
                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_unknown_state);
                clickIntent.putExtra(HOTWATER_STATE, HOTWATER_STATE_OFF);
            }
            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
            updateView.setOnClickPendingIntent(R.id.hotwater_widget_imagebutton, pendingIntentClick);

        } else {
            Log.d(LOG_TAG, "#onReceive(" + intent.getAction() +"): else part, setting pending intent only");
            Intent clickIntent = new Intent(HotWaterWidgetProvider.HOTWATER_WIDGET_CLICK);
            clickIntent.putExtra(HOTWATER_STATE, HOTWATER_STATE_OFF);
            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
            updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_unknown_state);
            updateView.setOnClickPendingIntent(R.id.hotwater_widget_imagebutton, pendingIntentClick);
        }

        //trigger update through AppWidgetManager
        AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, updateView);

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "#onUpdate(): method started");
        RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.waterwidget);


        //TEST
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> currentState = sharedPreferences.getAll();
        String hotWaterStoredState = (String)currentState.get(SharedPreferencesKeys.HEATINGSYSTEM_HOTWATERSUPPLY.getKey());
        Log.d(LOG_TAG, "hotwater stored State is: " + hotWaterStoredState);
        //TEST

        if (HOTWATER_STATE_ON.equals(hotWaterStoredState)) {
            updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_on_state);
        } else if(HOTWATER_STATE_OFF.equals(hotWaterStoredState)) {
            updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_off_state);
        } else {
            updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_unknown_state);
        }

        Intent clickIntent = new Intent(HotWaterWidgetProvider.HOTWATER_WIDGET_CLICK);

        PendingIntent pendingClickIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
        updateView.setOnClickPendingIntent(R.id.hotwater_widget_imagebutton, pendingClickIntent);

        Log.d(LOG_TAG, "#onUpdate(): Setting pending intent");

        for(int id : appWidgetIds){
            appWidgetManager.updateAppWidget(id, updateView);
        }
//        appWidgetManager.updateAppWidget(appWidgetIds, updateView);


        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(LOG_TAG, "#onEnabled() started");

        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG, "#onDisabled() started");

        super.onDisabled(context);
    }


}
