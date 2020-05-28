package com.twisthenry8gmail.weeklyphoenix.util

import com.twisthenry8gmail.weeklyphoenix.data.Goal

object GoalPropertyUtil {

    fun hasEndDate(endDate: Long) = endDate >= 0
}