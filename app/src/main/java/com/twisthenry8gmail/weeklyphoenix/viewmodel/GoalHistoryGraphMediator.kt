package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.util.Range
import com.twisthenry8gmail.graphview.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.data.minus
import com.twisthenry8gmail.weeklyphoenix.util.ColorUtil
import com.twisthenry8gmail.weeklyphoenix.util.DateTimeUtil
import com.twisthenry8gmail.weeklyphoenix.util.GoalDisplayUtil
import java.lang.Long.max
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class GoalHistoryGraphMediator(
    private val resources: Resources,
    private val goal: Goal,
    histories: List<GoalHistory>
) {

    private val historyPoints: List<GoalHistory> = histories.toMutableList().apply {

        add(
            GoalHistory(
                goal.id,
                LocalDate.ofEpochDay(goal.resetDate).minus(goal.reset).toEpochDay(),
                goal.progress,
                goal.target
            )
        )
    }

    private fun makeDateLabel(date: Long): String {

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

    private fun makeXAxis(): GraphElement {

        val labels = historyPoints.map {

            LabelAxis.Label(it.date.toDouble(), makeDateLabel(it.date))
        }

        return LabelAxis.X(Range(labels.first().point, labels.last().point), labels)
    }

    private fun makeYAxis(): GraphElement {

        val max = historyPoints.map { max(it.progress, it.target) }.max()!!.toDouble()

        val labels = listOf(
            LabelAxis.Label(
                goal.target.toDouble(),
                GoalDisplayUtil.displayProgressValue(resources, goal.type, goal.target)
            )
        )

        val style = LabelAxis.Style()
        style.textColorRes = R.color.light_grey

//        return LabelAxis.Y(Range(0.0, max), labels, style)

        val firstTarget = historyPoints.first().target
        return LabelAxis.Y(
            Range(0.0, max), listOf(
                LabelAxis.Label(
                    firstTarget.toDouble(),
                    GoalDisplayUtil.displayProgressValue(resources, goal.type, firstTarget)
                )
            )
        )
    }

    private fun makeMainLine(): GraphElement {

        val mainLineStyle = LineElement.Style()
        mainLineStyle.lineColor.colorInt = goal.color
//        mainLineStyle.fillLine = true
//        mainLineStyle.lineFillColor.colorInt = goal.color

        val points = historyPoints.map {

            DataPoint(it.date.toDouble(), it.progress.toDouble())
        }

        return LineElement(points, mainLineStyle)
    }

    private fun makeTargetLine(): GraphElement {

        val targetLineStyle = LineElement.Style()
//        targetLineStyle.lineColor.colorResource = R.color.light_grey
        targetLineStyle.lineColor.colorInt = ColorUtil.lightenGoalColor(goal.color)
//        targetLineStyle.dashWidthRes =
//            R.dimen.view_goal_target_line_dash to R.dimen.view_goal_target_line_gap

        val points = historyPoints.map {

            DataPoint(it.date.toDouble(), it.target.toDouble())
        }

        return LineElement(points, targetLineStyle)
    }

    private fun makePoints(): Array<GraphElement> {

        val style = LabelledPointElement.Style()
        style.color.colorInt = ColorUtil.lightenGoalColor(goal.color)

        val firstPoint = historyPoints.first()
        val lastPoint = historyPoints.last()

        return listOf(
            LabelledPointElement(
                DataPoint(
                    firstPoint.date.toDouble(),
                    firstPoint.target.toDouble()
                ),
                null,
                style
            ), LabelledPointElement(
                DataPoint(
                    lastPoint.date.toDouble(),
                    lastPoint.target.toDouble()
                ),
                GoalDisplayUtil.displayProgressValue(resources, goal.type, lastPoint.target),
                style
            )
        ).toTypedArray()
    }

    fun build(): List<GraphElement> {

        return listOf(makeXAxis(), makeYAxis(), makeMainLine(), makeTargetLine(), *makePoints())
    }

    companion object {

        const val MAX_HISTORY_POINTS = 4L
    }
}