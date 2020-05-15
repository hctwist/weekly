package com.twisthenry8gmail.weeklyphoenix.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.twisthenry8gmail.weeklyphoenix.services.GoalTimerReceiver
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.view.ActivityMain

object NotificationHelper {

    const val GOAL_TIMER_CHANNEL_ID = "timer_channel"

    const val TIMED_GOAL_NOTIFICATION_ID = 0

    fun createChannels(context: Context) {

        val timerChannel = NotificationChannel(
            GOAL_TIMER_CHANNEL_ID,
            context.getString(R.string.channel_goal_timer),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        // TODO Set light color etc
        timerChannel.description = context.getString(R.string.channel_goal_timer_desc)

        context.getSystemService<NotificationManager>()?.createNotificationChannel(timerChannel)
    }

    fun showGoalTimerNotification(context: Context, startTime: Long) {

        val timeText =
            DateTimeUtil.showGoalTime(context, (System.currentTimeMillis() - startTime) / 1000)

        val intent = Intent(context, ActivityMain::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, GOAL_TIMER_CHANNEL_ID)
            .setSmallIcon(R.drawable.round_hourglass_empty_24)
            .setContentTitle(context.getString(R.string.notification_goal_timer_title))
            .setContentText(timeText)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        NotificationManagerCompat.from(context).notify(TIMED_GOAL_NOTIFICATION_ID, builder.build())
    }

    fun cancelGoalTimerNotification(context: Context) {

        NotificationManagerCompat.from(context)
            .cancel(TIMED_GOAL_NOTIFICATION_ID)
    }

    fun scheduleGoalTimerNotification(context: Context, startAt: Long) {

        // TODO Start alarm when phone is restarted
        val alarmManager = context.getSystemService<AlarmManager>()

        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            startAt,
            60 * 1000,
            GoalTimerReceiver.buildPendingIntent(context)
        )
    }

    fun cancelGoalTimerNotificationAlarm(context: Context) {

        val alarmManager = context.getSystemService<AlarmManager>()
        alarmManager?.cancel(
            GoalTimerReceiver.buildPendingIntent(context)
        )
    }
}