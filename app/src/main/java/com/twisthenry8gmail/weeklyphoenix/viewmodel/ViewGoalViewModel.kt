package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.view.MenuItem
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import kotlinx.coroutines.launch

class ViewGoalViewModel(
    private val goalRepository: GoalRepository,
    private val currentGoalViewModel: CurrentGoalViewModel
) : BaseViewModel() {

    val name = currentGoalViewModel.requireCurrentGoal().name

    fun onMenuItemClick(menuItem: MenuItem): Boolean {

        return when (menuItem.itemId) {

            R.id.view_goal_delete -> {

                viewModelScope.launch {

                    goalRepository.delete(name)
                }
                navigateBack()
                true
            }

            else -> false
        }
    }

    class Factory(
        private val goalRepository: GoalRepository,
        private val currentGoalViewModel: CurrentGoalViewModel
    ) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(ViewGoalViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return ViewGoalViewModel(goalRepository, currentGoalViewModel) as T
            }

            throw IllegalArgumentException()
        }
    }
}