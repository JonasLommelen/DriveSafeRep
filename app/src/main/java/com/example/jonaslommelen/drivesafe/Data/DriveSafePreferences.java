package com.example.jonaslommelen.drivesafe.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.jonaslommelen.drivesafe.R;

public class DriveSafePreferences {

    public static String getPreferredLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForLanguage = context.getString(R.string.pref_language_key);
        String defaultLanguage = context.getString(R.string.pref_language_english);
        return prefs.getString(keyForLanguage, defaultLanguage);
    }

    public static boolean isAbv(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_default);
        String preferredUnits = prefs.getString(keyForUnits, defaultUnits);
        String metric = context.getString(R.string.pref_units_abv);
        if (metric.equals(preferredUnits)) {
            return true;
        } else {
            return false;
        }
    }
}
