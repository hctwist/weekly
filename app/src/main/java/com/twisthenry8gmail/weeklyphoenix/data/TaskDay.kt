package com.twisthenry8gmail.weeklyphoenix.data

import java.time.LocalDate

// TODO Is this class even needed?
class TaskDay(val date: Long, val tasks: List<Task>) {

    fun isToday() = LocalDate.now().toEpochDay() == date

    fun hasTasks() = tasks.isNotEmpty()
}