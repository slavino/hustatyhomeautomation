package com.hustaty.homeautomation;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import com.hustaty.homeautomation.enums.SharedPreferencesKeys;
import com.hustaty.homeautomation.util.ApplicationPreferences;
import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;

import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.http.MyHttpClient;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.util.LogUtil;

/**
 * User: llisivko
 * Date: 2/24/13
 * Time: 7:19 PM
 */
public class StatusFragment extends Fragment {

    private final static String LOG_TAG = StatusFragment.class.getName();
    protected ProgressDialog progressDialog;

    private View view;
    private MyHttpClient myHttpClient;

    ArduinoThermoServerStatus thermoServerStatus = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /*View */
        view = inflater.inflate(R.layout.status_fragment, container, false);
        myHttpClient = new MyHttpClient(view.getContext());

        (new HttpCommunicationTask()).execute();

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
                // TODO: ???
            }
        }
    }

    private class HttpCommunicationTask extends AsyncTask<Void, Void, Void> {

        public void showLoadingProgressDialog() {
            Log.d(LOG_TAG, "Showing progress dialog.");
            synchronized (this) {
                showProgressDialog("Executing HTTPS request to home automation server...");
            }
        }

        public void showProgressDialog(CharSequence message) {
            synchronized (this) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(StatusFragment.this.getActivity());
                    progressDialog.setIndeterminate(true);
                }

                progressDialog.setMessage(message);
                Log.d(LOG_TAG, "Showing progress dialog with message['" + message + "']");
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }
        }

        public synchronized void dismissProgressDialog() {
            synchronized (this) {
                Log.d(LOG_TAG, "Dismissing progress dialog.");
                if (progressDialog != null) {
                    try {
                        progressDialog.dismiss();
                    } catch (IllegalArgumentException iae) {
                        LogUtil.appendLog(LOG_TAG + "Error: " + iae.getMessage());
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArduinoThermoServerStatus thermoServerStatus = null;

            try {
//                showProgressDialog("Executing HTTPS request to home automation server...");
                thermoServerStatus = myHttpClient.getThermoServerStatus(false);
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

            StatusFragment.this.thermoServerStatus = thermoServerStatus;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dismissProgressDialog();
            updateUI();
        }

//        @Override
//        protected void onCancelled(ArduinoThermoServerStatus arduinoThermoServerStatus) {
//            dismissProgressDialog();
//            updateUI();
//        }
//
//        @Override
//        protected void onCancelled() {
//            dismissProgressDialog();
//        }
    }

    /**
     * Formats millis to human readable string.
     * @param ms
     * @return
     */
    private String formatMillis(String ms) {
        try {
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
        } catch (NumberFormatException nfe) {
            LogUtil.appendLog(LOG_TAG + "#formatMillis(): " + ms + nfe.getMessage());
        }
        return "N/A";

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateUI() {
        if (thermoServerStatus != null) {
            TextView workroom = (TextView) view.findViewById(R.id.textView_roomtemp_workroom);
            TextView bedroom = (TextView) view.findViewById(R.id.textView_roomtemp_bedroom);
            TextView bedroom2 = (TextView) view.findViewById(R.id.textView_roomtemp_bedroom2);
            TextView outside = (TextView) view.findViewById(R.id.textView_roomtemp_outside);
            TextView upperLobby = (TextView) view.findViewById(R.id.textView_roomtemp_upperlobby);
            TextView entranceHall = (TextView) view.findViewById(R.id.textView_roomtemp_entrancehall);
            TextView kitchen = (TextView) view.findViewById(R.id.textView_roomtemp_kitchen);

            TextView southchildroom = (TextView) view.findViewById(R.id.textView_roomtemp_southchldroom);
            TextView northchildroom = (TextView) view.findViewById(R.id.textView_roomtemp_northchldroom);

            TextView kidzPortableHumidity = (TextView) view.findViewById(R.id.textView_roomhum_kidz);
            TextView kidzPortableTemperature = (TextView) view.findViewById(R.id.textView_roomtemp_kidz);

            TextView diningroom = (TextView) view.findViewById(R.id.textView_roomtemp_diningroom);

            workroom.setText(thermoServerStatus.getT280F5B8504000019() + "\u00b0C");
            bedroom.setText(thermoServerStatus.getT28B79F8504000082() + "\u00b0C");
            bedroom2.setText(thermoServerStatus.getT282a54ab0400004e() + "\u00b0C");
            outside.setText(thermoServerStatus.getT28F82D850400001F() + "\u00b0C");
            upperLobby.setText(thermoServerStatus.getT28205B850400008B() + "\u00b0C");
            entranceHall.setText(thermoServerStatus.getT28F1E685040000DB() + "\u00b0C");
            kitchen.setText(thermoServerStatus.getT28C9C9AA040000EA() + "\u00b0C");

            southchildroom.setText(thermoServerStatus.getT288b4c5605000020() + "\u00b0C");
            northchildroom.setText(thermoServerStatus.getT28e6c455050000d4() + "\u00b0C");

            kidzPortableHumidity.setText(thermoServerStatus.getKidzHum() + "%");
            kidzPortableTemperature.setText(thermoServerStatus.getKidzTemp() + "\u00b0C");

            diningroom.setText(thermoServerStatus.getT289F84B80400004C() + "\u00b0C");//t289F84B80400004C

            TextView thermostat1Value = (TextView) view.findViewById(R.id.textView_thermostat_1_val);
            thermostat1Value.setText("1".equals(thermoServerStatus.getThermostat1()) ? "ON" : "OFF");

            TextView thermostat2Value = (TextView) view.findViewById(R.id.textView_thermostat_2_val);
            thermostat2Value.setText("1".equals(thermoServerStatus.getThermostat2()) ? "ON" : "OFF");

            TextView hotWaterSwitchValue = (TextView) view.findViewById(R.id.textView_hot_water_switch_val);
            hotWaterSwitchValue.setText("1".equals(thermoServerStatus.getHotWaterSwitch()) ? "ON" : "OFF");

            TextView hotWaterSupplyValue = (TextView) view.findViewById(R.id.textView_hot_water_supply_val);
            hotWaterSupplyValue.setText("1".equals(thermoServerStatus.getHotWaterSupply()) ? "ON" : "OFF");

            TextView heatingSupplyValue = (TextView) view.findViewById(R.id.textView_heating_supply_val);
//            heatingSupplyValue.setText("1".equals(thermoServerStatus.getHeatingState()) ? "ON" : "OFF");
            if("1".equals(thermoServerStatus.getHeatingState())) {
                heatingSupplyValue.setText("ON");
            } else if("0".equals(thermoServerStatus.getHeatingState())) {
                heatingSupplyValue.setText("OFF");
            } else {
                heatingSupplyValue.setText(thermoServerStatus.getHeatingState());
            }
            heatingSupplyValue.setText("1".equals(thermoServerStatus.getHeatingState()) ? "ON" : "OFF");

//            TextView remainingTimeForLastServerCommandValue = (TextView) view.findViewById(R.id.textView_remaining_time_for_last_server_command_val);
//            remainingTimeForLastServerCommandValue.setText(formatMillis(thermoServerStatus.getRemainingTimeForLastServerCommand()));
//
//            TextView arduinoUptimeValue = (TextView) view.findViewById(R.id.textView_arduino_uptime_val);
//            arduinoUptimeValue.setText(formatMillis(thermoServerStatus.getUptime()));
//
//            TextView arduino2UptimeValue = (TextView) view.findViewById(R.id.textView_arduino2_uptime_val);
//            arduino2UptimeValue.setText(formatMillis(thermoServerStatus.getUptime2()));
//
//            TextView lastCommunicationFromArduinoValue = (TextView) view.findViewById(R.id.textView_last_communication_from_arduino_val);
//            lastCommunicationFromArduinoValue.setText(thermoServerStatus.getLastCommunicationFromArduino());

            TextView securitySystemStatus = (TextView) view.findViewById(R.id.textView_security_system_armed);

            String armedState = "NONE ";
            if ("1".equals(thermoServerStatus.getSecurityArmed())) {
                if ("1".equals(thermoServerStatus.getSecurityPgY())) {
                    armedState = "NIGHT";
                } else if ("0".equals(thermoServerStatus.getSecurityPgY())) {
                    armedState = "FULL ";
                }
            }

            securitySystemStatus.setTypeface(Typeface.MONOSPACE);
            securitySystemStatus.setText(
                    "ARMED: " + armedState
                            + "\t\t ALARM: " + thermoServerStatus.getSecurityAlarm()
                            + "\nFAULT: " + thermoServerStatus.getSecurityFault()
                            + "\t\t    FIRE: " + thermoServerStatus.getSecurityFire()
                            + "\nLOW BATTERY: " + thermoServerStatus.getSecurityLowBattery()
                            + "\t\tPANIC: " + thermoServerStatus.getSecurityPanic()
                            + "\nPOWER: " + thermoServerStatus.getSecurityPowerSupply()
                            + "\t\t     TAMPER: " + thermoServerStatus.getSecurityTamper()
                            + "\nPgY: " + thermoServerStatus.getSecurityPgY()
                            + "\t\t  ARM: " + thermoServerStatus.getSecurityArmed()
            );

            final ImageButton sshImgButton = (ImageButton)view.findViewById(R.id.sshImageButton);
            sshImgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sshIp = ApplicationPreferences.getPreferences().get(SharedPreferencesKeys.APPLICATIONPREFERENCES_RECEIVED_GLOBALSERVERIPADDRESS.getKey()).toString();
                    Intent sshIntent = new Intent(Intent.ACTION_VIEW,  Uri.parse("ssh://root@" + sshIp));
                    startActivity(sshIntent);
                }
            });

            final ImageButton webImgButton = (ImageButton)view.findViewById(R.id.webImageButton);
            webImgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String webIp = ApplicationPreferences.getPreferences().get(SharedPreferencesKeys.APPLICATIONPREFERENCES_RECEIVED_GLOBALSERVERIPADDRESS.getKey()).toString();
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,  Uri.parse("https://" + webIp));
                    startActivity(webIntent);
                }
            });

        }

    }


}
