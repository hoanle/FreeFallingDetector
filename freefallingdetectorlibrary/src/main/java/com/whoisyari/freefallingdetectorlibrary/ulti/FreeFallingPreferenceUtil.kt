package com.whoisyari.freefallingdetector

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.whoisyari.freefallingdetectorlibrary.R

/**
 * Class to handle preference of lib
 */
internal class FreeFallingPreferenceUtil {

    companion object {
        private const val INTERVAL_TIME = "interval_to_detect"
        private const val MINIMUM_SPEED_TO_DECIDE = "minimum_speed_to_decide"
        private const val APPLICATION_COMPONENT = "application_component"
        private const val ALLOW_FOREGROUND = "allow_foreground"

        fun getIntervalTime(context: Context): Int {
            val preferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sdk_preferences),
                MODE_PRIVATE
            )
            return preferences.getInt(INTERVAL_TIME, 100)
        }

        fun setIntervalTime(context: Context, interval: Int) {
            val preferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sdk_preferences),
                MODE_PRIVATE
            )
            preferences.edit().putInt(INTERVAL_TIME, interval).apply();
        }

        fun getMinimumSpeed(context: Context): Int {
            val preferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sdk_preferences),
                MODE_PRIVATE
            )
            return preferences.getInt(MINIMUM_SPEED_TO_DECIDE, 10000)
        }

        fun setMinimumSpeed(context: Context, interval: Int) {
            val preferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sdk_preferences),
                MODE_PRIVATE
            )
            preferences.edit().putInt(MINIMUM_SPEED_TO_DECIDE, interval).apply();
        }

        fun getApplicationComponentName(context: Context): String {
            val preferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sdk_preferences),
                MODE_PRIVATE
            )
            return preferences.getString(APPLICATION_COMPONENT, "")!!
        }

        fun setApplicationComponentName(context: Context, componentName: String) {
            val preferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sdk_preferences),
                MODE_PRIVATE
            )
            preferences.edit().putString(APPLICATION_COMPONENT, componentName).apply();
        }

        fun isAllowedForeground(context: Context): Boolean {
            val preferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sdk_preferences),
                MODE_PRIVATE
            )
            return preferences.getBoolean(ALLOW_FOREGROUND, true)
        }

        fun setAllowedForeground(context: Context, allow: Boolean) {
            val preferences: SharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sdk_preferences),
                MODE_PRIVATE
            )
            preferences.edit().putBoolean(APPLICATION_COMPONENT, allow).apply();
        }
    }
}