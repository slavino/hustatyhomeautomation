package com.hustaty.homeautomation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
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
import com.google.android.gcm.GCMRegistrar;
import com.hustaty.homeautomation.receiver.ActivityBroadcastReceiver;
import com.hustaty.homeautomation.receiver.AlarmManagerBroadcastReceiver;
import com.hustaty.homeautomation.util.ApplicationPreferences;
import com.hustaty.homeautomation.util.CommonUtil;
import com.hustaty.homeautomation.util.GCMServerUtil;
import com.hustaty.homeautomation.util.LogUtil;

import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

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
        if(this.activityBroadcastReceiver != null) {
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
        if(this.activityBroadcastReceiver == null) {
            this.activityBroadcastReceiver = new ActivityBroadcastReceiver(this);
        }
        registerReceiver(this.activityBroadcastReceiver, new IntentFilter(AlarmManagerBroadcastReceiver.UI_LOCATION_UPDATE_INTENT));
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

    private void gcmStuff() {
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        setContentView(R.layout.main);
//        mDisplay = (TextView) findViewById(R.id.display);
        //registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtil.DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);

        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, (String)ApplicationPreferences.getPreferences().get("gcm_sender_id"));
        } else {

            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                //mDisplay.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                GCMServerUtil.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }
}
