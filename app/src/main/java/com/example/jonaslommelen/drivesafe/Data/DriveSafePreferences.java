package com.example.jonaslommelen.drivesafe.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.jonaslommelen.drivesafe.R;

public class DriveSafePreferences {

    public static String getWeight(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String weightKey = context.getString(R.string.pref_weight_key);
        String defaultWeight = context.getString(R.string.pref_weight_default);
        return prefs.getString(weightKey, defaultWeight);
    }

    public static String getHeight(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String heightKey = context.getString(R.string.pref_height_key);
        String defaultHeight = context.getString(R.string.pref_height_default);
        return prefs.getString(heightKey, defaultHeight);
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

    public static boolean isMale(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForGender = context.getString(R.string.pref_gender_key);
        String defaultGender = context.getString(R.string.pref_gender_default);
        String preferredGender = prefs.getString(keyForGender, defaultGender);
        String gender = context.getString(R.string.pref_gender_male);
        if (gender.equals(preferredGender)) {
            return true;
        } else {
            return false;
        }
    }
}
