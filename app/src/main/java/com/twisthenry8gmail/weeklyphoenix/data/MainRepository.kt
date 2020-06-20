package com.twisthenry8gmail.weeklyphoenix.data

import android.content.SharedPreferences

class MainRepository(private val sharedPreferences: SharedPreferences) {

    companion object {

        const val FIRST_TIME = "first_time"
    }

    fun isFirstTime(): Boolean {

        return sharedPreferences.getBoolean(FIRST_TIME, true)
    }

    fun setNotFirstTime() {

        sharedPreferences.edit().putBoolean(FIRST_TIME, false).apply()
    }
}