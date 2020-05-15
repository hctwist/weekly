package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.text.Editable
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import kotlinx.android.synthetic.main.fragment_add_goal_title.*

class AddGoalTitleViewModel(goalRepository: GoalRepository, val currentGoalViewModel: CurrentGoalViewModel) : BaseViewModel() {

    private var goalNames: List<String>? = null
    private val _canContinue = MediatorLiveData<Boolean>().apply {

        addSource(goalRepository.getNames()) {

            goalNames = it
            value = canContinue()
        }
    }
    val canContinue: LiveData<Boolean>
        get() = _canContinue

    private var currentText = ""

    fun onTextChanged(text: Editable?) {

        currentText = text.toString()
        _canContinue.value = canContinue()
    }

    fun onContinue() {

        currentGoalViewModel.requireCurrentGoal().name = currentText
        navigateTo(R.id.action_fragmentAddGoalTitle_to_fragmentAddGoalTarget)
    }

    fun onBack() {

        navigateBack()
    }

    private fun canContinue() = currentText.isNotEmpty() && goalNames?.contains(currentText) != true

    class Factory(private val goalRepository: GoalRepository, private val currentGoalViewModel: CurrentGoalViewModel) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(AddGoalTitleViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return AddGoalTitleViewModel(goalRepository, currentGoalViewModel) as T
            }

            throw IllegalArgumentException()
        }
    }
}