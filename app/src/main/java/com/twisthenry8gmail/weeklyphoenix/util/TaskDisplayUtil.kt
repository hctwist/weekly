package com.twisthenry8gmail.weeklyphoenix.util

import android.content.Context
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task
import com.twisthenry8gmail.weeklyphoenix.data.tasks.TaskSnapshot

object TaskDisplayUtil {

    fun getCardBackgroundColor(context: Context, taskSnapshot: TaskSnapshot): Int {

        return context.getColor(
            when (taskSnapshot.getState()) {

                TaskSnapshot.State.ACTIVE -> R.color.tasks_card_active

                TaskSnapshot.State.EMPTY -> R.color.tasks_card_empty

                TaskSnapshot.State.OVERDUE -> R.color.tasks_card_overdue
            }
        )
    }

    fun displayTasks(tasks: List<Task>?, numberToDisplay: Int): String {

        return if (tasks.isNullOrEmpty()) {

            ""
        } else {

            if (tasks.size <= numberToDisplay) {

                tasks.subList(0, tasks.size).joinToString("\n") { it.title }
            } else {

                tasks.subList(0, numberToDisplay - 1).joinToString("\n") { it.title }
                    .plus("\n...")
            }
        }
    }

    fun getTaskLines(tasks: List<Task>?, numberToDisplay: Int): List<String> {

        return if (tasks.isNullOrEmpty()) {

            emptyList()
        } else {

            if (tasks.size < numberToDisplay) {

                tasks.subList(0, tasks.size).map { it.title }
            } else {

                return tasks.subList(0, numberToDisplay - 1).map { it.title }.plus("...")
            }
        }
    }
}