package com.twisthenry8gmail.weeklyphoenix.data.goals

open class GoalSnapshot(
    val id: Int,
    val title: String,
    val progress: Long,
    val target: Long,
    val startDate: Long,
    val endDate: Long,
    val color: Int,
    val sortOrder: Int
)