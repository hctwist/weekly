package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.AnimatableData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import kotlinx.coroutines.launch

class AddGoalDoneViewModel(
    private val currentGoalViewModel: CurrentGoalViewModel,
    private val goalRepository: GoalRepository
) : BaseViewModel() {

    val goal: LiveData<Goal> = currentGoalViewModel.currentGoal

    private val _showMoreOptions = MutableLiveData<AnimatableData<Boolean>>().apply {

        value = AnimatableData(data = false, animate = false)
    }
    val showMoreOptions: LiveData<AnimatableData<Boolean>>
        get() = _showMoreOptions

    fun onClickShowMoreOptions() {

        _showMoreOptions.value = AnimatableData(data = true, animate = true)
    }

    fun onConfirm() {

        viewModelScope.launch {

            goalRepository.add(currentGoalViewModel.requireCurrentGoal())
        }

        navigateTo(R.id.action_global_fragmentMain2)
    }

    class Factory(
        private val currentGoalViewModel: CurrentGoalViewModel,
        private val goalRepository: GoalRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(AddGoalDoneViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return AddGoalDoneViewModel(currentGoalViewModel, goalRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}