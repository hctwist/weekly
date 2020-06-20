package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.os.Bundle
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalHistoryRepository
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.util.bundles.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch

class ViewGoalViewModel(
    private val args: Bundle?,
    private val androidResources: Resources,
    private val goalRepository: GoalRepository,
    private val goalHistoryRepository: GoalHistoryRepository
) : NavigatorViewModel() {

    val goal = goalRepository.get(GoalIdBundle.extractId(args))

    val histories: LiveData<List<GoalHistory>> = Transformations.switchMap(goal) {

        goalHistoryRepository.getAllFor(it, MAX_HISTORY_POINTS)
    }

    fun onClose() {

        navigateBack()
    }

    fun onDelete() {

        viewModelScope.launch {

            goalRepository.delete(goal.value!!.id)
        }
        navigateBack()
    }

    fun onAction() {

        goal.value?.let { g ->

            when (g.type) {

                Goal.Type.COUNTED -> {

                    viewModelScope.launch {

                        goalRepository.addProgress(g.id, 1)
                    }

                    if (Goal.wouldBeComplete(g, g.progress + 1)) {

                        // TODO
                    }
                }

                Goal.Type.TIMED -> {

                    goalRepository.startTimer(g.id)
                    navigateTo(R.id.action_fragmentViewGoal_to_fragmentGoalTimer)
                }
            }
        }
    }

    fun onPauseIncrease() {

        viewModelScope.launch {

            goalRepository.pauseIncrease(goal.value!!.id)
        }
    }

    companion object {

        const val MAX_HISTORY_POINTS = 4L
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