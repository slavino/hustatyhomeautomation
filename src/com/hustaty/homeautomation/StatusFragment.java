package com.hustaty.homeautomation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.http.MyHttpClient;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.util.LogUtil;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;

/**
 * User: llisivko
 * Date: 2/24/13
 * Time: 7:19 PM
 */
public class StatusFragment extends Fragment {

    private final static String LOG_TAG = StatusFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.status_fragment, container, false);

        MyHttpClient myHttpClient = new MyHttpClient(view.getContext());
        ArduinoThermoServerStatus thermoServerStatus = null;
        try {
            thermoServerStatus = myHttpClient.getThermoServerStatus(true);
        } catch (HomeAutomationException e) {
            showSettings(view.getContext());
            return null;
        } catch (ClientProtocolException e) {
            Log.e(LOG_TAG, e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#onCreateView():" + e.getMessage());
        } catch (IOException e) {
            myHttpClient.useAnotherURL();
            try {
                thermoServerStatus = myHttpClient.getThermoServerStatus(true);
            } catch (HomeAutomationException e1) {
                showSettings(view.getContext());
                return null;
            } catch (IOException e1) {
                Log.e(LOG_TAG, e1.getMessage());
                LogUtil.appendLog(LOG_TAG + "#onCreateView():" + e1.getMessage());
            }
        }

        if (thermoServerStatus != null) {
            TextView workroom = (TextView) view.findViewById(R.id.textView_roomtemp_workroom);
            TextView bedroom = (TextView) view.findViewById(R.id.textView_roomtemp_bedroom);
            TextView outside = (TextView) view.findViewById(R.id.textView_roomtemp_outside);
            TextView upperLobby = (TextView) view.findViewById(R.id.textView_roomtemp_upperlobby);
            TextView entranceHall = (TextView) view.findViewById(R.id.textView_roomtemp_entrancehall);
            workroom.setText(thermoServerStatus.getT280F5B8504000019() + "\u00b0C");
            bedroom.setText(thermoServerStatus.getT28B79F8504000082() + "\u00b0C");
            outside.setText(thermoServerStatus.getT28F82D850400001F() + "\u00b0C");
            upperLobby.setText(thermoServerStatus.getT28205B850400008B() + "\u00b0C");
            entranceHall.setText(thermoServerStatus.getT28F1E685040000DB() + "\u00b0C");

            TextView thermostat1Value = (TextView) view.findViewById(R.id.textView_thermostat_1_val);
            thermostat1Value.setText("1".equals(thermoServerStatus.getThermostat1()) ? "ON" : "OFF") ;

            TextView thermostat2Value = (TextView) view.findViewById(R.id.textView_thermostat_2_val);
            thermostat2Value.setText("1".equals(thermoServerStatus.getThermostat2()) ? "ON" : "OFF") ;

            TextView hotWaterSwitchValue = (TextView) view.findViewById(R.id.textView_hot_water_switch_val);
            hotWaterSwitchValue.setText("1".equals(thermoServerStatus.getHotWaterSwitch()) ? "ON" : "OFF") ;

            TextView hotWaterSupplyValue = (TextView) view.findViewById(R.id.textView_hot_water_supply_val);
            hotWaterSupplyValue.setText("1".equals(thermoServerStatus.getHotWaterSupply()) ? "ON" : "OFF") ;

            TextView heatingSupplyValue = (TextView) view.findViewById(R.id.textView_heating_supply_val);
            heatingSupplyValue.setText("1".equals(thermoServerStatus.getHeatingState()) ? "ON" : "OFF") ;

            TextView remainingTimeForLastServerCommandValue = (TextView) view.findViewById(R.id.textView_remaining_time_for_last_server_command_val);
            remainingTimeForLastServerCommandValue.setText(formatMillis(thermoServerStatus.getRemainingTimeForLastServerCommand())) ;

            TextView arduinoUptimeValue = (TextView) view.findViewById(R.id.textView_arduino_uptime_val);
            arduinoUptimeValue.setText(formatMillis(thermoServerStatus.getUptime())) ;

            TextView arduino2UptimeValue = (TextView) view.findViewById(R.id.textView_arduino2_uptime_val);
            arduino2UptimeValue.setText(formatMillis(thermoServerStatus.getUptime2())) ;

            TextView lastCommunicationFromArduinoValue = (TextView) view.findViewById(R.id.textView_last_communication_from_arduino_val);
            lastCommunicationFromArduinoValue.setText(thermoServerStatus.getLastCommunicationFromArduino()) ;

            TextView securitySystemStatus = (TextView) view.findViewById(R.id.textView_security_system_armed);
            securitySystemStatus.setText(
                    "ARMED: " + thermoServerStatus.getSecurityArmed()
                    + "\tALARM: " + thermoServerStatus.getSecurityAlarm()
                    + "\nFAULT: " + thermoServerStatus.getSecurityFault()
                    + "\tFIRE: " + thermoServerStatus.getSecurityFire()
                    + "\nLOW BATTERY: " + thermoServerStatus.getSecurityLowBattery()
                    + "\tPANIC: " + thermoServerStatus.getSecurityPanic()
                    + "\nPOWER: " + thermoServerStatus.getSecurityPowerSupply()
                    + "\tTAMPER: " + thermoServerStatus.getSecurityTamper()
                    + "\nPgY: " + thermoServerStatus.getSecurityPgY()
            ) ;

        }

        return view;
    }

    public void showSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        this.startActivityForResult(intent, SettingsActivity.REQUEST_CODE_FOR_SETTINGS);
        return;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SettingsActivity.REQUEST_CODE_FOR_SETTINGS) {
            if(resultCode == Activity.RESULT_OK) {

            }
        }
    }

    private String formatMillis(String ms) {
        Long millis = new Long(ms);
        Long seconds = millis/1000;
        Long minutes = seconds/60;
        Long hours = minutes/60;
        Long days  = hours/24;
        millis %= 1000;
        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        return ( (days > 0) ? days +"d " : "")
                + ( (hours > 0 || days > 0) ? hours + "h " : "")
                + ( (minutes > 0 || hours>0 || days > 0) ? minutes + "m " : "")
                + seconds + "." + millis + "s";
    }
}
