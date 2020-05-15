package com.twisthenry8gmail.weeklyphoenix.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.view.ActivityMain

object GoalTimerUtil {

    fun calculateScheduleOffset(startTime: Long): Long {

        val minute = 60 * 1000
        return minute - (System.currentTimeMillis() - startTime) % minute
    }
}