package com.hustaty.homeautomation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import com.hustaty.homeautomation.util.LogUtil;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * User: hustasl
 * Date: 2/20/13
 * Time: 12:44 PM
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    // logger entry
    private static final String LOG_TAG = SettingsActivity.class.getName();

    public static final int REQUEST_CODE_FOR_SETTINGS = 31;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        SharedPreferences.Editor editor = sp.edit();

        try {
            String macAddr, androidId;

            WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();

            macAddr = wifiInf.getMacAddress();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            if(macAddr == null) {
                macAddr = androidId;
            }

            UUID deviceUuid = new UUID(androidId.hashCode(), macAddr.hashCode());
            editor.putString("deviceID",deviceUuid.toString());

        } catch (Exception e) {
            if(e != null) {
                Log.e(LOG_TAG, e.getMessage());
                LogUtil.appendLog(LOG_TAG + "#onCreate(): " + e.getMessage());
            } else {
                //WTF happened Google?
                Log.e(LOG_TAG, "NULL Exception");
                LogUtil.appendLog(LOG_TAG + "#onCreate(): NULL Exception");
            }
        } finally {
            editor.commit();
            findPreference("deviceID").setSummary(sp.getString("deviceID", getResources().getString(R.string.deviceID)));
            findPreference("SECUPDATETIME").setSummary(sp.getString("SECUPDATETIME", getResources().getString(R.string.unknown_value)));
        }
        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {

        if ("mail_preference_key".equals(key)) {
            // Search for a valid mail pattern
            String pattern = "mailpattern";
            String value = sp.getString(key, null);
            if (!Pattern.matches(pattern, value)) {
                // The value is not a valid email address.
                // Do anything like advice the user or change the value
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(key, Activity.RESULT_OK);
        setResult(Activity.RESULT_OK, resultIntent);

    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "#onStop()");
        LogUtil.appendLog(LOG_TAG + "#onStop()");
        super.onStop();
    }
}
