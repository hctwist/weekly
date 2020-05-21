package com.twisthenry8gmail.weeklyphoenix.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.twisthenry8gmail.weeklyphoenix.WeeklyApplication
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper

class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            context?.let {

                val goalRepository = (it.applicationContext as WeeklyApplication).goalRepository

                if (goalRepository.isTiming()) {

                    val startTime = goalRepository.getTimingGoalStartTime()
                    NotificationHelper.showGoalTimerNotification(it, startTime)
                    NotificationHelper.scheduleNextGoalTimerNotification(it, startTime)
                }
            }
        }
    }
}