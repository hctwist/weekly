package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twisthenry8gmail.weeklyphoenix.data.Goal

class CurrentGoalViewModel : ViewModel() {

    val currentGoal = MutableLiveData<Goal>()

    fun requireCurrentGoal() = currentGoal.value!!

    fun postCurrentGoalUpdate() {

        currentGoal.value = currentGoal.value
    }
}