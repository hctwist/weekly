package com.twisthenry8gmail.weeklyphoenix.data

import java.time.LocalDate

class TaskDay(val date: Long, val currentTasks: List<Task>, val overdueTasks: List<Task>) {

    fun isToday() = LocalDate.now().toEpochDay() == date

    fun hasCurrentTasks() = currentTasks.isNotEmpty()

    fun hasOverdueTasks() = overdueTasks.isNotEmpty()

    fun hasTasks() = hasCurrentTasks() || hasOverdueTasks()
}