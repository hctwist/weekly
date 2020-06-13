package com.twisthenry8gmail.weeklyphoenix.util

import com.twisthenry8gmail.weeklyphoenix.data.Task

object TaskDisplayUtil {

    fun displayTasks(tasks: List<Task>?): String {

        return tasks?.joinToString("\n") { it.title } ?: ""
    }
}