package com.hustaty.homeautomation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.http.MyHttpClient;
import com.hustaty.homeautomation.model.ArduinoThermoServerStatus;
import com.hustaty.homeautomation.util.ApplicationPreferences;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends Activity {

    // logger entry
    private final static String LOG_TAG = MainActivity.class.getName();

    static Map<String, ?> preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        //load preferences
        MainActivity.preferences = ApplicationPreferences.getPreferences(this);

        if(MainActivity.preferences == null
                || MainActivity.preferences.size() == 0) {
            showSettings();
            return;
        }

		MyHttpClient myHttpClient = new MyHttpClient(getApplicationContext());
        ArduinoThermoServerStatus thermoServerStatus = null;
		try {
			thermoServerStatus = myHttpClient.getThermoServerStatus();
		} catch (HomeAutomationException e) {
            showSettings();
            return;
		} catch (ClientProtocolException e) {
            Log.e(LOG_TAG, e.getMessage());
		} catch (IOException e) {
            myHttpClient.useAnotherURL();
            try {
                thermoServerStatus = myHttpClient.getThermoServerStatus();
            } catch (HomeAutomationException e1) {
                showSettings();
                return;
            } catch (IOException e1) {
                Log.e(LOG_TAG, e1.getMessage());
            }
		}

        if(thermoServerStatus != null) {
            TextView workroom = (TextView) findViewById(R.id.textView_roomtemp_workroom);
            TextView bedroom = (TextView) findViewById(R.id.textView_roomtemp_bedroom);
            TextView outside = (TextView) findViewById(R.id.textView_roomtemp_outside);
            TextView upperLobby = (TextView) findViewById(R.id.textView_roomtemp_upperlobby);
            workroom.setText(thermoServerStatus.getT280F5B8504000019() + "\u00b0C");
            bedroom.setText(thermoServerStatus.getT28B79F8504000082() + "\u00b0C");
            outside.setText(thermoServerStatus.getT28F82D850400001F() + "\u00b0C");
            upperLobby.setText(thermoServerStatus.getT28205B850400008B() + "\u00b0C");
        }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.close:
                exit();
                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Exit application.
     */
    private void exit() {
        Log.i(LOG_TAG, "#MainActivity.exit(): ### EXITING APPLICATION ###");
        finish();
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    /**
     * Show settings panel.
     */
    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        return;
    }

}
