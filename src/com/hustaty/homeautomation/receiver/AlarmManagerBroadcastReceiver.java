package com.hustaty.homeautomation.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.http.MyHttpClient;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.model.TrafficInformation;
import com.hustaty.homeautomation.service.LocationService;
import com.hustaty.homeautomation.service.TrafficNotificationService;
import com.hustaty.homeautomation.util.LogUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    // logger entry
    private final static String LOG_TAG = AlarmManagerBroadcastReceiver.class.getName();

	final public static String ONE_TIME = "onetime";
    final public static String LOCATION_UPDATE_INTENT = "com.hustaty.homeautomation.LOCATION_UPDATE_INTENT";
    final public static String UI_LOCATION_UPDATE_INTENT = "com.hustaty.homeautomation.UI_LOCATION_UPDATE_INTENT";
    final public static String UI_EXTRA_ID = "arduinoThermoServerStatus";

	@Override
	public void onReceive(Context context, Intent intent) {

        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(LOG_TAG, "#onReceive(): BOOT_COMPLETED");
            LogUtil.appendLog(LOG_TAG + "#onReceive(): BOOT_COMPLETED");
            LocationService.obtainCurrentLocation(context);
            setAlarm(context);
        } else {
            Log.d(LOG_TAG, "#onReceive(): " + intent.getAction());
            LogUtil.appendLog(LOG_TAG + "#onReceive():" + intent.getAction());
            cancelAlarm(context);
            setAlarm(context);
        }

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HustatyHomeAutomation");
		// Acquire the lock
		wl.acquire();

		// You can do the processing here.
		Bundle extras = intent.getExtras();

		if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
            //TODO
		}

        MyHttpClient myHttpClient = new MyHttpClient(context);

        try {
            ArduinoThermoServerStatus arduinoThermoServerStatus = myHttpClient.getThermoServerStatus(false);
            Intent newIntent = new Intent(UI_LOCATION_UPDATE_INTENT);
            Gson gson = new Gson();
            newIntent.putExtra(UI_EXTRA_ID, gson.toJson(arduinoThermoServerStatus));
            context.sendBroadcast(newIntent);

            List<TrafficInformation> trafficInformationList = myHttpClient.getTrafficInformation(true);
            StringBuilder trafficInfoText = new StringBuilder();

            for(TrafficInformation trafficInformation : trafficInformationList) {
                trafficInfoText.append("1/" + trafficInformationList.size() + " ");
                trafficInfoText.append(trafficInformation.getType() + ": " + trafficInformation.getDescription() + "\n");
            }

            if(!("".equals(trafficInfoText.toString()))) {
                Calendar cal = Calendar.getInstance();
                Integer hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                boolean silentInfo = (hourOfDay < 7)  //not before 7a.m.
                        || myHttpClient.getWifiInfo().getSSID()
                            .equals(PreferenceManager.getDefaultSharedPreferences(context).getString("wifiSSID", "unknown"));
                new TrafficNotificationService(context, trafficInfoText.toString(), silentInfo);
            }

        } catch (HomeAutomationException e) {
            Log.e(LOG_TAG, "#onStartCommand(): " + e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#onStartCommand():" + e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, "#onStartCommand(): " + e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#onStartCommand():" + e.getMessage());
        }

        // Release the lock
		wl.release();
	}

	public void setAlarm(Context context) {
        Log.d(LOG_TAG, "#setAlarm(): ");
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.FALSE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, /*0*/ PendingIntent.FLAG_CANCEL_CURRENT);
		// After after 300 seconds
        int INTERVAL = 300*1000;
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, INTERVAL, pi);
	}

	public void cancelAlarm(Context context) {
        Log.d(LOG_TAG, "#cancelAlarm(): ");
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

//	public void setOnetimeTimer(Context context) {
//		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
//		intent.putExtra(ONE_TIME, Boolean.TRUE);
//		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
//	}

}
