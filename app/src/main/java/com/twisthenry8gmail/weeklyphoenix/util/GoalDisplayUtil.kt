package com.twisthenry8gmail.weeklyphoenix.util

import android.content.Context
import android.content.res.Resources
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import java.text.DecimalFormat
import java.time.temporal.ChronoUnit
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

    fun displayReset(resources: Resources, reset: Goal.Reset): String {

        val preset = Goal.ResetPreset.values()
            .find { reset.isPreset(it) }

        return resources.getString(preset!!.displayNameRes)
    }

    fun displayHistoryDate(goal: Goal, date: Long): String {

        return when {

            goal.reset.unit == ChronoUnit.DAYS && goal.reset.multiple == 1L -> {

                DateTimeUtil.displayShortDay(date)
            }

            goal.reset.unit == ChronoUnit.YEARS -> {

                DateTimeUtil.displayMonthAndYear(date)
            }

            else -> DateTimeUtil.displayShortDate(date)
        }
    }

    fun displayEndDate(resources: Resources, endDate: Long): String {

        return if (Goal.hasEndDate(endDate)) DateTimeUtil.displayMediumDate(endDate) else resources.getString(
            R.string.goal_no_end
        )
    }

    fun displayStyledProgressPercentage(context: Context, goal: Goal): SpannableString {

        val progressPercentageValue = displayProgressPercentageValue(context.resources, goal)

        val string = context.getString(R.string.percentage_pattern, progressPercentageValue)
        val valueIndex = string.indexOf(progressPercentageValue)
        val symbolIndex = if (valueIndex > 0) 0 else valueIndex + progressPercentageValue.length

        val spannable = SpannableString(string)
        val spans = arrayOf(
            ForegroundColorSpan(
                ColorUtil.resolveColorAttribute(
                    context,
                    android.R.attr.textColorSecondary
                )
            ),
            RelativeSizeSpan(0.5F),
            SuperscriptSpan()
        )

        spans.forEach {

            spannable.setSpan(
                it,
                symbolIndex,
                symbolIndex + 1,
                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        return spannable
    }

    fun displayProgressPercentageValue(resources: Resources, goal: Goal): String {

        return ((goal.progress.toDouble() / goal.target) * 100).roundToInt().toString()
    }

    fun displayProgressPercentage(resources: Resources, goal: Goal): String {

        return resources.getString(
            R.string.percentage_pattern,
            displayProgressPercentageValue(resources, goal)
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