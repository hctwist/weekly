package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.NonNullLiveData
import com.twisthenry8gmail.weeklyphoenix.NonNullMutableLiveData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.Goal
import com.twisthenry8gmail.weeklyphoenix.data.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigatorViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

class AddGoalViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository
) : NavigatorViewModel() {

    private val _type = NonNullMutableLiveData(Goal.Type.COUNTED)
    val type: NonNullLiveData<Goal.Type>
        get() = _type

    val suggestedTitles: Array<String> = androidResources.getStringArray(R.array.goal_suggested_titles)
    private val _titleHint = NonNullMutableLiveData(suggestedTitles.random())
    val titleHint: NonNullLiveData<String>
        get() = _titleHint
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

    private var _target: Long? = null
        get() = field ?: type.value.minIncrement
    var target: Long
        get() = _target!!
        set(value) {
            _target = value
        }

    var reset: Goal.Reset = Goal.ResetPreset.WEEKLY.toReset()

    val startDate = NonNullMutableLiveData(LocalDate.now().toEpochDay())

    val endDate = NonNullMutableLiveData(-1L)

    val increase = NonNullMutableLiveData(0L)

    val color = NonNullMutableLiveData {

        val cTypedArray = androidResources.obtainTypedArray(R.array.goal_colors)
        val value = cTypedArray.getColor(Random.nextInt(cTypedArray.length()), 0)
        cTypedArray.recycle()
        value
    }

    fun onBack() {

        navigateBack()
    }

    fun onSelectType(type: Goal.Type) {

        _type.value = type
    }

    fun onConfirmType() {

        navigateTo(R.id.action_fragmentAddGoalType_to_fragmentAddGoalTitle)
    }

    private fun isTitleValid(): Boolean {

        return if (title.isEmpty()) {

            existingGoalTitles?.contains(_titleHint.value) != true
        } else {

            existingGoalTitles?.contains(title) != true
        }
    }

    fun onConfirmTitle() {

        if (title.isEmpty()) {

            title = titleHint.value
        }
        navigateTo(R.id.action_fragmentAddGoalTitle_to_fragmentAddGoalTarget)
    }

    fun onConfirmTarget() {

        navigateTo(R.id.action_fragmentAddGoalTarget_to_fragmentAddGoalReset)
    }

    fun onConfirmReset() {

        navigateTo(R.id.action_fragmentAddGoalReset_to_fragmentAddGoalDone)
    }

    fun onIncreaseDecrement() {

        if (increase.value >= type.value.minIncrement) {

            increase.value -= type.value.minIncrement
        }
    }

    fun onIncreaseIncrement() {

        increase.value += type.value.minIncrement
    }

    fun onDone() {

        viewModelScope.launch {

            goalRepository.add(build())

            navigateTo(R.id.action_global_fragmentMain)
        }
    }

    private fun build(): Goal {

        return Goal(
            0,
            type.value,
            title,
            0,
            target,
            reset,
            Goal.getResetDateFrom(LocalDate.ofEpochDay(startDate.value), reset),
            increase.value,
            false,
            startDate.value,
            endDate.value,
            color.value
        )
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