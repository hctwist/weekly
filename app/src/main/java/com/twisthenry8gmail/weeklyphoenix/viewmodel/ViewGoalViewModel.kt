package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.*
import com.twisthenry8gmail.graphview.GraphElement
import com.twisthenry8gmail.weeklyphoenix.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistoryRepository
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch

class ViewGoalViewModel(
    private val args: Bundle?,
    private val androidResources: Resources,
    private val goalRepository: GoalRepository,
    private val goalHistoryRepository: GoalHistoryRepository
) : NavigatorViewModel() {

    val goal = goalRepository.get(GoalIdBundle.fetchId(args))

    val goalHistoryGraphData: LiveData<List<GraphElement>> =
        Transformations.switchMap(goal) { goal ->

            MediatorLiveData<List<GraphElement>>().apply {

                addSource(
                    goalHistoryRepository.getAllFor(
                        goal,
                        GoalHistoryGraphMediator.MAX_HISTORY_POINTS
                    )
                ) { histories ->

                    value = if (histories.isEmpty()) null
                    else GoalHistoryGraphMediator(androidResources, goal, histories).build()
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

        viewModelScope.launch {

            goalRepository.pauseIncrease(goal.value!!.id)
        }
    }

    class Factory(
        private val args: Bundle?,
        private val androidResources: Resources,
        private val goalRepository: GoalRepository,
        private val goalHistoryRepository: GoalHistoryRepository
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ViewGoalViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return ViewGoalViewModel(
                    args,
                    androidResources,
                    goalRepository,
                    goalHistoryRepository
                ) as T
            }

            throw IllegalArgumentException()
        }
    }
}