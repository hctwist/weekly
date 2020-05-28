package com.twisthenry8gmail.weeklyphoenix.util

import android.content.res.Resources
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import java.text.DecimalFormat
import kotlin.math.roundToInt

object GoalDisplayUtil {

    fun displayProgressValue(resources: Resources, type: Goal.Type, value: Long): String {

        return when (type) {

            Goal.Type.COUNTED -> value.toString()
            Goal.Type.TIMED -> displayGoalTime(resources, value)
        }
    }

    fun displayProgressToTarget(resources: Resources, goal: Goal): String {

        val progress = displayProgressValue(resources, goal.type, goal.progress)
        val target = displayProgressValue(resources, goal.type, goal.target)

        return resources.getString(R.string.goal_view_progress, progress, target)
    }

    fun displayReset(resources: Resources, goal: Goal): String {

        val preset = Goal.ResetPreset.values()
            .find { goal.reset.isPreset(it) }

        return resources.getString(preset!!.displayNameRes)
    }

    fun displayEndDate(resources: Resources, endDate: Long): String {

        return if (GoalPropertyUtil.hasEndDate(endDate)) DateTimeUtil.displayDate(endDate) else resources.getString(
            R.string.goal_no_end
        )
    }

    fun displayProgressPercentage(resources: Resources, goal: Goal): String {

        return resources.getString(
            R.string.percentage_pattern,
            ((goal.progress.toDouble() / goal.target) * 100).roundToInt().toString()
        )
    }

    private val goalTimeFormatter = DecimalFormat("00")

    fun displayGoalTime(resources: Resources, seconds: Long): String {

        val totalMinutes = seconds / 60
        val hours = totalMinutes / 60
        return resources.getString(
            R.string.time_pattern,
            goalTimeFormatter.format(hours),
            goalTimeFormatter.format(totalMinutes - hours * 60)
        )
    }

    class SubtitleGenerator(private val resources: Resources) {

        private val numbers by lazy { resources.getStringArray(R.array.numbers) }

        fun generateSubtitle(goal: Goal): String {

            when {
                goal.progress.toFloat() / goal.target >= 0.75F -> {

                    val left = when (goal.type) {

                        Goal.Type.COUNTED -> displayWrittenNumber(goal.target - goal.progress)
                        Goal.Type.TIMED -> displayGoalTime(
                            resources,
                            goal.target - goal.progress
                        )
                    }
                    return resources.getString(R.string.goal_subtitle_pattern_left, left)
                }
                goal.progress.toFloat() / goal.target >= 0.5F -> {

                    return resources.getString(R.string.goal_subtitle_pattern_over_half)
                }
                else -> {

                    val target = when (goal.type) {

                        Goal.Type.COUNTED -> displayWrittenNumber(goal.target)
                        Goal.Type.TIMED -> displayGoalTime(resources, goal.target)
                    }
                    return resources.getString(R.string.goal_subtitle_pattern_start, target)
                }
            }
        }

        private fun displayWrittenNumber(number: Long): String {

            return if (number > numbers.size) {

                number.toString()
            } else {

                numbers[(number - 1).toInt()]
            }
        }
    }
}