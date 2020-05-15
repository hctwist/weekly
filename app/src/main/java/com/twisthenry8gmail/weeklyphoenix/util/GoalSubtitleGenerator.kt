package com.twisthenry8gmail.weeklyphoenix.util

import android.content.Context
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal

class GoalSubtitleGenerator(val context: Context) {

    private val numbers = context.resources.getStringArray(R.array.numbers)

    fun generateSubtitle(goal: Goal): String {

        if (goal.progress.toFloat() / goal.target >= 0.75F) {

            val left = when (goal.type) {

                Goal.Type.COUNTED -> displayNumber(goal.target - goal.progress)
                Goal.Type.TIMED -> DateTimeUtil.showGoalTime(context, goal.target - goal.progress)
            }
            return context.getString(R.string.goal_subtitle_pattern_left, left)
        } else if (goal.progress.toFloat() / goal.target >= 0.5F) {

            return context.getString(R.string.goal_subtitle_pattern_over_half)
        } else {

            val target = when (goal.type) {

                Goal.Type.COUNTED -> displayNumber(goal.target)
                Goal.Type.TIMED -> DateTimeUtil.showGoalTime(context, goal.target)
            }
            return context.getString(R.string.goal_subtitle_pattern_start, target)
        }
    }

    private fun displayNumber(number: Long): String {

        return if (number > numbers.size) {

            number.toString()
        } else {

            numbers[(number - 1).toInt()]
        }
    }
}