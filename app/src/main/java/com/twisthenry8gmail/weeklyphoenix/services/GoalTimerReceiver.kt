package com.twisthenry8gmail.weeklyphoenix.services

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.twisthenry8gmail.weeklyphoenix.WeeklyApplication
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper

open class GoalTimerReceiver : BroadcastReceiver() {

    companion object {

        fun buildPendingIntent(context: Context): PendingIntent {

            val receiverIntent = Intent(context, GoalTimerReceiver::class.java)
            return PendingIntent.getBroadcast(context, 0, receiverIntent, 0)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        context?.let {

            val startTime =
                (it.applicationContext as WeeklyApplication).goalRepository.getTimingGoalStartTime()
            NotificationHelper.showGoalTimerNotification(it, startTime)
            NotificationHelper.scheduleNextGoalTimerNotification(it, startTime)
        }
    }
}