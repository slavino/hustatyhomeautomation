package com.hustaty.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hustaty.homeautomation.exception.HomeAutomationException;
import com.hustaty.homeautomation.http.MyHttpClient;
import com.hustaty.homeautomation.receiver.ActivityBroadcastReceiver;
import com.hustaty.homeautomation.receiver.AlarmManagerBroadcastReceiver;
import com.hustaty.homeautomation.util.ApplicationPreferences;
import com.hustaty.homeautomation.util.CommonUtil;
import com.hustaty.homeautomation.util.LogUtil;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private static final String ERROR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";

    protected String SENDER_ID;// = CommonUtil.getSenderId(this.getApplicationContext());

    GoogleCloudMessaging gcm;
    Context context;
    String regid;

    /* Tab identifiers */
    static String TAB_A = "Status";
    static String TAB_B = "Heating";
    static String TAB_C = "Hot Water";

    TabHost mTabHost;

    StatusFragment statusFragment;
    HeatingFragment heatingFragment;
    HotwaterFragment hotwaterFragment;

    AsyncTask<Void, Void, Void> mRegisterTask;

    // logger entry
    private final static String LOG_TAG = MainActivity.class.getName();

    static Map<String, ?> preferences;

    private ActivityBroadcastReceiver activityBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        TAB_A = getResources().getString(R.string.tabname_status, "Status");
        TAB_B = getResources().getString(R.string.tabname_heating, "Heating");
        TAB_C = getResources().getString(R.string.tabname_hotwater, "Hot Water");

        //load preferences
        MainActivity.preferences = ApplicationPreferences.getPreferences(this);

        if (MainActivity.preferences == null
                || MainActivity.preferences.size() == 0) {
            showSettings();
            return;
        }

        statusFragment = new StatusFragment();
        heatingFragment = new HeatingFragment();
        hotwaterFragment = new HotwaterFragment();

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setOnTabChangedListener(listener);
        mTabHost.setup();

        initializeTab();

        this.activityBroadcastReceiver = new ActivityBroadcastReceiver(this);
        registerReceiver(activityBroadcastReceiver, new IntentFilter(AlarmManagerBroadcastReceiver.UI_LOCATION_UPDATE_INTENT));

        //EXPERIMENTAL
        // Check device for Play Services APK.
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            context = getApplicationContext();
            SENDER_ID = CommonUtil.getSenderId(context);
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground(5);
            }

        }


        try {
            AlarmManagerBroadcastReceiver alarmManagerBroadcastReceiver = new AlarmManagerBroadcastReceiver();
            alarmManagerBroadcastReceiver.cancelAlarm(this);
            alarmManagerBroadcastReceiver.setAlarm(this);
            Log.d(LOG_TAG, "#onCreate(): cancelling and setting Alarms");
            LogUtil.appendLog(LOG_TAG + "#onCreate():  cancelling and setting Alarms");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            LogUtil.appendLog(LOG_TAG + "#onCreate(): " + e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    @Override
    protected void onPause() {
        if (this.activityBroadcastReceiver != null) {
            unregisterReceiver(this.activityBroadcastReceiver);
        } else {
            this.activityBroadcastReceiver = new ActivityBroadcastReceiver(this);
            registerReceiver(activityBroadcastReceiver, new IntentFilter(AlarmManagerBroadcastReceiver.UI_LOCATION_UPDATE_INTENT));
            unregisterReceiver(this.activityBroadcastReceiver);
        }
        Log.d(LOG_TAG, "#onPause(): unregistering UI broadcastReceiver");
        LogUtil.appendLog(LOG_TAG + "#onPause(): unregistering UI broadcastReceiver");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "#onResume(): registering UI broadcastReceiver");
        LogUtil.appendLog(LOG_TAG + "#onResume(): registering UI broadcastReceiver");
        if (this.activityBroadcastReceiver == null) {
            this.activityBroadcastReceiver = new ActivityBroadcastReceiver(this);
        }
        registerReceiver(this.activityBroadcastReceiver, new IntentFilter(AlarmManagerBroadcastReceiver.UI_LOCATION_UPDATE_INTENT));
        checkPlayServices();
    }

    /**
     * Exit application.
     */
    public void exit() {
        Log.i(LOG_TAG, "#exit(): ### EXITING APPLICATION ###");
        finish();
        System.runFinalizersOnExit(true);
        System.exit(0);
    }


    /**
     * Show settings activity.
     */
    public void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        return;
    }

    /**
     * Initialize the tabs and set views and identifiers for the tabs
     */
    public void initializeTab() {

        TabHost.TabSpec spec = mTabHost.newTabSpec(TAB_A);
        mTabHost.setCurrentTab(-3);

        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(TAB_A, R.drawable.info1));
        mTabHost.addTab(spec);


        spec = mTabHost.newTabSpec(TAB_B);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(TAB_B, R.drawable.tab_icon2));
        mTabHost.addTab(spec);

        spec = mTabHost.newTabSpec(TAB_C);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(android.R.id.tabcontent);
            }
        });
        spec.setIndicator(createTabView(TAB_C, R.drawable.hotwater1));
        mTabHost.addTab(spec);
    }

    /**
     * TabChangeListener for changing the tab when one of the tabs is pressed
     */
    TabHost.OnTabChangeListener listener = new TabHost.OnTabChangeListener() {
        public void onTabChanged(String tabId) {
            /*Set current tab..*/
            if (tabId.equals(TAB_A)) {
                pushFragments(tabId, statusFragment);
            } else if (tabId.equals(TAB_B)) {
                pushFragments(tabId, heatingFragment);
            } else if (tabId.equals(TAB_C)) {
                pushFragments(tabId, hotwaterFragment);
            }
        }
    };

    /*
     * adds the fragment to the FrameLayout
     */
    public void pushFragments(String tag, Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        ft.replace(android.R.id.tabcontent, fragment);
        ft.commit();
    }

    /**
     * returns the tab view i.e. the tab icon and text
     */
    private View createTabView(final String text, final int id) {
        View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageDrawable(getResources().getDrawable(id));
        ((TextView) view.findViewById(R.id.tab_text)).setText(text);
        return view;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(LOG_TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final int retryCount) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                if(retryCount-1 < 0) {
                    return ERROR_SERVICE_NOT_AVAILABLE;
                }
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                    Log.i(LOG_TAG, "#registerInBackground():#doInBackground(): REGID for GCM " + regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e(LOG_TAG, ex.getMessage());
                    if(ERROR_SERVICE_NOT_AVAILABLE.equalsIgnoreCase(ex.getMessage())
                        || "AUTHENTICATION_FAILED".equalsIgnoreCase(ex.getMessage()) ) {
                        registerInBackground(retryCount-1);
                    }
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object msg) {
                //mDisplay.append(msg + "\n");
                LogUtil.appendLog(LOG_TAG+": "+msg);
            }

        }.execute(null, null, null);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        LogUtil.appendLog(LOG_TAG+ ": ########## REGID is " + regid);
        Log.i(LOG_TAG, "DeviceID: " + ApplicationPreferences.getPreferences(this.getApplicationContext()).get("deviceID") + " has GCM RegID: " + regid);
        MyHttpClient myHttpClient = new MyHttpClient(context);
        try {
            myHttpClient.addGCMDeviceEntry(ApplicationPreferences.getPreferences(this.getApplicationContext()).get("deviceID").toString(), regid, true);
        } catch (IOException e) {
            Log.e(LOG_TAG, "#sendRegistrationIdToBackend(): " + e.getMessage());
        } catch (HomeAutomationException e) {
            Log.e(LOG_TAG, "#sendRegistrationIdToBackend(): " + e.getMessage());
        }
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
