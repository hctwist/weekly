package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.view.MenuItem
import androidx.lifecycle.*
import com.twisthenry8gmail.graphview.GraphElement
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistoryRepository
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch

class ViewGoalViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository,
    private val goalHistoryRepository: GoalHistoryRepository,
    private val currentGoalViewModel: CurrentGoalViewModel
) : NavigatorViewModel() {

    // TODO Better system for these two, increasePaused is a slight workaround
    val goal = currentGoalViewModel.currentGoal
    val increasePaused = MutableLiveData(goal.value!!.increasePaused)

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

        increasePaused.value = !increasePaused.value!!
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