package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.graphics.Color
import android.view.MenuItem
import androidx.lifecycle.*
import com.twisthenry8gmail.graphview.DataElement
import com.twisthenry8gmail.graphview.DataElements
import com.twisthenry8gmail.graphview.LineDataElement
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistoryRepository
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import kotlinx.coroutines.launch

class ViewGoalViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository,
    private val goalHistoryRepository: GoalHistoryRepository,
    private val currentGoalViewModel: CurrentGoalViewModel
) : NavigatorViewModel() {

    // TODO Better system
    val goal = currentGoalViewModel.currentGoal

    val goalHistoryGraphData = Transformations.switchMap(goal) { goal ->

        MediatorLiveData<DataElements>().apply {

            addSource(goalHistoryRepository.getAllFor(goal.id)) { histories ->

                val mainLine = LineDataElement(histories.map {

                    DataElement.DataPoint(it.startDate.toDouble(), it.progress.toDouble())
                }, 10F, Color.BLUE)

                val targetLine = LineDataElement(histories.map {

                    DataElement.DataPoint(it.startDate.toDouble(), it.target.toDouble())
                }, 10F, Color.LTGRAY)

                value = DataElement.combine(mainLine, targetLine)
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