package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.util.Range
import com.twisthenry8gmail.graphview.DataElement
import com.twisthenry8gmail.graphview.GraphElement
import com.twisthenry8gmail.graphview.LabelAxis
import com.twisthenry8gmail.graphview.LineElement
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.data.minus
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

    init {

        // TODO Fill in history points somehow

    }

    private fun makeDateLabel(date: Long): String {

        return when {

            goal.reset.unit == ChronoUnit.DAYS && goal.reset.multiple == 1L -> {

                DateTimeUtil.displayDay(date)
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

        return LabelAxis.Y(Range(0.0, max), labels, style)
    }

    private fun makeMainLine(): GraphElement {

        val mainLineStyle = LineElement.Style()
        mainLineStyle.lineColorRes = R.color.color_primary
        mainLineStyle.fillLine = true
        mainLineStyle.lineFillColorRes = R.color.color_primary_light

        val points = historyPoints.map {

            DataElement.DataPoint(it.date.toDouble(), it.progress.toDouble())
        }

        return LineElement(points, mainLineStyle)
    }

    private fun makeTargetLine(): GraphElement {

        val targetLineStyle = LineElement.Style()
        targetLineStyle.lineColorRes = R.color.light_grey
        targetLineStyle.dashWidthRes = R.dimen.half_margin to R.dimen.margin

        val points = historyPoints.map {

            DataElement.DataPoint(it.date.toDouble(), it.target.toDouble())
        }

        return LineElement(points, targetLineStyle)
    }

    fun build(): List<GraphElement> {

        return listOf(makeXAxis(), makeYAxis(), makeMainLine(), makeTargetLine())
    }

    companion object {

        const val MAX_HISTORY_POINTS = 4
    }
}