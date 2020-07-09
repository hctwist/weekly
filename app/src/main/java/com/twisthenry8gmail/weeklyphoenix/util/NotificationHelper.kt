package com.twisthenry8gmail.weeklyphoenix.util

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.view.ActivityMain

object NotificationHelper {

    private const val GOAL_TIMER_CHANNEL_ID = "timer_channel"
    const val TIMED_GOAL_NOTIFICATION_ID = 1

    fun createChannels(context: Context) {

        val timerChannel = NotificationChannel(
            GOAL_TIMER_CHANNEL_ID,
            context.getString(R.string.channel_goal_timer),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        timerChannel.description = context.getString(R.string.channel_goal_timer_desc)

        context.getSystemService<NotificationManager>()?.createNotificationChannel(timerChannel)
    }

    fun buildGoalTimerNotification(context: Context, startTime: Long): Notification {

        val timeText =
            GoalDisplayUtil.displayGoalTime(
                context.resources,
                (System.currentTimeMillis() - startTime) / 1000
            )

        val contentIntent = Intent(context, ActivityMain::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val contentPendingIntent = PendingIntent.getActivity(context, 1, contentIntent, 0)

        return NotificationCompat.Builder(context, GOAL_TIMER_CHANNEL_ID)
            .setSmallIcon(R.drawable.timer_notification)
            .setColor(context.getColor(R.color.color_primary))
            .setContentTitle(context.getString(R.string.notification_goal_timer_title))
            .setContentText(timeText)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    fun showGoalTimerNotification(context: Context, startTime: Long) {

        val notification = buildGoalTimerNotification(context, startTime)
        NotificationManagerCompat.from(context).notify(TIMED_GOAL_NOTIFICATION_ID, notification)
    }

    fun cancelGoalTimerNotification(context: Context) {

        NotificationManagerCompat.from(context).cancel(TIMED_GOAL_NOTIFICATION_ID)
    }
}