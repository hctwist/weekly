package com.twisthenry8gmail.weeklyphoenix

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

class PreferenceLiveData<T> private constructor(
    private val preferences: SharedPreferences,
    private val key: String,
    private val extractor: (SharedPreferences) -> T
) : LiveData<T>() {

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

            if (key == this.key) {

                value = extractor(sharedPreferences)
            }
        }

    override fun onActive() {

        value = extractor(preferences)
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {

        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    class Builder(private val preferences: SharedPreferences, private val key: String) {

        fun buildForBoolean(defaultValue: Boolean): PreferenceLiveData<Boolean> {

            return PreferenceLiveData(preferences, key) {

                it.getBoolean(key, defaultValue)
            }
        }
    }
}