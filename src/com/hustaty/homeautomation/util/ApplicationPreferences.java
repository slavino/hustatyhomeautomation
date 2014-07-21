package com.hustaty.homeautomation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.gms.games.GamesMetadata;

import java.util.Map;
import java.util.Set;

/**
 * User: hustasl
 * Date: 2/21/13
 * Time: 2:02 PM
 */
public class ApplicationPreferences {

    private static final String LOG_TAG = ApplicationPreferences.class.getName();

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

    public static void setValue(final Context context, String key, Object value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Set<?>) {
            try {
                Set<String> mySet = (Set<String>) value;
                editor.putStringSet(key, (Set<String>) value);
            } catch (ClassCastException classCastException) {
                LogUtil.appendLog(LOG_TAG);
            }
        }
        editor.commit();
    }
}
