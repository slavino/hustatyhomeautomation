package com.hustaty.homeautomation.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * User: hustasl
 * Date: 3/28/13
 * Time: 5:17 PM
 */
public class CommonUtil {

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION = "com.hustaty.homeautomation.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    /**
     * Google API project id registered to use GCM.
     */
    private static String SENDER_ID = "";

    public static final String getSenderId(final Context context) {
        if (SENDER_ID == null || SENDER_ID.isEmpty()) {
            SENDER_ID = PreferenceManager.getDefaultSharedPreferences(context).getString("gcm_sender_id", "0");
        }
        return SENDER_ID;
    }

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    /**
     * Custom method to format date for Xively URL
     * @param amountOfDays reflecting current time and shifting days +/-
     * @return formated string to be embedded to URL
     */
    private static String getHistoricalFormatedDateISO8601(int amountOfDays) {
        TimeZone tz = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance();

        //Add/subtract X days
        calendar.add(Calendar.DAY_OF_MONTH, amountOfDays);

        calendar.set(Calendar.SECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        sdf.setTimeZone(tz);

        return sdf.format(calendar.getTime());
    }

    /**
     * Wrapper with null value for color.
     * @param feedId
     * @param dataStreamName
     * @param chartName
     * @return
     */
    public static String getXivelyDatastreamURL(String feedId, String dataStreamName, String chartName) {
        return getXivelyDatastreamURL(feedId, dataStreamName, chartName, null);
    }

    /**
     * URL assembly for image chart.
     * @param feedId
     * @param dataStreamName
     * @param chartName
     * @param color common hexa code web #000000
     * @return
     */
    public static String getXivelyDatastreamURL(String feedId, String dataStreamName, String chartName, String color) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://api.xively.com/v2/feeds/" + feedId + "/datastreams/");
        stringBuilder.append(dataStreamName);
        stringBuilder.append(".png?w=1200&h=450&b=true&g=true&t=");
        stringBuilder.append(chartName);
        if(color != null) {
            stringBuilder.append("&c=" + color);
        }
        stringBuilder.append("&start=");
        stringBuilder.append(getHistoricalFormatedDateISO8601(-1));
        stringBuilder.append("&end=");
        stringBuilder.append(getHistoricalFormatedDateISO8601(0));
        stringBuilder.append("&timezone=Berlin");
        return stringBuilder.toString();
    }


}
