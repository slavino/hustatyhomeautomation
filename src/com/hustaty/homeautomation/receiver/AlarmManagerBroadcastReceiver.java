package com.hustaty.homeautomation.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.enums.SharedPreferencesKeys;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.http.MyHttpClient;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.model.TrafficInformation;
import com.hustaty.homeautomation.service.LocationService;
import com.hustaty.homeautomation.service.TrafficNotificationService;
import com.hustaty.homeautomation.util.LogUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    // logger entry
    private final static String LOG_TAG = AlarmManagerBroadcastReceiver.class.getName();

    final public static String ONE_TIME = "onetime";
    final public static String LOCATION_UPDATE_INTENT = "com.hustaty.homeautomation.LOCATION_UPDATE_INTENT";
    final public static String UI_LOCATION_UPDATE_INTENT = "com.hustaty.homeautomation.UI_LOCATION_UPDATE_INTENT";

    final public static String UI_INTENT_EXTRA_THERMOSTATUS_ID = "arduinoThermoServerStatus";
    final public static String UI_INTENT_EXTRA_TRAFFICINFO_ID = "trafficInformation";

    final public static String INTENT_EXTRA_IP_INFO = "IP";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(LOG_TAG, "#onReceive(): BOOT_COMPLETED");
            LogUtil.appendLog(LOG_TAG + "#onReceive(): BOOT_COMPLETED");
            LocationService.obtainCurrentLocation(context);
            setAlarm(context);
        } else if (LOCATION_UPDATE_INTENT.equals(intent.getAction())
                && (intent != null)
                && intent.hasExtra(INTENT_EXTRA_IP_INFO)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SharedPreferencesKeys.APPLICATIONPREFERENCES_RECEIVED_GLOBALSERVERIPADDRESS.getKey(), intent.getStringExtra(INTENT_EXTRA_IP_INFO));
            editor.commit();
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

        Bundle extras = intent.getExtras();

        MyHttpClient myHttpClient = new MyHttpClient(context);

        try {

            ArduinoThermoServerStatus arduinoThermoServerStatus = myHttpClient.getThermoServerStatus(false);

            //TEST
            updatePreferences(context, arduinoThermoServerStatus);
            //END OF TEST

            List<TrafficInformation> trafficInformationList = null;
            try {
                if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SharedPreferencesKeys.APPLICATIONPREFERENCES_ALLOW_TRAFFICNOTIFICATIONS.getKey(), false)) {
                    trafficInformationList = myHttpClient.getTrafficInformation(true);
                }
            } catch (HomeAutomationException hae) {
                trafficInformationList = new ArrayList<TrafficInformation>();
                TrafficInformation ti = new TrafficInformation();
                ti.setDescription("Error");
                ti.setType("ERR: ");
                trafficInformationList.add(ti);
                LogUtil.appendLog(LOG_TAG + "#onReceive(): raised Exception " + hae.getMessage());
            }

            Intent newIntent = new Intent(UI_LOCATION_UPDATE_INTENT);

            Gson gson = new Gson();
            newIntent.putExtra(UI_INTENT_EXTRA_THERMOSTATUS_ID, gson.toJson(arduinoThermoServerStatus));
            if (trafficInformationList != null && trafficInformationList.size() > 0) {
                newIntent.putExtra(UI_INTENT_EXTRA_TRAFFICINFO_ID, gson.toJson(trafficInformationList));
            }
            context.sendBroadcast(newIntent);

            //TEST with hotWater
            Intent hotWaterIntent = new Intent(HotWaterWidgetProvider.HOTWATER_STATE);
            hotWaterIntent.putExtra(HotWaterWidgetProvider.HOTWATER_STATE, arduinoThermoServerStatus.getHotWaterSupply());
            context.sendBroadcast(hotWaterIntent);
            //END OF TEST with hotWater

            StringBuilder trafficInfoText = new StringBuilder();

            int counter = 1;
            if (trafficInformationList != null) {
                for (TrafficInformation trafficInformation : trafficInformationList) {
                    trafficInfoText.append((counter++) + "/" + trafficInformationList.size() + " ");
                    trafficInfoText.append(trafficInformation.getType() + ": " + trafficInformation.getDescription() + "\n");
                }
            }

            if (!("".equals(trafficInfoText.toString()))) {
                Calendar cal = Calendar.getInstance();
                Integer hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                boolean silentInfo =
                        (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SharedPreferencesKeys.APPLICATIONPREFERENCES_SILENTTRAFFICNOTIFICATIONS.getKey(), false) //user selected silent
                                ||
                                (hourOfDay < 7)  //not before 7:00am
                                || (
                                myHttpClient != null
                                        && myHttpClient.getWifiInfo() != null
                                        && PreferenceManager.getDefaultSharedPreferences(context).getString(SharedPreferencesKeys.APPLICATIONPREFERENCES_WIFI_SSID.getKey(), "unknown").equals(myHttpClient.getWifiInfo().getSSID()))

                        );
                new TrafficNotificationService(context, trafficInfoText.toString(), silentInfo);
            }

        } catch (HomeAutomationException e) {
            Log.e(LOG_TAG, "#onReceive(HAException): " + e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#onReceive(HAException):" + e.getMessage());
        } catch (IOException e) {

            Log.e(LOG_TAG, "#onReceive(IOexception): " + e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#onReceive(IOexception):" + e.getMessage());

            //notify the widgets via Intents

            Intent newIntent = new Intent(UI_LOCATION_UPDATE_INTENT);
            Gson gson = new Gson();
            ArduinoThermoServerStatus arduinoThermoServerStatus = new ArduinoThermoServerStatus();
            newIntent.putExtra(UI_INTENT_EXTRA_THERMOSTATUS_ID, gson.toJson(arduinoThermoServerStatus));
            context.sendBroadcast(newIntent);

            //TEST with hotWater
            Intent hotWaterIntent = new Intent(HotWaterWidgetProvider.HOTWATER_STATE);
            hotWaterIntent.putExtra(HotWaterWidgetProvider.HOTWATER_STATE, HotWaterWidgetProvider.HOTWATER_STATE_UNKNOWN);
            context.sendBroadcast(hotWaterIntent);
            //END OF TEST with hotWater

        }

        // Release the lock
        wl.release();
    }

    public void setAlarm(Context context) {
        Log.d(LOG_TAG, "#setAlarm(): method started");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, /*0*/ PendingIntent.FLAG_CANCEL_CURRENT);
        // After after 300 seconds
        int INTERVAL = 300 * 1000;
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, INTERVAL, pi);
    }

    public void cancelAlarm(Context context) {
        Log.d(LOG_TAG, "#cancelAlarm(): ");
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private String updatePreferences(Context context, ArduinoThermoServerStatus arduinoThermoServerStatus) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> currentState = sharedPreferences.getAll();

        boolean changeOccured = false;

        SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_UPDATETIME.getKey(), mysqlDateFormat.format(new Date())).commit();

        if (!arduinoThermoServerStatus.getSecurityArmed().equals(currentState.get(SharedPreferencesKeys.SECURITYSYSTEM_ARMED.getKey()))) {
            changeOccured = true;
            sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_ARMED.getKey(), arduinoThermoServerStatus.getSecurityArmed()).commit();
        }

        if (!arduinoThermoServerStatus.getSecurityAlarm().equals(currentState.get(SharedPreferencesKeys.SECURITYSYSTEM_ALARM.getKey()))) {
            changeOccured = true;
            sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_ALARM.getKey(), arduinoThermoServerStatus.getSecurityAlarm()).commit();
        }

        if (!arduinoThermoServerStatus.getSecurityFault().equals(currentState.get(SharedPreferencesKeys.SECURITYSYSTEM_FAULT.getKey()))) {
            changeOccured = true;
            sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_FAULT.getKey(), arduinoThermoServerStatus.getSecurityFault()).commit();
        }

        if (!arduinoThermoServerStatus.getSecurityFire().equals(currentState.get(SharedPreferencesKeys.SECURITYSYSTEM_FIRE.getKey()))) {
            changeOccured = true;
            sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_FIRE.getKey(), arduinoThermoServerStatus.getSecurityFire()).commit();
        }

        if (!arduinoThermoServerStatus.getSecurityTamper().equals(currentState.get(SharedPreferencesKeys.SECURITYSYSTEM_TAMPERED.getKey()))) {
            changeOccured = true;
            sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_TAMPERED.getKey(), arduinoThermoServerStatus.getSecurityTamper()).commit();
        }

        if (!arduinoThermoServerStatus.getSecurityPowerSupply().equals(currentState.get(SharedPreferencesKeys.SECURITYSYSTEM_ACPOWERFAILURE.getKey()))) {
            changeOccured = true;
            sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_ACPOWERFAILURE.getKey(), arduinoThermoServerStatus.getSecurityPowerSupply()).commit();
        }

        if (!arduinoThermoServerStatus.getSecurityLowBattery().equals(currentState.get(SharedPreferencesKeys.SECURITYSYSTEM_LOWBATTERY.getKey()))) {
            changeOccured = true;
            sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_LOWBATTERY.getKey(), arduinoThermoServerStatus.getSecurityLowBattery()).commit();
        }

        if (!arduinoThermoServerStatus.getSecurityPgY().equals(currentState.get(SharedPreferencesKeys.SECURITYSYSTEM_PGY.getKey()))) {
            changeOccured = true;
            sharedPreferences.edit().putString(SharedPreferencesKeys.SECURITYSYSTEM_PGY.getKey(), arduinoThermoServerStatus.getSecurityPgY()).commit();
        }

        if (arduinoThermoServerStatus.getHotWaterSupply() != null) {
            sharedPreferences.edit().putString("hotWaterSupply", arduinoThermoServerStatus.getHotWaterSupply()).commit();
        }

        if (changeOccured) {
            play(context);
        }

        return null;
    }

    private void play(final Context context) {

        final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        final int sound = soundPool.load(context, R.raw.nice_cut, 1);

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                    soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1.0f);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1.0f);
            }
        }.start();

    }

//	public void setOnetimeTimer(Context context) {
//		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
//		intent.putExtra(ONE_TIME, Boolean.TRUE);
//		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
//	}

}
