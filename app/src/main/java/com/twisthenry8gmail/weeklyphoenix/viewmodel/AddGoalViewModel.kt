package com.twisthenry8gmail.weeklyphoenix.viewmodel

import android.content.res.Resources
import androidx.lifecycle.*
import com.twisthenry8gmail.weeklyphoenix.Event
import com.twisthenry8gmail.weeklyphoenix.NonNullLiveData
import com.twisthenry8gmail.weeklyphoenix.NonNullMutableLiveData
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.data.goals.Goal
import com.twisthenry8gmail.weeklyphoenix.data.goals.GoalRepository
import com.twisthenry8gmail.weeklyphoenix.data.goals.NewGoal
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigationCommand
import com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator.NavigationCommander
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

class AddGoalViewModel(
    private val androidResources: Resources,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _parentNavigationCommander = NavigationCommander()
    val parentNavigationCommander: LiveData<Event<NavigationCommand>>
        get() = _parentNavigationCommander

    private val _childNavigationCommander = NavigationCommander()
    val childNavigationCommander: LiveData<Event<NavigationCommand>>
        get() = _childNavigationCommander

    private val _page = NonNullMutableLiveData(0)
    val page: NonNullLiveData<Int>
        get() = _page

    val continueButtonText: LiveData<String> = Transformations.map(page) {

        getContinueButtonText(it)
    }

    val continueButtonIconRes: LiveData<Int> = page.map {

        getContinueButtonIconRes(it)
    }

    private val _type = NonNullMutableLiveData(Goal.Type.COUNTED)
    val type: NonNullLiveData<Goal.Type>
        get() = _type

    private val suggestedTitles: Array<String> =
        androidResources.getStringArray(R.array.goal_suggested_titles)
    private val _titleHint = NonNullMutableLiveData(suggestedTitles.random())
    val titleHint: NonNullLiveData<String>
        get() = _titleHint
    var title = ""
        set(value) {

            field = value
            updateCanContinue()
        }
    private var existingGoalTitles: List<String>? = null

    private val _canContinue = MediatorLiveData<Boolean>().apply {

        addSource(goalRepository.getTitles()) {

            existingGoalTitles = it
            updateCanContinue()
        }
    }
    val canContinue: LiveData<Boolean>
        get() = _canContinue

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

    private fun getContinueButtonText(page: Int): String {

        return when (page) {

            in 0..3 -> androidResources.getString(R.string.cont)
            else -> androidResources.getString(R.string.add_goal_done_add_goal)
        }
    }

    private fun getContinueButtonIconRes(page: Int): Int {

        return when (page) {

            in 0..3 -> R.drawable.round_arrow_forward_24
            else -> R.drawable.round_done_24
        }
    }

    fun onConfirm() {

        val currentPage = _page.value
        _page.value++
        updateCanContinue()

        when (currentPage) {

            0 -> onConfirmType()
            1 -> onConfirmTitle()
            2 -> onConfirmTarget()
            3 -> onConfirmReset()
            4 -> onDone()
        }
    }

    private fun onConfirmType() {

        _childNavigationCommander.navigateTo(R.id.action_fragmentAddGoalType_to_fragmentAddGoalTitle)
    }

    private fun onConfirmTitle() {

        _childNavigationCommander.navigateTo(R.id.action_fragmentAddGoalTitle_to_fragmentAddGoalTarget)
    }

    private fun onConfirmTarget() {

        _childNavigationCommander.navigateTo(R.id.action_fragmentAddGoalTarget_to_fragmentAddGoalReset)
    }

    private fun onConfirmReset() {

        _childNavigationCommander.navigateTo(R.id.action_fragmentAddGoalReset_to_fragmentAddGoalDone)
    }

    private fun onDone() {

        viewModelScope.launch {

            goalRepository.add(build())

            _parentNavigationCommander.navigateTo(R.id.action_global_fragmentMain)
        }
    }

    fun onBack() {

        if (page.value == 0) {

            _parentNavigationCommander.navigateBack()
        } else {

            _childNavigationCommander.navigateBack()
            _page.value--
        }
        updateCanContinue()
    }

    private fun updateCanContinue() {

        _canContinue.value = when (page.value) {

            1 -> {

                val resolvedTitle = resolveTitle()
                resolvedTitle.isNotEmpty() && existingGoalTitles?.contains(resolvedTitle) != true
            }

            else -> true
        }
    }

    fun onSelectType(type: Goal.Type) {

        if (_type.value != type) {

            target = type.minIncrement
            increase.value = 0L
        }
        _type.value = type
    }

    fun resolveTitle(): String {

        return if (title.isEmpty()) {

            titleHint.value
        } else {

            title.trim()
        }
    }

    fun onIncreaseDecrement() {

        if (increase.value >= type.value.minIncrement) {

            increase.value -= type.value.minIncrement
        }
    }

    fun onIncreaseIncrement() {

        increase.value += type.value.minIncrement
    }

    private fun build() = NewGoal(
        type.value,
        resolveTitle(),
        target,
        reset,
        increase.value,
        startDate.value,
        endDate.value,
        color.value
    )

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