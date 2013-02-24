package com.hustaty.homeautomation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * User: hustasl
 * Date: 2/21/13
 * Time: 2:02 PM
 */
public class ApplicationPreferences {

    public static Map<String, ?> preferences;

    public static Map<String, ?> getPreferences(final Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, ?> map = settings.getAll();
        ApplicationPreferences.preferences = map;
        return map;
    }

    public static Map<String, ?> getPreferences() {
        return preferences;
    }
}
