package com.twisthenry8gmail.weeklyphoenix.view.main

import com.twisthenry8gmail.weeklyphoenix.data.Goal
import java.time.LocalDate

object GoalAdapterDataBuilder : Comparator<GoalAdapter.Data> {

    fun build(header: GoalAdapter.Header, goals: List<Goal>): List<GoalAdapter.Data> {

        if (goals.isEmpty()) return emptyList()

        val data = ArrayList<GoalAdapter.Data>(1 + goals.size)
        data.add(GoalAdapter.Data.ofHeader(header))

        val date = LocalDate.now()

        goals.forEach {

            val type = when {

                it.isComplete() -> GoalAdapter.Data.Type.GOAL_COMPLETE
                LocalDate.ofEpochDay(it.startDate)
                    .isAfter(date) -> GoalAdapter.Data.Type.GOAL_SCHEDULED
                else -> GoalAdapter.Data.Type.GOAL
            }

            data.add(GoalAdapter.Data.ofGoal(type, it))
        }

        data.sortWith(this)

        return data
    }

    override fun compare(o1: GoalAdapter.Data?, o2: GoalAdapter.Data?): Int {

        // Disallow null values
        val d1 = o1!!
        val d2 = o2!!

        return if (d1.type == d2.type) {

            when (d1.type) {

                GoalAdapter.Data.Type.HEADER -> 0
                GoalAdapter.Data.Type.GOAL, GoalAdapter.Data.Type.GOAL_SCHEDULED, GoalAdapter.Data.Type.GOAL_COMPLETE -> compareGoal(
                    d1.asGoal(),
                    d2.asGoal()
                )
            }
        } else getOrder(d1.type) - getOrder(d2.type)
    }

    private fun compareGoal(g1: Goal, g2: Goal): Int {

        return g1.name.compareTo(g2.name)
    }

    private fun getOrder(type: GoalAdapter.Data.Type) = when (type) {

        GoalAdapter.Data.Type.HEADER -> 0
        GoalAdapter.Data.Type.GOAL -> 1
        GoalAdapter.Data.Type.GOAL_COMPLETE -> 2
        GoalAdapter.Data.Type.GOAL_SCHEDULED -> 3
    }
}