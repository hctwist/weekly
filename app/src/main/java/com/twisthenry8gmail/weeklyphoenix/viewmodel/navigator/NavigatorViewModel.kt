package com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.FragmentNavigator
import com.twisthenry8gmail.weeklyphoenix.Event

open class NavigatorViewModel : ViewModel() {

    private val _navigationCommander = NavigationCommander()
    val navigationCommander: LiveData<Event<NavigationCommand>>
        get() = _navigationCommander

    protected fun navigateTo(
        resId: Int,
        args: Bundle? = null,
        navigatorExtras: FragmentNavigator.Extras? = null
    ) {

        _navigationCommander.navigateTo(resId, args, navigatorExtras)
    }

    protected fun navigateBack() {

        _navigationCommander.navigateBack()
    }
}