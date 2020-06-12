package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twisthenry8gmail.weeklyphoenix.util.bundles.GoalIdBundle
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel

class GoalSuccessViewModel(
    private val arguments: Bundle?,
    private val goalRepository: GoalRepository
) : NavigatorViewModel() {

    val goal = goalRepository.get(GoalIdBundle.extractId(arguments))
    var shouldAnimateViews = true

    fun onDone() {

        navigateBack()
    }

    class Factory(
        private val arguments: Bundle?,
        private val goalRepository: GoalRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(GoalSuccessViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return GoalSuccessViewModel(arguments, goalRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}