package com.twisthenry8gmail.weeklyphoenix.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.twisthenry8gmail.weeklyphoenix.WeeklyApplication
import com.twisthenry8gmail.weeklyphoenix.util.GoalTimerUtil
import com.twisthenry8gmail.weeklyphoenix.util.NotificationHelper

class GoalTimerService : Service() {

    private val handler = Handler()
    private val updateRunnable = Runnable {

        NotificationHelper.showGoalTimerNotification(applicationContext, getStartTime())
        postNotificationUpdate()
    }

    override fun onCreate() {

        if (getGoalRepository().isTiming()) {

            val startTime = getStartTime()

            startForeground(
                NotificationHelper.TIMED_GOAL_NOTIFICATION_ID,
                NotificationHelper.buildGoalTimerNotification(this, startTime)
            )

            handler.postDelayed(updateRunnable, GoalTimerUtil.calculateScheduleOffset(startTime))
        } else {

            stopSelf()
        }
    }

    private fun postNotificationUpdate() {

        handler.postDelayed(updateRunnable, 1000)
    }

    private fun getGoalRepository() = (application as WeeklyApplication).goalRepository

    private fun getStartTime() = getGoalRepository().getTimingGoalStartTime()

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onDestroy() {

        handler.removeCallbacks(updateRunnable)
    }
}