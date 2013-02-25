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
            thermoServerStatus = myHttpClient.getThermoServerStatus();
        } catch (HomeAutomationException e) {
            showSettings(view.getContext());
            return null;
        } catch (ClientProtocolException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException e) {
            myHttpClient.useAnotherURL();
            try {
                thermoServerStatus = myHttpClient.getThermoServerStatus();
            } catch (HomeAutomationException e1) {
                showSettings(view.getContext());
                return null;
            } catch (IOException e1) {
                Log.e(LOG_TAG, e1.getMessage());
            }
        }

        if (thermoServerStatus != null) {
            TextView workroom = (TextView) view.findViewById(R.id.textView_roomtemp_workroom);
            TextView bedroom = (TextView) view.findViewById(R.id.textView_roomtemp_bedroom);
            TextView outside = (TextView) view.findViewById(R.id.textView_roomtemp_outside);
            TextView upperLobby = (TextView) view.findViewById(R.id.textView_roomtemp_upperlobby);
            workroom.setText(thermoServerStatus.getT280F5B8504000019() + "\u00b0C");
            bedroom.setText(thermoServerStatus.getT28B79F8504000082() + "\u00b0C");
            outside.setText(thermoServerStatus.getT28F82D850400001F() + "\u00b0C");
            upperLobby.setText(thermoServerStatus.getT28205B850400008B() + "\u00b0C");
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
}
