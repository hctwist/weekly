package com.twisthenry8gmail.weeklyphoenix.util

object GoalTimerUtil {

    fun calculateScheduleOffset(startTime: Long): Long {

        val minute = 60 * 1000
        return minute - (System.currentTimeMillis() - startTime) % minute
    }
}