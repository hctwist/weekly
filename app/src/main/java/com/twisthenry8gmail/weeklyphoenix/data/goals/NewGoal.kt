package com.twisthenry8gmail.weeklyphoenix.data.goals

import java.time.LocalDate

class NewGoal(
    val type: Goal.Type,
    val title: String,
    val target: Long,
    val reset: Goal.Reset,
    val increase: Long,
    val startDate: Long,
    val endDate: Long,
    val color: Int
) {

    fun buildGoal(sortOrder: Int) = Goal(
        0,
        type,
        title,
        0L,
        target,
        reset,
        Goal.getResetDateFrom(LocalDate.ofEpochDay(startDate), reset),
        increase,
        false,
        startDate,
        endDate,
        color,
        sortOrder
    )
}