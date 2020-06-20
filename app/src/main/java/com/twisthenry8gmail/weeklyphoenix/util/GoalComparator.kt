package com.twisthenry8gmail.weeklyphoenix.util

import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import java.time.LocalDate

class GoalComparator : Comparator<Goal> {

    private val now = LocalDate.now()

    override fun compare(g1: Goal, g2: Goal): Int {

        val comparedPriorities = getPriority(g2) - getPriority(g1)

        return if (comparedPriorities == 0) {

            compareContents(g1, g2)
        } else {

            comparedPriorities
        }
    }

    private fun getPriority(goal: Goal): Int {

        return when {

            goal.hasEnded(now) -> 0
            !goal.hasStarted(now) -> 1
            goal.isComplete() -> 2
            else -> 3
        }
    }

    private fun compareContents(g1: Goal, g2: Goal): Int {

        return g1.sortOrder.compareTo(g2.sortOrder)
    }
}