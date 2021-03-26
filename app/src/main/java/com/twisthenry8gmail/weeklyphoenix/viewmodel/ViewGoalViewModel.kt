package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.Event
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

    val goalId = GoalIdBundle.extractId(args)
    val goal = goalRepository.get(goalId)

    val averageProgress = goalHistoryRepository.getAverageFor(goalId)

    val histories = goal.switchMap {

        goalHistoryRepository.getAllFor(it, MAX_HISTORY_POINTS)
    }

    private val _showingInfo = MutableLiveData(Event(false))
    val showingInfo: LiveData<Event<Boolean>>
        get() = _showingInfo

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

                    if (Goal.isComplete(g.progress + 1, g.target)) {

                        // TODO Success
                    }
                }

                Goal.Type.TIMED -> {

                    goalRepository.startTimer(g.id)
                    navigateTo(R.id.action_fragmentViewGoal_to_fragmentGoalTimer)
                }
            }
        }
    }

    fun onShowInfoClicked() {

        _showingInfo.value = Event(true)
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