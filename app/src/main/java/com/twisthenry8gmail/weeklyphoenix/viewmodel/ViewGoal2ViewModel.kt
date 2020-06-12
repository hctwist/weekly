package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.os.Bundle
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.util.bundles.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistory
import com.twisthenry8gmail.weeklyphoenix.data.GoalHistoryRepository
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch

class ViewGoal2ViewModel(
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

                    if (g.withProgressIncrement(1).isComplete()) {

                        // TODO
                    }
                }

                Goal.Type.TIMED -> {

                    // TODO
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

            if (modelClass.isAssignableFrom(ViewGoal2ViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return ViewGoal2ViewModel(
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