package com.twisthenry8gmail.weeklyphoenix.util

import android.content.Context
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import java.text.DecimalFormat
import kotlin.math.roundToInt

object GoalDisplayUtil {

    fun displayProgressValue(context: Context, type: Goal.Type, value: Long): String {

        return when (type) {

            Goal.Type.COUNTED -> value.toString()
            Goal.Type.TIMED -> displayGoalTime(context, value)
        }
    }

    fun displayProgressToTarget(context: Context, goal: Goal): String {

        val progress = displayProgressValue(context, goal.type, goal.progress)
        val target = displayProgressValue(context, goal.type, goal.target)

        return context.getString(R.string.goal_view_progress, progress, target)
    }

    fun displayEndDate(context: Context, goal: Goal): String {

        return if (goal.hasEndDate()) DateTimeUtil.displayDate(goal.endDate) else context.resources.getString(
            R.string.goal_no_end
        )
    }

    fun displayProgressPercentage(context: Context, goal: Goal): String {

        return context.getString(
            R.string.percentage_pattern,
            ((goal.progress.toDouble() / goal.target) * 100).roundToInt().toString()
        )
    }

    private val goalTimeFormatter = DecimalFormat("00")

    fun displayGoalTime(context: Context, seconds: Long): String {

        val totalMinutes = seconds / 60
        val hours = totalMinutes / 60
        return context.getString(
            R.string.time_pattern,
            goalTimeFormatter.format(hours),
            goalTimeFormatter.format(totalMinutes - hours * 60)
        )
    }

    fun displayScheduled(context: Context, goal: Goal): String {

        return context.getString(R.string.goal_scheduled, DateTimeUtil.displayDate(goal.startDate))
    }

    class SubtitleGenerator(val context: Context) {

        private val numbers by lazy { context.resources.getStringArray(R.array.numbers) }

        fun generateSubtitle(goal: Goal): String {

            when {
                goal.progress.toFloat() / goal.target >= 0.75F -> {

                    val left = when (goal.type) {

                        Goal.Type.COUNTED -> displayWrittenNumber(goal.target - goal.progress)
                        Goal.Type.TIMED -> displayGoalTime(context, goal.target - goal.progress)
                    }
                    return context.getString(R.string.goal_subtitle_pattern_left, left)
                }
                goal.progress.toFloat() / goal.target >= 0.5F -> {

                    return context.getString(R.string.goal_subtitle_pattern_over_half)
                }
                else -> {

                    val target = when (goal.type) {

                        Goal.Type.COUNTED -> displayWrittenNumber(goal.target)
                        Goal.Type.TIMED -> displayGoalTime(context, goal.target)
                    }
                    return context.getString(R.string.goal_subtitle_pattern_start, target)
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