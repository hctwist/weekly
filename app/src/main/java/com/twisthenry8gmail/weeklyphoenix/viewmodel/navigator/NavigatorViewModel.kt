package com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator

import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.FragmentNavigator
import com.twisthenry8gmail.weeklyphoenix.Event

open class NavigatorViewModel : ViewModel() {

    private val _navigationCommander = MutableLiveData<Event<NavigationCommand>>()
    val navigationCommander: LiveData<Event<NavigationCommand>>
        get() = _navigationCommander

    protected fun navigateTo(
        resId: Int,
        args: Bundle? = null,
        navigatorExtras: FragmentNavigator.Extras? = null
    ) {

        _navigationCommander.value = Event(NavigationCommand.To(resId, args, navigatorExtras))
    }

    protected fun navigateBack() {

        _navigationCommander.value = Event(NavigationCommand.Back())
    }
}