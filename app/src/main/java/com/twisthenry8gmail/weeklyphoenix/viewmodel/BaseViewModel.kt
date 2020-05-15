package com.twisthenry8gmail.weeklyphoenix.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.twisthenry8gmail.weeklyphoenix.Event

open class BaseViewModel : ViewModel() {

    private val _navigationCommander = MutableLiveData<Event<NavigationCommand>>()
    val navigationCommander: LiveData<Event<NavigationCommand>>
        get() = _navigationCommander

    protected fun navigateTo(resId: Int) {

        _navigationCommander.value = Event(NavigationCommand.To(resId))
    }

    protected fun navigateBack() {

        _navigationCommander.value = Event(NavigationCommand.Back())
    }
}