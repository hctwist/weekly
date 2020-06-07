package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.*
import com.twisthenry8gmail.graphview.GraphElement
import com.twisthenry8gmail.weeklyphoenix.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.R
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

    val goal = goalRepository.get(GoalIdBundle.fetchId(args))

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