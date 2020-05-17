package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import kotlinx.coroutines.launch

class AddGoalDoneViewModel(
    private val currentGoalViewModel: CurrentGoalViewModel,
    private val goalRepository: GoalRepository
) : BaseViewModel() {

    val goal: LiveData<Goal> = currentGoalViewModel.currentGoal

    fun onConfirm() {

        viewModelScope.launch {

            goalRepository.add(currentGoalViewModel.requireCurrentGoal())
        }

        navigateTo(R.id.action_fragmentAddGoalDone_to_fragmentMain)
    }

    class Factory(
        private val currentGoalViewModel: CurrentGoalViewModel,
        private val goalRepository: GoalRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(AddGoalDoneViewModel::class.java)) {

                return AddGoalDoneViewModel(currentGoalViewModel, goalRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}