package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.NonNullLiveData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

class AddGoalViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository
) : NavigatorViewModel() {

    lateinit var type: Goal.Type
        private set

    var title = ""
        set(value) {

            field = value
            _validTitle.value = isTitleValid()
        }
    private var existingGoalTitles: List<String>? = null
    private val _validTitle = MediatorLiveData<Boolean>().apply {

        addSource(goalRepository.getTitles()) {

            existingGoalTitles = it
            value = isTitleValid()
        }
    }
    val validTitle: LiveData<Boolean>
        get() = _validTitle

    // TODO Better backing field solution?
    private var _target: Long? = null
        get() = field ?: type.minIncrement
    var target: Long
        get() = _target!!
        set(value) {
            _target = value
        }

    var reset: Goal.Reset = Goal.ResetPreset.WEEKLY.toReset()

    val startDate = NonNullLiveData(LocalDate.now().toEpochDay())

    val endDate = NonNullLiveData(-1L)

    val increase = NonNullLiveData(0L)

    val color = NonNullLiveData {

        val cTypedArray = androidResources.obtainTypedArray(R.array.goal_colors)
        val value = cTypedArray.getColor(Random.nextInt(cTypedArray.length()), 0)
        cTypedArray.recycle()
        value
    }

    fun onBack() {

        navigateBack()
    }

    fun onConfirmType(type: Goal.Type) {

        this.type = type
        navigateTo(R.id.action_fragmentAddGoalType_to_fragmentAddGoalTitle)
    }

    private fun isTitleValid() = title.isNotEmpty() && existingGoalTitles?.contains(title) != true

    fun onConfirmTitle() {

        navigateTo(R.id.action_fragmentAddGoalTitle_to_fragmentAddGoalTarget)
    }

    fun onConfirmTarget() {

        navigateTo(R.id.action_fragmentAddGoalTarget_to_fragmentAddGoalReset)
    }

    fun onConfirmReset() {

        navigateTo(R.id.action_fragmentAddGoalReset_to_fragmentAddGoalDone)
    }

    fun onDone() {

        viewModelScope.launch {

            goalRepository.add(build())
        }

        navigateTo(R.id.action_global_fragmentMain)
    }

    private fun build(): Goal {

        val goal = Goal(
            0,
            type,
            title,
            0,
            target,
            reset,
            -1,
            increase.value,
            false,
            startDate.value,
            endDate.value,
            color.value
        )

        goal.updateResetDate(LocalDate.ofEpochDay(startDate.value))

        return goal
    }

    class Factory(
        private val androidResources: Resources,
        private val goalRepository: GoalRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(AddGoalViewModel::class.java)) {

                @Suppress("UNCHECKED_CAST")
                return AddGoalViewModel(androidResources, goalRepository) as T
            }

            throw IllegalArgumentException()
        }
    }
}