package com.twisthenry8gmail.weeklyphoenix.viewmodel.navigator

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.FragmentNavigator
import com.twisthenry8gmail.weeklyphoenix.Event

class NavigationCommander: MutableLiveData<Event<NavigationCommand>>() {

    fun navigateTo(
        resId: Int,
        args: Bundle? = null,
        navigatorExtras: FragmentNavigator.Extras? = null
    ) {

        value = Event(NavigationCommand.To(resId, args, navigatorExtras))
    }

    fun navigateBack() {

        value = Event(NavigationCommand.Back())
    }
}