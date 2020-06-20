package com.twisthenry8gmail.weeklyphoenix.data.tasks

import java.time.LocalDate

class TaskSnapshot(val date: Long, val incompleteTasks: List<Task>) {

    fun isToday() = LocalDate.now().toEpochDay() == date

    fun inThePast() = date < LocalDate.now().toEpochDay()

    fun getState(): State {

        val now = LocalDate.now()
        val d = LocalDate.ofEpochDay(date)

        return when {

            incompleteTasks.isEmpty() -> State.EMPTY

            d.isBefore(now) -> State.OVERDUE

            else -> State.ACTIVE
        }
    }

    enum class State {

        ACTIVE, EMPTY, OVERDUE
    }
}