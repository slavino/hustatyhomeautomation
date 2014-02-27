package com.hustaty.homeautomation.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.hustaty.homeautomation.R;

/**
 * Created by user on 2/26/14.
 */
public class HotWaterWidgetProvider extends AppWidgetProvider {

    public static final String HOTWATER_WIDGET_CLICK = "com.hustaty.homeautomation.HOTWATER_WIDGET_CLICK";
    public static final String HOTWATER_STATE = "com.hustaty.homeautomation.HOTWATER_STATE";

    private static final String LOG_TAG = HotWaterWidgetProvider.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "#onReceive() started");
        super.onReceive(context, intent);

        RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.waterwidget);
        ComponentName thisWidget = new ComponentName(context, HotWaterWidgetProvider.class);

        if (HotWaterWidgetProvider.HOTWATER_WIDGET_CLICK.equals(intent.getAction())) {

            //toggle widget according to current state
            if (true /*hotwater is on*/) {
                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_on_state);
            } else {
                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_off_state);
            }

            Intent clickIntent = new Intent(HotWaterWidgetProvider.HOTWATER_WIDGET_CLICK);

            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
            updateView.setOnClickPendingIntent(R.id.hotwater_widget_imagebutton, pendingIntentClick);

            Log.d(LOG_TAG, "Setting pending intent");

            //get Appwidget manager and change widget image
            AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, updateView);

        } else if(HOTWATER_STATE.equals(intent.getAction())) {
            String hotwaterinfo = intent.getStringExtra(HOTWATER_STATE);
            Log.d(LOG_TAG, "--> Hotwater state is " + hotwaterinfo);
            if("0".equals(hotwaterinfo))  {
                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_off_state);
            } else if("1".equals(hotwaterinfo)) {
                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_off_state);
            } else {
                updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_unknown_state);
            }
        }

        //trigger update through AppWidgetManager
        AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, updateView);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOG_TAG, "#onUpdate() started");
        RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.waterwidget);

        appWidgetManager.updateAppWidget(appWidgetIds, updateView);

        if (true /* hotwater is ON */) {
            updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_on_state);
        } else {
            updateView.setImageViewResource(R.id.hotwater_widget_imagebutton, R.drawable.shower_widget_off_state);
        }

        Intent clickIntent = new Intent(HotWaterWidgetProvider.HOTWATER_WIDGET_CLICK);

        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
        updateView.setOnClickPendingIntent(R.id.hotwater_widget_imagebutton, pendingIntentClick);

        Log.d(LOG_TAG, "Setting pending intent");

        for(int id : appWidgetIds){
            appWidgetManager.updateAppWidget(id, updateView);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(LOG_TAG, "#onEnabled() started");

        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOG_TAG, "#onDisabled() started");

        super.onDisabled(context);
    }
}
