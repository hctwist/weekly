package com.twisthenry8gmail.weeklyphoenix.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.twisthenry8gmail.weeklyphoenix.WeeklyApplication
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper

class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            context?.startForegroundService(Intent(context, GoalTimerService::class.java))
        }
    }
}