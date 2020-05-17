package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import android.view.MenuItem
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.util.DateTimeUtil
import kotlinx.coroutines.launch

class ViewGoalViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository,
    private val currentGoalViewModel: CurrentGoalViewModel
) : BaseViewModel() {

    val goal = currentGoalViewModel.requireCurrentGoal()

    fun onMenuItemClick(menuItem: MenuItem): Boolean {

        return when (menuItem.itemId) {

            R.id.view_goal_delete -> {

                viewModelScope.launch {

                    goalRepository.delete(goal.name)
                }
                navigateBack()
                true
            }

            else -> false
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