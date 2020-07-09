package com.twisthenry8gmail.weeklyphoenix.util

import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalSnapshot
import java.time.LocalDate

class GoalComparator : Comparator<GoalSnapshot> {

    private val now = LocalDate.now()

    override fun compare(g1: GoalSnapshot, g2: GoalSnapshot): Int {

        val comparedPriorities = getPriority(g2) - getPriority(g1)

        return if (comparedPriorities == 0) {

            compareContents(g1, g2)
        } else {

            comparedPriorities
        }
    }

    private fun getPriority(goal: GoalSnapshot): Int {

        return when {

            Goal.hasEnded(now, goal.endDate) -> 0
            !Goal.hasStarted(now, goal.startDate) -> 1
            Goal.isComplete(goal.progress, goal.target) -> 2
            else -> 3
        }
    }

    private fun compareContents(g1: GoalSnapshot, g2: GoalSnapshot): Int {

        return g1.sortOrder.compareTo(g2.sortOrder)
    }
}