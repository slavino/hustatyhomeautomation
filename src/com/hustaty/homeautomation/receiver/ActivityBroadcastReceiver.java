package com.hustaty.homeautomation.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import com.google.gson.Gson;
import com.hustaty.homeautomation.R;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;

/**
 * User: hustasl
 * Date: 2/25/13
 * Time: 2:10 PM
 */
public class ActivityBroadcastReceiver extends BroadcastReceiver {

    //Logging support.
    private static final String LOG_TAG = ActivityBroadcastReceiver.class.getName();

    private Activity activity;

    public ActivityBroadcastReceiver(Activity activity) {
        this.activity = activity;
        Log.d(LOG_TAG, "#ActivityBroadcastReceiver");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(this.activity != null) {
            Log.d(LOG_TAG, "#onReceive(): " + intent.getAction());
            if(intent.hasExtra(AlarmManagerBroadcastReceiver.UI_EXTRA_ID)) {
                String json = intent.getStringExtra(AlarmManagerBroadcastReceiver.UI_EXTRA_ID);
                Log.d(LOG_TAG, json);
                Gson gson = new Gson();
                ArduinoThermoServerStatus thermoServerStatus = gson.fromJson(json, ArduinoThermoServerStatus.class);

                //TODO implement UI update
                TextView workroom = (TextView) activity.findViewById(R.id.textView_roomtemp_workroom);
                workroom.setText(thermoServerStatus.getT280F5B8504000019() + "\u00b0C");

            }
        }
    }
}
