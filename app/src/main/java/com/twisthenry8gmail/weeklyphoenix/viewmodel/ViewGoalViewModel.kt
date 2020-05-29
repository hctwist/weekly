package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.graphics.Color
import android.util.Range
import android.view.MenuItem
import androidx.lifecycle.*
import com.twisthenry8gmail.graphview.DataElement
import com.twisthenry8gmail.graphview.GraphElement
import com.twisthenry8gmail.graphview.LineElement
import com.twisthenry8gmail.graphview.TickAxis
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistoryRepository
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.util.DateTimeUtil
import kotlinx.coroutines.launch
import java.time.LocalDate

class ViewGoalViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository,
    private val goalHistoryRepository: GoalHistoryRepository,
    private val currentGoalViewModel: CurrentGoalViewModel
) : NavigatorViewModel() {

    // TODO Better system
    val goal = currentGoalViewModel.currentGoal

    val goalHistoryGraphData: LiveData<List<GraphElement>> =
        Transformations.switchMap(goal) { goal ->

            MediatorLiveData<List<GraphElement>>().apply {

                addSource(goalHistoryRepository.getAllFor(goal.id)) { histories ->

                    val resetDate = LocalDate.ofEpochDay(goal.resetDate)

                    val nPeriodsToShow = 5

                    val ticks = List(nPeriodsToShow) {

                        val tickDate = resetDate.minus(
                            (nPeriodsToShow - it) * goal.reset.multiple,
                            goal.reset.unit
                        )
                        TickAxis.Tick(
                            tickDate.toEpochDay().toDouble(),
                            DateTimeUtil.displayDate(tickDate)
                        )
                    }
                    val xAxis = TickAxis.X(Range(ticks.first().point, ticks.last().point), ticks)

                    val mainLine = LineElement(histories.map {

                        DataElement.DataPoint(it.startDate.toDouble(), it.progress.toDouble())
                    }, 10F, Color.BLUE)

                    val targetLine = LineElement(histories.map {

                        DataElement.DataPoint(it.startDate.toDouble(), it.target.toDouble())
                    }, 10F, Color.LTGRAY)

                    value = listOf(xAxis, mainLine, targetLine)
                }
            }
        }

    fun onMenuItemClick(menuItem: MenuItem): Boolean {

        return when (menuItem.itemId) {

            R.id.view_goal_delete -> {

                viewModelScope.launch {

                    goalRepository.delete(goal.value!!.id)
                }
                navigateBack()
                true
            }

            else -> false
        }
    }

    fun onPauseIncrease() {

        goal.value!!.increasePaused = !goal.value!!.increasePaused
        goal.value = goal.value
        viewModelScope.launch {

            goalRepository.pauseIncrease(goal.value!!.id, goal.value!!.increasePaused)
        }
    }

    class Factory(
        private val androidResources: Resources,
        private val goalRepository: GoalRepository,
        private val goalHistoryRepository: GoalHistoryRepository,
        private val currentGoalViewModel: CurrentGoalViewModel
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ViewGoalViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return ViewGoalViewModel(
                    androidResources,
                    goalRepository,
                    goalHistoryRepository,
                    currentGoalViewModel
                ) as T
            }

            throw IllegalArgumentException()
        }
    }
}