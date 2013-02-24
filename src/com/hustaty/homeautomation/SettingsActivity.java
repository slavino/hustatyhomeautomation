package com.hustaty.homeautomation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * User: hustasl
 * Date: 2/20/13
 * Time: 12:44 PM
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    // logger entry
    private final static String LOG_TAG = SettingsActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        SharedPreferences.Editor editor = sp.edit();

        try {
            final String macAddr, androidId;

            WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();

            macAddr = wifiInf.getMacAddress();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());
            editor.putString("deviceID",deviceUuid.toString());

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            editor.commit();
            findPreference("deviceID").setSummary(sp.getString("deviceID", getResources().getString(R.string.deviceID)));
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (key.equals("mail_preference_key")) {
            // Search for a valid mail pattern
            String pattern = "mailpattern";
            String value = sp.getString(key, null);
            if (!Pattern.matches(pattern, value)) {
                // The value is not a valid email address.
                // Do anything like advice the user or change the value
            }
        }
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "#onStop()");
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
    }
}