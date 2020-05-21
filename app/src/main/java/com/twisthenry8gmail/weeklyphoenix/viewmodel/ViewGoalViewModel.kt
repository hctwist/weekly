package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import kotlinx.coroutines.launch

class ViewGoalViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository,
    private val currentGoalViewModel: CurrentGoalViewModel
) : BaseViewModel() {

    val goal = currentGoalViewModel.currentGoal

    fun onMenuItemClick(menuItem: MenuItem): Boolean {

        return when (menuItem.itemId) {

            R.id.view_goal_delete -> {

                viewModelScope.launch {

                    goalRepository.delete(goal.value!!.name)
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

            goalRepository.pauseIncrease(goal.value!!.name, goal.value!!.increasePaused)
        }
    }

    class Factory(
        private val androidResources: Resources,
        private val goalRepository: GoalRepository,
        private val currentGoalViewModel: CurrentGoalViewModel
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ViewGoalViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return ViewGoalViewModel(androidResources, goalRepository, currentGoalViewModel) as T
            }

            throw IllegalArgumentException()
        }
    }
}