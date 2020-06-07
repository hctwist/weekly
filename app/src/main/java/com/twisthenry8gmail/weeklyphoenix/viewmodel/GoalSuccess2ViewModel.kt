package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twisthenry8gmail.weeklyphoenix.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel

class GoalSuccess2ViewModel(
    private val arguments: Bundle?,
    private val goalRepository: GoalRepository
) : NavigatorViewModel() {

    val goal = goalRepository.get(GoalIdBundle.fetchId(arguments))
    var shouldAnimateViews = true

    fun onDone() {

        navigateBack()
    }

    class Factory(
        private val arguments: Bundle?,
        private val goalRepository: GoalRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(GoalSuccess2ViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return GoalSuccess2ViewModel(arguments, goalRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}