package com.hustaty.homeautomation;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import com.hustaty.homeautomation.receiver.ActivityBroadcastReceiver;
import com.hustaty.homeautomation.receiver.AlarmManagerBroadcastReceiver;
import com.hustaty.homeautomation.util.ApplicationPreferences;

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

    // logger entry
    private final static String LOG_TAG = MainActivity.class.getName();

    static Map<String, ?> preferences;

    private ActivityBroadcastReceiver activityBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        unregisterReceiver(this.activityBroadcastReceiver);
        Log.d(LOG_TAG, "#onPause(): unregistering UI broadcastReceiver");
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "#onResume(): registering UI broadcastReceiver");
        registerReceiver(this.activityBroadcastReceiver, new IntentFilter(AlarmManagerBroadcastReceiver.UI_LOCATION_UPDATE_INTENT));
    }

    /**
     * Exit application.
     */
    public void exit() {
        Log.i(LOG_TAG, "#MainActivity.exit(): ### EXITING APPLICATION ###");
        finish();
        System.runFinalizersOnExit(true);
        System.exit(0);
    }


    public void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        return;
    }

    /*
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

    /*
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

    /*
     * returns the tab view i.e. the tab icon and text
     */
    private View createTabView(final String text, final int id) {
        View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageDrawable(getResources().getDrawable(id));
        ((TextView) view.findViewById(R.id.tab_text)).setText(text);
        return view;
    }

}
