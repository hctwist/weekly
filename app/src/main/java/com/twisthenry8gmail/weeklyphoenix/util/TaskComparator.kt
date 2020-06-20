package com.twisthenry8gmail.weeklyphoenix.util

import com.twisthenry8gmail.weeklyphoenix.data.tasks.Task

class TaskComparator : Comparator<Task> {

    override fun compare(o1: Task, o2: Task): Int {

        val completeCompare = o1.complete.compareTo(o2.complete)

        return if (completeCompare == 0) {

            o1.dateCreated.compareTo(o2.dateCreated)
        } else {

            completeCompare
        }
    }
}